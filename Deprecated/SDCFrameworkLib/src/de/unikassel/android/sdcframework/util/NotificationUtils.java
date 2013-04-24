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
package de.unikassel.android.sdcframework.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.app.SDCServiceController;

/**
 * Class providing notification utility functions
 * 
 * @author Katy Hilgenberg
 * 
 */
public class NotificationUtils
{
  /**
   * Does create a service notification
   * 
   * @param id
   *          the notification id to use
   * @param message
   *          the message
   * @param context
   *          the application context
   * @param vibrate
   *          does set vibration default
   * @param autoCancel
   *          does set auto cancel flag
   */
  public final static void serviceNotification( int id, String message,
      Context context, boolean vibrate, boolean autoCancel )
  {
    if ( context == null ) return;
    
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(
            Context.NOTIFICATION_SERVICE );
    
    // we do use the local phone time here 
    Notification notification =
        new Notification( R.drawable.serviceicon,
            message, System.currentTimeMillis() );
    notification.flags |= Notification.FLAG_AUTO_CANCEL;
    PendingIntent contentIntent =
        PendingIntent.getActivity( context, 0, new Intent( context,
            SDCServiceController.class ), 0 );
    if( vibrate )
      notification.defaults |= Notification.DEFAULT_VIBRATE;
    notification.setLatestEventInfo( context,
        context.getText( R.string.sdc_service_name ),
        notification.tickerText,
        contentIntent );
    notificationManager.notify( id, notification );
  }
  
  /**
   * Does cancel a notification by id
   * 
   * @param context
   *          the application context
   * @param id
   *          the notification id to cancel
   */
  public final static void cancelServiceNotification( Context context, int id )
  {
    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(
            Context.NOTIFICATION_SERVICE );
    notificationManager.cancel( id );
  }
}
