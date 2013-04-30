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

import de.unikassel.android.sdcframework.broadcast.SampleBroadcastServiceImpl;
import de.unikassel.android.sdcframework.broadcast.SampleListener;
import de.unikassel.android.sdcframework.broadcast.facade.SampleBroadcastService;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.SampleCollection;
import de.unikassel.android.sdcframework.data.independent.GeoLocation;
import de.unikassel.android.sdcframework.data.tests.TestGPSSampleData;
import de.unikassel.android.sdcframework.data.tests.TestSampleCollection;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;
import de.unikassel.android.sdcframework.util.tests.SampleCollectionEventObserverForTest;
import android.content.IntentFilter;
import android.test.AndroidTestCase;

/**
 * Tests for the asynchronous sample broadcast service.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestSampleBroadcastServiceImpl extends AndroidTestCase
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
   * Test method for the sample broadcast service
   */
  public final void testSampleBroadcastServiceImpl()
  {
    // we use an observed sample listener as receiver for the broadcasted
    // samples
    SampleListener sampleReceiver = new SampleListener();
    SampleCollectionEventObserverForTest observer =
        new SampleCollectionEventObserverForTest();
    sampleReceiver.registerEventObserver( observer );
    IntentFilter filter = new IntentFilter();
    filter.addAction( Sample.ACTION );
    filter.addAction( SampleCollection.ACTION );
    getContext().registerReceiver( sampleReceiver, filter );
    
    // create the source generating the sample events that the broadcast service
    // can observe
    ObservableEventSourceImpl< Sample > eventSource =
        new ObservableEventSourceImpl< Sample >();
    
    // create the broadcast service under test and connect it with the event
    // source
    SampleBroadcastService service =
        new SampleBroadcastServiceImpl( getContext(), 0L );
    eventSource.registerEventObserver( service.getObserver() );
    service.onCreate( getContext() );
    
    // start service
    service.onResume( getContext() );
    
    // do raise an event in the event source
    Sample sample =
        new Sample( SensorDeviceIdentifier.GPS, System.currentTimeMillis(), 0,
            false );
    sample.setData( TestGPSSampleData.createInitializedGPSSampleData() );
    GeoLocation location = new GeoLocation();
    location.setLat( 90. );
    location.setLon( 45. );
    sample.setLocation( location );
    eventSource.notify( sample );
    
    // wait a bit
    TestUtils.sleep( 3000 );
    
    // test if the listener has received the broadcasted sample
    assertEquals( "Expected intent received by listner", 1,
        observer.observedEvents.size() );
    SampleCollection receivedSamples = observer.observedEvents.get( 0 );
    assertEquals( "Expected one sample in observed collection", 1,
        receivedSamples.size() );
    assertEquals( "Expected same sample", sample,
        receivedSamples.getSamples().get( 0 ) );
    
    // stop service
    service.onPause( getContext() );
    
    // clean up
    service.onDestroy( getContext() );
    eventSource.unregisterEventObserver( service.getObserver() );
  }
  
  /**
   * Test method for the sample broadcast service with a frequency above 0 ms
   * (which leads to bulk transfers)
   */
  public final void testBulkSampleBroadcasts()
  {
    // we use an observed sample listener as receiver for the broadcasted
    // samples
    SampleListener sampleReceiver = new SampleListener();
    SampleCollectionEventObserverForTest observer =
        new SampleCollectionEventObserverForTest();
    sampleReceiver.registerEventObserver( observer );
    IntentFilter filter = new IntentFilter();
    filter.addAction( Sample.ACTION );
    filter.addAction( SampleCollection.ACTION );
    getContext().registerReceiver( sampleReceiver, filter );
    
    // create the source generating the sample events that the broadcast service
    // can observe
    ObservableEventSourceImpl< Sample > eventSource =
        new ObservableEventSourceImpl< Sample >();
    
    // create the broadcast service under test and connect it with the event
    // source
    SampleBroadcastService service =
        new SampleBroadcastServiceImpl( getContext(), 1000L );
    eventSource.registerEventObserver( service.getObserver() );
    service.onCreate( getContext() );
    
    // start service
    service.onResume( getContext() );
    
    // do raise an event in the event source
    SampleCollection sc = new SampleCollection();
    TestSampleCollection.fillSampleCollectionWithTestData( sc );
    for ( Sample sample : sc )
    {
      eventSource.notify( sample );
    }
    
    // wait a bit
    for( int i = 0; i < 30; ++i )
    {
      TestUtils.sleep( 100 );
    }
    
    // test if the listener has received the broadcasted sample
    assertEquals( "Unexpected intent count received by the listner", 1,
        observer.observedEvents.size() );
    SampleCollection receivedSamples = observer.observedEvents.get( 0 );
    assertEquals( "Unexpected sample count in observed collection", sc.size(),
        receivedSamples.size() );
    assertEquals( "Expected same samples", sc.toString(),
        receivedSamples.toString() );
    
    // stop service
    service.onPause( getContext() );
    
    // clean up
    service.onDestroy( getContext() );
    eventSource.unregisterEventObserver( service.getObserver() );
  }
}
