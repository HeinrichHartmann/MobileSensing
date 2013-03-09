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

import android.content.Context;
import de.unikassel.android.sdcframework.broadcast.facade.SampleBroadcastService;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.util.AbstractAsynchrounousSampleObserver;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of the sample broadcast service interface. <br/>
 * <br/>
 * This service can observe a sample event source, like the device manager, and
 * is broadcasting observed samples to the system in the form of Android
 * intents.
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
   * Constructor
   * 
   * @param applicationContext
   *          the application context
   */
  public SampleBroadcastServiceImpl( Context applicationContext )
  {
    super();
    this.applicationContext = applicationContext;
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
      // take sample from queue and broadcast it
      Sample sample = collector.dequeue();
      applicationContext.sendBroadcast( sample.getIntent() );
      
    }
    catch ( InterruptedException e )
    {}
    catch ( Exception e )
    {
      Logger.getInstance().error( this, "Exception in doWork" );
      e.printStackTrace();
    }
  }
}
