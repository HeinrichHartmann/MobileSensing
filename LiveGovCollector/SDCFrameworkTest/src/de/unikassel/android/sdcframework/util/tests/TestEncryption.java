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
package de.unikassel.android.sdcframework.util.tests;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PrivateKey;
import java.security.PublicKey;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.test.InstrumentationTestCase;

import de.unikassel.android.sdcframework.test.TestUtils;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.facade.Encryption;
import junit.framework.AssertionFailedError;

/**
 * Tests for the encryption class.
 * 
 * @author Katy Hilgenberg
 *
 */
public class TestEncryption 
extends InstrumentationTestCase
{
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception
  {
    super.setUp();
  }

  /* (non-Javadoc)
   * @see junit.framework.TestCase#tearDown()
   */
  @Override
  protected void tearDown() throws Exception
  {
    cleanUp();
    super.tearDown();
  }

  /**
   * The rsa file for encryption test
   */
  private static final String RSA_FILE = "encrypted.rsa";
  
  /**
   * The src file for archive encryption test
   */
  private static final String ARCHIVE_FILE = "archive.zip";
  
  /**
   * The decryption destination file for archive encryption test
   */
  private static final String DEC_ARCHIVE_FILE = "decrypted.zip";
  
  /**
   * Test method for {@link de.unikassel.android.sdcframework.util.facade.Encryption#md5(java.lang.String)}.
   */
  public final void testMd5()
  {
    assertEquals( "Unexpected md5 hash", "d41d8cd98f00b204e9800998ecf8427e",
        Encryption.md5( "" ) );
    assertEquals( "Unexpected md5 hash", "9e107d9d372bb6826bd81d3542a419d6",
        Encryption.md5( "The quick brown fox jumps over the lazy dog" ) );
    assertEquals( "Unexpected md5 hash", "e4d909c290d0fb1ca068ffaddf22cbd0",
        Encryption.md5( "The quick brown fox jumps over the lazy dog." ) );
  }
  
  /**
   * Test method for the RSA encryption
   */
  public final void testRSA()
  {
    Context targetContext = getInstrumentation().getTargetContext();
    Resources resources = getInstrumentation().getContext().getResources();
    AssetManager assetManager = resources.getAssets();
    
    String path = targetContext.getFilesDir().getAbsolutePath() + File.separator;
    File archiveFile = null;
    File pubKeyFile = null;
    File privKeyFile = null;
    try
    {
      pubKeyFile = FileUtils.fileFromPath( path + Encryption.PUBLIC_KEY_FILE );
      TestUtils.copyAssetFile( assetManager, Encryption.PUBLIC_KEY_FILE, new FileOutputStream(
          pubKeyFile ) );
      assertTrue( "Expected public key file copied", pubKeyFile.exists() );

      privKeyFile = FileUtils.fileFromPath( path + Encryption.PRIVATE_KEY_FILE );
      TestUtils.copyAssetFile( assetManager, Encryption.PRIVATE_KEY_FILE, new FileOutputStream(
          privKeyFile ) );
      assertTrue( "Expected private key file copied", privKeyFile.exists() );
      
      archiveFile = FileUtils.fileFromPath( path + ARCHIVE_FILE );
      TestUtils.copyAssetFile( assetManager, ARCHIVE_FILE, new FileOutputStream(
          archiveFile ) );
      assertTrue( "Expected archive file copied", archiveFile.exists() );
    }
    catch ( Exception e )
    {
      fail("Unexpected exception " + e.getMessage() );
      e.printStackTrace();
    }
    
    // test key import
    PublicKey pubFromFile = Encryption.readPublicKeyFromFile( pubKeyFile );
    assertNotNull( "Expected public key loaded from file", pubFromFile );
    PrivateKey privFromFile = Encryption.readPrivateKeyFromFile( privKeyFile );
    assertNotNull( "Expected private key loaded from file", privFromFile );
    
    // test encryption and decryption
    File rsaFile = FileUtils.fileFromPath( path + RSA_FILE );
    assertTrue( "Expected successful encryption", Encryption.encryptRSA(
        pubFromFile, archiveFile, rsaFile ) );
    File decryptedArchiveFile = FileUtils.fileFromPath( path + DEC_ARCHIVE_FILE );
    assertTrue( "Expected successful decryption", Encryption.decryptRSA(
        privFromFile, rsaFile, decryptedArchiveFile ) );

    assertEquals( "Expected same file size", archiveFile.length(), decryptedArchiveFile.length() );
    assertBinaryEquals( "Expected same file", archiveFile, decryptedArchiveFile );
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
  
  /**
   * Method to clean up after test run
   */
  private void cleanUp()
  {
    if ( FileUtils.fileFromPath( DEC_ARCHIVE_FILE ).exists() )
    {
      FileUtils.deleteFile( DEC_ARCHIVE_FILE );
    }
    if ( FileUtils.fileFromPath( RSA_FILE ).exists() )
    {
      FileUtils.deleteFile( RSA_FILE );
    }
  }
}
