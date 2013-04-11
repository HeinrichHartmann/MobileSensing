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
package de.unikassel.android.sdcframework.devices.facade;

import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import android.content.Context;

/**
 * Interface for any sensor device type.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface SensorDevice extends VisitableDevice
{
  /**
   * Getter for the sensors device identifier
   * 
   * @return the sensor device identifier
   */
  public abstract SensorDeviceIdentifier getDeviceIdentifier();
  
  /**
   * Getter for device scanning enabled state in the framework
   * 
   * @return true if the device scanning is enabled, false otherwise
   */
  public abstract boolean isDeviceScanningEnabled();
  
  /**
   * Setter for device scanning enabled state
   * 
   * @param enabled
   *          true to enable device scanning, false to disable it
   * @param context
   *          the application context
   * 
   * @return true if successful enabled, false otherwise
   */
  public abstract boolean
      enableDeviceScanning( boolean enabled, Context context );
  
  /**
   * Update method for the device configuration settings
   * 
   * @param configuration
   *          the device configuration
   * @param context
   *          the application context
   */
  public abstract void updateConfiguration(
      SensorDeviceConfiguration configuration, Context context );
  
  /**
   * Setter for the scanner
   * 
   * @param scanner
   *          the scanner to set
   * @param context
   *          the application context
   */
  public abstract void
      setScanner( SensorDeviceScanner scanner, Context context );
  
  /**
   * Getter for the scanner
   * 
   * @return the scanner
   */
  public abstract SensorDeviceScanner getScanner();
  
  /**
   * Getter for the sensor device configuration
   * 
   * @return the sensor device configuration
   */
  public abstract SensorDeviceConfiguration getConfiguration();
  
  /**
   * Test method for the system wide enabled state of the device
   * 
   * @param context
   *          the application context
   * @return true if device is enabled in system, false otherwise
   */
  public abstract boolean isDeviceInSystemEnabled( Context context );
  
  /**
   * Default handler for the fact, the system device state is changing from
   * disabled to enabled. <br/>
   * If the device is enabled in the framework than the device scanner will be
   * started.<br/>
   * A sensor device can call this method to proper handle the device is enabled
   * case.
   * 
   * @param context
   *          the application context
   */
  public abstract void doHandleDeviceEnabledBySystem( Context context );
  
  /**
   * Default handler for the fact, the system device state is changing from
   * enabled to disabled. <br/>
   * If the device is enabled in the framework and we are not in airplane mode,
   * than this handler is signaling the user that the device is needed but
   * disabled in the system. If the device scanner was running, it will be
   * stopped.<br/>
   * A sensor device can call this method to proper handle the device is
   * disabled case.
   * 
   * @param context
   *          the application context
   */
  public abstract void doHandleDeviceDisabledBySystem( Context context );
  
  /**
   * Is called when this device is created
   * 
   * @param context
   *          the application context
   */
  public abstract void onCreate( Context context );
  
  /**
   * Is called when the owner destroys this device
   * 
   * @param context
   *          the application context
   */
  public abstract void onDestroy( Context context );
}
