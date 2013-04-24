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
 * The light sensor device specific sample data, which is the ambient light level in SI lux units.<br/>
 * <br/>
 * ATTENTON: <br/>
 * There do exist simple binary sensor types which do only support bright and dark  measurement. 
 * In such a case, just two values will be set: a large
 * value for the bright state and a lower one for the dark state.
 * 
 * @see de.unikassel.android.sdcframework.devices.LightDevice
 * @see de.unikassel.android.sdcframework.devices.LightDeviceScanner
 * @author Katy Hilgenberg
 */
@Root( name = "data" )
public final class LightSampleData 
extends AbstractSampleData
{
  /**
   * The ambient light level in SI lux unit
   */
  @Element( name = "level" )
  private float lightLevel;
  
  /**
   * Constructor
   */
  public LightSampleData()
  {}
  
  /**
   * Constructor
   * 
   * @param sampleData
   *          the sample data to copy construct from
   */
  public LightSampleData( LightSampleData sampleData )
  {
    setLightLevel( sampleData.getLightLevel() );
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
    return new LightSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof LightSampleData )
    {
      LightSampleData sampleData = (LightSampleData) o;
      return getLightLevel() == sampleData.getLightLevel();
    }
    return false;
  }
  
  /**
   * Setter for the ambient light level
   * 
   * @param lightLevel
   *          the ambient light level
   */
  public final void setLightLevel( float lightLevel )
  {
    this.lightLevel = lightLevel;
  }
  
  /**
   * Getter for the ambient light level
   * 
   * @return the ambient light level
   */
  public final float getLightLevel()
  {
    return lightLevel;
  }
  @Override
  public String getValues()
  {
    // TODO Auto-generated method stub
    return "Light " + lightLevel;
  }
}
