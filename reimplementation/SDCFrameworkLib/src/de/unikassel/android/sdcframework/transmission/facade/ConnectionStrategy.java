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

import de.unikassel.android.sdcframework.util.facade.ChainWorker;

/**
 * Interface for connection strategy types. <br/>
 * <br/>
 * A connection strategy is configured for the
 * {@linkplain de.unikassel.android.sdcframework.transmission.UploadManager
 * upload manager} to control the kind of Internet access. <br/>
 * Any connection strategy is a worker in a chain of strategies. If a strategy
 * has a successor and the strategy fails, it will call its successor to try
 * another connection strategy.
 * 
 * @author Katy Hilgenberg
 * 
 */
public interface ConnectionStrategy
    extends ChainWorker< ProtocolStrategy >
{
  /**
   * Method to test for an available connection
   * 
   * @param protocolStrategy
   *          the upload strategy
   * 
   * @return true if a connection is available, false otherwise
   */
  public abstract boolean isConnectionAvailable(
      ProtocolStrategy protocolStrategy );
}
