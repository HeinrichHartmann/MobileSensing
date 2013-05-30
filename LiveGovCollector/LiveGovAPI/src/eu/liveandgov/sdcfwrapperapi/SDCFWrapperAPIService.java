package eu.liveandgov.sdcfwrapperapi;

import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class SDCFWrapperAPIService extends Service implements
		SDCServiceConnectionHolder.ServiceConnectionEventReceiver {

	public static final Class<?> serviceClass = ISDCService.class;
	private final SDCServiceConnectionHolder connectionHolder;
	private ISDCService service;
	private static final String TAG = "SDCFWrapperAPIService";

	public SDCFWrapperAPIService() {
		this.connectionHolder = new SDCServiceConnectionHolder(this,
				serviceClass);
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
		}
		if (action.equals(IntentConstants.ACTION_SAMPLING_DISABLE)) {
			enableSampling(false);
		}
		if (action.equals(IntentConstants.ACTION_SAMPLESTORAGE_ENABLE)) {
			enableSampleStorage(true);
		}
		if (action.equals(IntentConstants.ACTION_SAMPLESTORAGE_DISABLE)) {
			enableSampleStorage(false);
		}
		if (action.equals(IntentConstants.ACTION_SAMPLETRANSFER_ENABLE)) {
			enableSampleTransfer(true);
		}
		if (action.equals(IntentConstants.ACTION_SAMPLETRANSFER_DISABLE)) {
			enableSampleTransfer(false);
		}
		if (action.equals(IntentConstants.ACTION_SAMPLETRANSFER_TRIGGER)) {
			triggerSampleTransfer();
		}
		if (action.equals(IntentConstants.ACTION_SAMPLEBROADCAST_ENABLE)) {
			enableSampleBroadcasts(true);
		}
		if (action.equals(IntentConstants.ACTION_SAMPLEBROADCAST_DISABLE)) {
			enableSampleBroadcasts(false);
		}
		return START_STICKY;
	}

	public void startSDCFService() {
		Intent intent = new Intent(serviceClass.getName());
		getApplicationContext().startService(intent);
		connectionHolder.onCreate(this);
	}

	public void stopSDCFService() {
		connectionHolder.onDestroy(this);
		Intent intent = new Intent(serviceClass.getName());
		getApplicationContext().stopService(intent);
	}

	public boolean enableSampleTransfer(boolean enabledState) {
		try {
			getService().doEnableSampleTransfer(enabledState);
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	public boolean enableSampleStorage(boolean enabledState) {
		try {
			getService().doEnableSampleStorage(enabledState);
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	public boolean enableSampling(boolean enabledState) {
		try {
			getService().doEnableSampling(enabledState);
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	public boolean enableSampleBroadcasts(boolean enabledState) {
		try {
			getService().doEnableSampleBroadCasting(enabledState);
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

	public void updateSDCFConfig(String path) {

	}

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
	}

	@Override
	public void onAboutToDisconnect(ISDCService sdcService) {
		enableSampleBroadcasts(false);
		setService(null);
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
}
