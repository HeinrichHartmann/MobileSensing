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
 * The magnetic filed sensor device specific sample data, which are the ambient
 * magnetic fields in the X, Y and Z axis ( in micro-Tesla ).
 * 
 * @see de.unikassel.android.sdcframework.devices.MagneticFieldDevice
 * @see de.unikassel.android.sdcframework.devices.MagneticFieldDeviceScanner
 * @author Katy Hilgenberg
 */
@Root( name = "data" )
public final class MagneticFieldSampleData
    extends AbstractSampleData
{
  /**
   * The ambient magnetic fields in the x axis
   */
  @Element( name = "fieldX" )
  private float magneticFieldX;
  
  /**
   * The ambient magnetic fields in the y axis
   */
  @Element( name = "fieldY" )
  private float magneticFieldY;
  
  /**
   * The ambient magnetic fields in the z axis
   */
  @Element( name = "fieldZ" )
  private float magneticFieldZ;
  
  /**
   * Constructor
   */
  public MagneticFieldSampleData()
  {}
  
  /**
   * Constructor
   * 
   * @param sampleData
   *          the sample data to copy construct from
   */
  public MagneticFieldSampleData( MagneticFieldSampleData sampleData )
  {
    setMagneticFieldX( sampleData.getMagneticFieldX() );
    setMagneticFieldY( sampleData.getMagneticFieldY() );
    setMagneticFieldZ( sampleData.getMagneticFieldZ() );
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
    return new MagneticFieldSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof MagneticFieldSampleData )
    {
      MagneticFieldSampleData sampleData = (MagneticFieldSampleData) o;
      return getMagneticFieldX() == sampleData.getMagneticFieldX() &&
      getMagneticFieldY() == sampleData.getMagneticFieldY() &&
      getMagneticFieldZ() == sampleData.getMagneticFieldZ();
    }
    return false;
  }
  
  /**
   * Getter for the ambient magnetic fields in the x axis
   * 
   * @return the ambient magnetic fields in the x axis
   */
  public final float getMagneticFieldX()
  {
    return magneticFieldX;
  }
  
  /**
   * Setter for the ambient magnetic fields in the x axis
   * 
   * @param magneticFieldX
   *          the ambient magnetic fields in the x axis to set
   */
  public final void setMagneticFieldX( float magneticFieldX )
  {
    this.magneticFieldX = magneticFieldX;
  }
  
  /**
   * Getter for the ambient magnetic fields in the y axis
   * 
   * @return the ambient magnetic fields in the y axis
   */
  public final float getMagneticFieldY()
  {
    return magneticFieldY;
  }
  
  /**
   * Setter for the ambient magnetic fields in the y axis
   * 
   * @param magneticFieldY
   *          the ambient magnetic fields in the y axis to set
   */
  public final void setMagneticFieldY( float magneticFieldY )
  {
    this.magneticFieldY = magneticFieldY;
  }
  
  /**
   * Getter for the ambient magnetic fields in the z axis
   * 
   * @return the ambient magnetic fields in the z axis
   */
  public final float getMagneticFieldZ()
  {
    return magneticFieldZ;
  }
  
  /**
   * Setter for the ambient magnetic fields in the z axis
   * 
   * @param magneticFieldZ
   *          the the ambient magnetic fields in the z axis to set
   */
  public final void setMagneticFieldZ( float magneticFieldZ )
  {
    this.magneticFieldZ = magneticFieldZ;
  }
  
}
