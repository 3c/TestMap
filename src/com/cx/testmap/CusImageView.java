/**
 *    FILE: MyImageView.java
 *  AUTHOR: CX
 *    DATE: 2012-7-9
 *
 *   Copyright(c) 2011 Mobitide Android Team. All Rights Reserved.
 */
package com.cx.testmap;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

public class CusImageView extends ImageView {

	// This is the base transformation which is used to show the image
	// initially. The current computation for this shows the image in
	// it's entirety, letterboxing as needed. One could choose to
	// show the image as cropped instead.
	//
	// This matrix is recomputed when we go from the thumbnail image to
	// the full size image.

	private boolean isChangeScreen = false;
	private GestureDetector gestureScanner;

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		isChangeScreen = true;
		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			center(true, true);
			// layoutToCenter();
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			center(true, true);
		}
	}

	protected Matrix mBaseMatrix = new Matrix();

	// This is the supplementary transformation which reflects what
	// the user has done in terms of zooming and panning.
	//
	// This matrix remains the same when we go from the thumbnail image
	// to the full size image.
	protected Matrix mSuppMatrix = new Matrix();

	// This is the final matrix which is computed as the concatentation
	// of the base matrix and the supplementary matrix.
	private final Matrix mDisplayMatrix = new Matrix();

	// Temporary buffer used for getting the values out of a matrix.
	private final float[] mMatrixValues = new float[9];

	// The current bitmap being displayed.
	// protected final RotateBitmap mBitmapDisplayed = new RotateBitmap(null);
	protected Bitmap image = null;

	int mThisWidth = -1, mThisHeight = -1;

	float mMaxZoom = 3.0f;// 最大缩放比例
	float mMinZoom = 0.3f;// 最小缩放比例

	private int imageWidth;// 图片的原始宽度
	private int imageHeight;// 图片的原始高度

	private float scaleRate;// 图片适应屏幕的缩放比例
	private boolean isMove = false;

	public CusImageView(Context context) {
		super(context);
		init();
	}

	public CusImageView(Context context, AttributeSet attrs) {

		super(context, attrs);
		init();

	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		if (isMove) {

			float v[] = new float[9];
			Matrix m = getImageMatrix();
			m.getValues(v);
			// 图片实时的上下左右坐标
			float left, right;
			// 图片的实时宽，高
			float width, height;
			width = getScale() * getImageWidth();
			height = getScale() * getImageHeight();
			// 一下逻辑为移动图片和滑动gallery换屏的逻辑。如果没对整个框架了解的非常清晰，改动以下的代码前请三思！！！！！！

			if ((int) height <= GlobalConstants.screenHeight)// 如果图片当前大小<屏幕大小，直接处理滑屏事件
			{
				onScroll(e1, e2, distanceX, distanceY);

			} else {

				left = v[Matrix.MTRANS_X];
				right = left + width;
				Rect r = new Rect();
				getGlobalVisibleRect(r);
				if (distanceX > 0) {
					if (r.left > 0) {
						onScroll(e1, e2, distanceX, distanceY);
					} else if (right < GlobalConstants.screenWidth) {
						onScroll(e1, e2, distanceX, distanceY);
					} else {
						postTranslate(-distanceX, -distanceY);
					}
				} else if (distanceX < 0) {
					if (r.right < GlobalConstants.screenWidth) {
						onScroll(e1, e2, distanceX, distanceY);
					} else if (left > 0) {
						onScroll(e1, e2, distanceX, distanceY);
					} else {
						postTranslate(-distanceX, -distanceY);
					}
				}
			}

		} else {
			onScroll(e1, e2, distanceX, distanceY);
		}

		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// 判断上下边界是否越界

			float width = getScale() * getImageWidth();
			float height = getScale() * getImageHeight();
			// //Log.i(tag, "img width: " + width + "  img height : " +
			// height);
			// //Log.i(tag, "main width "+GlobalConstants.screenWidth+
			// " main height "+GlobalConstants.screenHeight);
			if ((int) width <= GlobalConstants.screenWidth && (int) height <= GlobalConstants.screenHeight)// 如果图片当前大小<屏幕大小，判断边界
			{
				break;
			}
			float v[] = new float[9];
			Matrix m = getImageMatrix();
			m.getValues(v);
			float top = v[Matrix.MTRANS_Y];
			float bottom = top + height;
			// //Log.i(tag, "top : " + top + " bottom:" + bottom);
			if ((int) height >= GlobalConstants.screenHeight) {
				if (top > 0) {
					// //Log.i(tag, "move top : " + (-top));
					postTranslateDur(-top, 200f);
				}
				if (bottom < GlobalConstants.screenHeight) {
					// //Log.i(tag, "move bottom : " +
					// (GlobalConstants.screenHeight - bottom));
					postTranslateDur(GlobalConstants.screenHeight - bottom, 200f);
				}
			}

			break;
		}
		return super.onTouchEvent(event);
	}

	private class MySimpleGesture extends SimpleOnGestureListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.view.GestureDetector.SimpleOnGestureListener#onSingleTapUp
		 * (android.view.MotionEvent)
		 */

		// 按两下的第二下Touch down时触发
		// public boolean onDoubleTap(MotionEvent e) {
		// View view = CusGallery.this.getSelectedView();
		// if (view instanceof MyImageView) {
		// imageView = (MyImageView) view;
		// //Log.i(tag, imageView.getScale() + "   " +
		// imageView.getScaleRate());
		// if (imageView.getScale() > imageView.getScaleRate()) {
		// imageView.zoomTo(imageView.getScaleRate(),
		// GlobalConstants.screenWidth / 2, GlobalConstants.screenHeight / 2,
		// 200f);
		// // imageView.layoutToCenter();
		// } else {
		// imageView.zoomTo(1.0f, GlobalConstants.screenWidth / 2,
		// GlobalConstants.screenHeight / 2, 200f);
		// }
		// }
		// return true;
		// }
	}

	public CusImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 计算图片要适应屏幕需要缩放的比例
	 */
	private void arithScaleRate() {
		float scaleWidth = GlobalConstants.screenWidth / (float) imageWidth;
		float scaleHeight = GlobalConstants.screenHeight / (float) imageHeight;
		scaleRate = Math.min(scaleWidth, scaleHeight);
	}

	public float getScaleRate() {
		return scaleRate;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			event.startTracking();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.isTracking() && !event.isCanceled()) {
			if (getScale() > 1.0f) {
				// If we're zoomed in, pressing Back jumps out to show the
				// entire image, otherwise Back returns the user to the gallery.
				zoomTo(1.0f);
				return true;
			}
		}
		return super.onKeyUp(keyCode, event);
	}

	protected Handler mHandler = new Handler();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.widget.ImageView#setImageDrawable(android.graphics.drawable.Drawable
	 * )
	 */

	@Override
	public void setImageBitmap(Bitmap bitmap) {
		super.setImageBitmap(bitmap);
		image = bitmap;
		if (image != null) {
			imageWidth = image.getWidth();
			imageHeight = image.getHeight();
			// 计算适应屏幕的比例
			arithScaleRate();
			if (scaleRate < 1) {
				// 缩放到屏幕大小
				zoomTo(scaleRate, GlobalConstants.screenWidth / 2f, GlobalConstants.screenHeight / 2f);
			}

			// 居中
			layoutToCenter();
			if (GlobalConstants.isLoad) {
				isChangeScreen = false;
				center(true, true);
			}
		}

		// imageView.zoomTo(scaleRate, Main.screenWidth / 2, Main.screenHeight /
		// 2
		// center(true, true);
	}

	// Center as much as possible in one or both axis. Centering is
	// defined as follows: if the image is scaled down below the
	// view's dimensions then center it (literally). If the image
	// is scaled larger than the view and is translated out of view
	// then translate it back into view (i.e. eliminate black bars).
	protected void center(boolean horizontal, boolean vertical) {
		// if (mBitmapDisplayed.getBitmap() == null) {
		// return;
		// }
		if (image == null) {
			return;
		}

		Matrix m = getImageViewMatrix();

		RectF rect = new RectF(0, 0, image.getWidth(), image.getHeight());
		// RectF rect = new RectF(0, 0, imageWidth * scaleRate, imageHeight *
		// scaleRate);

		m.mapRect(rect);

		float height = rect.height();
		float width = rect.width();

		float deltaX = 0, deltaY = 0;

		if (vertical) {
			int viewHeight;
			if (isChangeScreen)
				viewHeight = GlobalConstants.screenHeight;
			else
				viewHeight = getHeight();
			if (height < viewHeight) {
				deltaY = (viewHeight - height) / 2 - rect.top;
			} else if (rect.top > 0) {
				deltaY = -rect.top;
			} else if (rect.bottom < viewHeight) {
				deltaY = getHeight() - rect.bottom;
			}
		}

		if (horizontal) {
			int viewWidth;
			if (isChangeScreen)
				viewWidth = GlobalConstants.screenWidth;
			else
				viewWidth = getWidth();
			if (width < viewWidth) {
				deltaX = (viewWidth - width) / 2 - rect.left;
			} else if (rect.left > 0) {
				deltaX = -rect.left;
			} else if (rect.right < viewWidth) {
				deltaX = viewWidth - rect.right;
			}
		}

		postTranslate(deltaX, deltaY);
		setImageMatrix(getImageViewMatrix());
	}

	private void init() {
		setScaleType(ImageView.ScaleType.MATRIX);
		gestureScanner = new GestureDetector(new MySimpleGesture());
		setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					baseValue = 0;
					originalScale = getScale();
				}
				if (event.getAction() == MotionEvent.ACTION_MOVE) {
					if (event.getPointerCount() == 2) {
						isMove = false;
						float x = event.getX(0) - event.getX(1);
						float y = event.getY(0) - event.getY(1);
						float value = (float) Math.sqrt(x * x + y * y);// 计算两点的距离
						if (baseValue == 0) {
							baseValue = value;
						} else {
							float scale = value / baseValue;// 当前两点间的距离除以手指落下时两点间的距离就是需要缩放的比例。
							// scale the image
							zoomTo(originalScale * scale, x + event.getX(1), y + event.getY(1));

						}
					} else {
						isMove = true;
					}
				}
				return false;
			}
		});

	}

	/**
	 * 设置图片居中显示
	 */
	public void layoutToCenter() {
		// 正在显示的图片实际宽高
		// float width = (float) (imageWidth * scaleRate);
		// float height = (float) (imageHeight * scaleRate);

		float width = imageWidth * getScale();
		float height = imageHeight * getScale();
		// //Log.i(tag, "getScale()== "+getScale());
		// //Log.i(tag, "imageWidth== "+imageWidth+"  width getScale== "
		// +width);
		// 空白区域宽高
		float fill_width = GlobalConstants.screenWidth - width;
		float fill_height = GlobalConstants.screenHeight - height;
		// 需要移动的距离
		float tran_width = 0f;
		float tran_height = 0f;

		if (fill_width > 0)
			tran_width = fill_width / 2;
		if (fill_height > 0)
			tran_height = fill_height / 2;
		// System.out.println("tran_height ="+tran_height+"  tran_width="+tran_width);
		postTranslate(tran_width, tran_height);

		// setImageMatrix(getImageViewMatrix());
	}

	protected float getValue(Matrix matrix, int whichValue) {
		matrix.getValues(mMatrixValues);
		// mMinZoom =( Main.screenWidth/2f)/imageWidth;

		return mMatrixValues[whichValue];
	}

	// Get the scale factor out of the matrix.
	protected float getScale(Matrix matrix) {
		return getValue(matrix, Matrix.MSCALE_X);
	}

	protected float getScale() {
		return getScale(mSuppMatrix);
	}

	// Combine the base matrix and the supp matrix to make the final matrix.
	protected Matrix getImageViewMatrix() {
		// The final matrix is computed as the concatentation of the base matrix
		// and the supplementary matrix.
		mDisplayMatrix.set(mBaseMatrix);
		mDisplayMatrix.postConcat(mSuppMatrix);
		return mDisplayMatrix;
	}

	static final float SCALE_RATE = 1.25F;

	// Sets the maximum zoom, which is a scale relative to the base matrix. It
	// is calculated to show the image at 400% zoom regardless of screen or
	// image orientation. If in the future we decode the full 3 megapixel image,
	// rather than the current 1024x768, this should be changed down to 200%.
	protected float maxZoom() {
		if (image == null) {
			return 1F;
		}

		float fw = (float) image.getWidth() / (float) mThisWidth;
		float fh = (float) image.getHeight() / (float) mThisHeight;
		float max = Math.max(fw, fh) * 4;
		return max;
	}

	protected void zoomTo(float scale, float centerX, float centerY) {
		if (scale > mMaxZoom) {
			scale = mMaxZoom;
		} else if (scale < mMinZoom) {
			scale = mMinZoom;
		}

		float oldScale = getScale();
		float deltaScale = scale / oldScale;

		mSuppMatrix.postScale(deltaScale, deltaScale, centerX, centerY);
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	protected void zoomTo(final float scale, final float centerX, final float centerY, final float durationMs) {
		final float incrementPerMs = (scale - getScale()) / durationMs;
		final float oldScale = getScale();
		final long startTime = System.currentTimeMillis();
		isChangeScreen = false;
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);
				float target = oldScale + (incrementPerMs * currentMs);
				zoomTo(target, centerX, centerY);
				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}

	protected void zoomTo(float scale) {
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		zoomTo(scale, cx, cy);
	}

	protected void zoomToPoint(float scale, float pointX, float pointY) {
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		panBy(cx - pointX, cy - pointY);
		zoomTo(scale, cx, cy);
	}

	protected void zoomIn() {
		zoomIn(SCALE_RATE);
	}

	protected void zoomOut() {
		zoomOut(SCALE_RATE);
	}

	protected void zoomIn(float rate) {
		if (getScale() >= mMaxZoom) {
			return; // Don't let the user zoom into the molecular level.
		} else if (getScale() <= mMinZoom) {
			return;
		}
		if (image == null) {
			return;
		}

		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		mSuppMatrix.postScale(rate, rate, cx, cy);
		setImageMatrix(getImageViewMatrix());
	}

	protected void zoomOut(float rate) {
		if (image == null) {
			return;
		}
		isChangeScreen = false;
		float cx = getWidth() / 2F;
		float cy = getHeight() / 2F;

		// Zoom out to at most 1x.
		Matrix tmp = new Matrix(mSuppMatrix);
		tmp.postScale(1F / rate, 1F / rate, cx, cy);

		if (getScale(tmp) < 1F) {
			mSuppMatrix.setScale(1F, 1F, cx, cy);
		} else {
			mSuppMatrix.postScale(1F / rate, 1F / rate, cx, cy);
		}
		setImageMatrix(getImageViewMatrix());
		center(true, true);
	}

	public void postTranslate(float dx, float dy) {
		mSuppMatrix.postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}

	float _dy = 0.0f;

	protected void postTranslateDur(final float dy, final float durationMs) {
		_dy = 0.0f;
		final float incrementPerMs = dy / durationMs;
		final long startTime = System.currentTimeMillis();
		mHandler.post(new Runnable() {
			public void run() {
				long now = System.currentTimeMillis();
				float currentMs = Math.min(durationMs, now - startTime);

				postTranslate(0, incrementPerMs * currentMs - _dy);
				_dy = incrementPerMs * currentMs;

				if (currentMs < durationMs) {
					mHandler.post(this);
				}
			}
		});
	}

	protected void panBy(float dx, float dy) {
		postTranslate(dx, dy);
		setImageMatrix(getImageViewMatrix());
	}

}
