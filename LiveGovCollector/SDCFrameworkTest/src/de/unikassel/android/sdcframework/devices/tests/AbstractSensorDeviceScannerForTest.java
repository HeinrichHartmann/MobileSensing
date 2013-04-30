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
import de.unikassel.android.sdcframework.devices.AbstractSensorDeviceScanner;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;

/**
 * A test implementation of a scanner, which will never fail to start or stop
 * 
 * @author Katy Hilgenberg
 * 
 */
public class AbstractSensorDeviceScannerForTest extends AbstractSensorDeviceScanner
{
  /**
   * Flag to indicate is scanner was started
   */
  public boolean isAbleToStart = true;
  
  /**
   * Flag to indicate is scanner was started
   */
  public boolean isAbleToStop = true;
  
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
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#
   * start(android.content.Context)
   */
  @Override
  public boolean start( Context context )
  {
    return isAbleToStart;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#
   * stop(android.content.Context)
   */
  @Override
  public boolean stop( Context context )
  {
    return isAbleToStop;
  }
  
}