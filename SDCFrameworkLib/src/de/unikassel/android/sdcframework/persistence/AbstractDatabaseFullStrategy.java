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

import de.unikassel.android.sdcframework.persistence.facade.DatabaseFullStrategy;
import de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager;
import de.unikassel.android.sdcframework.util.AbstractChainWorker;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Abstract base class for a database full strategy type. It is calling its
 * successor to solve the problem if it fails. <br/>
 * <br/>
 * The execution method is implemented as the try to process samples again.
 * Extending strategies should override the
 * {@link #process(PersistentStorageManager) } method to implement an own
 * strategy and call the method of the super class in the end for another try to
 * store samples.
 * 
 * @see WaitStrategy
 * @see DeleteSamplesStrategy
 * @see NotificationStrategy
 * @see StopServiceStrategy
 * 
 * @author Katy Hilgenberg
 */
public abstract class AbstractDatabaseFullStrategy
    extends AbstractChainWorker< PersistentStorageManager >
    implements DatabaseFullStrategy
{
  
  /**
   * Constructor
   */
  public AbstractDatabaseFullStrategy()
  {
    super();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.AbstractChainWorker#process(java
   * .lang.Object)
   */
  @Override
  protected boolean process( PersistentStorageManager storageManager )
  {
    // first try if samples can be processed now
    try
    {
      if ( storageManager.doExecuteCurrentCommand() )
      {
        return true;
      }
      Logger.getInstance().warning( this, "failed to solve problem" );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return false;
  }
}
