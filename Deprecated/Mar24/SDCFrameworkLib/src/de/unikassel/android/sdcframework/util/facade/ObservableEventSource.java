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
 * Interface for classes creating {@linkplain ObservableEvent observable events
 * * }. <br/>
 * <br/>
 * Any class in the SDC framework which does act as observable event source must
 * implementing this interface for the observable {@linkplain ObservableEvent
 * event types}. <br/>
 * <br/>
 * Observers for the observable events have to extend the
 * {@linkplain EventObserver generic event observer class}.
 * 
 * @see EventObserver
 * @see ObservableEvent
 * @see de.unikassel.android.sdcframework.util.ObservableEventSourceImpl
 * 
 * @author Katy Hilgenberg
 * 
 * @param <T>
 *          the observed event type extending {@linkplain ObservableEvent}
 */
public interface ObservableEventSource< T extends ObservableEvent > extends
    ObserverRegistration< T >
{
  
  /**
   * Does set the changed flag and notifies all observers
   * 
   * @param data
   *          the observable event data to create notifications for
   */
  public abstract void notify( T data );
  
  /**
   * Test method for observers.
   * 
   * @return true if observers are registered, false otherwise
   */
  public abstract boolean hasObservers();
  
}