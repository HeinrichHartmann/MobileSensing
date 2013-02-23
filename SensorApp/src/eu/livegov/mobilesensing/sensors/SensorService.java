package eu.livegov.mobilesensing.sensors;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public abstract class SensorService extends Service {
	/*
	 * Methods to be implemented by Sensor Services 
	 */
	public abstract Metadata getMetadata();
	public abstract SensorValue getLastValue();
	public abstract void putSensorValue(SensorValue value);
	
	public abstract List<? extends SensorValue> pullData();
	
	/**
	 * starts recording of sensor values; is called by onBind()
	 */
	public abstract void start();
	
	/**
	 * stops recording of sensor values; is called by onUnBind()
	 */
	public abstract void stop();
	
	public class SensorServiceBinder extends Binder {

		public SensorService getService() {
			return SensorService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		start();
		return new SensorServiceBinder();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		stop();
		return false;
	}

}
