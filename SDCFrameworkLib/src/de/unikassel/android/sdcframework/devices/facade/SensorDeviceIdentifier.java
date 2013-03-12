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
package de.unikassel.android.sdcframework.devices.facade;

/**
 * Identifiers for all known sensor device types. <br/>
 * <br/>
 * The sensor identifiers will be used for internal sensor identification as
 * well as to identify the source device of
 * {@linkplain de.unikassel.android.sdcframework.data.Sample#getDeviceIdentifier()
 * collected samples}.
 * 
 * @author Katy Hilgenberg
 * 
 */
public enum SensorDeviceIdentifier
{
  /**
   * accelerometer sensor.
   */
  Accelerometer,
  
  /**
   * gyroscope sensor.
   */
  Gyroscope,

  /**
   * light sensor.
   */
  Light, 
  
  /**
   * magnetic field sensor.
   */
  MagneticField,
  
  /**
   * orientation sensor.
   */
  Orientation,
  
  /**
   * pressure sensor.
   */
  Pressure,
  
  /**
   * proximity sensor.
   */
  Proximity,
  
  /**
   * temperature sensor.
   */
  Temperature,
  
  /**
   * Android Wifi device.
   */
  Wifi,
  
  /**
   * Android Bluetooth device.
   */
  Bluetooth,
  
  /**
   * GPS device.
   */
  GPS,
  
  /**
   * Virtual network location device ( based on cell of wlan location information ).
   */
  NetworkLocation,
  
  /**
   * GSM (Global System for Mobile Communications).
   */
  GSM,
  
  /**
   * CDMA (Code Division Multiple Access).
   */
  // HINT: implement CDMA sensor ( but how to test in Europe ^^ ) ... or remove it
  CDMA,
  
  /**
   * Sensor polling a provider for collected Twitter samples.
   */
  Twitter, 
  
  /**
   * Virtual device to transport recorded sound files.
   */
  Audio,
  
  /**
   * Virtual device to transport one or more tags.
   */
  Tags,
  
  /**
   * Virtual device to transport the time provider synchronization state changes.
   */
  TimeSyncStateChanges,
  
  /**
   * unknown device
   */
  Unknown

}
