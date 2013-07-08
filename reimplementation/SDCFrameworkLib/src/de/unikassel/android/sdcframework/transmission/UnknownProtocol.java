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
package de.unikassel.android.sdcframework.transmission;

import java.io.File;
import java.util.UUID;

import android.content.Context;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolConfiguration;

/**
 * Implementation of a default behavior for unknown protocol types. <br/>
 * <br/>
 * This protocol will just raise an URL error.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class UnknownProtocol
    extends AbstractProtocol
{
  
  /**
   * Constructor
   * 
   * @param context
   *          the context
   * @param uuid
   *          the unique SDC installation identifier for this device
   * @param config
   *          the current transmission configuration
   */
  public UnknownProtocol( Context context,  UUID uuid,
      TransmissionProtocolConfiguration config )
  {
    super( context, uuid, config );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.transmission.AbstractProtocol#
   * doUploadFromStream(java.io.File)
   */
  @Override
  protected boolean doUploadFile( File file )
  {
    doHandleError( AbstractProtocol.UNKNOWN_PROTCOL );
    return false;
  }
  
}
