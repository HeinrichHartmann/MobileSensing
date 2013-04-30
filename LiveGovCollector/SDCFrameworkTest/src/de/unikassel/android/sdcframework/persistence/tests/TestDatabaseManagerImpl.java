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

import java.util.Collection;

import de.unikassel.android.sdcframework.persistence.DatabaseAdapterImpl;
import de.unikassel.android.sdcframework.persistence.DatabaseManagerImpl;
import de.unikassel.android.sdcframework.persistence.GetMaximumDatabaseSizeCommand;
import de.unikassel.android.sdcframework.persistence.InsertSamplesCommand;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseManager;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseSample;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.content.Context;
import android.database.sqlite.SQLiteFullException;
import android.test.AndroidTestCase;

/**
 * Test for the database manager implementation
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestDatabaseManagerImpl extends AndroidTestCase
{
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    getContext().deleteDatabase( TestDatabaseAdapter.testDBName );
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    getContext().deleteDatabase( TestDatabaseAdapter.testDBName );
    super.tearDown();
  }
  
  /**
   * Test method for Construction.
   */
  public final void testDatabaseManagerImpl()
  {
    
    try
    {
      new DatabaseManagerImpl( null, TestDatabaseAdapter.testDBName );
      fail( "Expected Exception due to context is null" );
    }
    catch ( Exception e )
    {}
    
    try
    {
      new DatabaseManagerImpl( getContext(), null );
      fail( "Expected IllegalArgumentException due to database name is null" );
    }
    catch ( Exception e )
    {}
    
    try
    {
      DatabaseManager manager =
          new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
      assertEquals( "Unexpected sample count in database", 0L,
          manager.getRecordCountInDatabase() );
      assertTrue( "Unexpected maximum database size",
          manager.getMaximumDatabaseSize() > 0L );
      long newSize = 10L;
      assertEquals( "Unexpected maximum database size set",
          newSize, manager.setMaximumDatabaseSize( newSize ) );
      assertEquals( "Unexpected maximum database size",
          newSize, manager.getMaximumDatabaseSize() );
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception from constructor" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.persistence.DatabaseManagerImpl#getRecordCountInDatabase()}
   * .
   */
  public final void testGetRecordCountInDatabase()
  {
    DatabaseManager manager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    
    // insert samples
    long cntInserted =
        TestDatabaseManagerImpl.insertSamplesIntoDatabase( getContext(), 100,
            0L );
    
    assertEquals( "Unexpected sample count in database", cntInserted,
        manager.getRecordCountInDatabase() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.persistence.DatabaseManagerImpl#setMaximumDatabaseSize(long)}
   * .
   */
  public final void testSetMaximumDatabaseSize()
  {
    DatabaseManagerImpl manager = new DatabaseManagerImpl( getContext(),
            TestDatabaseAdapter.testDBName );
    
    // test if set maximum size to 5 kilobytes succeeds
    Long newMaxSize = 10L;
    manager.setMaximumDatabaseSize( newMaxSize );
    
    // test if new maximum size value was set
    GetMaximumDatabaseSizeCommand getMaxSizeCommand =
        new GetMaximumDatabaseSizeCommand();
    try
    {
      getMaxSizeCommand.execute( manager.getDbAdapter() );
      // test for same size
      Long size = getMaxSizeCommand.getResult() >> 10;
      assertEquals( "Unexpected result", newMaxSize, size );
    }
    catch ( SQLiteFullException e )
    {
      fail( "Unexpected full exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.persistence.DatabaseManagerImpl#getMaximumDatabaseSize()}
   * .
   */
  public final void testGetMaximumDatabaseSize()
  {
    DatabaseManagerImpl manager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    GetMaximumDatabaseSizeCommand getMaxSizeCommand =
        new GetMaximumDatabaseSizeCommand();
    try
    {
      getMaxSizeCommand.execute( manager.getDbAdapter() );
      assertEquals( "Unexpected result",
          getMaxSizeCommand.getResult().longValue() >> 10,
          manager.getMaximumDatabaseSize() );
    }
    catch ( SQLiteFullException e )
    {
      fail( "Unexpected full exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.persistence.DatabaseManagerImpl#doDeleteOldestSamplesInDatabase(long, boolean)}
   * .
   */
  public final void testDoDeleteOldestSamplesInDatabase()
  {
    DatabaseManager manager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    
    // insert samples
    long cntSamples = 100;
    long cntInserted =
        TestDatabaseManagerImpl.insertSamplesIntoDatabase( getContext(),
            cntSamples, 0L );
    
    assertEquals( "Expected all samples inserted", cntSamples, cntInserted );
    
    assertEquals( "Unexpected sample count in database", cntInserted,
        manager.getRecordCountInDatabase() );
    
    // tests deletion of samples
    long cntDeleted =
        manager.doDeleteOldestSamplesInDatabase( cntInserted >> 2, false );
    assertEquals( "Unexpected sample count deleted", cntInserted >> 2,
        cntDeleted );
    
    long cntRemaining = cntInserted - cntDeleted;
    assertEquals( "Unexpected sample count in database", cntRemaining,
        manager.getRecordCountInDatabase() );
    
    cntDeleted = manager.doDeleteOldestSamplesInDatabase( cntInserted, false );
    assertEquals( "Unexpected sample count deleted", cntRemaining, cntDeleted );
    assertEquals( "Unexpected sample count in database", 0L,
        manager.getRecordCountInDatabase() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.persistence.DatabaseManagerImpl#doExecuteCommand(de.unikassel.android.sdcframework.persistence.facade.DatabaseCommand)}
   * .
   */
  public final void testDoExecuteCommand()
  {
    DatabaseManager manager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    
    long cnt = manager.getRecordCountInDatabase();
    
    Collection< DatabaseSample > samples =
        TestDatabaseAdapter.createTestData( 50 );
    InsertSamplesCommand command = new InsertSamplesCommand( samples );
    
    Boolean resultExpected = true;
    Boolean result = manager.doExecuteCommand( command );
    assertEquals( "Unexpected result", resultExpected, result );
    assertEquals( "Expected samples inserted", cnt + samples.size(),
        manager.getRecordCountInDatabase() );
  }
  
  /**
   * Method to insert samples into the database
   * 
   * @param context
   *          the application context
   * @param cntSamples
   *          the count of samples to insert into database
   * @param dbMaxDefaultSize
   *          the maximum database size to use
   * @return the count of samples which have been inserted
   */
  public static long insertSamplesIntoDatabase( Context context,
      long cntSamples, long dbMaxDefaultSize )
  {
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( TestDatabaseAdapter.testDBName, dbMaxDefaultSize,
            context );
    return TestDatabaseAdapter.insertSamples(
        TestDatabaseAdapter.createTestData( cntSamples ),
        dbAdapter );
  }
  
}
