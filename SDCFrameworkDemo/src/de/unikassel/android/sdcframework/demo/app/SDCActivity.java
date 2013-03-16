/*
 * Copyright (C) 2012, Katy Hilgenberg.
 * Special acknowledgments to: Knowledge & Data Engineering Group, University of Kassel (http://www.kde.cs.uni-kassel.de).
 * Contact: sdcf@cs.uni-kassel.de
 *
 * This file is part of the SDCFramework project.
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
package de.unikassel.android.sdcframework.demo.app;

import de.unikassel.android.sdcframework.demo.related.util.SimpleServiceConnectionEventReceiver;
import de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;

/**
 * Base class for activities using the SDC service.
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "Registered" )
public abstract class SDCActivity
    extends Activity
{
  
  /**
   * The SDC service connection holder
   */
  private final SDCServiceConnectionHolder connectionHolder;
  
  /**
   * The SDC service connection event receiver
   */
  private final SimpleServiceConnectionEventReceiver sdcEventReceiver;
  
  /**
   * Constructor
   */
  public SDCActivity()
  {
    super();
    this.sdcEventReceiver = new SimpleServiceConnectionEventReceiver( this );
    this.connectionHolder =
        new SDCServiceConnectionHolder( sdcEventReceiver, SDCActivityConstants.serviceClass );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    connectionHolder.onCreate( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onDestroy()
   */
  @Override
  protected void onDestroy()
  {
    connectionHolder.onDestroy( this );
    super.onDestroy();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume()
  {
    super.onResume();
    connectionHolder.onResume( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause()
  {
    connectionHolder.onPause( this );
    super.onPause();
  }
}