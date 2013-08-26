public class StopInfo {

	private Integer stopCode;
	private Integer x_kkj2;
	private Integer y_kkj2;
	private Float latitude;
	private Float longitude;
	private String stopName;
	private String stopNameSwedish;
	private String address;
	private String addressSwedish;
	private String platformNumber;
	private Integer x_kkj3;
	private Integer y_kkj3;
	private String stopLocationAreaName;
	private String stopLocationAreaNameSwedish;
	private Integer shelter;
	private String stopShortCode;
	private Float x_wgs84_proj;
	private Float y_wgs84_proj;
	private Character coordMethod;
	private Integer accessibilityClass;
	private String note;

	public Integer getStopCode() {
		return stopCode;
	}

	public Integer getX_kkj2() {
		return x_kkj2;
	}

	public Integer getY_kkj2() {
		return y_kkj2;
	}

	public Float getLatitude() {
		return latitude;
	}

	public Float getLongitude() {
		return longitude;
	}

	public String getStopName() {
		return stopName;
	}

	public String getStopNameSwedish() {
		return stopNameSwedish;
	}

	public String getAddress() {
		return address;
	}

	public String getAddressSwedish() {
		return addressSwedish;
	}

	public String getPlatformNumber() {
		return platformNumber;
	}

	public Integer getX_kkj3() {
		return x_kkj3;
	}

	public Integer getY_kkj3() {
		return y_kkj3;
	}

	public String getStopLocationAreaName() {
		return stopLocationAreaName;
	}

	public String getStopLocationAreaNameSwedish() {
		return stopLocationAreaNameSwedish;
	}

	public Integer getShelter() {
		return shelter;
	}

	public String getStopShortCode() {
		return stopShortCode;
	}

	public Float getX_wgs84_proj() {
		return x_wgs84_proj;
	}

	public Float getY_wgs84_proj() {
		return y_wgs84_proj;
	}

	public Character getCoordMethod() {
		return coordMethod;
	}

	public Integer getAccessibilityClass() {
		return accessibilityClass;
	}

	public String getNote() {
		return note;
	}

	public void setStopCode(String stopCode) {
		if (!stopCode.isEmpty()) {
			this.stopCode = Integer.parseInt(stopCode);
		}
	}

	public void setX_kkj2(String x_kkj2) {
		if (!x_kkj2.isEmpty()) {
			this.x_kkj2 = Integer.parseInt(x_kkj2);
		}
	}

	public void setY_kkj2(String y_kkj2) {
		if (!y_kkj2.isEmpty()) {
			this.y_kkj2 = Integer.parseInt(y_kkj2);
		}
	}

	public void setLatitude(String latitude) {
		if (!latitude.isEmpty()) {
			this.latitude = Float.valueOf(latitude);
		}
	}

	public void setLongitude(String longitude) {
		if (!longitude.isEmpty()) {
			this.longitude = Float.valueOf(longitude);
		}
	}

	public void setStopName(String stopName) {
		if (!stopName.isEmpty()) {
			this.stopName = stopName;
		}
	}

	public void setStopNameSwedish(String stopNameSwedish) {
		if (!stopNameSwedish.isEmpty()) {
			this.stopNameSwedish = stopNameSwedish;
		}
	}

	public void setAddress(String address) {
		if (!address.isEmpty()) {
			this.address = address;
		}
	}

	public void setAddressSwedish(String addressSwedish) {
		if (!addressSwedish.isEmpty()) {
			this.addressSwedish = addressSwedish;
		}
	}

	public void setPlatformNumber(String platformNumber) {
		if (!platformNumber.isEmpty()) {
			this.platformNumber = platformNumber;
		}
	}

	public void setX_kkj3(String x_kkj3) {
		if (!x_kkj3.isEmpty()) {
			this.x_kkj3 = Integer.parseInt(x_kkj3);
		}
	}

	public void setY_kkj3(String y_kkj3) {
		if (!y_kkj3.isEmpty()) {
			this.y_kkj3 = Integer.parseInt(y_kkj3);
		}
	}

	public void setStopLocationAreaName(String stopLocationAreaName) {
		if (!stopLocationAreaName.isEmpty()) {
			this.stopLocationAreaName = stopLocationAreaName;
		}
	}

	public void setStopLocationAreaNameSwedish(
			String stopLocationAreaNameSwedish) {
		if (!stopLocationAreaNameSwedish.isEmpty()) {
			this.stopLocationAreaNameSwedish = stopLocationAreaNameSwedish;
		}
	}

	public void setShelter(String shelter) {
		if (!shelter.isEmpty()) {
			this.shelter = Integer.parseInt(shelter);
		}
	}

	public void setStopShortCode(String stopShortCode) {
		if (!stopShortCode.isEmpty()) {
			this.stopShortCode = stopShortCode;
		}
	}

	public void setX_wgs84_proj(String x_wgs84_proj) {
		if (!x_wgs84_proj.isEmpty()) {
			this.x_wgs84_proj = Float.valueOf(x_wgs84_proj);
		}
	}

	public void setY_wgs84_proj(String y_wgs84_proj) {
		if (!y_wgs84_proj.isEmpty()) {
			this.y_wgs84_proj = Float.valueOf(y_wgs84_proj);
		}
	}

	public void setCoordMethod(String coordMethod) {
		if (!coordMethod.isEmpty()) {
			this.coordMethod = coordMethod.charAt(0);
		}
	}

	public void setAccessibilityClass(String accessibilityClass) {
		if (!accessibilityClass.isEmpty()) {
			this.accessibilityClass = Integer.parseInt(accessibilityClass);
		}
	}

	public void setNote(String note) {
		if (!note.isEmpty()) {
			this.note = note;
		}
	}

}
