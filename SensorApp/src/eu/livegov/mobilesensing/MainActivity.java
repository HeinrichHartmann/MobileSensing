package eu.livegov.mobilesensing;

import eu.livegov.mobilesensing.manager.SensorManager;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;
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
	
	
	public void startButton(View view){
		Log.i(Constants.LOG_TAG, "Clicked Start");

	}
	
	public void stopButton(View view){
		Log.i(Constants.LOG_TAG, "Clicked Stop");

	}
	
	public void managerStart(View view){
		Log.i(Constants.LOG_TAG, "Clicked ManagerStart");
		Context context = getApplicationContext();
		Intent service = new Intent(context, SensorManager.class);
		context.startService(service);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
