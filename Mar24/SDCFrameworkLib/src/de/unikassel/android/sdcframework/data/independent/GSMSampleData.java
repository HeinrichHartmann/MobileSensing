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

import java.util.List;
import java.util.Vector;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;


/**
 * The device specific sample data of a GSM sensor device are the {@link #operator
 * operator name}, the {@link #cellId cell id}, the {@link #locationAreaCode
 * location area code} and the {@link #signalStrength signal strength}.
 * 
 * @see de.unikassel.android.sdcframework.devices.GSMDevice
 * @see de.unikassel.android.sdcframework.devices.GSMDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "data")
public final class GSMSampleData 
extends AbstractSampleData
{
  
  /**
   * The operator name if available ( null if unknown )
   */
  @Element( name = "operator", required = false )
  private String operator;
  
  /**
   * The GSM cell ID 
   */
  @Element( name = "cid" )
  private int cellId;
  
  /**
   * The GSM location area code
   */
  @Element( name = "lac" )
  private int locationAreaCode;
  
  /**
   * The GSM received signal strength indicator
   */
  @Element( name = "rssi" )
  private int signalStrength;
  
  /**
   * 
   */
  @ElementList( name = "neighbors", required = false )
  private List< GSMNeighborCell > neighbors;
  
  /**
   * Constructor
   */
  public GSMSampleData()
  {
    setNeighbors( new Vector< GSMNeighborCell >() );
  }
  
  /**
   * Copy Constructor
   * 
   * @param sampleData
   *          the sample to copy from
   */
  public GSMSampleData( GSMSampleData sampleData )
  {
    setCellId( sampleData.getCellId() );
    setLocationAreaCode( sampleData.getLocationAreaCode() );
    setOperator( sampleData.getOperator() );
    setSignalStrength( sampleData.getSignalStrength() );
    setNeighbors( new Vector< GSMNeighborCell >( sampleData.getNeighbors() ) );
  }

  /* (non-Javadoc)
   * @see de.unikassel.android.sdcframework.data.facade.SampleData#doClone()
   */
  @Override
  public final SampleData doClone()
  {
    return new GSMSampleData( this );
  }
  
  /* (non-Javadoc)
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof GSMSampleData )
    {
      GSMSampleData sampleData = (GSMSampleData) o;
      List< GSMNeighborCell > sampleNeighbors = sampleData.getNeighbors();
      
      boolean equal = getCellId() == sampleData.getCellId() &&
              getLocationAreaCode() == sampleData.getLocationAreaCode() &&
              BasicSample.equals( getOperator(),
                  sampleData.getOperator() ) &&
              getSignalStrength() == sampleData.getSignalStrength();
      
      // test for equal neighbors
      for ( GSMNeighborCell neighbor : getNeighbors() )
      {
        equal = equal && sampleNeighbors.contains( neighbor );
        if ( !equal ) break;
      }
      return equal;
    }
    return false;
  }
  
  /**
   * Getter for the neighbors cell list
   * 
   * @return the neighbors cell list
   */
  public final List< GSMNeighborCell > getNeighbors()
  {
    return neighbors;
  }
  
  /**
   * Setter for the neighbors cell list
   * 
   * @param neighbors
   *          the neighbors cell list to set
   */
  public final void setNeighbors( List< GSMNeighborCell > neighbors )
  {
    this.neighbors = neighbors;
  }
  
  /**
   * Getter for the operator
   * 
   * @return the operator
   */
  public final String getOperator()
  {
    return operator;
  }
  
  /**
   * Setter for the operator
   * 
   * @param operator
   *          the operator to set
   */
  public final void setOperator( String operator )
  {
    this.operator = operator;
  }
  
  /**
   * Getter for the cell Id
   * 
   * @return the cell Id
   */
  public final int getCellId()
  {
    return cellId;
  }
  
  /**
   * Setter for the cell Id
   * 
   * @param cellId
   *          the cell Id to set
   */
  public final void setCellId( int cellId )
  {
    this.cellId = cellId;
  }
  
  /**
   * Getter for the location area code
   * 
   * @return the location area code
   */
  public final int getLocationAreaCode()
  {
    return locationAreaCode;
  }
  
  /**
   * Setter for the location area code
   * 
   * @param locationAreaCode
   *          the location area code to set
   */
  public final void setLocationAreaCode( int locationAreaCode )
  {
    this.locationAreaCode = locationAreaCode;
  }
  
  /**
   * Getter for the signal strength
   * 
   * @return the signal strength
   */
  public final int getSignalStrength()
  {
    return signalStrength;
  }
  
  /**
   * Setter for the signal strength
   * 
   * @param signalStrength
   *          the signal strength to set
   */
  public final void setSignalStrength( int signalStrength )
  {
    this.signalStrength = signalStrength;
  }
}
