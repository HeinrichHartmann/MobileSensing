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
package de.unikassel.android.sdcframework.util.tests;

import junit.framework.TestCase;

import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.TimeProvider;

/**
 * Tests for the time provider.
 * 
 * @author katy
 * 
 */
public class TestTimeProvider extends TestCase
{
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.TimeProvider#getInstance()}.
   */
  public void testGetInstance()
  {
    TimeProvider timeProvider = TimeProvider.getInstance();
    assertNotNull( "time provider instance should not be null",
        timeProvider );
    assertSame( "time provider instance should always be the same",
        timeProvider, TimeProvider.getInstance() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.TimeProvider#getTimeStamp()}.
   */
  public void testGetTimeStamp()
  {
    long sleepTime = 100;
    TimeProvider timeProvider = TimeProvider.getInstance();
    long timeStamp1 = timeProvider.getTimeStamp();
    
    TestUtils.sleep( sleepTime );
    
    long timeStamp2 = timeProvider.getTimeStamp();
    long diff = timeStamp2 - timeStamp1;
    
    if ( Math.abs( diff ) <= 0  )
    {
      fail( "Unexpected time difference after sleeping for nearly " + sleepTime
          + " ms" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.TimeProvider#toUTCString(long)}
   * .
   */
  public final void testToUTCString()
  {
    long timeStamp = 1304586006722L;
    String expected = "2011-05-05 09:00:06.722";
    String actual = TimeProvider.toUTCString( timeStamp );
    
    assertEquals( "Unexpected string representation for timestamp",
        expected, actual );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.TimeProvider#toUTCString(long)}
   * .
   */
  public final void testToUTCTime()
  {
    long timeStamp = 1304586006722L;
    String expected = "09:00:06";
    String actual = TimeProvider.toUTCTime( timeStamp );
    
    assertEquals( "Unexpected string representation for timestamp",
        expected, actual );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.TimeProvider#toUTCDate(long)}
   * .
   */
  public final void testToUTCDate()
  {
    long timeStamp = 1304586006722L;
    String expected = "2011-05-05";
    String actual = TimeProvider.toUTCDate( timeStamp );
    
    assertEquals( "Unexpected string representation for timestamp",
        expected, actual );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.TimeProvider#isUTCDayChange(Long)}
   * .
   */
  public final void testIsDayChange()
  {
    Long timeStamp1 = 1304586006722L;
    Long timeStamp2 = 1304553600000L;
    
    assertFalse( "Expected no day change", TimeProvider.isUTCDayChange( null ) );
    assertFalse( "Expected no day change", TimeProvider.isUTCDayChange( timeStamp1 ) );
    assertTrue( "Expected day change", TimeProvider.isUTCDayChange( timeStamp2) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.util.TimeProvider#getUTCDayTimeMillis(long)}
   * .
   */
  public final void testGetUTCDayTimeMillis()
  {
    long timeStamp1 = 1304586006722L;
    long millis = 1304553600000L;
    
    long utcDayTimeMillis = TimeProvider.getUTCDayTimeMillis( timeStamp1 );
    assertEquals( "Unexpected milliseconds", millis, utcDayTimeMillis);
  }
}
