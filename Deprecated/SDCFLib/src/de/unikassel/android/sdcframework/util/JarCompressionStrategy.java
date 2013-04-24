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
package de.unikassel.android.sdcframework.util;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;

/**
 * The compression strategy to create JAR archives.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class JarCompressionStrategy
    extends ZipCompressionStrategy
{
  
  /**
   * Constructor
   */
  public JarCompressionStrategy()
  {
    super( JarOutputStream.DEFLATED, Deflater.DEFAULT_COMPRESSION );
  }
  
  /**
   * Constructor
   * 
   * @param deflated
   *          flag for archive content compression ( true for compression )
   */
  public JarCompressionStrategy( boolean deflated )
  {
    super( deflated ? JarOutputStream.DEFLATED : JarOutputStream.STORED,
        Deflater.DEFAULT_COMPRESSION );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ZipCompressionStrategy#
   * createArchiveEntry(java.lang.String)
   */
  @Override
  protected ZipEntry createArchiveEntry( String file )
  {
    JarEntry jarEntry = new JarEntry( file );
    return jarEntry;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.ZipCompressionStrategy#
   * createDeflaterOutPutStream(java.io.BufferedOutputStream)
   */
  @Override
  protected ZipOutputStream
      createDeflaterOutPutStream( BufferedOutputStream out )
          throws IOException
  {
    return new JarOutputStream( out );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.ZipCompressionStrategy#
   * getArchiveExtension()
   */
  @Override
  public String getArchiveExtension()
  {
    return ArchiveTypes.jar.toString();
  }
  
}
