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

import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy;

/**
 * Implementation of a protocol which does never fail, to be used for test
 * purpose.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class FailSafeProtocol
    implements ProtocolStrategy
{
  /**
   * The context
   */
  private final Context context;
  
  /**
   * Constructor
   * 
   * @param context
   *          the application context
   */
  public FailSafeProtocol( Context context )
  {
    super();
    this.context = context;
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
  public void updateConfiguration( Context context,
      TransmissionProtocolConfiguration config )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * uploadFile()
   */
  @Override
  public boolean uploadFile()
  {
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * getContext()
   */
  @Override
  public Context getContext()
  {
    return context;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * getURL()
   */
  @Override
  public URL getURL()
  {
    try
    {
      return new URL( "http://localhost" );
    }
    catch ( MalformedURLException e )
    {}
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * getLastError()
   */
  @Override
  public String getLastError()
  {
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * setLastError(java.lang.String)
   */
  @Override
  public void setLastError( String lastError )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * setFileName(java.lang.String)
   */
  @Override
  public void setFileName( String fileName )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * getFileName()
   */
  @Override
  public String getFileName()
  {
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.transmission.facade.ProtocolStrategy#
   * getHost()
   */
  @Override
  public String getHost()
  {
    URL url = getURL();
    if ( url != null )
      return url.getHost();
    return null;
  }
}