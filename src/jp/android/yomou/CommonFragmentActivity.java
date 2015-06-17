package jp.android.yomou;

import jp.android.yomou.CommonFragment.CommonFragmentCallback;
import jp.android.yomou.CommonFragment.CommonFragmentCallbackProvider;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public class CommonFragmentActivity extends FragmentActivity implements CommonFragmentCallbackProvider {
	CommonAlertDialog alertDialog = null;
	CommonProgressDialog progressDialog = null;
	protected RequestQueue mQueue = null;

	@Override
	protected void onResume(){
		super.onResume();
		if( mQueue == null )
			mQueue = Volley.newRequestQueue(this);
		else
			mQueue.start();
	}

	@Override
	protected void onRestart(){
		super.onRestart();
		if( mQueue == null )
			mQueue = Volley.newRequestQueue(this);
		else
			mQueue.start();
	}

	@Override
	protected void onPause(){
		super.onPause();
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
	
	@Override
	public void startActivity(Intent intent){
		super.startActivity(intent);
		overridePendingTransition(0, 0);
	}
	
	@SuppressWarnings("unchecked")
	void Request( CommonJsonRequest request ){
		if( mQueue != null )
			mQueue.add(request);
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

	void DismissAlertDialog(){
		if( alertDialog != null ){
			if( alertDialog.isShowing() )
				alertDialog.dismiss();
			alertDialog = null;
		}
	}

	// ダイアログ表示を行う
	void ShowProgressDialog(){
		// プログレスバーの設定
		progressDialog = new CommonProgressDialog(this);
		// ProgressDialog のキャンセルが可能かどうか  
		progressDialog.setCancelable(false);
		progressDialog.show();
	}

	void DismissProgressDialog(){
		if( progressDialog != null ){
			if( progressDialog.isShowing() )
				progressDialog.dismiss();
			progressDialog = null;
		}
	}

	@Override
	public CommonFragmentCallback getCommonCallback() {
		return new CommonFragmentCallback(){
            @Override
            public void callback(CommonFragment commonFragmnet){
            }
        };
	}
}
