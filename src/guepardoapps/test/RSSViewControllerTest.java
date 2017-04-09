package guepardoapps.test;

import android.content.Context;

import guepardoapps.library.lucahome.common.enums.RSSFeed;

import guepardoapps.library.toolset.controller.BroadcastController;

import guepardoapps.mediamirror.common.SmartMirrorLogger;
import guepardoapps.mediamirror.common.constants.Broadcasts;
import guepardoapps.mediamirror.common.constants.Bundles;
import guepardoapps.mediamirror.view.model.RSSModel;

public class RSSViewControllerTest {

	private static final String TAG = RSSViewControllerTest.class.getSimpleName();
	private SmartMirrorLogger _logger;

	private Context _context;
	private BroadcastController _broadcastController;

	private RSSFeed _testRSSFeed = RSSFeed.GEHIRN_UND_GEIST;
	private boolean _testVisibility = true;

	public RSSViewControllerTest(Context context) {
		_logger = new SmartMirrorLogger(TAG);
		_logger.Info("Created test for RSSView");

		_context = context;
		_broadcastController = new BroadcastController(_context);

		sendTestBroadcast();
	}

	private void sendTestBroadcast() {
		_logger.Debug("sendTestBroadcast");

		RSSModel model = new RSSModel(_testRSSFeed, _testVisibility);
		_broadcastController.SendSerializableBroadcast(Broadcasts.SHOW_RSS_DATA_MODEL, Bundles.RSS_DATA_MODEL, model);
	}

	public boolean ValidateView(RSSFeed rssFeed, boolean visibility) {
		boolean success = true;

		if (rssFeed != _testRSSFeed) {
			_logger.Error("rssFeed FAILED!" + rssFeed.toString() + "!=" + _testRSSFeed.toString());
			success &= false;
		}
		if (visibility != _testVisibility) {
			_logger.Error("visibility FAILED!" + String.valueOf(visibility) + "!=" + String.valueOf(_testVisibility));
			success &= false;
		}

		if (success) {
			_logger.LogTest("Test SUCCEEDED!", success);
		} else {
			_logger.LogTest("Test FAILED!", success);
		}

		return success;
	}
}
