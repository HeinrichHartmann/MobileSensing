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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import android.content.Context;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.Toast;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.independent.GSMNeighborCell;
import de.unikassel.android.sdcframework.data.independent.GSMSampleData;
import de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.TimeInformation;
import de.unikassel.android.sdcframework.util.TimeProvider;

/**
 * Implementation of the GSM sensor device as active device providing sensor
 * data for the scanner.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class GSMDevice
    extends ScannerStateAwareSensorDevice
    implements SampleProvidingSensorDevice
{
  /**
   * The events to listen for in enabled state
   */
  private final static int eventsEnabled =
      PhoneStateListener.LISTEN_CELL_LOCATION |
          PhoneStateListener.LISTEN_SIGNAL_STRENGTHS |
          PhoneStateListener.LISTEN_SERVICE_STATE;
  
  /**
   * The events to listen for in enabled state
   */
  private final static int eventsDisabled =
      PhoneStateListener.LISTEN_SERVICE_STATE;
  
  /**
   * Private class implementing a phone state listener for GSM devices.
   * 
   * @author Katy Hilgenberg
   * 
   */
  private class GSMPhoneStateListener extends PhoneStateListener
  {
    /**
     * Constructor
     */
    public GSMPhoneStateListener()
    {
      super();
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.telephony.PhoneStateListener#onServiceStateChanged(android.telephony
     * .ServiceState)
     */
    @Override
    public final void onServiceStateChanged( ServiceState serviceState )
    {
      try
      {
        // handle state changes
        doReactOnStateChange( serviceState );
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this, "Exception in onServiceStateChanged" );
      }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.telephony.PhoneStateListener#onCellLocationChanged(android.telephony
     * .CellLocation)
     */
    @Override
    public final void onCellLocationChanged( CellLocation location )
    {
      try
      {
        if ( location instanceof GsmCellLocation )
        {
          // update stored cell location information
          GsmCellLocation cellLocation = (GsmCellLocation) location;
          int cellId = cellLocation.getCid();
          int locationAreaCode = cellLocation.getLac();
          updateCellLocation( cellId, locationAreaCode );
        }
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this, "Exception in onCellLocationChanged" );
      }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see
     * android.telephony.PhoneStateListener#onSignalStrengthsChanged(android
     * .telephony.SignalStrength)
     */
    @Override
    public final void onSignalStrengthsChanged( SignalStrength signalStrength )
    {
      try
      {
        if ( telephoyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM )
        {
          // update stored signal strength
          updateSignalStrength( signalStrength.getGsmSignalStrength() );
        }
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this,
            "Exception in onSignalStrengthsChanged" );
      }
    }
    
  }
  
  /**
   * The context
   */
  private Context context;
  
  /**
   * The last signaled service state
   */
  private final AtomicInteger lastServiceState;
  
  /**
   * The telephony manager reference
   */
  private final TelephonyManager telephoyManager;
  
  /**
   * The phone state listener to tack cell or signal strength changes
   */
  private final PhoneStateListener phoneStateListener;
  
  /**
   * The GSM sample holding the current device state
   */
  private final GSMSampleData currentSampleData;
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   */
  public GSMDevice( Context context )
  {
    super( SensorDeviceIdentifier.GSM );
    this.telephoyManager =
        (TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE );
    this.phoneStateListener = new GSMPhoneStateListener();
    this.lastServiceState = new AtomicInteger();
    this.currentSampleData = new GSMSampleData();
    setContext( context );
    setLastServiceState( ServiceState.STATE_IN_SERVICE );
  }
  
  /**
   * Getter for the last service state
   * 
   * @return the last service state
   */
  public final int getLastServiceState()
  {
    return lastServiceState.get();
  }
  
  /**
   * Setter for the last service state
   * 
   * @param lastServiceState
   *          the last service state to set
   */
  private synchronized final void setLastServiceState( int lastServiceState )
  {
    this.lastServiceState.set( lastServiceState );
  }
  
  /**
   * Setter for the context
   * 
   * @param context
   *          the context to set
   */
  private synchronized final void setContext( Context context )
  {
    this.context = context;
  }
  
  /**
   * Getter for the context
   * 
   * @return the context
   */
  private synchronized final Context getContext()
  {
    return context;
  }
  
  /**
   * Registration of listener for the given events
   * 
   * @return true if successful, false otherwise
   */
  private synchronized final boolean registerListener( int events )
  {
    if ( telephoyManager != null )
    {
      telephoyManager.listen( phoneStateListener, events );
      return true;
    }
    return false;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.devices.AbstractSensorDevice#
   * doSignalDeviceNotEnabledInSystem(android.content.Context)
   */
  @Override
  public final void
      doSignalDeviceNotEnabledInSystem( Context applicationContext )
  {
    String appName =
        applicationContext.getText( R.string.sdc_service_name ).toString();
    String message =
        applicationContext.getText( R.string.msg_gsm_unavailable ).toString();
    // signal to user that gsm is not available
    Toast.makeText( applicationContext, appName + ": " + message,
        Toast.LENGTH_LONG ).show();
    Logger.getInstance().warning( this,
        message + " (" + serviceStateToString( getLastServiceState() ) + ")" );
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
    int state = getLastServiceState();
    return state == ServiceState.STATE_IN_SERVICE ||
        state == ServiceState.STATE_EMERGENCY_ONLY;
  }
  
  /**
   * Updater for the current signal strength
   * 
   * @param signalStrength
   *          the current signal strengt
   */
  public synchronized final void updateSignalStrength( int signalStrength )
  {
    currentSampleData.setSignalStrength( signalStrength );
    
    updateNeighborCells();
  }
  
  /**
   * Updater for the current cell location
   * 
   * @param cellId
   *          the cell id
   * @param locationAreaCode
   *          the location area code
   */
  public synchronized final void updateCellLocation( int cellId,
      int locationAreaCode )
  {
    currentSampleData.setCellId( cellId );
    currentSampleData.setLocationAreaCode( locationAreaCode );
    
    updateNeighborCells();
  }
  
  /**
   * Method to update neighbor cell information
   */
  private final void updateNeighborCells()
  {
    // HINT: think about remove? seems rarely to work
    List< NeighboringCellInfo > listNeigborCells =
        telephoyManager.getNeighboringCellInfo();
    
    currentSampleData.getNeighbors().clear();
    if ( listNeigborCells != null )
    {
      for ( NeighboringCellInfo cellinfo : listNeigborCells )
      {
        int networkType = cellinfo.getNetworkType();
        
        // we do only sample GSM neighbors in a GSM sensor :)
        if ( networkType == TelephonyManager.NETWORK_TYPE_GPRS
            || networkType == TelephonyManager.NETWORK_TYPE_EDGE )
        {
          int cellId = cellinfo.getCid();
          int rssi = cellinfo.getRssi();
          if ( cellId != NeighboringCellInfo.UNKNOWN_CID &&
              rssi != NeighboringCellInfo.UNKNOWN_RSSI )
          {
            currentSampleData.getNeighbors().add(
                new GSMNeighborCell( cellId, rssi ) );
          }
        }
      }
    }
  }
  
  /**
   * Updater for the operator name
   * 
   * @param operatorName
   *          the operator name
   */
  public synchronized final void updateOperatorName( String operatorName )
  {
    currentSampleData.setOperator( operatorName );
  }
  
  /**
   * Handler for service state changes
   * 
   * @param serviceState
   *          the current service state
   */
  private final void doReactOnStateChange( ServiceState serviceState )
  {
    updateOperatorName( serviceState.getOperatorAlphaLong() );
    
    int state = serviceState.getState();
    
    // determine state change flags
    boolean oldStateWasEnabled = isDeviceInSystemEnabled( null );
    setLastServiceState( state );
    boolean newStateIsEnabled = isDeviceInSystemEnabled( null );
    
    if ( oldStateWasEnabled != newStateIsEnabled )
    {
      if ( newStateIsEnabled )
      {
        // device state changed to enabled
        doHandleDeviceEnabledBySystem( getContext() );
      }
      else
      {
        // device state changed to disabled
        doHandleDeviceDisabledBySystem( getContext() );
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.ScannerStateAwareSensorDevice
   * #onScannerRunningStateChange(boolean, android.content.Context)
   */
  @Override
  protected final void
      onScannerRunningStateChange( boolean isRunning, Context context )
  {
    if ( isRunning )
      registerListener( eventsEnabled );
    else
      registerListener( eventsDisabled );
  }
  
  /**
   * Does create a human readable string representation of the service state
   * 
   * @param serviceState
   *          the service state
   * @return the string representation of the service state
   */
  private final static String serviceStateToString( int serviceState )
  {
    String state = "unknown state";
    switch ( serviceState )
    {
      case ServiceState.STATE_POWER_OFF:
        state = "power off";
        break;
      
      case ServiceState.STATE_IN_SERVICE:
        state = "in service";
        break;
      
      case ServiceState.STATE_EMERGENCY_ONLY:
        state = "emergency only";
        break;
      
      case ServiceState.STATE_OUT_OF_SERVICE:
        state = "out of service";
        break;
    }
    return state;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.AbstractSensorDevice#onDestroy
   * (android.content.Context)
   */
  @Override
  public final void onDestroy( Context context )
  {
    registerListener( PhoneStateListener.LISTEN_NONE );
    setContext( null );
    super.onDestroy( context );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.facade.SampleProvidingSensorDevice
   * #getSample()
   */
  @Override
  public synchronized final Sample getSample()
  {
    TimeInformation ti = TimeProvider.getInstance().getAccurateTimeInformation();
    Sample sample =
        SampleFactory.getInstance().createSample( ti,
            getDeviceIdentifier(),
            getConfiguration().getSamplePriority().ordinal(),
            currentSampleData.doClone() );
    return sample;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.devices.AbstractSensorDevice#onCreate
   * (android.content.Context)
   */
  @Override
  public final void onCreate( Context context )
  {
    // do here that initialization stuff what would trigger an escape from
    // constructor
    if ( telephoyManager != null )
    {
      CellLocation cellLocation = telephoyManager.getCellLocation();
      if ( cellLocation instanceof GsmCellLocation )
      {
        GsmCellLocation gsmCellLocation = (GsmCellLocation) cellLocation;
        updateCellLocation( gsmCellLocation.getCid(), gsmCellLocation.getLac() );
        updateOperatorName( telephoyManager.getNetworkOperatorName() );
      }
    }
    registerListener( eventsEnabled );
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
    return currentSampleData.getCellId() > 0;
  }
}
