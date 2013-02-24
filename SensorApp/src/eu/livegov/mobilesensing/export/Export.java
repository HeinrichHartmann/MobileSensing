package eu.livegov.mobilesensing.export;

import java.util.ArrayList;
import java.util.List;
import eu.livegov.mobilesensing.sensors.SensorValue;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorService;
import eu.livegov.mobilesensing.sensors.accelerometer.AccelerometerSensorValue;
import eu.livegov.mobilesensing.sensors.gps.GpsSensorService;

import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONObject;


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
	
	public void storeData(){
	
	JSONObject object = new JSONObject();
	AccelerometerSensorService.getSensorName();
	GpsSensorService.
	
	
	
	
	object.put("Sensorname", AccelerometerSensorService.getSensorName());
	
	try {
		 
		FileWriter file = new FileWriter("c:\\test.json");
		file.write(object.toJSONString());
		file.flush();
		file.close();
 
	} catch (IOException e) {
		e.printStackTrace();
	}
 
	
 
     }
	

	
	
	}
	
	
	
	

	
	

}
