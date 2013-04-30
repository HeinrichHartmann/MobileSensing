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

import android.content.Intent;
import junit.framework.Assert;
import junit.framework.TestCase;

import de.unikassel.android.sdcframework.util.LogEvent;
import de.unikassel.android.sdcframework.util.TimeProvider;
import de.unikassel.android.sdcframework.util.facade.LogLevel;

/**
 * Tests for the log event.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestLogEvent extends TestCase
{
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.LogEvent#LogEvent(String, LogLevel, long)}
   * , {@link de.unikassel.android.sdcframework.util.LogEvent#getMessage()} and
   * {@link de.unikassel.android.sdcframework.util.LogEvent#getLogLevel()}.
   */
  public final void testLogEvent()
  {
    String message = "This is the log meassage";
    LogLevel level = LogLevel.WARNING;
    long timeStamp = TimeProvider.getInstance().getTimeStamp();
    LogEvent event = new LogEvent( message, level, timeStamp );
    
    assertEquals( "Expected equal message", message, event.getMessage() );
    assertEquals( "Expected equal log level", level, event.getLogLevel() );
    assertEquals( "Expected equal time stamp", timeStamp, event.getTimeStamp() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.LogEvent#toString()}.
   */
  public final void testToString()
  {
    String message = "This is the log meassage";
    long timeStamp = TimeProvider.getInstance().getTimeStamp();
    
    for ( LogLevel level : LogLevel.values() )
    {
      assertEquals( "Unexpected string representation",
          TimeProvider.toUTCTime( timeStamp ) + " <"
              + level.toString().charAt( 0 ) + ">: " + message,
          new LogEvent( message, level, timeStamp ).toString() );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.LogEvent#getLongMessage()}.
   */
  public final void testGetLongMessage()
  {
    String message = "This is the log meassage";
    long timeStamp = TimeProvider.getInstance().getTimeStamp();
    
    for ( LogLevel level : LogLevel.values() )
    {
      String exected = TimeProvider.toUTCString( timeStamp ) + "\t<"
          + level.toString().charAt( 0 ) + ">: " + message;
      String realMsg =
          new LogEvent( message, level, timeStamp ).getLongMessage();
      assertEquals( "Unexpected string representation", exected, realMsg );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.LogEvent#LogEvent(android.content.Intent)}
   * .
   */
  public final void testIntentConstruction()
  {
    
    LogEvent event =
        new LogEvent( "Test message", LogLevel.WARNING,
            System.currentTimeMillis() );
    Intent intent = event.getIntent();
    
    LogEvent eventFromIntent = new LogEvent( intent );
    
    Assert.assertEquals( "Expected same message", event.getMessage(),
        eventFromIntent.getMessage() );
    Assert.assertEquals( "Expected same time stamp", event.getTimeStamp(),
        eventFromIntent.getTimeStamp() );
    Assert.assertEquals( "Expected same log level", event.getLogLevel(),
        eventFromIntent.getLogLevel() );
    
  }
  
}
