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
import java.security.InvalidParameterException;
import java.security.PublicKey;

import de.unikassel.android.sdcframework.util.facade.Encryption;
import de.unikassel.android.sdcframework.util.facade.FileEncryptionStrategy;

/**
 * Implementation of the RSA file encryption strategy.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class RSAFileEncryptionStrategy
    implements FileEncryptionStrategy
{
  /**
   * The public key for encryption
   */
  private final PublicKey key;
  
  /**
   * Constructor
   * 
   * @param key
   *          the public key for encryption
   */
  public RSAFileEncryptionStrategy( PublicKey key )
  {
    if ( key == null )
      throw new InvalidParameterException( "public key can not be null" );
    this.key = key;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.FileEncryptionStrategy#
   * encryptFile(java.io.File, java.io.File)
   */
  @Override
  public boolean encryptFile( File srcFile, File destFile )
  {
    return Encryption.encryptRSA( key, srcFile, destFile );
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.util.facade.FileEncryptionStrategy#
   * getAlgorithmLetterCode()
   */
  @Override
  public String getAlgorithmLetterCode()
  {
    return "rsa";
  }
  
}
