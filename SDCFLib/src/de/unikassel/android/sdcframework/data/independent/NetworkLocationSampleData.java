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

import org.simpleframework.xml.Root;

/**
 * The device specific sample data for the rather virtual network location device ( based cell or wifi information ). <br/>
 * <br/>
 * 
 * @see LocationSampleData
 * @see de.unikassel.android.sdcframework.devices.NetworkLocationDevice
 * @see de.unikassel.android.sdcframework.devices.NetworkLocationDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "data" )
public final class NetworkLocationSampleData
extends LocationSampleData
{  
  /**
   * Constructor
   */
  public NetworkLocationSampleData()
  {}
  
  /**
   * Copy Constructor
   * 
   * @param sampleData
   *          the sample data to copy from
   */
  public NetworkLocationSampleData( NetworkLocationSampleData sampleData )
  {
    super( sampleData );
  }
  
  /**
   * Copy Constructor
   * 
   * @param sampleData
   *          the sample data to copy from
   */
  public NetworkLocationSampleData( LocationSampleData sampleData )
  {
    super( sampleData );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.data.facade.SampleData#doClone()
   */
  @Override
  public final SampleData doClone()
  {
    return new NetworkLocationSampleData( this );
  }  
}
