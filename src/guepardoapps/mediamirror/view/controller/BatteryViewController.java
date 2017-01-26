package guepardoapps.mediamirror.view.controller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.view.View;
import android.widget.TextView;
import guepardoapps.mediamirror.common.Constants;
import guepardoapps.mediamirror.common.SmartMirrorLogger;
import guepardoapps.toolset.controller.ReceiverController;
import guepardoapps.mediamirror.R;

public class BatteryViewController {

	private static final String TAG = BatteryViewController.class.getName();
	private SmartMirrorLogger _logger;

	private boolean _isInitialized;
	private boolean _screenEnabled;

	private Context _context;
	private ReceiverController _receiverController;

	private View _batteryAlarmView;
	private TextView _batteryValueTextView;

	public BatteryViewController(Context context) {
		_logger = new SmartMirrorLogger(TAG);
		_context = context;
		_receiverController = new ReceiverController(_context);
	}

	public void onCreate() {
		_logger.Debug("onCreate");

		_screenEnabled = true;

		_batteryAlarmView = (View) ((Activity) _context).findViewById(R.id.batteryAlarm);
		_batteryValueTextView = (TextView) ((Activity) _context).findViewById(R.id.batteryTextView);
	}

	public void onPause() {
		_logger.Debug("onPause");
	}

	public void onResume() {
		_logger.Debug("onResume");
		if (!_isInitialized) {
			_logger.Debug("Initializing!");
			_context.registerReceiver(_batteryInfoReveicer, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			_receiverController.RegisterReceiver(_screenEnableReceiver,
					new String[] { Constants.BROADCAST_SCREEN_ENABLED });
			_receiverController.RegisterReceiver(_screenDisableReceiver,
					new String[] { Constants.BROADCAST_SCREEN_OFF, Constants.BROADCAST_SCREEN_SAVER });
			_isInitialized = true;
		} else {
			_logger.Warn("Is ALREADY initialized!");
		}
	}

	public void onDestroy() {
		_logger.Debug("onDestroy");
		_context.unregisterReceiver(_batteryInfoReveicer);
		_receiverController.UnregisterReceiver(_screenEnableReceiver);
		_receiverController.UnregisterReceiver(_screenDisableReceiver);
		_isInitialized = false;
	}

	private BroadcastReceiver _batteryInfoReveicer = new BroadcastReceiver() {
		@Override
		public void onReceive(Context ctxt, Intent intent) {
			if (!_screenEnabled) {
				_logger.Debug("Screen is not enabled!");
				return;
			}
			
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			_batteryValueTextView.setText(String.valueOf(level) + "%");
			if (level > 15) {
				_batteryAlarmView.setBackgroundResource(R.drawable.circle_green);
			} else if (level <= 15 && level > 5) {
				_batteryAlarmView.setBackgroundResource(R.drawable.circle_yellow);
			} else {
				_batteryAlarmView.setBackgroundResource(R.drawable.circle_red);
			}
		}
	};

	private BroadcastReceiver _screenEnableReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_screenEnabled = true;

			_batteryAlarmView = (View) ((Activity) _context).findViewById(R.id.batteryAlarm);
			_batteryValueTextView = (TextView) ((Activity) _context).findViewById(R.id.batteryTextView);
		}
	};

	private BroadcastReceiver _screenDisableReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			_screenEnabled = false;
		}
	};
}