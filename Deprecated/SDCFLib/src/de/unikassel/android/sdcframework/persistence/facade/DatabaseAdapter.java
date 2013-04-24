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

import java.util.Collection;

import android.database.sqlite.SQLiteException;

/**
 * @author Katy Hilgenberg
 *
 */
public interface DatabaseAdapter
{
  /**
   * Method to open the database for write access
   * 
   * @return this database adapter
   * @throws SQLiteException
   *           if unable to open the database
   */
  public abstract DatabaseAdapter open() throws SQLiteException;
  
  /**
   * Method to open the database for read access
   * 
   * @return this database adapter
   * @throws SQLiteException
   *           if unable to open the database
   */
  public abstract DatabaseAdapter openForRead() throws SQLiteException;
  
  /**
   * Method to close the database
   */
  public abstract void close();
  
  /**
   * Method to insert a sample collection into the database
   * 
   * @param samples
   *          the sample collection to insert
   * @throws Exception
   */
  public abstract void insertSamples( Collection< DatabaseSample > samples )
      throws Exception;
  
  /**
   * Method to get the current record count
   * 
   * @return the current count of records stored
   */
  public abstract long getRecordCount();
  
  /**
   * Method to delete a given count of the oldest samples.
   * 
   * @param count
   *          the count of records to delete
   * @param deleteLowestPriorityFirst
   *          if true the sample for deletion will be selected first by lowest
   *          priority, second oldest time stamp
   * @return the count of deleted records
   */
  public abstract long deleteSamplesOrdered( long count,
      boolean deleteLowestPriorityFirst );
  
  /**
   * Method to remove the next "count" samples which will be selected ordered by
   * ascending priority and ascending time stamp from the database and stored in
   * a given sample collection. <br/>
   * This method is used to select samples for transmission.<br/>
   * Here the oldest samples with the highest priority will be selected first.
   * 
   * @param count
   *          the sample count to remove
   * @param sampleCollection
   *          the sample collection to store removed samples in
   * @return true if successful, false otherwise
   */
  public abstract boolean removeSamplesHighestPrioFirst( long count,
      Collection< DatabaseSample > sampleCollection );
  
  /**
   * Method to remove the next "count" samples which will be selected ordered by
   * descending priority and ascending time stamps from the database and stored
   * in a given sample collection. <br/>
   * This method is used to select samples for deletion in case of exceeding
   * database size. <br/>
   * Here the oldest samples with the lowest priority will be selected first.
   * 
   * @param count
   *          the sample count to remove
   * @param sampleCollection
   *          the sample collection to store removed samples in
   * @return true if successful, false otherwise
   */
  public abstract boolean removeSamplesLowestPrioFirst( long count,
      Collection< DatabaseSample > sampleCollection );
  
  /**
   * Method to remove the next "count" samples which will be selected ordered by
   * ascending time stamps from the database and stored in a given sample
   * collection. <br/>
   * This method is used to select samples for deletion in case of exceeding
   * database size. <br/>
   * Here, independent of the sample priority, the oldest samples will be
   * selected first.
   * 
   * @param count
   *          the sample count to remove
   * @param sampleCollection
   *          the sample collection to store removed samples in
   * @return true if successful, false otherwise
   */
  public abstract boolean removeSamplesOldestTimeStampFirst( long count,
      Collection< DatabaseSample > sampleCollection );
  
  /**
   * Setter for the maximum database size
   * 
   * @param size
   *          the maximum database size
   * @return the new maximum database size
   */
  public abstract long setMaximumDatabaseSize( long size );
  
  /**
   * Getter for the maximum database size
   * 
   * @return the maximum database size
   */
  public abstract long getMaximumDatabaseSize();
  
}