package eu.livegov.mobilesensing.manager;

import eu.livegov.mobilesensing.sensors.SensorService;

public class SensorDescription {
	public String sensorName;
	public Class<? extends SensorService> serviceClass;
	public SensorService serviceObject;
	
	public SensorDescription(Class<? extends SensorService> serviceClass) {
		this.serviceClass = serviceClass;
		this.sensorName = serviceClass.getSimpleName(); 
	}
}
