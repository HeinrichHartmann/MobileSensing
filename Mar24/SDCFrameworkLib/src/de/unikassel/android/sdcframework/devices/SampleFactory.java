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

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.BluetoothSampleData;
import de.unikassel.android.sdcframework.data.independent.GeoLocation;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.data.independent.WifiSampleData;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.TimeInformation;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.location.Location;
import android.net.wifi.ScanResult;

/**
 * A factory to create samples from Android specific types ( implemented as
 * singleton ).
 * 
 * @author Katy Hilgenberg
 * 
 */
public class SampleFactory
{
  /**
   * The location tracker
   */
  private final LocationTracker locationTracker;
  
  /**
   * The global singleton instance
   */
  private static SampleFactory instance;
  
  /**
   * Constructor
   */
  private SampleFactory()
  {
    super();
    locationTracker = new LocationTracker();
  }
  
  /**
   * Getter for the locationTracker
   * 
   * @return the locationTracker
   */
  public LocationTracker getLocationTracker()
  {
    return locationTracker;
  }
  
  /**
   * Getter for the global singleton instance
   * 
   * @return the global singleton instance
   */
  public static synchronized SampleFactory getInstance()
  {
    if ( instance == null )
    {
      instance = new SampleFactory();
    }
    return instance;
  }
  
  /**
   * Method to enable location tracking
   * 
   * @param enable
   *          flag for enabled state of location tracking 
   */
  public void enableLocationTracking(
      boolean enable )
  {
    locationTracker.setEnabled( enable );
  }
  
  /**
   * Method to create a sample from available information
   * @param timeInfo 
   *          the sample time stamp and NTP sync state
   * @param id
   *          the sensor device identifier
   * @param priority
   *          the priority
   * @param data
   *          the sensor specific data
   * @return the created sample
   */
  public Sample createSample( TimeInformation timeInfo, SensorDeviceIdentifier id, int priority,
      SampleData data )
  {
    Sample sample = null;
    
    // if we are out of time sync -> throw sample away
    if ( timeInfo != null )
    {
      // create a copy of the current state sample
      // and update time stamp and sample priority
      sample = new Sample( id );
      sample.setTimeStamp( timeInfo.ts );
      sample.setPriority( priority );
      sample.setData( data );
      sample.setTimeSynced( timeInfo.synced );
      
      // store the current most actual location fix
      Location currentLocation = locationTracker.getCurrentLocation();
      if ( currentLocation != null )
      {
        GeoLocation location = new GeoLocation();
        location.setLat( currentLocation.getLatitude() );
        location.setLon( currentLocation.getLongitude() );
        sample.setLocation( location );
      }
    }
    return sample;
  }
  
  /**
   * Method to create a wifi sample from a {@linkplain ScanResult}
   * 
   * @param scanResult
   *          the Wifi scan result to create the sample from
   * @param isConnected
   *          flag if it is the current Wifi connection
   * @return a Wifi sample
   */
  public WifiSampleData createWifiSampleData( ScanResult scanResult,
      boolean isConnected )
  {
    WifiSampleData data = new WifiSampleData();
    data.setSSID( scanResult.SSID );
    data.setBSSID( scanResult.BSSID );
    data.setCapabilities( scanResult.capabilities );
    data.setFrequency( scanResult.frequency );
    data.setLevel( scanResult.level );
    data.setConnected( isConnected );
    
    return data;
  }
  
  /**
   * Method to create an bluetooth sample from an {@linkplain BluetoothDevice}
   * 
   * @param bluetoothDevice
   *          the bluetooth device
   * @param rssi
   *          the received signal strength indicator
   * @return a bluetooth sample
   */
  public BluetoothSampleData createBluetoothSampleData(
      BluetoothDevice bluetoothDevice, Short rssi )
  {
    BluetoothSampleData data = new BluetoothSampleData();
    data.setAddress( bluetoothDevice.getAddress() );
    data.setName( bluetoothDevice.getName() );
    data.setRSSI( rssi );
    
    BluetoothClass bluetoothClass = bluetoothDevice.getBluetoothClass();
    if ( bluetoothClass != null )
    {
      data.setBluetoothClass( bluetoothClassToHumanReadableInformation( bluetoothClass ) );
    }
    
    return data;
  }
  
  /**
   * Does translate Android BluetoothClass in a human readable form
   * 
   * @param bluetoothClass
   *          the buetooth class
   * @return a human readable class representation
   */
  public String bluetoothClassToHumanReadableInformation(
      BluetoothClass bluetoothClass )
  {
    switch ( bluetoothClass.getMajorDeviceClass() )
    {
      case BluetoothClass.Device.Major.AUDIO_VIDEO:
      {
        return "AUDIO_VIDEO";
      }
      case BluetoothClass.Device.Major.COMPUTER:
      {
        return "COMPUTER";
      }
      case BluetoothClass.Device.Major.HEALTH:
      {
        return "HEALTH";
      }
      case BluetoothClass.Device.Major.IMAGING:
      {
        return "IMAGING";
      }
      case BluetoothClass.Device.Major.MISC:
      {
        return "MISC";
      }
      case BluetoothClass.Device.Major.NETWORKING:
      {
        return "NETWORKING";
      }
      case BluetoothClass.Device.Major.PERIPHERAL:
      {
        return "PERIPHERAL";
      }
      case BluetoothClass.Device.Major.PHONE:
      {
        return "PHONE";
      }
      case BluetoothClass.Device.Major.TOY:
      {
        return "TOY";
      }
      case BluetoothClass.Device.Major.UNCATEGORIZED:
      {
        return "UNCATEGORIZED";
      }
      case BluetoothClass.Device.Major.WEARABLE:
      {
        return "WEARABLE";
      }
    }
    return "UNCATEGORIZED";
  }
}
