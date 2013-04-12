package de.uni_koblenz.west.mobile_sensing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Servlet implementation class LivegovServlet
 */
@WebServlet("/upload")
public class Upload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	PreparedStatement pstmtInsertDeviceInfo;
	PreparedStatement pstmtInsertSample;
	long connected;
	
	public class Sample {		
		public String uuid;
		public String sensorid;
		public long ts;
		public int prio;
		public int synced;
		public String loc;
		public String data;
		public String dataclass;
	}
		
	public class DeviceInfo {
		public	String uuid;
		public	String device;
		public	String fingerprint;
		public	String id;
		public	String manufacturer;
		public	String model;
		public	String product;
		public	String androidVersion;
	}

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			// Load the JDBC driver 
			String driverName = "com.mysql.jdbc.Driver"; // MySQL Connector 
			Class.forName(driverName).newInstance(); 
	
		} catch (ClassNotFoundException e) {
			throw new UnavailableException(
					"DedicatedConnection.init() ClassNotFoundException: "
							+ e.getMessage());
		} catch (IllegalAccessException e) {
			throw new UnavailableException(
					"DedicatedConnection.init() IllegalAccessException: "
							+ e.getMessage());
		} catch (InstantiationException e) {
			throw new UnavailableException(
					"DedicatedConnection.init() InstantiationException: "
							+ e.getMessage());
		}

		try {
			// establish a connection
			String url = "jdbc:mysql://localhost/liveandgov?useUnicode=yes&characterEncoding=UTF-8"; // a JDBC url 
			connection = DriverManager.getConnection(url, "chrisschaefer", "00chrisschaefer00");
//			String url = "jdbc:mysql://localhost/wiki?useUnicode=yes&characterEncoding=UTF-8"; // a JDBC url 
//			connection = DriverManager.getConnection(url, "root", "123456");
			connected = System.currentTimeMillis();
			Statement stmtLink = connection.createStatement();

			String createSampleTable =
					"create table if not exists samples ( "
							+ "uuid text not null, "
							+ "sensorid text not null, "
							+ "ts bigint not null, "
							+ "prio integer not null, "
							+ "synced integer default null, "
							+ "loc text, " // location can be null!
							+ "data text not null, "
							+ "dataclass text not null )";

			stmtLink.execute(createSampleTable);
			
			String createDevInfoTable =
					"create table if not exists devinfo ( "
							+ "uuid text not null, "
							+ "device text, "
							+ "fingerprint text, "
							+ "id text, "
							+ "manufacturer text, "
							+ "model text, "
							+ "product text, "
							+ "androidVersion text)";

			stmtLink.execute(createDevInfoTable);
			
			String insertSample = "INSERT INTO samples ("
					+ "uuid, "
					+ "sensorid, "
					+ "ts, "
					+ "prio, "
					+ "synced, "
					+ "loc, " // location can be null!
					+ "data, "
					+ "dataclass" 
					+ ") VALUES (?,?,?,?,?,?,?,?)";
			pstmtInsertSample = connection.prepareStatement(insertSample);
			
			String insertDevice = "INSERT INTO devinfo (" 
					+ "uuid, "
					+ "device, "
					+ "fingerprint, "
					+ "id, "
					+ "manufacturer, "
					+ "model, "
					+ "product, "
					+ "androidVersion"
					+ ") VALUES (?,?,?,?,?,?,?,?)";
			pstmtInsertDeviceInfo = connection.prepareStatement(insertDevice);

		} catch (SQLException e) {
			throw new UnavailableException(
					"DedicatedConnection.init() SQLException: "
							+ e.getMessage());
		}
	}
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Upload() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String filename = request.getHeader("filename");
		String uuid = request.getHeader("uuid");
		Date date = new Date();
		DateFormat dateformat = DateFormat.getDateInstance(DateFormat.MEDIUM);
		DateFormat timeformat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
		String datetimestring = dateformat.format(date)+"_"+timeformat.format(date);
		String absoluteFilename = "/srv/liveandgov/uploads/"+uuid+"_"+datetimestring+"_"+filename;
		File outfile = new File(absoluteFilename);
		OutputStream outstream = new FileOutputStream(outfile);
		InputStream instream = request.getInputStream();
		copyStream(instream, outstream);
		outstream.flush();
		outstream.close();
		
		this.loadDataInDatabase(absoluteFilename);
	}

	private void copyStream(InputStream input, OutputStream output)
			throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead;
		while ((bytesRead = input.read(buffer)) != -1) {
			output.write(buffer, 0, bytesRead);
		}
	}

	public void loadDataInDatabase(String filename) {

		try {
			File file = new File(filename);
			ZipFile zipFile = new ZipFile(file);
			ZipEntry zipEntry = zipFile.getEntry("devinfo.xml");
			if (zipEntry != null) {
				DeviceInfo deviceInfo = this.readDevInfoXML( zipFile.getInputStream(zipEntry));
				this.insertDeviceInfo(deviceInfo);

				zipEntry = zipFile.getEntry("samples.xml");
				if (zipEntry != null) {
					
					this.insertSamples(this.readSamplesXML(deviceInfo.uuid, zipFile.getInputStream(zipEntry)));
				}
			}
			zipFile.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File " + filename + "not found in function Upload/loadDataInDatabase");
		}
		catch (IOException e) {
			System.out.println("IO error in function Upload/loadDataInDatabase");
		}
	}
	

	private List<Sample> readSamplesXML(String uuid, InputStream in) {
		List<Sample> samples = new ArrayList<Sample>();
		try {
			// First create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// Setup a new eventReader
			XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
			// Read the XML document    
			Sample sample = new Sample();
			boolean insideDataTag = false;
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();

				if (event.isStartElement()) {
					StartElement startElement = event.asStartElement();
					if (startElement.getName().getLocalPart() == "sample") {	
						sample = new Sample();
						sample.uuid = uuid;
						sample.data = "";
						Iterator<Attribute> attributes = startElement.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals("id")) {
								sample.sensorid = attribute.getValue();
							}
							else if (attribute.getName().toString().equals("synced")) {
								if(attribute.getValue().toString().equals("true")) {
									sample.synced = 1;
								}
								else {
									sample.synced = 0;
								}
							}
							else if (attribute.getName().toString().equals("ts")) {
								sample.ts = Long.parseLong(attribute.getValue());
							}
							else if (attribute.getName().toString().equals("prio")) {
								sample.prio = Integer.parseInt(attribute.getValue().toString());
							}
						}		
					}
					if (startElement.asStartElement().getName().getLocalPart().equals("data")) {
						Iterator<Attribute> attributes = startElement.getAttributes();
						while (attributes.hasNext()) {
							Attribute attribute = attributes.next();
							if (attribute.getName().toString().equals("class")) {
								sample.dataclass = attribute.getValue();
							}
						}
						insideDataTag = true;
						continue;
					}
				}
				if(insideDataTag) {
					if (event.isEndElement() && event.asEndElement().getName().getLocalPart().equals("data")) {							
						insideDataTag = false;
						samples.add(sample);
					}
					else {
						sample.data += event.toString().trim();
					}
				}
			}
			eventReader.close();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}
		
		return samples;		
	}
	 
	private DeviceInfo readDevInfoXML(InputStream in) {
		DeviceInfo deviceInfo = new DeviceInfo();

		    try {
		      // First create a new XMLInputFactory
		      XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		      // Setup a new eventReader
		      XMLEventReader eventReader = inputFactory.createXMLEventReader(in);
		      // Read the XML document    

		      while (eventReader.hasNext()) {
		        XMLEvent event = eventReader.nextEvent();

		        if (event.isStartElement()) {
		          if (event.isStartElement()) {
		            if (event.asStartElement().getName().getLocalPart()
		                .equals("device")) {
		              event = eventReader.nextEvent();
		              deviceInfo.device = event.asCharacters().getData();
		              continue;
		            }
		          }
		          if (event.asStartElement().getName().getLocalPart()
		              .equals("fingerprint")) {
		            event = eventReader.nextEvent();
		            deviceInfo.fingerprint = event.asCharacters().getData();
		            continue;
		          }

		          if (event.asStartElement().getName().getLocalPart()
		              .equals("id")) {
		            event = eventReader.nextEvent();
		            deviceInfo.id = event.asCharacters().getData();
		            continue;
		          }

		          if (event.asStartElement().getName().getLocalPart()
		              .equals("manufacturer")) {
		            event = eventReader.nextEvent();
		            deviceInfo.manufacturer = event.asCharacters().getData();
		            continue;
		          }
		          if (event.asStartElement().getName().getLocalPart()
			              .equals("model")) {
			            event = eventReader.nextEvent();
			            deviceInfo.model = event.asCharacters().getData();
			            continue;
			          }
		          if (event.asStartElement().getName().getLocalPart()
			              .equals("product")) {
			            event = eventReader.nextEvent();
			            deviceInfo.product = event.asCharacters().getData();
			            continue;
			          }
		          if (event.asStartElement().getName().getLocalPart()
			              .equals("androidVersion")) {
			            event = eventReader.nextEvent();
			            deviceInfo.androidVersion = event.asCharacters().getData();
			            continue;
			          }
		          if (event.asStartElement().getName().getLocalPart()
			              .equals("uuid")) {
			            event = eventReader.nextEvent();
			            deviceInfo.uuid = event.asCharacters().getData();
			            continue;
			          }
		        }
		      }
		    } catch (XMLStreamException e) {
		      e.printStackTrace();
		    }
			return deviceInfo;		
	}
	private void insertSamples(List<Sample> Samples) {
		try {		  
	        for (Sample sample : Samples) {	            
	            pstmtInsertSample.setString(1, sample.uuid);
	            pstmtInsertSample.setString(2, sample.sensorid);
	            pstmtInsertSample.setLong(3, sample.ts);
	            pstmtInsertSample.setInt(4, sample.prio);
	            pstmtInsertSample.setInt(5, sample.synced);
	    		pstmtInsertSample.setString(6, sample.loc);
	    		pstmtInsertSample.setString(7, sample.data);
	    		pstmtInsertSample.setString(8, sample.dataclass);       

	    		pstmtInsertSample.addBatch();
	        }
	        pstmtInsertSample.executeBatch();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void insertDeviceInfo(DeviceInfo deviceInfo) {

		try {		  
			pstmtInsertDeviceInfo.setString(1, deviceInfo.uuid);
			pstmtInsertDeviceInfo.setString(2, deviceInfo.device);
			pstmtInsertDeviceInfo.setString(3, deviceInfo.fingerprint);
			pstmtInsertDeviceInfo.setString(4, deviceInfo.id);
			pstmtInsertDeviceInfo.setString(5, deviceInfo.manufacturer);
			pstmtInsertDeviceInfo.setString(6, deviceInfo.model);
			pstmtInsertDeviceInfo.setString(7, deviceInfo.product);
			pstmtInsertDeviceInfo.setString(8, deviceInfo.androidVersion);
			pstmtInsertDeviceInfo.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void destroy() {
		    // close the connection
		    if (connection != null)
		      try {
		        connection.close();
		      } catch (SQLException ignore) {
		      }
		  }
}