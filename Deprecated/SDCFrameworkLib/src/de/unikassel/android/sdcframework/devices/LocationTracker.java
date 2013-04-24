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
package de.unikassel.android.sdcframework.devices;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.LocationSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.util.LifeCycleObjectImpl;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Class to track location information using available sensors.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class LocationTracker
    extends LifeCycleObjectImpl
    implements EventObserver< Sample >
{
  /**
   * The time difference for significance
   */
  private static final int SIGNIFICANT_TIME_DIFF = 0;
  
  /**
   * The current Location
   */
  private Location currentLocation;
  
  /**
   * Enabled flag
   */
  private boolean enabled;
  
  /**
   * the application context
   */
  private Context context;
  
  /**
   * Constructor
   */
  public LocationTracker()
  {}
  
  /* (non-Javadoc)
   * @see de.unikassel.android.sdcframework.util.LifeCycleObjectImpl#onCreate(android.content.Context)
   */
  @Override
  public final void onCreate( Context applicationContext )
  {
    this.context = applicationContext;
    super.onCreate( applicationContext );
  }
  
  /**
   * Method to get the last known location
   */
  private void getLastKnownLocation()
  {
    if( context == null ) return;
    
    // try to get last know location
    LocationManager locationManager =
        (LocationManager) context.getSystemService( Context.LOCATION_SERVICE );
    for ( String provider : locationManager.getProviders( true ) )
    {
      Location lastKnownLocation =
          locationManager.getLastKnownLocation( provider );
      
      if ( isBetterLocation( lastKnownLocation, getCurrentLocation() ) )
      {
        setCurrentLocation( lastKnownLocation );
      }
    }
    Logger.getInstance().debug( this, "requested last known location!" );
  }
  
  /**
   * Determines whether one Location reading is better than the current Location
   * fix ( source:
   * http://developer.android.com/guide/topics/location/obtaining-user
   * -location.html )
   * 
   * @param location
   *          the new location
   * @param currentBestLocation
   *          the current location
   */
  private final static boolean isBetterLocation( Location location,
      Location currentBestLocation )
  {
    // if the new location is invalid return false;
    if ( location == null )
    {
      return false;
    }
    
    if ( currentBestLocation == null )
    {
      // A new location is always better than no location
      return true;
    }
    
    // Check whether the new location fix is newer or older
    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > SIGNIFICANT_TIME_DIFF;
    boolean isSignificantlyOlder = timeDelta < -SIGNIFICANT_TIME_DIFF;
    boolean isNewer = timeDelta > 0;
    
    // If it's been more than two minutes since the current location, use the
    // new location
    // because the user has likely moved
    if ( isSignificantlyNewer )
    {
      return true;
      // If the new location is more than two minutes older, it must be worse
    }
    else if ( isSignificantlyOlder )
    {
      return false;
    }
    
    // Check whether the new location fix is more or less accurate
    int accuracyDelta =
        (int) ( location.getAccuracy() - currentBestLocation.getAccuracy() );
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;
    
    // Check if the old and new location are from the same provider
    boolean isFromSameProvider = isSameProvider( location.getProvider(),
             currentBestLocation.getProvider() );
    
    // Determine location quality using a combination of timeliness and accuracy
    if ( isMoreAccurate )
    {
      return true;
    }
    else if ( isNewer && !isLessAccurate )
    {
      return true;
    }
    else if ( isNewer && !isSignificantlyLessAccurate && isFromSameProvider )
    {
      return true;
    }
    return false;
  }
  
  /**
   * Method to check for the same provider
   * 
   * @param provider1
   *          first provider
   * @param provider2
   *          second provider
   * @return true if it is the same provider
   */
  private final static boolean isSameProvider( String provider1, String provider2 )
  {
    if ( provider1 == null )
    {
      return provider2 == null;
    }
    return provider1.equals( provider2 );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de.
   * unikassel.android.sdcframework.util.facade.ObservableEventSource,
   * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public final void onEvent(
      ObservableEventSource< ? extends Sample > eventSource,
      Sample observedEvent )
  {
    Location newLocation = locationFromSample( observedEvent );
    if ( isBetterLocation( newLocation, getCurrentLocation() ) )
    {
      setCurrentLocation( newLocation );
    }
  }
  
  /**
   * Method to create a location object from a location sample
   * 
   * @param sample
   *          the sample
   * @return the converted location sample or null
   */
  private final Location locationFromSample( Sample sample )
  {
    Location location = null;
    SampleData data = sample.getData();
    
    // test for correct sample data type
    if ( data instanceof LocationSampleData )
    {
      LocationSampleData locData = (LocationSampleData) data;
      location = new Location( sample.getDeviceIdentifier() );
      location.setTime( sample.getTimeStamp() );
      location.setLatitude( locData.getLatitude() );
      location.setLongitude( locData.getLongitude() );
      if ( locData.getAltitude() != null )
      {
        location.setAltitude( locData.getAltitude() );
      }
      if ( locData.getAccuracy() != null )
      {
        location.setAccuracy( locData.getAccuracy() );
      }
      if ( locData.getSpeed() != null )
      {
        location.setSpeed( locData.getSpeed() );
      }
    }
    return location;
  }
  
  /**
   * Setter for the currentLocation
   * 
   * @param currentLocation
   *          the currentLocation to set
   */
  private final synchronized void setCurrentLocation( Location currentLocation )
  {
    this.currentLocation = currentLocation;
  }
  
  /**
   * Getter for the currentLocation
   * 
   * @return the currentLocation
   */
  public final synchronized Location getCurrentLocation()
  {
    if( isEnabled() )
      return currentLocation;
    return null;
  }

  /**
   * Setter for the enabled
  
   * @param enabled the enabled to set
   */
  public synchronized void setEnabled( boolean enabled )
  {
    if( enabled && this.enabled == false )
    {
      getLastKnownLocation();
    }
    this.enabled = enabled;
  }

  /**
   * Getter for the enabled
  
   * @return the enabled
   */
  public synchronized boolean isEnabled()
  {
    return enabled;
  }
}
