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
import de.unikassel.android.sdcframework.provider.TagProviderData;
import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;
import android.test.AndroidTestCase;

/**
 * Test for the tag provider data
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestTagProviderData
    extends AndroidTestCase
{
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.TagProviderData#getProjectionMap()}
   * .
   */
  public final void testGetProjectionMap()
  {
    ContentProviderData providerData = TagProviderData.getInstance();
    HashMap< String, String > projectionMap = providerData.getProjectionMap();
    assertNotNull( "projection map can not be null", projectionMap );
    assertEquals( "projection map has wrong size", 4, projectionMap.size() );
    assertEquals( "wrong id entry in projection map",
        ContentProviderData._ID, projectionMap.get( ContentProviderData._ID ) );
    assertEquals( "wrong timestamp entry in projection map",
        ContentProviderData.TIMESTAMP,
        projectionMap.get( ContentProviderData.TIMESTAMP ) );
    assertEquals( "wrong timestamp entry in projection map",
        ContentProviderData.SYNCED,
        projectionMap.get( ContentProviderData.SYNCED ) );
    assertEquals( "wrong text entry in projection map",
        TagProviderData.TEXT,
        projectionMap.get( TagProviderData.TEXT ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.TagProviderData#getInstance()}
   * .
   */
  public final void testGetInstance()
  {
    ContentProviderData providerData = TagProviderData.getInstance();
    assertTrue( "Invalid base type",
        providerData instanceof AbstractContentProviderData );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.provider.TagProviderData#getContentTypeName()}
   * .
   */
  public final void testGetContentTypeName()
  {
    ContentProviderData providerData = TagProviderData.getInstance();
    assertEquals( "Unexpected content type name", "tags",
        providerData.getContentTypeName() );
  }
  
}
