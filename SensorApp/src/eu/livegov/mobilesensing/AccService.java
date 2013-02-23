package eu.livegov.mobilesensing;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class AccService extends Service {
	
	private String TAG ="Accelerometer Service";
	Sensor Sensor;
	SensorManager sensorManager;
	//Accelerometer acc = new Accelerometer();
	boolean present;
	float x_value = -1;
	float y_value = -1;
	float z_value = -1;
	long timestamp = -1;
	
	private LinkedBlockingQueue<AccValue> accQueue = new LinkedBlockingQueue<AccValue>();
	
	
	
	@SuppressWarnings("static-access")
	@Override
	public void onCreate(){
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		Log.i(TAG, "Service started");
		List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {
			present = true;
			Sensor = sensorList.get(0);
			
		} else {
			present = false;
		}
		if (present){

			sensorManager.registerListener(Listener, Sensor, SensorManager.SENSOR_DELAY_FASTEST);}
	}
	SensorEventListener Listener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {

		}
		
		
		public void onSensorChanged(SensorEvent arg0) {
			
			x_value = arg0.values[0];
			y_value = arg0.values[1];
			z_value = arg0.values[2];
			timestamp = arg0.timestamp;
			writeLog();

			
		}
	};
	
	public float getX(){
		return x_value;
	}
	public float getY(){
		return y_value;
	}
	public float getZ(){
		return z_value;
	}
	public long getTimestamp(){
		return timestamp;
	}
	public String getName(SensorManager sensorManager) {
		String accName;
		
		
		List<Sensor> sensorList = sensorManager.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {

			Sensor accSensor = sensorList.get(0);
			accName = accSensor.getName();

		} else {
			accName = "kein Sensor vorhanden";
		}

		return accName;
	}

	
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		return Service.START_NOT_STICKY;
	}
	
	private class AccValue{
		long timestamp;
		float x;
		float y;
		float z;
		
		public AccValue(long timestamp, float x, float y, float z){
			this.timestamp = timestamp;
			this.x = x;
			this.y = y;
			this.z = z;
		}
		
		
	}
	
	public List<AccValue> pullData(){
		
		List<AccValue> values = new ArrayList<AccValue>();
		return values;
	}
	
	public void writeLog(){
		Log.i(TAG, "Value X: " + Float.toString(getX()));
		Log.i(TAG, "Value Y: " + Float.toString(getY()));
		Log.i(TAG, "Value Z: " + Float.toString(getZ()));
		Log.i(TAG, "Value Timestamp: " + Long.toString(getTimestamp()));
	}
	
	@Override
	public void onDestroy(){
		sensorManager.unregisterListener(Listener);
		Log.i(TAG, "Service stopped");
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
