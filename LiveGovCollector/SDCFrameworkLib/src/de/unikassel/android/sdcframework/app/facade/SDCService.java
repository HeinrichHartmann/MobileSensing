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
package de.unikassel.android.sdcframework.app.facade;

import de.unikassel.android.sdcframework.service.facade.ServiceManager;
import android.app.Activity;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.os.PowerManager;

/**
 * @author Katy Hilgenberg
 * 
 */
public interface SDCService extends ComponentCallbacks
{
  
  /**
   * Out custom service intent action
   */
  public static final String ACTION =
      "de.unikassel.android.sdcframework.intent.action.SERVICESTATE";
  
  /**
   * The name for the intent extra holding the running flag
   */
  public static final String INTENT_NAME_RUNNING_FLAG = "SDCServiceIsRunning";
  
  /**
   * Getter for the service manager
   * 
   * @return the service manager
   */
  public ServiceManager getServiceManager();
  
  /**
   * Setter for the service manager
   * 
   * @param serviceManager
   *          the service manager to set
   */
  public void setServiceManager( ServiceManager serviceManager );
  
  /**
   * Getter for the service context power manager
   * 
   * @return the power manager in this context
   */
  public PowerManager getPowerManager();
  
  /**
   * Getter for the class of the control activity to start on selection of
   * service notification.
   * 
   * @return the class of the control activity for the service
   */
  public Class< ? extends Activity > getControlActivityClass();
  
  /**
   * Method to signal the service to stop itself.
   */
  public void stopSelf();
  
  /**
   * Getter for the application context.
   * 
   * @return the application context of the service
   */
  public Context getApplicationContext();
}