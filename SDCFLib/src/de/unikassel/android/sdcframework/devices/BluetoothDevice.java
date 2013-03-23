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

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of the bluetooth sensor device.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class BluetoothDevice extends
    SystemBroadcastReceivingDevice
{
  /**
   * Constructor
   * 
   * @param applicationContext
   *          the application context
   */
  public BluetoothDevice( Context applicationContext )
  {
    super( SensorDeviceIdentifier.Bluetooth, applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.SystemBroadcastReceivingDevice
   * #getIntentFilter()
   */
  @Override
  protected IntentFilter getIntentFilter()
  {
    IntentFilter intentFilter = new IntentFilter();
    intentFilter.addAction( BluetoothAdapter.ACTION_STATE_CHANGED );
    return intentFilter;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
   * isDeviceInSystemEnabled(android.content.Context)
   */
  @Override
  public boolean isDeviceInSystemEnabled( Context context )
  {
    return ( (BluetoothAdapter) BluetoothAdapter.getDefaultAdapter() ).isEnabled();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * doSignalDeviceNotEnabledInSystem(android.content.Context)
   */
  @Override
  protected void doSignalDeviceNotEnabledInSystem( Context applicationContext )
  {
    String message =
        applicationContext.getText( R.string.msg_Bluetooth_disabled ).toString();
    Logger.getInstance().warning( this, message );
    Intent enableIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
    enableIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
    applicationContext.startActivity( enableIntent );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.SystemBroadcastReceivingDevice
   * #onDeviceStateChange(android.content.Context, android.content.Intent)
   */
  @Override
  protected void onDeviceStateChange( Context context, Intent intent )
  {
    String action = intent.getAction();
    
    if ( BluetoothAdapter.ACTION_STATE_CHANGED.equals( action ) )
    {
      // test for state changes
      int prevState =
          intent.getIntExtra( BluetoothAdapter.EXTRA_PREVIOUS_STATE,
              BluetoothAdapter.STATE_OFF );
      int state =
          intent.getIntExtra( BluetoothAdapter.EXTRA_STATE,
              BluetoothAdapter.STATE_OFF );
      
      // display message if scanner is running and device was turned off
      if ( state == BluetoothAdapter.STATE_TURNING_OFF
          && prevState == BluetoothAdapter.STATE_ON )
      {
        // device is about to turn off, do handle the system state change
        doHandleDeviceDisabledBySystem( context );
      }
      else if ( state == BluetoothAdapter.STATE_ON
          && getConfiguration().isEnabled() )
      {
        // device was turned on in system and device is configured as enabled
        // -> update enable state to turn scanner on if, it was off
        doHandleDeviceEnabledBySystem( context );
      }
    }
  }
  
}
