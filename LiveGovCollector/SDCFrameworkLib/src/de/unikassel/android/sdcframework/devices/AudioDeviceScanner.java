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

import de.unikassel.android.sdcframework.data.independent.FileReferenceSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.provider.AudioProviderData;

import android.content.ContentResolver;
import android.database.Cursor;

/**
 * Implementation of the virtual audio sensor device scanner.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class AudioDeviceScanner extends ContentProviderDeviceScanner
{
  
  /**
   * Constructor
   * 
   * @param resolver
   *          the content resolver
   */
  public AudioDeviceScanner( ContentResolver resolver )
  {
    super( resolver, AudioProviderData.getInstance().getContentUri() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.ContentProviderDeviceScanner#
   * getSampleDataFromCursor(android.database.Cursor)
   */
  @Override
  protected SampleData getSampleDataFromCursor( Cursor cursor )
  {
    FileReferenceSampleData data = new FileReferenceSampleData();
    data.setFile( cursor.getString( cursor.getColumnIndex( AudioProviderData.LOCATION ) ) );
    return data;
  }
  
}
