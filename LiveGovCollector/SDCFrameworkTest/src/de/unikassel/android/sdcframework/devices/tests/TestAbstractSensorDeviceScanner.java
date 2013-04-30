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

import android.test.AndroidTestCase;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;

/**
 * Tests for the abstract base class for any sensor device scanner.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAbstractSensorDeviceScanner extends AndroidTestCase
{
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    super.setUp();
  }
  
  /**
   * Test method for Initialization after construction.
   */
  public final void testAbstractSensorDeviceScanner()
  {
    AbstractSensorDeviceScannerForTest scanner =
        new AbstractSensorDeviceScannerForTest();
    
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
    assertNull( "Expected device null", scanner.getDevice() );
    
    // test bidirectional link to device
    AbstractSensorDeviceForTest device1 =
        new AbstractSensorDeviceForTest( SensorDeviceIdentifier.Unknown );
    device1.getConfiguration().setEnabled( true );
    scanner.setDevice( device1, getContext() );
    assertSame( "Expected scanner set for device", scanner, device1.getScanner() );
    assertSame( "Expected device set for scanner", device1, scanner.getDevice() );
    assertEquals( "Expected scanner enabled statesame as enabled state",
        device1.isDeviceScanningEnabled(), scanner.isEnabled() );
    
    AbstractSensorDeviceForTest device2 =
          new AbstractSensorDeviceForTest( SensorDeviceIdentifier.Unknown );
    device2.getConfiguration().setEnabled( false );
    scanner.setDevice( device2, getContext() );
    assertNull( "Expected old device unlinked", device1.getScanner() );
    assertSame( "Expected scanner set for device", scanner, device2.getScanner() );
    assertSame( "Expected device set for scanner", device2, scanner.getDevice() );
    assertEquals( "Expected scanner enabled statesame as enabled state",
            device2.isDeviceScanningEnabled(), scanner.isEnabled() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.AbstractSensorDeviceScanner#enable(boolean, android.content.Context)}
   * .
   */
  public final void testEnable()
  {
    // create a test scanner
    AbstractSensorDeviceScannerForTest scanner = new AbstractSensorDeviceScannerForTest();
    assertFalse( "Expected scanner not running", scanner.isEnabled() );

    scanner.isAbleToStart = false;
    assertFalse( "Expected scanner failed to enable", scanner.enable( true, getContext() ) );
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
    
    scanner.isAbleToStart = true;
    assertTrue( "Expected scanner enabled successful", scanner.enable( true, getContext() ) );
    assertTrue( "Expected scanner running", scanner.isEnabled() );
    
    scanner.isAbleToStop = false;
    assertFalse( "Expected scanner failed to disable", scanner.enable( false, getContext() ) );
    assertTrue( "Expected scanner running", scanner.isEnabled() );
    
    scanner.isAbleToStop = true;
    assertTrue( "Expected scanner disabled", scanner.enable( false, getContext() ) );
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.AbstractSensorDeviceScanner#onDestroy(android.content.Context)}
   * .
   */
  public final void testOnDestroy()
  {
    // create a test scanner
    SensorDeviceScanner scanner = new AbstractSensorDeviceScannerForTest();
    
    // create not enabled sensor device
    SensorDevice device = new AbstractSensorDeviceForTest( SensorDeviceIdentifier.GPS );
    device.setScanner( scanner, getContext() );
    scanner.enable( true, getContext() );
    
    assertSame( "Expected scanner set for device", scanner, device.getScanner() );
    assertSame( "Expected device set for scanner", device, scanner.getDevice() );
    assertTrue( "Expected scanner running", scanner.isEnabled() );
    
    device.onDestroy( getContext() );
    assertNull( "Expected scanner cleared for device", device.getScanner() );
    assertNull( "Expected device cleared for scanner", scanner.getDevice() );
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
  }
  
}
