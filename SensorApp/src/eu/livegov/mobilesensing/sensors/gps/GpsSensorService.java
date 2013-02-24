package eu.livegov.mobilesensing.sensors.gps;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Context;
import android.hardware.Sensor;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import eu.livegov.mobilesensing.Constants;
import eu.livegov.mobilesensing.sensors.Metadata;
import eu.livegov.mobilesensing.sensors.SensorService;
import eu.livegov.mobilesensing.sensors.SensorValue;



public class GpsSensorService extends SensorService{
	public static final String SENSOR_NAME = "GPS";
	public static final String LOG_TAG = Constants.LOG_TAG;
	
	// Metadata about sensor
	Metadata  meta;
	
	// initialize GpsSensorValue
	GpsSensorValue lastValue = new GpsSensorValue(-1,-1,-1,-1);
	
	// Queue with Gps sensor data
	private LinkedBlockingQueue<GpsSensorValue> valueQueue = new LinkedBlockingQueue<GpsSensorValue>();
	

	// native Android Sensor classes
	Sensor androidSensor;
	LocationManager androidLocationManager;

	//////// Startup/Shutdown Service and Recording /////////
	
	public void onCreate() {
		// Called when service start

		// Check for Gps Sensor
		androidLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);		

		List<String> sensorList = androidLocationManager.getProviders(true);
		if (sensorList.size() > 0) {
			//androidSensor = sensorList.get(0);
		} else {
			Log.e(LOG_TAG, "Seonsor not found!");
			// Stop Service Throw Exception!
			stopSelf();
		}
		
		// set meta info
		meta = new Metadata(SENSOR_NAME);
		meta.autoSetSensorInfo(androidSensor);
		
		Log.i(LOG_TAG, "GPS service started");
	}
	
	@Override
	public void startRecording() {
		Log.i(LOG_TAG, "GPS RECORDING!");
		
		androidLocationManager.requestLocationUpdates(
				LocationManager.GPS_PROVIDER, 
				0, 
				0, 
				Listener
				);
	}
	// Location listener for recording
	 LocationListener Listener = new LocationListener(){
		    @Override
		    public void onLocationChanged(Location location) {
		        if (location != null) {
		        	lastValue = new GpsSensorValue(
		        			location.getTime(),
		        			(float) location.getLatitude(), 
		        			(float) location.getLongitude(), 
		        			(float) location.getAltitude()
							);
					valueQueue.add(lastValue);
		        }
		    }

			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {
				// TODO Auto-generated method stub
				
			}

		
		    };
		    
	@Override
	public void stopRecording() {
		// Stop Service
		androidLocationManager.removeUpdates(Listener);
		Log.i(LOG_TAG, "GPS stopped Recording.");		
	}
	
	//////// Method Implementation /////////
	
	@Override
	public Metadata getMetadata() {
		return meta;
	}

	@Override
	public SensorValue getLastValue() {
		return lastValue;
	}

	@Override
	public List<? extends SensorValue> pullData() {
		int queueSize = valueQueue.size();

		// Returns all SensorValues
		List<GpsSensorValue> values = new ArrayList<GpsSensorValue>(
				queueSize);

		Iterator<GpsSensorValue> iterator = valueQueue.iterator();
		while (iterator.hasNext()) {
			GpsSensorValue iteratorValue = (GpsSensorValue) iterator
					.next();
			values.add(iteratorValue);
		}

		// Clears the internal data queue
		valueQueue.clear();
		
		return values;
	}

	@Override
	public void putSensorValue(SensorValue value) {
		lastValue = (GpsSensorValue) value;
		valueQueue.add(lastValue);
	}
	
	public void writeLog(){
		Log.i(LOG_TAG, lastValue.toString() );
	}





}
