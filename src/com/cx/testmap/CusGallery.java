/**
 *    FILE: CusGallery.java
 *  AUTHOR: CX
 *    DATE: 2012-7-9
 *
 *   Copyright(c) 2011 Mobitide Android Team. All Rights Reserved.
 */
package com.cx.testmap;

/**
 * @author CX
 *
 */
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;
import android.widget.SpinnerAdapter;

public class CusGallery extends Gallery {
	private GestureDetector gestureScanner;
	private CusImageView imageView;
	private final int MOVE_MIN=80;
	OnPageEdgeListener mOnPageEdgeListener=null;
	// private String tag = "cx";
	private boolean isMove = false;
	public CusGallery(Context context) {
		super(context);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AbsSpinner#setAdapter(android.widget.SpinnerAdapter)
	 */
	@Override
	public void setAdapter(SpinnerAdapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);
	}

	public CusGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public CusGallery(Context context, AttributeSet attrs) {
		super(context, attrs);

		gestureScanner = new GestureDetector(new MySimpleGesture());
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				View view = CusGallery.this.getSelectedView();
				if (view instanceof CusImageView) {
					imageView = (CusImageView) view;
					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = imageView.getScale();
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
								imageView.zoomTo(originalScale * scale, x + event.getX(1), y + event.getY(1));

							}
						} else {
							isMove = true;
						}
					}
				}
				return false;
			}

		});
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		View view = CusGallery.this.getSelectedView();
		if (isMove) {
			if (view instanceof CusImageView) {
				imageView = (CusImageView) view;

				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				// 图片实时的上下左右坐标
				float left, right;
				// 图片的实时宽，高
				float width, height;
				width = imageView.getScale() * imageView.getImageWidth();
				height = imageView.getScale() * imageView.getImageHeight();
				// 一下逻辑为移动图片和滑动gallery换屏的逻辑。如果没对整个框架了解的非常清晰，改动以下的代码前请三思！！！！！！

				if ((int) height <= GlobalConstants.screenHeight)// 如果图片当前大小<屏幕大小，直接处理滑屏事件
				{
					super.onScroll(e1, e2, distanceX, distanceY);

				} else {

					left = v[Matrix.MTRANS_X];
					right = left + width;
					Rect r = new Rect();
					imageView.getGlobalVisibleRect(r);
					if (distanceX > 0) {
						if (r.left > 0) {
							super.onScroll(e1, e2, distanceX, distanceY);
						} else if (right < GlobalConstants.screenWidth) {
							super.onScroll(e1, e2, distanceX, distanceY);
						} else {
							imageView.postTranslate(-distanceX, -distanceY);
						}
					} else if (distanceX < 0) {
						if (r.right < GlobalConstants.screenWidth) {
							super.onScroll(e1, e2, distanceX, distanceY);
						} else if (left > 0) {
							super.onScroll(e1, e2, distanceX, distanceY);
						} else {
							imageView.postTranslate(-distanceX, -distanceY);
						}
					}
				}

			} else {
				super.onScroll(e1, e2, distanceX, distanceY);
			}

		}

		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if(mOnPageEdgeListener!=null){
			if(getSelectedItemPosition()==0){
				if((e2.getX()-e1.getX())>MOVE_MIN){
					mOnPageEdgeListener.onPageChange(-1);
					return true;
				}
			}else if(getSelectedItemPosition()==(getCount()-1)){
				if((e1.getX()-e2.getX())>MOVE_MIN){
					mOnPageEdgeListener.onPageChange(1);
					return true;
				}
			}
			
			
		}
		
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		gestureScanner.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			// 判断上下边界是否越界
			View view = CusGallery.this.getSelectedView();
			if (view instanceof CusImageView) {
				imageView = (CusImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale() * imageView.getImageHeight();
				// //Log.i(tag, "img width: " + width + "  img height : " +
				// height);
				// //Log.i(tag, "main width "+GlobalConstants.screenWidth+
				// " main height "+GlobalConstants.screenHeight);
				if ((int) width <= GlobalConstants.screenWidth && (int) height <= GlobalConstants.screenHeight)// 如果图片当前大小<屏幕大小，判断边界
				{
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				// //Log.i(tag, "top : " + top + " bottom:" + bottom);
				if ((int) height >= GlobalConstants.screenHeight) {
					if (top > 0) {
						// //Log.i(tag, "move top : " + (-top));
						imageView.postTranslateDur(-top, 200f);
					}
					if (bottom < GlobalConstants.screenHeight) {
						// //Log.i(tag, "move bottom : " +
						// (GlobalConstants.screenHeight - bottom));
						imageView.postTranslateDur(GlobalConstants.screenHeight - bottom, 200f);
					}
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
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			View view = CusGallery.this.getSelectedView();
			if (view instanceof CusImageView && e.getPointerCount() == 1) {
				// if(DataCache.handle_photo_position!=null)
				// {
				// DataCache.handle_photo_position.sendEmptyMessage(0x22);
				// //
				// imageView.zoomTo(imageView.getScale(),GlobalConstants.screenWidth,
				// GlobalConstants.screenHeight);
				// }
			}
			return true;
		}
		
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
	
	
	public void setOnPageEdgeListener(OnPageEdgeListener mOnPageEdgeListener){
		this.mOnPageEdgeListener=mOnPageEdgeListener;
	}

	public interface OnPageEdgeListener {
		/**
		 * -1 means after
		 *  1 means before
		 * @param position
		 */
		public void onPageChange(int position);
	}

}
