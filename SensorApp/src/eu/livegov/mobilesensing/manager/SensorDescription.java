package eu.livegov.mobilesensing.manager;

import eu.livegov.mobilesensing.sensors.SensorService;

public class SensorDescription {
	private String sensorName;
	private Class<? extends SensorService> serviceClass;
	private SensorService serviceObject;
	
	public SensorDescription(Class<? extends SensorService> serviceClass) {
		this.serviceClass = serviceClass;
		this.sensorName = serviceClass.getSimpleName(); 
	}
	
	public boolean isBound() {
		return (serviceObject != null);		
	}

	public boolean isRunning() {
		if (! isBound() ) return false;
		return serviceObject.running;
	}
	
	public boolean isRecording() {
		if (!isRunning()) return false;
		return serviceObject.recording;
	}

	///// SETTERS/GETTERS ////
	public String getSensorName() {
		return sensorName;
	}
	
	public Class<? extends SensorService> getServiceClass() {
		return serviceClass;
	}

	public SensorService getServiceObject() {
		return serviceObject;
	}

	public void setServiceObject(SensorService serviceObject) {
		this.serviceObject = serviceObject;
	}
		
	
}
