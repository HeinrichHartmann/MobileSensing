package eu.livegov.mobilesensing.manager;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import eu.livegov.mobilesensing.sensors.SensorService;
import eu.livegov.mobilesensing.sensors.SensorService.SensorServiceBinder;

public class SensorDescription {

	private Class<?> cls;
	private boolean connected = false;
	private SensorService service;
	private static final String LOG_TAG = "SensorDescription";

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.i(LOG_TAG, className + " connected");
			SensorServiceBinder binder = (SensorServiceBinder) service;
			SensorDescription.this.service = binder.getService();
			connected = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName className) {
			Log.i(LOG_TAG, "Binding " + className + " Failed");
			connected = false;
		}
	};

	public SensorDescription(Class<?> cls) {
		this.cls = cls;
	}

	public Class<?> getCls() {
		return cls;
	}

	public ServiceConnection getConnection() {
		return connection;
	}

	public SensorService getService() {
		return service;
	}

	public boolean isConnected() {
		return connected;
	}

}
