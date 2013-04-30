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

import android.content.Context;
import de.unikassel.android.sdcframework.devices.AbstractSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;

/**
 * A test implementation extending abstract sensor device
 * 
 * @author Katy Hilgenberg
 * 
 */
class AbstractSensorDeviceForTest extends AbstractSensorDevice
{
  /**
   * Flag to indicate that a call to onConfigurationChanged has happen
   */
  public boolean onConfigurationChangedWasCalled = false;
  
  /**
   * Flag to indicate that a call to onConfigurationChanged has happen
   */
  public boolean doSignalDeviceNotEnabledInSystemWasCalled = false;
  
  /**
   * Flag for device enabled in system
   */
  public boolean isDeviceEnabledInSystem = true;
  
  /**
   * Constructor
   * 
   * @param deviceId
   *          the device identifier
   */
  public AbstractSensorDeviceForTest( SensorDeviceIdentifier deviceId )
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
  public boolean isDeviceInSystemEnabled( Context context )
  {
    return isDeviceEnabledInSystem;
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
    onConfigurationChangedWasCalled = true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * doSignalDeviceNotEnabledInSystem(android.content.Context)
   */
  @Override
  protected void
      doSignalDeviceNotEnabledInSystem( Context applicationContext )
  {
    doSignalDeviceNotEnabledInSystemWasCalled = true;
  }
  
}