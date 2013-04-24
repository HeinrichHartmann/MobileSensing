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
import de.unikassel.android.sdcframework.util.facade.BroadcastableEvent;
import de.unikassel.android.sdcframework.util.facade.TimeProviderEvent;

/**
 * Abstract base class for time provider events
 * 
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractTimeProviderEvent
    implements BroadcastableEvent, TimeProviderEvent
{
  
  /**
   * The intent identifier for the time stamp field
   */
  public static final String TS = "ts";
  
  /**
   * The time stamp in UTC.
   */
  protected final long ts;
  
  /**
   * Constructor
   * 
   * @param ts
   *          the time stamp.
   */
  public AbstractTimeProviderEvent( long ts )
  {
    super();
    this.ts = ts;
  }
  
  /**
   * Constructor
   * 
   * @param intent
   *          the intent to create from
   */
  public AbstractTimeProviderEvent( Intent intent )
  {
    this.ts = intent.getLongExtra( TS, 0L );
  }
  
  @Override
  public final Intent getIntent()
  {
    Intent intent = new Intent();
    intent.setAction( getAction() );
    intent.putExtra( TS, ts );
    putAdditionalExtras( intent );
    return intent;
  }
  
  /**
   * Method to add extra information for intent creation.
   * 
   * @param intent
   *          the intent to add extras to
   */
  protected abstract void putAdditionalExtras( Intent intent );
  
  /**
   * Getter for the intent action.
   * 
   * @return the intent action.
   */
  protected abstract String getAction();
  
  @Override
  public long getTimeStamp()
  {
    return ts;
  }
  
}