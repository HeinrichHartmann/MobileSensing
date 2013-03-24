/*
 * Copyright (C) 2012, Katy Hilgenberg
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
package de.unikassel.android.sdcframework.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.LifeCycleObject;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * An observable alarm. Use the {@link AlarmBuilder} class to create alarm
 * instances.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ObservableAlarm extends BroadcastReceiver implements
    ObservableEventSource< AlarmEvent >, LifeCycleObject
{
  /**
   * The identifier key name
   */
  private static final String ID = "id";
  
  /**
   * The intent action
   */
  private final String ACTION =
      "de.unikassel.android.sdcframework.util.AlarmEvent";
  
  /**
   * The observable event source to delegate to
   */
  private final ObservableEventSource< AlarmEvent > eventSource;
  
  /**
   * The alarm manager to wake up device
   */
  private final AlarmManager alarmManager;
  
  /**
   * The context
   */
  private final Context context;
  
  /**
   * The last pending intent
   */
  private PendingIntent pendingIntent;
  
  /**
   * The unique action identifier.
   */
  private final String action;
  
  /**
   * The unique alarm identifier.
   */
  private final int id;
  
  /**
   * Constructor
   * 
   * @param context
   * @param id
   *          the unique alarm identifier
   */
  public ObservableAlarm( Context context, int id )
  {
    this.eventSource = new ObservableEventSourceImpl< AlarmEvent >();
    this.context = context;
    this.alarmManager =
        (AlarmManager) context.getSystemService( Context.ALARM_SERVICE );
    this.id = id;
    this.action = ACTION + id;
  }
  
  /**
   * Method to cancel the current alarm
   */
  public synchronized void cancelAlarm()
  {
    if ( pendingIntent != null )
    {
      alarmManager.cancel( pendingIntent );
      Logger.getInstance().debug( this, String.format( "Alarm %d canceled", getId() ) );
    }
    pendingIntent = null;
  }
  
  /**
   * Method to add an alarm
   * 
   * @param timeOffset
   *          the time offset in milliseconds
   */
  public synchronized void setAlarm( long timeOffset )
  {
    cancelAlarm();
    Intent intent = new Intent( action );
    intent.putExtra( ID, this.hashCode() );
    pendingIntent =
        PendingIntent.getBroadcast( context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT );
    
    long triggerAtTime = System.currentTimeMillis() + timeOffset;
    alarmManager.set( AlarmManager.RTC_WAKEUP,
        triggerAtTime, pendingIntent );
    
    triggerAtTime -= TimeProvider.getInstance().getOffset();
    Logger.getInstance().debug(
        this,
        String.format( "Alarm %d set to: %s", getId(),
            TimeProvider.toUTCString( triggerAtTime ) ) );
  }
  
  /**
   * Getter for the id
  
   * @return the id
   */
  public int getId()
  {
    return id;
  }

  /*
   * (non-Javadoc)
   * 
   * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
   * android.content.Intent)
   */
  @Override
  public void onReceive( Context context, Intent intent )
  {
    if ( action.equals( intent.getAction() ) )
    {
      int hashCode = intent.getIntExtra( ID, -1 );
      if ( hashCode == this.hashCode() )
      {
        Logger.getInstance().debug( this,
            String.format( "Alarm %d  event received!", getId() ) );
        notify( new AlarmEvent( this ) );
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * registerEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public synchronized void registerEventObserver(
      EventObserver< ? extends AlarmEvent > observer )
  {
    eventSource.registerEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * unregisterEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public synchronized void unregisterEventObserver(
      EventObserver< ? extends AlarmEvent > observer )
  {
    eventSource.unregisterEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * removeAllObservers()
   */
  @Override
  public synchronized void removeAllObservers()
  {
    eventSource.removeAllObservers();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.ObservableEventSource#notify
   * (de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public void notify( AlarmEvent data )
  {
    eventSource.notify( data );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onResume(
   * android.content.Context)
   */
  @Override
  public void onResume( Context applicationContext )
  {
    context.registerReceiver( this, new IntentFilter( action ) );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onPause(android
   * .content.Context)
   */
  @Override
  public void onPause( Context applicationContext )
  {
    cancelAlarm();
    try
    {
      context.unregisterReceiver( this );
    }
    catch ( IllegalArgumentException e )
    {}
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onCreate(
   * android.content.Context)
   */
  @Override
  public void onCreate( Context applicationContext )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onDestroy
   * (android.content.Context)
   */
  @Override
  public void onDestroy( Context applicationContext )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * hasObservers()
   */
  @Override
  public boolean hasObservers()
  {
    return eventSource.hasObservers();
  }
  
}
