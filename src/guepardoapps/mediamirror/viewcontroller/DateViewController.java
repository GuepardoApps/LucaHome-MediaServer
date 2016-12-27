package guepardoapps.mediamirror.viewcontroller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import guepardoapps.mediamirror.common.Constants;
import guepardoapps.mediamirror.common.SmartMirrorLogger;
import guepardoapps.mediamirror.model.*;
import guepardoapps.mediamirror.test.DateViewControllerTest;
import guepardoapps.mediamirror.R;

import guepardoapps.toolset.controller.ReceiverController;

public class DateViewController {

	private static final String TAG = DateViewController.class.getName();
	private SmartMirrorLogger _logger;

	private boolean _isInitialized;

	private Context _context;
	private ReceiverController _receiverController;

	private TextView _weekdayTextView;
	private TextView _dateTextView;
	private TextView _timeTextView;

	private DateViewControllerTest _dateViewTest;

	public DateViewController(Context context) {
		_logger = new SmartMirrorLogger(TAG);
		_context = context;
		_receiverController = new ReceiverController(_context);
	}

	public void onCreate() {
		_logger.Debug("onCreate");

		_weekdayTextView = (TextView) ((Activity) _context).findViewById(R.id.weekdayTextView);
		_dateTextView = (TextView) ((Activity) _context).findViewById(R.id.dateTextView);
		_timeTextView = (TextView) ((Activity) _context).findViewById(R.id.timeTextView);
	}

	public void onPause() {
		_logger.Debug("onPause");
	}

	public void onResume() {
		_logger.Debug("onResume");
		if (!_isInitialized) {
			_receiverController.RegisterReceiver(_updateViewReceiver,
					new String[] { Constants.BROADCAST_SHOW_DATE_MODEL });
			_isInitialized = true;
			_logger.Debug("Initializing!");

			if (Constants.TESTING_ENABLED) {
				if (_dateViewTest == null) {
					_dateViewTest = new DateViewControllerTest(_context);
				}
			}
		} else {
			_logger.Warn("Is ALREADY initialized!");
		}
	}

	public void onDestroy() {
		_logger.Debug("onDestroy");
		_receiverController.UnregisterReceiver(_updateViewReceiver);
		_isInitialized = false;
	}

	private BroadcastReceiver _updateViewReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_logger.Debug("_updateViewReceiver onReceive");
			DateModel model = (DateModel) intent.getSerializableExtra(Constants.BUNDLE_DATE_MODEL);
			if (model != null) {
				_logger.Debug(model.toString());

				_weekdayTextView.setText(model.GetWeekday());
				_dateTextView.setText(model.GetDate());
				_timeTextView.setText(model.GetTime());
			} else {
				_logger.Warn("model is null!");
			}

			if (Constants.TESTING_ENABLED) {
				_dateViewTest.ValidateView(_weekdayTextView.getText().toString(), _dateTextView.getText().toString(),
						_timeTextView.getText().toString());
			}
		}
	};
}