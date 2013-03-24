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
package de.unikassel.android.sdcframework.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;

/**
 * Service utility class providing a test function to check for a running
 * services instance.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ServiceUtils
{
  /**
   * Method to test for a running service by class.
   * 
   * @param applicationContext
   *          the application context
   * @param serviceClass
   *          the service to test for
   * @return true if the service is running, false otherwise
   */
  public static boolean isServiceRunning( Context applicationContext,
      Class< ? > serviceClass )
  {
    return isServiceRunning( applicationContext, getResolvedClassForService(
        applicationContext, serviceClass ) );
  }
  
  /**
   * Method to test for a running service by class name.
   * 
   * @param applicationContext
   *          the application context
   * @param serviceClassName
   *          the name of the service class to test for
   * @return true if the service is running, false otherwise
   */
  private static boolean isServiceRunning( Context applicationContext,
      String serviceClassName )
  {
    final ActivityManager activityManager =
        (ActivityManager) applicationContext.getSystemService( Context.ACTIVITY_SERVICE );
    
    for ( RunningServiceInfo service : activityManager.getRunningServices( Integer.MAX_VALUE ) )
    {
      if ( serviceClassName.equals( service.service.getClassName() ) )
      {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Method to test for the availability of a service
   * 
   * @param context
   *          the context to use
   * @param serviceClass
   *          the service class
   * @return true if intent can be called
   */
  public static boolean isServiceAvailable( Context context,
      Class< ? > serviceClass )
  {
    Intent intent = new Intent( serviceClass.getName() );
    List< ResolveInfo > list =
        context.getPackageManager().queryIntentServices( intent, 0 );
    return list.size() > 0;
  }
  
  /**
   * Method to test for the availability of the SDC service
   * 
   * @param context
   *          the context to use
   * @param serviceClass
   *          the service class
   * @return true if intent can be called
   */
  public static String getResolvedClassForService( Context context,
      Class< ? > serviceClass )
  {
    Intent intent = new Intent( serviceClass.getName() );
    List< ResolveInfo > list =
        context.getPackageManager().queryIntentServices( intent, 0 );
    
    if ( list.size() > 0 )
    {
      ResolveInfo info = list.get( 0 );
      if ( info.serviceInfo != null )
      {
        // return resolved name
        return info.serviceInfo.name;
      }
    }
    // return original name if none was resolved
    return serviceClass.getName();
  }
  
  /**
   * Method to stop a running service
   * 
   * @param applicationContext
   *          the application context
   * @param serviceClass
   *          the class of the service to stop
   * @return true if successful, false otherwise
   */
  public static boolean stopService( Context applicationContext,
      Class< ? > serviceClass )
  {
    // Intent intent =
    // new Intent( applicationContext, serviceClass );
    // applicationContext.stopService( intent );
    if ( !stopService( applicationContext, serviceClass.getName() ) )
    {
      return stopService( applicationContext, getResolvedClassForService(
          applicationContext, serviceClass ) );
    }
    return true;
  }
  
  /**
   * Method to stop a service by name (e.g. using the interface class name )
   * 
   * @param applicationContext
   *          the application context
   * @param serviceClassName
   *          the class name of the service to stop
   * @return true if successful, false otherwise
   */
  public static boolean stopService( Context applicationContext,
      String serviceClassName )
  {
    Intent intent =
        new Intent( serviceClassName );
    return applicationContext.stopService( intent );
  }
  
  /**
   * Method to start a service
   * 
   * @param applicationContext
   *          the application context
   * @param serviceClass
   *          the class of the service to start
   * @return If the service is being started or is already running, the
   *         ComponentName of the actual service that was started is returned;
   *         else if the service does not exist null is returned.
   */
  public static ComponentName startService( Context applicationContext,
      Class< ? > serviceClass )
  {
    // Intent intent =
    // new Intent( applicationContext, serviceClass );
    // return applicationContext.startService( intent );
    ComponentName component = startService( applicationContext, serviceClass.getName() );
    if ( component == null )
    {
      return startService( applicationContext, getResolvedClassForService(
          applicationContext, serviceClass ) );
    }
    return component;
  }
  
  /**
   * Method to start a service by name (e.g. using the interface class name )
   * 
   * @param applicationContext
   *          the application context
   * @param serviceClassName
   *          the class name of the service to start
   * @return If the service is being started or is already running, the
   *         ComponentName of the actual service that was started is returned;
   *         else if the service does not exist null is returned.
   */
  public static ComponentName startService( Context applicationContext,
      String serviceClassName )
  {
    Intent intent =
        new Intent( serviceClassName );
    return applicationContext.startService( intent );
  }
}
