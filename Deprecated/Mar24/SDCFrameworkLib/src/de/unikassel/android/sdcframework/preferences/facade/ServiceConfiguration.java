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
package de.unikassel.android.sdcframework.preferences.facade;

import de.unikassel.android.sdcframework.data.SDCConfiguration;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;

/**
 * The configuration for the framework service and persistence module.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface ServiceConfiguration
extends UpdatableConfiguration< ServiceConfiguration >
{  
  /**
   * Does update this configuration by a serializable configuration
   * 
   * @param config
   *          the serializable configuration to update from
   */
  public abstract void update( SDCConfiguration config );
  
  /**
   * Getter for the flag for sample broadcasts behavior
   * 
   * @return true if samples shall be broadcasted, false otherwise
   */
  public abstract boolean isBroadcastingSamples();
  
  /**
   * Setter for the flag for sample broadcasts behavior
   * 
   * @param isBroadcastingSamples
   *          the flag for sample broadcasts to set
   */
  public void setBroadcastingSamples( Boolean isBroadcastingSamples );
  
  /**
   * Getter for the flag for adding a location fix to each sample
   * 
   * @return true if a location fix is added to each sample, false otherwise
   */
  public abstract boolean isAddingSampleLocation();
  
  /**
   * Setter for the flag for adding a location fix to each sample
   * 
   * @param isAddingSampleLocation
   *          the flag for adding a location fix to each sample
   */
  public void setIsAddingSampleLocation( Boolean isAddingSampleLocation );
  
  /**
   * Getter for the flag for persistent storage behavior
   * 
   * @return true if samples will be stored persistent, false otherwise
   */
  public abstract boolean isStoringSamples();
  
  /**
   * Setter for flag for persistent storage behavior
   * 
   * @param isStoringSamples
   *          the flag for persistent storage behavior to set
   */
  public abstract void setStoringSamples( Boolean isStoringSamples );
  
  /**
   * Getter for the transmission behavior flag
   * 
   * @return true if samples will be transferred to a remote host, false
   *         otherwise
   */
  public abstract boolean isTransmittingSamples();
  
  /**
   * Setter for the transmission behavior flag
   * 
   * @param isTransmittingSamples
   *          the transmission behavior flag to set
   */
  public abstract void setTransmittingSamples( Boolean isTransmittingSamples );
  
  /**
   * Setter for the maximum database size in bytes
   * 
   * @param maxDBSize
   *          the maximum database size in bytes
   */
  public abstract void setMaximumDatabaseSize( Long maxDBSize );
  
  /**
   * Getter for the maximum database size in bytes
   * 
   * @return the maximum database size in bytes
   */
  public abstract long getMaximumDatabaseSize();
  
  /**
   * Setter for the wait time used by the wait strategy in case of a full
   * database
   * 
   * @param dbFullWaitTime
   *          the wait time in milliseconds to set
   */
  public abstract void setDBFullWaitTime( Long dbFullWaitTime );
  
  /**
   * Getter for the wait time used by the wait strategy in case of a full
   * database
   * 
   * @return the wait time in milliseconds
   */
  public abstract long getDBFullWaitTime();
  
  /**
   * Setter for the record count to be deleted in case of a full database ( used
   * by the sample deletion strategy )
   * 
   * @param dbFullDeletionRecordCount
   *          the deletion record count to set
   */
  public abstract void setDBFullDeletionRecordCount(
      Integer dbFullDeletionRecordCount );
  
  /**
   * Getter for the record count to be deleted in case of a full database ( used
   * by the sample deletion strategy )
   * 
   * @return the deletion record count
   */
  public abstract int getDBFullDeletionRecordCount();
  
  /**
   * Setter for the flag database deletion strategy priority flag
   * 
   * @param dbFullDeletionIsPriorityBased
   *          true if the database deletion strategy shall delete lower
   *          priorities first, false otherwise
   */
  public abstract void setDBFullDeletionPriorityBased(
      Boolean dbFullDeletionIsPriorityBased );
  
  /**
   * Getter for the flag database deletion strategy priority flag
   * 
   * @return true if the database deletion strategy shall delete lower
   *         priorities first, false otherwise
   */
  public abstract boolean isDBFullDeletionPriorityBased();
  
  /**
   * Setter for the configured database full strategy chain
   * 
   * @param dbFullStrategy
   *          the configured database full strategy chain
   */
  public abstract void setDBFullStrategy(
      DBFullStrategyDescription dbFullStrategy );
  
  /**
   * Getter for the configured Database full strategy chain
   * 
   * @return the configured Database full strategy chain
   */
  public abstract DBFullStrategyDescription getDBFullStrategy();
  
  /**
   * Setter for the transmission configuration
   * 
   * @param config
   *          the transmission configuration to set
   */
  public abstract void setTransmissionConfiguration(
      TransmissionConfiguration config );
  
  /**
   * Getter for the transmission configuration
   * 
   * @return the transmission configuration
   */
  public abstract TransmissionConfiguration getTransmissionConfiguration();
  
  /**
   * Setter for the log transfer configuration
   * 
   * @param config
   *          the log transfer configuration to set
   */
  public abstract void setLogTransferConfiguration( TransmissionProtocolConfiguration config);
  
  /**
   * Getter for the log transfer configuration
   * 
   * @return the log transfer configuration, or null if not configured
   */
  public abstract TransmissionProtocolConfiguration getLogTransferConfiguration();
}
