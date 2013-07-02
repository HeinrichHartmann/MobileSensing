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
package de.unikassel.android.sdcframework.broadcast;

import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.os.SystemClock;
import de.unikassel.android.sdcframework.broadcast.facade.SampleBroadcastService;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.SampleCollection;
import de.unikassel.android.sdcframework.util.AbstractAsynchrounousSampleObserver;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of the sample broadcast service interface. <br/>
 * <br/>
 * This service can observe a sample event source, like the device manager, and
 * is broadcasting observed samples with the configured frequency to the system.
 * 
 * @see SampleBroadcastService
 * @see AbstractAsynchrounousSampleObserver
 * @author Katy Hilgenberg
 * 
 */
public class SampleBroadcastServiceImpl
    extends AbstractAsynchrounousSampleObserver
    implements SampleBroadcastService
{
  /**
   * The application context for broadcasts
   */
  private final Context applicationContext;
  
  /**
   * The broadcast frequency in milliseconds
   */
  private final AtomicLong frequency;
  
  /**
   * The frequency wait lock
   */
  private final Object frequencyWaitLock;
  
  /**
   * The timestamp of the last execution
   */
  private final AtomicLong lastExecutionTimeStamp;
  
  /**
   * Constructor
   * 
   * @param applicationContext
   *          the application context
   * @param frequency
   *          the frequency
   */
  public SampleBroadcastServiceImpl( Context applicationContext, long frequency )
  {
    super();
    this.applicationContext = applicationContext;
    this.frequencyWaitLock = new Object();
    this.frequency = new AtomicLong( frequency );
    this.lastExecutionTimeStamp = new AtomicLong();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.AbstractAsynchrounousSampleObserver
   * #onResume(android.content.Context)
   */
  @Override
  public void onResume( Context applicationContext )
  {
    lastExecutionTimeStamp.set( SystemClock.elapsedRealtime() );
    super.onResume( applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onPause(android
   * .content.Context)
   */
  @Override
  public void onPause( Context applicationContext )
  {
    super.onPause( applicationContext );
    broadcastCachedSamples();
    
    int eventCount = collector.getEventCount();
    if ( eventCount > 0 )
    {
      Logger.getInstance().warning( this,
          "" + eventCount + " samples not broadcasted yet!" );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.AbstractWorkerThread#doWork()
   */
  @Override
  protected void doWork()
  {
    try
    {
      if ( frequency.get() == 0L )
      {
        // without frequency broadcast single samples as in version 1.3.3
        // -> backward compatibility
        Sample sample = collector.dequeue();
        applicationContext.sendBroadcast( sample.getIntent() );
      }
      else
      {
        long waitTime =
            lastExecutionTimeStamp.get() - SystemClock.elapsedRealtime()
                + frequency.get();
        if ( waitTime > 0L )
        {
          synchronized ( frequencyWaitLock )
          {
            frequencyWaitLock.wait( waitTime );
          }
        }
        
        lastExecutionTimeStamp.set( SystemClock.elapsedRealtime() );
        
        // take samples from queue and broadcast it
        broadcastCachedSamples();
      }
    }
    catch ( InterruptedException e )
    {}
    catch ( Exception e )
    {
      Logger.getInstance().error( this, "Exception in doWork" );
      e.printStackTrace();
    }
  }
  
  /**
   * Method to broadcast all cached samples
   */
  private synchronized void broadcastCachedSamples()
  {
    SampleCollection sc = new SampleCollection();
    if ( collector.dequeue( sc, collector.getEventCount() ) > 0 )
    {
      applicationContext.sendBroadcast( sc.getIntent() );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.broadcast.facade.SampleBroadcastService
   * #updateFrequency(long)
   */
  @Override
  public void updateFrequency( long frequency )
  {
    frequency = Math.max( frequency, 0L );
    if ( this.frequency.get() != frequency )
    {
      this.frequency.set( frequency );
      doSignalFrequencyChange();
    }
  }
  
  /**
   * Method to signal frequency changes
   */
  protected void doSignalFrequencyChange()
  {
    // signal frequency change
    synchronized ( frequencyWaitLock )
    {
      frequencyWaitLock.notifyAll();
    }
  }
}
