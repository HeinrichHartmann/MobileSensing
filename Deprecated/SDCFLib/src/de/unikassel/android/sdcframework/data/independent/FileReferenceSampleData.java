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
 * The device specific sample data of a sensor device providing samples as binary data files in a related format.
 * 
 * @see de.unikassel.android.sdcframework.devices.AudioDevice
 * @see de.unikassel.android.sdcframework.devices.AudioDeviceScanner
 * @author Katy Hilgenberg
 */
@Root( name = "data" )
public final class FileReferenceSampleData 
extends AbstractSampleData
{
  /**
   * The file name and relative path
   */
  @Element( name = "file", required = false )
  private String file;
  
  /**
   * Constructor
   */
  public FileReferenceSampleData()
  {}
  
  /**
   * Constructor
   * 
   * @param sampleData
   *          the sample data to copy construct from
   */
  public FileReferenceSampleData( FileReferenceSampleData sampleData )
  {
    setFile( sampleData.getFile() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.data.independent.SampleData#doClone()
   */
  @Override
  public final SampleData doClone()
  {
    return new FileReferenceSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof FileReferenceSampleData )
    {
      FileReferenceSampleData sampleData = (FileReferenceSampleData) o;
      return BasicSample.equals( getFile(),
              sampleData.getFile() );
    }
    return false;
  }
  
  /**
   * Setter for the file
   * 
   * @param file
   *          the path to the file
   */
  public final void setFile( String file )
  {
    this.file = file;
  }
  
  /**
   * Getter for the file
   * 
   * @return the file
   */
  public final String getFile()
  {
    return file;
  }
  
  /* (non-Javadoc)
   * @see de.unikassel.android.sdcframework.data.independent.AbstractSampleData#getRelatedData()
   */
  @Override
  public final String getRelatedData()
  {
    return getFile();
  }

  /* (non-Javadoc)
   * @see de.unikassel.android.sdcframework.data.independent.AbstractSampleData#updateRelatedData(java.lang.String)
   */
  @Override
  public final void updateRelatedData( String fileName )
  {
    setFile( fileName );
  }
  
}
