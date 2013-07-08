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
import de.unikassel.android.sdcframework.data.SampleCollection;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * The observable broadcast receiver implementation for {@linkplain Sample
 * samples}. <br/>
 * <br/>
 * 
 * @see SampleBroadcastServiceImpl
 * @author Katy Hilgenberg
 * 
 */
public class SampleListener
    extends BroadcastReceiver
    implements ObservableEventSource< SampleCollection >
{
  /**
   * The internal observable {@linkplain SampleCollection} source
   */
  private final ObservableEventSourceImpl< SampleCollection > sampleSource;
  
  /**
   * Constructor
   */
  public SampleListener()
  {
    super();
    sampleSource = new ObservableEventSourceImpl< SampleCollection >();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
   * android.content.Intent)
   */
  @Override
  public final void onReceive( Context context, Intent intent )
  {
    if ( sampleSource.getObservers().isEmpty() )
      return;
    
    if ( Sample.ACTION.equals( intent.getAction() ) )
    {
      Sample parcelableExtra =
          intent.getParcelableExtra( Sample.PARCELABLE_EXTRA_NAME );
      SampleCollection sampleCollection = new SampleCollection();
      sampleCollection.add( parcelableExtra );
      notify( sampleCollection );
    }
    else if ( SampleCollection.ACTION.equals( intent.getAction() ) )
    {
      SampleCollection sampleCollection =
          (SampleCollection) intent.getParcelableExtra( SampleCollection.PARCELABLE_EXTRA_NAME );
      notify( sampleCollection );
    }
  }
  
  /**
   * Method to register this class as broadcast receiver for samples in a given
   * context
   * 
   * @param context
   *          the context
   */
  public final void registerAsBroadCastReceiver( Context context )
  {
    IntentFilter filter = new IntentFilter();
    filter.addAction( Sample.ACTION );
    filter.addAction( SampleCollection.ACTION );
    context.registerReceiver( this, filter );
  }
  
  /**
   * Method to unregister this class as broadcast receiver for samples in a
   * given context
   * 
   * @param context
   *          the context
   */
  public final void unregisterAsBroadCastReceiver( Context context )
  {
    context.unregisterReceiver( this );
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
    sampleSource.removeAllObservers();
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
    return sampleSource.hasObservers();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * registerEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public final void registerEventObserver(
      EventObserver< ? extends SampleCollection > observer )
  {
    sampleSource.registerEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * unregisterEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public final void unregisterEventObserver(
      EventObserver< ? extends SampleCollection > observer )
  {
    sampleSource.unregisterEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.ObservableEventSource#notify
   * (de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public final void notify( SampleCollection data )
  {
    sampleSource.notify( data );
  }  
}
