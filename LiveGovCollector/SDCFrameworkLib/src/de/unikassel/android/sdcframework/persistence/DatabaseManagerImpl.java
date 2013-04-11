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

import android.content.Context;
import android.database.sqlite.SQLiteFullException;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseCommand;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseManager;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of a central database manager.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class DatabaseManagerImpl
    implements DatabaseManager
{
  /**
   * The internal database adapter
   */
  private final DatabaseAdapterImpl dbAdapter;
  
  /**
   * Constructor
   * 
   * @param applicationContext
   *          the application context
   * @param dbName
   *          the database name
   */
  public DatabaseManagerImpl( Context applicationContext, String dbName )
  {
    this.dbAdapter =
        new DatabaseAdapterImpl( dbName, 0L, applicationContext );
  }
  
  /**
   * Constructor
   * 
   * @param applicationContext
   *          the application context
   * @param maxDBSize
   *          the maximum database size
   * @param dbName
   *          the database name
   */
  public DatabaseManagerImpl( Context applicationContext,
      long maxDBSize, String dbName )
  {
    this.dbAdapter =
        new DatabaseAdapterImpl( dbName, maxDBSize, applicationContext );
  }
  
  /**
   * Getter for the dbAdapter
   * 
   * @return the dbAdapter
   */
  public final DatabaseAdapterImpl getDbAdapter()
  {
    return dbAdapter;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * getSampleCountInDatabase()
   */
  @Override
  public long getRecordCountInDatabase()
  {
    GetRecordCountCommand command =
        new GetRecordCountCommand();
    Long cntRecords = doExecuteCommand( command );
    return cntRecords == null ? 0L : cntRecords;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * setMaximumDatabaseSize(long)
   */
  @Override
  public long setMaximumDatabaseSize( long size )
  {
    // set database size in bytes ( size value is Kilobyte! )
    SetMaximumDatabaseSizeCommand command =
        new SetMaximumDatabaseSizeCommand( size << 10 );
    Long newSize = doExecuteCommand( command );
    // return database size value in kilobytes
    return newSize == null ? 0L : newSize >> 10;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * getMaximumDatabaseSize()
   */
  @Override
  public long getMaximumDatabaseSize()
  {
    GetMaximumDatabaseSizeCommand command =
        new GetMaximumDatabaseSizeCommand();
    Long newSize = doExecuteCommand( command );
    // return database size value in kilobytes
    return newSize == null ? 0L : newSize >> 10;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * doDeleteOldestSamplesInDatabase(long, boolean)
   */
  @Override
  public long doDeleteOldestSamplesInDatabase( long count,
      boolean lowestPriorityFirst )
  {
    DeleteSamplesCommand command =
        new DeleteSamplesCommand( count, lowestPriorityFirst );
    Long cntDeleted = doExecuteCommand( command );
    return cntDeleted == null ? 0L : cntDeleted;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * doExecuteCommand
   * (de.unikassel.android.sdcframework.persistence.facade.DatabaseCommand)
   */
  @Override
  public synchronized < T > T doExecuteCommand( DatabaseCommand< T > command )
      throws SQLiteFullException
  {
    T result = null;
    if ( command != null )
    {
      try
      {
        if ( command.execute( dbAdapter ) )
        {
          result = command.getResult();
        }
      }
      catch ( SQLiteFullException e )
      {
        // throw this special SQL exception to the caller
        throw e;
      }
      catch ( Exception e )
      {
        Logger.getInstance().warning( this,
            "DB command execution failed: " + e.getMessage() );
        e.printStackTrace();
      }
    }
    return result;
  }
}
