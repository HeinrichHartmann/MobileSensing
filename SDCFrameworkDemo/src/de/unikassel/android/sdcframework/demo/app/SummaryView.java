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

import de.unikassel.android.sdcframework.demo.related.util.CentralSampleSource;
import de.unikassel.android.sdcframework.demo.related.util.SampleCategory;
import de.unikassel.android.sdcframework.demo.related.view.ExtendedListViewFactory;
import de.unikassel.android.sdcframework.demo.related.view.SampleListAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

/**
 * A summary view for all samples
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "ViewConstructor" )
public class SummaryView
    extends ListView
{
  /**
   * The sample source
   */
  private final CentralSampleSource source;
  
  /**
   * the list adapter of our list view
   */
  private SampleListAdapter listAdapter;
  
  
  /**
   * the sample category to display
   */
  private final SampleCategory category;
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   * @param category the sample category to display
   * @param source
   *          the sample source
   */
  public SummaryView( Context context, CentralSampleSource source, SampleCategory category )
  {
    super( context );
    this.source = source;
    this.category = category;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.view.View#onAttachedToWindow()
   */
  @Override
  protected void onAttachedToWindow()
  {
    super.onAttachedToWindow();
    
    Context context = getContext();
    listAdapter =
        new SampleListAdapter( context, source, category,
            new ExtendedListViewFactory( context) );
    setAdapter( listAdapter );
    setFocusable( true );
    setChoiceMode( ListView.CHOICE_MODE_SINGLE );
    
    setOnItemClickListener( new AdapterView.OnItemClickListener()
    {
      @Override
      public void onItemClick( AdapterView< ? > parentView, View childView,
          int position,
          long id )
      {
        listAdapter.setSelected( position );
      }
    } );
    
    setOnItemSelectedListener( new AdapterView.OnItemSelectedListener()
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
  
  /*
   * (non-Javadoc)
   * 
   * @see android.view.View#onDetachedFromWindow()
   */
  @Override
  protected void onDetachedFromWindow()
  {
    super.onDetachedFromWindow();
  }
}