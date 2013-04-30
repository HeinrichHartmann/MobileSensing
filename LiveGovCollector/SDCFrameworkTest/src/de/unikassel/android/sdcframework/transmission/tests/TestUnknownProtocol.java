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

import de.unikassel.android.sdcframework.preferences.TransmissionProtocolConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.AbstractProtocol;
import de.unikassel.android.sdcframework.transmission.UnknownProtocol;
import de.unikassel.android.sdcframework.util.FileUtils;
import android.test.AndroidTestCase;

/**
 * @author Katy Hilgenberg
 * 
 */
public class TestUnknownProtocol extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    setContext( TestFileManager.getMockContext( getContext() ) );
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    TestFileManager.doCleanUpFiles( getContext() );
    super.tearDown();
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.UnknownProtocol#UnknownProtocol(android.content.Context, UUID, TransmissionProtocolConfiguration)}
   * .
   */
  public final void testUnknownProtocol()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
    config.setUserName( "user" );
    config.setAuthPassword( "dummy" );
    config.setURL( "http://localhost:8080" );
    
    UnknownProtocol protocol = new UnknownProtocol( getContext(), UUID.randomUUID(), config );
    assertSame( "Expected context set", getContext(), protocol.getContext() );
    assertNull( "Expected file name null", protocol.getFileName() );
    assertNull( "Expected last error null", protocol.getLastError() );
    assertNotNull( "Expected url not null", protocol.getURL() );
    assertEquals( "Expected url equal", config.getURL(),
        protocol.getURL().toString() );
    
    String fileName = "testfilename.ext";
    protocol.setFileName( fileName );
    assertEquals( "Expected file name set", fileName, protocol.getFileName() );
    
    String lastError = "An error text";
    protocol.setLastError( lastError );
    assertEquals( "Expected last error set", lastError, protocol.getLastError() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.UnknownProtocol#uploadFile()}
   * .
   */
  public final void testDoUploadFromStream()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
    config.setUserName( "" );
    config.setAuthPassword( "" );
    config.setURL( "ftp://localhost" );
    
    UnknownProtocol protocol = new UnknownProtocol( getContext(), UUID.randomUUID(), config );
    
    // test for valid url, valid filename but invalid protocol
    String fileName = 
      getContext().getFilesDir().getPath() + File.separatorChar + "dummy.jar";
    FileUtils.writeToTextFile( "dummy archive", fileName );
    protocol.setFileName( fileName );
    
    // expected false in any case even if file exists
    assertFalse( "Expected to fail always", protocol.uploadFile() );
    assertNotNull( "Expected last error not null", protocol.getLastError() );
    assertTrue( "Expected error set", protocol.getLastError().contains(
        AbstractProtocol.UNKNOWN_PROTCOL ) );
  }
  
}
