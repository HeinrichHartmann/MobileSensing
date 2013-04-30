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

import de.unikassel.android.sdcframework.data.independent.GeoLocation;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import junit.framework.TestCase;

/**
 * Tests for the geo locatio n data type.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestGeoLocation extends TestCase
{
  
  /**
   * Test method for construction, setter and getter.
   */
  public final void testGeoLocation()
  {
    GeoLocation loc = new GeoLocation();
    
    assertNotNull( "Expected latitude initialized", loc.getLat() );
    assertNotNull( "Expected longitude initialized", loc.getLon() );    
    
    // test setter with getter    
    double longitude = 9.46;
    loc.setLon( longitude );
    assertEquals( "Expected longitude set", longitude, loc.getLon() );
    
    double latitude = 51.31;
    loc.setLat( latitude );
    assertEquals( "Expected latitude set", latitude, loc.getLat() );
    
    // test copy construction
    assertEquals( "Expected equal sample", loc, new GeoLocation( loc ) );    
  }
  
  /**
   * Test method for equal comparison
   */
  public final void testEquals()
  { 
    GeoLocation location = createInitializedGeoLocation();
    
    assertEquals( "Expected object equal to itself", location, location );
    
    assertFalse( "Expected object not equal to an uninitialized sample",
        location.equals( new GeoLocation() ) );
  }
  
  /**
   * Test method for serialization.
   */
  public final void testSerialization()
  { 
    // create the test sample
    GeoLocation geoLoc = createInitializedGeoLocation();
    
    // serialize to String
    GeoLocation location = geoLoc;
    String sResult;
    try
    {
      // serialize to String
      sResult = location.toXML();

      // serialize to object
      location = GlobalSerializer.fromXML( GeoLocation.class, sResult );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
      fail( "Unexpected exception during serialization to string" );
    }
    
    assertEquals(
        "Expected object serialized from string equal to the original source",
        geoLoc, location );    
  }
  
  /**
   * Does create an initialized GeoLocation object 
   * 
   * @return an initialized GeoLocation object
   */
  public static GeoLocation createInitializedGeoLocation()
  {
    GeoLocation loc = new GeoLocation();
    
    loc.setLon( 9.37 );
    loc.setLat( 53.76 );
    return loc;
  }
  
}
