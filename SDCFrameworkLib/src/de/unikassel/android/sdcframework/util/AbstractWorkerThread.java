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
package de.unikassel.android.sdcframework.util;

import java.util.concurrent.atomic.AtomicBoolean;

import de.unikassel.android.sdcframework.util.facade.WorkerThread;

/**
 * Abstract base class for any thread in the SDC Framework. <br/>
 * <br/>
 * Does provide the following features:
 * <ul>
 * <li>can be used to execute cyclic work by implementing the protected method
 * {@linkplain #doWork()},</li>
 * <li>will be started by calling {@linkplain #startWork()},</li>
 * <li>work execution can be paused by calling {@linkplain #stopWork()},</li>
 * <li>will run as long as {@linkplain #doTerminate()} is not explicitly called.
 * </li>
 * </ul>
 * 
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractWorkerThread extends Thread implements
    WorkerThread
{
  /**
   * The new created state flag
   */
  private final AtomicBoolean isNew;
  
  /**
   * The terminated state flag
   */
  private final AtomicBoolean hasTerminated;
  
  /**
   * The isWorking state flag
   */
  private final AtomicBoolean isWorking;
  
  /**
   * The logging state flag
   */
  private final AtomicBoolean doLog;
  
  /**
   * Synchronization lock
   */
  private final Object syncLock;
  
  /**
   * Constructor
   * 
   */
  public AbstractWorkerThread()
  {
    super();
    isNew = new AtomicBoolean( true );
    doLog = new AtomicBoolean( true );
    hasTerminated = new AtomicBoolean( false );
    isWorking = new AtomicBoolean( false );
    syncLock = new Object();
    setDaemon( true );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.WorkerThread#isWorking()
   */
  @Override
  public final boolean isWorking()
  {
    return this.isWorking.get();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.WorkerThread#setLogging(boolean)
   */
  @Override
  public final void setLogging( boolean doLog )
  {
    this.doLog.set( doLog );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.WorkerThread#isLogging()
   */
  @Override
  public final boolean isLogging()
  {
    return doLog.get();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.WorkerThread#hasTerminated()
   */
  @Override
  public final boolean hasTerminated()
  {
    return hasTerminated.get();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Thread#start()
   */
  @Override
  public final synchronized void start()
  {
    if ( isNew.compareAndSet( true, false ) )
    {
      super.start();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  @Override
  public final void run()
  {
    super.run();
    
    while ( !hasTerminated() )
    {
      try
      {
        if ( isWorking() )
        {
          // next working cycle
          doWork();
        }
        else
        {
          synchronized ( syncLock )
          {
            // non busy wait for work state change
            syncLock.wait();
          }
        }
      }
      catch ( InterruptedException e )
      {}
      catch ( Exception e )
      {
        if ( isLogging() )
          Logger.getInstance().error( this,
              " Unexpected exception caught: " + e );
        e.printStackTrace();
      }
    }
    
    stopWork();
    doCleanUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.WorkerThread#startWork()
   */
  @Override
  public final synchronized void startWork()
  {
    if ( !hasTerminated() )
    {
      // safe test and set working state flag
      if ( isWorking.compareAndSet( false, true ) )
      {
        // if thread wasn't started yet or is sleeping -> switch to running
        doRun();
        
        logMessage( " started " );
      }
    }
  }
  
  /**
   * Does send the thread into running mode either by starting it if not alive
   * or by waking a sleeping thread up.
   */
  private final void doRun()
  {
    if ( isNew.get() )
    {
      // start a new thread
      start();
    }
    else
    {
      // wake up stopped thread
      synchronized ( syncLock )
      {
        syncLock.notifyAll();
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.WorkerThread#stopWork()
   */
  @Override
  public final synchronized void stopWork()
  {
    if ( isWorking.compareAndSet( true, false ) )
    {
      interrupt();
      logMessage( " stopped" );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.WorkerThread#doTerminate()
   */
  @Override
  public final synchronized void doTerminate()
  {
    // secure check and set flag for termination state
    if ( hasTerminated.compareAndSet( false, true ) )
    {
      // assure not working thread will become alive in run loop
      // to terminated
      interrupt();
      
      // start a new thread for initial termination
      if ( isNew.get() )
      {
        // start a new thread
        start();
      }
      
      // with termination flag set it will terminate now!
      logMessage( " terminating" );
    }
  }
  
  /**
   * Does log a message if logging is enabled
   * 
   * @param message
   *          the message to log
   */
  protected final void logMessage( String message )
  {
    if ( isLogging() )
      Logger.getInstance().info( this, message );
  }
  
  /**
   * The clean up method executed on termination
   */
  protected abstract void doCleanUp();
  
  /**
   * The working method executed in the running loop if started
   */
  protected abstract void doWork();
  
}
