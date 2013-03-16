/*
 * Copyright (C) 2012, Katy Hilgenberg.
 * Special acknowledgments to: Knowledge & Data Engineering Group, University of Kassel (http://www.kde.cs.uni-kassel.de).
 * Contact: sdcf@cs.uni-kassel.de
 *
 * This file is part of the SDCFramework project.
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
package de.unikassel.android.sdcframework.demo.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;
import de.unikassel.android.sdcframework.demo.R;
import de.unikassel.android.sdcframework.demo.related.util.CentralSampleSource;
import de.unikassel.android.sdcframework.demo.related.util.SampleCategory;
import de.unikassel.android.sdcframework.demo.related.util.SimpleServiceConnectionEventReceiver;
import de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder;
import de.unikassel.android.sdcframework.service.ServiceUtils;

/**
 * The implementation of the (demo) activity displaying sensor samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class SDCTabActivity
    extends TabActivity
{
  
  /**
   * Message, that the sdcf service apk is missing.
   */
  private final static String SDCF_MISSING_MSG =
      "The SDCF Service is not installed!\n"
          + "<a href=\"https://sourceforge.net/projects/sdcf/files/apps/SDCFramework%s.apk/download\">Download SDCFramework%s.apk</a>.";
  
  /**
   * Identifier for the alert dialog
   */
  private final static int ID_DLG_ALERT_SDCF = 1;
  
  /**
   * The SDC service connection holder
   */
  private final SDCServiceConnectionHolder connectionHolder;
  
  /**
   * The SDC service connection event receiver
   */
  private final SimpleServiceConnectionEventReceiver sdcEventReceiver;
  
  /**
   * The tab content factory
   */
  private TabHost.TabContentFactory contentFactory;
  
  /**
   * Constructor
   */
  public SDCTabActivity()
  {
    super();
    this.sdcEventReceiver = new SimpleServiceConnectionEventReceiver( this );
    this.connectionHolder =
        new SDCServiceConnectionHolder( sdcEventReceiver,
            SDCActivityConstants.serviceClass );
    this.contentFactory = new TabHost.TabContentFactory()
    {
      @Override
      public View createTabContent( String tag )
      {
        if ( SampleCategory.WIFI.toString().equals( tag ) )
        {
          return new SummaryView( SDCTabActivity.this,
              CentralSampleSource.getInstance(), SampleCategory.WIFI );
        }
        else if ( SampleCategory.BT.toString().equals( tag ) )
        {
          return new SummaryView( SDCTabActivity.this,
              CentralSampleSource.getInstance(), SampleCategory.BT );
        }
        else if ( SampleCategory.OTHER.toString().equals( tag ) )
        {
          return new SummaryView( SDCTabActivity.this,
              CentralSampleSource.getInstance(), SampleCategory.OTHER );
        }
        return null;
      }
    };
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    connectionHolder.onCreate( this );
    setContentView( R.layout.tabbed_view );
    
//    createTab( SampleCategory.WIFI );
//    createTab( SampleCategory.BT );
    createTab( SampleCategory.OTHER );
  }
  
  /**
   * Method to create a tab for a specific sensor device sample category
   * 
   * @param category
   *          view category
   */
  private void createTab( SampleCategory category )
  {
    TabHost tabHost = getTabHost();
    int drawable = 0;
    
    switch ( category )
    {
      case WIFI:
      {
        drawable = R.drawable.stat_sys_tether_wifi;
        break;
      }
      case BT:
      {
        drawable = R.drawable.stat_sys_tether_bluetooth;
        break;
      }
      case OTHER:
      {
        drawable = R.drawable.ic_menu_more;
        break;
      }
      default:
      {
        // add no tab if id is not supported
        return;
      }
    }
    
    // create tab
    String tabTag = category.toString();
    TabSpec spec = tabHost.newTabSpec( tabTag );
    spec.setContent( contentFactory );
    spec.setIndicator( "", getResources().getDrawable( drawable ) );
    tabHost.addTab( spec );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onDestroy()
   */
  @Override
  protected void onDestroy()
  {
    connectionHolder.onDestroy( this );
    super.onDestroy();
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
    
    if ( !ServiceUtils.isServiceAvailable( this,
        SDCActivityConstants.serviceClass ) )
    {
      showDialog( ID_DLG_ALERT_SDCF );
    }
    
    connectionHolder.onResume( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause()
  {
    connectionHolder.onPause( this );
    super.onPause();
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
    inflater.inflate( R.menu.view_optionmenu, menu );
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreateDialog(int)
   */
  @Override
  protected Dialog onCreateDialog( final int id )
  {
    Dialog dialog;
    
    switch ( id )
    {
      case ID_DLG_ALERT_SDCF:
      {
        AlertDialog.Builder builder;
        
        Context context = this;
        LayoutInflater inflater =
            (LayoutInflater) context.getSystemService( LAYOUT_INFLATER_SERVICE );
        View layout =
            inflater.inflate(
                R.layout.custom_dialog,
                                       (ViewGroup) findViewById( R.id.layout_dlg_root ) );
        
        TextView text = (TextView) layout.findViewById( R.id.fileEntry );
        text.setMovementMethod( LinkMovementMethod.getInstance() );
        String sVersion = "";
        try
        {
          PackageInfo  pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
          sVersion = String.format( SDCF_MISSING_MSG, pInfo.versionName, pInfo.versionName );
        }
        catch ( NameNotFoundException e )
        {}
        Spanned fromHtml = Html.fromHtml( sVersion );
        text.setText( fromHtml );
        
        ImageView image = (ImageView) layout.findViewById( R.id.image );
        image.setImageResource( R.drawable.icon );
        
        builder = new AlertDialog.Builder( context );
        builder.setView( layout );
        builder.setTitle( "Important" );
        builder.setPositiveButton( android.R.string.ok,
            new DialogInterface.OnClickListener()
            {
              @Override
              public void onClick( final DialogInterface dialog,
                  final int whichButton )
              {
                SDCTabActivity.this.finish();
              }
            } );
        dialog = builder.create();
        break;
      }
      default:
      {
        dialog = null;
        break;
      }
    }
    return dialog;
  }
}