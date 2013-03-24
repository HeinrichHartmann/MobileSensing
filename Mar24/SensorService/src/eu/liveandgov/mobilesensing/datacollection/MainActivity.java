package eu.liveandgov.mobilesensing.datacollection;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ToggleButton;
/**
 * 
 * Simple GUI for the Live+Gov Sensor API
 * 
 * Sends Intents to SensorService 
 * 
 * @author hartmann
 *
 */
public class MainActivity extends Activity {
	/*
	 * GUI Components
	 */
	private static ToggleButton serviceButton; 
	private static ToggleButton recordingButton; 
	private static Button transferButton;
	private static Spinner configDropdown;
	
	public void initGuiCpmponents() {
		serviceButton   = (ToggleButton) findViewById(R.id.serviceButton);
		recordingButton = (ToggleButton) findViewById(R.id.recordingButton);
		transferButton  = (Button)       findViewById(R.id.transferButton);
		configDropdown  = (Spinner)      findViewById(R.id.configDropdown);
	}
	
	/*
	 * LIFECYCLE MANAGEMENT
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initGuiCpmponents();
		setupConfigDropdown();
	}

	private void setupConfigDropdown() {
		configDropdown.setOnItemSelectedListener(new
				OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Log("Selected Sensor Preset " + arg2);
			}
			
			public void onNothingSelected(android.widget.AdapterView<?> arg0) {};
			}
		);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		
		return true;
	}

	/**
	 * LOG Messages
	 */
	private void Log(String msg){
		android.util.Log.i("MAIN", msg);
	}

	
	/*
	 * BUTTON HANDLING
	 */
	
	public void serviceButtonClick(View v){
		boolean newState = serviceButton.isChecked();
		Log("Service Button Clicked. New state: " + newState);

		if (newState == true) {
			sendAction(SensorService.ACTION_START_SERVICE);
		} else {
			sendAction(SensorService.ACTION_STOP_SERVICE);
		}
	}
	
	public void recordingButtonClick(View v){
		Log("Recording Button Clicked. New state: " + ((ToggleButton) v).isChecked());
	}
	
	public void transferButtonClick(View v){
		Log("Transfer Button Clicked");
	}

	/**
	 * Send message to SensorService
	 * @param action
	 */
	private void sendAction(String action) {
		Intent intent = new Intent(getBaseContext(), SensorService.class);
		intent.setAction(action);
		getBaseContext().startService(intent);
	}
	
}
