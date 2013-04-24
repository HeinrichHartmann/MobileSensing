package eu.livegov.mobilesensing.sensors;

import java.util.HashMap;
import java.util.Map;

import android.hardware.Sensor;

/**
 * interface for metadata of sensors
 */
public class Metadata {
	public String serviceName;
	public Map<String,Object> sensorInfo = new HashMap<String,Object>();
	
	public Metadata(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getServiceName(){
		return serviceName;
	}
	
	/**
	 * set Sensor information from Sesnsor object
	 * @param sensor
	 */
	public void autoSetSensorInfo(Sensor sensor){
		sensorInfo.put("MaximumRange", sensor.getMaximumRange());
		sensorInfo.put("MinDelay",sensor.getMinDelay());
		sensorInfo.put("Name",sensor.getName());
		sensorInfo.put("Power",sensor.getPower());
		sensorInfo.put("Resolution",sensor.getResolution());
		sensorInfo.put("Type",sensor.getType());
		sensorInfo.put("Vendor",sensor.getVendor());
		sensorInfo.put("Version",sensor.getVersion());
	}
}
