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
package de.unikassel.android.sdcframework.persistence.facade;

import android.database.sqlite.SQLiteFullException;

/**
 * Interface for the central database management component.
 * 
 * @author Katy Hilgenberg
 */
public interface DatabaseManager
{
  /**
   * Does execute a database command
   * 
   * @param command
   *          the command to execute
   * @param <T>
   *          the result type of the command
   * 
   * @return true if successful, false if a database full exception occurred
   * @throws SQLiteFullException
   *           if command execution fails due to database is full
   */
  public abstract < T extends Object > T doExecuteCommand(
      DatabaseCommand< T > command ) throws SQLiteFullException;
  
  /**
   * Getter for the current sample record count stored in database
   * 
   * @return the current sample record count in the database
   */
  public abstract long getRecordCountInDatabase();
  
  /**
   * Setter for the maximum database size in kilobytes
   * 
   * @param size
   *          the maximum database size
   * @return the new maximum database size
   */
  public abstract long setMaximumDatabaseSize( long size );
  
  /**
   * Getter for the maximum database size in kilobytes
   * 
   * @return the new maximum database size
   */
  public abstract long getMaximumDatabaseSize();
  
  /**
   * Method to delete the oldest "count" samples in the database<br/>
   * This method is used to delete samples for deletion in case of database size
   * maximum reached. <br/>
   * Depending on the flag lowestPriorityFirst the oldest samples will be
   * selected priority independent or not.
   * 
   * @param count
   *          the sample count to delete in database
   * @param lowestPriorityFirst
   *          if true the samples will be selected ordered by priority and time
   *          stamp, otherwise just by time stamp
   * @return the count of samples deleted in database
   */
  public abstract long doDeleteOldestSamplesInDatabase( long count,
      boolean lowestPriorityFirst );
}