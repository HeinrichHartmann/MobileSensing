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
package de.unikassel.android.sdcframework.provider.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;

import de.unikassel.android.sdcframework.provider.AbstractProvider;
import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;
import de.unikassel.android.sdcframework.util.TimeProvider;

/**
 * Test for the abstract provider.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAbstractProvider
    extends ProviderTestCase2< AbstractProviderForTest >
{
  /**
   * Allowed difference between the taken time provider reference time stamp and
   * the one automatically set by the content provider insert method.
   */
  public static final int ALLOWED_TIME_STAMP_DIFF = 100;
  
  /**
   * Constructor
   */
  public TestAbstractProvider()
  {
    super( AbstractProviderForTest.class,
        new ContentProviderDataForTest().getAuthority() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.ProviderTestCase2#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
    emptyDatabase();
  }
  
  /**
   * Method to empty the database
   */
  private void emptyDatabase()
  {
    AbstractProviderForTest provider = getProvider();
    provider.getDbHelper().getWritableDatabase().delete(
        provider.getProviderData().getDBTableName(), null, null );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  public void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.AbstractProvider#AbstractProvider(de.unikassel.android.sdcframework.provider.facade.ContentProviderData)}
   * .
   */
  public final void testAbstractProvider()
  {
    AbstractProviderForTest provider = getProvider();
    ContentProviderData providerData = provider.getProviderData();
    
    try
    {
      assertEquals( "Unexpected type", providerData.getContentType(),
          provider.getType( providerData.getContentUri() ) );
      assertEquals( "Unexpected type", providerData.getContentItemType(),
          provider.getType( Uri.withAppendedPath(
              providerData.getContentUri(), "/1" ) ) );
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception accessing content URI" );
    }
  }
  
  /**
   * Test method for query
   */
  public final void testQuery()
  {
    AbstractProvider provider = getProvider();
    ContentProviderData providerData = provider.getProviderData();
    
    try
    {
      Uri uri = providerData.getContentUri();
      
      Cursor cursor = provider.query( uri, null, null, null, null );
      assertNotNull( "Expected provider queryable", cursor );
      assertEquals( "Unexpected row count", 0, cursor.getCount() );
      assertEquals( "Unexpected column count", 3, cursor.getColumnCount() );
      cursor.close();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
  }
  
  /**
   * Test method for insert
   */
  public final void testInsertWithQuery()
  {
    AbstractProvider provider = getProvider();
    ContentProviderData providerData = provider.getProviderData();
    
    try
    {
      ContentValues values = new ContentValues();
      long ts = System.currentTimeMillis();
      String tsString = Long.toString( ts );
      values.put( ContentProviderData.TIMESTAMP, tsString );
      
      // time stamp is internally overridden by time provider time
      ts = TimeProvider.getInstance().getTimeStamp();
      Uri uri = provider.insert( providerData.getContentUri(), values );
      assertNotNull( "Insert failed unexpectedly", uri );
      
      Cursor cursor =
          provider.query( providerData.getContentUri(), null, null, null, null );
      assertEquals( "Unexpected record count", 1, cursor.getCount() );
      cursor.close();
      
      cursor = provider.query( uri, null, null, null, null );
      assertEquals( "Unexpected record count", 1, cursor.getCount() );
      assertEquals( "Unexpected column count", 3, cursor.getColumnCount() );
      
      cursor.moveToFirst();
      int columnIndex =
          cursor.getColumnIndex( providerData.getProjectionMap().get(
              ContentProviderData._ID ) );
      assertTrue( "Expected valid index for id", columnIndex != -1 );
      columnIndex =
          cursor.getColumnIndex( providerData.getProjectionMap().get(
              ContentProviderData.TIMESTAMP ) );
      assertTrue( "Expected valid index for timestamp", columnIndex != -1 );
      long tsFromDB = cursor.getLong( columnIndex );
      assertTrue( "Unexpected time stamp", tsFromDB - ts < ALLOWED_TIME_STAMP_DIFF );
      cursor.close();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
  }
  
  /**
   * Test method for query after insert and delete
   */
  public final void testInsertAndDeleteWithQuery()
  {
    AbstractProvider provider = getProvider();
    ContentProviderData providerData = provider.getProviderData();
    
    try
    {
      ContentValues values = new ContentValues();
      long ts = System.currentTimeMillis();
      String tsString = Long.toString( ts );
      values.put( ContentProviderData.TIMESTAMP, tsString );
      
      Uri uri = provider.insert( providerData.getContentUri(), values );
      assertNotNull( "Insert failed unexpectedly", uri );
      
      values = new ContentValues();
      values.put( ContentProviderData.TIMESTAMP,
          Long.toString( System.currentTimeMillis() ) );
      provider.insert( providerData.getContentUri(), values );
      assertNotNull( "Insert failed unexpectedly", uri );
      
      Cursor cursor =
          provider.query( providerData.getContentUri(), null, null, null, null );
      assertEquals( "Unexpected record count", 2, cursor.getCount() );
      cursor.close();
      
      cursor = provider.query( uri, null, null, null, null );
      assertEquals( "Unexpected record count", 1, cursor.getCount() );
      cursor.close();
      
      // test deletion of single records
      assertEquals( "Unexpected count of deleted records", 1, provider.delete(
          uri, null, null ) );
      cursor =
          provider.query( providerData.getContentUri(), null, null, null, null );
      assertEquals( "Unexpected record count", 1, cursor.getCount() );
      cursor.close();
      
      cursor = provider.query( uri, null, null, null, null );
      assertEquals( "Unexpected record count", 0, cursor.getCount() );
      cursor.close();
      
      // insert two more records ;
      assertNotNull( "Insert failed unexpectedly", provider.insert(
          providerData.getContentUri(), values ) );
      assertNotNull( "Insert failed unexpectedly", provider.insert(
          providerData.getContentUri(), values ) );
      
      // test deletion for all records
      cursor =
          provider.query( providerData.getContentUri(), null, null, null, null );
      assertEquals( "Unexpected record count", 3, cursor.getCount() );
      cursor.close();
      int cnt = provider.delete( providerData.getContentUri(), null, null );
      assertEquals( "Unexpected count of deleted records", 3, cnt );
      cursor =
          provider.query( providerData.getContentUri(), null, null, null, null );
      assertEquals( "Unexpected record count", 0, cursor.getCount() );
      cursor.close();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
  }
  
  /**
   * Test method for update
   */
  public final void testInsertAndUpdateWithQuery()
  {
    AbstractProvider provider = getProvider();
    ContentProviderData providerData = provider.getProviderData();
    
    try
    {
      ContentValues orgValues = new ContentValues();
      long tsOld = System.currentTimeMillis();
      orgValues.put( ContentProviderData.TIMESTAMP, Long.toString( tsOld ) );
      
      // time stamp is internally overridden by time provider time
      TimeProvider tp = TimeProvider.getInstance();
      tsOld = tp.getTimeStamp();
      Uri uri1 = provider.insert( providerData.getContentUri(), orgValues );
      assertNotNull( "Insert failed unexpectedly", uri1 );
      Uri uri2 = provider.insert( providerData.getContentUri(), orgValues );
      assertNotNull( "Insert failed unexpectedly", uri2 );
      
      // test single update
      ContentValues newValues = new ContentValues();
      long tsNew = System.currentTimeMillis() + 1;
      newValues.put( ContentProviderData.TIMESTAMP, Long.toString( tsNew ) );
      int cnt = provider.update( uri1, newValues, null, null );
      assertEquals( "Unexpected affected record count", 1, cnt );
      
      Cursor cursor = provider.query( uri1, null, null, null, null );
      assertEquals( "Unexpected record count", 1, cursor.getCount() );
      cursor.moveToFirst();
      int columnIndex =
          cursor.getColumnIndex( providerData.getProjectionMap().get(
              ContentProviderData.TIMESTAMP ) );
      assertTrue( "Expected valid index", columnIndex != -1 );
      long tsFromDB = cursor.getLong( columnIndex );
      assertEquals( "Unexpected time stamp", tsNew, tsFromDB );
      cursor.close();
      
      cursor = provider.query( uri2, null, null, null, null );
      assertEquals( "Unexpected record count", 1, cursor.getCount() );
      cursor.moveToFirst();
      columnIndex = cursor.getColumnIndex( providerData.getProjectionMap().get(
          ContentProviderData.TIMESTAMP ) );
      assertTrue( "Expected valid index", columnIndex != -1 );
      tsFromDB = cursor.getLong( columnIndex );
      assertTrue( "Unexpected time stamp",
          tsFromDB - tsOld < ALLOWED_TIME_STAMP_DIFF );
      cursor.close();
      
      // test multiple data
      cnt =
          provider.update( providerData.getContentUri(), newValues, null, null );
      assertEquals( "Unexpected affected record count", 2, cnt );
      
      cursor = provider.query( uri1, null, null, null, null );
      assertEquals( "Unexpected record count", 1, cursor.getCount() );
      cursor.moveToFirst();
      columnIndex = cursor.getColumnIndex( providerData.getProjectionMap().get(
          ContentProviderData.TIMESTAMP ) );
      assertTrue( "Expected valid index", columnIndex != -1 );
      tsFromDB = cursor.getLong( columnIndex );
      assertEquals( "Unexpected time stamp", tsNew, tsFromDB );
      cursor.close();
      
      cursor = provider.query( uri2, null, null, null, null );
      assertEquals( "Unexpected record count", 1, cursor.getCount() );
      cursor.moveToFirst();
      columnIndex = cursor.getColumnIndex( providerData.getProjectionMap().get(
          ContentProviderData.TIMESTAMP ) );
      assertTrue( "Expected valid index", columnIndex != -1 );
      tsFromDB = cursor.getLong( columnIndex );
      assertEquals( "Unexpected time stamp", tsNew, tsFromDB );
      cursor.close();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
  }
  
}
