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
package de.unikassel.android.sdcframework.data.independent;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * The device specific sample data of a Wifi sensor device are the {@linkplain #SSID
 * network name}, the {@link #BSSID access point adress}, the {@link #capabilities
 * device capabilities}, the {@link #level signal level } (dBm) and the
 * {@link #frequency channel frequency} (MHz).
 * 
 * @see de.unikassel.android.sdcframework.devices.WifiDevice
 * @see de.unikassel.android.sdcframework.devices.WifiDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "data")
public final class WifiSampleData 
extends AbstractSampleData
{
  /**
   * The network name.
   */
  @Element( name = "ssid", required = false )
  private String SSID;
  
  /**
   * The address of the access point.
   */
  @Element( name = "bssid", required = false )
  private String BSSID;
  
  /**
   * Describes the authentication, key management, and encryption schemes
   * supported by the access point.
   */
  @Element( name = "cap", required = false )
  private String capabilities;
  
  /**
   * The frequency in MHz of the channel over which the client is communicating
   * with the access point.
   */
  @Element( name = "freq" )
  private int frequency;
  
  /**
   * The detected signal level in dBm.
   */
  @Element( name = "sigLevel" )
  private int level;
  
  /**
   * The Flag if the device is currently connected to this network
   */
  @Element( name = "connected", required = false )
  private boolean connected;
  
  /**
   * Constructor
   */
  public WifiSampleData()
  {}
  
  /**
   * Copy-Constructor
   * 
   * @param sampleData
   *          the sample data to copy from
   */
  public WifiSampleData( WifiSampleData sampleData )
  {
    setBSSID( sampleData.getBSSID() );
    setSSID( sampleData.getSSID() );
    setCapabilities( sampleData.getCapabilities() );
    setFrequency( sampleData.getFrequency() );
    setLevel( sampleData.getLevel() );
    setConnected( sampleData.isConnected() ); 
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.data.facade.SampleData#doClone()
   */
  @Override
  public final SampleData doClone()
  {
    return new WifiSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof WifiSampleData )
    {
      WifiSampleData sampleData = (WifiSampleData) o;
      return getLevel() == sampleData.getLevel() &&
          getFrequency() == sampleData.getFrequency() &&
          BasicSample.equals( getBSSID(),
              sampleData.getBSSID() ) &&
          BasicSample.equals( getSSID(),
              sampleData.getSSID() ) &&
          BasicSample.equals( getCapabilities(),
              sampleData.getCapabilities() ) &&
          isConnected() == sampleData.isConnected();
    }
    return false;
  }
  
  /**
   * Getter for the network name
   * 
   * @return the network name
   */
  public final String getSSID()
  {
    return SSID;
  }
  
  /**
   * Setter for the network name
   * 
   * @param SSID
   *          the network name to set
   */
  public final void setSSID( String SSID )
  {
    this.SSID = SSID;
  }
  
  /**
   * Getter for the address of the access point
   * 
   * @return the address of the access point
   */
  public final String getBSSID()
  {
    return BSSID;
  }
  
  /**
   * Setter for the address of the access point
   * 
   * @param BSSID
   *          the address of the access point to set
   */
  public final void setBSSID( String BSSID )
  {
    this.BSSID = BSSID;
  }
  
  /**
   * Getter for the capabilities
   * 
   * @return the capabilities
   */
  public final String getCapabilities()
  {
    return capabilities;
  }
  
  /**
   * Setter for the capabilities
   * 
   * @param capabilities
   *          the capabilities to set
   */
  public final void setCapabilities( String capabilities )
  {
    this.capabilities = capabilities;
  }
  
  /**
   * Getter for the the channel frequency in MHz
   * 
   * @return the the channel frequency in MHz
   */
  public final int getFrequency()
  {
    return frequency;
  }
  
  /**
   * Setter for the channel frequency in MHz
   * 
   * @param frequency
   *          the the channel frequency in MHz to set
   */
  public final void setFrequency( int frequency )
  {
    this.frequency = frequency;
  }
  
  /**
   * Getter for the signal level in dBm
   * 
   * @return the signal level in dBm
   */
  public final int getLevel()
  {
    return level;
  }
  
  /**
   * Setter for the signal level in dBm
   * 
   * @param level
   *          the signal level in dBm to set
   */
  public final void setLevel( int level )
  {
    this.level = level;
  }

  /**
   * Getter for the connected
  
   * @return the connected
   */
  public boolean isConnected()
  {
    return connected;
  }

  /**
   * Setter for the connected flag
  
   * @param connected the connected flag to set
   */
  public void setConnected( boolean connected )
  {
    this.connected = connected;
  }
}
