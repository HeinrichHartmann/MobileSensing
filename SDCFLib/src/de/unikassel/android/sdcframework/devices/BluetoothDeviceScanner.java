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
package de.unikassel.android.sdcframework.devices;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.util.TimeInformation;
import de.unikassel.android.sdcframework.util.TimeProvider;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Implementation of the bluetooth sensor device scanner.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class BluetoothDeviceScanner extends SampleReceivingDeviceScanner
{
  /**
   * The bluetooth adapter
   */
  private final BluetoothAdapter bluetoothAdapter;
  
  /**
   * Constructor
   */
  public BluetoothDeviceScanner()
  {
    this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
    return ( device instanceof de.unikassel.android.sdcframework.devices.BluetoothDevice );
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
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction( BluetoothAdapter.ACTION_DISCOVERY_STARTED );
    intentFilter.addAction( BluetoothAdapter.ACTION_DISCOVERY_FINISHED );
    intentFilter.addAction( BluetoothDevice.ACTION_FOUND );
    return intentFilter;
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
    String action = intent.getAction();
    
    if ( BluetoothDevice.ACTION_FOUND.equals( action ) )
    {
      TimeInformation ti = TimeProvider.getInstance().getAccurateTimeInformation();
      int samplePriority =
            getDevice().getConfiguration().getSamplePriority().ordinal();
      BluetoothDevice bluetoothDevice =
            intent.getParcelableExtra( BluetoothDevice.EXTRA_DEVICE );
      Short rssi = null;
      if ( intent.hasExtra( BluetoothDevice.EXTRA_RSSI ) )
      {
        rssi =
              intent.getShortExtra( BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE );
      }
      SampleFactory sampleFactory = SampleFactory.getInstance();
      Sample sample =
        sampleFactory.createSample( ti, getDevice().getDeviceIdentifier(),
                samplePriority, sampleFactory.createBluetoothSampleData(
                    bluetoothDevice, rssi ) );
      if ( sample != null )
        notify( sample );
    }
    else
    {
      if ( BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals( action ) )
      {
        // signal scan started to worker thread
        setLastScanFinished( false );
      }
      else if ( BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals( action ) )
      {
        // signal scan finished to worker thread
        setLastScanFinished( true );
      }
    }
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
    doStopDeviceScan();
    boolean isScanRunning = bluetoothAdapter.startDiscovery();
    setLastScanFinished( !isScanRunning );
    return isScanRunning;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner#
   * doStopDeviceScan()
   */
  @Override
  public void doStopDeviceScan()
  {
    bluetoothAdapter.cancelDiscovery();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner#
   * onDestroy(android.content.Context)
   */
  @Override
  public void onDestroy( Context context )
  {
    if ( !isLastScanFinished() )
    {
      doStopDeviceScan();
    }
    super.onDestroy( context );
  }
}
