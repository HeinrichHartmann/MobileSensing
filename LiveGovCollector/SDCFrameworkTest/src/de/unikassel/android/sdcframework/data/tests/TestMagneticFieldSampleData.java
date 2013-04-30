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

import de.unikassel.android.sdcframework.data.independent.MagneticFieldSampleData;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import junit.framework.TestCase;

/**
 * Tests for the magnetic field samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestMagneticFieldSampleData extends TestCase
{
  
  /**
   * Test method for construction, setter and getter.
   */
  public final void testMagneticFieldSampleData()
  {
    MagneticFieldSampleData sampleData = new MagneticFieldSampleData();
    
    assertEquals( "Expected field x uninitialized ", 0.0,
        sampleData.getMagneticFieldX(), 0.000000001 );
    assertEquals( "Expected field y uninitialized ", 0.0,
        sampleData.getMagneticFieldY(), 0.000000001 );
    assertEquals( "Expected field z uninitialized ", 0.0,
        sampleData.getMagneticFieldZ(), 0.000000001 );
    assertNull( "Expected no related data", sampleData.getRelatedData() );
    
    // test setter with getter
    
    float fieldX = 0.2F;
    sampleData.setMagneticFieldX( fieldX );
    assertEquals( "Expected field x set", fieldX, sampleData.getMagneticFieldX() );
    
    float fieldY = 0.3F;
    sampleData.setMagneticFieldY( fieldY );
    assertEquals( "Expected field y set", fieldY, sampleData.getMagneticFieldY(),
        0.000000001 );
    
    float fieldZ = 0.4F;
    sampleData.setMagneticFieldZ( fieldZ );
    assertEquals( "Expected field z set", fieldZ, sampleData.getMagneticFieldZ() );
    
    // test copy construction
    assertEquals( "Expected equal sample", sampleData,
        new MagneticFieldSampleData( sampleData ) );
  }
  
  /**
   * Test method for equal comparison
   */
  public final void testEquals()
  {
    MagneticFieldSampleData sampleData = createInitializedSampleData();
    
    assertEquals( "Expected object equal to itself", sampleData, sampleData );
    
    assertEquals( "Expected object equal to a clone", sampleData,
        sampleData.doClone() );
    
    assertEquals( "Expected object equal to a copy construced instance",
        sampleData, new MagneticFieldSampleData( sampleData ) );
    
    assertFalse( "Expected object not equal to an uninitialized sample",
        sampleData.equals( new MagneticFieldSampleData() ) );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  {
    // create the test sample
    MagneticFieldSampleData orgSampleData = createInitializedSampleData();
    
    SampleData sampleData = orgSampleData;
    String sResult;
    try
    {
      // serialize to String
      sResult = sampleData.toXML();
      
      // serialize to object
      sampleData =
          GlobalSerializer.fromXML( MagneticFieldSampleData.class, sResult );
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
  public static MagneticFieldSampleData createInitializedSampleData()
  {
    MagneticFieldSampleData sample = new MagneticFieldSampleData();
    sample.setMagneticFieldX( 12.1F );
    sample.setMagneticFieldY( 1.8F );
    sample.setMagneticFieldZ( 3.7F );
    return sample;
  }
}
