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
 * Base class for virtual sensor devices which does not refer to a physical
 * sensor in the system.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class VirtualSensorDevice extends AbstractSensorDevice
{
  /**
   * Constructor
   * 
   * @param deviceId
   */
  public VirtualSensorDevice( SensorDeviceIdentifier deviceId )
  {
    super( deviceId );
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
    // device is always enabled
    return true;
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
    // device cannot be disabled in system
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * onConfigurationChanged()
   */
  @Override
  protected final void onConfigurationChanged()
  {
    // nothing to do on configuration changes as for frequency and priority
    // always the actual values will be used
  }
  
}