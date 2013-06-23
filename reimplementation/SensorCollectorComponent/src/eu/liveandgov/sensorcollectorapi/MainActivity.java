package eu.liveandgov.sensorcollectorapi;

import eu.liveandgov.sensorcollectorapi.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onButtonStartServiceClick(View v) {
		Intent intent = new Intent(IntentConstants.ACTION_SERVICE_START);
		getApplicationContext().startService(intent);
	}

	public void onButtonStopServiceClick(View v) {
		Intent intent = new Intent(IntentConstants.ACTION_SERVICE_STOP);
		getApplicationContext().startService(intent);
	}

	public void onButtonSamplingOnClick(View v) {
		Intent intent = new Intent(IntentConstants.ACTION_SAMPLING_ENABLE);
		getApplicationContext().startService(intent);
	}

	public void onButtonSamplingOffClick(View v) {
		Intent intent = new Intent(IntentConstants.ACTION_SAMPLING_DISABLE);
		getApplicationContext().startService(intent);
	}

	public void onButtonStorageOnClick(View v) {
		Intent intent = new Intent(IntentConstants.ACTION_SAMPLESTORAGE_ENABLE);
		getApplicationContext().startService(intent);
	}

	public void onButtonStorageOffClick(View v) {
		Intent intent = new Intent(IntentConstants.ACTION_SAMPLESTORAGE_DISABLE);
		getApplicationContext().startService(intent);
	}

	public void onButtonTransferOnClick(View v) {
		Intent intent = new Intent(IntentConstants.ACTION_SAMPLETRANSFER_ENABLE);
		getApplicationContext().startService(intent);
	}

	public void onButtonTransferOffClick(View v) {
		Intent intent = new Intent(
				IntentConstants.ACTION_SAMPLETRANSFER_DISABLE);
		getApplicationContext().startService(intent);
	}

	public void onButtonTriggerTransferClick(View v) {
		Intent intent = new Intent(
				IntentConstants.ACTION_SAMPLETRANSFER_TRIGGER);
		getApplicationContext().startService(intent);
	}

	public void onButtonBroadcastOnClick(View v) {
		Intent intent = new Intent(
				IntentConstants.ACTION_SAMPLEBROADCAST_ENABLE);
		getApplicationContext().startService(intent);
	}

	public void onButtonBroadcastOffClick(View v) {
		Intent intent = new Intent(
				IntentConstants.ACTION_SAMPLEBROADCAST_DISABLE);
		getApplicationContext().startService(intent);
	}

}
