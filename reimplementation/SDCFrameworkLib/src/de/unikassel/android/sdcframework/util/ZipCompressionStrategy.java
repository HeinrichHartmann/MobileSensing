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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;
import de.unikassel.android.sdcframework.util.facade.CompressionStrategy;

/**
 * The compression strategy to create ZIP archives.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ZipCompressionStrategy implements CompressionStrategy
{
  /**
   * The internal buffer size used while piping data
   */
  private static final int BUFFER_SIZE = 2048;
  
  /**
   * the internal buffer
   */
  private final byte buffer[];
  
  /**
   * The compression method
   */
  private final int method;
  
  /**
   * The compression level
   */
  private final int level;
  
  /**
   * Constructor
   */
  public ZipCompressionStrategy()
  {
    this( ZipOutputStream.DEFLATED, Deflater.DEFAULT_COMPRESSION );
  }
  
  /**
   * Constructor
   */
  protected ZipCompressionStrategy( int method, int level )
  {
    buffer = new byte[ BUFFER_SIZE ];
    this.method = method;
    this.level = level;
  }
  
  /**
   * Constructor
   * 
   * @param deflated
   *          flag for archive content compression ( true for compression )
   */
  public ZipCompressionStrategy( boolean deflated )
  {
    this( deflated ? ZipOutputStream.DEFLATED : ZipOutputStream.STORED,
        Deflater.DEFAULT_COMPRESSION );
  }
  
  /**
   * Getter for the method
   * 
   * @return the method
   */
  public final int getMethod()
  {
    return method;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see
   * de.unikassel.android.sdcframework.util.facade.CompressionStrategy#compress
   * (java.util.List, java.io.BufferedOutputStream)
   */
  @Override
  public boolean compress( List< String > files, BufferedOutputStream out )
      throws IOException
  {
    boolean result = true;
    
    // create s deflater output stream
    ZipOutputStream deflaterOut = createDeflaterOutPutStream( out );
    deflaterOut.setMethod( method );
    deflaterOut.setLevel( level );
    
    try
    {
      for ( String fileName : files )
      {
        // create file input stream
        File file = FileUtils.fileFromPath( fileName );
        FileInputStream in = new FileInputStream( file );
        BufferedInputStream bufferedIn = new BufferedInputStream( in );
        
        // Create and add a new zip entry
        ZipEntry zipEntry = createArchiveEntry( file.getName() );
        
        try
        {
          deflaterOut.putNextEntry( zipEntry );
          pipeInToOut( bufferedIn, deflaterOut );
          deflaterOut.closeEntry();
        }
        finally
        {
          in.close();
        }
      }
      result = true;
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
    
    deflaterOut.finish();
    deflaterOut.close();
    return result;
  }
  
  /**
   * Does create an archive entry for the compressed file
   * 
   * @param file
   *          the file name and path
   * @return the archive entry
   */
  protected ZipEntry createArchiveEntry( String file )
  {
    return new ZipEntry( file );
  }
  
  /**
   * Does create the deflater output stream
   * 
   * @param out
   *          the buffered output stream to wrap
   * @return the deflater output stream
   */
  protected ZipOutputStream
      createDeflaterOutPutStream( BufferedOutputStream out )
          throws IOException
  {
    return new ZipOutputStream( out );
  }
  
  /**
   * Method to pipe input into output stream
   * 
   * @param in
   *          the input stream
   * @param out
   *          the deflater output stream
   * @throws IOException
   *           in case of an IO exception
   */
  private void pipeInToOut( BufferedInputStream in, DeflaterOutputStream out )
      throws IOException
  {
    int count = in.read( buffer, 0, BUFFER_SIZE );
    while ( count != -1 )
    {
      out.write( buffer, 0, count );
      count = in.read( buffer, 0, BUFFER_SIZE );
    }
    out.flush();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.CompressionStrategy#
   * getArchiveExtension()
   */
  @Override
  public String getArchiveExtension()
  {
    return ArchiveTypes.zip.toString();
  }
  
}
