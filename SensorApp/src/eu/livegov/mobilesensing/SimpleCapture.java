package eu.livegov.mobilesensing;

import eu.livegov.mobilesensing.sensors.SensorValue;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SimpleCapture extends  Activity{
	
	TextView gps, accelerometer;
	AccelerometerSensorService accelerometerService = new AccelerometerSensorService();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_capture);
		
		accelerometer = (TextView) findViewById(R.id.textAccelerometer);
		gps = (TextView) findViewById(R.id.textGPS);
		
		
	}
	public void startRecording(View view){
	
		gps.setText(String.valueOf(accelerometerService.getLastValue()));
	
	}
	public void stopRecording(View view){

		
	}
	
	

}
