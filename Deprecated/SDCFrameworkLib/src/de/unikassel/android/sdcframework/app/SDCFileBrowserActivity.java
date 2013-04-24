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

import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.util.FileMatcherFilter;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.Logger;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple file browser activity.
 * 
 * @author Katy Hilgenberg
 * 
 */
/**
 * @author Katy Hilgenberg
 * 
 */
public class SDCFileBrowserActivity extends Activity implements
    OnItemClickListener
{
  /**
   * Intent extra key for the start directory.
   */
  public final static String TITLE = "Title";
  
  /**
   * Intent extra key for the start directory.
   */
  public final static String STARTDIR = "StartDir";

  /**
   * Intent extra key for an optional file pattern
   */
  public static final String PATTERN = "Pattern";
  
  /**
   * Intent extra key for the selected file in the activity result.
   */
  public final static String FILE = "ChoosenFile";
  
  /**
   * The path view.
   */
  private TextView pathView;
  
  /**
   * The list view adapter.
   */
  private FileListAdapter fileAdapter;
  
  /**
   * The navigate to parent directory button.
   */
  private ImageButton btnDirUp;
  
  /**
   * The choose selection button.
   */
  private Button btnChoose;
  
  /**
   * The current directory.
   */
  private File currentDir;
  
  /**
   * The selected file.
   */
  private File selectedFile;
  
  /**
   * The files list view.
   */
  private ListView filesView;
  
  /**
   * The file matcher filter.
   */
  private FileMatcherFilter filter;
  
  /**
   * Constructor
   */
  public SDCFileBrowserActivity()
  {
    super();
    this.filter = new FileMatcherFilter( ".*" );
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
    setContentView( R.layout.file_browser_layout );
    
    btnDirUp = (ImageButton) findViewById( R.id.btnDirUp );
    btnDirUp.setOnClickListener( new View.OnClickListener()
    {
      public void onClick( View v )
      {
        onDirUp();
      }
    } );
    btnChoose = (Button) findViewById( R.id.btnChoose );
    btnChoose.setOnClickListener( new View.OnClickListener()
    {
      public void onClick( View v )
      {
        onChooseSelection();
      }
    } );
    pathView = (TextView) findViewById( R.id.pathView );
    
    filesView = (ListView) findViewById( R.id.filesView );
    filesView.setOnItemClickListener( this );
    
    Intent intent = getIntent();
    File startDir = Environment.getRootDirectory();
    
    try
    {
      if ( intent.hasExtra( TITLE ) )
      {
        setTitle( intent.getStringExtra( TITLE ) );
      }
      if ( intent.hasExtra( STARTDIR ) )
      {
        startDir = FileUtils.fileFromPath( intent.getStringExtra( STARTDIR ) );
      }
      if( intent.hasExtra( PATTERN ))
      {

        this.filter = new FileMatcherFilter( intent.getStringExtra( PATTERN ) );
      }
    }
    catch ( Exception e )
    { 
      Logger.getInstance().error( this, "Invalid intent extras: " + e.getMessage() );
    }
    
    setCurrentDir( startDir );
  }
  
  /**
   * Setter for the current directory
   * 
   * @param dir
   *          the directory to set
   */
  public void setCurrentDir( File dir )
  {
    if ( dir == null )
      return;
    
    currentDir = dir;
    fileAdapter = new FileListAdapter( this, filter );
    filesView.setAdapter( fileAdapter );
    fileAdapter.setDirectory( currentDir );
    btnChoose.setEnabled( false );
    pathView.setText( dir.getAbsolutePath() );
  }
  
  /**
   * Setter for the selected file.
   * 
   * @param file the selected file
   */
  private void setSelection( File file )
  {
    this.selectedFile = file;
    btnChoose.setEnabled( true );
    pathView.setText( file.getAbsolutePath() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget
   * .AdapterView, android.view.View, int, long)
   */
  @Override
  public void onItemClick( AdapterView< ? > parent, View view, int position,
      long id )
  {
    File file = fileAdapter.getItem( position );
    if ( file.isDirectory() )
    {
      setCurrentDir( file );
      
    }
    else
    {
      setSelection( file );
    }    
  }

  /**
   * Handler for directory up navigation
   */
  protected void onDirUp()
  {
    if ( currentDir != null )
    {
      String parentPath = currentDir.getParent();
      if ( parentPath != null )
      {
        setCurrentDir( new File( parentPath ) );
      }
    }
  }
  
  /**
   * Handler for choose selected file
   */
  protected void onChooseSelection()
  { 
    if( selectedFile == null ) return;
    
    Intent result = new Intent();
    result.putExtra( FILE, selectedFile.getAbsolutePath() );
    setResult( Activity.RESULT_OK, result );
    finish();
  }
  
}
