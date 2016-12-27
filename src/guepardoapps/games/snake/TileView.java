package guepardoapps.games.snake;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import guepardoapps.mediamirror.R;
import guepardoapps.mediamirror.common.Constants;

import guepardoapps.toolset.common.Logger;

public class TileView extends View {
	private static final String TAG = TileView.class.getName();
	private Logger _logger;

	protected static int _tileSize;

	protected static int _tileCountX;
	protected static int _tileCountY;

	private static int _offsetX;
	private static int _offsetY;

	private Bitmap[] _tileArray;
	private int[][] _tileGrid;

	private final Paint _paint = new Paint();

	public TileView(Context context, AttributeSet attributeSet, int defStyle) {
		super(context, attributeSet, defStyle);
		_logger = new Logger(TAG, Constants.DEBUGGING_ENABLED);
		_logger.Debug("Created TileView...");

		TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TileView);
		_tileSize = typedArray.getInt(R.styleable.TileView_tileSize, 12);
		typedArray.recycle();
	}

	public TileView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		_logger = new Logger(TAG, Constants.DEBUGGING_ENABLED);
		_logger.Debug("Created TileView...");

		TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.TileView);
		_tileSize = typedArray.getInt(R.styleable.TileView_tileSize, 12);
		typedArray.recycle();
	}

	public void resetTiles(int tilecount) {
		_logger.Debug("resetTiles");
		_tileArray = new Bitmap[tilecount];
	}

	@Override
	protected void onSizeChanged(int width, int height, int prevwidth, int prevheight) {
		_logger.Debug("onSizeChanged");

		_tileCountX = (int) Math.floor(width / _tileSize);
		_tileCountY = (int) Math.floor(height / _tileSize);

		_offsetX = ((width - (_tileSize * _tileCountX)) / 2);
		_offsetY = ((height - (_tileSize * _tileCountY)) / 2);

		_tileGrid = new int[_tileCountX][_tileCountY];
		clearTiles();
	}

	public void loadTile(int key, Drawable tile) {
		_logger.Debug("loadTile");
		Bitmap bitmap = Bitmap.createBitmap(_tileSize, _tileSize, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		tile.setBounds(0, 0, _tileSize, _tileSize);
		tile.draw(canvas);
		_tileArray[key] = bitmap;
	}

	public void clearTiles() {
		_logger.Debug("clearTiles");
		for (int x = 0; x < _tileCountX; x++) {
			for (int y = 0; y < _tileCountY; y++) {
				setTile(0, x, y);
			}
		}
	}

	public void setTile(int tileindex, int x, int y) {
		_logger.Debug("setTile");
		_tileGrid[x][y] = tileindex;
	}

	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		_logger.Debug("onDraw");
		for (int x = 0; x < _tileCountX; x++) {
			for (int y = 0; y < _tileCountY; y++) {
				if (_tileGrid[x][y] > 0) {
					canvas.drawBitmap(_tileArray[_tileGrid[x][y]], _offsetX + x * _tileSize, _offsetY + y * _tileSize,
							_paint);
				}
			}
		}
	}
}