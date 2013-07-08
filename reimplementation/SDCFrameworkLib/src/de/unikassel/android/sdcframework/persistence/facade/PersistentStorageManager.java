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
package de.unikassel.android.sdcframework.persistence.facade;

import android.app.Activity;
import android.content.Context;
import de.unikassel.android.sdcframework.preferences.facade.ServiceConfiguration;
import de.unikassel.android.sdcframework.util.facade.AsynchrounousSampleObserver;

/**
 * Interface for the persistent storage manager an
 * asynchronous sample observer, responsible for the persistent storage task.
 * 
 * @see de.unikassel.android.sdcframework.persistence.PersistentStorageManagerImpl
 * @author Katy Hilgenberg
 * 
 */
public interface PersistentStorageManager
    extends AsynchrounousSampleObserver, DatabaseManager
{
  
  /**
   * Method to update internal database full strategy chain by configuration of
   * the service
   * 
   * @param context
   *          the application context
   * @param config
   *          the current service configuration to update from
   * @param controlActivityClass
   *          the control activity class or null 
   */
  public abstract void updateDatabaseFullStrategy( Context context,
      ServiceConfiguration config, Class< ? extends Activity > controlActivityClass );
  
  /**
   * Getter for the savedRecordCount
   * 
   * @return the savedRecordCount
   */
  public abstract long getSavedRecordCount();
  
  /**
   * Does execute the current outstanding database command
   * 
   * @return true if successful, false otherwise
   */
  public abstract boolean doExecuteCurrentCommand();
}