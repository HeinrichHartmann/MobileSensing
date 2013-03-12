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
package de.unikassel.android.sdcframework.broadcast;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * The observable broadcast receiver implementation for {@linkplain Sample
 * samples}. <br/>
 * <br/>
 * 
 * @see SampleBroadcastServiceImpl
 * @author Katy Hilgenberg
 * 
 */
public final class SampleListener
    extends BroadcastReceiver
    implements ObservableEventSource< Sample >
{
  /**
   * The internal observable {@linkplain Sample} source
   */
  private final ObservableEventSourceImpl< Sample > sampleSource;
  
  /**
   * Constructor
   */
  public SampleListener()
  {
    super();
    sampleSource = new ObservableEventSourceImpl< Sample >();
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
    if( sampleSource.getObservers().isEmpty() ) return;
    
    if ( Sample.ACTION.equals( intent.getAction() ) )
    {
      notify( new Sample( intent ) );
      
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * registerEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public void
      registerEventObserver( EventObserver< ? extends Sample > observer )
  {
    sampleSource.registerEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * unregisterEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public void unregisterEventObserver(
      EventObserver< ? extends Sample > observer )
  {
    sampleSource.unregisterEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * removeAllObservers()
   */
  @Override
  public void removeAllObservers()
  {
    sampleSource.removeAllObservers();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.ObservableEventSource#notify
   * (de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public void notify( Sample data )
  {
    sampleSource.notify( data );
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
    return sampleSource.hasObservers();
  }
  
}
