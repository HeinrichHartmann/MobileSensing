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

import android.content.Context;
import de.unikassel.android.sdcframework.service.facade.ServiceManager;
import de.unikassel.android.sdcframework.util.AbstractTimeProviderErrorStrategy;
import de.unikassel.android.sdcframework.util.TimeErrorEvent;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;

/**
 * Simple shutdown strategy in case of time provider synchronization errors.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ShutdownStrategy extends AbstractTimeProviderErrorStrategy
{
  
  /**
   * The sync error message.
   */
  private static final String SYNC_ERROR =
      "Unable to sync with NTP time provider!";
  
  /**
   * Constructor
   */
  public ShutdownStrategy()
  {
    super( TimeProviderErrorStrategyDescription.ShutdownService );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategy
   * #handleTimeErrorEvent
   * (de.unikassel.android.sdcframework.util.TimeErrorEvent,
   * de.unikassel.android.sdcframework.service.facade.ServiceManager)
   */
  @Override
  public final boolean handleTimeErrorEvent( TimeErrorEvent event,
      ServiceManager serviceManager )
  {
    switch ( event.getError() )
    {
      case TIME_SYNC_ERROR:
      {
        // stop the service
        serviceManager.stopServiceByReason( SYNC_ERROR );
        return true;
      }
    }
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategy
   * #enable(android.content.Context)
   */
  @Override
  public final void prepare( Context context )
  {
    // Nothing to do
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategy
   * #disable(android.content.Context)
   */
  @Override
  public final void finalize( Context context )
  {
    // Nothing to do
  }
  
}
