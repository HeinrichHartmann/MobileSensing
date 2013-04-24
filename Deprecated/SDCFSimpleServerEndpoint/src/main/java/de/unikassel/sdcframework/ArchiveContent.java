package de.unikassel.sdcframework;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.unikassel.android.sdcframework.data.independent.BasicSample;
import de.unikassel.android.sdcframework.data.independent.BasicSampleCollection;
import de.unikassel.android.sdcframework.data.independent.DeviceInformation;
import de.unikassel.android.sdcframework.data.independent.FileReferenceSampleData;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SampleData;
import de.unikassel.android.sdcframework.util.FileUtils;

/**
 * Utility class to read SDC Archive content. <br/>
 * <br/>
 * To use it, you need the simple-xml framework library (>= Version 2.5.3) and
 * the sdcframework_independent library.
 * 
 * @author Katy Hilgenberg
 */
public class ArchiveContent
{

	/**
	 * The log helper class
	 */
	private final static LogUtil log = new LogUtil( ArchiveContent.class );

	/**
	 * The raw SDCF data
	 */
	private final SDCFRawData rawData;

	/**
	 * The file map ( name to location mapping )
	 */
	private final Map< String, String > mapFiles = new HashMap< String, String >();

	/**
	 * Constructor
	 */
	private ArchiveContent()
	{
		rawData = new SDCFRawData();
	}

	/**
	 * Getter for the raw SDCF data
	 * 
	 * @return the raw data
	 */
	public SDCFRawData getRawData()
	{
		return rawData;
	}

	/**
	 * Getter for the file map
	 * 
	 * @return the file map
	 */
	public Map< String, String > getMapFiles()
	{
		return mapFiles;
	}

	/**
	 * Does create a SDC content instance from the archive content
	 * 
	 * @param archive
	 *            the archive file
	 * @param audioFilePath
	 *            the directory to store the files in
	 * @return the SDC content of the files in the archive
	 */
	public static ArchiveContent createFromArchive( File archive,
			String audioFilePath )
	{
		ArchiveContent content = new ArchiveContent();
		ZipInputStream zipStream = null;
		if ( !audioFilePath.endsWith( File.separator ) )
			audioFilePath += File.separatorChar;

		try
		{
			zipStream = new ZipInputStream( new BufferedInputStream(
					new FileInputStream( archive ) ) );

			ZipEntry entry = zipStream.getNextEntry();
			while ( entry != null )
			{
				String srcFileName = entry.getName();
				if ( srcFileName
						.equals( BasicSampleCollection.SAMPLE_COLLECTION_FILE ) )
				{
					// deserialize the device information
					content.getRawData()
							.setSampleCollection(
									GlobalSerializer
											.fromXML(
													BasicSampleCollection.class,
													FileUtils
															.readTextStreamContent( zipStream ) ) );
				}
				else if ( srcFileName
						.equals( DeviceInformation.DEVICE_INFO_FILE ) )
				{
					// deserialize the samples information
					content.getRawData()
							.setDeviceInfo(
									GlobalSerializer.fromXML(
											DeviceInformation.class,
											FileUtils
													.readTextStreamContent( zipStream ) ) );
				}
				else
				{
					// extract files map the storage path
					String destFileName = audioFilePath + srcFileName;
					content.getMapFiles().put( srcFileName, destFileName );

					OutputStream outStream = new BufferedOutputStream(
							new FileOutputStream( destFileName ) );
					try
					{
						byte[] buffer = new byte[1024];

						int count = zipStream.read( buffer );
						while ( count != -1 )
						{
							outStream.write( buffer, 0, count );
							count = zipStream.read( buffer );
						}
					}
					finally
					{
						outStream.close();
					}
				}
				entry = zipStream.getNextEntry();
			}
		}
		catch ( Exception e )
		{
			content = null;
			log.error( "Failed to extract archive content from file \""
					+ archive + "\"" );
		}
		finally
		{
			if ( zipStream != null )
			{
				try
				{
					zipStream.close();
				}
				catch ( IOException e )
				{}
			}
		}

		if ( content != null )
		{
			// update the related file path to the new location entries in the
			// file reference sample data
			for ( BasicSample sample : content.getRawData()
					.getSampleCollection().getSamples() )
			{
				SampleData data = sample.getData();
				if ( data instanceof FileReferenceSampleData )
				{
					FileReferenceSampleData fileRefData = (FileReferenceSampleData) data;
					String fileName = content.getMapFiles().get(
							fileRefData.getRelatedData() );
					fileRefData.updateRelatedData( fileName );
				}
			}
			content.getMapFiles().clear();
		}
		return content;
	}
}