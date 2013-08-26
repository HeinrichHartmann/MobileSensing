public class Extractors {

	public static LineInfo extractLineInfo(String line) {
		LineInfo info = new LineInfo();
		line = line.substring(1);
		info.setId(line.substring(0, 6).trim());
		info.setDate1(line.substring(6, 14).trim());
		info.setDate2(line.substring(14, 22).trim());
		info.setLanguage(line.substring(22, 23).trim());
		info.setLineName(line.substring(23, 83).trim());
		info.setTerminal1Name(line.substring(83, 103).trim());
		info.setTerminal2Name(line.substring(103, 123).trim());
		info.setStopCodeDir1(line.substring(123, 130).trim());
		info.setStopCodeDir2(line.substring(130, 137).trim());
		info.setLineLengthDir1(line.substring(137, 142).trim());
		info.setLineLengthDir2(line.substring(142, 147).trim());
		info.setTransportMean(line.substring(147, 149).trim());
		return info;
	}
	
	public static StopInfo extractStopInfo(String line) {
		StopInfo info = new StopInfo();
		line = line.substring(1);
		info.setStopCode(line.substring(0, 7).trim());
		info.setX_kkj2(line.substring(7, 14).trim());
		info.setY_kkj2(line.substring(14, 21).trim());
		info.setLatitude(line.substring(21, 29).trim());
		info.setLongitude(line.substring(29, 37).trim());
		info.setStopName(line.substring(37, 57).trim());
		info.setStopNameSwedish(line.substring(57, 77).trim());
		info.setAddress(line.substring(77, 97).trim());
		info.setAddressSwedish(line.substring(97, 117).trim());
		info.setPlatformNumber(line.substring(117, 120).trim());
		info.setX_kkj3(line.substring(120, 127).trim());
		info.setY_kkj3(line.substring(127, 134).trim());
		info.setStopLocationAreaName(line.substring(134, 154).trim());
		info.setStopLocationAreaNameSwedish(line.substring(154, 174).trim());
		info.setShelter(line.substring(174, 176).trim());
		info.setStopShortCode(line.substring(176, 182).trim());
		info.setX_wgs84_proj(line.substring(182, 190).trim());
		info.setY_wgs84_proj(line.substring(190, 198).trim());
		// WGS84-coordinates solx and soly for this stop calculated or measured
		// (L/M)
		info.setCoordMethod(line.substring(198, 199).trim());
		info.setAccessibilityClass(line.substring(199, 200).trim());
		info.setNote(line.substring(200, 215).trim());
		return info;
	}

	public static RouteInfo extractRouteInfo(String line) {
		RouteInfo info = new RouteInfo();
		line = line.substring(1);
		info.setRouteCode(line.substring(0, 6).trim());
		info.setRouteDir(line.substring(6, 7).trim());
		info.setValidFrom(line.substring(7, 15).trim());
		info.setValidTo(line.substring(15, 23).trim());
		info.setStopCode(line.substring(23, 30).trim());
		/*
		 * In case of geometry point relpysakki = M, else relpysakki= P = stop
		 * which is used by this route E = stop which is not used by this route
		 * X = crossing - = border
		 */
		info.setType(line.substring(30, 31).trim());
		info.setStopOrder(line.substring(31, 35).trim());
		// kkj-2?
		info.setX(line.substring(35, 42).trim());
		info.setY(line.substring(42, 49).trim());		
		return info;
	}

}
