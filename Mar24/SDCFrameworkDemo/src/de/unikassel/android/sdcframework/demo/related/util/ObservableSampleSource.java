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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;

/**
 * Base class for observable sample sources.
 * 
 * @author Katy Hilgenberg
 * @param <T>
 *          the observable event type
 * @param <U>
 *          the comparable type
 */
public abstract class ObservableSampleSource< T extends SortedSampleCollection< U >, U extends Comparable< ? super U > >
    extends ObservableEventSourceImpl< T >
{
  /**
   * The maximum time difference to store samples
   */
  public final long maxTimeDiffToKeepSamples;
  
  /**
   * The queue to store samples grouped by its time stamp
   */
  private final List< T > listSampleCollections;
  
  /**
   * The current frequency in milliseconds
   */
  private final AtomicLong frequency = new AtomicLong();
  
  /**
   * Time stamp of the last update
   */
  private final AtomicLong lastUpdateTime = new AtomicLong();
  
  /**
   * The count of most recent sample time stamps for merged results
   */
  protected static final int MAX_MERGE_COUNT = 3;
  
  /**
   * Constructor
   * 
   * @param maxTimeDiffToKeepSamples
   *          the maximum time difference in milliseconds to keep samples
   */
  public ObservableSampleSource( long maxTimeDiffToKeepSamples )
  {
    super();
    this.maxTimeDiffToKeepSamples = maxTimeDiffToKeepSamples;
    this.listSampleCollections =
        Collections.synchronizedList( new LinkedList< T >() );
  }
  
  /**
   * Getter for the current frequency in milliseconds
   * 
   * @return the current frequency in milliseconds
   */
  public long getFrequency()
  {
    return frequency.get();
  }
  
  /**
   * Setter for the current frequency in milliseconds
   * 
   * @param frequency
   *          the current frequency in milliseconds
   */
  protected void setFrequency( long frequency )
  {
    this.frequency.set( frequency );
  }
  
  /**
   * Getter for the last update time
   * 
   * @return the last update time
   */
  public long getLastUpdateTime()
  {
    return lastUpdateTime.get();
  }
  
  /**
   * Setter for the last update time
   * 
   * @param lastUpdateTime
   *          the last update time to set
   */
  public void setLastUpdateTime( long lastUpdateTime )
  {
    this.lastUpdateTime.set( lastUpdateTime );
  }
  
  /**
   * Getter for the maximum time difference in milliseconds to keep samples
  
   * @return the maximum time difference to keep samples
   */
  public long getMaxTimeDiffToKeepSamples()
  {
    return maxTimeDiffToKeepSamples;
  }

  /**
   * Method to prune the list with collected sample sets
   * 
   * @param actualTS
   *          the actual time stamp
   */
  protected final synchronized void pruneList( long actualTS )
  {
    int size = listSampleCollections.size() - 1;
    int pos = -1;
    
    // prune the sample history
    while ( pos < size )
    {
      T currentSet = listSampleCollections.get( pos + 1 );
      if ( ( actualTS - currentSet.getTs() ) <= maxTimeDiffToKeepSamples )
        break;
      pos++;
    }
    
    if ( pos >= 0 )
    {
      listSampleCollections.subList( 0, pos ).clear();
    }
  }
  
  /**
   * Preparation for a new sample
   * 
   * @param sample
   *          the sample to prepare
   * @return the collection to add the sample too
   */
  protected T prepareSample( BasicSample sample )
  {
    long ts = sample.getTimeStamp();
    
    // prepare
    int size = listSampleCollections.size();
    T currentSamples = size > 0 ? listSampleCollections.get( size - 1 )
        : null;
    if ( currentSamples == null || ts > currentSamples.getTs() )
    {
      // create new sample collection for the new time stamp
      if ( currentSamples != null )
        setFrequency( ts - currentSamples.getTs() );
      currentSamples = newCollection( ts );
      listSampleCollections.add( currentSamples );
      pruneList( ts );
      notify( currentSamples );
    }
    
    return currentSamples;
  }
  
  /**
   * Method to create a new collection
   * 
   * @param ts
   *          the time stamp for the new collection
   * @return a new collection for the type T
   */
  protected abstract T newCollection( long ts );
  
  /**
   * Method to create a new collection
   * 
   * @param collection
   *          the collection to clone from
   * @return a new collection for the type T
   */
  protected abstract T newCollection( T collection );
  
  /**
   * Method to add samples
   * 
   * @param sample
   *          the new sample
   */
  public final void addSample( BasicSample sample )
  {
    if ( !testForSampleDataType( sample.getData() ) ) return;
    
    T currentSamples = prepareSample( sample );
    addSample( currentSamples, sample );
    
    setLastUpdateTime( System.currentTimeMillis() );
  }
  
  /**
   * Method to add a new sample to the collection
   * 
   * @param currentSamples
   *          the current sample list
   * @param sample
   *          the sample data to wrap and add to the list
   */
  protected abstract void addSample( T currentSamples, BasicSample sample );
  
  /**
   * Method to test for sample data type
   * 
   * @param data
   *          the sample data
   * @return true if type is correct
   */
  protected abstract boolean testForSampleDataType( SampleData data );
  
  /**
   * Method to get the most recent sample
   * 
   * @param refreshInterval
   *          the time difference in milliseconds for which the samples have to
   *          be regarded as recent
   * @return the most recent samples
   */
  
  public final synchronized T getMostRecentSamples( long refreshInterval )
  {
    if ( ( System.currentTimeMillis() - getLastUpdateTime() ) < refreshInterval )
    {
      // merge collected sample set backwards to keep most recent samples
      int pos = listSampleCollections.size() - 1;
      int lastPos = Math.max( 0, pos - MAX_MERGE_COUNT );
      if ( pos >= lastPos )
      {
        T currentSet = listSampleCollections.get( pos );
        
        // clone the most recent set
        T result =
            newCollection( currentSet );
        pos--;
        
        // merge with older sets up to specified position
        while ( pos >= 0 )
        {
          currentSet = listSampleCollections.get( pos );
          result.addAll( currentSet );
          pos--;
        }
        
        // update sample rate
        return result;
      }
    }
    return newCollection( 0L );
  }
  
  /**
   * Method to get the stored sample time line
   * 
   * @return the most recent sample
   */
  public final List< T > getSampleTimeLine()
  {
    return listSampleCollections;
  }
}
