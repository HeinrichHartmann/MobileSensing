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

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * A simple file list adapter.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class FileListAdapter extends ArrayAdapter< File >
{
  /**
   * The activity context.
   */
  private final Activity context;
  
  /**
   * The file matcher filter.
   */
  private final FileMatcherFilter filter;
  
  /**
   * Constructor
   * 
   * @param context
   *          the activity context
   * @param filter
   *          a regular file pattern expression
   */
  public FileListAdapter( Activity context, FileMatcherFilter filter )
  {
    super( context, R.id.textViewFileName );
    this.context = context;
    this.filter = filter;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.widget.ArrayAdapter#getView(int, android.view.View,
   * android.view.ViewGroup)
   */
  @Override
  public View getView( int position, View convertView, ViewGroup parent )
  {
    View view = convertView;
    
    if ( convertView == null )
    {
      LayoutInflater inflator = context.getLayoutInflater();
      view = inflator.inflate( R.layout.file_list_entry, null );
    }
    
    TextView fileView = (TextView) view.findViewById( R.id.textViewFileName );
    File file = getItem( position );
    int color = Color.WHITE;
    if ( file.isDirectory() )
    {
      color = getContext().getResources().getColor( R.color.info_color );
    }
    fileView.setTextColor( color );
    fileView.setText( file.getName() );
    
    return view;
  }
  
  /**
   * File list update method.
   * 
   * @param dir
   *          the directory
   */
  public void setDirectory( File dir )
  {
    if ( dir.exists() && dir.isDirectory() )
    {
      File[] listFiles = dir.listFiles( filter );
      if ( listFiles != null )
      {
        for ( File file : listFiles )
        {
          if ( !file.isHidden() )
            add( file );
        }
      }
    }
  }
}
