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
package de.unikassel.android.sdcframework.persistence.facade;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.GeoLocation;

/**
 * A sample adaption for database access.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class DatabaseSample
{
  /**
   * the sample device identifier
   */
  public String deviceIdentifier;
  
  /**
   * the sample time stamp
   */
  public long timeStamp;
  
  /**
   * the sample priority for transmission
   */
  public int priority;
  
  /**
   * the sample time stamp synchronization state.
   */
  public boolean synced;
  
  /**
   * The device specific sensor data
   */
  public String data;
  
  /**
   * The serialized location information
   */
  public String location;
  
  /**
   * The class name of the data type
   */
  public String dataTypeClassName;
  
  /**
   * Constructor
   */
  public DatabaseSample()
  {}
  
  /**
   * Constructor
   * 
   * @param sample
   *          the sample to construct from
   * @throws Exception
   *           in case of serialization error
   */
  public DatabaseSample( Sample sample ) throws Exception
  {
    deviceIdentifier = sample.getDeviceIdentifier();
    timeStamp = sample.getTimeStamp();
    priority = sample.getPriority();
    synced = sample.isTimeSynced();
    dataTypeClassName = sample.getData().getClass().getName();
    GeoLocation loc = sample.getLocation();
    if ( loc != null )
    {
      location = loc.toXML();
    }
    data = sample.getData().toXML();
  }
  
  /**
   * Does create a sample from this database sample
   * 
   * @return a sample created from this database sample or null if creation
   *         fails
   */
  public Sample toSample()
  {
    Sample sample = new Sample( deviceIdentifier, timeStamp, priority, synced );
    sample.setLocationFromXML( location );
    if ( sample.setDataFromXML( dataTypeClassName, data ) )
      return sample;
    return null;
  }
  
}
