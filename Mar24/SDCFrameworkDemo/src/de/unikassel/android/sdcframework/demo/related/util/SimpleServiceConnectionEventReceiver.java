/*
 * Copyright (C) 2012, Katy Hilgenberg.
 * Special acknowledgments to: Knowledge & Data Engineering Group, University of Kassel (http://www.kde.cs.uni-kassel.de).
 * Contact: sdcf@cs.uni-kassel.de
 *
 * This file is part of the SDCFramework project.
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
package de.unikassel.android.sdcframework.demo.related.util;

import android.app.Activity;
import android.widget.Toast;
import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder;

/**
 * A simple SDC service connection receiver implementation.
 * 
 * @author Katy Hilgenberg, Martin Atzmueller
 * 
 */
public class SimpleServiceConnectionEventReceiver
    implements SDCServiceConnectionHolder.ServiceConnectionEventReceiver
{
  private static final String SDCF_SERVICE_NOT_AVAILABLE = "The SDC Framework Service is not available!";

  private static final String CONNECTION_TO_SDCF_SERVICE_LOST = "Connection to SDC Framework Service lost!";

  private static final String FAILED_TO_DISABLE_SAMPLE_BROADCASTING = "Failed to disable sample broadcasting!";

  private static final String FAILED_TO_ACTIVATE_SDCF_SERVICE_SAMPLE_BROADCASTING = "Failed to activate the SDC Framework Service sample broadcast feature!";

/**
   * The context activity
   */
  private final Activity activity;
  
  /**
   * Flag for the service availability
   */
  private boolean serviceNotAvailable;
  
  /**
   * Constructor
   * 
   * @param activity
   *          the context activity
   */
  public SimpleServiceConnectionEventReceiver( Activity activity )
  {
    super();
    this.activity = activity;
  }
  
  /**
   * Setter for the service unavailability flag
   * 
   * @param serviceNotAvailable
   *          the service unavailability flag to set
   */
  protected synchronized final void setServiceNotAvailable(
      boolean serviceNotAvailable )
  {
    this.serviceNotAvailable = serviceNotAvailable;
  }
  
  /**
   * Getter for the service unavailability flag
   * 
   * @return the service unavailability flag
   */
  public synchronized final boolean isServiceNotAvailable()
  {
    return serviceNotAvailable;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.util.SDCServiceConnectionHolder
   * .ServiceConnectionEventReceiver
   * #onConnectionEstablished(de.unikassel.android
   * .sdcframework.app.facade.ISDCService)
   */
  @Override
  public void onConnectionEstablished( ISDCService sdcService )
  {
    try
    {
      sdcService.doEnableSampleBroadCasting( true );
    }
    catch ( Exception e )
    {
      displayWarning( FAILED_TO_ACTIVATE_SDCF_SERVICE_SAMPLE_BROADCASTING );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.util.SDCServiceConnectionHolder
   * .ServiceConnectionEventReceiver
   * #onAboutToDisconnect(de.unikassel.android.sdcframework
   * .app.facade.ISDCService)
   */
  @Override
  public void onAboutToDisconnect( ISDCService sdcService )
  {
    try
    {
      sdcService.doEnableSampleBroadCasting( false );
    }
    catch ( Exception e )
    {
      displayWarning( FAILED_TO_DISABLE_SAMPLE_BROADCASTING );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.util.SDCServiceConnectionHolder
   * .ServiceConnectionEventReceiver#onConnectionLost()
   */
  @Override
  public void onConnectionLost()
  {
    displayWarning( CONNECTION_TO_SDCF_SERVICE_LOST );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.util.SDCServiceConnectionHolder
   * .ServiceConnectionEventReceiver#onBindingFailed()
   */
  @Override
  public void onBindingFailed()
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.util.SDCServiceConnectionHolder
   * .ServiceConnectionEventReceiver#onServiceUnavailable()
   */
  @Override
  public void onServiceUnavailable()
  {
    setServiceNotAvailable( true );
    displayWarning( SDCF_SERVICE_NOT_AVAILABLE );
  }
  
  /**
   * Does display a warning
   * 
   * @param text
   *          the warning text to display
   */
  private void displayWarning( String text )
  {
    Toast.makeText( activity.getApplicationContext(), text, Toast.LENGTH_LONG ).show();
  }
}
