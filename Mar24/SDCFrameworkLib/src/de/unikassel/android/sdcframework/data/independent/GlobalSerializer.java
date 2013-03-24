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

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import org.simpleframework.xml.core.Persister;

/**
 * The global serializer does hold the global Persister instance of the Simple
 * XML framework. It does provide static methods for serialization and
 * deserialization purpose.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class GlobalSerializer extends Persister
{
  /**
   * The global serializer instance
   */
  private final static Persister serializer = new Persister();
  
  /**
   * Method to serialize an object
   * 
   * @param source
   *          the source object to serialize
   * @return the serialized object as string
   * @throws Exception
   *           if the schema for the object is not valid
   */
  public static String toXml( Object source ) throws Exception
  {
    StringWriter resultBuffer = new StringWriter();
    serializer.write( source, resultBuffer );
    return resultBuffer.toString();
  }
  
  /**
   * Method to serialize an object into a file
   * 
   * @param source
   *          the source object to serialize
   * @param file
   *          the file to write serialized object into
   * @throws Exception
   *           if the schema for the object is not valid
   */
  public static void serializeToFile( Object source, File file )
      throws Exception
  {
    serializer.write( source, file );
  }
  
  /**
   * Method to serialize an object into an output stream
   * 
   * @param source
   *          the source object to serialize
   * @param os
   *          the output stream to write serialized object into
   * @throws Exception
   *           if the schema for the object is not valid
   */
  public static void serializeToStream( Object source, OutputStream os )
      throws Exception
  {
    serializer.write( source, os );
  }
  
  /**
   * Deserialization from the XML file to the type
   * 
   * @param <T>
   *          the type to deserialize
   * @param c
   *          the class of the type to deserialize
   * @param file
   *          the XML file
   * @return the deserialized instance of the class
   * @throws Exception
   *           if the object cannot be fully deserialized
   */
  public static < T > T serializeFromFile( Class< T > c, File file )
      throws Exception
  {
    return serializer.read( c, file );
  }
  
  /**
   * Deserialization from an input stream to the type
   * 
   * @param <T>
   *          the type to deserialize
   * @param c
   *          the class of the type to deserialize
   * @param is
   *          the input stream
   * @return the deserialized instance of the class
   * @throws Exception
   *           if the object cannot be fully deserialized
   */
  public static < T > T serializeFromStream( Class< T > c, InputStream is )
      throws Exception
  {
    return serializer.read( c, is );
  }
  
  /**
   * Deserialization from the XML representation to the type
   * 
   * @param <T>
   *          the type to deserialize
   * @param c
   *          the class of the type to deserialize
   * @param xml
   *          the XML representation of the type as string
   * @return the deserialized instance of this class
   * @throws Exception
   *           if the object cannot be fully deserialized
   */
  public static < T > T fromXML( Class< T > c, String xml )
      throws Exception
  {
    return serializer.read( c, xml );
  }
}
