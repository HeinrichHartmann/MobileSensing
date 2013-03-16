/*
 * Copyright (C) 2012, Katy Hilgenberg.
 * Special acknowledgments to: Knowledge & Data Engineering Group, University of Kassel (http://www.kde.cs.uni-kassel.de).
 * Contact: sdcf@cs.uni-kassel.de
 *
 * This file is part of the SDCFramework project.
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
package de.unikassel.android.sdcframework.demo.related.util;

import android.util.Log;
import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.data.independent.SampleData;

/**
 * @author Katy Hilgenberg
 * 
 */
public class EnhancedSampleData
    implements Comparable< EnhancedSampleData >
{
  
  /**
   * Reference to the full sample data
   */
  public final BasicSample sample;
  
  /**
   * Constructor
   * 
   * @param sample
   *          the sample
   */
  public EnhancedSampleData( BasicSample sample )
  {
    super();
    this.sample = sample;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public final int compareTo( EnhancedSampleData another )
  {
    if ( !this.equals( another ) )
    {
      if ( another == null ) return 1;
      int result =
          this.sample.getDeviceIdentifier().compareTo(
              another.sample.getDeviceIdentifier() );
      if ( result == 0 )
      {
        try
        {
          return compareSampleData( another.sample.getData() );
        }
        catch ( Exception e )
        {
          Log.e(
              EnhancedSampleData.class.getName(),
              "compared " + this.sample.toString() + " with "
                  + another.sample.toString() );
        }
      }
      return result;
    }
    return 0;
  }
  
  /**
   * Method for specific sample data comparison
   * 
   * @param data
   *          the sample data to compare with
   * @return the comparison result
   */
  protected int compareSampleData( SampleData data )
  {
    // default we don't care about sample data
    return 0;
  }
  
}
