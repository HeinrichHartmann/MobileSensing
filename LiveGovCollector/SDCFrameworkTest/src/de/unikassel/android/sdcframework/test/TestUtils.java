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
package de.unikassel.android.sdcframework.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import junit.framework.Assert;

import android.content.Context;
import android.content.res.AssetManager;
import de.unikassel.android.sdcframework.util.FileUtils;

/**
 * Class with utility functions for tests
 * 
 * @author Katy Hilgenberg
 *
 */
public class TestUtils
{ 
  /**
   * The default buffer size
   */
  public static final int BUFFER_SIZE = 1024;
  
  /**
   * Helper method to wait a bit
   * 
   * @param millis
   *          the milliseconds to sleep
   */
  public final static void sleep( long millis )
  {
    try
    {
      Thread.sleep( millis );
    }
    catch ( InterruptedException e )
    {}
  }
  
  /**
   * Does delete the local test files
   * 
   * @param targetContext
   *          the application target context
   * @param files
   *          the files to delete
   */
  public final static void deleteLocalFiles( Context targetContext,
      List< String > files )
  {
    for ( String fileName : files )
    {
      Assert.assertTrue( "Unexpected error while trying to delete the following file: " + fileName,
          FileUtils.deleteFile( fileName ) );
    }
  }
  
  /**
   * Method to copy a file from asset folder to a given out stream
   * 
   * @param assetManager
   *          the asset manager
   * @param fileName
   *          the name of the file to copy
   * @param out
   *          the out stream to write to
   * @throws IOException
   *           in case of IO error
   */
  public final static void copyAssetFile( AssetManager assetManager,
      String fileName, OutputStream out )
      throws IOException
  {
    InputStream in = assetManager.open( FileUtils.fileNameFromPath( fileName ) );
    
    byte[] buffer = new byte[ BUFFER_SIZE ];
    int count = in.read( buffer, 0, BUFFER_SIZE );
    while ( count != -1 )
    {
      out.write( buffer, 0, count );
      count = in.read( buffer, 0, BUFFER_SIZE );
    }
    out.flush();
    out.close();
    in.close();
  }

}
