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
package de.unikassel.android.sdcframework.preferences.facade;

import de.unikassel.android.sdcframework.util.facade.ObservableEvent;

/**
 * Interface for observable configuration change event types to be created by
 * the
 * {@linkplain de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl
 * preference manager} as event source. The related updated configuration can be
 * accessed using the {@link #getConfiguration()} method.
 * 
 * @author Katy Hilgenberg
 * @param <T>
 *          the configuration type
 * 
 */
public interface ConfigurationChangeEvent< T extends Configuration >
    extends ObservableEvent
{
  /**
   * Getter for the updated configuration, related to the Android preference
   * 
   * @return the new configuration
   */
  public abstract T getConfiguration();
}
