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

import de.unikassel.android.sdcframework.data.TransmissionProtocolConfigurationEntry;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;
import de.unikassel.android.sdcframework.util.ObjectUtils;

/**
 * Implementation of the configuration for a transmission protocol.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TransmissionProtocolConfigurationImpl implements TransmissionProtocolConfiguration
{
  /**
   * The URL for file transfer
   */
  private String url;
  
  /**
   * The user name for authentication
   */
  private String userName;
  
  /**
   * The password for authentication
   */
  private String authPassword;
  
  /**
   * The transmission strategy description
   */
  private ConnectionStrategyDescription strategy;
  
  /**
   * Constructor
   */
  public TransmissionProtocolConfigurationImpl()
  {
    super();
  }
  
   @Override
  public void update( TransmissionProtocolConfiguration configuration )
  {
    setAuthPassword( configuration.getAuthPassword() );
    setURL( configuration.getURL() );
    setTransmissionStrategy( configuration.getTransmissionStrategy() );
    setUserName( configuration.getUserName() );
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
  @Override
  public void update( TransmissionProtocolConfigurationEntry config )
  {
    setAuthPassword( config.getAuthPassword() );
    setURL( config.getURL() );
    String tmp = config.getConnectionStrategy();
    if ( tmp != null )
    {
      setTransmissionStrategy( ConnectionStrategyDescription.valueOf( tmp ) );
    }
    setUserName( config.getUserName() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o )
  {
    if ( o instanceof TransmissionProtocolConfiguration )
    {
      TransmissionProtocolConfiguration conf = (TransmissionProtocolConfiguration) o;
      return ObjectUtils.equals( conf.getURL(), getURL() ) &&
          ObjectUtils.equals( conf.getUserName(), getUserName() ) &&
          ObjectUtils.equals( conf.getAuthPassword(), getAuthPassword() ) &&
         ObjectUtils.equals( conf.getTransmissionStrategy(), getTransmissionStrategy() );
    }
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #getHost()
   */
  @Override
  public String getURL()
  {
    return url;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #setHost(java.lang.String)
   */
  @Override
  public void setURL( String url )
  {
    if ( url != null )
      this.url = url;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #getUserName()
   */
  @Override
  public String getUserName()
  {
    return userName;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #setUserName(java.lang.String)
   */
  @Override
  public void setUserName( String userName )
  {
    if ( userName != null )
      this.userName = userName;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #getAuthPassword()
   */
  @Override
  public String getAuthPassword()
  {
    return authPassword;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #setAuthPassword(java.lang.String)
   */
  @Override
  public void setAuthPassword( String authPassword )
  {
    if ( authPassword != null )
      this.authPassword = authPassword;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #getTransmissionStrategy()
   */
  @Override
  public ConnectionStrategyDescription getTransmissionStrategy()
  {
    return strategy;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * #
   * setTransmissionStrategy(de.unikassel.android.sdcframework.transmission.facade
   * .TransmissionStrategyDescription)
   */
  @Override
  public void setTransmissionStrategy(
      ConnectionStrategyDescription transmissionStrategy )
  {
    if ( transmissionStrategy != null )
      this.strategy = transmissionStrategy;
  }
}
