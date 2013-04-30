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


import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.LogEvent;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import android.test.AndroidTestCase;

/**
 * Tests for the observable logger redirecting to Android Log.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestLogger extends AndroidTestCase
{
  
  /**
   * Constructor
   */
  public TestLogger()
  {
    super();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    super.setUp();    
    // clear an available singleton instance
    Logger.releaseInstance();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    // clear test instance
    Logger.releaseInstance();
    super.tearDown();
  }
  
  /**
   * Testing preconditions
   */
  public void testPreconditions()
  {
    Logger logger = Logger.getInstance();
    assertEquals( "Expected info log level after first access", LogLevel.DEBUG,
        logger.getLogLevel() );
    assertTrue( "Expected redirection enabled after first access",
        logger.isRedirectingToAndroidLog() );
    
    assertTrue( "Expected log event dispatcher thread working",
        logger.isWorking() );
    assertEquals( "Expected collector queue empty", 0,
        logger.getCollector().getEventCount() );
    
    // test setters and getters
    logger.setLogLevel( LogLevel.DEBUG );
    assertEquals( "Expected changed log level", LogLevel.DEBUG,
        logger.getLogLevel() );
    logger.setLogLevel( LogLevel.ERROR );
    assertEquals( "Expected changed log level", LogLevel.ERROR,
        logger.getLogLevel() );
    
    logger.setRedirectingToAndroidLog( false );
    assertFalse( "Expected redirection flag false",
        logger.isRedirectingToAndroidLog() );
    logger.setRedirectingToAndroidLog( true );
    assertTrue( "Expected redirection flag true",
        logger.isRedirectingToAndroidLog() );
  }
  
  /**
   * Testing of the static methods
   */
  public void testStaticMethods()
  {
    LogLevel logLevel = LogLevel.WARNING;
    String msg = "Test";
    String msgExpected = this.getClass().getSimpleName() + ": " + msg;
    
    LogEvent logEvent = Logger.toLogEvent( this, logLevel, msg );
    assertEquals( "Unexpected log level in event", logLevel,
        logEvent.getLogLevel() );
    assertEquals( "Unexpected message in event", msgExpected,
        logEvent.getMessage() );
  }
  
  /**
   * Testing of logging
   */
  public void testLogging()
  {
    LogObserverForTest logObserver = new LogObserverForTest();
    Logger logger = Logger.getInstance();
    
    // register our log observer
    logger.registerEventObserver( logObserver );
    logger.setLogLevel( LogLevel.DEBUG );
    
    // use each log function one time
    String msg = "Test";
    doWriteLogEntries( msg );
    
    // check results
    doTestObservedLogEvents( logObserver, msg, 4, LogLevel.DEBUG );
  }
  
  /**
   * Does test observed log events for an expected count and types below a given
   * max level.
   * 
   * @param logObserver
   *          the log event observer
   * @param msg
   *          the log message
   * @param count
   *          the expected log event count
   * @param maxLevel
   *          the expected max log level
   */
  private void doTestObservedLogEvents( LogObserverForTest logObserver, String msg,
      int count, LogLevel maxLevel )
  {
    assertEquals( "Unexpected count of log events", count,
        logObserver.events.size() );
    
    for ( LogLevel level : LogLevel.values() )
    {
      String logMsg = Logger.toLogEvent( this, level, msg ).getMessage();
      LogEvent event = logObserver.events.get( level.ordinal() );
      assertEquals( "Unexpected log event level", level,
          event.getLogLevel() );
      assertEquals( "Unexpected log message", logMsg, event.getMessage() );
      
      if ( level.equals( maxLevel ) ) break;
    }
  }
  
  /**
   * Helper method to generate log messages. One of each log level type in
   * ascending order is written.
   * 
   * @param msg
   *          the message
   */
  private void doWriteLogEntries( String msg )
  {
    Logger logger = Logger.getInstance();
    logger.error( this, msg );
    logger.warning( this, msg );
    logger.info( this, msg );
    logger.debug( this, msg );
    
    // wait for event dispatching
    do
    {
      TestUtils.sleep( 100 );
    }
    while ( logger.getCollector().getEventCount() > 0 );
  }
  
  /**
   * Testing of log level changes and visibility of log messages
   */
  public void testLogLevelChanges()
  {
    String msg = "Test";
    LogObserverForTest logObserver = new LogObserverForTest();
    Logger logger = Logger.getInstance();
    logger.registerEventObserver( logObserver );
    
    // change log level, log an event of each level and check results
    for ( LogLevel level : LogLevel.values() )
    {
      logger.setLogLevel( level );
      logObserver.events.clear();
      doWriteLogEntries( msg ); 
      doTestObservedLogEvents( logObserver, msg, level.ordinal() + 1, level );
    }
  }
}
