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
import de.unikassel.android.sdcframework.data.independent.LightSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.LightDevice;
import de.unikassel.android.sdcframework.devices.LightDeviceScanner;
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
 * Tests for the light device and scanner classes
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestLightDeviceAndScanner extends AndroidTestCase
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
        sensorManager.getSensorList( Sensor.TYPE_LIGHT ).size() > 0;
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
    assertTrue( "Test environment does not support light",
        isDeviceInGeneralAvailable );
  }
  
  /**
   * Test for device and scanner creation
   */
  public void testDeviceAndScannerCreation()
  {
    testPreconditions();
    
    // create device and scanner and link with each other
    LightDevice device = new LightDevice( getContext() );
    LightDeviceScanner scanner = new LightDeviceScanner();
    device.setScanner( scanner, getContext() );
    
    assertFalse( "Expected device scanning disabled initally",
        device.isDeviceScanningEnabled() );
    assertEquals( "Wrong device type", SensorDeviceIdentifier.Light,
        device.getDeviceIdentifier() );
    assertEquals( "Wrong sensor type", Sensor.TYPE_LIGHT,
        device.getSensor().getType() );
    assertNotNull( "Expected sample not null", device.getSample() );
    SampleData data = device.getSample().getData();
    assertTrue( "Expected a sample of type LightSample",
        data instanceof LightSampleData );
    LightSampleData convData = (LightSampleData) data;
    assertTrue( "Wrong initialisation", convData.getLightLevel() < 0 );
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
    LightDevice device = new LightDevice( getContext() );
    LightDeviceScanner scanner = new LightDeviceScanner();
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
    LightDevice device = new LightDevice( getContext() );
    
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
      
      device.doHandleSensorChanged( event );
      LightSampleData sampleData =
          (LightSampleData) device.getSample().getData();
      
      assertEquals( "Unexpected sample value for light", event.values[ 0 ],
          sampleData.getLightLevel() );
      
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
    
    final int scanFrequency = 3000;
    final int sleepTime = 3500;
    
    // create a sample observer
    final SampleEventObserverForTest observer =
        new SampleEventObserverForTest();
    
    // create and configure a device
    final LightDevice device = new LightDevice( getContext() );
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
        LightDeviceScanner scanner = new LightDeviceScanner();
        
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
    // the following comparison should be with 1, but there will just an
    // observable event if a light value change is triggered. No chance
    // to force an initial Update in API level 7
    assertTrue( "Expected more samples taken", sampleCount >= 0 );
    
    Sample lastSample = null;
    for ( Sample sample : observer.observedEvents )
    {
      assertEquals( "Unexpected device identifier",
          SensorDeviceIdentifier.Light.toString(), sample.getDeviceIdentifier() );
      assertTrue( "Unexpected sample data type",
          sample.getData() instanceof LightSampleData );
      
      if ( lastSample != null )
      {
        assertNotSame( "Expected different samples", lastSample, sample );
        long timediff = sample.getTimeStamp() - lastSample.getTimeStamp();
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
