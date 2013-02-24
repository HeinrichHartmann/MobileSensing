package eu.livegov.mobilesensing.sensors.accelerometer;

import eu.livegov.mobilesensing.sensors.SensorValue;

/**
 * Container Object for Accelerometer Sensor Values
 * @author hartmann
 *
 */

public class AccelerometerSensorValue implements SensorValue {
	public long timestamp;
	public float x;
	public float y;
	public float z;
	
	// Constructor
	public AccelerometerSensorValue(long timestamp, float x, float y,
			float z) {

		this.timestamp = timestamp;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public String toString() {
		return "AccelerometerValue at " + 
				String.valueOf(timestamp) + ":" +
				String.valueOf(x) + "," + 
				String.valueOf(y) + "," + 
				String.valueOf(z); 
	}

	@Override
	public long getTimestamp() {

	return timestamp;
	}
}

