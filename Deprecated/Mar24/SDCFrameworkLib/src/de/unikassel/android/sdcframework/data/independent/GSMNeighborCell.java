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

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * A simple class to hold neighbor GSM cell information.
 * 
 * @see GSMSampleData
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "cell" )
public final class GSMNeighborCell
{
  /**
   * The GSM cell ID
   */
  @Attribute( name = "cid" )
  private int cellId;
  
  /**
   * The GSM received signal strength indicator
   */
  @Attribute( name = "rssi" )
  private int signalStrength;
  
  /**
   * Constructor
   */
  public GSMNeighborCell()
  {
    super();
  }
  
  /**
   * Constructor
   * 
   * @param cellId
   *          the cell id
   * @param signalStrength
   *          the signal strength
   */
  public GSMNeighborCell( int cellId, int signalStrength )
  {
    super();
    this.cellId = cellId;
    this.signalStrength = signalStrength;
  }
  
  /**
   * Getter for the cellId
   * 
   * @return the cellId
   */
  public final int getCellId()
  {
    return cellId;
  }
  
  /**
   * Setter for the cellId
   * 
   * @param cellId
   *          the cellId to set
   */
  public final void setCellId( int cellId )
  {
    this.cellId = cellId;
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
