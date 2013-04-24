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

import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.preferences.facade.DevicePreferencesCollection;
import de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences;

/**
 * Implementation of a collection of device preferences.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class DevicePreferencesCollectionImpl implements
    DevicePreferencesCollection
{
  /**
   * the contained sub devices of the sensor composition
   */
  private Map< SensorDeviceIdentifier, SensorDevicePreferences > mapPreferences;
  
  /**
   * Constructor
   */
  public DevicePreferencesCollectionImpl()
  {
    super();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * DevicePreferencesCollection# addPreferences
   * (de.unikassel.android.sdcframework
   * .preferences.facade.SingleDevicePreferences )
   */
  @Override
  public final boolean addPreferences( SensorDevicePreferences preferences )
  {
    if ( preferences != null )
    {
      getPreferencesMap().put( preferences.getDeviceIdentifier(), preferences );
      return true;
    }
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * DevicePreferencesCollection# getPreferences()
   */
  @Override
  public Collection< SensorDevicePreferences > getPreferences()
  {
    return Collections.unmodifiableCollection( getPreferencesMap().values() );
  }
  
  /**
   * Setter for the preferences map
   * 
   * @param mapPreferences
   *          the preferences map to set
   */
  private final void setPreferencesMap(
      Map< SensorDeviceIdentifier, SensorDevicePreferences > mapPreferences )
  {
    this.mapPreferences = mapPreferences;
  }
  
  /**
   * Getter for the the preferences map
   * 
   * @return the the preferences map
   */
  private final Map< SensorDeviceIdentifier, SensorDevicePreferences >
      getPreferencesMap()
  {
    if ( mapPreferences == null )
    {
      setPreferencesMap( new HashMap< SensorDeviceIdentifier, SensorDevicePreferences >() );
    }
    return mapPreferences;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * DevicePreferencesCollection# getPreferencesForDevice(java.lang.String)
   */
  @Override
  public final SensorDevicePreferences getPreferencesForDevice(
      SensorDeviceIdentifier deviceIdentifier )
  {
    return getPreferencesMap().get( deviceIdentifier );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * DevicePreferencesCollection#removeAll()
   */
  @Override
  public final void removeAll()
  {
    getPreferencesMap().clear();
  }
  
}
