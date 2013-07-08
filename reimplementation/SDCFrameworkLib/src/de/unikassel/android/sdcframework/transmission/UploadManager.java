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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import android.content.Context;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategy;
import de.unikassel.android.sdcframework.transmission.facade.UpdatableTransmissionComponent;
import de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy;
import de.unikassel.android.sdcframework.util.FileUtils;

/**
 * The manager responsible to handle the file upload to a remote server.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class UploadManager
    implements UpdatableTransmissionComponent<TransmissionConfiguration>
{  
  /**
   * The connection strategy to prepare Internet access via URLConnection
   */
  protected ConnectionStrategy connectionStrategy;
  
  /**
   * The strategy implementing the protocol
   */
  protected ProtocolStrategy protocolStrategy;
  
  /**
   * The unique device identifier created by the service
   */
  private final UUID uuid;
  
  /**
   * The control activity for notification purpose.
   */
  private final Class< ? > controlActivityClass;
  
  /**
   * Constructor
   * 
   * @param applicationContext
   *          the application context
   * @param config
   *          the current transmission configuration
   * @param uuid
   *          the unique device identifier created by the service
   * @param controlActivityClass
   *          the control activity class or null
   */
  public UploadManager( Context applicationContext,
      TransmissionConfiguration config, UUID uuid,
      Class< ? > controlActivityClass )
  {
    this.uuid = uuid;
    this.controlActivityClass = controlActivityClass;
    updateConfiguration( applicationContext, config );
  }
  
  /**
   * Getter for the connectionStrategy
   * 
   * @return the connectionStrategy
   */
  public synchronized ConnectionStrategy getConnectionStrategy()
  {
    return connectionStrategy;
  }
  
  /**
   * Getter for the protocolStrategy
   * 
   * @return the protocolStrategy
   */
  public synchronized ProtocolStrategy getProtocolStrategy()
  {
    return protocolStrategy;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.transmission.facade.
   * UpdatableTransmissionComponent#updateConfiguration(android.content.Context,
   * de
   * .unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * )
   */
  @Override
  public synchronized void updateConfiguration( Context context,
      TransmissionConfiguration config )
  {
    connectionStrategy =
        ConnectionStrategyBuilder.buildStrategy( config.getProtocolConfiguration(), controlActivityClass );
    protocolStrategy = null;
    TransmissionProtocolConfiguration protocolConfiguration = config.getProtocolConfiguration();
    try
    {
      // determine protocol type
      URL url = new URL( protocolConfiguration.getURL() );
      String protocol = url.getProtocol();
      
      if( "http".equals( protocol ) )
      {
        // HTTP the only protocol we do support right now
        protocolStrategy = new BasicAuthHttpProtocol( context, uuid, protocolConfiguration );
      }
    }
    catch ( MalformedURLException e )
    {}
    
    if( protocolStrategy == null )
    {
      // unknown protocol
      protocolStrategy = new UnknownProtocol( context, uuid, protocolConfiguration );
    }
  }
  
  /**
   * Does upload a file to the configured remote server using the given
   * authentication data
   * 
   * @param fileName
   *          the file to upload
   * @return true if successful, false otherwise
   */
  public synchronized boolean uploadFile( String fileName )
  {
    File file = FileUtils.fileFromPath( fileName );
    
    if ( file.exists() )
    {
      protocolStrategy.setFileName( fileName );
      if( connectionStrategy.doWork( protocolStrategy ) )
      {
        return true;
      }
      protocolStrategy.setFileName( null );
    }
    
    return false;
  }
}
