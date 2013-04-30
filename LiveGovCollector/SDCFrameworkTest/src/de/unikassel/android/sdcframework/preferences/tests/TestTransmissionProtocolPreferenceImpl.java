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
import de.unikassel.android.sdcframework.preferences.TransmissionProtocolConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.TransmissionProtocolPreferenceImpl;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolPreference;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;

/**
 * Tests for the transmission protocol preferences.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestTransmissionProtocolPreferenceImpl extends AndroidTestCase
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
   * Test method for creation
   */
  public final void testTransmissionPreferenceImpl()
  {
    TransmissionProtocolPreference preferences = new TransmissionProtocolPreferenceImpl( "" );
    
    assertNotNull( "Expected key not null", preferences.getKey() );
    assertTrue( "Unexpected default value",
        preferences.getDefault() instanceof TransmissionProtocolConfiguration );
    
    assertNotNull( "Expected password preference not null",
        preferences.getAuthenticationPasswordPreference() );
    assertNotNull( "Expected user name preference not null",
        preferences.getAuthenticationUserPreference() );
    assertNotNull( "Expected URL preference not null",
        preferences.getURLPreference() );
    assertNotNull( "Expected transmission strategy preference not null",
        preferences.getTransmissionStrategyPreference() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.TransmissionProtocolPreferenceImpl#getConfiguration(android.content.SharedPreferences)}
   * .
   */
  public final void testGetConfiguration()
  {
    TransmissionProtocolPreference preferences = new TransmissionProtocolPreferenceImpl("");
    
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    TransmissionProtocolConfiguration expectedConfig =
        createTransmissionProtocolConfiguration();
    
    SharedPreferences.Editor editor = sharedPreferences.edit();
    writeExpectedConfig( preferences, expectedConfig, editor );
    editor.commit();
    
    TransmissionProtocolConfiguration configuration =
        preferences.getConfiguration( sharedPreferences );
    assertEquals( "Unexpected configuration", expectedConfig, configuration );
  }
  
  /**
   * Does write the expected configuration to the shared preferences
   * 
   * @param preferences
   *          the transmission protocol preferences for key identification
   * @param expectedConfig
   *          the expected configuration
   * @param editor
   *          the editor
   */
  public static void
      writeExpectedConfig( TransmissionProtocolPreference preferences,
          TransmissionProtocolConfiguration expectedConfig,
          SharedPreferences.Editor editor )
  {
    editor.putString(
        preferences.getAuthenticationPasswordPreference().getKey(),
        expectedConfig.getAuthPassword() );
    editor.putString( preferences.getAuthenticationUserPreference().getKey(),
        expectedConfig.getUserName() );
    editor.putString( preferences.getURLPreference().getKey(),
        expectedConfig.getURL() );
    editor.putString( preferences.getTransmissionStrategyPreference().getKey(),
        expectedConfig.getTransmissionStrategy().toString() );
  }
  
  /**
   * Method to create a transmission configuration with values set
   * 
   * @return a transmission protocol configuration
   */
  public static TransmissionProtocolConfiguration createTransmissionProtocolConfiguration()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
    config.setAuthPassword( "mypass" );
    config.setURL( "http://0.0.0.0" );
    config.setTransmissionStrategy( ConnectionStrategyDescription.any_available );
    config.setUserName( "me" );
    return config;
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.TransmissionProtocolPreferenceImpl#setDefault(de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration)}
   * and
   * {@link de.unikassel.android.sdcframework.preferences.TransmissionProtocolPreferenceImpl#getDefault()}
   * .
   */
  public final void testDefaults()
  {
    TransmissionProtocolPreference preferences = new TransmissionProtocolPreferenceImpl("");
    
    TransmissionProtocolConfiguration defaultValue =
        createConfigDifferentToDefaults( preferences );
    
    preferences.setDefault( defaultValue );
    
    assertEquals( "Unexpected defaults after update", defaultValue,
        preferences.getDefault() );
  }
  
  /**
   * Does create a configuration with values different to preference defaults
   * 
   * @param preferences
   *          the preferences with defaults to create different configuration
   *          for
   * @return the new configuration
   */
  public static TransmissionProtocolConfiguration createConfigDifferentToDefaults(
      TransmissionProtocolPreference preferences )
  {
    TransmissionProtocolConfiguration diffConfig =
        new TransmissionProtocolConfigurationImpl();
    diffConfig.setAuthPassword( preferences.getAuthenticationPasswordPreference().getDefault()
        + "xy" );
    diffConfig.setURL( preferences.getURLPreference().getDefault()
        + "/upload" );
    diffConfig.setTransmissionStrategy( 
        !ConnectionStrategyDescription.any_available.equals( preferences.getTransmissionStrategyPreference().getDefault() )
        ? ConnectionStrategyDescription.any_available
        : ConnectionStrategyDescription.wlan );
    diffConfig.setUserName( preferences.getAuthenticationUserPreference().getDefault()
        + "&you" );
    return diffConfig;
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.TransmissionProtocolPreferenceImpl#testForKey(java.lang.String)}
   * .
   */
  public final void testTestForKey()
  {
    TransmissionProtocolPreference preferences = new TransmissionProtocolPreferenceImpl("");
    
    assertFalse( "Expected invalid key test fails",
        preferences.testForKey( preferences.getKey() ) );
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getAuthenticationPasswordPreference().getKey() ) );
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getAuthenticationUserPreference().getKey() ) );
    assertTrue( "Expected key test succeded",
        preferences.testForKey( preferences.getURLPreference().getKey() ) );
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getTransmissionStrategyPreference().getKey() ) );
  }
  
}
