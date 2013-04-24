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

import de.unikassel.android.sdcframework.data.TransmissionConfigurationEntry;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;

/**
 * The configuration for the transmission module.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface TransmissionConfiguration
extends UpdatableConfiguration< TransmissionConfiguration >
{
  
  /**
   * Does update this configuration by a serializable configuration
   * 
   * @param config
   *          the serializable configuration to update from
   */
  public abstract void update( TransmissionConfigurationEntry config );
  
  /**
   * Getter for the minimum count of samples to transfer
   * 
   * @return the minimum count of samples to transfer
   */
  public abstract int getMinSampleTransferCount();
  
  /**
   * Setter for the minimum count of samples to transfer
   * 
   * @param minSampleTransferCount
   *          the minimum count of samples to set
   */
  public abstract void setMinSampleTransferCount( Integer minSampleTransferCount );
  
  /**
   * Getter for the maximum count of samples to transfer
   * 
   * @return the maximum count of samples to transfer
   */
  public abstract int getMaxSampleTransferCount();
  
  /**
   * Setter for the maximum count of samples to transfer
   * 
   * @param maxSampleTransferCount
   *          the maximum count of samples to transfer to set
   */
  public abstract void setMaxSampleTransferCount( Integer maxSampleTransferCount );
  
  /**
   * Getter for the minimum transfer frequency
   * 
   * @return the minimum transfer frequency in seconds
   */
  public abstract long getMinTransferFrequency();
  
  /**
   * Setter for the minimum transfer frequency
   * 
   * @param minTransferFrequency
   *          the minimum transfer frequency in seconds to set
   */
  public abstract void setMinTransferFrequency( Long minTransferFrequency );
  
  /**
   * Getter for the protocol configuration to set
   * 
   * @return the protocol configuration to set
   */
  public abstract TransmissionProtocolConfiguration getProtocolConfiguration();
  
  /**
   * Setter for the protocol configuration to set
   * 
   * @param protocolConfig
   *          the protocol configuration to set
   */
  public abstract void setProtocolConfiguration( TransmissionProtocolConfiguration protocolConfig );
  
  /**
   * Getter for the archive type
   * 
   * @return the archive type
   */
  public abstract ArchiveTypes getArchiveType();
  
  /**
   * Setter for the archive type
   * 
   * @param archiveType
   *          the archive type to set
   */
  public abstract void setArchiveType( ArchiveTypes archiveType );
  
  /**
   * Getter for the encryption enabled flag
   * 
   * @return true if encryption is enabled
   */
  public abstract Boolean isEncryptionEnabled();
  
  /**
   * Setter for the encryption enabled flag
   * 
   * @param isEncryptionEnabled
   *          the encryption enabled flag to set
   */
  public abstract void setEncryptionEnabled( Boolean isEncryptionEnabled );
  
}
