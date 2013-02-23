package eu.livegov.mobilesensing.sensors;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public abstract class SensorService extends Service {

	public abstract Metadata getMetaData();
	public abstract SensorValue getLastValue();
	public abstract List<SensorValue> pullData();
	
	public class SensorServiceBinder extends Binder {

		public SensorService getService() {
			return SensorService.this;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new SensorServiceBinder();
	}

}
