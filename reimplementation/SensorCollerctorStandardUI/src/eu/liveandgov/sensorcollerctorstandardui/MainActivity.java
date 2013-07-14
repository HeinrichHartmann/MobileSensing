package eu.liveandgov.sensorcollerctorstandardui;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

	private ToggleButton serviceToggleButton;
	private ToggleButton recordingToggleButton;
	private ProgressBar recordingProgressBar;
	private Button transferButton;
	private ProgressBar transferProgressBar;
	private Spinner annotationSpinner;
	private EditText annotationText;
	private Button sendButton;
	private TextView logTextView;

	private BroadcastReceiver universalBroadcastReceiver;

	private boolean isRunning, isRecording, isTransfering;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Prevent keyboard automatically popping up
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Setup Service Toggle Button
		serviceToggleButton = (ToggleButton) findViewById(R.id.serviceToggleButton);

		// Setup Recording Toggle Button
		recordingToggleButton = (ToggleButton) findViewById(R.id.recordingToggleButton);
		recordingToggleButton.setEnabled(false);

		// Setup Recording Progress Bar
		recordingProgressBar = (ProgressBar) findViewById(R.id.recordingProgressBar);
		recordingProgressBar.setVisibility(View.INVISIBLE);

		// Setup Transfer Button
		transferButton = (Button) findViewById(R.id.transferButton);
		transferButton.setEnabled(false);

		// Setup Transfer Progress Bar
		transferProgressBar = (ProgressBar) findViewById(R.id.transferProgressBar);
		transferProgressBar.setVisibility(View.INVISIBLE);

		// Setup Annotation Spinner
		annotationSpinner = (Spinner) findViewById(R.id.annotationSpinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.movements_array,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		annotationSpinner.setAdapter(adapter);
		annotationSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						String annotation = annotationSpinner.getSelectedItem()
								.toString();
						sendAnnotation("Activity: " + annotation);
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}
				});
		annotationSpinner.setEnabled(false);

		// Setup Annotation Text
		annotationText = (EditText) findViewById(R.id.annotationText);
		annotationText.setEnabled(false);

		// Setup Send Button
		sendButton = (Button) findViewById(R.id.sendButton);
		sendButton.setEnabled(false);

		// Setup Log Text View
		logTextView = (TextView) findViewById(R.id.logTextView);
		// logTextView.setSingleLine(false);
		logTextView.setMovementMethod(new ScrollingMovementMethod());

		// Setup Broadcast Receiver
		universalBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				if (action.equals(IntentConstants.ACTION_STATUS)) {
					updateStates(intent.getBooleanExtra("running", false),
							intent.getBooleanExtra("recording", false));
				} else if (action.equals(IntentConstants.ACTION_LOG)) {
					addLogEntry(intent.getStringExtra("message"));
				}
			}
		};

		getApplicationContext().registerReceiver(universalBroadcastReceiver,
				new IntentFilter(IntentConstants.ACTION_STATUS));
		getApplicationContext().registerReceiver(universalBroadcastReceiver,
				new IntentFilter(IntentConstants.ACTION_LOG));

		// Get current states
		requestStatus();

	}

	@Override
	protected void onResume() {
		super.onResume();
		// Get current states
		requestStatus();
	}

	public void onServiceToggleButtonClick(View view) {
		if (isRunning) {
			Intent intent = new Intent(IntentConstants.ACTION_SERVICE_STOP);
			getApplicationContext().startService(intent);
		} else {
			Intent intent = new Intent(IntentConstants.ACTION_SERVICE_START);
			getApplicationContext().startService(intent);
		}
	}

	public void onRecordingToggleButtonClick(View view) {
		if (isRecording) {
			Intent intent = new Intent(IntentConstants.ACTION_SAMPLING_DISABLE);
			getApplicationContext().startService(intent);
			intent = new Intent(IntentConstants.ACTION_SAMPLETRANSFER_TRIGGER);
			getApplicationContext().startService(intent);
		} else {
			Intent intent = new Intent(IntentConstants.ACTION_SAMPLING_ENABLE);
			getApplicationContext().startService(intent);
		}
	}

	public void onTransferButtonClick(View view) {
		Intent intent = new Intent(
				IntentConstants.ACTION_SAMPLETRANSFER_TRIGGER);
		getApplicationContext().startService(intent);
	}

	public void onSendButtonClick(View view) {
		String annotation = annotationSpinner.getSelectedItem().toString();
		sendAnnotation("Activity: " + annotation);

		annotation = annotationText.getText().toString();
		if (annotation != null && annotation.length() != 0) {
			// store annotation text as tag sensor information
			sendAnnotation(annotation);
		}
	}

	private void sendAnnotation(String annotation) {
		Intent intent = new Intent(IntentConstants.ACTION_ANNOTATE);
		intent.putExtra("tag", annotation);
		getApplicationContext().startService(intent);
	}

	private void requestStatus() {
		Intent intent = new Intent(IntentConstants.ACTION_GET_STATUS);
		getApplicationContext().startService(intent);
	}

	private void updateStates(boolean isRunning, boolean isRecording) {
		this.isRunning = isRunning;
		this.isRecording = isRecording;
		updateButtons();
	}

	private void updateButtons() {
		if (isRunning) {
			recordingToggleButton.setEnabled(true);
			serviceToggleButton.setChecked(true);
			transferButton.setEnabled(true);
		} else {
			recordingToggleButton.setEnabled(false);
			recordingProgressBar.setVisibility(View.INVISIBLE);
			serviceToggleButton.setChecked(false);
			transferButton.setEnabled(false);
			annotationSpinner.setEnabled(false);
			annotationText.setEnabled(false);
			sendButton.setEnabled(false);
			return;
		}
		if (isRecording) {
			serviceToggleButton.setEnabled(false);
			recordingToggleButton.setChecked(true);
			annotationSpinner.setEnabled(true);
			annotationText.setEnabled(true);
			sendButton.setEnabled(true);
			recordingProgressBar.setVisibility(View.VISIBLE);
		} else {
			serviceToggleButton.setEnabled(true);
			recordingToggleButton.setChecked(false);
			annotationSpinner.setEnabled(false);
			annotationText.setEnabled(false);
			sendButton.setEnabled(false);
			recordingProgressBar.setVisibility(View.INVISIBLE);
		}
		if (isTransfering) {
			transferProgressBar.setVisibility(View.VISIBLE);
		} else {
			transferProgressBar.setVisibility(View.INVISIBLE);
		}
	}

	private void addLogEntry(String message) {
		logTextView.append(message + "\n");
		if (message.startsWith("TransferManagerImpl: Preparation started")
				|| message
						.startsWith("TransferManagerImpl: Transmission started")) {
			isTransfering = true;
			updateButtons();
		} else if (message
				.startsWith("TransferManagerImpl: Transmission ended")) {
			isTransfering = false;
			updateButtons();
		}
		// scroll to end
		logTextView.setSelected(true);
		Spannable textDisplayed = (Spannable) logTextView.getText();
		Selection.setSelection(textDisplayed, textDisplayed.length());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return false;
	}

}
