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
package de.unikassel.android.sdcframework.util.facade;

/**
 * Interface for processors in a Chain of Responsibility. <br/>
 * <br/>
 * A chain worker does know an optional successor and is calling its successor
 * if he is not responsible or unable to do the work for the calling client.
 * 
 * @author Katy Hilgenberg
 * @param <T>
 *          the workers client type
 * 
 */
public interface ChainWorker< T >
{
  
  /**
   * The work method called by the client
   * 
   * @param client
   *          the client to do the work for
   * @return true if successful, false otherwise
   */
  public abstract boolean doWork( T client );
  
  /**
   * The setter for the successor
   * 
   * @param successor
   *          the successor to set
   */
  public abstract void setSuccessor( ChainWorker< T > successor );
  
  /**
   * The getter for the successor
   * 
   * @return the successor
   */
  public abstract ChainWorker< T > getSuccessor();
  
  /**
   * A setter for the successor returning a reference to successor set
   * 
   * @param successor
   *          the successor to set
   * @return a reference to the successor set
   */
  public abstract ChainWorker< T > withSuccessor(
      ChainWorker< T > successor );
  
}