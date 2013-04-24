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
 * The device specific sample data of an gyroscope sensor are the rates of
 * rotation around the device's local X, Y and Z axis ( in radians/second,
 * positive in the counter-clockwise direction ) as provided by the Android API. <br/>
 * <br/>
 * 
 * @see android.hardware.SensorEvent#values
 * @see de.unikassel.android.sdcframework.devices.GyroscopeDevice
 * @see de.unikassel.android.sdcframework.devices.GyroscopeDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "data" )
public final class GyroscopeSampleData
    extends AbstractSampleData
{
  /**
   * The angular rotation speed around the x axis
   */
  @Element( name = "angSpeedX" )
  private float angularSpeedX;
  
  /**
   * The angular rotation speed around the y axis
   */
  @Element( name = "angSpeedY" )
  private float angularSpeedY;
  
  /**
   * The angular rotation speed around the z axis
   */
  @Element( name = "angSpeedZ" )
  private float angularSpeedZ;
  
  /**
   * Constructor
   */
  public GyroscopeSampleData()
  {}
  
  /**
   * Copy Constructor
   * 
   * @param sampleData
   *          the sample data to copy from
   */
  public GyroscopeSampleData( GyroscopeSampleData sampleData )
  {
    setAngularSpeedX( sampleData.getAngularSpeedX() );
    setAngularSpeedY( sampleData.getAngularSpeedY() );
    setAngularSpeedZ( sampleData.getAngularSpeedZ() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.data.facade.SampleData#doClone()
   */
  @Override
  public final SampleData doClone()
  {
    return new GyroscopeSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof GyroscopeSampleData )
    {
      GyroscopeSampleData sampleData = (GyroscopeSampleData) o;
      
      return getAngularSpeedX() == sampleData.getAngularSpeedX() &&
          getAngularSpeedY() == sampleData.getAngularSpeedY() &&
          getAngularSpeedZ() == sampleData.getAngularSpeedZ();
    }
    return false;
  }
  
  /**
   * Getter for the angular speed around the x axis
   * 
   * @return the angular speed around the x axis
   */
  public final float getAngularSpeedX()
  {
    return angularSpeedX;
  }
  
  /**
   * Setter for the the angular speed around the x axis
   * 
   * @param angularSpeedX
   *          the angular speed around the x axis to set
   */
  public final void setAngularSpeedX( float angularSpeedX )
  {
    this.angularSpeedX = angularSpeedX;
  }
  
  /**
   * Getter for the angular speed around the y axis
   * 
   * @return the angular speed around the y axis
   */
  public final float getAngularSpeedY()
  {
    return angularSpeedY;
  }
  
  /**
   * Setter for the angular speed around the y axis
   * 
   * @param angularSpeedY
   *          the angular speed around the y axis to set
   */
  public final void setAngularSpeedY( float angularSpeedY )
  {
    this.angularSpeedY = angularSpeedY;
  }
  
  /**
   * Getter for the angular speed around the z axis
   * 
   * @return the angular speed around the z axis
   */
  public final float getAngularSpeedZ()
  {
    return angularSpeedZ;
  }
  
  /**
   * Setter for the angular speed around the z axis
   * 
   * @param angularSpeedZ
   *          the angular speed around the z axis to set
   */
  public final void setAngularSpeedZ( float angularSpeedZ )
  {
    this.angularSpeedZ = angularSpeedZ;
  }
}
