package eu.livegov.mobilesensing.manager;

import android.content.Context;
import android.content.ServiceConnection;
import eu.livegov.mobilesensing.sensors.SensorService;

public class SensorDescription {
	private String sensorName;
	private Class<? extends SensorService> serviceClass;
	private SensorService serviceObject;
	private ServiceConnection serviceConnection;
	
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

	public void unbind(Context context){
		context.unbindService(serviceConnection);
		serviceConnection = null;
		serviceObject     = null;
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

	public void setServiceConnection(ServiceConnection serviceConnection) {
		this.serviceConnection = serviceConnection;
	}

	public ServiceConnection getServiceConnection() {
		return serviceConnection;
	}
	
}
