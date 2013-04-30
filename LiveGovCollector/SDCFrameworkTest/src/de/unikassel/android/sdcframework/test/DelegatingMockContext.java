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
package de.unikassel.android.sdcframework.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.RenamingDelegatingContext;

/**
 * A delegation mock context changing resource paths
 * 
 * @author Katy Hilgenberg
 * 
 */
public class DelegatingMockContext extends RenamingDelegatingContext
{
  /**
   * The used file prefix
   */
  public final static String PREFIX = "test.";
  
  /**
   * Constructor 
   * @param context the original context
   */
  public DelegatingMockContext( Context context )
  {
    super( context, PREFIX );
  }
  
  /* (non-Javadoc)
   * @see android.content.ContextWrapper#getSharedPreferences(java.lang.String, int)
   */
  @Override
  public SharedPreferences getSharedPreferences( String name, int mode )
  {
    return getBaseContext().getSharedPreferences( PREFIX + name, mode );
  }

}