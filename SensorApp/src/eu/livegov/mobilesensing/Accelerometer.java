package eu.livegov.mobilesensing;

import java.util.List;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.annotation.SuppressLint;




public class Accelerometer {
	//DatabaseAccess und Sensorname als Attribut eingefügt (Malte, WP1)
	float[] values = new float[3];
	float x_value = -1;
	float y_value = -1;
	float z_value = -1;
	float timestamp = -1;
	String sensorname = "Accelerometer";
	Sensor Sensor;

	
	SensorEventListener accListener = new SensorEventListener() {
		@Override
		public void onAccuracyChanged(Sensor arg0, int arg1) {

		}
		
		
		public void onSensorChanged(SensorEvent arg0) {
			
			x_value = arg0.values[0];
			y_value = arg0.values[1];
			z_value = arg0.values[2];
			values[0] = x_value;
			values[1] = y_value;
			values[2] = z_value;
			timestamp = arg0.timestamp;
		}
	};
	
	//geaendert, DatabaseAccess als Uebergabeparameter (Malte, WP1)
	public Accelerometer()
	{
	}
	public float[] getValues(){
		return values;
	}
	
	public String getSensorName(){
		return sensorname;
	}
	

	public float getTimestamp(){
		return timestamp;
	}
	
	
	public float getX(){
		return x_value;
	}
	public float getY(){
		return y_value;
	}
	public float getZ(){
		return z_value;
	}
	
	public String getName(SensorManager sensorManager) {
		String accName;
		
		
		List<Sensor> sensorList = sensorManager.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {

			Sensor accSensor = sensorList.get(0);
			accName = accSensor.getName();

		} else {
			accName = "kein Sensor vorhanden";
		}

		return accName;
	}
	
	protected float getMaxRange(SensorManager sensorManager) {

		float accMaxRange;
		List<Sensor> sensorList = sensorManager
				.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {

			Sensor accSensor = sensorList.get(0);
			accMaxRange = accSensor.getMaximumRange();

		} else {
			accMaxRange = 0;
		}

		return accMaxRange;

	}

	@SuppressLint("NewApi")
	protected int getMinDelay(SensorManager sensorManager) {

		int accMinDelay;
		List<Sensor> sensorList = sensorManager
				.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {

			Sensor accSensor = sensorList.get(0);
			accMinDelay = accSensor.getMinDelay();

		} else {
			accMinDelay = 0;
		}

		return accMinDelay;
	}



	protected float getPower(SensorManager sensorManager) {

		float accPower;
		List<Sensor> sensorList = sensorManager
				.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {

			Sensor accSensor = sensorList.get(0);
			accPower = accSensor.getPower();

		} else {
			accPower = 0;
		}

		return accPower;
	}

	protected float getResolution(SensorManager sensorManager) {

		float accRes;
		List<Sensor> sensorList = sensorManager
				.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {

			Sensor accSensor = sensorList.get(0);
			accRes = accSensor.getResolution();

		} else {
			accRes = 0;
		}

		return accRes;
	}

	protected int getType(SensorManager sensorManager) {

		int accType;
		List<Sensor> sensorList = sensorManager
				.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {

			Sensor accSensor = sensorList.get(0);
			accType = accSensor.getType();

		} else {
			accType = 0;
		}

		return accType;
	}

	protected String getVendor(SensorManager sensorManager) {

		String accVendor;
		List<Sensor> sensorList = sensorManager
				.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {

			Sensor accSensor = sensorList.get(0);
			accVendor = accSensor.getVendor();

		} else {
			accVendor = "kein Sensor vorhanden";
		}

		return accVendor;
	}

	protected int getVersion(SensorManager sensorManager) {

		int accVersion;
		List<Sensor> sensorList = sensorManager
				.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {

			Sensor accSensor = sensorList.get(0);
			accVersion = accSensor.getVersion();

		} else {
			accVersion = 0;
		}

		return accVersion;
	}

	protected String getDesc(SensorManager sensorManager) {

		String accDesc;
		List<Sensor> sensorList = sensorManager
				.getSensorList(android.hardware.Sensor.TYPE_ACCELEROMETER);
		if (sensorList.size() > 0) {

			Sensor accSensor = sensorList.get(0);
			accDesc = accSensor.toString();

		} else {
			accDesc = "keine Sensorbeschreibung vorhanden";
		}

		return accDesc;
	}
}
