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

import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.preferences.EnabledPreference;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

/**
 * Tests for the frequency preferences.
 * 
 * @author Katy Hilgenberg
 *
 */
public class TestEnabledPreference extends AndroidTestCase
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
    EnabledPreference preference = new EnabledPreference();
    
    assertEquals( "Unexpected key value", EnabledPreference.KEY,
        preference.getKey() );
    assertEquals( "Unexpected default value", EnabledPreference.DEFAULT,
        preference.getDefault() );
    
    String suffix = "test";
    preference = new EnabledPreference( suffix );
    
    assertEquals( "Unexpected key value", "test_" + EnabledPreference.KEY,
        preference.getKey() );
    assertEquals( "Unexpected default value", EnabledPreference.DEFAULT,
        preference.getDefault() );
  }
  
  /**
   * Test method for {@link de.unikassel.android.sdcframework.preferences.EnabledPreference#getConfiguration(android.content.SharedPreferences)}.
   */
  public final void testGetConfiguration()
  {
    EnabledPreference preference = new EnabledPreference();
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    Boolean value = false;
    editor.putBoolean( preference.getKey(), value );
    editor.commit();
    
    Boolean configuration = preference.getConfiguration( sharedPreferences );
    assertEquals( "Unexpected configuration", value, configuration );
    
    value = true;
    editor.putBoolean( preference.getKey(), value );
    editor.commit();
    
    configuration = preference.getConfiguration( sharedPreferences );
    assertEquals( "Unexpected configuration", value, configuration );
  }
  
}
