package eu.livegov.mobilesensing.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.livegov.mobilesensing.Constants;
import eu.livegov.mobilesensing.sensors.SensorService;
import eu.livegov.mobilesensing.sensors.SensorService.SensorServiceBinder;
import eu.livegov.mobilesensing.sensors.SensorValue;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * SensorManagerClass (SM) manages SensorServices
 * 
 * The SM...
 * 
 * * ..is a "started" service which runs in the background
 *   even when MainActivity is closed.
 *   
 * * ...starts and stops SensorServices.
 *   They are connected via the "Bind Service" approach
 *   
 * * ...communicates with the GUI via Intents
 * 
 * * ...aggregates data from the Sensors
 * 
 * * ...writes aggregated data to the file system 
 * 
 * @author hartmann
 *
 */
public class SensorManager extends Service implements SensorManagerInterface {
	private static final String LOG_TAG = Constants.LOG_TAG;
		
	// List of bound SensorServices
	private LinkedList<SensorService> services = new LinkedList<SensorService>();
	private LinkedList<Intent> servicesToBind = new LinkedList<Intent>();


	/**
	 * Required for service classes
	 */
	@Override
	public IBinder onBind(Intent intent) {
		Log.i(LOG_TAG,"Sensor Manager onBind");
		return null;
	}

	
	/**
	 * Called when SensorManager start ist triggered by Intent
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(LOG_TAG,"Called SensorManager Start");
		return super.onStartCommand(intent, flags, startId);		
	}

	/**
	 * Fill List of services which should be binded
	 * TODO: Read from config 
	 */
	public void setServicesToBind() {
		Log.i(LOG_TAG,"Set Services to Bind");
		// static AccelerometerSensorService for testing purposes; generate from
		// config later
		servicesToBind.add(new Intent(this, AccelerometerSensorService.class));
	}

	
	/**
	 * Bind all Sensors to SensorManager
	 */
	public void bindSensorServices() {
		Log.i(LOG_TAG,"Binding Services " + servicesToBind.size());
		for (Intent intent : servicesToBind) {
			bindService(intent, connection, BIND_AUTO_CREATE);	
		}
	}

	ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(LOG_TAG, className + " connected");
			SensorServiceBinder binder = (SensorServiceBinder) service;
			services.add(binder.getService());
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.i(LOG_TAG, "Binding " + className + " Failed");
		}
	};
	
	
	//////////////// METHODS FOR SENSOR HANDLING ///////////////////
	
	
	@Override
	public List<String> statusAll() {
		Log.i(LOG_TAG,"Status All: " + services.size());

		List<String> sensorStatusList = new ArrayList<String>();
		
		for ( SensorService mService : services ){
			String status = mService.getStatus();
			Log.i(LOG_TAG, 
					"Service " + mService.getMetadata().getServiceName() + "\n " +  
					status );
			
			sensorStatusList.add(status);
		}
		
		return sensorStatusList;
	}
	
	@Override
	public void setConfig(SensorManagerConfig config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startRecording() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stopRecording() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void storeData() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<SensorValue> getLastValues() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
