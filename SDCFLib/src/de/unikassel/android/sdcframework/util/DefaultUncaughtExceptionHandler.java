/*
 * Copyright (C) 2012, Katy Hilgenberg
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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import de.unikassel.android.sdcframework.util.facade.LogLevel;

/**
 * A default handler for uncaught exceptions.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class DefaultUncaughtExceptionHandler implements
    UncaughtExceptionHandler
{
  /**
   * the original Handler
   */
  private UncaughtExceptionHandler orgHandler;
  
  /**
   * Constructor
   * 
   * @param orgHandler
   *          the original handler
   */
  public DefaultUncaughtExceptionHandler( UncaughtExceptionHandler orgHandler )
  {
    super();
    this.orgHandler = orgHandler;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread
   * , java.lang.Throwable)
   */
  @Override
  public void uncaughtException( Thread thread, Throwable ex )
  {
    doLogUncaughtException( thread, ex );
    
    // redirect to the original handler
    orgHandler.uncaughtException( thread, ex );
  }
  
  /**
   * Method to log the exception
   * 
   * @param thread
   *          the thread
   * @param ex
   *          the exception
   */
  private void doLogUncaughtException( Thread thread, Throwable ex )
  {
    Throwable e = ex.fillInStackTrace();
    StringWriter traceOut = new StringWriter();
    e.printStackTrace( new PrintWriter( traceOut, true ) );
    StringBuffer msg =
        new StringBuffer( "Uncaught Exception: " ).append(
            ex.getMessage() ).append( '\n' ).append( traceOut.toString() );
    LogEvent logEvent =
        new LogEvent( msg.toString(), LogLevel.ERROR,
            TimeProvider.getInstance().getTimeStamp() );
    
    LogfileManager logManager = LogfileManager.getInstance();
    if ( logManager != null )
    {
      logManager.addLogEvent( logEvent );
      try
      {
        Thread.sleep( 5000 );
      }
      catch ( InterruptedException e1 )
      {}
    }
  }
  
}
