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
 * A transmission configuration does describe the default configuration settings
 * for the transmission service. <br/>
 * <br/>
 * The following settings can be configured:
 * <ul>
 * <li>a minimum and a maximum sample count for a single archive to transfer,</li>
 * <li>the {@link TransmissionProtocolConfigurationEntry configuration for the transmission protocol},
 * <li>a lower limit for the transmissions frequency in seconds,</li>
 * <li>the archive type ( jar or zip format ),</li>
 * <li>and the encryption flag (for rsa archive encryption).</li>
 * </ul>
 * 
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "transferConfig" )
public final class TransmissionConfigurationEntry
{
  /**
   * The minimum of samples to transfer
   */
  @Element( name = "minSampleCount", required = false )
  private Integer minSampleTransferCount;
  
  /**
   * The maximum of samples to transfer
   */
  @Element( name = "maxSampleCount", required = false )
  private Integer maxSampleTransferCount;
  
  /**
   * The minimum frequency for the sample transfer in seconds ( the real
   * transfer frequency does depend on many parameters and will differ )
   */
  @Element( name = "minTransferFrequency", required = false )
  private Long minTransferFrequency;
  
  /**
   * The archive type ( "jar" or "zip" )
   */
  @Element( name = "archiveType", required = false )
  private String archiveType;
  
  /**
   * The encryption enabled flag
   */
  @Element( name = "encrypt", required = false )
  private Boolean isEncryptionEnabled;
  
  /**
   * The transmission protocol configuration
   */
  @Element( name = "protocolConfig", required = false )
  private TransmissionProtocolConfigurationEntry protocolConfig;
  
  /**
   * Constructor
   */
  public TransmissionConfigurationEntry()
  {
    setProtocolConfig( new TransmissionProtocolConfigurationEntry() );
  }
  
  /**
   * Getter for the minimum count of samples to transfer
   * 
   * @return the minimum count of samples to transfer
   */
  public final Integer getMinSampleTransferCount()
  {
    return minSampleTransferCount;
  }
  
  /**
   * Setter for the minimum count of samples to transfer
   * 
   * @param minSampleTransferCount
   *          the minimum count of samples to set
   */
  public final void setMinSampleTransferCount( Integer minSampleTransferCount )
  {
    this.minSampleTransferCount = minSampleTransferCount;
  }
  
  /**
   * Getter for the maximum count of samples to transfer
   * 
   * @return the maximum count of samples to transfer
   */
  public final Integer getMaxSampleTransferCount()
  {
    return maxSampleTransferCount;
  }
  
  /**
   * Setter for the maximum count of samples to transfer
   * 
   * @param maxSampleTransferCount
   *          the maximum count of samples to transfer to set
   */
  public final void setMaxSampleTransferCount( Integer maxSampleTransferCount )
  {
    this.maxSampleTransferCount = maxSampleTransferCount;
  }
  
  /**
   * Getter for the minimum transfer frequency
   * 
   * @return the minimum transfer frequency
   */
  public final Long getMinTransferFrequency()
  {
    return minTransferFrequency;
  }
  
  /**
   * Setter for the minimum transfer frequency
   * 
   * @param minTransferFrequency
   *          the minimum transfer frequency to set
   */
  public final void setMinTransferFrequency( Long minTransferFrequency )
  {
    this.minTransferFrequency = minTransferFrequency;
  }
  
  /**
   * Getter for the transmission protocol configuration
   * 
   * @return the transmission protocol configuration
   */
  public final  TransmissionProtocolConfigurationEntry getProtocolConfig()
  {
    return protocolConfig;
  }
  
  /**
   * Setter for the URL for the file transfer
   * 
   * @param protocolConfig
   *          the transmission protocol configuration
   */
  public final void setProtocolConfig( TransmissionProtocolConfigurationEntry protocolConfig )
  {
    this.protocolConfig = protocolConfig;
  }
  
  /**
   * Getter for the archive type
   * 
   * @return the archive type
   */
  public final String getArchiveType()
  {
    return archiveType;
  }
  
  /**
   * Setter for the archive type
   * 
   * @param archiveType
   *          the archive type to set
   */
  public final void setArchiveType( String archiveType )
  {
    this.archiveType = archiveType;
  }
  
  /**
   * Getter for the encryption enabled flag
  
   * @return the encryption enabled flag
   */
  public Boolean getIsEncryptionEnabled()
  {
    return isEncryptionEnabled;
  }

  /**
   * Setter for the encryption enabled flag
  
   * @param isEncryptionEnabled the encryption enabled flag to set
   */
  public void setIsEncryptionEnabled( Boolean isEncryptionEnabled )
  {
    this.isEncryptionEnabled = isEncryptionEnabled;
  }  
}
