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

import java.util.TreeSet;

/**
 * Basic implementation of a generic sorted sample collection with a common time stamp
 * @author Katy Hilgenberg
 *
 */
/**
 * @author Katy Hilgenberg
 * 
 * @param <T>
 */
public class SortedSampleCollectionImpl< T extends Comparable< ? super T > >
    extends TreeSet< T >
    implements SortedSampleCollection< T >
{
  
  /**
   * the serial version id
   */
  private static final long serialVersionUID = -3603382021843231441L;
  
  /**
   * The collection timeStamp
   */
  private final long ts;
  
  /**
   * Constructor
   * 
   * @param ts
   *          the time stamp
   */
  public SortedSampleCollectionImpl( long ts )
  {
    this.ts = ts;
  }
  
  /**
   * Copy Constructor
   * 
   * @param collection
   *          the collection to copy construct from
   */
  public SortedSampleCollectionImpl( SortedSampleCollection< T > collection )
  {
    this.ts = collection.getTs();
    addAll( collection );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.util.SortedSampleCollection#getTs
   * ()
   */
  @Override
  public final long getTs()
  {
    return ts;
  }
}