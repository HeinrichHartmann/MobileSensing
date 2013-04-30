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

import de.unikassel.android.sdcframework.persistence.AbstractDatabaseCommand;
import de.unikassel.android.sdcframework.persistence.DatabaseAdapterImpl;
import de.unikassel.android.sdcframework.persistence.GetMaximumDatabaseSizeCommand;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.database.sqlite.SQLiteFullException;
import android.test.AndroidTestCase;

/**
 * Tests for the get maximum database size command.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestGetMaximumDatabaseSizeCommand extends AndroidTestCase
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
  public final void testGetMaximumDatabaseSizeCommand()
  {
    GetMaximumDatabaseSizeCommand command = new GetMaximumDatabaseSizeCommand();
    
    assertNull( "Expected result null after creation", command.getResult() );
    assertEquals( "Expected db open retry count is default",
        AbstractDatabaseCommand.DEFAULT_DB_OPEN_RETRY_COUNT,
        command.getDbOpenRetryCount() );
    assertTrue( "Expected opend database readonly flag set", command.isOpenReadOnly() );
  }
  
  /**
   * Test method for command execution
   * .
   */
  public final void testExecution()
  {
    DatabaseAdapterImpl dbAdapter =
      new DatabaseAdapterImpl( TestDatabaseAdapter.testDBName, Integer.MAX_VALUE,
          getContext() );
    
    // get reference size to test for using already tested class dbAdapter
    dbAdapter.open();
    Long maxSize = dbAdapter.getMaximumDatabaseSize();
    dbAdapter.close();
  
    GetMaximumDatabaseSizeCommand command = new GetMaximumDatabaseSizeCommand();
    try
    {
      command.execute( dbAdapter );
      assertNotNull( "Expected command execution result set", command.getResult() );
      assertEquals( "Unexpected result", maxSize, command.getResult() );
    }
    catch ( SQLiteFullException e )
    {
      fail( "Unexpected full exception" );
    }
  }
  
}
