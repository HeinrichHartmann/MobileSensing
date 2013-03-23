package de.unikassel.sdcframework;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import de.unikassel.android.sdcframework.data.independent.BasicSampleCollection;
import de.unikassel.android.sdcframework.data.independent.DeviceInformation;
import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.util.FileUtils;

/**
 * Serializable data type for raw SDC data. <br/>
 * <br/>
 * To use it, you need the simple-xml framework library (>= Version 2.5.3) and
 * the sdcframework_independent library.
 * 
 * @author Katy Hilgenberg
 */
@Root( name = "sdcRawData" )
public class SDCFRawData
{

	/**
	 * The filename for the serialized type
	 */
	public final static String FILE_NAME = "SDCFRawData.xml";

	/**
	 * The device information
	 */
	@Element( name = "deviceInfo", required = true )
	private DeviceInformation deviceInfo;

	/**
	 * The transmitted samples
	 */
	@Element( name = "sampleCollection", required = true )
	private BasicSampleCollection sampleCollection;

	/**
	 * Constructor
	 */
	public SDCFRawData()
	{}

	/**
	 * Getter for the device information
	 * 
	 * @return the device information
	 */
	public DeviceInformation getDeviceInfo()
	{
		return deviceInfo;
	}

	/**
	 * Setter for the device information
	 * 
	 * @param deviceInfo
	 *            the device information to set
	 */
	public void setDeviceInfo( DeviceInformation deviceInfo )
	{
		this.deviceInfo = deviceInfo;
	}

	/**
	 * Getter for the sample collection
	 * 
	 * @return the sample collection
	 */
	public BasicSampleCollection getSampleCollection()
	{
		return sampleCollection;
	}

	/**
	 * Setter for the sample collection
	 * 
	 * @param samples
	 *            the sample collection to set
	 */
	public void setSampleCollection( BasicSampleCollection samples )
	{
		this.sampleCollection = samples;
	}

	/**
	 * Method to create SDCF raw data from an XML File
	 * 
	 * @param fileName
	 *            the XML file name
	 * @return the deserialized raw data
	 * @throws Exception
	 */
	public static SDCFRawData fromXML( String fileName ) throws Exception
	{
		return GlobalSerializer.fromXML( SDCFRawData.class,
				FileUtils.readTextFileContent( fileName ) );
	}

	/**
	 * Method to create an XML representation of raw data
	 * 
	 * @param rawData
	 *            the raw data
	 * @return the XML representation of the raw data
	 * @throws Exception
	 */
	public static String toXML( SDCFRawData rawData ) throws Exception
	{
		return GlobalSerializer.toXml( rawData );
	}
}