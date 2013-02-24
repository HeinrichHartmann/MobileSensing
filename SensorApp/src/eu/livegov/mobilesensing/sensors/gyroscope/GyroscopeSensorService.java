package eu.livegov.mobilesensing.sensors.gyroscope;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import eu.livegov.mobilesensing.Constants;
import eu.livegov.mobilesensing.sensors.Metadata;
import eu.livegov.mobilesensing.sensors.SensorService;
import eu.livegov.mobilesensing.sensors.SensorValue;

public class GyroscopeSensorService extends SensorService{
	public static final String SENSOR_NAME = "Gyroscope";
	public static final String LOG_TAG = Constants.LOG_TAG;
	
	public static String getSensorName() {
		return SENSOR_NAME;
	};
	
	// Metadata about sensor
	Metadata  meta;
	
	// initialize GyroscopeSensorValue
	GyroscopeSensorValue lastValue = new GyroscopeSensorValue(-1,-1,-1,-1);
	
	// Queue with Gyroscope sensor data
	private LinkedBlockingQueue<GyroscopeSensorValue> valueQueue = new LinkedBlockingQueue<GyroscopeSensorValue>();

	// native Android Sensor classes
	Sensor androidSensor;
	SensorManager androidSensorManager;

	//////// Startup/Shutdown Service and Recording /////////
	
	public void onCreate() {
		super.onCreate();
		// Called when service start

		// Check for Gyroscope Sensor
		androidSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);		

		@SuppressWarnings("static-access")
		List<Sensor> sensorList = androidSensorManager.getSensorList(androidSensor.TYPE_GYROSCOPE);

		if (sensorList.size() > 0) {
			androidSensor = sensorList.get(0);
		} else {
			Log.e(LOG_TAG, "Sensor not found!");
			// Stop Service Throw Exception!
			stopSelf();
		}
		
		
		meta = new Metadata(SENSOR_NAME);
		meta.autoSetSensorInfo(androidSensor);
		
		
		Log.i(LOG_TAG, "Gyroscope service started");
		//startRecording();
	}
	@Override
	public void startRecording() {
		super.startRecording();

		Log.i(LOG_TAG, "Gyroscope RECORDING!");

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
			lastValue = new GyroscopeSensorValue(
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
		Log.i(LOG_TAG, "Gyroscope stopped Recording.");
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
		List<GyroscopeSensorValue> values = new ArrayList<GyroscopeSensorValue>(
				queueSize);

		Iterator<GyroscopeSensorValue> iterator = valueQueue.iterator();
		while (iterator.hasNext()) {
			GyroscopeSensorValue iteratorValue = (GyroscopeSensorValue) iterator
					.next();
			values.add(iteratorValue);
		}

		// Clears the internal data queue
		valueQueue.clear();
		
		return values;
	}


	@Override
	public void putSensorValue(SensorValue value) {
		lastValue = (GyroscopeSensorValue) value;
		valueQueue.add(lastValue);
	}
	
	public void writeLog(){
		Log.i(LOG_TAG, lastValue.toString() );
	}

}
