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
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.transmission.SampleGatheringController;

/**
 * Test for the sample gathering controller.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSampleGatheringController
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
   * {@link de.unikassel.android.sdcframework.transmission.SampleGatheringController#SampleGatheringController()}
   * .
   */
  public final void testSampleGatheringController()
  {
    SampleGatheringController controller = new SampleGatheringController();
    assertEquals( "Unexpected minimum sample count", 0L,
        controller.getMinSampleCount() );
    assertEquals( "Unexpected maximum sample count", 0L,
        controller.getMaxSampleCount() );
    assertEquals( "Unexpected available sample count", 0L,
        controller.getAvailableSampleCount() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.SampleGatheringController#consumAvailableSamples()}
   * .
   */
  public final void testConsumAvailableSamples()
  {
    SampleGatheringController controller = new SampleGatheringController();
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setMaxSampleTransferCount( 100 );
    config.setMinSampleTransferCount( 10 );
    
    controller.updateConfiguration( getContext(), config );
    
    long availableSampleCount = 5L;
    controller.calculatetWaitTime( availableSampleCount );
    
    assertEquals( "Unexpected available sample count", availableSampleCount,
        controller.getAvailableSampleCount() );
    controller.consumAvailableSamples();
    
    assertEquals( "Unexpected available sample count", 0L,
        controller.getAvailableSampleCount() );
    
    controller.calculatetWaitTime( 0L );
    
    assertEquals( "Unexpected available sample count", 0L,
        controller.getAvailableSampleCount() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.SampleGatheringController#updateConfiguration(android.content.Context, de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration)}
   * .
   */
  public final void testUpdateConfiguration()
  {
    SampleGatheringController controller = new SampleGatheringController();
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setMaxSampleTransferCount( 10 );
    config.setMinSampleTransferCount( 1 );
    
    controller.updateConfiguration( getContext(), config );
    assertEquals( "Unexpected minimum sample count",
        config.getMinSampleTransferCount(), controller.getMinSampleCount() );
    assertEquals( "Unexpected maximum sample count",
        config.getMaxSampleTransferCount(), controller.getMaxSampleCount() );
    
    config.setMaxSampleTransferCount( 5 );
    config.setMinSampleTransferCount( 10 );
    
    controller.updateConfiguration( getContext(), config );
    assertEquals( "Unexpected minimum sample count",
        config.getMinSampleTransferCount(), controller.getMinSampleCount() );
    assertEquals( "Unexpected maximum sample count",
        config.getMinSampleTransferCount(), controller.getMaxSampleCount() );
    
    // test reset
    long availableSampleCount = config.getMinSampleTransferCount();
    controller.calculatetWaitTime( availableSampleCount );
    
    assertEquals( "Unexpected available sample count", availableSampleCount,
        controller.getAvailableSampleCount() );
    
    controller.reset( 10L );
    
    assertEquals( "Unexpected available sample count", 10L,
        controller.getAvailableSampleCount() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.SampleGatheringController#calculatetWaitTime(long)}
   * .
   */
  public final void testGetWaitTime()
  {
    SampleGatheringController controller = new SampleGatheringController();
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setMaxSampleTransferCount( 100 );
    config.setMinSampleTransferCount( 10 );
    
    controller.updateConfiguration( getContext(), config );
    
    long availableSampleCount = config.getMinSampleTransferCount() - 1;
    long expectedWaitTime = 100L;
    
    controller.reset( 0L );
    TestUtils.sleep( availableSampleCount * 100 );
    
    long waitTime = controller.calculatetWaitTime( availableSampleCount );
    
    assertEquals( "Unexpected minimum sample count",
        config.getMinSampleTransferCount(), controller.getMinSampleCount() );
    assertEquals( "Unexpected maximum sample count",
        config.getMaxSampleTransferCount(), controller.getMaxSampleCount() );
    assertTrue( "Unexpected wait time", waitTime >= expectedWaitTime );
    assertEquals( "Unexpected available sample count", availableSampleCount,
        controller.getAvailableSampleCount() );
    
    availableSampleCount += 1;
    waitTime = controller.calculatetWaitTime( availableSampleCount );
    assertEquals( "Unexpected wait time", 0L, waitTime );
    assertEquals( "Unexpected available sample count", availableSampleCount,
        controller.getAvailableSampleCount() );
    
  }
  
}
