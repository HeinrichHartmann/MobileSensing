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
import de.unikassel.android.sdcframework.data.TransmissionConfigurationEntry;
import de.unikassel.android.sdcframework.preferences.TransmissionConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;

/**
 * Tests for the transmission configuration.
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "DefaultLocale" )
public class TestTransmissionConfigurationImpl extends InstrumentationTestCase
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
   * {@link de.unikassel.android.sdcframework.preferences.TransmissionConfigurationImpl#TransmissionConfigurationImpl()}
   * .
   */
  public final void testTransmissionConfigurationImpl()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    
    assertEquals( "Unexpected value for transfer frequency", 0L,
        config.getMinTransferFrequency() );
    assertEquals( "Unexpected value for minimum sample count", 0,
        config.getMinSampleTransferCount() );
    assertEquals( "Unexpected value for maximum sample count", 0,
        config.getMaxSampleTransferCount() );
    assertNull( "Expected archive type not set", config.getArchiveType() );
    assertNull( "Expected password not set", config.getProtocolConfiguration().getAuthPassword() );
    assertNull( "Expected user not set", config.getProtocolConfiguration().getUserName() );
    assertNull( "Expected URL not set", config.getProtocolConfiguration().getURL() );
    assertNull( "Expected transmission strategy not set",
        config.getProtocolConfiguration().getTransmissionStrategy() );
  }
  
  /**
   * Test method for setter and getter
   */
  public final void testSetterWithGetter()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    
    long minFrequency = 77777777;
    config.setMinTransferFrequency( minFrequency );
    assertEquals( "Unexpected value for transfer frequency", minFrequency,
        config.getMinTransferFrequency() );
    
    int minCount = 10;
    config.setMinSampleTransferCount( minCount );
    assertEquals( "Unexpected value for minimum sample count", minCount,
        config.getMinSampleTransferCount() );
    
    int maxCount = 10;
    config.setMaxSampleTransferCount( maxCount );
    assertEquals( "Unexpected value for maximum sample count", maxCount,
        config.getMaxSampleTransferCount() );
    
    ArchiveTypes archiveType = ArchiveTypes.zip;
    config.setArchiveType( archiveType );
    assertEquals( "Unexpected value for archive type", archiveType,
        config.getArchiveType() );
    
    String password = "??????";
    config.getProtocolConfiguration().setAuthPassword( password );
    assertEquals( "Unexpected value for password", password,
        config.getProtocolConfiguration().getAuthPassword() );
    
    String userName = "a name";
    config.getProtocolConfiguration().setUserName( userName );
    assertEquals( "Unexpected value for user", userName, config.getProtocolConfiguration().getUserName() );
    
    String url = "http://127.0.0.1";
    config.getProtocolConfiguration().setURL( url );
    assertEquals( "Unexpected value for URL", url, config.getProtocolConfiguration().getURL() );
    
    ConnectionStrategyDescription strategy =
        ConnectionStrategyDescription.any_available;
    config.getProtocolConfiguration().setTransmissionStrategy( strategy );
    assertEquals( "Unexpected value for transmission strategy", strategy,
        config.getProtocolConfiguration().getTransmissionStrategy() );
    
  }
  
  /**
   * Test method for comparison
   */
  public final void testComparison()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    
    config.setArchiveType( ArchiveTypes.jar );
    config.getProtocolConfiguration().setAuthPassword( "secret" );
    config.setMaxSampleTransferCount( 200 );
    config.setMinSampleTransferCount( 100 );
    config.setMinTransferFrequency( 10000000L );
    config.getProtocolConfiguration().setURL( "http://123.456.0.789" );
    config.getProtocolConfiguration().setUserName( "user" );
    config.getProtocolConfiguration().setTransmissionStrategy( ConnectionStrategyDescription.any_available );
    
    TransmissionConfiguration anotherConfig =
        new TransmissionConfigurationImpl();
    
    assertFalse( "Expected that configurations are not equal",
        config.equals( anotherConfig ) );
    
    anotherConfig.setArchiveType( config.getArchiveType() );
    anotherConfig.getProtocolConfiguration().setAuthPassword( config.getProtocolConfiguration().getAuthPassword() );
    anotherConfig.setMaxSampleTransferCount( config.getMaxSampleTransferCount() );
    anotherConfig.setMinSampleTransferCount( config.getMinSampleTransferCount() );
    anotherConfig.setMinTransferFrequency( config.getMinTransferFrequency() );
    anotherConfig.getProtocolConfiguration().setURL( config.getProtocolConfiguration().getURL() );
    anotherConfig.getProtocolConfiguration().setUserName( config.getProtocolConfiguration().getUserName() );
    anotherConfig.getProtocolConfiguration().setTransmissionStrategy( config.getProtocolConfiguration().getTransmissionStrategy() );
    
    assertTrue( "Expected that configurations are equal",
        config.equals( anotherConfig ) );
  }
  
  /**
   * Test method for update by another configuration
   */
  public final void testUpdateByConfiguration()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();

    TransmissionConfiguration anotherConfig =
        new TransmissionConfigurationImpl();    
    
    anotherConfig.setArchiveType( ArchiveTypes.jar );
    anotherConfig.getProtocolConfiguration().setAuthPassword( "secret" );
    anotherConfig.setMaxSampleTransferCount( 200 );
    anotherConfig.setMinSampleTransferCount( 100 );
    anotherConfig.setMinTransferFrequency( 10000000L );
    anotherConfig.getProtocolConfiguration().setURL( "http://localhost" );
    anotherConfig.getProtocolConfiguration().setUserName( "user" );
    anotherConfig.getProtocolConfiguration().setTransmissionStrategy( ConnectionStrategyDescription.any_available );
    
    assertFalse( "Expected configurations not equal",
        config.equals( anotherConfig ) );
    config.update( anotherConfig );
    assertTrue( "Expected same configuration after update",
        config.equals( anotherConfig ) );
  }
  
  /**
   * Test method for update by a TransmissionConfigurationEntry
   */
  public final void testUpdateByTransmissionConfigurationEntry()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    SDCConfiguration sdcConfig = null;
    try
    {
      sdcConfig =
          TestSDCConfigurationManager.readSDCConfigurationFromResource(
              TestSDCConfigurationManager.CONFIGFILE,
              getInstrumentation().getContext().getResources() );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Failed to read SDC configuration from file: " + e.getMessage() );
    }
    
    TransmissionConfiguration testConfig = new TransmissionConfigurationImpl();
    TransmissionConfigurationEntry serializableConfig = sdcConfig.getTransmissionConfiguration();
    testConfig.setArchiveType(
        ArchiveTypes.valueOf( serializableConfig.getArchiveType() ) );
    testConfig.getProtocolConfiguration().setAuthPassword(
        serializableConfig.getProtocolConfig().getAuthPassword() );
    testConfig.setMaxSampleTransferCount(
        serializableConfig.getMaxSampleTransferCount() );
    testConfig.setMinSampleTransferCount(
        serializableConfig.getMinSampleTransferCount() );
    testConfig.setMinTransferFrequency(
        serializableConfig.getMinTransferFrequency() );
    testConfig.getProtocolConfiguration().setURL(
        serializableConfig.getProtocolConfig().getURL() );
    testConfig.getProtocolConfiguration().setUserName(
        serializableConfig.getProtocolConfig().getUserName() );
    testConfig.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.valueOf(
            serializableConfig.getProtocolConfig().getConnectionStrategy().toLowerCase() ) );
    
    assertFalse( "Expected configurations not equal",
        config.equals( testConfig ) );
    config.update( serializableConfig );
    assertTrue( "Expected same configuration after update",
        config.equals( testConfig ) );
  }
  
}
