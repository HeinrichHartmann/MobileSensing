import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class Main {

	private static final String LINEFILE = "/path/to/linja.dat";
	private static final String STOPFILE = "/path/to/pys__kki.dat";
	private static final String ROUTEFILE = "/path/to/reittimuoto.dat";
	private static final String ENCODING = "latin1";

	private static List<LineInfo> lineInfoList = new LinkedList<LineInfo>();
	private static List<StopInfo> stopInfoList = new LinkedList<StopInfo>();
	private static List<RouteInfo> routeInfoList = new LinkedList<RouteInfo>();

	private static void readLineInfos() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(LINEFILE), ENCODING));
		long start = System.currentTimeMillis();
		while (in.ready()) {
			lineInfoList.add(Extractors.extractLineInfo(in.readLine()));
		}
		long end = System.currentTimeMillis();
		in.close();
		System.out.println("Read " + lineInfoList.size()
				+ " line information lines in " + (end - start) + "ms.");
	}

	private static void readStopInfos() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(STOPFILE), ENCODING));
		long start = System.currentTimeMillis();
		while (in.ready()) {
			stopInfoList.add(Extractors.extractStopInfo(in.readLine()));
		}
		long end = System.currentTimeMillis();
		in.close();
		System.out.println("Read " + stopInfoList.size()
				+ " stop information lines in " + (end - start) + "ms.");
	}

	private static void readRouteInfos() throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(ROUTEFILE), ENCODING));
		long start = System.currentTimeMillis();
		while (in.ready()) {
			routeInfoList.add(Extractors.extractRouteInfo(in.readLine()));
		}
		long end = System.currentTimeMillis();
		in.close();
		System.out.println("Read " + routeInfoList.size()
				+ " route information lines in " + (end - start) + "ms.");
	}

	public static void main(String[] args) throws Exception {
		readLineInfos();
		readStopInfos();
		readRouteInfos();
		Database.createTables();
		Database.fillLines(lineInfoList);
		Database.fillStops(stopInfoList);
		Database.fillRoutes(routeInfoList);
		System.out.println("Done.");
	}

}
