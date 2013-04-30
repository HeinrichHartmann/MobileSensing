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

import android.app.Activity;
import android.content.Context;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseCommand;
import de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.util.facade.EventObserver;

/**
 * Test implementation of the database manager interface for strategy test
 * purpose
 * 
 * @author Katy Hilgenberg
 * 
 */
class PersistentStorageManagerForTest
    implements PersistentStorageManager
{
  /**
   * The current sample count in db
   */
  public int dbSampleCount = 100;
  
  /**
   * Counter to count the calls to the processCurrentSamples Method
   */
  public int counter = 0;
  
  /**
   * The result returned by the method processCurrentSamples
   */
  boolean isProcessingCurrentSamples = false;
  
  /**
   * The flag to determine if deleteOldestSamplesInDatabase was called
   */
  boolean deleteOldestSamplesInDatabaseWasCalled = false;
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * getSampleCountInDatabase()
   */
  @Override
  public long getRecordCountInDatabase()
  {
    return dbSampleCount;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * setMaximumDatabaseSize(long)
   */
  @Override
  public long setMaximumDatabaseSize( long size )
  {
    return size;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * getMaximumDatabaseSize()
   */
  @Override
  public long getMaximumDatabaseSize()
  {
    return Integer.MAX_VALUE;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.persistence.facade.DatabaseManager#
   * deleteOldestSamplesInDatabase(long, boolean)
   */
  @Override
  public long doDeleteOldestSamplesInDatabase( long count,
      boolean lowestPriorityFirst )
  {
    deleteOldestSamplesInDatabaseWasCalled = true;
    return count;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager
   * #doExecuteCurrentCommand()
   */
  @Override
  public boolean doExecuteCurrentCommand()
  {
    counter++;
    return isProcessingCurrentSamples;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager
   * #getSavedRecordCount()
   */
  @Override
  public long getSavedRecordCount()
  {
    return dbSampleCount;
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
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.WorkerThread#isWorking()
   */
  @Override
  public boolean isWorking()
  {
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.WorkerThread#setLogging(boolean
   * )
   */
  @Override
  public void setLogging( boolean doLog )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.WorkerThread#isLogging()
   */
  @Override
  public boolean isLogging()
  {
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.WorkerThread#hasTerminated()
   */
  @Override
  public boolean hasTerminated()
  {
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.WorkerThread#doTerminate()
   */
  @Override
  public void doTerminate()
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.WorkerThread#startWork()
   */
  @Override
  public void startWork()
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.WorkerThread#stopWork()
   */
  @Override
  public void stopWork()
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onResume(
   * android.content.Context)
   */
  @Override
  public void onResume( Context applicationContext )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onPause(android
   * .content.Context)
   */
  @Override
  public void onPause( Context applicationContext )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onCreate(
   * android.content.Context)
   */
  @Override
  public void onCreate( Context applicationContext )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onDestroy
   * (android.content.Context)
   */
  @Override
  public void onDestroy( Context applicationContext )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.service.facade.SampleObserver#getObserver
   * ()
   */
  @Override
  public EventObserver< Sample > getObserver()
  {
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager
   * #updateDatabaseFullStrategy(android.content.Context,
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration,
   * java.lang.Class)
   */
  @Override
  public void updateDatabaseFullStrategy( Context context,
      ServiceConfiguration config,
      Class< ? extends Activity > controlActivityClass )
  {}
  
}