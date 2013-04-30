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
import de.unikassel.android.sdcframework.data.independent.GSMSampleData;
import de.unikassel.android.sdcframework.devices.GSMDevice;
import de.unikassel.android.sdcframework.devices.GSMDeviceScanner;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.tests.SampleEventObserverForTest;
import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.test.AndroidTestCase;

/**
 * @author Katy Hilgenberg
 * 
 */
public class TestGSMDeviceAndScanner extends AndroidTestCase
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
   * @see android.test.AndroidTestCase#setUp()
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
    TelephonyManager telephonyManager =
        (TelephonyManager) getContext().getSystemService(
            Context.TELEPHONY_SERVICE );
    assertTrue(
        "The test environment does not support GSM",
        telephonyManager != null
            && telephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM );
    CellLocation cellLocation = telephonyManager.getCellLocation();
    assertTrue( "GSM is not available, maybe sim card is missing", 
        cellLocation  != null && ((GsmCellLocation) cellLocation).getCid() > 0 );
  }
  
  /**
   * Test for device and scanner creation
   */
  public void testGSMDeviceAndScannerCreation()
  {
    testPreconditions();
    
    // create device and scanner and link with each other
    GSMDevice device = new GSMDevice( getContext() );
    GSMDeviceScanner scanner = new GSMDeviceScanner();
    device.setScanner( scanner, getContext() );
    
    assertFalse( "Expected device scanning disabled initally",
        device.isDeviceScanningEnabled() );
    assertEquals( "Wrong device type", SensorDeviceIdentifier.GSM,
        device.getDeviceIdentifier() );
    assertFalse( "Expected airplane mode requester returning false",
        device.isAirplaneModeOn( getContext() ) );
    assertNotNull( "Expected sample not null", device.getSample() );
    assertTrue( "Expected a sample of type AccelerometerSample",
        device.getSample().getData() instanceof GSMSampleData );
    assertEquals( "Expected last service state initialized",
        ServiceState.STATE_IN_SERVICE,
        device.getLastServiceState() );
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
    GSMDevice device = new GSMDevice( getContext() );
    GSMDeviceScanner scanner = new GSMDeviceScanner();
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
    
    GSMDevice device = new GSMDevice( getContext() );
    
    String operatorName = "OP";
    device.updateOperatorName( operatorName );
    GSMSampleData sampleData = (GSMSampleData) device.getSample().getData();
    assertEquals( "Unexpected operator name in sample", operatorName,
        sampleData.getOperator() );
    
    int cellId = 4711;
    int locationAreaCode = 1337;
    device.updateCellLocation( cellId, locationAreaCode );
    sampleData = (GSMSampleData) device.getSample().getData();
    assertEquals( "Unexpected cell id in sample", cellId,
        sampleData.getCellId() );
    assertEquals( "Unexpected location area code in sample", locationAreaCode,
        sampleData.getLocationAreaCode() );
    
    int signalStrength = 13;
    device.updateSignalStrength( signalStrength );
    sampleData = (GSMSampleData) device.getSample().getData();
    assertEquals( "Unexpected signal strength in sample", signalStrength,
        sampleData.getSignalStrength() );
    
    device.onDestroy( getContext() );
  }
  
  /**
   * Test for device and scanner sample performance
   */
  public void testSampling()
  {
    testPreconditions();
    
    final int scanFrequency = 200;
    final int sleepTime = 800;
    
    // create a sample observer
    final SampleEventObserverForTest observer =
        new SampleEventObserverForTest();
    
    // create and configure a device
    final GSMDevice device = new GSMDevice( getContext() );
    device.getConfiguration().setFrequency( scanFrequency );
    device.getConfiguration().setEnabled( true );
    device.onCreate( getContext() );
    
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
        GSMDeviceScanner scanner = new GSMDeviceScanner();
        
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
    TestUtils.sleep( sleepTime << 2 );
    
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
          SensorDeviceIdentifier.GSM.toString(), sample.getDeviceIdentifier() );
      assertTrue( "Unexpected sample data type",
          sample.getData() instanceof GSMSampleData );
      
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
