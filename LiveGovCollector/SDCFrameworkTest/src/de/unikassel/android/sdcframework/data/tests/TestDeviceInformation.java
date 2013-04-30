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

import java.util.UUID;

import de.unikassel.android.sdcframework.data.ConcreteDeviceInformation;
import de.unikassel.android.sdcframework.data.independent.DeviceInformation;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import junit.framework.TestCase;

/**
 * @author Katy Hilgenberg
 *
 */
public class TestDeviceInformation extends TestCase
{
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for {@link de.unikassel.android.sdcframework.data.independent.DeviceInformation#DeviceInformation()}.
   */
  public final void testDeviceInformation()
  {
    UUID randomUUID = UUID.randomUUID();
    DeviceInformation deviceInfo = new ConcreteDeviceInformation( randomUUID.toString() );

    String testString = "fgu dhgpx____i,.podhj,iom";
    
    assertNotNull( "Expected uuid initialized", deviceInfo.getUuid() );
    assertEquals( "Expected uuid set", randomUUID.toString(), deviceInfo.getUuid() );
    
    assertNotNull( "Expected device initialized", deviceInfo.getDevice() );
    deviceInfo.setDevice( testString );
    assertEquals( "Expected device set", testString, deviceInfo.getDevice() );
    
    assertNotNull( "Expected figerprint initialized", deviceInfo.getFingerprint() );
    deviceInfo.setFingerprint( testString );
    assertEquals( "Expected figerprint set", testString, deviceInfo.getFingerprint() );
    
    assertNotNull( "Expected id initialized", deviceInfo.getId() );
    deviceInfo.setId( testString );
    assertEquals( "Expected id set", testString, deviceInfo.getId() );
    
    assertNotNull( "Expected manufacturer initialized", deviceInfo.getManufacturer() );
    deviceInfo.setManufacturer( testString );
    assertEquals( "Expected manufacturer set", testString, deviceInfo.getManufacturer() );
    
    assertNotNull( "Expected model initialized", deviceInfo.getModel() );
    deviceInfo.setModel( testString );
    assertEquals( "Expected model set", testString, deviceInfo.getModel() );
    
    assertNotNull( "Expected product initialized", deviceInfo.getProduct() );
    deviceInfo.setProduct( testString );
    assertEquals( "Expected product set", testString, deviceInfo.getProduct() );
    
    assertNotNull( "Expected release initialized", deviceInfo.getRelease() );
    deviceInfo.setRelease( testString );
    assertEquals( "Expected release set", testString, deviceInfo.getRelease() );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  { 
    DeviceInformation deviceInfo = new ConcreteDeviceInformation( UUID.randomUUID().toString() );
   
    DeviceInformation deserializedDeviceInfo = deviceInfo;
    String sResult;
    try
    {
      // serialize to String
      sResult = deviceInfo.toXML();
      System.out.println( sResult );

      // serialize to object
      deserializedDeviceInfo = GlobalSerializer.fromXML( DeviceInformation.class, sResult );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization to string" );
    }
    
    assertEquals(
        "Expected object serialized from string equal to the original source",
        deviceInfo, deserializedDeviceInfo );    
  }
  
}
