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
package de.unikassel.android.sdcframework.preferences.tests;

import de.unikassel.android.sdcframework.preferences.LogLevelConfigurationChangeEventImpl;
import de.unikassel.android.sdcframework.preferences.LogLevelConfigurationImpl;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import junit.framework.TestCase;

/**
 * Test for the log level configuration change event
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestLogLevelConfigurationChangeEventImpl extends TestCase
{
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.LogLevelConfigurationChangeEventImpl#getConfiguration()}
   * .
   */
  public final void testGetConfiguration()
  {
    LogLevel level = LogLevel.WARNING;
    LogLevelConfigurationChangeEventImpl event = 
      new LogLevelConfigurationChangeEventImpl( new LogLevelConfigurationImpl(level) );
    assertEquals( "Unexpected log level", level, event.getConfiguration().getLogLevel() );
  }
  
}
