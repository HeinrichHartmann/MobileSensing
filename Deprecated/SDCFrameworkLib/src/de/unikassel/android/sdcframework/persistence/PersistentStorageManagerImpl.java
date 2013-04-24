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
package de.unikassel.android.sdcframework.persistence;

import java.util.Collection;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicLong;

import android.content.Context;
import android.database.sqlite.SQLiteFullException;
import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.SampleCollection;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseCommand;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseFullStrategy;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseManager;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseSample;
import de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.util.AbstractAsynchrounousSampleObserver;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * The persistent storage manager does provide the persistent storage feature
 * for the framework. <br/>
 * <br/>
 * It does extend the {@linkplain AbstractAsynchrounousSampleObserver} to cache
 * observed samples for further asynchronous processing.
 * 
 * @see de.unikassel.android.sdcframework.service.ServiceManagerImpl
 * @author Katy Hilgenberg
 * 
 */
public final class PersistentStorageManagerImpl
    extends AbstractAsynchrounousSampleObserver
    implements PersistentStorageManager
{
  /**
   * The actual count of records stored to database in the actual work period
   */
  private final AtomicLong savedRecordCount;
  
  /**
   * The database manager used for database access
   */
  private final DatabaseManager dbManager;
  
  /**
   * The strategy to use in case of SQLiteFullException
   */
  private DatabaseFullStrategy dbFullStrategy;
  
  /**
   * The last not finished database command
   */
  private InsertSamplesCommand currentCommand;
  
  /**
   * The service class
   */
  private final Class< ? extends SDCService > serviceClass;
  
  /**
   * Constructor
   * 
   * @param applicationContext
   *          the application context
   * @param config
   *          the service configuration
   * @param dbManager
   *          the database manager to use
   * @param serviceClass
   *          the service class
   */
  public PersistentStorageManagerImpl( Context applicationContext,
      ServiceConfiguration config, DatabaseManager dbManager,
      Class< ? extends SDCService > serviceClass )
  {
    super();
    if ( applicationContext == null )
      throw new IllegalArgumentException( "context is null" );
    if ( dbManager == null )
      throw new IllegalArgumentException( "dbManager is null" );
    if ( config == null )
      throw new IllegalArgumentException( "config is null" );
    if ( serviceClass == null )
      throw new IllegalArgumentException( "serviceClass is null" );
    this.serviceClass = serviceClass;
    this.dbManager = dbManager;
    this.savedRecordCount = new AtomicLong( 0L );
    
    updateDatabaseFullStrategy( applicationContext, config );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager
   * #updateDatabaseFullStrategy(android.content.Context,
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration)
   */
  @Override
  public final synchronized void updateDatabaseFullStrategy( Context context,
      ServiceConfiguration config )
  {
    this.dbFullStrategy = DatabaseFullStrategyBuilder.buildStrategy(
        context, config, serviceClass );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.AbstractWorkerThread#doWork()
   */
  @Override
  protected final void doWork()
  {
    try
    {
      if ( currentCommand == null )
      {
        // take samples from queue and process
        SampleCollection samples = new SampleCollection();
        if ( collector.dequeue( samples, Integer.MAX_VALUE ) > 0 )
        {
          currentCommand =
              new InsertSamplesCommand( convertSamplesToDBSamples( samples ) );
        }
        else
          sleep( 1000 );
      }
      
      // try to process last insert command
      doExecuteCurrentCommand();
    }
    catch ( SQLiteFullException e )
    {
      doHandleDatabaseSizeLimitExceeded();
    }
    catch ( InterruptedException e )
    {}
    catch ( Exception e )
    {
      Logger.getInstance().error( this,
          "Exception in doWork: " + e.getMessage() );
      e.printStackTrace();
    }
  }
  
  /**
   * Conversion of samples to database samples
   * 
   * @param samples
   *          the sample collection to convert to a database sample collection
   * @return a collection of the converted samples
   */
  private final Collection< DatabaseSample > convertSamplesToDBSamples(
      SampleCollection samples )
  {
    Collection< DatabaseSample > dbSamples =
        new Vector< DatabaseSample >();
    for ( Sample sample : samples )
    {
      try
      {
        dbSamples.add( new DatabaseSample( sample ) );
      }
      catch ( Exception e )
      {
        Logger.getInstance().warning( this,
            "failed to convert sample to database format: " + e.getMessage() );
        e.printStackTrace();
      }
    }
    return dbSamples;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager
   * #doExecuteCurrentCommand()
   */
  @Override
  public synchronized final boolean doExecuteCurrentCommand()
  {
    if ( currentCommand == null )
      return true;
    
    Boolean success = doExecuteCommand( currentCommand );
    if ( success != null )
    {
      if ( success )
      {
        // update current instance count of saved records
        long cnt = currentCommand.getSamples().size();
        long recordCount;
        do
        {
          recordCount = savedRecordCount.get();
        }
        while ( !savedRecordCount.compareAndSet( recordCount, recordCount + cnt ) );
        
        currentCommand = null;
      }
      return success;
    }
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager
   * #deleteOldestSamplesInDatabase(long, boolean)
   */
  @Override
  public synchronized final long doDeleteOldestSamplesInDatabase( long count,
      boolean lowestPriorityFirst )
  {
    return dbManager.doDeleteOldestSamplesInDatabase( count,
        lowestPriorityFirst );
  }
  
  /**
   * Handler for the SQL database full exception
   */
  private synchronized final void doHandleDatabaseSizeLimitExceeded()
  {
    Logger.getInstance().debug( this,
        "Total record count in database: " + getRecordCountInDatabase() );
    Logger.getInstance().error( this,
        "Database size limit exceeded. Applying strategy!" );
    
    if ( !dbFullStrategy.doWork( this ) )
    {
      Logger.getInstance().error( this,
          "Database full can not be resolved. Stopping work!" );
      stopWork();
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onResume(
   * android.content.Context)
   */
  @Override
  public final void onResume( Context applicationContext )
  {
    savedRecordCount.set( 0L );
    
    Logger.getInstance().debug( this,
        "Total record count in database: " + getRecordCountInDatabase() );
    
    super.onResume( applicationContext );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onPause(android
   * .content.Context)
   */
  @Override
  public final void onPause( Context applicationContext )
  {
    super.onPause( applicationContext );
    
    int eventCount = collector.getEventCount();
    if ( eventCount > 0 )
    {
      
      Logger.getInstance().warning( this,
          "" + eventCount + " unsaved samples in queue!" );
    }
    
    long savedRecordCount = getSavedRecordCount();
    if ( savedRecordCount > 0L )
    {
      Logger.getInstance().debug( this,
          "" + savedRecordCount + " sample(s) stored persistent!" );
    }
    Logger.getInstance().debug( this,
        "Total record count in database: " + getRecordCountInDatabase() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager
   * #getSavedRecordCount()
   */
  @Override
  public final long getSavedRecordCount()
  {
    return savedRecordCount.get();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * getSampleCountInDatabase()
   */
  @Override
  public synchronized final long getRecordCountInDatabase()
  {
    return dbManager.getRecordCountInDatabase();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * getMaximumDatabaseSize()
   */
  @Override
  public synchronized final long getMaximumDatabaseSize()
  {
    return dbManager.getMaximumDatabaseSize();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * setMaximumDatabaseSize(long)
   */
  @Override
  public synchronized final long setMaximumDatabaseSize( long size )
  {
    return dbManager.setMaximumDatabaseSize( size );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * doExecuteCommand
   * (de.unikassel.android.sdcframework.persistence.facade.DatabaseCommand)
   */
  @Override
  public < T > T doExecuteCommand( DatabaseCommand< T > command )
  {
    return dbManager.doExecuteCommand( command );
  }
}
