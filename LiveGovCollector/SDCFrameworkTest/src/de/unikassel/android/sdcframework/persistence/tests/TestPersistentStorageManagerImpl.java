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
package de.unikassel.android.sdcframework.persistence.tests;

import de.unikassel.android.sdcframework.app.SDCServiceImpl;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.SampleCollection;
import de.unikassel.android.sdcframework.data.tests.TestSampleCollection;
import de.unikassel.android.sdcframework.persistence.DatabaseAdapterImpl;
import de.unikassel.android.sdcframework.persistence.DatabaseManagerImpl;
import de.unikassel.android.sdcframework.persistence.NotificationStrategy;
import de.unikassel.android.sdcframework.persistence.PersistentStorageManagerImpl;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseManager;
import de.unikassel.android.sdcframework.preferences.ServiceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.test.DelegatingMockContext;
import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;
import android.app.NotificationManager;
import android.content.Context;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.LargeTest;

/**
 * Tests for the persistent storage manager impleentation
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestPersistentStorageManagerImpl extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    setContext( new DelegatingMockContext( getContext() ) );
    getContext().deleteDatabase( TestDatabaseAdapter.testDBName );
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    getContext().deleteDatabase( TestDatabaseAdapter.testDBName );
    super.tearDown();
  }
  
  /**
   * Test method for construction, setter and getter.
   */
  public final void testPersistentStorageManagerImpl()
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    config.setMaximumDatabaseSize( 10L );
    config.setDBFullStrategy( DBFullStrategyDescription.WAIT_DELETE_NOTIFY );
    config.setDBFullDeletionRecordCount( 100 );
    config.setDBFullWaitTime( 10L );
    
    DatabaseManager dbManager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    dbManager.setMaximumDatabaseSize( config.getMaximumDatabaseSize() );
    
    try
    {
      new PersistentStorageManagerImpl( null, config, dbManager,
          SDCServiceImpl.class, null );
      fail( "Expected Exception due to context is null" );
    }
    catch ( Exception e )
    {}
    
    try
    {
      new PersistentStorageManagerImpl( getContext(), null, dbManager,
          SDCServiceImpl.class, null );
      fail( "Expected IllegalArgumentException due to configuration parameter is null" );
    }
    catch ( Exception e )
    {}
    
    try
    {
      new PersistentStorageManagerImpl( getContext(), config, null,
          SDCServiceImpl.class, null );
      fail( "Expected IllegalArgumentException due to database manager is null" );
    }
    catch ( Exception e )
    {}
    
    try
    {
      new PersistentStorageManagerImpl( getContext(), config, dbManager, null,
          null );
      fail( "Expected IllegalArgumentException due to class is null" );
    }
    catch ( Exception e )
    {}
    
    try
    {
      PersistentStorageManagerImpl manager =
          new PersistentStorageManagerImpl( getContext(), config, dbManager,
              SDCServiceImpl.class, null );
      assertEquals( "Unexpected sample count in database", 0L,
          manager.getRecordCountInDatabase() );
      assertEquals( "Unexpected saved record count", 0L,
          manager.getSavedRecordCount() );
      assertEquals( "Unexpected maximum database size",
          config.getMaximumDatabaseSize(), manager.getMaximumDatabaseSize() );
      long newSize = config.getMaximumDatabaseSize() << 2;
      assertEquals( "Unexpected maximum database size set",
          newSize, manager.setMaximumDatabaseSize( newSize ) );
      assertEquals( "Unexpected maximum database size",
          newSize, manager.getMaximumDatabaseSize() );
    }
    catch ( Exception e )
    {
      fail( "Unexpected exception from constructor" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.persistence.PersistentStorageManagerImpl#doDeleteOldestSamplesInDatabase(long, boolean)}
   * .
   */
  public final void testDeleteOldestSamplesInDatabase()
  {
    long cntSamples = 50;
    ServiceConfiguration config = new ServiceConfigurationImpl();
    config.setMaximumDatabaseSize( 1024L );
    config.setDBFullStrategy( DBFullStrategyDescription.WAIT_DELETE_NOTIFY );
    config.setDBFullDeletionRecordCount( 2 );
    config.setDBFullWaitTime( 10L );
    
    DatabaseManager dbManager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    dbManager.setMaximumDatabaseSize( config.getMaximumDatabaseSize() );
    PersistentStorageManagerImpl manager =
        new PersistentStorageManagerImpl( getContext(), config, dbManager,
            SDCServiceImpl.class, null );
    
    // insert samples
    long cntInserted =
        TestDatabaseManagerImpl.insertSamplesIntoDatabase( getContext(),
            cntSamples, 0L /* size 0 will be ignored */);
    
    assertEquals( "Expected all samples inserted", cntSamples, cntInserted );
    
    assertEquals( "Unexpected sample count in database", cntInserted,
        manager.getRecordCountInDatabase() );
    
    // tests deletion of samples
    long cntDeleted =
        manager.doDeleteOldestSamplesInDatabase( cntInserted >> 2, false );
    assertEquals( "Unexpected sample count deleted", cntInserted >> 2,
        cntDeleted );
    
    long cntRemaining = cntInserted - cntDeleted;
    assertEquals( "Unexpected sample count in database", cntRemaining,
        manager.getRecordCountInDatabase() );
    
    cntDeleted = manager.doDeleteOldestSamplesInDatabase( cntInserted, false );
    assertEquals( "Unexpected sample count deleted", cntRemaining, cntDeleted );
    assertEquals( "Unexpected sample count in database", 0L,
        manager.getRecordCountInDatabase() );
  }
  
  /**
   * Test method for work flow and sample processing
   */
  @LargeTest
  public final void testWorkflow()
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    
    int count = 10;
    config.setMaximumDatabaseSize( (long) count );
    config.setDBFullStrategy( DBFullStrategyDescription.WAIT_DELETE_NOTIFY );
    config.setDBFullDeletionRecordCount( count << 2 );
    config.setDBFullWaitTime( 0L );
    
    // we do start with a full database to test the work flow with a deletion
    // strategy
    fillDatabaseUpToLimit( config );
    
    DatabaseManager dbManager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    dbManager.setMaximumDatabaseSize( config.getMaximumDatabaseSize() );
    
    PersistentStorageManagerImpl manager =
        new PersistentStorageManagerImpl( getContext(), config, dbManager,
            SDCServiceImpl.class, null );
    
    // create an event source for the storage manager and connect both
    ObservableEventSourceImpl< Sample > eventSource =
        new ObservableEventSourceImpl< Sample >();
    eventSource.registerEventObserver( manager.getObserver() );
    
    // generate samples to store by source
    SampleCollection sc = TestSampleCollection.createSamples( count );
    for ( Sample sample : sc )
    {
      eventSource.notify( sample );
    }
    
    // trigger persistent storage manager to work
    manager.onCreate( getContext() );
    assertFalse( "Expected manager not working", manager.isWorking() );
    manager.onResume( getContext() );
    assertTrue( "Expected manager working", manager.isWorking() );
    
    for ( int i = 0; i < count; ++i )
    {
      if ( manager.getSavedRecordCount() == count )
        break;
      TestUtils.sleep( 500 );
    }
    
    assertTrue( "Expected manager still working", manager.isWorking() );
    
    manager.onPause( getContext() );
    assertFalse( "Expected manager not working", manager.isWorking() );
    assertEquals( "Unexpected saved record count", count,
        manager.getSavedRecordCount() );
    
    manager.onDestroy( getContext() );
  }
  
  /**
   * Test method for database size limit exceeded.
   */
  @LargeTest
  public final void testDatabaseSizeExceeded()
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    config.setMaximumDatabaseSize( 5L );
    config.setDBFullStrategy( DBFullStrategyDescription.WAIT_DELETE_NOTIFY );
    config.setDBFullDeletionRecordCount( 0 );
    config.setDBFullWaitTime( 0L );
    
    fillDatabaseUpToLimit( config );
    
    DatabaseManager dbManager =
        new DatabaseManagerImpl( getContext(), TestDatabaseAdapter.testDBName );
    dbManager.setMaximumDatabaseSize( config.getMaximumDatabaseSize() );
    PersistentStorageManagerImpl manager =
        new PersistentStorageManagerImpl( getContext(), config, dbManager,
            SDCServiceImpl.class, null );
    
    // create an event source for the storage manager and connect both
    ObservableEventSourceImpl< Sample > eventSource =
        new ObservableEventSourceImpl< Sample >();
    eventSource.registerEventObserver( manager.getObserver() );
    
    // trigger persistent storage manager to work
    manager.onCreate( getContext() );
    manager.onResume( getContext() );
    TestUtils.sleep( 1000 );
    
    assertTrue( "Expected manager working", manager.isWorking() );
    
    // create samples in source
    SampleCollection sc = TestSampleCollection.createSamples( 200 );
    for ( Sample sample : sc )
    {
      eventSource.notify( sample );
      TestUtils.sleep( 100 );
      if ( !manager.isWorking() )
        break;
    }
    
    TestUtils.sleep( 1000 );
    boolean isWorking = manager.isWorking();
    
    manager.onPause( getContext() );
    manager.onDestroy( getContext() );
    
    assertFalse(
        "Expected manager has stopped work due to database full strategy failed",
        isWorking );
    
    NotificationManager notificationManager =
        (NotificationManager) getContext().getSystemService(
            Context.NOTIFICATION_SERVICE );
    
    notificationManager.cancel( NotificationStrategy.NOTIFICATION );
  }
  
  /**
   * Does fill the database with samples that next call with >= 10 samples to
   * insert would fail.
   * 
   * @param config
   *          the service configuration
   */
  private void fillDatabaseUpToLimit( ServiceConfiguration config )
  {
    // prepare database to be full
    DatabaseAdapterImpl dbAdapter =
        new DatabaseAdapterImpl( TestDatabaseAdapter.testDBName,
            config.getMaximumDatabaseSize(),
            getContext() );
    boolean success = true;
    while ( success )
    {
      dbAdapter.open();
      
      // insert test data and store record count
      try
      {
        dbAdapter.insertSamples( TestDatabaseAdapter.createTestData( 10 ) );
      }
      catch ( Exception e )
      {
        success = false;
      }
      finally
      {
        dbAdapter.close();
      }
    }
  }
}
