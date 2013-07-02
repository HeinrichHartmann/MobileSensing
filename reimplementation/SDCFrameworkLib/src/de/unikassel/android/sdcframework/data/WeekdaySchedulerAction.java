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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Performable Scheduler actions.
 * 
 * @author Katy Hilgenberg
 * 
 */
public enum WeekdaySchedulerAction
    implements Parcelable
{
  
  /**
   * Start the sdcf service.
   */
  StartService,
  
  /**
   * Stop the sdcf service.
   */
  StopService;
  
  /**
   * Method to determine the Weekday for a given ordinal.
   * 
   * @param ordinal
   *          the ordinal value
   * @return the Weekday for a given ordinal
   */
  public static WeekdaySchedulerAction valueOfOrdinal( int ordinal )
  {
    if ( ordinal < 0 || ordinal >= WeekdaySchedulerAction.values().length )
    {
      throw new IndexOutOfBoundsException();
    }
    return WeekdaySchedulerAction.values()[ ordinal ];
  }
  
  /**
   * The action package part.
   */
  public final static String ACTION =
      "de.unikassel.android.sdcframework.intent.action.sdcfScheduledAction";
  
  /**
   * The Parcelable creator.
   */
  public static final Parcelable.Creator< WeekdaySchedulerAction > CREATOR =
      new Parcelable.Creator< WeekdaySchedulerAction >()
  {
    
    @Override
    public WeekdaySchedulerAction createFromParcel( Parcel source )
    {
      return valueOfOrdinal( source.readInt() );
    }
    
    @Override
    public WeekdaySchedulerAction[] newArray( int size )
    {
      return new WeekdaySchedulerAction[ size ];
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
    dest.writeInt( ordinal() );
  }
  
  /**
   * Getter for the previous action expected to be predecessor of this action
   * 
   * @return the previous action for this action
   * 
   */
  public WeekdaySchedulerAction getPreviousAction()
  {
    switch ( this )
    {
      case StartService:
        return StopService;
      case StopService:
        return StartService;
    }
    return null;    
  }
}
