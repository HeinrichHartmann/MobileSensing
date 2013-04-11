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

import de.unikassel.android.sdcframework.util.facade.LogLevel;
import android.util.Log;

/**
 * The observable logger implemented as Singleton and observable for
 * {@linkplain LogEvent}s. <br/>
 * Does redirect any recived log message to the Android
 * {@linkplain android.util.Log Log} class, depending on the invoked log method
 * and the corresponding {@linkplain LogLevel log level}.
 * 
 * <br/>
 * Any message above the current log level will be sent to registered observers
 * as {@link LogEvent}. <br/>
 * <br/>
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class Logger extends EventDispatcherImpl< LogEvent >
{
  /**
   * The android log tag
   */
  private final static String TAG = "SDCService";
  
  /**
   * out current log level
   */
  private LogLevel logLevel;
  
  /**
   * Flag for redirection to Android log
   */
  private final AtomicBoolean isRedirectingToAndroidLog;
  
  /**
   * the singleton instance of the logger
   */
  private static Logger instance;
  
  /**
   * Getter for the global logger instance
   * 
   * @return the global logger instance
   */
  public synchronized static Logger getInstance()
  {
    if ( instance == null )
    {
      instance = new Logger();
      instance.startWork();
    }
    return instance;
  }
  
  /**
   * Does release the global singleton instance
   */
  public static synchronized void releaseInstance()
  {
    if ( instance != null )
    {
      instance.stopWork();
      instance.doCleanUp();
      instance.doTerminate();
      instance = null;
    }
  }
  
  /**
   * Constructor
   */
  private Logger()
  {
    super( new EventCollectorImpl< LogEvent >() );
    isRedirectingToAndroidLog = new AtomicBoolean( true );
    setLogLevel( LogLevel.DEBUG );
    // avoid recursion by disabling the logging feature of the dispatch worker
    // thread
    setLogging( false );
  }
  
  /**
   * Setter for the logLevel
   * 
   * @param logLevel
   *          the logLevel to set
   */
  public final synchronized void setLogLevel( LogLevel logLevel )
  {
    this.logLevel = logLevel;
  }
  
  /**
   * Getter for the logLevel
   * 
   * @return the logLevel
   */
  public final synchronized LogLevel getLogLevel()
  {
    return logLevel;
  }
  
  /**
   * Setter for the isRedirectingToAndroidLog flag
   * 
   * @param doRedircet
   *          if true log info will be redirected to Android Log
   */
  public final void setRedirectingToAndroidLog( boolean doRedircet )
  {
    isRedirectingToAndroidLog.set( doRedircet );
  }
  
  /**
   * Getter for the isRedirectingToAndroidLog flag
   * 
   * @return the isRedirectingToAndroidLog flag
   */
  public final boolean isRedirectingToAndroidLog()
  {
    return isRedirectingToAndroidLog.get();
  }
  
  /**
   * Does create a log event based on given information
   * 
   * @param src
   *          the source object
   * @param logLevel
   *          the log level
   * @param msg
   *          the log message
   * @return a log event
   */
  public final static LogEvent toLogEvent( Object src, LogLevel logLevel,
      String msg )
  {
    String message = msg;
    if ( src != null )
    {
      message = src.getClass().getSimpleName() + ": " + msg;
    }
    return new LogEvent( message, logLevel,
        TimeProvider.getInstance().getTimeStamp() );
  }
  
  /**
   * Converts our log level to Android Log level
   * 
   * @param logLevel
   *          the SDC log level
   * @return the corresponding Android Log level
   */
  public final static int toAndroidLogLevel( LogLevel logLevel )
  {
    switch ( logLevel )
    {
      case DEBUG:
      {
        return Log.DEBUG;
      }
      case INFO:
      {
        return Log.INFO;
      }
      case WARNING:
      {
        return Log.WARN;
      }
      case ERROR:
      {
        return Log.ERROR;
      }
      default:
      {
        return Log.DEBUG;
      }
    }
  }
  
  /**
   * Does redirect a log event message to the Andorid Log class
   * 
   * @param logEvent
   *          the log event
   */
  private final static void redirectToAndroidLog( LogEvent logEvent )
  {
    int level = toAndroidLogLevel( logEvent.getLogLevel() );
    
    switch ( level )
    {
      case Log.DEBUG:
      {
        Log.d( TAG, logEvent.getMessage() );
        break;
      }
      case Log.INFO:
      {
        Log.i( TAG, logEvent.getMessage() );
        break;
      }
      case Log.WARN:
      {
        Log.w( TAG, logEvent.getMessage() );
        break;
      }
      case Log.ERROR:
      {
        Log.e( TAG, logEvent.getMessage() );
        break;
      }
    }
  }
  
  /**
   * Does enqueue a new log event
   * 
   * @param src
   *          the events source
   * @param logLevel
   *          the log level
   * @param msg
   *          the message
   */
  private final void enqueue( Object src, LogLevel logLevel, String msg )
  {
    LogEvent logEvent = toLogEvent( src, logLevel, msg );
    getCollector().enqueue( logEvent );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.EventDispatcherImpl#doWork()
   */
  @Override
  protected final void doWork()
  {
    try
    {
      // take event from queue, redirect it to Log and notify observers
      LogEvent event = getCollector().dequeue();
      
      writeToLogfile(event);
      redirectToAndroidLog( event );
      if ( event.getLogLevel().ordinal() <= getLogLevel().ordinal() )
      {
        notify( event );
      }
    }
    catch ( InterruptedException e )
    {}
  }
  
  /**
   * @param event
   */
  private void writeToLogfile( LogEvent event )
  {
    LogfileManager logManager = LogfileManager.getInstance();
    if ( logManager != null )
    {
      logManager.addLogEvent( event );
    }
  }

  /**
   * Does log a debug message
   * 
   * @param src
   *          the message source object
   * @param msg
   *          the message
   */
  public final synchronized void debug( Object src, String msg )
  {
    enqueue( src, LogLevel.DEBUG, msg );
  }
  
  /**
   * Does log an info message
   * 
   * @param src
   *          the message source object
   * @param msg
   *          the message
   */
  public final synchronized void info( Object src, String msg )
  {
    enqueue( src, LogLevel.INFO, msg );
  }
  
  /**
   * Does log a warning message
   * 
   * @param src
   *          the message source object
   * @param msg
   *          the message
   */
  public final synchronized void warning( Object src, String msg )
  {
    enqueue( src, LogLevel.WARNING, msg );
  }
  
  /**
   * Does log an error message
   * 
   * @param src
   *          the message source object
   * @param msg
   *          the message
   */
  public final synchronized void error( Object src, String msg )
  {
    enqueue( src, LogLevel.ERROR, msg );
  }
}
