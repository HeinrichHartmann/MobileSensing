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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

/**
 * This is the framework independent serializable sample representation, which can
 * be used in pure Java projects for deserialization.<br/>
 * <br/>
 * A sensor device sample has 
 * <ul>
 * <li>a {@link #deviceIdentifier device identifier } for the source sensor
 * device,</li>
 * <li>a sample {@link #timeStamp time stamp} and</li>
 * <li>a transmission priority level corresponding to the configured device
 * priority,</li>
 * <li>a reference to the device specific {@linkplain SampleData sample data}.</li>
 * </ul>
 * <br/>
 * 
 * @see de.unikassel.android.sdcframework.data.Sample
 * @author Katy Hilgenberg
 * 
 */
public class BasicSample implements SerializableData
{
  
  /**
   * Out custom sample intent action
   */
  public static final String ACTION =
      "de.unikassel.android.sdcframework.intent.action.SAMPLE";
  
  /**
   * The intent identifier for the sensor identifier field
   */
  public static final String SensorID = "id";
  
  /**
   * The intent identifier for the time stamp field
   */
  public static final String Timestamp = "ts";
  
  /**
   * The intent identifier for the time synchronization state
   */
  public static final String TimeSyncState = "synced";
  
  /**
   * The intent identifier for the priority field
   */
  public static final String Prio = "prio";
  
  /**
   * The intent identifier for the data field
   */
  public static final String DataType = "dataType";
  
  /**
   * The intent identifier for the data field
   */
  public static final String SampleData = "data";
  
  /**
   * the sample device identifier
   */
  @Attribute( name = "id", required = true )
  protected String deviceIdentifier;
  
  /**
   * the sample time stamp
   */
  @Attribute( name = "ts", required = true )
  protected long timeStamp;
  
  /**
   * the sample priority for transmission
   */
  @Attribute( name = "prio", required = true )
  private int priority;
  
  /**
   * flag if the sample time stamp is taken while time provider was in sync with NTP time.
   */
  @Attribute( name = "synced", required = false )
  private Boolean timeSynced;
  
  /**
   * The device specific sensor data
   */
  @Element( name = "data" )
  private SampleData data;

  /**
   * The associated location
   */
  @Element( name = "location", required = false )
  private GeoLocation location;
  
  /**
   * Constructor
   */
  public BasicSample()
  {
    super();
  }
  
  /**
   * Method to create location from XML representation
   * 
   * @param xml
   *          the XML representation of the location
   * @return true if successful, false otherwise
   */
  public final boolean setLocationFromXML( String xml )
  {
    if( xml == null ) return true;
    
    // try to deserialize the location from XML
    try
    {
      GeoLocation location = GlobalSerializer.fromXML( GeoLocation.class, xml );
      setLocation( location );
      return true;
    }
    catch ( ClassNotFoundException e )
    {
      e.printStackTrace();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Method to create data from XML
   * 
   * @param dataType
   *          the data type class name
   * @param xml
   *          the XML representation of the data
   * @return true if successful, false otherwise
   */
  public final boolean setDataFromXML( String dataType, String xml )
  {
    // try to deserialize the given data type from XML
    Class< ? > c;
    try
    {
      c = Class.forName( dataType );
      Object object = GlobalSerializer.fromXML( c, xml );
      
      if ( object instanceof SampleData )
      {
        // we got valid data
        setData( (SampleData) object );
        return true;
      }
    }
    catch ( ClassNotFoundException e )
    {
      e.printStackTrace();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Test method for equivalence of two objects, allowing both being null as
   * well
   * 
   * @param o1
   *          first object
   * @param o2
   *          second object
   * @return true if equal pointers or equal values
   */
  public static final boolean equals( Object o1, Object o2 )
  {
    if ( o1 != null && o2 != null )
    {
      // both objects are initialized return comparison value
      return o1.equals( o2 );
    }
    // at least one object is null -> return true if both are null
    return o1 == o2;
  }
  
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof BasicSample )
    {
      BasicSample sample = (BasicSample) o;
      return equals( getDeviceIdentifier(), sample.getDeviceIdentifier() ) &&
          getTimeStamp() == sample.getTimeStamp() &&
          getPriority() == sample.getPriority() &&
          equals( getData(), sample.getData() ) &&
          equals( getLocation(), sample.getLocation() ) &&
          equals( isTimeSynced() , sample.isTimeSynced() );
      
    }
    return false;
  }
  
  /**
   * Setter for the time stamp ( the number of milliseconds since January 1,
   * 1970, 00:00:00 GMT )
   * 
   * @param timeStamp
   *          the time stamp to set
   */
  public final void setTimeStamp( long timeStamp )
  {
    this.timeStamp = timeStamp;
  }
  
  /**
   * Getter for the time stamp ( the number of milliseconds since January 1,
   * 1970, 00:00:00 GMT )
   * 
   * @return the time stamp
   */
  public final long getTimeStamp()
  {
    return timeStamp;
  }
  
  /**
   * Getter for the timeSynced
  
   * @return the timeSynced
   */
  public Boolean isTimeSynced()
  {
    return timeSynced;
  }

  /**
   * Setter for the timeSynced
  
   * @param timeSynced the timeSynced to set
   */
  public void setTimeSynced( Boolean timeSynced )
  {
    this.timeSynced = timeSynced;
  }

  /**
   * Setter for the priority ( ordinal value of a
   * {@link de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities
   * priority level} )
   * 
   * @param priority
   *          the priority to set
   */
  public final void setPriority( int priority )
  {
    this.priority = priority;
  }
  
  /**
   * Getter for the priority ( ordinal value of a
   * {@link de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities
   * priority level} )
   * 
   * @return the priority
   */
  public final int getPriority()
  {
    return priority;
  }
  
  /**
   * Getter for the sample data
   * 
   * @return the sample data
   */
  public final SampleData getData()
  {
    return data;
  }
  
  /**
   * Setter for the sample data
   * 
   * @param data
   *          the sample data to set
   */
  public final void setData( SampleData data )
  {
    this.data = data;
  }
  
  /**
   * Setter for the device identifier ( string representation of the sensors
   * {@link de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier
   * identifier} )
   * 
   * @param deviceIdentifier
   *          the device identifier to set
   */
  public final void setDeviceIdentifier( String deviceIdentifier )
  {
    this.deviceIdentifier = deviceIdentifier;
  }
  
  /**
   * Getter for the device identifier ( string representation of the sensors
   * {@link de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier
   * identifier} )
   * 
   * @return the device identifier
   */
  public final String getDeviceIdentifier()
  {
    return deviceIdentifier;
  }
  
  /**
   * Setter for the location
  
   * @param location the location to set
   */
  public void setLocation( GeoLocation location )
  {
    this.location = location;
  }

  /**
   * Getter for the location
  
   * @return the location
   */
  public GeoLocation getLocation()
  {
    return location;
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
   * @see de.unikassel.android.sdcframework.data.SerializableDataImpl#toXML()
   */
  @Override
  public final String toXML() throws Exception
  {
    return GlobalSerializer.toXml( this );
  }
}