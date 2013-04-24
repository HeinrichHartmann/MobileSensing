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
package de.unikassel.android.sdcframework.util;

import android.content.Context;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.util.facade.AsynchrounousSampleObserver;
import de.unikassel.android.sdcframework.util.facade.EventCollector;
import de.unikassel.android.sdcframework.util.facade.EventObserver;

/**
 * Abstract base class for asynchronous working sample observer components,
 * which do depend on the service life cycle.<br/>
 * <br/>
 * Main features: <br/>
 * <ul>
 * <li>can be attached as observer for {@linkplain Sample samples},</li>
 * <li>does maintain an {@linkplain EventCollector event collector} to store
 * observed {@linkplain Sample samples} in a thread safe queue for further
 * processing,</li>
 * <li>does run asynchronously as daemon worker thread,</li>
 * <li>does implement the life cycle object behavior.</li>
 * </ul>
 * 
 * Can be extended to implement a specific kind of sample data processing in the
 * {@linkplain #doWork()} method of the thread.
 * 
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractAsynchrounousSampleObserver
    extends AbstractWorkerThread
    implements AsynchrounousSampleObserver
{
  
  /**
   * The internal sample collector to store the observed events
   */
  protected final EventCollector< Sample > collector;
  
  /**
   * Constructor
   */
  public AbstractAsynchrounousSampleObserver()
  {
    super();
    this.collector = new EventCollectorImpl< Sample >();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.service.facade.AsynchrounousSampleObserver
   * #getObserver()
   */
  @Override
  public EventObserver< Sample > getObserver()
  {
    return collector.getEventObserver();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.AbstractWorkerThread#doCleanUp()
   */
  @Override
  protected void doCleanUp()
  {
    collector.clearCollectedEvents();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onCreate(
   * android.content.Context)
   */
  @Override
  public void onCreate( Context applicationContext )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onResume(
   * android.content.Context)
   */
  @Override
  public void onResume( Context applicationContext )
  {
    startWork();
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
    stopWork();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onDestroy
   * (android.content.Context)
   */
  @Override
  public void onDestroy( Context applicationContext )
  {
    doTerminate();
  }
  
}
