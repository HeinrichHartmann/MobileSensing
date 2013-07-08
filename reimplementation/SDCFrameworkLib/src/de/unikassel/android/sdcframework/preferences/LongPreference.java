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

/**
 * Preference implementation for long types.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class LongPreference extends SinglePreferenceImpl< Long >
{
  
  /**
   * Constructor
   * 
   * @param key
   *          the key value
   * @param defaultValue
   *          the default value
   */
  protected LongPreference( String key, Long defaultValue )
  {
    super( key, defaultValue );
  }
  
  /**
   * Constructor
   * 
   * @param keyPrefix
   *          the key prefix
   * @param keySuffix
   *          the key suffix
   * @param defaultValue
   *          the default value
   */
  public LongPreference( String keyPrefix, String keySuffix, Long defaultValue )
  {
    super( keyPrefix, keySuffix, defaultValue );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * getConfiguration(android.content.SharedPreferences)
   */
  @Override
  public Long getConfiguration( SharedPreferences sharedPreferences )
  {
    Long value =
        Long.parseLong( sharedPreferences.getString( getKey(),
            Long.valueOf( getDefault() ).toString() ) );
    return value;
  }
  
}
