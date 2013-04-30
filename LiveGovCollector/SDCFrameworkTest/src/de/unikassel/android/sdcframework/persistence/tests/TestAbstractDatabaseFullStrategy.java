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

import de.unikassel.android.sdcframework.persistence.AbstractDatabaseFullStrategy;
import de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager;
import junit.framework.TestCase;

/**
 * Test for the abstract base class for database fill strategies.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAbstractDatabaseFullStrategy extends TestCase
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
   * Test implementation of the abstract database full strategy
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class DatabaseFullStrategyForTest
      extends AbstractDatabaseFullStrategy
  {
    /**
     * Flag to signal execution
     */
    public boolean executed = false;
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.persistence.AbstractDatabaseFullStrategy
     * #execute(de.unikassel.android.sdcframework.persistence.facade.
     * PersistentStorageManager)
     */
    @Override
    protected boolean process( PersistentStorageManager storageManager )
    {
      executed = true;
      return super.process( storageManager );
    }
    
  }
  
  /**
   * Test chain execution
   */
  public void testChainExecution()
  {
    DatabaseFullStrategyForTest strategy0 = new DatabaseFullStrategyForTest();
    DatabaseFullStrategyForTest strategy1 = new DatabaseFullStrategyForTest();
    DatabaseFullStrategyForTest strategy2 = new DatabaseFullStrategyForTest();
    DatabaseFullStrategyForTest strategy3 = new DatabaseFullStrategyForTest();
    strategy0.withSuccessor( strategy1 ).withSuccessor( strategy2 ).withSuccessor(
        strategy3 );
    
    PersistentStorageManagerForTest dbManager =
        new PersistentStorageManagerForTest();
    
    // test with dbManager returning false
    dbManager.isProcessingCurrentSamples = false;
    dbManager.counter = 0;
    assertFalse( "Expected strategy not successful",
        strategy0.doWork( dbManager ) );
    assertEquals( "Expected all chainworkers executed", 4, dbManager.counter );
    
    DatabaseFullStrategyForTest current = strategy0;
    while ( current != null )
    {
      assertTrue( "Expected strategy executed", current.executed );
      current.executed = false;
      current = (DatabaseFullStrategyForTest) current.getSuccessor();
    }
    
    // test with dbManager returning true
    dbManager.isProcessingCurrentSamples = true;
    dbManager.counter = 0;
    assertTrue( "Expected work successful", strategy0.doWork( dbManager ) );
    assertEquals( "Expected 1 strategy executed", 1, dbManager.counter );
    
    int i = 0;
    current = strategy0;
    while ( current != null && i < dbManager.counter )
    {
      assertTrue( "Expected worker executed", current.executed );
      current.executed = false;
      current = (DatabaseFullStrategyForTest) current.getSuccessor();
      i++;
    }
  }
}
