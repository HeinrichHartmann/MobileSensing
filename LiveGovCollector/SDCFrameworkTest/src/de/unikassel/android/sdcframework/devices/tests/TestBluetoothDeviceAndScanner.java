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
import de.unikassel.android.sdcframework.data.independent.BluetoothSampleData;
import de.unikassel.android.sdcframework.devices.BluetoothDevice;
import de.unikassel.android.sdcframework.devices.BluetoothDeviceScanner;
import de.unikassel.android.sdcframework.devices.SampleFactory;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.tests.SampleEventObserverForTest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.test.AndroidTestCase;

/**
 * Tests for the Bluetooth device and scanner classes
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestBluetoothDeviceAndScanner extends AndroidTestCase
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
    if( BluetoothAdapter.getDefaultAdapter() != null )
    {
      isDeviceInGeneralAvailable = BluetoothAdapter.getDefaultAdapter().isEnabled();
    }
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
    assertTrue( "The test environment does not support Bluetooth",
        isDeviceInGeneralAvailable );
    assertTrue( "Bluetooth is not enabled in test environment",
        BluetoothAdapter.getDefaultAdapter().isEnabled() );
  }
  
  /**
   * Test for device and scanner creation
   */
  public void testBluetoothDeviceAndScannerCreation()
  {
    testPreconditions();
    
    // create device and scanner and link with each other
    BluetoothDevice device = new BluetoothDevice( getContext() );
    BluetoothDeviceScanner scanner = new BluetoothDeviceScanner();
    device.setScanner( scanner, getContext() );
    
    assertFalse( "Expected device scanning disabled initally",
        device.isDeviceScanningEnabled() );
    assertEquals( "Wrong device type", SensorDeviceIdentifier.Bluetooth,
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
    
    // create device and scanner and link with each other
    BluetoothDevice device = new BluetoothDevice( getContext() );
    BluetoothDeviceScanner scanner = new BluetoothDeviceScanner();
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
    
    BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
    assertTrue( "Preconditions not fullfilled", defaultAdapter != null
        && defaultAdapter.isEnabled() );
    
    int scanFrequency = 120000;
    
    // create a sample observer
    SampleEventObserverForTest observer = new SampleEventObserverForTest();
    
    final List< android.bluetooth.BluetoothDevice > scanResults =
        new Vector< android.bluetooth.BluetoothDevice >();
    
    // create sample receiver
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
        android.bluetooth.BluetoothDevice bluetoothDevice =
            intent.getParcelableExtra( android.bluetooth.BluetoothDevice.EXTRA_DEVICE );
        scanResults.add( bluetoothDevice );
      }
    };
    
    // create device and scanner and link with each other
    BluetoothDevice device = new BluetoothDevice( getContext() );
    // preset enabled and frequency state
    device.getConfiguration().setFrequency( scanFrequency );
    device.getConfiguration().setEnabled( true );
    BluetoothDeviceScanner scanner = new BluetoothDeviceScanner();
    
    // add the event observer to the scanner
    scanner.registerEventObserver( observer );
    
    // register test receiver for WIFI scan results
    getContext().registerReceiver( receiver,
        new IntentFilter( android.bluetooth.BluetoothDevice.ACTION_FOUND ) );

    // start device scanning and register observer
    device.setScanner( scanner, getContext() );
    TestUtils.sleep( 1000 );
    
    while ( !scanner.isLastScanFinished() )
    {
      TestUtils.sleep( 100 );
    }
    
    // unregister test receiver for scan results
    getContext().unregisterReceiver( receiver );
    // disable device scanning
    device.enableDeviceScanning( false, getContext() );
    
    // test for samples taken
    int sampleCount = observer.observedEvents.size();
    assertEquals( "Expected one sample per scan result taken",
        scanResults.size(), sampleCount );
    
    for ( int i = 0; i < sampleCount; ++i )
    {
      Sample sensorDeviceSample = observer.observedEvents.get( i );
      
      assertEquals( "Unexpected device identifier",
          SensorDeviceIdentifier.Bluetooth.toString(), sensorDeviceSample.getDeviceIdentifier() );
      assertTrue( "Unexpected sample data type",
          sensorDeviceSample.getData() instanceof BluetoothSampleData );
      BluetoothSampleData sampleData =
          (BluetoothSampleData) sensorDeviceSample.getData();
      
      android.bluetooth.BluetoothDevice btDevice = scanResults.get( i );
      
      assertEquals( "Unexpected adress", btDevice.getAddress(),
          sampleData.getAddress() );
      assertEquals( "Unexpected name", btDevice.getName(), sampleData.getName() );
      assertEquals(
          "Unexpected class",
          SampleFactory.getInstance().bluetoothClassToHumanReadableInformation( btDevice.getBluetoothClass() ),
          sampleData.getBluetoothClass() );
    }
    
    device.onDestroy( getContext() );
  }
}
