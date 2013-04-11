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

import android.content.Intent;
import de.unikassel.android.sdcframework.util.facade.EventErrorTypes;

/**
 * The time error event is used to signal time provider errors.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TimeErrorEvent extends AbstractTimeProviderEvent
{
  /**
   * Out custom intent action.
   */
  public static final String ACTION =
      "de.unikassel.android.sdcframework.intent.action.TIME_ERROR";
  
  /**
   * The intent identifier for the error field.
   */
  protected final static String ERROR = "offset";
  
  /**
   * The error
   */
  private final EventErrorTypes error;
  
  /**
   * Constructor
   * 
   * @param error
   *          the error
   * @param ts
   *          the time stamp.
   */
  public TimeErrorEvent( long ts, EventErrorTypes error )
  {
    super( ts );
    this.error = error;
  }
  
  /**
   * Constructor
   * 
   * @param intent
   *          the intent to create from
   */
  public TimeErrorEvent( Intent intent )
  {
    super( intent );
    if ( ACTION.equals( intent.getAction() ) )
    {
      this.error = EventErrorTypes.valueOf( intent.getStringExtra( ERROR ) );
    }
    else
    {
      this.error = null;
    }
  }
  
  /**
   * Getter for the error
   * 
   * @return the error
   */
  public EventErrorTypes getError()
  {
    return error;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.TimeProviderEvent#isSynced()
   */
  @Override
  public boolean isSynced()
  {
    return !( EventErrorTypes.OUT_OF_SYNC.equals( error ) || EventErrorTypes.TIME_SYNC_ERROR.equals( error ) );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.AbstractTimeProviderEvent#
   * putAdditionalExtras(android.content.Intent)
   */
  @Override
  protected void putAdditionalExtras( Intent intent )
  {
    intent.putExtra( ERROR, error.name() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.AbstractTimeProviderEvent#getAction
   * ()
   */
  @Override
  protected String getAction()
  {
    return ACTION;
  }
  
}
