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
 * The temperature sensor device specific sample data, which is the temperature in degree celsius.
 * 
 * @see de.unikassel.android.sdcframework.devices.TemperatureDevice
 * @see de.unikassel.android.sdcframework.devices.TemperatureDeviceScanner
 * @author Katy Hilgenberg
 */
@Root( name = "data" )
public final class TemperatureSampleData 
extends AbstractSampleData
{
  /**
   * The temperature in degree celsius
   */
  @Element( name = "temperature" )
  private float temperature;
  
  /**
   * Constructor
   */
  public TemperatureSampleData()
  {}
  
  /**
   * Constructor
   * 
   * @param sampleData
   *          the sample data to copy construct from
   */
  public TemperatureSampleData( TemperatureSampleData sampleData )
  {
    setTemperature( sampleData.getTemperature() );
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
    return new TemperatureSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof TemperatureSampleData )
    {
      TemperatureSampleData sampleData = (TemperatureSampleData) o;
      return getTemperature() == sampleData.getTemperature();
    }
    return false;
  }
  
  /**
   * Setter for the temperature
   * 
   * @param temperature
   *          the temperature to set
   */
  public final void setTemperature( float temperature )
  {
    this.temperature = temperature;
  }
  
  /**
   * Getter for the temperature
   * 
   * @return the temperature
   */
  public final float getTemperature()
  {
    return temperature;
  }
  
}
