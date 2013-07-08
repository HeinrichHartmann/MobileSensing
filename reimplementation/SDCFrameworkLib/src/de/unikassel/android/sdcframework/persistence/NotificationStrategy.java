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

import android.app.Activity;
import android.content.Context;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.persistence.facade.PersistentStorageManager;
import de.unikassel.android.sdcframework.util.NotificationUtils;

/**
 * Implementation of a the database full strategy as service notification. <br/>
 * <br/>
 * This strategy does only create a user notification, which whill be displayed
 * in the OS notification area . It should be used in the beginning of a
 * strategy chain.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class NotificationStrategy
    extends AbstractDatabaseFullStrategy
{
  
  /**
   * The notification id
   */
  public final static int NOTIFICATION = R.id.DatabaseFullNotification;
  
  /**
   * The application context
   */
  private final Context context;
  
  /**
   * The control activity class
   */
  private final Class< ? extends Activity > controlActivityClass;
  
  /**
   * Constructor
   * 
   * @param context
   *          the application context
   * @param controlActivityClass
   */
  public NotificationStrategy( Context context,
      Class< ? extends Activity > controlActivityClass )
  {
    super();
    this.context = context;
    this.controlActivityClass = controlActivityClass;
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
    NotificationUtils.serviceNotification( NOTIFICATION,
        "Maximum database size exceeded!", context, true, true,
        controlActivityClass );
    
    // this strategy can not really solve the problem ;)
    return false;
  }
}
