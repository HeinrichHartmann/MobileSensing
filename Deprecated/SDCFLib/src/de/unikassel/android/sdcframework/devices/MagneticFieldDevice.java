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
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import de.unikassel.android.sdcframework.data.independent.MagneticFieldSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;

/**
 * Implementation of the magnetic field sensor device.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class MagneticFieldDevice extends AbstractAndroidSensorDevice
{
  /**
   * The actual sample of the device, updated whenever the handler for sensor
   * changes is called
   */
  private final MagneticFieldSampleData currentSampleData;
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   */
  public MagneticFieldDevice( Context context )
  {
    super( SensorDeviceIdentifier.MagneticField, Sensor.TYPE_MAGNETIC_FIELD, context,
        SensorManager.SENSOR_DELAY_NORMAL );
    
    this.currentSampleData = new MagneticFieldSampleData();
    currentSampleData.setMagneticFieldX( Float.MIN_VALUE );
    currentSampleData.setMagneticFieldY( Float.MIN_VALUE );
    currentSampleData.setMagneticFieldZ( Float.MIN_VALUE );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractAndroidSensorDevice#
   * getCurrentSampleData()
   */
  @Override
  protected final synchronized SampleData getCurrentSampleData()
  {
    return currentSampleData;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractAndroidSensorDevice#
   * doHandleSensorChanged(android.hardware.SensorEvent)
   */
  @Override
  public final synchronized void doHandleSensorChanged( SensorEvent event )
  {
    if ( event.values != null && event.values.length == 3 )
    {
      currentSampleData.setMagneticFieldX( event.values[ 0 ] );
      currentSampleData.setMagneticFieldY( event.values[ 1 ] );
      currentSampleData.setMagneticFieldZ( event.values[ 2 ] );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice
   * #hasSample()
   */
  @Override
  public final synchronized boolean hasSample()
  {
    return !( currentSampleData.getMagneticFieldX() == Float.MIN_VALUE
        && currentSampleData.getMagneticFieldY() == Float.MIN_VALUE 
        && currentSampleData.getMagneticFieldZ() == Float.MIN_VALUE );
  }
  
}
