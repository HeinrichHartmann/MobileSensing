package de.mobilesensing.dummy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FirstService extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		Log.i(Constants.LOG_TAG, "Bound service");
		return null;
	}

	@Override
	public void onCreate() {
		Log.i(Constants.LOG_TAG, "Created Service");
		super.onCreate();
	}
	
	@Override
	public void onDestroy() {
		Log.i(Constants.LOG_TAG, "Destroyed Service");
		super.onDestroy();
	}
	
}
