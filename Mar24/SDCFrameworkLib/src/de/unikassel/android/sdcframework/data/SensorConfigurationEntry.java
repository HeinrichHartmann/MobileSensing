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
package de.unikassel.android.sdcframework.data;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * A sensor configuration does describe the default configuration for a
 * sensor type. <br/>
 * <br/>
 * It is used to configure default values as well as the general
 * availability of a sensor in the framework. All attributes but the id are optional.<br/>
 * <br/>
 * Examples: <br/>
 * <blockquote> &lt;sensor id="Wifi" enabled="true" frequency="30000"
 * prio="Level2"/&gt; <br/>
 * &lt;sensor id="GSM"/&gt; <br/>
 * &lt;sensor id="Accelerometer" enabled="true" frequency="1000"/> </blockquote>
 * 
 * @see SDCConfiguration
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "sensor" )
public final class SensorConfigurationEntry
{
  /**
   * The sensor identifier which has to be a valid string representation of a
   * {@linkplain de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier }
   * enumeration value ( e.g.: "Wifi" for SensorDeviceIdentifier.Wifi )
   */
  @Attribute( name = "id", required = true )
  private String sensorID;
  
  /**
   * The default enabled state of the sensor
   */
  @Attribute( name = "enabled", required = false )
  private boolean enabled;
  
  /**
   * The default sample frequency for the sensor in milliseconds
   */
  @Attribute( name = "frequency", required = false )
  private int frequency;
  
  /**
   * The default transmission priority for the sensors samples which has to be a
   * valid string representation of a
   * {@linkplain de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities }
   * enumeration value ( e.g.: "Level1" for SensorDevicePriorities.Level1 )
   */
  @Attribute( name = "prio", required = false )
  private String priority;
  
  /**
   * Constructor
   */
  public SensorConfigurationEntry()
  {
    super();
  }
  
  /**
   * Setter for the sensorID
   * 
   * @param sensorID
   *          the sensorID to set
   */
  public final void setSensorID( String sensorID )
  {
    this.sensorID = sensorID;
  }
  
  /**
   * Getter for the sensorID
   * 
   * @return the sensorID
   */
  public final String getSensorID()
  {
    return sensorID;
  }
  
  /**
   * Setter for the enabled
   * 
   * @param enabled
   *          the enabled to set
   */
  public final void setEnabled( boolean enabled )
  {
    this.enabled = enabled;
  }
  
  /**
   * Getter for the enabled
   * 
   * @return the enabled
   */
  public final boolean getEnabled()
  {
    return enabled;
  }
  
  /**
   * Setter for the frequency
   * 
   * @param frequency
   *          the frequency to set
   */
  public final void setFrequency( int frequency )
  {
    this.frequency = frequency;
  }
  
  /**
   * Getter for the frequency
   * 
   * @return the frequency
   */
  public final int getFrequency()
  {
    return frequency;
  }
  
  /**
   * Setter for the priority
   * 
   * @param priority
   *          the priority to set
   */
  public final void setPriority( String priority )
  {
    this.priority = priority;
  }
  
  /**
   * Getter for the priority
   * 
   * @return the priority
   */
  public final String getPriority()
  {
    return priority;
  }
  
}
