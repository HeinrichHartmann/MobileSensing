package de.mobilesensing.dummy;

import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.google.gson.Gson;

import de.mobilesensing.dummy.BinderService.LocalBinder;

import android.os.Bundle;
import android.os.IBinder;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.content.res.Resources;
public class MainActivity extends Activity {

	public static final String MSG = "MSG";
	public static final String MY_ACTION = "MYACTION";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Context context = getApplicationContext();
        Resources resources = getResources();
        
        Log.i(Constants.LOG_TAG,"Main: context:" + context.toString() );
        Log.i(Constants.LOG_TAG,"Main: resources:" + resources.toString() );

    }

    @Override
    protected void onResume() {
    	Log.i(Constants.LOG_TAG,"Main: resume");
    	super.onResume();
    }
    
    public class Trans implements Serializable {
    	public Trans() {
		}
    	
    	private String msg = "None";
		public Trans(String msg) {
    		this.msg = msg;
		}
		public String getMsg(){
			return msg;
		}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    @Override
    public void onPause(){
    	Log.i(Constants.LOG_TAG,"Main: paused" );
    	super.onPause();    	
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState){
    	Log.i(Constants.LOG_TAG,"Main: save instance" );
    	super.onSaveInstanceState(outState);
    }
    
    public void clickB1(View view){
    	// startService(new Intent(this, FirstService.class));

    	// Launch "AnotherAcivity"
        Trans t = new Trans("Hello fom TransferObject");
        String tS = (new Gson()).toJson(t);
        Intent intent = new Intent(getApplicationContext(), AnotherActicity.class);
        intent.putExtra("TO",tS);
        intent.putExtra(MSG, "Hello from Main!");
        startActivity(intent);
    }
    
    
    BinderService mService;
    boolean mBound = false;
    
    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            LocalBinder binder = (LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    public void clickB2(View view){
       // Bind to BinderService
       Intent intent = new Intent(getApplicationContext(), BinderService.class);
       bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
   }

   
}
