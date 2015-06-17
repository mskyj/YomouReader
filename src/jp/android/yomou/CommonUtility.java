package jp.android.yomou;

import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class CommonUtility {
	
	public static void print( String msg ){
		Log.d("Yomou",msg);
	}
	
	public static float getDisplayScale(Context context) {
	    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    DisplayMetrics dm = new DisplayMetrics();
	    wm.getDefaultDisplay().getMetrics(dm);
	    return dm.scaledDensity;
	}
	

	// プリファレンスからフラグを取得
	public static Boolean GetFlag( Context context, String key ){
		SharedPreferences pref = context.getSharedPreferences(Constant.PRE_NAME, Context.MODE_PRIVATE);
		return pref.getBoolean(key, false);
	}
	
	// プリファレンスにフラグを保存
	public static void SaveFlag( Context context, String key, boolean value ){
		SharedPreferences pref = context.getSharedPreferences(Constant.PRE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putBoolean( key, value );
		editor.commit();
	}
	
	// プリファレンスから文字列を取得
	public static String GetString( Context context, String key ){
		SharedPreferences pref = context.getSharedPreferences(Constant.PRE_NAME, Context.MODE_PRIVATE);
		return pref.getString(key, "");
	}
	
	// プリファレンスに文字列を保存
	public static void SaveString( Context context, String key, String value ){
		SharedPreferences pref = context.getSharedPreferences(Constant.PRE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString( key, value );
		editor.commit();
	}
	
	// プリファレンスから数値を取得
	public static int GetInt( Context context, String key ){
		SharedPreferences pref = context.getSharedPreferences(Constant.PRE_NAME, Context.MODE_PRIVATE);
		return pref.getInt(key, 0);
	}
	
	// プリファレンスに数値を保存
	public static void SaveInt( Context context, String key, int value ){
		SharedPreferences pref = context.getSharedPreferences(Constant.PRE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putInt( key, value );
		editor.commit();
	}
	
	public static String convertInputStreamReaderToString(InputStreamReader isr){
        StringBuilder builder = new StringBuilder();
        char[] buf = new char[1024];
        int numRead;
        try {
			while (0 <= (numRead = isr.read(buf))) {
			    builder.append(buf, 0, numRead);
			}
			return builder.toString();
		} catch (IOException e) {
			return "";
		}
    }
	
	public static int getInt( String str ){
		String s = str.replaceAll("[^0-9]","");
		if( s.length() <= 0 )
			return 0;
		else
			return Integer.valueOf(s);
	}
	
	public static String pullString( String str, String dem1, String dem2 ){
		int start = str.indexOf(dem1);
		if( start != -1 ){
			start += dem1.length();
			int end = str.indexOf(dem2,start);
			if( end != -1 )
				return str.substring(start, end);
		}
		return "";
	}
	
	public static String pullString( String str, String dem1, String dem2, int offset ){
		int start = str.indexOf(dem1,offset);
		if( start != -1 ){
			start += dem1.length();
			int end = str.indexOf(dem2,start);
			if( end != -1 )
				return str.substring(start, end);
		}
		return "";
	}
	
	public static Date getDate( String dateStr, String format ){
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.JAPANESE);
		Date date = null;
		try {
			date = sdf.parse(dateStr);
		} catch (ParseException e) {}
		return date;
	}
	
	public static String getString( Date date, String format ){
		SimpleDateFormat sdf = new SimpleDateFormat(format,Locale.JAPANESE);
		return sdf.format(date);
	}
	
	public static SlidingMenu setSideMenu(final Activity activity){
		SlidingMenu menu = new SlidingMenu(activity);
		menu.setMode(SlidingMenu.LEFT);
		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		menu.setShadowWidthRes(R.dimen.shadow_width);
		menu.setShadowDrawable(R.layout.shadow);
		//menu.setBehindOffsetRes(R.dimen.sidemenu_offset);	// 残り幅指定
		menu.setBehindWidthRes(R.dimen.sidemenu_width);		// 横幅指定
		menu.setFadeDegree(0.35f);
		menu.attachToActivity(activity, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.sidemenu);
		
		String activity_name = activity.getLocalClassName();
		if( !activity_name.equals("HomeActivity") ){
			((TextView)menu.findViewById(R.id.sidemenu_home_txt)).setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					activity.startActivity(new Intent(activity.getApplicationContext(), HomeActivity.class));
				}
			});
		}
		if( !activity_name.equals("NovelListActivity") ){
			((TextView)menu.findViewById(R.id.sidemenu_search_txt)).setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					activity.startActivity(new Intent(activity.getApplicationContext(), NovelListActivity.class));
				}
			});
		}
		if( !activity_name.equals("NovelRankingActivity") ){
			((TextView)menu.findViewById(R.id.sidemenu_ranking_txt)).setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					activity.startActivity(new Intent(activity.getApplicationContext(), NovelRankingActivity.class));
				}
			});
		}
		if( !activity_name.equals("MyFavoriteActivity") ){
			((TextView)menu.findViewById(R.id.sidemenu_favorite_txt)).setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					activity.startActivity(new Intent(activity.getApplicationContext(), MyFavoriteActivity.class));
				}
			});
		}
		if( !activity_name.equals("MyBookmarkActivity") ){
			((TextView)menu.findViewById(R.id.sidemenu_bookmark_txt)).setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					activity.startActivity(new Intent(activity.getApplicationContext(), MyBookmarkActivity.class));
				}
			});
		}
		if( !activity_name.equals("MyHistoryActivity") ){
			((TextView)menu.findViewById(R.id.sidemenu_history_txt)).setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					activity.startActivity(new Intent(activity.getApplicationContext(), MyHistoryActivity.class));
				}
			});
		}
		if( !activity_name.equals("MySettingActivity") ){
			((TextView)menu.findViewById(R.id.sidemenu_setting_txt)).setOnClickListener(new OnClickListener() {	
				@Override
				public void onClick(View v) {
					activity.startActivity(new Intent(activity.getApplicationContext(), MySettingActivity.class));
				}
			});
		}
		return menu;
	}
}
