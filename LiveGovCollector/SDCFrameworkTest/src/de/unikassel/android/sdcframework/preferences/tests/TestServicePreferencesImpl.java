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

import de.unikassel.android.sdcframework.data.Weekday;
import de.unikassel.android.sdcframework.data.WeekdaySchedulerAction;
import de.unikassel.android.sdcframework.data.WeekdaySchedule;
import de.unikassel.android.sdcframework.data.WeekdayScheduleEntry;
import de.unikassel.android.sdcframework.data.WeeklySchedule;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.ServicePreferencesImpl;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.ServicePreferences;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.AndroidTestCase;

/**
 * Tests for the framework service preferences.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestServicePreferencesImpl extends AndroidTestCase
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
  public final void testServicePreferencesImpl()
  {
    ServicePreferences preferences = new ServicePreferencesImpl();
    
    assertNotNull( "Expected key not null", preferences.getKey() );
    assertTrue( "Unexpected default value",
        preferences.getDefault() instanceof ServiceConfiguration );
    
    assertNotNull( "Expected sample broadcast preference not null",
        preferences.getSampleBroadcastsEnabledPreference() );
    assertNotNull( "Expected broadcast frequency not null",
        preferences.getBroadcastFrequencyPreference() );
    assertNotNull( "Expected sampling enabled preference not null",
        preferences.getSamplingEnabledPreference() );
    assertNotNull( "Expected sample location add preference not null",
        preferences.getSampleLocationFixEnabledPreference() );
    assertNotNull( "Expected persistent storage enabled preference not null",
        preferences.getPersistentStorageEnabledPreference() );
    assertNotNull( "Expected transmission enabled preference not null",
        preferences.getTransmissionEnabledPreference() );
    
    assertNotNull( "Expected database max size preference not null",
        preferences.getDBMaxSizePreference() );
    assertNotNull(
        "Expected database full deletion priority recognition preference not null",
        preferences.getDbFullDeletionIsPriorityBasedPreference() );
    assertNotNull(
        "Expected database full deletion record count preference not null",
        preferences.getDbFullDeletionRecordCountPreference() );
    assertNotNull( "Expected database full wait time preference not null",
        preferences.getDbFullWaitTimePreference() );
    assertNotNull( "Expected database full strategy preference not null",
        preferences.getDbFullStrategyPreference() );
    
    assertNotNull( "Expected transmission preference not null",
        preferences.getTransmissionPreference() );
    
    assertNotNull( "Expected schedule preference not null",
        preferences.getWeeklySchedulePreference() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ServicePreferencesImpl#getConfiguration(android.content.SharedPreferences)}
   * .
   */
  public final void testGetConfiguration()
  {
    ServicePreferences preferences = new ServicePreferencesImpl();
    
    SharedPreferences sharedPreferences =
        PreferenceManager.getDefaultSharedPreferences( getContext() );
    SharedPreferences.Editor editor = sharedPreferences.edit();
    
    ServiceConfiguration expectedConfig = new ServiceConfigurationImpl();
    expectedConfig.setBroadcastingSamples( true );
    expectedConfig.setBroadcastFrequency( 333L );
    expectedConfig.setSamplingEnabled( false );
    expectedConfig.setIsAddingSampleLocation( true );
    expectedConfig.setStoringSamples( false );
    expectedConfig.setTransmittingSamples( true );
    expectedConfig.setMaximumDatabaseSize( 471111111L );
    expectedConfig.setDBFullDeletionPriorityBased( true );
    expectedConfig.setDBFullDeletionRecordCount( 345 );
    expectedConfig.setDBFullWaitTime( 77L );
    expectedConfig.setDBFullStrategy( DBFullStrategyDescription.WAIT_NOTIFY_STOPSERVICE );
    expectedConfig.setTransmissionConfiguration(
        TestTransmissionPreferenceImpl.createTransmissionConfiguration() );
    expectedConfig.setLogTransferConfiguration(
        TestTransmissionProtocolPreferenceImpl.createTransmissionProtocolConfiguration() );
    expectedConfig.setWeeklySchedule( createWeeklySchedule() );
    
    editor.putBoolean(
        preferences.getSampleBroadcastsEnabledPreference().getKey(),
        expectedConfig.isBroadcastingSamples() );
    editor.putString( preferences.getBroadcastFrequencyPreference().getKey(),
        Long.toString( expectedConfig.getBroadcastFrequency() ) );
    editor.putBoolean(
        preferences.getSamplingEnabledPreference().getKey(),
        expectedConfig.isSamplingEnabled() );
    editor.putBoolean(
        preferences.getSampleLocationFixEnabledPreference().getKey(),
        expectedConfig.isAddingSampleLocation() );
    editor.putBoolean(
        preferences.getPersistentStorageEnabledPreference().getKey(),
        expectedConfig.isStoringSamples() );
    editor.putBoolean(
        preferences.getTransmissionEnabledPreference().getKey(),
        expectedConfig.isTransmittingSamples() );
    
    editor.putString( preferences.getDBMaxSizePreference().getKey(),
        Long.toString( expectedConfig.getMaximumDatabaseSize() ) );
    editor.putBoolean(
        preferences.getDbFullDeletionIsPriorityBasedPreference().getKey(),
        expectedConfig.isDBFullDeletionPriorityBased() );
    editor.putString(
        preferences.getDbFullDeletionRecordCountPreference().getKey(),
        Integer.toString( expectedConfig.getDBFullDeletionRecordCount() ) );
    editor.putString( preferences.getDbFullWaitTimePreference().getKey(),
        Long.toString( expectedConfig.getDBFullWaitTime() ) );
    editor.putString( preferences.getDbFullStrategyPreference().getKey(),
        expectedConfig.getDBFullStrategy().toString() );
    editor.putString( preferences.getWeeklySchedulePreference().getKey(),
        expectedConfig.getWeeklySchedule().toString() );
    
    TestTransmissionPreferenceImpl.writeExpectedConfig(
        preferences.getTransmissionPreference(),
        expectedConfig.getTransmissionConfiguration(), editor );
    
    TestTransmissionProtocolPreferenceImpl.writeExpectedConfig(
        preferences.getLogTransferPreference(),
        expectedConfig.getLogTransferConfiguration(), editor );
    editor.commit();
    
    ServiceConfiguration configuration =
        preferences.getConfiguration( sharedPreferences );
    assertEquals( "Unexpected configuration", expectedConfig, configuration );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ServicePreferencesImpl#getDefault()}
   * and for
   * {@link de.unikassel.android.sdcframework.preferences.ServicePreferencesImpl#setDefault(de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration)}
   * .
   */
  public final void testDefaults()
  {
    ServicePreferences preferences = new ServicePreferencesImpl();
    
    ServiceConfiguration defaultValue =
        new ServiceConfigurationImpl();
    defaultValue.setBroadcastingSamples(
        !preferences.getSampleBroadcastsEnabledPreference().getDefault() );
    defaultValue.setBroadcastFrequency(
        preferences.getBroadcastFrequencyPreference().getDefault() + 1200L );
    defaultValue.setSamplingEnabled(
        !preferences.getSamplingEnabledPreference().getDefault() );
    defaultValue.setIsAddingSampleLocation(
        !preferences.getSampleLocationFixEnabledPreference().getDefault() );
    defaultValue.setStoringSamples(
        !preferences.getPersistentStorageEnabledPreference().getDefault() );
    defaultValue.setTransmittingSamples(
        !preferences.getTransmissionEnabledPreference().getDefault() );
    defaultValue.setMaximumDatabaseSize(
        preferences.getDBMaxSizePreference().getDefault() + 10L );
    defaultValue.setDBFullDeletionPriorityBased(
        !preferences.getDbFullDeletionIsPriorityBasedPreference().getDefault() );
    defaultValue.setDBFullDeletionRecordCount(
        preferences.getDbFullDeletionRecordCountPreference().getDefault() + 12 );
    defaultValue.setDBFullWaitTime(
        preferences.getDbFullWaitTimePreference().getDefault() + 5 );
    DBFullStrategyDescription defaultStrategy =
        preferences.getDbFullStrategyPreference().getDefault();
    defaultValue.setDBFullStrategy( defaultStrategy == DBFullStrategyDescription.WAIT_DELETE_NOTIFY
        ? DBFullStrategyDescription.WAIT_NOTIFY_STOPSERVICE
        : DBFullStrategyDescription.WAIT_DELETE_NOTIFY );
    defaultValue.setTransmissionConfiguration(
        TestTransmissionPreferenceImpl.createConfigDifferentToDefaults(
            preferences.getTransmissionPreference() ) );
    defaultValue.setLogTransferConfiguration(
        TestTransmissionProtocolPreferenceImpl.createConfigDifferentToDefaults(
            preferences.getLogTransferPreference() ) );
    defaultValue.setWeeklySchedule( createWeeklySchedule() );
    
    preferences.setDefault( defaultValue );
    
    assertEquals( "Unexpected defaults after update", defaultValue,
        preferences.getDefault() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ServicePreferencesImpl#testForKey(java.lang.String)}
   * .
   */
  public final void testTestForKey()
  {
    ServicePreferences preferences = new ServicePreferencesImpl();
    String key1 = preferences.getDBMaxSizePreference().getKey();
    String key2 = preferences.getSampleBroadcastsEnabledPreference().getKey();
    String key3 =
        preferences.getDbFullDeletionIsPriorityBasedPreference().getKey();
    String key4 = preferences.getDbFullDeletionRecordCountPreference().getKey();
    String key5 = preferences.getDbFullWaitTimePreference().getKey();
    String key6 = preferences.getDbFullStrategyPreference().getKey();
    String key7 = preferences.getPersistentStorageEnabledPreference().getKey();
    String key8 = preferences.getTransmissionEnabledPreference().getKey();
    String key9 = preferences.getSampleLocationFixEnabledPreference().getKey();
    String key10 =
        preferences.getTransmissionPreference().getProtocolPreference().getURLPreference().getKey();
    String key11 =
        preferences.getTransmissionPreference().getProtocolPreference().getAuthenticationPasswordPreference().getKey();
    String key12 =
        preferences.getSamplingEnabledPreference().getKey();
    String key13 =
        preferences.getBroadcastFrequencyPreference().getKey();
    String key14 = preferences.getWeeklySchedulePreference().getKey();
    String invalidKey = "invalid";
    
    assertFalse( "Expected invalid key test fails",
        preferences.testForKey( invalidKey ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key1 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key2 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key3 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key4 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key5 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key6 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key7 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key8 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key9 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key10 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key11 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key12 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key13 ) );
    assertTrue( "Expected valid key test successful",
        preferences.testForKey( key14 ) );
  }


  /**
   * Method to create a test schedule.
   * 
   * @return a weekly schedule for test
   */
  public final static WeeklySchedule createWeeklySchedule()
  {
    WeeklySchedule schedule = new WeeklySchedule();
    WeekdaySchedule daySchedule = schedule.getScheduleForWeekday( Weekday.Monday );
    daySchedule.addEntry( new WeekdayScheduleEntry( 9007, WeekdaySchedulerAction.StartService ) );
    daySchedule.addEntry( new WeekdayScheduleEntry( 12103, WeekdaySchedulerAction.StopService ) );
    return schedule;
  }
}
