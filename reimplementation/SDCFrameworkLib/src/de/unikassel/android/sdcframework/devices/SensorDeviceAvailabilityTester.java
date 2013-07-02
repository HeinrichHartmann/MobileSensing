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
package de.unikassel.android.sdcframework.devices;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.provider.AudioProviderData;
import de.unikassel.android.sdcframework.provider.TagProviderData;
import de.unikassel.android.sdcframework.provider.TwitterProviderData;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * The global singleton to test for sensor device availability. With the first
 * call to the {link {@link #configure(Set, Context)} method, the availability
 * of supported devices is tested and the identifiers will be stored to be
 * queried later by a call to {@link #getAvailableSensorDevices()}.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class SensorDeviceAvailabilityTester
{
  /**
   * The singleton instance of the sensor availability tester manager
   */
  private static SensorDeviceAvailabilityTester instance;
  
  /**
   * Vector to store the detected sensor devices
   */
  private Vector< SensorDeviceIdentifier > vecSensorDevices;
  
  /**
   * flag if sensor device list is initialized
   */
  private boolean initialized;
  
  /**
   * Constructor
   */
  private SensorDeviceAvailabilityTester()
  {
    super();
    setInitialized( false );
  }
  
  /**
   * Access to the singleton instance of the preference manager
   * 
   * @return the instance
   */
  public synchronized final static SensorDeviceAvailabilityTester getInstance()
  {
    if ( instance == null )
    {
      instance = new SensorDeviceAvailabilityTester();
    }
    return instance;
  }
  
  /**
   * Getter for the vecSensorDevices
   * 
   * @return the vecSensorDevices
   */
  private final Vector< SensorDeviceIdentifier > getVecSensorDevices()
  {
    if ( vecSensorDevices == null )
    {
      setVecSensorDevices( new Vector< SensorDeviceIdentifier >() );
    }
    return vecSensorDevices;
  }
  
  /**
   * Setter for the vecSensorDevices
   * 
   * @param vecSensorDevices
   *          the vecSensorDevices to set
   */
  private final void setVecSensorDevices(
      Vector< SensorDeviceIdentifier > vecSensorDevices )
  {
    this.vecSensorDevices = vecSensorDevices;
  }
  
  /**
   * Getter for the initialized
   * 
   * @return the initialized
   */
  public final boolean isInitialized()
  {
    return initialized;
  }
  
  /**
   * Setter for the initialized
   * 
   * @param initialized
   *          the initialized to set
   */
  private final void setInitialized( boolean initialized )
  {
    this.initialized = initialized;
  }
  
  /**
   * Does detect available sensor devices
   * 
   * @param configuredDevices
   *          the configured devices for the service
   * @param applicationContext
   *          the application context
   */
  private synchronized final void detectAvailableSensorDevices(
      Set< SensorDeviceIdentifier > configuredDevices,
      Context applicationContext )
  {
    Vector< SensorDeviceIdentifier > vec = getVecSensorDevices();
    vec.clear();
    
    for ( SensorDeviceIdentifier id : configuredDevices )
    {
      if ( isDeviceAvailable( id, applicationContext ) )
      {
        vec.add( id );
      }
    }
    
    setInitialized( true );
  }
  
  /**
   * Test method for availability of a specific sensor device
   * 
   * @param identifier
   *          the device identifier
   * @param applicationContext
   *          the application context
   * @return true if sensor is available, false otherwise
   */
  private final boolean isDeviceAvailable( SensorDeviceIdentifier identifier,
      Context applicationContext )
  {
    SensorManager sensorManager =
        (SensorManager) applicationContext.getSystemService( Context.SENSOR_SERVICE );
    
    // HINT: enhance for further devices implemented in the framework
    switch ( identifier )
    {
      case Accelerometer:
      {
        return sensorManager.getSensorList( Sensor.TYPE_ACCELEROMETER ).size() > 0;
      }
      case Gyroscope:
      {
        return sensorManager.getSensorList( Sensor.TYPE_GYROSCOPE ).size() > 0;
      }
      case Light:
      {
        return sensorManager.getSensorList( Sensor.TYPE_LIGHT ).size() > 0;
      }
      case MagneticField:
      {
        return sensorManager.getSensorList( Sensor.TYPE_MAGNETIC_FIELD ).size() > 0;
      }
      case Orientation:
      {
        return sensorManager.getSensorList( Sensor.TYPE_ORIENTATION ).size() > 0;
      }
      case Pressure:
      {
        return sensorManager.getSensorList( Sensor.TYPE_PRESSURE ).size() > 0;
      }
      case Proximity:
      {
        return sensorManager.getSensorList( Sensor.TYPE_PROXIMITY ).size() > 0;
      }
      case Temperature:
      {
        return sensorManager.getSensorList( Sensor.TYPE_TEMPERATURE ).size() > 0;
      }
      case Wifi:
      {
        WifiManager wifiManager =
            (WifiManager) applicationContext.getSystemService( Context.WIFI_SERVICE );
        return wifiManager != null;
      }
      case Bluetooth:
      {
        return BluetoothAdapter.getDefaultAdapter() != null;
      }
      case GPS:
      {
        LocationManager locationManager =
            (LocationManager) applicationContext.getSystemService( Context.LOCATION_SERVICE );
        return locationManager.getProvider( LocationManager.GPS_PROVIDER ) != null;
        
      }
      case NetworkLocation:
      {
        LocationManager locationManager =
            (LocationManager) applicationContext.getSystemService( Context.LOCATION_SERVICE );
        return locationManager.getProvider( LocationManager.NETWORK_PROVIDER ) != null;
        
      }
      case GSM:
      {
        TelephonyManager telephonyManager =
            (TelephonyManager) applicationContext.getSystemService( Context.TELEPHONY_SERVICE );
        
        return telephonyManager != null
            && telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM;
      }
      case CDMA:
      {
        TelephonyManager telephonyManager =
            (TelephonyManager) applicationContext.getSystemService( Context.TELEPHONY_SERVICE );
        
        return telephonyManager != null
            && telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_CDMA;
      }
      case Twitter:
      {
        ContentResolver contentResolver =
            applicationContext.getContentResolver();
        if ( contentResolver.acquireContentProviderClient( 
            TwitterProviderData.getInstance().getContentUri() ) != null )
        {
          return true;
        }
      }
      case Audio:
      {
        ContentResolver contentResolver =
            applicationContext.getContentResolver();
        if ( contentResolver.acquireContentProviderClient(
            AudioProviderData.getInstance().getContentUri() ) != null )
        {
          return true;
        }        
      }
      case Tags:
      {
        ContentResolver contentResolver =
            applicationContext.getContentResolver();
        if ( contentResolver.acquireContentProviderClient( 
            TagProviderData.getInstance().getContentUri() ) != null )
        {
          return true;
        }
      }
      case TimeSyncStateChanges:
      {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Getter for available Sensor devices
   * 
   * @return a list with the identifiers of available sensor devices
   */
  public synchronized final List< SensorDeviceIdentifier > getAvailableSensorDevices()
  {
    return Collections.unmodifiableList( getVecSensorDevices() );
  }
  
  /**
   * Method to inject the sensor devices from service configuration
   * 
   * @param configuredDevices
   *          the configured devices for the service
   * @param applicationContext
   *          the application context
   */
  public final void configure( Set< SensorDeviceIdentifier > configuredDevices,
      Context applicationContext )
  {
    Set< SensorDeviceIdentifier > devicesConfiguredInFramework =
        configuredDevices;
    if ( configuredDevices.isEmpty() )
    {
      // no devices configured => just allow all known devices
      devicesConfiguredInFramework =
          new HashSet< SensorDeviceIdentifier >(
          Arrays.asList( SensorDeviceIdentifier.values() ) );
      Logger.getInstance().warning( this,
          "No sensors are configured! We do just allow all known types!" );
    }
    detectAvailableSensorDevices( devicesConfiguredInFramework,
        applicationContext );
  }
}
