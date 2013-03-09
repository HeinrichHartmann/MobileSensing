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

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceFactory;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceManager;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor;
import de.unikassel.android.sdcframework.devices.facade.VisitableDevice;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import de.unikassel.android.sdcframework.util.LifeCycleObjectImpl;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * Implementation of the sensor device manager implementation. It does create
 * and supervises all sensor devices for available supported sensor types and is
 * managing the sample observation and configuration updates for the device
 * module. <br/>
 * <br/>
 * The sensor device manager is the main interface to the device module. <br/>
 * To receive sensor samples an {@link EventObserver observer} for the type
 * {@linkplain Sample} must register here.
 * 
 * @see SensorDeviceFactory
 * @see ObservableEventSource
 * @author Katy Hilgenberg
 * 
 */
public final class SensorDeviceManagerImpl
    extends LifeCycleObjectImpl
    implements SensorDeviceManager
{
  
  /**
   * the sensor device factory
   */
  private final SensorDeviceFactory deviceFactory;
  
  /**
   * The preference manager
   */
  private final ApplicationPreferenceManager preferenceManager;
  
  /**
   * The sensor device map
   */
  private Map< SensorDeviceIdentifier, SensorDevice > mapDevices;
  
  /**
   * Constructor
   * 
   * @param preferenceManager
   *          the application preference manager
   * @throws InvalidParameterException
   *           in case of invalid parameters
   */
  public SensorDeviceManagerImpl( ApplicationPreferenceManager preferenceManager )
      throws InvalidParameterException
  {
    super();
    if ( preferenceManager == null )
      throw new InvalidParameterException( "preferenceManager is null" );
    this.preferenceManager = preferenceManager;
    this.deviceFactory = new SensorDeviceFactoryImpl();
  }
  
  /**
   * Getter for the device factory
   * 
   * @return the device factory
   */
  public SensorDeviceFactory getDeviceFactory()
  {
    return deviceFactory;
  }
  
  /**
   * Getter for the application preference manager
   * 
   * @return the application preference manager
   */
  public final ApplicationPreferenceManager getPreferenceManager()
  {
    return preferenceManager;
  }
  
  /**
   * Getter for the devices map
   * 
   * @return the devices map
   */
  private Map< SensorDeviceIdentifier, SensorDevice > getMapDevices()
  {
    if ( mapDevices == null )
    {
      setMapDevices( new HashMap< SensorDeviceIdentifier, SensorDevice >() );
    }
    return mapDevices;
  }
  
  /**
   * Setter for the devices map
   * 
   * @param mapDevices
   *          the devices map to set
   */
  private void setMapDevices(
      Map< SensorDeviceIdentifier, SensorDevice > mapDevices )
  {
    this.mapDevices = mapDevices;
  }
  
  /**
   * Getter for the devices
   * 
   * @return the device maintained by the manager
   */
  public Collection< SensorDevice > getDevices()
  {
    return Collections.unmodifiableCollection( getMapDevices().values() );
  }
  
  /**
   * Does create sensor device for all known available sensors
   * 
   * @param applicationContext
   *          the application context
   */
  private void createSensorDevices( Context applicationContext )
  {
    SensorDeviceFactory factory = getDeviceFactory();
    for ( SensorDeviceIdentifier identifier : SensorDeviceAvailabilityTester.getInstance().getAvailableSensorDevices() )
    {
      // create device and add it to the map
      SensorDevice sensorDevice =
          factory.createSensorDevice( identifier, applicationContext );
      if ( sensorDevice != null )
      {
        // call the device creation method
        sensorDevice.onCreate( applicationContext );
        // map to identifier
        getMapDevices().put( identifier, sensorDevice );
      }
      else
      {
        String msg =
            applicationContext.getText( R.string.err_device_creation ).toString();
        Logger.getInstance().error( this, msg + " " + identifier.toString() );
      }
    }
  }
  
  /**
   * Does disable all running device scanners
   * 
   * @param context
   *          the application context
   */
  private void disableDeviceScanning( Context context )
  {
    for ( SensorDevice device : getMapDevices().values() )
    {
      device.enableDeviceScanning( false, context );
    }
  }
  
  /**
   * Does configure all devices with preference settings. This will
   * automatically enable scanning of devices if configured.
   * 
   * @param applicationContext
   *          the application context
   */
  private void configureDevices( Context applicationContext )
  {
    for ( SensorDevice device : getMapDevices().values() )
    {
      // do get preferences for devices and update
      SensorDeviceIdentifier deviceIdentifier = device.getDeviceIdentifier();
      SensorDeviceConfiguration configuration =
          getConfiguration( deviceIdentifier, applicationContext );
      device.updateConfiguration( configuration, applicationContext );
    }
  }
  
  /**
   * Does determine the current device configuration
   * 
   * @param deviceIdentifier
   *          the device identifier
   * @param applicationContext
   *          the application context
   * @return a configuration for the sensor device created from it's preferences
   */
  private SensorDeviceConfiguration getConfiguration(
      SensorDeviceIdentifier deviceIdentifier, Context applicationContext )
  {
    return getPreferenceManager().getDeviceConfiguration(
        deviceIdentifier, applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.LifeCycleObjectImpl#onCreate(android
   * .content.Context)
   */
  @Override
  public void onCreate( Context applicationContext )
  {
    createSensorDevices( applicationContext );
    SampleFactory.getInstance().getLocationTracker().onCreate(
        applicationContext );
    
    super.onCreate( applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.LifeCycleObjectImpl#onResume(android
   * .content.Context)
   */
  @Override
  public void onResume( Context applicationContext )
  {
    configureDevices( applicationContext );
    SampleFactory.getInstance().getLocationTracker().onResume(
        applicationContext );
    
    super.onResume( applicationContext );
  }
  
  /**
   * Method to configure the location tracker
   * 
   * @param active
   *          flag is location tracker is active or not
   */
  private void configureLocationTracker( boolean active )
  {
    LocationTracker locationTracker =
        SampleFactory.getInstance().getLocationTracker();
    
    // first determine available location devices
    List< SensorDevice > locationDevices = new ArrayList< SensorDevice >();
    for ( SensorDevice device : getMapDevices().values() )
    {
      // do get preferences for devices and update
      SensorDeviceIdentifier deviceIdentifier = device.getDeviceIdentifier();
      switch ( deviceIdentifier )
      {
        case GPS:
        case NetworkLocation:
        {
          locationDevices.add( device );
          break;
        }
      }
    }
    
    for ( SensorDevice device : locationDevices )
    {
      if ( active )
        device.getScanner().registerEventObserver( locationTracker );
      else
        device.getScanner().unregisterEventObserver( locationTracker );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.LifeCycleObjectImpl#onPause(android
   * .content.Context)
   */
  @Override
  public void onPause( Context applicationContext )
  {
    // do disable device scanning
    disableDeviceScanning( applicationContext );
    SampleFactory.getInstance().getLocationTracker().onPause(
        applicationContext );
    
    super.onPause( applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.LifeCycleObjectImpl#onDestroy(android
   * .content.Context)
   */
  @Override
  public void onDestroy( Context applicationContext )
  {
    SampleFactory.getInstance().getLocationTracker().onDestroy(
        applicationContext );
    removeAllObservers();
    destroyDevices( applicationContext );
    
    super.onDestroy( applicationContext );
  }
  
  /**
   * Does destroy the devices and scanner
   * 
   * @param applicationContext
   *          the application context
   */
  private void destroyDevices( Context applicationContext )
  {
    for ( SensorDevice device : getMapDevices().values() )
    {
      device.onDestroy( applicationContext );
    }
    mapDevices.clear();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.VisitableDevice#accept
   * (de.unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor)
   */
  @Override
  public boolean accept( SensorDeviceVisitor visitor )
  {
    boolean doContinue = true;
    for ( VisitableDevice device : getMapDevices().values() )
    {
      if ( !doContinue )
        break;
      doContinue = device.accept( visitor );
    }
    return !doContinue;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObservableEventSource#
   * registerEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @Override
  public void registerEventObserver(
      EventObserver< ? extends Sample > observer )
  {
    for ( SensorDevice device : getMapDevices().values() )
    {
      device.getScanner().registerEventObserver( observer );
    }
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
    for ( SensorDevice device : getMapDevices().values() )
    {
      device.getScanner().unregisterEventObserver( observer );
    }
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
    for ( SensorDevice device : getMapDevices().values() )
    {
      device.getScanner().removeAllObservers();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDeviceManager#
   * enableLocationInfoPerSample(boolean)
   */
  @Override
  public void enableLocationInfoPerSample( boolean enable )
  {  
    SampleFactory.getInstance().enableLocationTracking( enable );  
    configureLocationTracker( enable );
    Logger.getInstance().debug( this, "Location tracking " + ( enable ? "enabled" : "disabled" ) );
  }
}
