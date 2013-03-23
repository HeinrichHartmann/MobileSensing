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
import de.unikassel.android.sdcframework.util.facade.ObservableEvent;

/**
 * Implementation of a the event dispatcher task as worker thread.
 * 
 * @see EventDispatcher
 * @see AbstractEventDispatcherImpl
 * @author Katy Hilgenberg
 * 
 * @param <T>
 *          the collected event type extending {@linkplain ObservableEvent}
 */
public class EventDispatcherImpl< T extends ObservableEvent >
    extends AbstractEventDispatcherImpl< T, T >
{
  /**
   * Constructor
   */
  @SuppressWarnings( "unused" )
  private EventDispatcherImpl()
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
  public EventDispatcherImpl( EventCollector< T > collector )
      throws InvalidParameterException
  {
    super( collector );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.AbstractWorkerThread#doWork()
   */
  @Override
  protected void doWork()
  {
    try
    {
      // take event from queue and notify observers
      T event = collector.dequeue();
      notify( event );
    }
    catch ( InterruptedException e )
    {}
    catch( Exception e )
    {
      Logger.getInstance().error( this, "Exception in doWork" );
      e.printStackTrace();
    }
  }
}
