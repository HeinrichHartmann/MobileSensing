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

import java.util.List;

import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.demo.related.util.CentralSampleSource;
import de.unikassel.android.sdcframework.demo.related.util.SampleCategory;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * A sample list adapter.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class SampleListAdapter
    extends BaseAdapter
    implements EventObserver< CentralSampleSource >
{
  /**
   * the selected position
   */
  private int selectedPosition = -1;
  
  /**
   * the sample source
   */
  protected final CentralSampleSource sampleSource;
  
  /**
   * the context
   */
  private final Context context;
  
  /**
   * the view factory
   */
  private final ListViewFactory factory;
  
  /**
   * the sample category to display
   */
  private final SampleCategory category;
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   * @param sampleSource
   *          the sample source
   * @param category
   *          the sample category to display
   * @param factory
   *          the list view factory
   */
  public SampleListAdapter( Context context, CentralSampleSource sampleSource,
      SampleCategory category, ListViewFactory factory )
  {
    super();
    this.sampleSource = sampleSource;
    this.context = context;
    this.factory = factory;
    this.category = category;
    sampleSource.registerEventObserver( this );
  }
  
  /**
   * Getter for the actual samples mapped
   * 
   * @return the the actual samples mapped
   */
  public List< BasicSample > getSampleCollection()
  {
    switch ( category )
    {
      case WIFI:
        return sampleSource.getMostRecentWifiSamples().getSamples();
      case BT:
        return sampleSource.getMostRecentBluetoothSamples().getSamples();
      case OTHER:
        return sampleSource.getMostRecentNonWifiOrBTSamples().getSamples();
      default:
        return sampleSource.getMostRecentSamples().getSamples();
    }
  }
  
  /**
   * Getter for the context
   * 
   * @return the context
   */
  protected Context getContext()
  {
    return context;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.widget.Adapter#getCount()
   */
  @Override
  public int getCount()
  {
    return getSampleCollection().size();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.widget.Adapter#getItem(int)
   */
  @Override
  public Object getItem( int position )
  {
    return getSampleCollection().get( position );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.widget.Adapter#getItemId(int)
   */
  @Override
  public long getItemId( int position )
  {
    return position;
  }
  
  /**
   * Method to create the internal table layout view
   * 
   * @return the new created layout table view
   */
  protected TableLayoutView createView()
  {
    return new TableLayoutView( context, this, factory );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.widget.Adapter#getView(int, android.view.View,
   * android.view.ViewGroup)
   */
  @Override
  public View getView( int position, View convertView, ViewGroup parent )
  {
    TableLayoutView layoutView = null;
    
    if ( convertView == null )
    {
      layoutView = createView();
    }
    else
    {
      layoutView = (TableLayoutView) convertView;
    }
    
    try
    {
      // update the cell renderer, and handle selection state
      layoutView.display( position, selectedPosition == position );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return layoutView;
  }
  
  /**
   * Position selection
   * 
   * @param position
   *          the position to select
   */
  public void setSelected( int position )
  {
    selectedPosition = position;
    notifyDataSetChanged();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de.
   * unikassel.android.sdcframework.util.facade.ObservableEventSource,
   * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
   */
  @Override
  public void onEvent(
      ObservableEventSource< ? extends CentralSampleSource > eventSource,
      CentralSampleSource observedEvent )
  {
    notifyDataSetChanged();
  }
}
