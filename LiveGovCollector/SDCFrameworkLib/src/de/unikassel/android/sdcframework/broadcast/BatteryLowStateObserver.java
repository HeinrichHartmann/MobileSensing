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

import de.unikassel.android.sdcframework.util.BatteryLowEvent;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.LifeCycleObject;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * An observable battery low broadcast receiver.
 * 
 * @author Katy Hilgenberg
 */
public class BatteryLowStateObserver
    extends BroadcastReceiver
    implements ObservableEventSource< BatteryLowEvent >, LifeCycleObject
{
  /**
   * The observable event source
   */
  private final ObservableEventSourceImpl< BatteryLowEvent > eventSource;
  
  /**
   * Constructor
   */
  public BatteryLowStateObserver()
  {
    this.eventSource =
        new ObservableEventSourceImpl< BatteryLowEvent >();
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
    if ( eventSource.getObservers().isEmpty() )
      return;
    
    if ( Intent.ACTION_BATTERY_LOW.equals( intent.getAction() ) )
    {
      notify( new BatteryLowEvent() );
    }
  }
  
  @Override
  public void registerEventObserver(
      EventObserver< ? extends BatteryLowEvent > observer )
  {
    eventSource.registerEventObserver( observer );
  }
  
  @Override
  public void unregisterEventObserver(
      EventObserver< ? extends BatteryLowEvent > observer )
  {
    eventSource.unregisterEventObserver( observer );
  }
  
  @Override
  public void removeAllObservers()
  {
    eventSource.removeAllObservers();
  }
  
  @Override
  public void notify( BatteryLowEvent data )
  {
    eventSource.notify( data );
  }
  
  @Override
  public void onResume( Context applicationContext )
  {
    applicationContext.registerReceiver( this, new IntentFilter(
        Intent.ACTION_BATTERY_LOW ) );
  }
  
  @Override
  public void onPause( Context applicationContext )
  {
    try
    {
      applicationContext.unregisterReceiver( this );
    }
    catch ( Exception e )
    {}
  }
  
  @Override
  public void onCreate( Context applicationContext )
  {}
  
  @Override
  public void onDestroy( Context applicationContext )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * hasObservers()
   */
  @Override
  public boolean hasObservers()
  {
    return eventSource.hasObservers();
  }
}
