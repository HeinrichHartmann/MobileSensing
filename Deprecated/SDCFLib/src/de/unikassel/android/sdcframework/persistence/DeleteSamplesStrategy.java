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

import java.security.InvalidParameterException;

import de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of a the database full strategy which does delete old samples. <br/>
 * <br/>
 * This strategy does delete the configured count of samples in the database.
 * Normally the oldest samples will be selected first, but if
 * configured, the oldest samples with lowest priority will be preferred for deletion.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class DeleteSamplesStrategy
    extends AbstractDatabaseFullStrategy
{
  
  /**
   * The count of samples to delete
   */
  protected final int countToDelete;
  
  /**
   * The count of samples to delete
   */
  protected final boolean lowestPriorityFirst;
  
  /**
   * Constructor
   * 
   * @param countToDelete
   *          the count of samples to delete
   * @param lowestPriorityFirst
   *          if true the oldest samples with lowest priority will be deleted
   *          first
   */
  public DeleteSamplesStrategy( int countToDelete, boolean lowestPriorityFirst )
  {
    super();
    this.countToDelete = countToDelete;
    this.lowestPriorityFirst = lowestPriorityFirst;
    if ( countToDelete < 0 )
      throw new InvalidParameterException(
          "countToDelete has to be more or equal to 0" );
  }
  
  /**
   * Getter for the record count to delete
   * 
   * @return the the record count to delete
   */
  public final int getCountToDelete()
  {
    return countToDelete;
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
    long countToDelete =
        Math.min( storageManager.getRecordCountInDatabase(), this.countToDelete );
    long cntDeletedSamples =
        storageManager.doDeleteOldestSamplesInDatabase( countToDelete,
            lowestPriorityFirst );
    
    if ( cntDeletedSamples > 0L )
    {
      Logger.getInstance().info( this,
          "" + cntDeletedSamples + " samples deleted!" );
      Logger.getInstance().debug(
          this,
          "Total record count in database: "
              + storageManager.getRecordCountInDatabase() );
    }
    return super.process( storageManager );
  }
  
  /**
   * Getter for the priority basted deletion flag
   * 
   * @return true if deletion is done for samples with lowest priority first.
   *         Otherwise it is done for the oldest samples
   */
  public Object isDeletingPriorityBased()
  {
    return lowestPriorityFirst;
  }
}