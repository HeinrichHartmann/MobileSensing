package eu.livegov.mobilesensing.manager;

import java.security.acl.LastOwnerException;
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
import eu.livegov.mobilesensing.sensors.gyroscope.GyroscopeSensorService;

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
	public static final String ACTION_UNBIND = "UNBIND";
	public static final String ACTION_START_RECORDING = "START_RECORDING";
	public static final String ACTION_STOP_RECORDING = "STOP_RECORDING";
	public static final String ACTION_STORE_DATA = "WRITE_DATA";
	public static final String ACTION_SEND_SENSOR_VALUES = "SEND_VALUES";
	public static final String ACTION_STATUS = "STATUS";
	public static final String INTENT_SENSOR_VALUES = "eu.livegov.mobilesensor.SENSOR_VALUE_INTENT";

	// STATICS
	public static boolean running = false;
	
	/**
	 * List of sensors available in the Framework
	 */	
	private static List<SensorDescription> availableSensors = new LinkedList<SensorDescription>();
	public  static Map<String,SensorDescription> getSensorDescription = new HashMap<String,SensorDescription>();
	
	/*
	 * Static Initialization
	 * 
	 * Add available sensors to sensor list
	 */
	static {
		// Generate list of available sensors
		addAvailableSensor(AccelerometerSensorService.class);
		addAvailableSensor(GpsSensorService.class);
		addAvailableSensor(GyroscopeSensorService.class);
	}

	private static void addAvailableSensor(Class<? extends SensorService> sensorClass){
		
		SensorDescription desc = new SensorDescription(sensorClass);
		
		availableSensors.add(desc);
		getSensorDescription.put(desc.getSensorName(), desc);
		
		Log.i(LOG_TAG,"Added sensor "+desc.getSensorName()+" to availableSensors");
	}
	
	/*
	 * SensorManager Startup
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
	 *  Intent Handling
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
		} else if (action.equals(ACTION_UNBIND)){
			unbindAllSensorServices();
		} else if (action.equals(ACTION_START_RECORDING)){
			startRecording();
		} else if (action.equals(ACTION_STOP_RECORDING)) {
			stopRecording();
		} else if (action.equals(ACTION_STATUS)) {
			statusAll();
		} else if (action.equals(ACTION_STORE_DATA)){
			storeData();
		} else if (action.equals(ACTION_SEND_SENSOR_VALUES)) {
			sendLastValues();
		} else {
			Log.e(LOG_TAG,"Unimplemented Action " + action);
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

	/*
	 * Manage Sensor Bindings
	 */

	private void bindSensorService(SensorDescription desc) {
		if (desc.isBound()) return;
		
		Log.i(LOG_TAG, "Binding sensor " + desc.getSensorName() );
		
		ServiceConnection serviceConnection = new ServiceConnection() {
			@Override
			public void onServiceConnected(ComponentName name,
					IBinder binder) {
						registerSensorServiceObject(this, name, binder);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// SensorService crashed hard
				String fullName = name.getClassName();
				String sensorName = fullName.substring(fullName.lastIndexOf('.') + 1);
				Log.i(LOG_TAG,"Service crashed" + sensorName);
				getSensorDescription.get(sensorName).crashed();
			}
		};

		Log.i(LOG_TAG,"Binding " + desc.getSensorName() + " with " + serviceConnection);
		
		bindService(
				new Intent(this, desc.getServiceClass()),
				serviceConnection,
				BIND_AUTO_CREATE
				);
	}
	
	
	private void registerSensorServiceObject(
			ServiceConnection serviceConnection,
			ComponentName name,
			IBinder iBinder){
				
		SensorServiceBinder binder = (SensorServiceBinder) iBinder;
		SensorService sensorService = binder.getService();
		String sensorName = sensorService.getClass().getSimpleName();
		
		if (!binder.startupSuccess) {
			Log.i(LOG_TAG,"SensorStartup " + sensorName + " unusccesfull. Unbinding.");
			unbindService(serviceConnection);
			return;
		}
		
		// Lookup and Update description update
		SensorDescription desc = getSensorDescription.get(sensorName);
		if (desc != null) {
			desc.setServiceObject(sensorService);
			desc.setServiceConnection(serviceConnection);
			Log.i(LOG_TAG,"Sensor " + sensorName + " registered.");			
		} else {
			Log.i(LOG_TAG,"Sensor " + sensorName + " not found in LookupMap. Unbinding.");
			unbindService(serviceConnection);
		}
	}

	private void bindAllSensorSerives(){
		for (SensorDescription desc : availableSensors){
			bindSensorService(desc);
		}
	}
	
	private void unbindSensorService(SensorDescription desc) {
		if (desc.isBound())	{
			Log.i(LOG_TAG,"Unbindung "+desc.getSensorName());
			desc.unbind(this);
		}
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
			Log.i(LOG_TAG, 
			  "Service " + desc.getSensorName() 
			+ "Status " + desc.getStatus()
		);
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
	
	/**
	 * Broadcast Last received SensorValues
	 * used for display in GUI 
	 */
	private void sendLastValues() {
		Log.i(LOG_TAG,"Broadcasting last values");
		Intent valueIntent = new Intent(INTENT_SENSOR_VALUES);
		valueIntent.putExtra("Value", "Data");
		sendBroadcast(valueIntent);
	}
	
	@Override
	public List<SensorValue> getLastValues() {
		LinkedList<SensorValue> Values = new LinkedList<SensorValue>();
		for (SensorDescription desc : availableSensors) {
			if (desc.isRecording()) {
				Values.add(desc.getServiceObject().getLastValue());
				Log.i(LOG_TAG, "lastValue " + desc.getServiceObject().getLastValue().toString());
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
