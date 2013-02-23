package eu.livegov.mobilesensing.test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import eu.livegov.mobilesensing.sensors.AccelerometerSensorService;
import eu.livegov.mobilesensing.sensors.AccelerometerSensorService.AccelerometerSensorValue;
import eu.livegov.mobilesensing.sensors.SensorValue;
import android.app.Service;
import android.test.AndroidTestCase;
import android.test.ServiceTestCase;

public class AccelerometerSensorServiceTest extends ServiceTestCase<AccelerometerSensorService> {
	public AccelerometerSensorServiceTest() {
		super(AccelerometerSensorService.class);
	}
	
	AccelerometerSensorService mService;
	
	ArrayList<AccelerometerSensorValue> dummyValues = new ArrayList<AccelerometerSensorValue>();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	    mService = getService();

	    // fill in dummy values
	    dummyValues.add(new AccelerometerSensorValue(1,111F,222F,333F));
	    dummyValues.add(new AccelerometerSensorValue(2,111F,222F,333F));
	    dummyValues.add(new AccelerometerSensorValue(3,111F,222F,333F));
	    
	    for (AccelerometerSensorValue v: dummyValues){
	    	mService.putSensorValue(v);
	    }	
	}

	public void testMetaData() {
		assertTrue(mService.getMetadata().getName() != null);
	}
	
	public void testLastValue(){
		assertTrue(
				mService.getLastValue().equals(
						dummyValues.get( dummyValues.size() -1 )
						));
	}
	
	public void testPullData() {
		List<SensorValue> data = mService.pullData();
		assertFalse(data.isEmpty());
		assertTrue(data.get(0).equals(dummyValues.get(0)));
	}

}
