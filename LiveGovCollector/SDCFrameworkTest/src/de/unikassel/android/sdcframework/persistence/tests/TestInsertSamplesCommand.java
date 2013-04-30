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

import de.unikassel.android.sdcframework.persistence.AbstractDatabaseCommand;
import de.unikassel.android.sdcframework.persistence.DatabaseAdapterImpl;
import de.unikassel.android.sdcframework.persistence.InsertSamplesCommand;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseSample;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.database.sqlite.SQLiteFullException;
import android.test.AndroidTestCase;

/**
 * Tests for the delete samples database command
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestInsertSamplesCommand extends AndroidTestCase
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
  public final void testInsertSamplesCommand()
  {
    Collection< DatabaseSample > sc = TestDatabaseAdapter.createTestData( 200 );
    InsertSamplesCommand command = new InsertSamplesCommand( sc );
    
    assertNull( "Expected result null after creation", command.getResult() );
    assertEquals( "Expected db open retry count is default",
        AbstractDatabaseCommand.DEFAULT_DB_OPEN_RETRY_COUNT,
        command.getDbOpenRetryCount() );
    assertFalse( "Expected opend database readonly flag not set",
        command.isOpenReadOnly() );
    assertSame( "Expected sample collection set", sc, command.getSamples() );
  }
  
  /**
   * Test method for command execution .
   */
  public final void testExecution()
  {
    Collection< DatabaseSample > sc = TestDatabaseAdapter.createTestData( 20 );
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( TestDatabaseAdapter.testDBName, Integer.MAX_VALUE,
            getContext() );
    InsertSamplesCommand command = new InsertSamplesCommand( sc );
    
    try
    {
      command.execute( dbAdapter );
      assertNotNull( "Expected command execution result set",
          command.getResult() );
      assertTrue( "Unexpected result", command.getResult() );
    }
    catch ( SQLiteFullException e )
    {
      fail( "Unexpected full exception" );
    }
  }
  
}
