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
package de.unikassel.android.sdcframework.provider;

import java.util.HashMap;

import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;
import de.unikassel.android.sdcframework.util.TimeInformation;
import de.unikassel.android.sdcframework.util.TimeProvider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

/**
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractProvider extends ContentProvider
{
  /**
   * The internal SQLite helper class
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class DatabaseHelper extends SQLiteOpenHelper
  {
    
    /**
     * Constructor
     * 
     * @param context
     *          the context
     */
    DatabaseHelper( Context context )
    {
      super( context, providerData.getDBName(), null,
          providerData.getDBVersion() );
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
      db.execSQL( providerData.getCreateTableStatement() );
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
      // for the moment we do know just one version
    }
  }
  
  /**
   * Projection map for the column names
   */
  private final HashMap< String, String > projectionMap;
  
  /**
   * The internal URI matcher
   */
  private final UriMatcher uriMatcher;
  
  /**
   * The content provider related data
   */
  private final ContentProviderData providerData;
  
  /**
   * The content resolver
   */
  private ContentResolver resolver;
  
  /**
   * The item uri identifier
   */
  private final static int SINGLE_ITEM = 1;
  
  /**
   * The directory uri identifier
   */
  private final static int DIRECTORY = 0;
  
  /**
   * The internally used database helper
   */
  private DatabaseHelper dbHelper;
  
  /**
   * Constructor
   * 
   * @param providerData
   *          the provider data
   */
  public AbstractProvider( ContentProviderData providerData )
  {
    this.providerData = providerData;
    
    uriMatcher = new UriMatcher( UriMatcher.NO_MATCH );
    uriMatcher.addURI( providerData.getAuthority(),
        providerData.getContentTypeName(), DIRECTORY );
    uriMatcher.addURI( providerData.getAuthority(),
        providerData.getContentTypeName() + "/*", SINGLE_ITEM );
    
    projectionMap = providerData.getProjectionMap();
  }
  
  /**
   * Getter for the provider data
   * 
   * @return the provider data
   */
  public final ContentProviderData getProviderData()
  {
    return providerData;
  }
  
  /**
   * Getter for the database helper
   * 
   * @return the database helper
   */
  public final SQLiteOpenHelper getDbHelper()
  {
    return dbHelper;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#onCreate()
   */
  @Override
  public final boolean onCreate()
  {
    Context context = getContext();
    resolver = context.getContentResolver();
    dbHelper = new DatabaseHelper( context );
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#query(android.net.Uri,
   * java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
   */
  @Override
  public final Cursor query( Uri uri, String[] projection, String selection,
      String[] selectionArgs, String sortOrder )
  {
    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
    
    qb.setTables( providerData.getDBTableName() );
    qb.setProjectionMap( projectionMap );
    
    int match = uriMatcher.match( uri );
    switch ( match )
    {
      case DIRECTORY:
      {
        // just query all
        break;
      }
      case SINGLE_ITEM:
      {
        String id = uri.getLastPathSegment();
        qb.appendWhere( projectionMap.get( ContentProviderData._ID ) + "=" + id );
        break;
      }
      default:
      {
        throw new IllegalArgumentException( "Unknown URI " + uri );
      }
    }
    
    SQLiteDatabase db = dbHelper.getReadableDatabase();
    Cursor c =
        qb.query( db, projection, selection, selectionArgs, null, null,
            sortOrder );
    
    c.setNotificationUri( resolver, uri );
    return c;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#getType(android.net.Uri)
   */
  @Override
  public final String getType( Uri uri )
  {
    int match = uriMatcher.match( uri );
    switch ( match )
    {
      case DIRECTORY:
      {
        return providerData.getContentType();
      }
      case SINGLE_ITEM:
      {
        return providerData.getContentItemType();
      }
    }
    throw new IllegalArgumentException( "Unknown URI " + uri );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#insert(android.net.Uri,
   * android.content.ContentValues)
   */
  @Override
  public final Uri insert( Uri uri, ContentValues values )
  {
    int match = uriMatcher.match( uri );
    switch ( match )
    {
      case DIRECTORY:
      {
        if ( values != null )
        {
          SQLiteDatabase db = dbHelper.getWritableDatabase();
          if ( TimeProvider.getInstance().isSynced() )
          {
            // due to the new time provider feature we do override the time
            // information here!
            values.remove( ContentProviderData.TIMESTAMP );
            TimeInformation ti =
                TimeProvider.getInstance().getAccurateTimeInformation();
            values.put( ContentProviderData.TIMESTAMP,
                Long.toString( ti.ts ) );
            values.remove( ContentProviderData.SYNCED );
            values.put( ContentProviderData.SYNCED, ti.synced ? 1 : 0 );
          }
          long rowId =
              db.insert( providerData.getDBTableName(),
                  ContentProviderData.TIMESTAMP,
                  values );
          if ( rowId > 0 )
          {
            Uri noteUri =
                ContentUris.withAppendedId( providerData.getContentUri(), rowId );
            resolver.notifyChange( noteUri, null );
            return noteUri;
          }
        }
        
        throw new SQLException( "Failed to insert data into " + uri );
      }
      default:
      {
        // any URI but the one for the table itself is invalid
        throw new IllegalArgumentException( "Invalid URI " + uri );
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#delete(android.net.Uri,
   * java.lang.String, java.lang.String[])
   */
  @Override
  public final int delete( Uri uri, String selection, String[] selectionArgs )
  {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int count;
    int match = uriMatcher.match( uri );
    switch ( match )
    {
      case DIRECTORY:
      {
        if ( TextUtils.isEmpty( selection ) )
        {
          // we have to delete all rows in the table
          selection = "1";
        }
        break;
      }
      case SINGLE_ITEM:
      {
        // selective deletion
        String id = uri.getLastPathSegment();
        String sConstraint = ContentProviderData._ID + "=" + id;
        
        if ( TextUtils.isEmpty( selection ) )
        {
          selection = sConstraint;
        }
        else
        {
          selection = selection + " and " + sConstraint;
        }
        break;
      }
      default:
      {
        throw new IllegalArgumentException( "Unknown URI " + uri );
      }
    }
    
    count = db.delete( providerData.getDBTableName(), selection, selectionArgs );
    resolver.notifyChange( uri, null );
    return count;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.content.ContentProvider#update(android.net.Uri,
   * android.content.ContentValues, java.lang.String, java.lang.String[])
   */
  @Override
  public final int update( Uri uri, ContentValues values, String selection,
      String[] selectionArgs )
  {
    SQLiteDatabase db = dbHelper.getWritableDatabase();
    int count;
    int match = uriMatcher.match( uri );
    switch ( match )
    {
      case DIRECTORY:
      {
        break;
      }
      case SINGLE_ITEM:
      {
        // selective deletion
        String id = uri.getLastPathSegment();
        String sConstraint = ContentProviderData._ID + "=" + id;
        
        if ( TextUtils.isEmpty( selection ) )
        {
          selection = sConstraint;
        }
        else
        {
          selection = selection + " and " + sConstraint;
        }
        break;
      }
      default:
      {
        throw new IllegalArgumentException( "Unknown URI " + uri );
      }
    }
    
    count =
        db.update( providerData.getDBTableName(), values, selection,
            selectionArgs );
    resolver.notifyChange( uri, null );
    return count;
  }
}
