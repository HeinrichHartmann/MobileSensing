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

import de.unikassel.android.sdcframework.persistence.DatabaseManagerImpl;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseManager;
import de.unikassel.android.sdcframework.persistence.tests.TestDatabaseAdapter;
import de.unikassel.android.sdcframework.persistence.tests.TestDatabaseManagerImpl;
import de.unikassel.android.sdcframework.preferences.TransmissionConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.transmission.FileManager;
import de.unikassel.android.sdcframework.transmission.TransferManagerImpl;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;
import android.os.SystemClock;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

/**
 * Tests for the transfer manager.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestTransferManagerImpl extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    TestFileManager.doCleanUpFiles( getContext() );
    
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
   * Test method for creation.
   */
  @LargeTest
  public final void testTransferManagerImpl()
  {
    UUID randomUUID = UUID.randomUUID();
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( ArchiveTypes.zip );
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.any_available );
    config.setMaxSampleTransferCount( 10 );
    config.setMinSampleTransferCount( 1 );
    config.setMinTransferFrequency( 1800L );
    config.getProtocolConfiguration().setAuthPassword( "" );
    config.getProtocolConfiguration().setUserName( "dummy" );
    config.getProtocolConfiguration().setURL( "localhost" );
    DatabaseManager dbManager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    
    // test creation with invalid parameters
    try
    {
      new TransferManagerImpl( null, config, dbManager, randomUUID, null );
      fail( "Expected invalid parameter exception" );
    }
    catch ( Exception e )
    {}
    
    try
    {
      
      new TransferManagerImpl( getContext(), null, dbManager, randomUUID, null );
      fail( "Expected invalid parameter exception" );
    }
    catch ( Exception e )
    {}
    
    try
    {
      
      new TransferManagerImpl( getContext(), config, null, randomUUID, null );
      fail( "Expected invalid parameter exception" );
    }
    catch ( Exception e )
    {}
    
    // test for empty configuration
    try
    {
      new TransferManagerImpl( getContext(),
          new TransmissionConfigurationImpl(), dbManager, randomUUID, null );
      fail( "Expected exception" );
    }
    catch ( Exception e )
    {}
    
    // test creation in clean environment
    try
    {
      TransferManagerImpl manager =
          new TransferManagerImpl( getContext(), config, dbManager, randomUUID, null );
      
      assertEquals( "Unexpected timestamp value", 0L,
          manager.getTimeStamp() );
      assertEquals( "Unexpected state value", 0L, manager.getCurrentState() );
      assertEquals( "Unexpected maimum for samples",
          config.getMaxSampleTransferCount(), manager.getMaxSampleCount() );
      assertEquals( "Unexpected mininum for samples",
          config.getMinSampleTransferCount(), manager.getMinSampleCount() );
      assertEquals( "Unexpected minimum for frequency",
          Math.max( 60L, config.getMinTransferFrequency() ),
          manager.getMinFrequency() );
      
      manager.onCreate( getContext() );
      assertEquals( "Unexpected state value", TransferManagerImpl.INIT,
          manager.getCurrentState() );
      
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception" );
    }
    
    // test creation with existing archive
    try
    {
      String testFileDir =
          getContext().getFilesDir().getPath() + File.separatorChar;
      String tmpDir =
          testFileDir + FileManager.TEMP_DIR_NAME + File.separatorChar;
      String archiveFile = tmpDir + FileManager.ARCHIVE_FILE + ".jar";
      FileUtils.writeToTextFile( "", archiveFile );
      
      // we do test for limitation of lower frequency here as well
      config.setMinTransferFrequency( 1L );
      TransferManagerImpl manager =
          new TransferManagerImpl( getContext(), config, dbManager, randomUUID, null );
      
      assertEquals( "Unexpected timestamp value", 0L,
          manager.getTimeStamp() );
      int currentState = manager.getCurrentState();
      assertEquals( "Unexpected state value", 0L, currentState );
      assertEquals( "Unexpected maimum for samples",
          config.getMaxSampleTransferCount(), manager.getMaxSampleCount() );
      assertEquals( "Unexpected mininum for samples",
          config.getMinSampleTransferCount(), manager.getMinSampleCount() );
      assertEquals( "Unexpected minimum for frequency",
          Math.max( TransferManagerImpl.MIN_FREQUENCY,
              config.getMinTransferFrequency() ),
          manager.getMinFrequency() );
      
      manager.onCreate( getContext() );
      currentState = manager.getCurrentState();
      assertEquals( "Unexpected state value", TransferManagerImpl.INIT,
          currentState );
      
      manager.onResume( getContext() );      
      do
      {
        TestUtils.sleep( 100 );
        currentState = manager.getCurrentState();
      }
      while ( currentState == TransferManagerImpl.INIT );
      TestUtils.sleep( 100 );
      
      assertEquals( "Unexpected state value", TransferManagerImpl.TRANSMISSION,
          manager.getCurrentState() );
      
      manager.onPause( getContext() );
      manager.onDestroy( getContext() );      
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.transmission.TransferManagerImpl#updateConfiguration(android.content.Context, de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration)}
   * .
   */
  public final void testUpdateConfiguration()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( ArchiveTypes.zip );
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.any_available );
    config.setMaxSampleTransferCount( 10 );
    config.setMinSampleTransferCount( 1 );
    config.setMinTransferFrequency( 1800L );
    config.getProtocolConfiguration().setAuthPassword( "" );
    config.getProtocolConfiguration().setUserName( "dummy" );
    config.getProtocolConfiguration().setURL( "localhost" );
    
    DatabaseManager dbManager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    TransferManagerImpl manager =
        new TransferManagerImpl( getContext(), config, dbManager,
            UUID.randomUUID(), null );
    
    TransmissionConfiguration updateConfig =
        new TransmissionConfigurationImpl();
    updateConfig.update( config );
    updateConfig.setMinSampleTransferCount( 100 );
    updateConfig.setMaxSampleTransferCount( 500 );
    updateConfig.setMinTransferFrequency( 60L );
    
    manager.updateConfiguration( getContext(), updateConfig );
    assertEquals( "Unexpected maimum for samples",
        updateConfig.getMaxSampleTransferCount(), manager.getMaxSampleCount() );
    assertEquals( "Unexpected mininum for samples",
        updateConfig.getMinSampleTransferCount(), manager.getMinSampleCount() );
    assertEquals( "Unexpected minimum for frequency",
        Math.max( 60L, updateConfig.getMinTransferFrequency() ),
        manager.getMinFrequency() );
    
    // test for update with minimum frequency above maximum value
    updateConfig.setMinSampleTransferCount( 500 );
    updateConfig.setMaxSampleTransferCount( 100 );
    
    manager.updateConfiguration( getContext(), updateConfig );
    assertEquals( "Unexpected maimum for samples",
        updateConfig.getMinSampleTransferCount(), manager.getMaxSampleCount() );
    assertEquals( "Unexpected mininum for samples",
        updateConfig.getMinSampleTransferCount(), manager.getMinSampleCount() );
    assertEquals( "Unexpected minimum for frequency",
        Math.max( 60L, updateConfig.getMinTransferFrequency() ),
        manager.getMinFrequency() );
  }
  
  /**
   * Test method for the life cycle
   */
  @LargeTest
  public final void testLifeCycle()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( ArchiveTypes.jar );
    config.getProtocolConfiguration().setTransmissionStrategy(
        ConnectionStrategyDescription.any_available );
    config.setMaxSampleTransferCount( 10 );
    config.setMinSampleTransferCount( 2 );
    config.setMinTransferFrequency( 1L );
    config.getProtocolConfiguration().setAuthPassword( "" );
    config.getProtocolConfiguration().setUserName( "dummy" );
    // use unknown protocol here with fail
    // always strategy
    config.getProtocolConfiguration().setURL( "localhost" );
    
    DatabaseManager dbManager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    
    // fill database
    long cnt = config.getMinSampleTransferCount() + 1;
    TestDatabaseManagerImpl.insertSamplesIntoDatabase( getContext(), cnt, -1L );
    assertEquals( "Expected samples in database", cnt,
        dbManager.getRecordCountInDatabase() );
    
    TransferManagerImpl manager =
        new TransferManagerImpl( getContext(), config, dbManager,
            UUID.randomUUID(), null );
    
    long frequency = manager.getMinFrequency() * 1000;
    long ts = SystemClock.uptimeMillis() - frequency;
    
    // start life cycle and tests
    manager.onCreate( getContext() );
    manager.onResume( getContext() );
    
    assertTrue( "Unexpected timestamp value",
        manager.getTimeStamp() >= ts );
    assertTrue( "Expected manager working", manager.isWorking() );
    
    while ( manager.getCurrentState() == TransferManagerImpl.INIT )
    {
      TestUtils.sleep( 10 );
    }
    assertEquals( "Unexpected state value",
        TransferManagerImpl.COLLECTING, manager.getCurrentState() );
    while ( manager.getCurrentState() == TransferManagerImpl.COLLECTING )
    {
      TestUtils.sleep( 100 );
    }
    assertTrue( "Unexpected state value",
        manager.getCurrentState() != TransferManagerImpl.COLLECTING );
    TestUtils.sleep( 2000L );
    
    // test successful collection of samples and preparation of archive
    String archiveFile = manager.getCurrentArchiveFileName();
    assertNotNull( "Expected archive file set", archiveFile );
    assertTrue( "Expected archive file created", FileUtils.fileFromPath(
        archiveFile ).exists() );
    assertTrue( "Unexpected state value",
        manager.getCurrentState() == TransferManagerImpl.TRANSMISSION );
    cnt =
        config.getMaxSampleTransferCount() - config.getMaxSampleTransferCount();
    assertEquals( "Unexpected sample count in database", cnt,
        dbManager.getRecordCountInDatabase() );
    
    // transfer to remote wont be tested here
    
    manager.onPause( getContext() );
    assertFalse( "Expected manager working", manager.isWorking() );
    assertFalse( "Expected manager not terminated", manager.hasTerminated() );
    
    manager.onDestroy( getContext() );
    TestUtils.sleep( 1000 );
    assertTrue( "Expected manager terminated", manager.hasTerminated() );
  }
  
}
