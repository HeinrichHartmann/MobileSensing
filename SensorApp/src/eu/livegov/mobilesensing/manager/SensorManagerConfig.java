package eu.livegov.mobilesensing.manager;

import java.util.List;

import eu.livegov.mobilesensing.sensors.SensorConfig;

/**
 * Contains information about:
 * - Which sensor Services to Start and thier parameters
 * - Global metadata for Output file
 * 
 * @author hartmann
 *
 */
public class SensorManagerConfig {
	List<SensorConfig> SensorConfigurations;
}
