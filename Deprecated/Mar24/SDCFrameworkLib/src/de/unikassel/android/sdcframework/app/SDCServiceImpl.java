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
package de.unikassel.android.sdcframework.app;

import java.io.PrintWriter;
import java.io.StringWriter;

import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.service.ServiceManagerImpl;
import de.unikassel.android.sdcframework.service.facade.ServiceManager;
import de.unikassel.android.sdcframework.util.DefaultUncaughtExceptionHandler;
import de.unikassel.android.sdcframework.util.LogfileManager;
import de.unikassel.android.sdcframework.util.Logger;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.RemoteException;
import android.widget.Toast;

/**
 * The sensor data collection framework service component. Internally it is
 * delegating to a {@linkplain ServiceManager management component}, which is
 * supervising all the other service components.
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "Registered" )
public final class SDCServiceImpl extends Service implements SDCService
{
  /**
   * the service binder
   */
  /**
   * 
   */
  private final ISDCService.Stub binder = new ISDCService.Stub()
  {
    /*
     * (non-Javadoc)
     * 
     * @see de.unikassel.android.sdcframework.app.facade.ISDCService#
     * doEnableSampleBroadCasting(boolean)
     */
    @Override
    public void doEnableSampleBroadCasting( boolean doEnable )
        throws RemoteException
    {
      getServiceManager().doEnableSampleBroadCasting( doEnable );
    }
  };
  
  /**
   * the notification identifier
   */
  private int NOTIFICATION = R.id.ServiceNotification;
  
  /**
   * the service manager maintaining the framework components
   */
  private ServiceManager servicManager;
  
  /**
   * Getter for the service manager
   * 
   * @return the service manager
   */
  public ServiceManager getServiceManager()
  {
    if ( servicManager == null )
    {
      setServiceManager( new ServiceManagerImpl() );
    }
    return servicManager;
  }
  
  /**
   * Setter for the service manager
   * 
   * @param serviceManager
   *          the service manager to set
   */
  public void setServiceManager( ServiceManager serviceManager )
  {
    // assure referential integrity
    ServiceManager oldServiceManager = this.servicManager;
    if ( oldServiceManager != serviceManager )
    {
      if ( oldServiceManager != null )
      {
        this.servicManager = null;
        oldServiceManager.setSDCService( null );
      }
      
      this.servicManager = serviceManager;
      
      if ( this.servicManager != null )
      {
        this.servicManager.setSDCService( this );
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onBind(android.content.Intent)
   */
  @Override
  public IBinder onBind( Intent intent )
  {
    return binder;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onCreate()
   */
  @Override
  public void onCreate()
  {
    initializeService();
    
    super.onCreate();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onDestroy()
   */
  @Override
  public void onDestroy()
  {
    shutDownService();
    
    // release instance references
    setServiceManager( null );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Service#onStartCommand(android.content.Intent, int, int)
   */
  @Override
  public int onStartCommand( Intent intent, int flags, int startId )
  {
    // signal service shall run until stopService is called explicitly
    return START_STICKY;
  }
  
  /**
   * main initialization method
   */
  private void initializeService()
  {
    // create and start service manager
    try
    {
      // first install default uncaught exception handler
      Thread.setDefaultUncaughtExceptionHandler( new DefaultUncaughtExceptionHandler(
          Thread.getDefaultUncaughtExceptionHandler() ) );
      
      Context applicationContext = getApplicationContext();
      getServiceManager().onCreate( applicationContext );
      getServiceManager().onResume( applicationContext );
      
      // sending a broadcast signaling running state changed to true
      broadcastRunningState( true );
      
      // create notification for controller activity access
      // and signal the user that the service was started.
      String text = "started";
      startForeground( getText( R.string.sdc_service_name ).toString() + " "
          + text );
      
      Logger.getInstance().info( this, text );
    }
    catch ( Exception ex )
    {
      Throwable e = ex.fillInStackTrace();
      StringWriter traceOut = new StringWriter();
      e.printStackTrace( new PrintWriter( traceOut, true ) );
      Logger.getInstance().error( this,
          "Fatal error on service initialization!\n" + traceOut.toString() );
      e.printStackTrace();
    }
  }
  
  /**
   * shutdown and clean up method for the service
   */
  private void shutDownService()
  {
    try
    {
      // stop and destroy service manager
      Context applicationContext = getApplicationContext();
      getServiceManager().onPause( applicationContext );
      
      // sending a broadcast signaling running state changed to false
      broadcastRunningState( false );
      
      // cancel the persistent notification
      stopForeground( true );
      
      // notify about state change
      String text = "stopped";
      Toast.makeText( this,
          getText( R.string.sdc_service_name ).toString() + " " + text,
          Toast.LENGTH_SHORT ).show();
      
      // destroy the service manager 
      //( will destroy maintained instances as well )
      getServiceManager().onDestroy( applicationContext );
      
      Logger.getInstance().info( this, text );
      setServiceManager( null );
      
      // finally destroy the global logger instance
      Logger.releaseInstance();
      LogfileManager.prepareReleaseInstance();
    }
    catch ( Exception ex )
    {
      StringWriter traceOut = new StringWriter();
      ex.printStackTrace( new PrintWriter( traceOut, true ) );
      Logger.getInstance().error( this,
          "Error on service shutdown!\n" + traceOut.toString() );
    }
  }
  
  /**
   * Does start the process in foreground and create and display a notification
   * for the user for quick access to the controlling activity.
   */
  private void startForeground( String text )
  {
    // create and send the persistent notification ( use local pho9ne time here
    // )
    Notification notification = new Notification( R.drawable.serviceicon, text,
        System.currentTimeMillis() );
    
    // the PendingIntent to launch the controller activity if the user selects
    // this notification
    PendingIntent contentIntent =
        PendingIntent.getActivity( this, 0, new Intent( this,
            SDCServiceController.class ), 0 );
    
    // set the info for the views that show in the notification panel.
    notification.setLatestEventInfo( this,
        getText( R.string.sdc_service_label ), text, contentIntent );
    
    // send the notification.
    // getNotificationManager().notify( NOTIFICATION, notification );
    startForeground( NOTIFICATION, notification );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * android.app.Service#onConfigurationChanged(android.content.res.Configuration
   * )
   */
  @Override
  public void onConfigurationChanged( Configuration newConfig )
  {
    super.onConfigurationChanged( newConfig );
  }
  
  /**
   * Getter for the service context power manager
   * 
   * @return the power manager in this context
   */
  public PowerManager getPowerManager()
  {
    return (PowerManager) getSystemService( Context.POWER_SERVICE );
    
  }
  
  /**
   * Method to broadcast the service running state change
   * 
   * @param isRunning
   *          flag for the service running state to broadcast
   */
  private void broadcastRunningState( boolean isRunning )
  {
    Intent intent = new Intent();
    intent.setAction( ACTION );
    intent.putExtra( INTENT_NAME_RUNNING_FLAG, isRunning );
    sendBroadcast( intent );
  }
}
