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

import de.unikassel.android.sdcframework.data.independent.AbstractSampleData;
import de.unikassel.android.sdcframework.data.independent.AccelerometerSampleData;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import junit.framework.TestCase;

/**
 * Tests for the accelerometer samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAccelerometerSampleData extends TestCase
{
  
  /**
   * Test method for construction, setter and getter.
   */
  public final void testAccelerometerSampleData()
  {
    AccelerometerSampleData sampleData = new AccelerometerSampleData();
    
    assertEquals( "Expected acceleration X uninitialized ", 0.0,
        sampleData.getAccelerationX(), 0.000000001 );
    assertEquals( "Expected acceleration Y uninitialized ", 0.0,
        sampleData.getAccelerationY(), 0.000000001 );
    assertEquals( "Expected acceleration Z uninitialized ", 0.0,
        sampleData.getAccelerationZ(), 0.000000001 );
    assertNull( "Expected no related data", sampleData.getRelatedData() );
    
    // test setter with getter
    
    float accX = 0.2F;
    sampleData.setAccelerationX( accX );
    assertEquals( "Expected acceleration X set", accX,
        sampleData.getAccelerationX() );
    
    float accY = 0.3F;
    sampleData.setAccelerationY( accY );
    assertEquals( "Expected acceleration X set", accY,
        sampleData.getAccelerationY(), 0.000000001 );
    
    float accZ = 0.4F;
    sampleData.setAccelerationZ( accZ );
    assertEquals( "Expected acceleration X set", accZ,
        sampleData.getAccelerationZ() );
    
    // test copy construction
    assertEquals( "Expected equal sample", sampleData,
        new AccelerometerSampleData(
            sampleData ) );
  }
  
  /**
   * Test method for equal comparison
   */
  public final void testEquals()
  {
    AbstractSampleData sampleData =
        createInitializedAccelerometerSampleData();
    
    assertEquals( "Expected object equal to itself", sampleData, sampleData );
    
    assertFalse( "Expected object not equal to an uninitialized sample",
        sampleData.equals( new AccelerometerSampleData() ) );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  {
    // create the test sample
    AccelerometerSampleData accSampleData =
        createInitializedAccelerometerSampleData();
    
    SampleData sampleData = accSampleData;
    String sResult;
    try
    {
      // serialize to String
      sResult = sampleData.toXML();
      
      // serialize to object
      sampleData =
          GlobalSerializer.fromXML( AccelerometerSampleData.class, sResult );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization to string" );
    }
    
    assertEquals(
        "Expected object serialized from string equal to the original source",
        accSampleData, sampleData );
  }
  
  /**
   * Does create initialized accelerometer sample
   * 
   * @return the initialized accelerometer sample
   */
  public static AccelerometerSampleData createInitializedAccelerometerSampleData()
  {
    AccelerometerSampleData sample = new AccelerometerSampleData();
    sample.setAccelerationX( 1.5F );
    sample.setAccelerationY( -0.5F );
    sample.setAccelerationZ( 8.91F );
    return sample;
  }
}
