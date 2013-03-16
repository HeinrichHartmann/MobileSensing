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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import de.unikassel.android.sdcframework.demo.R;
import de.unikassel.android.sdcframework.demo.related.util.CentralSampleSource;
import de.unikassel.android.sdcframework.demo.related.util.SampleCategory;
import de.unikassel.android.sdcframework.demo.related.view.ExtendedListViewFactory;
import de.unikassel.android.sdcframework.demo.related.view.SampleListAdapter;
/**
 * A test implementation for an activity receiving broadcasted samples.
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "Registered" )
public class SDCSampleViewer
    extends SDCActivity
{
  /**
   * the list adapter of our list view
   */
  SampleListAdapter listAdapter;
  
  /**
   * Constructor
   */
  public SDCSampleViewer()
  {
    super();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.related.app.sampleview.SDCActivity#onCreate
   * (android.os.Bundle)
   */
  @Override
  public void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    
    setContentView( R.layout.simple_sample_view );
    ListView listView = (ListView) findViewById( R.id.listView );
    
    listAdapter =
        new SampleListAdapter( this, CentralSampleSource.getInstance(),
            SampleCategory.ALL, new ExtendedListViewFactory( this ) );
    listView.setAdapter( listAdapter );
    listView.setFocusable( true );
    listView.setChoiceMode( ListView.CHOICE_MODE_SINGLE );
    
    listView.setOnItemClickListener( new AdapterView.OnItemClickListener()
    {
      @Override
      public void onItemClick( AdapterView< ? > parentView, View childView,
          int position,
          long id )
      {
        listAdapter.setSelected( position );
      }
    } );
    
    listView.setOnItemSelectedListener( new AdapterView.OnItemSelectedListener()
    {
      public void onItemSelected( AdapterView< ? > parentView, View childView,
          int position, long id )
      {
        listAdapter.setSelected( position );
      }
      
      public void onNothingSelected( AdapterView< ? > parentView )
      {
        listAdapter.setSelected( -1 );
      }
    } );
  }
}