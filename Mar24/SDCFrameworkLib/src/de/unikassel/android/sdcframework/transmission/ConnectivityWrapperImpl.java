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

import java.net.InetAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import de.unikassel.android.sdcframework.transmission.facade.ConnectivityWrapper;

/**
 * Implementation of the connectivity wrapper.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ConnectivityWrapperImpl
    implements ConnectivityWrapper
{
  /**
   * The singleton instance
   */
  private static ConnectivityWrapper instance = new ConnectivityWrapperImpl();;
  
  /**
   * Constructor
   */
  private ConnectivityWrapperImpl()
  {
    super();
  }
  
  /**
   * Getter for the singleton instance
   * 
   * @return the singleton instance
   */
  public static final ConnectivityWrapper getInstance()
  {
    return instance;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ConnectivityWrapper
   * #isNetworkConnected(int)
   */
  @Override
  public final boolean isNetworkConnected( Context context, int networkType )
  {
    if ( networkType < 0
        || ConnectivityManager.isNetworkTypeValid( networkType ) )
    {
      ConnectivityManager connectivityManager =
          (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
      NetworkInfo networkTypeInfo =
          networkType < 0 ? connectivityManager.getActiveNetworkInfo() :
              connectivityManager.getNetworkInfo( networkType );
      if ( networkTypeInfo != null )
      {
        if ( networkTypeInfo.isConnectedOrConnecting() )
        {
          return true;
        }
      }
    }
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ConnectivityWrapper
   * #testHostReachability(android.content.Context, java.lang.String)
   */
  @Override
  public final boolean testHostReachability( Context context, String hostName )
  {
//    ConnectivityManager connectivityManager =
//        (ConnectivityManager) context.getSystemService( Context.CONNECTIVITY_SERVICE );
//    
//    int hostAddress = addressFromHostName( hostName );
//    
//    // test for existing route to host
//    if ( hostAddress != -1 )
//    {
//      NetworkInfo networkTypeInfo = connectivityManager.getActiveNetworkInfo();
//      if ( networkTypeInfo != null )
//      {
//        return connectivityManager.requestRouteToHost( networkTypeInfo.getType(), hostAddress );
//      }
//
//    }
//    return false;

    try {
      InetAddress.getByName(hostName);
      return true;
    } catch (UnknownHostException e1) {
      return false;
    }
  }
  
  /**
   * Does create an integer host address from a host name.
   * 
   * @param hostname
   *          the host name to get hostAdress for
   * @return an integer representation of the host IP or -1 if not successful
   */
  @SuppressWarnings( "unused" )
  private final static int addressFromHostName( String hostname )
  {
    try
    {
      InetAddress inetAddress = InetAddress.getByName( hostname );
      
      byte[] addessBytes = inetAddress.getAddress();
      int address = ( addessBytes[ 0 ] & 0xff )
              | ( ( addessBytes[ 1 ] & 0xff ) << 8 )
              | ( ( addessBytes[ 2 ] & 0xff ) << 16 )
              | ( ( addessBytes[ 3 ] & 0xff ) << 24 );
      
      return address;
    }
    catch ( UnknownHostException e )
    {}
    return -1;
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
    return isNetworkConnected( context, -1 );
  }
  
}
