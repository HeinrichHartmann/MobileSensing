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
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner;
import de.unikassel.android.sdcframework.util.ObservableEventSourceImpl;

/**
 * Base class for any sensor device scanner type.
 * 
 * @author Katy Hilgenberg
 * 
 */
/**
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractSensorDeviceScanner
    extends ObservableEventSourceImpl< Sample >
    implements SensorDeviceScanner
{
  /**
   * the scanned sensor device
   */
  private SensorDevice device;
  
  /**
   * The active state flag
   */
  private final AtomicBoolean isActive;
  
  /**
   * Constructor
   */
  public AbstractSensorDeviceScanner()
  {
    super();
    isActive = new AtomicBoolean( false );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#setDevice
   * (de.unikassel.android.sdcframework.devices.facade.SensorDevice,
   * android.content.Context)
   */
  @Override
  public final void setDevice( SensorDevice device, Context context )
  {
    if ( device != null && !isCompatibleDevice( device ) )
      throw new InvalidParameterException( "Incompatible device type!" );
    
    SensorDevice oldDevice = this.device;
    if ( oldDevice != device )
    {
      if ( oldDevice != null )
      {
        this.device = null;
        oldDevice.setScanner( null, context );
      }
      this.device = device;
      if ( this.device != null )
      {
        this.device.setScanner( this, context );
      }
    }
  }
  
  /**
   * Validation method for a compatible device type
   * 
   * @param device
   *          the device to test for compatibility
   */
  protected abstract boolean isCompatibleDevice( SensorDevice device );
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner
   * #getDevice()
   */
  @Override
  public final SensorDevice getDevice()
  {
    return device;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#isEnabled
   * ()
   */
  @Override
  public final boolean isEnabled()
  {
    return isActive();
  }
  
  /**
   * Setter for the scanner activation state
   * 
   * @param active
   *          the state to set
   */
  protected final void setActive( boolean active )
  {
    isActive.set( active );
  }
  
  /**
   * Getter for the scanner activation state
   * 
   * @return the scanner activation state
   */
  protected final boolean isActive()
  {
    return isActive.get();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#enable
   * (boolean)
   */
  @Override
  public final synchronized boolean enable( boolean enable, Context context )
  {
    if ( enable )
    {
      // do only start scanner if it is not already running
      if ( !isEnabled() )
      {
        setActive( start( context ) );
      }
    }
    else if ( isEnabled() )
    {
      // stop a running scanner if enable is == false
      setActive( !stop( context ) );
    }
    
    return enable == isEnabled();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#onDestroy
   * (android.content.Context)
   */
  @Override
  public void onDestroy( Context context )
  {
    setDevice( null, context );
    // enable( false, context );
  }
}