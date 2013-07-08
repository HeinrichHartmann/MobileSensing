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
package de.unikassel.android.sdcframework.preferences.facade;

import android.content.Context;
import android.content.SharedPreferences;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.util.facade.LogLevel;

/**
 * Interface for the application preference manager.
 * 
 * @see ApplicationPreferenceManagerImpl
 * @author Katy Hilgenberg
 * 
 */
public interface ApplicationPreferenceManager
    extends ConfigurationChangeEventSource
{
  /**
   * Does return the shared preferences for the application context
   * 
   * @param applicationContext
   *          the application context
   * @return the applications shared preferences
   */
  public abstract SharedPreferences getSharedPreferences(
      Context applicationContext );
  
  /**
   * Does start the listening for application preferences changes
   * 
   * @param applicationContext
   *          the application context
   */
  public abstract void startListening( Context applicationContext );
  
  /**
   * Does stop the listening for application preferences changes
   * 
   * @param applicationContext
   *          the application context
   */
  public abstract void stopListening( Context applicationContext );
  
  /**
   * Getter for log level preferences
   * 
   * @return the log level preference
   */
  public abstract SinglePreference< LogLevelConfiguration >
      getLogLevelPreference();
  
  /**
   * Method to update the store log level preference
   * 
   * @param applicationContext
   *          the application context
   * @param logLevel
   *          the logLevel to update to
   */
  public abstract void updateLogLevelConfiguration( Context applicationContext,
      LogLevel logLevel );
  
  /**
   * Getter for UUID preferences
   * 
   * @return the UUID preference
   */
  public abstract UUIDPreference getUUIDPreference();
  
  /**
   * Getter for time provider preferences
   * 
   * @return the time provider preference
   */
  public abstract TimeProviderPreference getTimeProviderPreference();
  
  /**
   * Getter for specific device preferences
   * 
   * @param deviceIdentifier
   *          the device identifier
   * @return the preferences for the device with the given identifier
   */
  public abstract SensorDevicePreferences getPreferencesForDevice(
      SensorDeviceIdentifier deviceIdentifier );
  
  /**
   * Getter for the service preferences
   * 
   * @return the service preferences
   */
  public abstract ServicePreferences getServicePreferences();
  
  /**
   * Access to log level configuration created from current log level
   * preferences
   * 
   * @param applicationContext
   *          the application context
   * 
   * @return the current log level configuration
   */
  public abstract LogLevelConfiguration getLogLevelConfiguration(
      Context applicationContext );
  
  /**
   * Access to the time provider configuration created from current time
   * provider preferences
   * 
   * @param applicationContext
   *          the application context
   * 
   * @return the current time provider configuration
   */
  public abstract TimeProviderConfiguration getTimeProviderConfiguration(
      Context applicationContext );
  
  /**
   * Access to device configurations created from current device preferences
   * 
   * @param deviceIdentifier
   *          the device identifier
   * 
   * @param applicationContext
   *          the application context
   * @return the current device configuration
   */
  public abstract SensorDeviceConfiguration getDeviceConfiguration(
      SensorDeviceIdentifier deviceIdentifier,
      Context applicationContext );
  
  /**
   * Access to service configuration created from current service preferences
   * 
   * @param applicationContext
   *          the application context
   * 
   * @return the current service configuration
   */
  public abstract ServiceConfiguration getServiceConfiguration(
      Context applicationContext );
  
  /**
   * Access to the UUID configuration created from the current UUID preferences
   * 
   * @param applicationContext
   *          the application context
   * 
   * @return the current UUID configuration
   */
  public abstract String getUUIDConfiguration(
      Context applicationContext );
  
  /**
   * Method to update the stored device UUID.
   * 
   * @param applicationContext
   *          the application context
   * @param sUuid
   *          the new UUID to store in preferences
   */
  public abstract void updateUUIDConfiguration( Context applicationContext,
      String sUuid );
  
  /**
   * Internal destroy method to clean up references
   */
  public abstract void onDestroy();
  
  /**
   * Does reset the local stored changes for all preferences.
   * 
   * @param applicationContext
   *          the application context
   */
  public abstract void resetToDefaults( Context applicationContext );
  
  /**
   * Method to change the state for a given boolean preference
   * 
   * @param applicationContext
   *          the application context
   * @param preference
   *          the preference to change
   * @param state
   *          the state to set
   */
  public abstract void updatePreferenceState(
      Context applicationContext,
      SinglePreference< Boolean > preference, boolean state );
}
