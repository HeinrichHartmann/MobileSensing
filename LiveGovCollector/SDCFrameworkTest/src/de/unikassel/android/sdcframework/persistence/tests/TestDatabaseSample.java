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
package de.unikassel.android.sdcframework.persistence.tests;

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.GeoLocation;
import de.unikassel.android.sdcframework.data.tests.TestGSMSampleData;
import de.unikassel.android.sdcframework.data.tests.TestSampleCollection;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseSample;
import junit.framework.TestCase;

/**
 * Test for the database sample class
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestDatabaseSample extends TestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.persistence.facade.DatabaseSample#DatabaseSample(de.unikassel.android.sdcframework.data.Sample)}
   * .
   */
  public final void testDatabaseSample()
  {
    // test default construction
    DatabaseSample dbSample = new DatabaseSample();
    assertNull(
        "Expected sample creation fails for an uninitilized database sample",
        dbSample.toSample() );
    
    Sample sample =
        TestSampleCollection.createSample(
            SensorDeviceIdentifier.GSM,
            TestGSMSampleData.createInitializedGSMSampleData() );
    GeoLocation loc = new GeoLocation();
    loc.setLat( 180. );
    sample.setLocation( loc );
    try
    {
      dbSample = new DatabaseSample( sample );
      assertEquals( "Expected identifier set", sample.getDeviceIdentifier(),
          dbSample.deviceIdentifier );
      assertEquals( "Expected priority set", sample.getPriority(),
          dbSample.priority );
      assertEquals( "Expected time stamp set", sample.getTimeStamp(),
          dbSample.timeStamp );
      assertEquals( "Expected data set", sample.getData().toXML(),
          dbSample.data );
      assertEquals( "Expected data class type name set",
          sample.getData().getClass().getName(), dbSample.dataTypeClassName );
      assertEquals( "Expected location set", sample.getLocation().toXML(), dbSample.location );
      
      assertEquals( "Expected equal sample created from database sample",
          sample, dbSample.toSample() );
    }
    catch ( Exception e )
    {
      fail( "Unxpected exception for initilized sample" );
    }
    
  }
  
}
