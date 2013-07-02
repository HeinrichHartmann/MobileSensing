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

import android.content.SharedPreferences;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.SinglePreference;

/**
 * Implementation of the {@linkplain SensorDevicePreferences sensor device preferences}.
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
public final class SensorDevicePreferencesImpl implements
    SensorDevicePreferences
{
  /**
   * The preference screen key for the device
   */
  public static final String KEY_APPENDIX = "_preferences";
  
  /**
   * The device identifier
   */
  private SensorDeviceIdentifier deviceID;
  
  /**
   * The frequency preference
   */
  private final SinglePreference< Integer > frequencyPreference;
  
  /**
   * The priority preference
   */
  private final SinglePreference< SensorDevicePriorities > priorityPreference;
  
  /**
   * The enabled preference
   */
  private final SinglePreference< Boolean > enabledPreference;
  
  /**
   * Constructor
   * 
   * @param deviceID
   *          the device identifier
   */
  public SensorDevicePreferencesImpl( SensorDeviceIdentifier deviceID )
  {
    super();
    setDeviceIdentifier( deviceID );
    this.frequencyPreference =
        new FrequencyPreference( getDeviceIdentifier().toString() );
    this.priorityPreference =
        new PriorityLevelPreference( getDeviceIdentifier().toString() );
    this.enabledPreference =
        new EnabledPreference( getDeviceIdentifier().toString() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences
   * #getFrequencyPreference()
   */
  @Override
  public final SinglePreference< Integer > getFrequencyPreference()
  {
    return frequencyPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences
   * #getPriorityPreference()
   */
  @Override
  public final SinglePreference< SensorDevicePriorities >
      getPriorityPreference()
  {
    return priorityPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences
   * #getEnabledPreference()
   */
  @Override
  public final SinglePreference< Boolean > getEnabledPreference()
  {
    return enabledPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * getConfiguration(android.content.SharedPreferences)
   */
  @Override
  public final SensorDeviceConfiguration getConfiguration(
      SharedPreferences sharedPreferences )
  {
    int frequency =
        getFrequencyPreference().getConfiguration( sharedPreferences );
    SensorDevicePriorities priority =
        getPriorityPreference().getConfiguration( sharedPreferences );
    boolean enabled =
        getEnabledPreference().getConfiguration( sharedPreferences );
    return new SensorDeviceConfigurationImpl( frequency, priority, enabled );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences
   * #getDeviceIdentifier()
   */
  @Override
  public final SensorDeviceIdentifier getDeviceIdentifier()
  {
    return deviceID;
  }
  
  /**
   * Setter for the device identifier
   * 
   * @param identifier
   *          the device identifier
   */
  private final void setDeviceIdentifier( SensorDeviceIdentifier identifier )
  {
    this.deviceID = identifier;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SinglePreference#getKey
   * ()
   */
  @Override
  public final String getKey()
  {
    return getDeviceIdentifier().toString() + KEY_APPENDIX;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * getDefault()
   */
  @Override
  public final SensorDeviceConfiguration getDefault()
  {
    return new SensorDeviceConfigurationImpl(
        getFrequencyPreference().getDefault(),
        getPriorityPreference().getDefault(),
        getEnabledPreference().getDefault() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * setDefault(java.lang.Object)
   */
  @Override
  public final void setDefault( SensorDeviceConfiguration defaultValue )
  {
    getFrequencyPreference().setDefault( defaultValue.getFrequency() );
    getPriorityPreference().setDefault( defaultValue.getSamplePriority() );
    getEnabledPreference().setDefault( defaultValue.isEnabled() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * testForKey(java.lang.String)
   */
  @Override
  public boolean testForKey( String key )
  {
    return getFrequencyPreference().testForKey( key )
        || getEnabledPreference().testForKey( key )
        || getPriorityPreference().testForKey( key );
  }
}
