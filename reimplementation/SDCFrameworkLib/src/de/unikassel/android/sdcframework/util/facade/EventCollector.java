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
package de.unikassel.android.sdcframework.util.facade;

import java.util.Collection;

/**
 * Interface for event collectors. <br/>
 * <br/>
 * An event collector does collect {@linkplain ObservableEvent observable event
 * types} by storing the events in a thread safe queue. It does provide access
 * methods for a {@linkplain EventDispatcher event dispatcher} to dequeue the
 * stored events. <br/>
 * The internal {@link EventObserver} instance can be registered to any instance
 * of an {@link ObservableEventSource}, valid for the collected event type. <br/>
 * {@link EventError Errors} during event collection can be observed.
 * 
 * @author Katy Hilgenberg
 * 
 * @param <T>
 *          the collected event type extending {@linkplain ObservableEvent}
 * 
 */
public interface EventCollector< T extends ObservableEvent >
    extends ObservableEventSource< EventError >
{
  /**
   * Method to enqueue an event
   * 
   * @param event
   *          the event to enqueue
   * @return true if successful, false otherwise
   */
  public abstract boolean enqueue( T event );
  
  /**
   * Method to dequeue an event. Will block the caller if queue is empty!
   * 
   * @return the event
   * @throws InterruptedException
   *           is thrown if a calling thread is interrupted while blocking at
   *           the internal queue
   */
  public abstract T dequeue() throws InterruptedException;
  
  /**
   * Method to dequeue more than one element event.
   * 
   * @param collection
   *          the collection to dequeue elements into
   * @param maxElements
   *          the maximum element count to dequeue
   * 
   * @return the number of elements dequeued
   */
  public abstract int dequeue( Collection< ? super T > collection,
      int maxElements );
  
  /**
   * Getter for the count collected and stored events
   * 
   * @return the count of collected and stored events
   */
  public abstract int getEventCount();
  
  /**
   * Does clear the collected event queue
   */
  public abstract void clearCollectedEvents();
  
  /**
   * Getter for the event observer
   * 
   * @return the event observer
   */
  public abstract EventObserver< T > getEventObserver();
}
