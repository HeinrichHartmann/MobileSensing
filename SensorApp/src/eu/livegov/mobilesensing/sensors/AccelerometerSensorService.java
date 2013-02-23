package eu.livegov.mobilesensing.sensors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerSensorService extends SensorService {


	private String LOG_TAG ="Accelerometer Service";
	Sensor Sensor;
	SensorManager sensorManager;
	boolean present;
	//initialize AccelerometerSensorValue
	AccelerometerSensorValue lastValue = new AccelerometerSensorValue(-1,-1,-1,-1);
	
	//Queue with accelerometer sensor data (timestamp,x,y,z)
	private LinkedBlockingQueue<AccelerometerSensorValue> accQueue = new LinkedBlockingQueue<AccelerometerSensorValue>();
	
	
	
	public class AccelerometerMetadata implements Metadata {

		String name;

		public AccelerometerMetadata(String name) {
			this.name = name;
		}

		@Override
		public String getName() {
			// TODO Auto-generated method stub
			AccelerometerMetadata name = (AccelerometerMetadata) getMetadata();
			String accName = name.name;
			return accName;
		}
	}

	public static class AccelerometerSensorValue implements SensorValue {
		
	//Constructor	
	public AccelerometerSensorValue(long timestamp, float x, float y, float z){
	this.timestamp = timestamp;
	this.x = x;
	this.y = y;
	this.z = z;
	}
	
	long timestamp;
	float x;
	float y;
	float z;
	

		@Override
		public long getTimestamp() {
			// TODO Auto-generated method stub
		
			return 0;
		}
	}

	@Override
	public Metadata getMetadata() {
		// Returns Object containing basic information about the sensor
		String accName;
		if (present)
			accName = Sensor.getName();
		else
			accName = "kein Sensor vorhanden";
		AccelerometerMetadata m = new AccelerometerMetadata(accName);
		return m;
	}

	@Override
	public SensorValue getLastValue() {
		// Returns last Recorded sensor value
		return lastValue;
	}

	@Override
	public List<? extends SensorValue> pullData() {
		// Returns all SensorValues
		// Clears the internal data queue
		int queueSize = accQueue.size();
		List<AccelerometerSensorValue> values = new ArrayList<AccelerometerSensorValue>(
				queueSize);

		Iterator<AccelerometerSensorValue> iterator = accQueue.iterator();
		while (iterator.hasNext()) {
			AccelerometerSensorValue iteratorValue = (AccelerometerSensorValue) iterator
					.next();
			values.add(iteratorValue);
		}

		return values;
	}

	@SuppressWarnings("static-access")
	public void onCreate() {
		// Check for Accelerometer Sensor
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		Log.i(LOG_TAG, "Service started");
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);

		if (sensorList.size() > 0) {
			present = true;
			Sensor = sensorList.get(0);

		} else {
			present = false;
		}
	}

	@Override
	public void start() {
		// Start service
		if (present)
			sensorManager.registerListener(Listener, Sensor,
					SensorManager.SENSOR_DELAY_FASTEST);

	}

	SensorEventListener Listener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {

		}

		public void onSensorChanged(SensorEvent arg0) {

			lastValue = new AccelerometerSensorValue(arg0.timestamp,
					arg0.values[0], arg0.values[1], arg0.values[2]);
			accQueue.add(lastValue);
			writeLog();

		}
	};

	@Override
	public void stop() {
		// Stop Service
		sensorManager.unregisterListener(Listener);
		Log.i(LOG_TAG, "Service stopped");
	}

	@Override
	public void putSensorValue(SensorValue value) {
		// TODO Auto-generated method stub

	}


	public void writeLog(){
		Log.i(LOG_TAG, "Value X: " + Float.toString(lastValue.x));
		Log.i(LOG_TAG, "Value Y: " + Float.toString(lastValue.y));
		Log.i(LOG_TAG, "Value Z: " + Float.toString(lastValue.z));
		Log.i(LOG_TAG, "Value Timestamp: " + Long.toString(lastValue.timestamp));

	}
}
