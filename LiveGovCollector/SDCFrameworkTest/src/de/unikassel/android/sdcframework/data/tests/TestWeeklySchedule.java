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

import java.util.List;

import de.unikassel.android.sdcframework.data.Weekday;
import de.unikassel.android.sdcframework.data.WeekdaySchedule;
import de.unikassel.android.sdcframework.data.WeekdayScheduleEntry;
import de.unikassel.android.sdcframework.data.WeeklySchedule;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import android.test.AndroidTestCase;
import android.util.Log;

/**
 * Test for the type {@link WeeklySchedule}.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestWeeklySchedule extends AndroidTestCase
{
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeeklySchedule#equals(java.lang.Object)}
   * .
   */
  public final void testEquals()
  {
    WeeklySchedule schedule = createWeeklyScheduleForTest();
    WeeklySchedule otherSchedule = new WeeklySchedule();
    assertFalse( "Expected unequal schedules", otherSchedule.equals( schedule ) );
    
    WeekdaySchedule[] daySchedules = schedule.getSchedule();
    for ( int i = 0; i < daySchedules.length; ++i )
    {
      for ( WeekdayScheduleEntry entry : daySchedules[ i ].getEntries() )
      {
        otherSchedule.getScheduleForWeekday( daySchedules[ i ].getWeekday() ).addEntry(
            new WeekdayScheduleEntry( entry ) );
      }
    }
    
    assertTrue( "Expected equal schedules", otherSchedule.equals( schedule ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeeklySchedule#toString()}.
   */
  public final void testToString()
  {
    WeeklySchedule schedule = createWeeklyScheduleForTest();
    
    String xml = null;
    try
    {
      xml = schedule.toXML();
    }
    catch ( Exception e )
    {
      fail( "Unexpected serialization exception" );
    }
    
    assertEquals(
        "Expected string representation equals xml representation",
        xml, schedule.toString() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeeklySchedule#WeeklySchedule()}
   * and
   * {@link de.unikassel.android.sdcframework.data.WeeklySchedule#getSchedule()} 
   * and 
   * {@link de.unikassel.android.sdcframework.data.WeeklySchedule#getScheduleForWeekday(de.unikassel.android.sdcframework.data.Weekday)}
   * .
   */
  public final void testWeeklySchedule()
  {
    WeeklySchedule schedule = new WeeklySchedule();
    
    assertNotNull(
        "Expected day schedules available", schedule.getSchedule() );
    assertEquals(
        "Expected day schedules available for a whole week", 7,
        schedule.getSchedule().length );
    
    for ( Weekday weekday : Weekday.values() )
    {
      assertNotNull( "Missing day schedule for " + weekday.name(),
           schedule.getScheduleForWeekday( weekday ) );
      assertTrue( "Expected empty day schedule for " + weekday.name(),
          schedule.getScheduleForWeekday( weekday ).getEntries().isEmpty() );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeeklySchedule#toXML()}.
   */
  public final void testToXML()
  {
    WeeklySchedule schedule = createWeeklyScheduleForTest();
    
    try
    {
      String xml = schedule.toXML();
      Log.d( getClass().getSimpleName(), xml );
      WeeklySchedule scheduleFromXML =
          GlobalSerializer.fromXML( WeeklySchedule.class, xml );
      assertEquals(
          "Expected deserialized schedule equal to original one",
          schedule, scheduleFromXML );
    }
    catch ( Exception e )
    {
      fail( "Unexpected serialization exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeeklySchedule#size()}.
   */
  public final void testSize()
  {
    WeeklySchedule schedule = createWeeklyScheduleForTest();
    int expectedSize = 0;
    
    WeekdaySchedule[] daySchedules = schedule.getSchedule();
    for ( int i = 0; i < daySchedules.length; ++i )
    {
      expectedSize += daySchedules[ i ].getEntries().size();
    }
    assertEquals( "Unexpected size", expectedSize, schedule.size() );
    
    assertEquals( "Unexpected size of empty schedule", 0,
        new WeeklySchedule().size() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeeklySchedule#isValid()}.
   */
  public final void testIsValid()
  {
    WeeklySchedule schedule = createWeeklyScheduleForTest();
    
    assertTrue( "Expected new intialized schedule valid", schedule.isValid() );
    
    WeekdaySchedule[] daySchedules = schedule.getSchedule();
    for ( int i = 0; i < daySchedules.length; ++i )
    {
      assertTrue( "Expected day schedule " + i + " valid",
          daySchedules[ i ].isValid() );
      
      if ( i == 0 )
      {
        daySchedules[ i ].getEntries().get( 0 ).setValid( false );
      }
    }
    
    assertFalse(
        "Expected an schedule with invalid entries is invalid",
        schedule.isValid() );
  }
  
  /**
   * Does create a weekday schedule for test purpose.
   * 
   * @return a weekday schedule for test purpose.
   */
  public static WeeklySchedule createWeeklyScheduleForTest()
  {
    WeeklySchedule schedule = new WeeklySchedule();
    
    for ( WeekdaySchedule daySchedule : schedule.getSchedule() )
    {
      List< WeekdayScheduleEntry > entries =
          TestWeekdaySchedule.createWeekdayScheduleEntriesForTest();
      for ( WeekdayScheduleEntry entry : entries )
      {
        daySchedule.addEntry( entry );
      }
    }
    return schedule;
  }
}
