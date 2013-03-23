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
package de.unikassel.android.sdcframework.transmission.facade;

import de.unikassel.android.sdcframework.preferences.facade.UpdatableConfiguration;
import android.content.Context;

/**
 * Interface for components in the transmission package, which can be updated by
 * a configuration.
 * 
 * @author Katy Hilgenberg
 * @param <T> the configuration type
 * 
 */
public interface UpdatableTransmissionComponent<T extends UpdatableConfiguration<T>>
{
  /**
   * Method to update the component by a configuration
   * 
   * @param context
   *          the application context
   * @param config
   *          the configuration to update from
   */
  public abstract void updateConfiguration( Context context,
      T config );
}
