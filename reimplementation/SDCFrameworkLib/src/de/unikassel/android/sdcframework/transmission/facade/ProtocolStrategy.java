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
package de.unikassel.android.sdcframework.transmission.facade;

import java.net.URL;

import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;

import android.content.Context;

/**
 * Interface for protocol strategy types which do implement a special transfer
 * protocol.
 * 
 * @see de.unikassel.android.sdcframework.transmission.BasicAuthHttpProtocol
 * @author Katy Hilgenberg
 * 
 */
public interface ProtocolStrategy
    extends UpdatableTransmissionComponent<TransmissionProtocolConfiguration>
{
  /**
   * Does upload the current file set
   * 
   * @return true if successful, flase otherwise
   */
  public abstract boolean uploadFile();
  
  /**
   * Getter for the application context
   * 
   * @return the application context
   */
  public abstract Context getContext();
  
  /**
   * Getter for the URL
   * 
   * @return the URL
   */
  public abstract URL getURL();
  
  /**
   * Getter for the last error
   * 
   * @return the last error description or null if no error did occur
   */
  public abstract String getLastError();
  
  /**
   * Setter for the last error
   * 
   * @param lastError
   *          the laste error to set
   */
  public abstract void setLastError( String lastError );
  
  /**
   * Setter for the file to upload
   * 
   * @param fileName
   *          the name of the file to upload
   */
  public abstract void setFileName( String fileName );
  
  /**
   * Getter for name of the file to upload
   * 
   * @return the file to upload
   */
  public abstract String getFileName();
  
  /**
   * Getter for the host
   * 
   * @return the host if url is valid, otherwise null
   */
  public abstract String getHost();
}
