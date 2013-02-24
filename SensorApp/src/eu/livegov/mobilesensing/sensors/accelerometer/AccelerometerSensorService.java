package eu.livegov.mobilesensing.sensors.accelerometer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import eu.livegov.mobilesensing.Constants;
import eu.livegov.mobilesensing.sensors.Metadata;
import eu.livegov.mobilesensing.sensors.SensorService;
import eu.livegov.mobilesensing.sensors.SensorValue;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerSensorService extends SensorService {
	public static final String SENSOR_NAME = "Accelerometer";
	public static final String LOG_TAG = Constants.LOG_TAG;
	
	// Metadata about sensor
	Metadata  meta;
	
	// initialize AccelerometerSensorValue
	AccelerometerSensorValue lastValue = new AccelerometerSensorValue(-1,-1,-1,-1);
	
	// Queue with accelerometer sensor data
	private LinkedBlockingQueue<AccelerometerSensorValue> valueQueue = new LinkedBlockingQueue<AccelerometerSensorValue>();

	// native Android Sensor classes
	Sensor androidSensor;
	SensorManager androidSensorManager;

	//////// Startup/Shutdown Service and Recording /////////
	
	public void onCreate() {
		super.onCreate();
		// Called when service start

		// Check for Accelerometer Sensor
		androidSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);		

		@SuppressWarnings("static-access")
		List<Sensor> sensorList = androidSensorManager.getSensorList(androidSensor.TYPE_ACCELEROMETER);

		if (sensorList.size() > 0) {
			androidSensor = sensorList.get(0);
		} else {
			Log.e(LOG_TAG, "Sensor not found!");
			// Stop Service Throw Exception!
			stopSelf();
		}
		
		/* set meta info
		meta = new Metadata(SENSOR_NAME);
		meta.autoSetSensorInfo(androidSensor);
		*/
		
		Log.i(LOG_TAG, "Accelerometer service started");
		startRecording();
	}
	
	@Override
	public void startRecording() {
		super.startRecording();

		Log.i(LOG_TAG, "Accelerometer RECORDING!");

		androidSensorManager.registerListener(
				Listener, 
				androidSensor,
				SensorManager.SENSOR_DELAY_FASTEST
				);
	}
	
	// Event listener for recording
	SensorEventListener Listener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {

		}

		public void onSensorChanged(SensorEvent arg0) {
			// Read out sensor event
			lastValue = new AccelerometerSensorValue(
					arg0.timestamp,
					arg0.values[0], 
					arg0.values[1], 
					arg0.values[2]
					);
			
			valueQueue.add(lastValue);		
		}
	};
	

	@Override
	public void stopRecording() {
		super.stopRecording();
		
		// Stop Service
		androidSensorManager.unregisterListener(Listener);
		Log.i(LOG_TAG, "Accelerometer stopped Recording.");
	}
	
	//////// Method Implementation /////////
	
	@Override
	public Metadata getMetadata() {
		return meta;
		}
	
	@Override
	public SensorValue getLastValue() {
		return lastValue;
	}

	
	@Override
	public List<? extends SensorValue> pullData() {
		int queueSize = valueQueue.size();

		// Returns all SensorValues
		List<AccelerometerSensorValue> values = new ArrayList<AccelerometerSensorValue>(
				queueSize);

		Iterator<AccelerometerSensorValue> iterator = valueQueue.iterator();
		while (iterator.hasNext()) {
			AccelerometerSensorValue iteratorValue = (AccelerometerSensorValue) iterator
					.next();
			values.add(iteratorValue);
		}

		// Clears the internal data queue
		valueQueue.clear();
		
		return values;
	}

	@Override
	public void putSensorValue(SensorValue value) {
		lastValue = (AccelerometerSensorValue) value;
		valueQueue.add(lastValue);
	}

	public void writeLog(){
		Log.i(LOG_TAG, lastValue.toString() );
	}
}
