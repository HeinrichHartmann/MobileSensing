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
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.preferences.PriorityLevelPreference;

/**
 * Tests for the priority preferences.
 * 
 * @author Katy Hilgenberg
 *
 */
public class TestPriorityLevelPreference extends AndroidTestCase
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
    PriorityLevelPreference prioPref = new PriorityLevelPreference();
    
    assertEquals( "Unexpected key value", PriorityLevelPreference.KEY,
        prioPref.getKey() );
    assertEquals( "Unexpected default value", PriorityLevelPreference.DEFAULT,
        prioPref.getDefault() );
    
    String suffix = "test";
    prioPref = new PriorityLevelPreference( suffix );
    
    assertEquals( "Unexpected key value", "test_" + PriorityLevelPreference.KEY,
        prioPref.getKey() );
    assertEquals( "Unexpected default value", PriorityLevelPreference.DEFAULT,
        prioPref.getDefault() );
  }
  
  /**
   * Test method for {@link de.unikassel.android.sdcframework.preferences.PriorityLevelPreference#getConfiguration(android.content.SharedPreferences)}.
   */
  public final void testGetConfiguration()
  {
    PriorityLevelPreference prioPref = new PriorityLevelPreference();
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    for( SensorDevicePriorities prio : SensorDevicePriorities.values() )
    {
      editor.putString( prioPref.getKey(), Integer.valueOf( prio.ordinal() ).toString() );
      editor.commit();
      
      SensorDevicePriorities configPrio = prioPref.getConfiguration( sharedPreferences );
      assertEquals( "Unexpected configuration", prio, configPrio );
    }
  }
  
}
