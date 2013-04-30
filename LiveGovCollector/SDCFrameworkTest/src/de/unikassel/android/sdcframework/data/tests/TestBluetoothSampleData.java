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
package de.unikassel.android.sdcframework.data.tests;

import junit.framework.TestCase;

import de.unikassel.android.sdcframework.data.independent.BluetoothSampleData;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;

/**
 * Tests for the bluetooth samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestBluetoothSampleData extends TestCase
{
  
  /**
   * Test method for construction, setter and getter.
   */
  public final void testBluetoothSampleData()
  {
    BluetoothSampleData sampleData = new BluetoothSampleData();
    
    assertNull( "Expected RSSI uninitialized ", sampleData.getRSSI() );
    assertNull( "Expected adress uninitialized ", sampleData.getAddress() );
    assertNull( "Expected name uninitialized ", sampleData.getName() );
    assertNull( "Expected class uninitialized ",
        sampleData.getBluetoothClass() );
    assertNull( "Expected no related data", sampleData.getRelatedData() );
    
    
    // test setter with getter    
    Short rssi = 815;
    sampleData.setRSSI( rssi );
    assertEquals( "Expected RSSI set", rssi,
        sampleData.getRSSI() );
    
    String address = "address";
    sampleData.setAddress( address );
    assertEquals( "Expected address set", address, sampleData.getAddress() );
    
    String name = "name";
    sampleData.setName( name );
    assertEquals( "Expected name set", name, sampleData.getName() );
    
    String btClass = "class";
    sampleData.setBluetoothClass( btClass );
    assertEquals( "Expected class set", btClass,
        sampleData.getBluetoothClass() );
  }
  
  /**
   * Test method for equal comparison
   */
  public final void testEquals()
  {
    BluetoothSampleData sampleData = createInitializedBluetoothSampleData();
    
    assertEquals( "Expected object equal to itself", sampleData, sampleData );
    
    assertFalse( "Expected object not equal to an uninitialized sample",
        sampleData.equals( new BluetoothSampleData() ) );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  {
    // create the test btSampleDta
    BluetoothSampleData btSampleData = createInitializedBluetoothSampleData();
    
    // serialize to String
    SampleData sampleData = btSampleData;
    String sResult;
    try
    {
      // serialize to String
      sResult = sampleData.toXML();

      // serialize to object
      sampleData = GlobalSerializer.fromXML( BluetoothSampleData.class, sResult );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization to string" );
    }
    
    assertEquals(
        "Expected object serialized from string equal to the original source",
        btSampleData, sampleData );
  }
  
  /**
   * Does create an initialized bluetooth sample
   * 
   * @return an initialized bluetooth sample
   */
  public static BluetoothSampleData createInitializedBluetoothSampleData()
  {
    BluetoothSampleData sampleData = new BluetoothSampleData();
    sampleData.setAddress( "address" );
    sampleData.setBluetoothClass( "phone" );
    sampleData.setName( "name" );
    sampleData.setRSSI( (short) 10 );
    return sampleData;
  }
}
