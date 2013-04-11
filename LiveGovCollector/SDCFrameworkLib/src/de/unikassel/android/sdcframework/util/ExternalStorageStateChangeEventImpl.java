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

import de.unikassel.android.sdcframework.util.facade.ExternalStorageStateChangeEvent;

/**
 * Implementation of the external storage state change event.
 * 
 * @author Katy Hilgenberg
 * 
 */
/**
 * @author Katy Hilgenberg
 *
 */
public class ExternalStorageStateChangeEventImpl implements
    ExternalStorageStateChangeEvent
{
  /**
   * Internal state representation for external storage availability
   * 
   * @author Katy Hilgenberg
   * 
   */
  public enum State
  {
    /**
     * external storage is not available
     */
    UNAVAILABLE,
    
    /**
     * external storage is mounted for read
     */
    AVAILABLE_FOR_READ,
    
    /**
     * external storage is mounted for write
     */
    AVAILABLE_FOR_WRITE
  };
  
  /**
   * The current availability state
   */
  private final State state;
  
  /**
   * Constructor
   * 
   * @param state
   *          the current availability state
   */
  public ExternalStorageStateChangeEventImpl( State state )
  {
    super();
    this.state = state;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.ExternalStorageStateChangeEvent
   * #isExternalStorageAvailableForRead()
   */
  @Override
  public boolean isExternalStorageAvailableForRead()
  {
    return state != State.UNAVAILABLE;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.ExternalStorageStateChangeEvent
   * #isExternalStorageAvailableForWrite()
   */
  @Override
  public boolean isExternalStorageAvailableForWrite()
  {
    return state == State.AVAILABLE_FOR_WRITE;
  }  
}
