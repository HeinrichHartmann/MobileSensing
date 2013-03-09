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

import java.security.InvalidParameterException;

import android.os.Handler;

/**
 * This class does implement a runnable task to be executed delayed by an OS
 * handler in the service context. <br/>
 * <br/>
 * Every time this runnable is executed, it will post itself for another delayed
 * execution using the current configured frequency. Further more, each time it
 * is executed the linked scanner is invoked to take a sample from its
 * associated device.
 * 
 * @see SampleTakingDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
public final class SampleTakingTask implements Runnable
{
  /**
   * The scanner taking the samples on runnable execution
   */
  private final SampleTakingDeviceScanner scanner;
  
  /**
   * The handler to repost the task to for delayed runnable execution
   */
  private final Handler handler;
  
  /**
   * Constructor
   * 
   * @param scanner
   *          the scanner taking the samples on runnable execution
   * @param handler
   *          the handler to repost the task to for delayed runnable execution
   */
  public SampleTakingTask( SampleTakingDeviceScanner scanner, Handler handler )
  {
    super();
    if ( scanner == null )
      throw new InvalidParameterException( "scanner is null" );
    if ( handler == null )
      throw new InvalidParameterException( "handler is null" );
    this.scanner = scanner;
    this.handler = handler;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public final void run()
  {
    // restart timed task first
    if ( scanner.isEnabled() )
    {
      int frequency = scanner.getDevice().getConfiguration().getFrequency();
      handler.postDelayed( this, frequency );
      
      // take a sample
      scanner.takeSample();
    }
  }
}