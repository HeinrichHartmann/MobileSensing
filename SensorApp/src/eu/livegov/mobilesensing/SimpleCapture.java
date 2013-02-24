package eu.livegov.mobilesensing;

import eu.livegov.mobilesensing.R;
import eu.livegov.mobilesensing.manager.SensorManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class SimpleCapture extends Activity {

	private final static String LOG_TAG = "SimpleCaptureActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_capture);

		Log.i(LOG_TAG, "starting SensorManager");
		Intent service = new Intent(this, SensorManager.class);
		startService(service);

	}

	public void startRecording(View view) {
		Log.i(LOG_TAG, "sending ACTION_START_RECORDING");
		Intent service = new Intent(this, SensorManager.class);
		service.setAction(SensorManager.ACTION_START_RECORDING);
		startService(service);
	}

	public void stopRecording(View view) {
		Log.i(LOG_TAG, "sending ACTION_STOP_RECORDING");
		Intent service = new Intent(this, SensorManager.class);
		service.setAction(SensorManager.ACTION_STOP_RECORDING);
		startService(service);
	}

	public void storeData(View view) {
		Log.i(LOG_TAG, "sending ACTION_WRITE_DATA");
		Intent service = new Intent(this, SensorManager.class);
		service.setAction(SensorManager.ACTION_WRITE_DATA);
		startService(service);
	}

}
