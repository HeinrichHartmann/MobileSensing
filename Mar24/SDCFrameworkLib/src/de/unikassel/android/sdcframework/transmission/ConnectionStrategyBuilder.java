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
package de.unikassel.android.sdcframework.transmission;

import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.facade.ConnectivityWrapper;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;

/**
 * A Builder for the connection strategy chains. <br/>
 * <br/>
 * This builder does create a chain of connection strategies according to the
 * given @link {@link ConnectionStrategyDescription description} in the
 * transmission service configuration. 
 * 
 * @see UploadManager
 * @author Katy Hilgenberg
 * 
 */
public class ConnectionStrategyBuilder
{
  /**
   * Does build a database full strategy chain as configured by the given
   * definition
   * 
   * @param config
   *          the current transmission service configuration
   * @return the chain of database full strategies represented by the definition
   */
  public static ConnectionStrategy buildStrategy(
      TransmissionProtocolConfiguration config )
  {
    // default is available only strategy
    ConnectivityWrapper connectivityWrapper = ConnectivityWrapperImpl.getInstance();
    ConnectionStrategy strategy = new UseAvailableConnectionStrategy( connectivityWrapper );
    switch ( config.getTransmissionStrategy() )
    {
      case wlan:
      {
        strategy = new WLANConnectionStrategy( connectivityWrapper );
        break;
      }
      case mobile_connection:
      {
        strategy = new MobileConnectionStrategy( connectivityWrapper );
        break;
      }
      case wlan_else_mobile:
      {
        strategy = new WLANConnectionStrategy( connectivityWrapper );
        strategy.setSuccessor( new MobileConnectionStrategy( connectivityWrapper ) );
        break;
      }
    }
    return strategy;
  }
}
