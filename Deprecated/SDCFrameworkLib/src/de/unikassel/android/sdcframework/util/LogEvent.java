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

import android.content.Intent;
import de.unikassel.android.sdcframework.util.facade.BroadcastableEvent;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import de.unikassel.android.sdcframework.util.facade.ObservableEvent;

/**
 * The observable Log event.
 * 
 * @see Logger
 * @author Katy Hilgenberg
 * 
 */
public final class LogEvent implements ObservableEvent, BroadcastableEvent
{
  /**
   * Out custom log level intent action
   */
  public static final String ACTION =
      "de.unikassel.android.sdcframework.intent.action.LOG";
  
  /**
   * The intent identifier for the message field
   */
  public final static String MSG = "Message";
  
  /**
   * The intent identifier for the log level field
   */
  public final static String LOGLEVEL = "LogLevel";
  
  /**
   * The intent identifier for the time stamp field
   */
  public final static String TS = "TimeStamp";
  
  /**
   * The time stamp
   */
  private final long timeStamp;
  
  /**
   * The log message
   */
  private final String message;
  
  /**
   * The log level
   */
  private final LogLevel logLevel;
  
  /**
   * Constructor
   */
  @SuppressWarnings( "unused" )
  private LogEvent()
  {
    this( null, null, 0L );
  }
  
  /**
   * Constructor
   * 
   * @param message
   *          the log message
   * @param logLevel
   *          the log level
   * @param timeStamp
   *          the time stamp
   */
  public LogEvent( String message, LogLevel logLevel, long timeStamp )
  {
    super();
    this.message = message;
    this.logLevel = logLevel;
    this.timeStamp = timeStamp;
  }
  
  /**
   * Constructor
   * 
   * @param intent
   *          the intent to create from
   */
  public LogEvent( Intent intent )
  {
    if ( intent.getAction().equals( ACTION ) )
    {
      this.timeStamp = intent.getLongExtra( TS, 0L );
      this.logLevel =
          LogLevel.valueOf( intent.getStringExtra( LogEvent.LOGLEVEL ) );
      this.message = intent.getStringExtra( LogEvent.MSG );
    }
    else
    {
      this.message = null;
      this.logLevel = null;
      this.timeStamp = 0L;
    }
  }
  
  /**
   * Getter for the message
   * 
   * @return the message
   */
  public String getMessage()
  {
    return message;
  }
  
  /**
   * Getter for the log level
   * 
   * @return the log level
   */
  public LogLevel getLogLevel()
  {
    return logLevel;
  }
  
  /**
   * Getter for the time stamp
   * 
   * @return the time stamp
   */
  public final long getTimeStamp()
  {
    return timeStamp;
  }
  
  /**
   * Method to get a long string representation
   * 
   * @return the long log message
   */
  public final String getLongMessage()
  {
    return new StringBuffer( TimeProvider.toUTCString( timeStamp ) ).
        append( "\t<" ).append( logLevel.toString().charAt( 0 ) ).append( ">: " ).append(
            getMessage() ).toString();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public final String toString()
  {
    return new StringBuffer( TimeProvider.toUTCTime( timeStamp ) ).
        append( " <" ).append( logLevel.toString().charAt( 0 ) ).append( ">: " ).append(
            getMessage() ).toString();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.BroadcastableEvent#getIntent()
   */
  @Override
  public final Intent getIntent()
  {
    Intent intent = new Intent();
    intent.setAction( ACTION );
    intent.putExtra( LogEvent.TS, getTimeStamp() );
    intent.putExtra( LogEvent.MSG, getMessage() );
    intent.putExtra( LogEvent.LOGLEVEL, getLogLevel().toString() );
    return intent;
  }
}
