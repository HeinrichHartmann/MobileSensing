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

import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.AbstractAndroidSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.test.AndroidTestCase;

/**
 * Tests for the abstract base class for any android sensor api device
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAbstractAndroidSensorDevice extends AndroidTestCase
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
   * A test implementation of the abstract sensor device to wrap Android sensor
   * API devices
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class AndroidSensorDeviceForTest extends AbstractAndroidSensorDevice
  {
    /**
     * Flag to signal if the doHandleSensorChanged method was called
     */
    public boolean doHandleSensorChangedWasCalled = false;
    
    /**
     * Constructor
     * 
     * @param androidSensorType
     *          the Android sensor type identifier
     * @param context
     *          the context
     */
    public AndroidSensorDeviceForTest( int androidSensorType, Context context )
    {
      super( SensorDeviceIdentifier.Unknown, androidSensorType, context, SensorManager.SENSOR_DELAY_GAME );
    }
    
    /**
     * Test method to simulate scanner running state change without having a
     * scanner attached
     * 
     * @param isRunning
     *          the current scanner state after state change
     * @param context
     *          the context
     */
    public void doTriggerScannerRunningStateChanged( boolean isRunning,
        Context context )
    {
      onScannerRunningStateChange( isRunning, context );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.AbstractAndroidSensorDevice
     * #doHandleSensorChanged(android.hardware.SensorEvent)
     */
    @Override
    public void doHandleSensorChanged( SensorEvent event )
    {
      doHandleSensorChangedWasCalled = true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.AbstractAndroidSensorDevice
     * #doHandleSensorAccuracyChanged(android.hardware.Sensor, int)
     */
    @Override
    public void doHandleSensorAccuracyChanged( Sensor sensor, int accuracy )
    {
      // will not be tested
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice
     * #hasSample()
     */
    @Override
    public boolean hasSample()
    {
      return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.AbstractAndroidSensorDevice
     * #getCurrentSampleData()
     */
    @Override
    protected SampleData getCurrentSampleData()
    {
      // can only be tested together with a scanner
      // check the class TestSampleTakingDeviceScanner
      return null;
    }
    
  }
  
  /**
   * Test method to assure hardware is available in environment
   */
  public void testPreconditions()
  {
    SensorManager sensorManager =
        (SensorManager) getContext().getSystemService( Context.SENSOR_SERVICE );
    assertTrue( "Testplattform does not support any type of sensor",
        getAnAvailableSensor( sensorManager ) != null );
  }
  
  /**
   * Test method for construction
   */
  public final void testAbstractAndroidSensorDevice()
  {
    SensorManager sensorManager =
        (SensorManager) getContext().getSystemService( Context.SENSOR_SERVICE );
    Sensor sensor = getAnAvailableSensor( sensorManager );
    
    // test for valid type
    try
    {
      AndroidSensorDeviceForTest device =
          new AndroidSensorDeviceForTest( sensor.getType(), getContext() );
      assertNotNull( "Expected sensor initialized", device.getSensor() );
      assertNotNull( "Expected listener initialized", device.getListener() );
      TestUtils.sleep( 200 );
      assertFalse( "Expected no call to the sensor change handler",
          device.doHandleSensorChangedWasCalled );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected excpetion during construction" );
    }
    
    // tests for invalid types
    try
    {
      new AndroidSensorDeviceForTest( Integer.MIN_VALUE, getContext() );
      fail( "Expected an excpetion during construction with invalid sensor type" );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    try
    {
      new AndroidSensorDeviceForTest( Sensor.TYPE_ALL, getContext() );
      fail( "Expected an excpetion during construction with invalid sensor type" );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
  }
  
  /**
   * Test method for the sensor listening stuff
   */
  public final void testSensorListenerEvents()
  {
    SensorManager sensorManager =
        (SensorManager) getContext().getSystemService( Context.SENSOR_SERVICE );
    Sensor sensor = getAnAvailableSensor( sensorManager );
    
    AndroidSensorDeviceForTest device =
        new AndroidSensorDeviceForTest( sensor.getType(), getContext() );
    
    assertFalse( "Expected no call to the sensor change handler",
        device.doHandleSensorChangedWasCalled );
    
    // simulate scanner switch to running
    int sleepTime = 1000;
    device.doTriggerScannerRunningStateChanged( true, getContext() );
    TestUtils.sleep( sleepTime );
    assertTrue( "Expected a call to the sensor change handler",
        device.doHandleSensorChangedWasCalled );
    
    // simulate scanner switch to not running
    device.doTriggerScannerRunningStateChanged( false, getContext() );
    TestUtils.sleep( 100 );
    device.doHandleSensorChangedWasCalled = false;
    TestUtils.sleep( sleepTime );
    assertFalse( "Expected no more calls to the sensor change handler",
        device.doHandleSensorChangedWasCalled );
  }
  
  /**
   * Does try to get an available sensor device
   * 
   * @param sensorManager
   *          the sensor manager
   * @return a sensor device if available, null otherwise
   */
  private Sensor getAnAvailableSensor( SensorManager sensorManager )
  {
    int[] types =
        new int[] { Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE,
            Sensor.TYPE_LIGHT, Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_ORIENTATION, Sensor.TYPE_PRESSURE,
            Sensor.TYPE_PROXIMITY, Sensor.TYPE_TEMPERATURE };
    for ( int type : types )
    {
      Sensor sensor = sensorManager.getDefaultSensor( type );
      if ( sensor != null )
        return sensor;
    }
    return null;
  }
}
