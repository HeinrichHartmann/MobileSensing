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
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Generic implementation for an observable event source. <br/>
 * <br/>
 * This class is implementing the {@link ObservableEventSource
 * ObservableEventSource} interface. <br/>
 * <br/>
 * A class extending this type can notify it's registered observers about the
 * observed events by invoking the
 * {@link ObservableEventSourceImpl#notify(ObservableEvent) notify} method.
 * 
 * @author Katy Hilgenberg
 * @param <T>
 *          the observed event type extending {@linkplain ObservableEvent
 *          ObservableEvent}
 * 
 */
public class ObservableEventSourceImpl< T extends ObservableEvent >
    implements ObservableEventSource< T >
{
  /**
   * The observer collection
   */
  private final ConcurrentLinkedQueue< EventObserver< T > > observers;
  
  /**
   * Constructor
   */
  public ObservableEventSourceImpl()
  {
    super();
    observers = new ConcurrentLinkedQueue< EventObserver< T > >();
  }
  
  /**
   * Getter for the observers
   * 
   * @return the observers
   */
  public final Collection< EventObserver< T > > getObservers()
  {
    return Collections.unmodifiableCollection( observers );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.ObservableEventCreator#
   * registerEventObserver
   * (de.unikassel.android.sdcframework.util.AbstractEventObserver)
   */
  @SuppressWarnings( "unchecked" )
  @Override
  public final void
      registerEventObserver( EventObserver< ? extends T > observer )
  {
    if ( !observers.contains( observer ) )
    {
      if ( observers.add( (EventObserver< T >) observer ) )
      {
        onObserverRegistration( observer );
      }
    }
  }
  
  /**
   * Handler for registered observers ( to be overloaded by extending classes if
   * necessary )
   * 
   * @param observer
   *          the registered observer
   */
  protected void onObserverRegistration( EventObserver< ? extends T > observer )
  {
    // default do nothing
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.ObservableEventCreator#
   * unregisterEventObserver
   * (de.unikassel.android.sdcframework.util.AbstractEventObserver)
   */
  @Override
  public final void unregisterEventObserver(
      EventObserver< ? extends T > observer )
  {
    if ( observers.remove( observer ) )
    {
      onObserverUnregistration( observer );
    }
  }
  
  /**
   * Handler for unregistered observers ( to be overloaded by extending classes
   * if necessary )
   * 
   * @param observer
   *          the unregistered observer
   */
  protected void
      onObserverUnregistration( EventObserver< ? extends T > observer )
  {
    // default do nothing
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.ObservableEventCreator#notify(T)
   */
  @Override
  public final void notify( T data )
  {
    if ( data == null )
      return;
    
    Iterator< EventObserver< T >> it = observers.iterator();
    while ( it.hasNext() )
    {
      EventObserver< T > observer = it.next();
      try
      {
        observer.onEvent( this, data );
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this,
            "Exception during observer notification: " + e.getMessage() );
      }
    }
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
    observers.clear();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * hasObservers()
   */
  @Override
  public final boolean hasObservers()
  {
    return !observers.isEmpty();
  }
}
