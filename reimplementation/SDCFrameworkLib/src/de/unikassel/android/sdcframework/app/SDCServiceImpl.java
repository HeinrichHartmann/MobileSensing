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
package de.unikassel.android.sdcframework.app;

import de.unikassel.android.sdcframework.service.facade.ServiceManager;
import android.annotation.SuppressLint;
import android.app.Activity;

/**
 * The sensor data collection framework service component. Internally it is
 * delegating to a {@linkplain ServiceManager management component}, which is
 * supervising all the other service components.
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "Registered" )
public final class SDCServiceImpl 
extends AbstractSDCServiceImpl 
{
  
  /* (non-Javadoc)
   * @see de.unikassel.android.sdcframework.app.facade.SDCService#getControlActivityClass()
   */
  @Override
  public Class< ? extends Activity > getControlActivityClass()
  {
    return SDCServiceController.class;
  }

}
