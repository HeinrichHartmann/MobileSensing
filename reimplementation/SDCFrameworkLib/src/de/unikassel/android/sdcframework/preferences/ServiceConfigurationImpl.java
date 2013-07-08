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

import android.annotation.SuppressLint;
import de.unikassel.android.sdcframework.data.DatabaseConfigurationEntry;
import de.unikassel.android.sdcframework.data.SDCConfiguration;
import de.unikassel.android.sdcframework.data.TransmissionConfigurationEntry;
import de.unikassel.android.sdcframework.data.TransmissionProtocolConfigurationEntry;
import de.unikassel.android.sdcframework.data.WeeklySchedule;
import de.unikassel.android.sdcframework.persistence.DeleteSamplesStrategy;
import de.unikassel.android.sdcframework.persistence.WaitStrategy;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.util.ObjectUtils;

/**
 * Implementation of the service configuration.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ServiceConfigurationImpl
    implements ServiceConfiguration
{
  /**
   * The flag for the service sample broadcast behavior
   */
  private boolean isBroadcastingSamples;
  
  /**
   * The broadcast interval in milliseconds.
   */
  private long broadcastFrequency;
  
  /**
   * The sampling enabled state.
   */
  private boolean isSamplingEnabled;
  
  /**
   * The flag for attachment of a location fix to each sample
   */
  private boolean isAddingSampleLocation;
  
  /**
   * The flag for persistent storage of samples for transmission purpose
   */
  private boolean isStoringSamples;
  
  /**
   * The transmission behavior flag
   */
  private boolean isTransmittingSamples;
  
  /**
   * The maximum database size in bytes
   */
  private long maxDBSize;
  
  /**
   * Flag if the database full strategy does work priority based. <br/>
   * If this flag is true and the {link {@link DeleteSamplesStrategy database
   * full strategy} is executed, it will first delete the oldest samples with
   * lowest priority.
   */
  private boolean dbFullDeletionIsPriorityBased;
  
  /**
   * The record count which is deleted every time the {link
   * {@link DeleteSamplesStrategy database full strategy} is executed.
   */
  private int dbFullDeletionRecordCount;
  
  /**
   * The milliseconds the persistent storage manager is paused every time the
   * {link {@link WaitStrategy database full strategy} is executed.
   */
  private long dbFullWaitTime;
  
  /**
   * The configured @link {@link DBFullStrategyDescription database full
   * strategy }.
   */
  private DBFullStrategyDescription dbFullStrategy;
  
  /**
   * The transmission configuration
   */
  private final TransmissionConfiguration transmissionConfiguration;
  
  /**
   * The log transfer configuration
   */
  private final TransmissionProtocolConfiguration logTransferConfiguration;
  
  /**
   * The weekly schedule.
   */
  private WeeklySchedule schedule;
  
  /**
   * Constructor
   */
  public ServiceConfigurationImpl()
  {
    transmissionConfiguration = new TransmissionConfigurationImpl();
    logTransferConfiguration = new TransmissionProtocolConfigurationImpl();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.UpdatableConfiguration
   * #update(java.lang.Object)
   */
  @Override
  public void update( ServiceConfiguration configuration )
  {
    setBroadcastingSamples( configuration.isBroadcastingSamples() );
    setBroadcastFrequency( configuration.getBroadcastFrequency() );
    setSamplingEnabled( configuration.isSamplingEnabled() );
    setIsAddingSampleLocation( configuration.isAddingSampleLocation() );
    setStoringSamples( configuration.isStoringSamples() );
    setTransmittingSamples( configuration.isTransmittingSamples() );
    setMaximumDatabaseSize( configuration.getMaximumDatabaseSize() );
    setDBFullDeletionPriorityBased( configuration.isDBFullDeletionPriorityBased() );
    setDBFullDeletionRecordCount( configuration.getDBFullDeletionRecordCount() );
    setDBFullWaitTime( configuration.getDBFullWaitTime() );
    setDBFullStrategy( configuration.getDBFullStrategy() );
    getTransmissionConfiguration().update(
        configuration.getTransmissionConfiguration() );
    setLogTransferConfiguration( configuration.getLogTransferConfiguration() );
    setWeeklySchedule( configuration.getWeeklySchedule() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #update(de.unikassel.android.sdcframework.data.SDCConfiguration)
   */
  // @lint intended ignore of locals
  @SuppressLint( "DefaultLocale" )
  @Override
  public void update( SDCConfiguration config )
  {
    setBroadcastingSamples( config.isBroadcastingSamples() );
    setBroadcastFrequency( config.getBroadcastFrequency() );
    setSamplingEnabled( config.isSamplingEnabled() );
    setIsAddingSampleLocation( config.isAddingSampleLocation() );
    setStoringSamples( config.isStoringSamples() );
    setTransmittingSamples( config.isTransmittingSamples() );
    
    DatabaseConfigurationEntry databaseConfiguration =
        config.getDatabaseConfiguration();
    if ( databaseConfiguration != null )
    {
      setMaximumDatabaseSize( databaseConfiguration.getMaxDBSize() );
      setDBFullDeletionPriorityBased( databaseConfiguration.isDBFullDeletionPriorityBased() );
      setDBFullDeletionRecordCount( databaseConfiguration.getDBFullDeletionRecordCount() );
      setDBFullWaitTime( databaseConfiguration.getDBFullWaitTime() );
      String sDBFullStrategy = databaseConfiguration.getDBFullStrategy();
      if ( sDBFullStrategy != null )
      {
        setDBFullStrategy( DBFullStrategyDescription.valueOf( sDBFullStrategy.toUpperCase() ) );
      }
    }
    
    TransmissionConfigurationEntry transmissionConfig =
        config.getTransmissionConfiguration();
    if ( transmissionConfig != null )
    {
      transmissionConfiguration.update( transmissionConfig );
    }
    
    TransmissionProtocolConfigurationEntry logTransferConfig =
        config.getLogTransferConfiguration();
    if ( logTransferConfig != null )
    {
      this.logTransferConfiguration.update( logTransferConfig );
    }
    
    setWeeklySchedule( config.getWeeklySchedule() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o )
  {
    if ( o instanceof ServiceConfiguration )
    {
      ServiceConfiguration conf = (ServiceConfiguration) o;
      return conf.getMaximumDatabaseSize() == getMaximumDatabaseSize()
          &&
          conf.isBroadcastingSamples() == isBroadcastingSamples()
          &&
          conf.getBroadcastFrequency() == getBroadcastFrequency()
          &&
          conf.isSamplingEnabled() == isSamplingEnabled()
          &&
          conf.isAddingSampleLocation() == isAddingSampleLocation()
          &&
          conf.isStoringSamples() == isStoringSamples()
          &&
          conf.isTransmittingSamples() == isTransmittingSamples()
          &&
          conf.isDBFullDeletionPriorityBased() == isDBFullDeletionPriorityBased()
          &&
          conf.getDBFullDeletionRecordCount() == getDBFullDeletionRecordCount()
          &&
          conf.getDBFullWaitTime() == conf.getDBFullWaitTime()
          &&
          conf.getDBFullStrategy() == getDBFullStrategy()
          &&
          getTransmissionConfiguration().equals(
              conf.getTransmissionConfiguration() )
          &&
          ObjectUtils.equals( getLogTransferConfiguration(),
              conf.getLogTransferConfiguration() )
          &&
          ObjectUtils.equals( getWeeklySchedule(),
              conf.getWeeklySchedule() );
    }
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #isBroadcastingSamples()
   */
  @Override
  public boolean isBroadcastingSamples()
  {
    return isBroadcastingSamples;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setBroadcastingSamples(boolean)
   */
  @Override
  public void setBroadcastingSamples( Boolean isBroadcastingSamples )
  {
    if ( isBroadcastingSamples != null )
      this.isBroadcastingSamples = isBroadcastingSamples;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #isSamplingEnabled()
   */
  @Override
  public boolean isSamplingEnabled()
  {
    return isSamplingEnabled;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setSamplingEnabled(java.lang.Boolean)
   */
  @Override
  public void setSamplingEnabled( Boolean isSamplingEnabled )
  {
    if ( isSamplingEnabled != null )
      this.isSamplingEnabled = isSamplingEnabled;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #isStoringSamples()
   */
  @Override
  public final boolean isStoringSamples()
  {
    return isStoringSamples;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setStoringSamples(boolean)
   */
  @Override
  public final void setStoringSamples( Boolean isStoringSamples )
  {
    if ( isStoringSamples != null )
      this.isStoringSamples = isStoringSamples;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #isTransmittingSamples()
   */
  @Override
  public boolean isTransmittingSamples()
  {
    return isTransmittingSamples;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setTransmittingSamples(boolean)
   */
  @Override
  public void setTransmittingSamples( Boolean isTransmittingSamples )
  {
    if ( isTransmittingSamples != null )
      this.isTransmittingSamples = isTransmittingSamples;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setMaximumDatabaseSize(long)
   */
  @Override
  public void setMaximumDatabaseSize( Long maxDBSize )
  {
    if ( maxDBSize != null )
      this.maxDBSize = maxDBSize;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #getMaximumDatabaseSize()
   */
  @Override
  public long getMaximumDatabaseSize()
  {
    return maxDBSize;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setDBFullWaitTime(long)
   */
  @Override
  public void setDBFullWaitTime( Long dbFullWaitTime )
  {
    if ( dbFullWaitTime != null )
      this.dbFullWaitTime = dbFullWaitTime;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #getDBFullWaitTime()
   */
  @Override
  public long getDBFullWaitTime()
  {
    return dbFullWaitTime;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setDBFullDeletionRecordCount(int)
   */
  @Override
  public void setDBFullDeletionRecordCount( Integer dbFullDeletionRecordCount )
  {
    if ( dbFullDeletionRecordCount != null )
      this.dbFullDeletionRecordCount = dbFullDeletionRecordCount;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #getDBFullDeletionRecordCount()
   */
  @Override
  public int getDBFullDeletionRecordCount()
  {
    return dbFullDeletionRecordCount;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setDBFullDeletionPriorityBased(boolean)
   */
  @Override
  public void setDBFullDeletionPriorityBased(
      Boolean dbFullDeletionIsPriorityBased )
  {
    if ( dbFullDeletionIsPriorityBased != null )
      this.dbFullDeletionIsPriorityBased = dbFullDeletionIsPriorityBased;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #isDBFullDeletionPriorityBased()
   */
  @Override
  public boolean isDBFullDeletionPriorityBased()
  {
    return dbFullDeletionIsPriorityBased;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setDBFullStrategy(de.unikassel.android.sdcframework.persistence.facade.
   * DatabaseFullStrategyBuilder.DBFullStrategies)
   */
  @Override
  public void setDBFullStrategy( DBFullStrategyDescription dbFullStrategy )
  {
    if ( dbFullStrategy != null )
      this.dbFullStrategy = dbFullStrategy;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #getDBFullStrategy()
   */
  @Override
  public DBFullStrategyDescription getDBFullStrategy()
  {
    return dbFullStrategy;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setTransmissionConfiguration
   * (de.unikassel.android.sdcframework.preferences.
   * facade.TransmissionConfiguration)
   */
  @Override
  public void setTransmissionConfiguration( TransmissionConfiguration config )
  {
    transmissionConfiguration.update( config );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #getTransmissionConfiguration()
   */
  @Override
  public TransmissionConfiguration getTransmissionConfiguration()
  {
    return transmissionConfiguration;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setLogTransferConfiguration
   * (de.unikassel.android.sdcframework.preferences.facade
   * .TransmissionProtocolConfiguration)
   */
  @Override
  public void setLogTransferConfiguration(
      TransmissionProtocolConfiguration config )
  {
    this.logTransferConfiguration.update( config );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #getLogTransferConfiguration()
   */
  @Override
  public TransmissionProtocolConfiguration getLogTransferConfiguration()
  {
    return logTransferConfiguration;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #isAddingSampleLocation()
   */
  @Override
  public boolean isAddingSampleLocation()
  {
    return isAddingSampleLocation;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setIsAddingSampleLocation(java.lang.Boolean)
   */
  @Override
  public void setIsAddingSampleLocation( Boolean isAddingSampleLocation )
  {
    if ( isAddingSampleLocation != null )
      this.isAddingSampleLocation = isAddingSampleLocation;
  }
    
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #getBroadcastFrequency()
   */
  @Override
  public long getBroadcastFrequency()
  {
    return broadcastFrequency;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setBroadcastFrequency(long)
   */
  @Override
  public void setBroadcastFrequency( Long frequency )
  {
    if ( frequency != null )
      this.broadcastFrequency = frequency;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #setWeeklySchedule
   * (de.unikassel.android.sdcframework.data.independent.WeeklySchedule)
   */
  @Override
  public void setWeeklySchedule( WeeklySchedule schedule )
  {
    this.schedule = schedule;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration
   * #getWeeklySchedule()
   */
  @Override
  public WeeklySchedule getWeeklySchedule()
  {
    return schedule;
  }
}
