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

import java.security.InvalidParameterException;

import de.unikassel.android.sdcframework.util.facade.EventCollector;
import de.unikassel.android.sdcframework.util.facade.EventDispatcher;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Abstract Implementation of a event dispatcher as worker thread. <br/>
 * <br/>
 * The event dispatcher does collect observed events using an
 * {@linkplain EventCollector event collector} and does asynchronously dispatch
 * the events to the registered observers.<br/>
 * <br/>
 * The different types for the collected and the dispatched events do allow -
 * among other things - an implementation which does collect single events and
 * dispatch collections of the same event type.
 * 
 * @see EventDispatcher
 * @see EventCollectorImpl
 * @see ObservableEventSource
 * @author Katy Hilgenberg
 * 
 * @param <T>
 *          the collected event type extending {@linkplain ObservableEvent}
 * @param <U>
 *          the dispatched event type extending {@linkplain ObservableEvent}
 */
public abstract class AbstractEventDispatcherImpl< T extends ObservableEvent, U extends ObservableEvent >
    extends AbstractWorkerThread
    implements EventDispatcher< T, U >
{
  /**
   * The observable event source to delegate observer and notification handling
   * to
   */
  private final ObservableEventSource< U > eventSource;
  
  /**
   * The event collector to observe and dispatch collected events from
   */
  protected final EventCollector< T > collector;
  
  /**
   * Constructor
   */
  @SuppressWarnings( "unused" )
  private AbstractEventDispatcherImpl()
  {
    this( null );
  }
  
  /**
   * Constructor
   * 
   * @param collector
   *          the event collector to observe
   * @throws InvalidParameterException
   *           is thrown if the collector reference is null
   */
  public AbstractEventDispatcherImpl( EventCollector< T > collector )
      throws InvalidParameterException
  {
    super();
    eventSource =
        new ObservableEventSourceImpl< U >();
    this.collector = collector;
    if ( this.collector == null )
      throw new InvalidParameterException( "collector can not be null" );
  }
  
  /**
   * Getter for the eventSource
   * 
   * @return the eventSource
   */
  public final ObservableEventSource< U > getEventSource()
  {
    return eventSource;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.EventDispatcher#getCollector
   * ()
   */
  @Override
  public final EventCollector< T > getCollector()
  {
    return collector;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.AbstractWorkerThread#doCleanUp()
   */
  @Override
  protected final void doCleanUp()
  {
    collector.clearCollectedEvents();
    eventSource.removeAllObservers();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration
   * #registerEventObserver(de.unikassel.android.sdcframework.util.facade.
   * EventObserver)
   */
  @Override
  public final void
      registerEventObserver( EventObserver< ? extends U > observer )
  {
    eventSource.registerEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration
   * #unregisterEventObserver(de.unikassel.android.sdcframework.util.facade.
   * EventObserver)
   */
  @Override
  public final void unregisterEventObserver(
      EventObserver< ? extends U > observer )
  {
    eventSource.unregisterEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.ObservableEventSource#notify
   * (de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public void notify( U data )
  {
    eventSource.notify( data );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration
   * #removeAllObservers()
   */
  @Override
  public void removeAllObservers()
  {
    eventSource.removeAllObservers();
  }

  /* (non-Javadoc)
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#hasObservers()
   */
  @Override
  public boolean hasObservers()
  {
    return eventSource.hasObservers();
  }
}
