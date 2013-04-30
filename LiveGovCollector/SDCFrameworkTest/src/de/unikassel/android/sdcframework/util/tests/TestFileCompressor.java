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

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.FileCompressor;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.JarCompressionStrategy;
import de.unikassel.android.sdcframework.util.ZipCompressionStrategy;
import de.unikassel.android.sdcframework.util.facade.CompressionStrategy;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

/**
 * @author Katy Hilgenberg
 * 
 */
public class TestFileCompressor
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
   * {@link de.unikassel.android.sdcframework.util.FileCompressor#FileCompressor(de.unikassel.android.sdcframework.util.facade.CompressionStrategy)}
   * .
   */
  public final void testFileCompressor()
  {
    try
    {
      new FileCompressor( null );
      fail( "Expected InvalidParameterException exception" );
    }
    catch ( InvalidParameterException e )
    {}
    catch ( Exception e )
    {
      fail( "Unexpected exception type" );
    }
    
    CompressionStrategy strategy = new ZipCompressionStrategy();
    FileCompressor compressor = new FileCompressor( strategy );
    
    assertNotNull( "Expected strategy not null", compressor.getStrategy() );
    assertEquals( "Expected strategy set", strategy, compressor.getStrategy() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileCompressor#compressFiles(java.util.List, java.lang.String)}
   * .
   */
  public final void testCompressFiles()
  {
    // create test environment with 2 text files to compress
    Context targetContext = getInstrumentation().getTargetContext();
    Resources resources = getInstrumentation().getContext().getResources();
    AssetManager assetManager = resources.getAssets();
    
    String fileName1 = targetContext.getFilesDir().getAbsolutePath()
        + File.separator + "SDCConfigForTest.xml";
    String fileName2 = targetContext.getFilesDir().getAbsolutePath()
        + File.separator + "DeviceInfo.xml";
    String destFile1 = targetContext.getFilesDir().getAbsolutePath()
        + File.separator + "Test.zip";
    String destFile2 = targetContext.getFilesDir().getAbsolutePath()
    + File.separator + "Test.jar";
    
    List< String > fileNames = new ArrayList< String >();
    fileNames.add( fileName1 );
    fileNames.add( fileName2 );
    
    try
    {
      TestZipCompressionStrategy.createLocalTestFiles( targetContext,
          assetManager, fileName1, fileName2 );
      
      //  test with ZIP compression
      CompressionStrategy strategy = new ZipCompressionStrategy();
      FileCompressor compressor = new FileCompressor( strategy );      
      
      compressor.compressFiles( fileNames, destFile1 );
      
      String orgContent1 = FileUtils.readTextFileContent( fileName1 );
      String orgContent2 = FileUtils.readTextFileContent( fileName2 );
      String content1 = TestZipCompressionStrategy.readArchiveContent( destFile1, fileName1 );
      String content2 = TestZipCompressionStrategy.readArchiveContent( destFile1, fileName2 );
      assertEquals( "Expected same content for file " + fileName1, orgContent1,
          content1 );
      assertEquals( "Expected same content for file " + fileName2, orgContent2,
          content2 );
     
      //  test with jar compression
      strategy = new JarCompressionStrategy();
      compressor = new FileCompressor( strategy );      
      
      compressor.compressFiles( fileNames, destFile2 );
      
      orgContent1 = FileUtils.readTextFileContent( fileName1 );
      orgContent2 = FileUtils.readTextFileContent( fileName2 );
      content1 = TestZipCompressionStrategy.readArchiveContent( destFile2, fileName1 );
      content2 = TestZipCompressionStrategy.readArchiveContent( destFile2, fileName2 );
      assertEquals( "Expected same content for file " + fileName1, orgContent1,
          content1 );
      assertEquals( "Expected same content for file " + fileName2, orgContent2,
          content2 );      
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
    finally
    {
      fileNames.add( destFile1 );
      fileNames.add( destFile2 );
      TestUtils.deleteLocalFiles( targetContext, fileNames );
    }
  }  
}
