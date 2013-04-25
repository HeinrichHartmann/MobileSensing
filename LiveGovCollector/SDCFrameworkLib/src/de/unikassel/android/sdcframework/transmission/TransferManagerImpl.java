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
package de.unikassel.android.sdcframework.transmission;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.os.SystemClock;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.SampleCollection;
import de.unikassel.android.sdcframework.persistence.RemoveSamplesCommand;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseManager;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseSample;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy;
import de.unikassel.android.sdcframework.transmission.facade.TransferManager;
import de.unikassel.android.sdcframework.util.AbstractWorkerThread;
import de.unikassel.android.sdcframework.util.AlarmBuilder;
import de.unikassel.android.sdcframework.util.AlarmEvent;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.NetworkConnectionObserver;
import de.unikassel.android.sdcframework.util.NotificationUtils;
import de.unikassel.android.sdcframework.util.ObservableAlarm;
import de.unikassel.android.sdcframework.util.WakeLockHolder;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.NetworkStateChangeEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Implementation of the transfer manager which is the main access component for
 * the transmission module.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TransferManagerImpl
    extends AbstractWorkerThread
    implements TransferManager, EventObserver< NetworkStateChangeEvent >
{
  /**
   * The time to wait at maximum for configuration changes if transfer fails due
   * to invalid settings
   */
  private static final int WAIT_TIME_FOR_CONFIG_CHANGES = 300000;
  
  /**
   * The internal COLLECTING state of the transfer service
   * 
   */
  public final static int INIT = 0;
  
  /**
   * The internal COLLECTING state of the transfer service
   * 
   */
  public final static int COLLECTING = INIT + 1;
  
  /**
   * The internal PREPARATION state of the transfer service
   * 
   */
  public final static int PREPARATION = COLLECTING + 1;
  
  /**
   * The internal transmission state of the transfer service
   * 
   */
  public final static int TRANSMISSION = PREPARATION + 1;
  
  /**
   * The internal lower limit of wait time in seconds
   */
  public static final long MIN_FREQUENCY = 30L;
  
  /**
   * The start delay after transfer manager is activated.
   */
  public static final long INITIAL_DELAY = 30000;
  
  /**
   * The internal wake up time in case of lost connection ( milli seconds )
   */
  private static final long CONNECTION_WAKE_UP_TIME = 360000;
  
  /**
   * The database adapter used for DB access
   */
  private final DatabaseManager dbManager;
  
  /**
   * The manager for the file access and archive creation
   */
  private final FileManager fileManager;
  
  /**
   * The upload manager
   */
  private final UploadManager uploadManager;
  
  /**
   * The current state
   */
  private final AtomicInteger currentState;
  
  /**
   * The network connection observer
   */
  private final NetworkConnectionObserver connectionObserver;
  
  /**
   * The wait lock for unavailable connection
   */
  private final Object connectionWaitLock;
  
  /**
   * The wait lock for invalid url
   */
  private final Object protocolWaitLock;
  
  /**
   * The frequency wait lock
   */
  private final Object frequencyWaitLock;
  
  /**
   * The sample rate wait lock
   */
  private final Object sampleWaitLock;
  
  /**
   * The minimal transfer frequency in milliseconds
   */
  private final AtomicLong minFrequency;
  
  /**
   * The time stamp of last execution cycle
   */
  private final AtomicLong lastExecutionTimeStamp;
  
  /**
   * The current sample collection to transfer
   */
  private final SampleCollection currentSamples;
  
  /**
   * The controller for the sample gather task
   */
  private final SampleGatheringController gatheringController;
  
  /**
   * The observable wake up alarm
   */
  private final ObservableAlarm alarm;
  
  /**
   * The observable wake up alarm for forced transmission
   */
  private final ObservableAlarm forcedActivationAlarm;
  
  /**
   * The alarm observer to delegate to
   */
  private final EventObserver< AlarmEvent > alarmObserver;
  
  /**
   * The wake lock maintainer
   */
  private final WakeLockHolder wakeLockHolder;
  
  /**
   * Constructor
   * 
   * @param applicationContext
   *          the application context
   * @param config
   *          the current transmission configuration
   * @param dbManager
   *          the database manager to use
   * @param uuid
   *          the unique device identifier created by the service
   * @param controlActivityClass
   *          the control activity class or null
   */
  public TransferManagerImpl( Context applicationContext,
      TransmissionConfiguration config, DatabaseManager dbManager, UUID uuid,
      Class< ? > controlActivityClass )
  {
    super();
    if ( dbManager == null )
      throw new InvalidParameterException( "database manager is null" );
    if ( applicationContext == null )
      throw new InvalidParameterException( "context is null" );
    if ( config == null )
      throw new InvalidParameterException( "config is null" );
    if ( uuid == null )
      throw new InvalidParameterException( "uuid is null" );
    this.fileManager = new FileManager( applicationContext, config, uuid );
    this.dbManager = dbManager;
    this.uploadManager =
        new UploadManager( applicationContext, config, uuid,
            controlActivityClass );
    this.currentSamples = new SampleCollection();
    this.currentState = new AtomicInteger();
    this.minFrequency = new AtomicLong();
    this.connectionObserver =
        NetworkConnectionObserver.getInstance( applicationContext );
    this.connectionWaitLock = new Object();
    this.protocolWaitLock = new Object();
    this.frequencyWaitLock = new Object();
    this.sampleWaitLock = new Object();
    this.lastExecutionTimeStamp = new AtomicLong();
    this.gatheringController = new SampleGatheringController();
    this.alarm = AlarmBuilder.createAlarm( this, applicationContext );
    this.forcedActivationAlarm = AlarmBuilder.createAlarm( this, applicationContext );
    this.wakeLockHolder = new WakeLockHolder( applicationContext );
    Logger.getInstance().debug( this,
        wakeLockHolder.hashCode() + ": wake lock holder created" );
    
    this.alarmObserver = new EventObserver< AlarmEvent >()
    {
      @Override
      public void onEvent(
          ObservableEventSource< ? extends AlarmEvent > eventSource,
          AlarmEvent observedEvent )
      {
        // cancel any pending alarm
        alarm.cancelAlarm();
        int state = currentState.get();
        
        switch ( state )
        {
          case INIT:
          {
            doSignalFrequencyChange();
            break;
          }
          case COLLECTING:
            {
            doSignalSampleRateChanged();
            break;
          }
          case TRANSMISSION:
          {
            doSignalConnectionStateChange();
            doSignalProtocolChange();
          }
        }
      }
    };
    
    updateConfiguration( applicationContext, config );
  }
  
  @Override
  public synchronized void forcedActivation()
  {
    // delayed activation, allows e.g. the further processing of memory cached
    // samples
    forcedActivationAlarm.setAlarm( 1000L );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onCreate(
   * android.content.Context)
   */
  @Override
  public final void onCreate( Context applicationContext )
  {
    currentState.set( INIT );
    alarm.onCreate( applicationContext );
    forcedActivationAlarm.onCreate( applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onResume(
   * android.content.Context)
   */
  @Override
  public final void onResume( Context applicationContext )
  {
    gatheringController.reset( getCurrentRecordCount() );
    alarm.onResume( applicationContext );
    alarm.registerEventObserver( alarmObserver );
    forcedActivationAlarm.onResume( applicationContext );
    forcedActivationAlarm.registerEventObserver( alarmObserver );
    lastExecutionTimeStamp.set( SystemClock.elapsedRealtime() + INITIAL_DELAY );
    startWork();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onPause(android
   * .content.Context)
   */
  @Override
  public final void onPause( Context applicationContext )
  {
    forcedActivationAlarm.unregisterEventObserver( alarmObserver );
    alarm.unregisterEventObserver( alarmObserver );
    forcedActivationAlarm.onPause( applicationContext );
    alarm.onPause( applicationContext );
    stopWork();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onDestroy
   * (android.content.Context)
   */
  @Override
  public void onDestroy( Context applicationContext )
  {
    alarm.onDestroy( applicationContext );
    forcedActivationAlarm.onDestroy( null );
    doTerminate();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.AbstractWorkerThread#doCleanUp()
   */
  @Override
  protected final void doCleanUp()
  {
    if ( currentSamples.size() > 0 )
    {
      Logger.getInstance().info( this, "Files in queue, creating archive!" );
      if ( !doPrepareArchive() )
      {
        Logger.getInstance().error(
            this,
            "Failed to store collected samples in archive! "
                + currentSamples.size() + " samples lost!" );
      }
      currentSamples.clear();
    }
    fileManager.doCleanUp( false );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.transmission.facade.
   * UpdatableTransmissionComponent#updateConfiguration(android.content.Context,
   * de
   * .unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * )
   */
  @Override
  public synchronized final void updateConfiguration( Context context,
      TransmissionConfiguration config )
  {
    // minimum of transfer frequency will be limited to 60 seconds
    long minTransferFrequency =
        Math.max( MIN_FREQUENCY, config.getMinTransferFrequency() );
    minTransferFrequency *= 1000;
    
    if ( minFrequency.get() != minTransferFrequency )
    {
      this.minFrequency.set( minTransferFrequency );
      doSignalFrequencyChange();
    }
    
    int oldMinSampleCount = gatheringController.getMinSampleCount();
    gatheringController.updateConfiguration( context, config );
    if ( oldMinSampleCount != gatheringController.getMinSampleCount() )
    {
      doSignalSampleRateChanged();
    }
    
    fileManager.updateConfiguration( context, config );
    
    uploadManager.updateConfiguration( context, config );
    synchronized ( protocolWaitLock )
    {
      protocolWaitLock.notifyAll();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.AbstractWorkerThread#doWork()
   */
  @Override
  protected final void doWork()
  {
    try
    {
      int state = currentState.get();
      
      // follow frequency timing
      
      switch ( state )
      {
        case INIT:
        {
          // wait for demanded minimum frequency
          long currentMillis = SystemClock.elapsedRealtime();
          long waitTime =
              lastExecutionTimeStamp.get() + minFrequency.get() - currentMillis;
          
          if ( waitTime > 0L )
          {
            alarm.setAlarm( waitTime );
            
            synchronized ( frequencyWaitLock )
            {
              Logger.getInstance().debug( this,
                  "Waiting for next turn " + waitTime + " ms" );
              frequencyWaitLock.wait( waitTime );
            }
            
            alarm.cancelAlarm();
          }
          
          // update execution time stamp
          lastExecutionTimeStamp.set( SystemClock.elapsedRealtime() );
          currentState.set( COLLECTING );
          break;
        }
        case COLLECTING:
        {
          // is there an old file to transmit from last run?
          if ( fileManager.hasArchive() )
          {
            currentState.set( TRANSMISSION );
            Logger.getInstance().info( this, "Found a not transmitted archive!" );
            break;
          }
          
          long currentRecordCount = getCurrentRecordCount();
          long waitTime =
              gatheringController.calculatetWaitTime( currentRecordCount );
          
          if ( waitTime <= 0L )
          {
            Logger.getInstance().info(this, "Preparing " + currentRecordCount + " samples.");
            
            // collect available samples
            doPickSamplesFromDatabase();
            currentState.set( PREPARATION );
          }
          else
          {
            doWaitForSamples( waitTime );
          }
          break;
        }
        case PREPARATION:
        {
          // prepare archive for transmission
          if ( doPrepareArchive() )
          {
            currentState.set( TRANSMISSION );
          }
          break;
        }
        case TRANSMISSION:
        {
          // transfer archive
          if ( doTransferArchive() )
          {
            // prepare next transfer cycle
            currentState.set( INIT );
          }
          else
          {
            doReactOnUploadError();
          }
          break;
        }
      }
    }
    catch ( InterruptedException e )
    {}
  }
  
  /**
   * Method to wait for samples available for transmission
   * 
   * @param waitTime
   *          the time to wait
   * @throws InterruptedException
   *           in case of thread interruption
   */
  public void doWaitForSamples( long waitTime ) throws InterruptedException
  {
    // wait for enough samples
    Logger.getInstance().debug( this,
        "Waiting for samples " + waitTime + " ms" );
    
    alarm.setAlarm( waitTime );
    
    synchronized ( sampleWaitLock )
    {
      sampleWaitLock.wait( waitTime );
    }
    
    alarm.cancelAlarm();
  }
  
  /**
   * @throws InterruptedException
   */
  public void doReactOnUploadError() throws InterruptedException
  {
    ProtocolStrategy protocolStrategy = uploadManager.getProtocolStrategy();
    Context context = protocolStrategy.getContext();
    
    if ( !ConnectivityWrapperImpl.getInstance().isAnyConnectionAvailable(
         context ) )
    {
      // no Internet connection available
      Logger.getInstance().warning( this,
          "Upload failed! Waiting for available connection." );
      
      // wait for connectivity
      try
      {
        connectionObserver.registerEventObserver( this );
        
        // set an alarm for a wake up after
        alarm.setAlarm( CONNECTION_WAKE_UP_TIME );
        
        synchronized ( connectionWaitLock )
        {
          connectionWaitLock.wait();
        }
        
        alarm.cancelAlarm();
        
        NotificationUtils.cancelServiceNotification(
            protocolStrategy.getContext(),
            AbstractConnectionStrategy.NOTIFICATION );
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
      Logger.getInstance().warning( this, "Upload failed! Protocol error." );
      
      // wait URL setting changed
      synchronized ( protocolWaitLock )
      {
        // wait a maximum of milliseconds for configuration changes
        protocolWaitLock.wait( WAIT_TIME_FOR_CONFIG_CHANGES );
      }
      
      NotificationUtils.cancelServiceNotification(
          protocolStrategy.getContext(),
            AbstractConnectionStrategy.NOTIFICATION );
    }
  }
  
  /**
   * Does upload the archive to the configured host
   * 
   * @return true if successful, false otherwise
   */
  private final boolean doTransferArchive()
  {
    wakeLockHolder.acquireWakeLock();
    try
    {
      if ( !connectionObserver.isConnected() )
      {
        Thread.sleep( 2000 );
      }
      String currentArchiveName = fileManager.getCurrentArchive();
      if ( uploadManager.uploadFile( currentArchiveName ) )
      {
        Long fileSize = FileUtils.fileFromPath( currentArchiveName ).length() / 1024;
        fileManager.doCleanUp( true );
        Logger.getInstance().info(
            this,
            "Successful file ("
                + FileUtils.fileNameFromPath( currentArchiveName )
                + ", "
                + fileSize
                + "kb) transfer!" );
        return true;
      }
    }
    catch ( InterruptedException e )
    {}
    finally
    {
      wakeLockHolder.releaseWakeLock();
    }
    
    return false;
  }
  
  /**
   * Does create the archive for transmission
   * 
   * @return true if successful, false otherwise
   */
  private final boolean doPrepareArchive()
  {
    long time = SystemClock.uptimeMillis();
    if ( fileManager.createArchive( currentSamples ) )
    {
      currentSamples.clear();
      time = ( SystemClock.uptimeMillis() - time ) / 1000;
      Logger.getInstance().debug( this,
          "archive created in " + time + " s" );
      return true;
    }
    return false;
  }
  
  /**
   * Does remove available samples from the database.
   */
  private final void doPickSamplesFromDatabase()
  {
    long countToRemove = gatheringController.getAvailableSampleCount();
    
    if ( countToRemove > 0L )
    {
      long time = SystemClock.uptimeMillis();
      Collection< DatabaseSample > sc = new Vector< DatabaseSample >();
      RemoveSamplesCommand command =
          new RemoveSamplesCommand( sc, countToRemove );
      if ( dbManager.doExecuteCommand( command ) != null )
      {
        time = ( SystemClock.uptimeMillis() - time ) / 1000;
        Logger.getInstance().debug( this,
            sc.size() + " samples removed from DB in " + time + " s" );
        
        addSamples( sc );
        gatheringController.consumAvailableSamples();
      }
    }
  }
  
  /**
   * Does add a collection of database samples to the current sample collection
   * for transmission
   * 
   * @param samples
   *          the database sample collection to add to current sample collection
   */
  private final void addSamples( Collection< DatabaseSample > samples )
  {
    long time = SystemClock.uptimeMillis();
    for ( DatabaseSample dbSample : samples )
    {
      Sample sample = dbSample.toSample();
      if ( sample != null )
      {
        currentSamples.add( sample );
      }
      else
      {
        Logger.getInstance().error( this,
            "failed to create sample from database representation" );
      }
    }
    time = ( SystemClock.uptimeMillis() - time ) / 1000;
    Logger.getInstance().debug( this,
        samples.size() + " samples converted in " + time + " s" );
  }
  
  /**
   * Getter for the record count in the database
   * 
   * @return the current available record count
   */
  private long getCurrentRecordCount()
  {
    Long recordCount = dbManager.getRecordCountInDatabase();
    return recordCount == null ? 0L : recordCount;
  }
  
  /**
   * Getter for the minimum frequency to set
   * 
   * @return the minimum frequency to set
   */
  public final long getMinFrequency()
  {
    return minFrequency.get() / 1000;
  }
  
  /**
   * Getter for the minimum sample count to transfer
   * 
   * @return the minimum sample count to transfer
   */
  public final int getMinSampleCount()
  {
    return gatheringController.getMinSampleCount();
  }
  
  /**
   * Getter for the maximum sample count to transfer
   * 
   * @return the maximum sample count to transfer
   */
  public final int getMaxSampleCount()
  {
    return gatheringController.getMaxSampleCount();
  }
  
  /**
   * Getter for the current state
   * 
   * @return the current state
   */
  public final int getCurrentState()
  {
    return currentState.get();
  }
  
  /**
   * Getter for the time stamp of last execution
   * 
   * @return the time stamp of last execution
   */
  public final long getTimeStamp()
  {
    return lastExecutionTimeStamp.get();
  }
  
  /**
   * Method to access the filename of the current archive
   * 
   * @return the name of the current archive file
   */
  public final String getCurrentArchiveFileName()
  {
    return fileManager.getCurrentArchive();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de.
   * unikassel.android.sdcframework.util.facade.ObservableEventSource,
   * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public void onEvent(
      ObservableEventSource< ? extends NetworkStateChangeEvent > eventSource,
      NetworkStateChangeEvent observedEvent )
  {
    if ( observedEvent.isNetworkAvailable() )
    {
      doSignalConnectionStateChange();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.transmission.facade.TransferManager#
   * onSampleRateChanged()
   */
  @Override
  public void onSampleRateChanged()
  {
    gatheringController.onSampleRateChanged();
    doSignalSampleRateChanged();
  }
  
  /**
   * Method to signal protocol changes
   */
  protected void doSignalProtocolChange()
  {
    synchronized ( protocolWaitLock )
    {
      protocolWaitLock.notifyAll();
    }
  }
  
  /**
   * Method to signal connection state changes
   */
  protected void doSignalConnectionStateChange()
  {
    synchronized ( connectionWaitLock )
    {
      connectionWaitLock.notifyAll();
    }
  }
  
  /**
   * Method to signal sample rate changes
   */
  protected void doSignalSampleRateChanged()
  {
    // signal sample gathering setting changed
    synchronized ( sampleWaitLock )
    {
      sampleWaitLock.notifyAll();
    }
  }
  
  /**
   * Method to signal frequency changes
   */
  protected void doSignalFrequencyChange()
  {
    // signal frequency change
    synchronized ( frequencyWaitLock )
    {
      frequencyWaitLock.notifyAll();
    }
  }
}
