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

import android.net.Uri;
import android.provider.BaseColumns;
import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;

/**
 * Abstract base class for content provider types
 * 
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractContentProviderData
    implements ContentProviderData, BaseColumns
{
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.provider.facade.ContentProviderData#getDBName
   * ()
   */
  @Override
  public final String getDBName()
  {
    return getContentTypeName() + ".db";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.provider.facade.ContentProviderData#
   * getDBTableName()
   */
  @Override
  public final String getDBTableName()
  {
    return getContentTypeName();
  }
  
  /**
   * Constructor
   */
  public AbstractContentProviderData()
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.provider.facade.ContentProviderData#
   * getAuthority()
   */
  @Override
  public final String getAuthority()
  {
    return "de.unikassel.android.sdcframework.provider." +
        getContentTypeName() + "provider";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.provider.facade.ContentProviderData#
   * getContentType()
   */
  @Override
  public final String getContentType()
  {
    return "vnd.android.cursor.dir/vnd.unikassel.android.sdcframework."
        + getContentTypeName();
  }
  
  
  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.provider.facade.ContentProviderData#
   * getContentItemType()
   */
  @Override
  public final String getContentItemType()
  {
    return "vnd.android.cursor.item/vnd.unikassel.android.sdcframework."
        + getContentTypeName();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.provider.facade.ContentProviderData#
   * getContentUri()
   */
  @Override
  public final Uri getContentUri()
  {
    return Uri.parse( "content://" + getAuthority() + "/"
        + getContentTypeName() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.provider.facade.ContentProviderData#
   * getDBVersion()
   */
  @Override
  public final int getDBVersion()
  {
    return 1;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.provider.facade.ContentProviderData#
   * getProjectionMap()
   */
  @Override
  public HashMap< String, String > getProjectionMap()
  {
    HashMap< String, String > projectionMap = new HashMap< String, String >();
    projectionMap.put( ContentProviderData._ID, ContentProviderData._ID );
    projectionMap.put( ContentProviderData.TIMESTAMP,
        ContentProviderData.TIMESTAMP );
    projectionMap.put( ContentProviderData.SYNCED,
        ContentProviderData.SYNCED );
    return projectionMap;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.provider.facade.ContentProviderData#
   * getCreateTableStatement()
   */
  @Override
  public final String getCreateTableStatement()
  {
    HashMap< String, String > projectionMap = getProjectionMap();
    
    String columnName = projectionMap.get( ContentProviderData._ID );
    StringBuffer statement = new StringBuffer( "create table " );
    statement.append( getDBTableName() ).append( " ( " ).append( columnName ).append(
        " " ).append( getColumnDataType( columnName ) );
    
    for ( String id : projectionMap.keySet() )
    {
      if ( ContentProviderData._ID.equals( id ) )
      {
        continue;
      }
      columnName = projectionMap.get( id );
      String dataType = getColumnDataType( columnName );
      statement.append( ", " ).append( columnName ).append( " " ).append(
          dataType );
    }
    
    statement.append( " )" );
    return statement.toString();
  }
  
  /**
   * Getter for the database column type ( override this method to add
   * additional columns of extending classes )
   * 
   * @param columnName
   *          the row name ( as added as value to the projection map )
   * @return the data type description as string ( valid for a create table
   *         statement )
   */
  protected String getColumnDataType( String columnName )
  {
    HashMap< String, String > projectionMap = getProjectionMap();
    if ( projectionMap.get( ContentProviderData.TIMESTAMP ).equals( columnName ) )
    {
      return "integer not null";
    }
    else if ( projectionMap.get( ContentProviderData._ID ).equals( columnName ) )
    {
      return "integer primary key autoincrement";
    }
    else if ( projectionMap.get( ContentProviderData.SYNCED ).equals( columnName ) )
    {
      return "integer default null";
    }
    return "text not null";
  }
}
