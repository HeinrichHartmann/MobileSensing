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
package eu.liveandgoc.sensorcollector;

import java.util.concurrent.atomic.AtomicBoolean;

import eu.liveandgov.sensorcollector.R;
import de.unikassel.android.sdcframework.app.AbstractServiceControlActivity;
import de.unikassel.android.sdcframework.app.SDCFileBrowserActivity;
import de.unikassel.android.sdcframework.app.SDCServiceController;
import de.unikassel.android.sdcframework.app.facade.ISDCService;
import de.unikassel.android.sdcframework.provider.TagProviderData;
import de.unikassel.android.sdcframework.provider.facade.ContentProviderData;
import de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder;
import de.unikassel.android.sdcframework.service.ServiceUtils;
import de.unikassel.android.sdcframework.util.LogEvent;
import de.unikassel.android.sdcframework.util.Logger;
import de.unikassel.android.sdcframework.util.TimeProvider;
import de.unikassel.android.sdcframework.util.facade.BroadcastableEvent;
import de.unikassel.android.sdcframework.util.facade.EventObserver;
import de.unikassel.android.sdcframework.util.facade.LogLevel;
import de.unikassel.android.sdcframework.util.facade.ObservableEventSource;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * 
 * Live+Gov Sensor Collection Application
 * 
 * 
 * Builds upon the embedded SDCF application example. It does replace the
 * internal SDCF controller activity by extending the type
 * AbstractServiceControlActivity.
 * 
 * 
 * @author Katy Hilgenberg, Heinrich Hartmann, Richard Schütz
 * 
 */
public class ControlActivity extends AbstractServiceControlActivity
		implements SDCServiceConnectionHolder.ServiceConnectionEventReceiver,
		EventObserver<LogEvent> {
	/**
	 * The SDC service connection holder
	 */
	private final SDCServiceConnectionHolder connectionHolder;

	/**
	 * Start/stop service
	 */
	private ToggleButton toggleServiceStateButton;

	/**
	 * Start/stop recording
	 */
	private ToggleButton toggleRecordingStateButton;
	private ProgressBar recordingSpinner;

	/**
	 * Start sample transfer
	 */
	private Button transferButton;
	private ProgressBar transferSpinner;
	
	/**
	 * Annotations
	 */
	private Button annotateButton;
	private EditText annotationTextField;
	private Spinner annodationDropDown;

	/**
	 * Event Logging
	 */
	private final Handler logEventHandler;
	private TextView logView;

	/**
	 * Recording state flag
	 */
	private final AtomicBoolean isRecording;

	/**
	 * Service state flag
	 */
	private Boolean isRunning;

	/**
	 * Transfering
	 */
	private Boolean isTransferingData;
	
	/**
	 * Reference to a running service when bound
	 */
	private ISDCService service;

	/**
	 * Constructor
	 */
	public ControlActivity() {
		super(ActivityConstants.serviceClass);
		this.connectionHolder = new SDCServiceConnectionHolder(this,
				ActivityConstants.serviceClass);

		isTransferingData = false;
		this.isRecording = new AtomicBoolean(false);
		isRunning = false;
		
		
		/**
		 * Create Logging Handler
		 */
		logEventHandler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					if (msg.obj instanceof BroadcastableEvent) {
						LogEvent event = (LogEvent) msg.obj;
						handleLogEvent(event);
					}
				} catch (Exception e) {
					Logger.getInstance().error(ControlActivity.this,
							"Exception in handleMessage");
					e.printStackTrace();
				}
			}
		};

	}

	/**
	 * On Create Service
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/*********************************
		 * Register UI Elements
		 *********************************/

		/**
		 * Start/Stop Service
		 */
		toggleServiceStateButton = (ToggleButton) findViewById(R.id.startStopServiceButton);
		toggleServiceStateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setRunningFlag();
				if (ControlActivity.this.isRunning) {
					doStopService();
				} else {
					doStartService();
				}
			}
		});

		/**
		 * Toggle Recording
		 */
		toggleRecordingStateButton = (ToggleButton) findViewById(R.id.toggleRecordingButton);
		toggleRecordingStateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onToggleRecordingState();
			}
		});

		recordingSpinner = (ProgressBar) findViewById(R.id.progressSpinner);
		recordingSpinner.setVisibility(View.INVISIBLE);

		/**
		 * Toggle Transfer
		 */
		transferButton = (Button) findViewById(R.id.transferSamplesButton);
		transferButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				triggerSampleTransfer();
			}
		});
		transferSpinner = (ProgressBar) findViewById(R.id.trasnferSpinner);
		
		/**
		 * Annotations
		 */
		annotationTextField = (EditText) findViewById(R.id.annotationTextfield);
		annotationTextField.setEnabled(false);

		annotateButton = (Button) findViewById(R.id.sendAnnotationButton);
		annotateButton.setEnabled(false);

		annodationDropDown = (Spinner) findViewById(R.id.dropdownAnnotation);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.movements_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		annodationDropDown.setAdapter(adapter);
		annodationDropDown
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						if (ControlActivity.this.isRecording.get()) {
							String annotation = annodationDropDown
									.getSelectedItem().toString();
							sendAnnotationToSDCF("Activity: " + annotation);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {

					}
				});
		annodationDropDown.setEnabled(false);

		updateButtons();

		/**
		 * Logging
		 */
		logView = (TextView) findViewById(R.id.sdcfLogview);
		logView.setSingleLine(false);
		logView.setMovementMethod(new ScrollingMovementMethod());

		Logger.getInstance().setLogLevel(LogLevel.DEBUG);
		Logger.getInstance().registerEventObserver(this);		
	}

	/**
	 * On Service Resume
	 */
	@Override
	protected void onResume() {
		super.onResume();
		Logger.getInstance().registerEventObserver(this);

		setRunningFlag();
		updateButtons();

		if (!ServiceUtils.isServiceAvailable(this,
				ActivityConstants.serviceClass)) {
			AlertDialog alertDialog = new AlertDialog.Builder(this).create();
			alertDialog.setTitle("Important");
			alertDialog.setMessage("The SDCF Service is not installed!");
			alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});
			alertDialog.setIcon(R.drawable.ic_launcher);
			alertDialog.show();
		}

		if (isRunning) {
			connectionHolder.onCreate(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		Logger.getInstance().unregisterEventObserver(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		if (ServiceUtils.isServiceRunning(getApplicationContext(),
				ActivityConstants.serviceClass)) {
			connectionHolder.onDestroy(this);
		}
	}

	/**************************************************************
	 * Core Functions
	 **************************************************************/

	/**
	 * Read Annotations from UI and send to the service
	 */
	private void sendAnnotation() {
		String annotation = annodationDropDown.getSelectedItem().toString();
		sendAnnotationToSDCF("Activity: " + annotation);

		annotation = annotationTextField.getText().toString();
		if (annotation != null && annotation.length() != 0) {
			// store annotation text as tag sensor information
			sendAnnotationToSDCF(annotation);
		}
	}

	/**
	 * Method to start the service
	 */
	protected void doStartService() {
		ServiceUtils.startService(getApplicationContext(),
				ActivityConstants.serviceClass.getName());
		connectionHolder.onCreate(this);
		
		Logger.getInstance().registerEventObserver(this);
	}

	/**
	 * Method to stop the service
	 */
	protected void doStopService() {
		connectionHolder.onDestroy(this);
		ServiceUtils.stopService(getApplicationContext(),
				ActivityConstants.serviceClass.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unikassel.android.sdcframework.app.AbstractServiceControlActivity#
	 * onServiceRunningStateChanged(boolean)
	 */
	@Override
	protected void onServiceRunningStateChanged(boolean isRunning) {
		setRunningFlag();
		updateButtons();
	}

	/**
	 * Handler for recording state button events.
	 */
	protected void onToggleRecordingState() {
		if (toggleRecordingStateButton.isChecked()) {
			// Recording is checked now

			// Disable Sample Transfer?
			// enableSampleTransfer( false );

			// Enable sample Recording!
			startRecording();

			updateButtons();
			sendAnnotation();
		} else if (isRecording.get()) {
			// Recording is not checked and service is running

			// Stop sample recording
			stopRecording();

			// Transfer samples
			triggerSampleTransfer();
			
			updateButtons();
		}
	}

	public void onAnnotateClick(View view) {
		sendAnnotation();
	}


	/*************************************************************
	 * SDCF Connection 
	 *************************************************************/
	
	/**
	 * Method to send an annotation as tag sensor information to the SDC
	 * service.
	 * 
	 * @param tag
	 *            the tag value to write
	 */
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

		Logger.getInstance().info(this, "Annotaion Added: " + tag);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.
	 * ServiceConnectionEventReceiver
	 * #onConnectionEstablished(de.unikassel.android.
	 * sdcframework.app.facade.ISDCService)
	 */
	@Override
	public void onConnectionEstablished(ISDCService sdcService) {
		setService(sdcService);
		// do initialization tasks when the binding is established
		enableSampling(false);
		//enableSampleTransfer(false);
		//enableSampleBroadcasts(true);
		enableSampleStorage(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.
	 * ServiceConnectionEventReceiver
	 * #onAboutToDisconnect(de.unikassel.android.sdcframework
	 * .app.facade.ISDCService)
	 */
	@Override
	public void onAboutToDisconnect(ISDCService sdcService) {
		// do cleanup tasks when then binding is gone
		toggleRecordingStateButton.setChecked(false);
		enableSampleBroadcasts(false);
		setService(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.
	 * ServiceConnectionEventReceiver#onConnectionLost()
	 */
	@Override
	public void onConnectionLost() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.
	 * ServiceConnectionEventReceiver#onBindingFailed()
	 */
	@Override
	public void onBindingFailed() {
		// should normally not happen if the service manifest configuration is
		// valid
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unikassel.android.sdcframework.service.SDCServiceConnectionHolder.
	 * ServiceConnectionEventReceiver#onServiceUnavailable()
	 */
	@Override
	public void onServiceUnavailable() {
		// Ignore for applications with embedded SDCF service
	}

	/**
	 * Method to enable/disable the sample transfer feature.
	 * 
	 * @param enabledState
	 *            true to enable the feature, false to disable it
	 */
	public boolean enableSampleTransfer(boolean enabledState) {
		try {
			getService().doEnableSampleTransfer(enabledState);
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	/**
	 * Method to enable/disable the sample storage feature.
	 * 
	 * @param enabledState
	 *            true to enable the feature, false to disable it
	 */
	public boolean enableSampleStorage(boolean enabledState) {
		try {
			getService().doEnableSampleStorage(enabledState);
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	/**
	 * Method to enable/disable the sampling feature.
	 * 
	 * @param enabledState
	 *            true to enable the feature, false to disable it
	 */
	public boolean enableSampling(boolean enabledState) {
		try {
			getService().doEnableSampling(enabledState);
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	/**
	 * Method to enable/disable the service sample broadcast feature.
	 * 
	 * @param enabledState
	 *            true to enable the feature, false to disable it
	 */
	public boolean enableSampleBroadcasts(boolean enabledState) {
		try {
			getService().doEnableSampleBroadCasting(enabledState);
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	/**
	 * Method to force the activation and transfer of samples
	 */
	public boolean triggerSampleTransfer() {

		Logger.getInstance().info(this, "Starting sample transfer.");
		try {
			getService().doTriggerSampleTransfer();
			return true;
		} catch (RemoteException e) {
		} catch (NullPointerException e) {
		}
		return false;
	}

	/**
	 * Getter for the text view
	 * 
	 * @return the text view for logging
	 */
	private TextView getLogView() {
		return logView;
	}

	/**
	 * Method to handle for log events
	 * 
	 * @param logEvent
	 *            the log Event
	 */
	private void handleLogEvent(LogEvent logEvent) {
		TextView logView = getLogView();

		String message = logEvent.getMessage();
		logView.append(message);
		logView.append("\n");

		if(message.startsWith("TransferManagerImpl: Preparation started") ||
				message.startsWith("TransferManagerImpl: Transmission started")){
			isTransferingData = true;			
			updateButtons();
		} else if (message.startsWith("TransferManagerImpl: Transmission ended")){
			isTransferingData = false;
			updateButtons();
		}
		
		// scroll to end
		logView.setSelected(true);
		Spannable textDisplayed = (Spannable) logView.getText();
		Selection.setSelection(textDisplayed, textDisplayed.length());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.unikassel.android.sdcframework.util.facade.EventObserver#onEvent(de.
	 * unikassel.android.sdcframework.util.facade.ObservableEventSource,
	 * de.unikassel.android.sdcframework.util.facade.ObservableEvent)
	 */
	@Override
	public void onEvent(ObservableEventSource<? extends LogEvent> eventSource,
			LogEvent observedEvent) {
		Message msg = Message.obtain();
		msg.obj = observedEvent;
		msg.setTarget(logEventHandler);
		msg.sendToTarget();

	}

	protected synchronized void setService(ISDCService service) {
		this.service = service;
	}

	protected synchronized ISDCService getService() {
		return service;
	}

	/***************************************************************
	 * Options Menu *
	 ***************************************************************/

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPrepareOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		boolean isServiceRunning = ServiceUtils.isServiceRunning(
				getApplicationContext(), getServiceClass());

		MenuItem menuItem = menu.findItem(R.id.action_disable_transfer);
		if (menuItem != null) {
			menuItem.setEnabled(isServiceRunning);
		}
		menuItem = menu.findItem(R.id.action_force_transfer);
		if (menuItem != null) {
			menuItem.setEnabled(isServiceRunning);
		}
		return super.onPrepareOptionsMenu(menu);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_disable_transfer) {
			return enableSampleTransfer(false);
		} else if (itemId == R.id.action_force_transfer) {
			return enableSampleTransfer(true) && triggerSampleTransfer();
		}
		return super.onOptionsItemSelected(item);
	}

	/**********************************************
	 * HELPER
	 **********************************************/

	/**
	 * Set buttons to correct state Assumes isRunning and isRecording is
	 * properly set
	 */
	private void updateButtons() {

		if (!isRunning) {
			// Service Stopped
			toggleServiceStateButton.setEnabled(true);
			toggleServiceStateButton.setChecked(false);

			toggleRecordingStateButton.setEnabled(false);
			toggleRecordingStateButton.setChecked(false);

			transferButton.setEnabled(false);
			transferSpinner.setVisibility(View.INVISIBLE);
			
			annodationDropDown.setEnabled(false);
			annotationTextField.setEnabled(false);
			annotateButton.setEnabled(false);
			recordingSpinner.setVisibility(View.INVISIBLE);
		} else {
			if (!isRecording.get()) {
				// Service Running and not recording samples
				toggleServiceStateButton.setEnabled(true);
				toggleServiceStateButton.setChecked(true);

				toggleRecordingStateButton.setEnabled(true);
				toggleRecordingStateButton.setChecked(false);

				transferButton.setEnabled(true);

				annodationDropDown.setEnabled(false);
				annotationTextField.setEnabled(false);
				annotateButton.setEnabled(false);
				recordingSpinner.setVisibility(View.INVISIBLE);
			} else {
				// Service Running and recording samples
				toggleServiceStateButton.setEnabled(false);
				toggleServiceStateButton.setChecked(true);

				toggleRecordingStateButton.setEnabled(true);
				toggleRecordingStateButton.setChecked(true);

				annodationDropDown.setEnabled(true);
				annotationTextField.setEnabled(true);
				annotateButton.setEnabled(true);
				recordingSpinner.setVisibility(View.VISIBLE);
			}
			
			if (isTransferingData){
				transferSpinner.setVisibility(View.VISIBLE);
			} else {
				transferSpinner.setVisibility(View.INVISIBLE);
			}

		}
	}

	private void setRunningFlag() {
		isRunning = ServiceUtils.isServiceRunning(getApplicationContext(),
				ActivityConstants.serviceClass);
	}

	private void startRecording() {
		enableSampling(true);
		isRecording.set(true);
	}

	private void stopRecording() {
		enableSampling(false);
		isRecording.set(false);
	}
	
}
