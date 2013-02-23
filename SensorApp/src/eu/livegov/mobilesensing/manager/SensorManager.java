package eu.livegov.mobilesensing.manager;

import java.util.LinkedList;
import java.util.List;

import eu.livegov.mobilesensing.sensors.SensorService;
import eu.livegov.mobilesensing.sensors.SensorService.SensorServiceBinder;
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
public class SensorManager extends Service {
	private static final String LOG_TAG = "SensorManager";

	// List of bound SensorServices
	private LinkedList<SensorService> services = new LinkedList<SensorService>();
	// List of Sensors which are bound onCreate
	private LinkedList<Intent> servicesToBind = new LinkedList<Intent>();

	
	
	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(LOG_TAG, className + " connected");
			SensorServiceBinder binder = (SensorServiceBinder) service;
			services.add(binder.getService());
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
		}
	};

	private void bindSensorServices(List<Intent> servicesToBind,
			ServiceConnection connection) {
		for (Intent intent : servicesToBind) {
			bindService(intent, connection, BIND_AUTO_CREATE);
		}
	}

	private void determineServicesToBind() {
		// static AccelerometerSensorService for testing purposes; generate from
		// config later
		servicesToBind.add(new Intent(this, AccelerometerSensorService.class));
	}

	public void test() {
		determineServicesToBind();
		bindSensorServices(servicesToBind, connection);
	}

	public class SensorManagerBinder extends Binder {
		public SensorManager getService() {
			return SensorManager.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new SensorManagerBinder();
	}

	public void statusAll() {
		for ( SensorService mService : services ){
			Log.i(LOG_TAG, 
					"Service " + mService.getMetadata().getServiceName() + "\n" + 
					"Status "  + mService.getStatus()
					);
		}
	}
	
}
