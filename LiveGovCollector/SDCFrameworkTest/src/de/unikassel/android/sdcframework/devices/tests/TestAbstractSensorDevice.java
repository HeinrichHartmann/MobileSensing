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

import de.unikassel.android.sdcframework.devices.SensorDeviceConfigurationUpdateVisitor;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationChangeEventImpl;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfigurationChangeEvent;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.provider.Settings;
import android.test.AndroidTestCase;

/**
 * Tests for the abstract base class for any sensor device.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAbstractSensorDevice extends AndroidTestCase
{
  /**
   * Flag to store air plain mode setting
   */
  private int airplanemode;
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    // save airplane mode setting on device
    airplanemode = Settings.System.getInt( getContext().getContentResolver(),
        Settings.System.AIRPLANE_MODE_ON, 0 );
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
    // restore airplane mode setting on device
    Settings.System.getInt( getContext().getContentResolver(),
        Settings.System.AIRPLANE_MODE_ON, airplanemode );
    super.tearDown();
  }
  
  /**
   * Test method for Initialization after construction.
   */
  public final void testAbstractSensorDevice()
  {
    AbstractSensorDeviceForTest device = new AbstractSensorDeviceForTest( SensorDeviceIdentifier.GPS );
    
    assertEquals( "Expected identifier set", SensorDeviceIdentifier.GPS,
        device.getDeviceIdentifier() );
    assertNotNull( "Expected configuration not null", device.getConfiguration() );
    assertNull( "Expected scanner null", device.getScanner() );
    assertEquals( "Unexpected enabled value",
        device.getConfiguration().isEnabled(),
        device.isDeviceScanningEnabled() );
    assertFalse(
        "Expected no call to configuration change handler during construction",
        device.onConfigurationChangedWasCalled );
    assertFalse(
        "Expected no call to device not enabled in system handler during construction",
        device.doSignalDeviceNotEnabledInSystemWasCalled );
    
    // test bidirectional link to scanner
    AbstractSensorDeviceScannerForTest scanner = new AbstractSensorDeviceScannerForTest();
    device.getConfiguration().setEnabled( false );
    device.setScanner( scanner, getContext() );
    assertSame( "Expected scanner set for device", scanner, device.getScanner() );
    assertSame( "Expected device set for scanner", device, scanner.getDevice() );
    assertFalse( "Expected scanner disabled like device", scanner.isEnabled() );
    
    scanner = new AbstractSensorDeviceScannerForTest();
    device.getConfiguration().setEnabled( true );
    device.setScanner( scanner, getContext() );
    assertSame( "Expected scanner set for device", scanner, device.getScanner() );
    assertSame( "Expected device set for scanner", device, scanner.getDevice() );
    assertTrue( "Expected scanner enabled like device", scanner.isEnabled() );
    
    device.setScanner( null, getContext() );
    assertFalse( "Expected scanner disabled after deletion of link to device",
        scanner.isEnabled() );
    
    scanner = new AbstractSensorDeviceScannerForTest();
    device.getConfiguration().setEnabled( true );
    scanner.setDevice( device, getContext() );
    assertSame( "Expected scanner set for device", scanner, device.getScanner() );
    assertSame( "Expected device set for scanner", device, scanner.getDevice() );
    assertTrue( "Expected scanner enabled like device", scanner.isEnabled() );
    
    scanner.setDevice( null, getContext() );
    assertFalse( "Expected scanner disabled after deletion of link to device",
        scanner.isEnabled() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.AbstractSensorDevice#accept(de.unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor)}
   * .
   */
  public final void testAcceptVisitor()
  {
    AbstractSensorDeviceForTest device = new AbstractSensorDeviceForTest( SensorDeviceIdentifier.GPS );
    device.getConfiguration().setEnabled( false );
    device.getConfiguration().setFrequency( 1000 );
    device.getConfiguration().setSamplePriority( SensorDevicePriorities.Level0 );
    
    assertFalse(
        "Expected no call to configuration change handler during construction",
        device.onConfigurationChangedWasCalled );
    
    SensorDeviceConfigurationImpl configuration =
        new SensorDeviceConfigurationImpl();
    configuration.setEnabled( true );
    configuration.setFrequency( 10000 );
    configuration.setSamplePriority( SensorDevicePriorities.Level4 );
    
    SensorDeviceConfigurationChangeEvent update =
        new SensorDeviceConfigurationChangeEventImpl( configuration,
            device.getDeviceIdentifier() );
    
    SensorDeviceConfigurationUpdateVisitor visitor =
        new SensorDeviceConfigurationUpdateVisitor( update, getContext() );
    device.accept( visitor );
    
    assertTrue(
        "Expected a call to configuration change handler has happen",
        device.onConfigurationChangedWasCalled );
    assertFalse(
        "Expected no call to device not enabled in system handler",
        device.doSignalDeviceNotEnabledInSystemWasCalled );
    assertEquals( "Unexpected enabled value after accepting visitor ",
        configuration.isEnabled(), device.getConfiguration().isEnabled() );
    assertEquals( "Unexpected frequency value after accepting visitor ",
        configuration.getFrequency(), device.getConfiguration().getFrequency() );
    assertEquals( "Unexpected priority value after accepting visitor ",
        configuration.getSamplePriority(),
        device.getConfiguration().getSamplePriority() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.AbstractSensorDevice#enableDeviceScanning(boolean, android.content.Context)}
   * .
   */
  public final void testEnableDeviceScanning()
  {
    // create a test scanner
    SensorDeviceScanner scanner = new AbstractSensorDeviceScannerForTest();
    
    // create not enabled sensor device
    AbstractSensorDeviceForTest device = new AbstractSensorDeviceForTest( SensorDeviceIdentifier.GPS );
    device.getConfiguration().setEnabled( false );
    device.isDeviceEnabledInSystem = true;
    device.doSignalDeviceNotEnabledInSystemWasCalled = false;
    
    // 1a) try to enable/disable with device available in system but no scanner
    // available
    boolean result = device.enableDeviceScanning( true, getContext() );
    assertFalse(
        "Expected no call to device not enabled in system handler",
        device.doSignalDeviceNotEnabledInSystemWasCalled );
    assertFalse( "Expected call to enable failed due to scanner unavailable",
        result );
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
    
    scanner.start( getContext() );
    result = device.enableDeviceScanning( false, getContext() );
    assertFalse( "Expected call to enable failed due to scanner unavailable",
        result );
    
    // 1b) try to enable/disable with device available in system and scanner
    // available
    device.setScanner( scanner, getContext() );
    
    result = device.enableDeviceScanning( true, getContext() );
    assertFalse(
        "Expected no call to device not enabled in system handler",
        device.doSignalDeviceNotEnabledInSystemWasCalled );
    assertTrue( "Expected call to enable succeeded due to scanner available",
        result );
    assertTrue( "Expected scanner running", scanner.isEnabled() );
    
    result = device.enableDeviceScanning( false, getContext() );
    assertTrue(
        "Expected the call to disable succeeded due to scanner available",
        result );
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
    
    // 2a) try to enable/disable with device not available in system and
    // airplane mode off
    device.isDeviceEnabledInSystem = false;
    Settings.System.putInt( getContext().getContentResolver(),
        Settings.System.AIRPLANE_MODE_ON, 0 );
    
    result = device.enableDeviceScanning( true, getContext() );
    assertTrue(
        "Expected a call to device not enabled in system handler",
        device.doSignalDeviceNotEnabledInSystemWasCalled );
    assertTrue(
        "Expected call to enable returns true ( but scanner should be disabled )",
        result );
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
    
    scanner.start( getContext() );
    result = device.enableDeviceScanning( false, getContext() );
    assertTrue(
        "Expected the call to disable succeeded due to scanner available",
        result );
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
    
    // 2b) try to enable/disable with device not available in system and
    // airplane mode on
    device.doSignalDeviceNotEnabledInSystemWasCalled = false;
    Settings.System.putInt( getContext().getContentResolver(),
        Settings.System.AIRPLANE_MODE_ON, 1 );
    
    result = device.enableDeviceScanning( true, getContext() );
    assertFalse(
        "Expected no call to device not enabled in system handler",
        device.doSignalDeviceNotEnabledInSystemWasCalled );
    assertTrue(
        "Expected call to enable returns true ( but scanner should be disabled )",
        result );
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
    
    scanner.start( getContext() );
    result = device.enableDeviceScanning( false, getContext() );
    assertTrue(
        "Expected the call to disable succeeded due to scanner available",
        result );
    assertFalse( "Expected scanner not running", scanner.isEnabled() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.AbstractSensorDevice#updateConfiguration(de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration, android.content.Context)}
   * .
   */
  public final void testUpdateConfiguration()
  {
    AbstractSensorDeviceForTest device = new AbstractSensorDeviceForTest( SensorDeviceIdentifier.GPS );
    device.getConfiguration().setEnabled( false );
    device.getConfiguration().setFrequency( 1000 );
    device.getConfiguration().setSamplePriority( SensorDevicePriorities.Level0 );
    
    assertFalse(
        "Expected no call to configuration change handler during construction",
        device.onConfigurationChangedWasCalled );
    
    SensorDeviceConfigurationImpl configuration =
        new SensorDeviceConfigurationImpl();
    configuration.setEnabled( true );
    configuration.setFrequency( 10000 );
    configuration.setSamplePriority( SensorDevicePriorities.Level4 );
    
    device.updateConfiguration( configuration, getContext() );
    
    assertTrue(
        "Expected a call to configuration change handler",
        device.onConfigurationChangedWasCalled );
    assertFalse(
        "Expected no call to device not enabled in system handler",
        device.doSignalDeviceNotEnabledInSystemWasCalled );
    assertEquals( "Unexpected enabled value after accepting visitor ",
        configuration.isEnabled(), device.getConfiguration().isEnabled() );
    assertEquals( "Unexpected frequency value after accepting visitor ",
        configuration.getFrequency(), device.getConfiguration().getFrequency() );
    assertEquals( "Unexpected priority value after accepting visitor ",
        configuration.getSamplePriority(),
        device.getConfiguration().getSamplePriority() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.AbstractSensorDevice#onDestroy(android.content.Context)}
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
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.AbstractSensorDevice#isAirplaneModeOn(android.content.Context)}
   * .
   */
  public final void testIsAirplaneModeOn()
  {
    AbstractSensorDeviceForTest device = new AbstractSensorDeviceForTest( SensorDeviceIdentifier.GSM );
    
    // test airplane mode enabled
    Settings.System.putInt( getContext().getContentResolver(),
        Settings.System.AIRPLANE_MODE_ON, 1 );
    assertTrue( "Expected airplane mode on",
        device.isAirplaneModeOn( getContext() ) );
    
    // test airplane mode disabled
    Settings.System.putInt( getContext().getContentResolver(),
        Settings.System.AIRPLANE_MODE_ON, 0 );
    assertFalse( "Expected airplane mode off",
        device.isAirplaneModeOn( getContext() ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.AbstractSensorDevice#doHandleDeviceDisabledBySystem(android.content.Context)}
   * .
   */
  public final void testDoHandleDeviceDisabledBySystem()
  {
    AbstractSensorDeviceForTest device = new AbstractSensorDeviceForTest( SensorDeviceIdentifier.GSM );
    SensorDeviceScanner scanner = new AbstractSensorDeviceScannerForTest();
    
    device.getConfiguration().setEnabled( true );
    device.setScanner( scanner, getContext() );
    device.isDeviceEnabledInSystem = false;
    
    try
    {
      device.doHandleDeviceDisabledBySystem( null );
      assertFalse(
          "Expected no call to device not enabled in system handler",
          device.doSignalDeviceNotEnabledInSystemWasCalled );
      assertTrue(
          "Expected device enabled state unchanged",
          device.isDeviceScanningEnabled() );
      assertTrue(
          "Expected scanner still running", scanner.isEnabled() );
      
    }
    catch ( Exception e )
    {
      fail( "null context should not throw an exception" );
    }
    
    device.doHandleDeviceDisabledBySystem( getContext() );
    assertTrue(
        "Expected a call to device not enabled in system handler",
        device.doSignalDeviceNotEnabledInSystemWasCalled );
    assertTrue(
        "Expected device enabled state unchanged",
        device.isDeviceScanningEnabled() );
    assertFalse(
        "Expected scanner disabled if not available in system",
        scanner.isEnabled() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.AbstractSensorDevice#doHandleDeviceEnabledBySystem(android.content.Context)}
   * .
   */
  public final void testDoHandleDeviceEnabledBySystem()
  {
    AbstractSensorDeviceForTest device = new AbstractSensorDeviceForTest( SensorDeviceIdentifier.GSM );
    SensorDeviceScanner scanner = new AbstractSensorDeviceScannerForTest();
    
    device.getConfiguration().setEnabled( true );
    device.setScanner( scanner, getContext() );
    scanner.enable( false, getContext() );
    device.isDeviceEnabledInSystem = true;
    
    try
    {
      device.doHandleDeviceEnabledBySystem( null );
      assertFalse(
          "Expected no call to device not enabled in system handler",
          device.doSignalDeviceNotEnabledInSystemWasCalled );
      assertTrue(
          "Expected device enabled state unchanged",
          device.isDeviceScanningEnabled() );
      assertFalse(
          "Expected scanner still not running", scanner.isEnabled() );
      
    }
    catch ( Exception e )
    {
      fail( "null context should not throw an exception" );
    }
    
    device.doHandleDeviceEnabledBySystem( getContext() );
    assertFalse(
        "Expected no call to device not enabled in system handler",
        device.doSignalDeviceNotEnabledInSystemWasCalled );
    assertTrue(
        "Expected device enabled state unchanged",
        device.isDeviceScanningEnabled() );
    assertTrue( "Expected scanner enabled now", scanner.isEnabled() );
  }
  
}
