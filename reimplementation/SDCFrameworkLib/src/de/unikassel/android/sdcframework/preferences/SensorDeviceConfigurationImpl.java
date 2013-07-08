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

import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;

/**
 * Basic implementation of the sensor device configuration interface.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class SensorDeviceConfigurationImpl implements
    SensorDeviceConfiguration
{
  /**
   * The scan sample frequency rate. For Android sensor devices types this will
   * be the sensor delay value. For all other devices this will be define the
   * sample rate in milliseconds.
   */
  private int frequency;
  
  /**
   * The current priority for sendor device scan samples.
   */
  private SensorDevicePriorities priority;
  
  /**
   * The enabled state flag for the device
   */
  private boolean enabled;
  
  /**
   * Constructor
   * 
   * @param frequency
   *          the device sample frequency
   * @param priority
   *          the device sample priority
   * @param enabled
   *          the device enabled state
   */
  public SensorDeviceConfigurationImpl( int frequency,
      SensorDevicePriorities priority,
      boolean enabled )
  {
    super();
    this.frequency = frequency;
    this.priority = priority;
    this.enabled = enabled;
  }
  
  /**
   * Constructor
   */
  public SensorDeviceConfigurationImpl()
  {
    this( FrequencyPreference.DEFAULT, PriorityLevelPreference.DEFAULT,
        EnabledPreference.DEFAULT );
  }
  
  /**
   * Copy constructor
   * 
   * @param deviceConfig
   *          the device configuration to copy from
   */
  public SensorDeviceConfigurationImpl( SensorDeviceConfiguration deviceConfig )
  {
    this( deviceConfig.getFrequency(), deviceConfig.getSamplePriority(),
        deviceConfig.isEnabled() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ScannerConfiguration
   * #setFrequency(int)
   */
  @Override
  public synchronized void setFrequency( int frequency )
  {
    this.frequency = frequency;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ScannerConfiguration
   * #getFrequency()
   */
  @Override
  public synchronized int getFrequency()
  {
    return frequency;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ScannerConfiguration
   * #setSamplePriority
   * (de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities)
   */
  @Override
  public synchronized void setSamplePriority( SensorDevicePriorities priority )
  {
    this.priority = priority;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.ScannerConfiguration
   * #getSamplePriority()
   */
  @Override
  public synchronized SensorDevicePriorities getSamplePriority()
  {
    return priority;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration
   * #setEnabled(boolean)
   */
  @Override
  public synchronized void setEnabled( boolean enabled )
  {
    this.enabled = enabled;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration
   * #isEnabled()
   */
  @Override
  public synchronized boolean isEnabled()
  {
    return enabled;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o )
  {
    if ( o instanceof SensorDeviceConfiguration )
    {
      SensorDeviceConfiguration conf = (SensorDeviceConfiguration) o;
      return conf.getSamplePriority().equals( getSamplePriority() ) &&
          conf.getFrequency() == getFrequency() &&
          conf.isEnabled() == isEnabled();
    }
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.preferences.facade.UpdatableConfiguration
   * #update(java.lang.Object)
   */
  @Override
  public void update( SensorDeviceConfiguration configuration )
  {
    setFrequency( configuration.getFrequency() );
    setSamplePriority( configuration.getSamplePriority() );
    setEnabled( configuration.isEnabled() );
  }
  
}
