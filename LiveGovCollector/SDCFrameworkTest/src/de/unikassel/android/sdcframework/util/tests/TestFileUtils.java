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
import java.util.zip.ZipInputStream;

import de.unikassel.android.sdcframework.util.FileCompressor;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.ZipCompressionStrategy;

import android.content.Context;
import android.test.InstrumentationTestCase;

/**
 * @author Katy Hilgenberg
 * 
 */
public class TestFileUtils
    extends InstrumentationTestCase
{
  /**
   * The overall test file name
   */
  private final String rawTestFilename = "Test.file";
  
  /**
   * The overall test file name
   */
  private final String rawCopyFilename = "Test.cpy";
  
  /**
   * The overall test file name
   */
  private final String rawZipFilename = "Test.zip";
  
  /**
   * The test file content
   */
  private final String testContent = "This is some content\r\n for test!";
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    // create a test file
    File file =
        new File( getInstrumentation().getTargetContext().getFilesDir(),
            rawTestFilename );
    BufferedOutputStream out =
        new BufferedOutputStream( new FileOutputStream( file ) );
    out.write( testContent.getBytes(), 0, testContent.length() );
    out.close();
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    File file =
        new File( getInstrumentation().getTargetContext().getFilesDir(),
            rawTestFilename );
    if ( file.exists() ) file.delete();
    
    file =
        new File( getInstrumentation().getTargetContext().getFilesDir(),
            rawCopyFilename );
    if ( file.exists() ) file.delete();
    
    file =
        new File( getInstrumentation().getTargetContext().getFilesDir(),
            rawZipFilename );
    if ( file.exists() ) file.delete();
    super.tearDown();
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#fileNameFromPath(java.lang.String)}
   * .
   */
  public final void testFileNameFromPath()
  {
    Context targetContext = getInstrumentation().getTargetContext();
    String fileName = "Testfilename.what";
    String fileNameWithPath =
        targetContext.getFilesDir().getAbsolutePath() + File.separatorChar
            + fileName;
    assertEquals( "Unexpected file name extracted", fileName,
        FileUtils.fileNameFromPath( fileNameWithPath ) );
    
    fileNameWithPath = File.separatorChar + fileName;
    assertEquals( "Unexpected file name extracted", fileName,
        FileUtils.fileNameFromPath( fileNameWithPath ) );
    fileNameWithPath = "." + File.separatorChar + fileName;
    assertEquals( "Unexpected file name extracted", fileName,
        FileUtils.fileNameFromPath( fileNameWithPath ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#fileFromPath(java.lang.String)}
   * .
   */
  public final void testFileFromPath()
  {
    Context targetContext = getInstrumentation().getTargetContext();
    String fileName = "Testfilename.what";
    
    // test absolute path
    String fileNameWithPath =
        targetContext.getFilesDir().getAbsolutePath() + File.separatorChar
            + fileName;
    try
    {
      assertEquals( "Unexpected file name extracted", fileNameWithPath,
          FileUtils.fileFromPath( fileNameWithPath ).getAbsolutePath() );
      assertEquals( "Unexpected file name extracted", fileName,
          FileUtils.fileFromPath( fileNameWithPath ).getName() );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
    
    fileNameWithPath = File.separatorChar + fileName;
    try
    {
      assertEquals( "Unexpected file name extracted", fileNameWithPath,
          FileUtils.fileFromPath( fileNameWithPath ).getAbsolutePath() );
      assertEquals( "Unexpected file name extracted", fileName,
          FileUtils.fileFromPath( fileNameWithPath ).getName() );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
    
    // test relative path
    fileNameWithPath = "." + File.separatorChar + fileName;
    try
    {
      assertEquals( "Unexpected file name extracted", fileNameWithPath,
          FileUtils.fileFromPath( fileNameWithPath ).getPath() );
      assertEquals( "Unexpected file name extracted", fileName,
          FileUtils.fileFromPath( fileNameWithPath ).getName() );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#deleteFile(java.lang.String)}
   * .
   */
  public final void testDeleteFile()
  {
    File fileToDelete = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawTestFilename );
    assertTrue( "Expected file existing for test => check test setup!",
        fileToDelete.exists() );
    assertTrue( "Expected deletion successful",
        FileUtils.deleteFile( fileToDelete.getAbsolutePath() ) );
    assertFalse( "Expected file deleted", fileToDelete.exists() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#copy(java.lang.String, java.lang.String)}
   * .
   */
  public final void testCopyStringString()
  {
    File srcFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawTestFilename );
    File dstFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawCopyFilename );
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    assertFalse( "Expected file not " + dstFile.getAbsolutePath()
        + " existing for test => check test setup!",
        dstFile.exists() );
    
    assertTrue( "Expected copy successful",
        FileUtils.copy( srcFile.getAbsolutePath(), dstFile.getAbsolutePath() ) );
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    assertTrue( "Expected file " + dstFile.getAbsolutePath()
        + " existing after copy", dstFile.exists() );
    
    compareContent( srcFile, dstFile );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#copy(java.io.FileInputStream, java.lang.String)}
   * .
   */
  public final void testCopyFileInputStreamString()
  {
    File srcFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawTestFilename );
    File dstFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawCopyFilename );
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    assertFalse( "Expected file not " + dstFile.getAbsolutePath()
        + " existing for test => check test setup!",
        dstFile.exists() );
    
    try
    {
      FileInputStream sourceStream = new FileInputStream( srcFile );
      assertTrue( "Expected copy successful",
          FileUtils.copy( sourceStream, dstFile.getAbsolutePath() ) );
      sourceStream.close();
    }
    catch ( IOException e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    assertTrue( "Expected file " + dstFile.getAbsolutePath()
        + " existing after copy", dstFile.exists() );
    
    compareContent( srcFile, dstFile );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#copy(java.io.File, java.io.File)}
   * .
   */
  public final void testCopyFileFile()
  {
    File srcFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawTestFilename );
    File dstFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawCopyFilename );
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    assertFalse( "Expected file not " + dstFile.getAbsolutePath()
        + " existing for test => check test setup!",
        dstFile.exists() );
    
    assertTrue( "Expected copy successful",
        FileUtils.copy( srcFile, dstFile ) );
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    assertTrue( "Expected file " + dstFile.getAbsolutePath()
        + " existing after copy", dstFile.exists() );
    
    compareContent( srcFile, dstFile );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#copy(java.io.FileInputStream, java.io.FileOutputStream)}
   * .
   */
  public final void testCopyFileInputStreamFileOutputStream()
  {
    File srcFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawTestFilename );
    File dstFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawCopyFilename );
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    assertFalse( "Expected file not " + dstFile.getAbsolutePath()
        + " existing for test => check test setup!",
        dstFile.exists() );
    
    try
    {
      FileInputStream sourceStream = new FileInputStream( srcFile );
      FileOutputStream destStreamt = new FileOutputStream( dstFile );
      assertTrue( "Expected copy successful",
          FileUtils.copy( sourceStream, destStreamt ) );
      sourceStream.close();
      destStreamt.close();
    }
    catch ( IOException e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    assertTrue( "Expected file " + dstFile.getAbsolutePath()
        + " existing after copy", dstFile.exists() );
    
    compareContent( srcFile, dstFile );
  }
  
  /**
   * Method to compare file contents
   * 
   * @param srcFile
   *          the source file
   * @param dstFile
   *          the destination file
   */
  private void compareContent( File srcFile, File dstFile )
  {
    try
    {
      FileInputStream sourceStream = new FileInputStream( srcFile );
      FileInputStream destStreamt = new FileInputStream( dstFile );
      
      // we use the utility method to read from stream which is tested itself in
      // a separate test
      String srcText = FileUtils.readTextStreamContent( sourceStream );
      String dstText = FileUtils.readTextStreamContent( destStreamt );
      sourceStream.close();
      destStreamt.close();
      
      assertEquals( "Expetced both files have same content", srcText, dstText );
    }
    catch ( IOException e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#writeToTextFile(java.lang.String, java.lang.String)}
   * .
   */
  public final void testWriteToTextFile()
  {
    File dstFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawCopyFilename );
    assertFalse( "Expected file not " + dstFile.getAbsolutePath()
        + " existing for test => check test setup!",
        dstFile.exists() );
    
    assertTrue( "Expected write successful", FileUtils.writeToTextFile(
        this.testContent, dstFile.getAbsolutePath() ) );
    assertTrue( "Expected file " + dstFile.getAbsolutePath() + " existing",
        dstFile.exists() );
    // we use the utility method to read from file is tested itself in
    // a separate test
    String content = FileUtils.readTextFileContent( dstFile.getAbsolutePath() );
    assertEquals( "Unexpetced content written to file", this.testContent,
        content );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#readTextFileContent(java.lang.String)}
   * .
   */
  public final void testReadTextFileContent()
  {
    File srcFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawTestFilename );
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    
    String content = FileUtils.readTextFileContent( srcFile.getAbsolutePath() );
    assertEquals( "Unexpetced content read from file", this.testContent,
        content );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#readTextStreamContent(java.io.InputStream)}
   * .
   */
  public final void testReadTextStreamContent()
  {
    File srcFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawTestFilename );
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    
    try
    {
      FileInputStream sourceStream = new FileInputStream( srcFile );
      String content =
          FileUtils.readTextFileContent( srcFile.getAbsolutePath() );
      sourceStream.close();
      
      assertEquals( "Unexpetced content read from file", this.testContent,
          content );
    }
    catch ( IOException e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.FileUtils#readTextFileContentFromArchive(java.util.zip.ZipInputStream, java.lang.String)}
   * .
   */
  public final void testReadTextFileContentFromArchive()
  {
    File srcFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawTestFilename );
    File zipFile = new File(
        getInstrumentation().getTargetContext().getFilesDir(),
        rawZipFilename );
    assertTrue( "Expected file " + srcFile.getAbsolutePath()
        + " existing for test => check test setup!",
        srcFile.exists() );
    
    // create archive
    FileCompressor compressor =
        new FileCompressor( new ZipCompressionStrategy() );
    List< String > fileNames = new ArrayList< String >();
    fileNames.add( srcFile.getAbsolutePath() );
    compressor.compressFiles( fileNames, zipFile.getAbsolutePath() );
    assertTrue( "Expected zip file " + zipFile.getAbsolutePath()
        + " created", zipFile.exists() );
    
    try
    {
      ZipInputStream zipStream =
          new ZipInputStream( new BufferedInputStream(
              new FileInputStream( zipFile ) ) );
      String content =
          FileUtils.readTextFileContentFromArchive( zipStream,
              srcFile.getName() );
      zipStream.close();
      assertEquals( "Unexpetced content read from archive", this.testContent,
          content );
    }
    catch ( IOException e )
    {
      e.printStackTrace();
      fail( "Unexpected exception" );
    }
  }
  
}
