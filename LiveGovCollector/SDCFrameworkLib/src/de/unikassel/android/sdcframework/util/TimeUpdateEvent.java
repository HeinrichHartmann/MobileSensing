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

/**
 * The observable time update event. Used to signal state changes of the time
 * provider and to distribute the time offset.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class TimeUpdateEvent extends AbstractTimeProviderEvent
{
  /**
   * Out custom intent action.
   */
  public static final String ACTION =
      "de.unikassel.android.sdcframework.intent.action.TIME_OFFSET";
  
  /**
   * The intent identifier for the message field.
   */
  protected final static String OFFSET = "offset";
  
  /**
   * The time stamp offset.
   */
  final long offset;
  
  /**
   * Constructor
   */
  @SuppressWarnings( "unused" )
  private TimeUpdateEvent( long ts )
  {
    this( ts, 0L );
  }
  
  /**
   * Constructor
   * 
   * @param ts
   *          the time stamp   * 
   * @param offset
   *          the time offset
   */
  public TimeUpdateEvent( long ts, long offset )
  {
    super( ts );
    this.offset = offset;
  }
  
  /**
   * Constructor
   * 
   * @param intent
   *          the intent to create from
   */
  public TimeUpdateEvent( Intent intent )
  {
    super( intent );
    this.offset = intent.getLongExtra( OFFSET, 0L );
  }
  
  /**
   * Getter for the time offset
   * 
   * @return the time offset
   */
  public final long getTimeOffset()
  {
    return offset;
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
    return true;
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
    intent.putExtra( OFFSET, offset );
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
