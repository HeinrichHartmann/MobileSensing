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
package de.unikassel.android.sdcframework.preferences;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import de.unikassel.android.sdcframework.data.WeeklySchedule;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.preferences.facade.Configuration;
import de.unikassel.android.sdcframework.preferences.facade.DevicePreferencesCollection;
import de.unikassel.android.sdcframework.preferences.facade.LogLevelConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.LogLevelConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.ConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.ServicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.SinglePreference;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfigurationChangeEvent;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderPreference;
import de.unikassel.android.sdcframework.preferences.facade.UUIDPreference;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.GenericTypeManager;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

/**
 * The application preference manager for the SDC framework is providing a
 * uniform access to preferences or current configuration values. <br/>
 * <br/>
 * Provided features are:
 * <ul>
 * <li>an uniform access to the android
 * {@linkplain android.content.SharedPreferences shared preferences },</li>
 * <li>observation of preferences changes and notification about
 * {@linkplain ConfigurationChangeEvent configuration changes} to registered
 * observers,</li>
 * <li>does provide all the available configuration values for the framework and
 * the service, like {@linkplain SensorDeviceConfiguration device
 * configurations} or the {@linkplain ServiceConfiguration service
 * configuration}</li>
 * </ul>
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class ApplicationPreferenceManagerImpl
    implements ApplicationPreferenceManager
{
  
  /**
   * Observable event source implementation for sensor device configuration
   * change events
   * 
   * @see SensorDeviceConfigurationChangeEvent
   * @see ObservableEventSourceImpl
   * @author Katy Hilgenberg
   * 
   */
  public final class DeviceConfigurationChangeEventSource
      extends ObservableEventSourceImpl< SensorDeviceConfigurationChangeEvent >
  {}
  
  /**
   * Observable event source implementation for log level change events
   * 
   * @see LogLevelConfigurationChangeEvent
   * @see ObservableEventSourceImpl
   * @author Katy Hilgenberg
   * 
   */
  public final class LogLevelConfigurationChangeEventSource
      extends ObservableEventSourceImpl< LogLevelConfigurationChangeEvent >
  {}
  
  /**
   * Observable event source implementation for time provider change events
   * 
   * @see TimeProviderConfigurationChangeEvent
   * @see ObservableEventSourceImpl
   * @author Katy Hilgenberg
   * 
   */
  public final class TimeProviderConfigurationChangeEventSource
      extends ObservableEventSourceImpl< TimeProviderConfigurationChangeEvent >
  {}
  
  /**
   * Observable event source implementation for service configuration change
   * events
   * 
   * @see ServiceConfigurationChangeEvent
   * @see ObservableEventSourceImpl
   * @author Katy Hilgenberg
   * 
   */
  public final class ServiceConfigurationChangeEventSource
      extends ObservableEventSourceImpl< ServiceConfigurationChangeEvent >
  {}
  
  /**
   * Map to hold the supported change event sources mapped to events class name
   */
  private final Map< Class< ? extends ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > >, ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > > mapChangeEventSource;
  
  /**
   * The device preferences
   */
  private final DevicePreferencesCollection devicePreferences;
  
  /**
   * The log level preferences
   */
  private final SinglePreference< LogLevelConfiguration > logLevelPreference;
  
  /**
   * The UUID preferences
   */
  private final UUIDPreference uuidPreference;
  
  /**
   * The time provider preferences
   */
  private final TimeProviderPreference timeProviderPreference;
  
  /**
   * The service preferences
   */
  private final ServicePreferences servicePreferences;
  
  /**
   * the shared preferences listener
   */
  private final OnSharedPreferenceChangeListener listener;
  
  /**
   * preference key to device preferences mapping
   */
  private final Map< String, SensorDevicePreferences > mapKeyToDevicePreferences;
  
  /**
   * Constructor
   */
  public ApplicationPreferenceManagerImpl()
  {
    super();
    
    this.mapChangeEventSource =
        new HashMap< Class< ? extends ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > >,
          ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > >();
    this.mapKeyToDevicePreferences =
        new HashMap< String, SensorDevicePreferences >();
    this.devicePreferences = new DevicePreferencesCollectionImpl();
    this.logLevelPreference = new LogLevelPreferenceImpl();
    this.uuidPreference = new UUIDPreferenceImpl();
    this.timeProviderPreference = new TimeProviderPreferenceImpl();
    this.servicePreferences = new ServicePreferencesImpl();
    
    this.listener = new OnSharedPreferenceChangeListener()
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * android.content.SharedPreferences.OnSharedPreferenceChangeListener#
       * onSharedPreferenceChanged(android.content.SharedPreferences,
       * java.lang.String)
       */
      @Override
      public void onSharedPreferenceChanged(
          SharedPreferences sharedPreferences,
          String key )
      {
        handlePreferenceUpdate( sharedPreferences, key );
      }
    };
  }
  
  /**
   * Getter for the device update source
   * 
   * @return the device update source
   */
  private final DeviceConfigurationChangeEventSource
      getDeviceUpdateSource()
  {
    Class< DeviceConfigurationChangeEventSource > keyClass =
        DeviceConfigurationChangeEventSource.class;
    ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > observableEventSource =
        mapChangeEventSource.get( keyClass );
    
    if ( observableEventSource == null )
    {
      observableEventSource = new DeviceConfigurationChangeEventSource();
      mapChangeEventSource.put( keyClass, observableEventSource );
    }
    return keyClass.cast( observableEventSource );
  }
  
  /**
   * Getter for the log update source
   * 
   * @return the log update source
   */
  private final ObservableEventSource< LogLevelConfigurationChangeEvent >
      getLogUpdateSource()
  {
    Class< LogLevelConfigurationChangeEventSource > keyClass =
        LogLevelConfigurationChangeEventSource.class;
    ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > observableEventSource =
        mapChangeEventSource.get( keyClass );
    
    if ( observableEventSource == null )
    {
      observableEventSource = new LogLevelConfigurationChangeEventSource();
      mapChangeEventSource.put( keyClass, observableEventSource );
    }
    return keyClass.cast( observableEventSource );
  }
  
  /**
   * Getter for the time provider update source
   * 
   * @return the log update source
   */
  private final ObservableEventSource< TimeProviderConfigurationChangeEvent >
      getTimeProviderUpdateSource()
  {
    Class< TimeProviderConfigurationChangeEventSource > keyClass =
        TimeProviderConfigurationChangeEventSource.class;
    ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > observableEventSource =
        mapChangeEventSource.get( keyClass );
    
    if ( observableEventSource == null )
    {
      observableEventSource = new TimeProviderConfigurationChangeEventSource();
      mapChangeEventSource.put( keyClass, observableEventSource );
    }
    return keyClass.cast( observableEventSource );
  }
  
  /**
   * Getter for the service update source
   * 
   * @return the service update source
   */
  private final ObservableEventSource< ServiceConfigurationChangeEvent >
      getServiceUpdateSource()
  {
    Class< ServiceConfigurationChangeEventSource > keyClass =
        ServiceConfigurationChangeEventSource.class;
    ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > observableEventSource =
        mapChangeEventSource.get( keyClass );
    
    if ( observableEventSource == null )
    {
      observableEventSource = new ServiceConfigurationChangeEventSource();
      mapChangeEventSource.put( keyClass, observableEventSource );
    }
    return keyClass.cast( observableEventSource );
  }
  
  /**
   * Method to get a collection of the internal observable event sources for
   * test purpose
   * 
   * @return the observable event sources as collection
   */
  public final
      Collection< ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > >
      getObservableEventSources()
  {
    return Collections.unmodifiableCollection( mapChangeEventSource.values() );
  }
  
  /**
   * Getter for the device preferences
   * 
   * @return the device preferences
   */
  private final DevicePreferencesCollection getDevicePreferences()
  {
    return devicePreferences;
  }
  
  /**
   * Getter for the key to device preferences map
   * 
   * @return the key to device preferences map
   */
  private final Map< String, SensorDevicePreferences >
      getMapKeyToDevicePreferences()
  {
    return mapKeyToDevicePreferences;
  }
  
  /**
   * Handler for preference changes
   * 
   * @param sharedPreferences
   *          the shared preferences
   * @param key
   *          the preference key identifier
   */
  private final void handlePreferenceUpdate(
      SharedPreferences sharedPreferences,
      String key )
  {
    // test for device preference
    SensorDevicePreferences devicePrefs =
        getMapKeyToDevicePreferences().get( key );
    
    if ( devicePrefs != null )
    {
      // create sensor device configuration update as event for our event
      // observers
      SensorDeviceConfiguration deviceConfiguration =
          devicePrefs.getConfiguration( sharedPreferences );
      SensorDeviceConfigurationChangeEvent update =
          new SensorDeviceConfigurationChangeEventImpl( deviceConfiguration,
              devicePrefs.getDeviceIdentifier() );
      getDeviceUpdateSource().notify( update );
    }
    else if ( logLevelPreference.testForKey( key ) )
    {
      // log level preference change
      LogLevel level =
          logLevelPreference.getConfiguration( sharedPreferences ).getLogLevel();
      getLogUpdateSource().notify(
          new LogLevelConfigurationChangeEventImpl(
              new LogLevelConfigurationImpl( level ) ) );
    }
    else if ( servicePreferences.testForKey( key ) )
    {
      // service preference change
      ServiceConfiguration serviceConfig =
          servicePreferences.getConfiguration( sharedPreferences );
      getServiceUpdateSource().notify(
          new ServiceConfigurationChangeEventImpl( serviceConfig ) );
    }
    else if ( timeProviderPreference.testForKey( key ) )
    {
      // time provider preference change
      TimeProviderConfiguration configuration =
          timeProviderPreference.getConfiguration( sharedPreferences );
      getTimeProviderUpdateSource().notify(
          new TimeProviderConfigurationChangeEventImpl( configuration ) );
    }
  }
  
  /**
   * Does register device preferences for the key to device preference mapping
   * 
   * @param preferences
   *          the device preferences to register
   */
  private final void registerDevicePreferences(
      SensorDevicePreferences preferences )
  {
    Map< String, SensorDevicePreferences > map =
        getMapKeyToDevicePreferences();
    map.put( preferences.getEnabledPreference().getKey(), preferences );
    map.put( preferences.getFrequencyPreference().getKey(), preferences );
    map.put( preferences.getPriorityPreference().getKey(), preferences );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager
   * #getPreferencesForDevice(de.unikassel.android.sdcframework
   * .devices.facade.SensorDeviceIdentifier)
   */
  @Override
  public final synchronized SensorDevicePreferences getPreferencesForDevice(
      SensorDeviceIdentifier deviceIdentifier )
  {
    SensorDevicePreferences preferences =
        getDevicePreferences().getPreferencesForDevice( deviceIdentifier );
    
    if ( preferences == null )
    {
      // create new preferences on first access
      preferences = new SensorDevicePreferencesImpl( deviceIdentifier );
      getDevicePreferences().addPreferences( preferences );
      registerDevicePreferences( preferences );
    }
    return preferences;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#getLogLevelPreference()
   */
  @Override
  public final synchronized SinglePreference< LogLevelConfiguration >
      getLogLevelPreference()
  {
    return logLevelPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#getTimeProviderPreference()
   */
  @Override
  public final TimeProviderPreference getTimeProviderPreference()
  {
    return timeProviderPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#getPreferencesForService()
   */
  @Override
  public final ServicePreferences getServicePreferences()
  {
    return servicePreferences;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#getUUIDPreference()
   */
  @Override
  public UUIDPreference getUUIDPreference()
  {
    return uuidPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager
   * #getDeviceConfiguration(de.unikassel.android.sdcframework
   * .devices.facade.SensorDeviceIdentifier, android.content.Context)
   */
  @Override
  public final synchronized SensorDeviceConfiguration getDeviceConfiguration(
      SensorDeviceIdentifier deviceIdentifier,
      Context applicationContext )
  {
    SharedPreferences sharedPreferences =
        getSharedPreferences( applicationContext );
    
    return getPreferencesForDevice( deviceIdentifier ).getConfiguration(
        sharedPreferences );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager
   * #getLogLevelConfiguration(android.content.Context)
   */
  @Override
  public final synchronized LogLevelConfiguration getLogLevelConfiguration(
      Context applicationContext )
  {
    return getLogLevelPreference().getConfiguration(
        getSharedPreferences( applicationContext ) );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager
   * #updateLogLevelConfiguration(android.content.Context,
   * de.unikassel.android.sdcframework.util.facade.LogLevel)
   */
  @Override
  public void updateLogLevelConfiguration( Context applicationContext,
      LogLevel logLevel )
  {
    Editor editor = getSharedPreferences( applicationContext ).edit();
    editor.putString( getLogLevelPreference().getKey(), logLevel.name() );
    editor.commit();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager
   * #getTimeProviderConfiguration(android.content.Context)
   */
  @Override
  public final TimeProviderConfiguration getTimeProviderConfiguration(
      Context applicationContext )
  {
    return getTimeProviderPreference().getConfiguration(
        getSharedPreferences( applicationContext ) );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager
   * #getServiceConfiguration(android.content.Context)
   */
  @Override
  public final ServiceConfiguration getServiceConfiguration(
      Context applicationContext )
  {
    return getServicePreferences().getConfiguration(
        getSharedPreferences( applicationContext ) );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#getUUIDConfiguration(android.content.Context)
   */
  @Override
  public String getUUIDConfiguration( Context applicationContext )
  {
    return getUUIDPreference().getConfiguration(
        getSharedPreferences( applicationContext ) );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager
   * #updateUUIDConfiguration(android.content.Context, java.lang.String)
   */
  @Override
  public void
      updateUUIDConfiguration( Context applicationContext, String sUuid )
  {
    Editor editor = getSharedPreferences( applicationContext ).edit();
    editor.putString( getUUIDPreference().getKey(), sUuid );
    editor.commit();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#startListening(android.content.Context)
   */
  @Override
  public final synchronized void startListening( Context applicationContext )
  {
    getSharedPreferences( applicationContext ).registerOnSharedPreferenceChangeListener(
        listener );
    Logger.getInstance().info( this, " change listener started" );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#stopListening(android.content.Context)
   */
  @Override
  public final synchronized void stopListening( Context applicationContext )
  {
    getSharedPreferences( applicationContext ).unregisterOnSharedPreferenceChangeListener(
        listener );
    Logger.getInstance().info( this, " change listener stopped" );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#getSharedPreferences(android.content.Context)
   */
  @Override
  public final SharedPreferences getSharedPreferences(
      Context applicationContext )
  {
    return PreferenceManager.getDefaultSharedPreferences( applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * unregisterEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @SuppressWarnings( "unchecked" )
  @Override
  public final synchronized
      void
      unregisterEventObserver(
          EventObserver< ? extends ConfigurationChangeEvent< ? extends Configuration > > observer )
  {
    Class< ? > genericType =
        GenericTypeManager.getGenericTypeClass( observer.getClass() );
    if ( genericType == null )
      return;
    
    if ( LogLevelConfigurationChangeEvent.class.isAssignableFrom( genericType ) )
    {
      getLogUpdateSource().unregisterEventObserver(
          (EventObserver< LogLevelConfigurationChangeEvent >) observer );
    }
    else if ( TimeProviderConfigurationChangeEvent.class.isAssignableFrom( genericType ) )
    {
      getTimeProviderUpdateSource().unregisterEventObserver(
          (EventObserver< TimeProviderConfigurationChangeEvent >) observer );
    }
    else if ( SensorDeviceConfigurationChangeEvent.class.isAssignableFrom( genericType ) )
    {
      getDeviceUpdateSource().unregisterEventObserver(
          (EventObserver< SensorDeviceConfigurationChangeEvent >) observer );
    }
    else if ( ServiceConfigurationChangeEvent.class.isAssignableFrom( genericType ) )
    {
      getServiceUpdateSource().unregisterEventObserver(
          (EventObserver< ServiceConfigurationChangeEvent >) observer );
    }
    else
    {
      Logger.getInstance().warning(
          this,
          "Unregistration requested from unknown event observer type "
              + genericType.getName() );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * registerEventObserver
   * (de.unikassel.android.sdcframework.util.facade.EventObserver)
   */
  @SuppressWarnings( "unchecked" )
  @Override
  public final synchronized
      void
      registerEventObserver(
          EventObserver< ? extends ConfigurationChangeEvent< ? extends Configuration > > observer )
  {
    Class< ? > genericType =
        GenericTypeManager.getGenericTypeClass( observer.getClass() );
    if ( genericType == null )
      return;
    
    if ( LogLevelConfigurationChangeEvent.class.isAssignableFrom( genericType ) )
    {
      getLogUpdateSource().registerEventObserver(
          (EventObserver< LogLevelConfigurationChangeEvent >) observer );
    }
    else if ( TimeProviderConfigurationChangeEvent.class.isAssignableFrom( genericType ) )
    {
      getTimeProviderUpdateSource().registerEventObserver(
          (EventObserver< TimeProviderConfigurationChangeEvent >) observer );
    }
    else if ( SensorDeviceConfigurationChangeEvent.class.isAssignableFrom( genericType ) )
    {
      getDeviceUpdateSource().registerEventObserver(
          (EventObserver< SensorDeviceConfigurationChangeEvent >) observer );
    }
    else if ( ServiceConfigurationChangeEvent.class.isAssignableFrom( genericType ) )
    {
      getServiceUpdateSource().registerEventObserver(
          (EventObserver< ServiceConfigurationChangeEvent >) observer );
    }
    else
    {
      Logger.getInstance().warning(
          this,
          "Registration requested from unknown event observer type "
              + genericType.getName() );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#onDestroy()
   */
  @Override
  public final void onDestroy()
  {
    removeAllObservers();
    getMapKeyToDevicePreferences().clear();
    getDevicePreferences().removeAll();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * removeAllObservers()
   */
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ObserverRegistration#
   * removeAllObservers()
   */
  @Override
  public void removeAllObservers()
  {
    for ( ObservableEventSource< ? extends ConfigurationChangeEvent< ? extends Configuration > > eventSource : getObservableEventSources() )
    {
      eventSource.removeAllObservers();
    }
    mapChangeEventSource.clear();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#resetToDefaults()
   */
  @Override
  public void resetToDefaults( Context applicationContext )
  {
    // preserve schedule, uuid and log level settings
    String sUuid = getUUIDConfiguration( applicationContext );
    
    WeeklySchedule oldSchedule =
        getServicePreferences().getWeeklySchedulePreference().getConfiguration(
            getSharedPreferences( applicationContext ) );
    
    LogLevel logLevel =
        getLogLevelConfiguration( applicationContext ).getLogLevel();
    
    Editor editor = getSharedPreferences( applicationContext ).edit();
    editor.clear();
    editor.commit();
    
    updateUUIDConfiguration( applicationContext, sUuid );
    
    updateLogLevelConfiguration( applicationContext, logLevel );
    
    // do override the weekly schedule with the old one if exists (higher
    // priority of personal local schedule)
    if ( oldSchedule.size() > 0 )
    {
      updateSchedule( applicationContext, oldSchedule );
    }
  }
  
  /**
   * Internal update method for the weekly schedule
   * 
   * @param applicationContext
   *          the application context
   * @param schedule
   *          the schedule
   */
  protected final void updateSchedule( Context applicationContext,
      WeeklySchedule schedule )
  {
    Editor editor = getSharedPreferences( applicationContext ).edit();
    editor.putString(
        getServicePreferences().getWeeklySchedulePreference().getKey(),
        schedule.toString() );
    editor.commit();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ApplicationPreferenceManager#updatePreferenceState(android.content.Context,
   * de.unikassel.android.sdcframework.preferences.facade.SinglePreference,
   * boolean)
   */
  @Override
  public final void updatePreferenceState( Context applicationContext,
      SinglePreference< Boolean > preference, boolean state )
  {
    Editor editor = getSharedPreferences( applicationContext ).edit();
    editor.putBoolean( preference.getKey(), state );
    editor.commit();
  }
}
