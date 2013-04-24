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
 * The location fix data fro samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "location" )
public class GeoLocation implements SerializableData
{
  /**
   * The intent identifier for the longitude field
   */
  public static final String Lon = "longitude";
  
  /**
   * The intent identifier for the latitude field
   */
  public static final String Lat = "latitude";
  
  /**
   * The location longitude
   */
  @Element( name = "lon", required = true )
  private double lon;
  
  /**
   * The location latitude
   */
  @Element( name = "lat", required = true )
  private double lat;
  
  /**
   * Constructor
   */
  public GeoLocation()
  {
    super();
  }
  
  /**
   * Copy onstructor
   * 
   * @param location
   *          the location to copy from
   */
  public GeoLocation( GeoLocation location )
  {
    super();
    setLat( location.getLat() );
    setLon( location.getLon() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o )
  {
    if ( o instanceof GeoLocation )
    {
      GeoLocation loc = (GeoLocation) o;
      
      return getLat() == loc.getLat() &&
             getLon() == loc.getLon();
    }
    return false;
  }
  
  /**
   * Method to clone the sample data
   * 
   * @return the cloned object
   */
  public final GeoLocation doClone()
  {
    return new GeoLocation( this );
  }
  
  /**
   * Getter for the lon
   * 
   * @return the lon
   */
  public double getLon()
  {
    return lon;
  }
  
  /**
   * Setter for the longitude
   * 
   * @param lon
   *          the longitude to set
   */
  public void setLon( double lon )
  {
    this.lon = lon;
  }
  
  /**
   * Getter for the latitude
   * 
   * @return the latitude
   */
  public double getLat()
  {
    return lat;
  }
  
  /**
   * Setter for the lat
   * 
   * @param lat
   *          the lat to set
   */
  public void setLat( double lat )
  {
    this.lat = lat;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public final String toString()
  {
    try
    {
      return toXML();
    }
    catch ( Exception e )
    {}
    return "";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.data.SerializableData#toXML()
   */
  @Override
  public String toXML() throws Exception
  {
    return GlobalSerializer.toXml( this );
  }
  
}
