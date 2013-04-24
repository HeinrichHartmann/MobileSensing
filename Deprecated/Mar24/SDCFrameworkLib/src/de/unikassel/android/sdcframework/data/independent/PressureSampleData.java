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
 * The pressure sensor device specific sample data, which is the atmospheric pressure in hPa (millibar).
 * 
 * @see de.unikassel.android.sdcframework.devices.PressureDevice
 * @see de.unikassel.android.sdcframework.devices.PressureDeviceScanner
 * @author Katy Hilgenberg
 */
@Root( name = "data" )
public class PressureSampleData 
extends AbstractSampleData
{
  /**
   * The atmospheric pressure in hPa
   */
  @Element( name = "pressure" )
  private float pressure;
  
  /**
   * Constructor
   */
  public PressureSampleData()
  {}
  
  /**
   * Constructor
   * 
   * @param sampleData
   *          the sample data to copy construct from
   */
  public PressureSampleData( PressureSampleData sampleData )
  {
    setPressure( sampleData.getPressure() );
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
    return new PressureSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof PressureSampleData )
    {
      PressureSampleData sampleData = (PressureSampleData) o;
      return getPressure() == sampleData.getPressure();
    }
    return false;
  }
  
  /**
   * Setter for the pressure
   * 
   * @param pressure
   *          the pressure to set
   */
  public final void setPressure( float pressure )
  {
    this.pressure = pressure;
  }
  
  /**
   * Getter for the pressure
   * 
   * @return the pressure
   */
  public final float getPressure()
  {
    return pressure;
  }
  
}
