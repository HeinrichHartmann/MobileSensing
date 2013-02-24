package eu.livegov.mobilesensing;

import eu.livegov.mobilesensing.R;
import eu.livegov.mobilesensing.manager.SensorManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class SimpleCapture extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_capture);

		Intent service = new Intent(this, SensorManager.class);
		startService(service);

	}

	public void startRecording(View view) {
		Intent service = new Intent(this, SensorManager.class);
		service.setAction(SensorManager.ACTION_START_RECORDING);
		startService(service);
	}

	public void stopRecording(View view) {
		Intent service = new Intent(this, SensorManager.class);
		service.setAction(SensorManager.ACTION_STOP_RECORDING);
		startService(service);
	}

	public void storeData(View view) {
		Intent service = new Intent(this, SensorManager.class);
		service.setAction(SensorManager.ACTION_WRITE_DATA);
		startService(service);
	}

}
