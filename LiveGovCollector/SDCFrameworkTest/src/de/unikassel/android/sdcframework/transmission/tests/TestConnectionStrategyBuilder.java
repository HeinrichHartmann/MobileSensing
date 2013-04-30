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
package de.unikassel.android.sdcframework.transmission.tests;

import android.test.AndroidTestCase;
import de.unikassel.android.sdcframework.preferences.TransmissionConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.transmission.ConnectionStrategyBuilder;
import de.unikassel.android.sdcframework.transmission.MobileConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.UseAvailableConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.WLANConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;

/**
 * @author Katy Hilgenberg
 * 
 */
public class TestConnectionStrategyBuilder
    extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
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
   * {@link de.unikassel.android.sdcframework.transmission.ConnectionStrategyBuilder#buildStrategy(de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration, Class)}
   * .
   */
  public final void testBuildStrategy()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    
    // test first strategy
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.any_available );
    ConnectionStrategy strategy =
        ConnectionStrategyBuilder.buildStrategy(
            config.getProtocolConfiguration(), null );
    
    assertTrue( "Expected available connection strategy",
        strategy instanceof UseAvailableConnectionStrategy );
    assertNull( "Expected no follower", strategy.getSuccessor() );
    
    // test second strategy
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.mobile_connection );
    strategy =
        ConnectionStrategyBuilder.buildStrategy(
            config.getProtocolConfiguration(), null );
    
    assertTrue( "Expected mobile connection strategy",
        strategy instanceof MobileConnectionStrategy );
    assertNull( "Expected no follower", strategy.getSuccessor() );
    
    // test third strategy
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.wlan );
    strategy =
        ConnectionStrategyBuilder.buildStrategy(
            config.getProtocolConfiguration(), null );
    
    assertTrue( "Expected wlan connection strategy",
        strategy instanceof WLANConnectionStrategy );
    assertNull( "Expected no follower", strategy.getSuccessor() );
    
    // test forth strategy
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.wlan_else_mobile );
    strategy =
        ConnectionStrategyBuilder.buildStrategy(
            config.getProtocolConfiguration(), null );
    
    assertTrue( "Expected wlan connection strategy first",
        strategy instanceof WLANConnectionStrategy );
    assertNotNull( "Expected a follower", strategy.getSuccessor() );
    assertTrue( "Expected mobile connection strategy as follower",
        strategy.getSuccessor() instanceof MobileConnectionStrategy );
  }
  
}
