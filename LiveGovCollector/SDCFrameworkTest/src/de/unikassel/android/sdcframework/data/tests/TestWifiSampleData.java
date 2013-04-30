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
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.data.independent.WifiSampleData;

/**
 * Tests for the wifi samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestWifiSampleData extends TestCase
{
  
  /**
   * Test method for construction, setter and getter.
   */
  public final void testWifiSampleData()
  {
    // test default construction
    WifiSampleData sampleData = new WifiSampleData();
    
    assertEquals( "Expected frequency uninitialized ", 0,
        sampleData.getFrequency() );
    assertNull( "Expected SSID uninitialized ", sampleData.getSSID() );
    assertNull( "Expected BSSID uninitialized ", sampleData.getBSSID() );
    assertNull( "Expected capabilites uninitialized ",
        sampleData.getCapabilities() );
    assertEquals( "Expected signal level uninitialized ", 0, sampleData.getLevel() );
    assertNull( "Expected no related data", sampleData.getRelatedData() );
    
    
    // test setter with getter    
    int frequency = 815;
    sampleData.setFrequency( frequency );
    assertEquals( "Expected frequency set", frequency,
        sampleData.getFrequency() );
    
    String SSID = "ssid";
    sampleData.setSSID( SSID );
    assertEquals( "Expected SSID set", SSID, sampleData.getSSID() );
    
    String BSSID = "bssid";
    sampleData.setBSSID( BSSID );
    assertEquals( "Expected BSSID set", BSSID, sampleData.getBSSID() );
    
    String caps = "capabilites";
    sampleData.setCapabilities( caps );
    assertEquals( "Expected capabilites set", caps,
        sampleData.getCapabilities() );
    
    int level = 4711;
    sampleData.setLevel( level );
    assertEquals( "Expected signal level set", level, sampleData.getLevel() );
  }
  
  /**
   * Test method for equal comparison
   */
  public final void testEquals()
  { 
    // create the test sample
    WifiSampleData sampleData = createInitializedWifiSampleData();
    
    assertEquals( "Expected object equal to itself", sampleData, sampleData );
    
    assertEquals( "Expected object equal to a clone", sampleData,
        sampleData.doClone() );
    
    assertEquals( "Expected object equal to a copy construced instance",
        sampleData, new WifiSampleData( sampleData ) );
    
    assertFalse( "Expected object not equal to an uninitialized sample",
        sampleData.equals( new WifiSampleData() ) );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  {
    // create the test sample
    WifiSampleData wifiSampleData = createInitializedWifiSampleData();
    
    // serialize to String
    SampleData sampleData = wifiSampleData;
    String sResult;
    try
    {
      // serialize to String
      sResult = sampleData.toXML();

      // serialize to object
      sampleData = GlobalSerializer.fromXML( WifiSampleData.class, sResult );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization to string" );
    }
    
    assertEquals(
        "Expected object serialized from string equal to the original source",
        wifiSampleData, sampleData );
  }
  
  /**
   * Does create initialized wifi sample data
   * 
   * @return the initialized wifi sample
   */
  public static WifiSampleData createInitializedWifiSampleData()
  {
    WifiSampleData wifiSampleData = new WifiSampleData();
    wifiSampleData.setSSID( "SSID" );
    wifiSampleData.setBSSID( "BSSID" );
    wifiSampleData.setCapabilities( "capabilities" );
    wifiSampleData.setFrequency( 33 );
    wifiSampleData.setLevel( 128 );
    return wifiSampleData;
  }
  
}
