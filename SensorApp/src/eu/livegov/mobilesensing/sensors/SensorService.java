package eu.livegov.mobilesensing.sensors;

import java.util.List;

import eu.livegov.mobilesensing.Constants;


import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
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
	public static String LOG_TAG = Constants.LOG_TAG;
	
	/**
	 * Check if service is running.
	 * Set onCerate/onDestroy
	 * https://groups.google.com/forum/?fromgroups=#!topic/android-developers/jEvXMWgbgzE
	 */
	public boolean running = false;

	/**
	 * Check if serice is recording.
	 */
	public boolean recording = false;

	/**
	 * Returns name of Sensor
	 * @return sensorName
	 */
	public String getSensorName() {
		return this.getClass().getSimpleName();
	}
	
	/**
	 *  Returns Metadata object containing information about the sensor
	 *  @return meta 
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
	 * Called onBind
	 * Returns true if startup went ok 
	 * Returns false if there were errors in the starup process (sensor not found)
	 * 
	 * @return success
	 */
	public abstract boolean startupSensor();
	
	/**
	 * starts recording of sensor values; is called by onBind()
	 */
	public void startRecording() {
		recording = true;
	}
	
	/**
	 * stops recording of sensor values; is called by onUnBind()
	 */
	public void stopRecording() {
		recording = false;
	}
	
	/**
	 * Returns status:
	 * started/stopped/recording
	 */	
	public String getStatus(){
		if (running) {
			if (recording) {
				return "recording";
			} else {
				return "started";
			} 
		} else {
			return "stopped";
		}
	}
	
	/*
	 * Sensor Binding Classes
	 */
	public class SensorServiceBinder extends Binder {
		// Flag for startup
		public boolean startupSuccess = false;
		
		public SensorService getService() {
			return SensorService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(LOG_TAG, "SensorService binding request");
		
		SensorServiceBinder binder = new SensorServiceBinder();
		
		// Call startupSensor() on bind and set Flag
		binder.startupSuccess = startupSensor(); 

		return binder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(LOG_TAG, getSensorName() + " unbinded.");
		return false;
	}

	@Override
	public void onCreate() {
		Log.i(LOG_TAG, getSensorName() + " created.");
		running = true;
	}
	
	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, getSensorName() + "destroyed." );
		running = false;
	}
	
}
