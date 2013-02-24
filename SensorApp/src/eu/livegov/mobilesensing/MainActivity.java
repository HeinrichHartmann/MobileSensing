package eu.livegov.mobilesensing;

import eu.livegov.mobilesensing.manager.SensorManager;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;
import eu.livegov.mobilesensing.sensors.gps.GpsSensorService;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import eu.livegov.mobilesensing.Constants;

public class MainActivity extends Activity {
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	
	public void startButton(View view){
		Log.i(Constants.LOG_TAG, "Clicked Start");
		Context context = getApplicationContext();
		Intent service = new Intent(context, AccelerometerSensorService.class);
		context.startService(service);
	}
	
	public void stopButton(View view){
		Log.i(Constants.LOG_TAG, "Clicked Stop");
		Context context = getApplicationContext();
		Intent service = new Intent(context, AccelerometerSensorService.class);
		stopService(service);
	}
	

	public void managerStart(View view){
		Log.i(Constants.LOG_TAG, "Clicked ManagerStart");
		Context context = getApplicationContext();
		Intent service = new Intent(context, SensorManager.class);
		context.startService(service);
	}
	
	public void buttongps (View view){

		Context context = getApplicationContext();
		Intent service = new Intent(context, GpsSensorService.class);
		context.startService(service);
		Log.i(Constants.LOG_TAG, "Clicked GPS");
	}
	
	public void buttongpsstop (View view){

		Context context = getApplicationContext();
		Intent service = new Intent(context, GpsSensorService.class);
		context.stopService(service);
		Log.i(Constants.LOG_TAG, "Clicked GPS STOP");
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
