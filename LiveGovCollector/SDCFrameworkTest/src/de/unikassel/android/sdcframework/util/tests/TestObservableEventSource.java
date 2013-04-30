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

import junit.framework.TestCase;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Tests for the observable event source implementation.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestObservableEventSource extends TestCase
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
   * Private inner class implementing an event observer for the tested instance
   * of ObservableEventSourceImpl<TestEvent>. Does always store last observed
   * event;
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestObserver implements
      EventObserver< TestObservableEventSource.TestEvent >
  {
    /**
     * public reference to the last observed event
     */
    public TestEvent lastEvent = null;
    
    @Override
    public void onEvent(
        ObservableEventSource< ? extends TestEvent > eventSource,
        TestEvent observedEvent )
    {
      lastEvent = observedEvent;
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.ObservableEventSourceImpl#ObservableEventSourceImpl()}
   * .
   */
  public final void testObservableEventSourceImpl()
  {
    ObservableEventSourceImpl< TestEvent > observable =
        new ObservableEventSourceImpl< TestObservableEventSource.TestEvent >();
    
    // test initial construction
    assertNotNull( "Expected observers collection not null",
        observable.getObservers() );
    assertTrue( "Expected observers collection empty",
        observable.getObservers().isEmpty() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.ObservableEventSourceImpl#registerEventObserver(de.unikassel.android.sdcframework.util.facade.EventObserver)}
   * .
   */
  public final void testRegisterEventObserver()
  {
    ObservableEventSourceImpl< TestEvent > observable =
        new ObservableEventSourceImpl< TestObservableEventSource.TestEvent >();
    
    EventObserver< TestObservableEventSource.TestEvent > eventObserver =
        new TestObserver();
    
    observable.registerEventObserver( eventObserver );
    
    // test initial for observer existing
    assertFalse( "Expected observers collection not empty",
        observable.getObservers().isEmpty() );
    assertSame( "Expected observer in collection",
        eventObserver, observable.getObservers().toArray()[ 0 ] );
    
    // test adding same observer two times
    observable.registerEventObserver( eventObserver );
    assertEquals( "Expected observers collection with 1 element",
        1, observable.getObservers().size() );
    assertTrue( "Expected has observers ", observable.hasObservers() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.ObservableEventSourceImpl#unregisterEventObserver(de.unikassel.android.sdcframework.util.facade.EventObserver)}
   * .
   */
  public final void testUnregisterEventObserver()
  {
    ObservableEventSourceImpl< TestEvent > observable =
        new ObservableEventSourceImpl< TestObservableEventSource.TestEvent >();
    
    EventObserver< TestObservableEventSource.TestEvent > eventObserver1 =
        new TestObserver();
    EventObserver< TestObservableEventSource.TestEvent > eventObserver2 =
        new TestObserver();
    
    // do add 2 observers
    observable.registerEventObserver( eventObserver1 );
    observable.registerEventObserver( eventObserver2 );
    assertEquals( "Expected observers collection with 2 elements",
        2, observable.getObservers().size() );
    
    // do remove observer2
    observable.unregisterEventObserver( eventObserver2 );
    assertEquals( "Expected observers collection with 1 element",
        1, observable.getObservers().size() );
    assertSame( "Expected observer in collection",
        eventObserver1, observable.getObservers().toArray()[ 0 ] );
    assertTrue( "Expected has observers ", observable.hasObservers() );
    
    // do remove observer1
    observable.unregisterEventObserver( eventObserver1 );
    assertTrue( "Expected observers collection empty",
        observable.getObservers().isEmpty() );
    assertFalse( "Expected has observers ", observable.hasObservers() );
    
    // now add again for a test in changed order
    observable.registerEventObserver( eventObserver1 );
    observable.registerEventObserver( eventObserver2 );
    assertEquals( "Expected observers collection with 2 elements",
        2, observable.getObservers().size() );
    
    // do remove observer1
    observable.unregisterEventObserver( eventObserver1 );
    assertEquals( "Expected observers collection with 1 element",
        1, observable.getObservers().size() );
    assertSame( "Expected observer in collection",
        eventObserver2, observable.getObservers().toArray()[ 0 ] );
    
    // do remove observer2
    observable.unregisterEventObserver( eventObserver2 );
    assertTrue( "Expected observers collection empty",
        observable.getObservers().isEmpty() );
    
    // add again for a test of removing all
    observable.registerEventObserver( eventObserver1 );
    observable.registerEventObserver( eventObserver2 );
    assertEquals( "Expected observers collection with 2 elements",
        2, observable.getObservers().size() );
    
    observable.removeAllObservers();
    assertTrue( "Expected observers collection empty",
        observable.getObservers().isEmpty() );
  }
  
  /**
   * Private inner class to test ObservableEventSourceImpl. Does provide a
   * method to call notify for an provided event.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestEventSource extends ObservableEventSourceImpl< TestEvent >
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
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.ObservableEventSourceImpl#notify(de.unikassel.android.sdcframework.util.facade.ObservableEvent)}
   * .
   */
  public final void testNotifyT()
  {
    TestEvent testEvent = new TestEvent();
    
    // create observable test event source
    TestEventSource observable = new TestEventSource();
    
    // create an observer storing last observed event for test purpose
    TestObserver eventObserver = new TestObserver();
    assertTrue( "Expected observer has no stored event",
        eventObserver.lastEvent == null );
    
    observable.registerEventObserver( eventObserver );
    observable.doEvent( testEvent );
    
    // test if event was redirected to our observer
    assertTrue( "Expected observer has a stored event",
        eventObserver.lastEvent != null );
    assertSame( "Expected stored observed event equals the test event",
        testEvent, eventObserver.lastEvent );
  }
}
