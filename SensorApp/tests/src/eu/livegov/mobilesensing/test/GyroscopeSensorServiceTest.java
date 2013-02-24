package eu.livegov.mobilesensing.test;

import java.util.List;

import eu.livegov.mobilesensing.sensors.SensorValue;
import eu.livegov.mobilesensing.sensors.gyroscope.GyroscopeSensorService;
import eu.livegov.mobilesensing.sensors.gyroscope.GyroscopeSensorValue;
import android.content.Intent;
import android.test.ServiceTestCase;
import android.util.Log;

public class GyroscopeSensorServiceTest extends
		ServiceTestCase<GyroscopeSensorService> {

	public GyroscopeSensorServiceTest() {
		super(GyroscopeSensorService.class);

	}

	GyroscopeSensorService mService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		startService(new Intent(getContext(), GyroscopeSensorService.class));

		mService = getService();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		// Stop Service
		getContext().stopService(
				new Intent(getContext(), GyroscopeSensorService.class));
	}

	public void testMetaData() {
		assertTrue(mService != null);
		assertTrue(mService.getMetadata().getServiceName() == GyroscopeSensorService.SENSOR_NAME);
	}

	public void testLastValue() {
		assertTrue(mService != null);

		// Fill in Dummy Value
		GyroscopeSensorValue dummyValue = new GyroscopeSensorValue(0, 1, 2, 3);
		mService.putSensorValue(dummyValue);

		Log.i("TEST", dummyValue.toString());
		Log.i("TEST", mService.getLastValue().toString());
		assertTrue(mService.getLastValue().equals(dummyValue));
	}

	public void testPullData() {
		assertTrue(mService != null);

		// Fill in Dummy Value
		GyroscopeSensorValue dummyValue0 = new GyroscopeSensorValue(0, 1, 2, 3);
		GyroscopeSensorValue dummyValue1 = new GyroscopeSensorValue(0, 1, 2, 4);
		GyroscopeSensorValue dummyValue2 = new GyroscopeSensorValue(0, 1, 2, 5);
		GyroscopeSensorValue dummyValue3 = new GyroscopeSensorValue(0, 1, 2, 6);

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

		// Pull again should be empty
		data = mService.pullData();
		data = mService.pullData();
		assertTrue(data.isEmpty());

	}
}