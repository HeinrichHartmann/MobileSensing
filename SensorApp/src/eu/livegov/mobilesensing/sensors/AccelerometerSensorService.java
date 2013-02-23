package eu.livegov.mobilesensing.sensors;

import java.util.List;


public class AccelerometerSensorService extends SensorService {

	public class AccelerometerMetadata implements Metadata {@Override
	public String getName() {
			// TODO Auto-generated method stub
			return null;
	}
	}
	
	public class AccelerometerSensorValue implements SensorValue {
	@Override
		public long getTimestamp() {
			// TODO Auto-generated method stub
			return 0;
		}	
	}
	
	@Override
	public Metadata getMetadata() {
		// Returns Object containing basic information about the sensor
		return null;
	}

	@Override
	public SensorValue getLastValue() {
		// Returns last Recorded sensor value
		return null;
	}

	@Override
	public List<SensorValue> pullData() {
		// Returns all SensorValues
		// Clears the internal data queue
		return null;
	}

	@Override
	public void start() {
		// Start service		
	}

	@Override
	public void stop() {
		// Stop Service
		
	}

}
