package com.lh.exin.toolbar;

import android.content.*;
import android.util.*;
import u.aly.*;

public class DisplayParams
 {  
	/** 屏幕宽度——px */  
	public int screenWidth;  
	/** 屏幕高度——px */  
	public int screenHeight;  
	/** 屏幕密度——dpi */  
	public int densityDpi;  
	/** 缩放系数——densityDpi/160 */  
	public float scale;  
	/** 文字缩放系数 */  
	public float fontScale;  
	/** 屏幕朝向 */  
	public int screenOrientation;  
	/** 表示屏幕朝向垂直 */  
	public final static int SCREEN_ORIENTATION_VERTICAL = 1;  
	/** 表示屏幕朝向水平 */  
	public final static int SCREEN_ORIENTATION_HORIZONTAL = 2;  

	private static DisplayParams singleInstance;  

	/** 
	 * 私有构造方法 
	 *  
	 * @param context 
	 */  
	private DisplayParams(Context context) {  
		DisplayMetrics dm = context.getResources().getDisplayMetrics();  
		screenWidth = dm.widthPixels;  
		screenHeight = dm.heightPixels;  
		densityDpi = dm.densityDpi;  
		scale = dm.density;  
		fontScale = dm.scaledDensity;  
		screenOrientation = screenHeight > screenWidth ? SCREEN_ORIENTATION_VERTICAL  
			: SCREEN_ORIENTATION_HORIZONTAL;  
	}  

	/** 
	 * 获取实例 
	 *  
	 * @param context 
	 * @return 
	 */  
	public static DisplayParams getInstance(Context context) {  
		if (singleInstance == null) {  
			singleInstance = new DisplayParams(context);  
		}  
		return singleInstance;  
	}  

	/** 
	 * 获取新的实例 
	 *  
	 * @param context 
	 * @return 
	 */  
	public static DisplayParams getNewInstance(Context context) {  
		if (singleInstance != null) {  
			singleInstance = null;  
		}  
		return getInstance(context);  
	}  
}  
