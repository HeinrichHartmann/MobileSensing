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

import java.util.HashMap;
import java.util.Map;

import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.data.independent.WifiSampleData;
import de.unikassel.android.sdcframework.demo.related.util.EnhancedWifiData.Collection;

/**
 * The WIFI sample source.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ObservableWifiSource
    extends ObservableSampleSource< EnhancedWifiData.Collection, EnhancedWifiData >
{
  
  /**
   * the color set to use
   */
  private final int[] colors;
  
  /**
   * Mapping for networks to colors
   */
  private final Map< String, Integer > mapSSID2Color;
  
  /**
   * current color index
   */
  private int colorIndex;
  
  /**
   * Constructor
   */
  public ObservableWifiSource()
  {
    // keep samples for 1 Minute
    super( 60000L );
    this.colors = new ColorGenerator().getColors();
    this.colorIndex = 0;
    this.mapSSID2Color = new HashMap< String, Integer >();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.related.util.ObservableSampleSource#
   * newCollection(long)
   */
  @Override
  protected Collection newCollection( long ts )
  {
    return new EnhancedWifiData.CollectionImpl( ts );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.related.util.ObservableSampleSource#
   * newCollection
   * (de.unikassel.android.sdcframework.related.util.SortedSampleCollection)
   */
  @Override
  protected Collection newCollection( Collection collection )
  {
    return new EnhancedWifiData.CollectionImpl( collection );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.util.ObservableSampleSource#addSample
   * (de.unikassel.android.sdcframework.related.util.SortedSampleCollection,
   * de.unikassel.android.sdcframework.data.independent.BasicSample)
   */
  @Override
  protected void addSample( Collection currentSamples, BasicSample sample )
  {
    // add new sample to related collection
    WifiSampleData wifiData = (WifiSampleData) sample.getData();
    
    String ssid = wifiData.getSSID();
    Integer mappedColor = mapSSID2Color.get( ssid );
    if ( mappedColor == null )
    {
      mapSSID2Color.put( ssid, colors[ colorIndex ] );
      colorIndex = ( colorIndex + 3 ) % colors.length;
      mappedColor = mapSSID2Color.get( ssid );
    }
    
    currentSamples.add( new EnhancedWifiData( sample,
        WLANChannelExtractor.getChannel( wifiData.getFrequency() ),
        mappedColor ) );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.related.util.ObservableSampleSource#
   * testForSampleDataType
   * (de.unikassel.android.sdcframework.data.independent.SampleData)
   */
  @Override
  protected boolean testForSampleDataType( SampleData data )
  {
    return data instanceof WifiSampleData;
  }
}
