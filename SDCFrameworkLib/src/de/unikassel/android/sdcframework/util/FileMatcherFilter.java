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

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A file name filter using a matcher for regular filename expressions.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class FileMatcherFilter implements FilenameFilter
{
  /**
   * The pattern.
   */
  private final Pattern pattern;
  
  /**
   * Constructor
   * 
   * @param pattern
   */
  public FileMatcherFilter( String pattern )
  {
    super();
    this.pattern =
        Pattern.compile( pattern, Pattern.UNICODE_CASE
            | Pattern.CASE_INSENSITIVE );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
   */
  @Override
  public boolean accept( File dir, String name )
  {
    if ( new File( dir, name ).isDirectory() ) return true;
    Matcher m = pattern.matcher( name );
    return m.find();
  }  
}
