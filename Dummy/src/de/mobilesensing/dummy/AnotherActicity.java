package de.mobilesensing.dummy;

import com.google.gson.Gson;

import de.mobilesensing.dummy.MainActivity.Trans;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;

public class AnotherActicity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_another_acticity);
		Log.i(Constants.LOG_TAG,"AnotherActivity created!");
		
		Bundle extras = getIntent().getExtras();
		Log.i(Constants.LOG_TAG,"Passed msg:"+extras.getString(MainActivity.MSG));

		MainActivity.Trans tO= (new Gson()).fromJson(extras.getString("TO"), MainActivity.Trans.class);
		Log.i(Constants.LOG_TAG,"Passed TO:"+tO.getMsg());
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_another_acticity, menu);
		return true;
	}

}
