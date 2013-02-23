package eu.livegov.mobilesensing.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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
	private static final String LOG_TAG = "SensorManager";
	
	// List of bound SensorServices
	private LinkedList<SensorService> services = new LinkedList<SensorService>();
	private LinkedList<Intent> servicesToBind = new LinkedList<Intent>();

	ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(LOG_TAG, className + " connected");
			SensorServiceBinder binder = (SensorServiceBinder) service;
			services.add(binder.getService());
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.i(LOG_TAG, "Bind Failed");
		}
	};
		
	
	public void bindSensorServices() {
		Log.i(LOG_TAG,"Binding Services " + servicesToBind.size());
		for (Intent intent : servicesToBind) {
			bindService(intent, connection, BIND_AUTO_CREATE);	
		}
	}

	public void setServicesToBind() {
		Log.i(LOG_TAG,"Set Services to Bind");
		// static AccelerometerSensorService for testing purposes; generate from
		// config later
		servicesToBind.add(new Intent(getApplicationContext(), AccelerometerSensorService.class));
	}

	public class SensorManagerBinder extends Binder {
		public SensorManager getService() {
			return SensorManager.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(LOG_TAG,"Sensor Manager onBind");
		return new SensorManagerBinder();
	}

	
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
