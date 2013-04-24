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
package de.unikassel.android.sdcframework.persistence.facade;

import android.database.sqlite.SQLiteFullException;

/**
 * Interface for database commands.
 * 
 * @author Katy Hilgenberg
 * @param <T>
 *          the result type of the command
 */
public interface DatabaseCommand< T >
{
  /**
   * Method to execute the command
   * 
   * @param dbAdapter
   *          the database adapter to use for execution
   * @return true if successful, false otherwise
   * @throws SQLiteFullException
   *           if command execution fails due to the fact that the database is
   *           full
   */
  public abstract boolean execute( DatabaseAdapter dbAdapter )
      throws SQLiteFullException;
  
  /**
   * Getter for the result
   * 
   * @return the result
   */
  public abstract T getResult();
}