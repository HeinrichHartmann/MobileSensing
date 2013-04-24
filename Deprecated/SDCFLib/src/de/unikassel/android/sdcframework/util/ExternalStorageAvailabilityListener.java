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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import de.unikassel.android.sdcframework.util.ExternalStorageStateChangeEventImpl.State;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ExternalStorageStateChangeEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Implementation of an external storage state change listener as broadcast
 * receiver and observable source for the related
 * {@linkplain ExternalStorageStateChangeEvent event}.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ExternalStorageAvailabilityListener
    extends BroadcastReceiver
    implements ObservableEventSource< ExternalStorageStateChangeEvent >
{
  
  /**
   * The observable event source to delegate to
   */
  private final ObservableEventSource< ExternalStorageStateChangeEvent > eventSource;
  
  /**
   * last known external storage state
   */
  private ExternalStorageStateChangeEvent lastEvent;
  
  /**
   * Constructor
   */
  public ExternalStorageAvailabilityListener()
  {
    eventSource =
        new ObservableEventSourceImpl< ExternalStorageStateChangeEvent >();
    onExternalStorageStateChange();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.ObservableEventSource#notify
   * (de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public void notify( ExternalStorageStateChangeEvent data )
  {
    eventSource.notify( data );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * registerEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @SuppressWarnings( "unchecked" )
  @Override
  public void registerEventObserver(
      EventObserver< ? extends ExternalStorageStateChangeEvent > observer )
  {
    eventSource.registerEventObserver( observer );
    
    // notify new observers about the initial state
    notifyLastEvent( (EventObserver< ExternalStorageStateChangeEvent >) observer );
  }
  
  /**
   * Does notify the observer about the last event
   * 
   * @param observer
   *          the observer to notify
   */
  private void notifyLastEvent(
      EventObserver< ExternalStorageStateChangeEvent > observer )
  {
    observer.onEvent( this, lastEvent );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * removeAllObservers()
   */
  @Override
  public void removeAllObservers()
  {
    eventSource.removeAllObservers();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * unregisterEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public void unregisterEventObserver(
      EventObserver< ? extends ExternalStorageStateChangeEvent > observer )
  {
    eventSource.unregisterEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
   * android.content.Intent)
   */
  @Override
  public void onReceive( Context context, Intent intent )
  {
    onExternalStorageStateChange();
  }
  
  /**
   * handler for external storage state change events
   */
  private void onExternalStorageStateChange()
  {
    State actualState = State.UNAVAILABLE;
    
    String state = Environment.getExternalStorageState();
    if ( Environment.MEDIA_MOUNTED.equals( state ) )
    {
      actualState = State.AVAILABLE_FOR_WRITE;
    }
    else if ( Environment.MEDIA_MOUNTED_READ_ONLY.equals( state ) )
    {
      actualState = State.AVAILABLE_FOR_READ;
    }
    
    lastEvent = new ExternalStorageStateChangeEventImpl( actualState );
    notify( lastEvent );
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
