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
package de.unikassel.android.sdcframework.demo.related.view;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.demo.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Abstract base class for table layout views
 * 
 * @author Katy Hilgenberg
 * 
 */
@SuppressLint( "ViewConstructor" )
public class TableLayoutView
    extends TableLayout
{
  
  /**
   * The default padding used between entries
   */
  private static final int DEFAULT_PADDING = 5;
  
  /**
   * The device name view
   */
  private TextView viewDevice;
  
  /**
   * The time stamp view
   */
  private TextView viewTimeStamp;
  
  /**
   * The data view
   */
  private View viewData;
  
  /**
   * The flag to ignore the selection
   */
  private boolean ignoreSelection;
  
  /**
   * The view adapter
   */
  protected final SampleListAdapter adapter;
  
  /**
   * The list view factory
   */
  protected final ListViewFactory factory;
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   * @param adapter
   *          the adapter
   * @param factory
   *          the list view factory
   */
  public TableLayoutView( Context context, SampleListAdapter adapter,
      ListViewFactory factory )
  {
    super( context );
    this.adapter = adapter;
    this.factory = factory;
    
    createView();
  }
  
  /**
   * Getter for the ignore selection flag
   * 
   * @return the ignore selection flag
   */
  public boolean isIgnoreSelection()
  {
    return ignoreSelection;
  }
  
  /**
   * Setter for the ignore selection flag
   * 
   * @param ignoreSelection
   *          the ignore selection flag to set
   */
  public void setIgnoreSelection( boolean ignoreSelection )
  {
    this.ignoreSelection = ignoreSelection;
  }
  
  /**
   * Method for initial view creation
   */
  protected final void createView()
  {
    setColumnShrinkable( 0, true );
    setColumnStretchable( 0, true );
    setPadding( 1, 1, 1, 1 );
    
    TableRow row = new TableRow( getContext() );
    row.setLayoutParams( new TableLayout.LayoutParams(
        TableLayout.LayoutParams.FILL_PARENT,
        TableLayout.LayoutParams.WRAP_CONTENT
        ) );
    
    // device identifier display view
    viewDevice = new TextView( getContext() );
    viewDevice.setLayoutParams( new TableRow.LayoutParams(
        TableRow.LayoutParams.WRAP_CONTENT,
        TableRow.LayoutParams.WRAP_CONTENT
        ) );
    viewDevice.setTextColor( getContext().getResources().getColor( R.color.green ) );
    viewDevice.setTypeface( Typeface.DEFAULT, Typeface.BOLD );    
    viewDevice.setPadding( DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
        DEFAULT_PADDING );

    
    // time stamp display view
    viewTimeStamp = new TextView( getContext() );
    viewTimeStamp.setLayoutParams( new TableRow.LayoutParams(
        TableRow.LayoutParams.WRAP_CONTENT,
        TableRow.LayoutParams.WRAP_CONTENT
        ) );
    viewTimeStamp.setTypeface( Typeface.DEFAULT, Typeface.BOLD );
    viewTimeStamp.setTextColor( getContext().getResources().getColor( R.color.green ) );
    viewTimeStamp.setPadding( DEFAULT_PADDING, DEFAULT_PADDING,
        DEFAULT_PADDING, DEFAULT_PADDING );
    
    // sensor sample data display view
    viewData = factory.createDataView( getContext(), this );
    viewData.setLayoutParams( new TableRow.LayoutParams(
        TableLayout.LayoutParams.FILL_PARENT,
        TableLayout.LayoutParams.WRAP_CONTENT
        ) );
    viewData.setPadding( DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING,
        DEFAULT_PADDING );
    
    row.addView( viewDevice );
    row.addView( viewTimeStamp );
    addView( row );
    addView( viewData );
  }
  
  /**
   * Does create a string representation of the given time stamp
   * 
   * @param timeStamp
   *          the milliseconds since 01.01.1970
   * @return the time string
   */
  @SuppressLint( "SimpleDateFormat" )
  protected static final String toUTCString( long timeStamp )
  {
    SimpleDateFormat df = new SimpleDateFormat( "dd-MM-yyyy HH:mm:ss" );
    df.setTimeZone( TimeZone.getTimeZone( "GMT" ) );
    Calendar cal = new GregorianCalendar( TimeZone.getTimeZone( "GMT" ) );
    cal.setTimeInMillis( timeStamp );
    return df.format( cal.getTime() );
  }
  
  /**
   * Method to display the item at the selected position
   * 
   * @param position
   *          the position to display
   * @param selected
   *          flag if entry is selected
   */
  
  protected void display( int position, boolean selected )
  {
    BasicSample sample = adapter.getSampleCollection().get( position );
    String item = sample.getDeviceIdentifier();
    
    long timeStamp = sample.getTimeStamp();
    SampleData data = sample.getData();
    
    viewDevice.setText( item );
    
    viewTimeStamp.setText( toUTCString( timeStamp ) );
    try
    {
      factory.updateDataView( viewData, data );
    }
    catch ( Exception e )
    {}
    
    if ( selected || ignoreSelection )
    {
      viewData.setVisibility( VISIBLE );
    }
    else
    {
      viewData.setVisibility( GONE );
    }
  }
}