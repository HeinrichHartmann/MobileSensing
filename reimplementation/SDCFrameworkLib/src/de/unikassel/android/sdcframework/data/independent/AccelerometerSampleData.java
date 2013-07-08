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
package de.unikassel.android.sdcframework.data.independent;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * The device specific sample data of an accelerometer sensor are the
 * accelerations in x, y and z ( in m/s^2 ) axis direction as provided by the
 * Android API. <br/>
 * <br/>
 * <b>Important:</b> <br/>
 * the force of Gravity is not eliminated from the values. Thus, when the device
 * is laying on a table acceleration in z axis direction would be 9.81 m/s2.
 * 
 * @see android.hardware.SensorEvent#values
 * @see de.unikassel.android.sdcframework.devices.AccelerometerDevice
 * @see de.unikassel.android.sdcframework.devices.AccelerometerDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "data" )
public final class AccelerometerSampleData 
extends AbstractSampleData
{
  /**
   * The acceleration in x axis direction
   */
  @Element( name = "accX" )
  private float accelerationX;
  
  /**
   * The acceleration in y axis direction
   */
  @Element( name = "accY" )
  private float accelerationY;
  
  /**
   * The acceleration in z axis direction
   */
  @Element( name = "accZ" )
  private float accelerationZ;
  
  /**
   * Constructor
   */
  public AccelerometerSampleData()
  {}
  
  /**
   * Copy Constructor
   * 
   * @param sampleData
   *          the sample data to copy from
   */
  public AccelerometerSampleData( AccelerometerSampleData sampleData )
  {
    setAccelerationX( sampleData.getAccelerationX() );
    setAccelerationY( sampleData.getAccelerationY() );
    setAccelerationZ( sampleData.getAccelerationZ() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.data.facade.SampleData#doClone()
   */
  @Override
  public final SampleData doClone()
  {
    return new AccelerometerSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof AccelerometerSampleData )
    {
      AccelerometerSampleData sampleData = (AccelerometerSampleData) o;
      
      return getAccelerationX() == sampleData.getAccelerationX() &&
          getAccelerationY() == sampleData.getAccelerationY() &&
          getAccelerationZ() == sampleData.getAccelerationZ();
    }
    return false;
  }
  
  /**
   * Getter for the acceleration x direction
   * 
   * @return the acceleration x direction
   */
  public final float getAccelerationX()
  {
    return accelerationX;
  }
  
  /**
   * Setter for the acceleration x direction
   * 
   * @param accelerationX
   *          the acceleration x direction to set
   */
  public final void setAccelerationX( float accelerationX )
  {
    this.accelerationX = accelerationX;
  }
  
  /**
   * Getter for the acceleration y direction
   * 
   * @return the acceleration y direction
   */
  public final float getAccelerationY()
  {
    return accelerationY;
  }
  
  /**
   * Setter for the acceleration y direction
   * 
   * @param accelerationY
   *          the acceleration y direction to set
   */
  public final void setAccelerationY( float accelerationY )
  {
    this.accelerationY = accelerationY;
  }
  
  /**
   * Getter for the acceleration z direction
   * 
   * @return the acceleration z direction
   */
  public final float getAccelerationZ()
  {
    return accelerationZ;
  }
  
  /**
   * Setter for the acceleration in z direction
   * 
   * @param accelerationZ
   *          the acceleration z direction to set
   */
  public final void setAccelerationZ( float accelerationZ )
  {
    this.accelerationZ = accelerationZ;
  }
}
