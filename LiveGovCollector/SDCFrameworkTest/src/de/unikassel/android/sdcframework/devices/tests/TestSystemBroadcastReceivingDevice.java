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

import java.security.InvalidParameterException;

import de.unikassel.android.sdcframework.devices.SystemBroadcastReceivingDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.AndroidTestCase;

/**
 * Tests for the abstract system broadcast receiving device.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSystemBroadcastReceivingDevice extends AndroidTestCase
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
   * A self defined intent action for test purpose
   */
  public final static String ACTION = SystemBroadcastReceivingDeviceForTest.class.getName();
  
  /**
   * A test implementation extending system broadcast receiving device
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class SystemBroadcastReceivingDeviceForTest extends
      SystemBroadcastReceivingDevice
  {  
    
    /**
     * The last received intent from the device state change handler
     */
    public Intent lastIntent = null;

    /**
     * Constructor
     * 
     * @param applicationContext
     *          the application context
     * @throws InvalidParameterException
     *           thrown by superclass cosntructor in case of invalid parameters
     */
    public SystemBroadcastReceivingDeviceForTest( Context applicationContext )
        throws InvalidParameterException
    {
      super( SensorDeviceIdentifier.Unknown, applicationContext );
    }
    
    /**
     * Getter for the internal broadcast receiver for test purpose
     * 
     * @return the internal broadcast receiver
     */
    public BroadcastReceiver getBroadcastReceiver()
    {
      return receiver;
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
      // functionality is already tested in the test for AbstractSensorDevice
      return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.SystemBroadcastReceivingDevice
     * #onDeviceStateChange(android.content.Context, android.content.Intent)
     */
    @Override
    protected void onDeviceStateChange( Context context, Intent intent )
    {
      lastIntent = intent;    
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.SystemBroadcastReceivingDevice
     * #getIntentFilter()
     */
    @Override
    protected IntentFilter getIntentFilter()
    {
      return new IntentFilter( ACTION );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
     * doSignalDeviceNotEnabledInSystem(android.content.Context)
     */
    @Override
    protected void doSignalDeviceNotEnabledInSystem( Context applicationContext )
    {
      // functionality is already tested in the test for AbstractSensorDevice
    }
    
  }
  
  /**
   * Test method for construction and for broadcast receiving
   */
  public final void testSystemBroadcastReceivingDevice()
  {
    SystemBroadcastReceivingDeviceForTest device =
        new SystemBroadcastReceivingDeviceForTest( getContext() );
    assertNotNull( "Expected internal broadcast receiver initialized",
        device.getBroadcastReceiver() );
    
    assertNull( "Expected invalid intent reference", device.lastIntent );
    
    // test broadcast receiving
    Intent intent = new Intent();
    intent.setAction( ACTION );
    getContext().sendBroadcast( intent );
    TestUtils.sleep( 500 );

    assertNotNull( "Expected intent received", device.lastIntent );   
  }
  
}
