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

/**
 * Interface for all the types implementing device specific sensor sample data.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface SampleData extends SerializableData
{
  /**
   * Method to clone sample data
   * 
   * @return a copy of the sample data
   */
  public abstract SampleData doClone();
  
  /**
   * Getter for related data
   * 
   * @return the path to a related data file if existing, null otherwise
   */
  public abstract String getRelatedData();
  
  /**
   * Update method for related data ( used to update relative file location for
   * transmitted samples )
   * 
   * @param fileName
   *          the new filename
   */
  public abstract void updateRelatedData( String fileName );
}
