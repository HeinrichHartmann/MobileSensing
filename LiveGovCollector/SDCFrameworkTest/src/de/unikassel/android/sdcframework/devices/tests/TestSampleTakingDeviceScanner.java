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
import de.unikassel.android.sdcframework.data.tests.TestGPSSampleData;
import de.unikassel.android.sdcframework.data.tests.TestSampleCollection;
import de.unikassel.android.sdcframework.devices.SampleTakingDeviceScanner;
import de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.tests.SampleEventObserverForTest;
import android.content.Context;
import android.os.Handler;
import android.test.AndroidTestCase;

/**
 * Tests for the abstract sample taking device scanner base class.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSampleTakingDeviceScanner extends AndroidTestCase
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
  
  /* (non-Javadoc)
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  /**
   * A test implementation of a sensor device implementing the
   * SampleProvidingSensorDevice interface
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class SampleProvidingSensorDeviceForTest
      extends AbstractSensorDeviceForTest
      implements SampleProvidingSensorDevice
  {
    
    /**
     * The current sample
     */
    public Sample currentSample = null;
    
    /**
     * Flag if a new simple sensor device sample with just a time stamp set
     * shall be created in take sample ( if false just currentSample is returned
     * )
     */
    public boolean doCreateSampleWithTimeStamp = false;
    
    /**
     * Constructor
     * 
     * @param deviceId
     *          the device id
     */
    public SampleProvidingSensorDeviceForTest( SensorDeviceIdentifier deviceId )
    {
      super( deviceId );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice
     * #getSample()
     */
    @Override
    public Sample getSample()
    {
      if ( doCreateSampleWithTimeStamp )
      {
        Sample sample = new Sample();
        sample.setTimeStamp( System.currentTimeMillis() );
        return sample;
      }
      return currentSample;
    }

    /* (non-Javadoc)
     * @see de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice#hasSample()
     */
    @Override
    public boolean hasSample()
    {
      return true;
    }
    
  }
  
  /**
   * A test implementation of a scanner extending SampleTakingDeviceScanner
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class SampleTakingDeviceScannerForTest extends
      SampleTakingDeviceScanner
  {
    /**
     * Flag for scanner start was called
     */
    public boolean wasStarted = false;
    
    /**
     * Flag for scanner stop was called
     */
    public boolean wasStopped = false;
    
    /**
     * Method to access the private handler
     * 
     * @return the internal handler
     */
    public Handler getPrivateHandler()
    {
      return super.getHandler();
    }
    
    /**
     * Method to access the private sample task as runnable
     * 
     * @return the internal handler
     */
    public Runnable getPrivateSampleTask()
    {
      return super.getTimerTask();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.SampleTakingDeviceScanner#start
     * (android.content.Context)
     */
    @Override
    public boolean start( Context context )
    {
      wasStarted = true;
      return super.start( context );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.SampleTakingDeviceScanner#stop
     * (android.content.Context)
     */
    @Override
    public boolean stop( Context context )
    {
      wasStopped = true;
      return super.stop( context );
    }
  }
  
  /**
   * Test method for construction.
   */
  public final void testSampleTakingDeviceScanner()
  {
    SampleTakingDeviceScannerForTest scanner =
        new SampleTakingDeviceScannerForTest();
    assertNotNull( "Expected internal handler initialized on first access",
        scanner.getPrivateHandler() );
    assertNotNull( "Expected internal task initialized on first access",
        scanner.getPrivateSampleTask() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.SampleTakingDeviceScanner#takeSample()}
   * .
   */
  public final void testTakeSample()
  {
    // create scanner and device source
    SampleTakingDeviceScannerForTest scanner =
        new SampleTakingDeviceScannerForTest();
    SampleProvidingSensorDeviceForTest device =
        new SampleProvidingSensorDeviceForTest(
            SensorDeviceIdentifier.GPS );
    
    // add an event observer to the scanner
    SampleEventObserverForTest observer = new SampleEventObserverForTest();
    scanner.registerEventObserver( observer );
    
    try
    {
      device.currentSample = null;
      
      // first try for device null
      scanner.takeSample();
      
      scanner.setDevice( device, getContext() );
      
      // try for sample null
      scanner.takeSample();
      
      // test for a valid preset sample
      Sample sample =
          TestSampleCollection.createSample( SensorDeviceIdentifier.GPS,
              TestGPSSampleData.createInitializedGPSSampleData() );
      device.currentSample = sample;
      observer.observedEvents.clear();
      scanner.takeSample();
      
      assertEquals( "Expected one sample taken", 1,
          observer.observedEvents.size() );
      assertSame( "Expected same sample", sample,
          observer.observedEvents.get( 0 ) );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected excpetion" );
    }
    
    scanner.unregisterEventObserver( observer );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.SampleTakingDeviceScanner#start(android.content.Context)}
   * and
   * {@link de.unikassel.android.sdcframework.devices.SampleTakingDeviceScanner#stop(android.content.Context)}
   * .
   */
  public final void testStartAndStop()
  {
    final int scanFrequency = 200;
    final int sleepTime = 800;
    
    // create a device with samples with time stamp
    final SampleProvidingSensorDeviceForTest device =
        new SampleProvidingSensorDeviceForTest(
            SensorDeviceIdentifier.Unknown );
    device.getConfiguration().setFrequency( scanFrequency );
    device.getConfiguration().setEnabled( true );
    device.doCreateSampleWithTimeStamp = true;
    
    // create a sample observer
    final SampleEventObserverForTest observer =
        new SampleEventObserverForTest();
    
    /**
     * Internal looper test thread
     * 
     * @author Katy Hilgenberg
     * 
     */
    class ScannerTestLooperThread extends LooperThreadForTest
    {
      /**
       * Our scanner under test
       */
      public SampleTakingDeviceScannerForTest scanner =
          new SampleTakingDeviceScannerForTest();
      
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
        scanner = new SampleTakingDeviceScannerForTest();
        scanner.wasStarted = false;
        scanner.wasStopped = false;
        
        // add an event observer to the scanner
        scanner.registerEventObserver( observer );
        
        // start scanner
        scanner.setDevice( device, getContext() );
        
        // test if scanner was started as expected
        assertTrue( "Expected scanner running", scanner.isEnabled() );
        assertTrue( "Expected scanner start called", scanner.wasStarted );
        assertFalse( "Expected scanner stop not called", scanner.wasStopped );
      }
      
      /**
       * Scanner stop test
       */
      public void doStopScannerAndTest()
      {
        // stop scanner by setting the associated device to null
        scanner.wasStarted = false;
        scanner.wasStopped = false;
        scanner.setDevice( null, getContext() );
        
        // test if scanner was stopped as expected
        assertFalse( "Expected scanner not running", scanner.isEnabled() );
        assertFalse( "Expected scanner start not called", scanner.wasStarted );
        assertTrue( "Expected scanner stop called", scanner.wasStopped );
      }
    }
    ;
    
    // create a looper thread to test the handler based timing
    ScannerTestLooperThread looperThread = new ScannerTestLooperThread();
    
    // start the looper thread and wait for PREPARATION finished
    looperThread.start();
    while ( !looperThread.hasPreparationDone.get() )
    {
      TestUtils.sleep( scanFrequency / 2 );
    }
    TestUtils.sleep( sleepTime );
    
    looperThread.doStopScannerAndTest();
    
    // test for samples taken
    int sampleCount = observer.observedEvents.size();
    assertTrue( "Expected more samples taken", sampleCount >= sleepTime
        / scanFrequency );
    
    Sample lastSample = null;
    for ( Sample sample : observer.observedEvents )
    {
      if ( lastSample != null )
      {
        long timediff = sample.getTimeStamp() - lastSample.getTimeStamp();
        // REMARK: due to bad emulator timing this is just a weak test
        assertTrue( "Unexpected sample frequency " + timediff,
            Math.abs( timediff - scanFrequency ) < scanFrequency );
      }
      lastSample = sample;
    }
    
    // stop looper thread
    looperThread.interrupt();
  }
  
}
