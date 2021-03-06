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
package de.unikassel.android.sdcframework.util.facade;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Helper class to refer to generic type class information.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class GenericTypeManager
{
  /**
   * Method to determine the generic type of a class if available
   * 
   * @param instance
   *          the instance of the class extending an generic type
   * @return the class of the generic type if available, null otherwise
   */
  public final static Class< ? > getGenericTypeClass(
      Class< ? > instance )
  {
    if ( instance != null )
    {
      Type genericSuperclass = instance.getGenericSuperclass();
      
      if ( genericSuperclass instanceof ParameterizedType )
      {
        return (Class< ? >) ( (ParameterizedType) genericSuperclass ).getActualTypeArguments()[ 0 ];
      }
      else
      {
        Type[] genericInterfaces = instance.getGenericInterfaces();
        for ( Type type : genericInterfaces )
        {
          if ( type instanceof ParameterizedType ) { return (Class< ? >) ( (ParameterizedType) type ).getActualTypeArguments()[ 0 ]; }
        }
      }
    }
    return null;
  }
}
