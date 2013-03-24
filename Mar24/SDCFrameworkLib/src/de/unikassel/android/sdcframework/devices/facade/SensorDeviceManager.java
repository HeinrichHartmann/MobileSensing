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
package de.unikassel.android.sdcframework.devices.facade;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.util.facade.LifeCycleObject;
import de.unikassel.android.sdcframework.util.facade.ObserverRegistration;

/**
 * The interface for the sensor device managemer. <br/>
 * A sensor device manager does share the life cycle of the android service and
 * can be observed for provided {@link Sample sensor samples}.
 * 
 * @see de.unikassel.android.sdcframework.devices.SensorDeviceManagerImpl
 * @author Katy Hilgenberg
 * 
 */
public interface SensorDeviceManager
    extends LifeCycleObject, VisitableDevice,
    ObserverRegistration< Sample >
{ 
  /**
   * Method to enable location information per sample
   * @param enable if true, location information will be added to each sample  
   */
  public abstract void enableLocationInfoPerSample( boolean enable );
}
