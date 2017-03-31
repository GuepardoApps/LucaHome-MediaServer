package guepardoapps.mediamirror.view.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import guepardoapps.library.toolset.controller.ReceiverController;

import guepardoapps.mediamirror.R;
import guepardoapps.mediamirror.common.SmartMirrorLogger;
import guepardoapps.mediamirror.common.constants.Broadcasts;
import guepardoapps.mediamirror.common.constants.Bundles;
import guepardoapps.mediamirror.common.constants.Enables;
import guepardoapps.mediamirror.model.*;

import guepardoapps.test.DateViewControllerTest;

public class DateViewController {

	private static final String TAG = DateViewController.class.getSimpleName();
	private SmartMirrorLogger _logger;

	private boolean _isInitialized;
	private boolean _screenEnabled;

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

		_screenEnabled = true;

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
			_receiverController.RegisterReceiver(_updateViewReceiver, new String[] { Broadcasts.SHOW_DATE_MODEL });
			_receiverController.RegisterReceiver(_screenEnableReceiver, new String[] { Broadcasts.SCREEN_ENABLED });
			_receiverController.RegisterReceiver(_screenDisableReceiver,
					new String[] { Broadcasts.SCREEN_OFF, Broadcasts.SCREEN_SAVER });
			_isInitialized = true;
			_logger.Debug("Initializing!");

			if (Enables.TESTING) {
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
		_receiverController.UnregisterReceiver(_screenEnableReceiver);
		_receiverController.UnregisterReceiver(_screenDisableReceiver);

		_isInitialized = false;
	}

	private BroadcastReceiver _updateViewReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!_screenEnabled) {
				_logger.Debug("Screen is not enabled!");
				return;
			}

			_logger.Debug("_updateViewReceiver onReceive");
			DateModel model = (DateModel) intent.getSerializableExtra(Bundles.DATE_MODEL);
			if (model != null) {
				_logger.Debug(model.toString());

				_weekdayTextView.setText(model.GetWeekday());
				_dateTextView.setText(model.GetDate());
				_timeTextView.setText(model.GetTime());
			} else {
				_logger.Warn("model is null!");
			}

			if (Enables.TESTING) {
				_dateViewTest.ValidateView(_weekdayTextView.getText().toString(), _dateTextView.getText().toString(),
						_timeTextView.getText().toString());
			}
		}
	};

	private BroadcastReceiver _screenEnableReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_screenEnabled = true;

			_weekdayTextView = (TextView) ((Activity) _context).findViewById(R.id.weekdayTextView);
			_dateTextView = (TextView) ((Activity) _context).findViewById(R.id.dateTextView);
			_timeTextView = (TextView) ((Activity) _context).findViewById(R.id.timeTextView);
		}
	};

	private BroadcastReceiver _screenDisableReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_screenEnabled = false;
		}
	};
}
