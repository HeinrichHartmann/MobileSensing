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

import de.unikassel.android.sdcframework.data.independent.GyroscopeSampleData;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import junit.framework.TestCase;

/**
 * Tests for the gyroscope samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestGyroscopeSampleData extends TestCase
{
  
  /**
   * Test method for construction, setter and getter.
   */
  public final void testGyroscopeSampleData()
  {
    GyroscopeSampleData sampleData = new GyroscopeSampleData();
    
    assertEquals( "Expected angular speed X uninitialized ", 0.0,
        sampleData.getAngularSpeedX(), 0.000000001 );
    assertEquals( "Expected angular speed Y uninitialized ", 0.0,
        sampleData.getAngularSpeedX(), 0.000000001 );
    assertEquals( "Expected angular speed Z uninitialized ", 0.0,
        sampleData.getAngularSpeedZ(), 0.000000001 );
    assertNull( "Expected no related data", sampleData.getRelatedData() );
    
    // test setter with getter
    
    float speedX = 0.2F;
    sampleData.setAngularSpeedX( speedX );
    assertEquals( "Expected angular speed X set", speedX,
        sampleData.getAngularSpeedX() );
    
    float speedY = 0.3F;
    sampleData.setAngularSpeedX( speedY );
    assertEquals( "Expected angular speed X set", speedY,
        sampleData.getAngularSpeedX(), 0.000000001 );
    
    float speedZ = 0.4F;
    sampleData.setAngularSpeedZ( speedZ );
    assertEquals( "Expected angular speed X set", speedZ,
        sampleData.getAngularSpeedZ() );
    
    // test copy construction
    assertEquals( "Expected equal sample", sampleData,
        new GyroscopeSampleData(
            sampleData ) );
  }
  
  /**
   * Test method for equal comparison
   */
  public final void testEquals()
  {
    GyroscopeSampleData sampleData = createInitializedSampleData();
    
    assertEquals( "Expected object equal to itself", sampleData, sampleData );
    
    assertEquals( "Expected object equal to a clone", sampleData,
        sampleData.doClone() );
    
    assertEquals( "Expected object equal to a copy construced instance",
        sampleData, new GyroscopeSampleData( sampleData ) );
    
    assertFalse( "Expected object not equal to an uninitialized sample",
        sampleData.equals( new GyroscopeSampleData() ) );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  {
    // create the test sample
    GyroscopeSampleData orgSampleData = createInitializedSampleData();
    
    SampleData sampleData = orgSampleData;
    String sResult;
    try
    {
      // serialize to String
      sResult = sampleData.toXML();
      
      // serialize to object
      sampleData =
          GlobalSerializer.fromXML( GyroscopeSampleData.class, sResult );
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
  public static GyroscopeSampleData createInitializedSampleData()
  {
    GyroscopeSampleData sample = new GyroscopeSampleData();
    sample.setAngularSpeedX( 1.2F );
    sample.setAngularSpeedY( 0.5F );
    sample.setAngularSpeedZ( 1.91F );
    return sample;
  }
}
