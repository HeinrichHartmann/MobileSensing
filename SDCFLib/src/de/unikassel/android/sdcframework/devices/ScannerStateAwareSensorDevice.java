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
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;

/**
 * This class is an extension of the abstract sensor device for such devices,
 * which have to be aware of the scanner running state. In most cases these are
 * devices implementing the
 * {@link de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice }
 * interface.
 * 
 * @see AbstractAndroidSensorDevice
 * @see GSMDevice
 * @see GPSDevice
 * @author Katy Hilgenberg
 * 
 */
public abstract class ScannerStateAwareSensorDevice extends
    AbstractSensorDevice
{
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * onConfigurationChanged()
   */
  @Override
  protected void onConfigurationChanged()
  {
    // nothing to do on configuration changes as for frequency and priority
    // always the actual values will be used
  }
  
  /**
   * Constructor
   * 
   * @param deviceId
   *          the device identifier
   */
  public ScannerStateAwareSensorDevice( SensorDeviceIdentifier deviceId )
  {
    super( deviceId );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * enableDeviceScanning(boolean, android.content.Context)
   */
  @Override
  public final boolean enableDeviceScanning( boolean enabled, Context context )
  {
    boolean wasEnabled = getScanner().isEnabled();
    boolean result = super.enableDeviceScanning( enabled, context );
    boolean isEnabled = getScanner().isEnabled();
    
    if ( wasEnabled != isEnabled )
    {
      // depending on scanner running state add listener or remove it
      onScannerRunningStateChange( isEnabled, context );
    }
    return result;
  }
  
  /**
   * Handler to react on scanner state changes. It is called after each state
   * change and signals the new running state
   * 
   * @param isRunning
   *          the current scanner state after state change
   * @param context
   *          the context
   */
  protected abstract void onScannerRunningStateChange( boolean isRunning,
      Context context );
}