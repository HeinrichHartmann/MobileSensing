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
package de.unikassel.android.sdcframework.service.facade;

import android.content.Context;
import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceManager;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.util.facade.LifeCycleObject;

/**
 * Interface for the sensor data collection service manager. <br/<br/>
 * This is the main service component supervising all other sub services and
 * managers used by the frameworks.
 * 
 * @see de.unikassel.android.sdcframework.service.ServiceManagerImpl
 * @author Katy Hilgenberg
 * 
 */
public interface ServiceManager extends LifeCycleObject
{
  /**
   * Setter for the SDC service
   * 
   * @param service
   *          the SDC service to set
   */
  public abstract void setSDCService( SDCService service );
  
  /**
   * Getter for the SDC service
   * 
   * @return the SDC service
   */
  public abstract SDCService getSDCService();
  
  /**
   * Does stop the service, logs the message as warning and sends a
   * notification.
   * 
   * @param msg
   *          the message to log and for use in notification
   * 
   */
  public abstract void stopServiceByReason( String msg );
  
  /**
   * Getter for the service/application context
   * 
   * @return the context
   */
  public abstract Context getContext();
  
  /**
   * Getter for the application preference manager
   * 
   * @return the application preference manager
   */
  public abstract ApplicationPreferenceManager getPreferenceManager();
  
  /**
   * Getter for the sensor device manager
   * 
   * @return the sensor device manager
   */
  public abstract SensorDeviceManager getSensorDeviceManager();
  
  /**
   * Does enable or disable the sample broadcast feature
   * 
   * @param doEnable
   *          flag if sample broadcasting shall be enabled or disabled
   */
  void doEnableSampleBroadCasting( boolean doEnable );
  
  /**
   * Does activate or deactivate the sampling process for the running service.
   * This will permanent change the corresponding service setting as w
   * 
   * @param doEnable
   *          flag if sampling shall be active or not
   */
  void doEnableSampling( boolean doEnable );
  
  /**
   * Does change the persistent storage enabled state. This will permanent
   * change the corresponding service setting as well.
   * 
   * @param doEnable
   *          flag if the transfer feature shall be enabled or disabled
   */
  void doEnableSampleStorage( boolean doEnable );
  
  /**
   * Does change the sample transfer activation state. This will permanent
   * change the corresponding service setting as well.
   * 
   * @param doEnable
   *          flag if the transfer feature shall be enabled or disabled
   */
  void doEnableSampleTransfer( boolean doEnable );
  
  /**
   * Does manually trigger an instant sample transfer ( with a short delay ). If
   * the sample transfer feature is not enabled, it is activated automatically 
   * for a single archive transfer.
   * 
   * A manually triggered sample transfer does consider all configured transfer
   * settings but the frequency minimum: It will only take place if at least the
   * configured minimum of samples is available in the database, and the total
   * of transferred samples will not exceed the configured maximum.
   */
  void doTriggerSampleTransfer();
  
}
