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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * A static utility class providing functions for IO and file operations.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class FileUtils
{
  /**
   * The default buffer size
   */
  public static final int DEFAULT_BUFFER_SIZE = 1024;
  
  /**
   * Method to extract the filename from a path
   * 
   * @param pathToFile
   *          the path to the file
   * @return the raw filename without path but with extension
   */
  public static final String fileNameFromPath( String pathToFile )
  {
    int pos = pathToFile.lastIndexOf( File.separatorChar );
    return pathToFile.substring( pos + 1 );
  }
  
  /**
   * Method to create a file object from a path to a file
   * 
   * @param pathToFile
   *          the path to the file
   * @return a file object
   */
  public static final File fileFromPath( String pathToFile )
  {
    // Remark:
    // Android does not allow the construction of a file from a filename
    // containing a path!
    int pos = pathToFile.lastIndexOf( File.separatorChar );
    String path = pos < 0 ? "." : pathToFile.substring( 0, pos );
    String pureFileName = pathToFile.substring( pos + 1 );
    
    // create file object
    File file = new File( path, pureFileName );
    return file;
  }
  
  /**
   * Method to delete a file
   * 
   * @param filename
   *          the file name and path to delete
   * @return true if successful, false otherwise
   */
  public static final boolean deleteFile( String filename )
  {
    File file = fileFromPath( filename );
    return file.delete();
  }
  
  /**
   * Method to copy one file to another
   * 
   * @param sourceFilename
   *          the source file name and path
   * @param destFilename
   *          the destination file name and path
   * @return true if successful, false otherwise
   */
  public static final boolean copy( String sourceFilename, String destFilename )
  {
    return copy( fileFromPath( sourceFilename ), fileFromPath( destFilename ) );
  }
  
  /**
   * Method to copy a file from an input stream to another file
   * 
   * @param sourceStream
   *          the source file stream
   * @param destFilename
   *          the destination file name and path
   * @return true if successful, false otherwise
   */
  public static final boolean
      copy( FileInputStream sourceStream, String destFilename )
  {
    try
    {
      return copy( sourceStream, new FileOutputStream(
          fileFromPath( destFilename ) ) );
    }
    catch ( FileNotFoundException e )
    {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Method to copy one file to another
   * 
   * @param source
   *          the source file
   * @param dest
   *          the destination file
   * @return true if successful, false otherwise
   */
  public static final boolean copy( File source, File dest )
  {
    try
    {
      return copy( new FileInputStream( source ), new FileOutputStream( dest ) );
    }
    catch ( FileNotFoundException e )
    {
      e.printStackTrace();
    }
    return false;
  }
  
  /**
   * Method to copy one file stream to another
   * 
   * @param sourceStream
   *          the source file stream
   * @param destStream
   *          the destination file stream
   * @return true if successful, false otherwise
   */
  public static final boolean copy( FileInputStream sourceStream,
      FileOutputStream destStream )
  {
    boolean success = false;
    FileChannel inChannel = null;
    FileChannel outChannel = null;
    
    try
    {
      inChannel = sourceStream.getChannel();
      outChannel = destStream.getChannel();
      inChannel.transferTo( 0, inChannel.size(), outChannel );
      success = true;
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    finally
    {
      if ( inChannel != null && inChannel.isOpen() )
      {
        try
        {
          inChannel.close();
        }
        catch ( IOException e )
        {}
      }
      if ( outChannel != null && outChannel.isOpen() )
      {
        try
        {
          outChannel.close();
        }
        catch ( IOException e )
        {}
      }
    }
    return success;
  }
  
  /**
   * Does write text into a file
   * 
   * @param text
   *          the text to write
   * @param fileName
   *          the filename
   * @return the file content as string
   */
  public static final boolean writeToTextFile( String text, String fileName )
  {
    boolean result = false;
    Reader reader = new StringReader( text );
    Writer writer = null;
    try
    {
      char[] buffer = new char[ DEFAULT_BUFFER_SIZE ];
      writer =
          new BufferedWriter(
              new OutputStreamWriter( new FileOutputStream(
                  fileFromPath( fileName ) ), "UTF-8" ) );
      int count = reader.read( buffer );
      while ( count != -1 )
      {
        writer.write( buffer, 0, count );
        count = reader.read( buffer );
      }
      writer.flush();
      result = true;
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
    finally
    {
      if ( writer != null )
      {
        try
        {
          writer.close();
        }
        catch ( IOException e )
        {}
      }
    }
    return result;
  }
  
  /**
   * Does read the content of a text file
   * 
   * @param fileName
   *          the filename
   * @return the file content as string
   */
  public static final String readTextFileContent( String fileName )
  {
    String result = null;
    FileInputStream in = null;
    try
    {
      in = new FileInputStream( fileFromPath( fileName ) );
      result = readTextStreamContent( in );
      in.close();
    }
    catch ( IOException e )
    {
      e.printStackTrace();
    }
    finally
    {
      if ( in != null )
      {
        try
        {
          in.close();
        }
        catch ( IOException e )
        {}
      }
    }
    return result;
  }
  
  /**
   * Method to read input from text stream into a string
   * 
   * @param in
   *          the input stream
   * @return the string content from input stream
   */
  public static final String readTextStreamContent( InputStream in )
  {
    Writer writer = new StringWriter();
    Reader reader = null;
    
    try
    {
      char[] buffer = new char[ DEFAULT_BUFFER_SIZE ];
      reader = new BufferedReader(
                      new InputStreamReader( in, "UTF-8" ) );
      int count = reader.read( buffer );
      while ( count != -1 )
      {
        writer.write( buffer, 0, count );
        count = reader.read( buffer );
      }
      writer.flush();
      return writer.toString();
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Does read the content of a text file from an archive stream if the file
   * is contained in the archive
   * 
   * @param zipStream
   *          the archive stream
   * @param fileName
   *          the filename in archive to read
   * @return the uncompressed content of the file in the archive
   */
  public static final String readTextFileContentFromArchive(
      ZipInputStream zipStream, String fileName )
  {
    String rawFileName = fileNameFromPath( fileName );
    String result = null;
    try
    {
      ZipEntry entry = zipStream.getNextEntry();
      while ( entry != null )
      {
        if ( entry.getName().equals( rawFileName ) )
        {
          result = FileUtils.readTextStreamContent( zipStream );
          break;
        }
        entry = zipStream.getNextEntry();
      }
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    finally
    {
      if ( zipStream != null )
      {
        try
        {
          zipStream.close();
        }
        catch ( IOException e )
        {}
      }
    }
    return result;
  }
}
