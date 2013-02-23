package eu.livegov.mobilesensing.manager;

import java.util.List;

import eu.livegov.mobilesensing.sensors.SensorValue;

/**
 * Abstract description of the SensorManagerClass
 * 
 * @author hartmann
 *
 */
public interface SensorManagerInterface {
	/**
	 * Update internal configuration about
	 * sensors to record and their configuration
	 */
	public void setConfig(SensorManagerConfig config);

	/**
	 * Start recording of all currently registered sensors.
	 */
	public void startRecording();

	/**
	 * Stop recording of all currently registered sensors.
	 */
	public void stopRecording();
	
	/**
	 * Write data to file system
	 */
	public void storeData();

	/**
	 * Get Status of all running sensors
	 * 
	 * @return sensorStatusList
	 */
	public List<String> statusAll();
	
	/**
	 * Get last values of all recorded sensors
	 * 
	 * @return lastValues
	 */
	public List<SensorValue> getLastValues();
	
}
