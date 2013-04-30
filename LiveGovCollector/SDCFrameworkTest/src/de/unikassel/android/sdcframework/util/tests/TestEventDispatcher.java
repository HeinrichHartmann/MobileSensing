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
package de.unikassel.android.sdcframework.util.tests;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import junit.framework.TestCase;

import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.EventCollectorImpl;
import de.unikassel.android.sdcframework.util.EventDispatcherImpl;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Test for the event dispatcher
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestEventDispatcher extends TestCase
{
  
  /**
   * The internal event class extending {@linkplain ObservableEvent}.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestEvent implements ObservableEvent
  { 

  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.EventDispatcherImpl#EventDispatcherImpl(de.unikassel.android.sdcframework.util.facade.EventCollector)}
   * .
   */
  public final void testEventDispatcherImpl()
  {
    // test for construction from null pointer
    try
    {
      new EventDispatcherImpl< TestEventDispatcher.TestEvent >( null );
      fail( "Construction from collector null pointer should not be allowed" );
    }
    catch ( InvalidParameterException e )
    {}
    
    EventCollectorImpl< TestEvent > collector =
        new EventCollectorImpl< TestEventDispatcher.TestEvent >();
    EventDispatcherImpl< TestEvent > dispatcher =
        new EventDispatcherImpl< TestEventDispatcher.TestEvent >( collector );
    
    assertNotNull( "Expected internal collector not null",
        dispatcher.getCollector() );
    assertNotNull( "Expected internal observable event source not null",
        dispatcher.getEventSource() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.EventDispatcherImpl#getCollector()}
   * .
   */
  public final void testGetCollector()
  {
    EventCollectorImpl< TestEvent > collector =
        new EventCollectorImpl< TestEventDispatcher.TestEvent >();
    EventDispatcherImpl< TestEvent > dispatcher =
        new EventDispatcherImpl< TestEventDispatcher.TestEvent >( collector );
    
    assertSame( "Expected collector the same ", collector,
        dispatcher.getCollector() );
  }
  
  /**
   * Private inner class implementing an event observer for the tested instance
   * of EventDispatcherImpl< TestEvent >. Does always store last observed event;
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestObserver implements EventObserver< TestEvent >
  {
    /**
     * Public event list
     */
    public final ArrayList< TestEvent > events = new ArrayList< TestEvent >();
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de
     * .unikassel.android.sdcframework.util.facade.ObservableEventSource,
     * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
     */
    @Override
    public void onEvent(
        ObservableEventSource< ? extends TestEvent > eventSource,
        TestEvent observedEvent )
    {
      events.add( observedEvent );
    }
  }
  
  /**
   * Test method for event dispatching to registered observers
   */
  public final void testDispatching()
  {
    int count = 10;
    EventCollectorImpl< TestEvent > collector =
        new EventCollectorImpl< TestEventDispatcher.TestEvent >();
    EventDispatcherImpl< TestEvent > dispatcher =
        new EventDispatcherImpl< TestEventDispatcher.TestEvent >( collector );
    dispatcher.setLogging( false );
    // create and register event observer
    TestObserver observer = new TestObserver();
    dispatcher.registerEventObserver( observer );
    
    // enqueue events
    for ( int i = 0; i < count; ++i )
    {
      collector.enqueue( new TestEvent() );
    }
    assertEquals( "Expected collector queue with " + count + " elements",
        count,
        dispatcher.getCollector().getEventCount() );
    assertEquals( "Expected no observed events so far", 0,
        observer.events.size() );
    
    // start dispatcher and wait for empty collectors queue
    dispatcher.startWork();
    do
    {
      TestUtils.sleep( 100 );
    }
    while ( collector.getEventCount() > 0 );
    TestUtils.sleep( 100 );
    dispatcher.stopWork();
    
    assertEquals( "Expected " + count + " observed events now", count,
        observer.events.size() );
    observer.events.clear();
    
    // test clean up
    for ( int i = 0; i < count; ++i )
    {
      collector.enqueue( new TestEvent() );
    }
    assertTrue( "Expected collector queue filled with elements",
        dispatcher.getCollector().getEventCount() >= count - 1 );
    dispatcher.doTerminate();
    TestUtils.sleep( 1000 );
    
    assertEquals( "Expected collector queuey emptied", 0,
        dispatcher.getCollector().getEventCount() );
    assertTrue( "Expected dispatcher terminated",
        dispatcher.hasTerminated() );
    assertEquals( "Expected dispatcher in terminated sate",
        Thread.State.TERMINATED, dispatcher.getState() );
  }
  
}
