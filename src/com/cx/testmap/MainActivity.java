package com.cx.testmap;

import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout.LayoutParams;

import com.cx.testbean.CommentBean;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MainActivity extends MapActivity implements OnClickListener {

	private String testUrl = "http://oularapp.mobitide.com/index.php?c=mhaccount&a=mhshowComment&ua=android&deviceid=0001&appuid=4&version=0&appid=10226&appModuleId=12226&id=6&idtype=mhshopid&page=1";

	private ArrayList<ParseModule> listModules = new ArrayList<ParseModule>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		MapView mapView = new MapView(this, "godkey");
		Panel view = new Panel(this);
		MapController controller = mapView.getController();
		GeoPoint p = new GeoPoint(0, 0);
		MarkItemOverlay overlay = new MarkItemOverlay(p, null, new OnMarkItemClickListener() {

			@Override
			public void OnItemClick(Bundle bundle) {
				// TODO Auto-generated method stub

			}
		});

		setContentView(view);
		// listModules=parseModuleXml();
		// for(ParseModule module:listModules){
		// System.out.println(module);
		// }

		// CusImageView iv=(CusImageView) findViewById(R.id.iv_test);
		// InputStream is=getResources().openRawResource(R.drawable.test);
		// BitmapDrawable bmpDraw=new BitmapDrawable(is);
		// Bitmap bmp=bmpDraw.getBitmap();
		// iv.setImageBitmap(bmp);

	}

	class Panel extends View {
		public Panel(Context context) {
			super(context);
		}

		public void onDraw(Canvas canvas) {
			Paint p = new Paint();
			// System.out.println(canvas.getHeight() + " " + canvas.getWidth());
			// canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),
			// R.drawable.test), 0, 0, p);
			// p.setColor(Color.RED);
			// p.setStyle(Paint.Style.STROKE);// 设置填满
			// canvas.drawText("画矩形：", 10, 80, p);
			// for (int i = 0; i < 10; i++) {
			// RectF rectF = new RectF();
			// rectF.set(460 + 10 * i, 60 + 10 * i, 80 + 10 * i, 80 + 10 * i);
			// canvas.drawRect(rectF, p);// 正方形
			// }

			Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.test);
			Matrix matrix = new Matrix();
			matrix.postScale(0.4f, 0.4f);
			Bitmap dstbmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
			canvas.drawColor(Color.BLACK);
			canvas.drawBitmap(dstbmp, 30,30, null);
			p.setColor(Color.RED);
			p.setStyle(Paint.Style.STROKE);// 设置填满
			canvas.drawText("画矩形：", 10, 80, p);
			for (int i = 0; i < 10; i++) {
				RectF rectF = new RectF();
				rectF.set(60 + 10 * i, 60 + 10 * i, 80 + 10 * i, 80 + 10 * i);
				canvas.drawRect(rectF, p);// 正方形
			}
			
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// TODO Auto-generated method stub
			System.out.println(event.getX() + " " + event.getY());
			return super.onTouchEvent(event);
		}
	}

	@Override
	public void onClick(View v) {
		System.out.println(v.getLeft() + " h: " + v.getHeight());
	}

	public RectF getRectF() {
		RectF rectF = new RectF();
		rectF.set(60, 60, 80, 80);
		return rectF;
	}

	private void draw() {
		Paint p = new Paint();
		p.setColor(Color.RED);
	}

	public void test(View v) {

		ArrayList<CommentBean> listComment = new ArrayList<CommentBean>();
		for (CommentBean bean : listComment) {
			System.out.println(bean);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * @return setting data from xml
	 */
	private ArrayList<ParseModule> parseModuleXml() {
		ParseModuleXml contentHandler = new ParseModuleXml();
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(getResources().getAssets().open("parse_configuration.xml"), contentHandler);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return contentHandler.getParseResult();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
