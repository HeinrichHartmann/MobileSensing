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
package de.unikassel.android.sdcframework.persistence.tests;

import de.unikassel.android.sdcframework.app.SDCServiceImpl;
import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.persistence.StopServiceStrategy;
import de.unikassel.android.sdcframework.service.ServiceUtils;
import de.unikassel.android.sdcframework.test.TestUtils;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.Suppress;

/**
 * Test for the service stop strategy
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestStopServiceStrategy extends AndroidTestCase
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
   * Test method for
   * {@link de.unikassel.android.sdcframework.persistence.StopServiceStrategy#StopServiceStrategy(android.content.Context, Class)}
   * .
   */
  @Suppress
  // TODO Fix me
  public final void testStopServiceStrategyWithSampleProcessing()
  {
    StopServiceStrategy strategy =
        new StopServiceStrategy( getContext(), SDCServiceImpl.class );
    PersistentStorageManagerForTest manager =
        new PersistentStorageManagerForTest();
    
    manager.counter = 0;
    manager.isProcessingCurrentSamples = true;
    
    doStartService();
    assertTrue( "Expected service running", getSDCServiceRunningState() );
    assertFalse( "Expected strategy successful", strategy.process( manager ) );
    assertTrue( "Expected base class execute method not called",
        manager.counter == 0 );
    TestUtils.sleep( 5000 );
    assertFalse( "Expected service not running", getSDCServiceRunningState() );
  }
  
  /**
   * Does start the SDCService
   */
  private void doStartService()
  {
    if ( !getSDCServiceRunningState() )
    {
      assertNotNull( "Service should be available", ServiceUtils.startService( getContext(), ISDCService.class ));
      while ( !getSDCServiceRunningState() )
      {
        TestUtils.sleep( 100 );
      }
    }
  }
  
  /**
   * Getter for the SDC service running state
   * 
   * @return true if the service is running, false otherwise
   */
  boolean getSDCServiceRunningState()
  {
    return ServiceUtils.isServiceRunning( getContext(), ISDCService.class );
  }
}
