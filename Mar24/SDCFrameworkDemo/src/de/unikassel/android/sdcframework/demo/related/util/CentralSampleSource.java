/*
 * Copyright (C) 2012, Katy Hilgenberg.
 * Special acknowledgments to: Knowledge & Data Engineering Group, University of Kassel (http://www.kde.cs.uni-kassel.de).
 * Contact: sdcf@cs.uni-kassel.de
 *
 * This file is part of the SDCFramework project.
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
package de.unikassel.android.sdcframework.demo.related.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.data.independent.BasicSampleCollection;
import de.unikassel.android.sdcframework.data.independent.BluetoothSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.data.independent.WifiSampleData;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEvent;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Class to manage the received samples for the related views. It is implemented
 * as singleton, can be observed for changes, and is sending its own reference
 * as notification event.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class CentralSampleSource
    implements ObservableEventSource< CentralSampleSource >,
    IntentHandler, ObservableEvent
{
  /**
   * The observable WIFI sample source
   */
  private final ObservableWifiSource wifiSource;
  
  /**
   * The observable Bluetooth sample source
   */
  private final ObservableBluetoothSource btSource;
  
  /**
   * The observable event source to delegate to
   */
  private final ObservableEventSource< CentralSampleSource > eventSource;
  
  /**
   * The observer to observe wifi sample collection events
   */
  private final EventObserver< EnhancedWifiData.Collection > wifiObserver;
  
  /**
   * The observer to observe Bluetooth sample collection events
   */
  private final EventObserver< EnhancedBluetoothData.Collection > btObserver;
  
  /**
   * The map to store current samples per device
   */
  private final Map< String, BasicSampleCollection > mapCurrentSamples;
  
  /**
   * The singleton instance
   */
  private static CentralSampleSource instance = null;
  
  /**
   * Getter for the global instance
   * 
   * @return the global singleton instance
   */
  public synchronized static CentralSampleSource getInstance()
  {
    if ( instance == null )
    {
      instance = new CentralSampleSource();
    }
    return instance;
  }
  
  /**
   * Constructor
   */
  private CentralSampleSource()
  {
    super();
    
    this.mapCurrentSamples =
        Collections.synchronizedMap( new LinkedHashMap< String, BasicSampleCollection >() );
    this.eventSource = new ObservableEventSourceImpl< CentralSampleSource >();
    
    this.wifiObserver = new EventObserver< EnhancedWifiData.Collection >()
    {
      
      @Override
      public void onEvent(
          ObservableEventSource< ? extends EnhancedWifiData.Collection > eventSource,
          EnhancedWifiData.Collection observedEvent )
      {
        onWifiEvent();
      }
    };
    
    this.btObserver = new EventObserver< EnhancedBluetoothData.Collection >()
    {
      
      @Override
      public void onEvent(
          ObservableEventSource< ? extends EnhancedBluetoothData.Collection > eventSource,
          EnhancedBluetoothData.Collection observedEvent )
      {
        onBluetoothEvent();
      }
    };
    
    this.wifiSource = new ObservableWifiSource();
    this.wifiSource.registerEventObserver( wifiObserver );
    this.btSource = new ObservableBluetoothSource();
    this.btSource.registerEventObserver( btObserver );
  }
  
  /**
   * Getter for the Wifi source
   * 
   * @return the Wifi source
   */
  public ObservableWifiSource getWifiSource()
  {
    return wifiSource;
  }
  
  /**
   * Getter for the Bluetooh source
   * 
   * @return the Bluetooh source
   */
  public ObservableBluetoothSource getBluetoohSource()
  {
    return btSource;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.util.IntentHandler#handleSample
   * (de.unikassel.android.sdcframework.data.independent.BasicSample)
   */
  @Override
  public synchronized void handleSample( BasicSample sample )
  {
    SampleData data = sample.getData();
    if ( data instanceof WifiSampleData )
    {
      getWifiSource().addSample( sample );
    }
    else if ( data instanceof BluetoothSampleData )
    {
      getBluetoohSource().addSample( sample );
    }
    else
    {
      BasicSampleCollection sampleList = new BasicSampleCollection();
      sampleList.getSamples().add( sample );
      mapCurrentSamples.put( sample.getDeviceIdentifier(), sampleList );
      notify( this );
    }
  }
  
  /**
   * Getter for the most recent samples mapped by device names
   * 
   * @return a map with the most recent samples
   */
  public Map< String, BasicSampleCollection > getMostRecentSamplesMappedByType()
  {
    return new HashMap< String, BasicSampleCollection >( mapCurrentSamples );
  }
  
  /**
   * Getter for the most recent samples as collection of samples
   * 
   * @return a collection with the most recent samples
   */
  public BasicSampleCollection getMostRecentSamples()
  {
    BasicSampleCollection result = new BasicSampleCollection();
    List< BasicSample > sampleList = result.getSamples();
    for ( BasicSampleCollection sampleCollection : mapCurrentSamples.values() )
    {
      sampleList.addAll( sampleCollection.getSamples() );
    }
    return result;
  }
  
  /**
   * Getter for the most recent Wifi samples as collection of samples
   * 
   * @return a collection with the most recent Wifi samples
   */
  public BasicSampleCollection getMostRecentWifiSamples()
  {
    BasicSampleCollection result = new BasicSampleCollection();
    String sID = SensorDeviceIdentifier.Wifi.toString();
    if ( mapCurrentSamples.containsKey( sID ) )
    {
      result.getSamples().addAll( mapCurrentSamples.get( sID ).getSamples() );
    }
    return result;
  }
  
  /**
   * Getter for the most recent Bluetooth samples as collection of samples
   * 
   * @return a collection with the most recent Bluetooth samples
   */
  public BasicSampleCollection getMostRecentBluetoothSamples()
  {
    BasicSampleCollection result = new BasicSampleCollection();
    String sID = SensorDeviceIdentifier.Bluetooth.toString();
    if ( mapCurrentSamples.containsKey( sID ) )
    {
      result.getSamples().addAll( mapCurrentSamples.get( sID ).getSamples() );
    }
    return result;
  }
  
  /**
   * Getter for the most recent non Wifi or Bluetooth samples as collection of
   * samples
   * 
   * @return a collection with the most recent non Wifi or Bluetooth samples
   */
  public BasicSampleCollection getMostRecentNonWifiOrBTSamples()
  {
    BasicSampleCollection result = new BasicSampleCollection();
    List< BasicSample > sampleList = result.getSamples();
    for ( SensorDeviceIdentifier id : SensorDeviceIdentifier.values() )
    {
      if ( id.equals( SensorDeviceIdentifier.Wifi )
          || id.equals( SensorDeviceIdentifier.Bluetooth ) )
      {
        continue;
      }
      String sID = id.toString();
      if ( mapCurrentSamples.containsKey( sID ) )
      {
        sampleList.addAll( mapCurrentSamples.get( sID ).getSamples() );
      }
    }
    return result;
  }
  
  /**
   * Getter for the most recent Twitter samples as collection of samples
   * 
   * @return a collection with the most recent Twitter samples
   */
  public BasicSampleCollection getMostRecentTwitterSamples()
  {
    BasicSampleCollection result = new BasicSampleCollection();
    List< BasicSample > sampleList = result.getSamples();
    for ( SensorDeviceIdentifier id : SensorDeviceIdentifier.values() )
    {
      if ( id.equals( SensorDeviceIdentifier.Twitter ) )
      {
        String sID = id.toString();
        if ( mapCurrentSamples.containsKey( sID ) )
        {
          sampleList.addAll( mapCurrentSamples.get( sID ).getSamples() );
        }
      }
    }
    return result;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * registerEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public void registerEventObserver(
      EventObserver< ? extends CentralSampleSource > observer )
  {
    eventSource.registerEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * unregisterEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public void unregisterEventObserver(
      EventObserver< ? extends CentralSampleSource > observer )
  {
    eventSource.unregisterEventObserver( observer );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
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
   * @see
   * de.unikassel.android.sdcframework.util.facade.ObservableEventSource#notify
   * (de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public void notify( CentralSampleSource data )
  {
    eventSource.notify( data );
  }
  
  /**
   * Wifi event handle
   */
  public void onWifiEvent()
  {
    BasicSampleCollection list = new BasicSampleCollection();
    for ( EnhancedWifiData wifiData : wifiSource.getMostRecentSamples( Long.MAX_VALUE ) )
    {
      list.getSamples().add( wifiData.sample );
    }
    mapCurrentSamples.put( SensorDeviceIdentifier.Wifi.toString(), list );
    notify( this );
  }
  
  /**
   * Bluetooth event handle
   */
  public void onBluetoothEvent()
  {
    BasicSampleCollection list = new BasicSampleCollection();
    for ( EnhancedBluetoothData btData : btSource.getMostRecentSamples( Long.MAX_VALUE ) )
    {
      list.getSamples().add( btData.sample );
    }
    mapCurrentSamples.put( SensorDeviceIdentifier.Bluetooth.toString(), list );
    notify( this );
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
