/*
 * Copyright (C) 2012, Katy Hilgenberg
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

import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

/**
 * A simple class to maintain a wake lock instance
 * 
 * @author Katy Hilgenberg
 * 
 */
public class WakeLockHolder
{
  
  /**
   * The wake lock
   */
  private final WakeLock wakeLock;
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   */
  public WakeLockHolder( Context context )
  {
    this.wakeLock = createWakeLock( context );
  }
  
  /**
   * Getter for the wake lock
  
   * @return the wake lock
   */
  public WakeLock getWakeLock()
  {
    return wakeLock;
  }

  /**
   * Method to create a wake lock
   * 
   * @param context
   *          the context
   * @return the wake lock
   */
  private final WakeLock createWakeLock( Context context )
  {
    PowerManager pm =
        (PowerManager) context.getSystemService( Context.POWER_SERVICE );
    WakeLock newWakeLock = pm.newWakeLock( PowerManager.ACQUIRE_CAUSES_WAKEUP
              | PowerManager.SCREEN_DIM_WAKE_LOCK
            | PowerManager.ON_AFTER_RELEASE,
            getClass().getSimpleName() );
    Logger.getInstance().debug( this, hashCode() + ": wake lock created" );
    return newWakeLock;
  }
  
  /**
   * Does release the wake lock
   */
  public final void releaseWakeLock()
  {
    if ( wakeLock != null && wakeLock.isHeld() )
    {
      wakeLock.release();
      Logger.getInstance().debug( this, hashCode() + ": wake lock released" );
    }
  }
  
  /**
   * Does acquire the wake lock
   */
  public final void acquireWakeLock()
  {
    if ( wakeLock != null )
    {
      wakeLock.acquire();
      Logger.getInstance().debug( this, hashCode() + ": wake lock aquired" );
    }
  }
  
}
