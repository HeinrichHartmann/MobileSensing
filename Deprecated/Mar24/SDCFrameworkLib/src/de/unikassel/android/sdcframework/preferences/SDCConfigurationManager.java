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
package de.unikassel.android.sdcframework.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import android.content.Context;
import android.content.res.AssetManager;

import de.unikassel.android.sdcframework.data.SDCConfiguration;
import de.unikassel.android.sdcframework.data.SensorConfigurationEntry;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of a management component responsible to handle the
 * preconfigured default configuration, respectively to read it from the file
 * and update the default values in a preference manager instance. <br/<br/>
 * The default configuration can be defined in an XML file, which is located in
 * the projects asset folder. <br/>
 * A configuration example is given in the documentation of the class
 * {@linkplain SDCConfiguration SDCConfiguration}.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class SDCConfigurationManager
{
  /**
   * The map with available device defaults
   */
  private final Map< SensorDeviceIdentifier, SensorDeviceConfiguration > mapDeviceDefaults;
  
  /**
   * The service configuration
   */
  private SDCConfiguration sdcConfiguration;
  
  /**
   * Flag for the usage of an uploaded external configuration file;
   */
  private final boolean isUsingExternalConfiguration;
  
  /**
   * Constructor
   * 
   * @param appContext
   *          the application context
   * @param configFileName
   *          the configuration file name
   */
  public SDCConfigurationManager( Context appContext, String configFileName )
  {
    super();
    this.mapDeviceDefaults =
        new HashMap< SensorDeviceIdentifier, SensorDeviceConfiguration >();
    this.isUsingExternalConfiguration = readDefaultsFromPrivateFileFolder( appContext, configFileName );
    if ( !isUsingExternalConfiguration() )
    {
      readDefaultsFromAssetFolder( appContext, configFileName );
    }
  }
  
  /**
   * Does read the defaults from the configuration file
   * 
   * @param appContext
   *          the application context
   * @param configFileName
   *          the configuration file name
   */
  private final boolean readDefaultsFromPrivateFileFolder( Context appContext,
      String configFileName )
  {
    try
    {
      String filePath =
          appContext.getFilesDir().getPath() + File.separatorChar
              + configFileName;
      
      InputStream is = new FileInputStream( FileUtils.fileFromPath( filePath ) );
      readDefaults( is );
      Logger.getInstance().info(
          this,
          String.format(
              "External default configuration loaded from file %s.",
              configFileName ) );
      return true;
    }
    catch ( FileNotFoundException fnfe )
    {}
    catch ( Exception e )
    {
      Logger.getInstance().error(
          this,
          String.format(
              "Failed to read default configuration from file %s in private folder. Reason: %s",
              configFileName, e.getMessage() ) );
    }
    return false;
  }
  
  /**
   * Does read the defaults from the configuration file
   * 
   * @param appContext
   *          the application context
   * @param configFileName
   *          the configuration file name
   */
  private final void readDefaultsFromAssetFolder( Context appContext,
      String configFileName )
  {
    AssetManager assetManager = appContext.getResources().getAssets();
    
    try
    {
      InputStream is = assetManager.open( configFileName );
      readDefaults( is );
    }
    catch ( Exception e )
    {
      Logger.getInstance().error(
          this,
          String.format(
              "Failed to read default configuration from file %s in asset folder. Reason: %s",
              configFileName, e.getMessage() ) );
    }
  }
  
  /**
   * Does read the defaults from an input stream.
   * 
   * @param is
   *          the input stream
   * @throws Exception
   */
  private final void readDefaults( InputStream is ) throws Exception
  {
    mapDeviceDefaults.clear();
    sdcConfiguration =
        GlobalSerializer.serializeFromStream( SDCConfiguration.class, is );
    
    // store device configuration default values
    for ( SensorConfigurationEntry entry : sdcConfiguration.getListSensorConfigurations() )
    {
      // preset id with unknown device
      SensorDeviceIdentifier id = SensorDeviceIdentifier.Unknown;
      
      try
      {
        // try to read a valid identifier
        id = Enum.valueOf( SensorDeviceIdentifier.class, entry.getSensorID() );
      }
      catch ( Exception e )
      {}
      
      if ( SensorDeviceIdentifier.Unknown.equals( id ) )
      {
        // in case of errors log and continue with next entry
        Logger.getInstance().error( this,
            "Invalid sensor id in configuration file: " + entry.getSensorID() );
        continue;
      }
      
      // ignore more than one entry for a sensor device
      if ( mapDeviceDefaults.containsKey( id ) )
      {
        // in case of errors log and continue with next entry
        Logger.getInstance().warning( this,
            "Ignoring duplicate entry for sensor " + entry.getSensorID() );
        continue;
      }
      
      // get device configuration default values and use internal defaults if
      // they are not configured
      boolean enabled = entry.getEnabled();
      int frequency = entry.getFrequency();
      if ( frequency <= 0 )
      {
        // ignore frequencies below or equal to 0
        frequency = FrequencyPreference.DEFAULT;
      }
      
      // preset priority with internal default in case of corrupted entries
      // or not configured values ( this will override configuration )
      SensorDevicePriorities priorityLevel = PriorityLevelPreference.DEFAULT;
      if ( entry.getPriority() != null )
      {
        try
        {
          priorityLevel =
              Enum.valueOf( SensorDevicePriorities.class, entry.getPriority() );
        }
        catch ( Exception e )
        {}
      }
      
      // add sensor configuration defaults to the device map
      mapDeviceDefaults.put( id, new SensorDeviceConfigurationImpl(
            frequency, priorityLevel, enabled ) );
    }
    Logger.getInstance().debug(
        this, "Default configuration successfully loaded" );
  }
  
  /**
   * Does update preference defaults by pre-configured defaults if available
   * 
   * @param devicePreferences
   *          the sensor device to update defaults for
   */
  private final void updateDefaults( SensorDevicePreferences devicePreferences )
  {
    SensorDeviceConfiguration defaults =
        mapDeviceDefaults.get( devicePreferences.getDeviceIdentifier() );
    if ( defaults != null )
    {
      devicePreferences.setDefault( defaults );
    }
  }
  
  /**
   * Does update defaults for the managed device preferences
   * 
   * @param appPrefManager
   *          the application preference manager
   */
  public final void
      updateDefaults( ApplicationPreferenceManager appPrefManager )
  {
    // update device defaults by values from configuration file
    for ( Entry< SensorDeviceIdentifier, SensorDeviceConfiguration > entry : mapDeviceDefaults.entrySet() )
    {
      updateDefaults( appPrefManager.getPreferencesForDevice( entry.getKey() ) );
    }
    
    // update service defaults by values from configuration file if available
    appPrefManager.getServicePreferences().setDefault(
        createServiceConfiguration( appPrefManager ) );
    
    // update time provider defaults by values from configuration file if
    // available
    appPrefManager.getTimeProviderPreference().setDefault(
        createTimeProviderConfiguration( appPrefManager ) );
  }
  
  /**
   * Method to create a time provider default configuration from the XML file
   * entries to update the application preference defaults
   * 
   * @param appPrefManager
   *          the application preference manager
   * @return an updated default configuration for the time provider preferences
   */
  private TimeProviderConfiguration createTimeProviderConfiguration(
      ApplicationPreferenceManager appPrefManager )
  {
    // update time provider
    TimeProviderConfiguration tpConfig =
        appPrefManager.getTimeProviderPreference().getDefault();
    
    if ( sdcConfiguration != null )
    {
      tpConfig.update( sdcConfiguration.getTimeProviderConfigEntries() );
    }
    return tpConfig;
  }
  
  /**
   * Method to create a service default configuration from the XML file
   * preferences to update the application preference defaults
   * 
   * @param appPrefManager
   *          the application preference manager
   * @return an updated default configuration for the service preferences
   */
  private ServiceConfiguration createServiceConfiguration(
      ApplicationPreferenceManager appPrefManager )
  {
    ServiceConfiguration defaultConfiguration =
        appPrefManager.getServicePreferences().getDefault();
    if ( sdcConfiguration != null )
    {
      defaultConfiguration.update( sdcConfiguration );
    }
    return defaultConfiguration;
  }
  
  /**
   * Method to clean up before class can be destroyed
   */
  public final void onDestroy()
  {
    mapDeviceDefaults.clear();
    sdcConfiguration = null;
  }
  
  /**
   * Getter for the configured sensor devices
   * 
   * @return a list of the configured sensor devices to be supported by the SDC
   *         service
   */
  public Set< SensorDeviceIdentifier > getListDevices()
  {
    return Collections.unmodifiableSet( mapDeviceDefaults.keySet() );
  }

  /**
   * Method to test, if an external configuration is used.
  
   * @return true if an external configuration is used, false otherwise
   */
  public boolean isUsingExternalConfiguration()
  {
    return isUsingExternalConfiguration;
  }
}
