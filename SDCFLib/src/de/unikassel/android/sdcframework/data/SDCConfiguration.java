/*
 * Copyright (C) 2012, Katy Hilgenberg.
 * Special acknowledgments to: Knowledge & Data Engineering Group, University of Kassel (http://www.kde.cs.uni-kassel.de).
 * Contact: sdcf@cs.uni-kassel.de
 *
 * This file is part of the SDCFramework (Sensor Data Collection Framework) project.
 *
 * The SDCFramework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The SDCFramework is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the SDCFramework.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.unikassel.android.sdcframework.data;

import java.util.List;
import java.util.Vector;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import de.unikassel.android.sdcframework.data.independent.GlobalSerializer;
import de.unikassel.android.sdcframework.data.independent.SerializableData;

/**
 * This class is the serializable representation of the default configuration
 * for the SDC framework. <br/>
 * <br/>
 * For each available sensor it is necessary to configure a
 * {@link SensorConfigurationEntry sensor configuration entry} in the
 * corresponding XML configuration file, which is located in the asset folder of
 * the project. <br/>
 * <br/>
 * An example for a valid SDC framework configuration file: <br/>
 * <blockquote> &lt;sdcconfig&gt; <blockquote> <font color="#008000"> &lt;!--
 * the time providers configuration section --&gt; </font><br/>
 * &lt;providers&gt; <blockquote>
 * &lt;provider&gt;ptbtime1.ptb.de&lt;provider&gt; <br/>
 * </blockquote> &lt;/providers&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the sensor configuration section --&gt;
 * </font><br/>
 * &lt;sensors&gt; <blockquote> &lt;sensor id="Accelerometer" enabled="true"
 * frequency="10000" prio="Level4"/&gt;<br/>
 * &lt;sensor id="Bluetooth" enabled="true" frequency="60000" prio="Level3"/&gt;<br/>
 * &lt;sensor id="Wifi" enabled="true" frequency="60000" prio="Level2"/&gt;<br/>
 * &lt;sensor id="GPS" enabled="true" frequency="120000" prio="Level1"/&gt;<br/>
 * &lt;sensor id="GSM" enabled="true" frequency="60000" prio="Level0"/&gt;
 * </blockquote> &lt;/sensors&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- flag to indicate if sample broadcasting is
 * enabled or not --&gt; </font><br/>
 * &lt;broadcastSamples&gt;true&lt;/broadcastSamples&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- flag to indicate if location information is
 * added to each sample --&gt; </font><br/>
 * &lt;addSampleLocation&gt;true&lt;/addSampleLocation&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- flag to indicate if samples shall be stored
 * for transmission --&gt; </font><br/>
 * &lt;storeSamples&gt;true&lt;/storeSamples&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- flag to indicate if samples shall be
 * transfered to a remote server from time to time --&gt; </font><br/>
 * &lt;transferSamples&gt;true&lt;/transferSamples&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- flag to indicate if location information shall
 * be stored in each sample --&gt; </font><br/>
 * &lt;addSampleLocation&gt;true&lt;/addSampleLocation&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the database configuration section --&gt;
 * </font><br/>
 * &lt;dbConfig&gt;<blockquote> <font color="#008000"> &lt;!-- the maximum
 * database size in bytes --&gt; </font><br/>
 * &lt;maxSize&gt;10485760&lt;/maxSize&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the database full strategy chain configuration
 * ( valid values are: wait_delete_notify or wait_notify_stopservice ) --&gt;
 * </font><br/>
 * &lt;dbFullStrategy&gt;wait_delete_notify&lt;/dbFullStrategy&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the wait time used by the wait strategy ( in
 * milliseconds ) --&gt; </font><br/>
 * &lt;waitStrategyMillis&gt;10000&lt;/waitStrategyMillis&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the record count to delete used by the sample
 * deletion strategy --&gt; </font><br/>
 * &lt;delStrategyRecordCount&gt;1000&lt;/delStrategyRecordCount&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the flag indicating if sample deletion is done
 * for lower priorities first by the sample deletion strategy ( if true the
 * oldest samples with the lowest priority will be selected first for deletion,
 * otherwise just the oldest samples ) --&gt; </font><br/>
 * &lt;delStrategyUsePrio&gt;true&lt;/delStrategyUsePrio&gt; </blockquote>
 * &lt;/dbConfig&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the transmission configuration section --&gt;
 * </font><br/>
 * &lt;transferConfig&gt;<blockquote> <font color="#008000"> &lt;!-- the minimum
 * count of samples for a single transmission --&gt; </font><br/>
 * &lt;minSampleCount&gt;100&lt;/minSampleCount&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the maximum count of samples for a single
 * transmission --&gt; </font><br/>
 * &lt;maxSampleCount&gt;100&lt;/maxSampleCount&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the minimum transmission frequency in seconds
 * --&gt; </font><br/>
 * &lt;minTransferFrequency&gt;3600&lt;/minTransferFrequency&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the archive type to use for file transmission
 * --&gt; </font><br/>
 * &lt;archiveType&gt;zip&lt;/archiveType&gt;<br/>
 * <br/>
 * &lt;protocolConfig&gt;<blockquote> <font color="#008000"> &lt;!-- the URL to
 * transfer files to --&gt; </font><br/>
 * &lt;url&gt;http://localhost&lt;/url&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the user name for remote authentication --&gt;
 * </font><br/>
 * &lt;authName&gt;Karli&lt;/authName&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the user password for remote authentication
 * --&gt; </font><br/>
 * &lt;authPassword&gt;unknown&lt;/authPassword&gt;<br/>
 * <br/>
 * <font color="#008000"> &lt;!-- the transfer strategy to use ( e.g. just any
 * available wlan connection )--&gt; </font><br/>
 * &lt;transferStrategy&gt;wlan&lt;/transferStrategy&gt;<br/>
 * </blockquote> &lt;/protocolConfig&gt; </blockquote> &lt;/transferConfig&gt;
 * </blockquote> &lt;/sdcconfig&gt; </blockquote>
 * 
 * @see SensorConfigurationEntry
 * @see DatabaseConfigurationEntry
 * @see de.unikassel.android.sdcframework.preferences.SDCConfigurationManager
 * @author Katy Hilgenberg
 * 
 */
@Root( name = "sdcconfig" )
public final class SDCConfiguration implements SerializableData
{
  /**
   * The time provider entries
   */
  @Element( name = "timeProviders", required = false )
  private TimeProviderConfigurationEntries timeProviderConfiguration;
  
  /**
   * The list of available sensors in the SDC service
   */
  @ElementList( name = "sensors", required = false )
  private List< SensorConfigurationEntry > sensorConfigurations;
  
  /**
   * The flag for the service sample broadcast behavior
   */
  @Element( name = "broadcastSamples", required = false )
  private Boolean isBroadcastingSamples;
  
  /**
   * The flag to configure that each sample gets a location fix added
   */
  @Element( name = "addSampleLocation", required = false )
  private Boolean isAddingSampleLocation;
  
  /**
   * The flag for the service persistent storage behavior
   */
  @Element( name = "storeSamples", required = false )
  private Boolean isStoringSamples;
  
  /**
   * The flag for the service transmission behavior
   */
  @Element( name = "transferSamples", required = false )
  private Boolean isTransmittingSamples;
  
  /**
   * The database configuration
   */
  @Element( name = "dbConfig", required = false )
  private DatabaseConfigurationEntry databaseConfiguration;
  
  /**
   * The transmission service configuration
   */
  @Element( name = "transferConfig", required = false )
  private TransmissionConfigurationEntry transmissionConfiguration;
  
  /**
   * The log file transfer configuration
   */
  @Element( name = "logTransferConfig", required = false )
  private TransmissionProtocolConfigurationEntry logTransferConfiguration;
  
  /**
   * Constructor
   */
  public SDCConfiguration()
  {
    setListSensorConfigurations( new Vector< SensorConfigurationEntry >() );
    setDatabaseConfiguration( new DatabaseConfigurationEntry() );
    setTransmissionConfiguration( new TransmissionConfigurationEntry() );
  }
  
  /**
   * Getter for the sensorConfigurations
   * 
   * @return the sensorConfigurations
   */
  public final List< SensorConfigurationEntry > getListSensorConfigurations()
  {
    return sensorConfigurations;
  }
  
  /**
   * Setter for the sensorConfigurations
   * 
   * @param sensorConfigurations
   *          the sensorConfigurations to set
   */
  public final void
      setListSensorConfigurations(
          List< SensorConfigurationEntry > sensorConfigurations )
  {
    this.sensorConfigurations = sensorConfigurations;
  }
  
  /**
   * Getter for the NTP time provider entries
   * 
   * @return the NTP time provider entries
   */
  public TimeProviderConfigurationEntries getTimeProviderConfigEntries()
  {
    return timeProviderConfiguration;
  }
  
  /**
   * Setter for the NTP time provider entries
   * 
   * @param timeProviders
   *          the NTP time provider entries to set
   */
  public void setTimeProviderConfigEntries(
      TimeProviderConfigurationEntries timeProviders )
  {
    this.timeProviderConfiguration = timeProviders;
  }
  
  /**
   * Setter for flag for sample broadcasts behavior
   * 
   * @param isBroadcastingSamples
   *          the flag for sample broadcasts behavior to set
   */
  public final void setBroadcastingSamples( Boolean isBroadcastingSamples )
  {
    this.isBroadcastingSamples = isBroadcastingSamples;
  }
  
  /**
   * Getter for the flag for sample broadcasts behavior
   * 
   * @return true if samples shall be broadcasted, false otherwise
   */
  public final Boolean isBroadcastingSamples()
  {
    return isBroadcastingSamples;
  }
  
  /**
   * Getter for the flag to configure location fix per sample
   * 
   * @return the flag to configure location fix per sample
   */
  public Boolean isAddingSampleLocation()
  {
    return isAddingSampleLocation;
  }
  
  /**
   * Setter for the flag to configure location fix per sample
   * 
   * @param isAddingSampleLocation
   *          the flag to configure location fix per sample
   */
  public final void setIsAddingSampleLocation( Boolean isAddingSampleLocation )
  {
    this.isAddingSampleLocation = isAddingSampleLocation;
  }
  
  /**
   * Setter for the flag for persistent storage behavior
   * 
   * @param isStoringSamples
   *          the flag for persistent storage behavior to set
   */
  public final void setStoringSamples( Boolean isStoringSamples )
  {
    this.isStoringSamples = isStoringSamples;
  }
  
  /**
   * Getter for the flag for persistent storage behavior
   * 
   * @return true if samples will be stored persistent for transmission service
   */
  public final Boolean isStoringSamples()
  {
    return isStoringSamples;
  }
  
  /**
   * Setter for the isTransmittingSamples
   * 
   * @param isTransmittingSamples
   *          the isTransmittingSamples to set
   */
  public final void setTransmittingSamples( Boolean isTransmittingSamples )
  {
    this.isTransmittingSamples = isTransmittingSamples;
  }
  
  /**
   * Getter for the isTransmittingSamples
   * 
   * @return the isTransmittingSamples
   */
  public final Boolean isTransmittingSamples()
  {
    return isTransmittingSamples;
  }
  
  /**
   * Getter for the database configuration
   * 
   * @return the database configuration
   */
  public final DatabaseConfigurationEntry getDatabaseConfiguration()
  {
    return databaseConfiguration;
  }
  
  /**
   * Setter for the database configuration
   * 
   * @param databaseConfiguration
   *          the database configuration to set
   */
  public final void setDatabaseConfiguration(
      DatabaseConfigurationEntry databaseConfiguration )
  {
    this.databaseConfiguration = databaseConfiguration;
  }
  
  /**
   * Getter for the transmissionConfiguration
   * 
   * @return the transmissionConfiguration
   */
  public final TransmissionConfigurationEntry getTransmissionConfiguration()
  {
    return transmissionConfiguration;
  }
  
  /**
   * Getter for the logTransferConfiguration
   * 
   * @return the logTransferConfiguration
   */
  public TransmissionProtocolConfigurationEntry getLogTransferConfiguration()
  {
    return logTransferConfiguration;
  }
  
  /**
   * Setter for the logTransferConfiguration
   * 
   * @param logTransferConfiguration
   *          the logTransferConfiguration to set
   */
  public void setLogTransferConfiguration(
      TransmissionProtocolConfigurationEntry logTransferConfiguration )
  {
    this.logTransferConfiguration = logTransferConfiguration;
  }
  
  /**
   * Setter for the transmissionConfiguration
   * 
   * @param transmissionConfiguration
   *          the transmissionConfiguration to set
   */
  public final void setTransmissionConfiguration(
      TransmissionConfigurationEntry transmissionConfiguration )
  {
    this.transmissionConfiguration = transmissionConfiguration;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see de.unikassel.android.sdcframework.data.facade.SerializableData#toXML()
   */
  @Override
  public final String toXML() throws Exception
  {
    return GlobalSerializer.toXml( this );
  }
}
