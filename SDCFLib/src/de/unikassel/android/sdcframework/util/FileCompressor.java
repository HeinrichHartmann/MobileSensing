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
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

import de.unikassel.android.sdcframework.util.facade.CompressionStrategy;

/**
 * A utility class to create archive files using a compression
 * strategy.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class FileCompressor
{
  /**
   * The compression strategy to use
   */
  private CompressionStrategy strategy;
  
  /**
   * Constructor
   * 
   * @param strategy
   *          the compression strategy to use
   */
  public FileCompressor( CompressionStrategy strategy )
  {
    if ( strategy == null )
      throw new InvalidParameterException( "strategy  is null" );
    setStrategy( strategy );
  }
  
  /**
   * Setter for the strategy
  
   * @param strategy the strategy to set
   */
  public final void setStrategy( CompressionStrategy strategy )
  {
    if( strategy != null )
      this.strategy = strategy;
  }

  /**
   * Getter for the strategy
   * 
   * @return the strategy
   */
  public final CompressionStrategy getStrategy()
  {
    return strategy;
  }
  
  /**
   * Compression method
   * 
   * @param files
   *          a list with the files to compress
   * @param archive
   *          the archive file name and path
   * @return true if successful, false otherwise
   */
  public final boolean
      compressFiles( List< String > files, String archive )
  {
    
    BufferedOutputStream out;
    boolean result = false;
    try
    {
      // create output stream for destination file
      FileOutputStream fileOutputStream =
          new FileOutputStream( FileUtils.fileFromPath( archive ) );
      
      out = new BufferedOutputStream( fileOutputStream );
      result = strategy.compress( files, out );
      out.close();
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
    return result;
  }
  
  /**
   * Getter for the compression strategy depending archive type file extension
   * 
   * @return the archive type file extension
   */
  public final String getArchiveExtension()
  {
    return strategy.getArchiveExtension();
  }
}
