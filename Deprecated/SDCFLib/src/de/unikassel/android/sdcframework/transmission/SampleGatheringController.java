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
package de.unikassel.android.sdcframework.transmission;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.os.SystemClock;

import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.SampleRateChangeResponder;
import de.unikassel.android.sdcframework.transmission.facade.UpdatableTransmissionComponent;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * A class to realize a controller for the task to select samples from the database for transfer. <br/>
 * <br/>
 * It does control the available sample count in database and calculates the
 * necessary wait time to reach the required minimum sample count. <br/>
 * The available count of samples for is limited by a configurable minimum and 
 * maximum. <br/>
 * The wait time calculation does depend on the current sample rate and the
 * required minimum of samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class SampleGatheringController
    implements UpdatableTransmissionComponent<TransmissionConfiguration>, SampleRateChangeResponder
{
  /**
   * The amplification factor
   */
  private static final float AF = 1.01F;
  
  /**
   * The upper limit for wait times in milliseconds
   */
  private static final long MAX_FREQUENCY = 36000000L;
  
  /**
   * The lower limit for wait times in milliseconds
   */
  private static final long MIN_FREQUENCY = 1000L;
  
  /**
   * The delay for sample rate calculation in case of a rate change
   */
  private static final long SAMPLE_RATE_DELAY = 60000L;
  
  /**
   * The internal time stamp of last wait time calculation
   */
  private final AtomicLong lastTimeStamp;
  
  /**
   * The minimum sample count to transfer
   */
  private final AtomicInteger minSampleCount;
  
  /**
   * The maximum sample count to transfer
   */
  private final AtomicInteger maxSampleCount;
  
  /**
   * The available sample count in database
   */
  private final AtomicLong availableSampleCount;
  
  /**
   * The last record count in database
   */
  private long lastRecordCount;
  
  /**
   * Flag indicating a sample rate change
   */
  private boolean sampleRateHasChanged;
  
  /**
   * Constructor
   */
  public SampleGatheringController()
  {
    this.lastTimeStamp = new AtomicLong();
    this.minSampleCount = new AtomicInteger();
    this.maxSampleCount = new AtomicInteger();
    this.availableSampleCount = new AtomicLong();
  }
  
  /**
   * Does reset all values
   * 
   * @param currentRecordCount
   *          the current record count in database
   */
  public synchronized final void reset( long currentRecordCount )
  {
    sampleRateHasChanged = false;
    updateFields( currentRecordCount, SystemClock.elapsedRealtime() );
  }
  
  /**
   * Getter for the minimum sample count to transfer
   * 
   * @return the minimum sample count to transfer
   */
  public final int getMinSampleCount()
  {
    return minSampleCount.get();
  }
  
  /**
   * Getter for the maximum sample count to transfer
   * 
   * @return the maximum sample count to transfer
   */
  public final int getMaxSampleCount()
  {
    return maxSampleCount.get();
  }
  
  /**
   * Getter for the count of available samples
   * 
   * @return the count of available samples
   */
  public final long getAvailableSampleCount()
  {
    return availableSampleCount.get();
  }
  
  /**
   * Method to consume available samples
   */
  public final void consumAvailableSamples()
  {
    availableSampleCount.set( 0L );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.transmission.facade.
   * UpdatableTransmissionComponent#updateConfiguration(android.content.Context,
   * de
   * .unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * )
   */
  @Override
  public final void updateConfiguration( Context context,
      TransmissionConfiguration config )
  {
    int minSampleCount = config.getMinSampleTransferCount();
    this.minSampleCount.set( minSampleCount );
    
    int maxSampleCount =
        Math.max( minSampleCount, config.getMaxSampleTransferCount() );
    this.maxSampleCount.set( maxSampleCount );
  }
  
  /**
   * Does calculate the necessary wait time depending on the current record
   * count in database
   * 
   * @param currentRecordCount
   *          the current record count in database
   * @return the current wait time
   */
  public synchronized final long calculatetWaitTime( long currentRecordCount )
  {
    // first determine current sample rate
    long timeStamp = SystemClock.elapsedRealtime();
    long timeInterval = timeStamp - lastTimeStamp.get();
    
    long sampleIncrement =
        currentRecordCount - ( lastRecordCount + availableSampleCount.get() );
    
    // now update internal fields
    updateFields( currentRecordCount, timeStamp );
    
    // calculate the required wait time for a minimum of available samples
    long waitTime = calculateNewWaitTime( sampleIncrement, timeInterval );
    
    // clear rate changed flag
    sampleRateHasChanged = false;
    
    float sampleRate = sampleIncrement;
    sampleRate /= timeInterval;
    
    Logger.getInstance().debug(
        this,
        "available sampels = " + availableSampleCount.get()
            + ", sample rate = " + ( sampleRate * 1000 )
            + "/s, current wait time = " + waitTime + " ms" );
    
    return waitTime;
  }
  
  /**
   * Method to calculate an updated time to wait for samples
   * 
   * @param sampleIncrement
   *          the current sample increment
   * @param timeInterval
   *          the current time interval the current error
   * @return the new calculated time in milliseconds
   */
  private final long calculateNewWaitTime( long sampleIncrement, long timeInterval )
  {
    long currentWaitTime = 0L;
    long missingSampleCount = getMissingSampleCount();
    
    if ( sampleRateHasChanged )
    {
      // after a sample rate change we need a new cycle for a better sample rate
      // estimation
      currentWaitTime = SAMPLE_RATE_DELAY;

      // clear missing sample count to avoid error correction in next turn
      missingSampleCount = 0L;
    }
    else
    {
      if ( missingSampleCount > 0L )
      {
        // avoid division by zero and allow a growing wait time in case of missing increment
        sampleIncrement = Math.max( sampleIncrement, 1 );
        
        // calculate new wait time and clip to range          
        long waitTime =
            (long) ( ( missingSampleCount * AF * timeInterval )
                / sampleIncrement );
        currentWaitTime =
            Math.max( Math.min( waitTime, MAX_FREQUENCY ), MIN_FREQUENCY );
      }
    }
    
    return currentWaitTime;
  }
  
  /**
   * Method to determine the current missing sample count
   * 
   * @return the current missing sample count
   */
  public final long getMissingSampleCount()
  {
    return Math.max( 0L, minSampleCount.get() - availableSampleCount.get() );
  }
  
  /**
   * Method to calculate the available sample count for collecting task
   * 
   * @param currentRecordCount
   *          the current record count in database
   * @param timeStamp
   *          the current time stamp
   */
  private final void updateFields( long currentRecordCount, long timeStamp )
  {
    // update available sample count
    long cnt =
        Math.max( 0L, Math.min( maxSampleCount.get(), currentRecordCount ) );
    availableSampleCount.set( cnt );
    
    // store the record count reduced by the available samples, which
    // can be consumed by the controlled transfer thread
    lastRecordCount = currentRecordCount - availableSampleCount.get();
    
    // update time stamp
    lastTimeStamp.set( timeStamp );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.SampleRateChangeResponder
   * #onSampleRateChanged()
   */
  @Override
  public synchronized final void onSampleRateChanged()
  {
    sampleRateHasChanged= true;
  }
}
