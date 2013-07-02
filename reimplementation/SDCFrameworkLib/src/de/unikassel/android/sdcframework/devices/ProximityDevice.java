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
import de.unikassel.android.sdcframework.data.independent.ProximitySampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;

/**
 * Implementation of the proximity sensor device.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ProximityDevice extends SamplingCausingAndroidSensorDevice
{
  /**
   * The actual sample of the device, updated whenever the handler for sensor
   * changes is called
   */
  private final ProximitySampleData currentSampleData;
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   */
  public ProximityDevice( Context context )
  {
    super( SensorDeviceIdentifier.Proximity, Sensor.TYPE_PROXIMITY, context,
        SensorManager.SENSOR_DELAY_NORMAL );
    
    this.currentSampleData = new ProximitySampleData();
    currentSampleData.setProximityDistance( -1F );
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

  /* (non-Javadoc)
   * @see de.unikassel.android.sdcframework.devices.SamplingCausingSensorDevice#update(android.hardware.SensorEvent)
   */
  @Override
  protected boolean update( SensorEvent event )
  {
    if ( event.values != null && event.values.length == 3 )
    {
      currentSampleData.setProximityDistance( event.values[ 0 ] );
      return true;
    }
    return false;
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
    return currentSampleData.getProximityDistance() >= 0;
  }
  
}
