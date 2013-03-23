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

import java.security.InvalidParameterException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Abstract base class for sensor device which have to react on system state
 * changes signaled by broadcasts. <br/>
 * <br/>
 * In general that are devices like Wifi and Bluetooth, which can become
 * disabled globally in the system while the device state change is broadcasted
 * to the system. <br/>
 * Extending classes have to define the related Intent and to implement the
 * {@linkplain #onConfigurationChanged() handler} for device state changes.
 * 
 * @see WifiDevice
 * @see BluetoothDevice
 * @author Katy Hilgenberg
 * 
 */
public abstract class SystemBroadcastReceivingDevice extends
    AbstractSensorDevice
{
  
  /**
   * The broadcast receiver for device state changes
   */
  protected BroadcastReceiver receiver;
  
  /**
   * Constructor
   * 
   * @param deviceId
   *          the device identifier
   */
  @SuppressWarnings( "unused" )
  private SystemBroadcastReceivingDevice(
      SensorDeviceIdentifier deviceId )
  {
    this( deviceId, null );
  }
  
  /**
   * Constructor
   * 
   * @param deviceId
   *          the device identifier
   * @param applicationContext
   *          the application context
   * @throws InvalidParameterException
   *           is thrown in case of invalid parameters
   */
  public SystemBroadcastReceivingDevice(
      SensorDeviceIdentifier deviceId,
      Context applicationContext )
      throws InvalidParameterException
  {
    super( deviceId );
    if ( applicationContext == null )
      throw new InvalidParameterException( "context is null!" );
    
    registerReceiver( applicationContext );
  }
  
  /**
   * Setter for the receiver
   * 
   * @param receiver
   *          the receiver to set
   */
  private final void setReceiver( BroadcastReceiver receiver )
  {
    this.receiver = receiver;
  }
  
  /**
   * Getter for the broadcast receiver
   * 
   * @return the broadcast receiver
   */
  protected final BroadcastReceiver getReceiver()
  {
    if ( receiver == null )
    {
      setReceiver( new BroadcastReceiver()
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
          try
          {
            onDeviceStateChange( context, intent );
          }
          catch ( Exception e )
          {
            Logger.getInstance().error( this,
                "Exception in onDeviceStateChange" );
          }
        }
      } );
    }
    return receiver;
  }
  
  /**
   * Does register the broadcast receiver
   * 
   * @param context
   *          the application context
   */
  private void registerReceiver( Context context )
  {
    IntentFilter intentFilter = getIntentFilter();
    context.registerReceiver( getReceiver(), intentFilter );
  }
  
  /**
   * Does unregister the broadcast receiver
   * 
   * @param context
   *          the application context
   */
  private void unregisterReceiver( Context context )
  {
    context.unregisterReceiver( getReceiver() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.AbstractSensorDevice#onDestroy
   * (android.content.Context)
   */
  @Override
  public final void onDestroy( Context context )
  {
    unregisterReceiver( context );
    super.onDestroy( context );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * onConfigurationChanged()
   */
  @Override
  protected void onConfigurationChanged()
  {
    // nothing to do on configuration changes, extending classes can overload
    // this method if necessary
  }
  
  /**
   * Handler to react on broadcast intents for device changes
   * 
   * @param context
   *          the context
   * @param intent
   *          the intent
   */
  protected abstract void onDeviceStateChange( Context context, Intent intent );
  
  /**
   * Getter for intent filter used by the broadcast receiver
   * 
   * @return the intent filter used by the broadcast receiver
   */
  protected abstract IntentFilter getIntentFilter();
  
}