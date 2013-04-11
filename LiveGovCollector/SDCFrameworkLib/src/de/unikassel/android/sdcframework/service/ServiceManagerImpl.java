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

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.broadcast.BatteryLowStateObserver;
import de.unikassel.android.sdcframework.broadcast.SampleBroadcastServiceImpl;
import de.unikassel.android.sdcframework.broadcast.facade.SampleBroadcastService;
import de.unikassel.android.sdcframework.devices.SensorDeviceAvailabilityTester;
import de.unikassel.android.sdcframework.devices.SensorDeviceConfigurationUpdateVisitor;
import de.unikassel.android.sdcframework.devices.SensorDeviceManagerImpl;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceManager;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor;
import de.unikassel.android.sdcframework.persistence.DatabaseManagerImpl;
import de.unikassel.android.sdcframework.persistence.PersistentStorageManagerImpl;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseManager;
import de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.preferences.SDCConfigurationManager;
import de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.preferences.facade.LogLevelConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.service.facade.ServiceManager;
import de.unikassel.android.sdcframework.transmission.TransferManagerImpl;
import de.unikassel.android.sdcframework.transmission.facade.TransferManager;
import de.unikassel.android.sdcframework.util.BatteryLowEvent;
import de.unikassel.android.sdcframework.util.LifeCycleObjectImpl;
import de.unikassel.android.sdcframework.util.LogfileManager;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.NotificationUtils;
import de.unikassel.android.sdcframework.util.TimeErrorEvent;
import de.unikassel.android.sdcframework.util.TimeProvider;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategy;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;
import de.unikassel.android.sdcframework.util.facade.TimeProviderEvent;

/**
 * The Implementation of the sensor data collection service manager. <br/>
 * <br/>
 * The service manager does maintain the available framework components for the
 * service. <br/>
 * The main features are
 * <ul>
 * <li>creation and access to the {@linkplain SensorDeviceManager sensor device
 * manager}, the {@linkplain ApplicationPreferenceManager application preference
 * manager}, the {@linkplain SampleBroadcastService sample broadcast service},
 * the
 * {@linkplain de.unikassel.android.sdcframework.broadcast.facade.LogEventBroadcastService
 * log event broadcast service}, the {@linkplain PersistentStorageManager
 * persistent storage manager}, the {@linkplain TransferManager sample transfer
 * manager} and {@linkplain SDCConfigurationManager the configuration
 * management},</li>
 * <li>maintaining, attaching or detaching of the observers for configuration
 * changes and sample processing services,</li>
 * <li>handling of configuration change events,</li>
 * <li>broadcasting of log events,</li>
 * <li>...</li>
 * </ul>
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class ServiceManagerImpl
    extends LifeCycleObjectImpl
    implements ServiceManager, EventObserver< BatteryLowEvent >
{
  /**
   * The low on battery shutdown message.
   */
  private static final String LOW_BATTERY_MSG = "Shutdown due to low battery!";
  
  /**
   * The notification id.
   */
  public final static int NOTIFICATION = R.id.ServiceNotification;
  
  /**
   * Counter for requests to enable sample broadcasts.
   */
  private final AtomicInteger broadcastsEnabledCounter;
  
  /**
   * The SDC service.
   */
  private SDCService service;
  
  /**
   * The service configuration.
   */
  private final ServiceConfiguration serviceConfig;
  
  /**
   * The sensor device manager.
   */
  private SensorDeviceManager deviceManager;
  
  /**
   * The preference manager.
   */
  private final ApplicationPreferenceManager preferenceManager;
  
  /**
   * The time provider event observer.
   */
  private EventObserver< TimeProviderEvent > timeProviderEventObserver;
  
  /**
   * The sensor device configuration change event observer.
   */
  private EventObserver< SensorDeviceConfigurationChangeEvent > deviceConfigEventObserver;
  
  /**
   * The log level change event observer.
   */
  private EventObserver< LogLevelConfigurationChangeEvent > logLevelConfigEventObserver;
  
  /**
   * The time provider change event observer.
   */
  private EventObserver< TimeProviderConfigurationChangeEvent > timeProviderConfigEventObserver;
  
  /**
   * The service configuration change event observer.
   */
  private EventObserver< ServiceConfigurationChangeEvent > serviceConfigEventObserver;
  
  /**
   * The wake lock to keep CPU running while service is active.
   */
  private PowerManager.WakeLock wakeLock;
  
  /**
   * The sample broadcast service.
   */
  private SampleBroadcastService sampleBroadcastService;
  
  /**
   * The database manager.
   */
  private DatabaseManager dbManager;
  
  /**
   * The persistent storage manager.
   */
  private PersistentStorageManager storageManager;
  
  /**
   * The sample transfer manager.
   */
  private TransferManager transferManager;
  
  /**
   * Flag for creation state.
   */
  private boolean isCreated;
  
  /**
   * The battery low state observer.
   */
  private BatteryLowStateObserver batteryLowObserver;
  
  /**
   * The time provider error strategy.
   */
  private TimeProviderErrorStrategy timeErrorStrategy;
  
  /**
   * Constructor
   * 
   * @param controlActivityClass
   *          the control activity class or null
   */
  public ServiceManagerImpl( Class< ? > controlActivityClass )
  {
    super();
    this.preferenceManager = new ApplicationPreferenceManagerImpl();
    this.serviceConfig = new ServiceConfigurationImpl();
    this.broadcastsEnabledCounter = new AtomicInteger();
    setCreated( false );
    
    // initially we use a shutdown strategy for the starting phase,
    // will be automatically changed to the configured one after creation
    timeErrorStrategy = new ShutdownStrategy();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.app.facade.ServiceManager#setSDCService
   * (de.unikassel.android.sdcframework.app.SDCService)
   */
  @Override
  public final void setSDCService( SDCService service )
  {
    // assure referential integrity
    SDCService oldService = this.service;
    if ( oldService != service )
    {
      if ( oldService != null )
      {
        this.service = null;
        oldService.setServiceManager( null );
      }
      
      this.service = service;
      
      if ( this.service != null )
      {
        this.service.setServiceManager( this );
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.app.facade.ServiceManager#getSDCService
   * ()
   */
  @Override
  public final SDCService getSDCService()
  {
    return service;
  }
  
  /**
   * Getter for the serviceConfig
   * 
   * @return the serviceConfig
   */
  public final ServiceConfiguration getServiceConfig()
  {
    return serviceConfig;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.facade.ServiceManager#
   * getPreferenceManager()
   */
  @Override
  public final ApplicationPreferenceManager getPreferenceManager()
  {
    return preferenceManager;
  }
  
  /**
   * Setter for the sensor device manager
   * 
   * @param deviceManager
   *          the sensor device manager to set
   */
  private final void setSensorDeviceManager( SensorDeviceManager deviceManager )
  {
    this.deviceManager = deviceManager;
  }
  
  @Override
  public final SensorDeviceManager getSensorDeviceManager()
  {
    if ( deviceManager == null )
    {
      setSensorDeviceManager( new SensorDeviceManagerImpl(
          getPreferenceManager() ) );
    }
    return deviceManager;
  }
  
  /**
   * Getter for the sensor device configuration event observer
   * 
   * @return the sensor device configuration event observer
   */
  @SuppressWarnings( "unused" )
  private final EventObserver< SensorDeviceConfigurationChangeEvent >
      getDeviceConfigurationEventObserver()
  {
    return deviceConfigEventObserver;
  }
  
  /**
   * Setter for the sensor device configuration event observer
   * 
   * @param deviceConfigEventObserver
   *          the sensor device configuration event observer to set
   */
  private final
      void
      setDeviceConfigurationEventObserver(
          EventObserver< SensorDeviceConfigurationChangeEvent > deviceConfigEventObserver )
  {
    if ( this.deviceConfigEventObserver != deviceConfigEventObserver )
    {
      if ( this.deviceConfigEventObserver != null )
      {
        // unregister old observer
        getPreferenceManager().unregisterEventObserver(
            this.deviceConfigEventObserver );
      }
      
      this.deviceConfigEventObserver = deviceConfigEventObserver;
      
      if ( this.deviceConfigEventObserver != null )
      {
        // register old observer
        getPreferenceManager().registerEventObserver(
            this.deviceConfigEventObserver );
      }
    }
  }
  
  /**
   * Getter for the log level configuration event observer
   * 
   * @return the log level configuration event observer
   */
  @SuppressWarnings( "unused" )
  private final EventObserver< LogLevelConfigurationChangeEvent >
      getLogLevelConfigEventObserver()
  {
    return logLevelConfigEventObserver;
  }
  
  /**
   * Setter for the the log level configuration event observer
   * 
   * @param logLevelConfigEventObserver
   *          the log level configuration event observer to set
   */
  private final
      void
      setLogLevelConfigEventObserver(
          EventObserver< LogLevelConfigurationChangeEvent > logLevelConfigEventObserver )
  {
    
    if ( this.logLevelConfigEventObserver != logLevelConfigEventObserver )
    {
      if ( this.logLevelConfigEventObserver != null )
      {
        // unregister old observer
        getPreferenceManager().unregisterEventObserver(
            this.logLevelConfigEventObserver );
      }
      
      this.logLevelConfigEventObserver = logLevelConfigEventObserver;
      
      if ( this.logLevelConfigEventObserver != null )
      {
        // register old observer
        getPreferenceManager().registerEventObserver(
            this.logLevelConfigEventObserver );
      }
    }
  }
  
  /**
   * Getter for the time provider configuration event observer
   * 
   * @return the time provider configuration event observer
   */
  @SuppressWarnings( "unused" )
  private final EventObserver< TimeProviderConfigurationChangeEvent >
      getTimeProviderConfigEventObserver()
  {
    return timeProviderConfigEventObserver;
  }
  
  /**
   * Setter for the the time provider configuration event observer
   * 
   * @param timeProviderConfigEventObserver
   *          the time provider configuration event observer to set
   */
  private final
      void
      setTimeProviderConfigEventObserver(
          EventObserver< TimeProviderConfigurationChangeEvent > timeProviderConfigEventObserver )
  {
    
    if ( this.timeProviderConfigEventObserver != timeProviderConfigEventObserver )
    {
      if ( this.timeProviderConfigEventObserver != null )
      {
        // unregister old observer
        getPreferenceManager().unregisterEventObserver(
            this.timeProviderConfigEventObserver );
      }
      
      this.timeProviderConfigEventObserver = timeProviderConfigEventObserver;
      
      if ( this.timeProviderConfigEventObserver != null )
      {
        // register old observer
        getPreferenceManager().registerEventObserver(
            this.timeProviderConfigEventObserver );
      }
    }
  }
  
  /**
   * Getter for the serviceConfigEventObserver
   * 
   * @return the serviceConfigEventObserver
   */
  public final EventObserver< ServiceConfigurationChangeEvent >
      getServiceConfigEventObserver()
  {
    return serviceConfigEventObserver;
  }
  
  /**
   * Setter for the serviceConfigEventObserver
   * 
   * @param serviceConfigEventObserver
   *          the serviceConfigEventObserver to set
   */
  public final
      void
      setServiceConfigEventObserver(
          EventObserver< ServiceConfigurationChangeEvent > serviceConfigEventObserver )
  {
    if ( this.serviceConfigEventObserver != serviceConfigEventObserver )
    {
      if ( this.serviceConfigEventObserver != null )
      {
        // unregister old observer
        getPreferenceManager().unregisterEventObserver(
            this.serviceConfigEventObserver );
      }
      
      this.serviceConfigEventObserver = serviceConfigEventObserver;
      
      if ( this.serviceConfigEventObserver != null )
      {
        // register old observer
        getPreferenceManager().registerEventObserver(
            this.serviceConfigEventObserver );
      }
    }
  }
  
  /**
   * Getter for the timeProviderEventObserver
   * 
   * @return the timeProviderEventObserver
   */
  public final EventObserver< TimeProviderEvent >
      getTimeProviderEventObserver()
  {
    return timeProviderEventObserver;
  }
  
  /**
   * Setter for the timeProviderEventObserver
   * 
   * @param timeProviderEventObserver
   *          the timeProviderEventObserver to set
   */
  public final void
      setTimeProviderEventObserver(
          EventObserver< TimeProviderEvent > timeProviderEventObserver )
  {
    if ( this.timeProviderEventObserver != timeProviderEventObserver )
    {
      if ( this.timeProviderEventObserver != null )
      {
        // unregister old observer
        TimeProvider.getInstance().unregisterEventObserver(
            this.timeProviderEventObserver );
      }
      
      this.timeProviderEventObserver = timeProviderEventObserver;
      
      if ( this.timeProviderEventObserver != null )
      {
        // register old observer
        TimeProvider.getInstance().registerEventObserver(
            this.timeProviderEventObserver );
      }
    }
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
      PowerManager powerManager = getSDCService().getPowerManager();
      setWakeLock( powerManager.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK,
          getClass().getSimpleName() ) );
    }
    return wakeLock;
  }
  
  /**
   * Getter for the sample broadcast service
   * 
   * @return the sample broadcast service
   */
  private final SampleBroadcastService getSampleBroadcastService()
  {
    return sampleBroadcastService;
  }
  
  /**
   * Setter for the sample broadcast service
   * 
   * @param sampleBroadcastService
   *          the sample broadcast service to set
   */
  public final void setSampleBroadcastService(
      SampleBroadcastService sampleBroadcastService )
  {
    this.sampleBroadcastService = sampleBroadcastService;
  }
  
  /**
   * Setter for the dbManager
   * 
   * @param dbManager
   *          the dbManager to set
   */
  private final void setDbManager( DatabaseManager dbManager )
  {
    this.dbManager = dbManager;
  }
  
  /**
   * Getter for the dbManager
   * 
   * @return the dbManager
   */
  private final DatabaseManager getDbManager()
  {
    return dbManager;
  }
  
  /**
   * Getter for the storageManager
   * 
   * @return the storageManager
   */
  private final PersistentStorageManager getStorageManager()
  {
    return storageManager;
  }
  
  /**
   * Setter for the storageManager
   * 
   * @param storageManager
   *          the storageManager to set
   */
  private final void
      setStorageManager( PersistentStorageManager storageManager )
  {
    this.storageManager = storageManager;
  }
  
  /**
   * Getter for the transferManager
   * 
   * @return the transferManager
   */
  public final TransferManager getTransferManager()
  {
    return transferManager;
  }
  
  /**
   * Setter for the transferManager
   * 
   * @param transferManager
   *          the transferManager to set
   */
  public final void setTransferManager( TransferManager transferManager )
  {
    this.transferManager = transferManager;
  }
  
  private void
      setBatteryLowObserver( BatteryLowStateObserver batteryLowObserver )
  {
    this.batteryLowObserver = batteryLowObserver;
  }
  
  private BatteryLowStateObserver getBatteryLowObserver()
  {
    if ( batteryLowObserver == null )
    {
      setBatteryLowObserver( new BatteryLowStateObserver() );
    }
    return batteryLowObserver;
  }
  
  /**
   * Setter for the created flag
   * 
   * @param isCreated
   *          the created flag to set
   */
  protected final synchronized void setCreated( boolean isCreated )
  {
    this.isCreated = isCreated;
  }
  
  /**
   * Getter for the created flag
   * 
   * @return the created flag
   */
  protected final synchronized boolean isCreated()
  {
    return isCreated;
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
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.LifeCycleObjectImpl#onCreate(android
   * .content.Context)
   */
  @Override
  public final void onCreate( Context applicationContext )
  {
    TimeProvider.getInstance().asynchronousUpdateTime( applicationContext );
    
    // configure logger first
    ApplicationPreferenceManager prefManager = getPreferenceManager();
    Logger.getInstance().setLogLevel(
        prefManager.getLogLevelConfiguration( applicationContext ).getLogLevel() );
    
    // create the global log file transfer task
    LogfileManager.createInstance( applicationContext,
        getUUID( applicationContext ) );
    
    // attach time provider event observer
    attachTimeProviderEventObserver();
    
    // get framework default configuration and update the preference defaults
    String configFile =
        applicationContext.getText( R.string.sdc_config_file_name ).toString();
    SDCConfigurationManager serviceConfigurationManager =
        new SDCConfigurationManager( applicationContext, configFile );
    
    // update configuration settings by configured values
    serviceConfigurationManager.updateDefaults( prefManager );
    
    // update service configuration by preferences
    serviceConfig.update( prefManager.getServiceConfiguration( applicationContext ) );
    
    // update time provider by preferences
    TimeProvider.getInstance().updateProviders(
        prefManager.getTimeProviderConfiguration( applicationContext ).getProviders() );
    
    // update sensor device preferences by pre-configured values
    // configure sensor device availability tester with sensors from
    // configuration file
    SensorDeviceAvailabilityTester.getInstance().configure(
        serviceConfigurationManager.getListDevices(), applicationContext );
    serviceConfigurationManager.onDestroy();
    
    // create the database manager
    String dbName =
        applicationContext.getText( R.string.sdc_database_name ).toString();
    createDatabaseManager( applicationContext, dbName );
    
    // create storage manager instance for configured database name
    createStorageManager( applicationContext );
    
    // create transfer manager instance
    createTransferManager( applicationContext );
    
    // create sample broadcast service
    setSampleBroadcastService( new SampleBroadcastServiceImpl(
        applicationContext, serviceConfig.getBroadcastFrequency() ) );
    
    // create maintained framework components
    getSensorDeviceManager().onCreate( applicationContext );
    getSampleBroadcastService().onCreate( applicationContext );
    getBatteryLowObserver().onCreate( applicationContext );
    
    // create and attach event observers for configuration changes
    attachEventObserversForConfigurationChanges();
    
    // update the time provider synchronization error strategy
    updateTimeSyncErrorStrategy( getPreferenceManager().getTimeProviderConfiguration(
        applicationContext ) );
    
    setCreated( true );
    super.onCreate( applicationContext );
  }
  
  /**
   * Method to create and configure the transfer manager
   * 
   * @param applicationContext
   *          the application context
   */
  public final void createTransferManager( Context applicationContext )
  {
    UUID uuid = getUUID( applicationContext );
    
    setTransferManager( new TransferManagerImpl( applicationContext,
        serviceConfig.getTransmissionConfiguration(), getDbManager(), uuid,
        getSDCService().getControlActivityClass() ) );
    getTransferManager().onCreate( applicationContext );
  }
  
  /**
   * Getter for the device identifier unique per service installation
   * 
   * @param applicationContext
   *          the application context
   * @return the unique service device identifier ( unique per installation )
   */
  private UUID getUUID( Context applicationContext )
  {
    UUID uuid = null;
    String sUuid =
        getPreferenceManager().getUUIDConfiguration( applicationContext );
    if ( sUuid.length() == 0 )
    {
      uuid = UUID.randomUUID();
      Editor editor =
          getPreferenceManager().getSharedPreferences( applicationContext ).edit();
      editor.putString( getPreferenceManager().getUUIDPreference().getKey(),
          uuid.toString() );
      editor.commit();
    }
    else
      uuid = UUID.fromString( sUuid );
    return uuid;
  }
  
  /**
   * Method to create the database manager
   * 
   * @param applicationContext
   *          the application context
   * @param dbName
   *          the data base name
   */
  public final void createDatabaseManager( Context applicationContext,
      String dbName )
  {
    setDbManager( new DatabaseManagerImpl( applicationContext, dbName ) );
    getDbManager().setMaximumDatabaseSize(
        serviceConfig.getMaximumDatabaseSize() );
  }
  
  /**
   * Method to create and configure the storage manager
   * 
   * @param applicationContext
   *          the application context
   */
  private final void createStorageManager( Context applicationContext )
  {
    setStorageManager( new PersistentStorageManagerImpl( applicationContext,
        serviceConfig, getDbManager(), getSDCService().getClass(),
        getSDCService().getControlActivityClass() ) );
    getStorageManager().onCreate( applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.LifeCycleObjectImpl#onResume(android
   * .content.Context)
   */
  @Override
  public final void onResume( Context applicationContext )
  {
    super.onResume( applicationContext );
    
    // second try to update time if not up to date
    if ( !TimeProvider.getInstance().isSynced() )
    {
      TimeProvider.getInstance().updateTime(
          getContext() );
    }
    
    // do only resume the internal services if time is available
    if ( TimeProvider.getInstance().isSynced() ||
        TimeProviderErrorStrategyDescription.IgnoreAndObserveSyncStates.equals(
            getPreferenceManager().getTimeProviderConfiguration(
                applicationContext ).getErrorStrategyDescription() ) )
    {
      // clear broadcast enabled counter
      broadcastsEnabledCounter.set( 0 );
      
      // start configuration change listening
      getPreferenceManager().startListening( applicationContext );
      
      // configure components with the current configuration
      updateSampleBroadcastService( serviceConfig, true );
      updateStorageManager( serviceConfig, true );
      updateTransferManager( serviceConfig, true );
      updateSamplingConfiguration( serviceConfig, true );
      updateLogTransferConfig( serviceConfig.getLogTransferConfiguration() );
      
      // resume the battery low state observer
      getBatteryLowObserver().onResume( applicationContext );
      getBatteryLowObserver().registerEventObserver( this );
      
      // acquire wake lock
      acquireWakeLock();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.LifeCycleObjectImpl#onPause(android
   * .content.Context)
   */
  @Override
  public final void onPause( Context applicationContext )
  {
    // stop any asynchronous processes and timer
    TimeProvider.getInstance().unregisterEventObserver(
        getTimeProviderEventObserver() );
    TimeProvider.getInstance().stopAsynchronousUpdate( applicationContext );
    timeErrorStrategy.finalize( applicationContext );
    
    // pause the battery low state observer
    getBatteryLowObserver().unregisterEventObserver( this );
    getBatteryLowObserver().onPause( applicationContext );
    
    // stop configuration change listening
    getPreferenceManager().stopListening(
        applicationContext );
    
    // pause maintained framework components
    getSensorDeviceManager().onPause( applicationContext );
    getStorageManager().onPause( applicationContext );
    getSampleBroadcastService().onPause( applicationContext );
    getTransferManager().onPause( applicationContext );
    
    // release wake lock
    releaseWakeLock();
    
    super.onPause( applicationContext );
  }
  
  /**
   * Does attach the event observer for time provider events
   */
  private final void attachTimeProviderEventObserver()
  {
    setTimeProviderEventObserver( new EventObserver< TimeProviderEvent >()
    {
      @Override
      public void onEvent(
          ObservableEventSource< ? extends TimeProviderEvent > eventSource,
          TimeProviderEvent observedEvent )
      {
        handleTimeProviderEvent( observedEvent );
      }
    } );
  }
  
  /**
   * Does attach event observers for configuration changes
   */
  private final void attachEventObserversForConfigurationChanges()
  {
    setDeviceConfigurationEventObserver( new EventObserver< SensorDeviceConfigurationChangeEvent >()
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * de.unikassel.android.sdcframework.util.AbstractEventObserver#onEvent
       * (de.unikassel .android.sdcframework.util.ObservableEventCreator,
       * de.unikassel.android.sdcframework.util.ObservableEvent)
       */
      @Override
      public final
          void
          onEvent(
                  ObservableEventSource< ? extends SensorDeviceConfigurationChangeEvent > eventSource,
                  SensorDeviceConfigurationChangeEvent observedEvent )
      {
        handleDeviceConfigurationChange( observedEvent );
      }
    } );
    
    setLogLevelConfigEventObserver( new EventObserver< LogLevelConfigurationChangeEvent >()
    {
      
      /*
       * (non-Javadoc)
       * 
       * @see
       * de.unikassel.android.sdcframework.util.AbstractEventObserver#onEvent
       * (de.unikassel .android.sdcframework.util.facade.ObservableEventCreator,
       * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
       */
      @Override
      public final
          void
          onEvent(
              ObservableEventSource< ? extends LogLevelConfigurationChangeEvent > eventSource,
              LogLevelConfigurationChangeEvent observedEvent )
      {
        handleLogLevelConfigurationChange( observedEvent );
      }
    } );
    
    setTimeProviderConfigEventObserver( new EventObserver< TimeProviderConfigurationChangeEvent >()
    {
      
      @Override
      public
          void
          onEvent(
              ObservableEventSource< ? extends TimeProviderConfigurationChangeEvent > eventSource,
              TimeProviderConfigurationChangeEvent observedEvent )
      {
        handleTimeProviderConfigurationChange( observedEvent );
      }
      
    } );
    
    setServiceConfigEventObserver( new EventObserver< ServiceConfigurationChangeEvent >()
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * de.unikassel.android.sdcframework.util.AbstractEventObserver#onEvent
       * (de.unikassel .android.sdcframework.util.facade.ObservableEventCreator,
       * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
       */
      @Override
      public final
          void
          onEvent(
              ObservableEventSource< ? extends ServiceConfigurationChangeEvent > eventSource,
              ServiceConfigurationChangeEvent observedEvent )
      {
        handleServiceConfigurationChange( observedEvent );
      }
    } );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.LifeCycleObjectImpl#onDestroy(android
   * .content.Context)
   */
  @Override
  public final void onDestroy( Context applicationContext )
  {
    setCreated( false );
    
    // destroy maintained framework components
    getTransferManager().onDestroy( applicationContext );
    getSensorDeviceManager().onDestroy( applicationContext );
    getSampleBroadcastService().onDestroy( applicationContext );
    getStorageManager().onDestroy( applicationContext );
    getPreferenceManager().onDestroy();
    getBatteryLowObserver().onDestroy( applicationContext );
    
    super.onDestroy( applicationContext );
    
    // release instance references
    setDbManager( null );
    setDeviceConfigurationEventObserver( null );
    setLogLevelConfigEventObserver( null );
    setSensorDeviceManager( null );
    setTimeProviderEventObserver( null );
    setWakeLock( null );
    setSampleBroadcastService( null );
    setStorageManager( null );
    setBatteryLowObserver( null );
  }
  
  /**
   * Handler for time provider events
   * 
   * @param event
   *          the event
   */
  private synchronized final void handleTimeProviderEvent(
      TimeProviderEvent event )
  {
    if ( event instanceof TimeErrorEvent )
    {
      timeErrorStrategy.handleTimeErrorEvent( (TimeErrorEvent) event, this );
    }
  }
  
  /**
   * Handler for sensor device configuration changes
   * 
   * @param deviceChangeEvent
   *          the change event
   */
  private final void handleDeviceConfigurationChange(
      SensorDeviceConfigurationChangeEvent deviceChangeEvent )
  {
    if ( serviceConfig.isSamplingEnabled() )
    {
      SensorDeviceVisitor visitor =
          new SensorDeviceConfigurationUpdateVisitor( deviceChangeEvent,
              getContext() );
      boolean updated = getSensorDeviceManager().accept( visitor );
      if ( updated )
      {
        // in any case of device configuration changes we do signal a possible
        // sample rate change to the transfer manager
        getTransferManager().onSampleRateChanged();
      }
    }
  }
  
  /**
   * Handler for log level configuration changes
   * 
   * @param loglevelChangeEvent
   *          the change event
   */
  private final void handleLogLevelConfigurationChange(
      LogLevelConfigurationChangeEvent loglevelChangeEvent )
  {
    Logger.getInstance().setLogLevel(
        loglevelChangeEvent.getConfiguration().getLogLevel() );
  }
  
  /**
   * Handler for time provider configuration changes
   * 
   * @param observedEvent
   */
  protected final void handleTimeProviderConfigurationChange(
      TimeProviderConfigurationChangeEvent observedEvent )
  {
    TimeProviderConfiguration config = observedEvent.getConfiguration();
    TimeProvider.getInstance().updateProviders( config.getProviders() );
    
    updateTimeSyncErrorStrategy( config );
  }
  
  /**
   * Method to update the error strategy for time provider sync errors
   * 
   * @param config
   *          the update configuration
   */
  private synchronized void updateTimeSyncErrorStrategy(
      TimeProviderConfiguration config )
  {
    TimeProviderErrorStrategyDescription errorStrategy =
        config.getErrorStrategyDescription();
    if ( timeErrorStrategy == null
        || !timeErrorStrategy.getDescription().equals(
            errorStrategy ) )
    {
      Context context = getContext();
      // finalize old strategy
      timeErrorStrategy.finalize( context );
      // create and prepare new strategy
      timeErrorStrategy =
          TimeProviderErrorStrategyBuilder.buildStrategy(
              errorStrategy, this );
      timeErrorStrategy.prepare( context );
    }
    
  }
  
  /**
   * Handler for service configuration changes
   * 
   * @param observedEvent
   */
  protected final void handleServiceConfigurationChange(
      ServiceConfigurationChangeEvent observedEvent )
  {
    ServiceConfiguration updateConfig = observedEvent.getConfiguration();
    
    // update additional sample configuration
    updateSamplingConfiguration( updateConfig, false );
    
    // update sample broadcast service
    updateSampleBroadcastService( updateConfig, false );
    
    // update storage manager
    updateStorageManager( updateConfig, false );
    
    // update transmission service
    updateTransferManager( updateConfig, false );
    
    // update log transfer configuration
    updateLogTransferConfig( updateConfig.getLogTransferConfiguration() );
  }
  
  /**
   * Method to update the log transfer configuration
   * 
   * @param updateConfig
   *          the configuration to update from
   */
  private final void updateLogTransferConfig(
      TransmissionProtocolConfiguration updateConfig )
  {
    TransmissionProtocolConfiguration logTransferConfig =
        serviceConfig.getLogTransferConfiguration();
    LogfileManager logTransferTask = LogfileManager.getInstance();
    logTransferConfig.update( updateConfig );
    logTransferTask.updateConfiguration(
        getContext(), updateConfig );
  }
  
  /**
   * Method to update additional sampling settings
   * 
   * @param updateConfig
   *          the configuration to update from
   * @param force
   *          flag if a state update should be forced
   */
  private final void updateSamplingConfiguration(
      ServiceConfiguration updateConfig, boolean force )
  {
    boolean isAddingSampleLocation = updateConfig.isAddingSampleLocation();    
    if( isAddingSampleLocation != serviceConfig.isAddingSampleLocation() )
    {
      serviceConfig.setIsAddingSampleLocation( isAddingSampleLocation );
      
      getSensorDeviceManager().enableLocationInfoPerSample(
          isAddingSampleLocation );
    }

    boolean isSamplingEnabled = updateConfig.isSamplingEnabled();
    if( force || (isSamplingEnabled != serviceConfig.isSamplingEnabled() ) )
    {
      serviceConfig.setSamplingEnabled( updateConfig.isSamplingEnabled() );
      if ( serviceConfig.isSamplingEnabled() )
      {
        getSensorDeviceManager().onResume( getContext() );
      }
      else
      {
        getSensorDeviceManager().onPause( getContext() );
      }
    }
  }
  
  /**
   * Method to update sample broadcasting by current service configuration
   * 
   * @param updateConfig
   *          the configuration to update from
   * @param force
   *          flag if a state update should be forced
   */
  private final void updateSampleBroadcastService(
      ServiceConfiguration updateConfig, boolean force )
  {
    SampleBroadcastService sampleBroadcastService =
        getSampleBroadcastService();
    
    // update running state
    boolean isBroadcastingSamples = updateConfig.isBroadcastingSamples();
    if ( force || ( isBroadcastingSamples != serviceConfig.isBroadcastingSamples() ) )
    {
      serviceConfig.setBroadcastingSamples( isBroadcastingSamples );
      
      Context applicationContext = getContext();
      if ( isBroadcastingSamples )
      {
        getSensorDeviceManager().registerEventObserver(
            sampleBroadcastService.getObserver() );
        sampleBroadcastService.onResume( applicationContext );
      }
      else
      {
        getSensorDeviceManager().unregisterEventObserver(
            sampleBroadcastService.getObserver() );
        sampleBroadcastService.onPause( applicationContext );
      }
    }
    
    long frequency = updateConfig.getBroadcastFrequency();
    if( frequency != serviceConfig.getBroadcastFrequency() )
    {
      serviceConfig.setBroadcastFrequency( frequency );
      sampleBroadcastService.updateFrequency( frequency );
    }
  }
  
  /**
   * Method to update persistent storage management by current service
   * configuration
   * 
   * @param updateConfig
   *          the configuration to update from
   * @param force
   *          flag if a state update should be forced
   */
  private final void updateStorageManager( ServiceConfiguration updateConfig, boolean force )
  {
    boolean isStoringSamples = updateConfig.isStoringSamples();
    
    // first update running state
    if( force || (isStoringSamples != serviceConfig.isStoringSamples() ) )
    {
      Context applicationContext = getContext();
      PersistentStorageManager storageManager = getStorageManager();
      serviceConfig.setStoringSamples( isStoringSamples );
      
      if ( isStoringSamples )
      {
        getSensorDeviceManager().registerEventObserver(
            storageManager.getObserver() );
        storageManager.onResume( applicationContext );
      }
      else
      {
        getSensorDeviceManager().unregisterEventObserver(
            storageManager.getObserver() );
        storageManager.onPause( applicationContext );
      }
    }
    
    // update configuration
    updateDatabaseMaximumSize( updateConfig );
    updateStrategyConfiguration( updateConfig );
  }
  
  /**
   * Method to update the database full strategy configuration values
   * 
   * @param updateConfig
   *          the configuration to update from
   */
  private final void updateStrategyConfiguration(
      ServiceConfiguration updateConfig )
  {
    serviceConfig.setDBFullStrategy( updateConfig.getDBFullStrategy() );
    serviceConfig.setDBFullDeletionPriorityBased( updateConfig.isDBFullDeletionPriorityBased() );
    serviceConfig.setDBFullDeletionRecordCount( updateConfig.getDBFullDeletionRecordCount() );
    serviceConfig.setDBFullWaitTime( updateConfig.getDBFullWaitTime() );
    
    getStorageManager().updateDatabaseFullStrategy(
        getContext(), serviceConfig, getSDCService().getControlActivityClass() );
  }
  
  /**
   * Method to update database size by current service configuration
   * 
   * @param updateConfig
   *          the configuration to update from
   */
  private final void updateDatabaseMaximumSize(
      ServiceConfiguration updateConfig )
  {
    if ( serviceConfig.getMaximumDatabaseSize() != updateConfig.getMaximumDatabaseSize() )
    {
      serviceConfig.setMaximumDatabaseSize( updateConfig.getMaximumDatabaseSize() );
      
      long maxSizeRequested = serviceConfig.getMaximumDatabaseSize();
      long currentMaxSize = getStorageManager().getMaximumDatabaseSize();
      
      if ( maxSizeRequested != currentMaxSize )
      {
        Long newMaxSize =
            getStorageManager().setMaximumDatabaseSize( maxSizeRequested );
        currentMaxSize = getStorageManager().getMaximumDatabaseSize();
        
        if ( newMaxSize.longValue() != maxSizeRequested )
        {
          // store the new size as configuration if it is different ( can happen
          // due to DB page alignment )
          SharedPreferences sharedPreferences =
              preferenceManager.getSharedPreferences( getContext() );
          SharedPreferences.Editor editor = sharedPreferences.edit();
          editor.putString(
              preferenceManager.getServicePreferences().getDBMaxSizePreference().getKey(),
              newMaxSize.toString() );
          editor.commit();
        }
      }
    }
  }
  
  /**
   * Method to update sample transfer management by current service
   * configuration
   * 
   * @param updateConfig
   *          the configuration to update from
   * @param force
   *          flag if a state update should be forced
   */
  private final void updateTransferManager( ServiceConfiguration updateConfig, boolean force )
  {
    Context applicationContext = getContext();
    TransferManager transferManager = getTransferManager();
    
    boolean isTransmittingSamples = updateConfig.isTransmittingSamples();
    if ( force || isTransmittingSamples != serviceConfig.isTransmittingSamples() )
    {
      // first update running state
      serviceConfig.setTransmittingSamples( isTransmittingSamples );
      if ( isTransmittingSamples )
      {
        transferManager.onResume( applicationContext );
      }
      else
      {
        transferManager.onPause( applicationContext );
      }
    }
    
    // update configuration
    TransmissionConfiguration transmissionConfiguration =
        serviceConfig.getTransmissionConfiguration();
    boolean hasChanged =
        !transmissionConfiguration.equals( updateConfig.getTransmissionConfiguration() );
    if ( hasChanged )
    {
      transmissionConfiguration.update( updateConfig.getTransmissionConfiguration() );
      transferManager.updateConfiguration( applicationContext,
          transmissionConfiguration );
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
  public final void onEvent(
      ObservableEventSource< ? extends BatteryLowEvent > eventSource,
      BatteryLowEvent observedEvent )
  {
    stopServiceByReason( LOW_BATTERY_MSG );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.facade.ServiceManager#
   * stopServiceByReason(java.lang.String)
   */
  @Override
  public final void stopServiceByReason( String msg )
  {
    Logger.getInstance().warning( this, msg );
    Context context = getContext();
    NotificationUtils.serviceNotification( NOTIFICATION, msg, context, true,
        false, getSDCService().getControlActivityClass() );
    getSDCService().stopSelf();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.service.facade.ServiceManager#getContext
   * ()
   */
  @Override
  public Context getContext()
  {
    return getSDCService().getApplicationContext();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.facade.ServiceManager#
   * doEnableSampleBroadCasting(boolean)
   */
  @Override
  public final synchronized void doEnableSampleBroadCasting( boolean doEnable )
  {
    if ( doEnable )
    {
      // increment usage counter
      broadcastsEnabledCounter.incrementAndGet();
      // do start broadcast service if necessary
      doChangeBroadcastServiceState( doEnable );
    }
    else
    {
      // reduce usage counter
      if ( broadcastsEnabledCounter.get() > 0 )
      {
        if ( broadcastsEnabledCounter.decrementAndGet() == 0 )
        {
          // do stop broadcast service necessary
          doChangeBroadcastServiceState( doEnable );
        }
      }
    }
  }
  
  /**
   * Method to change the broadcast state of the service
   * 
   * @param doEnable
   */
  public final void doChangeBroadcastServiceState( boolean doEnable )
  {
    preferenceManager.updatePreferenceState(
        getContext(),
        preferenceManager.getServicePreferences().getSampleBroadcastsEnabledPreference(),
        doEnable );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.facade.ServiceManager#
   * doEnableSampling(boolean)
   */
  @Override
  public final synchronized void doEnableSampling( boolean doEnable )
  {
    preferenceManager.updatePreferenceState(
        getContext(),
        preferenceManager.getServicePreferences().getSamplingEnabledPreference(),
        doEnable );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.facade.ServiceManager#
   * doEnableSampleStorage(boolean)
   */
  @Override
  public void doEnableSampleStorage( boolean doEnable )
  {
    preferenceManager.updatePreferenceState(
        getContext(),
        preferenceManager.getServicePreferences().getPersistentStorageEnabledPreference(),
        doEnable );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.facade.ServiceManager#
   * doEnableSampleTransfer(boolean)
   */
  @Override
  public final synchronized void doEnableSampleTransfer( boolean doEnable )
  {
    preferenceManager.updatePreferenceState(
        getContext().getApplicationContext(),
        preferenceManager.getServicePreferences().getTransmissionEnabledPreference(),
        doEnable );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.facade.ServiceManager#
   * doTriggerSampleTransfer()
   */
  @Override
  public final synchronized void doTriggerSampleTransfer()
  {
    doEnableSampleTransfer( true );
    getTransferManager().forcedActivation();
  }
}
