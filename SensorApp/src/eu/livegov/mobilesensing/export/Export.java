package eu.livegov.mobilesensing.export;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import eu.livegov.mobilesensing.sensors.Metadata;
import eu.livegov.mobilesensing.sensors.SensorValue;
import eu.livegov.mobilesensing.sensors.SensorValueBatch;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorValue;
import eu.livegov.mobilesensing.sensors.gps.GpsSensorService;
import eu.livegov.mobilesensing.sensors.gyroscope.GyroscopeSensorValue;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;

import android.content.Context;


/**
 * 
 * 
 * Export Data, trial code
 * 
 * Later this implementation is in the ServiceManager.java
 * storeData()
 * 
 *
 */

public class Export {
	
	JSONObject output = new JSONObject();

	public void storeData(){
	
	/**
		* 
		* TESTDATA
		* 
	*/
		
	//dummie values
	AccelerometerSensorValue asva = new AccelerometerSensorValue(1,3,6,7);
	AccelerometerSensorValue asvb = new AccelerometerSensorValue(2,9,7,4);
	GyroscopeSensorValue gsva = new GyroscopeSensorValue(3,34,8,2);
	GyroscopeSensorValue gsvb = new GyroscopeSensorValue(3,34,8,2);
	
	//dummie Lists and filling
	List <SensorValue> accList = new ArrayList<SensorValue>();
	accList.add(asva); 
	accList.add(asvb);
	List <SensorValue> gyroList = new ArrayList<SensorValue>();
	gyroList.add(gsva);
	gyroList.add(gsvb);
	
	Map<String,Object> metamap = new HashMap<String,Object>();
	metamap.put("version", 123);
	
	Metadata meta1 = new Metadata ("Accelerometer");
	Metadata meta2= new Metadata("Gyroscope");
	
	SensorValueBatch batch1 = new SensorValueBatch();
	batch1.meta=meta1;
	batch1.values=accList;
	SensorValueBatch batch2 = new SensorValueBatch();
	batch2.meta=meta2;
	batch2.values=gyroList;
	
	//List<SensorValueBatch> Dummie
	List<SensorValueBatch> listBatch = new ArrayList<SensorValueBatch>();
	listBatch.add(batch1);
	listBatch.add(batch2);
		
//-------------------------------------------
 /**
  * 
  * 
  *  List<SensorValueBatch> Iteration,
  *  
  *  that we get from pullAllValues()	
  *  
  *  note: for now we try to write timestamp, x,y,z
  *  
  */
	
	
	for (SensorValueBatch newbatch : listBatch ){
		List<SensorValue>list = new ArrayList<SensorValue>();
		list.add((SensorValue) newbatch.values);
			
		for(SensorValue value : list){
			float x =  value.getX();
			float y = value.getY();
			float z = value.getZ();
			long t = value.getTimestamp();
			
			output.put("Timestamp", t);
			output.put("x", x);
			output.put("y", y);
			output.put("z", z);
			}
	}
	
	String FILENAME = "storedData.txt";
	String string;
	FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
	fos.write(string.getBytes());
	fos.close();
	
	
	/**
	 * try write to JSON
	 * 
	 * not yet implemented
	 * 
	 * 
	 */
	
	try {
		 
		FileWriter file = new FileWriter("c:\\test.json"); // SD PATH test
		file.write(output.toJSONString());
		file.flush();
		file.close();
 
	} catch (IOException e) {
		e.printStackTrace();
	}
 
	
 
     
	

	
	
	}
	
	
	
	

	
	

}
