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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.AccelerometerSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.AccelerometerDevice;
import de.unikassel.android.sdcframework.devices.AccelerometerDeviceScanner;
import de.unikassel.android.sdcframework.devices.SampleFactory;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.tests.SampleEventObserverForTest;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.test.AndroidTestCase;

/**
 * Tests for the accelerometer device and scanner classes
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAccelerometerDeviceAndScanner extends AndroidTestCase
{
  /**
   * Flag for hardware availability
   */
  private boolean isDeviceInGeneralAvailable = false; 
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    SensorManager sensorManager =
      (SensorManager) getContext().getSystemService( Context.SENSOR_SERVICE );
    isDeviceInGeneralAvailable = sensorManager.getSensorList( Sensor.TYPE_ACCELEROMETER ).size() > 0;
   
    SampleFactory.getInstance().getLocationTracker().onCreate(
        getContext() );
    SampleFactory.getInstance().enableLocationTracking( true );
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
   * Test method to assure hardware is available in environment
   */
  public void testPreconditions()
  {
    assertTrue( "Test environment does not support accelerometer", isDeviceInGeneralAvailable );
  }
  
  /**
   * Test for accelerometer device and scanner creation
   */
  public void testAccelerometerDeviceAndScannerCreation()
  {
    testPreconditions();
    
    // create device and scanner and link with each other
    AccelerometerDevice device = new AccelerometerDevice( getContext() );
    AccelerometerDeviceScanner scanner = new AccelerometerDeviceScanner();
    device.setScanner( scanner, getContext() );
    
    assertFalse( "Expected device scanning disabled initally",
        device.isDeviceScanningEnabled() );
    assertEquals( "Wrong device type", SensorDeviceIdentifier.Accelerometer,
        device.getDeviceIdentifier() );
    assertEquals( "Wrong sensor type", Sensor.TYPE_ACCELEROMETER,
        device.getSensor().getType() );
    assertNotNull( "Expected sample not null", device.getSample() );
    SampleData data = device.getSample().getData();
    assertTrue( "Expected a sample of type AccelerometerSample",
        data instanceof AccelerometerSampleData );
    AccelerometerSampleData convData = (AccelerometerSampleData) data;
    assertEquals( "Wrong initialisation", Float.MIN_VALUE,
        convData.getAccelerationX() );
    assertEquals( "Wrong initialisation", Float.MIN_VALUE,
        convData.getAccelerationY() );
    assertEquals( "Wrong initialisation", Float.MIN_VALUE,
        convData.getAccelerationZ() );
    assertEquals( "Expected device associated with scanner", scanner,
        device.getScanner() );
    assertEquals( "Expected scanner associated with device", device,
        scanner.getDevice() );
    assertFalse( "Expected scanner disabled initally", scanner.isEnabled() );
    
    device.onDestroy( getContext() );
  }
  
  /**
   * Test for accelerometer device and scanner enable/disable
   */
  public void testEnableDisableDeviceScanning()
  {
    testPreconditions();
    
    // create device and scanner and link with each other
    AccelerometerDevice device = new AccelerometerDevice( getContext() );
    AccelerometerDeviceScanner scanner = new AccelerometerDeviceScanner();
    device.setScanner( scanner, getContext() );
    
    assertFalse( "Expected device scanning disabled",
        device.isDeviceScanningEnabled() );
    assertFalse( "Expected scanner disabled", scanner.isEnabled() );
    
    device.getConfiguration().setEnabled( true );
    device.enableDeviceScanning( true, getContext() );
    
    assertTrue( "Expected device scanning enabled",
        device.isDeviceScanningEnabled() );
    assertTrue( "Expected scanner enabled", scanner.isEnabled() );
    
    device.getConfiguration().setEnabled( false );
    device.enableDeviceScanning( false, getContext() );
    
    assertFalse( "Expected device scanning disabled",
        device.isDeviceScanningEnabled() );
    assertFalse( "Expected scanner disabled", scanner.isEnabled() );
    
    device.onDestroy( getContext() );
  }
  
  /**
   * Test for valid device sample updates
   */
  public void testSampleUpdate()
  {
    testPreconditions();
    
    // we do test the device here for injected sensor data and created samples
    AccelerometerDevice device = new AccelerometerDevice( getContext() );
    
    try
    {
      // create sensor event by using evil reflection to access the 
      // hidden constructor
      Constructor< SensorEvent > constr =
          SensorEvent.class.getDeclaredConstructor( int.class );
      constr.setAccessible( true );
      
      SensorEvent event = constr.newInstance( new Object[] { 3 } );
      
      // add sensor values
      event.values[ 0 ] = 12.4F;
      event.values[ 1 ] = -7.1976F;
      event.values[ 2 ] = 123F;
      
      device.doHandleSensorChanged( event );
      AccelerometerSampleData sampleData = (AccelerometerSampleData) device.getSample().getData();
      
      assertEquals( "Unexpected sample value for X acceleration", event.values[ 0 ],
          sampleData.getAccelerationX() );
      assertEquals( "Unexpected sample value for Y acceleration", event.values[ 1 ],
          sampleData.getAccelerationY() );
      assertEquals( "Unexpected sample value for Z acceleration", event.values[ 2 ],
          sampleData.getAccelerationZ() );
      
      device.onDestroy( getContext() );
    }
    catch ( InvocationTargetException e )
    {
      e.printStackTrace();
      fail( "Unexpected InvocationTargetException" );
    }
    catch ( NoSuchMethodException e )
    {
      e.printStackTrace();
      fail( "Unexpected NoSuchMethodException" );
    }
    catch ( InstantiationException e )
    {
      e.printStackTrace();
      fail( "Unexpected InstantiationException" );
    }
    catch ( IllegalAccessException e )
    {
      e.printStackTrace();
      fail( "Unexpected IllegalAccessException" );
    }
  }
  
  /**
   * Test for device and scanner sample performance
   */
  public void testSampling()
  {
    testPreconditions();
    
    final int scanFrequency = 600;
    final int sleepTime = 1500;
    
    // create a sample observer
    final SampleEventObserverForTest observer =
        new SampleEventObserverForTest();
    
    // create and configure a device
    final AccelerometerDevice device = new AccelerometerDevice( getContext() );
    device.getConfiguration().setFrequency( scanFrequency );
    device.getConfiguration().setEnabled( true );
    
    // create scanner in looper thread to allow asynchronous event handling
    /**
     * Internal looper test thread
     * 
     * @author Katy Hilgenberg
     * 
     */
    class LooperThread extends LooperThreadForTest
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * de.unikassel.android.sdcframework.devices.tests.LooperThreadForTest#
       * doPrepareTest()
       */
      @Override
      public void doPrepareTest()
      {
        // create the scanner in the looper thread context to implicitly
        // associate handler with the threads looper
        AccelerometerDeviceScanner scanner = new AccelerometerDeviceScanner();
        
        // attach scanner to device
        device.setScanner( scanner, getContext() );
        
        // add the event observer to the scanner
        scanner.registerEventObserver( observer );
      }
    }
    ;
    
    // create a looper thread instance
    LooperThread looperThread = new LooperThread();
    
    // start the looper thread and wait for PREPARATION finished
    looperThread.start();
    while ( !looperThread.hasPreparationDone.get() )
    {
      TestUtils.sleep( scanFrequency / 2 );
    }
    TestUtils.sleep( sleepTime );
    
    // disable device scanning
    device.enableDeviceScanning( false, getContext() );
    
    // test for samples taken
    int sampleCount = observer.observedEvents.size();
    assertTrue( "Expected more samples taken", sampleCount >= sleepTime / scanFrequency );
    
    Sample lastSample = null;
    for ( Sample sample : observer.observedEvents )
    {
      assertEquals( "Unexpected device identifier",
          SensorDeviceIdentifier.Accelerometer.toString(), sample.getDeviceIdentifier() );
      assertTrue( "Unexpected sample data type",
          sample.getData() instanceof AccelerometerSampleData );
      if ( lastSample != null )
      {
        assertNotSame( "Expected different samples", lastSample, sample );
        long timediff = sample.getTimeStamp() - lastSample.getTimeStamp();
        assertNotNull( "Expected geo location not null ", sample.getLocation() );
        // REMARK: due to bad emulator timing this is just a weak test
        assertTrue( "Unexpected sample frequency " + timediff,
            Math.abs( timediff - scanFrequency ) < scanFrequency );
      }
      lastSample = sample;
    }
    
    // stop looper thread
    device.onDestroy( getContext() );
    looperThread.interrupt();
  }
}
