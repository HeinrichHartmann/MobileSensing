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

import java.util.Collection;

import android.database.sqlite.SQLiteFullException;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseAdapter;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseSample;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of the remove samples database command.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class RemoveSamplesCommand
    extends AbstractDatabaseCommand< Boolean >
{
  /**
   * The collection to store the removed samples in
   */
  private final Collection< DatabaseSample > samples;
  
  /**
   * The sample count to remove from database
   */
  private final long count;
  
  /**
   * Constructor
   * 
   * @param samples
   *          the sample collection to store removed samples in
   * @param count
   *          the count of samples to remove from data base
   */
  public RemoveSamplesCommand( Collection< DatabaseSample > samples, long count )
  {
    super( false );
    this.samples = samples;
    this.count = count;
  }
  
  /**
   * Getter for the samples
   * 
   * @return the samples
   */
  public final Collection< DatabaseSample > getSamples()
  {
    return samples;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.AbstractDatabaseCommand#
   * applyCommand(de.unikassel.android.sdcframework.persistence.DatabaseAdapter)
   */
  @Override
  protected Boolean applyCommand( DatabaseAdapter dbAdapter )
  {
    try
    {
      dbAdapter.removeSamplesHighestPrioFirst( count, samples );
      return true;
    }
    catch ( SQLiteFullException e )
    {
      // throw this special SQL exception to the caller
      throw e;
    }
    catch ( Exception e )
    {
      Logger.getInstance().error(
          this,
            " Unexpected exception during command execution: " + e + ": "
                + e.getMessage() );
      e.printStackTrace();
    }
    return false;
  }
  
}
