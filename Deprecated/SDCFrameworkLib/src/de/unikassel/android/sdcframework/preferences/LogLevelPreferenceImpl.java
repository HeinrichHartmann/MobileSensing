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
package de.unikassel.android.sdcframework.preferences;

import android.content.SharedPreferences;
import de.unikassel.android.sdcframework.preferences.facade.LogLevelConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.SinglePreference;
import de.unikassel.android.sdcframework.util.facade.LogLevel;

/**
 * Implementation of the preference for the {@link LogLevel} type, which is a
 * single preference bounded to the {@link LogLevelConfiguration}.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class LogLevelPreferenceImpl
    implements SinglePreference< LogLevelConfiguration >
{
  /**
   * The log level preference
   */
  private final StringPreference logLevelPreference;
  
  /**
   * Identifier log level key
   */
  public static final String KEY = "loglevel";
  
  /**
   * The log level default value
   */
  public static final LogLevel DEFAULT = LogLevel.INFO;
  
  /**
   * Constructor
   */
  public LogLevelPreferenceImpl()
  {
    super();
    this.logLevelPreference =
        new StringPreference( KEY, DEFAULT.toString() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * SinglePreferenceWithDefault#
   * getConfiguration(android.content.SharedPreferences)
   */
  @Override
  public final LogLevelConfiguration getConfiguration(
      SharedPreferences sharedPreferences )
  {
    LogLevel result = DEFAULT;
    String logLevel = logLevelPreference.getConfiguration( sharedPreferences );
    
    // try to find enumeration match for the log level
    for ( LogLevel level : LogLevel.values() )
    {
      if ( logLevel.equals( level.toString() ) )
      {
        result = level;
        break;
      }
    }
    return new LogLevelConfigurationImpl( result );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SinglePreference#getKey
   * ()
   */
  @Override
  public String getKey()
  {
    return logLevelPreference.getKey();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * getDefault()
   */
  @Override
  public LogLevelConfiguration getDefault()
  {
    return new LogLevelConfigurationImpl( DEFAULT );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * setDefault(java.lang.Object)
   */
  @Override
  public void setDefault( LogLevelConfiguration defaultValue )
  {
    logLevelPreference.setDefault( defaultValue.getLogLevel().toString() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * testForKey(java.lang.String)
   */
  @Override
  public boolean testForKey( String key )
  {
    return logLevelPreference.testForKey( key );
  }
  
}
