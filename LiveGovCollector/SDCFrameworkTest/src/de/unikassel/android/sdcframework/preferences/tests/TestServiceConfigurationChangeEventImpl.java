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

import de.unikassel.android.sdcframework.preferences.ServiceConfigurationChangeEventImpl;
import de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfigurationChangeEvent;
import junit.framework.TestCase;

/**
 * Tests for the service configuration update change event.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestServiceConfigurationChangeEventImpl extends TestCase
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
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.preferences.ServiceConfigurationChangeEventImpl#getConfiguration()}
   * .
   */
  public final void testGetConfiguration()
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    config.setBroadcastingSamples( true );
    config.setSamplingEnabled( false );
    config.setIsAddingSampleLocation( true );
    config.setMaximumDatabaseSize( 10000L );
    ServiceConfigurationChangeEvent event = new ServiceConfigurationChangeEventImpl( config  );

    assertNotNull( "Expected configuration nut null", event.getConfiguration() );
    assertSame( "Unexpected configuration object", config, event.getConfiguration() );
  }
  
}
