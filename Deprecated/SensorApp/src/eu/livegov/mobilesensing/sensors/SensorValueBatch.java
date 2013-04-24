package eu.livegov.mobilesensing.sensors;

import java.util.List;

/**
 * Container format for SensorValue Export
 * 
 * @author hartmann
 */
public class SensorValueBatch {
	public List<SensorValue> values;
	public Metadata meta;
	// add Recording Details
}
