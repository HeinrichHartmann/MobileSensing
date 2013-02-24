package eu.livegov.mobilesensing;

import java.util.Timer;

import eu.livegov.mobilesensing.R;
import eu.livegov.mobilesensing.R.id;
import eu.livegov.mobilesensing.R.layout;
import eu.livegov.mobilesensing.sensors.SensorValue;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;
import eu.livegov.mobilesensing.sensors.gps.GpsSensorService;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class SimpleCapture extends  Activity{
	
	TextView gps, accelerometer;
	AccelerometerSensorService accelerometerService = new AccelerometerSensorService();
	GpsSensorService gpsService = new GpsSensorService();
	boolean recording;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_capture);
		
		
		
		accelerometer = (TextView) findViewById(R.id.textAccelerometer);
		gps = (TextView) findViewById(R.id.textGPS);
		
		/**
		 * Checkbox-Listener
		 */
		CheckBox repeatChkBx =
			    ( CheckBox ) findViewById( R.id.checkBox1 );
			repeatChkBx.setOnCheckedChangeListener(new OnCheckedChangeListener()
			{
			    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			    {
			        if ( isChecked )
			        {
			           // startRecording();
			        }

			    }
			});
		
		
		
	}
	public void startRecording(View view){
		
		recording=true;
		
		Context context = getApplicationContext();
		Intent service1 = new Intent(context, AccelerometerSensorService.class);
		Intent service2 = new Intent(context, GpsSensorService.class);
		
		context.startService(service1);
		context.startService(service2);
		
		
			
		CountDownTimer timer = new CountDownTimer(1000 ,500){

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTick(long arg0) {
				// TODO Auto-generated method stub
				accelerometer.setText(String.valueOf(accelerometerService.getLastValue()));
				gps.setText(String.valueOf(gpsService.getLastValue()));
			}
			
			
		};
	
		while (recording==true)
		{ timer.start();
		}

	}
	public void stopRecording(View view){
		recording=false;
		accelerometer.setText(String.valueOf(0));
		gps.setText(String.valueOf(0));
		
	}
	
	//Timer timer = new Timer();
	
	
	

	

}

