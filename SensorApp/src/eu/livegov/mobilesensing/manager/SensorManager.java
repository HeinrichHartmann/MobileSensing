package eu.livegov.mobilesensing.manager;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eu.livegov.mobilesensing.Constants;
import eu.livegov.mobilesensing.sensors.SensorValue;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * SensorManagerClass (SM) manages SensorServices
 * 
 * The SM...
 * 
 * * ..is a "started" service which runs in the background even when
 * MainActivity is closed.
 * 
 * * ...starts and stops SensorServices. They are connected via the
 * "Bind Service" approach
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

	public static final String LOG_TAG = Constants.LOG_TAG;

	public static final String ACTION_BIND = "BIND";
	public static final String ACTION_START_RECORDING = "START_RECORDING";
	public static final String ACTION_STOP_RECORDING = "STOP_RECORDING";

	private LinkedList<SensorDescription> usedSensors;

	public static boolean running = false;

	/**
	 * Required for service classes
	 */
	@Override
	public IBinder onBind(Intent intent) {
		// Sensor Manager cannot be bound.
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(LOG_TAG, "Sensor Manager started");
		running = true;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(LOG_TAG, "Sensor Manager stopped");
		running = false;
	}

	/**
	 * Called when SensorManager start is triggered by Intent
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();

		if (action == null) {
			Log.i(LOG_TAG, "Called SensorManager without action");
			return super.onStartCommand(intent, flags, startId);
		}

		// Handle action requests
		if (action.equals(ACTION_BIND)) {
			setServicesToBind();
			bindSensorServices();
		}

		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * Fill List of services which should be binded TODO: Read from config
	 */
	public void setServicesToBind() {
		Log.i(LOG_TAG, "Set Services to Bind");
		// static AccelerometerSensorService for testing purposes; generate from
		// config later
		usedSensors
				.add(new SensorDescription(AccelerometerSensorService.class));
	}

	private void bindSensorServices() {
		for (SensorDescription desc : usedSensors) {
			bindService(new Intent(this, desc.getClass()),
					desc.getConnection(), BIND_AUTO_CREATE);
		}
	}

	// ////////////// METHODS FOR SENSOR HANDLING ///////////////////

	@Override
	public List<String> statusAll() {
		Log.i(LOG_TAG, "Status All: " + usedSensors.size());

		List<String> sensorStatusList = new ArrayList<String>();

		for (SensorDescription desc : usedSensors) {
			String status = desc.getService().getStatus();
			Log.i(LOG_TAG, "Service "
					+ desc.getService().getMetadata().getServiceName() + "\n "
					+ status);

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
		for (SensorDescription desc : usedSensors) {
			desc.getService().startRecording();
		}
	}

	@Override
	public void stopRecording() {
		for (SensorDescription desc : usedSensors) {
			desc.getService().stopRecording();
		}
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
