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

import de.unikassel.android.sdcframework.data.TransmissionProtocolConfigurationEntry;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;

/**
 * The configuration for a transmission protocol.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface TransmissionProtocolConfiguration
extends UpdatableConfiguration< TransmissionProtocolConfiguration >
{
  
  /**
   * Does update this configuration by a serializable configuration
   * 
   * @param config
   *          the serializable configuration to update from
   */
  public abstract void update( TransmissionProtocolConfigurationEntry config );
  
  /**
   * Getter for the URL for file transfer
   * 
   * @return the URL for file transfer
   */
  public abstract String getURL();
  
  /**
   * Setter for the URL for file transfer
   * 
   * @param url
   *          the URL for file transfer
   */
  public abstract void setURL( String url );
  
  /**
   * Getter for the authentication user name
   * 
   * @return the authentication user name
   */
  public abstract String getUserName();
  
  /**
   * Setter for the authentication user name
   * 
   * @param userName
   *          the authentication user name to set
   */
  public abstract void setUserName( String userName );
  
  /**
   * Getter for the authentication password
   * 
   * @return the authentication password
   */
  public abstract String getAuthPassword();
  
  /**
   * Setter for the authentication password
   * 
   * @param authPassword
   *          the authentication password to set
   */
  public abstract void setAuthPassword( String authPassword );
  
  /**
   * Getter for the transfer connection strategy description
   * 
   * @return the transfer connection strategy description
   */
  public abstract ConnectionStrategyDescription getTransmissionStrategy();
  
  /**
   * Setter for the transfer connection strategy description
   * 
   * @param getTransmissionStrategy
   *          the transfer connection strategy description to set
   */
  public abstract void setTransmissionStrategy(
      ConnectionStrategyDescription getTransmissionStrategy );
  
}
