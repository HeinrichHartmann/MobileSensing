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
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import de.unikassel.android.sdcframework.preferences.TransmissionProtocolConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.BasicAuthHttpProtocol;
import de.unikassel.android.sdcframework.util.FileCompressor;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.ZipCompressionStrategy;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.Suppress;

/**
 * Special test for the HTTP protocol using a development server.
 * 
 * @author Katy Hilgenberg
 * 
 */
@Suppress
public class TestHttpProtocol
    extends InstrumentationTestCase
{
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.AbstractProtocol#uploadFile()}
   * .
   */
  public final void testUploadFile()
  {
    String pathToFile =
        getInstrumentation().getTargetContext().getFilesDir().getPath()
            + File.separatorChar;
    final String fileName = pathToFile + "JUnitTestFileForArchive.txt";
    final String archiveName = pathToFile + "test_archive.zip";
    
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
    config.setUserName( "androidapp" );
    config.setAuthPassword( "md5hallowelt" );
    config.setURL( "http://conferator.org/ht2011/peerradar/restserver/android/pushSensorInformation" );
    
    BasicAuthHttpProtocol protocol =
        new BasicAuthHttpProtocol( getInstrumentation().getContext(), UUID.randomUUID(), config );
    
    // test file transmission to server
    try
    {
      // create test content
      FileUtils.writeToTextFile(
          "This is a simple test file generated for a JUnit upload test",
          fileName );
      
      assertTrue( "Expected file created",
          FileUtils.fileFromPath( fileName ).exists() );
      
      // create archive with test content
      FileCompressor fc = new FileCompressor( new ZipCompressionStrategy() );
      List< String > files = new Vector< String >();
      files.add( fileName );
      fc.compressFiles( files, archiveName );
      
      assertTrue( "Expected archive created",
          FileUtils.fileFromPath( archiveName ).exists() );
      
      boolean success = false;
      try
      {
        protocol.setFileName( archiveName );
        success = protocol.uploadFile();
      }
      finally
      {
        FileUtils.deleteFile( archiveName );
      }
      assertTrue( "Expected upload successful", success );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception: " + e.getMessage() );
    }
    finally
    {
      FileUtils.deleteFile( fileName );
    }
  }
  
}
