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
package de.unikassel.android.sdcframework.data;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * A database configuration does describe the configuration for the persistent
 * storage management. <br/>
 * <br/>
 * The following settings can be configured:
 * <ul>
 * <li>the maximum database size in kilobytes,</li>
 * <li>the strategy combination to apply in case of a database size limit
 * overrun ( database is full ),</li>
 * <li>the parameters of the sample deletion strategy ( count of samples to
 * delete and the flag for preferred deletion of lowest priority samples ),</li>
 * <li>and the parameter of the wait strategy ( the time to wait in milliseconds
 * ).</li>
 * </ul>
 * 
 * @see SDCConfiguration
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "transferConfig" )
public final class DatabaseConfigurationEntry
{
  /**
   * The maximum database size in kilobytes
   */
  @Element( name = "maxSize", required = false )
  private Long maxDBSize;
  
  /**
   * The flag which is indicating that sample deletion is done for lower
   * priorities first ( in case of database is full and deletion strategy is
   * executed ).
   */
  @Element( name = "delStrategyUsePrio", required = false )
  private Boolean isDBFullDeletionPriorityBased;
  
  /**
   * The record count to delete ( in case of database is full and deletion
   * strategy is executed )
   */
  @Element( name = "delStrategyRecordCount", required = false )
  private Integer dbFullDeletionRecordCount;
  
  /**
   * The wait time in milliseconds for the wait strategy.
   */
  @Element( name = "waitStrategyMillis", required = false )
  private Long dbFullWaitTime;
  
  /**
   * The description of the strategy chain to apply if the database is full. <br/>
   * Has to be the string representation of a valid
   de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescriptiontrategyDescription
   * strategy description}.
   */
  @Element( name = "dbFullStrategy", required = false )
  private String dbFullStrategy;
  
  /**
   * Constructor
   */
  public DatabaseConfigurationEntry()
  {}
  
  /**
   * Getter for the maxDBSize
   * 
   * @return the maxDBSize
   */
  public final Long getMaxDBSize()
  {
    return maxDBSize;
  }
  
  /**
   * Setter for the maxDBSize
   * 
   * @param maxDBSize
   *          the maxDBSize to set
   */
  public final void setMaxDBSize( Long maxDBSize )
  {
    this.maxDBSize = maxDBSize;
  }
  
  /**
   * Setter for for the flag which is indicating that sample deletion is done
   * for lower priorities first ( in case of database is full and deletion
   * strategy is executed ).
   * 
   * @param isDBFullDeletionPriorityBased
   *          the flag to set
   */
  public final void
      setDBFullDeletionPriorityBased( Boolean isDBFullDeletionPriorityBased )
  {
    this.isDBFullDeletionPriorityBased = isDBFullDeletionPriorityBased;
  }
  
  /**
   * Getter for the flag which is indicating that sample deletion is done for
   * lower priorities first ( in case of database is full and deletion strategy
   * is executed ).
   * 
   * @return true if sample deletion is done priority based if database is full
   */
  public final Boolean isDBFullDeletionPriorityBased()
  {
    return isDBFullDeletionPriorityBased;
  }
  
  /**
   * Setter for the record count to delete in case of database full and deletion
   * strategy is executed.
   * 
   * @param dbFullDeletionRecordCount
   *          the the record count to set
   */
  public final void setDBFullDeletionRecordCount( Integer dbFullDeletionRecordCount )
  {
    this.dbFullDeletionRecordCount = dbFullDeletionRecordCount;
  }
  
  /**
   * Getter for the record count to delete in case of database full and deletion
   * strategy is executed.
   * 
   * @return the record count to delete
   */
  public final Integer getDBFullDeletionRecordCount()
  {
    return dbFullDeletionRecordCount;
  }
  
  /**
   * Setter for the time to pause and wait in case of database full and wait
   * strategy is executed.
   * 
   * @param dbFullWaitTime
   *          the wait time in milliseconds to set
   */
  public final void setDBFullWaitTime( Long dbFullWaitTime )
  {
    this.dbFullWaitTime = dbFullWaitTime;
  }
  
  /**
   * Getter for for the time to pause and wait in case of database full and wait
   * strategy is executed.
   * 
   * @return the time to wait
   */
  public final Long getDBFullWaitTime()
  {
    return dbFullWaitTime;
  }
  
  /**
   * Setter for the strategy chain description to execute if database is full.<br/>
   * Has to be the string representation of a {@linkplain de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription
   * strategy description}.
   * 
   * @param dbFullStrategy
   *          the string representation of {@linkplain de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription
   *          strategy description} to set
   */
  public final void setDBFullStrategy( String dbFullStrategy )
  {
    this.dbFullStrategy = dbFullStrategy;
  }
  
  /**
   * Getter for the dbFullStrategy
   * 
   * @return the dbFullStrategy
   */
  public final String getDBFullStrategy()
  {
    return dbFullStrategy;
  }
  
}
