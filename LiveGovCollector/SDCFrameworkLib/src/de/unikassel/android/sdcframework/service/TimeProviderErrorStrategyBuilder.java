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

import de.unikassel.android.sdcframework.service.facade.ServiceManager;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategy;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;

/**
 * A builder for time provider error strategies.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class TimeProviderErrorStrategyBuilder
{
  /**
   * Does create a strategy instance by type qualifier.
   * 
   * @param type
   *          the description type for the strategy to build
   * @param manager
   *          the aservice manager instance
   * @return the requested strategy instance or null if type is unknown
   */
  public final static TimeProviderErrorStrategy buildStrategy(
      TimeProviderErrorStrategyDescription type, ServiceManager manager )
  {
    if ( type != null )
    {
      switch ( type )
      {
        case IgnoreAndObserveSyncStates:
          return new TimeProviderOutOfSyncIgnoreStrategy( manager );
        case ShutdownService:
          return new ShutdownStrategy();
      }
    }
    
    return null;
  }
}
