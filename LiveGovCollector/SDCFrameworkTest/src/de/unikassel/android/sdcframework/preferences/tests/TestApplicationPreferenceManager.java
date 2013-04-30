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
package de.unikassel.android.sdcframework.preferences.tests;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.data.WeeklySchedule;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl.ServiceConfigurationChangeEventSource;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl.TimeProviderConfigurationChangeEventSource;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationChangeEventImpl;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.ServicePreferencesImpl;
import de.unikassel.android.sdcframework.preferences.TimeProviderPreferenceImpl;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl.DeviceConfigurationChangeEventSource;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl.LogLevelConfigurationChangeEventSource;
import de.unikassel.android.sdcframework.preferences.facade.Configuration;
import de.unikassel.android.sdcframework.preferences.facade.LogLevelConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.ConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.ServicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfigurationChangeEvent;
import de.unikassel.android.sdcframework.util.AbstractWorkerThread;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;
import de.unikassel.android.sdcframework.util.facade.WorkerThread;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

/**
 * Tests for the application preferences manager.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestApplicationPreferenceManager
    extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    super.setUp();
  }
  
  /**
   * Private inner class implementing a sensor device configuration changed
   * observer.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestDeviceConfigObserver implements
      EventObserver< SensorDeviceConfigurationChangeEvent >
  {
    /**
     * public event list
     */
    public final ArrayList< SensorDeviceConfigurationChangeEvent > events =
        new ArrayList< SensorDeviceConfigurationChangeEvent >();
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de
     * .unikassel.android.sdcframework.util.facade.ObservableEventSource,
     * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
     */
    @Override
    public
        void
        onEvent(
            ObservableEventSource< ? extends SensorDeviceConfigurationChangeEvent > eventSource,
            SensorDeviceConfigurationChangeEvent observedEvent )
    {
      events.add( observedEvent );
    }
  }
  
  /**
   * Private inner class implementing a log level configuration changed
   * observer.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestLogLevelConfigObserver implements
      EventObserver< LogLevelConfigurationChangeEvent >
  {
    /**
     * Public event list
     */
    public final ArrayList< LogLevelConfigurationChangeEvent > events =
        new ArrayList< LogLevelConfigurationChangeEvent >();
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de
     * .unikassel.android.sdcframework.util.facade.ObservableEventSource,
     * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
     */
    @Override
    public
        void
        onEvent(
            ObservableEventSource< ? extends LogLevelConfigurationChangeEvent > eventSource,
            LogLevelConfigurationChangeEvent observedEvent )
    {
      events.add( observedEvent );
    }
    
  }
  
  /**
   * Private inner class implementing a time provider configuration changed
   * observer.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TimeProviderConfigObserver
      implements EventObserver< TimeProviderConfigurationChangeEvent >
  {
    /**
     * Public event list
     */
    public final ArrayList< TimeProviderConfigurationChangeEvent > events =
        new ArrayList< TimeProviderConfigurationChangeEvent >();
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de
     * .unikassel.android.sdcframework.util.facade.ObservableEventSource,
     * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
     */
    @Override
    public
        void
        onEvent(
            ObservableEventSource< ? extends TimeProviderConfigurationChangeEvent > eventSource,
            TimeProviderConfigurationChangeEvent observedEvent )
    {
      events.add( observedEvent );
    }
    
  }
  
  /**
   * Private inner class implementing a service configuration changed observer.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestServiceConfigObserver implements
      EventObserver< ServiceConfigurationChangeEvent >
  {
    /**
     * Public event list
     */
    public final ArrayList< ServiceConfigurationChangeEvent > events =
        new ArrayList< ServiceConfigurationChangeEvent >();
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de
     * .unikassel.android.sdcframework.util.facade.ObservableEventSource,
     * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
     */
    @Override
    public
        void
        onEvent(
            ObservableEventSource< ? extends ServiceConfigurationChangeEvent > eventSource,
            ServiceConfigurationChangeEvent observedEvent )
    {
      events.add( observedEvent );
    }
    
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#ApplicationPreferenceManagerImpl()}
   * .
   */
  public final void testConstruction()
  {
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    assertNotNull( "Expected Instance initialized", apm );
    assertEquals( "Unexpected count of observable event sources", 0,
        apm.getObservableEventSources().size() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#getPreferencesForDevice(de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier)}
   * .
   */
  public final void testGetPreferencesForDevice()
  {
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    
    for ( SensorDeviceIdentifier id : SensorDeviceIdentifier.values() )
    {
      SensorDevicePreferences preferencesForDevice =
          apm.getPreferencesForDevice( id );
      assertNotNull( "Expected preferences for device not null",
          preferencesForDevice );
      assertEquals( "Expected same device identifier", id,
          preferencesForDevice.getDeviceIdentifier() );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#getLogLevelPreference()}
   * .
   */
  public final void testGetLogLevelPreference()
  {
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    assertNotNull( "Expected preferences for log level not null",
        apm.getLogLevelPreference() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#getTimeProviderPreference()}
   * .
   */
  public final void testGetTimeProviderPreference()
  {
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    assertNotNull( "Expected preferences for time provider not null",
        apm.getTimeProviderPreference() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#getServicePreferences}
   * .
   */
  public final void testGetServicePreference()
  {
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    assertNotNull( "Expected preferences for service not null",
        apm.getServicePreferences() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#getDeviceConfiguration(de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier, android.content.Context)}
   * .
   */
  public final void testGetDeviceConfiguration()
  {
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    for ( SensorDeviceIdentifier deviceID : SensorDeviceIdentifier.values() )
    {
      SensorDevicePreferences preference =
          apm.getPreferencesForDevice( deviceID );
      
      // calculate random values for a device configuration
      Boolean enabled = ( (int) ( Math.random() * 100 ) % 2 ) > 0;
      Integer frequency = (int) ( Math.random() * 10000 );
      SensorDevicePriorities priority = null;
      Integer prioVal =
          (int) ( Math.random() * SensorDevicePriorities.values().length );
      
      // try to find enumeration match for the priority
      for ( SensorDevicePriorities prio : SensorDevicePriorities.values() )
      {
        if ( prioVal.equals( prio.ordinal() ) )
        {
          priority = prio;
          break;
        }
      }
      assertNotNull( "priority should not be null here!", priority );
      
      editor.putBoolean( preference.getEnabledPreference().getKey(), enabled );
      editor.putString( preference.getFrequencyPreference().getKey(),
          frequency.toString() );
      editor.putString( preference.getPriorityPreference().getKey(),
          prioVal.toString() );
      editor.commit();
      
      SensorDeviceConfiguration expectedConfig =
          new SensorDeviceConfigurationImpl( frequency,
              priority, enabled );
      SensorDeviceConfiguration configuration =
          apm.getDeviceConfiguration( deviceID, getContext() );
      assertEquals( "Unexpected configuration", expectedConfig, configuration );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#getLogLevelConfiguration(android.content.Context)}
   * .
   */
  public final void testGetLogLevelConfiguration()
  {
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    for ( LogLevel level : LogLevel.values() )
    {
      editor.putString( apm.getLogLevelPreference().getKey(), level.toString() );
      editor.commit();
      
      LogLevel configLevel =
          apm.getLogLevelConfiguration( getContext() ).getLogLevel();
      assertEquals( "Unexpected configuration", level, configLevel );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#getTimeProviderConfiguration(android.content.Context)}
   * .
   */
  public final void testGetTimeProviderConfiguration()
  {
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    List< String > listProviders = new ArrayList< String >();
    listProviders.add( "ptbtime1.ptb.de" );
    listProviders.add( "ptbtime2.ptb.de" );
    listProviders.add( "atom.uhr.de" );
    StringBuffer buffer =
        new StringBuffer( listProviders.get( 0 ) ).append(
            TimeProviderPreferenceImpl.SEPARATOR ).append(
            listProviders.get( 1 ) ).append(
            TimeProviderPreferenceImpl.SEPARATOR ).append(
            listProviders.get( 2 ) );
    editor.putString(
        apm.getTimeProviderPreference().getProvidersPreference().getKey(),
        buffer.toString() );
    
    TimeProviderErrorStrategyDescription strategy =
        TimeProviderErrorStrategyDescription.IgnoreAndObserveSyncStates;
    editor.putString(
        apm.getTimeProviderPreference().getErrorStrategyPreference().getKey(),
        strategy.name() );
    
    editor.commit();
    
    List< String > providers =
        apm.getTimeProviderConfiguration( getContext() ).getProviders();
    for ( String provider : listProviders )
    {
      assertTrue( "Expected provider in configuration " + provider,
          providers.contains( provider ) );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#getServiceConfiguration(android.content.Context)}
   * .
   */
  public final void testGetServiceConfiguration()
  {
    Long maxDBSize = 13374711L;
    ServiceConfiguration expectedConfig = new ServiceConfigurationImpl();
    expectedConfig.setBroadcastingSamples( true );
    expectedConfig.setBroadcastFrequency( 77L );
    expectedConfig.setSamplingEnabled( false );
    expectedConfig.setStoringSamples( true );
    expectedConfig.setTransmittingSamples( true );
    expectedConfig.setMaximumDatabaseSize( maxDBSize );
    expectedConfig.setDBFullDeletionPriorityBased( true );
    expectedConfig.setDBFullDeletionRecordCount( 1000 );
    expectedConfig.setDBFullWaitTime( 10000L );
    expectedConfig.setDBFullStrategy( DBFullStrategyDescription.WAIT_NOTIFY_STOPSERVICE );
    expectedConfig.setTransmissionConfiguration(
        TestTransmissionPreferenceImpl.createTransmissionConfiguration() );
    expectedConfig.setIsAddingSampleLocation( true );
    expectedConfig.setLogTransferConfiguration(
        TestTransmissionProtocolPreferenceImpl.createTransmissionProtocolConfiguration() );
    expectedConfig.setWeeklySchedule( new WeeklySchedule() );
    
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    
    SharedPreferences.Editor editor = sharedPreferences.edit();
    ServicePreferences preferences = new ServicePreferencesImpl();
    
    editor.putBoolean(
        preferences.getSampleBroadcastsEnabledPreference().getKey(),
        expectedConfig.isBroadcastingSamples() );
    editor.putString( preferences.getBroadcastFrequencyPreference().getKey(),
        Long.toString( expectedConfig.getBroadcastFrequency() ) );
    editor.putBoolean(
        preferences.getSamplingEnabledPreference().getKey(),
        expectedConfig.isSamplingEnabled() );
    editor.putBoolean(
        preferences.getPersistentStorageEnabledPreference().getKey(),
        expectedConfig.isStoringSamples() );
    editor.putBoolean(
        preferences.getTransmissionEnabledPreference().getKey(),
        expectedConfig.isTransmittingSamples() );
    
    editor.putString( preferences.getDBMaxSizePreference().getKey(),
        maxDBSize.toString() );
    editor.putBoolean(
        preferences.getDbFullDeletionIsPriorityBasedPreference().getKey(),
        expectedConfig.isDBFullDeletionPriorityBased() );
    editor.putString(
        preferences.getDbFullDeletionRecordCountPreference().getKey(),
        Integer.toString( expectedConfig.getDBFullDeletionRecordCount() ) );
    editor.putString( preferences.getDbFullWaitTimePreference().getKey(),
        Long.toString( expectedConfig.getDBFullWaitTime() ) );
    editor.putString( preferences.getDbFullStrategyPreference().getKey(),
        expectedConfig.getDBFullStrategy().toString() );
    
    TestTransmissionPreferenceImpl.writeExpectedConfig(
        preferences.getTransmissionPreference(),
        expectedConfig.getTransmissionConfiguration(), editor );
    
    TestTransmissionProtocolPreferenceImpl.writeExpectedConfig(
        preferences.getLogTransferPreference(),
        expectedConfig.getLogTransferConfiguration(), editor );
    editor.putBoolean(
        preferences.getSampleLocationFixEnabledPreference().getKey(),
        expectedConfig.isTransmittingSamples() );
    editor.putString( preferences.getWeeklySchedulePreference().getKey(),
        expectedConfig.getWeeklySchedule().toString() );
    editor.commit();
    
    ServiceConfiguration configuration =
        apm.getServiceConfiguration( getContext() );
    assertEquals( "Unexpected configuration", expectedConfig, configuration );
  }
  
  /**
   * Test method for event notification and the methods
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#startListening(android.content.Context)}
   * and
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#stopListening(android.content.Context)}
   * . .
   */
  public final void testStartListeningAndStopListening()
  {
    final ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    final SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    
    // create test observers
    TestLogLevelConfigObserver logConfObserver =
        new TestLogLevelConfigObserver();
    TestDeviceConfigObserver deviceConfObserver =
        new TestDeviceConfigObserver();
    TestServiceConfigObserver serviceConfObserver =
        new TestServiceConfigObserver();
    TimeProviderConfigObserver tpConfObserver =
        new TimeProviderConfigObserver();
    
    // start listening and register observers
    apm.registerEventObserver( logConfObserver );
    apm.registerEventObserver( deviceConfObserver );
    apm.registerEventObserver( serviceConfObserver );
    apm.registerEventObserver( tpConfObserver );
    assertEquals( "Unexpected count of observable event sources", 4,
        apm.getObservableEventSources().size() );
    
    final SharedPreferences.Editor editor = sharedPreferences.edit();
    apm.startListening( getContext() );
    
    final Vector< SensorDeviceConfigurationChangeEvent > vecDeviceUpdates =
        doCreateDevicePreferenceChanges( apm );
    
    WorkerThread asyncUpdater = new AbstractWorkerThread()
    {
      @Override
      protected void doWork()
      {
        // do change preferences to trigger update events
        doChangeTimeProviderEntriesInPreferences( apm, editor );
        while ( !editor.commit() )
          ;
        doChangeLogLevelEntryInPreferences( apm, editor );
        while ( !editor.commit() )
          ;
        doChangeServiceEntriesInPreferences( apm, editor );
        while ( !editor.commit() )
          ;
        doChangeDevicePreferences( vecDeviceUpdates, apm, editor );
        while ( !editor.commit() )
          ;
        stopWork();
      }
      
      @Override
      protected void doCleanUp()
      {}
    };
    asyncUpdater.setLogging( false );
    asyncUpdater.startWork();
    
    while ( asyncUpdater.isWorking() )
    {
      TestUtils.sleep( 1000 );
    }
    
    // wait for event dispatching
    TestUtils.sleep( 1000 );
    
    // stop listening
    apm.stopListening( getContext() );
    
    // HINT: The event count tests may fail on Api level 10 (Android >=2.3)
    // ( seems to be an preference event delivery problem )
    
    // test for expected events
    
    assertEquals( "Service configuration update event count is wrong",
        3, serviceConfObserver.events.size() );
    
    assertTrue(
        "Device configuration update event count is wrong",
        SensorDeviceIdentifier.values().length <= deviceConfObserver.events.size() );
    
    for ( SensorDeviceConfigurationChangeEvent eventOrg : vecDeviceUpdates )
    {
      boolean eventAvailable = false;
      for ( SensorDeviceConfigurationChangeEvent event : deviceConfObserver.events )
      {
        if ( eventOrg.getDeviceIdentifier().equals( event.getDeviceIdentifier() ) )
        {
          SensorDeviceConfiguration config = event.getConfiguration();
          if ( eventOrg.getConfiguration().equals( config ) )
          {
            eventAvailable = true;
            break;
          }
        }
      }
      String msg = "Missing device configuration change event for "
          + eventOrg.getDeviceIdentifier().toString();
      assertTrue( msg, eventAvailable );
    }
    
    assertEquals( "time provider configuration update event count is wrong",
        2, tpConfObserver.events.size() );
    TimeProviderConfigurationChangeEvent tpEvent =
        tpConfObserver.events.get( 0 );
    assertEquals(
        "wrong time provider count in the configuration update event",
        1, tpEvent.getConfiguration().getProviders().size() );
    
    assertEquals( "Loglevel configuration update event count is wrong",
        1, logConfObserver.events.size() );
    
    assertEquals( "Wrong log level configuration change event",
        LogLevel.WARNING,
        logConfObserver.events.get( 0 ).getConfiguration().getLogLevel() );
    
    logConfObserver.events.clear();
    deviceConfObserver.events.clear();
    serviceConfObserver.events.clear();
    tpConfObserver.events.clear();
    
    // do change preference to trigger update events
    doChangeLogLevelEntryInPreferences( apm, editor );
    doChangeDevicePreferences( vecDeviceUpdates, apm, editor );
    doChangeServiceEntriesInPreferences( apm, editor );
    doChangeTimeProviderEntriesInPreferences( apm, editor );
    
    TestUtils.sleep( 1000 );
    
    // test for unexpected events
    assertEquals( "Loglevel configuration update event count is wrong",
        0, logConfObserver.events.size() );
    assertEquals( "Service configuration update event count is wrong",
        0, serviceConfObserver.events.size() );
    assertEquals( "Device configuration update event count is wrong",
        0, deviceConfObserver.events.size() );
    assertEquals( "timeProvider configuration update event count is wrong",
        0, tpConfObserver.events.size() );
    
    apm.removeAllObservers();
  }
  
  /**
   * Method to generate device configuration change information
   * 
   * @param apm
   *          the application preference manager instance
   * @return a vector with the new device configuration change events
   */
  private Vector< SensorDeviceConfigurationChangeEvent >
      doCreateDevicePreferenceChanges(
          ApplicationPreferenceManagerImpl apm )
  {
    // do change devices preference in shared preferences
    Vector< SensorDeviceConfigurationChangeEvent > vecDeviceConfigs =
        new Vector< SensorDeviceConfigurationChangeEvent >();
    
    for ( SensorDeviceIdentifier deviceID : SensorDeviceIdentifier.values() )
    {
      SensorDeviceConfiguration deviceConfig =
          apm.getDeviceConfiguration( deviceID, getContext() );
      
      Boolean enabled = ( (int) ( Math.random() * 100 ) % 2 ) > 0;
      Integer frequency = (int) ( Math.random() * 10000 );
      SensorDevicePriorities priority = null;
      Integer prioVal =
          (int) ( Math.random() * SensorDevicePriorities.values().length );
      
      // try to find enumeration match for the priority
      for ( SensorDevicePriorities prio : SensorDevicePriorities.values() )
      {
        if ( prioVal.equals( prio.ordinal() ) )
        {
          priority = prio;
          break;
        }
      }
      assertNotNull( "priority should not be null here!", priority );
      
      // create updates and store as configurations
      deviceConfig.setEnabled( enabled );
      deviceConfig.setFrequency( frequency );
      deviceConfig.setSamplePriority( priority );
      vecDeviceConfigs.add( new SensorDeviceConfigurationChangeEventImpl(
          new SensorDeviceConfigurationImpl( deviceConfig ), deviceID ) );
    }
    
    return vecDeviceConfigs;
  }
  
  /**
   * Method to generate device configuration change events
   * 
   * @param apm
   *          the application preference manager instance
   * @param vecDeviceConfigs
   *          the new device configs to set
   * @param editor
   *          the shared preferences editor
   */
  private static void doChangeDevicePreferences(
      Vector< SensorDeviceConfigurationChangeEvent > vecDeviceConfigs,
      ApplicationPreferenceManagerImpl apm, SharedPreferences.Editor editor )
  {
    // do change devices preference in shared preferences
    for ( SensorDeviceConfigurationChangeEvent deviceEvent : vecDeviceConfigs )
    {
      SensorDeviceIdentifier deviceID = deviceEvent.getDeviceIdentifier();
      SensorDevicePreferences preference =
          apm.getPreferencesForDevice( deviceID );
      SensorDeviceConfiguration deviceConfig = deviceEvent.getConfiguration();
      
      // write updates and store expected configurations
      editor.putBoolean( preference.getEnabledPreference().getKey(),
          deviceConfig.isEnabled() );
      editor.putString( preference.getFrequencyPreference().getKey(),
          Integer.toString( deviceConfig.getFrequency() ) );
      editor.putString( preference.getPriorityPreference().getKey(),
          Integer.toString( deviceConfig.getSamplePriority().ordinal() ) );
    }
  }
  
  /**
   * Method to generate log level change events
   * 
   * @param apm
   *          the application preference manager instance
   * @param editor
   *          the shared preferences editor
   */
  private static void doChangeLogLevelEntryInPreferences(
      ApplicationPreferenceManagerImpl apm, SharedPreferences.Editor editor )
  {
    editor.putString( apm.getLogLevelPreference().getKey(),
        LogLevel.WARNING.toString() );
  }
  
  /**
   * Method to generate service change events
   * 
   * @param apm
   *          the application preference manager instance
   * @param editor
   *          the shared preferences editor
   */
  private void doChangeServiceEntriesInPreferences(
      ApplicationPreferenceManagerImpl apm, SharedPreferences.Editor editor )
  {
    ServicePreferences preferences = apm.getServicePreferences();
    ServiceConfiguration config = apm.getServiceConfiguration( getContext() );
    Long maxDBSize = config.getMaximumDatabaseSize() + 10L;
    editor.putString( preferences.getDBMaxSizePreference().getKey(),
        maxDBSize.toString() );
    editor.putBoolean(
        preferences.getSampleBroadcastsEnabledPreference().getKey(),
        !config.isBroadcastingSamples() );
    editor.putBoolean(
        preferences.getSamplingEnabledPreference().getKey(),
        !config.isSamplingEnabled());
  }
  
  /**
   * Method to generate time provider change events
   * 
   * @param apm
   *          the application preference manager instance
   * @param editor
   *          the shared preferences editor
   */
  private void doChangeTimeProviderEntriesInPreferences(
      ApplicationPreferenceManagerImpl apm, Editor editor )
  {
    editor.putString( apm.getTimeProviderPreference().getProvidersPreference().getKey(),
        "mydummy.de" );
    editor.putString( apm.getTimeProviderPreference().getErrorStrategyPreference().getKey(),
    "startegy" );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#getSharedPreferences(android.content.Context)}
   * .
   */
  public final void testGetSharedPreferences()
  {
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    
    SharedPreferences expectedSharedPrefs =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    SharedPreferences sharedPrefs = apm.getSharedPreferences( getContext() );
    
    assertEquals( "Unexpected shared preferences", expectedSharedPrefs,
        sharedPrefs );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#registerEventObserver(EventObserver)}
   * and *
   * {@link de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl#unregisterEventObserver(EventObserver)}
   * .
   */
  public final void testEventObserverRegistration()
  {
    ApplicationPreferenceManagerImpl apm =
        new ApplicationPreferenceManagerImpl();
    
    // create test observers
    TestLogLevelConfigObserver logConfObserver =
        new TestLogLevelConfigObserver();
    TestDeviceConfigObserver deviceConfObserver =
        new TestDeviceConfigObserver();
    TestServiceConfigObserver serviceConfObserver =
        new TestServiceConfigObserver();
    TimeProviderConfigObserver tpConfObserver =
        new TimeProviderConfigObserver();
    
    // test adding observers for different types
    assertEquals( "Unexpected count of observable event sources", 0,
        apm.getObservableEventSources().size() );
    LogLevelConfigurationChangeEventSource logConfEventSource = null;
    DeviceConfigurationChangeEventSource deviceConfEventSource = null;
    ServiceConfigurationChangeEventSource serviceConfEventSource = null;
    TimeProviderConfigurationChangeEventSource tpConfEventSource = null;
    
    apm.registerEventObserver( logConfObserver );
    assertEquals( "Unexpected count of observable event sources", 1,
        apm.getObservableEventSources().size() );
    Iterator< ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > >> iterator =
        apm.getObservableEventSources().iterator();
    ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > observable =
        iterator.next();
    assertTrue( "Unexpected observable type",
        observable instanceof LogLevelConfigurationChangeEventSource );
    logConfEventSource = (LogLevelConfigurationChangeEventSource) observable;
    assertTrue( "Expected log configuration event observer registered",
        logConfEventSource.getObservers().contains( logConfObserver ) );
    
    apm.registerEventObserver( tpConfObserver );
    assertEquals( "Unexpected count of observable event sources", 2,
        apm.getObservableEventSources().size() );
    
    apm.registerEventObserver( deviceConfObserver );
    assertEquals( "Unexpected count of observable event sources", 3,
        apm.getObservableEventSources().size() );
    
    apm.registerEventObserver( serviceConfObserver );
    assertEquals( "Unexpected count of observable event sources", 4,
        apm.getObservableEventSources().size() );
    
    // get the observable event source for further tests
    iterator = apm.getObservableEventSources().iterator();
    while ( iterator.hasNext() )
    {
      observable = iterator.next();
      
      if ( observable instanceof LogLevelConfigurationChangeEventSource )
      {
        logConfEventSource =
            (LogLevelConfigurationChangeEventSource) observable;
      }
      else if ( observable instanceof TimeProviderConfigurationChangeEventSource )
      {
        tpConfEventSource =
            (TimeProviderConfigurationChangeEventSource) observable;
      }
      else if ( observable instanceof ServiceConfigurationChangeEventSource )
      {
        serviceConfEventSource =
            (ServiceConfigurationChangeEventSource) observable;
      }
      else
      {
        deviceConfEventSource =
            (DeviceConfigurationChangeEventSource) observable;
      }
    }
    
    assertTrue( "Expected log configuration event observer registered",
        logConfEventSource.getObservers().contains( logConfObserver ) );
    assertTrue( "Expected device configuration event observer registered",
        deviceConfEventSource.getObservers().contains( deviceConfObserver ) );
    assertTrue( "Expected service configuration event observer registered",
        serviceConfEventSource.getObservers().contains( serviceConfObserver ) );
    assertTrue(
        "Expected time provider configuration event observer registered",
        tpConfEventSource.getObservers().contains( tpConfObserver ) );
    
    // test unregister observers for different types
    apm.unregisterEventObserver( logConfObserver );
    assertFalse( "Expected log configuration event observer unregistered",
        logConfEventSource.getObservers().contains( logConfObserver ) );
    assertEquals( "Expected no log configuration event observer is registered",
        0, logConfEventSource.getObservers().size() );
    
    apm.unregisterEventObserver( tpConfObserver );
    assertFalse(
        "Expected time provider configuration event observer unregistered",
        tpConfEventSource.getObservers().contains( tpConfObserver ) );
    assertEquals( "Expected no log configuration event observer is registered",
        0, tpConfEventSource.getObservers().size() );
    
    apm.unregisterEventObserver( serviceConfObserver );
    assertFalse( "Expected service configuration event observer unregistered",
        serviceConfEventSource.getObservers().contains( serviceConfObserver ) );
    assertEquals( "Expected no log configuration event observer is registered",
        0, serviceConfEventSource.getObservers().size() );
    
    apm.unregisterEventObserver( deviceConfObserver );
    assertFalse( "Expected device configuration event observer unregistered",
        deviceConfEventSource.getObservers().contains( deviceConfObserver ) );
    assertEquals(
        "Expected no device configuration event observer is registered",
        0, deviceConfEventSource.getObservers().size() );
    
    // test remove all
    apm.registerEventObserver( logConfObserver );
    apm.registerEventObserver( deviceConfObserver );
    apm.registerEventObserver( serviceConfObserver );
    apm.registerEventObserver( tpConfObserver );
    
    int cnt = 0;
    iterator = apm.getObservableEventSources().iterator();
    while ( iterator.hasNext() )
    {
      cnt++;
      iterator.next();
    }
    assertEquals( "Unexpected count of registered event observers", 4, cnt );
    
    apm.removeAllObservers();
    cnt = 0;
    iterator = apm.getObservableEventSources().iterator();
    while ( iterator.hasNext() )
    {
      cnt++;
      iterator.next();
    }
    assertEquals( "Unexpected count of registered event observers", 0, cnt );
  }
}
