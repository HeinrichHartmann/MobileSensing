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
 * The proximity sensor device specific sample data, which is the proximity
 * sensor distance measured in centimeters. <br/>
 * <br/>
 * ATTENTON: <br/>
 * There do exist very simple binary sensor types which do only support near and
 * far state measurement. In such a case, just two values will be set: a large
 * value for the far state and a lower one for the near state.
 * 
 * @see de.unikassel.android.sdcframework.devices.ProximityDevice
 * @see de.unikassel.android.sdcframework.devices.ProximityDeviceScanner
 * @author Katy Hilgenberg
 */
@Root( name = "data" )
public final class ProximitySampleData
    extends AbstractSampleData
{
  /**
   * The proximity sensor distance measured in centimeters, resp. the near and
   * far state values.
   */
  @Element( name = "distance" )
  private float proximityDistance;
  
  /**
   * Constructor
   */
  public ProximitySampleData()
  {}
  
  /**
   * Constructor
   * 
   * @param sampleData
   *          the sample data to copy construct from
   */
  public ProximitySampleData( ProximitySampleData sampleData )
  {
    setProximityDistance( sampleData.getProximityDistance() );
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
    return new ProximitySampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof ProximitySampleData )
    {
      ProximitySampleData sampleData = (ProximitySampleData) o;
      return getProximityDistance() == sampleData.getProximityDistance();
    }
    return false;
  }
  
  /**
   * Setter for the proximity sensor distance
   * 
   * @param proximityDistance
   *          the proximity sensor distance to set
   */
  public final void setProximityDistance( float proximityDistance )
  {
    this.proximityDistance = proximityDistance;
  }
  
  /**
   * Getter for the proximity sensor distance
   * 
   * @return the proximity sensor distance
   */
  public final float getProximityDistance()
  {
    return proximityDistance;
  }
  @Override
  public String getValues()
  {
    return "Prox. " + proximityDistance;
  }
  
}
