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
package de.unikassel.android.sdcframework.data;

import java.security.InvalidParameterException;

import de.unikassel.android.sdcframework.data.independent.DeviceInformation;

/**
 * The concrete device information data used internally.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ConcreteDeviceInformation extends DeviceInformation
{
  /**
   * Constructor
   * 
   * @param sUuid
   *          the unique identifier string representation
   */
  public ConcreteDeviceInformation( String sUuid )
  {
    if ( sUuid == null )
      throw new InvalidParameterException( "UUID can not be null" );
    setUuid( sUuid );
    setProduct( android.os.Build.PRODUCT );
    setModel( android.os.Build.MODEL );
    setDevice( android.os.Build.DEVICE );
    setManufacturer( android.os.Build.MANUFACTURER );
    setId( android.os.Build.ID );
    setRelease( android.os.Build.VERSION.RELEASE );
    setFingerprint( android.os.Build.FINGERPRINT );
  }
  
}
