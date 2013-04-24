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
package de.unikassel.android.sdcframework.preferences;

import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderConfigurationChangeEvent;

/**
 * The implementation of the time provider configuration change event
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TimeProviderConfigurationChangeEventImpl implements
    TimeProviderConfigurationChangeEvent
{
  /**
   * The time provider configuration
   */
  private final TimeProviderConfiguration configuration;
  
  /**
   * Constructor
   */
  @SuppressWarnings( "unused" )
  private TimeProviderConfigurationChangeEventImpl()
  {
    this( null );
  }
  
  /**
   * Constructor
   * 
   * @param configuration
   *          the time provider configuration
   */
  public TimeProviderConfigurationChangeEventImpl(
      TimeProviderConfiguration configuration )
  {
    super();
    this.configuration = configuration;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ConfigurationChangeEvent
   * #getConfiguration()
   */
  @Override
  public TimeProviderConfiguration getConfiguration()
  {
    return configuration;
  }
  
}
