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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import android.os.Parcel;
import android.os.Parcelable;

import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SerializableData;

/**
 * Weekday schedule representation.
 * 
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "daySchedule" )
public class WeekdaySchedule
    implements SerializableData, Parcelable
{
  /**
   * The associated weekday.
   */
  private final Weekday weekday;
  
  /**
   * The list with the weekday schedule entries.
   */
  private final List< WeekdayScheduleEntry > scheduleEntryList;
  
  /**
   * Constructor
   * 
   * @param source
   *          the associated weekday schedule
   */
  public WeekdaySchedule( Parcel source )
  {
    super();
    this.weekday = Weekday.CREATOR.createFromParcel( source );
    this.scheduleEntryList = new ArrayList< WeekdayScheduleEntry >();
    source.readList( this.scheduleEntryList,
        WeekdayScheduleEntry.class.getClassLoader() );
    for ( WeekdayScheduleEntry entry : scheduleEntryList )
    {
      entry.setWeekdaySchedule( this );
      addEntry( entry );
    }
  }
  
  /**
   * Constructor
   * 
   * @param weekday
   *          the weekday
   * @param scheduleEntryList
   *          the scheduled entries for this weekday
   */
  public WeekdaySchedule(
      @Attribute( name = "weekday", required = true ) Weekday weekday,
      @ElementList( name = "entries" ) List< WeekdayScheduleEntry > scheduleEntryList )
  {
    super();
    this.weekday = weekday;
    this.scheduleEntryList = new ArrayList< WeekdayScheduleEntry >();
    if ( scheduleEntryList != null )
    {
      for ( WeekdayScheduleEntry entry : scheduleEntryList )
      {
        entry.setWeekdaySchedule( this );
        addEntry( entry );
      }
    }
  }
  
  /**
   * Constructor
   * 
   * @param weekday
   *          the weekday
   */
  public WeekdaySchedule( Weekday weekday )
  {
    this(weekday, null);
  }
  
  /**
   * Getter for the weekday
   * 
   * @return the weekday
   */
  @Attribute( name = "weekday", required = true )
  public Weekday getWeekday()
  {
    return weekday;
  }
  
  /**
   * Method to add entries.
   * 
   * @param entry
   *          the schedule entry to add
   */
  public void addEntry( WeekdayScheduleEntry entry )
  {
    if ( !scheduleEntryList.contains( entry ) )
    {
      int i = scheduleEntryList.size();
      while ( i > 0 )
      {
        WeekdayScheduleEntry listEntry = scheduleEntryList.get( i - 1 );
        if ( listEntry.getSeconds() < entry.getSeconds() )
          break;
        --i;
      }
      scheduleEntryList.add( i, entry );
      entry.setWeekdaySchedule( this );
    }
  }
  
  /**
   * Method to remove entries.
   * 
   * @param entry
   *          the schedule entry to remove
   */
  public void removeEntry( WeekdayScheduleEntry entry )
  {
    if ( scheduleEntryList.contains( entry ) )
    {
      scheduleEntryList.remove( entry );
      entry.setWeekdaySchedule( null );
    }
  }
  
  /**
   * Getter for the weekday schedule entries.
   * 
   * @return the weekday schedule entries
   */
  @ElementList( name = "entries" )
  public List< WeekdayScheduleEntry > getEntries()
  {
    return scheduleEntryList;
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
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals( Object o )
  {
    if ( o instanceof WeekdaySchedule )
    {
      WeekdaySchedule other = (WeekdaySchedule) o;
      if ( weekday.equals( other.weekday ) )
      {
        Iterator< WeekdayScheduleEntry > it = scheduleEntryList.iterator();
        Iterator< WeekdayScheduleEntry > itOther =
            other.getEntries().iterator();
        
        boolean isEqual = it.hasNext() == itOther.hasNext();
        while ( isEqual && it.hasNext() )
        {
          isEqual =
              it.next().equals( itOther.next() )
                  && it.hasNext() == itOther.hasNext();
        }
        return isEqual;
      }
    }
    return false;
  }
  
  /**
   * Test method for a valid weekday schedule.
   * 
   * @return true if valid, false otherwise.
   */
  public boolean isValid()
  {
    boolean valid = true;
    for ( int i = 0; i < scheduleEntryList.size() && valid; ++i )
    {
      valid = valid && scheduleEntryList.get( i ).isValid();
    }
    return valid;
  }
  
  /**
   * The Parcelable creator.
   */
  public static final Parcelable.Creator< WeekdaySchedule > CREATOR =
      new Parcelable.Creator< WeekdaySchedule >()
  {
    
    @Override
    public WeekdaySchedule createFromParcel( Parcel source )
    {
      return new WeekdaySchedule( source );
      
    }
    
    @Override
    public WeekdaySchedule[] newArray( int size )
    {
      return new WeekdaySchedule[ size ];
    }
  };
  
  /*
   * (non-Javadoc)
   * 
   * @see android.os.Parcelable#describeContents()
   */
  @Override
  public int describeContents()
  {
    return 0;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
   */
  @Override
  public void writeToParcel( Parcel dest, int flags )
  {
    weekday.writeToParcel( dest, flags );
    dest.writeList( scheduleEntryList );
  }
  
}
