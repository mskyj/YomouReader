package jp.android.yomou;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

@SuppressWarnings("rawtypes")
public class CommonJsonRequest extends Request {

	private Map<String, String> mParams;
	private final Listener<JSONObject> mListener;
	private Priority mPriority = Priority.NORMAL;

	public CommonJsonRequest(int method, String url, Listener<JSONObject> listener,
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
	protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
		String resStr = new String(response.data);
		if( resStr.length()<1 )
			return null;
		CommonUtility.print(resStr);
		JSONObject resultJson;
		try {
            resultJson = new JSONObject(resStr);
        } catch (Exception e) {
            return null;
        }
        return Response.success(resultJson, getCacheEntry());
	}

	@Override
	protected void deliverResponse(Object response) {
		mListener.onResponse((JSONObject)response);
	}
}
