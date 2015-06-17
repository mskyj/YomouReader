package jp.android.yomou;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class CommonFragment extends Fragment {

	CommonAlertDialog alertDialog = null;
	CommonProgressDialog progressDialog = null;
	protected RequestQueue mQueue = null;
	View rootView = null;
	CommonFragmentCallback mCallback = null;
	
	@Override  
    public void onAttach(Activity activity) {  
        super.onAttach(activity);
        
        if( mQueue == null )
			mQueue = Volley.newRequestQueue(activity);
		else
			mQueue.start();
        
        if(activity instanceof CommonFragmentCallbackProvider == false){
            throw new ClassCastException(activity.getLocalClassName() + " must implements callback");
        }
        CommonFragmentCallbackProvider provider = (CommonFragmentCallbackProvider)activity;
        mCallback = provider.getCommonCallback();
	}
	
	@Override  
    public void onResume() {  
        super.onResume();
        
        if( mQueue == null )
			mQueue = Volley.newRequestQueue(getActivity());
		else
			mQueue.start();
	}

	@Override
	public void onPause(){
		super.onPause();
		if( mQueue != null ){
			mQueue.stop();
		}
		DismissAlertDialog();
		DismissProgressDialog();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if( mQueue != null )
			mQueue.cancelAll(this);
	}
	
	@Override
	public void startActivity(Intent intent){
		super.startActivity(intent);
		getActivity().overridePendingTransition(0, 0);
	}
	
	@SuppressWarnings("unchecked")
	void Request( CommonJsonRequest request ){
		if( mQueue != null )
			mQueue.add(request);
	}
	
	void UpdateView(){
		
	}
	
	public static interface CommonFragmentCallbackProvider {
		 
        public CommonFragmentCallback getCommonCallback();
 
    }
 
    public static interface CommonFragmentCallback {
 
        public void callback(CommonFragment commonFragment);
 
    }

	void ShowAlertDialog( String title, String message ){
		alertDialog = new CommonAlertDialog(getActivity());
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
		progressDialog = new CommonProgressDialog(getActivity());
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
}
