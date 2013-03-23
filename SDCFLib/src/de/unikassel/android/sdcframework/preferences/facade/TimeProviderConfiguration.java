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

import java.util.List;

import de.unikassel.android.sdcframework.data.TimeProviderConfigurationEntries;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;

/**
 * Interface for the configuration of an NTP time provider.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface TimeProviderConfiguration
    extends UpdatableConfiguration< TimeProviderConfiguration >
{
  /**
   * Getter for the NTP time provider list
   * 
   * @return the NTP time provider list
   */
  public abstract List< String > getProviders();
  
  /**
   * Getter for the error strategy description
   * 
   * @return the error strategy description
   */
  public abstract TimeProviderErrorStrategyDescription getErrorStrategyDescription();
  
  /**
   * Method to update the time provider configuration by configuration file
   * entries.
   * 
   * @param config
   *          the NTP time provider configuration entries
   */
  public abstract void update( TimeProviderConfigurationEntries config );
  
}
