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

import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.preferences.LongPreference;

/**
 * Interface for the preferences of the service and the persistence module. <br/>
 * <br/>
 * 
 * The following preferences are available
 * <ul>
 * <li>flags for the enabled state of sample broadcasting, persistent storage
 * and sample transmission,</li>
 * <li>a limit for the maximum database size,</li>
 * <li>a flag if deletion is done priority based from deletion strategy,</li>
 * <li>a sample count to delete by deletion strategy,</li>
 * <li>a time for the wait strategy part,</li>
 * <li>a database full strategy description,</li>
 * <li>the {@linkplain TransmissionPreference preferences for the transmission
 * part } of the framework.</li>
 * </ul>
 * <br/>
 * <br/>
 * Internal defaults are:
 * <ul>
 * <li>false for all the enabled state flags,</li>
 * <li>10485760 bytes (10 MB) as maximum database size,</li>
 * <li>false for priority based deletion flag,</li>
 * <li>1000 for the deletion record count,</li>
 * <li>10000 milliseconds as wait time,</li>
 * <li>wait_notify_delete as database full strategy,</li>
 * <li>for transmission defaults refer to {@linkplain TransmissionPreference}.</li>
 * </ul>
 * <br/>
 * Internal defaults are used if there is no default configuration available in
 * the XML configuration file of the framework.
 * 
 * @see ServiceConfiguration
 * @see de.unikassel.android.sdcframework.data.SDCConfiguration
 * @author Katy Hilgenberg
 * 
 */
public interface ServicePreferences
    extends SinglePreference< ServiceConfiguration >
{
  /**
   * Getter for the sample broadcasts enabled preference
   * 
   * @return the sample broadcasts enabled preference
   */
  public abstract SinglePreference< Boolean >
      getSampleBroadcastsEnabledPreference();
  
  /**
   * Getter for the broadcasts frequency preference
   * 
   * @return the broadcasts frequency preference
   */
  public abstract LongPreference getBroadcastFrequencyPreference();
  
  /**
   * Getter for the sample location fix enabled preference
   * 
   * @return the sample location fix enabled preference
   */
  public abstract SinglePreference< Boolean >
      getSampleLocationFixEnabledPreference();
  
  /**
   * Getter for the persistence storage enabled preference
   * 
   * @return the the persistence storage enabled preference
   */
  public abstract SinglePreference< Boolean >
      getPersistentStorageEnabledPreference();
  
  /**
   * Getter for the sampling enabled preference
   * 
   * @return the sampling enabled preference
   */
  public abstract SinglePreference< Boolean >
      getSamplingEnabledPreference();
  
  /**
   * Getter for the sample transmission enabled preference
   * 
   * @return the sample transmission enabled preference
   */
  public abstract SinglePreference< Boolean >
      getTransmissionEnabledPreference();
  
  /**
   * Getter for the database size limit preference
   * 
   * @return the the database size limit preference
   */
  public abstract SinglePreference< Long > getDBMaxSizePreference();
  
  /**
   * Getter for the preference for priority based deletion
   * 
   * @return the preference for priority based deletion
   */
  public abstract SinglePreference< Boolean >
      getDbFullDeletionIsPriorityBasedPreference();
  
  /**
   * Getter the record count preference of the deletion strategy
   * 
   * @return the record count preference of deletion strategy
   */
  public abstract SinglePreference< Integer >
      getDbFullDeletionRecordCountPreference();
  
  /**
   * Getter for the preference for the wait strategy time
   * 
   * @return the preference for the wait strategy time
   */
  public abstract SinglePreference< Long > getDbFullWaitTimePreference();
  
  /**
   * Getter for the preference for the database full strategy description
   * 
   * @return the the preference for the database full strategy description
   */
  public abstract SinglePreference< DBFullStrategyDescription >
      getDbFullStrategyPreference();
  
  /**
   * Getter for preference with the transmission settings
   * 
   * @return the preference with the transmission settings
   */
  public abstract TransmissionPreference getTransmissionPreference();
  
  /**
   * Getter for preference with the log transfer settings
   * 
   * @return the preference with the log transfer settings
   */
  public abstract TransmissionProtocolPreference getLogTransferPreference();
}
