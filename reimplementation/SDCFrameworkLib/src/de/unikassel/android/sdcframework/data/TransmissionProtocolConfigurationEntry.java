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
 * A transmission protocol configuration does describe the default configuration settings
 * for a transmission protocol. <br/>
 * <br/>
 * The following settings can be configured:
 * <ul>
 * <li>the URL for the remote server receiving the archive ( protocol, IP, port etc. ),</li>
 * <li>the user authentication for the remote server,</li>
 * <li>and the
 * {@link de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription
 * description of the transfer connection strategy}.</li>
 * </ul>
 * 
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "protocolConfig" )
public final class TransmissionProtocolConfigurationEntry
{
  /**
   * The URL for the file transfer
   */
  @Element( name = "url", required = false )
  private String url;
  
  /**
   * The user name for authentication
   */
  @Element( name = "authName", required = false )
  private String userName;
  
  /**
   * The password for authentication
   */
  @Element( name = "authPassword", required = false )
  private String authPassword;
  
  
  /**
   * The transfer connection strategy description
   */
  @Element( name = "transferStrategy", required = false )
  private String connectionStrategy;
  
  /**
   * Constructor
   */
  public TransmissionProtocolConfigurationEntry()
  {}
  
  /**
   * Getter for the URL for the file transfer
   * 
   * @return the URL for the file transfer
   */
  public final String getURL()
  {
    return url;
  }
  
  /**
   * Setter for the URL for the file transfer
   * 
   * @param url
   *          the URL for the file transfer
   */
  public final void setURL( String url )
  {
    this.url = url;
  }
  
  /**
   * Getter for the authentication user name
   * 
   * @return the authentication user name
   */
  public final String getUserName()
  {
    return userName;
  }
  
  /**
   * Setter for the authentication user name
   * 
   * @param userName
   *          the authentication user name to set
   */
  public final void setUserName( String userName )
  {
    this.userName = userName;
  }
  
  /**
   * Getter for the authentication password
   * 
   * @return the authentication password
   */
  public final String getAuthPassword()
  {
    return authPassword;
  }
  
  /**
   * Setter for the authentication password
   * 
   * @param authPassword
   *          the authentication password to set
   */
  public final void setAuthPassword( String authPassword )
  {
    this.authPassword = authPassword;
  }

  /**
   * Getter for the transfer connection strategy description
   * 
   * @return the transfer connection strategy description
   */
  public final String getConnectionStrategy()
  {
    return connectionStrategy;
  }
  
  /**
   * Setter for the transfer connection strategy description
   * 
   * @param connectionStrategy
   *          the transfer connection strategy description to set
   */
  public final void setConnectionStrategy( String connectionStrategy )
  {
    this.connectionStrategy = connectionStrategy;
  }  
}
