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

import de.unikassel.android.sdcframework.preferences.facade.LogLevelConfiguration;
import de.unikassel.android.sdcframework.util.facade.LogLevel;

/**
 * Implementation of the configuration for the log level.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class LogLevelConfigurationImpl implements LogLevelConfiguration
{
  /**
   * The log level value of the configuration
   */
  private final LogLevel level;
  
  /**
   * Constructor
   */
  @SuppressWarnings( "unused" )
  private LogLevelConfigurationImpl()
  {
    this( null );
  }
  
  /**
   * Constructor
   * 
   * @param level
   *          the value for the log level change
   */
  public LogLevelConfigurationImpl( LogLevel level )
  {
    super();
    this.level = level;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.LogLevelConfiguration
   * #getLogLevel()
   */
  @Override
  public final LogLevel getLogLevel()
  {
    return level;
  }
}
