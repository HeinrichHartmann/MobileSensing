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

import java.util.Collection;

import android.content.Context;

/**
 * Interface for a composition of sensor devices.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface SensorDeviceComposite extends SensorDevice
{
  
  /**
   * Getter for the sub devices
   * 
   * @return the devices
   */
  public abstract Collection< SensorDevice > getDevices();
  
  /**
   * To add a device to the composite type
   * 
   * @param device
   *          the device to add
   * @return true if successful, false otherwise
   */
  public abstract boolean addSensorDevice( SensorDevice device );
  
  /**
   * To remove a device from the composite type
   * 
   * @param device
   *          the device to remove
   * @param context
   *          the application context
   */
  public abstract void removeSensorDevice( VisitableDevice device,
      Context context );
  
  /**
   * To get a specific device
   * 
   * @param identifier
   *          the device identifier
   * @return the device with the given identifier or null
   */
  public abstract VisitableDevice getSensorDevice( String identifier );
  
}