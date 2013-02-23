package eu.livegov.mobilesensing.test;

import eu.livegov.mobilesensing.AccService;
import eu.livegov.mobilesensing.Accelerometer;
import android.app.Service;
import android.test.AndroidTestCase;
import android.test.ServiceTestCase;

public class AcceralometerTest extends ServiceTestCase<AccService> {
	public AcceralometerTest() {
		super(AccService.class);
		// TODO Auto-generated constructor stub
	}
	
	AccService mService;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();

	    mService = getService();

	  } // end of setUp() method definition
	
	public void testConditions(){
		assertTrue(true);
	}

}
