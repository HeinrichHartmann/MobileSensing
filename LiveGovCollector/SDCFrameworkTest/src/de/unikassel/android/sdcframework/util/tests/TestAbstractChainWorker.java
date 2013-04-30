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
package de.unikassel.android.sdcframework.util.tests;

import de.unikassel.android.sdcframework.util.AbstractChainWorker;
import junit.framework.TestCase;

/**
 * Tests for the abstract chain worker implementation.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAbstractChainWorker extends TestCase
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
   * Test client for the chain worker
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestClient
  {
    /**
     * Counter to count the calls to the doSignalWork Method
     */
    public int counter = 0;
    
    /**
     * Call back method for the worker to signal that his execute method was called
     */
    public void doSignalWork()
    { 
      counter++;
    }
  }
  
  /**
   * Test implementation of the abstract chain worker class
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestChainWorker extends AbstractChainWorker< TestClient >
  {
    /**
     * The execution result to return
     */
    public boolean executionResult = false;
    
    /**
     * Flag to signal execution
     */
    public boolean executed = false;
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.AbstractChainWorker#process(java
     * .lang.Object)
     */
    @Override
    protected boolean process( TestClient client )
    {
      client.doSignalWork();
      executed = true;
      return executionResult;
    }
  }
  
  /**
   * Test construction, and successor concatenation
   */
  public void testAbstractChainWorker()
  {
    TestChainWorker worker = new TestChainWorker();
    assertNull( "Expected successor not set after construction", worker.getSuccessor() );
    
    // test normal setter
    TestChainWorker successor1 = new TestChainWorker();
    worker.setSuccessor( successor1 );
    assertNotNull( "Expected successor set", worker.getSuccessor() );
    assertSame( "Expected successor same", successor1, worker.getSuccessor() );
    worker.setSuccessor( null );
    assertNull( "Expected successor cleared", worker.getSuccessor() );

    TestChainWorker successor2 = new TestChainWorker();
    TestChainWorker successor3 = new TestChainWorker();
    // test setting successors set by withSuccessor setter
    worker.withSuccessor( successor1 ).withSuccessor( successor2 ).withSuccessor( successor3 );
    assertNotNull( "Expected successor1 set", worker.getSuccessor() );
    assertSame( "Expected successor1 same", successor1, worker.getSuccessor() );
    assertNotNull( "Expected successor2 set", successor1.getSuccessor() );
    assertSame( "Expected successor2 same", successor2, successor1.getSuccessor() );
    assertNotNull( "Expected successor3 set", successor2.getSuccessor() );
    assertSame( "Expected successor3 same", successor3, successor2.getSuccessor() );
    assertNull( "Expected successor3 without successor", successor3.getSuccessor() );
  }
  
  /**
   * Test chain execution
   */
  public void testChainExecution()
  {
    TestChainWorker worker = new TestChainWorker();
    TestChainWorker successor1 = new TestChainWorker();
    TestChainWorker successor2 = new TestChainWorker();
    TestChainWorker successor3 = new TestChainWorker();
    worker.withSuccessor( successor1 ).withSuccessor( successor2 ).withSuccessor( successor3 );
    
    TestClient client = new TestClient();
    
    // test default with all successors returning false
    client.counter = 0;  
    assertFalse( "Expected work not successful", worker.doWork( client ) );
    assertEquals( "Expected all chainworkers executed", 4, client.counter );
    
    TestChainWorker current = worker;
    while( current != null )
    {
      assertTrue( "Expected worker executed", current.executed );
      current.executed = false;
      current = (TestChainWorker) current.getSuccessor();
    }
    
    // test with successor3 returning true
    successor3.executionResult = true;
    client.counter = 0;  
    assertTrue( "Expected work successful", worker.doWork( client ) );
    assertEquals( "Expected all chainworkers executed", 4, client.counter );
    
    current = worker;
    while( current != null )
    {
      assertTrue( "Expected worker executed", current.executed );
      current.executed = false;
      current = (TestChainWorker) current.getSuccessor();
    }
    
    // test with successor2 returning true
    successor2.executionResult = true;
    client.counter = 0;  
    assertTrue( "Expected work successful", worker.doWork( client ) );
    assertEquals( "Expected 3 chainworker(s) executed", 3, client.counter );
    
    int i = 0;
    current = worker;
    while( current != null )
    {
      boolean expectedExecuted = i < client.counter;
      assertEquals( "Unexpected worker execution state", expectedExecuted, current.executed );
      current.executed = false;
      current = (TestChainWorker) current.getSuccessor();
      i++;
    }   
    
    // test with successor1 returning true
    successor1.executionResult = true;
    client.counter = 0;  
    assertTrue( "Expected work successful", worker.doWork( client ) );
    assertEquals( "Expected 2 chainworker(s)", 2, client.counter );
    
    i = 0;
    current = worker;
    while( current != null )
    {
      boolean expectedExecuted = i < client.counter;
      assertEquals( "Unexpected worker execution state", expectedExecuted, current.executed );
      current.executed = false;
      current = (TestChainWorker) current.getSuccessor();
      i++;
    }      
    
    // test with worker returning true
    worker.executionResult = true;
    client.counter = 0;  
    assertTrue( "Expected work successful", worker.doWork( client ) );
    assertEquals( "Expected 1 chainworker(s) executed", 1, client.counter );
    
    i = 0;
    current = worker;
    while( current != null )
    {
      boolean expectedExecuted = i < client.counter;
      assertEquals( "Unexpected worker execution state", expectedExecuted, current.executed );
      current.executed = false;
      current = (TestChainWorker) current.getSuccessor();
      i++;
    }   
  }
}
