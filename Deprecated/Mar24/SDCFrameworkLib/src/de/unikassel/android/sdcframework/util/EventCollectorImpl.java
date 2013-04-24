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
package de.unikassel.android.sdcframework.util;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import de.unikassel.android.sdcframework.util.facade.EventError;
import de.unikassel.android.sdcframework.util.facade.EventErrorTypes;
import de.unikassel.android.sdcframework.util.facade.EventCollector;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Generic type implementing a thread safe event queue. <br/>
 * <br/>
 * The class does provide an observer for the event type to collect, which can
 * be added to an observable event source. The observed events be enqueued
 * automatically in an internal thread safe event queue. <br/>
 * Stored events can be dequeued by a call to {@linkplain #dequeue()} for
 * further processing.
 * 
 * For example, the
 * {@linkplain de.unikassel.android.sdcframework.util.AbstractAsynchrounousSampleObserver
 * AbstractAsynchrounousSampleObserver} is using an instance of this class
 * bounded to {@linkplain de.unikassel.android.sdcframework.data.Sample Sample}
 * to asynchronously queue observed sensor samples.
 * 
 * @author Katy Hilgenberg
 * 
 * @param <T>
 *          the collected event type extending {@linkplain ObservableEvent}
 * @see EventCollector
 * 
 */
public final class EventCollectorImpl< T extends ObservableEvent >
    implements EventCollector< T >
{
  /**
   * Inner class implementing the event observer for the event type
   * 
   * @author Katy Hilgenberg
   */
  private final class EventObserverImpl implements EventObserver< T >
  {
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de
     * .unikassel.android.sdcframework.util.facade.ObservableEventSource,
     * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
     */
    @Override
    public void
        onEvent( ObservableEventSource< ? extends T > eventSource,
            T observedEvent )
    {
      if ( !enqueue( observedEvent ) )
      {
        EventCollectorImpl.this.notify( new EventError(
            EventErrorTypes.FAILED_ENQUEUE,
            observedEvent ) );
      }
    }
  }
  
  /**
   * the queue to store the events in
   */
  private final BlockingQueue< T > queue;
  
  /**
   * The event observer
   */
  private EventObserverImpl eventObserver;
  
  /**
   * The observable error event source
   */
  private ObservableEventSourceImpl< EventError > errorEventSource;
  
  /**
   * Constructor
   */
  public EventCollectorImpl()
  {
    // initialize final queues
    queue = new LinkedBlockingQueue< T >();
  }
  
  /**
   * Setter for the eventObserver
   * 
   * @param eventObserver
   *          the eventObserver to set
   */
  private final void setEventObserver( EventObserverImpl eventObserver )
  {
    this.eventObserver = eventObserver;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.EventCollector#getEventObserver
   * ()
   */
  @Override
  public final synchronized EventObserver< T > getEventObserver()
  {
    if ( eventObserver == null )
    {
      setEventObserver( new EventObserverImpl() );
    }
    return eventObserver;
  }
  
  /**
   * Getter for the observable error event source
   * 
   * @return the observable error event source
   */
  public final ObservableEventSourceImpl< EventError > getErrorEventSource()
  {
    if ( errorEventSource == null )
    {
      setErrorEventSource( new ObservableEventSourceImpl< EventError >() );
    }
    return errorEventSource;
  }
  
  /**
   * Setter for the observable error event source
   * 
   * @param errorEventSource
   *          the observable error event source to set
   */
  private final void setErrorEventSource(
      ObservableEventSourceImpl< EventError > errorEventSource )
  {
    this.errorEventSource = errorEventSource;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.EventCollector#enqueue(de
   * .unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public final boolean enqueue( T event )
  {
    try
    {
      queue.put( event );
    }
    catch ( Exception e )
    {
      return false;
    }
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.EventCollector#dequeue()
   */
  @Override
  public final T dequeue() throws InterruptedException
  {
    return queue.take();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.EventCollector#dequeue(java
   * .util.Collection, int)
   */
  @Override
  public int dequeue( Collection< ? super T > collection, int maxElements )
  {
    if ( collection != null )
    {
      return queue.drainTo( collection, maxElements );
    }
    return 0;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.EventCollector#getEventCount
   * ()
   */
  @Override
  public final int getEventCount()
  {
    return queue.size();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.EventCollector#
   * clearCollectedEvents()
   */
  @Override
  public final void clearCollectedEvents()
  {
    queue.clear();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * registerEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public final void
      registerEventObserver( EventObserver< ? extends EventError > observer )
  {
    getErrorEventSource().registerEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * unregisterEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public final void unregisterEventObserver(
      EventObserver< ? extends EventError > observer )
  {
    getErrorEventSource().unregisterEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * removeAllObservers()
   */
  @Override
  public final void removeAllObservers()
  {
    getErrorEventSource().removeAllObservers();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.ObservableEventSource#notify
   * (de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public final void notify( EventError data )
  {
    getErrorEventSource().notify( data );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * hasObservers()
   */
  @Override
  public boolean hasObservers()
  {
    return getErrorEventSource().hasObservers();
  }
}
