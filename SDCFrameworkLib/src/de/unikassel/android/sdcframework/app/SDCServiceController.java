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

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Environment;
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
import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.provider.AudioProviderData;
import de.unikassel.android.sdcframework.provider.TwitterProviderData;
import de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder;
import de.unikassel.android.sdcframework.service.ServiceRunningStateListener;
import de.unikassel.android.sdcframework.service.ServiceUtils;
import de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.ServiceConnectionEventReceiver;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.LogEvent;
import de.unikassel.android.sdcframework.util.LogfileManager;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.facade.BroadcastableEvent;
import de.unikassel.android.sdcframework.util.facade.Encryption;
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
    extends Activity
    implements EventObserver< LogEvent >
{
  
  /**
   * Title for RSA key file selection.
   */
  private static final String TITLE_RSA_KEY_SELECTION = "Select RSA Public Key File";

  /**
   * Title for XML configuration key file selection.
   */
  private static final String TITLE_XML_CONFIG_FILE_SELECTION = "Select XML Configuration File";

  /**
   * File selection dialog identifier for pub key file selection.
   */
  private static final int SELECT_EXT_RSA_PUBKEY_FILE = 0;
  
  /**
   * File selection dialog identifier for external defaults file selection.
   */
  private static final int SELECT_EXT_DEFAULTS_FILE = 1;
  
  /**
   * The service class name
   */
  private final Class< ? > serviceClass = ISDCService.class;
  
  /**
   * The service action name
   */
  private final String action = SDCService.ACTION;
  
  /**
   * The event handler to handle log event messages
   */
  private final Handler eventHandler;
  
  /**
   * The preference manager
   */
  private final ApplicationPreferenceManager prefManager;
  
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
   * The service running state listener
   */
  private final ServiceRunningStateListener serviceRunningStateListner;
 
  /**
   * the stop service button listener
   */
  private final OnClickListener broadcastListener;


  private SDCServiceConnectionHolder connectionHolder;

  private ISDCService sdcService;
  
  private static boolean toggle;
  
  /**
   * Constructor
   */
  public SDCServiceController()
  {
    super();
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
    
    this.prefManager = new ApplicationPreferenceManagerImpl();
    
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
        ServiceUtils.startService( getApplicationContext(), serviceClass );
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
        ServiceUtils.stopService( SDCServiceController.this, serviceClass );
      }
    };
    
        
    this.serviceRunningStateListner = new ServiceRunningStateListener( action )
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * de.unikassel.android.sdcframework.service.ServiceRunningStateListener
       * #serviceStateChanged(boolean)
       */
      @Override
      protected void serviceStateChanged( boolean isRunning )
      {
        updateButtons( isRunning );
        if ( isRunning == false )
        {
          Logger.getInstance().unregisterEventObserver(
              SDCServiceController.this );
        }
      }
    };
    
    this.textColorMap = new SparseIntArray();
    
    
    ServiceConnectionEventReceiver reciever = new ServiceConnectionEventReceiver(){

      @Override
      public void onConnectionEstablished( ISDCService newSdcService )
      {
        Logger.getInstance().info(this, "Connection established");
        sdcService = newSdcService;
      }

      @Override
      public void onAboutToDisconnect( ISDCService sdcService )
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onConnectionLost()
      {
        // TODO Auto-generated method stub
        {
        }
      }

      @Override
      public void onBindingFailed()
      {
        // TODO Auto-generated method stub
        
      }

      @Override
      public void onServiceUnavailable()
      {
        // TODO Auto-generated method stub
        
      }
      
    };

    this.connectionHolder = new SDCServiceConnectionHolder( reciever , serviceClass );

    this.broadcastListener = new OnClickListener()
    { 
      @Override
      public void onClick( View v )
      {
          Logger.getInstance().info( this , "Set Broadcasting " + toggle );

          if ( sdcService != null){
            try
            {
              sdcService.doEnableSampleBroadCasting( toggle );
              toggle = ! toggle; 
            }
            catch ( RemoteException e )
            {
              Logger.getInstance().info( this , "Broadcasting failed" );
            }
          } else {
            Logger.getInstance().info( this , "sdcService is Null" );
          }
          
          
      }
    };


    
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
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
    
    button = (Button) findViewById( R.id.broadcast_button );
    button.setOnClickListener( broadcastListener );
    
    connectionHolder.onCreate( this );
    
    // configure log view
    getLogView();
  }
  
  /**
   * Does change Button states depending on service running state
   * 
   * @param serviceIsRunning
   *          flag if the service is Running
   */
  private void updateButtons( boolean serviceIsRunning )
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
    MenuInflater inflater = getMenuInflater();
    inflater.inflate( R.menu.optionmenu, menu );
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
            serviceClass );
    MenuItem menuItem = menu.findItem( R.id.clearDB );
    if( menuItem != null ) menuItem.setEnabled( !isServiceRunning );
    menuItem = menu.findItem( R.id.externalConfiguration );
    if( menuItem != null ) menuItem.setEnabled( !isServiceRunning );
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
    if ( itemId == R.id.preferences )
    {
      result = onPreferences();
    }
    else if ( itemId == R.id.clearlog )
    {
      result = onClearLog();
    }
    else if ( itemId == R.id.clearDB )
    {
      result = onClearDB();
      
    }
    else if ( itemId == R.id.loadDefaults )
    {
      result = onLoadDefaults();
    }
    else if ( itemId == R.id.loadPublicKey )
    {
      result = onLoadRSAPublicKey();
    }
    return result;
  }
  
  /**
   * Method to handle the selection of "load external defaults" in the option
   * menu
   * 
   * @return true if successful, false otherwise
   */
  private boolean onLoadRSAPublicKey()
  {
    Intent intent = new Intent( this, SDCFileBrowserActivity.class );
    intent.putExtra( SDCFileBrowserActivity.TITLE, TITLE_RSA_KEY_SELECTION );
    intent.putExtra( SDCFileBrowserActivity.STARTDIR,
        Environment.getExternalStorageDirectory().getAbsolutePath() );
    intent.putExtra( SDCFileBrowserActivity.PATTERN, "\\.*\\.key" );
    startActivityForResult( intent, SELECT_EXT_RSA_PUBKEY_FILE );
    return true;
  }
  
  /**
   * Method to handle the selection of "load external defaults" in the option
   * menu.
   * 
   * @return true if successful, false otherwise
   */
  private boolean onLoadDefaults()
  {
    Intent intent = new Intent( this, SDCFileBrowserActivity.class );
    intent.putExtra( SDCFileBrowserActivity.TITLE, TITLE_XML_CONFIG_FILE_SELECTION );
    intent.putExtra( SDCFileBrowserActivity.STARTDIR,
        Environment.getExternalStorageDirectory().getAbsolutePath() );
    intent.putExtra( SDCFileBrowserActivity.PATTERN, "\\.*\\.xml" );
    startActivityForResult( intent, SELECT_EXT_DEFAULTS_FILE );
    return true;
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
   * Method to handle for selection of "preferences" in the option menu
   * 
   * @return true if successful, false otherwise
   */
  private boolean onPreferences()
  {
    Intent intent = new Intent( this, SDCPreferenceActivity.class );
    startActivity( intent );
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
   * @see android.app.Activity#onResume()
   */
  @Override
  protected void onResume()
  {
    // register ourself as observable of the broadcast logListener
    Logger.getInstance().registerEventObserver( this );
    
    IntentFilter filter = new IntentFilter();
    filter.addAction( SDCService.ACTION );
    getApplicationContext().registerReceiver( serviceRunningStateListner,
        filter );
    
    // update button states
    boolean serviceIsRunning =
        ServiceUtils.isServiceRunning( getApplicationContext(),
            serviceClass );
    
    updateButtons( serviceIsRunning );
    super.onResume();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause()
  {
    getApplicationContext().unregisterReceiver( serviceRunningStateListner );
    Logger.getInstance().unregisterEventObserver( this );
    
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
    // free field instances
    prefManager.onDestroy();
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
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onActivityResult(int, int,
   * android.content.Intent)
   */
  @Override
  protected void
      onActivityResult( int requestCode, int resultCode, Intent data )
  {
    // Make sure the request was successful
    if ( resultCode == RESULT_OK )
    {
      switch ( requestCode )
      {
        case SELECT_EXT_RSA_PUBKEY_FILE:
        {
          String srcFile = data.getStringExtra( SDCFileBrowserActivity.FILE );
          String destFile = getFilesDir().getPath() + File.separatorChar
              + Encryption.PUBLIC_KEY_FILE;
          FileUtils.copy( srcFile, destFile );
          break;
        }
        case SELECT_EXT_DEFAULTS_FILE:
        {
          String srcFile = data.getStringExtra( SDCFileBrowserActivity.FILE );
          String destFile = getFilesDir().getPath() + File.separatorChar
              + getText( R.string.sdc_config_file_name ).toString();
          if ( FileUtils.copy( srcFile, destFile ) )
          {
            prefManager.resetToDefaults( this.getApplicationContext() );
          }
          break;
        }
      }
    }
  }
  
}
