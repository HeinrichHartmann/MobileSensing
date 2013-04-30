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

import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.TestCase;

import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.AbstractWorkerThread;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Tests for the abstract base class for worker threads.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAbstractWorkerThread extends TestCase
{  
  /**
   * Inner test class for an extension of the abstract worker thread.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestWorkerThread extends AbstractWorkerThread
  {
    /**
     * execution flag for the doWork() method
     */
    public final AtomicBoolean hasExecutedDoWork;
    
    /**
     * execution flag for the doCleanUp() method
     */
    public final AtomicBoolean hasExecutedCleanUp;
    
    /**
     * Constructor
     */
    public TestWorkerThread()
    {
      super();
      hasExecutedDoWork = new AtomicBoolean( false );
      hasExecutedCleanUp = new AtomicBoolean( false );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.AbstractWorkerThread#doCleanUp()
     */
    @Override
    protected void doCleanUp()
    {
      hasExecutedCleanUp.set( true );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see de.unikassel.android.sdcframework.util.AbstractWorkerThread#doWork()
     */
    @Override
    protected void doWork()
    {
      hasExecutedDoWork.set( true );
    }
  }
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    Logger.getInstance().setRedirectingToAndroidLog( false );
    super.setUp();
  }

  /**
   * Test method for construction and initial settings .
   */
  public final void testAbstractWorkerThread()
  {
    TestWorkerThread worker = new TestWorkerThread();
    
    // test initial default settings after construction
    assertEquals( "unexpected logging state after construction", true,
        worker.isLogging() );
    assertEquals( "unexpected termination state after construction",
        false, worker.hasTerminated() );
    assertEquals( "unexpected working state after construction", false,
        worker.isWorking() );
    assertEquals( "unexpected daemon state after construction", true,
        worker.isDaemon() );
    assertFalse( "expected thread not started yet", worker.isAlive() );
    assertEquals( "unexpected thread state", Thread.State.NEW,
        worker.getState() );
  }
  
  /**
   * Test method for a working life cycle of the worker thread
   */
  public final void testStartAndStopWork()
  {
    TestWorkerThread worker = new TestWorkerThread();
    
    // disable logging as this is redirecting to Android Log instance
    worker.setLogging( false );
    assertEquals(
        "unexpected logging state after a call to setLogging()", false,
        worker.isLogging() );
    
    // test first start after construction
    worker.startWork();
    assertTrue( "expected thread alive after initial start",
        worker.isAlive() );
    TestUtils.sleep( 1000 );
    assertEquals( "unexpected working state after initial start", true,
        worker.isWorking() );
    assertEquals( "unexpected termination state after initial start",
        false, worker.hasTerminated() );
    assertTrue( "expected doWork() method executed after initial start",
        worker.hasExecutedDoWork.get() );
    assertEquals( "unexpected thread state after initial start",
        Thread.State.RUNNABLE, worker.getState() );
    
    // test first stop after start
    worker.stopWork();
    TestUtils.sleep( 1000 );
    assertTrue( "expected thread alive after stop",
        worker.isAlive() );
    assertEquals( "unexpected working state after stop", false,
        worker.isWorking() );
    assertEquals( "unexpected termination state after stop",
        false, worker.hasTerminated() );
    assertEquals( "unexpected thread state after stop",
        Thread.State.WAITING, worker.getState() );
    worker.hasExecutedDoWork.set( false );
    TestUtils.sleep( 1000 );
    assertFalse( "expected doWork() method executed after stop",
        worker.hasExecutedDoWork.get() );
    
    // test restart after stop
    worker.startWork();
    TestUtils.sleep( 1000 );
    assertTrue( "expected thread alive after restart",
        worker.isAlive() );
    assertEquals( "unexpected working state after restartt", true,
        worker.isWorking() );
    assertEquals( "unexpected termination state after restartt",
        false, worker.hasTerminated() );
    assertTrue( "expected doWork() method executed after restartt",
        worker.hasExecutedDoWork.get() );
    assertEquals( "unexpected thread state after restartt",
        Thread.State.RUNNABLE, worker.getState() );
    
    // test termination after working
    worker.doTerminate();
    TestUtils.sleep( 1000 );
    assertEquals( "unexpected thread state after termination",
        Thread.State.TERMINATED, worker.getState() );
  }
  
  /**
   * Test method for thread termination and clean up
   */
  public final void testThreadTermination()
  {
    // test termination directly after construction
    TestWorkerThread worker = new TestWorkerThread();
    worker.setLogging( false );
    worker.doTerminate();
    TestUtils.sleep( 1000 );
    assertTrue(
        "expected doCleanUp() method executed for termination after creation",
        worker.hasExecutedCleanUp.get() );
    assertEquals(
        "unexpected termination state for termination after creation",
        true, worker.hasTerminated() );
    assertEquals(
        "unexpected working state for termination after creation",
        false, worker.isWorking() );
    assertEquals(
        "unexpected thread state for termination after creation",
        Thread.State.TERMINATED, worker.getState() );
    
    // test termination after start
    worker = new TestWorkerThread();
    worker.setLogging( false );
    worker.startWork();
    TestUtils.sleep( 1000 );
    worker.doTerminate();
    TestUtils.sleep( 1000 );
    assertTrue(
        "expected doCleanUp() method executed for termination after start",
        worker.hasExecutedCleanUp.get() );
    assertEquals(
        "unexpected termination state for termination after start",
        true, worker.hasTerminated() );
    assertEquals(
        "unexpected working state for termination after start",
        false, worker.isWorking() );
    assertEquals( "unexpected thread state for termination after start",
        Thread.State.TERMINATED, worker.getState() );
    
    // test termination after stop
    worker = new TestWorkerThread();
    worker.setLogging( false );
    worker.startWork();
    TestUtils.sleep( 1000 );
    worker.stopWork();
    TestUtils.sleep( 1000 );
    worker.doTerminate();
    TestUtils.sleep( 1000 );
    assertTrue(
        "expected doCleanUp() method executed for termination after stop",
        worker.hasExecutedCleanUp.get() );
    assertEquals(
        "unexpected termination state for termination after stop",
        true, worker.hasTerminated() );
    assertEquals( "unexpected working state for termination after stop",
        false, worker.isWorking() );
    assertEquals( "unexpected thread state  for termination after stop",
        Thread.State.TERMINATED, worker.getState() );
  }
}
