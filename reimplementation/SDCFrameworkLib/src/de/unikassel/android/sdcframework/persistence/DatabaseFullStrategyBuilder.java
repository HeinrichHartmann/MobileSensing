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
import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.persistence.facade.DatabaseFullStrategy;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;

/**
 * A static implementation of a builder for database full strategy chains. <br/>
 * <br/>
 * This builder does create a chain of database full strategies according to the
 * given @link {@link DBFullStrategyDescription description} in the service
 * configuration.
 * 
 * @see de.unikassel.android.sdcframework.service.ServiceManagerImpl
 * @see de.unikassel.android.sdcframework.persistence.PersistentStorageManagerImpl
 * @author Katy Hilgenberg
 * 
 */
public class DatabaseFullStrategyBuilder
{
  
  /**
   * Constructor
   */
  private DatabaseFullStrategyBuilder()
  {
    super();
  }
  
  /**
   * Does build a database full strategy chain as configured by the given
   * definition
   * 
   * @param context
   *          the application context
   * @param serviceConfig
   *          the current service configuration
   * @param serviceClass
   *          the service class
   * @param activityClass
   *          the class of the control activity
   * @return the chain of database full strategies represented by the definition
   */
  public static DatabaseFullStrategy buildStrategy( Context context,
      ServiceConfiguration serviceConfig,
      Class< ? extends SDCService > serviceClass,
      Class< ? extends Activity > activityClass )
  {
    DatabaseFullStrategy strategy =
        new WaitStrategy( serviceConfig.getDBFullWaitTime() );
    
    switch ( serviceConfig.getDBFullStrategy() )
    {
      case WAIT_DELETE_NOTIFY:
      {
        strategy.withSuccessor( new DeleteSamplesStrategy(
            serviceConfig.getDBFullDeletionRecordCount(),
            serviceConfig.isDBFullDeletionPriorityBased() ) )
            .withSuccessor( new NotificationStrategy( context, activityClass ) );
        break;
      }
      case WAIT_NOTIFY_STOPSERVICE:
      {
        strategy.withSuccessor( new NotificationStrategy( context, activityClass ) )
            .withSuccessor( new StopServiceStrategy( context, serviceClass ) );
        break;
      }
    }
    return strategy;
  }
}
