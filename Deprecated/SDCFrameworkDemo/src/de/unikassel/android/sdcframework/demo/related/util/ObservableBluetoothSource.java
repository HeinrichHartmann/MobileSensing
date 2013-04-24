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

import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.data.independent.BluetoothSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.demo.related.util.EnhancedBluetoothData.Collection;

/**
 * The Bluetooth sample source.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ObservableBluetoothSource
    extends ObservableSampleSource< EnhancedBluetoothData.Collection, EnhancedBluetoothData >
{
  
  /**
   * Constructor
   */
  public ObservableBluetoothSource()
  {
    // keep samples for 10 Minutes
    super( 600000L );
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
    return new EnhancedBluetoothData.CollectionImpl( ts );
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
    return new EnhancedBluetoothData.CollectionImpl( collection );
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
    currentSamples.add( new EnhancedBluetoothData( sample ) );
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
    return data instanceof BluetoothSampleData;
  }
}
