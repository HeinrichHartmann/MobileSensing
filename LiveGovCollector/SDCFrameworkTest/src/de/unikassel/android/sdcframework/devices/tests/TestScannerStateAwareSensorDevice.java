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

import de.unikassel.android.sdcframework.devices.ScannerStateAwareSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.content.Context;
import android.test.AndroidTestCase;

/**
 * Test for the scanner state aware sensor device class.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestScannerStateAwareSensorDevice extends AndroidTestCase
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
  
  /* (non-Javadoc)
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * A test implementation extending scanner state aware sensor device
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class ScannerStateAwareSensorDeviceForTest extends
      ScannerStateAwareSensorDevice
  {
    /**
     * Flag to indicate that a call to onScannerRunningStateChange has happen
     */
    public boolean onScannerRunningStateChangeWasCalled = false;
    
    /**
     * Storage of the isRunningParameter
     */
    public boolean isRunningParameter = false;
    
    /**
     * Constructor
     * @param id the sensor device id
     */
    public ScannerStateAwareSensorDeviceForTest( SensorDeviceIdentifier id )
    {
      super( id );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
     * isDeviceInSystemEnabled(android.content.Context)
     */
    @Override
    public boolean isDeviceInSystemEnabled( Context context )
    {
      return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.ScannerStateAwareSensorDevice
     * #onScannerRunningStateChange(boolean, android.content.Context)
     */
    @Override
    protected void onScannerRunningStateChange( boolean isRunning,
        Context context )
    {
      isRunningParameter = isRunning;
      onScannerRunningStateChangeWasCalled = true;
      TestScannerStateAwareSensorDevice.assertNotNull( "Context has to be initialized", context );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
     * doSignalDeviceNotEnabledInSystem(android.content.Context)
     */
    @Override
    protected void
        doSignalDeviceNotEnabledInSystem( Context applicationContext )
    {}
    
  }
  
  /**
   * Test method for Construction
   */
  public final void testScannerStateAwareSensorDevice()
  {
    ScannerStateAwareSensorDeviceForTest device =
        new ScannerStateAwareSensorDeviceForTest( SensorDeviceIdentifier.Light );
    assertEquals( "Expected identifier set", SensorDeviceIdentifier.Light,
        device.getDeviceIdentifier() );
    assertNotNull( "Expected configuration not null", device.getConfiguration() );
    assertNull( "Expected scanner null", device.getScanner() );
    assertEquals( "Unexpected enabled value",
        device.getConfiguration().isEnabled(),
        device.isDeviceScanningEnabled() );
    assertFalse(
        "Expected no call to scanner running state change handler during construction",
        device.onScannerRunningStateChangeWasCalled );
    assertFalse(
        "Expected isRunningParemeter initialized", device.isRunningParameter );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.ScannerStateAwareSensorDevice#enableDeviceScanning(boolean, android.content.Context)}
   * .
   */
  public final void testEnableDeviceScanning()
  {
    ScannerStateAwareSensorDeviceForTest device =
      new ScannerStateAwareSensorDeviceForTest( SensorDeviceIdentifier.Unknown );   
    device.onScannerRunningStateChangeWasCalled = false;
    device.isRunningParameter = false;
    
    // attach a scanner
    SensorDeviceScanner scanner = new AbstractSensorDeviceScannerForTest();
    scanner.setDevice( device, getContext() );
    scanner.enable( false, getContext() ); 
    
    assertFalse(
        "Expected no call to scanner running state change handler yet",
        device.onScannerRunningStateChangeWasCalled );
    assertFalse(
        "Unxpected isRunningParemeter", device.isRunningParameter );
    assertFalse(
        "Expected Scanner not running", scanner.isEnabled() );
    
    // do test disable for a disabled device
    device.enableDeviceScanning( false, getContext() );
    
    assertFalse(
        "Expected no call to scanner running state change handler",
        device.onScannerRunningStateChangeWasCalled );
    assertFalse(
        "Unxpected isRunningParemeter", device.isRunningParameter );
    assertFalse(
        "Expected Scanner not running", scanner.isEnabled() );
    
    // do test enable for a disabled device
    device.enableDeviceScanning( true, getContext() );
    
    assertTrue(
        "Expected a call to scanner running state change handler",
        device.onScannerRunningStateChangeWasCalled );
    assertTrue(
        "Unxpected isRunningParemeter", device.isRunningParameter );
    assertTrue(
        "Expected Scanner running", scanner.isEnabled() );
    
    // do test enable for an enabled device
    device.onScannerRunningStateChangeWasCalled = false;
    device.enableDeviceScanning( true, getContext() );
    
    assertFalse(
        "Expected a call to scanner running state change handler",
        device.onScannerRunningStateChangeWasCalled );
    assertTrue(
        "Unxpected isRunningParemeter", device.isRunningParameter );
    assertTrue(
        "Expected Scanner running", scanner.isEnabled() );
    
    // do test disable for an enabled device  
    device.enableDeviceScanning( false, getContext() );
    
    assertTrue(
        "Expected a call to scanner running state change handler",
        device.onScannerRunningStateChangeWasCalled );
    assertFalse(
        "Unxpected isRunningParemeter", device.isRunningParameter );
    assertFalse(
        "Expected Scanner not running", scanner.isEnabled() );
  }
  
}
