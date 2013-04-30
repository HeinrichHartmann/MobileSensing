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
import de.unikassel.android.sdcframework.persistence.DatabaseFullStrategyBuilder;
import de.unikassel.android.sdcframework.persistence.DeleteSamplesStrategy;
import de.unikassel.android.sdcframework.persistence.NotificationStrategy;
import de.unikassel.android.sdcframework.persistence.StopServiceStrategy;
import de.unikassel.android.sdcframework.persistence.WaitStrategy;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseFullStrategy;
import de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import android.test.AndroidTestCase;

/**
 * Tests for the databse strategy builder class.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestDatabaseFullStrategyBuilder
    extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
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
   * {@link de.unikassel.android.sdcframework.persistence.DatabaseFullStrategyBuilder#buildStrategy(android.content.Context, ServiceConfiguration, Class, Class)}
   * .
   */
  public final void testBuildStrategy()
  {
    ServiceConfiguration serviceConfig = new ServiceConfigurationImpl();
    serviceConfig.setDBFullStrategy( DBFullStrategyDescription.WAIT_DELETE_NOTIFY );
    serviceConfig.setDBFullDeletionPriorityBased( true );
    serviceConfig.setDBFullDeletionRecordCount( 333 );
    serviceConfig.setDBFullWaitTime( 13L );
    
    // test first configuration
    DatabaseFullStrategy strategy =
        DatabaseFullStrategyBuilder.buildStrategy( getContext(), serviceConfig,
            SDCServiceImpl.class, null );
    assertTrue( "Expected wait strategy first",
        strategy instanceof WaitStrategy );
    WaitStrategy waitStrategy = (WaitStrategy) strategy;
    assertEquals( "Expected wait strategy configured with wait time",
        serviceConfig.getDBFullWaitTime(), waitStrategy.getSleepTime() );
    assertTrue( "Expected successor strategy is set",
        strategy.getSuccessor() instanceof DatabaseFullStrategy );
    
    strategy = (DatabaseFullStrategy) strategy.getSuccessor();
    assertTrue( "Expected successor is delete stratgey",
        strategy instanceof DeleteSamplesStrategy );
    DeleteSamplesStrategy deleteStrategy = (DeleteSamplesStrategy) strategy;
    assertEquals( "Expected delete strategy configured with record count",
        serviceConfig.getDBFullDeletionRecordCount(),
        deleteStrategy.getCountToDelete() );
    assertEquals( "Expected delete strategy configured with priority flag",
        serviceConfig.isDBFullDeletionPriorityBased(),
        deleteStrategy.isDeletingPriorityBased() );
    
    assertTrue( "Expected successor strategy is set",
        strategy.getSuccessor() instanceof DatabaseFullStrategy );
    strategy = (DatabaseFullStrategy) strategy.getSuccessor();
    assertTrue( "Expected successor is wait strategy",
        strategy instanceof NotificationStrategy );
    assertNull( "Expected no more successors", strategy.getSuccessor() );
    
    // test second configuration
    serviceConfig.setDBFullStrategy( DBFullStrategyDescription.WAIT_NOTIFY_STOPSERVICE );
    strategy =
        DatabaseFullStrategyBuilder.buildStrategy( getContext(), serviceConfig,
            SDCServiceImpl.class, null );
    assertTrue( "Expected wait strategy first",
        strategy instanceof WaitStrategy );
    waitStrategy = (WaitStrategy) strategy;
    assertEquals( "Expected wait strategy configured with wait time",
        serviceConfig.getDBFullWaitTime(), waitStrategy.getSleepTime() );
    assertTrue( "Expected successor strategy is set",
        strategy.getSuccessor() instanceof DatabaseFullStrategy );
    
    strategy = (DatabaseFullStrategy) strategy.getSuccessor();
    assertTrue( "Expected successor strategy is notification strategy",
        strategy instanceof NotificationStrategy );
    assertTrue( "Expected successor strategy is set",
        strategy.getSuccessor() instanceof DatabaseFullStrategy );
    
    strategy = (DatabaseFullStrategy) strategy.getSuccessor();
    assertTrue( "Expected successor strategy is stop service strategy",
        strategy instanceof StopServiceStrategy );
    assertNull( "Expected no more successors", strategy.getSuccessor() );
  }
  
}
