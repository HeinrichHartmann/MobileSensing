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

/**
 * Implementation of a simple frequency preference which is an integer type preference.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class FrequencyPreference
    extends IntegerPreference
{
  /**
   * The frequency key
   */
  public static final String KEY = "frequency";
  
  /**
   * The frequency default value
   */
  public static final Integer DEFAULT = 12000;
  
  /**
   * Constructor
   */
  public FrequencyPreference()
  {
    super( KEY, DEFAULT );
  }
  
  /**
   * Constructor
   * 
   * @param keySuffix
   *          the key suffix
   */
  public FrequencyPreference( String keySuffix )
  {
    super( keySuffix, KEY, DEFAULT );
  }
}
