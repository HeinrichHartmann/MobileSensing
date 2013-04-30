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

import de.unikassel.android.sdcframework.data.independent.NetworkLocationSampleData;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import junit.framework.TestCase;

/**
 * Tests for the network location samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestNetworkLocationSampleData extends TestCase
{
  
  /**
   * Test method for construction, setter and getter.
   */
  public final void testNetworkLocationSampleData()
  {
    NetworkLocationSampleData sampleData = new NetworkLocationSampleData();
    
    assertNull( "Expected latitude uninitialized", sampleData.getLatitude() );
    assertNull( "Expected longitude uninitialized", sampleData.getLongitude() );
    
    assertNull( "Expected Operator uninitialized ", sampleData.getAltitude() );
    assertNull( "Expected Operator uninitialized ", sampleData.getAccuracy() );
    assertNull( "Expected Operator uninitialized ", sampleData.getSpeed() );
    assertNull( "Expected no related data", sampleData.getRelatedData() );
    
    
    // test setter with getter
    
    double longitude = 9.46;
    sampleData.setLongitude( longitude );
    assertEquals( "Expected longitude set", longitude, sampleData.getLongitude() );
    
    double latitude = 51.31;
    sampleData.setLatitude( latitude );
    assertEquals( "Expected latitude set", latitude, sampleData.getLatitude() );
    
    Double altitude = 250.10;
    sampleData.setAltitude( altitude );
    assertEquals( "Expected altitude set", altitude, sampleData.getAltitude() );
    
    Float speed = 1.3F;
    sampleData.setSpeed( speed );
    assertEquals( "Expected speed set", speed, sampleData.getSpeed() );
    
    Float accuracy = 1.3F;
    sampleData.setAccuracy( accuracy );
    assertEquals( "Expected accuracy set", accuracy, sampleData.getAccuracy() );
    
    // test copy construction
    assertEquals( "Expected equal sample", sampleData, new NetworkLocationSampleData( sampleData ) );    
  }
  
  /**
   * Test method for equal comparison
   */
  public final void testEquals()
  { 
    
    NetworkLocationSampleData sampleData = createInitializedNetworkLocationSampleData();
    
    assertEquals( "Expected object equal to itself", sampleData, sampleData );
    
    assertEquals( "Expected object equal to a clone", sampleData,
        sampleData.doClone() );
    
    assertEquals( "Expected object equal to a copy construced instance",
        sampleData, new NetworkLocationSampleData( sampleData ) );
    
    assertFalse( "Expected object not equal to an uninitialized sample",
        sampleData.equals( new NetworkLocationSampleData() ) );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  { 
    // create the test sample
    NetworkLocationSampleData gpsSampleData = createInitializedNetworkLocationSampleData();
    
    // serialize to String
    SampleData sampleData = gpsSampleData;
    String sResult;
    try
    {
      // serialize to String
      sResult = sampleData.toXML();

      // serialize to object
      sampleData = GlobalSerializer.fromXML( NetworkLocationSampleData.class, sResult );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization to string" );
    }
    
    assertEquals(
        "Expected object serialized from string equal to the original source",
        gpsSampleData, sampleData );    
  }
  
  /**
   * Does create an initialized NetworkLocation sample
   * 
   * @return an initialized NetworkLocation sample
   */
  public static NetworkLocationSampleData createInitializedNetworkLocationSampleData()
  {
    NetworkLocationSampleData sample = new NetworkLocationSampleData();
    
    sample.setLongitude( 9.37 );
    sample.setLatitude( 53.76 );
    sample.setAltitude( 150.33 );
    sample.setSpeed( 10.2F );
    sample.setAccuracy( 12.4F );
    
    return sample;
  }
  
}
