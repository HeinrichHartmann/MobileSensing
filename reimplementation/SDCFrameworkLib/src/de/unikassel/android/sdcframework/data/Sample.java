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
package de.unikassel.android.sdcframework.data;

import org.simpleframework.xml.Root;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;

import de.unikassel.android.sdcframework.data.independent.AccelerometerSampleData;
import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.data.independent.BluetoothSampleData;
import de.unikassel.android.sdcframework.data.independent.GPSSampleData;
import de.unikassel.android.sdcframework.data.independent.GSMSampleData;
import de.unikassel.android.sdcframework.data.independent.GeoLocation;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.data.independent.WifiSampleData;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.facade.BroadcastableEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEvent;

/**
 * Implementation of the sensor device sample used inside of the framework. <br/>
 * <br/>
 * A sensor device sample is observable, parcelable and broadcastable. It does
 * extend the {@linkplain BasicSample pure Java sample representation}.
 * 
 * @see SampleData
 * @see GSMSampleData
 * @see GPSSampleData
 * @see AccelerometerSampleData
 * @see BluetoothSampleData
 * @see WifiSampleData
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "sample" )
public final class Sample
    extends BasicSample
    implements ObservableEvent, BroadcastableEvent, Parcelable
{
  
  /**
   * The parcelable extra name for intent transport.
   */
  public static final String PARCELABLE_EXTRA_NAME = Sample.class.getSimpleName();
  
  /**
   * The Parcelable creator.
   */
  public static final Parcelable.Creator< Sample > CREATOR =
      new Parcelable.Creator< Sample >()
  {
    
    @Override
    public Sample createFromParcel( Parcel source )
    {
      return new Sample( source );
    }
    
    @Override
    public Sample[] newArray( int size )
    {
      return new Sample[ size ];
    }
  };
  
  /**
   * Constructor
   */
  public Sample()
  {
    super();
  }
  
  /**
   * Constructor
   * 
   * @param id
   *          the sensor device identifier
   */
  public Sample( SensorDeviceIdentifier id )
  {
    this();
    setDeviceIdentifier( id.toString() );
  }
  
  /**
   * Constructor
   * 
   * @param sId
   *          the sensor device identifier string representation
   * @param timeStamp
   *          the sample time stamp
   * @param priority
   *          the sample transmission priority
   * @param timeSynced
   *          flag is time stamp is valid ( time provider was in sync with NTP
   *          time)
   */
  public Sample( String sId, long timeStamp,
      int priority, boolean timeSynced )
  {
    this();
    setDeviceIdentifier( sId );
    setTimeStamp( timeStamp );
    setPriority( priority );
    setTimeSynced( timeSynced );
  }
  
  /**
   * Constructor
   * 
   * @param id
   *          the sensor device identifier
   * @param timeStamp
   *          the sample time stamp
   * @param priority
   *          the sample transmission priority
   * @param timeSynced
   *          flag is time stamp is valid ( time provider was in sync with NTP
   *          time)
   */
  public Sample( SensorDeviceIdentifier id, long timeStamp,
      int priority, boolean timeSynced )
  {
    this( id.toString(), timeStamp, priority, timeSynced );
  }
  
  /**
   * Copy-Constructor
   * 
   * @param sample
   *          the sample to copy construct from
   */
  public Sample( Sample sample )
  {
    this();
    setDeviceIdentifier( sample.getDeviceIdentifier() );
    setTimeStamp( sample.getTimeStamp() );
    setPriority( sample.getPriority() );
    setTimeSynced( sample.isTimeSynced() );
    GeoLocation location = sample.getLocation();
    if ( location != null )
    {
      setLocation( location.doClone() );
    }
    setData( sample.getData().doClone() );
  }
  
  /**
   * Constructor
   * 
   * @param source
   *          the parcel source
   */
  public Sample( Parcel source )
  {
    readFromParcel( source );
  }
  
  /**
   * Method to read all values from a parcel
   * 
   * @param source
   *          the parcel source
   */
  private void readFromParcel( Parcel source )
  {
    setDeviceIdentifier( source.readString() );
    setTimeStamp( source.readLong() );
    setPriority( source.readInt() );
    setTimeSynced( source.readByte() == 1 );
    boolean hasGeoLocation = source.readByte() == 1;
    if ( hasGeoLocation )
    {
      GeoLocation location = new GeoLocation();
      location.setLat( source.readDouble() );
      location.setLon( source.readDouble() );
      setLocation( location );
    }
    String className = source.readString();
    setDataFromXML( className, source.readString() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
   */
  @Override
  public void writeToParcel( Parcel dest, int flags )
  {
    dest.writeString( getDeviceIdentifier() );
    dest.writeLong( getTimeStamp() );
    dest.writeInt( getPriority() );
    dest.writeByte( (byte) ( isTimeSynced() ? 1 : 0 ) );
    
    GeoLocation location = getLocation();
    dest.writeByte( (byte) ( location != null ? 1 : 0 ) );
    if ( location != null )
    {
      dest.writeDouble( location.getLat() );
      dest.writeDouble( location.getLon() );
    }
    
    try
    {
      dest.writeString( getData().getClass().getName() );
      dest.writeString( getData().toXML() );
    }
    catch ( Exception e )
    {}
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.BroadcastableEvent#getIntent()
   */
  @Override
  public final Intent getIntent()
  {
    Intent intent = new Intent();
    intent.setAction( ACTION );
    intent.putExtra( PARCELABLE_EXTRA_NAME, this );
    return intent;
  }
  
  /**
   * Getter for related data
   * 
   * @return the path to a related data file if existing, null otherwise
   */
  public String getRelatedData()
  {
    SampleData data = getData();
    return data != null ? data.getRelatedData() : null;
  }
  
  /**
   * Update method for related data ( used to update relative file location for
   * transmitted samples )
   * 
   * @param fileName
   *          the new filename
   */
  public void updateRelatedData( String fileName )
  {
    SampleData data = getData();
    if ( data != null )
    {
      data.updateRelatedData( fileName );
    }
  }
  
  @Override
  public int describeContents()
  {
    return 0;
  }
}
