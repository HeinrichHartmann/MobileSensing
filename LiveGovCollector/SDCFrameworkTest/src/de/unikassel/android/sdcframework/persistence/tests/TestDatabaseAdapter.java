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
package de.unikassel.android.sdcframework.persistence.tests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.AccelerometerSampleData;
import de.unikassel.android.sdcframework.data.independent.BluetoothSampleData;
import de.unikassel.android.sdcframework.data.independent.GPSSampleData;
import de.unikassel.android.sdcframework.data.independent.GSMSampleData;
import de.unikassel.android.sdcframework.data.independent.FileReferenceSampleData;
import de.unikassel.android.sdcframework.data.independent.GeoLocation;
import de.unikassel.android.sdcframework.data.independent.TwitterSampleData;
import de.unikassel.android.sdcframework.data.independent.WifiSampleData;
import de.unikassel.android.sdcframework.data.tests.TestAccelerometerSampleData;
import de.unikassel.android.sdcframework.data.tests.TestBluetoothSampleData;
import de.unikassel.android.sdcframework.data.tests.TestGPSSampleData;
import de.unikassel.android.sdcframework.data.tests.TestGSMSampleData;
import de.unikassel.android.sdcframework.data.tests.TestSampleCollection;
import de.unikassel.android.sdcframework.data.tests.TestWifiSampleData;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.persistence.DatabaseAdapterImpl;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseSample;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.test.AndroidTestCase;

/**
 * @author Katy Hilgenberg
 * 
 */
public class TestDatabaseAdapter extends AndroidTestCase
{
  /**
   * The maximum database size for all not size base tests
   */
  private final static long dbMaxDefaultSize = 1024 * 1024;
  
  /**
   * The test database name
   */
  public final static String testDBName = "TestDatabase";
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    getContext().deleteDatabase( testDBName );
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    getContext().deleteDatabase( testDBName );
    super.tearDown();
  }
  
  /**
   * Test method for creation, open, close and initial record count
   */
  public final void testDatabaseAdapter()
  {
    try
    {
      new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception while creating database adapter" );
    }
    
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    
    try
    {
      dbAdapter.open();
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception in open database adapter" );
    }
    
    try
    {
      long count = dbAdapter.getRecordCount();
      assertEquals( "Expected empty database", 0, count );
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception while requesting record count " );
    }
    
    try
    {
      dbAdapter.close();
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception in open database adapter" );
    }
  }
  
  /**
   * Test method for sample insertion.
   */
  public final void testInsertSamples()
  {
    // create Test data to write to database
    Collection< DatabaseSample > sc = getCollectionWithTestData();
    
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    dbAdapter.open();
    
    try
    {
      dbAdapter.insertSamples( sc );
    }
    catch ( Exception e )
    {
      dbAdapter.close();
      fail( "Unexpected exception while trying to insert samples into database" );
    }
    
    long count = dbAdapter.getRecordCount();
    dbAdapter.close();
    assertEquals( "Unexpected record count in database", sc.size(), count );
    
    // try to read samples with another adapter instance
    dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    dbAdapter.open();
    count = dbAdapter.getRecordCount();
    dbAdapter.close();
    assertEquals( "Unexpected record count in database", sc.size(), count );
    
    // test erase of all records
    dbAdapter.open();
    
    boolean success = false;
    try
    {
      success = dbAdapter.deleteAll();
      count = dbAdapter.getRecordCount();
      dbAdapter.close();
    }
    catch ( Exception e )
    {
      dbAdapter.close();
      fail( "Unexpected exception while trying to insert samples into database" );
    }
    
    assertEquals( "Expected empty database", 0, count );
    assertTrue( "Failed to delete all records", success );
  }
  
  /**
   * Simple test method for sample insertion and removal
   */
  public final void testInsertAndRemove()
  {
    Collection< DatabaseSample > sc = new Vector< DatabaseSample >();
    
    Sample sample =
        new Sample( SensorDeviceIdentifier.GPS, System.currentTimeMillis(),
            SensorDevicePriorities.Level0.ordinal(), true );
    sample.setData( TestGPSSampleData.createInitializedGPSSampleData() );
    GeoLocation location = new GeoLocation();
    location.setLat( 180. );
    location.setLon( -27. );
    sample.setLocation( location );
    try
    {
      sc.add( new DatabaseSample( sample ) );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during creation of database samples" );
    }
    
    // insert the sample
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    assertEquals( "Expected sample added", 1, insertSamples( sc, dbAdapter ) );
    
    // remove the sample and test for same content
    sc.clear();
    dbAdapter.open();
    boolean success = dbAdapter.removeSamplesHighestPrioFirst( 1, sc );
    long currentCount = dbAdapter.getRecordCount();
    dbAdapter.close();
    assertTrue( "Failed to remove sample from database", success );
    assertEquals( "Unexpected record count in database", 0, currentCount );
    assertEquals( "Unexpected removed sample count", 1, sc.size() );
    
    DatabaseSample dbSample = sc.iterator().next();
    assertEquals( "Expected same content in removed sample", sample,
        dbSample.toSample() );
  }
  
  /**
   * Test method for sample insertion and removal by oldest time stamp.
   */
  public final void testRemoveSamples()
  {
    int limit = 3;
    
    // create Test data to write to database
    Collection< DatabaseSample > sc = getCollectionWithTestData();
    
    for ( DatabaseSample sample : sc )
    {
      int prio =
          (int) ( Math.random() * SensorDevicePriorities.values().length );
      sample.priority = prio;
    }
    
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    long count = insertSamples( sc, dbAdapter );
    
    // tests successive removal of records
    Collection< DatabaseSample > removedSamples =
        new Vector< DatabaseSample >();
    int removedSampleCount = 0;
    while ( count > 0 )
    {
      try
      {
        dbAdapter.open();
        boolean success =
            dbAdapter.removeSamplesOldestTimeStampFirst( limit, removedSamples );
        long currentCount = dbAdapter.getRecordCount();
        dbAdapter.close();
        
        removedSampleCount += Math.min( limit, count );
        count = Math.max( 0, count - limit );
        
        assertTrue( "Failed to remove " + limit + " records from database",
            success );
        assertEquals( "Unexpected record count in database", count,
            currentCount );
        assertEquals( "Unexpected sample count removed", removedSamples.size(),
            removedSampleCount );
      }
      catch ( Exception e )
      {
        dbAdapter.close();
        fail( "Unexpected exception while trying to remove samples from database" );
      }
    }
    
    // test final states
    dbAdapter.open();
    count = dbAdapter.getRecordCount();
    dbAdapter.close();
    
    assertEquals( "Unexpected record count in database", 0, count );
    assertEquals( "Unexpected sample count removed from db", sc.size(),
        removedSampleCount );
    
    // test if samples have been removed in descending priority and ascending
    // time stamp order
    DatabaseSample lastSample = null;
    for ( DatabaseSample sample : removedSamples )
    {
      if ( lastSample != null )
      {
        if ( lastSample.priority == sample.priority )
        {
          // tests ascending order by time stamp for samples with same priority
          assertTrue(
              "Expected current sample timestamp " + sample.timeStamp
                  + " more than last sample timestamp "
                  + lastSample.timeStamp,
              lastSample.timeStamp <= sample.timeStamp );
        }
      }
      lastSample = sample;
    }
  }
  
  /**
   * Test method for sample insertion and removal by lowest priority.
   */
  public final void testRemoveSamplesByLowestPriority()
  {
    int limit = 3;
    
    // create Test data to write to database
    Collection< DatabaseSample > sc = getCollectionWithTestData();
    
    for ( DatabaseSample sample : sc )
    {
      int prio =
          (int) ( Math.random() * SensorDevicePriorities.values().length );
      sample.priority = prio;
    }
    
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    long count = insertSamples( sc, dbAdapter );
    
    // tests successive removal of records
    Collection< DatabaseSample > removedSamples =
        new Vector< DatabaseSample >();
    int removedSampleCount = 0;
    while ( count > 0 )
    {
      try
      {
        dbAdapter.open();
        boolean success =
            dbAdapter.removeSamplesLowestPrioFirst( limit, removedSamples );
        long currentCount = dbAdapter.getRecordCount();
        dbAdapter.close();
        
        removedSampleCount += Math.min( limit, count );
        count = Math.max( 0, count - limit );
        
        assertTrue( "Failed to remove " + limit + " records from database",
            success );
        assertEquals( "Unexpected record count in database", count,
            currentCount );
        assertEquals( "Unexpected sample count removed", removedSamples.size(),
            removedSampleCount );
      }
      catch ( Exception e )
      {
        dbAdapter.close();
        fail( "Unexpected exception while trying to remove samples from database" );
      }
    }
    
    // test final states
    dbAdapter.open();
    count = dbAdapter.getRecordCount();
    dbAdapter.close();
    
    assertEquals( "Unexpected record count in database", 0, count );
    assertEquals( "Unexpected sample count removed from db", sc.size(),
        removedSampleCount );
    
    // test if samples have been removed in descending priority and ascending
    // time stamp order
    DatabaseSample lastSample = null;
    for ( DatabaseSample sample : removedSamples )
    {
      if ( lastSample != null )
      {
        // tests ascending sort by priority
        assertTrue(
            "Expected priority level of current sample less than last sample",
            lastSample.priority >= sample.priority );
        
        if ( lastSample.priority == sample.priority )
        {
          // tests ascending order by time stamp for samples with same priority
          assertTrue(
              "Expected current sample timestamp " + sample.timeStamp
                  + " more than last sample timestamp "
                  + lastSample.timeStamp,
              lastSample.timeStamp <= sample.timeStamp );
        }
      }
      lastSample = sample;
    }
  }
  
  /**
   * Test method for sample insertion and removal by highest priority.
   */
  public final void testRemoveSamplesByHighestPriority()
  {
    int limit = 3;
    
    // create Test data to write to database
    Collection< DatabaseSample > sc = getCollectionWithTestData();
    
    for ( DatabaseSample sample : sc )
    {
      int prio =
          (int) ( Math.random() * SensorDevicePriorities.values().length );
      sample.priority = prio;
    }
    
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    long count = insertSamples( sc, dbAdapter );
    
    // tests successive removal of records
    Collection< DatabaseSample > removedSamples =
        new Vector< DatabaseSample >();
    int removedSampleCount = 0;
    while ( count > 0 )
    {
      try
      {
        dbAdapter.open();
        boolean success =
            dbAdapter.removeSamplesHighestPrioFirst( limit, removedSamples );
        long currentCount = dbAdapter.getRecordCount();
        dbAdapter.close();
        
        removedSampleCount += Math.min( limit, count );
        count = Math.max( 0, count - limit );
        
        assertTrue( "Failed to remove " + limit + " records from database",
            success );
        assertEquals( "Unexpected record count in database", count,
            currentCount );
        assertEquals( "Unexpected sample count removed", removedSamples.size(),
            removedSampleCount );
      }
      catch ( Exception e )
      {
        dbAdapter.close();
        fail( "Unexpected exception while trying to remove samples from database" );
      }
    }
    
    // test final states
    dbAdapter.open();
    count = dbAdapter.getRecordCount();
    dbAdapter.close();
    
    assertEquals( "Unexpected record count in database", 0, count );
    assertEquals( "Unexpected sample count removed from db", sc.size(),
        removedSampleCount );
    
    // test if samples have been removed in ascending priority and ascending
    // time stamp order
    DatabaseSample lastSample = null;
    for ( DatabaseSample sample : removedSamples )
    {
      if ( lastSample != null )
      {
        // tests ascending sort by priority
        assertTrue(
            "Expected priority level of current sample more than last sample",
            lastSample.priority <= sample.priority );
        
        if ( lastSample.priority == sample.priority )
        {
          // tests ascending order by time stamp for samples with same priority
          assertTrue(
              "Expected current sample timestamp " + sample.timeStamp
                  + " more than last sample timestamp "
                  + lastSample.timeStamp,
              lastSample.timeStamp <= sample.timeStamp );
        }
      }
      lastSample = sample;
    }
  }
  
  /**
   * Test method for sample deletion by oldest time stamp
   */
  public final void testDeleteSamplesWithOldestTimestamp()
  {
    // create Test data to write to database
    Collection< DatabaseSample > sc = getCollectionWithTestData();
    
    for ( DatabaseSample sample : sc )
    {
      int prio =
          (int) ( Math.random() * SensorDevicePriorities.values().length );
      sample.priority = prio;
    }
    
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    insertSamples( sc, dbAdapter );
    
    // tests deletion of oldest records
    try
    {
      dbAdapter.open();
      long recordCountBefore = dbAdapter.getRecordCount();
      long countToDelete = recordCountBefore / 2;
      long removedSampleCount =
          dbAdapter.deleteSamplesOrdered( countToDelete, false );
      long recordCountAfter = dbAdapter.getRecordCount();
      dbAdapter.close();
      
      assertEquals( "Unexpected return value for delete records",
          countToDelete,
          removedSampleCount );
      assertEquals( "Unexpected count of deleted records", countToDelete,
          recordCountBefore - recordCountAfter );
      
      Collection< DatabaseSample > remainingSamples =
          new Vector< DatabaseSample >();
      dbAdapter.open();
      dbAdapter.removeSamplesOldestTimeStampFirst( recordCountAfter,
          remainingSamples );
      dbAdapter.close();
      
      List< DatabaseSample > originalSamples =
          new ArrayList< DatabaseSample >( sc );
      Comparator< DatabaseSample > comparator =
          new Comparator< DatabaseSample >()
                {
                  @Override
                  public int compare( DatabaseSample o1, DatabaseSample o2 )
                  {
                    return (int) ( o1.timeStamp - o2.timeStamp );
                  }
                };
      Collections.sort( originalSamples, comparator );
      
      for ( int i = 0; i < removedSampleCount; ++i )
      {
        DatabaseSample sample = originalSamples.get( i );
        assertFalse(
            "Expected oldest original samples not in collection of remaing samples",
            remainingSamples.contains( sample ) );
      }
    }
    catch ( Exception e )
    {
      dbAdapter.close();
      fail( "Unexpected exception while trying to delete samples from database" );
    }
  }
  
  /**
   * Test method for sample deletion by lowest priority first
   */
  public final void testDeleteSamplesLowestPrioFirst()
  {
    // create Test data to write to database
    Collection< DatabaseSample > sc = getCollectionWithTestData();
    
    for ( DatabaseSample sample : sc )
    {
      int prio =
          (int) ( Math.random() * SensorDevicePriorities.values().length );
      sample.priority = prio;
    }
    
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    insertSamples( sc, dbAdapter );
    
    // tests deletion of oldest records
    try
    {
      dbAdapter.open();
      long recordCountBefore = dbAdapter.getRecordCount();
      long countToDelete = recordCountBefore / 2;
      long removedSampleCount =
          dbAdapter.deleteSamplesOrdered( countToDelete, true );
      long recordCountAfter = dbAdapter.getRecordCount();
      dbAdapter.close();
      
      assertEquals( "Unexpected return value for delete records",
          countToDelete,
          removedSampleCount );
      assertEquals( "Unexpected count of deleted records", countToDelete,
          recordCountBefore - recordCountAfter );
      
      Collection< DatabaseSample > remainingSamples =
          new Vector< DatabaseSample >();
      dbAdapter.open();
      dbAdapter.removeSamplesOldestTimeStampFirst( recordCountAfter,
          remainingSamples );
      dbAdapter.close();
      
      List< DatabaseSample > originalSamples =
          new ArrayList< DatabaseSample >( sc );
      Comparator< DatabaseSample > comparator =
          new Comparator< DatabaseSample >()
                {
                  @Override
                  public int compare( DatabaseSample o1, DatabaseSample o2 )
                  {
                    int result = o2.priority - o1.priority;
                    if ( result == 0 )
                    {
                      result = (int) ( o1.timeStamp - o2.timeStamp );
                    }
                    return result;
                  }
                };
      Collections.sort( originalSamples, comparator );
      
      for ( int i = 0; i < removedSampleCount; ++i )
      {
        DatabaseSample sample = originalSamples.get( i );
        assertFalse(
            "Expected oldest original samples with lowest priority deleted",
            remainingSamples.contains( sample ) );
      }
    }
    catch ( Exception e )
    {
      dbAdapter.close();
      fail( "Unexpected exception while trying to delete samples from database" );
    }
  }
  
  /**
   * Test method for sample insertion and removal by highest priority.
   */
  public final void testSetMaximumDatabaseSize()
  {
    getContext().deleteDatabase( testDBName );
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    dbAdapter.open();
    
    // test initial maximum size
    assertEquals( "Expected new maximum size set", dbMaxDefaultSize,
        dbAdapter.getMaximumDatabaseSize() );
    
    // change maximum size
    long size = dbAdapter.getPageSize() * 10;
    
    assertEquals( "Expected size update successful", size,
        dbAdapter.setMaximumDatabaseSize( size ) );
    
    assertEquals( "Expected new maximum size set", size,
        dbAdapter.getMaximumDatabaseSize() );
    
    dbAdapter.close();
    
    // tests for new instance
    dbAdapter.open();
    
    assertEquals( "Expected new maximum size set", size,
        dbAdapter.getMaximumDatabaseSize() );
    
    dbAdapter.close();
  }
  
  /**
   * Test method for sample insertion and removal by highest priority.
   */
  public final void testMaximumDatabaseSizeExceeded()
  {
    getContext().deleteDatabase( testDBName );
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( testDBName, dbMaxDefaultSize, getContext() );
    dbAdapter.open();
    
    // limit size
    long maxSize = dbAdapter.getPageSize() * 10;
    assertEquals( "Expected new maximum size set", maxSize,
        dbAdapter.setMaximumDatabaseSize( maxSize ) );
    
    // create Test data to write to database
    Collection< DatabaseSample > sc = new Vector< DatabaseSample >();
    try
    {
      for ( int i = 0; i < 200; ++i )
      {
        sc.add( new DatabaseSample( TestSampleCollection.createSample(
            SensorDeviceIdentifier.GSM,
            TestGSMSampleData.createInitializedGSMSampleData() ) ) );
      }
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception" );
    }
    assertEquals( "Unexpected size of sample collection", 200, sc.size() );
    
    // write to DB to trigger size exceeded exception
    try
    {
      // insert test data
      dbAdapter.insertSamples( sc );
      assertEquals( "Expected " + sc.size() + " records added", sc.size(),
          dbAdapter.getRecordCount() );
      fail( "Expected exception due to DB size limit exceeded" );
    }
    catch ( Exception e )
    {}
    finally
    {
      dbAdapter.close();
    }
  }
  
  /**
   * Method to insert a sample collection into the given database
   * 
   * @param sampleCollection
   *          the sample collection to insert into database
   * @param dbAdapter
   *          the database adapter
   * @return the current record count in database
   */
  public static long insertSamples(
      Collection< DatabaseSample > sampleCollection,
      DatabaseAdapterImpl dbAdapter )
  {
    dbAdapter.open();
    // insert test data and store record count
    try
    {
      dbAdapter.insertSamples( sampleCollection );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during sample insertion" );
    }
    long count = dbAdapter.getRecordCount();
    dbAdapter.close();
    return count;
  }
  
  /**
   * Does fill a sample collection with database sample test data
   * 
   * @return a database sample collection
   */
  public static final Collection< DatabaseSample > getCollectionWithTestData()
  {
    Collection< DatabaseSample > sampleCollection =
        new Vector< DatabaseSample >();
    
    try
    {
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample(
              SensorDeviceIdentifier.Accelerometer,
              new AccelerometerSampleData() ) ) );
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample( SensorDeviceIdentifier.Wifi,
              new WifiSampleData() ) ) );
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample( SensorDeviceIdentifier.Bluetooth,
              new BluetoothSampleData() ) ) );
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample( SensorDeviceIdentifier.GSM,
              new GSMSampleData() ) ) );
      GPSSampleData sampleData = new GPSSampleData();
      sampleData.setLatitude( 77.7 );
      sampleData.setLongitude( 12.3 );
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample( SensorDeviceIdentifier.GPS,
              sampleData ) ) );
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample( SensorDeviceIdentifier.Audio,
              new FileReferenceSampleData() ) ) );
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample( SensorDeviceIdentifier.Twitter,
              new TwitterSampleData() ) ) );
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample(
              SensorDeviceIdentifier.Accelerometer,
              TestAccelerometerSampleData.createInitializedAccelerometerSampleData() ) ) );
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample( SensorDeviceIdentifier.Wifi,
              TestWifiSampleData.createInitializedWifiSampleData() ) ) );
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample( SensorDeviceIdentifier.Bluetooth,
              TestBluetoothSampleData.createInitializedBluetoothSampleData() ) ) );
      sampleCollection.add( new DatabaseSample(
          TestSampleCollection.createSample( SensorDeviceIdentifier.GSM,
              TestGSMSampleData.createInitializedGSMSampleData() ) ) );
      GeoLocation loc = new GeoLocation();
      loc.setLat( Math.random() * 180 );
      loc.setLon( Math.random() * 180 );
      DatabaseSample databaseSample =
          new DatabaseSample( TestSampleCollection.createSample(
              SensorDeviceIdentifier.GSM,
              TestGSMSampleData.createInitializedGSMSampleData() ) );
      databaseSample.location = loc.toXML();
      sampleCollection.add( databaseSample );
      
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during creation of database samples" );
    }
    return sampleCollection;
  }
  
  /**
   * Does create a collection with a specific amount of test database samples
   * 
   * @param count
   *          the count of samples to create
   * @return the created test data
   */
  public static Collection< DatabaseSample > createTestData( long count )
  {
    Collection< DatabaseSample > sc = new Vector< DatabaseSample >();
    
    try
    {
      for ( long i = 0; i < count; ++i )
      {
        GeoLocation loc = new GeoLocation();
        loc.setLat( Math.random() * 180 );
        loc.setLon( Math.random() * 180 );
        DatabaseSample databaseSample =
            new DatabaseSample( TestSampleCollection.createSample(
                SensorDeviceIdentifier.GSM,
                TestGSMSampleData.createInitializedGSMSampleData() ) );
        databaseSample.location = loc.toXML();
        sc.add( databaseSample );
      }
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during creation of database samples" );
    }
    return sc;
  }
  
}
