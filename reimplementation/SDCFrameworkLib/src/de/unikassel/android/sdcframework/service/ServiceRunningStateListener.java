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
package de.unikassel.android.sdcframework.service;

import java.security.InvalidParameterException;

import de.unikassel.android.sdcframework.app.facade.SDCService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * The broadcast receiver for the SDCService running state. <br/<br/>
 * As the service running state changes will be signaled by broadcasted intents, this
 * listener is used to track the state changes in the
 * {@linkplain de.unikassel.android.sdcframework.app.AbstractServiceControlActivity
 * service control activity }.
 * 
 * 
 * @author Katy Hilgenberg
 * 
 */
public abstract class ServiceRunningStateListener
    extends BroadcastReceiver
{
  /**
   * the action string for the intent
   */
  private final String action;
  
  /**
   * Constructor
   * @param action the action string of the intent to listen for
   */
  public ServiceRunningStateListener( String action )
  {
    super();
    if( action == null )
      throw new InvalidParameterException( "action cannot be null" );
    this.action = action;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
   * android.content.Intent)
   */
  @Override
  public void onReceive( Context context, Intent intent )
  {
    if ( action.equals( intent.getAction() ) )
    {
      boolean isRunning =
          intent.getBooleanExtra( SDCService.INTENT_NAME_RUNNING_FLAG, false );
      serviceStateChanged( isRunning );
    }
  }
  
  /**
   * Running state change handler
   * 
   * @param isRunning
   *          flag if the service is running
   */
  protected abstract void serviceStateChanged( boolean isRunning );
}
