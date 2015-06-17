package jp.android.yomou;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

public class CommonStringRequest extends Request<String> {

	private Map<String, String> mParams;
	private final Listener<String> mListener;
	private Priority mPriority = Priority.NORMAL;

	public CommonStringRequest(int method, String url, Listener<String> listener,
			ErrorListener errorListener) {
		super(method, url, errorListener);
		mParams = new HashMap<String,String>();
		mListener = listener;
	}
	
	public void addParam(String key, String value){
		mParams.put(key, value);
	}
	
	public void setParams( Map<String, String> params ){
		mParams = params;
	}

	@Override
	protected Map<String,String> getParams(){
		return mParams;
	}
	
	public void setPriority(final Priority priority) {
		this.mPriority = priority;
	}
	
	@Override
	public Priority getPriority() {
		return mPriority;
	}
	
	@Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String,String> params = new HashMap<String, String>();
        params.put("Content-Type","application/x-www-form-urlencoded");
        return params;
    }

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		String resStr = new String(response.data);
		if( resStr.length()<1 )
			return null;
		//CommonUtility.print(resStr);
        return Response.success(resStr, getCacheEntry());
	}

	@Override
	protected void deliverResponse(String response) {
		mListener.onResponse(response);
	}
}
