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

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import android.content.Context;

/**
 * Interface for sensor any device scanner type. A scanner is responsible to
 * receive samples from a sensor device in the configured frequency and can be
 * observed for the provided sensor samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface SensorDeviceScanner extends
    ObservableEventSource< Sample >
{
  /**
   * Does start sampling
   * 
   * @param context
   *          the application context
   * 
   * @return true if successful started, false otherwise
   */
  public abstract boolean start( Context context );
  
  /**
   * Does stop sampling
   * 
   * @param context
   *          the application context
   * 
   * @return true if successful started, false otherwise
   */
  public abstract boolean stop( Context context );
  
  /**
   * Does set the enabled state for the scanner. Will invoke call to start() or
   * stop() depending on current running state.
   * 
   * @param enable
   *          enabled state for the scanner to set
   * @param context
   *          the application context
   * 
   * @return true if successful enabled, false otherwise
   */
  public abstract boolean enable( boolean enable, Context context );
  
  /**
   * Getter for the enabled state
   * 
   * @return true if scanner is running, false otherwise
   */
  public abstract boolean isEnabled();
  
  /**
   * Setter for the sensor device
   * 
   * @param device
   *          the sensor device to set
   * 
   * @param context
   *          the application context
   */
  public abstract void setDevice( SensorDevice device, Context context );
  
  /**
   * Getter for the sensor device
   * 
   * @return the sensor device
   */
  public abstract SensorDevice getDevice();
  
  /**
   * Is called when the owner destroys this scanner
   * 
   * @param context
   *          the application context
   */
  public abstract void onDestroy( Context context );
}
