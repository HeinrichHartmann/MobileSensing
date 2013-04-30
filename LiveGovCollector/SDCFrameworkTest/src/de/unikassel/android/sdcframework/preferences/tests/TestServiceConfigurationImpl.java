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

import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;
import de.unikassel.android.sdcframework.data.SDCConfiguration;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;

/**
 * Tests for the service configuration.
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "DefaultLocale" )
public class TestServiceConfigurationImpl extends InstrumentationTestCase
{
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }
  
  /**
   * Test for preconditions
   */
  public final void testPreconditions()
  {
    AssetManager assetManager =
        getInstrumentation().getContext().getResources().getAssets();
    try
    {
      assetManager.open( TestSDCConfigurationManager.CONFIGFILE );
    }
    catch ( IOException e )
    {
      fail( "Configuration file not found! Please correct name if is has been changes in the framework." );
    }
  }
  
  /**
   * Test method for construction
   * {@link de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl#ServiceConfigurationImpl()}
   * .
   */
  public final void testServiceConfigurationImpl()
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    
    assertFalse( "Unexpected broadcast flag after initalization",
        config.isBroadcastingSamples() );
    assertEquals(
        "Unexpected broadcast frequency after initalization", 0L,
        config.getBroadcastFrequency() );
    assertFalse( "Unexpected flag for sample location",
        config.isAddingSampleLocation() );
    assertFalse( "Unexpected persistent storage flag after initalization",
        config.isStoringSamples() );
    assertFalse( "Unexpected sampling enabled flag after initalization",
        config.isSamplingEnabled() );
    assertFalse( "Unexpected transmission flag after initalization",
        config.isTransmittingSamples() );
    
    assertEquals(
        "Unexpected value for maximum database size after intialization", 0L,
        config.getMaximumDatabaseSize() );
    assertFalse(
        "Unexpected value for DB full deletion priority recognition",
        config.isDBFullDeletionPriorityBased() );
    assertEquals(
        "Unexpected value for DB full deletion record count", 0,
        config.getDBFullDeletionRecordCount() );
    assertEquals(
        "Unexpected value for DB full wait time", 0L,
        config.getDBFullWaitTime() );
    assertNull( "Unexpected value for DB full strategy",
        config.getDBFullStrategy() );
  }
  
  /**
   * Test method for setter and getter
   */
  public final void testSetterWithGetter()
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    
    config.setBroadcastingSamples( true );
    assertTrue( "Expected broadcast flag set", config.isBroadcastingSamples() );
    
    long frequency = 4711L;
    config.setBroadcastFrequency( frequency );
    assertEquals( "Unxpected broadcast frequency", frequency,
        config.getBroadcastFrequency() );
    
    config.setSamplingEnabled( false );
    assertFalse( "Expected sampling disabled",
        config.isSamplingEnabled() );
    
    config.setIsAddingSampleLocation( true );
    assertTrue( "Expected sample location flag set",
        config.isAddingSampleLocation() );
    
    config.setStoringSamples( true );
    assertTrue( "Expected persistent storage flag cleared",
        config.isStoringSamples() );
    
    config.setTransmittingSamples( true );
    assertTrue( "Expected transmission flag cleared", config.isStoringSamples() );
    
    long maxDBSize = 5120L;
    config.setMaximumDatabaseSize( maxDBSize );
    assertEquals( "Expected value for maximum databse size set", maxDBSize,
        config.getMaximumDatabaseSize() );
    
    config.setDBFullDeletionPriorityBased( true );
    assertTrue( "Expected value for DB full deletion priority recognition set",
        config.isDBFullDeletionPriorityBased() );
    
    int recordCount = 200;
    config.setDBFullDeletionRecordCount( recordCount );
    assertEquals(
        "Unexpected value for DB full deletion record count", recordCount,
        config.getDBFullDeletionRecordCount() );
    
    long waitTime = 900L;
    config.setDBFullWaitTime( waitTime );
    assertEquals(
        "Unexpected value for DB full wait time", waitTime,
        config.getDBFullWaitTime() );
    
    DBFullStrategyDescription strategy =
        DBFullStrategyDescription.WAIT_NOTIFY_STOPSERVICE;
    config.setDBFullStrategy( strategy );
    assertEquals(
        "Unexpected value for DB full strategy", strategy,
        config.getDBFullStrategy() );
  }
  
  /**
   * Test method for comparison
   */
  public final void testComparison()
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    config.setBroadcastingSamples( true );
    config.setBroadcastFrequency( 815L );
    config.setSamplingEnabled( true );
    config.setIsAddingSampleLocation( true );
    config.setStoringSamples( true );
    config.setTransmittingSamples( true );
    config.setMaximumDatabaseSize( 10000L );
    config.setDBFullDeletionPriorityBased( true );
    config.setDBFullDeletionRecordCount( 1000 );
    config.setDBFullWaitTime( 10000L );
    config.setDBFullStrategy( DBFullStrategyDescription.WAIT_DELETE_NOTIFY );
    TransmissionConfiguration transmissionConfiguration =
        config.getTransmissionConfiguration();
    transmissionConfiguration.setArchiveType( ArchiveTypes.jar );
    transmissionConfiguration.getProtocolConfiguration().setAuthPassword(
        "secret" );
    transmissionConfiguration.setMaxSampleTransferCount( 200 );
    transmissionConfiguration.setMinSampleTransferCount( 100 );
    transmissionConfiguration.setMinTransferFrequency( 10000000L );
    transmissionConfiguration.getProtocolConfiguration().setURL(
        "http://123.456.0.789" );
    transmissionConfiguration.getProtocolConfiguration().setUserName( "user" );
    transmissionConfiguration.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.any_available );
    
    ServiceConfiguration anotherConfig = new ServiceConfigurationImpl();
    assertFalse( "Expected that configurations are not equal",
        config.equals( anotherConfig ) );
    
    anotherConfig.setBroadcastingSamples( config.isBroadcastingSamples() );
    anotherConfig.setBroadcastFrequency( config.getBroadcastFrequency() );
    anotherConfig.setSamplingEnabled( config.isSamplingEnabled() );
    anotherConfig.setIsAddingSampleLocation( config.isAddingSampleLocation() );
    anotherConfig.setStoringSamples( config.isStoringSamples() );
    anotherConfig.setTransmittingSamples( config.isTransmittingSamples() );
    anotherConfig.setMaximumDatabaseSize( config.getMaximumDatabaseSize() );
    anotherConfig.setDBFullDeletionPriorityBased( config.isDBFullDeletionPriorityBased() );
    anotherConfig.setDBFullDeletionRecordCount( config.getDBFullDeletionRecordCount() );
    anotherConfig.setDBFullWaitTime( config.getDBFullWaitTime() );
    anotherConfig.setDBFullStrategy( config.getDBFullStrategy() );
    TransmissionConfiguration anotherTransmissionConfiguration =
        anotherConfig.getTransmissionConfiguration();
    anotherTransmissionConfiguration.setArchiveType(
        transmissionConfiguration.getArchiveType() );
    anotherTransmissionConfiguration.getProtocolConfiguration().setAuthPassword(
        transmissionConfiguration.getProtocolConfiguration().getAuthPassword() );
    anotherTransmissionConfiguration.setMaxSampleTransferCount(
        transmissionConfiguration.getMaxSampleTransferCount() );
    anotherTransmissionConfiguration.setMinSampleTransferCount(
        transmissionConfiguration.getMinSampleTransferCount() );
    anotherTransmissionConfiguration.setMinTransferFrequency(
        transmissionConfiguration.getMinTransferFrequency() );
    anotherTransmissionConfiguration.getProtocolConfiguration().setURL(
        transmissionConfiguration.getProtocolConfiguration().getURL() );
    anotherTransmissionConfiguration.getProtocolConfiguration().setUserName(
        transmissionConfiguration.getProtocolConfiguration().getUserName() );
    anotherTransmissionConfiguration.getProtocolConfiguration().setTransmissionStrategy(
        transmissionConfiguration.getProtocolConfiguration().getTransmissionStrategy() );
    
    assertTrue( "Expected that configurations are equal",
        config.equals( anotherConfig ) );
  }
  
  /**
   * Test method for update by another configuration
   * {@link de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl#update(de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration)}
   * .
   */
  public final void testUpdateByConfiguration()
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    
    ServiceConfiguration anotherConfig = new ServiceConfigurationImpl();
    anotherConfig.setBroadcastingSamples( true );
    anotherConfig.setBroadcastFrequency( 800L );
    anotherConfig.setSamplingEnabled( !config.isSamplingEnabled() );
    anotherConfig.setIsAddingSampleLocation( true );
    anotherConfig.setStoringSamples( true );
    anotherConfig.setTransmittingSamples( true );
    anotherConfig.setMaximumDatabaseSize( 4711L );
    anotherConfig.setDBFullDeletionPriorityBased( true );
    anotherConfig.setDBFullDeletionRecordCount( 1000 );
    anotherConfig.setDBFullWaitTime( 10000L );
    anotherConfig.setDBFullStrategy( DBFullStrategyDescription.WAIT_DELETE_NOTIFY );
    anotherConfig.getTransmissionConfiguration().setArchiveType(
        ArchiveTypes.jar );
    anotherConfig.getTransmissionConfiguration().getProtocolConfiguration().setAuthPassword(
        "secret" );
    anotherConfig.getTransmissionConfiguration().setMaxSampleTransferCount( 200 );
    anotherConfig.getTransmissionConfiguration().setMinSampleTransferCount( 100 );
    anotherConfig.getTransmissionConfiguration().setMinTransferFrequency(
        10000000L );
    anotherConfig.getTransmissionConfiguration().getProtocolConfiguration().setURL(
        "http://123.456.0.789" );
    anotherConfig.getTransmissionConfiguration().getProtocolConfiguration().setUserName(
        "user" );
    anotherConfig.getTransmissionConfiguration().getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.any_available );
    
    anotherConfig.getLogTransferConfiguration().setURL( "http://localhost/link" );
    anotherConfig.getLogTransferConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.wlan );
    
    assertFalse( "Expected configurations not equal",
        config.equals( anotherConfig ) );
    config.update( anotherConfig );
    assertTrue( "Expected same configuration after update",
        config.equals( anotherConfig ) );
  }
  
  /**
   * Test method for update by SDCCOnfiguration
   * {@link de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl#update(de.unikassel.android.sdcframework.data.SDCConfiguration)
   * )} .
   */
  public final void testUpdateBySDCConfiguration()
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    SDCConfiguration serializableConfig = null;
    try
    {
      serializableConfig =
          TestSDCConfigurationManager.readSDCConfigurationFromResource(
              TestSDCConfigurationManager.CONFIGFILE,
              getInstrumentation().getContext().getResources() );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Failed to read SDC configuration from file: " + e.getMessage() );
    }
    
    ServiceConfiguration testConfig = new ServiceConfigurationImpl();
    testConfig.setBroadcastFrequency( 
        serializableConfig.getBroadcastFrequency() );
    testConfig.setBroadcastingSamples(
        serializableConfig.isBroadcastingSamples() );
    testConfig.setSamplingEnabled(
        serializableConfig.isSamplingEnabled() );
    testConfig.setIsAddingSampleLocation(
        serializableConfig.isAddingSampleLocation() );
    testConfig.setStoringSamples(
        serializableConfig.isStoringSamples() );
    testConfig.setTransmittingSamples(
        serializableConfig.isTransmittingSamples() );
    testConfig.setMaximumDatabaseSize(
        serializableConfig.getDatabaseConfiguration().getMaxDBSize() );
    testConfig.setDBFullDeletionPriorityBased(
        serializableConfig.getDatabaseConfiguration().isDBFullDeletionPriorityBased() );
    testConfig.setDBFullDeletionRecordCount(
        serializableConfig.getDatabaseConfiguration().getDBFullDeletionRecordCount() );
    testConfig.setDBFullWaitTime(
        serializableConfig.getDatabaseConfiguration().getDBFullWaitTime() );
    testConfig.setDBFullStrategy(
        DBFullStrategyDescription.valueOf(
            serializableConfig.getDatabaseConfiguration().getDBFullStrategy().toUpperCase() ) );
    
    TransmissionConfiguration transmissionConfiguration =
        testConfig.getTransmissionConfiguration();
    transmissionConfiguration.setArchiveType(
        ArchiveTypes.valueOf( serializableConfig.getTransmissionConfiguration().getArchiveType() ) );
    transmissionConfiguration.getProtocolConfiguration().setAuthPassword(
        serializableConfig.getTransmissionConfiguration().getProtocolConfig().getAuthPassword() );
    transmissionConfiguration.setMaxSampleTransferCount(
        serializableConfig.getTransmissionConfiguration().getMaxSampleTransferCount() );
    transmissionConfiguration.setMinSampleTransferCount(
        serializableConfig.getTransmissionConfiguration().getMinSampleTransferCount() );
    transmissionConfiguration.setMinTransferFrequency(
        serializableConfig.getTransmissionConfiguration().getMinTransferFrequency() );
    transmissionConfiguration.getProtocolConfiguration().setURL(
        serializableConfig.getTransmissionConfiguration().getProtocolConfig().getURL() );
    transmissionConfiguration.getProtocolConfiguration().setUserName(
        serializableConfig.getTransmissionConfiguration().getProtocolConfig().getUserName() );
    transmissionConfiguration.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.valueOf(
            serializableConfig.getTransmissionConfiguration().getProtocolConfig().getConnectionStrategy().toLowerCase() ) );
    
    TransmissionProtocolConfiguration logTransferConfiguration =
        testConfig.getLogTransferConfiguration();
    logTransferConfiguration.setAuthPassword( serializableConfig.getLogTransferConfiguration().getAuthPassword() );
    logTransferConfiguration.setURL( serializableConfig.getLogTransferConfiguration().getURL() );
    logTransferConfiguration.setUserName( serializableConfig.getLogTransferConfiguration().getUserName() );
    logTransferConfiguration.setTransmissionStrategy(
        ConnectionStrategyDescription.valueOf(
            serializableConfig.getLogTransferConfiguration().getConnectionStrategy().toLowerCase() ) );
    
    assertFalse( "Expected configurations not equal",
        config.equals( testConfig ) );
    config.update( serializableConfig );
    assertTrue( "Expected same configuration after update",
        config.equals( testConfig ) );
  }
  
}
