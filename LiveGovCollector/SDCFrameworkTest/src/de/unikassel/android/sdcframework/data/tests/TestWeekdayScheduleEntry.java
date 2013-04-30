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

import de.unikassel.android.sdcframework.data.Weekday;
import de.unikassel.android.sdcframework.data.WeekdaySchedule;
import de.unikassel.android.sdcframework.data.WeekdayScheduleEntry;
import de.unikassel.android.sdcframework.data.WeekdaySchedulerAction;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Parcel;
import android.test.AndroidTestCase;

/**
 * Tests for the type {@link WeekdayScheduleEntry}.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestWeekdayScheduleEntry extends AndroidTestCase
{
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#equals(java.lang.Object)}
   * .
   */
  public final void testEquals()
  {
    int startTime = 4711;
    WeekdaySchedulerAction startAction = WeekdaySchedulerAction.StartService;
    WeekdaySchedulerAction stopAction = WeekdaySchedulerAction.StopService;
    WeekdayScheduleEntry entry1 =
        new WeekdayScheduleEntry( startTime, startAction );
    WeekdayScheduleEntry entry2 =
        new WeekdayScheduleEntry( startTime, stopAction );
    WeekdayScheduleEntry entry3 =
        new WeekdayScheduleEntry( startTime, stopAction );
    WeekdayScheduleEntry entry4 =
        new WeekdayScheduleEntry( startTime + 1, stopAction );
    
    assertFalse( "Expected entry1 is not equal to entry2",
        entry1.equals( entry2 ) );
    assertTrue( "Expected entry2 is equal to entry3", entry2.equals( entry3 ) );
    assertFalse( "Expected entry3 is not equal to entry4",
        entry3.equals( entry4 ) );
    
    entry2.setValid( true );
    entry3.setValid( false );
    assertTrue(
        "Expected entry2 is equal to entry3 even with different validity states",
        entry2.equals( entry3 ) );
    
    entry2.setWeekdaySchedule( new WeekdaySchedule( Weekday.Monday ) );
    entry3.setWeekdaySchedule( new WeekdaySchedule( Weekday.Tuesday ) );
    assertTrue(
        "Expected entry2 is equal to entry3 even with different associated weekdays",
        entry2.equals( entry3 ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#toString()}
   * .
   */
  public final void testToString()
  {
    WeekdayScheduleEntry entry =
        new WeekdayScheduleEntry( 47110, WeekdaySchedulerAction.StartService );
    
    String xml = null;
    try
    {
      xml = entry.toXML();
    }
    catch ( Exception e )
    {
      fail( "Unexpected serialization exception" );
    }
    
    assertEquals(
        "Expected string representation equals xml representation",
        xml, entry.toString() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#WeekdayScheduleEntry(android.content.Intent)}
   * and
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#getIntent()}
   * .
   */
  public final void testWeekdayScheduleEntryIntent()
  {
    WeekdayScheduleEntry orgEntry =
        new WeekdayScheduleEntry( 81500, WeekdaySchedulerAction.StopService );
    WeekdaySchedule weekdaySchedule1 = new WeekdaySchedule( Weekday.Sunday );
    orgEntry.setWeekdaySchedule( weekdaySchedule1 );
    
    Intent intent = orgEntry.getIntent();
    
    assertTrue( "Expected extra for Weekdayschedule",
        intent.hasExtra( WeekdayScheduleEntry.class.getSimpleName() ) );
    assertTrue( "Expected extra for Weekday",
        intent.hasExtra( Weekday.class.getSimpleName() ) );
    Weekday weekday = intent.getParcelableExtra( Weekday.class.getSimpleName() );
    assertTrue( "Expected weekday of parcel is equal to weekday of entry",
        orgEntry.getWeekday().equals( weekday ) );
    WeekdayScheduleEntry entryFromParcel =
        intent.getParcelableExtra( WeekdayScheduleEntry.class.getSimpleName() );
    assertTrue( "Expected entry is equal to entry from parcel",
        orgEntry.equals( entryFromParcel ) );
    
    WeekdayScheduleEntry entryFromIntent =
        new WeekdayScheduleEntry( intent );
    assertTrue( "Expected entry is equal to entryFromIntent",
        orgEntry.equals( entryFromIntent ) );
    assertTrue(
        "Expected weekday of entry is equal to weekday of entryFromIntent",
        orgEntry.getWeekday().equals( entryFromIntent.getWeekday() ) );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#WeekdayScheduleEntry(de.unikassel.android.sdcframework.data.WeekdayScheduleEntry)}
   * .
   */
  public final void testWeekdayScheduleEntryWeekdayScheduleEntry()
  {
    WeekdayScheduleEntry entry1 =
        new WeekdayScheduleEntry( 81500, WeekdaySchedulerAction.StopService );
    WeekdaySchedule weekdaySchedule1 = new WeekdaySchedule( Weekday.Sunday );
    entry1.setWeekdaySchedule( weekdaySchedule1 );
    entry1.setValid( !entry1.isValid() );
    
    WeekdayScheduleEntry entry2 =
        new WeekdayScheduleEntry( entry1 );
    
    assertEquals( "Expected equal time", entry1.getSeconds(),
        entry2.getSeconds() );
    assertEquals( "Expected equal validity state", entry1.isValid(),
        entry2.isValid() );
    assertEquals( "Expected equal action", entry1.getAction(),
        entry2.getAction() );
    assertNull( "Expected no weekday schedule associated after copy construction",
        entry2.getWeekdaySchedule() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#WeekdayScheduleEntry(int, de.unikassel.android.sdcframework.data.WeekdaySchedulerAction)}
   * .
   */
  public final void testWeekdayScheduleEntryIntWeekdaySchedulerAction()
  {
    int seconds = 81500;
    WeekdaySchedulerAction action = WeekdaySchedulerAction.StopService;
    WeekdayScheduleEntry entry =
        new WeekdayScheduleEntry( seconds, action );
    assertNull( "Expected weekday null without an associated weekday schedule",
        entry.getWeekday() );
    assertEquals( "Expected seconds intitialized with constructor parameter",
        seconds, entry.getSeconds() );
    assertEquals( "Expected action intitialized with constructor parameter",
        action, entry.getAction() );
    assertTrue( "Expected validity flag intitialized with true",
        entry.isValid() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#getWeekdaySchedule()}
   * and
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#setWeekdaySchedule(de.unikassel.android.sdcframework.data.WeekdaySchedule)}
   * .
   */
  public final void testGetWeekdaySchedule()
  {
    WeekdayScheduleEntry entry =
        new WeekdayScheduleEntry( 1337, WeekdaySchedulerAction.StartService );
    assertNull( "Expected weekday schedule null without an association",
        entry.getWeekdaySchedule() );
    WeekdaySchedule weekdaySchedule = new WeekdaySchedule( Weekday.Thursday );
    entry.setWeekdaySchedule( weekdaySchedule );
    assertNotNull(
        "Expected weekday schedule not null after an association",
        entry.getWeekdaySchedule() );
    assertSame( "Unexpected associated weekday schedule", weekdaySchedule,
        entry.getWeekdaySchedule() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#getWeekday()}
   * .
   */
  public final void testGetWeekday()
  {
    WeekdayScheduleEntry entry =
        new WeekdayScheduleEntry( 1337, WeekdaySchedulerAction.StartService );
    assertNull( "Expected weekday null without an associated weekday schedule",
        entry.getWeekday() );
    entry.setWeekdaySchedule( new WeekdaySchedule( Weekday.Thursday ) );
    assertNotNull(
        "Expected weekday not null after association with a weekday schedule",
        entry.getWeekday() );
    assertEquals( "Unexpected associated weekday", Weekday.Thursday,
        entry.getWeekday() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#getSeconds()}
   * and
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#setSeconds(int)}
   * .
   */
  public final void testGetSeconds()
  {
    int seconds = 471100;
    WeekdayScheduleEntry entry =
        new WeekdayScheduleEntry( seconds, WeekdaySchedulerAction.StopService );
    assertEquals(
        "Expected seconds intitialized with constructor parameter",
        seconds, entry.getSeconds() );
    seconds = seconds - 3456;
    entry.setSeconds( seconds );
    assertEquals(
        "Expected seconds intitialized with constructor parameter",
        seconds, entry.getSeconds() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#getMilliseconds()}
   * .
   */
  public final void testGetMilliseconds()
  {
    int seconds = 113456;
    WeekdayScheduleEntry entry =
        new WeekdayScheduleEntry( seconds, WeekdaySchedulerAction.StopService );
    assertEquals( "Expected milliseconds intitialized",
        seconds * 1000, entry.getMilliseconds() );
    seconds = seconds - 3456;
    entry.setSeconds( seconds );
    assertEquals( "Expected milliseconds intitialized",
        seconds * 1000, entry.getMilliseconds() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#getAction()}
   * and
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#setAction(de.unikassel.android.sdcframework.data.WeekdaySchedulerAction)}
   * .
   */
  public final void testGetAction()
  {
    WeekdayScheduleEntry entry =
        new WeekdayScheduleEntry( 81500, WeekdaySchedulerAction.StopService );
    assertEquals(
        "Expected validity flag intitialized with constructor parameter",
        WeekdaySchedulerAction.StopService, entry.getAction() );
    entry.setAction( WeekdaySchedulerAction.StartService );
    assertEquals( "Expected validity flag updated",
        WeekdaySchedulerAction.StartService, entry.getAction() );
    entry.setAction( WeekdaySchedulerAction.StopService );
    assertEquals( "Expected validity flag updated",
        WeekdaySchedulerAction.StopService, entry.getAction() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#toXML()}
   * .
   */
  public final void testToXML()
  {
    WeekdayScheduleEntry entry =
        new WeekdayScheduleEntry( 47110, WeekdaySchedulerAction.StartService );
    
    try
    {
      String xml = entry.toXML();
      WeekdayScheduleEntry entryFromXML =
          GlobalSerializer.fromXML( WeekdayScheduleEntry.class, xml );
      assertEquals(
          "Expected deserialized entry equals original one", entry,
          entryFromXML );
    }
    catch ( Exception e )
    {
      fail( "Unexpected serialization exception" );
    }
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#isValid()}
   * and
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#setValid(boolean)}
   * .
   */
  public final void testIsValid()
  {
    WeekdayScheduleEntry entry =
        new WeekdayScheduleEntry( 81500, WeekdaySchedulerAction.StopService );
    assertTrue( "Expected validity flag intitialized with true",
        entry.isValid() );
    entry.setValid( false );
    assertFalse( "Expected validity flag changed", entry.isValid() );
    entry.setValid( true );
    assertTrue( "Expected validity flag changed", entry.isValid() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#describeContents()}
   * .
   */
  public final void testDescribeContents()
  {
    assertEquals( "Expected no special content", 0, new WeekdayScheduleEntry(
        0, WeekdaySchedulerAction.StartService ).describeContents() );
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#writeToParcel(android.os.Parcel, int)}
   * and *
   * {@link de.unikassel.android.sdcframework.data.WeekdayScheduleEntry#WeekdayScheduleEntry(android.os.Parcel)}
   * .
   */
  @SuppressLint( "Recycle" )
  public final void testWriteToParcel()
  {
    WeekdayScheduleEntry entry = new WeekdayScheduleEntry(
        13, WeekdaySchedulerAction.StartService );
    Parcel parcel = Parcel.obtain();
    entry.writeToParcel( parcel, 0 );
    parcel.setDataPosition( 0 );
    
    WeekdayScheduleEntry entryFromParcel =
        WeekdayScheduleEntry.CREATOR.createFromParcel( parcel );
    
    assertEquals(
        "Expected entry created from parcel equal to the original entry",
        entry, entryFromParcel );
  }
  
}
