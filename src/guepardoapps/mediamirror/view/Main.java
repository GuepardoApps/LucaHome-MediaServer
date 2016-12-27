package guepardoapps.mediamirror.view;

import com.google.android.youtube.player.YouTubeBaseActivity;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import guepardoapps.mediamirror.common.SmartMirrorLogger;
import guepardoapps.mediamirror.controller.ScreenController;
import guepardoapps.mediamirror.services.*;
import guepardoapps.mediamirror.tts.TTSService;
import guepardoapps.mediamirror.viewcontroller.*;
import guepardoapps.mediamirror.R;

public class Main extends YouTubeBaseActivity {

	private static final String TAG = Main.class.getName();
	private SmartMirrorLogger _logger;

	private static final int[] PERMISSION_REQUEST_IDS = new int[] { 24565730 };
	private static final String[] PERMISSIONS = new String[] { Manifest.permission.WRITE_SETTINGS };

	private Context _context;

	private BatteryViewController _batteryViewController;
	private BirthdayViewController _birthdayViewController;
	private CenterViewController _centerViewController;
	private CurrentWeatherViewController _currentWeatherViewController;
	private DateViewController _dateViewController;
	private ForecastWeatherViewController _forecastWeatherViewController;
	private GameViewController _gameViewController;
	private IpAdressViewController _ipAdressViewController;
	private RaspberryViewController _raspberryViewController;
	private RSSViewController _rssViewController;
	private ToastController _toastController;
	private VolumeViewController _volumeViewController;

	private ScreenController _screenController;

	private TTSService _ttsService;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.main);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		_logger = new SmartMirrorLogger(TAG);
		_logger.Debug("onCreate");

		_context = this;
		checkPermissions();

		initializeController();
		initializeServices();

		_batteryViewController.onCreate();
		_birthdayViewController.onCreate();
		_centerViewController.onCreate();
		_currentWeatherViewController.onCreate();
		_dateViewController.onCreate();
		_forecastWeatherViewController.onCreate();
		_gameViewController.onCreate();
		_ipAdressViewController.onCreate();
		_raspberryViewController.onCreate();
		_rssViewController.onCreate();
		_toastController.onCreate();
		_volumeViewController.onCreate();

		_screenController.onCreate();

		_ttsService.Init();

		startServices();
	}

	@Override
	public void onResume() {
		super.onResume();
		_logger.Debug("onResume");

		_batteryViewController.onResume();
		_birthdayViewController.onResume();
		_centerViewController.onResume();
		_currentWeatherViewController.onResume();
		_dateViewController.onResume();
		_forecastWeatherViewController.onResume();
		_gameViewController.onResume();
		_ipAdressViewController.onResume();
		_raspberryViewController.onResume();
		_rssViewController.onResume();
		_toastController.onResume();
		_volumeViewController.onResume();

		_screenController.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
		_logger.Debug("onPause");

		_batteryViewController.onPause();
		_birthdayViewController.onPause();
		_centerViewController.onPause();
		_currentWeatherViewController.onPause();
		_dateViewController.onPause();
		_forecastWeatherViewController.onPause();
		_gameViewController.onPause();
		_ipAdressViewController.onPause();
		_raspberryViewController.onPause();
		_rssViewController.onPause();
		_toastController.onPause();
		_volumeViewController.onPause();

		_screenController.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		_logger.Debug("onDestroy");

		_batteryViewController.onDestroy();
		_birthdayViewController.onDestroy();
		_centerViewController.onDestroy();
		_currentWeatherViewController.onDestroy();
		_dateViewController.onDestroy();
		_forecastWeatherViewController.onDestroy();
		_gameViewController.onDestroy();
		_ipAdressViewController.onDestroy();
		_raspberryViewController.onDestroy();
		_rssViewController.onDestroy();
		_toastController.onDestroy();
		_volumeViewController.onDestroy();

		_screenController.onDestroy();

		_ttsService.Dispose();
	}

	private void initializeController() {
		_batteryViewController = new BatteryViewController(_context);
		_birthdayViewController = new BirthdayViewController(_context);
		_centerViewController = new CenterViewController(_context);
		_currentWeatherViewController = new CurrentWeatherViewController(_context);
		_dateViewController = new DateViewController(_context);
		_forecastWeatherViewController = new ForecastWeatherViewController(_context);
		_gameViewController = new GameViewController(_context);
		_ipAdressViewController = new IpAdressViewController(_context);
		_raspberryViewController = new RaspberryViewController(_context);
		_rssViewController = new RSSViewController(_context);
		_toastController = new ToastController(_context);
		_volumeViewController = new VolumeViewController(_context);

		_screenController = new ScreenController(_context);
	}

	private void initializeServices() {
		_ttsService = new TTSService(_context);
	}

	private void startServices() {
		startService(new Intent(_context, MainService.class));
		startService(new Intent(_context, TimeListenerService.class));
		startService(new Intent(_context, ControlServiceStateService.class));
	}

	private void checkPermissions() {
		if (PERMISSION_REQUEST_IDS.length == PERMISSIONS.length) {
			for (int index = 0; index < PERMISSIONS.length; index++) {
				if (ContextCompat.checkSelfPermission(_context,
						PERMISSIONS[index]) != PackageManager.PERMISSION_GRANTED) {

					ActivityCompat.requestPermissions((Activity) _context, new String[] { PERMISSIONS[index] },
							PERMISSION_REQUEST_IDS[index]);
				}
			}
		}
	}
}