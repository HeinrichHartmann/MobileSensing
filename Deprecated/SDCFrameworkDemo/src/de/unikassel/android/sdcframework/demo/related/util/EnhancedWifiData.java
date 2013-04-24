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
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.data.independent.WifiSampleData;

/**
 * Wrapper class to hold the WIFI sample together with necessary additional
 * information.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class EnhancedWifiData
    extends EnhancedSampleData
{
  /**
   * The collection type interface
   * 
   * @author Katy Hilgenberg
   * 
   */
  public interface Collection extends SortedSampleCollection< EnhancedWifiData >
  {}
  
  /**
   * The collection type implementation
   * 
   * @author Katy Hilgenberg
   * 
   */
  public static class CollectionImpl
      extends SortedSampleCollectionImpl< EnhancedWifiData >
      implements Collection
  {
    
    /**
     * The serial version id
     */
    private static final long serialVersionUID = 1487746845106975597L;
    
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
   * A reference to the WIFI specific sample data
   */
  public final WifiSampleData data;
  
  /**
   * the assigned color
   */
  public final int color;
  
  /**
   * the WLAN channel
   */
  public final int channel;
  
  /**
   * Constructor
   * 
   * @param sample
   *          the sample
   * @param channel
   *          the WLAN channel
   * @param color
   *          the display color
   */
  public EnhancedWifiData( BasicSample sample,
      int channel,
      int color )
  {
    super( sample );
    SampleData data = sample.getData();
    if ( data instanceof WifiSampleData )
    {
      this.data = (WifiSampleData) data;
    }
    else
    {
      this.data = null;
      throw new InvalidParameterException( "Wrong sample data type" );
    }
    this.channel = channel;
    this.color = color;
  }
  
  /**
   * Method for specific sample data comparison
   * 
   * @param data
   *          the sample data to compare with
   */
  protected int compareSampleData( SampleData data )
  {
    if ( data instanceof WifiSampleData )
    {
      WifiSampleData anotherData = (WifiSampleData) data;
      return this.data.getBSSID().compareTo(
          anotherData.getBSSID() );
      
    }
    return 1;
  }
}