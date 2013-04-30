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

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Parcel;
import android.test.AndroidTestCase;

import de.unikassel.android.sdcframework.data.SampleCollection;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.AccelerometerSampleData;
import de.unikassel.android.sdcframework.data.independent.BluetoothSampleData;
import de.unikassel.android.sdcframework.data.independent.GSMSampleData;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.data.independent.WifiSampleData;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.util.FileUtils;

/**
 * Test for the sample collection.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSampleCollection
    extends AndroidTestCase
{
  /**
   * file pointer
   */
  private File file = null;
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    if ( file != null )
    {
      FileUtils.deleteFile( file.getAbsolutePath() );
    }
    super.tearDown();
  }
  
  /**
   * Test method for construction, setter, getter and other core methods .
   */
  public final void testSampleCollection()
  {
    // test construction
    SampleCollection sc = new SampleCollection();
    assertNotNull(
        "Expected getter for collection does always deliver an instance",
        sc.getSamples() );
    
    // tests setter for sample list
    Vector< Sample > vecSamples =
        new Vector< Sample >();
    sc.setSamples( vecSamples );
    assertSame( "Expected vector changed", vecSamples, sc.getSamples() );
    assertEquals( "Unexpected sample count in list", 0, sc.getSamples().size() );
    assertTrue( "Unexpected empty state", sc.isEmpty() );
    
    // test adding samples
    Sample gsmSample =
        createSample( SensorDeviceIdentifier.GSM,
            TestGSMSampleData.createInitializedGSMSampleData() );
    sc.add( gsmSample );
    assertEquals( "Unexpected sample count in list", 1, sc.getSamples().size() );
    assertEquals( "Unexpected size of list", 1, sc.size() );
    assertTrue( "Expected sample in list", sc.getSamples().contains( gsmSample ) );
    assertTrue( "Expected sample in list", sc.contains( gsmSample ) );
    assertFalse( "Unexpected empty state", sc.isEmpty() );
    
    Sample wifiSample =
        createSample( SensorDeviceIdentifier.Wifi,
            TestWifiSampleData.createInitializedWifiSampleData() );
    sc.add( wifiSample );
    assertEquals( "Unexpected sample count in list", 2, sc.getSamples().size() );
    assertTrue( "Expected sample in list",
        sc.getSamples().contains( wifiSample ) );
    assertTrue( "Expected sample in list", sc.contains( wifiSample ) );
    
    Sample bluetoothSample =
        createSample( SensorDeviceIdentifier.Bluetooth,
            new BluetoothSampleData() );
    sc.add( bluetoothSample );
    assertEquals( "Unexpected sample count in list", 3, sc.getSamples().size() );
    assertTrue( "Expected sample in list", sc.getSamples().contains(
        bluetoothSample ) );
    assertTrue( "Expected sample in list", sc.contains( bluetoothSample ) );
    
    // test toArray methods
    Object[] array = sc.toArray();
    assertEquals( "Expected array with 3 entires", 3, array.length );
    Sample[] typedArray = new Sample[ sc.size() ];
    sc.toArray( typedArray );
    for ( int i = 0; i < sc.size(); ++i )
    {
      assertNotNull( "Expected array element not null", array[ i ] );
      assertTrue( "Expected sample from array in list",
          sc.contains( array[ i ] ) );
      
      assertNotNull( "Expected typed array element not null", typedArray[ i ] );
      assertTrue( "Expected sample from typed array in list",
          sc.contains( typedArray[ i ] ) );
    }
    
    // test collection based access methods
    SampleCollection sc2 = new SampleCollection();
    sc2.addAll( sc );
    assertEquals( "Unexpected sample count in list", sc.getSamples().size(),
        sc2.getSamples().size() );
    assertEquals( "Unexpected size of list", sc.size(), sc2.size() );
    assertTrue( "Expected all samples in new list",
        sc2.getSamples().containsAll( sc.getSamples() ) );
    assertTrue( "Expected contains all returns true", sc2.containsAll( sc ) );
    
    assertTrue( "Unexpected result of removeAll", sc2.removeAll( sc ) );
    assertEquals( "Unexpected sample count in list", 0, sc2.getSamples().size() );
    
    sc2.addAll( sc );
    assertTrue( "Expected all samples in new list",
        sc2.getSamples().containsAll( sc.getSamples() ) );
    
    // test remove
    assertTrue( "Unexpected result of remove", sc.remove( bluetoothSample ) );
    assertEquals( "Unexpected sample count in list", 2, sc.getSamples().size() );
    assertFalse( "Expected sample removed from list",
        sc.contains( bluetoothSample ) );
    assertTrue( "Expected sample in list", sc.contains( wifiSample ) );
    assertTrue( "Expected sample in list", sc.contains( gsmSample ) );
    
    assertTrue( "Unexpected result of retainAll", sc2.retainAll( sc ) );
    assertEquals( "Unexpected sample count in list", 2, sc2.getSamples().size() );
    assertTrue( "Expected sample in list", sc2.contains( wifiSample ) );
    assertTrue( "Expected sample in list", sc2.contains( gsmSample ) );
    assertFalse( "Expected sample in list", sc2.contains( bluetoothSample ) );
    
    // test clear
    sc.clear();
    assertEquals( "Unexpected sample count in list", 0, sc.getSamples().size() );
    
    // test intent getter
    sc = new SampleCollection();
    fillSampleCollectionWithTestData( sc );
    Intent intent = sc.getIntent();
    assertEquals( "Expected Action set", SampleCollection.ACTION,
        intent.getAction() );
    assertEquals(
        "Expected equal sample content",
        sc.toString(),
        ( (SampleCollection) intent.getParcelableExtra( SampleCollection.PARCELABLE_EXTRA_NAME ) ).toString() );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testStringSerialization()
  {
    SampleCollection sc = new SampleCollection();
    
    // test for empty collection
    try
    {
      // serialize to xml
      String xml = GlobalSerializer.toXml( sc );
      System.out.println( xml );
      
      // serialize a new object from xml to object
      SampleCollection sc2 =
          GlobalSerializer.fromXML( SampleCollection.class, xml );
      
      assertEquals(
          "Expected object serialized from string equal to the original source",
          sc.toString(), sc2.toString() );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization" );
    }
    
    fillSampleCollectionWithTestData( sc );
    
    try
    {
      // serialize to xml
      String xml = GlobalSerializer.toXml( sc );
      System.out.println( xml );
      
      // the following code will fail in the test environment for API levels
      // below
      // 2.2
      // due to class not found exception in the Dalvik virtual machine.
      // Seems to be a user right problem for reflection stuff.
      if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1 )
      {
        // serialize a new object from xml to object
        SampleCollection sc2 =
            GlobalSerializer.fromXML( SampleCollection.class, xml );
        
        assertEquals(
            "Expected object serialized from string equal to the original source",
            sc.toString(), sc2.toString() );
      }
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization" );
    }
  }
  
  /**
   * Test method for serialization.
   */
  public final void testFileSerialization()
  {
    SampleCollection sc = new SampleCollection();
    try
    {
      file = File.createTempFile( "test_", ".xml", getContext().getCacheDir() );
    }
    catch ( IOException e1 )
    {
      fail( "Unexpected exception" );
    }
    
    // test for empty collection
    try
    {
      // serialize to xml
      GlobalSerializer.serializeToFile( sc, file );
      
      // serialize a new object from xml to object
      SampleCollection sc2 =
          GlobalSerializer.serializeFromFile( SampleCollection.class, file );
      
      assertEquals(
          "Expected object serialized from string equal to the original source",
          sc.toString(), sc2.toString() );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization" );
    }
    
    fillSampleCollectionWithTestData( sc );
    
    try
    {
      // serialize to xml
      GlobalSerializer.serializeToFile( sc, file );
      
      // the following code will fail in the test environment for API levels
      // below
      // 2.2
      // due to class not found exception in the Dalvik virtual machine.
      // Seems to be a user right problem for reflection stuff.
      if ( Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1 )
      {
        // serialize a new object from xml to object
        SampleCollection sc2 =
            GlobalSerializer.serializeFromFile( SampleCollection.class, file );
        
        assertEquals(
            "Expected object serialized from string equal to the original source",
            sc.toString(), sc2.toString() );
      }
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization" );
    }
  }
  
  /**
   * Does fill a sample collection with sample test data
   * 
   * @param sampleCollection
   *          the sample collection to fill with test data
   */
  public static final void fillSampleCollectionWithTestData(
      SampleCollection sampleCollection )
  {
    // test for non empty collection
    sampleCollection.add( createSample( SensorDeviceIdentifier.Accelerometer,
        new AccelerometerSampleData() ) );
    sampleCollection.add( createSample( SensorDeviceIdentifier.Wifi,
        new WifiSampleData() ) );
    sampleCollection.add( createSample( SensorDeviceIdentifier.Bluetooth,
        new BluetoothSampleData() ) );
    sampleCollection.add( createSample( SensorDeviceIdentifier.GSM,
        new GSMSampleData() ) );
    sampleCollection.add( createSample( SensorDeviceIdentifier.Accelerometer,
        TestAccelerometerSampleData.createInitializedAccelerometerSampleData() ) );
    sampleCollection.add( createSample( SensorDeviceIdentifier.Wifi,
        TestWifiSampleData.createInitializedWifiSampleData() ) );
    sampleCollection.add( createSample( SensorDeviceIdentifier.Bluetooth,
        TestBluetoothSampleData.createInitializedBluetoothSampleData() ) );
    sampleCollection.add( createSample( SensorDeviceIdentifier.GSM,
        TestGSMSampleData.createInitializedGSMSampleData() ) );
  }
  
  /**
   * Method to create an initialized sample
   * 
   * @param id
   *          the sensor device identifier
   * @param data
   *          the sensor data
   * @return the created sample
   */
  public static final Sample createSample(
      SensorDeviceIdentifier id, SampleData data )
  {
    Sample sample = new Sample( id );
    sample.setData( data );
    sample.setPriority( SensorDevicePriorities.Level2.ordinal() );
    sample.setTimeStamp( System.currentTimeMillis() );
    sample.setTimeSynced( true );
    return sample;
  }
  
  /**
   * Method to create a collection of samples
   * 
   * @param count
   *          the count of samples to create
   * @return a collection of samples
   */
  public static SampleCollection createSamples( long count )
  {
    // create Test data to write to database
    SampleCollection sc = new SampleCollection();
    for ( long i = 0; i < count; ++i )
    {
      sc.add( createSample( SensorDeviceIdentifier.GSM,
          TestGSMSampleData.createInitializedGSMSampleData() ) );
    }
    return sc;
  }
  
  /**
   * Test for the parcelable interface.
   */
  @SuppressLint( "Recycle" )
  public void testParcelable()
  {
    // create the test sample
    SampleCollection sc = new SampleCollection();
    fillSampleCollectionWithTestData( sc );
    
    Parcel parcel = Parcel.obtain();
    sc.writeToParcel( parcel, 0 );
    parcel.setDataPosition( 0 );
    
    SampleCollection scFromParcel =
        SampleCollection.CREATOR.createFromParcel( parcel );
    
    assertEquals(
        "Expected object created from parcel equal to the original object",
        sc.toString(), scFromParcel.toString() );
  }
}
