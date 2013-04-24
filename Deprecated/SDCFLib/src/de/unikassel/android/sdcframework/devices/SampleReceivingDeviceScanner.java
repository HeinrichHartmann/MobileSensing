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

import java.util.concurrent.atomic.AtomicBoolean;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import de.unikassel.android.sdcframework.util.AbstractWorkerThread;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Abstract base class for scanner types, which do receive sensor data from
 * system broadcasts after the explicit initiation of a system scan. <br/>
 * <br/>
 * Such a scanner does run a thread to trigger device scans in the configured
 * frequency and it is using a broadcast receiver to receive the scan results. <br/>
 * Extending classes have to define the related Intent and to implement the
 * {@linkplain #doHandleScanResults(Intent) handler} for received samples, as
 * well as the {@linkplain #doStartDeviceScan() start} and
 * {@linkplain #doStopDeviceScan() stop} methods for the system scan.
 * 
 * @see WifiDeviceScanner
 * @see BluetoothDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
public abstract class SampleReceivingDeviceScanner extends
    AbstractSensorDeviceScanner
{
  /**
   * Inner class for a device polling worker thread
   * 
   * @author Katy Hilgenberg
   * 
   */
  private final class ScanWorkerThread extends AbstractWorkerThread
  {
    /**
     * Constructor
     */
    public ScanWorkerThread()
    {
      super();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * de.unikassel.android.sdcframework.util.AbstractWorkerThread#doCleanUp()
     */
    @Override
    protected final void doCleanUp()
    {}
    
    /*
     * (non-Javadoc)
     * 
     * @see de.unikassel.android.sdcframework.util.AbstractWorkerThread#doWork()
     */
    @Override
    protected final void doWork()
    {
      if ( isEnabled() )
      {
        try
        {
          if ( isLastScanFinished() )
          {
            if ( doStartDeviceScan() )
            {
              setLastScanFinished( false );
              int frequency =
                  getDevice().getConfiguration().getFrequency();
              sleep( frequency );
            }
          }
        }
        catch ( InterruptedException e )
        {}
      }
    }
  }
  
  /**
   * The broadcast receiver for the device scan results
   */
  protected final BroadcastReceiver receiver;
  
  /**
   * the worker thread
   */
  protected final ScanWorkerThread worker;
  
  /**
   * Flag indicating if last scan has finished ( minimal scan frequency is
   * always limited by device speed )
   */
  protected AtomicBoolean lastScanFinished;
  
  /**
   * Constructor
   */
  public SampleReceivingDeviceScanner()
  {
    super();
    this.lastScanFinished = new AtomicBoolean( false );
    this.worker = new ScanWorkerThread();
    this.receiver = new BroadcastReceiver()
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * android.content.BroadcastReceiver#onReceive(android.content.Context,
       * android.content.Intent)
       */
      @Override
      public void onReceive( Context context, Intent intent )
      {
        // as long as we do have an active scan running do handle results
        if ( !isLastScanFinished() )
        {
          try
          {
            doHandleScanResults( intent );
          }
          catch ( Exception e )
          {
            Logger.getInstance().error( SampleReceivingDeviceScanner.this,
                "Exception in doHandleScanResults (BroadcastReceiver): " + e.getMessage() );
          }
        }
      }
    };
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#start
   * (android.content.Context)
   */
  @Override
  public final boolean start( Context context )
  {
    setLastScanFinished( true );
    
    // register broadcast receiver for device scan results
    registerReceiver( context );
    
    // start worker thread
    worker.startWork();
    return worker.isWorking();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SensorDeviceScanner#stop
   * (android.content.Context)
   */
  @Override
  public final boolean stop( Context context )
  {
    if ( !isLastScanFinished() )
    {
      doStopDeviceScan();
    }
    setLastScanFinished( false );
    
    // stop working cycle of thread
    worker.interrupt();
    worker.stopWork();
    
    // unregister broadcast receiver for the device scan results
    unregisterReceiver( context );
    
    return true;
  }
  
  /**
   * Does register the broadcast receiver for device scan results
   * 
   * @param context
   *          the application context
   */
  private final void registerReceiver( Context context )
  {
    IntentFilter intentFilter = getIntentFilter();
    context.registerReceiver( receiver, intentFilter );
    
  }
  
  /**
   * Does unregister the broadcast receiver for device scan results
   * 
   * @param context
   *          the application context
   */
  private final void unregisterReceiver( Context context )
  {
    context.unregisterReceiver( receiver );
  }
  
  /**
   * Getter for the lastScanFinished
   * 
   * @return the lastScanFinished
   */
  public final boolean isLastScanFinished()
  {
    return lastScanFinished.get();
  }
  
  /**
   * Setter for the lastScanFinished
   * 
   * @param lastScanFinished
   *          the lastScanFinished to set
   */
  public final void setLastScanFinished( boolean lastScanFinished )
  {
    this.lastScanFinished.set( lastScanFinished );
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
    // finally terminate our worker thread
    worker.doTerminate();
    super.onDestroy( context );
  }
  
  /**
   * Getter for intent filter used by the broadcast receiver
   * 
   * @return the intent filter used by the broadcast receiver
   */
  protected abstract IntentFilter getIntentFilter();
  
  /**
   * Handler for the device scan results
   * 
   * @param intent
   *          the broadcast intent to handle
   */
  protected abstract void doHandleScanResults( Intent intent );
  
  /**
   * Method to start a device scan
   * 
   * @return true if successful, false otherwise
   */
  protected abstract boolean doStartDeviceScan();
  
  /**
   * Method to stop a device scan which is called if scanner is stopped while a
   * scan is ongoing
   */
  protected abstract void doStopDeviceScan();
  
}