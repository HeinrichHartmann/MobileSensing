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
 * The device specific sample data of a sensor device with a simple text
 * information (e.g. the sensor device for tags). It does just contain a text
 * field.
 * 
 * @see de.unikassel.android.sdcframework.devices.TwitterDevice
 * @see de.unikassel.android.sdcframework.devices.TwitterDeviceScanner
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "data" )
public final class TextSampleData
    extends AbstractSampleData
{
  /**
   * The text (tags).
   */
  @Element( name = "txt", required = false )
  private String text;
  
  /**
   * Constructor
   */
  public TextSampleData()
  {}
  
  /**
   * Constructor
   * 
   * @param sampleData
   *          the sample data to copy construct from
   */
  public TextSampleData( TextSampleData sampleData )
  {
    setText( sampleData.getText() );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.data.facade.SampleData#doClone()
   */
  @Override
  public final SampleData doClone()
  {
    return new TextSampleData( this );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  protected final Object clone() throws CloneNotSupportedException
  {
    return doClone();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public final boolean equals( Object o )
  {
    if ( o instanceof TextSampleData )
    {
      TextSampleData sampleData = (TextSampleData) o;
      return BasicSample.equals( getText(),
              sampleData.getText() );
    }
    return false;
  }
  
  /**
   * Setter for the text (tags)
   * 
   * @param text
   *          the text (tags) to set
   */
  public final void setText( String text )
  {
    this.text = text;
  }
  
  /**
   * Getter for the text (tags)
   * 
   * @return the text (tags)
   */
  public final String getText()
  {
    return text;
  }
}
