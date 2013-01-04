package com.cx.testmap;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class MarkItemOverlay extends Overlay {

	// 保存要在界面上显示的数据Bundle对象
	private Bundle data;

	// 当前标记在地图上显示的经纬度对象
	private GeoPoint geoPoint;

	// 标记图层开始X,Y轴坐标和结束X,Y轴坐标
	private int start_x = 0, start_y = 0, end_x = 0, end_y = 0;

	// 标记图层单击监听器
	private OnMarkItemClickListener onClickListener;

	// 标记图层高度
	final int height = 50;
	// 底部箭头X轴上的偏移
	final int x_offset = 10;
	// 底部箭头Y轴上的偏移
	final int y_offset = 20;
	// 左边X轴上的偏移
	final int left_x_offset = 30;

	public MarkItemOverlay(GeoPoint point, Bundle bundle, OnMarkItemClickListener onMarkItemClickListener) {
		this.geoPoint = point;
		this.data = bundle;
		this.onClickListener = onMarkItemClickListener;
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean isShape) {
		super.draw(canvas, mapView, isShape);

		// 标识地图移动的时候是否重复画，在地图显示界别小于某个级别的时候是否需要在绘画
		if (!isShape && mapView.getZoomLevel() > 13) {
			String locationName = data.getString("locationName");
			// 将经纬度对象通过墨卡托投影转化为屏幕坐标
			Point point = new Point();
			Projection projection = mapView.getProjection();
			projection.toPixels(geoPoint, point);

			// 定义在画板上绘画文字的画笔对象
			Paint paint = new Paint();
			paint.setColor(Color.WHITE);
			paint.setTextSize(mapView.getZoomLevel() + 2);
			paint.setStrokeWidth(1);
			paint.setStyle(Style.FILL_AND_STROKE);
			paint.setTypeface(Typeface.MONOSPACE);

			// 文字背景层画笔对象
			Paint text_layout_paint = new Paint();
			text_layout_paint.setColor(0xff4a9ade);

			// 当前经纬度在地图上的墨卡托投影x,y坐标
			final int x = point.x;
			final int y = point.y;

			// 尖角底部线条右边第一个点的x,y坐标值
			int bottom_right_x = x + x_offset;
			// 右下角的y轴坐标
			int bottom_right_y = y - y_offset;

			// 文字显示所需要占用的宽度
			int width = locationName.length() * (mapView.getZoomLevel() + 2) + left_x_offset;

			// 右下角的x轴坐标
			int right_point_x = bottom_right_x + width - 2 * x_offset - left_x_offset;
			// 右上角的Y轴坐标
			int right_point_top_y = bottom_right_y - height;

			// 左上角的X轴坐标
			int left_point_x = right_point_x - width;
			// 箭头左上角X轴的坐标
			int bottom_left_x = left_point_x + left_x_offset;

			start_x = left_point_x;
			start_y = right_point_top_y;
			end_x = right_point_x;
			end_y = bottom_right_y;

			Path path = new Path();
			// 移动到开始绘画的原点坐标
			path.moveTo(x, y);
			// 尖角点到底部右下缺口的直线
			path.lineTo(bottom_right_x, bottom_right_y);
			// 底部右边的直线
			path.lineTo(right_point_x, bottom_right_y);
			// 右边框的直线
			path.lineTo(right_point_x, right_point_top_y);
			// 上边框的直线
			path.lineTo(left_point_x, right_point_top_y);
			// 左边框的直线
			path.lineTo(left_point_x, bottom_right_y);
			// 底部左半边的直线
			path.lineTo(bottom_left_x, bottom_right_y);
			// 尖角点到底部左下缺口的直线
			path.lineTo(x, y);
			// 将多边形画到画布上
			canvas.drawPath(path, text_layout_paint);

			// canvas.drawTextOnPath(locationName, path, 10, 0, paint);

			// 绘画需要显示的文本信息
			canvas.drawText(locationName, left_point_x + 10, bottom_right_y - 15, paint);
		}
	}

	@Override
	public boolean onTap(GeoPoint p_geopoint, MapView mapView) {
		Point point = new Point();
		Projection projection = mapView.getProjection();
		projection.toPixels(p_geopoint, point);

		int x = point.x;
		int y = point.y;

		// 判断当前点击的坐标位置是否在标记图层范围内，如果在则通知外部进行相应操作。
		if ((x >= start_x && x <= end_x) && (y >= start_y && y <= end_y)) {
			// System.out.println("onTap");
			if (onClickListener != null) {
				onClickListener.OnItemClick(data);
			}
		}

		return super.onTap(p_geopoint, mapView);
	}

}
