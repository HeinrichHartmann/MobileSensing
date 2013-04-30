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

import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.persistence.AbstractDatabaseCommand;
import de.unikassel.android.sdcframework.persistence.DatabaseAdapterImpl;
import de.unikassel.android.sdcframework.persistence.DeleteSamplesCommand;
import de.unikassel.android.sdcframework.persistence.GetRecordCountCommand;
import de.unikassel.android.sdcframework.persistence.InsertSamplesCommand;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseSample;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.database.sqlite.SQLiteFullException;
import android.test.AndroidTestCase;

/**
 * Tests for the insert samples database command
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestDeleteSamplesCommand extends AndroidTestCase
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
   * Test method for construction.
   */
  public final void testDeleteSamplesCommand()
  {
    int deletionCount = 10;
    boolean lowestPrioFirst = true;
    DeleteSamplesCommand command =
        new DeleteSamplesCommand( deletionCount, lowestPrioFirst );
    
    assertNull( "Expected result null after creation", command.getResult() );
    assertEquals( "Expected db open retry count is default",
        AbstractDatabaseCommand.DEFAULT_DB_OPEN_RETRY_COUNT << 1,
        command.getDbOpenRetryCount() );
    assertFalse( "Expected opend database readonly flag not set",
        command.isOpenReadOnly() );
    assertEquals( "Expected deletion count set", deletionCount,
        command.getCount() );
    assertEquals( "Expected delete lowest priority fisrts flag set",
        lowestPrioFirst, command.isLowestPriorityFirst() );
    
    deletionCount <<= 2;
    lowestPrioFirst = !lowestPrioFirst;
    command = new DeleteSamplesCommand( deletionCount, lowestPrioFirst );
    
    assertNull( "Expected result null after creation", command.getResult() );
    assertTrue( "Expected db open retry count more or equal 5",
        command.getDbOpenRetryCount() >= 5 );
    assertFalse( "Expected opend database readonly flag not set",
        command.isOpenReadOnly() );
    assertEquals( "Expected deletion count set", deletionCount,
        command.getCount() );
    assertEquals( "Expected delete lowest priority fisrts flag set",
        lowestPrioFirst, command.isLowestPriorityFirst() );
  }
  
  /**
   * Test method for command execution .
   */
  public final void testExecution()
  {
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( TestDatabaseAdapter.testDBName, Integer.MAX_VALUE,
            getContext() );
    
    Collection< DatabaseSample > sc = TestDatabaseAdapter.getCollectionWithTestData();
    
    for ( DatabaseSample sample : sc )
    {
      int prio =
          (int) ( Math.random() * SensorDevicePriorities.values().length );
      sample.priority = prio;
    }
    
    // insert test data into the database
    InsertSamplesCommand insertCommand = new InsertSamplesCommand( sc );
    
    try
    {
      insertCommand.execute( dbAdapter );
    }
    catch ( SQLiteFullException e )
    {
      fail( "Unexpected full exception" );
    }
    
    // tests deletion of oldest records
    long deletionCount = sc.size() >> 1;
    DeleteSamplesCommand command =
        new DeleteSamplesCommand( deletionCount, false );
    
    try
    {
      command.execute( dbAdapter );
    }
    catch ( SQLiteFullException e )
    {
      fail( "Unexpected full exception" );
    }
    assertNotNull( "Expected command execution result set", command.getResult() );
    assertEquals( "Unexpected deleted record count",
        Long.valueOf( deletionCount ), command.getResult() );
    
    // sort original samples by time stamp in ascending order ( oldest first )
    List< DatabaseSample > originalSamples = new ArrayList< DatabaseSample >( sc );
    Comparator< DatabaseSample > comparatorTimeStamp = new Comparator< DatabaseSample >()
    {
      @Override
      public int compare( DatabaseSample o1, DatabaseSample o2 )
      {
        return (int) ( o1.timeStamp - o2.timeStamp );
      }
    };
    Collections.sort( originalSamples, comparatorTimeStamp );
    
    // get remaining samples from database
    Collection< DatabaseSample > remainingSamples = new Vector< DatabaseSample >();
    dbAdapter.open();
    try
    {
      dbAdapter.removeSamplesOldestTimeStampFirst( sc.size() - deletionCount,
          remainingSamples );
    }
    catch ( Exception e )
    {
      fail( "Unexpected full exception" );
    }
    finally
    {
      dbAdapter.close();
    }
    
    // test if the right samples have been deleted
    for ( int i = 0; i < deletionCount; ++i )
    {
      DatabaseSample sample = originalSamples.get( i );
      assertFalse(
          "Expected oldest original samples not in collection of remaing samples",
          remainingSamples.contains( sample ) );
    }
    
    GetRecordCountCommand getRCCommand = new GetRecordCountCommand();
    try
    {
      getRCCommand.execute( dbAdapter );
      assertTrue( "Expected database empty now", getRCCommand.getResult() == 0L );
    }
    catch ( SQLiteFullException e )
    {
      fail( "Unexpected full exception" );
    }
    
    // insert same test data into the database again
    try
    {
      insertCommand.execute( dbAdapter );
    }
    catch ( SQLiteFullException e )
    {
      fail( "Unexpected full exception" );
    }
    
    // tests deletion of oldest records with lowest priority first
    command = new DeleteSamplesCommand( deletionCount, true );
    try
    {
      command.execute( dbAdapter );
    }
    catch ( SQLiteFullException e )
    {
      fail( "Unexpected full exception" );
    }
    assertNotNull( "Expected command execution result set", command.getResult() );
    assertEquals( "Unexpected deleted record count",
        Long.valueOf( deletionCount ), command.getResult() );
    
    // sort original samples by time stamp and lowest priority in ascending
    // order ( oldest with lowest prio first )
    Comparator< DatabaseSample > comparatorPrioAndTimeStamp =
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
    Collections.sort( originalSamples, comparatorPrioAndTimeStamp );
    
    // get remaining samples from database
    remainingSamples.clear();
    dbAdapter.open();
    try
    {
      dbAdapter.removeSamplesOldestTimeStampFirst( sc.size() - deletionCount,
          remainingSamples );
    }
    catch ( Exception e )
    {
      fail( "Unexpected full exception" );
    }
    finally
    {
      dbAdapter.close();
    }
    
    // test if the right samples have been deleted
    for ( int i = 0; i < deletionCount; ++i )
    {
      DatabaseSample sample = originalSamples.get( i );
      assertFalse(
          "Expected oldest original samples with lowest priority deleted",
          remainingSamples.contains( sample ) );
    }
  }
  
}
