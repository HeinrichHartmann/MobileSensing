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

import java.security.InvalidParameterException;

import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.data.independent.BluetoothSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;

/**
 * Wrapper class to hold a Bluetooth sample .
 * 
 * @author Katy Hilgenberg
 * 
 */
public class EnhancedBluetoothData
    extends EnhancedSampleData
{
  /**
   * The collection type interface
   * 
   * @author Katy Hilgenberg
   * 
   */
  public interface Collection extends SortedSampleCollection< EnhancedBluetoothData >
  {}
  
  /**
   * The collection type implementation
   * 
   * @author Katy Hilgenberg
   * 
   */
  public static class CollectionImpl
      extends SortedSampleCollectionImpl< EnhancedBluetoothData >
      implements Collection
  {   
    /**
     * The serial version id
     */
    private static final long serialVersionUID = -9075374753370781475L;

    /**
     * Constructor
     * 
     * @param ts
     *          the time stamp
     */
    public CollectionImpl( long ts )
    {
      super( ts );
    }
    
    /**
     * Copy Constructor
     * 
     * @param collection
     *          the collection to copy construct from
     */
    public CollectionImpl( Collection collection )
    {
      super( collection );
    }
  }
  
  /**
   * A reference to the Bluetooth specific sample data
   */
  public final BluetoothSampleData data;
  
  /**
   * Constructor
   * 
   * @param sample
   *          the sample
   */
  public EnhancedBluetoothData( BasicSample sample )
  {
    super( sample );
    SampleData data = sample.getData();
    if ( data instanceof BluetoothSampleData )
    {
      this.data = (BluetoothSampleData) data;
    }
    else
    {
      this.data = null;
      throw new InvalidParameterException( "Wrong sample data type" );
    }
  }
  
  /**
   * Method for specific sample data comparison
   * 
   * @param data
   *          the sample data to compare with
   */
  protected int compareSampleData( SampleData data )
  {
    int result = 1;
    if ( data instanceof BluetoothSampleData )
    {
      BluetoothSampleData anotherData = (BluetoothSampleData) data;
      result = this.data.getBluetoothClass().compareTo(
          anotherData.getBluetoothClass() );
      if( result == 0 )
      {
        if( this.data.getName() != null && anotherData.getName() != null )
        {
          result = this.data.getName().compareTo(
            anotherData.getName() );
        }
        if( result == 0 )
        {
          return this.data.getAddress().compareTo(
              anotherData.getAddress() );
        }      
      }     
    }
    return result;
  }
}