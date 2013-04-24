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

import android.content.SharedPreferences;

/**
 * Generic interface for a single preference with a default value. <br/>
 * <br/>
 * A single preference has a value type, a preference key, a default value and
 * can extract it's configuration value from the shared preferences.
 * 
 * @author Katy Hilgenberg
 * 
 * @param <T>
 *          the preference value type
 */
public interface SinglePreference< T extends Object >
{
  /**
   * Getter for the preference key
   * 
   * @return the preference key
   */
  public abstract String getKey();
  
  /**
   * Getter for the current value in the shared preferences
   * 
   * @param sharedPreferences
   *          the shared application preferences
   * @return the current value
   */
  public abstract T getConfiguration( SharedPreferences sharedPreferences );
  
  /**
   * Getter for the default value
   * 
   * @return the default value
   */
  public abstract T getDefault();
  
  /**
   * Setter for the default value
   * 
   * @param defaultValue
   *          the default value to set
   */
  public abstract void setDefault( T defaultValue );
  
  /**
   * Method to test for a preferences key support
   * 
   * @param key
   *          the key to test for
   * @return true if this key is supported, false otherwise
   */
  public abstract boolean testForKey( String key );
  
}