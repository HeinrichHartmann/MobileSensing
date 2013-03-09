package eu.liveandgov.mobilesensing.prototype;

import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.app.facade.SDCService;
import de.unikassel.android.sdcframework.service.ServiceRunningStateListener;
import de.unikassel.android.sdcframework.service.ServiceUtils;
import android.os.Bundle;
import android.app.Activity;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	/**
	 * The service running state listener
	 */
	private final ServiceRunningStateListener serviceRunningStateListner;

	/**
	 * The service action name
     */
	private final String action = SDCService.ACTION;

	/**
	 * The service class name
	 */
	private final Class< ? > serviceClass = ISDCService.class;

	private static boolean isRunning = false;
	
	private static ToggleButton toggle;
	
	public MainActivity() {
	    this.serviceRunningStateListner = new ServiceRunningStateListener( action )
	    {
	      @Override
	      protected void serviceStateChanged( boolean isRunning )
	      {	
	    	  // ServiceState Changes
	    	  Log.i("TAG", "Running:" + isRunning);
	    	  MainActivity.isRunning = isRunning;
	    	  updateButtons();
	      }
	    };
	}
	
	private void updateButtons(){
		isRunning = ServiceUtils.isServiceRunning(getApplicationContext(), serviceClass );
		
		if (isRunning){
			toggle.setChecked(true);
		} else {			
			toggle.setChecked(false);
		}
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

	    IntentFilter filter = new IntentFilter();
	    filter.addAction( SDCService.ACTION );
	    getApplicationContext().registerReceiver( serviceRunningStateListner,
	            filter );

	    toggle = (ToggleButton) findViewById( R.id.toggle );

	    
	    updateButtons();
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void start(View view) {
        // start the sensor data collection service
		ServiceUtils.startService( getApplicationContext(), serviceClass );
	}
	public void stop(View view) {
        // stop the running service ( can fail if other activities are still
        // bounded to it )
        ServiceUtils.stopService( getApplicationContext(), serviceClass );
	}

	public void toggle(View view)	{
		if (isRunning) {
			stop(view);
		} else {
			start(view);			
		}
		updateButtons();
	}

}
