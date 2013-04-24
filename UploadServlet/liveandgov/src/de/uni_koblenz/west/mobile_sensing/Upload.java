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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * Servlet implementation class LivegovServlet
 */

public class Upload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DataSource database;

	PreparedStatement pstmtInsertDeviceInfo;

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
	
	String insertDevice = "INSERT INTO devinfo (" 
			+ "uuid, "
			+ "textuuid, "
			+ "device, "
			+ "fingerprint, "
			+ "id, "
			+ "manufacturer, "
			+ "model, "
			+ "product, "
			+ "androidVersion"
			+ ") VALUES (?,?,?,?,?,?,?,?,?)";
	
	public class Sample {
		public int uuid;
		public String sensorid;
		public long ts;
		public int prio;
		public int synced;
		public String loc;
		public String data;
		public String dataclass;
	}
		
	public class DeviceInfo {
		public int uuid;
		public	String textuuid;
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
		Connection connection = null;
		Statement stmtLink = null;
		Context envContext;
		try {
			envContext = new InitialContext();
			database = (DataSource)envContext.lookup("java:/comp/env/jdbc/liveandgov");

			connection = database.getConnection();
			stmtLink = connection.createStatement();

			String createDevInfoTable =
					"create table if not exists devinfo ( "
							+ "uuid integer not null, "
							+ "textuuid text not null, "
							+ "device text, "
							+ "fingerprint text, "
							+ "id text, "
							+ "manufacturer text, "
							+ "model text, "
							+ "product text, "
							+ "androidVersion text, "
							+ "PRIMARY KEY (uuid) )";

			stmtLink.execute(createDevInfoTable);
			
			String createSampleTable =
					"create table if not exists samples ( "
					        + "uuid integer not null, "
							+ "sensorid text not null, "
							+ "ts bigint not null, "
							+ "prio integer not null, "
							+ "synced integer default null, "
							+ "loc text, " // location can be null!
							+ "data text not null, "
							+ "dataclass text not null, "
							+ "FOREIGN KEY (uuid) REFERENCES devinfo(uuid) )";

			stmtLink.execute(createSampleTable);			

		} catch (SQLException e) {
			throw new UnavailableException(e.getMessage());
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		} finally {
			try { if (stmtLink != null) stmtLink.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
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
		if(request.getParameter("logfile") == null) {
			this.loadDataInDatabase(absoluteFilename);
		}
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
				deviceInfo = this.insertDeviceInfo(deviceInfo);

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
	

	private List<Sample> readSamplesXML(int uuid, InputStream in) {
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
			            deviceInfo.textuuid = event.asCharacters().getData();
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
		PreparedStatement pstmtInsertSample = null;
		Connection connection = null;
		try {
			connection = database.getConnection();
			pstmtInsertSample = connection.prepareStatement(insertSample);
			
	        for (Sample sample : Samples) {
	        	pstmtInsertSample.setInt(1, sample.uuid);
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
		} finally {
			try { if (pstmtInsertSample != null) pstmtInsertSample.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
		
	}
	private DeviceInfo insertDeviceInfo(DeviceInfo deviceInfo) {
		PreparedStatement pstmtInsertDeviceInfo = null;
		PreparedStatement pstmtCheckDeviceInfo = null;
		PreparedStatement pstmtGetMaxIdDeviceInfo = null;
		Connection connection = null;
		ResultSet rs = null;
		try {		
			connection = database.getConnection();			
						
			pstmtCheckDeviceInfo = connection.prepareStatement(""
					+ "SELECT uuid "
					+ "FROM   devinfo "
					+ "WHERE  textuuid = ?");			
			pstmtCheckDeviceInfo.setString(1, deviceInfo.textuuid);
			rs = pstmtCheckDeviceInfo.executeQuery();
			
			boolean deviceExists = false;
			while (rs.next()) {
				// the device exists
				deviceInfo.uuid = rs.getInt(1);
				deviceExists = true;
				break;
			}
			
			if(!deviceExists) {
				pstmtGetMaxIdDeviceInfo	= connection.prepareStatement("SELECT Max(uuid) FROM devinfo");	
				rs = pstmtGetMaxIdDeviceInfo.executeQuery();
				while (rs.next()) {
					deviceInfo.uuid = rs.getInt(1) + 1;
					break;
				}
							
				pstmtInsertDeviceInfo = connection.prepareStatement(insertDevice);
				
				pstmtInsertDeviceInfo.setInt(1, deviceInfo.uuid);
				pstmtInsertDeviceInfo.setString(2, deviceInfo.textuuid);
				pstmtInsertDeviceInfo.setString(3, deviceInfo.device);
				pstmtInsertDeviceInfo.setString(4, deviceInfo.fingerprint);
				pstmtInsertDeviceInfo.setString(5, deviceInfo.id);
				pstmtInsertDeviceInfo.setString(6, deviceInfo.manufacturer);
				pstmtInsertDeviceInfo.setString(7, deviceInfo.model);
				pstmtInsertDeviceInfo.setString(8, deviceInfo.product);
				pstmtInsertDeviceInfo.setString(9, deviceInfo.androidVersion);
				pstmtInsertDeviceInfo.execute();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (pstmtCheckDeviceInfo != null) pstmtCheckDeviceInfo.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (pstmtGetMaxIdDeviceInfo != null) pstmtGetMaxIdDeviceInfo.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (pstmtInsertDeviceInfo != null) pstmtInsertDeviceInfo.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
		return deviceInfo;
	}
	
}