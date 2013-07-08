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
package de.unikassel.android.sdcframework.app.scheduler;

import de.unikassel.android.sdcframework.data.WeekdaySchedule;
import de.unikassel.android.sdcframework.data.WeekdayScheduleEntry;
import de.unikassel.android.sdcframework.data.WeekdaySchedulerAction;
import de.unikassel.android.sdcframework.data.WeeklySchedule;

/**
 * The class responsible for SDCF schedule validation.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class ScheduleValidator
{
  /**
   * Method to validate a schedule.
   * 
   * @param schedule
   *          the schedule to validate
   * @return true if schedule is valid, false otherwise.
   */
  public final static boolean validate( WeeklySchedule schedule )
  {
    if ( schedule.size() > 0 )
    {
      boolean started = false;
      WeekdayScheduleEntry firstEntry = null;
      WeekdayScheduleEntry lastEntry = null;
      
      for ( WeekdaySchedule weekdaySchedule : schedule.getSchedule() )
      {
        for ( WeekdayScheduleEntry entry : weekdaySchedule.getEntries() )
        {
          lastEntry = entry;
          if ( firstEntry == null )
            firstEntry = entry;
          
          if ( entry.getAction().equals( WeekdaySchedulerAction.StartService ) )
          {
            entry.setValid( !started );
            started = true;
          }
          else if ( entry.getAction().equals(
              WeekdaySchedulerAction.StopService ) )
          {
            entry.setValid( started );
            started = false;
          }
        }
      }
      
      if ( firstEntry.getAction().equals( lastEntry.getAction() ) )
      { 
        lastEntry.setValid( false );      
      }
      else
      {
        firstEntry.setValid( true );     
      }
    }
    return schedule.isValid();
  }
}
