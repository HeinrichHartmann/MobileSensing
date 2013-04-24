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
package de.unikassel.android.sdcframework.preferences.facade;

import java.util.Collection;

import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;

/**
 * Interface for a collection of device preferences.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface DevicePreferencesCollection
{
  /**
   * Method to add device preferences to the composition
   * 
   * @param preferences
   *          the preferences to add
   * @return true if successful, false otherwise
   */
  public abstract boolean addPreferences( SensorDevicePreferences preferences );
  
  /**
   * Getter for all device preferences
   * 
   * @return the preferences collection of the composite type
   */
  public abstract Collection< SensorDevicePreferences > getPreferences();
  
  /**
   * Getter for specific device preferences
   * 
   * @param deviceIdentifier
   *          the device identifier
   * @return the preferences for the device with the given identifier
   */
  public abstract SensorDevicePreferences getPreferencesForDevice(
      SensorDeviceIdentifier deviceIdentifier );
  
  /**
   * Does remove all stored device preferences
   */
  public abstract void removeAll();
  
}
