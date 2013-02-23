package eu.livegov.mobilesensing.test;

import java.util.List;

import eu.livegov.mobilesensing.sensors.SensorValue;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorValue;
import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

public class AccelerometerSensorServiceTest extends ServiceTestCase<AccelerometerSensorService> {

	public AccelerometerSensorServiceTest() {
		super(AccelerometerSensorService.class);
	}
 	
	AccelerometerSensorService mService;
    	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

        startService(
        		new Intent(getContext(),  
        		AccelerometerSensorService.class));

        mService = getService();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		// Stop Service
		getContext().stopService(
				new Intent(	getContext(),
				AccelerometerSensorService.class
				));
	}
		
	public void testMetaData() {
		assertTrue(mService != null);
		assertTrue(mService.getMetadata().getServiceName() == AccelerometerSensorService.SENSOR_NAME);
	}
	
	public void testLastValue(){
		assertTrue(mService != null);
		
		// Fill in Dummy Value
		AccelerometerSensorValue dummyValue = new AccelerometerSensorValue(0, 1, 2, 3);
		mService.putSensorValue(dummyValue);

		Log.i("TEST",dummyValue.toString());
		Log.i("TEST",mService.getLastValue().toString());
		assertTrue(mService.getLastValue().equals(dummyValue));
	}
	
	public void testPullData() {
		assertTrue(mService != null);

		// Fill in Dummy Value
		AccelerometerSensorValue dummyValue0 = new AccelerometerSensorValue(0, 1, 2, 3);
		AccelerometerSensorValue dummyValue1 = new AccelerometerSensorValue(0, 1, 2, 4);
		AccelerometerSensorValue dummyValue2 = new AccelerometerSensorValue(0, 1, 2, 5);
		AccelerometerSensorValue dummyValue3 = new AccelerometerSensorValue(0, 1, 2, 6);

		mService.putSensorValue(dummyValue0);
		mService.putSensorValue(dummyValue1);
		mService.putSensorValue(dummyValue2);
		mService.putSensorValue(dummyValue3);

		List<? extends SensorValue> data = mService.pullData();
		assertFalse(data.isEmpty());
		assertTrue(data.get(0).equals(dummyValue0));
		assertTrue(data.get(1).equals(dummyValue1));
		assertTrue(data.get(2).equals(dummyValue2));
		assertTrue(data.get(3).equals(dummyValue3));
	}
}
