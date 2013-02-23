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
	
	Sensor Sensor;
	SensorManager sensorManager;
	Metadata  meta;
	
	//initialize AccelerometerSensorValue
	AccelerometerSensorValue lastValue = new AccelerometerSensorValue(-1,-1,-1,-1);
	
	//Queue with accelerometer sensor data
	private LinkedBlockingQueue<AccelerometerSensorValue> sensorValueQueue = new LinkedBlockingQueue<AccelerometerSensorValue>();
	
	@Override
	public Metadata getMetadata() {
		return meta;
		}
	
	@Override
	public SensorValue getLastValue() {
		return lastValue;
	}
	
	@SuppressWarnings("static-access")
	public void onCreate() {
		Log.i(LOG_TAG, "Accelerometer created");
		
		// Check for Accelerometer Sensor
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);		
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

		if (sensorList.size() > 0) {
			Sensor = sensorList.get(0);
		} else {
			Log.e(LOG_TAG, "Seonsor not found!");
			// Stop Service Throw Exception!
		}
		
		meta = new Metadata(SENSOR_NAME);
		meta.autoSetSensorInfo(Sensor);
	}
	
	@Override
	public void startRecording() {
		// Start service
		Log.i(LOG_TAG, "Accelerometer RECORDING!");
		
		sensorManager.registerListener(
				Listener, 
				Sensor,
				SensorManager.SENSOR_DELAY_FASTEST
				);
	}

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
			
			sensorValueQueue.add(lastValue);		
//			writeLog();
		}
	};

	@Override
	public void stopRecording() {
		// Stop Service
		sensorManager.unregisterListener(Listener);
		Log.i(LOG_TAG, "Accelerometer stopped Recording.");
	}

	

	
	@Override
	public List<? extends SensorValue> pullData() {
		// Returns all SensorValues
		// Clears the internal data queue
		int queueSize = sensorValueQueue.size();
		List<AccelerometerSensorValue> values = new ArrayList<AccelerometerSensorValue>(
				queueSize);

		Iterator<AccelerometerSensorValue> iterator = sensorValueQueue.iterator();
		while (iterator.hasNext()) {
			AccelerometerSensorValue iteratorValue = (AccelerometerSensorValue) iterator
					.next();
			values.add(iteratorValue);
		}

		return values;
	}

	@Override
	public void putSensorValue(SensorValue value) {
		lastValue = (AccelerometerSensorValue) value;
		sensorValueQueue.add(lastValue);
	}

	public void writeLog(){
		Log.i(LOG_TAG, lastValue.toString() );
	}
}
