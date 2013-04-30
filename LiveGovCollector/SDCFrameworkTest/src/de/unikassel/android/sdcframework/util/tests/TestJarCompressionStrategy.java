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
package de.unikassel.android.sdcframework.util.tests;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.JarCompressionStrategy;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;

/**
 * @author Katy Hilgenberg
 * 
 */
public class TestJarCompressionStrategy
    extends InstrumentationTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.JarCompressionStrategy#JarCompressionStrategy()}
   * .
   */
  public final void testJarCompressionStrategy()
  {
    JarCompressionStrategy compressionStrategy = new JarCompressionStrategy();
    assertEquals( "Expected method deflated set", JarOutputStream.DEFLATED,
        compressionStrategy.getMethod() );
    assertEquals( "Unexpected archive extension", ArchiveTypes.jar.toString(),
        compressionStrategy.getArchiveExtension() );
    compressionStrategy = new JarCompressionStrategy( true );
    assertEquals( "Expected method deflated set", JarOutputStream.DEFLATED,
            compressionStrategy.getMethod() );
    assertEquals( "Unexpected archive extension", ArchiveTypes.jar.toString(),
        compressionStrategy.getArchiveExtension() );
    compressionStrategy = new JarCompressionStrategy( false );
    assertEquals( "Expected method deflated set", JarOutputStream.STORED,
            compressionStrategy.getMethod() );
    assertEquals( "Unexpected archive extension", ArchiveTypes.jar.toString(),
        compressionStrategy.getArchiveExtension() );
  }
  
  /**
   * Test compression.
   */
  public final void testCompress()
  {
    Context targetContext = getInstrumentation().getTargetContext();
    Resources resources = getInstrumentation().getContext().getResources();
    AssetManager assetManager = resources.getAssets();
    
    String fileName1 = targetContext.getFilesDir().getAbsolutePath()
        + File.separator + "SDCConfigForTest.xml";
    String fileName2 = targetContext.getFilesDir().getAbsolutePath()
        + File.separator + "DeviceInfo.xml";
    String destFile = targetContext.getFilesDir().getAbsolutePath()
    + File.separator + "Test.jar";
    
    try
    {
      TestZipCompressionStrategy.createLocalTestFiles(
          getInstrumentation().getTargetContext(),
          assetManager, fileName1, fileName2 );
      
      // try to compress it
      JarCompressionStrategy compressionStrategy = new JarCompressionStrategy();
      BufferedOutputStream bufferedOut =
        new BufferedOutputStream( new FileOutputStream(
            FileUtils.fileFromPath( destFile ) ) );
      
      List< String > fileNames = new ArrayList< String >();
      fileNames.add( fileName1 );
      fileNames.add( fileName2 );
      compressionStrategy.compress( fileNames, bufferedOut );
      bufferedOut.close();
      
      String orgContent1 = FileUtils.readTextFileContent( fileName1 );
      String orgContent2 = FileUtils.readTextFileContent( fileName2 );
      String content1 = readArchiveContent( destFile, fileName1 );
      String content2 = readArchiveContent( destFile, fileName2 );
      assertEquals( "Expected same content for file " + fileName1, orgContent1, content1 );
      assertEquals( "Expected same content for file " + fileName2, orgContent2, content2 );

      fileNames.add( destFile );
      TestUtils.deleteLocalFiles( targetContext, fileNames );
      
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
  }
  
  /**
   * Does read the uncompressed archive content
   * 
   * @param archive
   *          the archive
   * @param fileName
   *          the filename in archive to read
   * @return the uncompressed content of the file in the archive
   */
  private String readArchiveContent( String archive, String fileName )
  {
    String result = "";
    try
    {
      JarInputStream jarStream =
        new JarInputStream( new BufferedInputStream(
            new FileInputStream( FileUtils.fileFromPath(
                archive ) ) ) );
      result = FileUtils.readTextFileContentFromArchive( jarStream, fileName );
    }
    catch ( IOException e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
    return result;
  }
  
}
