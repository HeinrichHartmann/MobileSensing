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

import de.unikassel.android.sdcframework.preferences.TransmissionConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.test.ConnectivityWrapperForTest;
import de.unikassel.android.sdcframework.transmission.FailSafeProtocol;
import de.unikassel.android.sdcframework.transmission.BasicAuthHttpProtocol;
import de.unikassel.android.sdcframework.transmission.MobileConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.UnknownProtocol;
import de.unikassel.android.sdcframework.transmission.UploadManager;
import de.unikassel.android.sdcframework.transmission.UseAvailableConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.WLANConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;
import android.content.Context;
import android.test.AndroidTestCase;

/**
 * @author Katy Hilgenberg
 * 
 */
public class TestUploadManager extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    // create a context redirecting the files directory
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
    // clean up temporary files
    TestFileManager.doCleanUpFiles( getContext() );
    super.tearDown();
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.UploadManager#UploadManager(Context, TransmissionConfiguration, UUID, Class)}
   * .
   */
  public final void testUploadManager()
  {
    
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( ArchiveTypes.jar );
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.any_available );
    config.getProtocolConfiguration().setAuthPassword( "secret" );
    config.getProtocolConfiguration().setUserName( "dummy" );
    config.getProtocolConfiguration().setURL( "http://localhost" );
    
    // test for http protocol
    try
    {
      UploadManager manager =
          new UploadManager( getContext(), config, UUID.randomUUID(), null );
      
      ProtocolStrategy protocol = manager.getProtocolStrategy();
      assertNotNull( "Expected protocol created", protocol );
      assertTrue( "Expected protocol is http",
          protocol instanceof BasicAuthHttpProtocol );
      assertSame( "Expected context set", getContext(), protocol.getContext() );
      assertNull( "Expected file name null", protocol.getFileName() );
      assertNull( "Expected last error null", protocol.getLastError() );
      assertNotNull( "Expected url not null", protocol.getURL() );
      assertEquals( "Expected url equal",
          config.getProtocolConfiguration().getURL(),
          protocol.getURL().toString() );
      
      ConnectionStrategy connection = manager.getConnectionStrategy();
      assertNotNull( "Expected connection strategy created", connection );
      assertTrue( "Expected connection strategy of correct type",
          connection instanceof UseAvailableConnectionStrategy );
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception" );
    }
    
    // test for unknown protocol
    config.getProtocolConfiguration().setURL( "localhost" );
    try
    {
      UploadManager manager =
          new UploadManager( getContext(), config, UUID.randomUUID(), null );
      
      ProtocolStrategy protocol = manager.getProtocolStrategy();
      assertNotNull( "Expected protocol created", protocol );
      assertTrue( "Expected protocol is unknown",
          protocol instanceof UnknownProtocol );
      assertSame( "Expected context set", getContext(), protocol.getContext() );
      assertNull( "Expected file name null", protocol.getFileName() );
      assertNull( "Expected last error null", protocol.getLastError() );
      assertNull( "Expected url null", protocol.getURL() );
      
      ConnectionStrategy connection = manager.getConnectionStrategy();
      assertNotNull( "Expected connection strategy created", connection );
      assertTrue( "Expected connection strategy of correct type",
          connection instanceof UseAvailableConnectionStrategy );
      assertNull( "Expected connection strategy without successor",
          connection.getSuccessor() );
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.UploadManager#updateConfiguration(android.content.Context, de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration)}
   * .
   */
  public final void testUpdateConfiguration()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( ArchiveTypes.jar );
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.any_available );
    config.getProtocolConfiguration().setUserName( "" );
    config.getProtocolConfiguration().setAuthPassword( "" );
    config.getProtocolConfiguration().setURL( "" );
    
    UploadManager manager =
        new UploadManager( getContext(), config, UUID.randomUUID(), null );
    
    ProtocolStrategy protocol = manager.getProtocolStrategy();
    assertTrue( "Expected protocol is unknown",
        protocol instanceof UnknownProtocol );
    assertNull( "Expected url is null", protocol.getURL() );
    ConnectionStrategy connection = manager.getConnectionStrategy();
    assertNotNull( "Expected connection strategy created", connection );
    assertTrue( "Expected connection strategy of correct type",
        connection instanceof UseAvailableConnectionStrategy );
    assertNull( "Expected connection strategy without successor",
        connection.getSuccessor() );
    
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.wlan_else_mobile );
    config.getProtocolConfiguration().setAuthPassword( "secret" );
    config.getProtocolConfiguration().setUserName( "dummy" );
    config.getProtocolConfiguration().setURL( "http://localhost" );
    
    // test update configuration
    manager.updateConfiguration( getContext(), config );
    
    protocol = manager.getProtocolStrategy();
    assertTrue( "Expected protocol is http",
        protocol instanceof BasicAuthHttpProtocol );
    assertNotNull( "Expected url not null", protocol.getURL() );
    assertEquals( "Expected url equal",
        config.getProtocolConfiguration().getURL(),
        protocol.getURL().toString() );
    
    connection = manager.getConnectionStrategy();
    assertNotNull( "Expected connection strategy created", connection );
    assertTrue( "Expected connection strategy of correct type",
        connection instanceof WLANConnectionStrategy );
    assertNotNull( "Expected connection strategy with successor",
        connection.getSuccessor() );
    assertTrue( "Expected successor of correct type",
        connection.getSuccessor() instanceof MobileConnectionStrategy );
  }
  
  /**
   * Test class extending UploadManager with a setter for the connection and the
   * protocol strategy to override both for test purpose.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class UploadManagerForTest
      extends UploadManager
  {
    
    /**
     * Constructor
     * 
     * @param applicationContext
     *          the application context
     * @param config
     *          the current transmission configuration
     */
    public UploadManagerForTest( Context applicationContext,
        TransmissionConfiguration config )
    {
      super( applicationContext, config, UUID.randomUUID(), null );
    }
    
    /**
     * Setter for the protocol
     * 
     * @param protocolStrategy
     *          the protocol to set
     */
    public void setProtocol( ProtocolStrategy protocolStrategy )
    {
      this.protocolStrategy = protocolStrategy;
    }
    
    /**
     * Setter for the connection strategy
     * 
     * @param connectionStrategy
     *          the connection strategy
     */
    public void setConnectionStrategy( ConnectionStrategy connectionStrategy )
    {
      this.connectionStrategy = connectionStrategy;
    }
    
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.UploadManager#uploadFile(java.lang.String)}
   * .
   */
  public final void testUploadFile()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.any_available );
    config.getProtocolConfiguration().setAuthPassword( "secret" );
    config.getProtocolConfiguration().setUserName( "dummy" );
    config.getProtocolConfiguration().setURL( "localhost" );
    
    ConnectivityWrapperForTest connectivityWrapper =
        new ConnectivityWrapperForTest();
    connectivityWrapper.isHostReachable = true;
    connectivityWrapper.isNetworkConnected = true;
    
    UploadManagerForTest manager =
        new UploadManagerForTest( getContext(), config );
    // let manager use a fail safe test protocol
    manager.setProtocol( new FailSafeProtocol( getContext() ) );
    manager.setConnectionStrategy( new UseAvailableConnectionStrategy(
        connectivityWrapper, null ) );
    
    // test for invalid filename
    String fileName = "./dummy.ext";
    assertFalse( "Expected upload fails", manager.uploadFile( fileName ) );
    assertNull( "Expected filename null in protocol",
        manager.getProtocolStrategy().getFileName() );
    assertNull( "Expected last error null in protocol",
        manager.getProtocolStrategy().getLastError() );
    
    // test for valid filename
    fileName =
        getContext().getFilesDir().getPath() + File.separatorChar + "dummy.jar";
    FileUtils.writeToTextFile( "dummy archive", fileName );
    assertTrue( "Expected upload succeded", manager.uploadFile( fileName ) );
    assertNull( "Expected filename null in protocol",
        manager.getProtocolStrategy().getFileName() );
    assertNull( "Expected last error null in protocol",
        manager.getProtocolStrategy().getLastError() );
    
    // test for valid filename but connection unavailable
    connectivityWrapper.isNetworkConnected = false;
    assertFalse( "Expected upload fails", manager.uploadFile( fileName ) );
    assertNull( "Expected filename null in protocol",
        manager.getProtocolStrategy().getFileName() );
    assertNull( "Expected last error null in protocol",
        manager.getProtocolStrategy().getLastError() );
  }
  
}
