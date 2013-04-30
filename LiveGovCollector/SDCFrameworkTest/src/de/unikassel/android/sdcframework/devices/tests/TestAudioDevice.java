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
package de.unikassel.android.sdcframework.devices.tests;

import de.unikassel.android.sdcframework.devices.AudioDevice;
import de.unikassel.android.sdcframework.devices.VirtualSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import android.test.AndroidTestCase;

/**
 * Test for the audio device type.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TestAudioDevice extends AndroidTestCase
{
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.test.AndroidTestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }
  
  /**
   * Test method for
   * {@link de.unikassel.android.sdcframework.devices.AudioDevice#AudioDevice(android.content.Context)}
   * .
   */
  public final void testAudioDevice()
  {
    AudioDevice device = new AudioDevice( getContext() );
    assertTrue( "expected that an audio device is a virtual sensor device",
        device instanceof VirtualSensorDevice );
    assertEquals( "unexpected device identifier",
        SensorDeviceIdentifier.Audio, device.getDeviceIdentifier() );
    assertTrue( "expected device always enabled",
        device.isDeviceInSystemEnabled( getContext() ) );
  }
  
}
