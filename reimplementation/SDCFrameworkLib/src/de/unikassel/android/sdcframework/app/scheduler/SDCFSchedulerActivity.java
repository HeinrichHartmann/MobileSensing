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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewParent;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.app.facade.DialogUtils;
import de.unikassel.android.sdcframework.data.Weekday;
import de.unikassel.android.sdcframework.data.WeekdaySchedule;
import de.unikassel.android.sdcframework.data.WeekdayScheduleEntry;
import de.unikassel.android.sdcframework.data.WeekdaySchedulerAction;
import de.unikassel.android.sdcframework.data.WeeklySchedule;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.preferences.facade.SinglePreference;
import de.unikassel.android.sdcframework.util.Logger;

/**
 * Scheduler for the the SDCF weekly time schedule.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class SDCFSchedulerActivity
    extends Activity
{
  
  /**
   * Constant to request a new schedule entry.
   */
  private static final int REQUEST_NEW_ENTRY = 0;
  
  /**
   * Constant to request changes for an existing schedule entry.
   */
  private static final int EDIT_OLD_ENTRY = 1;
  
  /**
   * The schedule.
   */
  private WeeklySchedule schedule;
  
  /**
   * The view to weekday schedule mapping.
   */
  private final Map< View, WeekdaySchedule > mapView2Schedule;
  
  /**
   * Currently edited entry.
   */
  private WeekdayScheduleEntry editedEntry;
  
  /**
   * The preference manager
   */
  private final ApplicationPreferenceManager prefManager;
  
  /**
   * The add entry button.
   */
  private Button btnAdd;
  
  /**
   * The add save schedule button.
   */
  private Button btnSave;
  
  /**
   * Constructor
   */
  public SDCFSchedulerActivity()
  {
    super();
    this.mapView2Schedule = new HashMap< View, WeekdaySchedule >();
    this.prefManager = new ApplicationPreferenceManagerImpl();
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
    setContentView( R.layout.scheduler_layout );
    
    btnAdd = (Button) findViewById( R.id.btnAdd );
    btnAdd.setOnClickListener( new OnClickListener()
    {
      
      @Override
      public void onClick( View v )
      {
        onAddEntry();
      }
    } );
    btnSave = (Button) findViewById( R.id.btnChoose );
    btnSave.setOnClickListener( new OnClickListener()
    {
      
      @Override
      public void onClick( View v )
      {
        onSaveSchedule();
      }
    } );
    
    
    setSchedule( loadSchedule() );   
  }

  /**
   * @param newSchedule the new schedule to set
   */
  public void setSchedule( WeeklySchedule newSchedule )
  {
    this.schedule = newSchedule;
    
    mapView2Schedule.put( findViewById( R.id.Monday ),
        schedule.getScheduleForWeekday( Weekday.Monday ) );
    mapView2Schedule.put( findViewById( R.id.Tuesday ),
        schedule.getScheduleForWeekday( Weekday.Tuesday ) );
    mapView2Schedule.put( findViewById( R.id.Wednesday ),
        schedule.getScheduleForWeekday( Weekday.Wednesday ) );
    mapView2Schedule.put( findViewById( R.id.Thursday ),
        schedule.getScheduleForWeekday( Weekday.Thursday ) );
    mapView2Schedule.put( findViewById( R.id.Friday ),
        schedule.getScheduleForWeekday( Weekday.Friday ) );
    mapView2Schedule.put( findViewById( R.id.Saturday ),
        schedule.getScheduleForWeekday( Weekday.Saturday ) );
    mapView2Schedule.put( findViewById( R.id.Sunday ),
        schedule.getScheduleForWeekday( Weekday.Sunday ) );
    
    for ( View view : mapView2Schedule.keySet() )
    {
      TextView workDayNameView =
          (TextView) view.findViewById( R.id.weekdayName );
      WeekdaySchedule schedule = mapView2Schedule.get( view );
      workDayNameView.setText(
          schedule.getWeekday().name().substring( 0, 3 ) );
      
      ListView scheduleEntryList =
          (ListView) view.findViewById( R.id.weekdayScheduleEntryList );
      scheduleEntryList.setAdapter( new WeekdayScheduleEntryListAdapter( this,
          schedule.getEntries() ) );
      
      registerForContextMenu( scheduleEntryList );
    }
    
    editedEntry = null; 
    refreshView();
  }
  
  /**
   * Method to update button states
   */
  protected void refreshView()
  {
    btnSave.setEnabled( ScheduleValidator.validate( schedule ) );
    
    for ( View view : mapView2Schedule.keySet() )
    {
      
      ListView scheduleEntryList =
          (ListView) view.findViewById( R.id.weekdayScheduleEntryList );
      ( (WeekdayScheduleEntryListAdapter) ( (ListView) scheduleEntryList ).getAdapter() ).notifyDataSetChanged();
    }
  }
  
  /**
   * Does load the schedule from the service preferences.
   * 
   * @return the weekly schedule
   */
  protected WeeklySchedule loadSchedule()
  {
    return prefManager.getServicePreferences().getWeeklySchedulePreference().getConfiguration(
        prefManager.getSharedPreferences( getApplicationContext() ) );
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
    inflater.inflate( R.menu.scheduler_option_menu, menu );
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
    MenuItem menuItem = menu.findItem( R.id.saveSchedule );
    if ( menuItem != null )
      menuItem.setEnabled( btnSave.isEnabled() );
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
    int itemId = item.getItemId();
    if ( itemId == R.id.addEntry )
    {
      return onAddEntry();
    }
    else if ( itemId == R.id.saveSchedule )
    {
      return onSaveSchedule();
    }
    else if ( itemId == R.id.clearSchedule )
    {
      return onClearSchedule();
    }
    return super.onOptionsItemSelected( item );
  }
  
  /**
   * Method to save a schedule and leave
   * @return true if successful, false otherwise.
   */
  private boolean onClearSchedule()
  {
    setSchedule( new WeeklySchedule() );
    return true;
  }

  /**
   * Method to save a schedule and leave
   * 
   * @return true if successful, false otherwise.
   */
  private boolean onSaveSchedule()
  {
    if ( ScheduleValidator.validate( schedule ) )
    {
      try
      {
        SinglePreference< WeeklySchedule > schedulePref =
            prefManager.getServicePreferences().getWeeklySchedulePreference();
        Editor editor =
            prefManager.getSharedPreferences( getApplicationContext() ).edit();
        editor.putString( schedulePref.getKey(), schedule.toXML() );
        editor.commit();
        
        Intent result = new Intent();
        setResult( Activity.RESULT_OK, result );
        
        Logger.getInstance().debug( this, schedule.toString() );
        finish();
        return true;
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this, e.getMessage() );
        DialogUtils.showAlertMessage( this, String.format(
            getResources().getString(
                R.string.err_saving_schedule ), e.getMessage() ) );
      }
    }
    
    return false;
  }
  
  /**
   * Handler for add entry.
   * 
   * @return true if successful, false otherwise
   */
  private boolean onAddEntry()
  {
    Intent intent = new Intent( this, SDCFSchedulerEditEntryActivity.class );
    startActivityForResult( intent, REQUEST_NEW_ENTRY );
    return true;
  }
  
  /**
   * Handler for edit entry.
   * 
   * @param entry
   *          the entry to wedit
   * 
   * @return true if successful, false otherwise
   */
  private boolean onEditEntry( WeekdayScheduleEntry entry )
  {
    Intent intent = entry.getIntent();
    intent.setClass( this, SDCFSchedulerEditEntryActivity.class );
    editedEntry = entry;
    startActivityForResult( intent, EDIT_OLD_ENTRY );
    return true;
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
        case REQUEST_NEW_ENTRY:
        {
          addNewEntry( data );
          break;
        }
        case EDIT_OLD_ENTRY:
        {
          deleteEntry( editedEntry );
          addNewEntry( data );
          break;
        }
      }
    }
  }
  
  /**
   * Method to create a new entry from intent data.
   * 
   * @param data
   *          the intent data to create new entry from
   */
  protected void addNewEntry( Intent data )
  {
    String val = data.getStringExtra( SDCFSchedulerEditEntryActivity.WEEKDAY );
    Entry< View, WeekdaySchedule > weekdayScheduleEntry =
        getWeekdayScheduleEntry( val );
    WeekdaySchedule weekdaySchedule = weekdayScheduleEntry.getValue();
    
    int startTime =
        data.getIntExtra( SDCFSchedulerEditEntryActivity.TIME, 0 );
    val = data.getStringExtra( SDCFSchedulerEditEntryActivity.ACTION );
    WeekdayScheduleEntry newEntry = new WeekdayScheduleEntry( 
        startTime, WeekdaySchedulerAction.valueOf( val ) );
    weekdaySchedule.addEntry( newEntry );
    
    refreshView();
  }
  
  /**
   * Method to request weekday entry value for a string description value.
   * 
   * @param val
   *          the value to get an entry for
   * @return the entry for the given value or null
   */
  private Entry< View, WeekdaySchedule > getWeekdayScheduleEntry( String val )
  {
    Weekday weekday = Weekday.valueOf( val );
    for ( Entry< View, WeekdaySchedule > entry : mapView2Schedule.entrySet() )
    {
      if ( weekday.equals( entry.getValue().getWeekday() ) )
        return entry;
    }
    return null;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onDestroy()
   */
  @Override
  protected void onDestroy()
  {
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
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onPause()
   */
  @Override
  protected void onPause()
  {
    super.onPause();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu,
   * android.view.View, android.view.ContextMenu.ContextMenuInfo)
   */
  @Override
  public void onCreateContextMenu( ContextMenu menu, View v,
      ContextMenuInfo menuInfo )
  {
    super.onCreateContextMenu( menu, v, menuInfo );
    MenuInflater inflater = getMenuInflater();
    inflater.inflate( R.menu.scheduler_context_menu, menu );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
   */
  @Override
  public boolean onContextItemSelected( MenuItem item )
  {
    if ( item.getItemId() == R.id.editEntry )
    {
      return editEntry( (AdapterContextMenuInfo) item.getMenuInfo() );
    }
    else if ( item.getItemId() == R.id.deleteEntry )
    {
      return deleteEntry( (AdapterContextMenuInfo) item.getMenuInfo() );
    }
    return super.onContextItemSelected( item );
  }
  
  /**
   * Delete entry context menu handler.
   * 
   * @param info
   *          the context menu info
   * @return true if successful, false otherwise
   */
  private boolean deleteEntry( AdapterContextMenuInfo info )
  {
    ViewParent parent = info.targetView.getParent();
    if ( parent instanceof ListView )
    {
      ListView view = (ListView) parent;
      if ( view.getAdapter() instanceof WeekdayScheduleEntryListAdapter )
      {
        WeekdayScheduleEntryListAdapter adapter =
            (WeekdayScheduleEntryListAdapter) view.getAdapter();
        WeekdayScheduleEntry entry = adapter.getItem( (int) info.id );
        deleteEntry( entry );
        return true;
      }
    }
    return false;
  }
  
  /**
   * Method to delete an entry.
   * @param entry
   *          the entry to delete
   */
  protected void deleteEntry( WeekdayScheduleEntry entry )
  {
    entry.setWeekdaySchedule( null );
    refreshView();
  }
  
  /**
   * Edit entry context menu handler.
   * 
   * @param info
   *          the context menu info
   * @return true if successful, false otherwise
   */
  private boolean editEntry( AdapterContextMenuInfo info )
  {
    ViewParent parent = info.targetView.getParent();
    if ( parent instanceof ListView )
    {
      ListView view = (ListView) parent;
      if ( view.getAdapter() instanceof WeekdayScheduleEntryListAdapter )
      {
        WeekdayScheduleEntryListAdapter adapter =
            (WeekdayScheduleEntryListAdapter) view.getAdapter();
        WeekdayScheduleEntry entry = adapter.getItem( (int) info.id );
        return onEditEntry( entry );
      }
    }
    return false;
  }
}