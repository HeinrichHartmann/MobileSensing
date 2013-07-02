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
package de.unikassel.android.sdcframework.app;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.provider.AudioProviderData;
import de.unikassel.android.sdcframework.provider.TwitterProviderData;
import de.unikassel.android.sdcframework.service.ServiceUtils;
import de.unikassel.android.sdcframework.util.LogEvent;
import de.unikassel.android.sdcframework.util.LogfileManager;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.facade.BroadcastableEvent;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;

/**
 * The main controller activity for the sensor data collection service. It does
 * provide
 * <ul>
 * <li>a view to start and stop the {@linkplain SDCServiceImpl},</li>
 * <li>an option menu to start the {@linkplain SDCPreferenceActivity preference
 * activity } for configuration,</li>
 * <li>and a text view to display {@linkplain LogEvent log event messages}.</li>
 * </ul>
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "Registered" )
public final class SDCServiceController
    extends AbstractServiceControlActivity
    implements EventObserver< LogEvent >
{
  /**
   * The event handler to handle log event messages
   */
  private final Handler eventHandler;
  
  /**
   * the start service button listener
   */
  private final OnClickListener startListener;
  
  /**
   * the stop service button listener
   */
  private final OnClickListener stopListener;
  
  /**
   * The text color map
   */
  private final SparseIntArray textColorMap;
  
  /**
   * The text view for logging
   */
  private TextView logView;
  
  /**
   * Reference to a running service when bound.
   */
  private ISDCService service;
  
  /**
   * The service connection
   */
  private final ServiceConnection serviceConnection;
  
  /**
   * Constructor
   */
  public SDCServiceController()
  {
    super( ISDCService.class );
    
    this.serviceConnection = new ServiceConnection()
    {
      
      /*
       * (non-Javadoc)
       * 
       * @see
       * android.content.ServiceConnection#onServiceDisconnected(android.content
       * .ComponentName)
       */
      @Override
      public void onServiceDisconnected( ComponentName name )
      {
        setService( null );
      }
      
      /*
       * (non-Javadoc)
       * 
       * @see
       * android.content.ServiceConnection#onServiceConnected(android.content
       * .ComponentName, android.os.IBinder)
       */
      @Override
      public void onServiceConnected( ComponentName name, IBinder service )
      {
        setService( ISDCService.Stub.asInterface( service ) );
      }
    };
    
    eventHandler = new Handler()
    {
      public void handleMessage( Message msg )
      {
        try
        {
          if ( msg.obj instanceof BroadcastableEvent )
          {
            LogEvent event = (LogEvent) msg.obj;
            handleLogEvent( event );
          }
        }
        catch ( Exception e )
        {
          Logger.getInstance().error( SDCServiceController.this,
              "Exception in handleMessage" );
          e.printStackTrace();
        }
      }
    };
    
    this.startListener = new OnClickListener()
    {
      /*
       * (non-Javadoc)
       * 
       * @see android.view.View.OnClickListener#onClick(android.view.View)
       */
      @Override
      public void onClick( View v )
      {
        // start the sensor data collection service
        Logger.getInstance().registerEventObserver( SDCServiceController.this );
        ServiceUtils.startService( getApplicationContext(), getServiceClass() );
      }
    };
    
    this.stopListener = new OnClickListener()
    {
      /*
       * (non-Javadoc)
       * 
       * @see android.view.View.OnClickListener#onClick(android.view.View)
       */
      @Override
      public void onClick( View v )
      {
        // stop the running service ( can fail if other activities are still
        // bounded to it )
        if ( getService() != null )
        {
          setService( null );
          unbindService( serviceConnection );
        }
        ServiceUtils.stopService( SDCServiceController.this, getServiceClass() );
      }
    };
    
    this.textColorMap = new SparseIntArray();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  /**
   * Getter for the service
   * 
   * @return the service
   */
  public ISDCService getService()
  {
    return service;
  }
  
  /**
   * Setter for the service
   * 
   * @param service
   *          the service to set
   */
  public void setService( ISDCService service )
  {
    this.service = service;
  }
  
  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    setContentView( R.layout.sdc_service_controller );
    
    // fill text color map with resource data
    Resources res = getResources();
    textColorMap.put( LogLevel.ERROR.ordinal(),
        res.getColor( R.color.error_color ) );
    textColorMap.put( LogLevel.INFO.ordinal(),
        res.getColor( R.color.info_color ) );
    textColorMap.put( LogLevel.WARNING.ordinal(),
        res.getColor( R.color.warning_color ) );
    textColorMap.put( LogLevel.DEBUG.ordinal(),
        res.getColor( R.color.debug_color ) );
    
    // add button listener
    Button button = (Button) findViewById( R.id.start_button );
    button.setOnClickListener( startListener );
    button = (Button) findViewById( R.id.stop_button );
    button.setOnClickListener( stopListener );
    
    // configure log view
    getLogView();
  }
  
  @Override
  protected void onResume()
  {
    // register ourself as observable of the broadcast logListener
    Logger.getInstance().registerEventObserver( this );
    
    // update button states
    boolean serviceIsRunning =
        ServiceUtils.isServiceRunning( getApplicationContext(),
            getServiceClass() );
    
    super.onResume();
    
    updateButtons( serviceIsRunning );
  }
  
  @Override
  protected void onPause()
  {
    Logger.getInstance().unregisterEventObserver( this );
    if ( getService() != null )
    {
      setService( null );
      unbindService( serviceConnection );
    }
    super.onPause();
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
    updateButtons( isRunning );
    if ( isRunning == false )
    {
      Logger.getInstance().unregisterEventObserver( this );
    }
    else if ( getService() == null )
    {
      Intent intent = new Intent( getServiceClass().getName() );
      bindService( intent, serviceConnection, 0 );
    }
  }
  
  /**
   * Does change Button states depending on service running state
   * 
   * @param serviceIsRunning
   *          flag if the service is Running
   */
  void updateButtons( boolean serviceIsRunning )
  {
    Button button = (Button) findViewById( R.id.start_button );
    button.setEnabled( !serviceIsRunning );
    button = (Button) findViewById( R.id.stop_button );
    button.setEnabled( serviceIsRunning );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
   */
  @Override
  public boolean onCreateOptionsMenu( Menu menu )
  {
    super.onCreateOptionsMenu( menu );
    
    MenuInflater inflater = getMenuInflater();
    inflater.inflate( R.menu.extended_optionmenu, menu );
    return true;
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
    MenuItem menuItem = menu.findItem( R.id.clearDB );
    if ( menuItem != null )
      menuItem.setEnabled( !isServiceRunning );
    menuItem = menu.findItem( R.id.triggerTransfer );
    if ( menuItem != null )
      menuItem.setEnabled( isServiceRunning );
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
    boolean result = super.onOptionsItemSelected( item );
    int itemId = item.getItemId();
    
    if ( itemId == R.id.clearlog )
    {
      result = onClearLog();
    }
    else if ( itemId == R.id.triggerTransfer )
    {
      result = onTriggerTransfer();
      
    }
    else if ( itemId == R.id.clearDB )
    {
      result = onClearDB();
      
    }
    return result;
  }
  
  /**
   * Method to trigger an archive transfer manually
   * 
   * @return true if successful, false otherwise
   */
  private boolean onTriggerTransfer()
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
  
  /**
   * Method to handle the selection of "clear database" in the option menu
   * 
   * @return true if successful, false otherwise
   */
  private boolean onClearDB()
  {
    ContentResolver contentResolver =
        getApplicationContext().getContentResolver();
    contentResolver.delete( TwitterProviderData.getInstance().getContentUri(),
        null, null );
    contentResolver.delete( AudioProviderData.getInstance().getContentUri(),
        null, null );
    
    String dbName =
        getText( R.string.sdc_database_name ).toString();
    
    return deleteDatabase( dbName );
  }
  
  /**
   * Method to handle selection of "clear log" in the option menu
   * 
   * @return true if successful, false otherwise
   */
  private boolean onClearLog()
  {
    getLogView().setText( "" );
    LogfileManager.clearAllLogs();
    return true;
  }
  
  /**
   * Getter for the text view
   * 
   * @return the text view for logging
   */
  private TextView getLogView()
  {
    if ( logView == null )
    {
      setLogView( (TextView) findViewById( R.id.sdc_logview ) );
      logView.setSingleLine( false );
      logView.setMovementMethod( new ScrollingMovementMethod() );
    }
    return logView;
  }
  
  /**
   * Setter for the logView
   * 
   * @param logView
   *          the logView to set
   */
  private void setLogView( TextView logView )
  {
    this.logView = logView;
  }
  
  /**
   * Method to handle for log events
   * 
   * @param logEvent
   *          the log Event
   */
  private void handleLogEvent( LogEvent logEvent )
  {
    TextView logView = getLogView();
    
    // determine color
    LogLevel logLevel = logEvent.getLogLevel();
    int color = textColorMap.get( logLevel.ordinal() );
    
    SpannableString text = SpannableString.valueOf( logEvent.toString() );
    text.setSpan( new ForegroundColorSpan( color ), 0, text.length(),
        Spannable.SPAN_INCLUSIVE_EXCLUSIVE );
    
    logView.append( text );
    logView.append( "\n" );
    
    // scroll to end
    logView.setSelected( true );
    Spannable textDisplayed = (Spannable) logView.getText();
    Selection.setSelection( textDisplayed, textDisplayed.length() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onDestroy()
   */
  @Override
  protected void onDestroy()
  {
    // free field instances
    setLogView( null );
    
    super.onDestroy();
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
  public void onEvent( ObservableEventSource< ? extends LogEvent > eventSource,
      LogEvent observedEvent )
  {
    Message msg = Message.obtain();
    msg.obj = observedEvent;
    msg.setTarget( eventHandler );
    
    msg.sendToTarget();
  }
}
