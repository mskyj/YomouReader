package jp.android.yomou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;


public abstract class CommonActivity extends Activity{

	CommonAlertDialog alertDialog = null;
	CommonProgressDialog mProgressDialog = null;
	boolean mBackFlag = false;
	protected RequestQueue mQueue = null;
	protected SlidingMenu mMenu = null;
	protected GestureDetector mDetector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		mDetector = new GestureDetector(getApplicationContext(),onGestureListener);
	}

	@Override
	protected void onStart(){
		super.onStart();

		mMenu = CommonUtility.setSideMenu(this);
	}

	@Override
	protected void onResume(){
		super.onResume();
		if( mQueue == null ){
			mQueue = Volley.newRequestQueue(this);
		}
		else
			mQueue.start();

		if( mMenu.isMenuShowing() )
			mMenu.toggle(false);
	}

	@Override
	protected void onRestart(){
		super.onRestart();
		if( mQueue == null ){
			mQueue = Volley.newRequestQueue(this);
		}
		else
			mQueue.start();
	}

	@Override
	protected void onPause(){
		super.onPause();
		mBackFlag = false;
		if( mQueue != null ){
			mQueue.stop();
		}
		DismissAlertDialog();
		DismissProgressDialog();
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		if( mQueue != null )
			mQueue.cancelAll(this);
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);
	}

	public void finishWithAnim(){
		super.finish();
		overridePendingTransition(R.anim.activity_close_enter, R.anim.activity_close_exit);
	}

	@Override
	public void startActivity(Intent intent){
		startActivityForResult(intent,0);
		overridePendingTransition(0, 0);
	}

	public void startActivityWithAnim(Intent intent){
		startActivityForResult(intent,0);
		overridePendingTransition(R.anim.activity_open_enter, R.anim.activity_open_exit);
	}

	@SuppressWarnings("unchecked")
	void Request( CommonJsonRequest request ){
		if( mQueue != null ){
			mQueue.add(request);
		}
	}

	void Request( CommonStringRequest request ){
		if( mQueue != null ){
			mQueue.add(request);
		}
	}

	void ShowAlertDialog( String title, String message ){
		alertDialog = new CommonAlertDialog(this);
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setPositiveButton(new View.OnClickListener() {
			@Override
			public void onClick(View view) { 
				alertDialog.dismiss();
				alertDialog = null;
			}
		});
		alertDialog.show();
	}

	void ShowFinishDialog( String title, String message ){
		alertDialog = new CommonAlertDialog(this);
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		alertDialog.setPositiveButton(new View.OnClickListener() {
			@Override
			public void onClick(View view) { 
				alertDialog.dismiss();
				alertDialog = null;
				finish();
			}
		});
		alertDialog.show();
	}

	void DismissAlertDialog(){
		if( alertDialog != null ){
			if( alertDialog.isShowing() )
				alertDialog.dismiss();
			alertDialog = null;
		}
	}

	// ダイアログ表示を行う
	void ShowProgressDialog(){
		if( mProgressDialog == null ){
			// プログレスバーの設定
			mProgressDialog = new CommonProgressDialog(this);
			// ProgressDialog のキャンセルが可能かどうか  
			//mProgressDialog.setCancelable(false);
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();
		}
	}

	void DismissProgressDialog(){
		if( mProgressDialog != null ){
			if( mProgressDialog.isShowing() )
				mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}

	@Override
	public void onBackPressed(){
		Intent returnIntent = new Intent();
		setResult(RESULT_CANCELED, returnIntent); 
		super.onBackPressed();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_CANCELED){
			mBackFlag = true;
		}
	}

	@Override
	public boolean onKeyDown(int keycode, KeyEvent e) {
		switch(keycode) {
		case KeyEvent.KEYCODE_MENU:
			mMenu.toggle();
			return true;
		}

		return super.onKeyDown(keycode, e);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if( mDetector.onTouchEvent(event) )
			return true;
		return super.dispatchTouchEvent(event);
	}

	// 複雑なタッチイベントを取得
	private final SimpleOnGestureListener onGestureListener = new SimpleOnGestureListener() {
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			// TODO Auto-generated method stub
			//CommonUtility.print("Gesture:"+"onDoubleTap");
			/*
			if( !mMenu.isMenuShowing() )
				mMenu.toggle();
			return true;
			 */
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e){
			//CommonUtility.print("Gesture:"+"onDoubleTapEvent");
			return super.onDoubleTapEvent(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			//CommonUtility.print("Gesture:"+"onDown");
			return super.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			//CommonUtility.print("Gesture:"+"onFling");
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			//CommonUtility.print("Gesture:"+"onLongPress");
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			//CommonUtility.print("Gesture:"+"onScroll");
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public void onShowPress(MotionEvent e) {
			//CommonUtility.print("Gesture:"+"onShowPress");
			super.onShowPress(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			//CommonUtility.print("Gesture:"+"onSingleTapConfirmed");
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			//CommonUtility.print("Gesture:"+"onSingleTapUp");
			return super.onSingleTapUp(e);
		}
	};

	/*
	// 非同期でプログレスダイアログを表示
	private class ProgressTask extends AsyncTask<Void, Void, Void>{
		Activity mActivity = null;
		boolean mIsShowing = true;
		CommonProgressDialog mProgressDialog = null;

		protected ProgressTask( Activity a ){
			mActivity = a;
		}
		public boolean isShowing(){
			return mIsShowing;
		}
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			mIsShowing = true;
			showDialog();
		}

		@Override
		protected Void doInBackground(Void... params) {
			try {
				// 60秒まつ
				Thread.sleep(60000);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
			return null;
		}

		@Override  
		protected void onPostExecute(Void result) {  
			super.onPostExecute(result);  
			dismissDialog();  
			mIsShowing = false;  
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
			dismissDialog();
			mIsShowing = false;
		}

		public void showDialog() {
			// プログレスバーの設定
			mProgressDialog = new CommonProgressDialog(mActivity);
			// ProgressDialog のキャンセルが可能かどうか  
			//mProgressDialog.setCancelable(false);
			mProgressDialog.setCancelable(true);
			mProgressDialog.show();
		}

		public void dismissDialog() {
			if( mProgressDialog.isShowing() )
				mProgressDialog.dismiss();
			mProgressDialog = null;
		}
	}
	 */
}
