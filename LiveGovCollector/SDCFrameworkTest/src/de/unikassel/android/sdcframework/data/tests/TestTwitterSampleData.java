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

import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.data.independent.TwitterSampleData;
import junit.framework.TestCase;

/**
 * Tests for the Twitter sample data.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestTwitterSampleData extends TestCase
{
  
  /**
   * Test method for construction, setter and getter.
   */
  public final void testTestTwitterSampleData()
  {
    TwitterSampleData sampleData = new TwitterSampleData();
    
    assertNull( "Expected message uninitialized ", sampleData.getMessage() );
    assertNull( "Expected no related data", sampleData.getRelatedData() );
    
    // test setter with getter
    String msg = "This is a test message";
    sampleData.setMessage( msg );
    assertEquals( "Expected message set", msg, sampleData.getMessage() );
    
    // test copy construction
    assertEquals( "Expected equal sample", sampleData, new TwitterSampleData(
        sampleData ) );
  }
  
  /**
   * Test method for equal comparison
   */
  public final void testEquals()
  {
    TwitterSampleData sampleData = createInitializedTwitterSampleData();
    
    assertEquals( "Expected object equal to itself", sampleData, sampleData );
    
    assertEquals( "Expected object equal to a clone", sampleData,
        sampleData.doClone() );
    
    assertEquals( "Expected object equal to a copy construced instance",
        sampleData, new TwitterSampleData( sampleData ) );
    
    assertFalse( "Expected object not equal to an uninitialized sample",
        sampleData.equals( new TwitterSampleData() ) );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  {
    // create the test sample
    TwitterSampleData twitterSampleData = createInitializedTwitterSampleData();
    
    SampleData sampleData = twitterSampleData;
    String sResult;
    try
    {
      // serialize to String
      sResult = sampleData.toXML();
      
      // serialize to object
      sampleData = GlobalSerializer.fromXML( TwitterSampleData.class, sResult );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization to string" );
    }
    
    assertEquals(
        "Expected object serialized from string equal to the original source",
        twitterSampleData, sampleData );
  }
  
  /**
   * Does create an initialized twitter sample
   * 
   * @return the initialized sample
   */
  public static TwitterSampleData createInitializedTwitterSampleData()
  {
    TwitterSampleData sample = new TwitterSampleData();
    sample.setMessage( "This is a twitter message" );
    return sample;
  }
}
