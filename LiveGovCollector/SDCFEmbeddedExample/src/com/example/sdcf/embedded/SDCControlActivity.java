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
package com.example.sdcf.embedded;

import java.util.concurrent.atomic.AtomicBoolean;

import com.example.sdcf.embedded.R;
import de.unikassel.android.sdcframework.app.AbstractServiceControlActivity;
import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.provider.TagProviderData;
import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;
import de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder;
import de.unikassel.android.sdcframework.service.ServiceUtils;
import de.unikassel.android.sdcframework.util.TimeProvider;
import android.os.Bundle;
import android.os.RemoteException;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * Main Activity of the embedded SDCF application example.
 * It does replace the internal SDCF controller activity by extending the type
 * AbstractServiceControlActivity.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class SDCControlActivity
    extends AbstractServiceControlActivity
    implements SDCServiceConnectionHolder.ServiceConnectionEventReceiver
{    
  /**
   * The SDC service connection holder
   */
  private final SDCServiceConnectionHolder connectionHolder;
  
  /**
   * the start/stop service button.
   */
  private ToggleButton toggleServiceStateBtn;
  
  /**
   * the start/stop recording button.
   */
  private ToggleButton toggleRecordingStateBtn;
  
  /**
   * annotation text field.
   */
  private EditText editAnnotation;
  
  private Button buttonAnnotate;
  private EditText editAnnotation2;
  private Spinner spinnerAnnotation;
  
  /**
   * Recording state flag.
   */
  private final AtomicBoolean isRecording;
  
  /**
   * reference to a running service when bound.
   */
  private ISDCService service;
  
  /**
   * Constructor
   */
  public SDCControlActivity()
  {
    super( ActivityConstants.serviceClass );
    this.connectionHolder =
        new SDCServiceConnectionHolder( this, ActivityConstants.serviceClass );
    this.isRecording = new AtomicBoolean( false );
  }
  
  protected synchronized void setService( ISDCService service )
  {
    this.service = service;
  }
  
  protected synchronized ISDCService getService()
  {
    return service;
  }
  
  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.activity_main );
    
    toggleServiceStateBtn =
        (ToggleButton) findViewById( R.id.startStopServiceButton );
    
    toggleRecordingStateBtn =
        (ToggleButton) findViewById( R.id.toggleRecordingButton );
    toggleRecordingStateBtn.setOnClickListener( new OnClickListener()
    {
      @Override
      public void onClick( View v )
      {
        onToggleRecordingState();
      }
    } );
    
    editAnnotation = (EditText) findViewById( R.id.editAnnotation );
    editAnnotation2 = (EditText) findViewById( R.id.EditText01 );
    editAnnotation2.setEnabled(false);
    buttonAnnotate = (Button) findViewById ( R.id.button1 );
    buttonAnnotate.setEnabled(false);
    spinnerAnnotation = (Spinner) findViewById( R.id.spinner1 );
    
    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.movements_array, android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinnerAnnotation.setAdapter(adapter);
    
    toggleServiceStateBtn.setOnClickListener( new OnClickListener()
    {
      @Override
      public void onClick( View v )
      {
        if ( ServiceUtils.isServiceRunning( SDCControlActivity.this,
            ActivityConstants.serviceClass ) )
        {
          doStopService();
        }
        else
        {
          doStartService();
        }
      }
    } );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume()
  {
    super.onResume();
    
    toggleServiceStateBtn.setChecked( ServiceUtils.isServiceRunning(
        getApplicationContext(), ActivityConstants.serviceClass ) );
    
    if ( !ServiceUtils.isServiceAvailable( this,
        ActivityConstants.serviceClass ) )
    {
      AlertDialog alertDialog = new AlertDialog.Builder( this ).create();
      alertDialog.setTitle( "Important" );
      alertDialog.setMessage( "The SDCF Service is not installed!" );
      alertDialog.setButton( "OK", new DialogInterface.OnClickListener()
      {
        @Override
        public void onClick( DialogInterface dialog, int which )
        {
          finish();
        }
      } );
      alertDialog.setIcon( R.drawable.ic_launcher );
      alertDialog.show();
    }
    
    boolean isRunning = ServiceUtils.isServiceRunning( getApplicationContext(),
        ActivityConstants.serviceClass );
    if ( isRunning )
    {
      connectionHolder.onCreate( this );
    }
    toggleRecordingStateBtn.setEnabled( isRunning );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause()
  {
    super.onPause();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onDestroy()
   */
  @Override
  protected void onDestroy()
  {
    super.onDestroy();
    
    if ( ServiceUtils.isServiceRunning( getApplicationContext(),
        ActivityConstants.serviceClass ) )
    {
      connectionHolder.onDestroy( this );
    }
  }
  
  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate( R.menu.main, menu );
    return super.onCreateOptionsMenu( menu );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
   */
  @Override
  public boolean onPrepareOptionsMenu( Menu menu )
  {
    boolean isServiceRunning =
      ServiceUtils.isServiceRunning( getApplicationContext(),
          getServiceClass() );
  
    MenuItem menuItem = menu.findItem( R.id.action_disable_transfer );
    if ( menuItem != null )
    {
      menuItem.setEnabled( isServiceRunning );
    }
    menuItem = menu.findItem( R.id.action_force_transfer );
    if ( menuItem != null )
    {
      menuItem.setEnabled( isServiceRunning );
    }
    return super.onPrepareOptionsMenu( menu );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
   */
  @Override
  public boolean onOptionsItemSelected( MenuItem item )
  {
    int itemId = item.getItemId();
    if ( itemId == R.id.action_disable_transfer )
    {
      return enableSampleTransfer( false );
    }
    else if ( itemId == R.id.action_force_transfer )
    {
      return enableSampleTransfer( true ) && triggerSampleTransfer() ;
    }
    return super.onOptionsItemSelected( item );
  }
  
  /**
   * Method to start the service
   */
  protected void doStartService()
  {
    ServiceUtils.startService( getApplicationContext(),
        ActivityConstants.serviceClass.getName() );
    connectionHolder.onCreate( this );
  }
  
  /**
   * Method to stop the service
   */
  protected void doStopService()
  {
    connectionHolder.onDestroy( this );
    ServiceUtils.stopService( getApplicationContext(),
        ActivityConstants.serviceClass.getName() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.app.AbstractServiceControlActivity#
   * onServiceRunningStateChanged(boolean)
   */
  @Override
  protected void onServiceRunningStateChanged( boolean isRunning )
  {
    toggleServiceStateBtn.setChecked( isRunning );
    toggleRecordingStateBtn.setEnabled( isRunning );
  }
  
  /**
   * Handler for recording state button events.
   * 
   * @param isEnabled
   *          flag if recording is active or not
   */
  protected void onToggleRecordingState()
  {
    if ( toggleRecordingStateBtn.isChecked() )
    {
      // start recording for the given annotation
      String annotation = editAnnotation.getText().toString();
//      if ( annotation == null || annotation.length() == 0 )
//      {
//        toggleRecordingStateBtn.setChecked( false );
//        Toast.makeText( this, "Missing annotation for recording!", Toast.LENGTH_LONG ).show();
//        return;
//      }
      
      if ( annotation != null && annotation.length() != 0 )
      {
          // store annotation text as tag sensor information
          sendAnnotationToSDCF( annotation );
      }
      
      annotation = spinnerAnnotation.getSelectedItem().toString();
      sendAnnotationToSDCF("Activity: " + annotation );
      
      // stop the transfer service and enable sampling
      enableSampleTransfer( false );
      enableSampling( true );

      isRecording.set( true );
      buttonAnnotate.setEnabled( true );
      editAnnotation.setEnabled( false );
      editAnnotation2.setEnabled( true );
      spinnerAnnotation.setEnabled( false );
      toggleServiceStateBtn.setEnabled( false );
    }
    else if ( isRecording.get() )
    {
      // stop recording
      isRecording.set( false );
      buttonAnnotate.setEnabled( false );
      editAnnotation.setEnabled( true );
      editAnnotation2.setEnabled( false );
      spinnerAnnotation.setEnabled( true );
      toggleServiceStateBtn.setEnabled( true );
      enableSampling( false );      
      // the next statement will reactivate sample transfer and forces a transmission of the stored samples.
      triggerSampleTransfer();
    }
  }
  
  public void onAnnotateClick(View view) {
	  String annotation = editAnnotation2.getText().toString();
      if ( annotation == null || annotation.length() == 0 )
      {
        Toast.makeText( this, "No annotation entered", Toast.LENGTH_LONG ).show();
        return;
      }
      sendAnnotationToSDCF( annotation );
      Toast.makeText( this, "Annotation added", Toast.LENGTH_LONG ).show();
  }
  
  /**
   * Method to send an annotation as tag sensor information to the SDC service.
   * 
   * @param tag
   *          the tag value to write
   */
  private void sendAnnotationToSDCF( String tag )
  {
    ContentValues values = new ContentValues();
    // IMPORTANT: The time stamp is internally overridden by the service
    // which does generate its own sample time stamp when removing tag data.
    // To guarantee a reliable time stamp, the service should run when
    // adding annotation information that samples will be processed in time.
    values.put( ContentProviderData.TIMESTAMP,
        Long.toString( TimeProvider.getInstance().getTimeStamp() ) );
    values.put( TagProviderData.TEXT, tag );
    getContentResolver().insert(
        TagProviderData.getInstance().getContentUri(), values );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.
   * ServiceConnectionEventReceiver
   * #onConnectionEstablished(de.unikassel.android.
   * sdcframework.app.facade.ISDCService)
   */
  @Override
  public void onConnectionEstablished( ISDCService sdcService )
  {
    setService( sdcService );
    // do initialization tasks when the binding is established
    enableSampling( false );
    enableSampleTransfer( false );
    enableSampleBroadcasts( true );
    enableSampleStorage( true );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.
   * ServiceConnectionEventReceiver
   * #onAboutToDisconnect(de.unikassel.android.sdcframework
   * .app.facade.ISDCService)
   */
  @Override
  public void onAboutToDisconnect( ISDCService sdcService )
  {
    // do cleanup tasks when then binding is gone
    toggleRecordingStateBtn.setChecked( false );
    enableSampleBroadcasts( false );
    setService( null );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.
   * ServiceConnectionEventReceiver#onConnectionLost()
   */
  @Override
  public void onConnectionLost()
  {}
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.
   * ServiceConnectionEventReceiver#onBindingFailed()
   */
  @Override
  public void onBindingFailed()
  {
    // should normally not happen if the service manifest configuration is valid
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.
   * ServiceConnectionEventReceiver#onServiceUnavailable()
   */
  @Override
  public void onServiceUnavailable()
  {
    // Ignore for applications with embedded SDCF service
  }
  
  /**
   * Method to enable/disable the sample transfer feature.
   * 
   * @param enabledState
   *          true to enable the feature, false to disable it
   */
  public boolean enableSampleTransfer( boolean enabledState )
  {
    try
    {
      getService().doEnableSampleTransfer( enabledState );
      return true;
    }
    catch ( RemoteException e )
    {}
    catch ( NullPointerException e )
    {}
    return false;
  }
  
  /**
   * Method to enable/disable the sample storage feature.
   * 
   * @param enabledState
   *          true to enable the feature, false to disable it
   */
  public boolean enableSampleStorage( boolean enabledState )
  {
    try
    {
      getService().doEnableSampleStorage( enabledState );
      return true;
    }
    catch ( RemoteException e )
    {}
    catch ( NullPointerException e )
    {}
    return false;
  }
  
  /**
   * Method to enable/disable the sampling feature.
   * 
   * @param enabledState
   *          true to enable the feature, false to disable it
   */
  public boolean enableSampling( boolean enabledState )
  {
    try
    {
      getService().doEnableSampling( enabledState );
      return true;
    }
    catch ( RemoteException e )
    {}
    catch ( NullPointerException e )
    {}
    return false;
  }
  
  /**
   * Method to enable/disable the service sample broadcast feature.
   * 
   * @param enabledState
   *          true to enable the feature, false to disable it
   */
  public boolean enableSampleBroadcasts( boolean enabledState )
  {
    try
    {
      getService().doEnableSampleBroadCasting( enabledState );
      return true;
    }
    catch ( RemoteException e )
    {}
    catch ( NullPointerException e )
    {}
    return false;
  }
  
  /**
   * Method to force the activation and transfer of samples
   */
  public boolean triggerSampleTransfer()
  {
    try
    {
      getService().doTriggerSampleTransfer();
      return true;
    }
    catch ( RemoteException e )
    {}
    catch ( NullPointerException e )
    {}
    return false;
  }
}
