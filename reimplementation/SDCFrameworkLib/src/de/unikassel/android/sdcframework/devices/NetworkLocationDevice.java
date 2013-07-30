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
package de.unikassel.android.sdcframework.devices;

import android.content.Context;
import android.location.LocationManager;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.data.independent.NetworkLocationSampleData;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Implementation of the network location sensor device using the location
 * manager to get cell tower or wlan based location information.
 * 
 * @see ScannerStateAwareSensorDevice
 * @see SampleProvidingSensorDevice
 * @author Katy Hilgenberg
 */
public final class NetworkLocationDevice
    extends AbstractLocationDevice
{
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   */
  public NetworkLocationDevice( Context context )
  {
    super( context, SensorDeviceIdentifier.NetworkLocation,
        LocationManager.NETWORK_PROVIDER );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractLocationDevice#
   * getCurrentSampleData()
   */
  @Override
  protected SampleData getCurrentSampleData()
  {
    Logger.getInstance().info(this, "Latitude="+getLocationData().getLatitude()+": Longitude="+getLocationData().getLongitude());
    return new NetworkLocationSampleData( getLocationData() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractLocationDevice#
   * getDeviceDisabledMessageID()
   */
  @Override
  protected int getDeviceDisabledMessageID()
  {
    return R.string.msg_netloc_disabled;
  }
}
