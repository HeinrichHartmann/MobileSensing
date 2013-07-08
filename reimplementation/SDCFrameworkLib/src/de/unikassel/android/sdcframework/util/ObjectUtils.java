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
package de.unikassel.android.sdcframework.util;

/**
 * A static utility class providing functions for object comnparision.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class ObjectUtils
{
  
  /**
   * Test method for the equivalence of two objects which does allow for both
   * objects being null.
   * 
   * @param o1
   *          first object
   * @param o2
   *          second object
   * @return true if equal pointers or equal values
   */
  public static final boolean equals( Object o1, Object o2 )
  {
    if ( o1 != null && o2 != null )
    {
      // both objects are initialized return comparison value
      return o1.equals( o2 );
    }
    // at least one object is null -> return true if both are null
    return o1 == o2;
  }
  
}
