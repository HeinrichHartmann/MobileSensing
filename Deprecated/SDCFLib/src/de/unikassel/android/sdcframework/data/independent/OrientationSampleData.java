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
 * The orientation sensor device specific sample data, which are heading (angle
 * between the magnetic north direction and the y-axis, 0=North, 90=East,
 * 180=South, 270=West ), pitch ( rotation around x-axis,-180 to 180, with
 * positive values when the z-axis moves toward the y-axis) and roll ( rotation
 * around y-axis, -90 to 90, with positive values when the x-axis moves toward
 * the z-axis ).
 * 
 * @see de.unikassel.android.sdcframework.devices.OrientationDevice
 * @see de.unikassel.android.sdcframework.devices.OrientationDeviceScanner
 * @author Katy Hilgenberg
 */
@Root( name = "data" )
public final class OrientationSampleData
    extends AbstractSampleData
{
  /**
   * The ambient angle between the magnetic north direction and the y-axis
   */
  @Element( name = "heading" )
  private float heading;
  
  /**
   * The rotation around the x-axis
   */
  @Element( name = "pitch" )
  private float pitch;
  
  /**
   * The rotation around the y-axis
   */
  @Element( name = "roll" )
  private float roll;
  
  /**
   * Constructor
   */
  public OrientationSampleData()
  {}
  
  /**
   * Constructor
   * 
   * @param sampleData
   *          the sample data to copy construct from
   */
  public OrientationSampleData( OrientationSampleData sampleData )
  {
    setHeading( sampleData.getHeading() );
    setPitch( sampleData.getPitch() );
    setRoll( sampleData.getRoll() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.data.independent.SampleData#doClone()
   */
  @Override
  public final SampleData doClone()
  {
    return new OrientationSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof OrientationSampleData )
    {
      OrientationSampleData sampleData = (OrientationSampleData) o;
      return getHeading() == sampleData.getHeading() &&
          getPitch() == sampleData.getPitch() &&
          getRoll() == sampleData.getRoll();
    }
    return false;
  }
  
  /**
   * Getter for the angle between the magnetic north direction and the y-axis
   * 
   * @return the angle between the magnetic north direction and the y-axis
   */
  public final float getHeading()
  {
    return heading;
  }
  
  /**
   * Setter for the angle between the magnetic north direction and the y-axis
   * 
   * @param heading
   *          the angle between the magnetic north direction and the y-axis to set
   */
  public final void setHeading( float heading )
  {
    this.heading = heading;
  }
  
  /**
   * Getter for the rotation around the x-axis
   * 
   * @return the rotation around the x-axis
   */
  public final float getPitch()
  {
    return pitch;
  }
  
  /**
   * Setter for the rotation around the x-axis
   * 
   * @param pitch
   *          the rotation around the x-axis to set
   */
  public final void setPitch( float pitch )
  {
    this.pitch = pitch;
  }
  
  /**
   * Getter for the rotation around the y-axis
   * 
   * @return the rotation around the y-axis
   */
  public final float getRoll()
  {
    return roll;
  }
  
  /**
   * Setter for the rotation around the y-axis
   * 
   * @param roll
   *          the rotation around the y-axis to set
   */
  public final void setRoll( float roll )
  {
    this.roll = roll;
  }
  
}
