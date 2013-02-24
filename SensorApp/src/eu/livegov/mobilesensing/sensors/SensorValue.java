package eu.livegov.mobilesensing.sensors;

/*
 * ContainerClass for individual Sensor Values
 * * timestamp
 * * value
 */
public interface SensorValue {

	/**
	 * String representation of SensorValue as String
	 * @return sensorValueString
	 */
	public String toString();

	/** 
	 * Time of recording, required for testing
	 * @return timestamp
	 */
	public long getTimestamp();
}
