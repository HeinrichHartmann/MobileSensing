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
package de.unikassel.android.sdcframework.test;

import java.io.IOException;

import android.content.res.AssetManager;
import android.test.AndroidTestCase;
import de.unikassel.android.sdcframework.data.SDCConfiguration;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.preferences.SDCConfigurationManager;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.preferences.tests.TestSDCConfigurationManager;

/**
 * Test to validate the current framework XML configuration file in the SDC
 * projects asset folder.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestFrameworkConfiguration
    extends AndroidTestCase
{
  /**
   * The configuration filename
   */
  public final static String CONFIGFILE = "SDCConfig.xml";
  
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
        getContext().getResources().getAssets();
    try
    {
      assetManager.open( CONFIGFILE );
    }
    catch ( IOException e )
    {
      fail( "Configuration file not found!\nPlease fix name if it have been changed in the framework." );
    }
  }
  
  /**
   * Test method for Construction and update of default values.
   */
  public final void testConstructionAndUpdateOfDefaultsForCurrentConfigFile()
  {
    // create preference manager with device defaults
    ApplicationPreferenceManager appPrefManager =
        new ApplicationPreferenceManagerImpl();
    
    SDCConfiguration config =
        TestSDCConfigurationManager.readConfigurationForTest( getContext(),
            CONFIGFILE );
    assertNotNull( "Failed to read configuration", config );
    
    try
    {
      // create manager and trigger default update
      SDCConfigurationManager manager =
          new SDCConfigurationManager( getContext(), CONFIGFILE );
      manager.updateDefaults( appPrefManager );
    }
    catch( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
  }
}
