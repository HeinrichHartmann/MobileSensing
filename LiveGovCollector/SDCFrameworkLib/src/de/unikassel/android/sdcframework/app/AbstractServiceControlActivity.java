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
import java.io.FileInputStream;
import java.io.InputStream;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.data.SDCConfiguration;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.preferences.SDCConfigurationManager;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.service.ServiceRunningStateListener;
import de.unikassel.android.sdcframework.service.ServiceUtils;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.facade.Encryption;

/**
 * Base class for service control activities.
 * 
 * @author Katy Hilgenberg
 * 
 */
public abstract class AbstractServiceControlActivity
    extends Activity
{
  
  /**
   * The service class name
   */
  private final Class< ? > serviceClass;
  
  /**
   * The service action name
   */
  private final String action = SDCService.ACTION;
  
  /**
   * Extension for file backups.
   */
  private static final String BAK_EXTENSION = ".bak";
  
  /**
   * Title for RSA key file selection.
   */
  private static final String TITLE_RSA_KEY_SELECTION =
      "Select RSA Public Key File";
  
  /**
   * Title for XML configuration key file selection.
   */
  private static final String TITLE_XML_CONFIG_FILE_SELECTION =
      "Select XML Configuration File";
  
  /**
   * File selection dialog identifier for pub key file selection.
   */
  private static final int SELECT_EXT_RSA_PUBKEY_FILE = 0;
  
  /**
   * File selection dialog identifier for external defaults file selection.
   */
  private static final int SELECT_EXT_DEFAULTS_FILE = 1;
  
  /**
   * The service running state listener
   */
  private final ServiceRunningStateListener serviceRunningStateListner;
  
  /**
   * The preference manager
   */
  private final ApplicationPreferenceManager prefManager;
  
  /**
   * Constructor
   * 
   * @param serviceClass
   */
  public AbstractServiceControlActivity( Class< ? > serviceClass )
  {
    super();
    this.serviceClass = serviceClass;
    
    this.prefManager = new ApplicationPreferenceManagerImpl();
    
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
        onServiceRunningStateChanged( isRunning );
      }
    };
  }
  
  /**
   * Getter for the service class
   * 
   * @return the service class
   */
  public final Class< ? > getServiceClass()
  {
    return serviceClass;
  }
  
  /**
   * Getter for preference manager
   * 
   * @return the preference manager
   */
  public final ApplicationPreferenceManager getPrefManager()
  {
    return prefManager;
  }
  
  /**
   * Does display an alert message box.
   * 
   * @param context
   *          the application context
   * @param msg
   *          the message to display
   */
  protected static final void showAlertMessage( Context context, String msg )
  {
    final AlertDialog alertDialog = new AlertDialog.Builder( context ).create();
    alertDialog.setTitle( context.getResources().getText( R.string.sdc_app_name ) );
    alertDialog.setMessage( msg );
    alertDialog.setButton( context.getResources().getText(
        R.string.str_ok ), new DialogInterface.OnClickListener()
    {
      public void onClick( DialogInterface dialog, int whichButton )
      {
        alertDialog.cancel();
      }
    } );
    alertDialog.setCancelable( true );
    alertDialog.setCanceledOnTouchOutside( true );
    alertDialog.show();
  }
  
  /**
   * Method to handle the selection of "load external defaults" in the option
   * menu
   * 
   * @return true if successful, false otherwise
   */
  private final boolean onLoadRSAPublicKey()
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
  private final boolean onLoadDefaults()
  {
    Intent intent = new Intent( this, SDCFileBrowserActivity.class );
    intent.putExtra( SDCFileBrowserActivity.TITLE,
        TITLE_XML_CONFIG_FILE_SELECTION );
    intent.putExtra( SDCFileBrowserActivity.STARTDIR,
        Environment.getExternalStorageDirectory().getAbsolutePath() );
    intent.putExtra( SDCFileBrowserActivity.PATTERN, "\\.*\\.xml" );
    startActivityForResult( intent, SELECT_EXT_DEFAULTS_FILE );
    return true;
  }
  
  /**
   * Method to handle for selection of "preferences" in the option menu
   * 
   * @return true if successful, false otherwise
   */
  protected boolean onPreferences()
  {
    Intent intent = new Intent( this, SDCPreferenceActivity.class );
    startActivity( intent );
    return true;
  }
  
  @Override
  protected void onResume()
  {
    IntentFilter filter = new IntentFilter();
    filter.addAction( SDCService.ACTION );
    getApplicationContext().registerReceiver( serviceRunningStateListner,
        filter );
    super.onResume();
  }
  
  @Override
  protected void onPause()
  {
    getApplicationContext().unregisterReceiver( serviceRunningStateListner );
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
    super.onDestroy();
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
    inflater.inflate( R.menu.basic_optionmenu, menu );
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
    
    MenuItem menuItem = menu.findItem( R.id.externalConfiguration );
    if ( menuItem != null )
    {
      menuItem.setEnabled( !isServiceRunning );
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
    boolean result = super.onOptionsItemSelected( item );
    int itemId = item.getItemId();
    if ( itemId == R.id.preferences )
    {
      result = onPreferences();
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
          
          try
          {
            // Validate configuration first
            Serializer serializer = new Persister();
            InputStream is =
                new FileInputStream( FileUtils.fileFromPath( srcFile ) );
            serializer.read( SDCConfiguration.class, is );
            
            // if we get here deserialization works
            String sdcfDefaultConfigFileName =
                getText( R.string.sdc_config_file_name ).toString();
            String destFile = getFilesDir().getPath() + File.separatorChar
                + sdcfDefaultConfigFileName;
            
            // back up last configuration, if necessaRY
            String bakFilename = destFile + BAK_EXTENSION;
            if ( FileUtils.fileFromPath( destFile ).exists() )
            {
              FileUtils.copy( destFile, bakFilename );
            }
            
            if ( FileUtils.copy( srcFile, destFile ) )
            {
              SDCConfigurationManager manager =
                    new SDCConfigurationManager( this,
                        sdcfDefaultConfigFileName );
              
              boolean valid = manager.isUsingExternalConfiguration();
              String reason = getResources().getString(
                  R.string.str_unknown );
              try
              {
                manager.updateDefaults( prefManager );
              }
              catch ( Exception e )
              {
                valid = false;
                reason = e.getMessage();
              }
              
              if ( valid )
              {
                // if we get here the configuration should work
                prefManager.resetToDefaults( this.getApplicationContext() );
              }
              else
              {
                FileUtils.deleteFile( destFile );
                
                // restore backup if necessary
                if ( FileUtils.fileFromPath( bakFilename ).exists() )
                {
                  FileUtils.copy( bakFilename, destFile );
                  FileUtils.deleteFile( bakFilename );
                }
                
                showAlertMessage( this, String.format(
                    getResources().getString(
                        R.string.err_external_config_invalid_params ), reason ) );
              }
            }
            else
            {
              showAlertMessage( this, String.format( getResources().getString(
                  R.string.err_external_file_copy ), srcFile ) );
            }
          }
          catch ( Exception e )
          {
            showAlertMessage( this, String.format( getResources().getString(
                R.string.err_external_config_deserialization ), e.getMessage() ) );
          }
          break;
        }
      }
    }
  }
  
  /**
   * Handler to react on service running state changes.
   * 
   * @param isRunning
   *          running state flag
   */
  protected abstract void onServiceRunningStateChanged( boolean isRunning );
  
}