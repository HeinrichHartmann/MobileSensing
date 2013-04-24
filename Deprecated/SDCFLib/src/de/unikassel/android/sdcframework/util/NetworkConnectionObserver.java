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

import java.util.concurrent.atomic.AtomicBoolean;

import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.NetworkStateChangeEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

/**
 * Implementation of an observer for network connection state changes as global singleton.
 * 
 * @author Katy Hilgenberg
 * 
 */
/**
 * @author Katy Hilgenberg
 *
 */
/**
 * @author Katy Hilgenberg
 * 
 */
public class NetworkConnectionObserver
    extends BroadcastReceiver
    implements ObservableEventSource< NetworkStateChangeEvent >
{
  /**
   * The observable event source
   */
  private final ObservableEventSource< NetworkStateChangeEvent > eventSource;
  
  /**
   * Observation state flag
   */
  private boolean isObserving;
  
  /**
   * last connection state
   */
  private final AtomicBoolean isNotConnected;
  
  /**
   * The application context.
   */
  private Context context;
  
  /**
   * The singleton instance
   */
  private static NetworkConnectionObserver instance;
  
  /**
   * Getter for the instance
   * 
   * @param context
   *          the application context.
   * 
   * @return the instance
   */
  public synchronized static NetworkConnectionObserver getInstance(
      Context context )
  {
    if ( instance == null )
    {
      instance = new NetworkConnectionObserver( context );
    }
    return instance;
  }
  
  /**
   * Constructor
   * 
   * @param context
   *          the application context.
   */
  private NetworkConnectionObserver( Context context )
  {
    super();
    eventSource = new ObservableEventSourceImpl< NetworkStateChangeEvent >();
    isObserving = false;
    isNotConnected = new AtomicBoolean();
    this.context = context;
  }
  
  /**
   * Setter for the not connected state
   * 
   * @param isNotConnected
   *          the not connected state to set
   */
  private synchronized void setNotConnected( boolean isNotConnected )
  {
    this.isNotConnected.set( isNotConnected );
  }
  
  /**
   * Getter for the not connected state
   * 
   * @return the not connected state
   */
  private synchronized boolean isNotConnected()
  {
    return isNotConnected.get();
  }
  
  /**
   * Getter for the last known connection state
   * 
   * @return the last known connection state
   */
  public boolean isConnected()
  {
    return !isNotConnected();
  }
  
  /**
   * Does start network state observation
   */
  private synchronized void startObservation()
  {
    if ( !isObserving )
    {
      IntentFilter filter = new IntentFilter();
      filter.addAction( ConnectivityManager.CONNECTIVITY_ACTION );
      context.registerReceiver( this, filter );
      setNotConnected( true );
      isObserving = true;
      Logger.getInstance().info( this, "Observation started" );
    }
  }
  
  /**
   * Does stop network state observation
   */
  private synchronized void stopObservation()
  {
    if ( isObserving )
    {
      context.unregisterReceiver( this );
      isObserving = false;
      Logger.getInstance().info( this, "Observation stopped" );
    }
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
    String action = intent.getAction();
    
    if ( action.equals( ConnectivityManager.CONNECTIVITY_ACTION ) )
    {
      boolean noConnectivity =
          intent.getBooleanExtra( ConnectivityManager.EXTRA_NO_CONNECTIVITY,
              false );
      if ( isNotConnected() != noConnectivity )
      {
        Logger.getInstance().info( this, "Network state changed: connected = " + isConnected() );
        setNotConnected( noConnectivity );
        notify( new NetworkStateChangeEventImpl( !isNotConnected() ) );
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration
   * #registerEventObserver(de.unikassel.android.sdcframework.util.facade.
   * EventObserver)
   */
  @Override
  public void registerEventObserver(
      EventObserver< ? extends NetworkStateChangeEvent > observer )
  {
    eventSource.registerEventObserver( observer );
    startObservation();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration
   * #unregisterEventObserver(de.unikassel.android.sdcframework.util.facade.
   * EventObserver)
   */
  @Override
  public void unregisterEventObserver(
      EventObserver< ? extends NetworkStateChangeEvent > observer )
  {
    eventSource.unregisterEventObserver( observer );
    if ( !eventSource.hasObservers() )
      stopObservation();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration
   * #removeAllObservers()
   */
  @Override
  public void removeAllObservers()
  {
    eventSource.removeAllObservers();
    stopObservation();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.ObservableEventSource#notify
   * (de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public void notify( NetworkStateChangeEvent data )
  {
    eventSource.notify( data );
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
    return eventSource.hasObservers();
  }
  
}
