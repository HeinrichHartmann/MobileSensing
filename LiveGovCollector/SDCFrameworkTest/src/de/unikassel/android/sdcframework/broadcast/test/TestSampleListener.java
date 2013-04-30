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
package de.unikassel.android.sdcframework.broadcast.test;

import de.unikassel.android.sdcframework.broadcast.SampleListener;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.SampleCollection;
import de.unikassel.android.sdcframework.data.independent.GeoLocation;
import de.unikassel.android.sdcframework.data.tests.TestGPSSampleData;
import de.unikassel.android.sdcframework.data.tests.TestSampleCollection;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.tests.SampleCollectionEventObserverForTest;
import android.test.AndroidTestCase;

/**
 * Tests for the sample listener receiving broadcasted samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSampleListener extends AndroidTestCase
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
   * Tests for log listener receiving broadcasts of log events
   */
  public void testSampleListener()
  {
    SampleCollectionEventObserverForTest observer =
        new SampleCollectionEventObserverForTest();
    SampleListener sampleReceiver = new SampleListener();
    
    // register the log listener as broadcast receiver
    sampleReceiver.registerAsBroadCastReceiver( getContext() );
    
    // add an event observer
    sampleReceiver.registerEventObserver( observer );
    
    Sample sample =
        new Sample( SensorDeviceIdentifier.GPS, System.currentTimeMillis(), 0,
            false );
    sample.setData( TestGPSSampleData.createInitializedGPSSampleData() );
    GeoLocation location = new GeoLocation();
    location.setLat( 90. );
    location.setLon( 45. );
    sample.setLocation( location );
    getContext().sendBroadcast( sample.getIntent() );
    
    TestUtils.sleep( 1000 );
    
    // test received sample
    assertEquals( "Expected intent received by listner", 1,
        observer.observedEvents.size() );
    SampleCollection receivedSamples = observer.observedEvents.get( 0 );
    assertEquals( "Expected one sample in observed collection", 1,
        receivedSamples.size() );
    assertEquals( "Expected same sample", sample,
        receivedSamples.getSamples().get( 0 ) );
    
    SampleCollection sc = new SampleCollection();
    TestSampleCollection.fillSampleCollectionWithTestData( sc );
    getContext().sendBroadcast( sc.getIntent() );
    
    TestUtils.sleep( 1000 );
    
    // test received sample
    assertEquals( "Expected intent received by listner", 2,
        observer.observedEvents.size() );
    receivedSamples = observer.observedEvents.get( 1 );
    assertEquals( "Expected same sample collection", sc.toString(),
        receivedSamples.toString() );
    
    sampleReceiver.unregisterAsBroadCastReceiver( getContext() );
  }
}
