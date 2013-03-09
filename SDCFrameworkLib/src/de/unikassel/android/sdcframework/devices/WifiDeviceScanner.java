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

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.util.TimeInformation;
import de.unikassel.android.sdcframework.util.TimeProvider;

/**
 * Implementation of the wifi sensor device scanner.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class WifiDeviceScanner extends SampleReceivingDeviceScanner
{
  /**
   * The Wifi manager
   */
  private final WifiManager wifiManager;
  
  /**
   * Constructor
   */
  @SuppressWarnings( "unused" )
  private WifiDeviceScanner()
  {
    this( null );
  }
  
  /**
   * Constructor
   * 
   * @param context
   *          the application context
   */
  public WifiDeviceScanner( Context context )
  {
    super();
    this.wifiManager =
        (WifiManager) context.getSystemService( Context.WIFI_SERVICE );
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
    return ( device instanceof WifiDevice );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner#
   * getIntentFilter()
   */
  @Override
  protected final IntentFilter getIntentFilter()
  {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION );
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
  public final void doHandleScanResults( Intent intent )
  {
    String action = intent.getAction();
    
    if ( WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals( action ) )
    {
      TimeInformation ti = TimeProvider.getInstance().getAccurateTimeInformation();
      String currentBSSID = getCurrentConnectionBSSID();
      
      // if we are out of time sync -> throw samples away
      // get and handle scan results
      int samplePriority =
            getDevice().getConfiguration().getSamplePriority().ordinal();
      
      List< ScanResult > scanResults = wifiManager.getScanResults();
      
      // signal scan done to our worker thread
      setLastScanFinished( true );
      
      // create samples and notify observers
      for ( ScanResult scanResult : scanResults )
      {
        boolean isConnected = scanResult.BSSID.equals( currentBSSID );
        SampleFactory sampleFactory = SampleFactory.getInstance();
        Sample sample =
          sampleFactory.createSample( ti, getDevice().getDeviceIdentifier(),
                  samplePriority, sampleFactory.createWifiSampleData(
                      scanResult, isConnected ) );
        if ( sample != null )
          notify( sample );
      }
    }
  }
  
  /**
   * Getter for the current connection BSSID
   * 
   * @return the current connection BSSID, or null if not connected
   */
  private String getCurrentConnectionBSSID()
  {
    WifiInfo connectionInfo = wifiManager.getConnectionInfo();
    if ( connectionInfo != null )
    {
      return connectionInfo.getBSSID();
    }
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.SampleReceivingDeviceScanner#
   * doStartDeviceScan()
   */
  @Override
  public final boolean doStartDeviceScan()
  {
    return wifiManager.startScan();
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
  {}
}
