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
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfigurationChangeEvent;

/**
 * Implementation of the visitor for sensor device configuration updates.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class SensorDeviceConfigurationUpdateVisitor implements
    SensorDeviceVisitor
{
  /**
   * the configuration update
   */
  SensorDeviceConfigurationChangeEvent update;
  
  /**
   * The application context
   */
  Context context;
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor
   * #visit(de.unikassel.android.sdcframework.devices.facade.SensorDevice)
   */
  /**
   * Constructor
   */
  private SensorDeviceConfigurationUpdateVisitor()
  {
    super();
  }
  
  /**
   * Constructor
   * 
   * @param update
   *          the configuration update
   * @param context
   *          the application context
   */
  public SensorDeviceConfigurationUpdateVisitor(
      SensorDeviceConfigurationChangeEvent update, Context context )
  {
    this();
    setUpdate( update );
    setContext( context );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor#visit
   * (de.unikassel.android.sdcframework.devices.facade.SensorDevice,
   * android.content.Context)
   */
  @Override
  public boolean visit( SensorDevice device )
  {
    if ( device.getDeviceIdentifier().equals( getUpdate().getDeviceIdentifier() ) )
    {
      device.updateConfiguration( getUpdate().getConfiguration(), getContext() );
      return false;
    }
    return true;
  }
  
  /**
   * Getter for the configuration update
   * 
   * @return the configuration update for the device
   */
  public SensorDeviceConfigurationChangeEvent getUpdate()
  {
    return update;
  }
  
  /**
   * Setter for the configuration update
   * 
   * @param update
   *          the configuration update to set
   */
  private void setUpdate( SensorDeviceConfigurationChangeEvent update )
  {
    this.update = update;
  }
  
  /**
   * Getter for the context
   * 
   * @return the context
   */
  private Context getContext()
  {
    return context;
  }
  
  /**
   * Setter for the context
   * 
   * @param context
   *          the context to set
   */
  private void setContext( Context context )
  {
    this.context = context;
  }
  
}
