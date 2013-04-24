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
 * The device specific sample data of a bluetooth sensor device are the {@link #rssi
 * received signal strength indicator}, the {@link #name friendly bluetooth name},
 * the {@link #address hardware adress } and the {@link #bluetoothClass bluetooth
 * class}.
 * 
 * @see de.unikassel.android.sdcframework.devices.BluetoothDevice
 * @see de.unikassel.android.sdcframework.devices.BluetoothDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "data")
public final class BluetoothSampleData 
extends AbstractSampleData
{
  /**
   * The received signal strength indicator of the device ( usually a value in
   * the range 0 to 255 ). Here it's just the short extra information provided
   * by the Android-Api.
   */
  @Element( name = "rssi", required = false )
  private Short rssi;
  
  /**
   * The friendly bluetooth name of the device ( can be null if unknown )
   */
  @Element( name = "name", required = false )
  private String name;
  
  /**
   * The bluetooth hardware address as string
   */
  @Element( name = "adress", required = false )
  private String address;
  
  /**
   * The Android bluetooth class ( can be null if unknown ). The class describes
   * general characteristics and capabilities of a device.
   */
  @Element( name = "class", required = false )
  private String bluetoothClass;
  
  /**
   * Constructor
   */
  public BluetoothSampleData()
  {}
  
  /**
   * Constructor
   * 
   * @param sampleData
   *          the sample data to copy construct from
   */
  public BluetoothSampleData( BluetoothSampleData sampleData )
  {
    setRSSI( sampleData.getRSSI() );
    setName( sampleData.getName() );
    setBluetoothClass( sampleData.getBluetoothClass() );
    setAddress( sampleData.getAddress() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.data.facade.SampleData#doClone()
   */
  @Override
  public final SampleData doClone()
  {
    return new BluetoothSampleData( this );
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof BluetoothSampleData )
    {
      BluetoothSampleData sampleData = (BluetoothSampleData) o;
      return BasicSample.equals( getAddress(),
              sampleData.getAddress() ) &&
          BasicSample.equals( getBluetoothClass(),
              sampleData.getBluetoothClass() ) &&
          BasicSample.equals( getName(),
              sampleData.getName() ) &&
          BasicSample.equals( getRSSI(), sampleData.getRSSI() );
      
    }
    return false;
  }
  
  /**
   * Getter for the received signal strength indicator ( RSSI )
   * 
   * @return the RSSI
   */
  public final Short getRSSI()
  {
    return rssi;
  }
  
  /**
   * Setter for the received signal strength indicator ( RSSI )
   * 
   * @param rssi
   *          the RSSI to set
   */
  public final void setRSSI( Short rssi )
  {
    this.rssi = rssi;
  }
  
  /**
   * Getter for the friendly bluetooth name
   * 
   * @return the name
   */
  public final String getName()
  {
    return name;
  }
  
  /**
   * Setter for the friendly bluetooth name
   * 
   * @param name
   *          the name to set
   */
  public final void setName( String name )
  {
    this.name = name;
  }
  
  /**
   * Getter for the hardware address
   * 
   * @return the hardware address
   */
  public final String getAddress()
  {
    return address;
  }
  
  /**
   * Setter for the hardware address
   * 
   * @param address
   *          the hardware address to set
   */
  public final void setAddress( String address )
  {
    this.address = address;
  }
  
  /**
   * Getter for the bluetoothClass
   * 
   * @return the bluetoothClass
   */
  public final String getBluetoothClass()
  {
    return bluetoothClass;
  }
  
  /**
   * Setter for the bluetooth class
   * 
   * @param bluetoothClass
   *          the bluetooth class to set
   */
  public final void setBluetoothClass( String bluetoothClass )
  {
    this.bluetoothClass = bluetoothClass;
  }
}
