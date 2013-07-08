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
package de.unikassel.android.sdcframework.app.scheduler;

import java.util.List;

import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.data.WeekdayScheduleEntry;
import de.unikassel.android.sdcframework.data.WeekdaySchedulerAction;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * The array adapter for weekday schedule list entries.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class WeekdayScheduleEntryListAdapter
    extends ArrayAdapter< WeekdayScheduleEntry >
{
  
  /**
   * Ongoing text constant.
   */
  private static final String ONGOING_TEXT = " ... ";
  
  /**
   * The layout inflater.
   */
  private final LayoutInflater inflater;
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   * @param entries
   *          the entry list
   */
  public WeekdayScheduleEntryListAdapter( Context context,
      List< WeekdayScheduleEntry > entries )
  {
    super( context, R.layout.weekday_schedule_entry_layout, entries );
    this.inflater =
        (LayoutInflater) context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE );
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
    
    if ( view == null )
    {
      view = inflater.inflate( R.layout.weekday_schedule_entry_layout, null );
    }
    
    WeekdayScheduleEntry item = getItem( position );
    TextView leftText = (TextView) view.findViewById( R.id.scheduleEntryLeft );
    TextView rightText = (TextView) view.findViewById( R.id.scheduleEntryRight );
    
    int minutes = item.getSeconds() / 60;
    String sTime = String.format( "%2d:%02d", minutes / 60, minutes % 60 );

    int color = getContext().getResources().getColor( item.isValid() ? R.color.light_gray : R.color.error_color );
    leftText.setTextColor( color );
    rightText.setTextColor( color );
    
    if ( WeekdaySchedulerAction.StartService.equals( item.getAction() ) )
    {
      leftText.setText( sTime );
      rightText.setText( ONGOING_TEXT );
    }
    else
    {
      rightText.setText( sTime );
      leftText.setText( ONGOING_TEXT );
    }
    
    return view;
  }
  
}
