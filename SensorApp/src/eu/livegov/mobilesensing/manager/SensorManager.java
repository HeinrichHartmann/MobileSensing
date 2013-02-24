package eu.livegov.mobilesensing.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import eu.livegov.mobilesensing.Constants;
import eu.livegov.mobilesensing.sensors.SensorService;
import eu.livegov.mobilesensing.sensors.SensorValue;
import eu.livegov.mobilesensing.sensors.SensorService.SensorServiceBinder;
import eu.livegov.mobilesensing.sensors.SensorValueBatch;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;
import eu.livegov.mobilesensing.sensors.gps.GpsSensorService;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
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
	// CONSTANTS
	public static final String LOG_TAG = Constants.LOG_TAG;
	public static final String ACTION_BIND = "BIND";
	public static final String ACTION_START_RECORDING = "START_RECORDING";
	public static final String ACTION_STOP_RECORDING = "STOP_RECORDING";

	// STATICS
	public static boolean running = false;
	
	/**
	 * List of sensors available in the Framework
	 */	
	private static List<SensorDescription> availableSensors = new LinkedList<SensorDescription>();
	public  static Map<String,SensorDescription> getSensorDescription = new HashMap<String,SensorDescription>();
	
	/*
	 * Static Initialization
	 * Add available sensors to sensor list
	 */
	static {
		// Generate list of available sensors
		addAvailableSensor(AccelerometerSensorService.class);
		addAvailableSensor(GpsSensorService.class);
	}

	private static void addAvailableSensor(Class<? extends SensorService> sensorClass){
		
		SensorDescription desc = new SensorDescription(sensorClass);
		
		availableSensors.add(desc);
		getSensorDescription.put(desc.getSensorName(), desc);
		
		Log.i(LOG_TAG,"Added sensor "+desc.getSensorName()+" to availableSensors");
	}
	
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
		unbindAllSensorServices();		
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
		Log.i(LOG_TAG, "Called SensorManager with action " + action);

		// Handle action requests
		if (action.equals(ACTION_BIND)) {
			bindAllSensorSerives();
		} else if (action.equals(ACTION_START_RECORDING)){
			startRecording();
		} else if (action.equals(ACTION_STOP_RECORDING)) {
			stopRecording();
		}

		return super.onStartCommand(intent, flags, startId);
	}


	private void bindSensorService(SensorDescription desc) {
		if (desc.isBound()) return;
		
		Log.i(LOG_TAG, "Binding sensor " + desc.getSensorName() );
		
		String sensorName = desc.getSensorName();

		ServiceConnection serviceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name,
					IBinder service) {
				SensorServiceBinder binder = (SensorServiceBinder) service;
				SensorService sensorService = binder.getService();
				registerSensorServiceObject(sensorService);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				Log.i(LOG_TAG,"Disconnected Sensor " + name);
			}
		};
		
		desc.setServiceConnection(serviceConnection);

		bindService(
				new Intent(this, desc.getServiceClass()),
				serviceConnection,
				BIND_AUTO_CREATE);
	}
	
	private void registerSensorServiceObject(SensorService sensorService){	
		String sensorName = sensorService.getClass().getSimpleName();
		SensorDescription desc = getSensorDescription.get(sensorName);
		if (desc != null) {
			desc.setServiceObject(sensorService);
			Log.i(LOG_TAG,"Sensor " + sensorName + " registered");
		} else {
			Log.i(LOG_TAG,"Sensor " + sensorName + " not found");
		}
	}

	private void bindAllSensorSerives(){
		for (SensorDescription desc : availableSensors){
			bindSensorService(desc);
		}
	}
	
	private void unbindSensorService(SensorDescription desc) {
		if (desc.isBound())	desc.unbind(this);
	}
	
	private void unbindAllSensorServices(){
		for (SensorDescription desc : availableSensors){
			unbindSensorService(desc);
		}
	}
	
	// ////////////// METHODS FOR SENSOR HANDLING ///////////////////

	@Override
	public List<String> statusAll() {
		Log.i(LOG_TAG, "Status All: " + availableSensors.size());

		List<String> sensorStatusList = new ArrayList<String>();

		for (SensorDescription desc : availableSensors) {
			if (!desc.isRunning()) continue;
			String status = desc.getServiceObject().getStatus();
			Log.i(LOG_TAG, "Service "
				+ desc.getServiceObject().getMetadata().getServiceName() + "\n "
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
		for (SensorDescription desc : availableSensors) {
			if (desc.isBound()) {
				desc.getServiceObject().startRecording();
			}
		}
	}

	@Override
	public void stopRecording() {
		for (SensorDescription desc : availableSensors) {
			if (desc.isBound()){
				desc.getServiceObject().stopRecording();
			}
		}
	}

	@Override
	public void storeData() {
		// TODO Auto-generated method stub
	}	
	
	@Override
	public List<SensorValue> getLastValues() {
		LinkedList<SensorValue> Values = new LinkedList<SensorValue>();
		for (SensorDescription desc : availableSensors) {
			if (desc.isRecording()) {
				Values.add(desc.getServiceObject().getLastValue());
			}
		}
		return Values;
	}

	public List<SensorValueBatch> pullAllValues() {
		LinkedList<SensorValueBatch> Batches = new LinkedList<SensorValueBatch>();
		for (SensorDescription desc : availableSensors) {
			if (desc.isRecording()) {
				SensorValueBatch batch = new SensorValueBatch();
				batch.values = (List<SensorValue>) desc.getServiceObject().pullData();
				batch.meta   = desc.getServiceObject().getMetadata();				
			}
		}
		return Batches;
	}

	
}
