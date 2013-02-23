package eu.livegov.mobilesensing.sensors;

/*
 * ContainerClass for individual Sensor Values
 * * timestamp
 * * value
 */
public interface SensorValue {
	// time of recording, required for testing
	public long getTimestamp();
}
