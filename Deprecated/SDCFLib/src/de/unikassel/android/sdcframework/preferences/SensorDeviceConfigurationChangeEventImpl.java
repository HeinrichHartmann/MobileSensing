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

import de.unikassel.android.sdcframework.devices.SensorDeviceConfigurationUpdateVisitor;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfigurationChangeEvent;

/**
 * Implementation of the sensor device configuration change event. <br/>
 * <br/>
 * This class will be used by the
 * {@linkplain SensorDeviceConfigurationUpdateVisitor configuration update
 * visitor} to transport the changed device configurations.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class SensorDeviceConfigurationChangeEventImpl implements
    SensorDeviceConfigurationChangeEvent
{
  /**
   * The device configuration update
   */
  private final SensorDeviceConfiguration configuration;
  
  /**
   * The device identifier
   */
  private final SensorDeviceIdentifier identifier;
  
  /**
   * Constructor
   */
  @SuppressWarnings( "unused" )
  private SensorDeviceConfigurationChangeEventImpl()
  {
    this( null, null );
  }
  
  /**
   * Constructor
   * 
   * @param configuration
   *          the device configuration
   * @param identifier
   *          the device identifier
   */
  public SensorDeviceConfigurationChangeEventImpl(
      SensorDeviceConfiguration configuration, SensorDeviceIdentifier identifier )
  {
    super();
    this.configuration = configuration;
    this.identifier = identifier;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.
   * SensorDeviceConfigurationChangeEvent#getDeviceIdentifier()
   */
  @Override
  public final SensorDeviceIdentifier getDeviceIdentifier()
  {
    return identifier;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ConfigurationChangeEvent
   * #getConfiguration()
   */
  @Override
  public final SensorDeviceConfiguration getConfiguration()
  {
    return configuration;
  }
  
}
