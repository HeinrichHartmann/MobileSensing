import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Database {

	private static Connection conn;

	static {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			conn = DriverManager
					.getConnection("jdbc:mysql://localhost/database?user=user&password=password");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createTables() throws SQLException {
		Statement s = conn.createStatement();
		s.execute("CREATE TABLE IF NOT EXISTS `lines`"
				+ "( id VARCHAR(7),"
				+ "date1 DATE,"
				+ " date2 DATE,"
				+ " language VARCHAR(1),"
				+ " lineName VARCHAR(60),"
				+ " terminal1Name VARCHAR(20),"
				+ " terminal2Name VARCHAR(20),"
				+ " stopCodeDir1 INTEGER,"
				+ " stopCodeDir2 INTEGER,"
				+ " lineLengthDir1 INTEGER,"
				+ " lineLengthDir2 INTEGER,"
				+ " transportMean INTEGER )");
		s.execute("CREATE TABLE IF NOT EXISTS stops"
				+ "( stopCode INTEGER,"
				+ " x_kkj2 INTEGER,"
				+ " y_kkj2 INTEGER,"
				+ " latitude FLOAT,"
				+ " longitude FLOAT,"
				+ " stopName VARCHAR(20),"
				+ " stopNameSwedish VARCHAR(20),"
				+ " address VARCHAR(20),"
				+ " addressSwedish VARCHAR(20),"
				+ " platformNumber VARCHAR(3),"
				+ " x_kkj3 INTEGER,"
				+ " y_kkj3 INTEGER,"
				+ " stopLocationAreaName VARCHAR(20),"
				+ " stopLocationAreaNameSwedish VARCHAR(20),"
				+ " shelter INTEGER,"
				+ " stopShortCode VARCHAR(6),"
				+ " x_wgs84_proj FLOAT,"
				+ " y_wgs84_proj FLOAT,"
				+ " coordMethod VARCHAR(1),"
				+ " accessibilityClass INTEGER,"
				+ " note VARCHAR(15) )");
		s.execute("CREATE TABLE IF NOT EXISTS routes"
				+ "( routeCode VARCHAR(6),"
				+ " routeDir VARCHAR(1),"
				+ " validFrom DATE,"
				+ " validTo DATE,"
				+ " stopCode INTEGER,"
				+ " type VARCHAR(1),"
				+ " stopOrder INTEGER,"
				+ " x INTEGER,"
				+ " y INTEGER )");
		s.close();
	}
	
	public static void fillLines(List<LineInfo> infos) throws SQLException {
		PreparedStatement ps = conn
				.prepareStatement("INSERT INTO `lines` VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		for (LineInfo lineInfo : infos) {
			if (lineInfo.getId() != null) {
				ps.setString(1, lineInfo.getId());
			} else {
				ps.setNull(1, java.sql.Types.VARCHAR);
			}
			if (lineInfo.getDate1() != null) {
				java.sql.Date sqldate = new java.sql.Date(lineInfo.getDate1()
						.getTime());
				ps.setDate(2, sqldate);
			} else {
				ps.setNull(2, java.sql.Types.DATE);
			}
			if (lineInfo.getDate2() != null) {
				java.sql.Date sqldate = new java.sql.Date(lineInfo.getDate2()
						.getTime());
				ps.setDate(3, sqldate);
			} else {
				ps.setNull(3, java.sql.Types.DATE);
			}
			if (lineInfo.getLanguage() != null) {
				ps.setString(4, lineInfo.getLanguage().toString());
			} else {
				ps.setNull(4, java.sql.Types.VARCHAR);
			}
			if (lineInfo.getLineName() != null) {
				ps.setString(5, lineInfo.getLineName());
			} else {
				ps.setNull(5, java.sql.Types.VARCHAR);
			}
			if (lineInfo.getTerminal1Name() != null) {
				ps.setString(6, lineInfo.getTerminal1Name());
			} else {
				ps.setNull(6, java.sql.Types.VARCHAR);
			}
			if (lineInfo.getTerminal2Name() != null) {
				ps.setString(7, lineInfo.getTerminal2Name());
			} else {
				ps.setNull(7, java.sql.Types.VARCHAR);
			}
			if (lineInfo.getStopCodeDir1() != null) {
				ps.setInt(8, lineInfo.getStopCodeDir1());
			} else {
				ps.setNull(8, java.sql.Types.INTEGER);
			}
			if (lineInfo.getStopCodeDir2() != null) {
				ps.setInt(9, lineInfo.getStopCodeDir2());
			} else {
				ps.setNull(9, java.sql.Types.INTEGER);
			}
			if (lineInfo.getLineLengthDir1() != null) {
				ps.setInt(10, lineInfo.getLineLengthDir1());
			} else {
				ps.setNull(10, java.sql.Types.INTEGER);
			}
			if (lineInfo.getLineLengthDir2() != null) {
				ps.setInt(11, lineInfo.getLineLengthDir2());
			} else {
				ps.setNull(11, java.sql.Types.INTEGER);
			}
			if (lineInfo.getTransportMean() != null) {
				ps.setInt(12, lineInfo.getTransportMean());
			} else {
				ps.setNull(12, java.sql.Types.INTEGER);
			}
			ps.addBatch();
		}
		ps.executeBatch();
		ps.close();
	}
	
	public static void fillStops(List<StopInfo> stopInfos) throws SQLException {
		PreparedStatement ps = conn
				.prepareStatement("INSERT INTO stops VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		for (StopInfo stopInfo : stopInfos) {
			if (stopInfo.getStopCode() != null) {
				ps.setInt(1, stopInfo.getStopCode());
			} else {
				ps.setNull(1, java.sql.Types.INTEGER);
			}
			if (stopInfo.getX_kkj2() != null) {
				ps.setInt(2, stopInfo.getX_kkj2());
			} else {
				ps.setNull(2, java.sql.Types.INTEGER);
			}
			if (stopInfo.getY_kkj2() != null) {
				ps.setInt(3, stopInfo.getY_kkj2());
			} else {
				ps.setNull(3, java.sql.Types.INTEGER);
			}
			if (stopInfo.getLatitude() != null) {
				ps.setFloat(4, stopInfo.getLatitude());
			} else {
				ps.setNull(4, java.sql.Types.FLOAT);
			}
			if (stopInfo.getLongitude() != null) {
				ps.setFloat(5, stopInfo.getLongitude());
			} else {
				ps.setNull(5, java.sql.Types.FLOAT);
			}
			if (stopInfo.getStopName() != null) {
				ps.setString(6, stopInfo.getStopName());
			} else {
				ps.setNull(6, java.sql.Types.VARCHAR);
			}
			if (stopInfo.getStopNameSwedish() != null) {
				ps.setString(7, stopInfo.getStopNameSwedish());
			} else {
				ps.setNull(7, java.sql.Types.VARCHAR);
			}
			if (stopInfo.getAddress() != null) {
				ps.setString(8, stopInfo.getAddress());
			} else {
				ps.setNull(8, java.sql.Types.VARCHAR);
			}
			if (stopInfo.getAddressSwedish() != null) {
				ps.setString(9, stopInfo.getAddressSwedish());
			} else {
				ps.setNull(9, java.sql.Types.VARCHAR);
			}
			if (stopInfo.getPlatformNumber() != null) {
				ps.setString(10, stopInfo.getPlatformNumber());
			} else {
				ps.setNull(10, java.sql.Types.VARCHAR);
			}
			if (stopInfo.getX_kkj3() != null) {
				ps.setInt(11, stopInfo.getX_kkj3());
			} else {
				ps.setNull(11, java.sql.Types.INTEGER);
			}
			if (stopInfo.getY_kkj3() != null) {
				ps.setInt(12, stopInfo.getY_kkj3());
			} else {
				ps.setNull(12, java.sql.Types.INTEGER);
			}
			if (stopInfo.getStopLocationAreaName() != null) {
				ps.setString(13, stopInfo.getStopLocationAreaName());
			} else {
				ps.setNull(13, java.sql.Types.VARCHAR);
			}
			if (stopInfo.getStopLocationAreaNameSwedish() != null) {
				ps.setString(14, stopInfo.getStopLocationAreaNameSwedish());
			} else {
				ps.setNull(14, java.sql.Types.VARCHAR);
			}
			if (stopInfo.getShelter() != null) {
				ps.setInt(15, stopInfo.getShelter());
			} else {
				ps.setNull(15, java.sql.Types.INTEGER);
			}
			if (stopInfo.getStopShortCode() != null) {
				ps.setString(16, stopInfo.getStopShortCode());
			} else {
				ps.setNull(16, java.sql.Types.VARCHAR);
			}
			if (stopInfo.getX_wgs84_proj() != null) {
				ps.setFloat(17, stopInfo.getX_wgs84_proj());
			} else {
				ps.setNull(17, java.sql.Types.FLOAT);
			}
			if (stopInfo.getY_wgs84_proj() != null) {
				ps.setFloat(18, stopInfo.getY_wgs84_proj());
			} else {
				ps.setNull(18, java.sql.Types.FLOAT);
			}
			if (stopInfo.getCoordMethod() != null) {
				ps.setString(19, stopInfo.getCoordMethod().toString());
			} else {
				ps.setNull(19, java.sql.Types.VARCHAR);
			}
			if (stopInfo.getAccessibilityClass() != null) {
				ps.setInt(20, stopInfo.getAccessibilityClass());
			} else {
				ps.setNull(20, java.sql.Types.INTEGER);
			}
			if (stopInfo.getNote() != null) {
				ps.setString(21, stopInfo.getNote());
			} else {
				ps.setNull(21, java.sql.Types.VARCHAR);
			}
			ps.addBatch();
		}
		ps.executeBatch();
		ps.close();
	}
	
	public static void fillRoutes(List<RouteInfo> routeInfos) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("INSERT INTO routes VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
		for (RouteInfo routeInfo : routeInfos) {
			if (routeInfo.getRouteCode() != null) {
				ps.setString(1, routeInfo.getRouteCode());
			} else {
				ps.setNull(1, java.sql.Types.VARCHAR);
			}
			if (routeInfo.getRouteDir() != null) {
				ps.setString(2, routeInfo.getRouteDir().toString());
			} else {
				ps.setNull(2, java.sql.Types.VARCHAR);
			}
			if (routeInfo.getValidFrom() != null) {
				java.sql.Date sqldate = new java.sql.Date(routeInfo.getValidFrom().getTime());
				ps.setDate(3, sqldate);
			} else {
				ps.setNull(3, java.sql.Types.DATE);
			}
			if (routeInfo.getValidTo() != null) {
				java.sql.Date sqldate = new java.sql.Date(routeInfo.getValidTo().getTime());
				ps.setDate(4, sqldate);
			} else {
				ps.setNull(4, java.sql.Types.DATE);
			}
			if (routeInfo.getStopCode() != null) {
				ps.setInt(5, routeInfo.getStopCode());
			} else {
				ps.setNull(5, java.sql.Types.INTEGER);
			}
			if (routeInfo.getType() != null) {
				ps.setString(6, routeInfo.getType().toString());
			} else {
				ps.setNull(6, java.sql.Types.VARCHAR);
			}
			if (routeInfo.getStopOrder() != null) {
				ps.setInt(7, routeInfo.getStopOrder());
			} else {
				ps.setNull(7, java.sql.Types.INTEGER);
			}
			if (routeInfo.getX() != null) {
				ps.setInt(8, routeInfo.getX());
			} else {
				ps.setNull(8, java.sql.Types.INTEGER);
			}
			if (routeInfo.getY() != null) {
				ps.setInt(9, routeInfo.getY());
			} else {
				ps.setNull(9, java.sql.Types.INTEGER);
			}
			ps.addBatch();
		}
		ps.executeBatch();
		ps.close();
	}
}
