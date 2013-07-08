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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Pattern;

import de.unikassel.android.sdcframework.data.ConcreteDeviceInformation;
import de.unikassel.android.sdcframework.data.Sample;
import de.unikassel.android.sdcframework.data.SampleCollection;
import de.unikassel.android.sdcframework.data.independent.BasicSampleCollection;
import de.unikassel.android.sdcframework.data.independent.DeviceInformation;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration;
import de.unikassel.android.sdcframework.transmission.facade.UpdatableTransmissionComponent;
import de.unikassel.android.sdcframework.util.FileCompressor;
import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.JarCompressionStrategy;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.RSAFileEncryptionStrategy;
import de.unikassel.android.sdcframework.util.ZipCompressionStrategy;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;
import de.unikassel.android.sdcframework.util.facade.Encryption;
import de.unikassel.android.sdcframework.util.facade.FileEncryptionStrategy;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import android.content.Context;
import android.content.res.AssetManager;

/**
 * A file management component for the transmission service. <br/>
 * <br/>
 * Does provide the functionality to create a device information XML file as
 * well as a temporary file directory. In addition it does manage the archive
 * creation.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class FileManager
    implements UpdatableTransmissionComponent< TransmissionConfiguration >
{
  
  /**
   * The name of the temporary sub directory for files
   */
  public final static String TEMP_DIR_NAME = "tmp";
  
  /**
   * The filename for the archive without extension
   */
  public final static String ARCHIVE_FILE = "sdcarchive";
  
  /**
   * The temporary file to store serialized samples in
   */
  private final String sampleFile;
  
  /**
   * The file to store permanent device info in
   */
  private final String deviceFile;
  
  /**
   * The file path and base name without extension for archive files
   */
  private final String archiveFileName;
  
  /**
   * The file to store a new created archive in
   */
  private String currentArchive;
  
  /**
   * The file compressor used to create archives
   */
  private final FileCompressor fileCompressor;
  
  /**
   * The file encryption used to create archives
   */
  private FileEncryptionStrategy encryptionStrategy;
  
  /**
   * Constructor
   * 
   * @param applicationContext
   *          the application context
   * @param config
   *          the transmission configuration
   * @param uuid
   *          the unique device identifier created by the service
   */
  public FileManager( Context applicationContext,
      TransmissionConfiguration config, UUID uuid )
  {
    String filesPath =
        applicationContext.getFilesDir().getPath() + File.separatorChar;
    String tmpPath = filesPath + TEMP_DIR_NAME + File.separatorChar;
    this.sampleFile = tmpPath + BasicSampleCollection.SAMPLE_COLLECTION_FILE;
    this.deviceFile = filesPath + DeviceInformation.DEVICE_INFO_FILE;
    this.archiveFileName = tmpPath + ARCHIVE_FILE + ".";
    this.fileCompressor = new FileCompressor( new ZipCompressionStrategy() );
    this.encryptionStrategy = null;
    
    createTmpDirectory( tmpPath );
    createDeviceInformation( uuid );
    updateConfiguration( applicationContext, config );
  }
  
  /**
   * Getter for the current sample file name
   * 
   * @return the current sample file name
   */
  public final String getSampleFile()
  {
    return sampleFile;
  }
  
  /**
   * Getter for the current archive file name
   * 
   * @return the current archive file name
   */
  public synchronized final String getCurrentArchive()
  {
    return currentArchive;
  }
  
  /**
   * Getter for the device file name
   * 
   * @return the device file name
   */
  public final String getDeviceFile()
  {
    return deviceFile;
  }
  
  /**
   * Getter for the fileCompressor
   * 
   * @return the file ompressor
   */
  public final FileCompressor getFileCompressor()
  {
    return fileCompressor;
  }
  
  /**
   * Does create the temporary directory if it does not exist
   * 
   * @param dir
   *          the absolute temporary directory filename
   */
  private void createTmpDirectory( String dir )
  {
    File tmpDirectory = FileUtils.fileFromPath( dir );
    if ( !tmpDirectory.exists() )
    {
      if ( !tmpDirectory.mkdir() )
      {
        Logger.getInstance().error( this,
            "Failed to create directory for temporary files!" );
      }
    }
    else
    {
      testForExistingArchive( dir );
    }
  }
  
  /**
   * Does create the device information file to be added to the sample
   * collection archive file ( if it does not exist )
   * 
   * @param uuid
   *          the unique device identifier created by the service
   */
  private void createDeviceInformation( UUID uuid )
  {
    DeviceInformation deviceInformation =
        new ConcreteDeviceInformation( uuid.toString() );
    boolean doUpdate = true;
    
    if ( FileUtils.fileFromPath( deviceFile ).exists() )
    {
      // compare for device information changes
      try
      {
        String sContent = FileUtils.readTextFileContent( deviceFile );
        DeviceInformation deviceInfo =
            GlobalSerializer.fromXML( DeviceInformation.class,
                sContent );
        doUpdate = !deviceInformation.equals( deviceInfo );
        System.out.println( sContent );
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this,
            "Failed to open stored device information file!" );
        e.printStackTrace();
      }
    }
    
    if ( doUpdate )
    {
      try
      {
        FileUtils.writeToTextFile( deviceInformation.toXML(), deviceFile );
        Logger.getInstance().debug( this,
            "Updated device information:\n" + deviceInformation.toXML() );
      }
      catch ( Exception e )
      {
        Logger.getInstance().error( this,
            "Failed to store device information file!" );
        e.printStackTrace();
      }
    }
  }
  
  /**
   * Does create a new archive containing the device description file and the
   * XML file with the serialized sample collection
   * 
   * @param samples
   *          the sample collection
   * @return true if successful, false otherwise
   */
  public synchronized boolean createArchive( SampleCollection samples )
  {
    currentArchive = null;
    
    try
    {
      List< String > files = new Vector< String >();
      // test for file referencing samples
      SampleCollection samplesWithFiles = new SampleCollection();
      for ( Sample sample : samples )
      {
        if ( sample.getRelatedData() != null )
        {
          samplesWithFiles.add( sample );
        }
      }
      
      for ( Sample sample : samplesWithFiles )
      {
        // test for file availability
        File relatedFile = FileUtils.fileFromPath( sample.getRelatedData() );
        if ( !relatedFile.exists() || !relatedFile.isFile() )
        {
          // remove sample from the collection for transmission
          samples.remove( sample );
          Logger.getInstance().error( this,
              "Related file not found: " + relatedFile.getAbsolutePath() +
                  ". Skipping sample!" );
        }
        else
        {
          // add file path to the file transfer list
          files.add( relatedFile.getAbsolutePath() );
          // change file reference entry for the sample to the file name without
          // path
          String fileName =
              FileUtils.fileNameFromPath( sample.getRelatedData() );
          sample.updateRelatedData( fileName );
        }
      }
      
      // create the new archive
      GlobalSerializer.serializeToFile( samples,
          FileUtils.fileFromPath( sampleFile ) );
      files.add( deviceFile );
      files.add( sampleFile );
      
      currentArchive = createArchiveWithFiles( files );
      
      // optionally apply encryption
      currentArchive = encryptArchive( currentArchive );
      
    }
    catch ( Exception e )
    {
      Logger.getInstance().error( this,
          "Failed to create temporary sample file!" );
      e.printStackTrace();
      // if we fail, -> clean up any temporary archive
      deleteFile( currentArchive );
      currentArchive = null;
    }
    finally
    {
      // do always clean up sample file
      deleteFile( sampleFile );
    }
    
    return currentArchive != null;
  }
  
  /**
   * Method to encrypt the archive if configured
   * 
   * @param sArchive
   *          the archive to encrypt
   * @return the encrypted archive name, or null in case of errors
   */
  private String encryptArchive( String sArchive )
  {
    String tmpArchive = null;
    
    // if configured, encrypt archive for transmission
    if ( encryptionStrategy != null )
    {
      tmpArchive =
          archiveFileName + encryptionStrategy.getAlgorithmLetterCode();
      
      if ( !encryptionStrategy.encryptFile( FileUtils.fileFromPath( sArchive ),
          FileUtils.fileFromPath( tmpArchive ) ) )
      {
        Logger.getInstance().error( this, "Failed to encrypt the archive file!" );
        deleteFile( tmpArchive );
        tmpArchive = null;
      }
      
      // do always delete the unencrypted archive
      deleteFile( sArchive );
      return tmpArchive;
    }
    
    return sArchive;
  }
  
  /**
   * Method to create an archive from files
   * 
   * @param files
   *          the files to add
   * @return the created archive file path
   */
  private String createArchiveWithFiles( List< String > files )
  {
    String sArchive = archiveFileName + fileCompressor.getArchiveExtension();
    
    if ( fileCompressor.compressFiles( files, sArchive ) )
    {
      if ( Logger.getInstance().getLogLevel().equals( LogLevel.DEBUG ) )
      {
        Long sumOriginalSizes = 0L;
        for ( String fileName : files )
        {
          sumOriginalSizes += FileUtils.fileFromPath( fileName ).length();
        }
        long size = FileUtils.fileFromPath( sArchive ).length();
        // calculate compression ratio
        float ratio = 100.F;
        ratio -=
              ( sumOriginalSizes > 0 ? ( size * 100.F / sumOriginalSizes )
                  : 100.F );
        Logger.getInstance().debug(
            this,
              String.format( "archive size: %d bytes, ratio %.2f%%", size,
                  ratio ) );
      }
      return sArchive;
    }
    
    // if we fail -> clean up
    Logger.getInstance().error( this, "Failed to create archive file!" );
    deleteFile( sArchive );
    return null;
  }
  
  /**
   * Test method for existing archives
   * 
   * @return true if an archive exists, false otherwise
   */
  public synchronized boolean hasArchive()
  {
    return currentArchive != null;
  }
  
  /**
   * Does check for an existing archive file
   * 
   * @param dir
   *          the absolute temporary directory filename
   */
  private void testForExistingArchive( String dir )
  {
    currentArchive = null;
    
    File tmpDir = FileUtils.fileFromPath( dir );
    if ( tmpDir.exists() && tmpDir.isDirectory() )
    {
      // create a matcher for file name tests
      Pattern fileMatcher = Pattern.compile( ".*" + ARCHIVE_FILE + ".*" );
      
      for ( File file : tmpDir.listFiles() )
      {
        if ( fileMatcher.matcher( file.getName() ).matches() )
        {
          currentArchive = file.getAbsolutePath();
          break;
        }
      }
      
    }
  }
  
  /**
   * Does cleanup any temporary files stored
   * 
   * @param deleteArchive
   *          flag if archive file shall be deleted as well
   */
  public synchronized void doCleanUp( boolean deleteArchive )
  {
    deleteFile( sampleFile );
    
    if ( deleteArchive )
    {
      deleteFile( currentArchive );
      currentArchive = null;
    }
  }
  
  /**
   * Does cleanup any temporary files stored
   */
  private void deleteFile( String fileName )
  {
    if ( fileName != null && FileUtils.fileFromPath( fileName ).exists() )
    {
      if ( !FileUtils.deleteFile( fileName ) )
      {
        Logger.getInstance().warning( this, "Failed to delete file " + fileName );
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.transmission.facade.
   * UpdatableTransmissionComponent#updateConfiguration(android.content.Context,
   * de
   * .unikassel.android.sdcframework.preferences.facade.TransmissionConfiguration
   * )
   */
  @Override
  public synchronized void updateConfiguration( Context context,
      TransmissionConfiguration config )
  {
    ArchiveTypes currentType =
        fileCompressor.getStrategy() instanceof JarCompressionStrategy
            ? ArchiveTypes.jar : ArchiveTypes.zip;
    
    ArchiveTypes newType = config.getArchiveType();
    if ( !currentType.equals( newType ) )
    {
      switch ( newType )
      {
        case jar:
          fileCompressor.setStrategy( new JarCompressionStrategy() );
          break;
        
        case zip:
          fileCompressor.setStrategy( new ZipCompressionStrategy() );
          break;
      }
    }
    
    if ( config.isEncryptionEnabled() )
    {
      if ( encryptionStrategy == null )
      {
        try
        {
          PublicKey pubKey = getPubKeyFromFilesFolder( context );
          if ( pubKey == null )
          {
            pubKey = getPubKeyFromAssetFolder( context );
          }
          encryptionStrategy = new RSAFileEncryptionStrategy( pubKey );
        }
        catch ( Exception e )
        {
          Logger.getInstance().error(
              this,
              "Failed to create RSA encryption strategy. Reason: "
                  + e.getMessage() );
          e.printStackTrace();
        }
      }
    }
    else
    {
      encryptionStrategy = null;
    }
  }
  
  /**
   * Does load the public key file from the private files folder.
   * 
   * @param context
   *          the context
   * @return the public key if found, null otherwise
   * @throws IOException
   */
  protected PublicKey getPubKeyFromFilesFolder( Context context )
  {
    try
    {
      String pubKeyFile =
          context.getFilesDir().getPath() + File.separatorChar
              + Encryption.PUBLIC_KEY_FILE;
      InputStream is =
          new FileInputStream( FileUtils.fileFromPath( pubKeyFile ) );
      return Encryption.readPublicKeyFromStream( is );
    }
    catch ( FileNotFoundException fnfe )
    {}
    catch ( Exception e )
    {
      Logger.getInstance().error(
          this,
          "Failed to load public key file from files folder. Reason: "
              + e.getMessage() );
      e.printStackTrace();
    }
    return null;
  }
  
  /**
   * Does load the public key file from the asset folder.
   * 
   * @param context
   *          the context
   * @return the public key if found, null otherwise
   * @throws IOException
   */
  protected PublicKey getPubKeyFromAssetFolder( Context context )
  {
    try
    {
      AssetManager assetManager = context.getResources().getAssets();
      InputStream is = assetManager.open( Encryption.PUBLIC_KEY_FILE );
      return Encryption.readPublicKeyFromStream( is );
    }
    catch ( Exception e )
    {
      Logger.getInstance().error(
          this,
          "Failed to load public key file from asset folder. Reason: "
              + e.getMessage() );
      e.printStackTrace();
    }
    return null;
  }
}
