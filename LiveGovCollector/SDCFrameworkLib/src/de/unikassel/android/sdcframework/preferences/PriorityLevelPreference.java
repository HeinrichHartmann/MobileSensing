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
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;

/**
 * Implementation of the preference for the {@link SensorDevicePriorities device
 * priority}.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class PriorityLevelPreference
    extends SinglePreferenceImpl< SensorDevicePriorities >
{
  /**
   * The frequency key
   */
  public static final String KEY = "priority";
  /**
   * The deafult value
   */
  public static final SensorDevicePriorities DEFAULT =
      SensorDevicePriorities.Level2;
  
  /**
   * Constructor
   */
  public PriorityLevelPreference()
  {
    super( KEY, DEFAULT );
  }
  
  /**
   * Constructor
   * 
   * @param keySuffix
   *          the key suffix
   */
  public PriorityLevelPreference( String keySuffix )
  {
    super( keySuffix, KEY, DEFAULT );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * SinglePreferenceWithDefault#
   * getConfiguration(android.content.SharedPreferences)
   */
  @Override
  public final SensorDevicePriorities getConfiguration(
      SharedPreferences sharedPreferences )
  {
    Integer priority =
        Integer.parseInt( sharedPreferences.getString( getKey(),
            Integer.valueOf( getDefault().ordinal() ).toString() ) );
    SensorDevicePriorities result = toPriorityLevel( priority );
    return ( result == null ? DEFAULT : result );
  }
  
  /**
   * Does convert an ordinal level value to it's corresponding sensor device
   * priority level. For invalid ordinal values null will be returned.
   * 
   * @param ordinalLevel
   *          the ordinal level value
   * @return the corresponding priority level or null if no level did match
   */
  public static SensorDevicePriorities toPriorityLevel( Integer ordinalLevel )
  {
    SensorDevicePriorities result = null;
    
    // try to find enumeration match for the priority
    for ( SensorDevicePriorities prio : SensorDevicePriorities.values() )
    {
      if ( ordinalLevel.equals( prio.ordinal() ) )
      {
        result = prio;
        break;
      }
    }
    return result;
  }
  
}
