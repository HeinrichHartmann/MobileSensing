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

import java.security.InvalidParameterException;

import android.content.Context;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.facade.ConnectivityWrapper;
import de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy;
import de.unikassel.android.sdcframework.util.AbstractChainWorker;
import de.unikassel.android.sdcframework.util.NotificationUtils;

/**
 * Abstract base class for connection strategy types, knowing a successor to
 * delegate to if the strategy fails. <br/>
 * <br/>
 * 
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractConnectionStrategy
    extends AbstractChainWorker< ProtocolStrategy >
    implements ConnectionStrategy
{
  /**
   * The notification id
   */
  public final static int NOTIFICATION = R.id.UploadNotification;
  
  /**
   * The the connectivity wrapper to access network information
   */
  private final ConnectivityWrapper connectivityWrapper;
  
  /**
   * Constructor
   * 
   * @param connectivityWrapper
   *          the connectivity wrapper to use
   */
  public AbstractConnectionStrategy( ConnectivityWrapper connectivityWrapper )
  {
    super();
    if ( connectivityWrapper == null )
      throw new InvalidParameterException( "connectivityWrapper is null " );
    this.connectivityWrapper = connectivityWrapper;
  }
  
  /**
   * Getter for the connectivity wrapper
  
   * @return the connectivity wrapper
   */
  protected ConnectivityWrapper getConnectivityWrapper()
  {
    return connectivityWrapper;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.AbstractChainWorker#doWork(java.
   * lang.Object)
   */
  @Override
  public final boolean doWork( ProtocolStrategy protocolStrategy )
  {
    boolean success = super.doWork( protocolStrategy );
    String lastError = protocolStrategy.getLastError();
    
    // if last error is set notify user
    if ( lastError != null )
    {
      NotificationUtils.serviceNotification( NOTIFICATION, lastError,
          protocolStrategy.getContext(), false, true );
      protocolStrategy.setLastError( null );
    }
    return success;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.AbstractChainWorker#process(java
   * .lang.Object)
   */
  @Override
  protected final boolean process( ProtocolStrategy protocolStrategy )
  {
    if ( isConnectionAvailable( protocolStrategy ) )
    {
      // return true if successful
      return protocolStrategy.uploadFile();
    }
    else
    {
      // if the protocol did not fail so far, we do have a connection problem
      if ( protocolStrategy.getLastError() == null )
        protocolStrategy.setLastError( "No Internet connection available!" );
    }
    return false;
  }
  
  /**
   * Internal method to test for an available connection of a specific type
   * 
   * @param context
   *          the application context
   * @param networkType
   *          the network type
   * @param hostName
   *          the host name if a rout to host shall be tested, otherwise null
   * 
   * @return true if a connection is available, false otherwise
   */
  protected final boolean isConnectionAvailable( Context context,
      int networkType, String hostName )
  {
    if ( getConnectivityWrapper().isNetworkConnected( context, networkType ) )
    {
      if ( hostName != null )
      {
        return getConnectivityWrapper().testHostReachability( context, hostName );
      }
      return true;
    }
    return false;
  }
}
