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
package de.unikassel.android.sdcframework.transmission.tests;

import java.io.File;
import java.util.UUID;

import de.unikassel.android.sdcframework.data.SampleCollection;
import de.unikassel.android.sdcframework.data.independent.BasicSampleCollection;
import de.unikassel.android.sdcframework.data.independent.DeviceInformation;
import de.unikassel.android.sdcframework.data.tests.TestSampleCollection;
import de.unikassel.android.sdcframework.preferences.TransmissionConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.transmission.FileManager;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.JarCompressionStrategy;
import de.unikassel.android.sdcframework.util.ZipCompressionStrategy;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;
import android.content.Context;
import android.test.AndroidTestCase;

/**
 * Test for the FileManager class.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestFileManager
    extends AndroidTestCase
{
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    // create a context redirecting the files directory
    setContext( getMockContext( getContext() ) );
    super.setUp();
    
  }
  
  /**
   * Getter for a mock context redirecting files directory to cache directory
   * for test purpose
   * 
   * @param context
   *          the context to delegate other stuff to
   * 
   * @return a mock context redirecting files to cache directory
   */
  public static DelegatingMockContext getMockContext( Context context )
  {
    return new DelegatingMockContext( context )
    {
      
      /*
       * (non-Javadoc)
       * 
       * @see android.content.ContextWrapper#getFilesDir()
       */
      @Override
      public File getFilesDir()
      {
        // / for this test we do redirect files directory to cache directory
        return super.getCacheDir();
      }
      
    };
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    doCleanUpFiles( getContext() );
    super.tearDown();
  }
  
  /**
   * Method to clean up created files
   * 
   * @param context
   *          the context to use
   */
  public static void doCleanUpFiles( Context context )
  {
    String testFilesDir = context.getFilesDir().getPath() + File.separatorChar;
    String tmpDir =
        testFilesDir + FileManager.TEMP_DIR_NAME;
    File tmpDirectory = FileUtils.fileFromPath( tmpDir );
    if ( tmpDirectory.exists() )
    {
      for ( File file : tmpDirectory.listFiles() )
      {
        FileUtils.deleteFile( file.getAbsolutePath() );
      }
      FileUtils.deleteFile( tmpDir );
    }
    FileUtils.deleteFile( testFilesDir + DeviceInformation.DEVICE_INFO_FILE );
  }
  
  /**
   * Test method for Construction .
   */
  public final void testFileManager()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( ArchiveTypes.jar );
    
    String testFileDir =
        getContext().getFilesDir().getPath() + File.separatorChar;
    String tmpDir =
        testFileDir + FileManager.TEMP_DIR_NAME + File.separatorChar;
    String deviceFile = testFileDir + DeviceInformation.DEVICE_INFO_FILE;
    String sampleFile = tmpDir + BasicSampleCollection.SAMPLE_COLLECTION_FILE;
    
    assertFalse( "Expected temporary directory not existing",
        FileUtils.fileFromPath( tmpDir ).exists() );
    assertFalse( "Expected device file not existing", FileUtils.fileFromPath(
        deviceFile ).exists() );
    assertFalse( "Expected sample file not existing", FileUtils.fileFromPath(
        sampleFile ).exists() );
    
    // test for clean environment
    UUID randomUUID = UUID.randomUUID();
    FileManager manager = new FileManager( getContext(), config, randomUUID );
    
    assertEquals( "Expected device filename set", deviceFile,
        manager.getDeviceFile() );
    assertEquals( "Expected sample filename set", sampleFile,
        manager.getSampleFile() );
    assertNull( "Expected archive filename null after creation",
        manager.getCurrentArchive() );
    assertFalse( "Expected archive not existing", manager.hasArchive() );
    
    assertTrue( "Expected temporary directory existing",
        FileUtils.fileFromPath( tmpDir ).exists() );
    assertTrue( "Expected device file existing", FileUtils.fileFromPath(
        deviceFile ).exists() );
    assertNotNull( "Expected compressor set", manager.getFileCompressor() );
    
    assertEquals( "Expected file compressor configured",
        config.getArchiveType().toString(),
        manager.getFileCompressor().getArchiveExtension() );
    assertTrue(
        "Expected file compressors strategy configured",
        manager.getFileCompressor().getStrategy() instanceof JarCompressionStrategy );
    
    // test for existing archive
    String archiveFile =
        tmpDir + FileManager.ARCHIVE_FILE + '.'
            + config.getArchiveType().toString();
    FileUtils.writeToTextFile( "", archiveFile );
    config.setArchiveType( ArchiveTypes.zip );
    manager = new FileManager( getContext(), config, randomUUID );
    
    assertEquals( "Expected device filename set", deviceFile,
        manager.getDeviceFile() );
    assertEquals( "Expected sample filename set", sampleFile,
        manager.getSampleFile() );
    assertTrue( "Expected archive existing", manager.hasArchive() );
    assertEquals( "Expected archive filename set to existing file name",
        archiveFile, manager.getCurrentArchive() );
    
    assertTrue( "Expected temporary directory existing",
        FileUtils.fileFromPath( tmpDir ).exists() );
    assertTrue( "Expected device file existing", FileUtils.fileFromPath(
        deviceFile ).exists() );
    assertNotNull( "Expected compressor set", manager.getFileCompressor() );
    
    assertEquals( "Expected file compressor configured",
        config.getArchiveType().toString(),
        manager.getFileCompressor().getArchiveExtension() );
    assertTrue(
        "Expected file compressors strategy configured",
        manager.getFileCompressor().getStrategy() instanceof ZipCompressionStrategy );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.FileManager#createArchive(de.unikassel.android.sdcframework.data.SampleCollection)}
   * .
   */
  public final void testCreateArchive()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( ArchiveTypes.jar );
    UUID randomUUID = UUID.randomUUID();
    FileManager manager = new FileManager( getContext(), config, randomUUID );
    
    SampleCollection sc = TestSampleCollection.createSamples( 10 );
    assertTrue( "Expected archive creation successful",
        manager.createArchive( sc ) );
    
    String tmpDir =
        getContext().getFilesDir().getPath() + File.separatorChar
            + FileManager.TEMP_DIR_NAME + File.separatorChar;
    
    assertEquals( "Expected archive filename set", tmpDir
        + FileManager.ARCHIVE_FILE + '.' + config.getArchiveType().toString(),
        manager.getCurrentArchive() );
    assertFalse( "Expected temporary sample file cleaned up",
        FileUtils.fileFromPath(
            tmpDir + BasicSampleCollection.SAMPLE_COLLECTION_FILE ).exists() );
    assertTrue( "Expected archive file created",
        FileUtils.fileFromPath( manager.getCurrentArchive() ).exists() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.FileManager#doCleanUp(boolean)}
   * .
   */
  public final void testDoCleanUp()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( ArchiveTypes.jar );
    UUID randomUUID = UUID.randomUUID();
    FileManager manager = new FileManager( getContext(), config, randomUUID );
    
    String tmpDir =
        getContext().getFilesDir().getPath() + File.separatorChar
            + FileManager.TEMP_DIR_NAME + File.separatorChar;
    String sampleFile = tmpDir + BasicSampleCollection.SAMPLE_COLLECTION_FILE;
    
    // create a temporary sample file and test clean up without archive
    FileUtils.writeToTextFile( "temporary", sampleFile );
    assertTrue( "Expected temporary sample file existing",
        FileUtils.fileFromPath( sampleFile ).exists() );
    
    manager.doCleanUp( false );
    
    assertFalse( "Expected temporary sample file cleaned up",
        FileUtils.fileFromPath( sampleFile ).exists() );
    
    // now create archive with a temporary file and test archive clean up flag
    SampleCollection sc = TestSampleCollection.createSamples( 10 );
    assertTrue( "Expected archive creation successful",
        manager.createArchive( sc ) );
    
    String archiveFile = manager.getCurrentArchive();
    assertTrue( "Expected archive file created",
        FileUtils.fileFromPath( archiveFile ).exists() );
    
    FileUtils.writeToTextFile( "temporary", sampleFile );
    assertTrue( "Expected temporary sample file existing",
        FileUtils.fileFromPath( sampleFile ).exists() );
    manager.doCleanUp( false );
    
    assertTrue( "Expected archive still existing",
        FileUtils.fileFromPath( archiveFile ).exists() );
    assertFalse( "Expected temporary sample file cleaned up",
        FileUtils.fileFromPath( sampleFile ).exists() );
    
    FileUtils.writeToTextFile( "temporary", sampleFile );
    assertTrue( "Expected temporary sample file existing",
        FileUtils.fileFromPath( sampleFile ).exists() );
    manager.doCleanUp( true );
    
    assertFalse( "Expected archive file cleaned up",
        FileUtils.fileFromPath( archiveFile ).exists() );
    assertFalse( "Expected temporary sample file cleaned up",
        FileUtils.fileFromPath( sampleFile ).exists() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.FileManager#updateConfiguration(android.content.Context, de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration)}
   * .
   */
  public final void testUpdateConfiguration()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( ArchiveTypes.jar );
    UUID randomUUID = UUID.randomUUID();
    FileManager manager = new FileManager( getContext(), config, randomUUID );
    
    assertEquals( "Expected file compressor configured",
        config.getArchiveType().toString(),
        manager.getFileCompressor().getArchiveExtension() );
    assertTrue(
        "Expected file compressors strategy configured",
        manager.getFileCompressor().getStrategy() instanceof JarCompressionStrategy );
    
    // test update
    config.setArchiveType( ArchiveTypes.zip );
    manager.updateConfiguration( getContext(), config );
    
    assertEquals( "Expected file compressor configured",
        config.getArchiveType().toString(),
        manager.getFileCompressor().getArchiveExtension() );
    assertFalse(
        "Expected file compressors strategy configured",
        manager.getFileCompressor().getStrategy() instanceof JarCompressionStrategy );
    
    config.setArchiveType( ArchiveTypes.jar );
    manager.updateConfiguration( getContext(), config );
    
    assertEquals( "Expected file compressor configured",
        config.getArchiveType().toString(),
        manager.getFileCompressor().getArchiveExtension() );
    assertTrue(
        "Expected file compressors strategy configured",
        manager.getFileCompressor().getStrategy() instanceof JarCompressionStrategy );
  }
  
}
