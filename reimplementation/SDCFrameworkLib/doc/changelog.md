<h1>Versions & Changelog History</h1>

[TOC]

----------------

### Upcoming Version 1.3.4 - Extended AIDL interface, Bulk Broadcasts, Manual triggered Archive Transfer & Weekly Schedule 

* ___bugfixes___
    * Since 1.3.3: Each time the device is configured by an external file the unique device identifier is accidently erased and a new one will be created. Fix: The uuid configuration is preserved now, together with the personal weekly schedule and the configured log level.  
    
* ___general___
	* added a new option to manually trigger an archive transfer to the SDCServiceController activity.
    * added a new sampling enabled state setting to enable/disable sampling by default. 
    * The sampling preference can be changed through the IPC interface now, as well as the enabled state preferences for sample transfer and persistent storage. 
    * decoupled the service and its control activity to allow individual activity implementations with an embedded sdcf service.
    * Extended the SDCFrameworkDemo application to demonstrate the general usage of data sink (resp. content provider) based sensors. The main activity does demonstrate how to start/stop an installed service from other applications and allows the start of three further activities:
        * the sample viewer which was the startup activity in older revisions,
        * a new example activity for audio sample recording, using the AudioProvider for data exchange with the service.
        * another activities which does use the broadcasted wifi samples for a visual channel analysis.
        * The IntentReceiver does extend the SampleListener from the library project now, to support any known intent representations of samples automatically.
    * The new SDCEmbeddedExample application was introduced to demonstrate the development of an own control application with an embedded sdcf service:
        * It does allow the explicit recording of annotated sensor samples. The transfer service is stopped by default and will be enabled manually for a single archive transfers after a recording is finished.
        * The annotation is transmitted as tag sensor sample and can be used to identify the related samples by charging all samples with an equal or higher time stamp as annotated by the transmitted tag.
        * When a recording is started, sdcf sampling is activated.
        * When a recording is finished, the sdcf sampling feature is de-activated, and a single archive transfer is triggered.
    * added the possibility to define a weekly start/stop schedule for an installed sdcf service. 

* ___app package___
    * extracted abstract base classes for control activities with a corresponding embedded SDC service implementations.
    * Extended the SDCPreferenceActivity for the new preferences.
    * AIDL service interface extension for essential control functions ( compare .app.facade.ISDCService.aidl ):
        * enable/disable sampling
        * enable/disable sample transfer
        * enable/disable sample storage
        * forced activation of an enabled but paused transfer task   
    * added two activities to create a weekly schedule for the sdcf service and extended the preference activity to start it.
    * added a new scheduler service.

* ___data & preference package___
    * Updated the service configuration and preference types for the new "sampling enabled" preference and the "broadcast frequency" preference.
    * Sample & SampleCollections are Android-Parcelable types now, both can be broadcasted and will use it's Parcel representation as extend extra.
    * added the types for a weekly schedule representation (Weekday*, WeeklySchedule).
    * Updated the service configuration and preference types to support the new weekly schedule configuration.

* ___broadcast package___
    * The SampleBroadcastService implementation does support a frequency now (> 0ms will trigger bulk broadcasts, if frequency is 0ms the old single sample intents are broadcasted.
    * The SampleListener does support both, single and bulk intents now and can be used as base class for own broadcast receivers (see for example the IntentReceiver in the SDCFramewokDemo project).

* ___service package___
    * ServiceManager was extended to support the new service interface and the new configuration parameters.

* ___transmission package___
    * TransferManager was extended to support a forced nearly instant transfer activation in case of full-filled requirements (e.g. the configured minimum sample count must be available in the database). 


----------------

### Version 1.3.3 - Configuration by external files

* ___bugfixes___
    * Timeprovider Synchronization strategy does unregister from network connection observer now in case of replacement. 

* ___general___
    * The main option menu of the controller activity provides an entry to load settings from an external xml-file now, as well as to load and install a RSA public key file. A loaded settings file does override the provided default settings from the asset folder and does allow an easier configuration of many devices with common settings.
    * The startup behavior of the service was changed for the synchronization error strategy "IgnoreAndObserveSyncStates". The service will start now even if an initial time sync fails. 
    
* ___app package___

    * added a FileBrowserActivity supporting a file filter and root directory pre-setting by intent parameters.
    * added a FileListAdapter for the file list in the browsing activity.

* ___util package___

    * added a FileMatcherFilter class as FilenameFilter which supports regular expressions for file names.
    
* ___preferences package___
    * added a method to reset stored settings to the ApplicationPreferenceManager

----------------

### Version 1.3.2 - New out-of-sync strategy

* any kind of sensor sample is tagged with the actual time sync state now.
* the time provider does generate sync state change events whenever it get out-of-sync or in-sync (in-sync refers to a NTP time update).
* time provider sync state change events are observed, collected and transmitted like sensor samples.
* removed the log broadcasting feature to reduce the system load.
* updated the simple library to version 2.6.9
* the service version information is displayed in the service preference screen now.

----------------

### Version 1.3.1 - Extracted Android library project

* extracted an Android library project __SDCFrameworkLib__ from the __SDCFramework__ application project.

__SDCFrameworkLib__

* added a battery low listener to the service, which will trigger an automatic shutdown.
* added an uncaught exception handler to the service.
* added a virtual tag sensor device using a data sink like the twitter provider.(tag sensor data will be stored as String). 

__SDCFrameworkDemo__

* new layout and style for the viewer demo application.
* the viewer demo application does now display an hint with a link to the downloadable service application if the service is not installed.

----------------

### Version 1.3.0 - NTP based time provider, UUID, RSA Encryption, ...

__SDCFramework__

* ___general___

    * added the SntpClient class, which is a not public part of the Android source.
    * added the open source header for LGPL.

* ___util package___

    * extended the observable event source base implementation by handlers for registered/unregistered observers, which can be overridden if necessary.
    * added time event types: TimeUpdateEvent, TimeErrorEvent.
    * changed the TimeProvider
        * does know an offset to the system time now,
        * the offset is updated using NTP time servers,
        * date and time changes in the system are observed and will trigger a new offset update,
        * the offset changes are observable.
    * added a TimeChangelistener to observe time or date changes.
    * added RSA ( PKCS#1 ) encryption/decryption as well as key generation to the static Encryption class.
    * added an asynchronous time update mechanism using wake locks to wake up a sleeping device.

* ___provider package___

    * AbstractContentProvider: insert operations will override the time stamp with the time provider time now.

* ___broadcast package___

    * added a LogListener as global broadcast receiver to store log information in daily log files and trigger the file transfer to a remote host.

* ___device package___

    * changed TimeProvider access of some scanner types.
    * added a new Scanner & Device Pair (to avoid performance issues for simple binary sensor types):
        * the PassiveSampleTakingDeviceScanner, which is triggered for sampling by device updates, and 
        * the corresponding device type SamplingCausingSensorDevice, which initates sampling on sensor data changes.
    * changed the Temperature, Light and Proximity sensor devices and scanners to extend the new types.
    * WififDeviceScanner: added support for connected flag.
    * added a new Scanner Device Pair for network locations ( based on WLAN/GSM Cell information ): NetworkLocationDeviceScanner, NetworkLocationDevice.
    * added a new abstract base class for the GPS and network location device types "AbstractLocationDevice".
    * the SampleFactory is now a global singleton instance and holds an Location tracker ( any sample will get a location information attached now, if configured and information is available ).

* ___data package___

    * added the TimeProviderConfigurationEntries type.
    * enhanced the SDCConfiguration by the new time provider element.
    * clean up of redundant info in the DeviceInformation.
    * added a unique identifier (UUID): unique per service installation.
    * extracted the independent DeviceInformation parts to the independent sub package.
    * added a concrete device information type ConcreteDeviceInformation.
    * WifiSampleData: added connected flag for a WIFI sample (true if a connection is established).
    * added log file transfer settings and changed the transmission configuration.

* ___preferences package___

    * added time provider configuration and preference types and interfaces.
    * added a UUIDPrecerence type.
    * adjusted the ApplicationPreferenceManager for the new preferences.
    * added transmission configuration parameter for archive encryption .
    * added a new service preference to enable/disable sample location information.
    * extracted the transmission protocol settings to an own type to be reusable for log file transfer settings.

* ___app package___

    * enhanced SDCPreferenceActivity by the time provider settinsg and the uuid value.
    * enhanced SDCPreferenceActivity by the transmission encryption flag
    * SDCServiceImpl: added a method to test especially for the SDCF service running state ( to be used in external apps ).
    * enhanced preference activity for the new enable/disable sample location information.

* ___service package___

    * added a method for UUID generation at first service start ( UUID is stored in the application preferences ).
    * added sample location preference update.

* ___transmission package___

    * FileManager: added archive encryption
    * added  UUID as http protocol parameter
    * added the global LogfileManager
    * added an alarm management to wake up a sleeping device on events

__SDCFrameworkTest__

* applied changes from the provider package extensions.
* applied test changes from the data and preference package changes ( added  tests for new types )
* added tests for the new time provider preference, configuration and event types.
* updated SDCConfiguration related tests.
* updated Application preference manager tests.
* updated tests affected by the unique device identifier.
* created a pure Java RSA encryption test and updated the teest for the Encryption class for RSA.
* moved some support methods to a TestUtil class.

----------------

### Version 1.2.0 - Sensor extensions: gyroscope, light, ...

__SDCFramework__

* ___general___

    * adjusted the default values for sensors in the configuration file ( located in the projects asset folder ).

* ___data.independent package___

      * added GyroscopeSampleData, LightSampleData, MagneticFieldSampleData, OrientationDampleData, PressureSampleData, PoximitySampleData and TemperatureSampleData.

* ___devices package___

    * AbstractAndroidSensorDevice type
        * implemented the formerly abstract "getSample" method final and added an new abstract method "getCurrentSampleData" (to be implemented by any extending class for access to the device specific sample data).
        * added the sensor delay constant as final field and constructor parameter, to be set by extending classes.
    * adjusted the AccelerometerDevice
    * added GyroscopeDevice, LightDevice, MagneticFieldDevice, OrientationDevice, PressureDevice, ProximityDevice, TemperatureDevice and all related scanners.
    * extended the SensordeviceFactory for the new types

__SDCFrameworkTest__

* added tests for all the new types (data, devices and scanners) and enhanced some existing tests.

----------------

### Version 1.1.0 - Virtual audio device extension

__SDCFramework__

* ___general___

    * switched to Eclipse ADT Plugin Version 15.
    * added a change log file to doc folder to track changes after the initial project revision.
    * added a new jar library with the public framework classes (to be used in other projects related to the framework). 
    * made the initial independent jar library really independent from Android types ( new name is sdcframework_independent.jar) 	 .
    * fixed description strings in the resource files.

* ___provider package___

    * extracted base classes and interfaces.
    * providers do extend the AbstractProvider type now.
    * a provider has related ContentProviderData now.
    * the Twitter provider implementation was adjusted.
    * added an AudioProvider and the related AudioProviderData.

* ___sample package___

    * the type SampleData can now refer to a related file ( which is packed into the archive for transmission ).
    * introduced the abstract base class AbstractSampleData ( for the common behavior of sample data types ).
    * added FileReferenceSampleData type which does refer to a file.
    * a Sample can now be queried for a related data file.

* ___devices package___

    * implementation of the doDeleteGatheredData method moved to the ContentProviderDeviceScanner.
    * added AudioDevice and AudioDeviceScanner ( a virtual device with an associated content provider device scanner type).

* ___util package___

    * added an observable external storage state change event type (ExternalStorageStateChangeEvent).
    * added the related ExternalStorageAvailabilityListener observable for external storage state change event.

* ___service package___

    * fixed a problem with the default configuration initialization.

* ___transmission package___

    * added the ability to transport related files also, packed into the archives root.
    * changed the connection strategy descriptions to be more comprehensible to others.

__SDCFrameworkDemo__

* applied changes from the provider package.

__SDCFrameworkTest__

* applied changes from the provider and sample package.
* added the missing tests for provider packaged and the latest scanner and device types.

----------------

### Version 1.0.0 - Initial project version

* The initial version is documented in detail in the file [ProjectReport.pdf](http://sourceforge.net/projects/sdcf/files/doc/ProjectReport.pdf/download).