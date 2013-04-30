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
import java.util.ArrayList;
import java.util.List;

import de.unikassel.android.sdcframework.data.SDCConfiguration;
import de.unikassel.android.sdcframework.preferences.TimeProviderConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;

import android.content.res.AssetManager;
import android.test.InstrumentationTestCase;

/**
 * Tests for the time provider configuration.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestTimeProviderConfigurationImpl extends InstrumentationTestCase
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
  public final void testTimeProviderConfigurationImpl()
  {
    TimeProviderConfiguration config =
        new TimeProviderConfigurationImpl( null, null );
    
    assertNull( "Expected providers null initially",
        config.getProviders() );
    assertNull( "Expected error strategy null initially",
        config.getErrorStrategyDescription() );
  }
  
  /**
   * Test method for comparison
   */
  public final void testComparison()
  {
    List< String > listProviders1 = new ArrayList< String >();
    listProviders1.add( "ptbtime1.ptb.de" );
    listProviders1.add( "ptbtime2.ptb.de" );
    List< String > listProviders2 = new ArrayList< String >( listProviders1 );
    listProviders1.add( "atom.uhr.de" );
    TimeProviderErrorStrategyDescription strategy =
        TimeProviderErrorStrategyDescription.IgnoreAndObserveSyncStates;
    TimeProviderErrorStrategyDescription anotherStrategy =
        TimeProviderErrorStrategyDescription.ShutdownService;
    
    TimeProviderConfiguration config =
        new TimeProviderConfigurationImpl( listProviders1, strategy );
    TimeProviderConfiguration config1 =
        new TimeProviderConfigurationImpl( listProviders2, strategy );
    TimeProviderConfiguration config2 =
        new TimeProviderConfigurationImpl( listProviders1, anotherStrategy );
    
    assertFalse( "Expected that configurations are not equal",
        config.equals( config1 ) );
    assertFalse( "Expected that configurations are not equal",
        config.equals( config2 ) );
    
    config1.getProviders().add( listProviders1.get( 2 ) );
    assertTrue( "Expected that configurations are equal",
        config.equals( config1 ) );
  }
  
  /**
   * Test method for update by another configuration
   * {@link de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl#update(de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration)}
   * .
   */
  public final void testUpdateByConfiguration()
  {
    List< String > listProviders = new ArrayList< String >();
    listProviders.add( "ptbtime1.ptb.de" );
    listProviders.add( "ptbtime2.ptb.de" );
    listProviders.add( "atom.uhr.de" );
    TimeProviderErrorStrategyDescription strategy =
        TimeProviderErrorStrategyDescription.IgnoreAndObserveSyncStates;
    TimeProviderErrorStrategyDescription anotherStrategy =
        TimeProviderErrorStrategyDescription.ShutdownService;
    
    TimeProviderConfiguration config =
        new TimeProviderConfigurationImpl( listProviders, strategy );
    
    TimeProviderConfiguration anotherConfig =
        new TimeProviderConfigurationImpl( new ArrayList< String >(),
            anotherStrategy );
    
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
    
    TimeProviderConfiguration config =
        new TimeProviderConfigurationImpl(
            serializableConfig.getTimeProviderConfigEntries().getProviders(),
            TimeProviderErrorStrategyDescription.valueOf( 
                serializableConfig.getTimeProviderConfigEntries().getErrorStrategy() ) );
    
    TimeProviderConfiguration testConfig =
        new TimeProviderConfigurationImpl( null, null );
    
    assertFalse( "Expected configurations not equal",
        config.equals( testConfig ) );
    testConfig.update( serializableConfig.getTimeProviderConfigEntries() );
    assertTrue( "Expected same configuration after update",
        config.equals( testConfig ) );
  }
  
}
