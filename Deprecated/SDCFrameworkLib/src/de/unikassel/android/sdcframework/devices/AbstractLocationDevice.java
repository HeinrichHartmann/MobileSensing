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

import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.LocationSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.TimeInformation;
import de.unikassel.android.sdcframework.util.TimeProvider;

/**
 * Implementation of an abstract location provider based sensor device as sample
 * providing device.
 * 
 * @see ScannerStateAwareSensorDevice
 * @see SampleProvidingSensorDevice
 * @author Katy Hilgenberg
 */
public abstract class AbstractLocationDevice
    extends ScannerStateAwareSensorDevice
    implements SampleProvidingSensorDevice
{
  /**
   * The lower frequency to avoid battery drain
   */
  private static final int LOWER_FREQUENCY = 30000;
  
  /**
   * Internal location listener implementation
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class ProviderLocationListener implements LocationListener
  {
    /**
     * The observed provider
     */
    private final String provider;
    
    /**
     * The last known state
     */
    private final AtomicInteger lastState;
    
    /**
     * Constructor
     * 
     * @param provider
     *          the observed provider
     */
    public ProviderLocationListener( String provider )
    {
      super();
      lastState = new AtomicInteger( LocationProvider.AVAILABLE );
      this.provider = provider;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onLocationChanged(android.location.
     * Location)
     */
    @Override
    public final void onLocationChanged( Location location )
    {
      try
      {
        doHandleLocationChanged( location );
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this, "Exception in onLocationChanged" );
      }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.location.LocationListener#onStatusChanged(java.lang.String,
     * int, android.os.Bundle)
     */
    @Override
    public final void onStatusChanged( String provider, int status,
        Bundle extras )
    {
      try
      {
        if ( this.provider.equals( provider ) &&
            status != lastState.getAndSet( status ) )
        {
          if ( status == LocationProvider.OUT_OF_SERVICE )
          {
            Logger.getInstance().warning(
                this, this.provider + " provider out of service" );
          }
        }
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this, "Exception in onStatusChanged" );
      }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public final void onProviderEnabled( String provider )
    {
      // nothing to do as this listener is only attached if GPS provider is
      // enabled
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public final void onProviderDisabled( String provider )
    {
      try
      {
        if ( this.provider.equals( provider ) )
        {
          if ( isDeviceScanningEnabled() )
          {
            doHandleDeviceDisabledBySystem( getContext() );
          }
        }
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this, "Exception in onProviderDisabled" );
      }
    }
  }
  
  /**
   * Internal location provider state listener implementation
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class ProviderStateListener implements LocationListener
  {
    /**
     * The observed provider
     */
    private final String provider;
    
    /**
     * Constructor
     * 
     * @param provider
     *          the provider to observe for state changes
     */
    public ProviderStateListener( String provider )
    {
      super();
      this.provider = provider;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onLocationChanged(android.location.
     * Location)
     */
    @Override
    public final void onLocationChanged( Location location )
    {
      // do nothing on location changes
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see android.location.LocationListener#onStatusChanged(java.lang.String,
     * int, android.os.Bundle)
     */
    @Override
    public final void onStatusChanged( String provider, int status,
        Bundle extras )
    {
      // nothing to do
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public final void onProviderEnabled( String provider )
    {
      try
      {
        if ( this.provider.equals( provider ) )
        {
          if ( isDeviceScanningEnabled() )
          {
            doHandleDeviceEnabledBySystem( getContext() );
          }
        }
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this, "Exception in onProviderEnabled" );
      }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public final void onProviderDisabled( String provider )
    {
      // nothing to do as this listener is only attached if either the GPS
      // provider is not enabled in the system or this device is already
      // disabled by configuration.
    }
  }
  
  /**
   * The service application context
   */
  private Context context;
  
  /**
   * The GPS location listener used for sampling
   */
  private ProviderLocationListener locationListener;
  
  /**
   * The GPS state listener used for adding/removal of location listener
   */
  private ProviderStateListener stateListener;
  
  /**
   * The current available sample
   */
  private final LocationSampleData currentSampleData;
  
  /**
   * Flag if location listener is registered
   */
  private boolean isLocationListenerRegistered;
  
  /**
   * Flag if state listener is registered
   */
  private boolean isStateListenerRegistered;
  
  /**
   * The location provider
   */
  private final String provider;
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   * @param id
   *          the sensor device identifier
   * @param provider
   *          the network provider
   */
  public AbstractLocationDevice( Context context, SensorDeviceIdentifier id,
      String provider )
  {
    super( id );
    this.provider = provider;
    setContext( context );
    this.currentSampleData = new LocationSampleData();
  }
  
  /**
   * Setter for the context
   * 
   * @param context
   *          the context to set
   */
  private final void setContext( Context context )
  {
    this.context = context;
  }
  
  /**
   * Getter for the context
   * 
   * @return the context
   */
  private final Context getContext()
  {
    return context;
  }
  
  /**
   * Getter for the locationManager
   * 
   * @return the locationManager
   */
  private final LocationManager getLocationManager()
  {
    return (LocationManager) getContext().getSystemService(
        Context.LOCATION_SERVICE );
  }
  
  /**
   * Getter for the locationListener
   * 
   * @return the locationListener
   */
  private final ProviderLocationListener getLocationListener()
  {
    if ( locationListener == null )
    {
      setLocationListener( new ProviderLocationListener( provider ) );
    }
    return locationListener;
  }
  
  /**
   * Setter for the locationListener
   * 
   * @param locationListener
   *          the locationListener to set
   */
  private final void setLocationListener(
      ProviderLocationListener locationListener )
  {
    this.locationListener = locationListener;
  }
  
  /**
   * Setter for the stateListener
   * 
   * @param stateListener
   *          the stateListener to set
   */
  private final void setStateListener( ProviderStateListener stateListener )
  {
    this.stateListener = stateListener;
  }
  
  /**
   * Getter for the stateListener
   * 
   * @return the stateListener
   */
  private final ProviderStateListener getStateListener()
  {
    if ( stateListener == null )
    {
      setStateListener( new ProviderStateListener( provider ) );
    }
    return stateListener;
  }
  
  /**
   * Getter for the isLocationListenerRegistered flag
   * 
   * @return the isLocationListenerRegistered flag
   */
  public final boolean isLocationListenerRegistered()
  {
    return isLocationListenerRegistered;
  }
  
  /**
   * Setter for the isLocationListenerRegistered flag
   * 
   * @param isLocationListenerRegistered
   *          the isLocationListenerRegistered flag to set
   */
  private final void setLocationListenerRegistered(
      boolean isLocationListenerRegistered )
  {
    this.isLocationListenerRegistered = isLocationListenerRegistered;
  }
  
  /**
   * Getter for the isStateListenerRegistered flag
   * 
   * @return the isStateListenerRegistered flag
   */
  public boolean isStateListenerRegistered()
  {
    return isStateListenerRegistered;
  }
  
  /**
   * Setter for the isStateListenerRegistered flag
   * 
   * @param isStateListenerRegistered
   *          the isStateListenerRegistered flag to set
   */
  public void setStateListenerRegistered( boolean isStateListenerRegistered )
  {
    this.isStateListenerRegistered = isStateListenerRegistered;
  }
  
  /**
   * Does register the location listener
   */
  private final void registerLocationListener()
  {
    if ( !isLocationListenerRegistered() )
    {
      // limit frequency by internal minimum to avoid battery drain
      int frequency =
          Math.max( getConfiguration().getFrequency(), getLowerFrequency() );
      
      LocationManager locationManager = getLocationManager();
      
      getLocationListener().onLocationChanged(
          locationManager.getLastKnownLocation( provider ) );
      locationManager.requestLocationUpdates( provider,
          frequency, getMinDistance(), getLocationListener() );
      
      setLocationListenerRegistered( true );
      Logger.getInstance().info( this, "location listener registered" );
    }
  }
  
  /**
   * The minimum frequency for location updates
   * 
   * @return the minimum frequency for location updates
   */
  protected int getLowerFrequency()
  {
    return LOWER_FREQUENCY;
  }
  
  /**
   * The minimum distance in meters for location updates
   * 
   * @return the minimum distance in meters
   */
  protected float getMinDistance()
  {
    return 0;
  }
  
  /**
   * Does unregister the location listener
   */
  private final void unregisterLocationListener()
  {
    if ( isLocationListenerRegistered() )
    {
      getLocationManager().removeUpdates( getLocationListener() );
      Logger.getInstance().info( this, "location listener unregistered" );
      setLocationListenerRegistered( false );
    }
  }
  
  /**
   * Does register the GPS state listener
   */
  private final void registerStateListener()
  {
    if ( !isStateListenerRegistered() )
    {
      // do set frequency and distance to high values as this listener is only
      // interested in state changes
      getLocationManager().requestLocationUpdates(
          provider,
          Long.MAX_VALUE, Float.MAX_VALUE, getStateListener() );
      Logger.getInstance().info( this, provider + " state listener registered" );
      setStateListenerRegistered( true );
    }
  }
  
  /**
   * Does unregister the GPS state listener
   */
  private final void unregisterStateListener()
  {
    if ( isStateListenerRegistered() )
    {
      getLocationManager().removeUpdates( getStateListener() );
      Logger.getInstance().info( this,
          provider + " state listener unregistered" );
      setStateListenerRegistered( false );
    }
  }
  
  /**
   * Handler for the location changed event
   * 
   * @param location
   *          the changed location
   */
  public final synchronized void doHandleLocationChanged( Location location )
  {
    if ( location != null )
    {
      currentSampleData.setLatitude( location.getLatitude() );
      currentSampleData.setLongitude( location.getLongitude() );
      
      currentSampleData.setAltitude( location.hasAltitude()
          ? location.getAltitude() : null );
      currentSampleData.setSpeed( location.hasSpeed() ? location.getSpeed()
          : null );
      currentSampleData.setAccuracy( location.hasAccuracy()
          ? location.getAccuracy() : null );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
   * isDeviceInSystemEnabled(android.content.Context)
   */
  @Override
  public final boolean isDeviceInSystemEnabled( Context context )
  {
    return getLocationManager().isProviderEnabled( provider );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * doSignalDeviceNotEnabledInSystem(android.content.Context)
   */
  @Override
  protected final void doSignalDeviceNotEnabledInSystem(
      Context applicationContext )
  {
    registerStateListener();
    
    String appName =
        applicationContext.getText( R.string.sdc_service_name ).toString();
    int resID = getDeviceDisabledMessageID();
    String message = applicationContext.getText( resID ).toString();
    // for the moment we do just ask the user to enable device
    Toast.makeText( applicationContext, appName + ": " + message,
        Toast.LENGTH_LONG ).show();
    Logger.getInstance().warning( this, message );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.ScannerStateAwareSensorDevice
   * #onScannerRunningStateChange(boolean, android.content.Context)
   */
  @Override
  protected final void onScannerRunningStateChange( boolean isRunning,
      Context context )
  {
    if ( isRunning )
    {
      unregisterStateListener();
      registerLocationListener();
    }
    else
    {
      unregisterLocationListener();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.ScannerStateAwareSensorDevice
   * #onConfigurationChanged()
   */
  @Override
  protected final void onConfigurationChanged()
  {
    super.onConfigurationChanged();
    
    if ( isLocationListenerRegistered() )
    {
      unregisterLocationListener();
      registerLocationListener();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.AbstractSensorDevice#onCreate
   * (android.content.Context)
   */
  @Override
  public final void onCreate( Context context )
  {
    super.onCreate( context );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.AbstractSensorDevice#onDestroy
   * (android.content.Context)
   */
  @Override
  public final void onDestroy( Context context )
  {
    unregisterStateListener();
    super.onDestroy( context );
    setLocationListener( null );
    setStateListener( null );
    setContext( null );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.AbstractSensorDevice#isAirplaneModeOn
   * (android.content.Context)
   */
  @Override
  public final boolean isAirplaneModeOn( Context applicationContext )
  {
    // GPS should work even in airplane mode
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice
   * #hasSample()
   */
  @Override
  public boolean hasSample()
  {
    return currentSampleData.getLatitude() != null
        && currentSampleData.getLongitude() != null;
  }
  
  /**
   * Getter for the currentSampleData
   * 
   * @return the currentSampleData
   */
  protected LocationSampleData getLocationData()
  {
    return currentSampleData;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice
   * #getSample()
   */
  @Override
  public final synchronized Sample getSample()
  {
    TimeInformation ti = TimeProvider.getInstance().getAccurateTimeInformation();
    Sample sample =
      SampleFactory.getInstance().createSample( ti, getDeviceIdentifier(),
            getConfiguration().getSamplePriority().ordinal(),
            getCurrentSampleData() );
    return sample;
  }
  
  /**
   * Method to get the current sample data
   * 
   * @return the current sample data
   */
  protected abstract SampleData getCurrentSampleData();
  
  /**
   * Method to get the device disabled message
   * 
   * @return the device disabled message
   */
  protected abstract int getDeviceDisabledMessageID();
}
