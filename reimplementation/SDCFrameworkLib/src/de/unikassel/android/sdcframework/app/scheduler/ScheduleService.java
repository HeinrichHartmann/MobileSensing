/*
 * Copyright (C) 2012, Katy Hilgenberg.
 * Special acknowledgments to: Knowledge & Data Engineering Group, University of Kassel (http://www.kde.cs.uni-kassel.de).
 * Contact: sdcf@cs.uni-kassel.de
 *
 * This file is part of the SDCFramework (Sensor Data Collection Framework) project.
 *
 * The SDCFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The SDCFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the SDCFramework.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unikassel.android.sdcframework.app.scheduler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.data.Weekday;
import de.unikassel.android.sdcframework.data.WeekdaySchedule;
import de.unikassel.android.sdcframework.data.WeekdayScheduleEntry;
import de.unikassel.android.sdcframework.data.WeekdaySchedulerAction;
import de.unikassel.android.sdcframework.data.WeeklySchedule;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.service.ServiceUtils;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.TimeProvider;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;

/**
 * The scheduler background process.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ScheduleService extends IntentService
{
  /**
   * A simple structure to store the data for an alarm.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class ScheduledAlarm
  {
    /**
     * The related weekday schedule entry
     */
    public WeekdayScheduleEntry schedule;
    
    /**
     * The calculated alarm time
     */
    public Calendar alarmTime;
  }
  
  /**
   * The action this service does process from intents.
   */
  private static final String ACTION = WeekdaySchedulerAction.ACTION;
  
  /**
   * The preference manager
   */
  private ApplicationPreferenceManager prefManager;
  
  /**
   * The alarm manager
   */
  private AlarmManager alarmManager;
  
  /**
   * Date formatter for log messages.
   */
  @SuppressLint( "SimpleDateFormat" )
  private final SimpleDateFormat df = new SimpleDateFormat();
  
  /**
   * The wake lock to keep CPU running while service is active.
   */
  private PowerManager.WakeLock wakeLock;
  
  /**
   * Constructor
   */
  public ScheduleService()
  {
    super( ScheduleService.class.getSimpleName() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.IntentService#onCreate()
   */
  @Override
  public void onCreate()
  {
    super.onCreate();
    this.prefManager = new ApplicationPreferenceManagerImpl();
    this.alarmManager =
        (AlarmManager) getSystemService( Context.ALARM_SERVICE );
    acquireWakeLock();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.IntentService#onDestroy()
   */
  @Override
  public void onDestroy()
  {
    releaseWakeLock();
    prefManager.onDestroy();
    prefManager = null;
    alarmManager = null;
    super.onDestroy();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.IntentService#onHandleIntent(android.content.Intent)
   */
  @Override
  protected final void onHandleIntent( Intent intent )
  {
    // get time stamp first
    Calendar now = Calendar.getInstance();
    
    Logger.getInstance().info( this,
        "received intent at " + df.format( now.getTime() ) );
    
    // ignore wrong ACTIONS
    if ( !ACTION.equals( intent.getAction() ) )
      return;
    
    WeeklySchedule schedule =
        prefManager.getServicePreferences().getWeeklySchedulePreference().getConfiguration(
            prefManager.getSharedPreferences( getApplicationContext() ) );
    
    // unparcel intent data first
    WeekdayScheduleEntry lastScheduledEntry =
        getEntryFromIntent( intent, schedule );
    
    // cancel any pending intent
    alarmManager.cancel( createPendingIntent( createIntent( this ), this ) );
    
    // create new alarm
    ScheduledAlarm alarm = getNextAlarm( schedule, now, lastScheduledEntry );
    setAlarm( alarm );
    
    WeekdaySchedulerAction currentAction = null;
    if ( lastScheduledEntry != null )
    {
      // the event was raised by an alarm
      Logger.getInstance().info( this,
          "Execution of alarm event: " + lastScheduledEntry.toString() );
      currentAction = lastScheduledEntry.getAction();
    }
    else if ( alarm != null )
    {
      // update to expected current state if necessary
      currentAction = alarm.schedule.getAction().getPreviousAction();
      Logger.getInstance().info(
          this,
          "Update with previous action to force expected running state: "
              + currentAction.toString() );
    }
    execute( currentAction );
    
  }
  
  /**
   * Method to set a new alarm for next event.
   * 
   * @param alarm
   *          the alarm to create
   */
  private final void setAlarm( ScheduledAlarm alarm )
  {
    if ( alarm != null )
    {
      Intent intent = createIntent( this );
      String extra = alarm.schedule.toString();
      intent.putExtra( WeekdayScheduleEntry.class.getSimpleName(), extra );
      intent.putExtra( Weekday.class.getSimpleName(),
          alarm.schedule.getWeekday().name() );
      
      PendingIntent pendingIntent =
          createPendingIntent( intent, this );
      alarm.alarmTime.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
      
      alarmManager.set( AlarmManager.RTC_WAKEUP,
          alarm.alarmTime.getTimeInMillis(), pendingIntent );
      
      Logger.getInstance().info( this,
          "scheduled next service state change for "
              + df.format( alarm.alarmTime.getTimeInMillis() ) + ": "
              + alarm.schedule );
    }
  }
  
  /**
   * Does create a pending intent to start this service.
   * 
   * @param intent
   *          the intent to wrap
   * @param context
   *          the context
   * @return a pending intent to start this service
   */
  private final static PendingIntent createPendingIntent( Intent intent,
      Context context )
  {
    return PendingIntent.getService( context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT );
  }
  
  /**
   * Does create an intent to start the schedule service
   * 
   * @param applicationContext
   *          the context
   * 
   * @return an intent to start the schedule service
   */
  public final static Intent createIntent( Context applicationContext )
  {
    return new Intent( ACTION ).setClass(
        applicationContext, ScheduleService.class );
  }
  
  /**
   * Method to calculate the next alarm based on time and a given schedule.
   * 
   * @param schedule
   *          the current schedule
   * @param now
   *          the current time stamp
   * @param lastScheduledEntry
   *          the last scheduled entry if known
   * @return the next scheduled alarm
   */
  private final ScheduledAlarm getNextAlarm( WeeklySchedule schedule,
      Calendar now, WeekdayScheduleEntry lastScheduledEntry )
  {
    if ( schedule.size() == 0 )
      return null;
    
    ScheduledAlarm alarm = new ScheduledAlarm();
    
    // initialize with date of todays weekday
    alarm.alarmTime = TimeProvider.getDayBegin( now );
    alarm.schedule = null;
    
    int startTimeLimit =
        ( now.get( Calendar.MINUTE ) + now.get( Calendar.HOUR_OF_DAY ) * 60 )
            * 60 + now.get( Calendar.SECOND );
    
    // start search at todays weekday
    Weekday weekday = Weekday.valueOf( now );
    
    // just avoid restarting same schedule if alarm was raised exact in time
    // (processing takes place in the minute window)
    if ( lastScheduledEntry != null
        && weekday.equals( lastScheduledEntry.getWeekday() ) &&
        lastScheduledEntry.getSeconds() >= startTimeLimit )
    {
      startTimeLimit = lastScheduledEntry.getSeconds() + 1;
    }
    
    while ( true )
    {
      for ( WeekdayScheduleEntry entry : schedule.getScheduleForWeekday(
          weekday ).getEntries() )
      {
        if ( entry.getSeconds() >= startTimeLimit )
        {
          alarm.schedule = entry;
          alarm.alarmTime.add( Calendar.MILLISECOND, entry.getMilliseconds() );
          break;
        }
      }
      // stop as soon as an activity was found
      if ( alarm.schedule != null )
        break;
      
      alarm.alarmTime.add( Calendar.HOUR_OF_DAY, 24 );
      startTimeLimit = 0;
      weekday = Weekday.next( weekday );
    }
    
    return alarm;
  }
  
  /**
   * Does execute a scheduled action
   * 
   * @param action
   *          the action to execute
   */
  protected final void execute( WeekdaySchedulerAction action )
  {
    boolean isRunning =
        ServiceUtils.isServiceRunning( getApplicationContext(),
            ISDCService.class );
    
    // execute action
    if ( WeekdaySchedulerAction.StopService.equals( action ) && isRunning )
    {
      ServiceUtils.stopService( getApplicationContext(),
          ISDCService.class );
    }
    else if ( WeekdaySchedulerAction.StartService.equals( action )
        && !isRunning )
    {
      ServiceUtils.startService( getApplicationContext(),
          ISDCService.class );
    }
  }
  
  /**
   * Method to get a scheduled valid entry from an intent (valid according to
   * the given schedule ).
   * 
   * @param intent
   *          the intent to create scheduled entry from
   * @param schedule
   *          the schedule to validate an existing entry for
   * @return the scheduled valid entry from this intent or null
   */
  private final WeekdayScheduleEntry getEntryFromIntent( Intent intent,
      WeeklySchedule schedule )
  {
    if ( intent.hasExtra( WeekdayScheduleEntry.class.getSimpleName() ) )
    {
      try
      {
        // get scheduled entry and validate
        String extra = intent.getStringExtra( Weekday.class.getSimpleName() );
        Weekday weekday = Weekday.valueOf( extra );
        
        extra =
            intent.getStringExtra( WeekdayScheduleEntry.class.getSimpleName() );
        WeekdayScheduleEntry entry;
        
        entry = GlobalSerializer.fromXML( WeekdayScheduleEntry.class, extra );
        entry.setWeekdaySchedule( new WeekdaySchedule( weekday ) );
        
        // test if entry has an assigned weekday and if it is still in the
        // schedule
        if ( weekday != null
            &&
            schedule.getScheduleForWeekday( weekday ).getEntries().contains(
                entry ) )
        {
          return entry;
        }
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this, Log.getStackTraceString( e ) );
      }
    }
    return null;
  }
  
  /**
   * Setter for the wake lock
   * 
   * @param wakeLock
   *          the wake lock to set
   */
  private final void setWakeLock( PowerManager.WakeLock wakeLock )
  {
    this.wakeLock = wakeLock;
  }
  
  /**
   * Getter for the wake lock
   * 
   * @return the wake lock
   */
  private final PowerManager.WakeLock getWakeLock()
  {
    if ( wakeLock == null )
    {
      PowerManager powerManager =
          (PowerManager) getSystemService( Context.POWER_SERVICE );
      setWakeLock( powerManager.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK,
          getClass().getSimpleName() ) );
    }
    return wakeLock;
  }
  
  /**
   * Does release the wake lock
   */
  private final void releaseWakeLock()
  {
    WakeLock wakeLock = getWakeLock();
    if ( wakeLock != null && wakeLock.isHeld() )
    {
      wakeLock.release();
      Logger.getInstance().info( this, "wake lock released" );
    }
  }
  
  /**
   * Does acquire the wake lock
   */
  private final void acquireWakeLock()
  {
    WakeLock wakeLock = getWakeLock();
    if ( wakeLock != null )
    {
      wakeLock.acquire();
      Logger.getInstance().info( this, "wake lock aquired" );
    }
  }
}
