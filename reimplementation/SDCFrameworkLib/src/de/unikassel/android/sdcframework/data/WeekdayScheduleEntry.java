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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SerializableData;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.facade.BroadcastableEvent;

/**
 * A weekday schedule entry marks a time stamp hh:mm:ss at the connected weekday
 * and an action to perform.
 * 
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "entry" )
public class WeekdayScheduleEntry
    implements SerializableData, Parcelable, BroadcastableEvent
{
  /**
   * The associated weekday schedule.
   */
  private WeekdaySchedule weekdaySchedule;
  
  /**
   * The time offset in seconds (hh:mm:ss) at the related weekday.
   */
  private int time;
  
  /**
   * The action to perform
   */
  private WeekdaySchedulerAction action;
  
  /**
   * The validation flag for display purpose.
   */
  private boolean isValid;
  
  /**
   * Constructor
   * 
   * @param intent
   *          the intent to create from
   */
  public WeekdayScheduleEntry( Intent intent )
  {
    this(
        (WeekdayScheduleEntry) intent.getParcelableExtra( WeekdayScheduleEntry.class.getSimpleName() ) );
    Weekday day = intent.getParcelableExtra( Weekday.class.getSimpleName() );
    setWeekdaySchedule( new WeekdaySchedule( day ) );
  }
  
  /**
   * Constructor
   * 
   * @param source
   *          the source to copy from
   */
  public WeekdayScheduleEntry( WeekdayScheduleEntry source )
  {
    this( source.getSeconds(), source.getAction() );
    this.isValid = source.isValid;
  }
  
  /**
   * Constructor
   * 
   * @param source
   *          the associated weekday schedule
   */
  public WeekdayScheduleEntry( Parcel source )
  {
    super();
    this.weekdaySchedule = null;
    this.time = source.readInt();
    this.action = WeekdaySchedulerAction.CREATOR.createFromParcel( source );
    this.isValid = true;
  }
  
  /**
   * Constructor
   * 
   * @param startTime
   *          the time stamp
   * @param action
   *          the action to perform
   */
  public WeekdayScheduleEntry(
      @Attribute( name = "time", required = true ) int startTime,
      @Attribute( name = "action", required = true ) WeekdaySchedulerAction action )
  {
    this.weekdaySchedule = null;
    this.time = startTime;
    this.action = action;
    this.isValid = true;
  }
  
  /**
   * Getter for the day
   * 
   * @return the day
   */
  public WeekdaySchedule getWeekdaySchedule()
  {
    return weekdaySchedule;
  }
  
  /**
   * Getter for the week day
   * 
   * @return the week day if assigned, null otherwise
   */
  public Weekday getWeekday()
  {
    return weekdaySchedule != null ? weekdaySchedule.getWeekday() : null;
  }
  
  /**
   * Setter for the weekday schedule
   * 
   * @param weekdaySchedule
   *          the weekday schedule to set
   */
  public void setWeekdaySchedule( WeekdaySchedule weekdaySchedule )
  {
    if ( this.weekdaySchedule == weekdaySchedule )
      return;
    
    if ( this.weekdaySchedule != null )
    {
      this.weekdaySchedule.removeEntry( this );
    }
    this.weekdaySchedule = weekdaySchedule;
    if ( this.weekdaySchedule != null )
    {
      weekdaySchedule.addEntry( this );
    }
  }
  
  /**
   * Getter for the time in second precision
   * 
   * @return the time in second precision
   */
  @Attribute( name = "time", required = true )
  public int getSeconds()
  {
    return time;
  }
  
  /**
   * Getter for the time in millisecond precision
   * 
   * @return the time in millisecond precision
   */
  public int getMilliseconds()
  {
    return time * 1000;
  }
  
  /**
   * Setter for the time
   * 
   * @param time
   *          the time to set
   */
  public void setSeconds( int time )
  {
    this.time = time;
  }
  
  /**
   * Getter for the action
   * 
   * @return the action
   */
  @Attribute( name = "action", required = true )
  public WeekdaySchedulerAction getAction()
  {
    return action;
  }
  
  /**
   * Setter for the action
   * 
   * @param action
   *          the action to set
   */
  public void setAction( WeekdaySchedulerAction action )
  {
    this.action = action;
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
  public String toString()
  {
    try
    {
      return toXML();
    }
    catch ( Exception e )
    {
      Logger.getInstance().error( this, Log.getStackTraceString( e ) );
    }
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
    if ( o instanceof WeekdayScheduleEntry )
    {
      WeekdayScheduleEntry other = (WeekdayScheduleEntry) o;
      return time == other.getSeconds() && action.equals( other.action );
    }
    return false;
  }
  
  /**
   * Setter for the isValid
   * 
   * @param isValid
   *          the isValid to set
   */
  public void setValid( boolean isValid )
  {
    this.isValid = isValid;
  }
  
  /**
   * Getter for the isValid
   * 
   * @return the isValid
   */
  public boolean isValid()
  {
    return isValid;
  }
  
  /**
   * The Parcelable creator.
   */
  public static final Parcelable.Creator< WeekdayScheduleEntry > CREATOR =
      new Parcelable.Creator< WeekdayScheduleEntry >()
  {
    
    @Override
    public WeekdayScheduleEntry createFromParcel( Parcel source )
    {
      return new WeekdayScheduleEntry( source );
      
    }
    
    @Override
    public WeekdayScheduleEntry[] newArray( int size )
    {
      return new WeekdayScheduleEntry[ size ];
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
    dest.writeInt( time );
    action.writeToParcel( dest, flags );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.BroadcastableEvent#getIntent
   * ()
   */
  @Override
  public Intent getIntent()
  {
    Intent intent = new Intent();
    Parcelable pacelable = weekdaySchedule.getWeekday();
    intent.putExtra( Weekday.class.getSimpleName(), pacelable );
    pacelable = this;
    intent.putExtra( WeekdayScheduleEntry.class.getSimpleName(), pacelable );
    intent.getExtras().setClassLoader( getClass().getClassLoader() );
    return intent;
  }
  
}
