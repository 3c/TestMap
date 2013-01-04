package com.cx.testmap;

import android.content.Context;
import android.os.Environment;
import android.util.DisplayMetrics;

public class GlobalConstants {

	public static int screenWidth = 480;
	public static int screenHeight = 854;
	public static boolean isLoad;

	public static void init(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}
}
