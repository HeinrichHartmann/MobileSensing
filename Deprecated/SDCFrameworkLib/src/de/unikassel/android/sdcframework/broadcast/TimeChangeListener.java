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
package de.unikassel.android.sdcframework.broadcast;

import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.TimeProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * The internally used time change listener
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TimeChangeListener
    extends BroadcastReceiver
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
   * android.content.Intent)
   */
  @Override
  public void onReceive( Context context, Intent intent )
  {
    final String action = intent.getAction();
    if ( Intent.ACTION_DATE_CHANGED.equals( action ) ||
        Intent.ACTION_TIME_CHANGED.equals( action ) )
    {
      Logger.getInstance().debug(
          this, "Date/time change was signaled by system" );
      
      TimeProvider.getInstance().asynchronousUpdateTime( context.getApplicationContext() );      
    }
  }
}