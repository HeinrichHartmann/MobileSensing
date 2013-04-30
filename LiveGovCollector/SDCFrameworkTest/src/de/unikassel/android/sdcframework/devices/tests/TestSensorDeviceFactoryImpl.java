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
import java.util.Set;

import de.unikassel.android.sdcframework.devices.SensorDeviceAvailabilityTester;
import de.unikassel.android.sdcframework.devices.SensorDeviceFactoryImpl;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.test.AndroidTestCase;

/**
 * Tests for the sensor device factory.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSensorDeviceFactoryImpl extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
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
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test sensor device factory
   */
  public final void testSensorDeviceFactory()
  {
    // create factory
    SensorDeviceFactoryImpl factory = new SensorDeviceFactoryImpl();
    
    // create list for currently implemented sensors
    Set< SensorDeviceIdentifier > sensors =
        new HashSet< SensorDeviceIdentifier >();
    
    SensorDeviceAvailabilityTester.getInstance().configure( sensors,
        getContext() );
    for ( SensorDeviceIdentifier sensor : SensorDeviceAvailabilityTester.getInstance().getAvailableSensorDevices() )
    {
      sensors.add( sensor );
    }
    
    for ( SensorDeviceIdentifier sensor : sensors )
    {
      SensorDevice device = factory.createSensorDevice( sensor, getContext() );
      assertNotNull( "Expected device created: " + sensor.toString(), device );
      assertEquals( "Unexpected device identifier", sensor,
            device.getDeviceIdentifier() );
      assertFalse( "Expected device scanning disabled initally",
            device.isDeviceScanningEnabled() );
      SensorDeviceScanner scanner = device.getScanner();
      assertNotNull( "Expected scanner created and assigned to device",
            scanner );
      assertFalse( "Expected device scanner not running initially",
            scanner.isEnabled() );
    }
  }
}
