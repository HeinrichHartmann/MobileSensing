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
package de.unikassel.android.sdcframework.preferences;

import android.content.SharedPreferences;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.ServicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.SinglePreference;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolPreference;

/**
 * Implementation of the service preferences.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ServicePreferencesImpl implements
    ServicePreferences
{
  /**
   * 
   */
  private static final String LOG_TRANSFER_KEY = "log_transfer";
  
  /**
   * The absolute minimum for DB size ( can not be underrun by configuration )
   */
  public final static long MIN_DB_SIZE = 5120L;
  
  /**
   * The sample broadcasts enabled preference
   */
  private final BooleanPreference sampleBroadcastsEnabledPreference;
  
  /**
   * The sample location fix enabled preference
   */
  private final BooleanPreference sampleLocationFixEnabledPreference;
  
  /**
   * The persistent storage enabled preference
   */
  private final BooleanPreference persistentStorageEnabledPreference;
  
  /**
   * The persistent storage enabled preference
   */
  private final BooleanPreference transmissionEnabledPreference;
  
  /**
   * The database size limit preference
   */
  private final LongPreference dbSizeLimitPreference;
  
  /**
   * The database full deletion priority recognition flag preference
   */
  private final BooleanPreference dbFullDeletionIsPriorityBasedPreference;
  
  /**
   * The database full deletion record count preference
   */
  private final IntegerPreference dbFullDeletionRecordCountPreference;
  
  /**
   * The database full wait time preference
   */
  private final LongPreference dbFullWaitTimePreference;
  
  /**
   * The database full strategy preference
   */
  private final SinglePreference< DBFullStrategyDescription > dbFullStrategyPreference;
  
  /**
   * The preference for the transmission settings
   */
  private final TransmissionPreference transmissionPreference;
  
  /**
   * The preference for the log transfer settings
   */
  private TransmissionProtocolPreference logTransferPreference;
  
  /**
   * Constructor
   */
  public ServicePreferencesImpl()
  {
    super();
    
    this.sampleBroadcastsEnabledPreference =
        new BooleanPreference( "sdc_broadcast_samples", false );
    
    this.sampleLocationFixEnabledPreference =
        new BooleanPreference( "sdc_sample_location_enabled", false );
    
    this.persistentStorageEnabledPreference =
        new BooleanPreference( "sdc_persistent_store_samples", false );
    
    this.transmissionEnabledPreference =
          new BooleanPreference( "sdc_transfer_samples", false );
    
    this.dbSizeLimitPreference =
        new LongPreference( "sdc_max_db_size", 10485760L );
    
    this.dbFullDeletionIsPriorityBasedPreference =
        new BooleanPreference( "sdc_dbfull_del_priobased", false );
    
    this.dbFullDeletionRecordCountPreference =
        new IntegerPreference( "sdc_dbfull_del_cntrecords", 1000 );
    
    this.dbFullWaitTimePreference =
          new LongPreference( "sdc_dbfull_waittime", 10000L );
    
    this.dbFullStrategyPreference =
            new SinglePreferenceImpl< DBFullStrategyDescription >(
                "sdc_dbfull_strategy",
                DBFullStrategyDescription.WAIT_DELETE_NOTIFY )
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * de.unikassel.android.sdcframework.preferences.facade.SinglePreference
       * #getConfiguration(android.content.SharedPreferences)
       */
      @Override
      public DBFullStrategyDescription getConfiguration(
          SharedPreferences sharedPreferences )
      {
        DBFullStrategyDescription strategy = getDefault();
        String strategyDescr =
            sharedPreferences.getString( getKey(), strategy.toString() );
        try
        {
          strategy = DBFullStrategyDescription.valueOf( strategyDescr );
        }
        catch ( Exception e )
        {}
        return strategy;
      }
      
    };
    
    this.transmissionPreference = new TransmissionPreferenceImpl();
    this.logTransferPreference =
        new TransmissionProtocolPreferenceImpl( LOG_TRANSFER_KEY );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ServicePreferences#getSampleBroadcastsEnabledPreference()
   */
  @Override
  public SinglePreference< Boolean > getSampleBroadcastsEnabledPreference()
  {
    return sampleBroadcastsEnabledPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServicePreferences
   * #getSampleLocationFixEnabledPreference()
   */
  @Override
  public SinglePreference< Boolean > getSampleLocationFixEnabledPreference()
  {
    return sampleLocationFixEnabledPreference;
  }
  
  /**
   * Getter for the persistentStorageEnabledPreference
   * 
   * @return the persistentStorageEnabledPreference
   */
  public final SinglePreference< Boolean >
      getPersistentStorageEnabledPreference()
  {
    return persistentStorageEnabledPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServicePreferences
   * #getTransmissionEnabledPreference()
   */
  @Override
  public SinglePreference< Boolean > getTransmissionEnabledPreference()
  {
    return transmissionEnabledPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ServicePreferences#getDBMaxSizePreference()
   */
  @Override
  public SinglePreference< Long > getDBMaxSizePreference()
  {
    return dbSizeLimitPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServicePreferences
   * #getDbFullDeletionIsPriorityBasedPreference()
   */
  @Override
  public final SinglePreference< Boolean >
      getDbFullDeletionIsPriorityBasedPreference()
  {
    return dbFullDeletionIsPriorityBasedPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServicePreferences
   * #getDbFullDeletionRecordCountPreference()
   */
  @Override
  public final SinglePreference< Integer >
      getDbFullDeletionRecordCountPreference()
  {
    return dbFullDeletionRecordCountPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServicePreferences
   * #getDbFullWaitTimePreference()
   */
  @Override
  public final SinglePreference< Long > getDbFullWaitTimePreference()
  {
    return dbFullWaitTimePreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServicePreferences
   * #getDbFullStrategyPreference()
   */
  @Override
  public final SinglePreference< DBFullStrategyDescription >
      getDbFullStrategyPreference()
  {
    return dbFullStrategyPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServicePreferences
   * #getTransmissionPreference()
   */
  @Override
  public TransmissionPreference getTransmissionPreference()
  {
    return transmissionPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServicePreferences
   * #getLogTransferPreference()
   */
  @Override
  public TransmissionProtocolPreference getLogTransferPreference()
  {
    return logTransferPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SinglePreference#getKey
   * ()
   */
  @Override
  public String getKey()
  {
    return "sdc_preferences";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.
   * ServicePreferences #getConfiguration(android.content.SharedPreferences)
   */
  @Override
  public ServiceConfiguration getConfiguration(
      SharedPreferences sharedPreferences )
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    config.setMaximumDatabaseSize( Math.max(
        getDBMaxSizePreference().getConfiguration( sharedPreferences ),
        MIN_DB_SIZE ) );
    config.setBroadcastingSamples( getSampleBroadcastsEnabledPreference().getConfiguration(
        sharedPreferences ) );
    config.setIsAddingSampleLocation( getSampleLocationFixEnabledPreference().getConfiguration(
        sharedPreferences ) );
    config.setStoringSamples( getPersistentStorageEnabledPreference().getConfiguration(
        sharedPreferences ) );
    config.setTransmittingSamples( getTransmissionEnabledPreference().getConfiguration(
        sharedPreferences ) );
    config.setDBFullDeletionPriorityBased( getDbFullDeletionIsPriorityBasedPreference().getConfiguration(
        sharedPreferences ) );
    config.setDBFullDeletionRecordCount( getDbFullDeletionRecordCountPreference().getConfiguration(
        sharedPreferences ) );
    config.setDBFullWaitTime( getDbFullWaitTimePreference().getConfiguration(
        sharedPreferences ) );
    config.setDBFullStrategy( getDbFullStrategyPreference().getConfiguration(
        sharedPreferences ) );
    config.setTransmissionConfiguration( getTransmissionPreference().getConfiguration(
        sharedPreferences ) );
    config.setLogTransferConfiguration( getLogTransferPreference().getConfiguration(
        sharedPreferences ) );
    return config;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * getDefault()
   */
  @Override
  public ServiceConfiguration getDefault()
  {
    ServiceConfiguration config = new ServiceConfigurationImpl();
    config.setBroadcastingSamples( getSampleBroadcastsEnabledPreference().getDefault() );
    config.setIsAddingSampleLocation( getSampleLocationFixEnabledPreference().getDefault() );
    config.setStoringSamples( getPersistentStorageEnabledPreference().getDefault() );
    config.setTransmittingSamples( getTransmissionEnabledPreference().getDefault() );
    config.setMaximumDatabaseSize( getDBMaxSizePreference().getDefault() );
    config.setDBFullDeletionPriorityBased( getDbFullDeletionIsPriorityBasedPreference().getDefault() );
    config.setDBFullDeletionRecordCount( getDbFullDeletionRecordCountPreference().getDefault() );
    config.setDBFullWaitTime( getDbFullWaitTimePreference().getDefault() );
    config.setDBFullStrategy( getDbFullStrategyPreference().getDefault() );
    config.setTransmissionConfiguration( getTransmissionPreference().getDefault() );
    config.setLogTransferConfiguration( getLogTransferPreference().getDefault() );
    return config;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * setDefault(java.lang.Object)
   */
  @Override
  public void setDefault( ServiceConfiguration defaultValue )
  {
    getSampleBroadcastsEnabledPreference().setDefault(
        defaultValue.isBroadcastingSamples() );
    getSampleLocationFixEnabledPreference().setDefault(
        defaultValue.isAddingSampleLocation() );
    getPersistentStorageEnabledPreference().setDefault(
        defaultValue.isStoringSamples() );
    getTransmissionEnabledPreference().setDefault(
        defaultValue.isTransmittingSamples() );
    getDBMaxSizePreference().setDefault( defaultValue.getMaximumDatabaseSize() );
    getDbFullDeletionIsPriorityBasedPreference().setDefault(
        defaultValue.isDBFullDeletionPriorityBased() );
    getDbFullDeletionRecordCountPreference().setDefault(
        defaultValue.getDBFullDeletionRecordCount() );
    getDbFullWaitTimePreference().setDefault( defaultValue.getDBFullWaitTime() );
    getDbFullStrategyPreference().setDefault( defaultValue.getDBFullStrategy() );
    getTransmissionPreference().setDefault(
        defaultValue.getTransmissionConfiguration() );
    getLogTransferPreference().setDefault( defaultValue.getLogTransferConfiguration() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * testForKey(java.lang.String)
   */
  @Override
  public boolean testForKey( String key )
  {
    return getSampleBroadcastsEnabledPreference().testForKey( key )
        || getSampleLocationFixEnabledPreference().testForKey( key )
        || getPersistentStorageEnabledPreference().testForKey( key )
        || getTransmissionEnabledPreference().testForKey( key )
        || getDBMaxSizePreference().testForKey( key )
        || getDbFullDeletionIsPriorityBasedPreference().testForKey( key )
        || getDbFullDeletionRecordCountPreference().testForKey( key )
        || getDbFullWaitTimePreference().testForKey( key )
        || getDbFullStrategyPreference().testForKey( key )
        || getTransmissionPreference().testForKey( key )
        || getLogTransferPreference().testForKey( key );
  }
}
