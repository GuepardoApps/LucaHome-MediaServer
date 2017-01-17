package guepardoapps.mediamirror.server;

import android.content.Context;
import android.content.Intent;
import guepardoapps.games.common.GameConstants;
import guepardoapps.mediamirror.common.Constants;
import guepardoapps.mediamirror.common.SmartMirrorLogger;
import guepardoapps.mediamirror.common.enums.RSSFeed;
import guepardoapps.mediamirror.common.enums.YoutubeId;
import guepardoapps.mediamirror.controller.MediaVolumeController;
import guepardoapps.mediamirror.controller.ScreenController;
import guepardoapps.mediamirror.model.CenterModel;
import guepardoapps.mediamirror.model.RSSModel;

import guepardoapps.toolset.controller.BroadcastController;
import guepardoapps.toolset.controller.CommandController;

public class DataHandler {

	private static final String TAG = DataHandler.class.getName();
	private SmartMirrorLogger _logger;

	private Context _context;

	private CommandController _commandController;
	private BroadcastController _broadcastController;
	private MediaVolumeController _mediaVolumeController;

	public DataHandler(Context context) {
		_logger = new SmartMirrorLogger(TAG);

		_context = context;

		_commandController = new CommandController(_context);
		_broadcastController = new BroadcastController(_context);
		_mediaVolumeController = new MediaVolumeController(_context);
	}

	public String PerformAction(String command) {
		if (command == null) {
			_logger.Warn("Command is null!");
			return "Command is null!";
		}

		_logger.Debug("PerformAction with data: " + command);
		if (command.startsWith("ACTION:")) {
			ServerAction action = convertCommandToAction(command);

			if (action != null) {
				_logger.Debug("action: " + action.toString());
				String data = convertCommandToData(command);
				_logger.Debug("data: " + data);

				switch (action) {
				case PING:
					return "Mediamirror available!";
				case SHOW_YOUTUBE_VIDEO:
					if (data.length() < 4) {
						int youtubeIdInt = -1;
						try {
							youtubeIdInt = Integer.parseInt(data);
						} catch (Exception e) {
							_logger.Error(e.toString());
							_logger.Warn("Setting youtubeId to 0!");
							youtubeIdInt = 0;
						} finally {
							YoutubeId youtubeId = YoutubeId.GetById(youtubeIdInt);
							if (youtubeId == null) {
								_logger.Warn("youtubeId is null! Setting to default");
								youtubeId = YoutubeId.THE_GOOD_LIFE_STREAM;
							}
							CenterModel youtubeModel = new CenterModel(false, "", true, youtubeId.GetYoutubeId(), false,
									"");
							_logger.Info("Created center model: " + youtubeModel.toString());
							_broadcastController.SendSerializableBroadcast(Constants.BROADCAST_SHOW_CENTER_MODEL,
									Constants.BUNDLE_CENTER_MODEL, youtubeModel);
						}
					} else if (data.length() == 11) {
						CenterModel youtubeModel = new CenterModel(false, "", true, data, false, "");
						_logger.Info("Created center model: " + youtubeModel.toString());
						_broadcastController.SendSerializableBroadcast(Constants.BROADCAST_SHOW_CENTER_MODEL,
								Constants.BUNDLE_CENTER_MODEL, youtubeModel);
					} else {
						_logger.Warn("Wrong size for data of youtube id!");
					}
					break;
				case PLAY_YOUTUBE_VIDEO:
					_context.sendBroadcast(new Intent(Constants.BROADCAST_PLAY_VIDEO));
					break;
				case STOP_YOUTUBE_VIDEO:
					_context.sendBroadcast(new Intent(Constants.BROADCAST_STOP_VIDEO));
					break;
				case SHOW_WEBVIEW:
					CenterModel webviewModel = new CenterModel(false, "", false, null, true, data);
					_logger.Info("Created center model: " + webviewModel.toString());
					_broadcastController.SendSerializableBroadcast(Constants.BROADCAST_SHOW_CENTER_MODEL,
							Constants.BUNDLE_CENTER_MODEL, webviewModel);
					break;
				case SHOW_CENTER_TEXT:
					CenterModel centerTextModel = new CenterModel(true, data, false, null, false, "");
					_logger.Info("Created center model: " + centerTextModel.toString());
					_broadcastController.SendSerializableBroadcast(Constants.BROADCAST_SHOW_CENTER_MODEL,
							Constants.BUNDLE_CENTER_MODEL, centerTextModel);
					break;
				case SET_RSS_FEED:
					int feedIdInt = -1;
					try {
						feedIdInt = Integer.parseInt(data);
					} catch (Exception e) {
						_logger.Error(e.toString());
						_logger.Warn("Setting feedIdInt to 0!");
						feedIdInt = 0;
					} finally {
						RSSFeed rssFeed = RSSFeed.GetById(feedIdInt);
						if (rssFeed == null) {
							_logger.Warn("rssFeed is null! Setting to default");
							rssFeed = RSSFeed.DEFAULT;
						}
						RSSModel rSSFeedModel = new RSSModel(rssFeed, true);
						_logger.Info("Created rssfeed model: " + rSSFeedModel.toString());
						_broadcastController.SendSerializableBroadcast(Constants.BROADCAST_UPDATE_RSS_FEED,
								Constants.BUNDLE_RSS_MODEL, rSSFeedModel);
					}
					break;
				case RESET_RSS_FEED:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_RESET_RSS_FEED);
					break;
				case UPDATE_CURRENT_WEATHER:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_PERFORM_CURRENT_WEATHER_UPDATE);
					break;
				case UPDATE_FORECAST_WEATHER:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_PERFORM_FORECAST_WEATHER_UPDATE);
					break;
				case UPDATE_RASPBERRY_TEMPERATURE:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_PERFORM_TEMPERATURE_UPDATE);
					break;
				case UPDATE_IP_ADDRESS:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_PERFORM_IP_ADDRESS_UPDATE);
					break;
				case UPDATE_BIRTHDAY_ALARM:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_PERFORM_BIRTHDAY_UPDATE);
					break;
				case INCREASE_VOLUME:
					_mediaVolumeController.IncreaseVolume();
					return action.toString() + ":" + _mediaVolumeController.GetCurrentVolume();
				case DECREASE_VOLUME:
					_mediaVolumeController.DecreaseVolume();
					return action.toString() + ":" + _mediaVolumeController.GetCurrentVolume();
				case MUTE_VOLUME:
					_mediaVolumeController.MuteVolume();
					return action.toString() + ":Muted";
				case UNMUTE_VOLUME:
					_mediaVolumeController.UnmuteVolume();
					return action.toString() + ":" + _mediaVolumeController.GetCurrentVolume();
				case GET_CURRENT_VOLUME:
					return action.toString() + ":" + _mediaVolumeController.GetCurrentVolume();
				case INCREASE_SCREEN_BRIGHTNESS:
					_broadcastController.SendIntBroadcast(Constants.BROADCAST_ACTION_SCREEN_BRIGHTNESS,
							Constants.BUNDLE_SCREEN_BRIGHTNESS, ScreenController.INCREASE);
					break;
				case DECREASE_SCREEN_BRIGHTNESS:
					_broadcastController.SendIntBroadcast(Constants.BROADCAST_ACTION_SCREEN_BRIGHTNESS,
							Constants.BUNDLE_SCREEN_BRIGHTNESS, ScreenController.DECREASE);
					break;
				case PLAY_ALARM:
					// TODO implement
					break;
				case STOP_ALARM:
					// TODO implement
					break;
				case GAME_COMMAND:
					_broadcastController.SendStringBroadcast(Constants.BROADCAST_GAME_COMMAND,
							Constants.BUNDLE_GAME_COMMAND, data);
					break;
				case GAME_PONG_START:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_START_PONG);
					break;
				case GAME_PONG_STOP:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_STOP_PONG);
					break;
				case GAME_PONG_PAUSE:
					_broadcastController.SendStringBroadcast(Constants.BROADCAST_GAME_COMMAND,
							Constants.BUNDLE_GAME_COMMAND, GameConstants.GAME + ":" + GameConstants.PAUSE);
					break;
				case GAME_PONG_RESUME:
					_broadcastController.SendStringBroadcast(Constants.BROADCAST_GAME_COMMAND,
							Constants.BUNDLE_GAME_COMMAND, GameConstants.GAME + ":" + GameConstants.RESUME);
					break;
				case GAME_PONG_RESTART:
					_broadcastController.SendStringBroadcast(Constants.BROADCAST_GAME_COMMAND,
							Constants.BUNDLE_GAME_COMMAND, GameConstants.GAME + ":" + GameConstants.RESTART);
					break;
				case GAME_SNAKE_START:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_START_SNAKE);
					break;
				case GAME_SNAKE_STOP:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_STOP_SNAKE);
					break;
				case GAME_TETRIS_START:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_START_TETRIS);
					break;
				case GAME_TETRIS_STOP:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_STOP_TETRIS);
					break;
				case SCREEN_ON:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_SCREEN_ON);
					break;
				case SCREEN_OFF:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_SCREEN_OFF);
					break;
				case SCREEN_SAVER:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_SCREEN_SAVER);
					break;
				case SCREEN_NORMAL:
					_broadcastController.SendSimpleBroadcast(Constants.BROADCAST_SCREEN_NORMAL);
					break;
				case SYSTEM_REBOOT:
					_commandController.RebootDevice();
					break;
				case SYSTEM_SHUTDOWN:
					_commandController.ShutDownDevice();
					break;
				default:
					_logger.Warn("Action not handled!\n" + action.toString());
					return "Action not handled!\n" + action.toString();
				}
				return "OK:Command performed:" + action.toString();
			} else {
				_logger.Warn("Action failed to be converted! Is null!\n" + command);
				return "Action failed to be converted! Is null!\n" + command;
			}
		} else {
			_logger.Warn("Command has wrong format!\n" + command);
			return "Command has wrong format!\n" + command;
		}
	}

	public void Dispose() {
		_mediaVolumeController.Dispose();
	}

	private ServerAction convertCommandToAction(String command) {
		_logger.Debug(command);

		String[] entries = command.split("\\&");
		if (entries.length == 2) {
			String action = entries[0];
			action = action.replace("ACTION:", "");
			_logger.Debug("Action is: " + action);

			ServerAction serverAction = ServerAction.GetByString(action);
			_logger.Debug("Found action: " + serverAction.toString());
			return serverAction;
		}

		_logger.Warn("Wrong size of entries: " + String.valueOf(entries.length));
		return null;
	}

	private String convertCommandToData(String command) {
		_logger.Debug(command);

		String[] entries = command.split("\\&");
		if (entries.length == 2) {
			String data = entries[1];
			data = data.replace("DATA:", "");
			_logger.Debug("Found data: " + data);
			return data;
		}

		_logger.Warn("Wrong size of entries: " + String.valueOf(entries.length));
		return "";
	}
}
