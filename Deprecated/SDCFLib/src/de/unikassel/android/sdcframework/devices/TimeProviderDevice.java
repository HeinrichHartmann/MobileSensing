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
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.TimeProviderSampleData;
import de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.TimeInformation;
import de.unikassel.android.sdcframework.util.TimeProvider;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import de.unikassel.android.sdcframework.util.facade.TimeProviderEvent;

/**
 * Implementation of the the time provider synchronization state observing
 * sensor device.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class TimeProviderDevice extends VirtualSensorDevice
    implements SampleProvidingSensorDevice, EventObserver< TimeProviderEvent >
{
  /**
   * The actual sample of the device, updated whenever the handler for sensor
   * changes is called
   */
  private final TimeProviderSampleData currentSampleData;
  
  /**
   * The event time stamp and sync state.
   */
  private TimeInformation timeInfo;
  
  /**
   * Constructor
   */
  public TimeProviderDevice()
  {
    super( SensorDeviceIdentifier.TimeSyncStateChanges );
    
    this.currentSampleData = new TimeProviderSampleData();
    currentSampleData.setSynced( false );
    timeInfo = null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * enableDeviceScanning(boolean, android.content.Context)
   */
  @Override
  public final boolean enableDeviceScanning( boolean enabled, Context context )
  {
    boolean wasEnabled = getScanner().isEnabled();
    boolean result = super.enableDeviceScanning( enabled, context );
    boolean isEnabled = getScanner().isEnabled();
    
    if ( wasEnabled != isEnabled )
    {
      if ( isEnabled )
      {
        TimeProvider.getInstance().registerEventObserver( this );
      }
      else
      {
        TimeProvider.getInstance().unregisterEventObserver( this );
      }
    }
    return result;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice
   * #getSample()
   */
  @Override
  public synchronized Sample getSample()
  {
    Sample sample =
        SampleFactory.getInstance().createSample( timeInfo,
            getDeviceIdentifier(),
            getConfiguration().getSamplePriority().ordinal(),
            currentSampleData.doClone() );
    return sample;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice
   * #hasSample()
   */
  @Override
  public boolean hasSample()
  {
    return timeInfo != null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de.
   * unikassel.android.sdcframework.util.facade.ObservableEventSource,
   * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public synchronized void onEvent(
      ObservableEventSource< ? extends TimeProviderEvent > eventSource,
      TimeProviderEvent observedEvent )
  {
    timeInfo = new TimeInformation( observedEvent.getTimeStamp(), observedEvent.isSynced() );
    currentSampleData.setSynced( observedEvent.isSynced() );
    
    if ( getScanner() instanceof PassiveSampleTakingDeviceScanner )
    {
      ( (PassiveSampleTakingDeviceScanner) getScanner() ).takeSample();
      Logger.getInstance().debug( this, "TP Event: "+ observedEvent.toString() + "\n"+ getSample().toString() );
      timeInfo = null;
    }
  }
}
