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

import java.util.List;
import java.util.Vector;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.WifiSampleData;
import de.unikassel.android.sdcframework.devices.WifiDevice;
import de.unikassel.android.sdcframework.devices.WifiDeviceScanner;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.tests.SampleEventObserverForTest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.test.AndroidTestCase;

/**
 * Tests for the Wifi device and scanner classes
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestWifiDeviceAndScanner extends AndroidTestCase
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
    WifiManager wifiManager =
        (WifiManager) getContext().getSystemService( Context.WIFI_SERVICE );
    assertTrue( "The test environment does not support Wifi",
        wifiManager != null );
    assertTrue( "Wifi is not enabled in test environment",
        wifiManager.isWifiEnabled() );
  }
  
  /**
   * Test for device and scanner creation
   */
  public void testWifiDeviceAndScannerCreation()
  {
    testPreconditions();
    
    WifiDevice device = new WifiDevice( getContext() );
    WifiDeviceScanner scanner = new WifiDeviceScanner( getContext() );
    device.setScanner( scanner, getContext() );
    
    assertFalse( "Expected device scanning disabled initally",
        device.isDeviceScanningEnabled() );
    assertEquals( "Wrong device type", SensorDeviceIdentifier.Wifi,
        device.getDeviceIdentifier() );
    assertFalse( "Expected airplane mode requester returning false",
        device.isAirplaneModeOn( getContext() ) );
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
    
    WifiDevice device = new WifiDevice( getContext() );
    WifiDeviceScanner scanner = new WifiDeviceScanner( getContext() );
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
   * Test for device and scanner sample performance
   */
  public void testSampling()
  {
    testPreconditions();
    
    int scanFrequency = 1000;
    int sleepTime = 2900;
    
    // create a sample observer
    SampleEventObserverForTest observer = new SampleEventObserverForTest();
    
    final List< ScanResult > scanResults = new Vector< ScanResult >();
    
    // create wifi sample receiver
    BroadcastReceiver receiver = new BroadcastReceiver()
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * android.content.BroadcastReceiver#onReceive(android.content.Context,
       * android.content.Intent)
       */
      @Override
      public void onReceive( Context context, Intent intent )
      {
        // save scan results for test
        WifiManager wifiManager =
            (WifiManager) getContext().getSystemService( Context.WIFI_SERVICE );
        scanResults.addAll( wifiManager.getScanResults() );
      }
    };
    
    // create device and scanner and link with each other
    WifiDevice device = new WifiDevice( getContext() );
    // preset enabled and frequency state
    device.getConfiguration().setFrequency( scanFrequency );
    device.getConfiguration().setEnabled( true );
    WifiDeviceScanner scanner = new WifiDeviceScanner( getContext() );
    
    // start device scanning and register observer
    device.setScanner( scanner, getContext() );
    // add the event observer to the scanner
    scanner.registerEventObserver( observer );
    
    // register test receiver for scan results
    getContext().registerReceiver( receiver,
        new IntentFilter( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION ) );
    
    // TestUtils.sleep for a while
    TestUtils.sleep( sleepTime );
    
    // unregister test receiver for scan results
    getContext().unregisterReceiver( receiver );
    // disable device scanning
    device.enableDeviceScanning( false, getContext() );
    
    // test for samples taken
    int sampleCount = observer.observedEvents.size();
    assertTrue( "Expected more samplestaken", sampleCount >= scanResults.size() );
    
    for ( int i = 0; i < scanResults.size(); ++i )
    {
      Sample sensorDeviceSample = observer.observedEvents.get( i );
      assertEquals( "Unexpected device identifier",
          SensorDeviceIdentifier.Wifi.toString(),
          sensorDeviceSample.getDeviceIdentifier() );
      assertTrue( "Unexpected sample data type",
          sensorDeviceSample.getData() instanceof WifiSampleData );
      WifiSampleData sampleData = (WifiSampleData) sensorDeviceSample.getData();
      ScanResult scanResult = scanResults.get( i );
      assertEquals( "Unexpected BSSIS", scanResult.BSSID, sampleData.getBSSID() );
      assertEquals( "Unexpected capabilities", scanResult.capabilities,
          sampleData.getCapabilities() );
      assertEquals( "Unexpected frequency", scanResult.frequency,
          sampleData.getFrequency() );
      assertEquals( "Unexpected level", scanResult.level, sampleData.getLevel() );
      assertEquals( "Unexpected SSID", scanResult.SSID, sampleData.getSSID() );
    }
    
    device.onDestroy( getContext() );
  }
  
}
