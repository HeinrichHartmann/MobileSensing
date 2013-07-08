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
package de.unikassel.android.sdcframework.app;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.text.method.DigitsKeyListener;
import de.unikassel.android.sdcframework.R;
import de.unikassel.android.sdcframework.app.scheduler.SDCFSchedulerActivity;
import de.unikassel.android.sdcframework.app.scheduler.ScheduleService;
import de.unikassel.android.sdcframework.data.ConcreteDeviceInformation;
import de.unikassel.android.sdcframework.data.independent.DeviceInformation;
import de.unikassel.android.sdcframework.devices.SensorDeviceAvailabilityTester;
import de.unikassel.android.sdcframework.devices.facade.SensorDeviceIdentifier;
import de.unikassel.android.sdcframework.devices.facade.SensorDevicePriorities;
import de.unikassel.android.sdcframework.persistence.facade.DBFullStrategyDescription;
import de.unikassel.android.sdcframework.preferences.ApplicationPreferenceManagerImpl;
import de.unikassel.android.sdcframework.preferences.SDCConfigurationManager;
import de.unikassel.android.sdcframework.preferences.facade.ApplicationPreferenceManager;
import de.unikassel.android.sdcframework.preferences.facade.LogLevelConfiguration;
import de.unikassel.android.sdcframework.preferences.facade.SensorDevicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.ServicePreferences;
import de.unikassel.android.sdcframework.preferences.facade.SinglePreference;
import de.unikassel.android.sdcframework.preferences.facade.TimeProviderPreference;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionPreference;
import de.unikassel.android.sdcframework.preferences.facade.TransmissionProtocolPreference;
import de.unikassel.android.sdcframework.transmission.facade.ConnectionStrategyDescription;
import de.unikassel.android.sdcframework.util.facade.ArchiveTypes;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import de.unikassel.android.sdcframework.util.facade.TimeProviderErrorStrategyDescription;

/**
 * The activity for the preference screen. <br/>
 * <br/>
 * Does provide the device and service configuration screen. The service and
 * device preference screens will be created dynamically at runtime.<br/>
 * Any defaults configured in the service configuration file in the projects
 * asset folder can be overridden here.<br/>
 * The configured device types in the service configuration will limit the
 * available devices in the preference screen, in the same way as for the
 * service itself.
 * 
 * @see ApplicationPreferenceManagerImpl
 * @see SDCConfigurationManager
 * @see SensorDeviceAvailabilityTester
 * @author Katy Hilgenberg
 * 
 */
public final class SDCPreferenceActivity extends PreferenceActivity
{
  /**
   * Scheduler update request code.
   */
  private static final int SCHEDULE_UPDATE = 0;
  
  /**
   * The preference manager
   */
  private final ApplicationPreferenceManager prefManager;
  
  /**
   * Constructor
   */
  public SDCPreferenceActivity()
  {
    super();
    this.prefManager = new ApplicationPreferenceManagerImpl();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
   */
  @Override
  protected void onCreate( Bundle savedInstanceState )
  {
    super.onCreate( savedInstanceState );
    
    // update sensor device preferences and availability tester by configuration
    String configFile = getText( R.string.sdc_config_file_name ).toString();
    SDCConfigurationManager serviceConfigurationManager =
        new SDCConfigurationManager( getApplicationContext(), configFile );
    serviceConfigurationManager.updateDefaults( prefManager );
    SensorDeviceAvailabilityTester.getInstance().configure(
        serviceConfigurationManager.getListDevices(), getApplicationContext() );
    serviceConfigurationManager.onDestroy();
    
    PreferenceScreen root =
        getPreferenceManager().createPreferenceScreen( this );
    
    createDevicePreferences( root );
    createServicePreferences( root );
    createServiceSchedulerPreferences( root );
    createDeviceInfoDisplayInfo( root );
    
    PackageInfo pInfo;
    try
    {
      pInfo = getPackageManager().getPackageInfo( getPackageName(), 0 );
      String sVersion = pInfo.versionName;
      PreferenceScreen prefScreen =
          getPreferenceManager().createPreferenceScreen( this );
      prefScreen.setKey( getText( R.string.pref_key_version_screen ).toString() );
      prefScreen.setTitle( R.string.title_version );
      prefScreen.setSummary( sVersion );
      root.addPreference( prefScreen );
    }
    catch ( NameNotFoundException e )
    {}
    
    setPreferenceScreen( root );
    
    setPreferenceDependencies();
  }
  
  /**
   * Does set preference dependencies after tree was created
   */
  private void setPreferenceDependencies()
  {
    // add dependency of transfer enabled from persistent storage enabled
    Preference transmissionCheckBox =
        getPreferenceManager().findPreference(
            prefManager.getServicePreferences().getTransmissionEnabledPreference().getKey() );
    if ( transmissionCheckBox != null )
    {
      String parentKey =
          prefManager.getServicePreferences().getPersistentStorageEnabledPreference().getKey();
      transmissionCheckBox.setDependency( parentKey );
    }
  }
  
  /**
   * Does create the service scheduler section dynamically in the root screen.
   * 
   * @param root
   *          the root element to add preferences to
   */
  private void createServiceSchedulerPreferences( PreferenceScreen root )
  {
    PreferenceScreen schedulerPrefScreen =
        getPreferenceManager().createPreferenceScreen( this );
    schedulerPrefScreen.setKey( getText( R.string.pref_key_scheduler ).toString() );
    schedulerPrefScreen.setTitle( R.string.pref_category_scheduler );
    schedulerPrefScreen.setSummary( R.string.sum_scheduler );
    
    schedulerPrefScreen.setOnPreferenceClickListener( new OnPreferenceClickListener()
    {
      /*
       * (non-Javadoc)
       * 
       * @see
       * android.preference.Preference.OnPreferenceClickListener#onPreferenceClick
       * (android.preference.Preference)
       */
      @Override
      public boolean onPreferenceClick( Preference preference )
      {
        return onSchedulerStart();
      }
    } );
    root.addPreference( schedulerPrefScreen );
  }
  
  /**
   * Handler for scheduler preference selection.
   * 
   * @return true if successful, false otherwise
   */
  protected boolean onSchedulerStart()
  {
    Intent intent = new Intent( this, SDCFSchedulerActivity.class );
    startActivityForResult( intent, SCHEDULE_UPDATE );
    return true;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see android.preference.PreferenceActivity#onActivityResult(int, int,
   * android.content.Intent)
   */
  @Override
  protected void
      onActivityResult( int requestCode, int resultCode, Intent data )
  {
    // Make sure the request was successful
    if ( resultCode == RESULT_OK )
    {
      switch ( requestCode )
      {
        case SCHEDULE_UPDATE:
        {
          Intent intent = ScheduleService.createIntent( getApplicationContext() );
          startService( intent );
          break;
        }
      }
    }
  }
  
  /**
   * Does create the device info display section dynamically in the root screen
   * 
   * @param root
   *          the root element to add preferences to
   */
  private void createDeviceInfoDisplayInfo( PreferenceScreen root )
  {
    // create the preference screen for service preferences
    PreferenceScreen infoPrefScreen =
        getPreferenceManager().createPreferenceScreen( this );
    infoPrefScreen.setKey( getText( R.string.pref_key_device_info ).toString() );
    infoPrefScreen.setTitle( R.string.pref_category_device_info );
    infoPrefScreen.setSummary( R.string.sum_service_device_info );
    root.addPreference( infoPrefScreen );
    
    String sUuid = prefManager.getUUIDConfiguration( this );
    PreferenceScreen prefScreen =
        getPreferenceManager().createPreferenceScreen( this );
    prefScreen.setKey( getText( R.string.pref_key_uuid_screen ).toString() );
    prefScreen.setTitle( R.string.title_uuid );
    prefScreen.setSummary( sUuid );
    infoPrefScreen.addPreference( prefScreen );
    
    DeviceInformation deviceInfo = new ConcreteDeviceInformation( sUuid );
    
    // device
    prefScreen = getPreferenceManager().createPreferenceScreen( this );
    prefScreen.setTitle( R.string.title_device );
    prefScreen.setSummary( deviceInfo.getDevice() );
    infoPrefScreen.addPreference( prefScreen );
    
    // manufacturer
    prefScreen = getPreferenceManager().createPreferenceScreen( this );
    prefScreen.setTitle( R.string.title_manufacturer );
    prefScreen.setSummary( deviceInfo.getManufacturer() );
    infoPrefScreen.addPreference( prefScreen );
    
    // model
    prefScreen = getPreferenceManager().createPreferenceScreen( this );
    prefScreen.setTitle( R.string.title_model );
    prefScreen.setSummary( deviceInfo.getModel() );
    infoPrefScreen.addPreference( prefScreen );
    
    // identifier
    prefScreen = getPreferenceManager().createPreferenceScreen( this );
    prefScreen.setTitle( R.string.title_identifier );
    prefScreen.setSummary( deviceInfo.getId() );
    infoPrefScreen.addPreference( prefScreen );
    
    // os version
    prefScreen = getPreferenceManager().createPreferenceScreen( this );
    prefScreen.setTitle( R.string.title_os_version );
    prefScreen.setSummary( deviceInfo.getRelease() );
    infoPrefScreen.addPreference( prefScreen );
    
    // fingerprint
    prefScreen = getPreferenceManager().createPreferenceScreen( this );
    prefScreen.setTitle( R.string.title_fingerprint );
    prefScreen.setSummary( deviceInfo.getFingerprint() );
    infoPrefScreen.addPreference( prefScreen );
  }
  
  /**
   * Does create the service preferences dynamically in the root screen
   * 
   * @param root
   *          the root element to add service preferences to
   */
  private void createServicePreferences( PreferenceScreen root )
  {
    // create the preference screen for service preferences
    PreferenceScreen servicePrefScreen =
        getPreferenceManager().createPreferenceScreen( this );
    servicePrefScreen.setKey( getText( R.string.pref_key_service_config ).toString() );
    servicePrefScreen.setTitle( R.string.pref_category_service_config );
    servicePrefScreen.setSummary( R.string.sum_service_config );
    root.addPreference( servicePrefScreen );
    
    ServicePreferences servicePrefs = prefManager.getServicePreferences();
    
    // create the preference entries for sample broadcast service part
    createSamplingRelatedPreferences( servicePrefScreen, servicePrefs );
    
    // create the preference entries for sample broadcast service part
    createSampleBroadcastPreferences( servicePrefScreen, servicePrefs );
    
    // create the preference entries for persistent storage service part
    createPersistentStoragePreferences( servicePrefScreen, servicePrefs );
    
    // create the preference entries for transmission service part
    createTransmissionPreferences( servicePrefScreen, servicePrefs );
    
    // create preference screen for log level
    createLoggingPreferences( servicePrefScreen, servicePrefs );
    
    // create preference screen for time provider
    createTimeProviderPreferences( servicePrefScreen );
  }
  
  /**
   * Does create the time provider preferences dynamically in the given root
   * screen
   * 
   * @param root
   *          the root element to add preferences to
   */
  private void
      createTimeProviderPreferences( PreferenceScreen root )
  {
    // create category entry
    PreferenceCategory categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_timeprovider_config );
    root.addPreference( categoryPref );
    
    TimeProviderPreference pref = prefManager.getTimeProviderPreference();
    
    // create text edit screen for list of NTP servers
    EditTextPreference editNTPProviders = new EditTextPreference( this );
    editNTPProviders.setKey( pref.getProvidersPreference().getKey() );
    editNTPProviders.setTitle( R.string.titel_timeprovider_edit );
    editNTPProviders.setSummary( R.string.sum_timeprovider_edit );
    editNTPProviders.setDefaultValue( pref.getProvidersEntryFromList(
        pref.getDefault().getProviders() ) );
    categoryPref.addPreference( editNTPProviders );
    
    // create list for error strategy selection
    CharSequence[] entries = getTimeProviderErrorStrategyEntries();
    CharSequence[] values = entries;
    
    ListPreference listLevels = new ListPreference( this );
    listLevels.setEntries( entries );
    listLevels.setEntryValues( values );
    listLevels.setDialogTitle( R.string.sum_sync_err_strategy_list );
    listLevels.setKey( pref.getErrorStrategyPreference().getKey() );
    listLevels.setTitle( R.string.titel_sync_err_strategy_list );
    listLevels.setSummary( R.string.sum_sync_err_strategy_list );
    listLevels.setDefaultValue( pref.getErrorStrategyPreference().getDefault().name() );
    categoryPref.addPreference( listLevels );
  }
  
  /**
   * Does create the logging preferences dynamically in the given root screen
   * 
   * @param root
   *          the root element to add preferences to
   * @param servicePrefs
   *          the service preference to create from
   */
  private void createLoggingPreferences( PreferenceScreen root,
      ServicePreferences servicePrefs )
  {
    SinglePreference< LogLevelConfiguration > loglevelPref =
        prefManager.getLogLevelPreference();
    
    // create category entry
    PreferenceCategory categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_logging_config );
    root.addPreference( categoryPref );
    
    // create list for log level selection
    CharSequence[] logLevelEntries = getLogLevelEntries();
    CharSequence[] logLevelEntryValues = getLogLevelEntryValues();
    
    ListPreference listLevels = new ListPreference( this );
    listLevels.setEntries( logLevelEntries );
    listLevels.setEntryValues( logLevelEntryValues );
    listLevels.setDialogTitle( R.string.sum_service_loglevel_list );
    listLevels.setKey( loglevelPref.getKey() );
    listLevels.setTitle( R.string.titel_service_loglevel_list );
    listLevels.setSummary( R.string.sum_service_loglevel_list );
    listLevels.setDefaultValue( loglevelPref.getDefault().getLogLevel().toString() );
    categoryPref.addPreference( listLevels );
    
    TransmissionProtocolPreference logTransferPreference =
        servicePrefs.getLogTransferPreference();
    
    // add a preference screen for host configuration
    PreferenceScreen hostPrefScreen =
        getPreferenceManager().createPreferenceScreen( this );
    hostPrefScreen.setKey( getText( R.string.pref_key_host_config ).toString() );
    hostPrefScreen.setTitle( getText( R.string.title_host_config ).toString() );
    hostPrefScreen.setSummary( getText( R.string.sum_host_config ).toString() );
    root.addPreference( hostPrefScreen );
    
    categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_host_config );
    hostPrefScreen.addPreference( categoryPref );
    
    // create text edit screen for URL
    EditTextPreference editHostIP = new EditTextPreference( this );
    editHostIP.setKey( logTransferPreference.getURLPreference().getKey() );
    editHostIP.setTitle( R.string.titel_url_edit );
    editHostIP.setSummary( R.string.sum_url_edit );
    editHostIP.setDefaultValue( logTransferPreference.getDefault().getURL() );
    categoryPref.addPreference( editHostIP );
    
    // create text edit screen for authentication user name
    EditTextPreference editUserName = new EditTextPreference( this );
    editUserName.setKey( logTransferPreference.getAuthenticationUserPreference().getKey() );
    editUserName.setTitle( R.string.title_username_edit );
    editUserName.setSummary( R.string.sum_username_edit );
    editUserName.setDefaultValue( logTransferPreference.getDefault().getUserName() );
    categoryPref.addPreference( editUserName );
    
    // create text edit screen for authentication user password
    EditTextPreference editPassword = new EditTextPreference( this );
    editPassword.setKey( logTransferPreference.getAuthenticationPasswordPreference().getKey() );
    editPassword.setTitle( R.string.title_password_edit );
    editPassword.setSummary( R.string.sum_password_edit );
    editPassword.setDefaultValue( logTransferPreference.getDefault().getAuthPassword() );
    categoryPref.addPreference( editPassword );
  }
  
  /**
   * Does create the preferences related to the sampling task dynamically in the
   * given root screen
   * 
   * @param root
   *          the root element to add preferences to
   * @param servicePrefs
   *          the service preference to create from
   */
  private void createSamplingRelatedPreferences(
      PreferenceScreen root, ServicePreferences servicePrefs )
  {
    PreferenceCategory categoryPref;
    // create category entry
    categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_sample_related_config );
    root.addPreference( categoryPref );
    
    // create check box for broadcast service enabled state
    CheckBoxPreference samplingEnabledCheckBox = new CheckBoxPreference( this );
    samplingEnabledCheckBox.setKey( servicePrefs.getSamplingEnabledPreference().getKey() );
    samplingEnabledCheckBox.setTitle( getText( R.string.titel_sampling_enabled_checkbox ) );
    samplingEnabledCheckBox.setSummary( R.string.sum_sampling_enabled_checkbox );
    samplingEnabledCheckBox.setDefaultValue( servicePrefs.getDefault().isSamplingEnabled() );
    categoryPref.addPreference( samplingEnabledCheckBox );
    
    // create check box for location fix per sample enabled flag
    CheckBoxPreference checkBox = new CheckBoxPreference( this );
    checkBox.setKey( servicePrefs.getSampleLocationFixEnabledPreference().getKey() );
    checkBox.setTitle( getText( R.string.titel_sample_loc_fix_enabled_checkbox ) );
    checkBox.setSummary( R.string.sum_sample_loc_fix_enabled_checkbox );
    checkBox.setDefaultValue( servicePrefs.getDefault().isAddingSampleLocation() );
    categoryPref.addPreference( checkBox );
  }
  
  /**
   * Does create the sample broadcast service preferences dynamically in the
   * given root screen
   * 
   * @param root
   *          the root element to add preferences to
   * @param servicePrefs
   *          the service preference to create from
   */
  private void createSampleBroadcastPreferences(
      PreferenceScreen root, ServicePreferences servicePrefs )
  {
    PreferenceCategory categoryPref;
    // create category entry for broadcast settings
    categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_broadcast_config );
    root.addPreference( categoryPref );
    
    // create check box for broadcast service enabled state
    CheckBoxPreference broadcastCheckBox = new CheckBoxPreference( this );
    broadcastCheckBox.setKey( servicePrefs.getSampleBroadcastsEnabledPreference().getKey() );
    broadcastCheckBox.setTitle( getText( R.string.titel_broadcast_enabled_checkbox ) );
    broadcastCheckBox.setSummary( R.string.sum_broadcast_enabled_checkbox );
    broadcastCheckBox.setDefaultValue( servicePrefs.getDefault().isBroadcastingSamples() );
    categoryPref.addPreference( broadcastCheckBox );
    
    // create text edit screen for maximum database size
    EditTextPreference editFrequency = new EditTextPreference( this );
    editFrequency.setKey( servicePrefs.getBroadcastFrequencyPreference().getKey() );
    editFrequency.setTitle( R.string.title_broadcast_frequency_edit );
    editFrequency.setSummary( R.string.sum_broadcast_frequency_edit );
    editFrequency.getEditText().setKeyListener(
        DigitsKeyListener.getInstance() );
    editFrequency.setDefaultValue( Long.toString( servicePrefs.getDefault().getBroadcastFrequency() ) );
    categoryPref.addPreference( editFrequency );
  }
  
  /**
   * Does create the transmission service preferences dynamically in the given
   * root screen
   * 
   * @param root
   *          the root element to add preferences to
   * @param servicePrefs
   *          the service preference to create from
   */
  private void createTransmissionPreferences(
      PreferenceScreen root, ServicePreferences servicePrefs )
  {
    // create category entry
    PreferenceCategory categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_transfer_config );
    root.addPreference( categoryPref );
    
    // create check box for transfer service enabled state
    CheckBoxPreference transmissionCheckBox = new CheckBoxPreference( this );
    transmissionCheckBox.setKey( servicePrefs.getTransmissionEnabledPreference().getKey() );
    transmissionCheckBox.setTitle( getText( R.string.titel_transfer_enabled_checkbox ) );
    transmissionCheckBox.setSummary( R.string.sum_transfer_enabled_checkbox );
    transmissionCheckBox.setDefaultValue( servicePrefs.getDefault().isTransmittingSamples() );
    categoryPref.addPreference( transmissionCheckBox );
    
    // create preference screen for transfer setting
    PreferenceScreen transferPrefScreen =
        getPreferenceManager().createPreferenceScreen( this );
    transferPrefScreen.setKey( getText( R.string.pref_key_transfer_config ).toString() );
    transferPrefScreen.setTitle( getText( R.string.title_transfer_config ).toString() );
    transferPrefScreen.setSummary( getText( R.string.sum_transfer_config ).toString() );
    categoryPref.addPreference( transferPrefScreen );
    
    TransmissionPreference transmissionPreference =
        servicePrefs.getTransmissionPreference();
    
    categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_transmission_config );
    transferPrefScreen.addPreference( categoryPref );
    
    // add other configuration entries
    // create text edit screen for minimum samples to transfer
    EditTextPreference editMinSamples = new EditTextPreference( this );
    editMinSamples.setKey( transmissionPreference.getMinSampleTransferCountPreference().getKey() );
    editMinSamples.setTitle( R.string.titel_min_samples_to_transfer_edit );
    editMinSamples.setSummary( R.string.sum_min_samples_to_transfer_edit );
    editMinSamples.getEditText().setKeyListener(
        DigitsKeyListener.getInstance() );
    editMinSamples.setDefaultValue( Integer.toString(
        transmissionPreference.getDefault().getMinSampleTransferCount() ) );
    categoryPref.addPreference( editMinSamples );
    
    // create text edit screen for maximum samples to transfer
    EditTextPreference editMaxSamples = new EditTextPreference( this );
    editMaxSamples.setKey( transmissionPreference.getMaxSampleTransferCountPreference().getKey() );
    editMaxSamples.setTitle( R.string.titel_max_samples_to_transfer_edit );
    editMaxSamples.setSummary( R.string.sum_max_samples_to_transfer_edit );
    editMaxSamples.getEditText().setKeyListener(
        DigitsKeyListener.getInstance() );
    editMaxSamples.setDefaultValue( Integer.toString(
        transmissionPreference.getDefault().getMaxSampleTransferCount() ) );
    categoryPref.addPreference( editMaxSamples );
    
    // create text edit screen for transfer frequency minimum
    EditTextPreference editTransferFrequency = new EditTextPreference( this );
    editTransferFrequency.setKey( transmissionPreference.getMinTransferFrequencyPreference().getKey() );
    editTransferFrequency.setTitle( R.string.titel_min_transfer_frequency_edit );
    editTransferFrequency.setSummary( R.string.sum_min_transfer_frequency_edit );
    editTransferFrequency.getEditText().setKeyListener(
        DigitsKeyListener.getInstance() );
    editTransferFrequency.setDefaultValue( Long.toString(
        transmissionPreference.getDefault().getMinTransferFrequency() ) );
    categoryPref.addPreference( editTransferFrequency );
    
    // create selection for archive type
    ListPreference listTypes = new ListPreference( this );
    CharSequence[] entries =
        new CharSequence[ ArchiveTypes.values().length ];
    int i = 0;
    for ( ArchiveTypes type : ArchiveTypes.values() )
    {
      entries[ i++ ] = type.toString();
    }
    listTypes.setEntries( entries );
    listTypes.setEntryValues( entries );
    listTypes.setDialogTitle( R.string.titel_archive_type_selection );
    listTypes.setKey( transmissionPreference.getArchiveTypePreference().getKey() );
    listTypes.setTitle( R.string.titel_archive_type_selection );
    listTypes.setSummary( R.string.sum_archive_type_selection );
    listTypes.setDefaultValue( transmissionPreference.getDefault().getArchiveType().toString() );
    categoryPref.addPreference( listTypes );
    
    // create check box for encryption of transfer archive
    CheckBoxPreference encryptionCheckBox = new CheckBoxPreference( this );
    encryptionCheckBox.setKey( transmissionPreference.getEncryptionEnabledPreference().getKey() );
    encryptionCheckBox.setTitle( getText( R.string.titel_encryption_enabled_checkbox ) );
    encryptionCheckBox.setSummary( R.string.sum_encryption_enabled_checkbox );
    encryptionCheckBox.setDefaultValue( transmissionPreference.getDefault().isEncryptionEnabled() );
    categoryPref.addPreference( encryptionCheckBox );
    
    // create selection for transfer strategy type
    ListPreference listStrategies = new ListPreference( this );
    CharSequence[] strategyEntries =
        new CharSequence[ ConnectionStrategyDescription.values().length ];
    i = 0;
    for ( ConnectionStrategyDescription strategy : ConnectionStrategyDescription.values() )
    {
      strategyEntries[ i++ ] = strategy.toString();
    }
    listStrategies.setEntries( strategyEntries );
    listStrategies.setEntryValues( strategyEntries );
    listStrategies.setDialogTitle( R.string.titel_transfer_strategy_selection );
    listStrategies.setKey( transmissionPreference.getProtocolPreference().getTransmissionStrategyPreference().getKey() );
    listStrategies.setTitle( R.string.titel_transfer_strategy_selection );
    listStrategies.setSummary( R.string.sum_transfer_strategy_selection );
    listStrategies.setDefaultValue( transmissionPreference.getDefault().getProtocolConfiguration().getTransmissionStrategy().toString() );
    categoryPref.addPreference( listStrategies );
    
    createHostConfiguration( root, transmissionPreference );
  }
  
  /**
   * Does create the host preferences dynamically in the given root screen
   * 
   * @param root
   *          the root element to add preferences to
   * @param transmissionPreference
   *          the transmission preference to create from
   */
  private void createHostConfiguration( PreferenceScreen root,
      TransmissionPreference transmissionPreference )
  {
    PreferenceCategory categoryPref;
    // add a preference screen for host configuration
    PreferenceScreen hostPrefScreen =
        getPreferenceManager().createPreferenceScreen( this );
    hostPrefScreen.setKey( getText( R.string.pref_key_host_config ).toString() );
    hostPrefScreen.setTitle( getText( R.string.title_host_config ).toString() );
    hostPrefScreen.setSummary( getText( R.string.sum_host_config ).toString() );
    root.addPreference( hostPrefScreen );
    
    categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_host_config );
    hostPrefScreen.addPreference( categoryPref );
    
    // create text edit screen for URL
    EditTextPreference editHostIP = new EditTextPreference( this );
    editHostIP.setKey( transmissionPreference.getProtocolPreference().getURLPreference().getKey() );
    editHostIP.setTitle( R.string.titel_url_edit );
    editHostIP.setSummary( R.string.sum_url_edit );
    editHostIP.setDefaultValue( transmissionPreference.getProtocolPreference().getDefault().getURL() );
    categoryPref.addPreference( editHostIP );
    
    // create text edit screen for authentication user name
    EditTextPreference editUserName = new EditTextPreference( this );
    editUserName.setKey( transmissionPreference.getProtocolPreference().getAuthenticationUserPreference().getKey() );
    editUserName.setTitle( R.string.title_username_edit );
    editUserName.setSummary( R.string.sum_username_edit );
    editUserName.setDefaultValue( transmissionPreference.getProtocolPreference().getDefault().getUserName() );
    categoryPref.addPreference( editUserName );
    
    // create text edit screen for authentication user password
    EditTextPreference editPassword = new EditTextPreference( this );
    editPassword.setKey( transmissionPreference.getProtocolPreference().getAuthenticationPasswordPreference().getKey() );
    editPassword.setTitle( R.string.title_password_edit );
    editPassword.setSummary( R.string.sum_password_edit );
    editPassword.setDefaultValue( transmissionPreference.getProtocolPreference().getDefault().getAuthPassword() );
    categoryPref.addPreference( editPassword );
  }
  
  /**
   * Does create the persistent storage preferences dynamically in the given
   * root screen
   * 
   * @param root
   *          the root element to add preferences to
   * @param servicePrefs
   *          the service preference to create from
   */
  private void createPersistentStoragePreferences(
      PreferenceScreen root, ServicePreferences servicePrefs )
  {
    // create category entry for database configurations
    PreferenceCategory categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_db_config );
    root.addPreference( categoryPref );
    
    // create check box for storage service enabled state
    CheckBoxPreference storageCheckBox = new CheckBoxPreference( this );
    storageCheckBox.setKey( servicePrefs.getPersistentStorageEnabledPreference().getKey() );
    storageCheckBox.setTitle( getText( R.string.titel_storage_enabled_checkbox ) );
    storageCheckBox.setSummary( R.string.sum_storage_enabled_checkbox );
    storageCheckBox.setDefaultValue( servicePrefs.getDefault().isStoringSamples() );
    categoryPref.addPreference( storageCheckBox );
    
    // create preference screen for db setting
    PreferenceScreen dbPrefScreen =
        getPreferenceManager().createPreferenceScreen( this );
    dbPrefScreen.setKey( getText( R.string.pref_key_db_config ).toString() );
    dbPrefScreen.setTitle( getText( R.string.title_db_config ).toString() );
    dbPrefScreen.setSummary( getText( R.string.sum_db_config ).toString() );
    categoryPref.addPreference( dbPrefScreen );
    
    categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_db_size );
    dbPrefScreen.addPreference( categoryPref );
    
    // create text edit screen for maximum database size
    EditTextPreference editDBSizeText = new EditTextPreference( this );
    editDBSizeText.setKey( servicePrefs.getDBMaxSizePreference().getKey() );
    editDBSizeText.setTitle( R.string.titel_db_size_edit );
    editDBSizeText.setSummary( R.string.sum_db_size_edit );
    editDBSizeText.getEditText().setKeyListener(
        DigitsKeyListener.getInstance() );
    editDBSizeText.setDefaultValue( Long.toString( servicePrefs.getDefault().getMaximumDatabaseSize() ) );
    categoryPref.addPreference( editDBSizeText );
    
    categoryPref = new PreferenceCategory( this );
    categoryPref.setTitle( R.string.pref_category_db_full_strategy );
    dbPrefScreen.addPreference( categoryPref );
    
    // create selection for strategy configuration
    ListPreference listStrategies = new ListPreference( this );
    CharSequence[] entries =
        new CharSequence[ DBFullStrategyDescription.values().length ];
    int i = 0;
    for ( DBFullStrategyDescription strategy : DBFullStrategyDescription.values() )
    {
      entries[ i++ ] = strategy.toString();
    }
    listStrategies.setEntries( entries );
    listStrategies.setEntryValues( entries );
    listStrategies.setDialogTitle( R.string.titel_db_full_strategy_selection );
    listStrategies.setKey( servicePrefs.getDbFullStrategyPreference().getKey() );
    listStrategies.setTitle( R.string.titel_db_full_strategy_selection );
    listStrategies.setSummary( R.string.sum_db_full_strategy_selection );
    listStrategies.setDefaultValue( servicePrefs.getDefault().getDBFullStrategy().toString() );
    categoryPref.addPreference( listStrategies );
    
    // create text edit screen for wait time in case of database full
    EditTextPreference editWaittimeText = new EditTextPreference( this );
    editWaittimeText.setKey( servicePrefs.getDbFullWaitTimePreference().getKey() );
    editWaittimeText.setTitle( R.string.titel_db_full_waittime_edit );
    editWaittimeText.setSummary( R.string.sum_db_full_waittime_edit );
    editWaittimeText.getEditText().setKeyListener(
        DigitsKeyListener.getInstance() );
    editWaittimeText.setDefaultValue( Long.toString( servicePrefs.getDefault().getDBFullWaitTime() ) );
    categoryPref.addPreference( editWaittimeText );
    
    // create check box for sample priority recognition
    CheckBoxPreference checkBoxDeletePrioBased = new CheckBoxPreference( this );
    checkBoxDeletePrioBased.setKey( servicePrefs.getDbFullDeletionIsPriorityBasedPreference().getKey() );
    checkBoxDeletePrioBased.setTitle( getText( R.string.titel_db_full_delete_prio_flag ) );
    checkBoxDeletePrioBased.setSummary( R.string.sum_db_full_delete_prio_flag );
    checkBoxDeletePrioBased.setDefaultValue( servicePrefs.getDefault().isDBFullDeletionPriorityBased() );
    categoryPref.addPreference( checkBoxDeletePrioBased );
    
    // create text edit screen for record count to delete
    EditTextPreference ediRecordCounttText = new EditTextPreference( this );
    ediRecordCounttText.setKey( servicePrefs.getDbFullDeletionRecordCountPreference().getKey() );
    ediRecordCounttText.setTitle( R.string.titel_db_full_recordcount_edit );
    ediRecordCounttText.setSummary( R.string.sum_db_full_recordcount_edit );
    ediRecordCounttText.getEditText().setKeyListener(
        DigitsKeyListener.getInstance() );
    ediRecordCounttText.setDefaultValue( Integer.toString( servicePrefs.getDefault().getDBFullDeletionRecordCount() ) );
    categoryPref.addPreference( ediRecordCounttText );
  }
  
  /**
   * Does create the sensor devices preferences dynamically in the root screen
   * 
   * @param root
   *          the root element to add device preferences to
   */
  private void createDevicePreferences( PreferenceScreen root )
  {
    CharSequence[] sensorPriorityLevelEntries = getSensorPriorityLevelEntries();
    CharSequence[] sensorPriorityLevelEntryValues =
        getSensorPriorityLevelEntryValues();
    
    // create the preference screen for sensor device preferences
    PreferenceScreen sensorPrefScreen =
        getPreferenceManager().createPreferenceScreen( this );
    sensorPrefScreen.setKey( getText( R.string.pref_key_sensor_config ).toString() );
    sensorPrefScreen.setTitle( R.string.pref_category_sensor_config );
    sensorPrefScreen.setSummary( R.string.sum_sensor_config );
    root.addPreference( sensorPrefScreen );
    
    // get a list of configured sensor devices and create the preference sub
    // screens requesting device preferences from the preference manager
    List< SensorDeviceIdentifier > availableSensorDevices =
        SensorDeviceAvailabilityTester.getInstance().getAvailableSensorDevices();
    
    for ( SensorDeviceIdentifier id : availableSensorDevices )
    {
      SensorDevicePreferences devicePreferences =
          prefManager.getPreferencesForDevice( id );
      String deviceName = devicePreferences.getDeviceIdentifier().toString();
      
      // create preference screen for current device
      PreferenceScreen prefScreen =
          getPreferenceManager().createPreferenceScreen( this );
      prefScreen.setKey( devicePreferences.getKey() );
      prefScreen.setTitle( deviceName );
      prefScreen.setSummary( getText( R.string.sum_single_sensor_config ) + " "
          + deviceName );
      sensorPrefScreen.addPreference( prefScreen );
      
      // create category entry
      PreferenceCategory categoryPref = new PreferenceCategory( this );
      categoryPref.setTitle( getText( R.string.pref_category_single_sensor_config )
          + " " + deviceName );
      prefScreen.addPreference( categoryPref );
      
      // create check box for enabled state
      CheckBoxPreference enabledCheckBox = new CheckBoxPreference( this );
      enabledCheckBox.setKey( devicePreferences.getEnabledPreference().getKey() );
      enabledCheckBox.setTitle( getText( R.string.titel_sensor_enabled_checkbox )
          + " " + deviceName );
      enabledCheckBox.setSummary( R.string.sum_sensor_enabled_checkbox );
      enabledCheckBox.setDefaultValue( devicePreferences.getDefault().isEnabled() );
      categoryPref.addPreference( enabledCheckBox );
      
      // create list for priority level of scan samples
      ListPreference listLevels = new ListPreference( this );
      listLevels.setEntries( sensorPriorityLevelEntries );
      listLevels.setEntryValues( sensorPriorityLevelEntryValues );
      listLevels.setDialogTitle( R.string.sum_sensor_sample_prio_list );
      listLevels.setKey( devicePreferences.getPriorityPreference().getKey() );
      listLevels.setTitle( R.string.titel_sensor_sample_prio_list );
      listLevels.setSummary( R.string.sum_sensor_sample_prio_list );
      listLevels.setDefaultValue( Integer.toString(
          devicePreferences.getDefault().getSamplePriority().ordinal() ) );
      categoryPref.addPreference( listLevels );
      
      // create text edit for frequency
      EditTextPreference editFrequency = new EditTextPreference( this );
      editFrequency.setKey( devicePreferences.getFrequencyPreference().getKey() );
      editFrequency.setTitle( R.string.titel_sensor_frequency_edit );
      editFrequency.setSummary( R.string.sum_sensor_frequency_edit );
      editFrequency.getEditText().setKeyListener(
          DigitsKeyListener.getInstance() );
      editFrequency.setDefaultValue( Integer.toString( devicePreferences.getDefault().getFrequency() ) );
      
      categoryPref.addPreference( editFrequency );
    }
  }
  
  /**
   * Does create the sensor priority level entries for the list box
   * 
   * @return the sensor priority level entries for the list box
   */
  private CharSequence[] getSensorPriorityLevelEntries()
  {
    CharSequence[] entries =
        new CharSequence[ SensorDevicePriorities.values().length ];
    int i = 0;
    for ( SensorDevicePriorities prio : SensorDevicePriorities.values() )
    {
      entries[ i ] = prio.toString();
      i++;
    }
    return entries;
  }
  
  /**
   * Does create the sensor priority level entry values for the list box
   * 
   * @return the sensor priority level entry values for the list box
   */
  private CharSequence[] getSensorPriorityLevelEntryValues()
  {
    CharSequence[] entries =
        new CharSequence[ SensorDevicePriorities.values().length ];
    int i = 0;
    for ( SensorDevicePriorities prio : SensorDevicePriorities.values() )
    {
      entries[ i ] = "" + prio.ordinal();
      i++;
    }
    return entries;
  }
  
  /**
   * Does create the time provider error strategy entries for the list box
   * 
   * @return the time provider error strategy entries for the list box
   */
  private CharSequence[] getTimeProviderErrorStrategyEntries()
  {
    CharSequence[] entries =
        new CharSequence[ TimeProviderErrorStrategyDescription.values().length ];
    int i = 0;
    for ( TimeProviderErrorStrategyDescription value : TimeProviderErrorStrategyDescription.values() )
    {
      entries[ i ] = value.toString();
      i++;
    }
    return entries;
  }
  
  /**
   * Does create the log level entries for the list box
   * 
   * @return the log level entries for the list box
   */
  private CharSequence[] getLogLevelEntries()
  {
    CharSequence[] entries = new CharSequence[ LogLevel.values().length ];
    int i = 0;
    for ( LogLevel level : LogLevel.values() )
    {
      entries[ i ] = level.toString();
      i++;
    }
    return entries;
  }
  
  /**
   * Does create the log level entry values for the list box
   * 
   * @return the log level entry values for the list box
   */
  private CharSequence[] getLogLevelEntryValues()
  {
    return getLogLevelEntries();
  }
}
