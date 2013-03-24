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
package de.unikassel.android.sdcframework.service;

import android.content.Context;
import android.util.Log;
import de.unikassel.android.sdcframework.devices.SensorDeviceConfigurationUpdateVisitor;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationChangeEventImpl;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfigurationChangeEvent;
import de.unikassel.android.sdcframework.service.facade.ServiceManager;
import de.unikassel.android.sdcframework.util.AbstractTimeProviderErrorStrategy;
import de.unikassel.android.sdcframework.util.AlarmBuilder;
import de.unikassel.android.sdcframework.util.AlarmEvent;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.NetworkConnectionObserver;
import de.unikassel.android.sdcframework.util.ObservableAlarm;
import de.unikassel.android.sdcframework.util.TimeErrorEvent;
import de.unikassel.android.sdcframework.util.TimeProvider;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.NetworkStateChangeEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;

/**
 * Simple ignore strategy in case of time provider synchronization errors.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TimeProviderOutOfSyncIgnoreStrategy
    extends AbstractTimeProviderErrorStrategy
    implements EventObserver< NetworkStateChangeEvent >
{
  
  /**
   * The time offset for synchronization retries.
   */
  private static final long RETRY_SYNC_OFFSET = 600000L;// 3600000L;
  
  /**
   * The alarm for retry attempts.
   */
  private final ObservableAlarm alarm;
  
  /**
   * The alarm observer to delegate to.
   */
  private final EventObserver< AlarmEvent > alarmObserver;
  
  /**
   * The service manager.
   */
  private final ServiceManager manager;
  
  /**
   * The flag is the time sync state device was enabled or not.
   */
  private boolean timeSyncStateDeviceWasEnabeled;
  
  /**
   * The network connection observer
   */
  private final NetworkConnectionObserver connectionObserver;
  
  /**
   * Constructor
   * 
   * @param manager
   *          the service manager
   */
  public TimeProviderOutOfSyncIgnoreStrategy( ServiceManager manager )
  {
    super( TimeProviderErrorStrategyDescription.IgnoreAndObserveSyncStates );
    this.manager = manager;
    this.alarm = AlarmBuilder.createAlarm( this, manager.getContext() );
    this.alarmObserver = new EventObserver< AlarmEvent >()
    {
      
      @Override
      public void onEvent(
          ObservableEventSource< ? extends AlarmEvent > eventSource,
          AlarmEvent observedEvent )
      {
        startSyncProcess();
      }
      
    };
    
    alarm.onCreate( manager.getContext() );
    alarm.registerEventObserver( alarmObserver );
    connectionObserver =
        NetworkConnectionObserver.getInstance( manager.getContext() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategy
   * #handleTimeErrorEvent
   * (de.unikassel.android.sdcframework.util.TimeErrorEvent,
   * de.unikassel.android.sdcframework.service.facade.ServiceManager)
   */
  @Override
  public final boolean handleTimeErrorEvent( TimeErrorEvent event,
      ServiceManager serviceManager )
  {
    switch ( event.getError() )
    {
      case TIME_SYNC_ERROR:
      {
        // schedule a retry attempt
        Logger.getInstance().debug( this,
            "Scheduling a new attempt for time synchronization" );
        alarm.setAlarm( RETRY_SYNC_OFFSET );
        connectionObserver.registerEventObserver( this );
        return true;
      }
    }
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategy
   * #enable(android.content.Context)
   */
  @Override
  public final void prepare( Context context )
  {
    alarm.onResume( context );
    
    // force the time sync state device to be enabled
    SensorDeviceConfiguration config =
        manager.getPreferenceManager().getDeviceConfiguration(
            SensorDeviceIdentifier.TimeSyncStateChanges, context );
    timeSyncStateDeviceWasEnabeled = config.isEnabled();
    
    if ( !timeSyncStateDeviceWasEnabeled )
    {
      config.setEnabled( true );
      SensorDeviceConfigurationChangeEvent deviceChangeEvent =
          new SensorDeviceConfigurationChangeEventImpl( config,
              SensorDeviceIdentifier.TimeSyncStateChanges );
      SensorDeviceVisitor visitor =
          new SensorDeviceConfigurationUpdateVisitor( deviceChangeEvent,
              context );
      manager.getSensorDeviceManager().accept( visitor );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategy
   * #disable(android.content.Context)
   */
  @Override
  public final void finalize( Context context )
  {
    alarm.onPause( context );
    connectionObserver.unregisterEventObserver( this );
    
    if ( timeSyncStateDeviceWasEnabeled )
    {
      // reset to configuration states
      SensorDeviceConfiguration config =
          manager.getPreferenceManager().getDeviceConfiguration(
              SensorDeviceIdentifier.TimeSyncStateChanges, context );
      SensorDeviceConfigurationChangeEvent deviceChangeEvent =
          new SensorDeviceConfigurationChangeEventImpl( config,
              SensorDeviceIdentifier.TimeSyncStateChanges );
      SensorDeviceVisitor visitor =
          new SensorDeviceConfigurationUpdateVisitor( deviceChangeEvent,
              context );
      manager.getSensorDeviceManager().accept( visitor );
    }
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
    if( observedEvent.isNetworkAvailable() )
    {
      startSyncProcess();
    }
  }
  
  /**
   * Method to trigger a synchronization process
   */
  protected void startSyncProcess()
  {
    Log.d( this.getClass().getSimpleName(), "startSyncProcess called" );
    if ( !TimeProvider.getInstance().isSynced() )
    {
      Logger.getInstance().debug( this,
          "Started a new asynchronous time synchronization" );
      TimeProvider.getInstance().asynchronousUpdateTime(
          TimeProviderOutOfSyncIgnoreStrategy.this.manager.getContext() );
    }
    alarm.cancelAlarm();
    connectionObserver.unregisterEventObserver( this );
    Log.d( this.getClass().getSimpleName(), "startSyncProcess finished" );
  }
  
}
