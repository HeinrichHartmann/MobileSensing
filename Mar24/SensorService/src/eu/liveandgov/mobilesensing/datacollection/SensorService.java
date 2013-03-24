package eu.liveandgov.mobilesensing.datacollection;

import java.io.File;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Live+Gov Sensing Service
 * 
 * Provides Intent-API for integration into Live+Gov Framework. 
 * Controls the SDCF service components for sensor data collection.
 * 
 * @author hartmann
 *
 */

public class SensorService extends Service {	
	/*
	 * API DESCRIPTION
	 */
	public static final String INTENT_TYPE = "eu.liveandgov.mobilesensing";
	public static final String ACTION_START_SERVICE = "START";
	public static final String ACTION_STOP_SERVICE =  "STOP";

	/**
	 * LOG Messages
	 */
	private void Log(String msg){
		android.util.Log.i("SERVICE", msg);
	}
	
	/**
	 * Constructor
	 */
	public SensorService() {
		Log("Service Object Constructed");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		Log("Service recieved action " + action);
		
		if (action.equals(ACTION_START_SERVICE)){
			startService();
		} else if (action.equals(ACTION_STOP_SERVICE)){
			stopService();
		} else {
			Log("No Handler for action " + action);
		}
		
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		throw new UnsupportedOperationException("Not yet implemented");
	}

	/*
	 * START/STOP SERVICE
	 */	
	public boolean isRunning = false;
	
	public void startService(){
		// START Sensor Service
	}

	public void stopService(){
		// STOP Sensor Service
	}

	
	/*
	 * SERVCICE CONFIGURATION
	 */
	
	public void setConfig(File XML) {
		// update sensor configuration according to XML file.
	}
	
	/*
	 * START/STOP RECORDING
	 */
	public boolean isRecording = false;
	
	public void startRecording(){
		// START Recording of Sensor samples
	}

	public void stopRecording(){
		// STOP Recording of Sensor samples
	}

	/*
	 * TRANSFER SAMPLES
	 */
	public void transferSamples(){
		// Clear DB and transfer samples
	}
	
}
