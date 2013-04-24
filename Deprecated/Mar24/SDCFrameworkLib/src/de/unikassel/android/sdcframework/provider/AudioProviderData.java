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
package de.unikassel.android.sdcframework.provider;

import java.util.HashMap;

import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;

/**
 * The class holding the audio provider related data.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class AudioProviderData
    extends AbstractContentProviderData
{
  /**
   * The sound record location
   */
  public static final String LOCATION = "location";
  
  /**
   * The singleton instance
   */
  private final static AudioProviderData instance =
      new AudioProviderData();
  
  /**
   * Constructor 
   */
  private AudioProviderData()
  {
    super();
  }

  /*
   * (non-Javadoc)
   * 
   * @seede.unikassel.android.sdcframework.provider.facade.ContentProviderData#
   * getContentTypeName()
   */
  @Override
  public String getContentTypeName()
  {
    return "sndrecordloc";
  }
  
  /**
   * Getter for the singleton instance
   * 
   * @return the singleton instance
   */
  public static final ContentProviderData getInstance()
  {
    return instance;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.provider.AbstractContentProviderData#
   * getProjectionMap()
   */
  @Override
  public HashMap< String, String > getProjectionMap()
  {
    HashMap< String, String > projectionMap = super.getProjectionMap();
    projectionMap.put( AudioProviderData.LOCATION,
        AudioProviderData.LOCATION );
    return projectionMap;
  }
}