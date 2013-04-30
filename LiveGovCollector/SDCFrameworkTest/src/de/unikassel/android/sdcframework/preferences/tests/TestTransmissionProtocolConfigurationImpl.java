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
import de.unikassel.android.sdcframework.data.TransmissionProtocolConfigurationEntry;
import de.unikassel.android.sdcframework.preferences.TransmissionProtocolConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;

/**
 * Tests for the transmission protocol configuration.
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "DefaultLocale" )
public class TestTransmissionProtocolConfigurationImpl extends InstrumentationTestCase
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
      fail( "Configuration file not found! Please correct the name, if it has been changed in the framework." );
    }
  }
  
  /**
   * Test method for construction.
   */
  public final void testTransmissionProtocolConfigurationImpl()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
    
    assertNull( "Expected password not set", config.getAuthPassword() );
    assertNull( "Expected user not set", config.getUserName() );
    assertNull( "Expected URL not set", config.getURL() );
    assertNull( "Expected transmission strategy not set",
        config.getTransmissionStrategy() );
  }
  
  /**
   * Test method for setter and getter
   */
  public final void testSetterWithGetter()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
    
    String password = "??????";
    config.setAuthPassword( password );
    assertEquals( "Unexpected value for password", password,
        config.getAuthPassword() );
    
    String userName = "a name";
    config.setUserName( userName );
    assertEquals( "Unexpected value for user", userName, config.getUserName() );
    
    String url = "http://127.0.0.1";
    config.setURL( url );
    assertEquals( "Unexpected value for URL", url, config.getURL() );
    
    ConnectionStrategyDescription strategy =
        ConnectionStrategyDescription.any_available;
    config.setTransmissionStrategy( strategy );
    assertEquals( "Unexpected value for transmission strategy", strategy,
        config.getTransmissionStrategy() );
    
  }
  
  /**
   * Test method for comparison
   */
  public final void testComparison()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
    
    config.setAuthPassword( "secret" );
    config.setURL( "http://123.456.0.789" );
    config.setUserName( "user" );
    config.setTransmissionStrategy( ConnectionStrategyDescription.any_available );
    
    TransmissionProtocolConfiguration anotherConfig =
        new TransmissionProtocolConfigurationImpl();
    
    assertFalse( "Expected that configurations are not equal",
        config.equals( anotherConfig ) );
    
    anotherConfig.setAuthPassword( config.getAuthPassword() );
    anotherConfig.setURL( config.getURL() );
    anotherConfig.setUserName( config.getUserName() );
    anotherConfig.setTransmissionStrategy( config.getTransmissionStrategy() );
    
    assertTrue( "Expected that configurations are equal",
        config.equals( anotherConfig ) );
  }
  
  /**
   * Test method for update by another configuration
   */
  public final void testUpdateByConfiguration()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();

    TransmissionProtocolConfiguration anotherConfig =
        new TransmissionProtocolConfigurationImpl();    
    
    anotherConfig.setAuthPassword( "secret" );
    anotherConfig.setURL( "http://localhost" );
    anotherConfig.setUserName( "user" );
    anotherConfig.setTransmissionStrategy( ConnectionStrategyDescription.any_available );
    
    assertFalse( "Expected configurations not equal",
        config.equals( anotherConfig ) );
    config.update( anotherConfig );
    assertTrue( "Expected same configuration after update",
        config.equals( anotherConfig ) );
  }
  
  /**
   * Test method for update by a TransmissionProtocolConfigurationEntry
   */
  public final void testUpdateByTransmissionConfigurationEntry()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
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
    
    TransmissionProtocolConfiguration testConfig = new TransmissionProtocolConfigurationImpl();
    TransmissionProtocolConfigurationEntry serializableConfig = sdcConfig.getTransmissionConfiguration().getProtocolConfig();
    testConfig.setAuthPassword(
        serializableConfig.getAuthPassword() );
    testConfig.setURL(
        serializableConfig.getURL() );
    testConfig.setUserName(
        serializableConfig.getUserName() );
    testConfig.setTransmissionStrategy(
        ConnectionStrategyDescription.valueOf(
            serializableConfig.getConnectionStrategy().toLowerCase() ) );
    
    assertFalse( "Expected configurations not equal",
        config.equals( testConfig ) );
    config.update( serializableConfig );
    assertTrue( "Expected same configuration after update",
        config.equals( testConfig ) );
  }
  
}
