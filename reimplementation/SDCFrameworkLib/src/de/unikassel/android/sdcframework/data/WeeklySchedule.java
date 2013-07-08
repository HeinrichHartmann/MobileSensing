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
package de.unikassel.android.sdcframework.data;

import org.simpleframework.xml.ElementArray;
import org.simpleframework.xml.Root;

import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SerializableData;

/**
 * A weekly runtime schedule for the service.
 * 
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "weeklySchedule" )
public class WeeklySchedule 
implements SerializableData
{    
  /**
   * The schedule.
   */
  private final WeekdaySchedule schedule[];
  
  /**
   * Constructor
   */
  public WeeklySchedule()
  {
    super();
    this.schedule = new WeekdaySchedule[ Weekday.values().length ];
    for ( Weekday day : Weekday.values() )
    {
      this.schedule[ day.ordinal() ] = new WeekdaySchedule( day );
    }
  }
  
  /**
   * Constructor
   * 
   * @param schedule
   */
  public WeeklySchedule(
      @ElementArray( name = "entries" ) WeekdaySchedule[] schedule )
  {
    super();
    this.schedule = new WeekdaySchedule[ Weekday.values().length ];
    for ( WeekdaySchedule daySchedule : schedule )
    {
      this.schedule[ daySchedule.getWeekday().ordinal() ] = daySchedule;
    }
  }
  
  /**
   * Getter for the schedule.
   * 
   * @return the schedule
   */
  @ElementArray( name = "entries" )
  public WeekdaySchedule[] getSchedule()
  {
    return schedule;
  }
  
  /**
   * Getter for the schedule.
   * 
   * @param day
   *          the weekday
   * 
   * @return the schedule
   */
  public WeekdaySchedule getScheduleForWeekday( Weekday day )
  {
    return schedule[ day.ordinal() ];
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.data.independent.SerializableData#toXML()
   */
  @Override
  public String toXML() throws Exception
  {
    return GlobalSerializer.toXml( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public final String toString()
  {
    try
    {
      return toXML();
    }
    catch ( Exception e )
    {}
    return "";
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o )
  {
    if ( o instanceof WeeklySchedule )
    {
      WeeklySchedule other = (WeeklySchedule) o;
      for ( Weekday day : Weekday.values() )
      {
        if ( !getScheduleForWeekday( day ).equals(
            other.getScheduleForWeekday( day ) ) )
          return false;
      }
      return true;
    }
    return false;
  }
  
  /**
   * Does return the size of the schedule as total count of schedule entries.
   * 
   * @return the count of schedule entries
   */
  public int size()
  {
    int size = 0;
    for ( WeekdaySchedule daySchedule : schedule )
    {
      size += daySchedule.getEntries().size();
    }
    return size;
  }
  
  /**
   * Test method for a valid schedule.
   * 
   * @return true if valid, false otherwise.
   */
  public boolean isValid()
  {
    boolean valid = true;
    for ( int i = 0; i < schedule.length && valid; ++i )
    {
      valid = valid && schedule[i].isValid();
    }
    return valid;
  }
  
}
