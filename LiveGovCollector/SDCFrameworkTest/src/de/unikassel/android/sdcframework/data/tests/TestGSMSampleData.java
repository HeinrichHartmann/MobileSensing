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

import de.unikassel.android.sdcframework.data.independent.GSMSampleData;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import junit.framework.TestCase;

/**
 * Tests for the GSM samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestGSMSampleData extends TestCase
{
  /**
   * Test method for construction, setter and getter.
   */
  public final void testGSM()
  {
    GSMSampleData sampleData = new GSMSampleData();
    
    assertNull( "Expected Operator uninitialized ", sampleData.getOperator() );
    assertEquals( "Expected cellId uninitialized", 0,
        sampleData.getCellId() );
    assertEquals( "Expected location area code uninitialized", 0,
        sampleData.getLocationAreaCode() );
    assertEquals( "Expected signal strength uninitialized", 0,
        sampleData.getSignalStrength() );
    assertNull( "Expected no related data", sampleData.getRelatedData() );
    
    
    // test setter with getter
    
    String operator = "operator";
    sampleData.setOperator( operator );
    assertEquals( "Expected operator set", operator, sampleData.getOperator() );
    
    int cellID = 7815;
    sampleData.setCellId( cellID );
    assertEquals( "Expected cell id set", cellID, sampleData.getCellId() );
    
    int locationAreaCode = 7815;
    sampleData.setLocationAreaCode( locationAreaCode );
    assertEquals( "Expected location area code set", locationAreaCode,
        sampleData.getLocationAreaCode() );
    
    int signalStrength = 47;
    sampleData.setSignalStrength( signalStrength );
    assertEquals( "Expected signal strength set", signalStrength,
        sampleData.getSignalStrength() );
    
    // test copy construction
    GSMSampleData newGSMSampleData = new GSMSampleData( sampleData );
    assertEquals( "Expected equal sample", sampleData, newGSMSampleData );
  }
  
  /**
   * Test method for equal comparison
   */
  public final void testEquals()
  { 
    
    GSMSampleData sampleData = createInitializedGSMSampleData();
    
    assertEquals( "Expected object equal to itself", sampleData, sampleData );
    
    assertEquals( "Expected object equal to a clone", sampleData,
        sampleData.doClone() );
    
    assertEquals( "Expected object equal to a copy construced instance",
        sampleData, new GSMSampleData( sampleData ) );
    
    assertFalse( "Expected object not equal to an uninitialized sample",
        sampleData.equals( new GSMSampleData() ) );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  { 
    // create the test sample
    GSMSampleData gsmSampleData = createInitializedGSMSampleData();
    
    // serialize to String
    SampleData sampleData = gsmSampleData;
    String sResult;
    try
    {
      // serialize to String
      sResult = sampleData.toXML();

      // serialize to object
      sampleData = GlobalSerializer.fromXML( GSMSampleData.class, sResult );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization to string" );
    }
    
    assertEquals(
        "Expected object serialized from string equal to the original source",
        gsmSampleData, sampleData );    
  }
  
  /**
   * Does create an initialized GSM sample
   * 
   * @return an initialized GSM sample
   */
  public static GSMSampleData createInitializedGSMSampleData()
  {
    GSMSampleData sample = new GSMSampleData();
    
    sample.setOperator( "operator" );
    sample.setCellId( 1999 );
    sample.setLocationAreaCode( 446 );
    sample.setSignalStrength( 12 );
    
    return sample;
  }
  
}
