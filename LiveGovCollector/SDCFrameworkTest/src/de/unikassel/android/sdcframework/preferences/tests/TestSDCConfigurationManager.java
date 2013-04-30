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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;
import de.unikassel.android.sdcframework.data.SDCConfiguration;
import de.unikassel.android.sdcframework.data.SensorConfigurationEntry;
import de.unikassel.android.sdcframework.data.TransmissionConfigurationEntry;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.preferences.FrequencyPreference;
import de.unikassel.android.sdcframework.preferences.PriorityLevelPreference;
import de.unikassel.android.sdcframework.preferences.SDCConfigurationManager;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.TimeProviderConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.TransmissionConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.ServicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderPreference;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;

/**
 * Tests for the default preferences manager.
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "DefaultLocale" )
public class TestSDCConfigurationManager
    extends InstrumentationTestCase
{
  /**
   * The configuration filename
   */
  public final static String CONFIGFILE = "SDCConfigForTest.xml";
  
  /**
   * The configuration filename
   */
  public final static String EMPTY_CONFIGFILE = "SDCEmptyConfigForTest.xml";
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }
  
  /**
   * Test for preconditions
   */
  public final void testPreconditions()
  {
    AssetManager assetManager =
        getInstrumentation().getContext().getResources().getAssets();
    try
    {
      assetManager.open( CONFIGFILE );
      assetManager.open( EMPTY_CONFIGFILE );
    }
    catch ( IOException e )
    {
      fail( "One or more configuration file(s) not found!\nPlease check name(s)." );
    }
  }
  
  /**
   * Test method for Construction and update of default values in case of an
   * empty configuration.
   */
  public final void testConstructionAndUpdateOfDefaultsForEmptyConfiguration()
  {
    // create preference manager with device defaults
    ApplicationPreferenceManager appPrefManager =
        new ApplicationPreferenceManagerImpl();
    
    ServiceConfiguration defaultValues =
        appPrefManager.getServicePreferences().getDefault();
    
    // create manager and trigger default update
    SDCConfigurationManager manager =
        new SDCConfigurationManager( getInstrumentation().getContext(),
            EMPTY_CONFIGFILE );
    manager.updateDefaults( appPrefManager );
    
    ServiceConfiguration newDefaultValues =
        appPrefManager.getServicePreferences().getDefault();
    assertEquals( "Expected application preference defaults unchanged",
        defaultValues, newDefaultValues );
  }
  
  /**
   * Test method for Construction and update of default values.
   */
  @SuppressLint( "DefaultLocale" )
  public final void testConstructionAndUpdateOfDefaults()
  {
    // create preference manager with device defaults
    ApplicationPreferenceManager appPrefManager =
        new ApplicationPreferenceManagerImpl();
    
    SDCConfiguration config =
        readConfigurationForTest( getInstrumentation().getContext(), CONFIGFILE );
    Map< SensorDeviceIdentifier, SensorDeviceConfiguration > mapConfigsFromFile =
        prepareApplicationPreferencesForTest( config, appPrefManager );
    
    // create manager and trigger default update
    SDCConfigurationManager manager =
        new SDCConfigurationManager( getInstrumentation().getContext(),
            CONFIGFILE );
    Assert.assertFalse( "Expected devices configured", manager.getListDevices().isEmpty() );
    manager.updateDefaults( appPrefManager );
    
    // test if defaults are updated as expected
    for ( Entry< SensorDeviceIdentifier, SensorDeviceConfiguration > entry : mapConfigsFromFile.entrySet() )
    {
      SensorDevicePreferences devicePreferences =
          appPrefManager.getPreferencesForDevice( entry.getKey() );
      SensorDeviceConfiguration entryConfig = entry.getValue();
      
      assertEquals( "Unexpected default priority level in device preferences",
          entryConfig.getSamplePriority(),
          devicePreferences.getPriorityPreference().getDefault() );
      assertEquals( "Unexpected default enabled state in device preferences",
          entryConfig.isEnabled(),
          devicePreferences.getEnabledPreference().getDefault().booleanValue() );
      assertEquals( "Unexpected default frequency in device preferences",
          entryConfig.getFrequency(),
          devicePreferences.getFrequencyPreference().getDefault().intValue() );
    }
    
    TimeProviderPreference timeProviderPreference =
        appPrefManager.getTimeProviderPreference();
    List< String > providers =
        timeProviderPreference.getDefault().getProviders();
    assertEquals( "Unexpected provider count", 4,
        providers.size() );
    String provider = "ntps1-1.cs.tu-berlin.de";
    assertTrue( "Expected provider in configuration list " + provider,
        providers.contains( provider ) );
    provider = "ptbtime1.ptb.de";
    assertTrue( "Expected provider in configuration list " + provider,
        providers.contains( provider ) );
    provider = "ptbtime2.ptb.de";
    assertTrue( "Expected provider in configuration list " + provider,
        providers.contains( provider ) );
    provider = "atom.uhr.de";
    assertTrue( "Expected provider in configuration list " + provider,
        providers.contains( provider ) );
    
    ServicePreferences servicePreferences =
        appPrefManager.getServicePreferences();
    
    assertEquals(
        "Unexpected default broadcast flag in service preferences",
        config.isBroadcastingSamples(),
        servicePreferences.getSampleBroadcastsEnabledPreference().getDefault() );       
    assertEquals(
        "Unexpected default for sampling enabled state in service preferences",
        config.isSamplingEnabled(),
        servicePreferences.getSamplingEnabledPreference().getDefault() );     
    assertEquals(
        "Unexpected default flag for adding sample locations",
        config.isAddingSampleLocation(),
        servicePreferences.getSampleLocationFixEnabledPreference().getDefault() );
    assertEquals(
        "Unexpected default flag for persistent storage in service preferences",
        config.isStoringSamples(),
        servicePreferences.getPersistentStorageEnabledPreference().getDefault() );
    assertEquals(
        "Unexpected default flag for sample transmission enabled in service preferences",
        config.isTransmittingSamples(),
        servicePreferences.getTransmissionEnabledPreference().getDefault() );
    
    assertEquals(
        "Unexpected default maximum database size in service preferences",
        config.getDatabaseConfiguration().getMaxDBSize(),
        servicePreferences.getDBMaxSizePreference().getDefault() );
    assertEquals(
        "Unexpected default for database full deletion done priority based in service preferences",
        config.getDatabaseConfiguration().isDBFullDeletionPriorityBased(),
        servicePreferences.getDbFullDeletionIsPriorityBasedPreference().getDefault() );
    assertEquals(
        "Unexpected default for database full record count to delete in service preferences",
        config.getDatabaseConfiguration().getDBFullDeletionRecordCount(),
        servicePreferences.getDbFullDeletionRecordCountPreference().getDefault() );
    assertEquals(
        "Unexpected default for database full wait time in service preferences",
        config.getDatabaseConfiguration().getDBFullWaitTime(),
        servicePreferences.getDbFullWaitTimePreference().getDefault() );
    assertEquals(
        "Unexpected default for database full strategy in service preferences",
        config.getDatabaseConfiguration().getDBFullStrategy().toUpperCase(),
        servicePreferences.getDbFullStrategyPreference().getDefault().toString() );
    
    assertEquals(
        "Unexpected transmission archive type in service preferences",
        config.getTransmissionConfiguration().getArchiveType().toLowerCase(),
        servicePreferences.getTransmissionPreference().getDefault().getArchiveType().toString() );
    assertEquals(
        "Unexpected authentication password in service preferences",
        config.getTransmissionConfiguration().getProtocolConfig().getAuthPassword(),
        servicePreferences.getTransmissionPreference().getProtocolPreference().getDefault().getAuthPassword() );
    assertEquals(
        "Unexpected transmission strategy in service preferences",
        config.getTransmissionConfiguration().getProtocolConfig().getConnectionStrategy().toLowerCase(),
        servicePreferences.getTransmissionPreference().getProtocolPreference().getDefault().getTransmissionStrategy().toString() );
    assertEquals(
        "Unexpected max sample count for transmission in service preferences",
        config.getTransmissionConfiguration().getMaxSampleTransferCount().intValue(),
        servicePreferences.getTransmissionPreference().getDefault().getMaxSampleTransferCount() );
    assertEquals(
        "Unexpected min sample count for transmission in service preferences",
        config.getTransmissionConfiguration().getMinSampleTransferCount().intValue(),
        servicePreferences.getTransmissionPreference().getDefault().getMinSampleTransferCount() );
    assertEquals(
        "Unexpected min frequency for transmission in service preferences",
        config.getTransmissionConfiguration().getMinTransferFrequency().longValue(),
        servicePreferences.getTransmissionPreference().getDefault().getMinTransferFrequency() );
    assertEquals(
        "Unexpected URLfor transmission in service preferences",
        config.getTransmissionConfiguration().getProtocolConfig().getURL(),
        servicePreferences.getTransmissionPreference().getProtocolPreference().getDefault().getURL() );
    assertEquals(
        "Unexpected user name for transmission in service preferences",
        config.getTransmissionConfiguration().getProtocolConfig().getUserName(),
        servicePreferences.getTransmissionPreference().getProtocolPreference().getDefault().getUserName() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.SDCConfigurationManager#onDestroy()}
   * .
   */
  public final void testOnDestroy()
  {
    // create preference manager with device defaults
    ApplicationPreferenceManager appPrefManager =
        new ApplicationPreferenceManagerImpl();
    
    // read configuration and prepare for test
    SDCConfiguration config =
        readConfigurationForTest( getInstrumentation().getContext(), CONFIGFILE );
    Map< SensorDeviceIdentifier, SensorDeviceConfiguration > mapConfigsFromFile =
        prepareApplicationPreferencesForTest( config, appPrefManager );
    
    SDCConfigurationManager manager =
        new SDCConfigurationManager( getInstrumentation().getContext(),
            CONFIGFILE );
    
    // do destroy the management object
    // ( should clear any stored device default values )
    manager.onDestroy();
    
    // test if any device defaults are cleared
    // ( expecting manager not updating device defaults after having been
    // destroyed )
    manager.updateDefaults( appPrefManager );
    
    for ( Entry< SensorDeviceIdentifier, SensorDeviceConfiguration > entry : mapConfigsFromFile.entrySet() )
    {
      SensorDevicePreferences devicePreferences =
          appPrefManager.getPreferencesForDevice( entry.getKey() );
      SensorDeviceConfiguration entryConfig = entry.getValue();
      
      assertFalse( "Unexpected default priority level in device preferences",
          entryConfig.getSamplePriority().equals(
              devicePreferences.getPriorityPreference().getDefault() ) );
      assertFalse(
          "Unexpected default enabled state in device preferences",
          entryConfig.isEnabled() == devicePreferences.getEnabledPreference().getDefault().booleanValue() );
      assertFalse(
          "Unexpected default frequency in device preferences",
          entryConfig.getFrequency() == devicePreferences.getFrequencyPreference().getDefault().intValue() );
    }
  }
  
  /**
   * Does read the sensor configuration from file and store it in a map. In
   * addition for each sensor in file the default preference in application
   * preference manager will be set to different values.
   * 
   * @param config
   *          the SDC configuration to prepare
   * @param appPrefManager
   *          the application preference manager
   * @return a map with the sensor configurations from the configuration file
   */
  public static Map< SensorDeviceIdentifier, SensorDeviceConfiguration >
      prepareApplicationPreferencesForTest( SDCConfiguration config,
          ApplicationPreferenceManager appPrefManager )
  {
    // configure service preference defaults in preference manager to be
    // different from those configured in the configuration file
    ServicePreferences servicePreferences =
        appPrefManager.getServicePreferences();
    servicePreferences.getSampleBroadcastsEnabledPreference().setDefault(
        !config.isBroadcastingSamples() );
    servicePreferences.getSamplingEnabledPreference().setDefault(
        !config.isSamplingEnabled() );
    servicePreferences.getSampleLocationFixEnabledPreference().setDefault(
        !config.isAddingSampleLocation() );
    servicePreferences.getPersistentStorageEnabledPreference().setDefault(
        !config.isStoringSamples() );
    servicePreferences.getTransmissionEnabledPreference().setDefault(
        !config.isTransmittingSamples() );
    servicePreferences.getDBMaxSizePreference().setDefault(
        config.getDatabaseConfiguration().getMaxDBSize() + 10 );
    servicePreferences.getDbFullDeletionIsPriorityBasedPreference().setDefault(
        !config.getDatabaseConfiguration().isDBFullDeletionPriorityBased() );
    servicePreferences.getDbFullDeletionRecordCountPreference().setDefault(
        config.getDatabaseConfiguration().getDBFullDeletionRecordCount() << 2 );
    servicePreferences.getDbFullWaitTimePreference().setDefault(
        config.getDatabaseConfiguration().getDBFullWaitTime() << 2 );
    String dbFullStrategy =
        config.getDatabaseConfiguration().getDBFullStrategy();
    DBFullStrategyDescription strategy =
        DBFullStrategyDescription.WAIT_DELETE_NOTIFY;
    if ( strategy.toString().equals( dbFullStrategy ) )
      strategy = DBFullStrategyDescription.WAIT_NOTIFY_STOPSERVICE;
    servicePreferences.getDbFullStrategyPreference().setDefault(
        strategy );
    
    TransmissionConfigurationEntry transmissionConfig =
        config.getTransmissionConfiguration();
    TransmissionConfiguration transmissionDefaults =
        new TransmissionConfigurationImpl();
    transmissionDefaults.setArchiveType( transmissionConfig.getArchiveType().equals(
        ArchiveTypes.jar ) ? ArchiveTypes.zip : ArchiveTypes.jar );
    transmissionDefaults.getProtocolConfiguration().setAuthPassword( "another_"
        + transmissionConfig.getProtocolConfig().getAuthPassword() );
    transmissionDefaults.setMaxSampleTransferCount( transmissionConfig.getMaxSampleTransferCount() + 100 );
    transmissionDefaults.setMinSampleTransferCount( transmissionConfig.getMinSampleTransferCount() + 100 );
    transmissionDefaults.setMinTransferFrequency( transmissionConfig.getMinTransferFrequency() + 100 );
    transmissionDefaults.getProtocolConfiguration().setURL( transmissionConfig.getProtocolConfig().getURL() + "/tmp" );
    transmissionDefaults.getProtocolConfiguration().setUserName( "another_"
        + transmissionConfig.getProtocolConfig().getUserName() );
    transmissionDefaults.getProtocolConfiguration().setTransmissionStrategy( !ConnectionStrategyDescription.any_available.toString().equals(
        transmissionConfig.getProtocolConfig().getConnectionStrategy().toUpperCase() )
        ? ConnectionStrategyDescription.any_available
        : ConnectionStrategyDescription.wlan );
    servicePreferences.getTransmissionPreference().setDefault(
        transmissionDefaults );
    
    TimeProviderConfiguration tpDefaults = new TimeProviderConfigurationImpl( null, null );
    TimeProviderPreference tpPref = appPrefManager.getTimeProviderPreference();
    tpPref.setDefault( tpDefaults  );
    
    // get devices and configuration defaults from file and store it for the
    // test run and for each sensor in the configuration file do change the
    // defaults to different values for the update test run
    Map< SensorDeviceIdentifier, SensorDeviceConfiguration > mapConfigsFromFile =
        new HashMap< SensorDeviceIdentifier, SensorDeviceConfiguration >();
    
    for ( SensorConfigurationEntry entry : config.getListSensorConfigurations() )
    {
      SensorDeviceIdentifier id = null;
      
      try
      {
        id = Enum.valueOf( SensorDeviceIdentifier.class, entry.getSensorID() );
        assertFalse( "Invalid sensor identifier in configuration file "
            + entry.getSensorID(),
            SensorDeviceIdentifier.Unknown.equals( id ) );
      }
      catch ( Exception e )
      {
        e.printStackTrace();
        fail( "Invalid sensor identifier in configuration file: "
            + entry.getSensorID() );
      }
      
      // get device configuration default values and use internal defaults if
      // they are not configured
      boolean enabled = entry.getEnabled();
      int frequency = entry.getFrequency();
      assertTrue( "Invalid frequency for sensor " + entry.getSensorID()
          + " in configuration file: " + frequency, frequency >= 0 );
      if ( frequency == 0 )
      {
        frequency = FrequencyPreference.DEFAULT;
      }
      
      SensorDevicePriorities priorityLevel = PriorityLevelPreference.DEFAULT;
      if ( entry.getPriority() != null )
      {
        try
        {
          priorityLevel =
              Enum.valueOf( SensorDevicePriorities.class, entry.getPriority() );
        }
        catch ( Exception e )
        {
          e.printStackTrace();
          fail( "Invalid priority for sensor " + entry.getSensorID()
              + " in configuration file: " + entry.getPriority() );
        }
      }
      
      assertFalse( "Duplicate entry for sensor " + entry.getSensorID()
          + " in configuration ", mapConfigsFromFile.containsKey( id ) );
      
      mapConfigsFromFile.put( id, new SensorDeviceConfigurationImpl(
          frequency, priorityLevel, enabled ) );
      
      // set device preference to different values
      SensorDevicePreferences devicePreferences =
          appPrefManager.getPreferencesForDevice( id );
      Integer prio = priorityLevel.ordinal() + 1;
      prio %= SensorDevicePriorities.values().length;
      SensorDevicePriorities diffPriorityLevel =
          Enum.valueOf( SensorDevicePriorities.class, "Level" + prio.toString() );
      devicePreferences.getPriorityPreference().setDefault( diffPriorityLevel );
      devicePreferences.getEnabledPreference().setDefault( !entry.getEnabled() );
      devicePreferences.getFrequencyPreference().setDefault( frequency + 5 );
    }
    
    // well if no sensor is configured frame work is obsolete isn't it? ;)
    assertTrue( "There's no valid sensor configured",
        mapConfigsFromFile.size() > 0 );
    
    return mapConfigsFromFile;
  }
  
  /**
   * Method to read the current SDC framework configuration from file
   * 
   * @param context
   *          the context
   * @param fileName
   *          the configuration filename
   * @return the SDCConfiguration
   */
  public static SDCConfiguration readConfigurationForTest( Context context,
      String fileName )
  {
    Resources resources = context.getResources();
    SDCConfiguration config = null;
    try
    {
      config = readSDCConfigurationFromResource( fileName, resources );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Failed to read configuration settings from " + fileName + ": "
          + e.getMessage() );
    }
    return config;
  }
  
  /**
   * Does load an SDCConfiguration from a resource file
   * 
   * @param fileName
   *          the filename
   * @param resources
   *          the resource to load configuration from
   * @return the configuration if successful
   * @throws Exception
   *           in case of serialization errors
   */
  public static SDCConfiguration readSDCConfigurationFromResource(
      String fileName,
      Resources resources ) throws Exception
  {
    AssetManager assetManager = resources.getAssets();
    Serializer serializer = new Persister();
    InputStream is = assetManager.open( fileName );
    
    return serializer.read( SDCConfiguration.class, is );
  }
}
