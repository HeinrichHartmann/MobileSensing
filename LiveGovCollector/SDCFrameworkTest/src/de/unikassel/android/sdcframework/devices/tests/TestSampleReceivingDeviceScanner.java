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

import de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.LogEvent;
import de.unikassel.android.sdcframework.util.TimeProvider;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import de.unikassel.android.sdcframework.util.facade.WorkerThread;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.AndroidTestCase;

/**
 * Tests for the abstract sample receiving device scanner class.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSampleReceivingDeviceScanner extends AndroidTestCase
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
   * A test implementation of a scanner extending SampleReceivingDeviceScanner.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class SampleReceivingDeviceScannerForTest extends
      SampleReceivingDeviceScanner
  {
    /**
     * The intent filter to use
     */
    public final IntentFilter filter = new IntentFilter();
    
    /**
     * The flag for a call to the start device scan method
     */
    public boolean doStartDeviceScanWasCalled = false;
    
    /**
     * The flag for a call to the stop device scan method
     */
    public boolean doStopDeviceScanWasCalled = false;

    /**
     * The flag for a call to the device scan result handler
     */
    public boolean doHandleScanResultsWasCalled = false;
    
    /**
     * The last received intent from the scan result handler
     */
    public Intent lastIntent;
    
    /**
     * Constructor
     */
    public SampleReceivingDeviceScannerForTest()
    {
      super();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see de.unikassel.android.sdcframework.devices.AbstractSensorDeviceScanner#
     * isCompatibleDevice
     * (de.unikassel.android.sdcframework.devices.facade.SensorDevice)
     */
    @Override
    protected boolean isCompatibleDevice( SensorDevice device )
    {
      return true;
    }
    
    /**
     * Getter for the internal worker thread for test purpose
     * 
     * @return the internal worker thread
     */
    public WorkerThread getWorkerThread()
    {
      return worker;
    }
    
    /**
     * getter for the internal broadcast receiver for test purpose
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
     * @see
     * de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner#
     * getIntentFilter()
     */
    @Override
    protected IntentFilter getIntentFilter()
    {
      return filter;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner#
     * doHandleScanResults(android.content.Intent)
     */
    @Override
    public void doHandleScanResults( Intent intent )
    {
      doHandleScanResultsWasCalled = true;  
      lastIntent = intent;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner#
     * doStartDeviceScan()
     */
    @Override
    public boolean doStartDeviceScan()
    {
      doStartDeviceScanWasCalled = true;
      return true;
    }

    /* (non-Javadoc)
     * @see de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner#doStopDeviceScan()
     */
    @Override
    public void doStopDeviceScan()
    {
      doStopDeviceScanWasCalled = true;
    }
    
  }
  
  /**
   * Test method for construction and destruction.
   */
  public final void testSampleReceivingDeviceScanner()
  {
    SampleReceivingDeviceScannerForTest scanner =
        new SampleReceivingDeviceScannerForTest();
    
    // test initialization
    assertFalse( "Expected last scan finished initialized with false",
        scanner.isLastScanFinished() );
    WorkerThread workerThread = scanner.getWorkerThread();
    assertNotNull( "Expected internal worker thread not null",
        workerThread );
    assertFalse( "Expected internal worker thread not working",
        workerThread.isWorking() );
    assertFalse( "Expected internal worker thread not terminated yet",
        workerThread.hasTerminated() );
    assertNotNull( "Expected internal broadcast reveiver not null",
        scanner.getBroadcastReceiver() );
    assertFalse( "Expected no call to doStartDeviceScan",
        scanner.doStartDeviceScanWasCalled );
    assertFalse( "Expected no call to doStopDeviceScan",
        scanner.doStopDeviceScanWasCalled );
    assertFalse( "Expected no call to doHandleScanResults",
        scanner.doHandleScanResultsWasCalled );
    
    // test destroy
    scanner.onDestroy( getContext() );
    assertTrue( "Expected internal worker thread terminated yet",
        workerThread.hasTerminated() );
    
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner#start(android.content.Context)}
   * and
   * {@link de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner#stop(android.content.Context)}
   * .
   */
  public final void testStartAndStop()
  {
    SampleReceivingDeviceScannerForTest scanner =
        new SampleReceivingDeviceScannerForTest();
    WorkerThread workerThread = scanner.getWorkerThread();
    
    assertFalse( "Expected internal worker thread not working",
        workerThread.isWorking() );
    assertFalse( "Expected no call to doStartDeviceScan",
        scanner.doStartDeviceScanWasCalled );
    assertFalse( "Expected no call to doStopDeviceScan",
        scanner.doStopDeviceScanWasCalled );
    assertFalse( "Expected no call to doHandleScanResults",
        scanner.doHandleScanResultsWasCalled );
    
    // will will use our internal LogEvent as intent for the test
    scanner.filter.addAction( LogEvent.ACTION );
    
    // test scanner start
    scanner.enable( true, getContext() );
    TestUtils.sleep( 500 );
    assertTrue( "Expected internal worker thread working",
        workerThread.isWorking() );
    assertTrue( "Expected a call to doStartDeviceScan",
        scanner.doStartDeviceScanWasCalled );
    assertFalse( "Expected no call to doStopDeviceScan",
        scanner.doStopDeviceScanWasCalled );
    assertFalse( "Expected no call to doHandleScanResults",
        scanner.doHandleScanResultsWasCalled );
    assertNull( "Expected invalid intent reference", scanner.lastIntent );
    
    // test broadcast receiving
    Intent intent = new LogEvent( "test", LogLevel.DEBUG, TimeProvider.getInstance().getTimeStamp() ).getIntent();
    getContext().sendBroadcast( intent );
    TestUtils.sleep( 500 );

    assertTrue( "Expected a call to doHandleScanResults",
        scanner.doHandleScanResultsWasCalled );
    assertNotNull( "Expected intent received", scanner.lastIntent );   
    
    // test scanner stop
    scanner.enable( false, getContext() );
    assertFalse( "Expected internal worker thread not working",
        workerThread.isWorking() );    
    assertTrue( "Expected a call to doStopDeviceScan",
        scanner.doStopDeviceScanWasCalled );
  }
}
