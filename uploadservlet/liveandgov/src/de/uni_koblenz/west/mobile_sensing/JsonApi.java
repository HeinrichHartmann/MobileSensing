package de.uni_koblenz.west.mobile_sensing;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class JsonApi
 */
@WebServlet("/JsonApi")
public class JsonApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection connection;
	long connected;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JsonApi() {
        super();
        // TODO Auto-generated constructor stub
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

		} catch (SQLException e) {
			throw new UnavailableException(
					"DedicatedConnection.init() SQLException: "
							+ e.getMessage());
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		String sensorid = request.getParameter("sensorid");
		if(sensorid.equals("GPS")) {
			writeGPSData(request,response);
		}
		// TODO
	}

	private void writeGPSData(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		try {
			String selectSQL = "SELECT DISTINCT data FROM samples WHERE sensorid = 'GPS' AND uuid = ? AND ts BETWEEN ? AND ? ORDER BY ts LIMIT 1000";
			PreparedStatement pstmtGPS = connection.prepareStatement(selectSQL);
			pstmtGPS.setString(1, request.getParameter("uuid"));
			pstmtGPS.setLong(2, Long.parseLong(request.getParameter("tsFrom")));
			pstmtGPS.setLong(3, Long.parseLong(request.getParameter("tsTo")));
			ResultSet rs = pstmtGPS.executeQuery();
			
			PrintWriter writer = response.getWriter();
			boolean firstPair = true;
			writer.print("{\"data\":[");
			while (rs.next()) {
				String data = rs.getString("data");
				Pattern regexLat = Pattern.compile("<lat>(.*?)</lat>", Pattern.DOTALL);
				Pattern regexLon = Pattern.compile("<lon>(.*?)</lon>", Pattern.DOTALL);
				Matcher matcher = regexLat.matcher(data);	
				if (matcher.find()) {
				    String lat = matcher.group(1);
				    if(firstPair) {
					    writer.print("[" + lat + ",");
					    firstPair = false;
				    }
				    else {
				    	writer.print(",[" + lat + ",");				    	
				    }
				}
				matcher = regexLon.matcher(data);				
				if (matcher.find()) {
				    String lon = matcher.group(1);
				    writer.print(lon + "]");
				}
			}
			writer.print("]}");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
