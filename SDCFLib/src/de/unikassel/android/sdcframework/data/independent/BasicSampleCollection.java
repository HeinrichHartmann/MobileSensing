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

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

/**
 * Implementation of a collection of {@linkplain BasicSample}s.
 * 
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "sampleCollection" )
public class BasicSampleCollection
{
  /**
   * The filename for the serialized sample collection
   */
  public final static String SAMPLE_COLLECTION_FILE = "samples.xml";
  
  /**
   * The collection of sensor device Samples
   */
  @ElementList( name = "samples" )
  private List< BasicSample > samples;
  
  /**
   * Constructor
   */
  public BasicSampleCollection()
  {
    setSamples( new Vector< BasicSample >() );
  }
  
  /**
   * Setter for the samples
   * 
   * @param samples
   *          the samples to set
   */
  public final void setSamples( List< BasicSample > samples )
  {
    this.samples = samples;
  }
  
  /**
   * Access to the collection
   * 
   * @return the sample collection
   */
  public List< BasicSample > getSamples()
  {
    return samples;
  }
}
