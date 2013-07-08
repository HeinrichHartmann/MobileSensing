package de.unikassel.android.sdcframework.app.scheduler;

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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.data.Weekday;
import de.unikassel.android.sdcframework.data.WeekdayScheduleEntry;
import de.unikassel.android.sdcframework.data.WeekdaySchedulerAction;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Activity to create a new scheduler entry.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class SDCFSchedulerEditEntryActivity
    extends Activity
{
  /**
   * The intent extra key for the weekday.
   */
  public static final String WEEKDAY = "entry_weekday";
  
  /**
   * The intent extra key for the action.
   */
  
  public static final String ACTION = "entry_action";
  
  /**
   * The intent extra key for the time.
   */
  public static final String TIME = "entry_time";
  
  /**
   * The week day selection element.
   */
  private Spinner weekdaySpinner;
  
  /**
   * The action selection element.
   */
  private Spinner actionSpinner;
  
  /**
   * The
   * time picker element.
   */
  private TimePicker timePicker;
  
  /**
   * Inner class to implement an enumeration spinner adapter.
   * 
   * @author Katy Hilgenberg
   * @param <T>
   *          the enumeration type
   * 
   */
  public class EnumSpinnerAdapter< T extends Enum< T >>
      extends ArrayAdapter< T > implements
      SpinnerAdapter
  {
    
    /**
     * Constructor
     * 
     * @param context
     *          the context
     * @param enumClass
     *          the class of the enumeration type of this adapter
     */
    public EnumSpinnerAdapter( Context context, Class< T > enumClass )
    {
      super( context, android.R.layout.simple_spinner_dropdown_item,
          enumClass.getEnumConstants() );
    }
    
  }
  
  /**
   * Constructor
   */
  public SDCFSchedulerEditEntryActivity()
  {
    super();
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
    setContentView( R.layout.add_schedule_layout );
    
    weekdaySpinner = (Spinner) findViewById( R.id.selectWeekday );
    weekdaySpinner.setAdapter( new EnumSpinnerAdapter< Weekday >( this,
        Weekday.class ) );
    actionSpinner = (Spinner) findViewById( R.id.selectAction );
    actionSpinner.setAdapter( new EnumSpinnerAdapter< WeekdaySchedulerAction >( this,
        WeekdaySchedulerAction.class ) );
    timePicker = (TimePicker) findViewById( R.id.timePicker );
    timePicker.setIs24HourView( true );
    
    Button btnSave = (Button) findViewById( R.id.btnChoose );
    btnSave.setOnClickListener( new OnClickListener()
    {
      @Override
      public void onClick( View v )
      {
        onSave();
      }
    } );
    
    Intent intent = getIntent();
    
    try
    {
      if ( intent.hasExtra( WeekdayScheduleEntry.class.getSimpleName() ) )
      {
        WeekdayScheduleEntry entry = new WeekdayScheduleEntry( intent );
        weekdaySpinner.setSelection( entry.getWeekday().ordinal() );
        actionSpinner.setSelection( entry.getAction().ordinal() );
        long minutes = entry.getSeconds() / 60;
        timePicker.setCurrentMinute( (int) ( minutes % 60 ) );
        timePicker.setCurrentHour( (int) ( minutes / 60 ) );
      }
    }
    catch ( Exception e )
    {
      Logger.getInstance().error( this,
          "Invalid intent extras: " + e.getMessage() );
    }
  }
  
  /**
   * Save handler.
   */
  private void onSave()
  {
    Intent result = new Intent();
    result.putExtra( WEEKDAY, weekdaySpinner.getSelectedItem().toString() );
    result.putExtra( ACTION, actionSpinner.getSelectedItem().toString() );
    int time =
        ( timePicker.getCurrentHour() * 60 + timePicker.getCurrentMinute() ) * 60;
    result.putExtra( TIME, time );
    setResult( Activity.RESULT_OK, result );
    finish();
  }
}