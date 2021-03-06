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

import de.unikassel.android.sdcframework.data.independent.OrientationSampleData;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import junit.framework.TestCase;

/**
 * Tests for the orientation samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestOrientationSampleData extends TestCase
{
  
  /**
   * Test method for construction, setter and getter.
   */
  public final void testOrientationSampleData()
  {
    OrientationSampleData sampleData = new OrientationSampleData();
    
    assertEquals( "Expected heading uninitialized ", 0.0,
        sampleData.getHeading(), 0.000000001 );
    assertEquals( "Expected pitch uninitialized ", 0.0,
        sampleData.getPitch(), 0.000000001 );
    assertEquals( "Expected roll uninitialized ", 0.0,
        sampleData.getRoll(), 0.000000001 );
    assertNull( "Expected no related data", sampleData.getRelatedData() );
    
    // test setter with getter
    
    float heading = 0.2F;
    sampleData.setHeading( heading );
    assertEquals( "Expected heading set", heading, sampleData.getHeading() );
    
    float pitch = 0.3F;
    sampleData.setPitch( pitch );
    assertEquals( "Expected pitch set", pitch, sampleData.getPitch(),
        0.000000001 );
    
    float roll = 0.4F;
    sampleData.setRoll( roll );
    assertEquals( "Expected roll set", roll, sampleData.getRoll() );
    
    // test copy construction
    assertEquals( "Expected equal sample", sampleData,
        new OrientationSampleData( sampleData ) );
  }
  
  /**
   * Test method for equal comparison
   */
  public final void testEquals()
  {
    OrientationSampleData sampleData = createInitializedSampleData();
    
    assertEquals( "Expected object equal to itself", sampleData, sampleData );
    
    assertEquals( "Expected object equal to a clone", sampleData,
        sampleData.doClone() );
    
    assertEquals( "Expected object equal to a copy construced instance",
        sampleData, new OrientationSampleData( sampleData ) );
    
    assertFalse( "Expected object not equal to an uninitialized sample",
        sampleData.equals( new OrientationSampleData() ) );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  {
    // create the test sample
    OrientationSampleData orgSampleData = createInitializedSampleData();
    
    SampleData sampleData = orgSampleData;
    String sResult;
    try
    {
      // serialize to String
      sResult = sampleData.toXML();
      
      // serialize to object
      sampleData =
          GlobalSerializer.fromXML( OrientationSampleData.class, sResult );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization to string" );
    }
    
    assertEquals(
        "Expected object serialized from string equal to the original source",
        orgSampleData, sampleData );
  }
  
  /**
   * Does create initialized sample data
   * 
   * @return the initialized sample data
   */
  public static OrientationSampleData createInitializedSampleData()
  {
    OrientationSampleData sample = new OrientationSampleData();
    sample.setHeading( 270 );
    sample.setPitch( -73 );
    sample.setRoll( 34 );
    return sample;
  }
}
