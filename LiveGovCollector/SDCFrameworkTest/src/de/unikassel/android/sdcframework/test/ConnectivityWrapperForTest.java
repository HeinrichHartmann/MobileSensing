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
package de.unikassel.android.sdcframework.test;

import android.content.Context;
import de.unikassel.android.sdcframework.transmission.facade.ConnectivityWrapper;

/**
 * Connectivity wrapper for test purpose.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ConnectivityWrapperForTest implements ConnectivityWrapper
{
  /**
   * The result of the isNetworkConnected method
   */
  public boolean isNetworkConnected = false;
  
  /**
   * The result of the testHostReachability method
   */
  public boolean isHostReachable = false;
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ConnectivityWrapper
   * #isNetworkConnected(android.content.Context, int)
   */
  @Override
  public boolean isNetworkConnected( Context context, int networkType )
  {
    return isNetworkConnected;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ConnectivityWrapper
   * #testHostReachability(android.content.Context, java.lang.String)
   */
  @Override
  public boolean testHostReachability( Context context, String hostName )
  {
    return isHostReachable;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ConnectivityWrapper
   * #isAnyConnectionAvailable(android.content.Context)
   */
  @Override
  public boolean isAnyConnectionAvailable( Context context )
  {
    return isNetworkConnected;
  }
  
}
