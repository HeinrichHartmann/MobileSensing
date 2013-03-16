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

import de.unikassel.android.sdcframework.data.independent.SampleData;
import android.content.Context;
import android.view.View;

/**
 * Interface for list view factories to work together with the abstract table
 * layout view.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface ListViewFactory
{
  /**
   * Method to create the data view
   * 
   * @param context
   *          the context
   * @param layoutView
   *          the layout view
   * @return the view for the sample data
   */
  public abstract View createDataView( Context context,
      TableLayoutView layoutView );
  
  /**
   * Method to update the data view with new content.
   * 
   * @param dataView
   *          the data view to update
   * @param sampleData
   *          the new sample data
   * @throws Exception
   */
  public abstract void updateDataView( View dataView, SampleData sampleData )
    throws Exception;
}