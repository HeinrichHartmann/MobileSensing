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

import de.unikassel.android.sdcframework.test.ConnectivityWrapperForTest;
import de.unikassel.android.sdcframework.transmission.FailSafeProtocol;
import de.unikassel.android.sdcframework.transmission.UseAvailableConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.facade.ConnectivityWrapper;
import de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy;
import android.test.AndroidTestCase;

/**
 * Test for the use available connection strategy
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestUseAvailableConnectionStrategy extends AndroidTestCase
{
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for construction
   */
  public final void testUseAvailableConnectionStrategy()
  {
    ConnectivityWrapper connectivityWrapper = new ConnectivityWrapperForTest();
    
    // test construction with invalid parameter
    try
    {
      new UseAvailableConnectionStrategy( null, null );
      fail( "Expected InvalidParameterException" );
    }
    catch ( Exception e )
    {}
    
    // test construction with valid parameter
    try
    {
      new UseAvailableConnectionStrategy( connectivityWrapper, null );
      
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.UseAvailableConnectionStrategy#isConnectionAvailable(de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy)}
   * and
   * {@link de.unikassel.android.sdcframework.transmission.UseAvailableConnectionStrategy#doWork(ProtocolStrategy)}
   * .
   */
  public final void testIsConnectionAvailableAndDoWork()
  {
    ProtocolStrategy protocolStrategy = new FailSafeProtocol( getContext() );
    
    ConnectivityWrapperForTest connectivityWrapper =
        new ConnectivityWrapperForTest();
    UseAvailableConnectionStrategy strategy =
        new UseAvailableConnectionStrategy( connectivityWrapper, null );
    
    connectivityWrapper.isNetworkConnected = true;
    assertTrue( "Expected strategy returning true for connection available",
        strategy.isConnectionAvailable( protocolStrategy ) );
    assertTrue( "Expected strategy returning true",
        strategy.doWork( protocolStrategy ) );
    
    connectivityWrapper.isNetworkConnected = false;
    assertFalse( "Expected strategy returning false for connection available",
        strategy.isConnectionAvailable( protocolStrategy ) );
    assertFalse( "Expected strategy returning false",
        strategy.doWork( protocolStrategy ) );
  }
  
}
