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
 * Base class for location specific sample data (GPS, Cell Info OR Wifif based) are the
 * {@linkplain #longitude }, {@linkplain #latitude }, {@linkplain #altitude } and
 * {@linkplain #speed speed over ground information} as provided by the Android API. <br/>
 * <br/>
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "data" )
public class LocationSampleData
extends AbstractSampleData
{
  /**
   * The longitude
   */
  @Element( name = "lon", required = true )
  private Double longitude;
  
  /**
   * The latitude
   */
  @Element( name = "lat", required = true )
  private Double latitude;
  
  /**
   * The altitude
   */
  @Element( name = "alt", required = false )
  private Double altitude;
  
  /**
   * The speed over ground in m/s
   */
  @Element( name = "speed", required = false )
  private Float speed;
  
  /**
   * The accuracy of the GPS fix
   */
  @Element( name = "accuracy", required = false )
  private Float accuracy;
  
  /**
   * Constructor
   */
  public LocationSampleData()
  {}
  
  /**
   * Copy Constructor
   * 
   * @param sampleData
   *          the sample data to copy from
   */
  public LocationSampleData( LocationSampleData sampleData )
  {
    setLongitude( sampleData.getLongitude() );
    setLatitude( sampleData.getLatitude() );
    setAltitude( sampleData.getAltitude() );
    setSpeed( sampleData.getSpeed() );
    setAccuracy( sampleData.getAccuracy() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.data.facade.SampleData#doClone()
   */
  @Override
  public SampleData doClone()
  {
    return new LocationSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof LocationSampleData )
    {
      LocationSampleData sampleData = (LocationSampleData) o;
      
      return BasicSample.equals( getLongitude(),
          sampleData.getLongitude() ) &&
          BasicSample.equals( getLatitude(),
              sampleData.getLatitude() ) &&
          BasicSample.equals( getAltitude(),
              sampleData.getAltitude() ) &&
          BasicSample.equals( getSpeed(),
              sampleData.getSpeed() ) &&
          BasicSample.equals( getAccuracy(),
              sampleData.getAccuracy() );
    }
    return false;
  }
  
  /**
   * Getter for the longitude
   * 
   * @return the longitude
   */
  public final Double getLongitude()
  {
    return longitude;
  }
  
  /**
   * Setter for the longitude
   * 
   * @param longitude
   *          the longitude to set
   */
  public final void setLongitude( Double longitude )
  {
    this.longitude = longitude;
  }
  
  /**
   * Getter for the latitude
   * 
   * @return the latitude
   */
  public final Double getLatitude()
  {
    return latitude;
  }
  
  /**
   * Setter for the latitude
   * 
   * @param latitude
   *          the latitude to set
   */
  public final void setLatitude( Double latitude )
  {
    this.latitude = latitude;
  }
  
  /**
   * Getter for the altitude
   * 
   * @return the altitude
   */
  public final Double getAltitude()
  {
    return altitude;
  }
  
  /**
   * Setter for the altitude
   * 
   * @param altitude
   *          the altitude to set
   */
  public final void setAltitude( Double altitude )
  {
    this.altitude = altitude;
  }
  
  /**
   * Getter for the speed
   * 
   * @return the speed
   */
  public final Float getSpeed()
  {
    return speed;
  }
  
  /**
   * Setter for the speed
   * 
   * @param speed
   *          the speed to set
   */
  public final void setSpeed( Float speed )
  {
    this.speed = speed;
  }
  
  /**
   * Getter for the accuracy
   * 
   * @return the accuracy
   */
  public final Float getAccuracy()
  {
    return accuracy;
  }
  
  /**
   * Setter for the accuracy
   * 
   * @param accuracy
   *          the accuracy to set
   */
  public final void setAccuracy( Float accuracy )
  {
    this.accuracy = accuracy;
  }
  
}
