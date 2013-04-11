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
import de.unikassel.android.sdcframework.persistence.facade.DatabaseCommand;
import de.unikassel.android.sdcframework.util.Logger;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;

/**
 * Abstract base class for database commands. It does allow homogeneous command
 * execution using a database adapter, as well as a basic handling of open
 * errors and exceptions.<br/>
 * <br/>
 * Any extending concrete command class has to implement the abstract protected
 * {@link #applyCommand(DatabaseAdapter)} method, to implement the concrete
 * command behavior on an open database.
 * 
 * @author Katy Hilgenberg
 * @param <T>
 *          the result type of the command
 * 
 */
public abstract class AbstractDatabaseCommand< T >
    implements DatabaseCommand< T >
{
  /**
   * The default value for the count of repeated tries to open the database
   */
  public final static int DEFAULT_DB_OPEN_RETRY_COUNT = 5;
  
  /**
   * The count of retries if we fail to open the database
   */
  private final int dbOpenRetryCount;
  
  /**
   * The database open access flag
   */
  private final boolean openReadOnly;
  
  /**
   * The command execution result
   */
  private T result;
  
  /**
   * Constructor
   * 
   * @param openReadOnly
   *          flag if database can be open read only to execute the command
   * @param dbOpenRetryCount
   *          retry count for database open command
   */
  public AbstractDatabaseCommand( boolean openReadOnly, int dbOpenRetryCount )
  {
    super();
    this.openReadOnly = openReadOnly;
    this.dbOpenRetryCount = dbOpenRetryCount;
    result = null;
  }
  
  /**
   * Constructor
   * 
   * @param openReadOnly
   *          flag if database can be open read only to execute the command
   */
  public AbstractDatabaseCommand( boolean openReadOnly )
  {
    this( openReadOnly, DEFAULT_DB_OPEN_RETRY_COUNT );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.DatabaseCommand#getResult
   * ()
   */
  @Override
  public final T getResult()
  {
    return result;
  }
  
  /**
   * Getter for the dbOpenRetryCount
   * 
   * @return the dbOpenRetryCount
   */
  public final int getDbOpenRetryCount()
  {
    return dbOpenRetryCount;
  }
  
  /**
   * Getter for the openReadOnly flag
   * 
   * @return the openReadOnly
   */
  public final boolean isOpenReadOnly()
  {
    return openReadOnly;
  }
  
  /**
   * Internal method to execute the command and store the result
   * 
   * @param dbAdapter
   *          the database adapter to use for execution
   * @throws SQLiteFullException
   *           if command execution fails due to the fact that the database is
   *           full
   */
  protected final void internalExecute( DatabaseAdapter dbAdapter )
      throws SQLiteFullException
  {
    try
    {
      // first open the database ( block until successful )
      if ( openDatabase( dbAdapter ) )
      {
        // now try to execute the command itself
        result = applyCommand( dbAdapter );
      }
    }
    catch ( SQLiteFullException e )
    {
      // throw this special SQL exception to the caller
      throw e;
    }
    catch ( SQLException e )
    {
      onSQLException( e );
    }
    finally
    {
      dbAdapter.close();
    }
  }
  
  /**
   * Method to open the database
   * 
   * @param dbAdapter
   *          the database adapter result success true if successful
   */
  private boolean openDatabase( DatabaseAdapter dbAdapter )
  {
    boolean success = false;
    int cntTries = 0;
    
    while ( !success && cntTries <= dbOpenRetryCount )
    {
      try
      {
        if ( openReadOnly )
        {
          dbAdapter.openForRead();
        }
        else
        {
          dbAdapter.open();
        }
        success = true;
      }
      catch ( SQLiteFullException e )
      {
        // throw this special SQL exception to the caller
        throw e;
      }
      catch ( SQLiteException e )
      {
        cntTries++;
        if ( cntTries < dbOpenRetryCount )
        {
          // do wait a moment before retry opening
          try
          {
            Thread.sleep( 500 );
          }
          catch ( InterruptedException e1 )
          {}
        }
        else
        {
          // log last exception
          Logger.getInstance().error(
              this,
              "Failed to open database in "
                  + ( openReadOnly ? "read" : "write" )
                  + " mode: " + e.getMessage() );
        }
      }
    }
    
    return success;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.DatabaseCommand#execute
   * (de.unikassel.android.sdcframework.persistence.facade.DatabaseAdapter)
   */
  @Override
  public boolean execute( DatabaseAdapter dbAdapter )
      throws SQLiteFullException
  {
    internalExecute( dbAdapter );
    return result != null;
  }
  
  /**
   * Method to apply the basic database command operation.
   * 
   * @param dbAdapter
   *          the database adapter to use
   * @return the command result
   */
  protected abstract T applyCommand( DatabaseAdapter dbAdapter );
  
  /**
   * SQLException handler
   * 
   * @param e
   *          the exception
   */
  private void onSQLException( SQLException e )
  {
    // default we do just log the exception
    Logger.getInstance().error( this,
        "SQLException in execute: " + e.getMessage() );
    e.printStackTrace();
  }
}
