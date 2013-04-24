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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of a simple HTTP protocol without authentication.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class SimpleHttpProtocol extends AbstractProtocol

{
  /**
   * The IO exception message
   */
  private static final String IO_EXCEPTION = "IO exception";
  
  /**
   * The protocol exception message
   */
  private static final String PROTOCOL_EXCEPTION = "Protocol exception";
  
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
  public SimpleHttpProtocol( Context context, UUID uuid,
      TransmissionProtocolConfiguration config )
  {
    super( context, uuid, config );
  }
  
  /**
   * Method for an HTTP upload of file stream
   * 
   * @param file
   *          the input file
   * 
   * @return true if successful, false otherwise
   */
  private final boolean httpUpload( File file )
  {
    DefaultHttpClient client = new DefaultHttpClient();
    
    try
    {
      String fileName = FileUtils.fileNameFromPath( file.getName() );
      String contentType = getContentType( fileName );
      
      URL url = getURL();
      configureForAuthentication( client, url );
      
      HttpPost httpPost = new HttpPost( url.toURI() );
      
      FileEntity fileEntity = new FileEntity( file, contentType );
      fileEntity.setContentType( contentType );
      fileEntity.setChunked( true );
      
      httpPost.setEntity( fileEntity );
      httpPost.addHeader( "filename", fileName );
      httpPost.addHeader( "uuid", getUuid().toString() );
      
      HttpResponse response = client.execute( httpPost );
      
      int statusCode = response.getStatusLine().getStatusCode();
      boolean success =
          statusCode == HttpStatus.SC_OK ||
              statusCode == HttpStatus.SC_NO_CONTENT;
      Logger.getInstance().debug( this, "Server returned: " + statusCode );
      
      // clean up if necessary
      HttpEntity resEntity = response.getEntity();
      if ( resEntity != null )
      {
        resEntity.consumeContent();
      }
      
      if ( !success )
      {
        doHandleError( "Unexpected server response: "
            + response.getStatusLine() );
        success = false;
      }
      
      return success;
    }
    catch ( ClientProtocolException e )
    {
      e.printStackTrace();
      doHandleError( PROTOCOL_EXCEPTION + ": " + e.getMessage() );
    }
    catch ( IOException e )
    {
      e.printStackTrace();
      doHandleError( IO_EXCEPTION + ": " + e.getMessage() );
    }
    catch ( URISyntaxException e )
    {
      setURL( null );
      doHandleError( INVALID_URL );
    }
    finally
    {
      client.getConnectionManager().shutdown();
    }
    return false;
  }
  
  /**
   * Method to configure the client for authentication
   * 
   * @param client
   *          the default http client instance to configure
   * @param url
   *          the upload url
   */
  protected void configureForAuthentication( DefaultHttpClient client,
      URL url )
  {
    // nothing to do for HTTP protocol without authentication
  }
  
  /**
   * Method to determine the file type
   * 
   * @param fileName
   *          the file name
   * @return the content mime type for the file
   */
  private final String getContentType( String fileName )
  {
    String contentType = "application/octet-stream";
    String ext = FileUtils.fileNameFromPath( fileName );
    ext = ext.substring( ext.lastIndexOf( '.' ) + 1 );
    if ( ext.equals( "txt" ) )
    {
      contentType = "text/plain";
    }
    else if( ext.equals( "jar" ) )
    {
      contentType = "application/java-archive";
    }
    else if ( ext.equals( "zip" ) )
    {
      contentType = "application/zip";
    }
    return contentType;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.transmission.AbstractProtocol#
   * doUploadFromStream(java.io.File)
   */
  @Override
  protected final boolean doUploadFile( File file )
  {
    return httpUpload( file );
  }
}
