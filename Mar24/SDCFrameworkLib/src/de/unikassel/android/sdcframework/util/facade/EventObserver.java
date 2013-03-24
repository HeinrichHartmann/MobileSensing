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
 * Interface for event observers.
 * 
 * @see ObservableEvent
 * @see ObservableEventSource
 * 
 * @author Katy Hilgenberg
 * 
 * @param <T>
 *          the observed event type extending {@linkplain ObservableEvent
 *          ObservableEvent}
 */
public interface EventObserver< T extends ObservableEvent >
{
  
  /**
   * This method is called whenever the observed object does create an observed
   * event
   * 
   * @param eventSource
   *          the event generator
   * @param observedEvent
   *          the event created by the observed object
   */
  public abstract void onEvent( ObservableEventSource< ? extends T > eventSource,
      T observedEvent );
  
}