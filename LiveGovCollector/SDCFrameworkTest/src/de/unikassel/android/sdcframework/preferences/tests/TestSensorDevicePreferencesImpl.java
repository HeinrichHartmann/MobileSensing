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
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.preferences.EnabledPreference;
import de.unikassel.android.sdcframework.preferences.FrequencyPreference;
import de.unikassel.android.sdcframework.preferences.PriorityLevelPreference;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.SensorDevicePreferencesImpl;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

/**
 * Tests for the sensor device preferences.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSensorDevicePreferencesImpl extends AndroidTestCase
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
  public final void testSensorDevicePreferencesImpl()
  {
    for ( SensorDeviceIdentifier deviceID : SensorDeviceIdentifier.values() )
    {
      String keyExpected =
          deviceID.toString() + "" + SensorDevicePreferencesImpl.KEY_APPENDIX;
      SensorDeviceConfiguration defaultValue = new SensorDeviceConfigurationImpl(
          FrequencyPreference.DEFAULT,
          PriorityLevelPreference.DEFAULT,
          EnabledPreference.DEFAULT );
      
      SensorDevicePreferencesImpl preference =
          new SensorDevicePreferencesImpl( deviceID );
      
      assertEquals( "Unexpected device identifier", deviceID, preference.getDeviceIdentifier() );      
      assertEquals( "Unexpected key value", keyExpected, preference.getKey() );
      assertEquals( "Unexpected default value", defaultValue, preference.getDefault() );
      
      assertNotNull( "Expected frequency preference not null", preference.getFrequencyPreference() );
      assertNotNull( "Expected enabled preference not null", preference.getEnabledPreference() );
      assertNotNull( "Expected priority preference not null", preference.getPriorityPreference() );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.SensorDevicePreferencesImpl#getConfiguration(android.content.SharedPreferences)}
   * .
   */
  public final void testGetConfiguration()
  {
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    for ( SensorDeviceIdentifier deviceID : SensorDeviceIdentifier.values() )
    {
      SensorDevicePreferencesImpl preference =
          new SensorDevicePreferencesImpl( deviceID );
      
      // calc random values for a device configuration
      Boolean enabled = ( (int) ( Math.random() * 100 ) % 2 ) > 0;
      Integer frequency = (int) ( Math.random() * 10000 );
      SensorDevicePriorities priority = null;
      Integer prioVal = (int) ( Math.random() * SensorDevicePriorities.values().length );
      
      // try to find enumeration match for the priority
      for ( SensorDevicePriorities prio : SensorDevicePriorities.values() )
      {
        if ( prioVal.equals( prio.ordinal() ) )
        {
          priority = prio;
          break;
        }
      }
      assertNotNull( "priority should not be null here!", priority );
      
      editor.putBoolean( preference.getEnabledPreference().getKey(), enabled );
      editor.putString( preference.getFrequencyPreference().getKey(), frequency.toString() );
      editor.putString( preference.getPriorityPreference().getKey(), prioVal.toString() );
      editor.commit();

      SensorDeviceConfiguration expectedConfig = new SensorDeviceConfigurationImpl( frequency,
          priority, enabled);
      SensorDeviceConfiguration configuration = preference.getConfiguration( sharedPreferences );
      assertEquals( "Unexpected configuration", expectedConfig , configuration );
    }
  }
  
}
