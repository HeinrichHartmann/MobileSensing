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
import de.unikassel.android.sdcframework.data.TransmissionConfigurationEntry;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;

/**
 * Implementation of the configuration for the transmission module.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TransmissionConfigurationImpl implements TransmissionConfiguration
{
  /**
   * The minimum of samples to transfer
   */
  private int minSampleTransferCount;
  
  /**
   * The maximum of samples to transfer
   */
  private int maxSampleTransferCount;
  
  /**
   * The minimum frequency for the sample transfer in seconds ( the real
   * transfer frequency does depend on many parameters and will differ )
   */
  private long minTransferFrequency;
  
  /**
   * The archive type
   */
  private ArchiveTypes archiveType;
  
  /**
   * The encryption enabled flag
   */
  private boolean isEncryptionEnabled;
  
  /**
   * The protocol configuration
   */
  private final TransmissionProtocolConfiguration protocolConfig;
  
  /**
   * Constructor
   */
  public TransmissionConfigurationImpl()
  {
    super();
    this.protocolConfig = new TransmissionProtocolConfigurationImpl();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.UpdatableConfiguration
   * #update(java.lang.Object)
   */
  @Override
  public void update( TransmissionConfiguration configuration )
  {
    setArchiveType( configuration.getArchiveType() );
    setMaxSampleTransferCount( configuration.getMaxSampleTransferCount() );
    setMinSampleTransferCount( configuration.getMinSampleTransferCount() );
    setMinTransferFrequency( configuration.getMinTransferFrequency() );
    setEncryptionEnabled( configuration.isEncryptionEnabled() );
    getProtocolConfiguration().update( configuration.getProtocolConfiguration() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #
   * update(de.unikassel.android.sdcframework.data.TransmissionConfigurationEntry
   * )
   */
  @SuppressLint( "DefaultLocale" ) 
  @Override
  public void update( TransmissionConfigurationEntry config )
  {
    String tmp = config.getArchiveType();
    if ( tmp != null )
    {
      setArchiveType( ArchiveTypes.valueOf( tmp.toLowerCase() ) );
    }
    setMaxSampleTransferCount( config.getMaxSampleTransferCount() );
    setMinSampleTransferCount( config.getMinSampleTransferCount() );
    getProtocolConfiguration().update( config.getProtocolConfig() );
    setMinTransferFrequency( config.getMinTransferFrequency() );
    setEncryptionEnabled( config.getIsEncryptionEnabled() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o )
  {
    if ( o instanceof TransmissionConfiguration )
    {
      TransmissionConfiguration conf = (TransmissionConfiguration) o;
      return conf.getMinSampleTransferCount() == getMinSampleTransferCount() &&
          conf.getMaxSampleTransferCount() == getMaxSampleTransferCount() &&
          conf.getMinTransferFrequency() == getMinTransferFrequency() &&
          conf.getProtocolConfiguration().equals( getProtocolConfiguration() ) &&
          equals( conf.getArchiveType(), getArchiveType() ) &&
          equals( conf.isEncryptionEnabled(), isEncryptionEnabled() );
    }
    return false;
  }
  
  /**
   * Test method for equivalence of two objects, allowing both being null as
   * well
   * 
   * @param o1
   *          first object
   * @param o2
   *          second object
   * @return true if equal pointers or equal values
   */
  private static final boolean equals( Object o1, Object o2 )
  {
    if ( o1 != null && o2 != null )
    {
      // both objects are initialized return comparison value
      return o1.equals( o2 );
    }
    // at least one object is null -> return true if both are null
    return o1 == o2;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #getMinSampleTransferCount()
   */
  @Override
  public int getMinSampleTransferCount()
  {
    return minSampleTransferCount;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #setMinSampleTransferCount(int)
   */
  @Override
  public void setMinSampleTransferCount( Integer minSampleTransferCount )
  {
    if ( minSampleTransferCount != null )
      this.minSampleTransferCount = minSampleTransferCount;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #getMaxSampleTransferCount()
   */
  @Override
  public int getMaxSampleTransferCount()
  {
    return maxSampleTransferCount;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #setMaxSampleTransferCount(int)
   */
  @Override
  public void setMaxSampleTransferCount( Integer maxSampleTransferCount )
  {
    if ( maxSampleTransferCount != null )
      this.maxSampleTransferCount = maxSampleTransferCount;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #getMinTransferFrequency()
   */
  @Override
  public long getMinTransferFrequency()
  {
    return minTransferFrequency;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #setMinTransferFrequency(long)
   */
  @Override
  public void setMinTransferFrequency( Long minTransferFrequency )
  {
    if ( minTransferFrequency != null )
      this.minTransferFrequency = minTransferFrequency;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #getArchiveType()
   */
  @Override
  public ArchiveTypes getArchiveType()
  {
    return archiveType;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #setArchiveType(de.unikassel.android.sdcframework.util.facade.ArchiveTypes)
   */
  @Override
  public void setArchiveType( ArchiveTypes archiveType )
  {
    if ( archiveType != null )
      this.archiveType = archiveType;
  }
  
  /*

  /* (non-Javadoc)
   * @see de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration#getProtocolConfiguration()
   */
  @Override
  public TransmissionProtocolConfiguration getProtocolConfiguration()
  {
    return protocolConfig;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #
   * setProtocolConfiguration(de.unikassel.android.sdcframework.preferences.facade
   * .TransmissionProtocolConfiguration)
   */
  @Override
  public void setProtocolConfiguration(
      TransmissionProtocolConfiguration protocolConfig )
  {
    this.protocolConfig.update( protocolConfig );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #isEncryptionEnabled()
   */
  @Override
  public Boolean isEncryptionEnabled()
  {
    return isEncryptionEnabled;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #setEncryptionEnabled(java.lang.Boolean)
   */
  @Override
  public void setEncryptionEnabled( Boolean isEncryptionEnabled )
  {
    if ( isEncryptionEnabled != null )
      this.isEncryptionEnabled = isEncryptionEnabled;
  }
}
