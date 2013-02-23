package eu.livegov.mobilesensing;



import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class AccService extends Service {
	
	private String TAG ="Accelerometer Service";
	Sensor Sensor;
	SensorManager sensorManager;
	Accelerometer acc = new Accelerometer();
	boolean present;
	
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

			sensorManager.registerListener(acc.accListener, Sensor, SensorManager.SENSOR_DELAY_FASTEST);}
	}
	
	public int onStartCommand(Intent intent, int flags, int startId) {
		writeLog();
		return Service.START_NOT_STICKY;
	}
	
	private class AccValue{
		long timestamp;
		float[] values;
	}
	
	public List<AccValue> pullData(){
		
		List<AccValue> values = new ArrayList<AccValue>();
		return values;
	}
	
	public void writeLog(){
		Log.i(TAG, "Value X: " + Float.toString(acc.getX()));
		Log.i(TAG, "Value Y: " + Float.toString(acc.getY()));
		Log.i(TAG, "Value Z: " + Float.toString(acc.getZ()));
		Log.i(TAG, "Value Timestamp: " + Long.toString(acc.getTimestamp()));
	}
	
	@Override
	public void onDestroy(){
		Log.i(TAG, "Service stopped");
	}
	
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
