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
package de.unikassel.android.sdcframework.provider.facade;

import java.util.HashMap;

import android.net.Uri;

/**
 * Interface for content provider data definition types.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface ContentProviderData
{
  
  /**
   * The unique row identifier
   */
  public static final String _ID = "_id";
  
  /**
   * The column name for sample data time stamp
   */
  public static final String TIMESTAMP = "timestamp";

  /**
   * The column name for synchronization state
   */
  public static final String SYNCED = "synced";
  
  /**
   * Getter for the content type name
   * 
   * @return the content type name ( must be unique for all content types, used
   *         as table name and to build authority, database name and content URI
   *         )
   */
  public abstract String getContentTypeName();
  
  /**
   * Getter for the database name
   * 
   * @return the database name
   */
  public abstract String getDBName();
  
  /**
   * Getter for the database table name
   * 
   * @return the database table name
   */
  public abstract String getDBTableName();
  
  /**
   * Getter for the authority ( the provider name )
   * 
   * @return the authority
   */
  public abstract String getAuthority();
  
  /**
   * Getter for the content URI
   * 
   * @return the content URI
   */
  public abstract Uri getContentUri();
  
  /**
   * Getter for the content type
   * 
   * @return the content type for the directory
   */
  public abstract String getContentType();
  
  /**
   * Getter for the item content type
   * 
   * @return the content type for items
   */
  public abstract String getContentItemType();
  
  /**
   * Getter for the database version
   * 
   * @return the database version
   */
  public abstract int getDBVersion();
  
  /**
   * Getter for the create table statement
   * 
   * @return the create table statement
   */
  public abstract String getCreateTableStatement();
  
  /**
   * Getter for the projection map for table column identification
   * 
   * @return the projection map
   */
  public abstract HashMap< String, String > getProjectionMap();
  
}
