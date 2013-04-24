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

import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.data.independent.GeoLocation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * A broadcast receiver component for sample intents.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class IntentReceiver
    extends BroadcastReceiver
{
  /**
   * The sample intent action
   */
  public static final String ACTION = BasicSample.ACTION;
  
  /**
   * The handler for received intents
   */
  private final IntentHandler handler;
  
  /**
   * Constructor
   */
  public IntentReceiver()
  {
    super();
    this.handler = CentralSampleSource.getInstance();
  }
  
  /**
   * Getter for the handler
   * 
   * @return the handler
   */
  public IntentHandler getHandler()
  {
    return handler;
  }
  
  /**
   * Method to register this receiver in a context
   * 
   * @param context
   *          the context
   */
  public void register( Context context )
  {
    
    IntentFilter filter = new IntentFilter();
    filter.addAction( IntentReceiver.ACTION );
    context.registerReceiver( this, filter );
  }
  
  /**
   * Method to register this receiver in a context
   * 
   * @param context
   *          the context
   */
  public void unregister( Context context )
  {
    context.unregisterReceiver( this );
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
    if ( ACTION.equals( intent.getAction() ) )
    {
      if ( getHandler() != null )
      {
        if ( intent.getAction().equals( BasicSample.ACTION ) )
        {
          BasicSample sample = new BasicSample();
          
          sample.setDeviceIdentifier( intent.getStringExtra( BasicSample.SensorID ) );
          sample.setTimeStamp( intent.getLongExtra( BasicSample.Timestamp, 0L ) );
          sample.setPriority( intent.getIntExtra( BasicSample.Prio, 0 ) );
          if ( intent.hasExtra( GeoLocation.Lat )
              && intent.hasExtra( GeoLocation.Lon ) )
          {
            GeoLocation location = new GeoLocation();
            location.setLat( intent.getDoubleExtra( GeoLocation.Lat, 0. ) );
            location.setLon( intent.getDoubleExtra( GeoLocation.Lon, 0. ) );
            sample.setLocation( location );
          }
          if ( intent.hasExtra( BasicSample.DataType )
              && intent.hasExtra( BasicSample.SampleData ) )
          {
            sample.setDataFromXML(
                intent.getStringExtra( BasicSample.DataType ),
                intent.getStringExtra( BasicSample.SampleData ) );
          }
          
          getHandler().handleSample( sample );
        }
      }
      
    }
  }
  
}
