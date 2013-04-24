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

import android.content.Context;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceFactory;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner;

/**
 * Implementation of the sensor device factory, to be used by the
 * {@link SensorDeviceManagerImpl sensor device manager} to create the available
 * sensor devices.
 * 
 * @see SensorDeviceManagerImpl
 * @author Katy Hilgenberg
 * 
 */
public final class SensorDeviceFactoryImpl implements SensorDeviceFactory
{
  /**
   * Constructor
   */
  public SensorDeviceFactoryImpl()
  {
    super();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDeviceFactory#
   * createSensorDevice
   * (de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier)
   */
  @Override
  public SensorDevice createSensorDevice(
      SensorDeviceIdentifier deviceIdentifier, Context applicationContext )
  {
    try
    {
      SensorDevice device =
          doCreateSensorDevice( deviceIdentifier, applicationContext );
      SensorDeviceScanner scanner =
          doCreateSensorDeviceScanner( deviceIdentifier, applicationContext );
      
      if ( device != null && scanner != null )
      {
        // connect scanner and device and return device
        device.setScanner( scanner, applicationContext );
        return device;
      }
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    // return null on error or unknown resource
    return null;
  }
  
  /**
   * Internal device creation method
   * 
   * @param deviceIdentifier
   *          the device identifier
   * @param applicationContext
   *          the application context
   * @return the device, or null if unknown
   */
  private SensorDevice doCreateSensorDevice(
      SensorDeviceIdentifier deviceIdentifier, Context applicationContext )
  {
    // HINT: enhance for further devices
    
    switch ( deviceIdentifier )
    {
      case Accelerometer:
      {
        return new AccelerometerDevice( applicationContext );
      }
      case Gyroscope:
      {
        return new GyroscopeDevice( applicationContext );
      }
      case Light:
      {
        return new LightDevice( applicationContext );
      }
      case MagneticField:
      {
        return new MagneticFieldDevice( applicationContext );
      }
      case Orientation:
      {
        return new OrientationDevice( applicationContext );
      }
      case Pressure:
      {
        return new PressureDevice( applicationContext );
      }
      case Proximity:
      {
        return new ProximityDevice( applicationContext );
      }
      case Temperature:
      {
        return new TemperatureDevice( applicationContext );
      }
      case Wifi:
      {
        return new WifiDevice( applicationContext );
      }
      case Bluetooth:
      {
        return new BluetoothDevice( applicationContext );
      }
      case GPS:
      {
        return new GPSDevice( applicationContext );
      }
      case NetworkLocation:
      {
        return new NetworkLocationDevice( applicationContext );
      }
      case GSM:
      {
        return new GSMDevice( applicationContext );
      }
      case CDMA:
      {
        // not supported in the current version
        break;
      }
      case Twitter:
      {
        return new TwitterDevice( applicationContext );
      }
      case Audio:
      {
        return new AudioDevice( applicationContext );
      }
      case Tags:
      {
        return new TagDevice( applicationContext );
      }
      case TimeSyncStateChanges:
      {
        return new TimeProviderDevice();
      }
    }
    
    return null;
  }
  
  /**
   * Internal sensor device scanner creation method
   * 
   * @param deviceIdentifier
   *          the device identifier
   * @param applicationContext
   *          the application context
   * @return the device scanner, or null if unknown
   */
  private SensorDeviceScanner doCreateSensorDeviceScanner(
      SensorDeviceIdentifier deviceIdentifier, Context applicationContext )
  {
    // HINT: enhance for further device scanner
    
    switch ( deviceIdentifier )
    {
      case Accelerometer:
      {
        return new AccelerometerDeviceScanner();
      }
      case Gyroscope:
      {
        return new GyroscopeDeviceScanner();
      }
      case Light:
      {
        return new LightDeviceScanner();
      }
      case MagneticField:
      {
        return new MagneticFieldDeviceScanner();
      }
      case Orientation:
      {
        return new OrientationDeviceScanner();
      }
      case Pressure:
      {
        return new PressureDeviceScanner();
      }
      case Proximity:
      {
        return new ProximityDeviceScanner();
      }
      case Temperature:
      {
        return new TemperatureDeviceScanner();
      }
      case Wifi:
      {
        return new WifiDeviceScanner( applicationContext );
      }
      case Bluetooth:
      {
        return new BluetoothDeviceScanner();
      }
      case GPS:
      {
        return new GPSDeviceScanner();
      }
      case NetworkLocation:
      {
        return new NetworkLocationDeviceScanner();
      }
      case GSM:
      {
        return new GSMDeviceScanner();
      }
      case CDMA:
      {
        // not supported in the current version
        break;
      }
      case Twitter:
      {
        return new TwitterDeviceScanner( applicationContext.getContentResolver() );
      }
      case Audio:
      {
        return new AudioDeviceScanner( applicationContext.getContentResolver() );
      }
      case Tags:
      {
        return new TagDeviceScanner( applicationContext.getContentResolver() );
      }
      case TimeSyncStateChanges:
      {
        return new TimeProviderDeviceScanner();
      }
    }
    
    return null;
  }
}
