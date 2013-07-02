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

import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;

/**
 * Interface for the preferences of a transmission protocol. <br/>
 * <br/>
 * 
 * the following preferences can be configured
 * <ul>
 * <li>the remote URL for the file transfer,</li>
 * <li>the user name for authentication,</li>
 * <li>the password for authentication
 * <li>the transmission strategy description.</li>
 * </ul>
 * <br/>
 * <br/>
 * Internal defaults are:
 * <ul>an empty URL,</li>
 * <li>an empty user name,</li>
 * <li>an empty password,
 * <li>a strategy using WLAN only for transmission.</li>
 * </ul>
 * <br/>
 * Internal defaults are used if there is no default configuration available in
 * the XML configuration file of the framework.
 * 
 * @author Katy Hilgenberg
 *
 */
public interface TransmissionProtocolPreference extends
    SinglePreference< TransmissionProtocolConfiguration >
{ 
  /**
   * Getter for the preference for the transfer URL
   * 
   * @return the preference for the transfer URL
   */
  public abstract SinglePreference< String >
      getURLPreference();
  
  /**
   * Getter for the preference for the authentication user name
   * 
   * @return the preference for the authentication user name
   */
  public abstract SinglePreference< String >
      getAuthenticationUserPreference();
  
  /**
   * Getter for the preference for the authentication password
   * 
   * @return the preference for the authentication password
   */
  public abstract SinglePreference< String >
      getAuthenticationPasswordPreference();
  
  /**
   * Getter for the preference for the transmission strategy
   * 
   * @return the preference for the transmission strategy
   */
  public abstract SinglePreference< ConnectionStrategyDescription >
      getTransmissionStrategyPreference();
}
