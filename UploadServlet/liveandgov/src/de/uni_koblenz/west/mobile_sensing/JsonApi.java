package de.uni_koblenz.west.mobile_sensing;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class JsonApi
 */

public class JsonApi extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DataSource database;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JsonApi() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init(ServletConfig config) throws ServletException {
		super.init(config);
		Context envContext;
		try {
			envContext = new InitialContext();
			database = (DataSource)envContext.lookup("java:/comp/env/jdbc/liveandgov");
		} catch (NamingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		
		String sensorid = request.getParameter("sensorid");
		if(sensorid != null) {
			if(sensorid.equals("GPS")) {
				writeGPSData(request,response);
			}
			else if(sensorid.equals("Tags")) {
				writeTagsData(request,response);
			}
		}
		if(request.getParameter("overview") != null) {
			writeOverviewData(request,response);
		}
		// TODO
	}

	private void writeTagsData(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String selectAllTags = ""
				+ "SELECT ts, "
				+ "       data "
				+ "FROM   samples "
				+ "WHERE  sensorid = 'Tags' "
				+ "       AND uuid = ? "
				+ "       AND ts BETWEEN ? AND ? "
				+ "ORDER  BY ts "
				+ "LIMIT  1000";

		String selectNearestCoord = ""
				+ "SELECT Abs(ts - ?) AS a, "
				+ "       data "
				+ "FROM   samples "
				+ "WHERE  sensorid = 'GPS' "
				+ "       AND uuid = ? "
				+ "       AND ts BETWEEN ? AND ? "
				+ "ORDER  BY a "
				+ "LIMIT  1";
//		String allInOne = ""
//				+ "SELECT s1.ts AS ts1, "
//				+ "       s1.data, "
//				+ "       (SELECT data "
//				+ "        FROM   samples "
//				+ "        WHERE  sensorid = 'GPS' "
//				+ "        ORDER  BY Abs(ts1 - ts) "
//				+ "        LIMIT  1) "
//				+ "FROM   samples s1 "
//				+ "WHERE  s1.sensorid = 'Tags'"
//				+ "       AND uuid = ? "
//				+ "       AND ts BETWEEN ? AND ? ";
		
		Connection connection = null;
		PreparedStatement pstmtAllTags = null;
		ResultSet rs = null;
		PreparedStatement pstmtNearestCoord = null;
		ResultSet rs2 = null;
		
		try {
			connection = database.getConnection();
			pstmtAllTags = connection.prepareStatement(selectAllTags);
			pstmtAllTags.setInt(1, Integer.parseInt(request.getParameter("uuid")));
			pstmtAllTags.setLong(2, Long.parseLong(request.getParameter("tsFrom")));
			pstmtAllTags.setLong(3, Long.parseLong(request.getParameter("tsTo")));
	
			pstmtNearestCoord = connection.prepareStatement(selectNearestCoord);
			
			rs = pstmtAllTags.executeQuery();
			PrintWriter writer = response.getWriter();
			boolean firstPair = true;
			writer.print("{\"data\":[");
			while (rs.next()) {
				if(firstPair) {
					firstPair = false;
				}
				else {
					writer.print(",");
				}
				writer.print("{\"tag\":\"" + tagXmlToJson(rs.getString("data")) + "\"");
				writer.print(",\"ts\":" + rs.getLong("ts"));
				
				// find the nearest GPS coordinate to localize the tag
				pstmtNearestCoord.setLong(1, rs.getLong("ts"));
				pstmtNearestCoord.setInt(2, Integer.parseInt(request.getParameter("uuid")));
				pstmtNearestCoord.setLong(3, Long.parseLong(request.getParameter("tsFrom")));
				pstmtNearestCoord.setLong(4, Long.parseLong(request.getParameter("tsTo")));
				rs2 = pstmtNearestCoord.executeQuery();
				while (rs2.next()) {
					writer.print(",\"latlon\":" + latLonXmlToJson(rs2.getString("data")) + "}");
				}				
			}
			writer.print("]}");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (rs2 != null) rs2.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (pstmtAllTags != null) pstmtAllTags.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (pstmtNearestCoord != null) pstmtNearestCoord.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
	}

	private void writeOverviewData(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String selectAllTags = ""
				+ "SELECT Count(*), "
				+ "       s.sensorid, "
				+ "       d.uuid, "
				+ "       d.textuuid, "
				+ "       d.model, "
				+ "       Min(s.ts), "
				+ "       Max(s.ts) "
				+ "FROM   samples s "
				+ "       JOIN devinfo d "
				+ "         ON s.uuid = d.uuid "
				+ "GROUP  BY d.uuid, "
				+ "          d.textuuid, "
				+ "          d.model, "
				+ "          s.sensorid";
		
		Connection connection = null;
		PreparedStatement pstmtAllTags = null;
		ResultSet rs = null;
		try {
			connection = database.getConnection();
			pstmtAllTags = connection.prepareStatement(selectAllTags);		
			rs = pstmtAllTags.executeQuery();
			PrintWriter writer = response.getWriter();
			boolean firstPair = true;
			writer.print("{\"data\":[");
			while (rs.next()) {
				if(firstPair) {
					firstPair = false;
				}
				else {
					writer.print(",");
				}
				writer.print("{\"count\":" + rs.getInt(1));
				writer.print(",\"sensorid\":\"" + rs.getString(2) + "\"");
				writer.print(",\"uuid\":" + rs.getInt(3));
				writer.print(",\"textuuid\":\"" + rs.getString(4) + "\"");
				writer.print(",\"model\":\"" + rs.getString(5) + "\"");
				writer.print(",\"min\":" + rs.getLong(6));
				writer.print(",\"max\":" + rs.getLong(7) + "}");				
			}
			writer.print("]}");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (pstmtAllTags != null) pstmtAllTags.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
	}
	
	
	private String tagXmlToJson(String xml){
		// data looks like <txt>37GD</txt>
		int tagLength = xml.length();
		return xml.substring(5,tagLength-6);
	}

	private void writeGPSData(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		
		String selectSQL = ""
				+ "SELECT DISTINCT data "
				+ "FROM   samples "
				+ "WHERE  sensorid = 'GPS' "
				+ "       AND uuid = ? "
				+ "       AND ts BETWEEN ? AND ? "
				+ "ORDER  BY ts "
				+ "LIMIT  1000 ";
		Connection connection = null;
		PreparedStatement pstmtGPS = null;
		ResultSet rs = null;
		try {
			connection = database.getConnection();
			pstmtGPS = connection.prepareStatement(selectSQL);
			pstmtGPS.setInt(1, Integer.parseInt(request.getParameter("uuid")));
			pstmtGPS.setLong(2, Long.parseLong(request.getParameter("tsFrom")));
			pstmtGPS.setLong(3, Long.parseLong(request.getParameter("tsTo")));
			rs = pstmtGPS.executeQuery();
			
			PrintWriter writer = response.getWriter();
			boolean firstPair = true;
			writer.print("{\"data\":[");
			while (rs.next()) {
				String data = rs.getString("data");
				if(firstPair) {
					writer.print(latLonXmlToJson(data));
					firstPair = false;
				}
				else {
					writer.print("," + latLonXmlToJson(data));
				}
			}
			writer.print("]}");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (pstmtGPS != null) pstmtGPS.close(); } catch (SQLException e) { e.printStackTrace(); }
			try { if (connection != null) connection.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
	}

	private String latLonXmlToJson(String data) {
		Pattern regexLat = Pattern.compile("<lat>(.*?)</lat>", Pattern.DOTALL);
		Pattern regexLon = Pattern.compile("<lon>(.*?)</lon>", Pattern.DOTALL);
		Matcher matcher = regexLat.matcher(data);	
		String result = "";
		if (matcher.find()) {
		    String lat = matcher.group(1);
			result += ("[" + lat + ",");
		}
		else {
			return "";
		}
		matcher = regexLon.matcher(data);				
		if (matcher.find()) {
		    String lon = matcher.group(1);
		    result += (lon + "]");
		}
		else {
			return "";
		}
		return result;
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
}
