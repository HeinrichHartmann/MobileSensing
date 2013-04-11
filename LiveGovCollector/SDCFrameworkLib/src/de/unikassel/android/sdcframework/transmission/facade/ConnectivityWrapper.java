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
package de.unikassel.android.sdcframework.transmission.facade;

import android.content.Context;

/**
 * Interface for a connectivity wrapper. 
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface ConnectivityWrapper
{
  /**
   * Test method for an available network connection
   * 
   * @param context
   *          the application context
   * @param networkType
   *          the {@link android.net.ConnectivityManager network type}
   * @return true if the requested network type is valid and connected
   */
  public abstract boolean isNetworkConnected( Context context, int networkType );
  
  /**
   * Does test if the host is reachable
   * 
   * @param context
   *          the application context
   * @param hostName
   *          the host name to detect route to
   * 
   * @return true if there is a route to host, false otherwise
   */
  public abstract boolean testHostReachability(
      Context context, String hostName );

  /**
   * Test method for any available Internet connection
   * 
   * @param context
   *          the application context
   * @return true if any Internet connection is available, false otherwise
   */
  public abstract boolean isAnyConnectionAvailable( Context context );
}
