package eu.livegov.mobilesensing.sensors;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Abstract class describing SensorServices
 *
 * SensorServices are bound to the SensorManager
 * When .start() is called the sensor starts recording
 * data into an intenal queue.
 * When .stop() is called the recording is stoppen
 * 
 * Data is transfered via:
 * * getLastValue
 * * pullData
 * 
 * @author hartmann
 *
 */
public abstract class SensorService extends Service {
	// Service status flag
	public boolean running = false;
	
	/**
	 *  Returns Metadata object containing information about the sensor 
	 */
	public abstract Metadata getMetadata();
	
	/**
	 * Returns last recorded sensor value as SensorValue object.
	 */
	public abstract SensorValue getLastValue();
	
	/**
	 * Returns cached SensorValues and clears the cache
	 */
	public abstract List<? extends SensorValue> pullData();

	/**
	 * Add sensor values to the cache. Needed for testing.
	 */
	public abstract void putSensorValue(SensorValue value);
	
	/**
	 * starts recording of sensor values; is called by onBind()
	 */
	public abstract void start();
	
	/**
	 * stops recording of sensor values; is called by onUnBind()
	 */
	public abstract void stop();
	
	/**
	 * Returns "running" if service is bound. "stopped" otherwis
	 */	
	public String getStatus(){
		return running ? "tunning" : "stopped";
	}
	
	/*
	 * Sensor Binding Classes
	 */
	public class SensorServiceBinder extends Binder {
		public SensorService getService() {
			return SensorService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(getClass().getName(), "binding request");
		start();
		running = true;
		return new SensorServiceBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(getClass().getName(), "unbinding");
		stop();
		running = false;
		return false;
	}


}
