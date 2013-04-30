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
package de.unikassel.android.sdcframework.data.tests;

import android.content.Intent;
import android.os.Build;
import android.os.Parcel;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.GeoLocation;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import junit.framework.TestCase;

/**
 * Tests for the sample class
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSample extends TestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.Sample#Sample()} .
   */
  public final void testSensorDeviceSampleImpl()
  {
    GeoLocation location = new GeoLocation();
    location.setLat( 128. );
    location.setLat( 90. );
    
    // test default construction
    Sample sample = new Sample();
    assertNull( "Expected identifier uninitialzed",
        sample.getDeviceIdentifier() );
    assertNull( "Expected data uninitialzed", sample.getData() );
    assertNull( "Expected location uninitialzed", sample.getLocation() );
    assertNull( "Expected related data uninitialzed", sample.getRelatedData() );
    assertEquals( "Expected priority uninitialzed", 0, sample.getPriority() );
    assertEquals( "Expected time stamp uninitialzed", 0L, sample.getTimeStamp() );
    assertNull( "Expected time stamp sync state not set", sample.isTimeSynced() );
    
    // test construction by identifier
    sample = new Sample( SensorDeviceIdentifier.GSM );
    assertNotNull( "Expected identifier initialzed",
        sample.getDeviceIdentifier() );
    assertEquals( "Expected identifier set",
        SensorDeviceIdentifier.GSM.toString(),
        sample.getDeviceIdentifier() );
    assertNull( "Expected data uninitialzed", sample.getData() );
    assertNull( "Expected location uninitialzed", sample.getLocation() );
    assertNull( "Expected related data uninitialzed", sample.getRelatedData() );
    assertEquals( "Expected priority uninitialzed", 0, sample.getPriority() );
    assertEquals( "Expected time stamp uninitialzed", 0L, sample.getTimeStamp() );
    assertNull( "Expected time stamp sync state not set", sample.isTimeSynced() );
    
    // test construction by identifier, time stamp and
    long timestamp = System.currentTimeMillis();
    int prio = SensorDevicePriorities.Level1.ordinal();
    sample =
        new Sample( SensorDeviceIdentifier.GSM, timestamp, prio, true );
    assertNotNull( "Expected identifier initialzed",
        sample.getDeviceIdentifier() );
    assertEquals( "Expected identifier set",
        SensorDeviceIdentifier.GSM.toString(), sample.getDeviceIdentifier() );
    assertNotNull( "Expected priority initialzed", sample.getPriority() );
    assertEquals( "Expected priority set", prio, sample.getPriority() );
    assertEquals( "Expected time stamp set", timestamp, sample.getTimeStamp() );
    assertNull( "Expected data uninitialzed", sample.getData() );
    assertNull( "Expected location uninitialzed", sample.getLocation() );
    assertNull( "Expected related data uninitialzed", sample.getRelatedData() );
    assertTrue( "Expected time stamp synced", sample.isTimeSynced() );
    
    // test copy construction
    Sample sampleToCopy = sample;
    SampleData data = TestGSMSampleData.createInitializedGSMSampleData();
    sample.setData( data );
    sample.setLocation( location );
    sample = new Sample( sample );
    assertEquals( "Expected identifier set",
        sampleToCopy.getDeviceIdentifier(), sample.getDeviceIdentifier() );
    assertEquals( "Expected priority set", sampleToCopy.getPriority(),
        sample.getPriority() );
    assertEquals( "Expected time stamp set", sampleToCopy.getTimeStamp(),
        sample.getTimeStamp() );
    assertNotNull( "Expected data initialzed", sample.getData() );
    assertEquals( "Expected location initialzed", location,
        sample.getLocation() );
    assertEquals( "Expected data set", sampleToCopy.getData(),
        sample.getData() );
    assertTrue( "Expected time stamp synced", sample.isTimeSynced() );
    
    // test setter and getter
    sample = new Sample();
    
    String sensorID = SensorDeviceIdentifier.Accelerometer.toString();
    sample.setDeviceIdentifier( sensorID );
    assertEquals( "Expected identifier set",
        sensorID,
        sample.getDeviceIdentifier() );
    
    sample.setTimeStamp( timestamp );
    assertEquals( "Expected time stamp set", timestamp, sample.getTimeStamp() );
    
    sample.setTimeSynced( true );
    assertTrue( "Expected time stamp synced", sample.isTimeSynced() );
    
    sample.setPriority( prio );
    assertEquals( "Expected priority set", prio, sample.getPriority() );
    
    sample.setData( data );
    assertEquals( "Expected data set", sampleToCopy.getData(),
        sample.getData() );
    
    sample.setLocation( location );
    assertEquals( "Expected location set", location, sample.getLocation() );
    
    // test construction from intent
    data =
        TestAccelerometerSampleData.createInitializedAccelerometerSampleData();
    sample.setData( data );
    
    Intent intent = new Intent();
    intent.setAction( Sample.ACTION );
    intent.putExtra( Sample.PARCELABLE_EXTRA_NAME, sample );
    
    Sample sampleFromIntent =
        intent.getParcelableExtra( Sample.PARCELABLE_EXTRA_NAME );
    assertEquals( "Expected identifier set", sample.getDeviceIdentifier(),
        sampleFromIntent.getDeviceIdentifier() );
    assertEquals( "Expected time stamp set", sample.getTimeStamp(),
        sampleFromIntent.getTimeStamp() );
    assertEquals( "Expected time stamp synced", sample.isTimeSynced(),
        sampleFromIntent.isTimeSynced() );
    assertEquals( "Expected priority set", sample.getPriority(),
        sampleFromIntent.getPriority() );
    assertEquals( "Expected data set", sample.getData(),
        sampleFromIntent.getData() );
    assertEquals( "Expected location set", sample.getLocation(),
        sampleFromIntent.getLocation() );
    
    // test intent getter
    Intent sampleIntent = sample.getIntent();
    assertEquals( "Expected Action set", Sample.ACTION,
        sampleIntent.getAction() );
    assertEquals( "Expected equal sample content",
        sample, intent.getParcelableExtra( Sample.PARCELABLE_EXTRA_NAME ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.Sample#equals(java.lang.Object)}
   * .
   */
  public final void testEqualsObject()
  {
    Sample sample =
        new Sample( SensorDeviceIdentifier.Bluetooth );
    sample.setTimeStamp( System.currentTimeMillis() );
    sample.setTimeSynced( true );
    sample.setPriority( 4 );
    GeoLocation location = new GeoLocation();
    location.setLat( 128. );
    location.setLat( 90. );
    sample.setLocation( location );
    sample.setData( TestBluetoothSampleData.createInitializedBluetoothSampleData() );
    
    assertEquals( "Expected object equal to itself", sample, sample );
    
    Sample newSample = new Sample();
    
    newSample.setDeviceIdentifier( sample.getDeviceIdentifier() );
    newSample.setData( sample.getData().doClone() );
    newSample.setPriority( sample.getPriority() );
    newSample.setTimeStamp( sample.getTimeStamp() );
    newSample.setLocation( sample.getLocation() );
    assertFalse( "Expected object not equal",
        sample.equals( newSample ) );
    
    newSample = new Sample( sample );
    assertTrue( "Expected object equal to copy contructed one",
        sample.equals( newSample ) );
    assertFalse(
        "Expected object not equal to an object instance of another class",
        sample.equals( new Object() ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.Sample#toString()} .
   */
  public final void testToString()
  {
    Sample sample =
        new Sample( SensorDeviceIdentifier.Bluetooth );
    sample.setTimeStamp( System.currentTimeMillis() );
    sample.setPriority( 4 );
    GeoLocation location = new GeoLocation();
    location.setLat( 128. );
    location.setLat( 90. );
    sample.setLocation( location );
    sample.setData( TestBluetoothSampleData.createInitializedBluetoothSampleData() );
    
    String string = sample.toString();
    assertNotNull( "Expected string representation not null", string );
    assertTrue( "Unexpected string representation length", string.length() > 0 );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  {
    // create the test sample
    Sample orgSample =
        new Sample( SensorDeviceIdentifier.Bluetooth );
    orgSample.setTimeStamp( System.currentTimeMillis() );
    orgSample.setTimeSynced( true );
    orgSample.setPriority( 4 );
    GeoLocation location = new GeoLocation();
    location.setLat( 128. );
    location.setLat( 90. );
    orgSample.setLocation( location );
    orgSample.setData( TestBluetoothSampleData.createInitializedBluetoothSampleData() );
    
    Sample sample = orgSample;
    String sResult;
    try
    {
      // serialize to String
      sResult = orgSample.toXML();
      
      // the following code will fail in the test environment for API levels
      // below 2.2
      // due to class not found exception in the Dalvik virtual machine.
      // Seems to be a user right problem for reflection stuff.
      if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1 )
      {
        // serialize to object
        sample =
            GlobalSerializer.fromXML( Sample.class, sResult );
        
        assertEquals(
              "Expected object serialized from string equal to the original source",
              orgSample, sample );
      }
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization to string" );
    }
  }
  
  /**
   * Test for the {@linkplain android.os.Parcelable } ability.
   */
  public void testParcelable()
  {
    // create the test sample
    Sample orgSample =
        new Sample( SensorDeviceIdentifier.Bluetooth );
    orgSample.setTimeStamp( System.currentTimeMillis() );
    orgSample.setTimeSynced( true );
    orgSample.setPriority( 4 );
    GeoLocation location = new GeoLocation();
    location.setLat( 128. );
    location.setLat( 90. );
    orgSample.setLocation( location );
    orgSample.setData( TestBluetoothSampleData.createInitializedBluetoothSampleData() );
    
    Parcel parcel = Parcel.obtain();
    orgSample.writeToParcel( parcel, 0 );
    parcel.setDataPosition( 0 );
    
    Sample sampleFromParcel =
        Sample.CREATOR.createFromParcel( parcel );
    
    assertEquals(
        "Expected object created from parcel equal to the original object",
        orgSample, sampleFromParcel );
  }
  
}
