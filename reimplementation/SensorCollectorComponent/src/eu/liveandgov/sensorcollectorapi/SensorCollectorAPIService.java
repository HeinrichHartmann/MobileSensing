package eu.liveandgov.sensorcollectorapi;

import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.provider.TagProviderData;
import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;
import de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder;
import de.unikassel.android.sdcframework.util.LogEvent;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.TimeProvider;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class SensorCollectorAPIService extends Service implements
		SDCServiceConnectionHolder.ServiceConnectionEventReceiver,
		EventObserver<LogEvent> {

	public static final Class<?> serviceClass = ISDCService.class;
	private final SDCServiceConnectionHolder connectionHolder;
	private ISDCService service;
	private static final String TAG = "SensorCollectorAPIService";

	private boolean isRunning, isRecording, storageEnabled, transferEnabled,
			broadcastEnabled, loggingEnabled = true;

	public SensorCollectorAPIService() {
		this.connectionHolder = new SDCServiceConnectionHolder(this,
				serviceClass);
		Logger.getInstance().setLogLevel(LogLevel.DEBUG);
		Logger.getInstance().registerEventObserver(this);
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		Log.i(TAG, "Received intent with action " + action);
		if (action.equals(IntentConstants.ACTION_SERVICE_START)) {
			startSDCFService();
		}
		if (action.equals(IntentConstants.ACTION_SERVICE_STOP)) {
			stopSDCFService();
		}
		if (action.equals(IntentConstants.ACTION_SAMPLING_ENABLE)) {
			enableSampling(true);
			broadcastStatus();
		}
		if (action.equals(IntentConstants.ACTION_SAMPLING_DISABLE)) {
			enableSampling(false);
			broadcastStatus();
		}
		if (action.equals(IntentConstants.ACTION_SAMPLESTORAGE_ENABLE)) {
			enableSampleStorage(true);
			broadcastStatus();
		}
		if (action.equals(IntentConstants.ACTION_SAMPLESTORAGE_DISABLE)) {
			enableSampleStorage(false);
			broadcastStatus();
		}
		if (action.equals(IntentConstants.ACTION_SAMPLETRANSFER_ENABLE)) {
			enableSampleTransfer(true);
			broadcastStatus();
		}
		if (action.equals(IntentConstants.ACTION_SAMPLETRANSFER_DISABLE)) {
			enableSampleTransfer(false);
			broadcastStatus();
		}
		if (action.equals(IntentConstants.ACTION_SAMPLETRANSFER_TRIGGER)) {
			triggerSampleTransfer();
			broadcastStatus();
		}
		if (action.equals(IntentConstants.ACTION_SAMPLEBROADCAST_ENABLE)) {
			enableSampleBroadcasts(true);
			broadcastStatus();
		}
		if (action.equals(IntentConstants.ACTION_SAMPLEBROADCAST_DISABLE)) {
			enableSampleBroadcasts(false);
			broadcastStatus();
		}
		if (action.equals(IntentConstants.ACTION_ANNOTATE)) {
			sendAnnotationToSDCF(intent.getStringExtra("tag"));
		}
		if (action.equals(IntentConstants.ACTION_GET_STATUS)) {
			broadcastStatus();
		}
		if (action.equals(IntentConstants.ACTION_LOGGING_ENABLE)) {
			loggingEnabled = true;
		}
		if (action.equals(IntentConstants.ACTION_LOGGING_DISABLE)) {
			loggingEnabled = false;
		}
//		if (action.equals(IntentConstants.ACTION_CHANGE_CONFIG)) {
//			changeSDCFConfig(intent.getStringExtra("path");)
//		}
		return START_STICKY;
	}

	public void startSDCFService() {
		Intent intent = new Intent(serviceClass.getName());
		getApplicationContext().startService(intent);
		connectionHolder.onCreate(this);
		Logger.getInstance().registerEventObserver(this);
	}

	public void stopSDCFService() {
		connectionHolder.onDestroy(this);
		Intent intent = new Intent(serviceClass.getName());
		getApplicationContext().stopService(intent);
	}

	public boolean enableSampleTransfer(boolean enabledState) {
		try {
			getService().doEnableSampleTransfer(enabledState);
			transferEnabled = enabledState;
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	public boolean enableSampleStorage(boolean enabledState) {
		try {
			getService().doEnableSampleStorage(enabledState);
			storageEnabled = enabledState;
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	public boolean enableSampling(boolean enabledState) {
		try {
			getService().doEnableSampling(enabledState);
			isRecording = enabledState;
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	public boolean enableSampleBroadcasts(boolean enabledState) {
		try {
			getService().doEnableSampleBroadCasting(enabledState);
			broadcastEnabled = enabledState;
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	public boolean triggerSampleTransfer() {
		try {
			getService().doTriggerSampleTransfer();
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	private void sendAnnotationToSDCF(String tag) {
		ContentValues values = new ContentValues();
		// IMPORTANT: The time stamp is internally overridden by the service
		// which does generate its own sample time stamp when removing tag data.
		// To guarantee a reliable time stamp, the service should run when
		// adding annotation information that samples will be processed in time.
		values.put(ContentProviderData.TIMESTAMP,
				Long.toString(TimeProvider.getInstance().getTimeStamp()));
		values.put(TagProviderData.TEXT, tag);
		getContentResolver().insert(
				TagProviderData.getInstance().getContentUri(), values);
		
		Intent loggingIntent = new Intent(IntentConstants.ACTION_LOG);
		loggingIntent.putExtra("message", "Annotation recorded: " + tag);
		getApplicationContext().sendBroadcast(loggingIntent);
	}

	public void broadcastStatus() {
		Intent statusIntent = new Intent(IntentConstants.ACTION_STATUS);
		statusIntent.putExtra("running", isRunning);
		statusIntent.putExtra("recording", isRecording);
		statusIntent.putExtra("storage", storageEnabled);
		statusIntent.putExtra("transfer", transferEnabled);
		statusIntent.putExtra("broadcast", broadcastEnabled);
		getApplicationContext().sendBroadcast(statusIntent);
	}

//	public void changeSDCFConfig(String path) {
//
//	}

	protected synchronized void setService(ISDCService service) {
		this.service = service;
	}

	protected synchronized ISDCService getService() {
		return service;
	}

	@Override
	public void onConnectionEstablished(ISDCService sdcService) {
		setService(sdcService);
		enableSampling(false);
		enableSampleTransfer(false);
		enableSampleBroadcasts(true);
		enableSampleStorage(true);
		isRunning = true;
		broadcastStatus();
	}

	@Override
	public void onAboutToDisconnect(ISDCService sdcService) {
		enableSampleBroadcasts(false);
		setService(null);
		isRunning = false;
		broadcastStatus();
	}

	@Override
	public void onConnectionLost() {
	}

	@Override
	public void onBindingFailed() {
	}

	@Override
	public void onServiceUnavailable() {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onEvent(ObservableEventSource<? extends LogEvent> eventSource,
			LogEvent observedEvent) {
		if (loggingEnabled) {
			LogEvent event = (LogEvent) observedEvent;
			Intent loggingIntent = new Intent(IntentConstants.ACTION_LOG);
			loggingIntent.putExtra("message", event.getMessage());
			getApplicationContext().sendBroadcast(loggingIntent);
		}

	}
}
