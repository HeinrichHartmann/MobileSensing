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

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.widget.Toast;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of the wifi sensor device.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class WifiDevice extends SystemBroadcastReceivingDevice
{
  /**
   * Constructor
   * 
   * @param applicationContext
   *          the application context
   */
  public WifiDevice( Context applicationContext )
  {
    super( SensorDeviceIdentifier.Wifi, applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.SystemBroadcastReceivingDevice#
   * getIntentFilter()
   */
  @Override
  protected final IntentFilter getIntentFilter()
  {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction( WifiManager.WIFI_STATE_CHANGED_ACTION );
    return intentFilter;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
   * isDeviceInSystemEnabled(android.content.Context)
   */
  @Override
  public final boolean isDeviceInSystemEnabled( Context context )
  {
    return ( (WifiManager) context.getSystemService( Context.WIFI_SERVICE ) ).isWifiEnabled();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * doSignalDeviceNotEnabledInSystem(android.content.Context)
   */
  @Override
  protected final void doSignalDeviceNotEnabledInSystem(
      Context applicationContext )
  {
    String appName =
        applicationContext.getText( R.string.sdc_service_name ).toString();
    String wifiMessage =
        applicationContext.getText( R.string.msg_wifi_disabled ).toString();
    // for the moment we do just ask the user to enable wifi
    Toast.makeText( applicationContext, appName + ": " + wifiMessage,
        Toast.LENGTH_LONG ).show();
    Logger.getInstance().warning( this, wifiMessage );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.SystemBroadcastReceivingDevice
   * #onDeviceStateChange(android.content.Context, android.content.Intent)
   */
  @Override
  protected final void onDeviceStateChange( Context context, Intent intent )
  {
    String action = intent.getAction();
    
    if ( WifiManager.WIFI_STATE_CHANGED_ACTION.equals( action ) )
    {
      // test for state changes
      int prevState =
          intent.getIntExtra( WifiManager.EXTRA_PREVIOUS_WIFI_STATE,
              WifiManager.WIFI_STATE_UNKNOWN );
      int state =
          intent.getIntExtra( WifiManager.EXTRA_WIFI_STATE,
              WifiManager.WIFI_STATE_UNKNOWN );
      
      // display message if scanner is running and wifi was turned off
      if ( state == WifiManager.WIFI_STATE_DISABLING
          && prevState == WifiManager.WIFI_STATE_ENABLED )
      {
        doHandleDeviceDisabledBySystem( context );
      }
      else if ( state == WifiManager.WIFI_STATE_ENABLED
          && getConfiguration().isEnabled() )
      {
        // wifi was enabled and device is configured as enabled
        // -> update enable state to turn scanner on if it was off
        doHandleDeviceEnabledBySystem( context );
      }
    }
  }
}
