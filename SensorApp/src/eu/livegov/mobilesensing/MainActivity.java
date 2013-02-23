package eu.livegov.mobilesensing;

import eu.livegov.mobilesensing.sensors.AccelerometerSensorService;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.content.Context;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}
	
	
	public void startButton(View view){
		Context context = getApplicationContext();
		Intent service = new Intent(context, AccelerometerSensorService.class);
		context.startService(service);
	}
	
	public void stopButton(View view){
		Context context = getApplicationContext();
		Intent service = new Intent(context, AccelerometerSensorService.class);
		stopService(service);
	}
	
	
	
	
	
	
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
