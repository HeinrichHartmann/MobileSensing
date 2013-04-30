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

import java.util.ArrayList;
import java.util.List;

import de.unikassel.android.sdcframework.data.Weekday;
import de.unikassel.android.sdcframework.data.WeekdaySchedule;
import de.unikassel.android.sdcframework.data.WeekdayScheduleEntry;
import de.unikassel.android.sdcframework.data.WeekdaySchedulerAction;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import android.annotation.SuppressLint;
import android.os.Parcel;
import android.test.AndroidTestCase;

/**
 * Tests for the type {@link WeekdaySchedule}.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestWeekdaySchedule extends AndroidTestCase
{
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#equals(java.lang.Object)}
   * .
   */
  public final void testEquals()
  {
    WeekdaySchedule daySchedule = createWeekdayScheduleForTest();
    WeekdaySchedule otherDaySchedule = new WeekdaySchedule( daySchedule.getWeekday() );
    assertFalse( "Expected unequal day schedules", otherDaySchedule.equals( daySchedule ) );    
    
    for( WeekdayScheduleEntry entry : daySchedule.getEntries() )
    {
      otherDaySchedule.addEntry( new WeekdayScheduleEntry( entry ) );
    }
    
    assertTrue( "Expected equal day schedules", otherDaySchedule.equals( daySchedule ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#WeekdaySchedule(de.unikassel.android.sdcframework.data.Weekday, java.util.List)}
   * .
   */
  public final void testWeekdayScheduleWeekdayListOfWeekdayScheduleEntry()
  {
    List< WeekdayScheduleEntry > randomEntryList =
        createWeekdayScheduleEntriesForTest();
    Weekday randomWeekday =
        Weekday.valueOfOrdinal( (int) ( Math.random() * 7 ) );
    WeekdaySchedule daySchedule =
        new WeekdaySchedule( randomWeekday, randomEntryList );
    assertEquals( "Unexpected weekday", randomWeekday, daySchedule.getWeekday() );
    assertNotNull( "Expected entry list intitialized", daySchedule.getEntries() );
    assertEquals( "Expected same entry count", randomEntryList.size(),
        daySchedule.getEntries().size() );
    assertTrue( "Expected all entries added",
        daySchedule.getEntries().containsAll( randomEntryList ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#WeekdaySchedule(de.unikassel.android.sdcframework.data.Weekday)}
   * and
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#getWeekday()}
   * .
   */
  public final void testWeekdayScheduleWeekday()
  {
    for ( Weekday weekday : Weekday.values() )
    {
      WeekdaySchedule daySchedule = new WeekdaySchedule( weekday );
      assertEquals( "Unexpected weekday", weekday, daySchedule.getWeekday() );
      assertNotNull( "Expected entry list intitialized",
          daySchedule.getEntries() );
      assertTrue( "Expected entry list empty",
          daySchedule.getEntries().isEmpty() );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#addEntry(de.unikassel.android.sdcframework.data.WeekdayScheduleEntry)}
   * and
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#getEntries()}
   * and
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#removeEntry(de.unikassel.android.sdcframework.data.WeekdayScheduleEntry)}
   * .
   */
  public final void testEntryList()
  {
    WeekdaySchedule daySchedule = new WeekdaySchedule( Weekday.Saturday );
    assertNotNull( "Expected entry list available", daySchedule.getEntries() );
    assertEquals( "Expected entry list empty", 0,
        daySchedule.getEntries().size() );
    
    List< WeekdayScheduleEntry > entries =
        createWeekdayScheduleEntriesForTest();
    int cnt = 0;
    for ( WeekdayScheduleEntry entry : entries )
    {
      daySchedule.addEntry( entry );
      cnt++;
      assertTrue( "Expected entry added", daySchedule.getEntries().contains(
          entry ) );
      assertEquals( "Unexpected entry count", cnt,
          daySchedule.getEntries().size() );
    }
    
    assertEquals( "Expected same entry count", entries.size(),
        daySchedule.getEntries().size() );
    assertTrue( "Expected all entries added",
        daySchedule.getEntries().containsAll( entries ) );
    
    WeekdayScheduleEntry entryToRemove = entries.get( entries.size() >> 1 );
    daySchedule.removeEntry( entryToRemove );
    assertFalse( "Expected entry removed", daySchedule.getEntries().contains(
        entryToRemove ) );
    assertEquals( "Unexpected entry count", entries.size() - 1,
        daySchedule.getEntries().size() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#toXML()}.
   */
  public final void testToXML()
  {
    WeekdaySchedule daySchedule = createWeekdayScheduleForTest();
    try
    {
      String xml = daySchedule.toXML();
      WeekdaySchedule dayScheduleFromXML =
          GlobalSerializer.fromXML( WeekdaySchedule.class, xml );
      assertEquals(
          "Expected deserialized day schedule equals original one",
          daySchedule,
          dayScheduleFromXML );
    }
    catch ( Exception e )
    {
      fail( "Unexpected serialization exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#isValid()}.
   */
  public final void testIsValid()
  {
    WeekdaySchedule daySchedule = createWeekdayScheduleForTest();
    
    assertTrue( "Expected new intialized day schedule valid", daySchedule.isValid() );
    
    for ( int i = 0; i < daySchedule.getEntries().size(); ++i )
    {
      WeekdayScheduleEntry entry = daySchedule.getEntries().get( i );
      assertTrue( "Expected entry " + i + " valid", entry.isValid() );
      if ( i == 0 )
      {
        entry.setValid( false );
      }
    }
    
    assertFalse(
        "Expected an weekday schedule with invalid entries is not valid",
        daySchedule.isValid() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#describeContents()}
   * .
   */
  public final void testDescribeContents()
  {
    assertEquals( "Expected no special content", 0, new WeekdaySchedule(
        Weekday.Friday ).describeContents() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#writeToParcel(android.os.Parcel, int)}
   * and
   * {@link de.unikassel.android.sdcframework.data.WeekdaySchedule#WeekdaySchedule(android.os.Parcel)}
   * .
   */
  @SuppressLint( "Recycle" )
  public final void testWriteToParcel()
  {
    WeekdaySchedule schedule = createWeekdayScheduleForTest();
    Parcel parcel = Parcel.obtain();
    schedule.writeToParcel( parcel, 0 );
    parcel.setDataPosition( 0 );
    
    WeekdaySchedule scheduleFromParcel =
        WeekdaySchedule.CREATOR.createFromParcel( parcel );
    
    assertEquals(
        "Expected weekday schedule created from parcel equal to the original weekday schedule",
        schedule, scheduleFromParcel );
  }
  
  /**
   * Does create a weekday schedule for test purpose.
   * 
   * @return a weekday schedule for test purpose.
   */
  public static WeekdaySchedule createWeekdayScheduleForTest()
  {
    WeekdaySchedule schedule =
        new WeekdaySchedule(
            Weekday.valueOfOrdinal( (int) ( Math.random() * 7 ) ) );
    List< WeekdayScheduleEntry > entries =
        createWeekdayScheduleEntriesForTest();
    
    for ( WeekdayScheduleEntry entry : entries )
    {
      schedule.addEntry( entry );
    }
    return schedule;
  }
  
  /**
   * Does create a weekday schedule entry list for test purpose.
   * 
   * @return a weekday schedule entry list.
   */
  public static List< WeekdayScheduleEntry >
      createWeekdayScheduleEntriesForTest()
  {
    List< WeekdayScheduleEntry > list = new ArrayList< WeekdayScheduleEntry >();
    int rnd = 2 + (int) ( Math.random() * 10 );
    for ( int i = 0; i < rnd; ++i )
    {
      int rndTime = (int) ( Math.random() * 24 * 60 * 60 );
      list.add( new WeekdayScheduleEntry( rndTime, i % 2 == 0
          ? WeekdaySchedulerAction.StartService
          : WeekdaySchedulerAction.StopService ) );
    }
    return list;
  }
  
}
