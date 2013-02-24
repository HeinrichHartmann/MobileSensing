package eu.livegov.mobilesensing.sensors.gps;

import eu.livegov.mobilesensing.sensors.SensorValue;

public class GpsSensorValue implements SensorValue {
	public long timestamp;
	public float latitude;
	public float longitude;
	public float altitude;

	// Constructor
	public GpsSensorValue(long timestamp, float latitude, float longitude,
			float altitude) {

		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;

	}

	public String toString() {
		return "AccelerometerValue at " + 
				String.valueOf(timestamp) + ":" +
				String.valueOf(latitude) + "," + 
				String.valueOf(longitude) + "," + 
				String.valueOf(altitude); 
	}
	
	@Override
	public long getTimestamp() {

		return timestamp;
	}

}
