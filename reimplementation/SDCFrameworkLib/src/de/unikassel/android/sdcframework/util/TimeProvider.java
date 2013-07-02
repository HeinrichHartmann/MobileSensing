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
package de.unikassel.android.sdcframework.util;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import de.unikassel.android.sdcframework.transmission.ConnectivityWrapperImpl;
import de.unikassel.android.sdcframework.util.facade.EventErrorTypes;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.NetworkStateChangeEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import de.unikassel.android.sdcframework.util.facade.TimeProviderEvent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.SntpClient;
import android.os.SystemClock;

/**
 * The internal time provider. <br/>
 * <br/>
 * This class does encapsulate the frameworks time service. It does request an
 * actual time from an ntp server and stores the offset to correct the local
 * system time. <br/>
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class TimeProvider
    extends ObservableEventSourceImpl< TimeProviderEvent >
{
  
  /**
   * The list with the providers to use
   */
  private final String[] INITIAL_PROVIDERS = { "ntps1-1.cs.tu-berlin.de",
      "ptbtime1.ptb.de", "ptbtime2.ptb.de", "atom.uhr.de" };
  
  /**
   * The maximum count for asynchronous synchronization retires
   */
  private static final int CNT_MAX_SYNC_ATTEMPTS = 5;
  
  /**
   * Time to wait for network available
   */
  private static final long CONNECTION_WAIT_TIME = 15000L;
  
  /**
   * Thread for asynchronous time updates
   * 
   * @author Katy Hilgenberg
   * 
   */
  class UpdateThread extends AbstractWorkerThread
      implements EventObserver< NetworkStateChangeEvent >
  {
    
    /**
     * the network connection observer
     */
    private final NetworkConnectionObserver connectionObserver;
    
    /**
     * The wake lock holder instance
     */
    private final WakeLockHolder lockHolder;
    
    /**
     * The context
     */
    private final Context context;
    
    /**
     * The counter for sync attempts
     */
    private int attemptCounter;
    
    /**
     * The {@link Connection} wait lock
     */
    private final Object connectionWaitLock;
    
    /**
     * Constructor
     * 
     * @param context
     *          the context
     */
    public UpdateThread( Context context )
    {
      super();
      this.context = context;
      this.attemptCounter = 0;
      this.lockHolder = new WakeLockHolder( context );
      Logger.getInstance().debug( this,
          lockHolder.hashCode() + ": wake lock holder created" );
      this.connectionWaitLock = new Object();
      this.connectionObserver = NetworkConnectionObserver.getInstance( context );
      lockHolder.acquireWakeLock();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.AbstractWorkerThread#doCleanUp()
     */
    @Override
    protected void doCleanUp()
    {
      lockHolder.releaseWakeLock();
      stopAsynchronousUpdate( context );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see de.unikassel.android.sdcframework.util.AbstractWorkerThread#doWork()
     */
    @Override
    protected void doWork()
    {
      // try at least x times
      if ( attemptCounter < CNT_MAX_SYNC_ATTEMPTS )
      {
        attemptCounter++;
        Logger.getInstance().debug(
              this,
              "Asynchronous provider time update ( " + attemptCounter
                  + ". attempt )" );
        
        if ( asynchhronousUpdateTime( context ) )
        {
          TimeProvider.getInstance().notifyUpdated();
          Logger.getInstance().debug(
                this, "Asynchronous provider time update finished!" );
          doTerminate();
        }
        else if ( !ConnectivityWrapperImpl.getInstance().isAnyConnectionAvailable(
              context )
              || !connectionObserver.isConnected() )
        {
          try
          {
            connectionObserver.registerEventObserver( this );
            
            // wait a maximum of X seconds
            synchronized ( connectionWaitLock )
            {
              connectionWaitLock.wait( CONNECTION_WAIT_TIME );
            }
            
          }
          catch ( InterruptedException e )
          {}
          finally
          {
            connectionObserver.unregisterEventObserver( this );
          }
        }
        else
        {
          try
          {
            Thread.sleep( CONNECTION_WAIT_TIME );
          }
          catch ( InterruptedException e )
          {}
        }
      }
      else
      {
        Logger.getInstance().debug(
              this, "Asynchronous provider time update failed!" );
        // finally notify observers
        TimeProvider.getInstance().notifySyncError( getTimeStamp() );
        // trigger thread termination
        doTerminate();
      }
    }
    
    /**
     * Method to update the time provider time offset
     * 
     * @param context
     *          the context
     */
    private final boolean asynchhronousUpdateTime( Context context )
    {
      return TimeProvider.getInstance().syncTime( context );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de
     * .unikassel.android.sdcframework.util.facade.ObservableEventSource,
     * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
     */
    @Override
    public void onEvent(
        ObservableEventSource< ? extends NetworkStateChangeEvent > eventSource,
        NetworkStateChangeEvent observedEvent )
    {
      if ( observedEvent.isNetworkAvailable() )
      {
        synchronized ( connectionWaitLock )
        {
          connectionWaitLock.notifyAll();
        }
      }
    }
  }
  
  /**
   * The internal time offset
   */
  private AtomicLong offset;
  
  /**
   * The time stamp of the last sync update
   */
  private AtomicLong lastUpdateTs;
  
  /**
   * Flag if time is initialized
   */
  private final AtomicBoolean synced;
  
  /**
   * Flag for update in progress
   */
  private final AtomicBoolean updateInProgress;
  
  /**
   * The singleton instance of the time provider
   */
  private static TimeProvider instance;
  
  /**
   * The list with time providers
   */
  private final List< String > providers;
  
  /**
   * The instance of a running update thread
   */
  private UpdateThread updateThread;
  
  /**
   * Constructor
   */
  private TimeProvider()
  {
    offset = new AtomicLong( 0L );
    this.synced = new AtomicBoolean( false );
    this.lastUpdateTs = new AtomicLong( 0L );
    this.updateInProgress = new AtomicBoolean( false );
    this.updateThread = null;
    providers = new ArrayList< String >( Arrays.asList( INITIAL_PROVIDERS ) );
  }
  
  /**
   * Access to the global time provider instance
   * 
   * @return the global time provider instance
   */
  public final synchronized static TimeProvider getInstance()
  {
    if ( instance == null )
    {
      instance = new TimeProvider();
    }
    return instance;
  }
  
  /**
   * Getter for the offset
   * 
   * @return the offset
   */
  public long getOffset()
  {
    return offset.get();
  }
  
  /**
   * Setter for the offset
   * 
   * @param offset
   *          the offset to set
   */
  private void setOffset( long offset )
  {
    this.offset.set( offset );
  }
  
  /**
   * Method to update the NTP provider list
   * 
   * @param newProviders
   *          the new provider list
   */
  public final synchronized void updateProviders( List< String > newProviders )
  {
    if ( newProviders.size() > 0 )
    {
      providers.clear();
      providers.addAll( newProviders );
    }
  }
  
  /**
   * Getter for the providers
  
   * @return the providers
   */
  public synchronized List< String > getProviders()
  {
    return new ArrayList< String >( providers );
  }

  /**
   * Getter for the time ( maybe out of sync )
   * 
   * @return the current UTC time as time stamp
   */
  public final long getTimeStamp()
  {
    return System.currentTimeMillis() - offset.get();
  }
  
  /**
   * Getter for the accurate time stamp
   * 
   * @return the current UTC time as time stamp, or null if out of sync
   */
  public final TimeInformation getAccurateTimeInformation()
  {
    return new TimeInformation( getTimeStamp(), synced.get() );
  }
  
  /**
   * Method to update the internal time offset
   * 
   * @param context
   *          the context
   * 
   * @return true if successful, false otherwise
   */
  public final boolean updateTime( Context context )
  {
    synced.set( false );
    long ts = getTimeStamp();
    
    Logger.getInstance().debug(
        this, "Time provider time update was triggred ... " );
    
    if ( syncTime( context ) )
    {
      notifyUpdated();
      return true;
    }
    
    notifySyncError( ts );
    return false;
  }
  
  /**
   * Method to update the internal time offset asynchronously
   * 
   * @param context
   *          the context
   */
  public final void asynchronousUpdateTime( Context context )
  {
    synced.set( false );
    notify( new TimeErrorEvent( getTimeStamp(), EventErrorTypes.OUT_OF_SYNC ) );
    
    if ( updateInProgress.compareAndSet( false, true ) )
    {
      updateThread = new UpdateThread( context );
      updateThread.startWork();
    }
  }
  
  /**
   * Method to stop an update asynchronous update of the internal time offset
   * 
   * @param context
   *          the context
   */
  public final void stopAsynchronousUpdate( Context context )
  {
    if ( updateInProgress.compareAndSet( true, false ) )
    {
      updateThread.stopWork();
      updateThread.doTerminate();
      updateThread = null;
    }
  }
  
  /**
   * Method to synchronize time with a NTP server
   * 
   * @return true if successful, false otherwise
   */
  private final boolean syncTime( Context context )
  {
    android.net.SntpClient sntpClient = new SntpClient();
    
    for ( String provider : getProviders() )
    {
      // after a wake up we do often fail with a route to host, even if network
      // is connected
      // thus we do force a route to host request here
      if ( !ConnectivityWrapperImpl.getInstance().testHostReachability(
          context, provider ) )
        Logger.getInstance().debug( this, "No route to host: " + provider );
      
      if ( sntpClient.requestTime( provider, 5000 ) )
      {
        long phoneTime = System.currentTimeMillis();
        long now =
            sntpClient.getNtpTime() + SystemClock.elapsedRealtime()
                - sntpClient.getNtpTimeReference();
        setOffset( phoneTime - now );
        lastUpdateTs.set( getTimeStamp() );
        synced.compareAndSet( false, true );
        break;
      }
    }
    
    return synced.get();
  }
  
  /**
   * Does create a long string representation of the given time stamp
   * 
   * @param timeStamp
   *          the milliseconds since 01.01.1970
   * @return the time stamp string
   */
  @SuppressLint( "SimpleDateFormat" ) // intended use of UTC time here
  public final static String toUTCString( long timeStamp )
  {
    SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss.S" );
    df.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
    Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ) );
    cal.setTimeInMillis( timeStamp );
    return df.format( cal.getTime() );
  }
  
  /**
   * Does create a time string representation of the given time stamp
   * 
   * @param timeStamp
   *          the milliseconds since 01.01.1970
   * @return the time string
   */
  @SuppressLint( "SimpleDateFormat" ) // intended use of UTC time here
  public final static String toUTCTime( long timeStamp )
  {
    SimpleDateFormat df = new SimpleDateFormat( "HH:mm:ss" );
    df.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
    Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ) );
    cal.setTimeInMillis( timeStamp );
    return df.format( cal.getTime() );
  }
  
  /**
   * Does create a date string representation of the given time stamp
   * 
   * @param timeStamp
   *          the milliseconds since 01.01.1970
   * @return the date string
   */
  @SuppressLint( "SimpleDateFormat" ) // intended use of UTC time here
  public final static String toUTCDate( long timeStamp )
  {
    SimpleDateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
    df.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
    Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ) );
    cal.setTimeInMillis( timeStamp );
    return df.format( cal.getTime() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.ObservableEventSourceImpl#
   * onObserverRegistration
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  protected final void onObserverRegistration(
      EventObserver< ? extends TimeProviderEvent > observer )
  {
    // on registration inform the observer about current offset if available,
    // otherwise signal a missing time update
    if ( synced.get() )
    {
      notifyUpdated();
    }
    else
    {
      notifyOutOfSync( getTimeStamp() );
    }
  }
  
  /**
   * Method to send an sync notification
   */
  protected void notifyUpdated()
  {
    notify( new TimeUpdateEvent( lastUpdateTs.get(), offset.get() ) );
  }
  
  /**
   * Method to send an sync notification
   * 
   * @param ts
   *          the time stamp
   */
  protected void notifyOutOfSync( long ts )
  {
    notify( new TimeErrorEvent( ts, EventErrorTypes.OUT_OF_SYNC ) );
  }
  
  /**
   * Method to send an sync notification
   * 
   * @param ts
   *          the time stamp
   */
  protected void notifySyncError( long ts )
  {
    notify( new TimeErrorEvent( ts, EventErrorTypes.TIME_SYNC_ERROR ) );
  }
  
  /**
   * Getter for the currents day time milliseconds
   * 
   * @return the currents day time stamp
   */
  public final static long getUTCDayTimeMillis()
  {
    return getUTCDayTimeMillis( TimeProvider.getInstance().getTimeStamp() );
  }
  
  /**
   * Getter for the day time milliseconds for a given time stamp
   * 
   * @param ts
   *          the time stamp to get day time milliseconds for ( at 00:00:00.0
   *          Midnight )
   * @return the day time stamp
   */
  public final static long getUTCDayTimeMillis( long ts )
  {
    Calendar calTime = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ) );
    calTime.setTimeInMillis( ts );
    return getDayBegin( calTime ).getTimeInMillis();
  }
  
  /**
   * Getter for the day day start time stamp of a given calendar date
   * 
   * @param calValue
   *          the calendar date time to get day start for ( same day at 00:00:00.0
   *          Midnight )
   * @return the time stamp of the day
   */
  public final static Calendar getDayBegin( Calendar calValue )
  {
    Calendar dayTime = Calendar.getInstance( calValue.getTimeZone() );
    dayTime.clear();
    dayTime.set( Calendar.YEAR, calValue.get( Calendar.YEAR ) );
    dayTime.set( Calendar.MONTH, calValue.get( Calendar.MONTH ) );
    dayTime.set( Calendar.DAY_OF_MONTH, calValue.get( Calendar.DAY_OF_MONTH ) );
    return dayTime;
  }
  
  /**
   * Test method for day changes
   * 
   * @param curUTCTime
   *          current time provider time
   * @return true if day has just changed
   */
  public final static boolean isUTCDayChange( Long curUTCTime )
  {
    // test for a valid available time first
    if ( curUTCTime != null )
    {
      // get offset between time stamp and midnight of the same day
      long offset = curUTCTime - getUTCDayTimeMillis( curUTCTime );
      // tolerance is 15 seconds after midnight
      if ( offset >= 0L && offset < 15000L )
      {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Test method for time provider synchronization state
   * 
   * @return true if synchronized with NTP time
   */
  public boolean isSynced()
  {
    return synced.get();
  }
  
}
