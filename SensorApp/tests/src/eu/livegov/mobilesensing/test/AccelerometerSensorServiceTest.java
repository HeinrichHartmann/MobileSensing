package eu.livegov.mobilesensing.test;

import eu.livegov.mobilesensing.sensors.AccelerometerSensorService;
import android.app.Service;
import android.test.AndroidTestCase;
import android.test.ServiceTestCase;

public class AccelerometerSensorServiceTest extends ServiceTestCase<AccelerometerSensorService> {
	public AccelerometerSensorServiceTest() {
		super(AccelerometerSensorService.class);
		// TODO Auto-generated constructor stub
	}
	
	AccelerometerSensorService mService;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	    mService = getService();
	    // set dummmy values
	}

	public void testMetaData() {
		assertTrue(mService.getMetadata().getName() != null);
	}
	
	public void testLastValue(){
		assertTrue(mService.getLastValue() != null);
	}
	
	public void testPullData() {
		assertFalse(mService.pullData().isEmpty());
	}

}
