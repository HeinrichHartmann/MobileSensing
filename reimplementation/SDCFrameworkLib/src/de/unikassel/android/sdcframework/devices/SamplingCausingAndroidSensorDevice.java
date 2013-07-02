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

import android.content.Context;
import android.hardware.SensorEvent;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;

/**
 * Base class for Android Sensor based devices, which do cause sampling in case
 * of sensor data change events. This class is intended to work together with a
 * {@link PassiveSampleTakingDeviceScanner}.
 * 
 * @see TemperatureDevice
 * @see LightDevice
 * @see ProximityDevice
 * @author Katy Hilgenberg
 * 
 */
public abstract class SamplingCausingAndroidSensorDevice extends
    AbstractAndroidSensorDevice
{
  /**
   * Constructor
   * 
   * @param deviceId
   *          the device identifier
   * @param androidSensorType
   *          the Android sensor type
   * @param context
   *          the application context
   * @param sensorDelay
   *          the sensor delay
   * @throws InvalidParameterException
   *           if sensor type is unavailable or unknown
   */
  public SamplingCausingAndroidSensorDevice( SensorDeviceIdentifier deviceId,
      int androidSensorType, Context context, int sensorDelay )
      throws InvalidParameterException
  {
    super( deviceId, androidSensorType, context, sensorDelay );
  }
  
  /**
   * Method to trigger sampling
   */
  private final void triggerSampling()
  {
    if ( getScanner() instanceof PassiveSampleTakingDeviceScanner )
    {
      ( (PassiveSampleTakingDeviceScanner) getScanner() ).takeSample();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractAndroidSensorDevice#
   * doHandleSensorChanged(android.hardware.SensorEvent)
   */
  @Override
  public final void doHandleSensorChanged( SensorEvent event )
  {
    if ( update( event ) )
    {
      triggerSampling();
    }
  }
  
  /**
   * Method to update the sensor data
   * 
   * @param event
   *          the sensor event to handle
   * @return true if data have been updated, false otherwise
   */
  protected abstract boolean update( SensorEvent event );
  
}