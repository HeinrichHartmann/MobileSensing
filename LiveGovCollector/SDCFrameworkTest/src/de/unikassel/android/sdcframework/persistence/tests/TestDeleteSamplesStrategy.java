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

import de.unikassel.android.sdcframework.persistence.DeleteSamplesStrategy;
import junit.framework.TestCase;

/**
 * Test for the deletion strategy
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestDeleteSamplesStrategy extends TestCase
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
   * Test for the deletion strategy.
   */
  public final void testDeleteSamplesStrategy()
  {
    DeleteSamplesStrategy strategy = new DeleteSamplesStrategy( 100, false );
    PersistentStorageManagerForTest manager = new PersistentStorageManagerForTest();
    
    manager.counter = 0;
    manager.isProcessingCurrentSamples = false;
    manager.deleteOldestSamplesInDatabaseWasCalled = false;
    
    assertFalse( "Expected strategy not successful",
        strategy.process( manager ) );
    assertTrue( "Expected base class execute method called",
        manager.counter == 1 );
    assertTrue( "Expected deletion method on database manager was called",
        manager.deleteOldestSamplesInDatabaseWasCalled );
    
    manager.counter = 0;
    manager.isProcessingCurrentSamples = true;
    manager.deleteOldestSamplesInDatabaseWasCalled = false;
    
    assertTrue( "Expected strategy successful", strategy.process( manager ) );
    assertTrue( "Expected base class execute method called",
        manager.counter == 1 );
    assertTrue( "Expected deletion method on database manager was called",
        manager.deleteOldestSamplesInDatabaseWasCalled );
  }
  
}
