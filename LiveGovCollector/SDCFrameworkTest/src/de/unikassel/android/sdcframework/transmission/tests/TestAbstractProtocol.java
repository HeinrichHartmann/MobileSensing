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
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.facade.Encryption;
import android.content.Context;
import android.test.AndroidTestCase;

/**
 * @author Katy Hilgenberg
 * 
 */
public class TestAbstractProtocol extends AndroidTestCase
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
   * Test protocol implementation
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class TestProtocol extends AbstractProtocol
  {
    /**
     * Flag indication if doUploadFromStream method was called
     */
    public boolean doUploadFromStreamWasCalled = false;
    
    /**
     * Constructor
     * 
     * @param context
     *          the context
     * @param uuid
     *          the unique device identifier
     * @param config
     *          the configuration
     */
    public TestProtocol( Context context, UUID uuid,
        TransmissionProtocolConfiguration config )
    {
      super( context, uuid, config );
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see de.unikassel.android.sdcframework.transmission.AbstractProtocol#
     * doUploadFromStream(java.io.File, java.lang.String)
     */
    @Override
    public boolean doUploadFile( File file )
    {
      doUploadFromStreamWasCalled = true;
      return doUploadFromStreamWasCalled;
    }
    
    /**
     * Getter for the internal user name
     * 
     * @return the internal user name
     */
    public String getHiddenUserName()
    {
      return getUserName();
    }
    
    /**
     * Getter for the internal password
     * 
     * @return the internal password
     */
    public String getHiddenPassword()
    {
      return getMd5Password();
    }
    
  }
  
  /**
   * Test method for construction, setter and getter
   */
  public final void testAbstractProtocol()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
    config.setUserName( "user" );
    config.setAuthPassword( "dummy" );
    config.setURL( "http://localhost:8080" );
    
    TestProtocol protocol = new TestProtocol( getContext(), UUID.randomUUID(), config );
    assertSame( "Expected context set", getContext(), protocol.getContext() );
    assertEquals( "Expected user name set", config.getUserName(),
        protocol.getHiddenUserName() );
    assertEquals( "Expected password set",
        Encryption.md5( config.getAuthPassword() ),
        protocol.getHiddenPassword() );
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
   * {@link de.unikassel.android.sdcframework.transmission.AbstractProtocol#updateConfiguration(Context, TransmissionProtocolConfiguration)}
   * .
   */
  public final void testUpdateConfiguration()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
    config.setUserName( "" );
    config.setAuthPassword( "" );
    config.setURL( "" );
    
    TestProtocol protocol = new TestProtocol( getContext(), UUID.randomUUID(), config );
    
    config.setUserName( "user" );
    config.setAuthPassword( "dummy" );
    config.setURL( "http://localhost:8080" );
    
    protocol.updateConfiguration( getContext(), config );
    
    assertEquals( "Expected user name set", config.getUserName(),
        protocol.getHiddenUserName() );
    assertEquals( "Expected password set",
        Encryption.md5( config.getAuthPassword() ),
        protocol.getHiddenPassword() );
    assertNotNull( "Expected url not null", protocol.getURL() );
    assertEquals( "Expected url equal", config.getURL(),
        protocol.getURL().toString() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.AbstractProtocol#uploadFile()}
   * .
   */
  public final void testUploadFile()
  {
    TransmissionProtocolConfiguration config = new TransmissionProtocolConfigurationImpl();
    config.setUserName( "" );
    config.setAuthPassword( "" );
    config.setURL( "" );
    
    TestProtocol protocol = new TestProtocol( getContext(), UUID.randomUUID(), config );
    
    // test for filename null
    protocol.uploadFile();
    
    assertNull( "Expected last error null", protocol.getLastError() );
    assertFalse( "Expected doUploadFromStream not called",
        protocol.doUploadFromStreamWasCalled );
    
    // test for url null
    String fileName = "dummy.ext";
    protocol.setFileName( fileName );
    protocol.uploadFile();
    
    assertNotNull( "Expected last error not null", protocol.getLastError() );
    assertEquals( "Expected error set", AbstractProtocol.INVALID_URL,
        protocol.getLastError() );
    assertFalse( "Expected doUploadFromStream not called",
        protocol.doUploadFromStreamWasCalled );
    
    // test with invalid file name
    config.setURL( "http://localhost" );
    protocol.updateConfiguration( getContext(), config );
    protocol.uploadFile();
    
    assertNotNull( "Expected last error not null", protocol.getLastError() );
    assertTrue( "Expected error set", protocol.getLastError().contains(
        AbstractProtocol.FILE_NOT_FOUND ) );
    assertTrue( "Expected error set", protocol.getLastError().contains(
        fileName ) );
    assertFalse( "Expected doUploadFromStream not called",
        protocol.doUploadFromStreamWasCalled );
    
    // test for valid filename
    fileName =
        getContext().getFilesDir().getPath() + File.separatorChar + "dummy.jar";
    FileUtils.writeToTextFile( "dummy archive", fileName );
    protocol.setFileName( fileName );
    protocol.uploadFile();
    
    assertNull( "Expected last error null", protocol.getLastError() );
    assertTrue( "Expected doUploadFromStream was called",
        protocol.doUploadFromStreamWasCalled );
    
  }
  
}
