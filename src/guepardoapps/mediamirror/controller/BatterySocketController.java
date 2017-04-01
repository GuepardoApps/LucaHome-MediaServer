package guepardoapps.mediamirror.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Toast;

import guepardoapps.library.lucahome.common.enums.MediaMirrorSelection;

import guepardoapps.library.toastview.ToastView;

import guepardoapps.library.toolset.controller.NetworkController;
import guepardoapps.library.toolset.controller.ReceiverController;

import guepardoapps.mediamirror.common.SmartMirrorLogger;
import guepardoapps.mediamirror.common.constants.RaspPiConstants;
import guepardoapps.mediamirror.services.RESTService;

public class BatterySocketController {

	private static final String TAG = BatterySocketController.class.getSimpleName();
	private SmartMirrorLogger _logger;

	private static final int LOWER_BATTERY_LIMIT = 10;
	private static final int UPPER_BATTERY_LIMIT = 90;

	private boolean _isInitialized;
	private boolean _isSocketActive = false;

	private Context _context;
	private NetworkController _networkController;
	private ReceiverController _receiverController;

	private BroadcastReceiver _batteryInfoReveicer = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
			if (level > UPPER_BATTERY_LIMIT) {
				disableBatterySocket();
			} else if (level < LOWER_BATTERY_LIMIT) {
				enableBatterySocket();
			}
		}
	};

	public BatterySocketController(Context context) {
		_logger = new SmartMirrorLogger(TAG);
		_logger.Info(TAG + " created");
		_context = context;
		_receiverController = new ReceiverController(_context);
		_networkController = new NetworkController(_context, null);
	}

	public void Start() {
		_logger.Debug("Start");
		if (!_isInitialized) {
			_logger.Debug("Initializing!");
			_receiverController.RegisterReceiver(_batteryInfoReveicer, new String[] { Intent.ACTION_BATTERY_CHANGED });
			_isInitialized = true;
		} else {
			_logger.Warn("Is ALREADY initialized!");
		}
	}

	public void Dispose() {
		_logger.Debug("Dispose");
		_receiverController.UnregisterReceiver(_batteryInfoReveicer);
		_isInitialized = false;
	}

	private void enableBatterySocket() {
		if (_isSocketActive) {
			_logger.Warn("Already activated socket!");
			return;
		}

		_logger.Debug("enableBatterySocket");
		setBatterySocket(true);
	}

	private void disableBatterySocket() {
		if (!_isSocketActive) {
			_logger.Warn("Already deactivated socket!");
			return;
		}

		_logger.Debug("disableBatterySocket");
		setBatterySocket(false);
	}

	private void setBatterySocket(boolean enable) {
		String localIp = _networkController.GetIpAddress();
		if (localIp != null) {
			try {
				String localSocket = MediaMirrorSelection.GetByIp(localIp).GetSocket();
				if (localSocket != null) {
					_logger.Debug("setBatterySocket " + localSocket + " to "
							+ ((enable) ? RaspPiConstants.SOCKET_STATE_ON : RaspPiConstants.SOCKET_STATE_OFF));

					Intent serviceIntent = new Intent(_context, RESTService.class);
					Bundle serviceData = new Bundle();

					serviceData.putString(RaspPiConstants.BUNDLE_REST_ACTION, RaspPiConstants.SET_SOCKET + localSocket
							+ ((enable) ? RaspPiConstants.SOCKET_STATE_ON : RaspPiConstants.SOCKET_STATE_OFF));
					serviceData.putString(RaspPiConstants.BUNDLE_REST_DATA, "");
					serviceData.putString(RaspPiConstants.BUNDLE_REST_BROADCAST, "");

					serviceIntent.putExtras(serviceData);
					_context.startService(serviceIntent);

					_isSocketActive = enable;
				} else {
					_logger.Error("Did not found socket for " + localIp);
					ToastView.error(_context, "Did not found socket for " + localIp, Toast.LENGTH_LONG).show();
				}
			} catch (Exception ex) {
				_logger.Error(ex.toString());
				ToastView.error(_context, "Did not found socket for " + localIp, Toast.LENGTH_LONG).show();
			}
		}
	}
}
