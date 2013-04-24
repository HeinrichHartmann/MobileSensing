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
package de.unikassel.android.sdcframework.util.facade;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Interface for compression strategiy types.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface CompressionStrategy
{
  /**
   * The compression method
   * 
   * @param files
   *          a list with the files to compress
   * @param out
   *          the output stream to compress files into
   * @return true if successful, false otherwise
   * @throws IOException
   *           if closing the output stream fails
   */
  public abstract boolean compress( List< String > files,
      BufferedOutputStream out ) throws IOException;
  
  /**
   * Getter for an archive file extension of this strategy
   * 
   * @return the archive file extension of this strategy
   */
  public abstract String getArchiveExtension();
  
}
