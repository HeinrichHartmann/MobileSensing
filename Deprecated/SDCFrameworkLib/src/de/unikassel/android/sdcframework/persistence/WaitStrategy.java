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
package de.unikassel.android.sdcframework.persistence;

import de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager;

/**
 * Implementation of a the database full strategy which does just wait a bit. <br/>
 * <br/>
 * The idea behind this strategy is, that the transmission service may be just a
 * bit busy but soon samples will be removed for transmission.<br/>
 * <br/>
 * This strategy should just be a first choice, e.g. for the case of an absent
 * transmission possibility, because the queued samples in the storage service
 * may exceed memory limits soon.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class WaitStrategy
    extends AbstractDatabaseFullStrategy
{
  /**
   * The wait time
   */
  private final long sleepTime;
  
  /**
   * Constructor
   * 
   * @param waitTime
   *          the time to wait in milliseconds
   */
  public WaitStrategy( long waitTime )
  {
    super();
    this.sleepTime = waitTime;
  }
  
  /**
   * Getter for the wait time
   * 
   * @return the wait time
   */
  public final long getSleepTime()
  {
    return sleepTime;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.AbstractDatabaseFullStrategy
   * #execute
   * (de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager
   * )
   */
  @Override
  public final boolean process( PersistentStorageManager storageManager )
  {
    try
    {
      Thread.sleep( sleepTime );
    }
    catch ( InterruptedException e )
    {}
    return super.process( storageManager );
  }
  
}
