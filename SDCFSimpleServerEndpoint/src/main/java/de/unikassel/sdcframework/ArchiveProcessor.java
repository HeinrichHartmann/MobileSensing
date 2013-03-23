package de.unikassel.sdcframework;

import java.io.File;
import java.security.InvalidParameterException;
import java.security.PrivateKey;
import java.sql.SQLException;

import de.unikassel.android.sdcframework.util.FileUtils;
import de.unikassel.android.sdcframework.util.facade.Encryption;

/**
 * SDF framework archive extractor.
 * 
 * @author Katy Hilgenberg
 * 
 */
public class ArchiveProcessor implements Runnable
{
	/**
	 * The log helper class
	 */
	private final LogUtil log;

	/**
	 * The private key for decryption
	 */
	private final PrivateKey key;

	/**
	 * The working directory
	 */
	private final File workdir;

	/**
	 * Constructor
	 * 
	 * @param keyFile
	 *            the private key file
	 * 
	 * @param path
	 *            the working directory path
	 */
	public ArchiveProcessor( File keyFile, String path )
	{
		this.log = new LogUtil( this.getClass() );
		if ( !keyFile.exists() )
		{
			throw new InvalidParameterException(
					"rsa private key file does not exist!" );
		}
		this.key = Encryption.readPrivateKeyFromFile( keyFile );
		this.workdir = new File( path );
		if( !workdir.exists() || !workdir.isDirectory() )
		{
			throw new InvalidParameterException(
					"invalid working directory!" );
		}
	}

	/**
	 * Constructor
	 * 
	 * @param path
	 *            the working directory path
	 */
	public ArchiveProcessor( String path )
	{
		this.log = new LogUtil( this.getClass() );
		this.key = null;
		this.workdir = new File( path );
		if( !workdir.exists() || !workdir.isDirectory() )
		{
			throw new InvalidParameterException(
					"invalid working directory!" );
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		int cnt = 0;
		
		// new log file will be created -> mark older files for transfer
        for ( File file : workdir.listFiles() )
        {
        	if( file.isDirectory() ) continue;
        	try
    		{
        		String xmlFile = process( file );
				if ( xmlFile != null )
				{
					if ( new File( xmlFile ).exists() )
					{
						// in case of success ... delete archive
						file.delete();
					}
					cnt++;
					log.info( "Successfully processed archive: "
							+ file.getName() );
				}
				else
				{
					log.error( "Failed to process file "
							+ file.getName() );
				}
    		}
    		catch ( Exception e )
    		{
    			log.error( "Failed to query new data from mesurement table. Reason: "
    					+ e.getMessage() );
    		}
    		finally
    		{
    		}
        }
        
		log.info( "Processed " + cnt + " archives!" );
	}

	/**
	 * Does process the current record in a result set
	 * 
	 * @param file
	 *            the actual file to process
	 * @return the path to the extracted raw data XML file
	 * @throws SQLException
	 */
	public String process( File file ) throws SQLException
	{
		// The extraction process depends on the file format
		String name = FileUtils.fileNameFromPath( file.getAbsolutePath() );
		int pos = name.lastIndexOf( '.' );
		String format = name.substring( pos + 1 );
		String archiveFile = file.getAbsolutePath();

		if ( "octet-stream".equals( format ) )
		{
			// decrypt rsa encrypted archives
			String newArchiveFile = decrypt( archiveFile );
			archiveFile = newArchiveFile;
		}

		// process the archive
		return process( archiveFile );
	}

	/**
	 * Methods to extract archive content
	 * 
	 * @param archiveFile
	 *            the archive file
	 * @return the path to the extracted raw data XML file
	 */
	private String process( String archiveFile )
	{
		if ( archiveFile == null )
			return null;

		// remove file extension from filename
		int extensionPos = archiveFile.lastIndexOf( '.' );
		String rawFileName = archiveFile.substring( 0, extensionPos );

		// create directories
		int uuidEndPos = rawFileName.lastIndexOf( '-' );
		StringBuffer path = new StringBuffer( rawFileName.substring( 0,
				uuidEndPos ) );

		// uuid directory
		File dir = new File( path.toString() );
		if ( !dir.exists() && !dir.mkdir() )
		{
			log.debug( "Failed to create device directory \"" + path.toString()
					+ "\"" );
			return null;
		}
		path.append( File.separatorChar );
		path.append( rawFileName.substring( uuidEndPos + 1, extensionPos ) );

		// time stamp subdirectory
		dir = new File( path.toString() );
		if ( !dir.exists() && !dir.mkdir() )
		{
			log.debug( "Failed to create timestamp directory \""
					+ path.toString() + "\"" );
			return null;
		}

		// store path as return value
		String rawDataPath = dir.getPath();

		// extract files
		File archive = new File( archiveFile );
		ArchiveContent content = ArchiveContent.createFromArchive( archive,
				rawDataPath );

		// clean up temporary files
		File tmpFile = new File( rawFileName + ".tmp" );
		if ( tmpFile.exists() )
		{
			tmpFile.delete();
		}

		if ( content != null )
		{
			// store serialized raw data
			path.append( File.separatorChar );
			path.append( SDCFRawData.FILE_NAME );
			SDCFRawData rawData = content.getRawData();
			String xmlFile = path.toString();
			try
			{
				if ( FileUtils.writeToTextFile( SDCFRawData.toXML( rawData ),
						xmlFile ) )
				{
					return xmlFile;
				}
				log.debug( "Failed to store serialized raw data to \""
						+ xmlFile + "\". Reason: IO Error" );
			}
			catch ( Exception e )
			{
				log.debug( "Failed to store serialized raw data to \""
						+ xmlFile + "\". Reason: " + e.getMessage() );
			}
		}
		else
		{
			log.debug( "Failed to extract archive content to \"" + rawDataPath
					+ "\"" );
		}
		return null;
	}

	/**
	 * Does decrypt an archive file
	 * 
	 * @param archiveFile
	 *            the archive file
	 * @return the decrypted files name
	 */
	private String decrypt( String archiveFile )
	{
		
		String filename = archiveFile.substring( 0,
				archiveFile.lastIndexOf( '.' ) );
		String decryptedFile = filename + ".tmp";
		File destFile = new File( decryptedFile );

		if ( key != null &&
				Encryption.decryptRSA( key, new File( archiveFile ), destFile ) )
		{
			log.debug( "Decryption of file \"" + archiveFile + "\" done." );
		}
		else
		{
			log.error( "Decryption failed for file \"" + archiveFile + "\"" );
			if ( destFile.exists() )
			{
				destFile.delete();
			}
			decryptedFile = null;
		}
		return decryptedFile;
	}
}
