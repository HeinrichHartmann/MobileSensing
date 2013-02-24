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
	public static boolean running = false;

	/**
	 * Check if serice is recording.
	 */
	public static boolean recording = false;

	
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
		if (running){
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
		public SensorService getService() {
			return SensorService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(LOG_TAG, "SensorService binding request");
		
		// Call Startup
		if (! startupSensor()) {
			// Startup failed, e.g. no sensor found
			return null; 
		} else {
			// Startup successfull
			return new SensorServiceBinder();
		}
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.i(LOG_TAG, "SensorService unbinding");
		return false;
	}

	@Override
	public void onCreate() {
		Log.i(LOG_TAG, "Created Sensor service");
		super.onCreate();
		running = true;
	}
	
	@Override
	public void onDestroy() {
		Log.i(LOG_TAG, "Destroyed Sensor service");
		super.onDestroy();
		running = false;
	}
	
	public void unbindSelf() {
		stopSelf();
	}
	
}
