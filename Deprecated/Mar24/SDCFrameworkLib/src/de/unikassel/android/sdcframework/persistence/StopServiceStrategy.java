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
package de.unikassel.android.sdcframework.persistence;

import android.content.Context;
import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager;
import de.unikassel.android.sdcframework.service.ServiceUtils;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of a the database full strategy which does stop the running
 * service. <br/>
 * <br/>
 * This strategy does stop the running data collection service. It should always
 * be the last strategy in a chain. To notify the user a
 * {@linkplain NotificationStrategy} should be one of it's predecessors.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class StopServiceStrategy
    extends AbstractDatabaseFullStrategy
{
  /**
   * The application context
   */
  private final Context context;
  
  /**
   * The service class
   */
  private final Class< ? extends SDCService > serviceClass;
  
  /**
   * Constructor
   * 
   * @param context
   *          the application context
   * @param serviceClass
   *          the service class
   */
  public StopServiceStrategy( Context context,
      Class< ? extends SDCService > serviceClass )
  {
    super();
    this.context = context;
    this.serviceClass = serviceClass;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.persistence.AbstractDatabaseFullStrategy
   * #execute
   * (de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager
   * )
   */
  @Override
  public boolean process( PersistentStorageManager storageManager )
  {
    if( !ServiceUtils.stopService( context, serviceClass ) )
    {
      Logger.getInstance().error( this, "Failed to stop service" );
    }
    
    // we do ignore successors here as the service will be stopped
    // and do signal unresolved problem to the caller
    return false;
  }
  
}
