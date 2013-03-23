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

/**
 * This is a base class for a passive sample taking device scanner, which does
 * ignore the sample frequency and is triggered by the associated device to for
 * sampling. <br/>
 * It does extend the sample taking device scanner to derive the
 * {@link #takeSample()} method, but overrides the start and stop methods to
 * avoid spawning a timer task.
 * 
 * It is intended to be used for the binary sensor types like light and
 * proximity, which does rarely generate events.
 * 
 * @see TemperatureDeviceScanner
 * @see LightDeviceScanner
 * @see ProximityDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
public class PassiveSampleTakingDeviceScanner extends SampleTakingDeviceScanner
{
  
  /**
   * Constructor
   */
  public PassiveSampleTakingDeviceScanner()
  {
    super();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.SampleTakingDeviceScanner#start
   * (android.content.Context)
   */
  @Override
  public final boolean start( Context context )
  {
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.SampleTakingDeviceScanner#stop
   * (android.content.Context)
   */
  @Override
  public final boolean stop( Context context )
  {
    return true;
  }
  
}
