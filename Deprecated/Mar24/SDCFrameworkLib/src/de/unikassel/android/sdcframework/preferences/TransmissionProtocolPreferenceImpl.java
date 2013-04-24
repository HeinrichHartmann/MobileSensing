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
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolPreference;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;

/**
 * Implementation of the preferences for a transmission protocol configuration.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TransmissionProtocolPreferenceImpl implements
    TransmissionProtocolPreference
{
  /**
   * The preference for the URL for file transfer
   */
  private final StringPreference urlPreference;
  
  /**
   * The preference for the authentication user name
   */
  private final StringPreference authUserPreference;
  
  /**
   * The preference for the authentication password
   */
  private final StringPreference authPasswordPreference;
  
  /**
   * The preference for the transmission strategy
   */
  private final SinglePreference< ConnectionStrategyDescription > transmissionStrategyPreference;
  
  /**
   * Constructor
   * 
   * @param prefix
   *          the prefix from the parent entry to use for the key name
   */
  public TransmissionProtocolPreferenceImpl( String prefix )
  {
    super();
    
    this.urlPreference = new StringPreference( prefix, "url", "" );
    
    this.authUserPreference = new StringPreference( prefix, "user", "" );
    
    this.authPasswordPreference = new StringPreference( prefix, "password", "" );
    
    this.transmissionStrategyPreference =
        new SinglePreferenceImpl< ConnectionStrategyDescription >( prefix,
            "strategy",
            ConnectionStrategyDescription.any_available )
    {
      
      /*
       * (non-Javadoc)
       * 
       * @see
       * de.unikassel.android.sdcframework.preferences.facade.SinglePreference
       * #getConfiguration(android.content.SharedPreferences)
       */
      @Override
      public ConnectionStrategyDescription getConfiguration(
          SharedPreferences sharedPreferences )
      {
        ConnectionStrategyDescription strategy = getDefault();
        String strategyName =
            sharedPreferences.getString( getKey(), strategy.toString() );
        try
        {
          strategy = ConnectionStrategyDescription.valueOf( strategyName );
        }
        catch ( Exception e )
        {}
        return strategy;
      }
      
    };
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * getConfiguration(android.content.SharedPreferences)
   */
  @Override
  public TransmissionProtocolConfiguration getConfiguration(
      SharedPreferences sharedPreferences )
  {
    TransmissionProtocolConfiguration config =
        new TransmissionProtocolConfigurationImpl();
    config.setAuthPassword( getAuthenticationPasswordPreference().getConfiguration(
        sharedPreferences ) );
    config.setURL( getURLPreference().getConfiguration(
        sharedPreferences ) );
    config.setTransmissionStrategy( getTransmissionStrategyPreference().getConfiguration(
        sharedPreferences ) );
    config.setUserName( getAuthenticationUserPreference().getConfiguration(
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
  public TransmissionProtocolConfiguration getDefault()
  {
    TransmissionProtocolConfiguration config =
        new TransmissionProtocolConfigurationImpl();
    config.setAuthPassword( getAuthenticationPasswordPreference().getDefault() );
    config.setURL( getURLPreference().getDefault() );
    config.setTransmissionStrategy( getTransmissionStrategyPreference().getDefault() );
    config.setUserName( getAuthenticationUserPreference().getDefault() );
    return config;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.preferences.facade.SinglePreference#
   * setDefault(java.lang.Object)
   */
  @Override
  public void setDefault( TransmissionProtocolConfiguration defaultValue )
  {
    getAuthenticationPasswordPreference().setDefault(
        defaultValue.getAuthPassword() );
    getAuthenticationUserPreference().setDefault( defaultValue.getUserName() );
    getURLPreference().setDefault( defaultValue.getURL() );
    getTransmissionStrategyPreference().setDefault(
        defaultValue.getTransmissionStrategy() );
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
    return getAuthenticationPasswordPreference().testForKey( key ) ||
        getAuthenticationUserPreference().testForKey( key ) ||
        getURLPreference().testForKey( key ) ||
        getTransmissionStrategyPreference().testForKey( key );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference
   * #getHostPreference()
   */
  @Override
  public SinglePreference< String > getURLPreference()
  {
    return urlPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference
   * #getAuthenticationUserPreference()
   */
  @Override
  public SinglePreference< String > getAuthenticationUserPreference()
  {
    return authUserPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference
   * #getAuthenticationPasswordPreference()
   */
  @Override
  public SinglePreference< String > getAuthenticationPasswordPreference()
  {
    return authPasswordPreference;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference
   * #getTransmissionStrategyPreference()
   */
  @Override
  public SinglePreference< ConnectionStrategyDescription >
      getTransmissionStrategyPreference()
  {
    return transmissionStrategyPreference;
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
  
}
