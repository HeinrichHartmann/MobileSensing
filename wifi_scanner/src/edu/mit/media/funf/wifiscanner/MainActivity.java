package edu.mit.media.funf.wifiscanner;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class MainActivity extends Activity {

	
	public void ButtonClick(View view){
		TextView text = (TextView)findViewById(R.id.textView1);
		text.setText("Hehe");
	}
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);
            
            final Context context = this;

            
            CheckBox enabledCheckbox = (CheckBox)findViewById(R.id.enabledCheckbox); 
            enabledCheckbox.setChecked(MainPipeline.isEnabled(context));
            enabledCheckbox.setOnClickListener(new OnClickListener(){
            	
				@Override
				public void onClick(View v) {
					  
					Intent archiveIntent = new Intent(context, MainPipeline.class);
                    String action = MainPipeline.ACTION_ENABLE;
                    archiveIntent.setAction(action);
                    startService(archiveIntent);
					
				}});
            

            Button archiveButton = (Button)findViewById(R.id.archiveButton);
            archiveButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            Intent archiveIntent = new Intent(context, MainPipeline.class);
                            archiveIntent.setAction(MainPipeline.ACTION_ARCHIVE_DATA);
                            startService(archiveIntent);
                    }
            });
            
          }
    }
