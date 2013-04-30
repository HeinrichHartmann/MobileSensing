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
import de.unikassel.android.sdcframework.data.independent.OrientationSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.OrientationDevice;
import de.unikassel.android.sdcframework.devices.OrientationDeviceScanner;
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
 * Tests for the orientation device and scanner classes
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestOrientationDeviceAndScanner extends AndroidTestCase
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
    isDeviceInGeneralAvailable =
        sensorManager.getSensorList( Sensor.TYPE_ORIENTATION ).size() > 0;
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
    assertTrue( "Test environment does not support orientation",
        isDeviceInGeneralAvailable );
  }
  
  /**
   * Test for device and scanner creation
   */
  public void testOrientationDeviceAndScannerCreation()
  {
    testPreconditions();
    
    // create device and scanner and link with each other
    OrientationDevice device = new OrientationDevice( getContext() );
    OrientationDeviceScanner scanner = new OrientationDeviceScanner();
    device.setScanner( scanner, getContext() );
    
    assertFalse( "Expected device scanning disabled initally",
        device.isDeviceScanningEnabled() );
    assertEquals( "Wrong device type", SensorDeviceIdentifier.Orientation,
        device.getDeviceIdentifier() );
    assertEquals( "Wrong sensor type", Sensor.TYPE_ORIENTATION,
        device.getSensor().getType() );
    assertNotNull( "Expected sample not null", device.getSample() );
    SampleData data = device.getSample().getData();
    assertTrue( "Expected a sample of type OrientationSample",
        data instanceof OrientationSampleData );
    OrientationSampleData convData = (OrientationSampleData) data;
    assertEquals( "Wrong initialisation", Float.MIN_VALUE,
        convData.getHeading() );
    assertEquals( "Wrong initialisation", Float.MIN_VALUE,
        convData.getPitch() );
    assertEquals( "Wrong initialisation", Float.MIN_VALUE,
        convData.getRoll() );
    assertEquals( "Expected device associated with scanner", scanner,
        device.getScanner() );
    assertEquals( "Expected scanner associated with device", device,
        scanner.getDevice() );
    assertFalse( "Expected scanner disabled initally", scanner.isEnabled() );
    
    device.onDestroy( getContext() );
  }
  
  /**
   * Test for device and scanner enable/disable
   */
  public void testEnableDisableDeviceScanning()
  {
    testPreconditions();
    
    // create device and scanner and link with each other
    OrientationDevice device = new OrientationDevice( getContext() );
    OrientationDeviceScanner scanner = new OrientationDeviceScanner();
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
    OrientationDevice device = new OrientationDevice( getContext() );
    
    try
    {
      // create sensor event by using evil reflection to access the
      // hidden constructor
      Constructor< SensorEvent > constr =
          SensorEvent.class.getDeclaredConstructor( int.class );
      constr.setAccessible( true );
      
      SensorEvent event = constr.newInstance( new Object[] { 3 } );
      
      // add sensor values
      event.values[ 0 ] = 2.4F;
      event.values[ 1 ] = 1.1976F;
      event.values[ 2 ] = 0.5F;
      
      device.doHandleSensorChanged( event );
      SampleData data = device.getSample().getData();
      
      assertTrue( "Expected a sample data of type OrientationSampleData",
          data instanceof OrientationSampleData );
      OrientationSampleData sampleData = (OrientationSampleData) data;
      assertEquals( "Unexpected sample value for heading",
          event.values[ 0 ],
          sampleData.getHeading() );
      assertEquals( "Unexpected sample value for pitch",
          event.values[ 1 ],
          sampleData.getPitch() );
      assertEquals( "Unexpected sample value for roll",
          event.values[ 2 ],
          sampleData.getRoll() );
      
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
    final OrientationDevice device = new OrientationDevice( getContext() );
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
        OrientationDeviceScanner scanner = new OrientationDeviceScanner();
        
        // attach scanner to device
        device.setScanner( scanner, getContext() );
        
        // add the event observer to the scanner
        scanner.registerEventObserver( observer );
      }
    }
    ;
    
    // create a looper thread instance
    LooperThread looperThread = new LooperThread();
    
    // start the looper thread and wait for preparation finished
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
    assertTrue( "Expected more samples taken", sampleCount >= sleepTime
        / scanFrequency );
    
    Sample lastSample = null;
    for ( Sample sample : observer.observedEvents )
    {
      assertEquals( "Unexpected device identifier",
          SensorDeviceIdentifier.Orientation.toString(),
          sample.getDeviceIdentifier() );
      assertTrue( "Unexpected sample data type",
          sample.getData() instanceof OrientationSampleData );
      
      if ( lastSample != null )
      {
        assertNotSame( "Expected different samples", lastSample, sample );
        long timediff = sample.getTimeStamp() - lastSample.getTimeStamp();
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
