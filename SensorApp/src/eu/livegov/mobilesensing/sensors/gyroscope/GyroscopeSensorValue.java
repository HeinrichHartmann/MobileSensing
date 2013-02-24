package eu.livegov.mobilesensing.sensors.gyroscope;

import eu.livegov.mobilesensing.sensors.SensorValue;

/**
 * Container Object for Accelerometer Sensor Values
 *
 */

public class GyroscopeSensorValue implements SensorValue{
	public long timestamp;
	public float x;
	public float y;
	public float z;
	
	// Constructor
	public GyroscopeSensorValue(long timestamp, float x, float y,
			float z) {

		this.timestamp = timestamp;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	@Override
	public long getTimestamp() {
		return timestamp;
	}
	
	public String toString() {
		return "Gyroscope Value at " + 
				String.valueOf(timestamp) + ":" +
				String.valueOf(x) + "," + 
				String.valueOf(y) + "," + 
				String.valueOf(z); 
	}

}
