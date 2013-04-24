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
import java.util.HashSet;
import java.util.Set;

import de.unikassel.android.sdcframework.persistence.facade.DatabaseAdapter;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseSample;
import de.unikassel.android.sdcframework.util.Logger;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * The database adapter class does wrap the direct Android database access.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class DatabaseAdapterImpl implements DatabaseAdapter
{
  /**
   * There's a limit in maximum number of placeholders in compiled SQL statement
   */
  private static final long MAX_NUM_PLACEHOLDERS = 200;
  
  /**
   * The row identifier column name
   */
  private static final String KEY_ROWID = "ID";
  
  /**
   * The sensor identifier column name
   */
  private static final String KEY_SENSORID = "SENSORID";
  
  /**
   * The sensor identifier column name
   */
  private static final String KEY_TIMESTAMP = "TS";
  
  /**
   * The priority column name
   */
  private static final String KEY_PRIO = "PRIO";
  
  /**
   * The time stamp sync state column
   */
  private static final String KEY_SYNCED = "SYNCED";
  
  /**
   * The data column name
   */
  private static final String KEY_DATA = "DATA";
  
  /**
   * The data type class name
   */
  private static final String KEY_DATA_CLASS = "DATACLASS";
  
  /**
   * The location column name
   */
  private static final String KEY_LOCATION = "LOC";
  
  /**
   * The order by time stamp statement ( oldest time stamps first )
   */
  private static final String ORDER_BY_TS = KEY_TIMESTAMP + " ASC";
  
  /**
   * The order by time ascending priority and stamp statement ( ascending time
   * stamp order, highest priority first )
   */
  private static final String ORDER_BY_ASC_PRIO_TS = KEY_PRIO + " ASC " + ", "
      + ORDER_BY_TS;
  
  /**
   * The order by time descending priority and stamp statement ( ascending time
   * stamp order, lowest priority first )
   */
  private static final String ORDER_BY_DESC_PRIO_TS = KEY_PRIO + " DESC "
      + ", " + ORDER_BY_TS;
  
  /**
   * The table name
   */
  public static final String DB_TABLE = "samples";
  
  /**
   * The first index name
   */
  public static final String DB_INDEX1 = "samplesidx1";
  /**
   * The second index name
   */
  public static final String DB_INDEX2 = "samplesidx2";
  
  /**
   * The database version
   */
  private static final int DB_VERSION = 4;
  
  /**
   * The table creation statement
   */
  private static final String DB_CREATE_TABLE =
      "create table if not exists " + DB_TABLE + " ( "
          + KEY_ROWID + " integer primary key autoincrement, "
          + KEY_SENSORID + " text not null, "
          + KEY_TIMESTAMP + " integer not null, "
          + KEY_PRIO + " integer not null, "
          + KEY_SYNCED + " integer default null, "
          + KEY_LOCATION + " text, " // location can be null!
          + KEY_DATA + " text not null, "
          + KEY_DATA_CLASS + " text not null );";
  
  /**
   * The first index creation statements
   */
  private static final String DB_CREATE_INDEX1 =
      "create index if not exists " + DB_INDEX1 + " on " + DB_TABLE + " ( "
          + ORDER_BY_ASC_PRIO_TS + "  );";
  /**
   * The first index creation statements
   */
  private static final String DB_CREATE_INDEX2 =
      "create index if not exists " + DB_INDEX2 + " on " + DB_TABLE + " ( "
          + ORDER_BY_DESC_PRIO_TS + " );";
  
  /**
   * The first table update statement statement
   */
  private static final String DB_UPDATE_1 =
      "alter table " + DB_TABLE + " add "
          + KEY_LOCATION + " text;";
  
  /**
   * The first table update statement statement
   */
  private static final String DB_UPDATE_2 =
      "alter table " + DB_TABLE + " add "
          + KEY_SYNCED + " integer default null;";
  
  /**
   * The internal SQLite helper class
   * 
   * @author Katy Hilgenberg
   * 
   */
  private final static class DatabaseHelper extends SQLiteOpenHelper
  {
    
    /**
     * The maximum database size
     */
    private long maxDBSize;
    
    /**
     * Constructor
     * 
     * @param dbName
     *          the database name
     * @param maxDBSize
     *          the maximum database size
     * @param context
     *          the context
     */
    DatabaseHelper( String dbName, long maxDBSize, Context context )
    {
      super( context, dbName, null, DB_VERSION );
      this.maxDBSize = maxDBSize;
    }
    
    /**
     * Setter for the maxDBSize
     * 
     * @param maxDBSize
     *          the maxDBSize to set
     */
    public final void setMaxDBSize( long maxDBSize )
    {
      this.maxDBSize = maxDBSize;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public final void onCreate( SQLiteDatabase db )
    {
      db.execSQL( DB_CREATE_TABLE );
      db.execSQL( DB_CREATE_INDEX1 );
      db.execSQL( DB_CREATE_INDEX2 );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public final void onOpen( SQLiteDatabase db )
    {
      if ( !db.isReadOnly() )
      {
        if ( maxDBSize > 0L )
          this.setMaxDBSize( db.setMaximumSize( maxDBSize ) );
        else
          maxDBSize = db.getMaximumSize();
      }
      super.onOpen( db );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    @Override
    public final void onUpgrade( SQLiteDatabase db, int oldVersion,
                            int newVersion )
    {
      if ( newVersion > oldVersion )
      {
        switch ( oldVersion )
        {
          case 1:
          {
            db.execSQL( DB_UPDATE_1 );
          }
          case 2:
          {
            db.execSQL( DB_CREATE_INDEX1 );
            db.execSQL( DB_CREATE_INDEX2 );
          }
          case 3:
          {
            db.execSQL( DB_UPDATE_2 );
            break;
          }   
        }
      }
    }
  }
  
  /**
   * The SQLite helper
   */
  private final DatabaseHelper dbHelper;
  
  /**
   * The database
   */
  private SQLiteDatabase db;
  
  /**
   * Constructor
   * 
   * @param dbName
   *          the database name
   * @param applicationContext
   *          the application context
   */
  public DatabaseAdapterImpl( String dbName,
      Context applicationContext )
  {
    this( dbName, 0L, applicationContext );
  }
  
  /**
   * Constructor
   * 
   * @param dbName
   *          the database name
   * @param maxDBSize
   *          the maximum databse size
   * @param applicationContext
   *          the application context
   */
  public DatabaseAdapterImpl( String dbName, long maxDBSize,
      Context applicationContext )
  {
    if ( applicationContext == null )
    {
      throw new IllegalArgumentException( "applicationContext is Null" );
    }
    if ( dbName == null || dbName.length() < 1 )
    {
      throw new IllegalArgumentException( "dbName is Null or empty" );
    }
    dbHelper = new DatabaseHelper( dbName, maxDBSize, applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.DatabaseAdapter#open()
   */
  @Override
  public final DatabaseAdapter open() throws SQLiteException
  {
    this.db = dbHelper.getWritableDatabase();
    return this;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseAdapter#
   * openForRead()
   */
  @Override
  public final DatabaseAdapter openForRead() throws SQLiteException
  {
    this.db = dbHelper.getReadableDatabase();
    return this;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.DatabaseAdapter#close
   * ()
   */
  @Override
  public final void close()
  {
    dbHelper.close();
  }
  
  /**
   * Method to insert a sample into the database
   * 
   * @param sample
   *          the sample to insert
   * @return the row id of the sample
   */
  private final long insertSample( DatabaseSample sample ) throws SQLException
  {
    ContentValues initialValues = new ContentValues();
    initialValues.put( KEY_SENSORID, sample.deviceIdentifier );
    initialValues.put( KEY_TIMESTAMP, sample.timeStamp );
    initialValues.put( KEY_PRIO, sample.priority );
    initialValues.put( KEY_SYNCED, sample.synced ? 1 : 0 );
    initialValues.put( KEY_DATA_CLASS, sample.dataTypeClassName );
    initialValues.put( KEY_DATA, sample.data );
    initialValues.put( KEY_LOCATION, sample.location );
    return db.insertOrThrow( DB_TABLE, null, initialValues );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.DatabaseAdapter#insertSamples
   * (java.util.Collection)
   */
  @Override
  public final void insertSamples( Collection< DatabaseSample > samples )
      throws Exception
  {
    db.beginTransaction();
    try
    {
      for ( DatabaseSample sample : samples )
      {
        insertSample( sample );
      }
      db.setTransactionSuccessful();
    }
    finally
    {
      db.endTransaction();
    }
  }
  
  /**
   * Method to delete a set of samples from the database
   * 
   * @param rowIds
   *          a set with unique row identifiers from database to delete
   * @return the affected record count
   */
  private final long deleteSamples( Set< Long > rowIds )
  {
    if ( rowIds.size() < 1 )
      return 0;
    
    String[] sWhereArgs = new String[ rowIds.size() ];
    StringBuffer sWhere = new StringBuffer( KEY_ROWID );
    sWhere.append( " IN ( " );
    int i = 0;
    for ( Long rowId : rowIds )
    {
      if ( i > 0 )
      {
        sWhere.append( ", " );
      }
      sWhereArgs[ i ] = rowId.toString();
      sWhere.append( '?' );
      ++i;
    }
    sWhere.append( " )" );
    
    return db.delete( DB_TABLE, sWhere.toString(), sWhereArgs );
  }
  
  /**
   * Method to delete all stored records at once
   * 
   * @return true if successful, false otherwise
   */
  public final boolean deleteAll()
  {
    db.beginTransaction();
    try
    {
      db.delete( DB_TABLE, null, null );
      db.setTransactionSuccessful();
    }
    finally
    {
      db.endTransaction();
    }
    return getRecordCount() == 0;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.DatabaseAdapter#getRecordCount
   * ()
   */
  @Override
  public final long getRecordCount()
  {
    return DatabaseUtils.queryNumEntries( db, DB_TABLE );
  }
  
  /**
   * Getter for samples ordered by the given statement
   * 
   * @param limit
   *          the count of rows to retrieve
   * @param orderByStatement
   *          the order by statement
   * @return the cursor for all samples sorted by priority descending
   */
  private final Cursor getSamplesOrdered( long limit, String orderByStatement )
  {
    return db.query(
        DB_TABLE,
        new String[] {
            KEY_ROWID,
            KEY_SENSORID,
            KEY_PRIO,
            KEY_SYNCED,
            KEY_TIMESTAMP,
            KEY_LOCATION,
            KEY_DATA,
            KEY_DATA_CLASS
        },
        null,
        null,
        null,
        null,
        orderByStatement,
        Long.toString( limit ) );
  }
  
  /**
   * Does create a sample from cursor position
   * 
   * @param cursor
   *          the database cursor
   * @return the sample created from cursor position
   */
  private final DatabaseSample sampleFromCursor( Cursor cursor )
  {
    DatabaseSample sample = new DatabaseSample();
    sample.deviceIdentifier =
        cursor.getString( cursor.getColumnIndexOrThrow( KEY_SENSORID ) );
    sample.timeStamp =
        cursor.getLong( cursor.getColumnIndexOrThrow( KEY_TIMESTAMP ) );
    sample.priority = cursor.getInt( cursor.getColumnIndexOrThrow( KEY_PRIO ) );
    sample.synced = cursor.getInt( cursor.getColumnIndex( KEY_SYNCED ) ) == 1;
    sample.data =
        cursor.getString( cursor.getColumnIndexOrThrow( KEY_DATA ) );
    sample.dataTypeClassName =
        cursor.getString( cursor.getColumnIndexOrThrow( KEY_DATA_CLASS ) );
    sample.location =
        cursor.getString( cursor.getColumnIndexOrThrow( KEY_LOCATION ) );
    return sample;
  }
  
  /**
   * Method to remove the next "count" samples selected ordered by priority and
   * time stamp from the database and stored in a given sample collection.
   * 
   * @param count
   *          the sample count to remove
   * @param sampleCollection
   *          the sample collection to store removed samples in
   * @param orderByStatement
   *          the order by statement to use for the query
   * @return true if successful, false otherwise
   */
  private final boolean removeSamples( long count,
      Collection< DatabaseSample > sampleCollection, String orderByStatement )
  {
    boolean success = true;
    db.beginTransaction();
    
    try
    {
      Set< Long > rowIds = new HashSet< Long >();
      
      // fetch request in a loop to not exceed compile limits for SQL statements
      while ( success && count > 0 )
      {
        long currentCount = Math.min( MAX_NUM_PLACEHOLDERS, count );
        
        Cursor cursor = getSamplesOrdered( currentCount, orderByStatement );
        if ( cursor.moveToFirst() )
        {
          do
          {
            rowIds.add( cursor.getLong( cursor.getColumnIndexOrThrow( KEY_ROWID ) ) );
            sampleCollection.add( sampleFromCursor( cursor ) );
          }
          while ( cursor.moveToNext() );
        }
        cursor.close();
        
        if ( deleteSamples( rowIds ) <= 0 )
        {
          Logger.getInstance().error( this,
              "Failed to delete queried samples in database" );
          success = false;
        }
        rowIds.clear();
        count -= MAX_NUM_PLACEHOLDERS;
      }
      
      if ( success )
        db.setTransactionSuccessful();
    }
    finally
    {
      db.endTransaction();
    }
    return success;
  }
  
  /**
   * Method to delete a given count of records using the given order by
   * statement
   * 
   * @param count
   *          the count of records to delete
   * @param orderByStatement
   *          the order by statement
   * @return the count of deleted records
   */
  private final long deleteSamplesOrdered( long count, String orderByStatement )
  {
    boolean success = true;
    long cntDeleted = 0;
    db.beginTransaction();
    
    try
    {
      Set< Long > rowIds = new HashSet< Long >();
      
      // fetch request in a loop to not exceed compile limits for SQL statements
      while ( success && count > 0 )
      {
        long currentCount = Math.min( MAX_NUM_PLACEHOLDERS, count );
        
        Cursor cursor = getSamplesOrdered( currentCount, orderByStatement );
        if ( cursor.moveToFirst() )
        {
          do
          {
            rowIds.add( cursor.getLong( cursor.getColumnIndexOrThrow( KEY_ROWID ) ) );
          }
          while ( cursor.moveToNext() );
        }
        cursor.close();
        
        long cnt = deleteSamples( rowIds );
        if ( cnt <= 0 )
        {
          Logger.getInstance().error( this,
              "Failed to delete samples in database" );
          success = false;
        }
        else
        {
          cntDeleted += cnt;
        }
        
        rowIds.clear();
        count -= MAX_NUM_PLACEHOLDERS;
      }
      
      if ( success )
      {
        db.setTransactionSuccessful();
      }
      else
        cntDeleted = 0;
    }
    finally
    {
      db.endTransaction();
    }
    return cntDeleted;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.DatabaseAdapter#
   * deleteSamplesOrdered(long, boolean)
   */
  @Override
  public final long deleteSamplesOrdered( long count,
      boolean deleteLowestPriorityFirst )
  {
    if ( deleteLowestPriorityFirst )
    {
      return deleteSamplesOrdered( count, ORDER_BY_DESC_PRIO_TS );
    }
    return deleteSamplesOrdered( count, ORDER_BY_TS );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.DatabaseAdapter#
   * removeSamplesHighestPrioFirst(long, java.util.Collection)
   */
  @Override
  public final boolean removeSamplesHighestPrioFirst( long count,
      Collection< DatabaseSample > sampleCollection )
  {
    return removeSamples( count, sampleCollection, ORDER_BY_ASC_PRIO_TS );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.DatabaseAdapter#
   * removeSamplesLowestPrioFirst(long, java.util.Collection)
   */
  @Override
  public final boolean removeSamplesLowestPrioFirst( long count,
      Collection< DatabaseSample > sampleCollection )
  {
    return removeSamples( count, sampleCollection, ORDER_BY_DESC_PRIO_TS );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.DatabaseAdapter#
   * removeSamplesOldestTimeStampFirst(long, java.util.Collection)
   */
  @Override
  public final boolean removeSamplesOldestTimeStampFirst( long count,
      Collection< DatabaseSample > sampleCollection )
  {
    return removeSamples( count, sampleCollection, ORDER_BY_TS );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.DatabaseAdapter#
   * setMaximumDatabaseSize(long)
   */
  @Override
  public final long setMaximumDatabaseSize( long size )
  {
    long newSize = db.setMaximumSize( size );
    dbHelper.setMaxDBSize( newSize );
    return newSize;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.DatabaseAdapter#
   * getMaximumDatabaseSize()
   */
  @Override
  public final long getMaximumDatabaseSize()
  {
    return db.getMaximumSize();
  }
  
  /**
   * Getter for the current database page size in bytes
   * 
   * @return the page size in bytes
   */
  public final long getPageSize()
  {
    return db.getPageSize();
  }
}
