package eu.livegov.mobilesensing.test;

import eu.livegov.mobilesensing.manager.SensorManager;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;
import android.animation.ArgbEvaluator;
import android.content.Intent;
import android.test.ServiceTestCase;

public class SensorManagerTest extends ServiceTestCase<SensorManager> {

	public SensorManagerTest() {
		super(SensorManager.class);
	}
	
	SensorManager mService;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		// Start service
        startService(
        		new Intent(getContext(),  
        		SensorManager.class));

        mService = getService();
	}

	
	public void testAll(){
		assertTrue(mService != null);
		
		mService.setServicesToBind();
		
		mService.bindSensorServices();
		
		mService.statusAll();
		
	}
	
}
