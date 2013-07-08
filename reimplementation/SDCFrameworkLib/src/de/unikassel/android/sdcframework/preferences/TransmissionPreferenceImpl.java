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
import de.unikassel.android.sdcframework.preferences.facade.SinglePreference;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolPreference;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;

/**
 * Implementation of the preferences for the transmission configuration.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TransmissionPreferenceImpl implements TransmissionPreference
{
  /**
   * The shared key prefix
   */
  private static final String PREFIX = "sdc_transfer";
  
  /**
   * The preference for the minimum count of samples to transfer
   */
  private final IntegerPreference minSampleTransferCountPreference;
  
  /**
   * The preference for the maximum count of samples to transfer
   */
  private final IntegerPreference maxSampleTransferCountPreference;
  
  /**
   * The preference for the minimum transmission frequency in seconds
   */
  private final LongPreference minTransferFrequencyPreference;
  
  /**
   * The preference for the transmission protocol 
   */
  private final TransmissionProtocolPreference protocolPreference;
  
  /**
   * The preference for the archive type
   */
  private final SinglePreference< ArchiveTypes > archiveTypePreference;
  
  /**
   * The preference for the encryption enabled flag
   */
  private final SinglePreference< Boolean > encryptionEnabledPreference;
  
  /**
   * Constructor
   */
  public TransmissionPreferenceImpl()
  {
    super();
    
    this.minSampleTransferCountPreference =
        new IntegerPreference( PREFIX, "min_samples", 100 );
    
    this.maxSampleTransferCountPreference =
        new IntegerPreference( PREFIX, "max_samples", 1000 );
    
    this.minTransferFrequencyPreference =
        new LongPreference( PREFIX, "frequency", 1800000L );
    
    this.protocolPreference = new TransmissionProtocolPreferenceImpl( PREFIX );
    
    this.archiveTypePreference =
        new SinglePreferenceImpl< ArchiveTypes >( PREFIX, "archive",
            ArchiveTypes.zip )
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * de.unikassel.android.sdcframework.preferences.facade.SinglePreference
       * #getConfiguration(android.content.SharedPreferences)
       */
      @Override
      public ArchiveTypes
          getConfiguration( SharedPreferences sharedPreferences )
      {
        ArchiveTypes type = getDefault();
        String typeName =
            sharedPreferences.getString( getKey(), type.toString() );
        try
        {
          type = ArchiveTypes.valueOf( typeName );
        }
        catch ( Exception e )
        {}
        return type;
      }
      
    };
    
    this.encryptionEnabledPreference =
        new BooleanPreference( PREFIX, "encrypt", false );
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
    return "sdc_transmission";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * getConfiguration(android.content.SharedPreferences)
   */
  @Override
  public TransmissionConfiguration getConfiguration(
      SharedPreferences sharedPreferences )
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( getArchiveTypePreference().getConfiguration(
        sharedPreferences ) );
    config.setMaxSampleTransferCount( getMaxSampleTransferCountPreference().getConfiguration(
        sharedPreferences ) );
    config.setMinSampleTransferCount( getMinSampleTransferCountPreference().getConfiguration(
        sharedPreferences ) );
    config.setMinTransferFrequency( getMinTransferFrequencyPreference().getConfiguration(
        sharedPreferences ) );
    config.setProtocolConfiguration( getProtocolPreference().getConfiguration( sharedPreferences ) );
    config.setEncryptionEnabled( getEncryptionEnabledPreference().getConfiguration(
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
  public TransmissionConfiguration getDefault()
  {
    TransmissionConfiguration config = new TransmissionConfigurationImpl();
    config.setArchiveType( getArchiveTypePreference().getDefault() );
    config.setMaxSampleTransferCount( getMaxSampleTransferCountPreference().getDefault() );
    config.setMinSampleTransferCount( getMinSampleTransferCountPreference().getDefault() );
    config.setProtocolConfiguration( getProtocolPreference().getDefault() );
    config.setMinTransferFrequency( getMinTransferFrequencyPreference().getDefault() );
    config.setEncryptionEnabled( getEncryptionEnabledPreference().getDefault() );
    return config;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * setDefault(java.lang.Object)
   */
  @Override
  public void setDefault( TransmissionConfiguration defaultValue )
  {
    getArchiveTypePreference().setDefault( defaultValue.getArchiveType() );
    getMaxSampleTransferCountPreference().setDefault(
        defaultValue.getMaxSampleTransferCount() );
    getMinSampleTransferCountPreference().setDefault(
        defaultValue.getMinSampleTransferCount() );
    getProtocolPreference().setDefault( defaultValue.getProtocolConfiguration() );
    getMinTransferFrequencyPreference().setDefault(
        defaultValue.getMinTransferFrequency() );
    getEncryptionEnabledPreference().setDefault( defaultValue.isEncryptionEnabled() );
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
    return getArchiveTypePreference().testForKey( key ) ||
        getMaxSampleTransferCountPreference().testForKey( key ) ||
        getMinSampleTransferCountPreference().testForKey( key ) ||
        getProtocolPreference().testForKey( key ) ||
        getMinTransferFrequencyPreference().testForKey( key ) ||
        getEncryptionEnabledPreference().testForKey( key ) ;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference
   * #getMinSampleTransferCountPreference()
   */
  @Override
  public SinglePreference< Integer > getMinSampleTransferCountPreference()
  {
    return minSampleTransferCountPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference
   * #getMaxSampleTransferCountPreference()
   */
  @Override
  public SinglePreference< Integer > getMaxSampleTransferCountPreference()
  {
    return maxSampleTransferCountPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference
   * #getMinTransferFrequencyPreference()
   */
  @Override
  public SinglePreference< Long > getMinTransferFrequencyPreference()
  {
    return minTransferFrequencyPreference;
  }
  
  /* (non-Javadoc)
   * @see de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference#getProtocolPreference()
   */
  @Override
  public TransmissionProtocolPreference getProtocolPreference()
  {
    return protocolPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference
   * #getArchiveTypePreference()
   */
  @Override
  public SinglePreference< ArchiveTypes > getArchiveTypePreference()
  {
    return archiveTypePreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference
   * #getEncryptionEnabledPreference()
   */
  @Override
  public SinglePreference< Boolean > getEncryptionEnabledPreference()
  {
    return encryptionEnabledPreference;
  }
  
}
