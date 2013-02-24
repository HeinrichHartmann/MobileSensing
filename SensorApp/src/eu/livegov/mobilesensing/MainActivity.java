package eu.livegov.mobilesensing;

import eu.livegov.mobilesensing.manager.SensorManager;
import eu.livegov.mobilesensing.sensors.gps.GpsSensorService;
import eu.livegov.mobilesensing.sensors.gyroscope.GyroscopeSensorService;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.content.Context;
import eu.livegov.mobilesensing.Constants;

public class MainActivity extends Activity {
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	public void managerStart(View view){
		Log.i(Constants.LOG_TAG, "Clicked ManagerStart");
		Context context = getApplicationContext();
		Intent service = new Intent(context, SensorManager.class);
		context.startService(service);
	}
	
	public void managerStop(View view){
		Log.i(Constants.LOG_TAG, "Clicked ManagerStop");
		Context context = getApplicationContext();
		Intent service = new Intent(context, SensorManager.class);
		context.stopService(service);
	}

	public void managerBind(View view){
		Log.i(Constants.LOG_TAG, "Clicked ManagerBind");
		Context context = getApplicationContext();
		Intent service = new Intent(context, SensorManager.class);
		service.setAction(SensorManager.ACTION_BIND);
		context.startService(service);
	}
	

	public void startRecording(View view){
		Context context = getApplicationContext();
		Intent service = new Intent(context, SensorManager.class);
		service.setAction(SensorManager.ACTION_START_RECORDING);
		context.startService(service);
	}
	
	public void stopRecording(View view){
		Context context = getApplicationContext();
		Intent service = new Intent(context, SensorManager.class);
		service.setAction(SensorManager.ACTION_STOP_RECORDING);
		context.startService(service);
	}
	public void writeData(View view){
		Context context = getApplicationContext();
		Intent service = new Intent(context, SensorManager.class);
		service.setAction(SensorManager.ACTION_WRITE_DATA);
		context.startService(service);
	}

	public void statusAll(View view){
		Log.i(Constants.LOG_TAG, "Clicked Button");
		Context context = getApplicationContext();
		Intent service = new Intent(context, SensorManager.class);
		service.setAction(SensorManager.ACTION_STATUS);
		context.startService(service);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
