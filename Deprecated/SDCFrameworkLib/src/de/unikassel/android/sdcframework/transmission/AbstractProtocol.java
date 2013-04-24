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
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.facade.Encryption;

/**
 * Abstract base class for protocol types.
 * 
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractProtocol
    implements ProtocolStrategy
{
  
  /**
   * The file not found message
   */
  public static final String FILE_NOT_FOUND = "File not found";
  
  /**
   * The invalid URL message
   */
  public static final String INVALID_URL = "Invalid URL";
  
  /**
   * The unknown protocol message
   */
  public static final String UNKNOWN_PROTCOL = "Unknown protocol";
  
  /**
   * The URL to upload files to
   */
  private URL url;
  
  /**
   * The user name for authentication
   */
  private String userName;
  
  /**
   * The password for authentication
   */
  private String authPassword;
  
  /**
   * The current file to upload
   */
  private String fileName;
  
  /**
   * the application context to use
   */
  protected final Context context;
  
  /**
   * The unique SDC installation identifier for this device
   */
  private final UUID uuid;
  
  /**
   * The last error message
   */
  protected String lastError;
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   * @param uuid
   *          the unique SDC installation identifier for this device
   * @param config
   *          the current transmission configuration
   */
  public AbstractProtocol( Context context, UUID uuid,
      TransmissionProtocolConfiguration config )
  {
    this.context = context;
    this.uuid = uuid;
    this.lastError = null;
    updateConfiguration( context, config );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * getURL()
   */
  @Override
  public final synchronized URL getURL()
  {
    return url;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * getHost()
   */
  @Override
  public final String getHost()
  {
    if ( url != null )
      return url.getHost();
    return null;
  }
  
  /**
   * Setter for the url
   * 
   * @param url
   *          the url to set
   */
  protected final void setURL( URL url )
  {
    this.url = url;
  }
  
  /**
   * Getter for the userName
   * 
   * @return the userName
   */
  protected final String getUserName()
  {
    return userName;
  }
  
  /**
   * Getter for the authPassword
   * 
   * @return the authPassword
   */
  protected final String getAuthPassword()
  {
    return authPassword;
  }
  
  /**
   * Getter for the authPassword
   * 
   * @return the authPassword
   */
  protected final String getMd5Password()
  {
    return Encryption.md5( authPassword );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * getContext()
   */
  @Override
  public final Context getContext()
  {
    return context;
  }
  
  /**
   * Getter for the uuid
  
   * @return the uuid
   */
  protected UUID getUuid()
  {
    return uuid;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * setFileName(java.lang.String)
   */
  @Override
  public final void setFileName( String fileName )
  {
    this.fileName = fileName;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * getFileName()
   */
  @Override
  public final String getFileName()
  {
    return fileName;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * setLastError(java.lang.String)
   */
  @Override
  public final void setLastError( String lastError )
  {
    this.lastError = lastError;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * getLastError()
   */
  @Override
  public final String getLastError()
  {
    return lastError;
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
  public final synchronized void updateConfiguration( Context context,
      TransmissionProtocolConfiguration config )
  {
    authPassword = config.getAuthPassword();
    userName = config.getUserName();
    try
    {
      url = null;
      url = new URL( config.getURL() );
    }
    catch ( MalformedURLException e )
    {
      Logger.getInstance().error( this, INVALID_URL + ": " + config.getURL() );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * uploadFile()
   */
  @Override
  public synchronized final boolean uploadFile()
  {
    if ( fileName != null )
    {
      if ( url != null )
      {
        setLastError( null );
        File fileFromPath = FileUtils.fileFromPath( fileName );
        if ( fileFromPath.exists() )
        {
          return doUploadFile( fileFromPath );
        }
        else
        {
          // set a last error
          setLastError( FILE_NOT_FOUND + ": " + fileName );
        }
      }
      else
      {
        doHandleError( INVALID_URL );
      }
    }
    return false;
  }
  
  /**
   * Error handler
   * 
   * @param errorMsg
   *          the error massage
   */
  protected final void doHandleError( String errorMsg )
  {
    setLastError( errorMsg );
    Logger.getInstance().warning( this, lastError );
  }
  
  /**
   * Does upload a file according to the concrete protocol
   * 
   * @param file
   *          the file for upload
   * @return true if successful, false otherwise ( last error will be set in
   *         this case )
   */
  protected abstract boolean doUploadFile( File file );
}