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
import android.provider.Settings;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.devices.facade.SensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor;
import de.unikassel.android.sdcframework.preferences.SensorDeviceConfigurationImpl;
import de.unikassel.android.sdcframework.preferences.facade.SensorDeviceConfiguration;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Base class for any sensor device type.
 * 
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractSensorDevice implements SensorDevice
{
  /**
   * The sensor device deviceIdentifier
   */
  private SensorDeviceIdentifier deviceIdentifier;
  
  /**
   * The sensor device configuration
   */
  private SensorDeviceConfiguration configuration;
  
  /**
   * The scanner for the sensor device
   */
  private SensorDeviceScanner scanner;
  
  /**
   * Default constructor ( it's private with purpose )
   */
  @SuppressWarnings( "unused" )
  private AbstractSensorDevice()
  {
    this( SensorDeviceIdentifier.Unknown );
  }
  
  /**
   * Constructor for devices with an Android sub type
   * 
   * @param deviceId
   *          the device identifier
   */
  public AbstractSensorDevice( SensorDeviceIdentifier deviceId )
  {
    super();
    setDeviceIdentifier( deviceId );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDevice#accept(de
   * .unikassel.android.sdcframework.devices.facade.SensorDeviceVisitor)
   */
  @Override
  public final boolean accept( SensorDeviceVisitor visitor )
  {
    return visitor.visit( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
   * getDeviceIdentifier()
   */
  @Override
  public final SensorDeviceIdentifier getDeviceIdentifier()
  {
    return deviceIdentifier;
  }
  
  /**
   * Setter for the device identifier
   * 
   * @param deviceIdentifier
   *          the device identifier to set
   */
  protected final void setDeviceIdentifier(
      SensorDeviceIdentifier deviceIdentifier )
  {
    this.deviceIdentifier = deviceIdentifier;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
   * isDeviceScanningEnabled()
   */
  @Override
  public final boolean isDeviceScanningEnabled()
  {
    return getConfiguration().isEnabled();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
   * enableDeviceScanning(boolean)
   */
  @Override
  public boolean enableDeviceScanning( boolean enabled, Context context )
  {
    try
    {
      boolean isEnabledInSystem = isDeviceInSystemEnabled( context );
      
      boolean doEnable = enabled;
      // handle system state conflicts
      if ( !isEnabledInSystem && doEnable )
      {
        // device is not available thus we will assure the scanner get's
        // disabled
        // and the user is informed if necessary
        if ( !isAirplaneModeOn( context ) )
          doSignalDeviceNotEnabledInSystem( context );
        doEnable = false;
      }
      
      // change scanner enabled state depending on the enable request and
      // current
      // system state
      boolean success = false;
      SensorDeviceScanner scanner = getScanner();
      if ( scanner != null )
      {
        success = scanner.enable( doEnable, context );
        if ( !success )
        {
          String msg = context.getText( R.string.err_enable_device ).toString();
          Logger.getInstance().error( this, msg + deviceIdentifier.toString() );
        }
      }
      return success;
    }
    catch ( Exception e )
    {
      Logger.getInstance().error( this, "Exception in enableDeviceScanning" );
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Setter for the configuration
   * 
   * @param configuration
   *          the configuration to set
   */
  private final void setConfiguration( SensorDeviceConfiguration configuration )
  {
    this.configuration = configuration;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDevice#getConfiguration
   * ()
   */
  @Override
  public final SensorDeviceConfiguration getConfiguration()
  {
    if ( configuration == null )
    {
      setConfiguration( new SensorDeviceConfigurationImpl() );
    }
    return configuration;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDevice#setScanner
   * (de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner,
   * android.content.Context)
   */
  @Override
  public final void setScanner( SensorDeviceScanner scanner, Context context )
  {
    SensorDeviceScanner oldScanner = this.scanner;
    if ( oldScanner != scanner )
    {
      if ( oldScanner != null )
      {
        enableDeviceScanning( false, context );
        this.scanner = null;
        oldScanner.setDevice( null, context );
      }
      this.scanner = scanner;
      if ( this.scanner != null )
      {
        this.scanner.setDevice( this, context );
        enableDeviceScanning( isDeviceScanningEnabled(), context );
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDevice#getScanner ()
   */
  @Override
  public final SensorDeviceScanner getScanner()
  {
    return scanner;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
   * updateConfiguration(de.unikassel.android.sdcframework.devices.facade.
   * SensorDeviceConfiguration)
   */
  @Override
  public final void updateConfiguration(
      SensorDeviceConfiguration configuration,
      Context context )
  {
    SensorDeviceConfiguration config = getConfiguration();
    config.update( configuration );
    // signal update of configuration
    onConfigurationChanged();
    // call enable for current enabled state to adjust device state if necessary
    enableDeviceScanning( config.isEnabled(), context );
  }
  
  /**
   * Callback to signal configuration of device has changed
   */
  protected abstract void onConfigurationChanged();
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDevice#onCreate(
   * android.content.Context)
   */
  @Override
  public void onCreate( Context context )
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDevice#onDestroy
   * (android.content.Context)
   */
  @Override
  public void onDestroy( Context context )
  {
    SensorDeviceScanner scanner = getScanner();
    if ( scanner != null )
    {
      scanner.onDestroy( context );
      setScanner( null, context );
    }
  }
  
  /**
   * Getter for the state of the airplaine mode
   * 
   * @param applicationContext
   *          the application contex
   * @return true if enabled, false otherwise
   */
  public boolean isAirplaneModeOn( Context applicationContext )
  {
    
    int airplaneMode =
        Settings.System.getInt( applicationContext.getContentResolver(),
              Settings.System.AIRPLANE_MODE_ON, 0 );
    return airplaneMode != 0;
    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
   * doHandleDeviceDisabledBySystem(android.content.Context)
   */
  @Override
  public final void doHandleDeviceDisabledBySystem( Context context )
  {
    if ( context != null )
    {
      // disable scanner
      enableDeviceScanning( isDeviceScanningEnabled(), context );
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
   * doHandleDeviceEnabledBySystem(android.content.Context)
   */
  @Override
  public final void doHandleDeviceEnabledBySystem( Context context )
  {
    if ( context != null )
    {
      enableDeviceScanning( isDeviceScanningEnabled(), context );
    }
  }
  
  /**
   * Method to signal the user that the device is disabled in the system and
   * needed by this service. <br/>
   * This method is called from the handler
   * {@linkplain #doHandleDeviceDisabledBySystem} to react on system state
   * changes from enabled to disabled.<br/>
   * The implementation depends on the concrete device.
   * 
   * @param applicationContext
   *          the application context
   */
  protected abstract void doSignalDeviceNotEnabledInSystem(
      Context applicationContext );
  
}
