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

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.preferences.LogLevelPreferenceImpl;
import de.unikassel.android.sdcframework.util.facade.LogLevel;

/**
 * Tests for the log level preferences.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestLogLevelPreference extends AndroidTestCase
{
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
  }
  
  /**
   * Test method for construction
   */
  public final void testPreconditions()
  {
    LogLevelPreferenceImpl logPref = new LogLevelPreferenceImpl();
    
    assertEquals( "Unexpected key value", LogLevelPreferenceImpl.KEY,
        logPref.getKey() );
    assertEquals( "Unexpected default value", LogLevelPreferenceImpl.DEFAULT,
        logPref.getDefault().getLogLevel() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.LogLevelPreferenceImpl#getConfiguration(android.content.SharedPreferences)}
   * .
   */
  public final void testGetConfiguration()
  {
    LogLevelPreferenceImpl logPref = new LogLevelPreferenceImpl();
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    for( LogLevel level : LogLevel.values() )
    {
      editor.putString( logPref.getKey(), level.toString() );
      editor.commit();
      
      LogLevel configLevel = logPref.getConfiguration( sharedPreferences ).getLogLevel();
      assertEquals( "Unexpected configuration", level, configLevel );
    }
  }
  
}
