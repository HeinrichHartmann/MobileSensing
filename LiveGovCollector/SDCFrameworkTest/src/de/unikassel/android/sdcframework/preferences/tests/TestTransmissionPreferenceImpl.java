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
import de.unikassel.android.sdcframework.preferences.TransmissionConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.TransmissionPreferenceImpl;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;

/**
 * The SDCFrameworkTest project.
 */

/**
 * Tests for the transmission preferences.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestTransmissionPreferenceImpl extends AndroidTestCase
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
    TransmissionPreference preferences = new TransmissionPreferenceImpl();
    
    assertNotNull( "Expected key not null", preferences.getKey() );
    assertTrue( "Unexpected default value",
        preferences.getDefault() instanceof TransmissionConfiguration );
    
    assertNotNull( "Expected archive type preference not null",
        preferences.getArchiveTypePreference() );
    assertNotNull( "Expected password preference not null",
        preferences.getProtocolPreference().getAuthenticationPasswordPreference() );
    assertNotNull( "Expected user name preference not null",
        preferences.getProtocolPreference().getAuthenticationUserPreference() );
    assertNotNull( "Expected maximum sample count preference not null",
        preferences.getMaxSampleTransferCountPreference() );
    assertNotNull( "Expected minimum sample count preference not null",
        preferences.getMinSampleTransferCountPreference() );
    assertNotNull( "Expected transfer frequency preference not null",
        preferences.getMinTransferFrequencyPreference() );
    assertNotNull( "Expected URL preference not null",
        preferences.getProtocolPreference().getURLPreference() );
    assertNotNull( "Expected encryption enabled preference not null",
        preferences.getEncryptionEnabledPreference() );
    assertNotNull( "Expected transmission strategy preference not null",
        preferences.getProtocolPreference().getTransmissionStrategyPreference() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.TransmissionPreferenceImpl#getConfiguration(android.content.SharedPreferences)}
   * .
   */
  public final void testGetConfiguration()
  {
    TransmissionPreference preferences = new TransmissionPreferenceImpl();
    
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    TransmissionConfiguration expectedConfig =
        createTransmissionConfiguration();
    
    SharedPreferences.Editor editor = sharedPreferences.edit();
    writeExpectedConfig( preferences, expectedConfig, editor );
    editor.commit();
    
    TransmissionConfiguration configuration =
        preferences.getConfiguration( sharedPreferences );
    assertEquals( "Unexpected configuration", expectedConfig, configuration );
  }
  
  /**
   * Does write the expected configuration to the shared preferences
   * 
   * @param preferences
   *          the transmission preferences for key identification
   * @param expectedConfig
   *          the expected configuration
   * @param editor
   *          the editor
   */
  public static void
      writeExpectedConfig( TransmissionPreference preferences,
          TransmissionConfiguration expectedConfig,
          SharedPreferences.Editor editor )
  {
    editor.putString( preferences.getArchiveTypePreference().getKey(),
        expectedConfig.getArchiveType().toString() );
    editor.putString(
        preferences.getProtocolPreference().getAuthenticationPasswordPreference().getKey(),
        expectedConfig.getProtocolConfiguration().getAuthPassword() );
    editor.putString( preferences.getProtocolPreference().getAuthenticationUserPreference().getKey(),
        expectedConfig.getProtocolConfiguration().getUserName() );
    editor.putString(
        preferences.getMaxSampleTransferCountPreference().getKey(),
        Integer.toString( expectedConfig.getMaxSampleTransferCount() ) );
    editor.putString(
        preferences.getMinSampleTransferCountPreference().getKey(),
        Integer.toString( expectedConfig.getMinSampleTransferCount() ) );
    editor.putString( preferences.getMinTransferFrequencyPreference().getKey(),
        Long.toString( expectedConfig.getMinTransferFrequency() ) );
    editor.putString( preferences.getProtocolPreference().getURLPreference().getKey(),
        expectedConfig.getProtocolConfiguration().getURL() );
    editor.putBoolean(
            preferences.getEncryptionEnabledPreference().getKey(),
            expectedConfig.isEncryptionEnabled() );
    editor.putString( preferences.getProtocolPreference().getTransmissionStrategyPreference().getKey(),
        expectedConfig.getProtocolConfiguration().getTransmissionStrategy().toString() );
  }
  
  /**
   * Method to create a transmission configuration with values set
   * 
   * @return a transmission configuration
   */
  public static TransmissionConfiguration createTransmissionConfiguration()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( ArchiveTypes.jar );
    config.getProtocolConfiguration().setAuthPassword( "mypass" );
    config.setMaxSampleTransferCount( 2000 );
    config.setMinSampleTransferCount( 7 );
    config.setMinTransferFrequency( 4711L );
    config.getProtocolConfiguration().setURL( "http://0.0.0.0" );
    config.setEncryptionEnabled( true );
    config.getProtocolConfiguration().setTransmissionStrategy( ConnectionStrategyDescription.any_available );
    config.getProtocolConfiguration().setUserName( "me" );
    return config;
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.TransmissionPreferenceImpl#setDefault(de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration)}
   * and
   * {@link de.unikassel.android.sdcframework.preferences.TransmissionPreferenceImpl#getDefault()}
   * .
   */
  public final void testDefaults()
  {
    TransmissionPreference preferences = new TransmissionPreferenceImpl();
    
    TransmissionConfiguration defaultValue =
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
  public static TransmissionConfiguration createConfigDifferentToDefaults(
      TransmissionPreference preferences )
  {
    TransmissionConfiguration diffConfig =
        new TransmissionConfigurationImpl();
    diffConfig.setArchiveType( ArchiveTypes.jar.equals( preferences.getArchiveTypePreference().getDefault() )
        ? ArchiveTypes.zip : ArchiveTypes.jar );
    diffConfig.getProtocolConfiguration().setAuthPassword( preferences.getProtocolPreference().getAuthenticationPasswordPreference().getDefault()
        + "xy" );
    diffConfig.setMaxSampleTransferCount( preferences.getMaxSampleTransferCountPreference().getDefault() + 1 );
    diffConfig.setMinSampleTransferCount( preferences.getMinSampleTransferCountPreference().getDefault() + 1 );
    diffConfig.setMinTransferFrequency( preferences.getMinTransferFrequencyPreference().getDefault() + 1L );
    diffConfig.getProtocolConfiguration().setURL( preferences.getProtocolPreference().getURLPreference().getDefault()
        + "/upload" );
    Boolean encryptionDefault = preferences.getEncryptionEnabledPreference().getDefault();
    diffConfig.setEncryptionEnabled( encryptionDefault == null ? true : !encryptionDefault );
    diffConfig.getProtocolConfiguration().setTransmissionStrategy( 
        !ConnectionStrategyDescription.any_available.equals( preferences.getProtocolPreference().getTransmissionStrategyPreference().getDefault() )
        ? ConnectionStrategyDescription.any_available
        : ConnectionStrategyDescription.wlan );
    diffConfig.getProtocolConfiguration().setUserName( preferences.getProtocolPreference().getAuthenticationUserPreference().getDefault()
        + "&you" );
    return diffConfig;
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.TransmissionPreferenceImpl#testForKey(java.lang.String)}
   * .
   */
  public final void testTestForKey()
  {
    TransmissionPreference preferences = new TransmissionPreferenceImpl();
    
    assertFalse( "Expected invalid key test fails",
        preferences.testForKey( preferences.getKey() ) );
    
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getArchiveTypePreference().getKey() ) );
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getProtocolPreference().getAuthenticationPasswordPreference().getKey() ) );
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getProtocolPreference().getAuthenticationUserPreference().getKey() ) );
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getMaxSampleTransferCountPreference().getKey() ) );
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getMinSampleTransferCountPreference().getKey() ) );
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getMinTransferFrequencyPreference().getKey() ) );
    assertTrue( "Expected key test succeded",
        preferences.testForKey( preferences.getProtocolPreference().getURLPreference().getKey() ) );
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getEncryptionEnabledPreference().getKey() ) );
    assertTrue(
        "Expected key test succeded",
        preferences.testForKey( preferences.getProtocolPreference().getTransmissionStrategyPreference().getKey() ) );
  }
  
}
