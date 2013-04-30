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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;
import junit.framework.TestCase;

import de.unikassel.android.sdcframework.util.EventCollectorImpl;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;
import de.unikassel.android.sdcframework.util.facade.EventError;
import de.unikassel.android.sdcframework.util.facade.EventErrorTypes;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Tests for the event collector implementation.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestEventCollector extends TestCase
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
   * {@link de.unikassel.android.sdcframework.util.EventCollectorImpl#EventCollectorImpl()}
   * .
   */
  public final void testEventCollectorImpl()
  {
    EventCollectorImpl< TestEvent > eventCollector =
        new EventCollectorImpl< TestEvent >();
    
    // test initial construction
    assertNotNull( "Expected collectors event observer is not null",
        eventCollector.getEventObserver() );
    assertNotNull(
        "Expected collectors observable error event source is not null",
        eventCollector.getErrorEventSource() );
    assertEquals( "Expected empty event queue",
        0, eventCollector.getEventCount() );
    Assert.assertFalse( "Expected no observers",
        eventCollector.hasObservers() );
  }
  
  /**
   * Private inner class to simulate an observable event source for the
   * collector. Does provide a method to call trigger event notification.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestEventProducer extends ObservableEventSourceImpl< TestEvent >
  {
    /**
     * Method to raise an event
     * 
     * @param event
     *          the event
     */
    public void doEvent( TestEvent event )
    {
      notify( event );
    }
  };
  
  /**
   * Test method for event collection ( enqueue and dequeue )
   */
  public final void testEnqueuAndDequeue()
  {
    int count = 10;
    TestEventProducer eventProducer = new TestEventProducer();
    EventCollectorImpl< TestEvent > eventCollector =
        new EventCollectorImpl< TestEvent >();
    
    // connect observable event source and collector
    eventProducer.registerEventObserver( eventCollector.getEventObserver() );
    
    // test enqueue and dequeue functionality for observed events
    for ( int i = 1; i <= count; ++i )
    {
      TestEvent event = new TestEvent();
      eventProducer.doEvent( event );
      assertEquals( "Expected 1 event in queue",
          1, eventCollector.getEventCount() );
      try
      {
        assertSame( "Expected dequeued event equals the notified event",
            event, eventCollector.dequeue() );
        assertEquals( "Expected event queue is empty",
            0, eventCollector.getEventCount() );
      }
      catch ( InterruptedException e )
      {
        fail( "Unexpected interrupt exception during dequeue" );
      }
    }
    
    // disconnect observable event source and collector
    eventProducer.unregisterEventObserver( eventCollector.getEventObserver() );
    assertTrue( "event producer should have no ebserver now",
        eventProducer.getObservers().isEmpty() );
  }
  
  /**
   * Test method for event observed enqueue
   */
  public final void testClearCollectedEvents()
  {
    int count = 10;
    TestEventProducer eventProducer = new TestEventProducer();
    EventCollectorImpl< TestEvent > eventCollector =
        new EventCollectorImpl< TestEvent >();
    
    // connect observable event source and collector
    eventProducer.registerEventObserver( eventCollector.getEventObserver() );
    
    // test enqueue functionality for observed events
    for ( int i = 1; i <= count; ++i )
    {
      TestEvent event = new TestEvent();
      eventProducer.doEvent( event );
      assertEquals( "Expected " + i + " event in queue",
          i, eventCollector.getEventCount() );
    }
    
    // test queue clearing
    eventCollector.clearCollectedEvents();
    assertEquals( "Expected event queue is empty",
        0, eventCollector.getEventCount() );
    
    // disconnect observable event source and collector
    eventProducer.unregisterEventObserver( eventCollector.getEventObserver() );
    assertTrue( "event producer should have no ebserver now",
        eventProducer.getObservers().isEmpty() );
  }
  
  /**
   * Test method for observer registration and de-registration
   */
  public final void testObserverRegistration()
  {
    EventCollectorImpl< TestEvent > eventCollector =
        new EventCollectorImpl< TestEvent >();
    
    final List< EventError > errors = new ArrayList< EventError >();
    
    
    EventObserver< EventError > observer = new EventObserver< EventError >()
    {
      @Override
      public void onEvent(
          ObservableEventSource< ? extends EventError > eventSource,
          EventError observedEvent )
      {
        errors.add( observedEvent );
      }
    };
    
    int cnt = eventCollector.getErrorEventSource().getObservers().size();
    eventCollector.registerEventObserver( observer );
    assertEquals( "Expected observer registered", cnt + 1,
        eventCollector.getErrorEventSource().getObservers().size() );
    Assert.assertTrue( "Expected no observers",
        eventCollector.hasObservers() );
    eventCollector.notify( new EventError( EventErrorTypes.NO_ERROR, null ) );
    assertEquals( "Expected one event received",
        1, errors.size() );
    eventCollector.unregisterEventObserver( observer );
    assertEquals( "Expected observer de-registered", cnt,
        eventCollector.getErrorEventSource().getObservers().size() );
    eventCollector.registerEventObserver( observer );
    eventCollector.removeAllObservers();
    assertEquals( "Expected observer de-registered", 0,
        eventCollector.getErrorEventSource().getObservers().size() );
  }
  
}
