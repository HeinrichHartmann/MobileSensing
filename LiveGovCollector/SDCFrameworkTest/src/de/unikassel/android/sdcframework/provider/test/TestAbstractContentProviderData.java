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
package de.unikassel.android.sdcframework.provider.test;

import java.util.HashMap;

import de.unikassel.android.sdcframework.provider.AbstractContentProviderData;
import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Test for the abstract content provider data type.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAbstractContentProviderData extends AndroidTestCase
{
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.AbstractContentProviderData#getDBName()}
   * .
   */
  public final void testGetDBName()
  {
    AbstractContentProviderData testProviderdata =
        new ContentProviderDataForTest();
    assertEquals( "Unexpected database name",
        ContentProviderDataForTest.TYPE_NAME + ".db",
        testProviderdata.getDBName() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.AbstractContentProviderData#getDBTableName()}
   * .
   */
  public final void testGetDBTableName()
  {
    AbstractContentProviderData testProviderdata =
        new ContentProviderDataForTest();
    assertEquals( "Unexpected database table name",
        ContentProviderDataForTest.TYPE_NAME, testProviderdata.getDBTableName() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.AbstractContentProviderData#getAuthority()}
   * .
   */
  public final void testGetAuthority()
  {
    AbstractContentProviderData testProviderdata =
        new ContentProviderDataForTest();
    assertEquals( "Unexpected authority",
        "de.unikassel.android.sdcframework.provider." +
            ContentProviderDataForTest.TYPE_NAME + "provider",
        testProviderdata.getAuthority() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.AbstractContentProviderData#getContentType()}
   * .
   */
  public final void testGetContentType()
  {
    AbstractContentProviderData testProviderdata =
        new ContentProviderDataForTest();
    assertEquals( "Unexpected content type",
        "vnd.android.cursor.dir/vnd.unikassel.android.sdcframework." +
            ContentProviderDataForTest.TYPE_NAME,
        testProviderdata.getContentType() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.AbstractContentProviderData#getContentItemType()}
   * .
   */
  public final void testGetContentItemType()
  {
    AbstractContentProviderData testProviderdata =
        new ContentProviderDataForTest();
    assertEquals( "Unexpected content type",
        "vnd.android.cursor.item/vnd.unikassel.android.sdcframework." +
            ContentProviderDataForTest.TYPE_NAME,
        testProviderdata.getContentItemType() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.AbstractContentProviderData#getContentUri()}
   * .
   */
  public final void testGetContentUri()
  {
    AbstractContentProviderData testProviderdata =
        new ContentProviderDataForTest();
    Uri uriExpected =
        Uri.parse( "content://" + testProviderdata.getAuthority() + "/"
            + ContentProviderDataForTest.TYPE_NAME );
    
    assertEquals( "Unexpected content URI",
        uriExpected, testProviderdata.getContentUri() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.AbstractContentProviderData#getDBVersion()}
   * .
   */
  public final void testGetDBVersion()
  {
    AbstractContentProviderData testProviderdata =
        new ContentProviderDataForTest();
    assertEquals( "Unexpected database version",
        1, testProviderdata.getDBVersion() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.AbstractContentProviderData#getProjectionMap()}
   * .
   */
  public final void testGetProjectionMap()
  {
    AbstractContentProviderData testProviderdata =
        new ContentProviderDataForTest();
    HashMap< String, String > projectionMap =
        testProviderdata.getProjectionMap();
    assertNotNull( "projection map can not be null", projectionMap );
    assertEquals( "projection map has wrong size", 3, projectionMap.size() );
    assertEquals( "wrong id entry in projection map",
        ContentProviderData._ID, projectionMap.get( ContentProviderData._ID ) );
    assertEquals( "wrong timestamp entry in projection map",
        ContentProviderData.TIMESTAMP,
        projectionMap.get( ContentProviderData.TIMESTAMP ) );
    assertEquals( "wrong time stamp sync entry in projection map",
        ContentProviderData.SYNCED,
        projectionMap.get( ContentProviderData.SYNCED ) );
  }
  
}
