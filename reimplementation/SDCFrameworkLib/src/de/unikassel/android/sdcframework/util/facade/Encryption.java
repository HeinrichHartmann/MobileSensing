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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Utility class for encryption and checksums.
 * 
 * @author Katy Hilgenberg
 * 
 */
public final class Encryption
{
  /**
   * private key file name
   */
  public static final String PRIVATE_KEY_FILE = "private.key";
  
  /**
   * public key file name
   */
  public static final String PUBLIC_KEY_FILE = "public.key";
  
  /**
   * The RSA key length
   */
  private static final int RSA_KEY_LENGTH = 2048;
  
  /**
   * The RSA algorithm description
   */
  private static final String RSA_ALGORITHM = "RSA";
  
  /**
   * The RSA transformation description for encryption
   */
  private static final String RSA_ENCRYPTION = RSA_ALGORITHM
      + "/ECB/PKCS1Padding";
  
  /**
   * Method to create an MD5 hash for a given string
   * 
   * @param text
   *          the string to create MD5 hash for
   * @return the MD5 hash for the text
   */
  public static final String md5( String text )
  {
    String hash = null;
    try
    {
      MessageDigest messageDigest =
          java.security.MessageDigest.getInstance( "MD5" );
      messageDigest.update( text.getBytes() );
      byte digest[] = messageDigest.digest();
      
      StringBuffer hexString = new StringBuffer();
      for ( int i = 0; i < digest.length; i++ )
      {
        String hexDigest = Integer.toHexString( 0xFF & digest[ i ] );
        if ( hexDigest.length() < 2 )
          hexString.append( '0' );
        hexString.append( hexDigest );
      }
      hash = hexString.toString();
    }
    catch ( NoSuchAlgorithmException e )
    {
      e.printStackTrace();
    }
    return hash;
  }
  
  /**
   * Method for RSA file encryption
   * 
   * @param key
   *          the public key to use for encryption
   * @param srcFile
   *          the source file to encrypt
   * @param destFile
   *          the file to store encrypted data in
   * @return true if successful, false otherwise
   */
  public static final boolean encryptRSA( PublicKey key, File srcFile,
      File destFile )
  {
    BufferedInputStream is = null;
    BufferedOutputStream os = null;
    boolean result = false;
    try
    {
      Cipher cipher = Cipher.getInstance( RSA_ENCRYPTION );
      cipher.init( Cipher.ENCRYPT_MODE, key );
      is = new BufferedInputStream( new FileInputStream( srcFile ) );
      os = new BufferedOutputStream( new FileOutputStream( destFile ) );
      byte[] bytes = new byte[ RSA_KEY_LENGTH / 8 - 11 ];
      int i = is.read( bytes );
      while ( i != -1 )
      {
        byte[] encBytes = cipher.doFinal( bytes, 0, i );
        os.write( encBytes );
        i = is.read( bytes );
      }
      os.flush();
      result = true;
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if ( is != null )
        {
          is.close();
        }
        if ( os != null )
        {
          os.close();
        }
      }
      catch ( Exception e )
      {}
    }
    return result;
  }
  
  /**
   * Method for RSA file decryption
   * 
   * @param key
   *          the private key to use for decryption
   * @param srcFile
   *          the source file to decrypt
   * @param destFile
   *          the file to store decrypted data in
   * @return true if successful, false otherwise
   */
  public static final boolean decryptRSA( PrivateKey key, File srcFile,
      File destFile )
  {
    BufferedInputStream is = null;
    BufferedOutputStream os = null;
    boolean result = false;
    try
    {
      Cipher cipher = Cipher.getInstance( RSA_ENCRYPTION );
      cipher.init( Cipher.DECRYPT_MODE, key );
      is = new BufferedInputStream( new FileInputStream( srcFile ) );
      os = new BufferedOutputStream( new FileOutputStream( destFile ) );
      byte[] bytes = new byte[ RSA_KEY_LENGTH / 8 ];
      int i = is.read( bytes );
      while ( i != -1 )
      {
        byte[] encBytes = cipher.doFinal( bytes, 0, i );
        os.write( encBytes );
        i = is.read( bytes );
      }
      os.flush();
      result = true;
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    finally
    {
      try
      {
        if ( is != null )
        {
          is.close();
        }
        if ( os != null )
        {
          os.close();
        }
      }
      catch ( Exception e )
      {}
    }
    return result;
  }
  
  /**
   * Method to create an RSA key pair of RSA_KEY_LENGTH bit length and save the
   * key bytes to files.
   * 
   * @return the genrated key pair
   */
  public static final KeyPair createRSAKeyPair()
  {
    try
    {
      KeyPairGenerator generator = KeyPairGenerator.getInstance( RSA_ALGORITHM );
      generator.initialize( RSA_KEY_LENGTH );
      KeyPair keyPair = generator.genKeyPair();
      PublicKey publicKey = keyPair.getPublic();
      PrivateKey privateKey = keyPair.getPrivate();
      
      saveRSAKeyToFile( PUBLIC_KEY_FILE, publicKey.getEncoded() );
      saveRSAKeyToFile( PRIVATE_KEY_FILE, privateKey.getEncoded() );
      return keyPair;
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Method to store a public or a private RSA key bytes
   * 
   * @param file
   *          the file to save the key into
   * @param bytes
   *          the key bytes
   * @throws IOException
   */
  public static final void saveRSAKeyToFile( String file, byte[] bytes )
      throws IOException
  {
    OutputStream os = new BufferedOutputStream( new FileOutputStream( file ) );
    try
    {
      os.write( bytes );
    }
    finally
    {
      os.close();
    }
  }
  
  /**
   * Method to read a public key from file
   * 
   * @param file
   *          the file to read the public key from
   * @return the public key
   */
  public static final PublicKey readPublicKeyFromFile( File file )
  {
    PublicKey key = null;
    try
    {
      byte[] bytes = readRSAKeyFromFile( file );
      key =
          KeyFactory.getInstance( RSA_ALGORITHM ).generatePublic(
              new X509EncodedKeySpec( bytes ) );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return key;
  }
  
  /**
   * Method to read a public key from an input stream
   * 
   * @param is
   *          the input stream to read from
   * @return the public key
   */
  public static final PublicKey readPublicKeyFromStream( InputStream is )
  {
    PublicKey key = null;
    try
    {
      byte[] bytes = readRSAKeyFromStream( is );
      key = KeyFactory.getInstance( RSA_ALGORITHM ).generatePublic(
              new X509EncodedKeySpec( bytes ) );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return key;
  }
  
  /**
   * Method to read a private key from file
   * 
   * @param file
   *          the file to read the private key from
   * @return the private key
   */
  public static final PrivateKey readPrivateKeyFromFile( File file )
  {
    PrivateKey key = null;
    try
    {
      byte[] bytes = readRSAKeyFromFile( file );
      key =
          KeyFactory.getInstance( RSA_ALGORITHM ).generatePrivate(
              new PKCS8EncodedKeySpec( bytes ) );
    }
    catch ( Exception e )
    {
      e.printStackTrace();
    }
    return key;
  }
  
  /**
   * Method to store a public or a private RSA key bytes
   * 
   * @param file
   *          the file to read the public key from
   * @return the key bytes
   * @throws IOException
   */
  private static final byte[] readRSAKeyFromFile( File file )
      throws IOException
  {
    long length = file.length();
    if ( length > 0 )
    {
      return readRSAKeyFromStream( new BufferedInputStream(
          new FileInputStream( file ) ) );
    }
    return null;
  }
  
  /**
   * Method to store a public or a private RSA key bytes
   * 
   * @param is
   *          the input stream to read the public key from
   * @return the key bytes
   * @throws IOException
   */
  public static final byte[] readRSAKeyFromStream( InputStream is )
      throws IOException
  {
    ByteArrayOutputStream os = new ByteArrayOutputStream();
    byte[] buffer = new byte[ 1024 ];
    try
    {
      int i = is.read( buffer );
      while ( i != -1 )
      {
        os.write( buffer, 0, i );
        i = is.read( buffer );
      }
    }
    finally
    {
      is.close();
      os.flush();
    }
    return os.toByteArray();
  }
}
