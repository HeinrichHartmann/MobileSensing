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
 * Interface for worker threads.
 * 
 * @see de.unikassel.android.sdcframework.util.AbstractWorkerThread
 * @author Katy Hilgenberg
 */
public interface WorkerThread
{
  
  /**
   * Getter for the working flag
   * 
   * @return the working flag
   */
  public abstract boolean isWorking();
  
  /**
   * Setter for the logging flag
   * 
   * @param doLog
   *          flag if logging should be done
   */
  public abstract void setLogging( boolean doLog );
  
  /**
   * Getter for the logging flag
   * 
   * @return the logging flag
   */
  public abstract boolean isLogging();
  
  /**
   * Getter for the terminated flag
   * 
   * @return the terminated flag
   */
  public abstract boolean hasTerminated();
  
  /**
   * Does stop current work and trigger thread termination
   */
  public abstract void doTerminate();
  
  /**
   * Does start working if thread is not in working state
   */
  public abstract void startWork();
  
  /**
   * Does stop working if thread is in working state
   */
  public abstract void stopWork();
  
}