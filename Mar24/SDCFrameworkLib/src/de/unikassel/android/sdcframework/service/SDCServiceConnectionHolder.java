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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.util.facade.LifeCycleObject;

/**
 * Implementation of life cycle dependent SDC service connection holder.<br/>
 * <br/>
 * This class can establish, destroy and observe a connection with the SDC
 * service. It does act based on the activity life cycle, thus the connection is
 * established on creation and destroyed on destruction. The related methods
 * have to be called by the controlling activity. To get notifications about
 * related events, the activity should implement the
 * ServiceConnectionEventReceiver interface.
 * 
 * 
 * @author Katy Hilgenberg
 * 
 */
public class SDCServiceConnectionHolder
    implements LifeCycleObject
{
  /**
   * Interface for receivers of service connection events
   * 
   * @author Katy Hilgenberg
   * 
   */
  public interface ServiceConnectionEventReceiver
  {
    /**
     * This event is raised on connection establishment
     * 
     * @param sdcService
     */
    public abstract void onConnectionEstablished( ISDCService sdcService );
    
    /**
     * This event is raised right before connection will be closed
     * 
     * @param sdcService
     *          the AIDL SDC service interface
     */
    public abstract void onAboutToDisconnect( ISDCService sdcService );
    
    /**
     * This event is raised in case of lost connection
     */
    public abstract void onConnectionLost();
    
    /**
     * This event is raised if binding failed
     */
    public abstract void onBindingFailed();
    
    /**
     * This event is raised if the service is not available on the system
     */
    public abstract void onServiceUnavailable();
  }
  
  /**
   * The service connection
   */
  private final ServiceConnection serviceConnection;
  
  /**
   * The service availability state flag
   */
  private boolean serviceAvailable;
  
  /**
   * The service interface
   */
  private ISDCService sdcService;
  
  /**
   * The event receiver list
   */
  private final List< ServiceConnectionEventReceiver > listEventReceivers;
  
  /**
   * The service class
   */
  private final Class< ? > serviceClass;
  
  /**
   * The service component name
   */
  private ComponentName serviceComponent;
  
  /**
   * Constructor
   * 
   * @param receiver
   *          the event receiver
   * @param serviceClass
   *          the service class
   */
  public SDCServiceConnectionHolder( ServiceConnectionEventReceiver receiver,
      Class< ? > serviceClass )
  {
    this.serviceClass = serviceClass;
    this.listEventReceivers =
        Collections.synchronizedList( new LinkedList< ServiceConnectionEventReceiver >() );
    addEventReceiver( receiver );
    
    this.serviceConnection = new ServiceConnection()
    {
      
      /*
       * (non-Javadoc)
       * 
       * @see
       * android.content.ServiceConnection#onServiceDisconnected(android.content
       * .ComponentName)
       */
      @Override
      public void onServiceDisconnected( ComponentName name )
      {
        sdcService = null;
        for ( ServiceConnectionEventReceiver receiver : listEventReceivers )
        {
          receiver.onConnectionLost();
        }
      }
      
      /*
       * (non-Javadoc)
       * 
       * @see
       * android.content.ServiceConnection#onServiceConnected(android.content
       * .ComponentName, android.os.IBinder)
       */
      @Override
      public void onServiceConnected( ComponentName name, IBinder service )
      {
        sdcService = ISDCService.Stub.asInterface( service );
        
        for ( ServiceConnectionEventReceiver receiver : listEventReceivers )
        {
          receiver.onConnectionEstablished( sdcService );
        }
      }
    };
  }
  
  /**
   * Getter for the serviceComponent
   * 
   * @return the serviceComponent
   */
  public ComponentName getServiceComponent()
  {
    return serviceComponent;
  }
  
  /**
   * Setter for the serviceComponent
   * 
   * @param serviceComponent
   *          the serviceComponent to set
   */
  public void setServiceComponent( ComponentName serviceComponent )
  {
    this.serviceComponent = serviceComponent;
  }
  
  /**
   * Constructor
   * 
   * @param serviceClass
   *          the service class
   * 
   */
  public SDCServiceConnectionHolder( Class< ? extends SDCService > serviceClass )
  {
    this( null, serviceClass );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onResume(
   * android.content.Context)
   */
  @Override
  public void onResume( Context applicationContext )
  { 

  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onPause(android
   * .content.Context)
   */
  @Override
  public void onPause( Context applicationContext )
  { 

  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onCreate(
   * android.content.Context)
   */
  @Override
  public void onCreate( Context applicationContext )
  {
    // test for service availability first
    serviceAvailable =
        ServiceUtils.isServiceAvailable( applicationContext, serviceClass );
    if ( serviceAvailable )
    {
      // if service is not running we do start it to avoid service get's
      // destroyed on unbound
      if ( !ServiceUtils.isServiceRunning( applicationContext, serviceClass ) )
      {
        ServiceUtils.startService( applicationContext, serviceClass );
      }
      
      Intent intent = new Intent( serviceClass.getName() );
      if ( !applicationContext.bindService( intent, serviceConnection, 0 ) )
      {
        for ( ServiceConnectionEventReceiver receiver : listEventReceivers )
        {
          receiver.onBindingFailed();
        }
      }
    }
    else
    {
      for ( ServiceConnectionEventReceiver receiver : listEventReceivers )
      {
        receiver.onServiceUnavailable();
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.LifeCycleObject#onDestroy
   * (android.content.Context)
   */
  @Override
  public void onDestroy( Context applicationContext )
  {
    if ( serviceAvailable )
    {
      if ( sdcService != null )
      {
        for ( ServiceConnectionEventReceiver receiver : listEventReceivers )
        {
          receiver.onAboutToDisconnect( sdcService );
        }
      }
      
      applicationContext.unbindService( serviceConnection );
    }
  }
  
  /**
   * Method to add an event receiver
   * 
   * @param receiver
   *          the event receiver
   */
  public void addEventReceiver( ServiceConnectionEventReceiver receiver )
  {
    if ( receiver != null )
      listEventReceivers.add( receiver );
  }
  
  /**
   * Method to remove an event receiver
   * 
   * @param receiver
   *          the event receiver
   */
  public void removeEventReceiver( ServiceConnectionEventReceiver receiver )
  {
    if ( receiver != null )
      listEventReceivers.remove( receiver );
  }
}
