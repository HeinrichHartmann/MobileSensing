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

import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner;

/**
 * Interface for the preferences of a single {@linkplain SensorDevice sensor
 * device}. <br/>
 * <br/>
 * 
 * A single sensor device has the following preferences:
 * <ul>
 * <li>a scan frequency which is device dependent and can be configured in
 * milliseconds ( any configured values below the minimal response time of the
 * device will just be ignored by {@linkplain SensorDeviceScanner device
 * scanner} ),</li>
 * <li>a {@linkplain SensorDevicePriorities priority} for the sensor samples and
 * </li>
 * <li>an {@linkplain SensorDevice#isDeviceScanningEnabled() enabled state}
 * indicating if samples will be provided for that device or not.</li>
 * </ul>
 * <br/>
 * <br/>
 * Internal defaults are:
 * <ul>
 * <li>12000 milliseconds frequency,</li>
 * <li>{@linkplain SensorDevicePriorities#Level2 highest priority} level,</li>
 * <li>the device will not be enabled for sample scanning.</li>
 * </ul>
 * <br/>
 * Internal defaults are used if there is no default configuration available in
 * the XML configuration file of the framework.
 * 
 * @see de.unikassel.android.sdcframework.preferences.FrequencyPreference
 * @see de.unikassel.android.sdcframework.preferences.PriorityLevelPreference
 * @see de.unikassel.android.sdcframework.preferences.EnabledPreference
 * @author Katy Hilgenberg
 * 
 */
public interface SensorDevicePreferences extends
    SinglePreference< SensorDeviceConfiguration >
{  
  /**
   * Getter for the device identifier
   * 
   * @return the device identifier
   */
  public abstract SensorDeviceIdentifier getDeviceIdentifier();
  
  /**
   * Getter for the frequency preference
   * 
   * @return the frequency preference
   */
  public abstract SinglePreference< Integer > getFrequencyPreference();
  
  /**
   * Getter for the priority preference
   * 
   * @return the priority preference
   */
  public abstract SinglePreference< SensorDevicePriorities >
      getPriorityPreference();
  
  /**
   * Getter for the enabled flag preference
   * 
   * @return the enabled flag preference
   */
  public abstract SinglePreference< Boolean > getEnabledPreference();
}
