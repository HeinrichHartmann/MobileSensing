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

/**
 * Interface for event dispatchers. <br/>
 * <br/>
 * An event dispatcher is linked to an {@linkplain EventCollector event
 * collector} and does asynchronously dequeue and dispatch the stored events to
 * its registered observers.
 * 
 * @author Katy Hilgenberg
 * 
 * @param <T>
 *          the collected event type extending {@linkplain ObservableEvent}
 * @param <U>
 *          the dispatched event type extending {@linkplain ObservableEvent}
 * @see ObservableEventSource
 */
public interface EventDispatcher< T extends ObservableEvent, U extends ObservableEvent >
    extends ObservableEventSource< U >, WorkerThread
{
  /**
   * Getter for the collector
   * 
   * @return the collector
   */
  public abstract EventCollector< T > getCollector();
}
