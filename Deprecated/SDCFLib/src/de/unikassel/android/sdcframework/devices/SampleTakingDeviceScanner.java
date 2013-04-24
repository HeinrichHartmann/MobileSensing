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
import android.os.Handler;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Base class for sensor device scanner types, which do active take a sample of
 * the associated device in the configured frequency. <br/>
 * <br/>
 * The timing is done by the usage of an OS handler to schedule the timed task. <br/>
 * This scanner is designed to work together with devices implementing the
 * {@linkplain SampleProvidingSensorDevice} interface.
 * 
 * @see SampleProvidingSensorDevice
 * @see AccelerometerDeviceScanner
 * @see GSMDeviceScanner
 * @see GPSDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
public abstract class SampleTakingDeviceScanner extends
    AbstractSensorDeviceScanner
{
  
  /**
   * The handler used for timing instead of a timer object
   */
  private Handler handler;
  
  /**
   * The timer task to be executed on timer event
   */
  private SampleTakingTask timerTask;
  
  /**
   * Constructor
   */
  public SampleTakingDeviceScanner()
  {
    super();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDeviceScanner#
   * isCompatibleDevice
   * (de.unikassel.android.sdcframework.devices.facade.SensorDevice)
   */
  @Override
  protected boolean isCompatibleDevice( SensorDevice device )
  {
    return ( device instanceof SampleProvidingSensorDevice );
  }
  
  /**
   * The cyclic executed method to take a sample from the device
   */
  public void takeSample()
  {
    try
    {
      SampleProvidingSensorDevice sampleProvidingSensorDevice =
            (SampleProvidingSensorDevice) getDevice();
      
      if ( sampleProvidingSensorDevice.hasSample() )
      {
        Sample sample =
              sampleProvidingSensorDevice.getSample();
        notify( sample );
      }
    }
    catch ( Exception e )
    {
      Logger.getInstance().error( this, "Exception in takeSample" );
    }
  }
  
  /**
   * Getter for the handler
   * 
   * @return the handler
   */
  protected final Handler getHandler()
  {
    if ( handler == null )
    {
      setHandler( new Handler() );
    }
    return handler;
  }
  
  /**
   * Setter for the handler
   * 
   * @param handler
   *          the handler to set
   */
  private final void setHandler( Handler handler )
  {
    this.handler = handler;
  }
  
  /**
   * Getter for the timer task
   * 
   * @return the timer task
   */
  protected final SampleTakingTask getTimerTask()
  {
    if ( timerTask == null )
    {
      setTimerTask( new SampleTakingTask( this, getHandler() ) );
    }
    return timerTask;
  }
  
  /**
   * Setter for the timer task
   * 
   * @param timerTask
   *          the timer task to set
   */
  private final void setTimerTask( SampleTakingTask timerTask )
  {
    this.timerTask = timerTask;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#start
   * (android.content.Context)
   */
  @Override
  public boolean start( Context context )
  {
    // start repeated timed task
    Handler handler = getHandler();
    Logger.getInstance().debug( this,
        "Starting for handler " + handler.toString() );
    SampleTakingTask timerTask = getTimerTask();
    handler.removeCallbacks( timerTask );
    handler.postDelayed( timerTask, 50 );
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#stop
   * (android.content.Context)
   */
  @Override
  public boolean stop( Context context )
  {
    // stop our next timed task
    Handler handler = getHandler();
    handler.removeCallbacks( getTimerTask() );
    Logger.getInstance().debug( this,
        "Stopped for handler " + handler.toString() );
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.AbstractSensorDeviceScanner#onDestroy
   * (android.content.Context)
   */
  @Override
  public void onDestroy( Context context )
  {
    super.onDestroy( context );
    setHandler( null );
    setTimerTask( null );
  }
}