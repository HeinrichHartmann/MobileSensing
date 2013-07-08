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

import java.net.URL;
import java.util.UUID;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;

/**
 * Implementation of the HTTP protocol with basic authentication.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class BasicAuthHttpProtocol extends SimpleHttpProtocol

{
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
  public BasicAuthHttpProtocol( Context context, UUID uuid,
      TransmissionProtocolConfiguration config )
  {
    super( context, uuid, config );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.transmission.SimpleHttpProtocol#
   * configureForAuthentication(org.apache.http.impl.client.DefaultHttpClient,
   * java.net.URL)
   */
  @Override
  protected final void configureForAuthentication( DefaultHttpClient client,
      URL url )
  {
    client.getCredentialsProvider().setCredentials(
        new AuthScope( url.getHost(), -1 ),
        new UsernamePasswordCredentials( getUserName(),
            getMd5Password() ) );
  }
}
