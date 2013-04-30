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
package de.unikassel.android.sdcframework.devices.tests;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.unikassel.android.sdcframework.devices.SensorDeviceAvailabilityTester;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.provider.AudioProviderData;
import de.unikassel.android.sdcframework.provider.TwitterProviderData;
import de.unikassel.android.sdcframework.provider.TagProviderData;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.test.AndroidTestCase;

/**
 * Tests for the sensor device availability tester.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSensorDeviceAvailabilityTester extends AndroidTestCase
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
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Testing preconditions
   */
  public void testPreconditions()
  {
    SensorDeviceAvailabilityTester availabilityTester =
        SensorDeviceAvailabilityTester.getInstance();
    
    assertNotNull( "Expected instance", availabilityTester );
    assertSame( "Expected always same instance until release",
        availabilityTester, SensorDeviceAvailabilityTester.getInstance() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.SensorDeviceAvailabilityTester#getAvailableSensorDevices()}
   * .
   */
  public final void testGetAvailableSensorDevices()
  {
    Set< SensorDeviceIdentifier > availableSensorDevices =
        new HashSet< SensorDeviceIdentifier >();
    
    //HINT: enhance for further devices implemented in the framework
    ContentResolver contentResolver = getContext().getContentResolver();
    if ( contentResolver.acquireContentProviderClient( 
        AudioProviderData.getInstance().getContentUri() ) != null )
    {
      availableSensorDevices.add( SensorDeviceIdentifier.Twitter );
    }
    
    if ( contentResolver.acquireContentProviderClient( 
        TwitterProviderData.getInstance().getContentUri() ) != null )
    {
      availableSensorDevices.add( SensorDeviceIdentifier.Twitter );
    }
    
    if ( contentResolver.acquireContentProviderClient( 
        TagProviderData.getInstance().getContentUri() ) != null )
    {
      availableSensorDevices.add( SensorDeviceIdentifier.Tags );
    }
    
    SensorManager sensorManager =
        (SensorManager) getContext().getSystemService( Context.SENSOR_SERVICE );
    if ( sensorManager.getSensorList( Sensor.TYPE_ACCELEROMETER ).size() > 0 )
      availableSensorDevices.add( SensorDeviceIdentifier.Accelerometer );
    if( sensorManager.getSensorList( Sensor.TYPE_GYROSCOPE ).size() > 0 )
      availableSensorDevices.add( SensorDeviceIdentifier.Gyroscope );
    if( sensorManager.getSensorList( Sensor.TYPE_LIGHT ).size() > 0 )
      availableSensorDevices.add( SensorDeviceIdentifier.Light );
    if( sensorManager.getSensorList( Sensor.TYPE_MAGNETIC_FIELD ).size() > 0 )
      availableSensorDevices.add( SensorDeviceIdentifier.MagneticField );
    if( sensorManager.getSensorList( Sensor.TYPE_ORIENTATION).size() > 0 )
      availableSensorDevices.add( SensorDeviceIdentifier.Orientation );
    if( sensorManager.getSensorList( Sensor.TYPE_PROXIMITY ).size() > 0 )
      availableSensorDevices.add( SensorDeviceIdentifier.Proximity );
    if( sensorManager.getSensorList( Sensor.TYPE_PRESSURE ).size() > 0 )
      availableSensorDevices.add( SensorDeviceIdentifier.Pressure );
    if( sensorManager.getSensorList( Sensor.TYPE_TEMPERATURE ).size() > 0 )
      availableSensorDevices.add( SensorDeviceIdentifier.Temperature );
    
    WifiManager wifiManager =
        (WifiManager) getContext().getSystemService( Context.WIFI_SERVICE );
    if ( wifiManager != null )
      availableSensorDevices.add( SensorDeviceIdentifier.Wifi );
    
    if ( BluetoothAdapter.getDefaultAdapter() != null )
      availableSensorDevices.add( SensorDeviceIdentifier.Bluetooth );
    
    LocationManager locationManager =
        (LocationManager) getContext().getSystemService(
        Context.LOCATION_SERVICE );
    if ( locationManager.getProvider( LocationManager.GPS_PROVIDER ) != null )
      availableSensorDevices.add( SensorDeviceIdentifier.GPS );
    
    TelephonyManager telephonyManager =
        (TelephonyManager) getContext().getSystemService(
        Context.TELEPHONY_SERVICE );
    if ( telephonyManager != null
        && telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM )
      availableSensorDevices.add( SensorDeviceIdentifier.GSM );
    
    // do test if the available devices have been detected
    SensorDeviceAvailabilityTester availabilityTester =
        SensorDeviceAvailabilityTester.getInstance();
    
    availabilityTester.configure( availableSensorDevices, getContext() );
    
    List< SensorDeviceIdentifier > detectedAvailableSensorDevices =
        availabilityTester.getAvailableSensorDevices();
    
    assertEquals( "Unexpected device count", availableSensorDevices.size(),
        detectedAvailableSensorDevices.size() );
    
    for ( SensorDeviceIdentifier id : availableSensorDevices )
    {
      assertTrue( "Missing sensor device id " + id.toString(),
          detectedAvailableSensorDevices.contains( id ) );
    }
    
  }
  
}
