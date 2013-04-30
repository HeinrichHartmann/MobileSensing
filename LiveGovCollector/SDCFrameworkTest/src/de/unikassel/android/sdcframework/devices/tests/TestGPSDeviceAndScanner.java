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

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.GPSSampleData;
import de.unikassel.android.sdcframework.devices.GPSDevice;
import de.unikassel.android.sdcframework.devices.GPSDeviceScanner;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.tests.SampleEventObserverForTest;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.test.AndroidTestCase;

/**
 * Tests for the GPS device and scanner classes
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestGPSDeviceAndScanner extends AndroidTestCase
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
   * Test method to assure hardware is available in environment
   */
  public void testPreconditions()
  {
    LocationManager locationManager =
        (LocationManager) getContext().getSystemService(
            Context.LOCATION_SERVICE );
    assertTrue( "The test environment does not support GPS",
        locationManager.getProvider( LocationManager.GPS_PROVIDER ) != null );
    assertTrue( "GPS provider is not enabled in test environment",
        locationManager.isProviderEnabled( LocationManager.GPS_PROVIDER ) );
    
  }
  
  /**
   * Test for device and scanner creation
   */
  public void testGPSDeviceAndScannerCreation()
  {
    testPreconditions();
    
    // create device and scanner and link with each other
    GPSDevice device = new GPSDevice( getContext() );
    GPSDeviceScanner scanner = new GPSDeviceScanner();
    device.setScanner( scanner, getContext() );
    device.onCreate( getContext() );
    
    assertFalse( "Expected device scanning disabled initally",
        device.isDeviceScanningEnabled() );
    assertEquals( "Wrong device type", SensorDeviceIdentifier.GPS,
        device.getDeviceIdentifier() );
    assertFalse( "Expected airplane mode requester returning false",
        device.isAirplaneModeOn( getContext() ) );
    assertNotNull( "Expected sample not null", device.getSample() );
    assertTrue( "Expected a sample of type AccelerometerSample",
        device.getSample().getData() instanceof GPSSampleData );
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
    GPSDevice device = new GPSDevice( getContext() );
    GPSDeviceScanner scanner = new GPSDeviceScanner();
    device.setScanner( scanner, getContext() );
    device.onCreate( getContext() );
    
    assertFalse( "Expected device scanning disabled",
        device.isDeviceScanningEnabled() );
    assertFalse( "Expected scanner disabled", scanner.isEnabled() );
    
    device.getConfiguration().setEnabled( true );
    device.enableDeviceScanning( true, getContext() );
    
    assertTrue( "Expected device scanning enabled",
        device.isDeviceScanningEnabled() );
    assertTrue( "Expected scanner enabled", scanner.isEnabled() );
    assertTrue( "Expected location listener registered",
        device.isLocationListenerRegistered() );
    
    device.getConfiguration().setEnabled( false );
    device.enableDeviceScanning( false, getContext() );
    
    assertFalse( "Expected device scanning disabled",
        device.isDeviceScanningEnabled() );
    assertFalse( "Expected scanner disabled", scanner.isEnabled() );
    
    device.onDestroy( getContext() );
    assertFalse( "Expected location listener unregistered",
        device.isLocationListenerRegistered() );
  }
  
  /**
   * Test for valid device sample updates
   */
  public void testSampleUpdate()
  {
    testPreconditions();
    
    GPSDevice device = new GPSDevice( getContext() );
    device.onCreate( getContext() );
    
    // Set up your a location
    
    Location location = new Location( LocationManager.GPS_PROVIDER );
    location.setLatitude( 10.0 );
    location.setLongitude( 20.0 );
    location.setAltitude( 20.0 );
    location.setSpeed( 1.4F );
    
    // test the sample update
    device.doHandleLocationChanged( location );
    GPSSampleData sample = (GPSSampleData) device.getSample().getData();
    
    assertEquals( "Unexpected sample value for latitude",
        location.getLatitude(), sample.getLatitude() );
    assertEquals( "Unexpected sample value for longitude",
        location.getLongitude(), sample.getLongitude() );
    assertEquals( "Unexpected sample value for altitude",
        location.getAltitude(), sample.getAltitude() );
    assertEquals( "Unexpected sample value for latitude", location.getSpeed(),
        sample.getSpeed() );
    
    device.onDestroy( getContext() );
  }
  
  /**
   * Test for device and scanner sample performance
   */
  public void testSampling()
  {
    testPreconditions();
    
    final int scanFrequency = 500;
    final int maxSleepTime = 10000;
    
    // create a sample observer
    final SampleEventObserverForTest observer =
        new SampleEventObserverForTest();
    
    // create and configure a device
    final GPSDevice device = new GPSDevice( getContext() );
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
        GPSDeviceScanner scanner = new GPSDeviceScanner();
        
        // attach scanner to device
        device.setScanner( scanner, getContext() );
        device.onCreate( getContext() );
        
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
      TestUtils.sleep( 100 );
    }
    
    long ts = System.currentTimeMillis();
    while( observer.observedEvents.size() < 1 && 
        ( System.currentTimeMillis() - ts ) < maxSleepTime )
    {
      TestUtils.sleep( 100 );
    }
    
    // disable device scanning
    device.enableDeviceScanning( false, getContext() );
    
    // test for samples taken
    int sampleCount = observer.observedEvents.size();
    
    // REMARK: this test may fail inside of buildings or due to the long
    // duration of an initial scan after reactivation
    assertTrue( "Expected more samples taken", sampleCount >= 1 );
    
    Sample lastSample = null;
    for ( Sample sample : observer.observedEvents )
    {
      assertEquals( "Unexpected device identifier",
          SensorDeviceIdentifier.GPS.toString(), sample.getDeviceIdentifier() );
      assertTrue( "Unexpected sample data type",
          sample.getData() instanceof GPSSampleData );
      
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
