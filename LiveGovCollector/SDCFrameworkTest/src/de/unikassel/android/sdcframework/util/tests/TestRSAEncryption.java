package de.unikassel.android.sdcframework.util.tests;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

import android.test.suitebuilder.annotation.Suppress;

import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.facade.Encryption;
import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * The SDCFrameworkTest project.
 */

/**
 * RSA Encryption unit tests.
 * 
 * @author Katy Hilgenberg
 * 
 */
@Suppress
public class TestRSAEncryption extends TestCase
{
  
  /**
   * The decryption destination file for encryption test
   */
  private static final String DEC_FILE = "decrypted.xml";
  
  /**
   * The rsa file for encryption test
   */
  private static final String RSA_FILE = "encrypted.rsa";
  
  /**
   * The src file for encryption test
   */
  private static final String SRC_FILE = "samples.xml";
  
  /**
   * The src file for archive encryption test
   */
  private static final String ARCHIVE_FILE = "assets/archive.zip";
  
  /**
   * The decryption destination file for archive encryption test
   */
  private static final String DEC_ARCHIVE_FILE = "decrypted.zip";
  
  /*
   * (non-Javadoc)
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    cleanUp();
    super.setUp();
  }
  
  /**
   * Method to clean up befor and after testrun
   */
  private void cleanUp()
  {
    if ( FileUtils.fileFromPath( DEC_ARCHIVE_FILE ).exists() )
    {
      FileUtils.deleteFile( DEC_ARCHIVE_FILE );
    }
    if ( FileUtils.fileFromPath( DEC_FILE ).exists() )
    {
      FileUtils.deleteFile( DEC_FILE );
    }
    if ( FileUtils.fileFromPath( RSA_FILE ).exists() )
    {
      FileUtils.deleteFile( RSA_FILE );
    }
    if ( FileUtils.fileFromPath( Encryption.PRIVATE_KEY_FILE ).exists() )
    {
      FileUtils.deleteFile( Encryption.PRIVATE_KEY_FILE );
    }
    if ( FileUtils.fileFromPath( Encryption.PUBLIC_KEY_FILE ).exists() )
    {
      FileUtils.deleteFile( Encryption.PUBLIC_KEY_FILE );
    }
  }
  
  /**
   * RSA Encryption test
   */
  public final void testRSAEncryption()
  {
    // test key generation, storage to file and reload from file
    KeyPair keypair = Encryption.createRSAKeyPair();
    assertNotNull( "Expected key pair created", keypair );
    
    PublicKey pub = keypair.getPublic();
    assertNotNull( "Expected public key created", pub );
    PrivateKey priv = keypair.getPrivate();
    assertNotNull( "Expected private key created", priv );
    
    PublicKey pubFromFile =
        Encryption.readPublicKeyFromFile(
            FileUtils.fileFromPath( Encryption.PUBLIC_KEY_FILE ) );
    assertNotNull( "Expected public key stored in file", pubFromFile );
    PrivateKey privFromFile =
        Encryption.readPrivateKeyFromFile(
            FileUtils.fileFromPath( Encryption.PRIVATE_KEY_FILE ) );
    assertNotNull( "Expected private key stored in file", privFromFile );
    assertEquals( "Expected public key is equal", pub, pubFromFile );
    assertEquals( "Expected private key is equal", priv, privFromFile );
    
    // test encryption and decryption
    assertTrue( "Expected successful encryption", Encryption.encryptRSA(
        pubFromFile, FileUtils.fileFromPath( SRC_FILE ),
        FileUtils.fileFromPath( RSA_FILE ) ) );
    assertTrue( "Expected successful decryption", Encryption.decryptRSA(
        privFromFile, FileUtils.fileFromPath( RSA_FILE ),
        FileUtils.fileFromPath( DEC_FILE ) ) );
    String srcFileContent = FileUtils.readTextFileContent( SRC_FILE );
    String decFileContent = FileUtils.readTextFileContent( DEC_FILE );
    assertEquals( "Expected same file content", srcFileContent, decFileContent );
    cleanUp();
  }
  
  /**
   * RSA Encryption test an binary archive
   */
  public final void testBinaryRSAEncryption()
  {
    // test key generation, storage to file and reload from file
    KeyPair keypair = Encryption.createRSAKeyPair();
    assertNotNull( "Expected key pair created", keypair );
    
    PublicKey pub = keypair.getPublic();
    assertNotNull( "Expected public key created", pub );
    PrivateKey priv = keypair.getPrivate();
    assertNotNull( "Expected private key created", priv );
    
    PublicKey pubFromFile =
        Encryption.readPublicKeyFromFile(
            FileUtils.fileFromPath( Encryption.PUBLIC_KEY_FILE ) );
    assertNotNull( "Expected public key stored in file", pubFromFile );
    PrivateKey privFromFile =
        Encryption.readPrivateKeyFromFile(
            FileUtils.fileFromPath( Encryption.PRIVATE_KEY_FILE ) );
    assertNotNull( "Expected private key stored in file", privFromFile );
    assertEquals( "Expected public key is equal", pub, pubFromFile );
    assertEquals( "Expected private key is equal", priv, privFromFile );
    
    // test encryption and decryption
    assertTrue( "Expected successful encryption", Encryption.encryptRSA(
        pubFromFile, FileUtils.fileFromPath( ARCHIVE_FILE ),
        FileUtils.fileFromPath( RSA_FILE ) ) );
    assertTrue( "Expected successful decryption", Encryption.decryptRSA(
        privFromFile, FileUtils.fileFromPath( RSA_FILE ),
        FileUtils.fileFromPath( DEC_ARCHIVE_FILE ) ) );
    
    File srcFile = FileUtils.fileFromPath( ARCHIVE_FILE );
    File decFile = FileUtils.fileFromPath( DEC_ARCHIVE_FILE );
    assertEquals( "Expected same file size", srcFile.length(), decFile.length() );
    assertBinaryEquals( "Expected same file", srcFile, decFile );
    cleanUp();
  }
  
  /**
   * Method to binary compare two files ( test method is copied from the
   * junit-addon project, to avoid adding a library. Refer to:
   * http://junit-addons.sourceforge.net/)
   * 
   * @param message
   *          the message
   * @param expected
   *          reference file
   * @param actual
   *          file to test
   * @throws AssertionFailedError
   */
  public final static void assertBinaryEquals( String message, File expected, File actual )
      throws AssertionFailedError
  {
    FileInputStream isExpected = null;
    FileInputStream isActual = null;
    
    try
    {
      try
      {
        isExpected = new FileInputStream( expected );
        isActual = new FileInputStream( actual );
        
        assertNotNull( message, expected );
        assertNotNull( message, actual );
        
        byte[] expBuff = new byte[ 8192 ];
        byte[] actBuff = new byte[ 8192 ];
        
        long pos = 0;
        while ( true )
        {
          int expLength = isExpected.read( expBuff, 0, 8192 );
          int actLength = isActual.read( actBuff, 0, 8192 );
          
          if ( expLength < actLength )
          {
            fail( "actual file is longer" );
          }
          if ( expLength > actLength )
          {
            fail( "actual file is shorter" );
          }
          
          if ( expLength == 0 )
          {
            return;
          }
          
          for ( int i = 0; i < expBuff.length; ++i )
          {
            if ( expBuff[ i ] != actBuff[ i ] )
            {
              String formatted = "";
              if ( message != null )
              {
                formatted = message + " ";
              }
              fail( formatted + "files differ at byte " + ( pos + i + 1 ) );
            }
          }
          
          pos += expBuff.length;
          return;
        }
      }
      finally
      {
        isExpected.close();
        isActual.close();
      }
    }
    catch ( IOException e )
    {
      throw new AssertionFailedError( e.getMessage() );
    }
  }
}
