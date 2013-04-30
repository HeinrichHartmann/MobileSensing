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
package de.unikassel.android.sdcframework.data.tests;

import java.util.ArrayList;
import java.util.List;

import de.unikassel.android.sdcframework.data.SDCConfiguration;
import de.unikassel.android.sdcframework.data.SensorConfigurationEntry;
import de.unikassel.android.sdcframework.data.TimeProviderConfigurationEntries;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;
import junit.framework.TestCase;

/**
 * Tests for the SDCConfiguration, the SensorConfigurationEntry, the
 * TransmissionConfigurationEntry and the DatabaseConfigurationEntry classes.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSDCConfiguration extends TestCase
{
  /**
   * Test method for construction, setter and getter.
   */
  public final void testSDCConfigurationEntries()
  {
    // test sensor configuration entry
    SensorConfigurationEntry entry = new SensorConfigurationEntry();
    assertNull( "Expected sensor id is null initially", entry.getSensorID() );
    assertNull( "Expected priority is null initially", entry.getPriority() );
    assertEquals( "Expected frequency is 0 initially", 0, entry.getFrequency() );
    assertFalse( "Expected enabled is false initially", entry.getEnabled() );
    
    entry.setSensorID( SensorDeviceIdentifier.Accelerometer.toString() );
    assertEquals( "Expected sensor id set",
        SensorDeviceIdentifier.Accelerometer.toString(), entry.getSensorID() );
    entry.setPriority( SensorDevicePriorities.Level2.toString() );
    assertEquals( "Expected priority set",
        SensorDevicePriorities.Level2.toString(), entry.getPriority() );
    entry.setFrequency( 100000 );
    assertEquals( "Expected frequency set", 100000, entry.getFrequency() );
    entry.setEnabled( true );
    assertTrue( "Expected enabled set to true", entry.getEnabled() );
    
    // test sdc configuration
    SDCConfiguration config = new SDCConfiguration();
    
    assertNull( "Expected flag for broadcasting samples is null initially",
        config.isBroadcastingSamples() );
    config.setBroadcastingSamples( true );
    assertTrue( "Expected flag for broadcasting samples set",
        config.isBroadcastingSamples() );
    
    assertNull( "Expected broadcast frequency is null initially",
        config.getBroadcastFrequency() );
    long broadcastFrequency = 77L;
    config.setBroadcastFrequency( broadcastFrequency );
    assertEquals( "Expected broadcast frequency set", broadcastFrequency,
        config.getBroadcastFrequency().longValue() );
    
    assertNull( "Expected flag for sampling is null initially",
        config.isSamplingEnabled() );
    config.setIsSamplingEnabled( true );
    assertTrue( "Expected flag for sampling set",
        config.isSamplingEnabled() );
    
    assertNull(
        "Expected flag for persistent storage of samples is null initially",
        config.isStoringSamples() );
    config.setStoringSamples( true );
    assertTrue( "Expected flag for persistent storage of samples set",
        config.isStoringSamples() );
    
    assertNull( "Expected flag for transmission of samples is null initially",
        config.isTransmittingSamples() );
    config.setTransmittingSamples( true );
    assertTrue( "Expected flag for transmission of samples set",
        config.isTransmittingSamples() );
    
    assertNotNull( "Expected list initialized",
        config.getListSensorConfigurations() );
    
    assertNull( "Expected maximum db size is null initially",
        config.getDatabaseConfiguration().getMaxDBSize() );
    Long maxDBSize = 4711L;
    config.getDatabaseConfiguration().setMaxDBSize( maxDBSize );
    assertEquals( "Expected maximum db size set", maxDBSize,
        config.getDatabaseConfiguration().getMaxDBSize() );
    
    // test time provider configuration entries
    assertNull( "Expected time provider entries not null initially",
        config.getTimeProviderConfigEntries() );
    config.setTimeProviderConfigEntries( new TimeProviderConfigurationEntries() );
    TimeProviderConfigurationEntries timeProviderEntries =
        config.getTimeProviderConfigEntries();
    assertNotNull( "Expected time providers list initiallized",
        timeProviderEntries.getProviders() );
    List< String > listProviders = new ArrayList< String >();
    listProviders.add( "ptbtime1.ptb.de" );
    listProviders.add( "ptbtime2.ptb.de" );
    listProviders.add( "atom.uhr.de" );
    timeProviderEntries.setProviders( listProviders );
    assertSame(
        "Expected list of providers set",
        listProviders,
        timeProviderEntries.getProviders() );
    TimeProviderErrorStrategyDescription strategy =
        TimeProviderErrorStrategyDescription.IgnoreAndObserveSyncStates;
    timeProviderEntries.setErrorStrategy( strategy.name() );
    assertEquals(
        "Expected error staretgy set set",
        strategy.name(), timeProviderEntries.getErrorStrategy() );
    
    // test database configuration entry
    assertNull(
        "Expected flag for database full sample deletion is null initially",
        config.getDatabaseConfiguration().isDBFullDeletionPriorityBased() );
    config.getDatabaseConfiguration().setDBFullDeletionPriorityBased( true );
    assertTrue(
        "Expected flag for database full sample deletion is done priority based set",
        config.getDatabaseConfiguration().isDBFullDeletionPriorityBased() );
    
    assertNull(
        "Expected record count to delete in case of database full is null initially",
        config.getDatabaseConfiguration().getDBFullDeletionRecordCount() );
    Integer dbFullDeletionRecordCount = 1000;
    config.getDatabaseConfiguration().setDBFullDeletionRecordCount(
        dbFullDeletionRecordCount );
    assertEquals(
        "Expected record count to delete in case of database full is set",
        dbFullDeletionRecordCount,
        config.getDatabaseConfiguration().getDBFullDeletionRecordCount() );
    
    assertNull(
        "Expected time to wait in case of database full is null initially",
        config.getDatabaseConfiguration().getDBFullWaitTime() );
    Long dbFullWaitTime = 445L;
    config.getDatabaseConfiguration().setDBFullWaitTime( dbFullWaitTime );
    assertEquals(
        "Expected time to wait in case of database full is  set",
        dbFullWaitTime, config.getDatabaseConfiguration().getDBFullWaitTime() );
    
    assertNull(
        "Expected strategy definition in case of database full is null initially",
        config.getDatabaseConfiguration().getDBFullStrategy() );
    String dbFullStrategy =
        DBFullStrategyDescription.WAIT_DELETE_NOTIFY.toString();
    config.getDatabaseConfiguration().setDBFullStrategy( dbFullStrategy );
    assertEquals(
        "Expected strategy definition in case of database full is not set",
        dbFullStrategy, config.getDatabaseConfiguration().getDBFullStrategy() );
    
    // test transmission configuration entry
    assertNull(
        "Expected minimum of samples to transfer is null initially",
        config.getTransmissionConfiguration().getMinSampleTransferCount() );
    Integer minSampleTransferCount = 12;
    config.getTransmissionConfiguration().setMinSampleTransferCount(
        minSampleTransferCount );
    assertEquals(
        "Expected minimum of samples to transfer is set",
        minSampleTransferCount,
        config.getTransmissionConfiguration().getMinSampleTransferCount() );
    
    assertNull(
        "Expected maximum of samples to transfer is null initially",
        config.getTransmissionConfiguration().getMaxSampleTransferCount() );
    Integer maxSampleTransferCount = 33;
    config.getTransmissionConfiguration().setMaxSampleTransferCount(
        maxSampleTransferCount );
    assertEquals(
        "Expected maximum of samples to transfer is set",
        maxSampleTransferCount,
        config.getTransmissionConfiguration().getMaxSampleTransferCount() );
    
    assertNull(
        "Expected minimum transfer frequency is null initially",
        config.getTransmissionConfiguration().getMinTransferFrequency() );
    Long minTransferFrequency = 7000L;
    config.getTransmissionConfiguration().setMinTransferFrequency(
        minTransferFrequency );
    assertEquals(
        "Expected minimum transfer frequency is set",
        minTransferFrequency,
        config.getTransmissionConfiguration().getMinTransferFrequency() );
    
    assertNotNull( "Expected protocol config is not null initially",
        config.getTransmissionConfiguration().getProtocolConfig() );
    
    assertNull( "Expected URL is null initially",
        config.getTransmissionConfiguration().getProtocolConfig().getURL() );
    String remoteIP = "http://192.168.0.13";
    config.getTransmissionConfiguration().getProtocolConfig().setURL( remoteIP );
    assertEquals(
        "Expected URL is set",
        remoteIP,
        config.getTransmissionConfiguration().getProtocolConfig().getURL() );
    
    assertNull( "Expected user name is default",
        config.getTransmissionConfiguration().getProtocolConfig().getUserName() );
    String userName = "me";
    config.getTransmissionConfiguration().getProtocolConfig().setUserName(
        userName );
    assertEquals(
        "Expected user name is set",
        userName,
        config.getTransmissionConfiguration().getProtocolConfig().getUserName() );
    
    assertNull(
        "Expected password is default",
        config.getTransmissionConfiguration().getProtocolConfig().getAuthPassword() );
    String authPassword = "secret";
    config.getTransmissionConfiguration().getProtocolConfig().setAuthPassword(
        authPassword );
    assertEquals(
        "Expected password is set",
        authPassword,
        config.getTransmissionConfiguration().getProtocolConfig().getAuthPassword() );
    
    assertNull( "Expected archive type is default",
        config.getTransmissionConfiguration().getArchiveType() );
    String archiveType = "zip";
    config.getTransmissionConfiguration().setArchiveType( archiveType );
    assertEquals(
        "Expected archive type is set",
        archiveType, config.getTransmissionConfiguration().getArchiveType() );
    
    assertNull(
        "Expected connection strategy description is default",
        config.getTransmissionConfiguration().getProtocolConfig().getConnectionStrategy() );
    String connectionStrategy = "????";
    config.getTransmissionConfiguration().getProtocolConfig().setConnectionStrategy(
        connectionStrategy );
    assertEquals(
        "Expected connection strategy description is set",
        connectionStrategy,
        config.getTransmissionConfiguration().getProtocolConfig().getConnectionStrategy() );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  {
    SDCConfiguration config = new SDCConfiguration();
    
    // test for empty collection
    try
    {
      // serialize to xml
      String xml = config.toXML();
      System.out.println( xml );
      
      // serialize a new object from xml to object
      SDCConfiguration config2 =
          GlobalSerializer.fromXML( SDCConfiguration.class, xml );
      
      assertTrue(
          "Expected object serialized from string equal to the original source",
          config2.getListSensorConfigurations().isEmpty() );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization" );
    }
    
    // add sensor entries
    SensorConfigurationEntry entry = new SensorConfigurationEntry();
    entry.setSensorID( SensorDeviceIdentifier.Accelerometer.toString() );
    entry.setEnabled( true );
    entry.setFrequency( 1000 );
    config.getListSensorConfigurations().add( entry );
    entry = new SensorConfigurationEntry();
    entry.setSensorID( SensorDeviceIdentifier.GPS.toString() );
    entry.setEnabled( false );
    entry.setFrequency( 60000 );
    entry.setPriority( SensorDevicePriorities.Level0.toString() );
    config.getListSensorConfigurations().add( entry );
    entry = new SensorConfigurationEntry();
    entry.setSensorID( SensorDeviceIdentifier.Wifi.toString() );
    entry.setEnabled( true );
    entry.setFrequency( 60000 );
    entry.setPriority( SensorDevicePriorities.Level1.toString() );
    config.getListSensorConfigurations().add( entry );
    
    // add common service configuration
    config.setBroadcastingSamples( true );
    config.setBroadcastFrequency( 4711L );
    config.setIsSamplingEnabled( false );
    config.setStoringSamples( true );
    config.setTransmittingSamples( true );
    
    // add time provider configuration entries
    List< String > listProviders = new ArrayList< String >();
    listProviders.add( "ptbtime1.ptb.de" );
    listProviders.add( "ptbtime2.ptb.de" );
    listProviders.add( "atom.uhr.de" );
    TimeProviderErrorStrategyDescription strategy =
        TimeProviderErrorStrategyDescription.IgnoreAndObserveSyncStates;
    config.setTimeProviderConfigEntries( new TimeProviderConfigurationEntries() );
    config.getTimeProviderConfigEntries().setProviders( listProviders );
    config.getTimeProviderConfigEntries().setErrorStrategy( strategy.name() );
    
    // add database configuration
    config.getDatabaseConfiguration().setMaxDBSize( 10485760L );
    config.getDatabaseConfiguration().setDBFullDeletionPriorityBased( true );
    config.getDatabaseConfiguration().setDBFullDeletionRecordCount( 1000 );
    config.getDatabaseConfiguration().setDBFullWaitTime( 10000L );
    config.getDatabaseConfiguration().setDBFullStrategy(
        DBFullStrategyDescription.WAIT_NOTIFY_STOPSERVICE.toString() );
    
    // add transmission configuration
    config.getTransmissionConfiguration().setMinSampleTransferCount( 44 );
    config.getTransmissionConfiguration().setMaxSampleTransferCount( 1500 );
    config.getTransmissionConfiguration().setMinTransferFrequency( 60000L );
    config.getTransmissionConfiguration().setArchiveType( "jar" );
    config.getTransmissionConfiguration().getProtocolConfig().setUserName(
        "name" );
    config.getTransmissionConfiguration().getProtocolConfig().setAuthPassword(
        "omg" );
    config.getTransmissionConfiguration().getProtocolConfig().setURL(
        "http://heise.de" );
    config.getTransmissionConfiguration().getProtocolConfig().setConnectionStrategy(
        "do nothing" );
    
    try
    {
      // serialize to xml
      String xml = config.toXML();
      System.out.println( xml );
      
      // serialize a new object from xml
      SDCConfiguration config2 =
          GlobalSerializer.fromXML( SDCConfiguration.class, xml );
      
      assertEquals( "Expected same list size",
          config.getListSensorConfigurations().size(),
          config2.getListSensorConfigurations().size() );
      
      // test device settings
      for ( int i = 0; i < config.getListSensorConfigurations().size(); ++i )
      {
        SensorConfigurationEntry orgEntry =
            config.getListSensorConfigurations().get( i );
        SensorConfigurationEntry newEntry =
            config2.getListSensorConfigurations().get( i );
        assertEquals( "Expected same sensor id",
            orgEntry.getSensorID(), newEntry.getSensorID() );
        assertEquals( "Expected same frequency",
            orgEntry.getFrequency(), newEntry.getFrequency() );
        assertEquals( "Expected same enabled state",
            orgEntry.getEnabled(), newEntry.getEnabled() );
        assertEquals( "Expected same priority state",
            orgEntry.getPriority(), newEntry.getPriority() );
      }
      
      // test service settings
      assertTrue( "Expected broadcasting of samples enabled",
          config2.isBroadcastingSamples() );
      assertEquals( "Unxpected broadcastfrequency", config.getBroadcastFrequency(),
          config2.getBroadcastFrequency() );
      assertFalse( "Expected sampling disbled",
          config2.isSamplingEnabled() );
      assertTrue( "Expected persistent storing of samples enabled",
          config2.isStoringSamples() );
      assertTrue( "Expected transmission of samples enabled",
          config2.isTransmittingSamples() );
      
      // test time provider settings
      List< String > eventProviders =
          config2.getTimeProviderConfigEntries().getProviders();
      assertEquals( "Unexpected provider count", listProviders.size(),
          eventProviders.size() );
      for ( String provider : listProviders )
      {
        assertTrue( "Expected provider in list " + provider,
            eventProviders.contains( provider ) );
      }
      assertEquals( "Unexpected error strategy", strategy.name(),
          config2.getTimeProviderConfigEntries().getErrorStrategy() );
      
      // test database settings
      assertEquals( "Expected database size set",
          config.getDatabaseConfiguration().getMaxDBSize(),
          config2.getDatabaseConfiguration().getMaxDBSize() );
      assertEquals(
          "Unexpected value for database full deletion done priority based flag",
          config.getDatabaseConfiguration().isDBFullDeletionPriorityBased(),
          config2.getDatabaseConfiguration().isDBFullDeletionPriorityBased() );
      assertEquals( "Unexpected value for deletion record count",
          config.getDatabaseConfiguration().getDBFullDeletionRecordCount(),
          config2.getDatabaseConfiguration().getDBFullDeletionRecordCount() );
      assertEquals( "Unexpected value for wait time",
          config.getDatabaseConfiguration().getDBFullWaitTime(),
          config2.getDatabaseConfiguration().getDBFullWaitTime() );
      assertEquals( "Unexpected value for database full strategy",
          config.getDatabaseConfiguration().getDBFullStrategy(),
          config2.getDatabaseConfiguration().getDBFullStrategy() );
      
      assertEquals( "Unexpected value for minimum sample transfer count",
          config.getTransmissionConfiguration().getMinSampleTransferCount(),
          config2.getTransmissionConfiguration().getMinSampleTransferCount() );
      assertEquals( "Unexpected value for maximum sample transfer count",
          config.getTransmissionConfiguration().getMaxSampleTransferCount(),
          config2.getTransmissionConfiguration().getMaxSampleTransferCount() );
      assertEquals( "Unexpected value for minimum transfer frequency",
          config.getTransmissionConfiguration().getMinTransferFrequency(),
          config2.getTransmissionConfiguration().getMinTransferFrequency() );
      assertEquals( "Unexpected value for remote host",
          config.getTransmissionConfiguration().getProtocolConfig().getURL(),
          config2.getTransmissionConfiguration().getProtocolConfig().getURL() );
      assertEquals(
          "Unexpected value for user name",
          config.getTransmissionConfiguration().getProtocolConfig().getUserName(),
          config2.getTransmissionConfiguration().getProtocolConfig().getUserName() );
      assertEquals(
          "Unexpected value for password",
          config.getTransmissionConfiguration().getProtocolConfig().getAuthPassword(),
          config2.getTransmissionConfiguration().getProtocolConfig().getAuthPassword() );
      assertEquals( "Unexpected value for archive type",
          config.getTransmissionConfiguration().getArchiveType(),
          config2.getTransmissionConfiguration().getArchiveType() );
      assertEquals(
          "Unexpected value for connection strategy",
          config.getTransmissionConfiguration().getProtocolConfig().getConnectionStrategy(),
          config2.getTransmissionConfiguration().getProtocolConfig().getConnectionStrategy() );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization" );
    }
  }
}
