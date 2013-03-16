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
package de.unikassel.android.sdcframework.data.independent;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;


/**
 * The sample data of the time provider synchronization state changes (synchronized or not).
 * 
 * @see de.unikassel.android.sdcframework.devices.TimeProviderDevice
 * @see de.unikassel.android.sdcframework.devices.TimeProviderDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "data")
public final class TimeProviderSampleData 
extends AbstractSampleData
{
  /**
   * The Flag if the time provider is currently synchronized.
   */
  @Element( name = "synced", required = false )
  private boolean synced;
  
  /**
   * Constructor
   */
  public TimeProviderSampleData()
  {}
  
  /**
   * Copy-Constructor
   * 
   * @param sampleData
   *          the sample data to copy from
   */
  public TimeProviderSampleData( TimeProviderSampleData sampleData )
  {
    setSynced( sampleData.isSynced()); 
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.data.facade.SampleData#doClone()
   */
  @Override
  public final SampleData doClone()
  {
    return new TimeProviderSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof TimeProviderSampleData )
    {
      TimeProviderSampleData sampleData = (TimeProviderSampleData) o;
      return isSynced() == sampleData.isSynced();
    }
    return false;
  }

  /**
   * Setter for the synchronization state.
  
   * @param synced the synchronization state to set
   */
  public void setSynced( boolean synced )
  {
    this.synced = synced;
  }

  /**
   * Getter for the synchronization state.
  
   * @return the synchronization state
   */
  public boolean isSynced()
  {
    return synced;
  }
  
  @Override
  public String getValues()
  {
    return "Time sync " + synced; 
  }
}
