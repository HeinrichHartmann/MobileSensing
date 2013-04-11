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

import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.TimeInformation;
import de.unikassel.android.sdcframework.util.TimeProvider;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Base class for an {@link android.hardware.Sensor Android Sensor} type. <br/>
 * <br/>
 * Does provide the sensor listener and the logic for listener registration
 * depending on the scanner running state. Any extending class has just to
 * implement the abstract handler methods for sensor changes and the methods
 * from the {@link SampleProvidingSensorDevice} interface.
 * 
 * @see AccelerometerDevice
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractAndroidSensorDevice
    extends ScannerStateAwareSensorDevice
    implements SampleProvidingSensorDevice
{
  /**
   * The sensor listener
   */
  private SensorEventListener sensorListener;
  
  /**
   * The corresponding Android sensor
   */
  private final Sensor sensor;
  
  /**
   * The sensor delay
   */
  private final int sensorDelay;
  
  /**
   * 
   * Constructor
   * 
   * @param deviceId
   *          the device identifier
   * @param androidSensorType
   *          the Android sensor type
   * @param context
   *          the application context
   * @param sensorDelay
   *          the sensor delay
   * @throws InvalidParameterException
   *           if sensor type is unavailable or unknown
   */
  public AbstractAndroidSensorDevice( SensorDeviceIdentifier deviceId,
      int androidSensorType, Context context, int sensorDelay )
      throws InvalidParameterException
  {
    super( deviceId );
    this.sensorDelay = sensorDelay;
    SensorManager sensorManager =
        (SensorManager) context.getSystemService( Context.SENSOR_SERVICE );
    
    this.sensor = sensorManager.getDefaultSensor( androidSensorType );
    if ( this.getSensor() == null || androidSensorType == Sensor.TYPE_ALL )
      throw new InvalidParameterException( "invalid sensor type or unavailable" );
  }
  
  /**
   * Setter for the sensor event listener
   * 
   * @param sensorListener
   *          the sensor event listener
   */
  private final void setListener( SensorEventListener sensorListener )
  {
    this.sensorListener = sensorListener;
  }
  
  /**
   * Getter for the sensor listener
   * 
   * @return the sensor listener
   */
  public final SensorEventListener getListener()
  {
    if ( sensorListener == null )
    {
      setListener( new SensorEventListener()
      {
        /*
         * (non-Javadoc)
         * 
         * @see
         * android.hardware.SensorEventListener#onSensorChanged(android.hardware
         * .SensorEvent)
         */
        @Override
        public void onSensorChanged( SensorEvent event )
        {
          try
          {
            if ( getSensor().equals( event.sensor ) )
            {
              doHandleSensorChanged( event );
            }
          }
          catch ( Exception e )
          {
            Logger.getInstance().error( this, "Exception in onSensorChanged" );
          }
        }
        
        /*
         * (non-Javadoc)
         * 
         * @see
         * android.hardware.SensorEventListener#onAccuracyChanged(android.hardware
         * .Sensor, int)
         */
        @Override
        public void onAccuracyChanged( Sensor changedSensor, int accuracy )
        {
          try
          {
            if ( getSensor().equals( changedSensor ) )
            {
              doHandleSensorAccuracyChanged( changedSensor, accuracy );
            }
          }
          catch ( Exception e )
          {
            Logger.getInstance().error( this, "Exception in onAccuracyChanged" );
          }
          
        }
      } );
    }
    return sensorListener;
  }
  
  /**
   * Getter for the sensor
   * 
   * @return the sensor
   */
  public final Sensor getSensor()
  {
    return sensor;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.facade.SensorDevice#
   * isDeviceInSystemEnabled(android.content.Context)
   */
  @Override
  public final boolean isDeviceInSystemEnabled( Context context )
  {
    return sensor != null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * doSignalDeviceNotEnabledInSystem(android.content.Context)
   */
  @Override
  protected final void doSignalDeviceNotEnabledInSystem(
      Context applicationContext )
  {
    // nothing to do as sensor is either available or not
  }
  
  /**
   * Does register the sensor listener for the device type
   * 
   * @param context
   *          the application context
   */
  private final void registerListener( Context context )
  {
    SensorManager sensorManager =
        (SensorManager) context.getSystemService( Context.SENSOR_SERVICE );
    
    sensorManager.registerListener( getListener(), getSensor(), sensorDelay );
  }
  
  /**
   * Does unregister the sensor listener for the device type
   * 
   * @param context
   *          the application context
   */
  private final void unregisterListener( Context context )
  {
    SensorManager sensorManager =
        (SensorManager) context.getSystemService( Context.SENSOR_SERVICE );
    
    sensorManager.unregisterListener( getListener() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.ScannerStateAwareSensorDevice
   * #onScannerRunningStateChange(boolean)
   */
  @Override
  protected final void
      onScannerRunningStateChange( boolean isRunning, Context context )
  {
    // depending on scanner running state add listener or remove it
    if ( isRunning )
      registerListener( context );
    else
      unregisterListener( context );
  }
  
  /**
   * Handler for the sensor changed event ( with incoming updated sensor data )
   * 
   * @param event
   *          the sensor event to handle
   */
  protected abstract void doHandleSensorChanged( SensorEvent event );
  
  /**
   * Handler for the sensor accuracy changed event
   * 
   * @param sensor
   *          the sensor
   * @param accuracy
   *          the accuracy
   */
  protected void doHandleSensorAccuracyChanged( Sensor sensor, int accuracy )
  {
    // for the moment this seems not of interest
    // can be overridden of extending types
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice
   * #getSample()
   */
  @Override
  public final synchronized Sample getSample()
  {
    TimeInformation ti = TimeProvider.getInstance().getAccurateTimeInformation();
    Sample sample =
        SampleFactory.getInstance().createSample( ti, getDeviceIdentifier(),
            getConfiguration().getSamplePriority().ordinal(),
            getCurrentSampleData().doClone() );
    return sample;
  }
  
  /**
   * Getter for the current sample data
   * 
   * @return the current sample data
   */
  protected abstract SampleData getCurrentSampleData();
}
