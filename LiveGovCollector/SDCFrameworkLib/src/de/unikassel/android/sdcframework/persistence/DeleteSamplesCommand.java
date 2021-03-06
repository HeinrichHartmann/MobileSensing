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

import de.unikassel.android.sdcframework.persistence.facade.DatabaseAdapter;

/**
 * Implementation of the sample deletion database command.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class DeleteSamplesCommand
    extends AbstractDatabaseCommand< Long >
{
  /**
   * The record count to delete from database
   */
  private final long count;
  
  /**
   * Flag, indicating if samples with lower priority shall be deleted first
   */
  private final boolean lowestPriorityFirst;
  
  /**
   * Constructor
   * 
   * @param count
   *          the record count to delete from database
   * @param lowestPriorityFirst
   *          flag if samples with lower priority shall be deleted first
   */
  public DeleteSamplesCommand( long count, boolean lowestPriorityFirst )
  {
    super( false, DEFAULT_DB_OPEN_RETRY_COUNT << 1 );
    this.count = count;
    this.lowestPriorityFirst = lowestPriorityFirst;
  }
  
  /**
   * Getter for the count of samples to delete
   * 
   * @return the count of samples to delete
   */
  public final long getCount()
  {
    return count;
  }
  
  /**
   * Getter for the lowestPriorityFirst flag
   * 
   * @return the lowestPriorityFirst flag
   */
  public final boolean isLowestPriorityFirst()
  {
    return lowestPriorityFirst;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.AbstractDatabaseCommand#
   * applyCommand(de.unikassel.android.sdcframework.persistence.DatabaseAdapter)
   */
  @Override
  protected final Long applyCommand( DatabaseAdapter dbAdapter )
  {
    return dbAdapter.deleteSamplesOrdered( count, lowestPriorityFirst );
  }
  
}
