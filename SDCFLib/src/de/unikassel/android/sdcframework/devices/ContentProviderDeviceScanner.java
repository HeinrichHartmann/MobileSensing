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
package de.unikassel.android.sdcframework.devices;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.TimeInformation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

/**
 * Abstract base class for content provider based device scanner types. <br/>
 * <br/>
 * A content provider based scanner does register itself as observer for a
 * content provider and creates samples from the notified data. It can be used
 * together with the {@link VirtualSensorDevice} to create virtual observable
 * sensor devices which are not related to a physical device. <br/>
 * <br/>
 * Just to mention it, this scanner is triggered by content provider changes and
 * is simply ignoring the configured device frequency.
 * 
 * @see TwitterDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
public abstract class ContentProviderDeviceScanner extends
    AbstractSensorDeviceScanner
{
  /**
   * The content resolver context
   */
  private final ContentResolver resolver;
  
  /**
   * The URI for the scanned provider content
   */
  private final Uri contentURI;
  
  /**
   * The content observer
   */
  private final ContentObserver contentObserver;
  
  /**
   * Constructor
   * 
   * @param resolver
   *          the content resolver
   * @param contentURI
   */
  public ContentProviderDeviceScanner( ContentResolver resolver, Uri contentURI )
  {
    super();
    this.resolver = resolver;
    if ( this.resolver == null )
      throw new IllegalArgumentException( "resolver is null" );
    this.contentURI = contentURI;
    this.contentObserver = new ContentObserver( new Handler() )
    {
      /*
       * (non-Javadoc)
       * 
       * @see android.database.ContentObserver#onChange(boolean)
       */
      @Override
      public void onChange( boolean selfChange )
      {
        super.onChange( selfChange );
        doGatherSamples();
      }
      
    };
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDeviceScanner#
   * isCompatibleDevice
   * (de.unikassel.android.sdcframework.devices.facade.SensorDevice)
   */
  @Override
  protected boolean isCompatibleDevice( SensorDevice device )
  {
    return ( device instanceof VirtualSensorDevice );
  }
  
  /**
   * Getter for the content resolver
   */
  protected final ContentResolver getContentResolver()
  {
    return resolver;
  }
  
  /**
   * Getter for the content observer
   * 
   * @return the content observer
   */
  protected ContentObserver getContentObserver()
  {
    return contentObserver;
  }
  
  /**
   * Getter for the content URI
   * 
   * @return the content URI
   */
  protected final Uri getContentURI()
  {
    return contentURI;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#start
   * (android.content.Context)
   */
  @Override
  public final boolean start( Context context )
  {
    getContentResolver().registerContentObserver( getContentURI(), true,
        contentObserver );
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#stop
   * (android.content.Context)
   */
  @Override
  public final boolean stop( Context context )
  {
    getContentResolver().unregisterContentObserver( contentObserver );
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.AbstractSensorDeviceScanner#onDestroy
   * (android.content.Context)
   */
  @Override
  public void onDestroy( Context context )
  {
    super.onDestroy( context );
  }
  
  /**
   * The gathering method to request available data from the content provider,
   * create samples from it, notify observers and finally remove gathered data
   * from the provider content.
   */
  
  protected final synchronized void doGatherSamples()
  {
    // test for available data from the content provider
    ContentResolver contentResolver = getContentResolver();
    if ( contentResolver != null )
    {
      Queue< Long > rowIds = doNotifyForSamples( contentResolver );
      
      // delete processed twitter data
      doDeleteGatheredData( contentResolver, rowIds );
    }
    else
    {
      Logger.getInstance().error( this,
          "Content provider not available: " + getContentURI() );
    }
  }
  
  /**
   * Method to delete content from the provider
   * 
   * @param contentResolver
   *          the content resolver to use
   * @param rowIds
   *          the row id's to delete
   */
  public void doDeleteGatheredData( ContentResolver contentResolver,
      Queue< Long > rowIds )
  {
    try
    {
      // delete messages in a loop to avoid SQL statement limits exceeding
      while ( rowIds.size() > 0 )
      {
        long count = Math.min( 200, rowIds.size() );
        String[] sWhereArgs = new String[ rowIds.size() ];
        StringBuffer sWhere = new StringBuffer( ContentProviderData._ID );
        sWhere.append( " IN ( " );
        int i = 0;
        for ( Long rowId : rowIds )
        {
          if ( i > 0 )
          {
            sWhere.append( ", " );
          }
          sWhereArgs[ i ] = rowId.toString();
          rowIds.remove( rowId );
          sWhere.append( '?' );
          ++i;
          
          if ( i >= count )
            break;
        }
        sWhere.append( " )" );
        
        contentResolver.delete( getContentURI(), sWhere.toString(), sWhereArgs );
      }
    }
    catch ( Exception e )
    {
      Logger.getInstance().error( this,
          "Exception while deleting samples from provider: " + e.getMessage() );
    }
  }
  
  /**
   * Method to gather content from the provider and create sample notifications
   * 
   * @param contentResolver
   *          the content resolver to use
   * @return the id's of the gathered rows
   */
  public final Queue< Long >
      doNotifyForSamples( ContentResolver contentResolver )
  {
    Queue< Long > rowIds = new ConcurrentLinkedQueue< Long >();
    
    Cursor cursor =
        contentResolver.query( getContentURI(), null, null, null, null );
    
    try
    {
      if ( cursor.moveToFirst() )
      {
        do
        {
          rowIds.add( cursor.getLong( cursor.getColumnIndexOrThrow( ContentProviderData._ID ) ) );
          
          long timeStamp =
              cursor.getLong( cursor.getColumnIndex( ContentProviderData.TIMESTAMP ) );
          boolean synced =
            cursor.getInt( cursor.getColumnIndex( ContentProviderData.SYNCED ) ) == 1;
          Sample sample =
              SampleFactory.getInstance().createSample( new TimeInformation( timeStamp, synced ), 
                  getDevice().getDeviceIdentifier(),
                  getDevice().getConfiguration().getSamplePriority().ordinal(),
                  getSampleDataFromCursor( cursor ) );
          if ( sample != null )
          {
            notify( sample );
          }
        }
        while ( cursor.moveToNext() );
      }
    }
    catch ( Exception e )
    {
      Logger.getInstance().error( this,
          "Exception while reading content from provider: " + e.getMessage() );
    }
    finally
    {
      cursor.close();
    }
    
    return rowIds;
  }
  
  /**
   * Method to extract concrete sample data at database cursor position
   * 
   * @param cursor
   *          the database cursor
   * @return the extracted sample data
   */
  protected abstract SampleData getSampleDataFromCursor( Cursor cursor );
}