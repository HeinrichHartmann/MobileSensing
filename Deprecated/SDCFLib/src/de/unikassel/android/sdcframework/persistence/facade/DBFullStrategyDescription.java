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
package de.unikassel.android.sdcframework.persistence.facade;

/**
 * The enumeration defining the database full strategy combinations.
 * 
 * @author Katy Hilgenberg
 * 
 */
public enum DBFullStrategyDescription
{
  /**
   * This does define the following strategy chain: <br/>
   * {@link de.unikassel.android.sdcframework.persistence.WaitStrategy} ->
   * {@link de.unikassel.android.sdcframework.persistence.DeleteSamplesStrategy}
   * ->
   * {@link de.unikassel.android.sdcframework.persistence.NotificationStrategy} <br/>
   * <br/>
   * First it will wait a configured time span, than it does try to delete the
   * configured amount of sample records and finally it will sent a user
   * notification.
   */
  WAIT_DELETE_NOTIFY,
  
  /**
   * This does define the following strategy chain: <br/>
   * {@link de.unikassel.android.sdcframework.persistence.WaitStrategy} ->
   * {@link de.unikassel.android.sdcframework.persistence.NotificationStrategy}
   * ->
   * {@link de.unikassel.android.sdcframework.persistence.StopServiceStrategy}<br/>
   * <br/>
   * First it will wait a configured time span, than it does sent a user
   * notification and finally the
   * {@link de.unikassel.android.sdcframework.app.SDCServiceImpl} will be stopped
   */
  WAIT_NOTIFY_STOPSERVICE,
}