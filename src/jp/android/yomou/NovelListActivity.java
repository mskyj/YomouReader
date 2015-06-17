package jp.android.yomou;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class NovelListActivity extends CommonActivity {

	private ArrayList<NovelBaseData> novelList = new ArrayList<NovelBaseData>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novel_list);

		((Button)findViewById(R.id.novel_list_exe_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String keyword = ((EditText)findViewById(R.id.novel_list_keyword_input)).getText().toString();
				CommonUtility.SaveString(getApplicationContext(), Constant.PRE_SEARCH_KEYWORD, keyword);
				RequestData();
			}
		});
		((ImageButton)findViewById(R.id.novel_list_menu_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivityWithAnim(new Intent(getApplicationContext(), NovelSearchActivity.class));
			}
		});
	}


	@Override
	protected void onResume(){
		super.onResume();

		RequestData();
	}

	private void RequestData(){		
		String keyword = CommonUtility.GetString(getApplicationContext(), Constant.PRE_SEARCH_KEYWORD);
		int sort = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_SORT);
		int genre = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_GENRE);
		int type = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_TYPE);
		int time_from = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_TIME_FROM);
		int time_to = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_TIME_TO);
		int char_from = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_CHAR_FROM);
		int char_to = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_CHAR_TO);
		

		String sortStr[] = {"","favnovelcnt","reviewcnt","hyoka","hyokaasc","impressioncnt","hyokacnt","hyokacntasc","weekly","lengthdesc","lengthasc","ncodedesc","old"};
		String genreStr[] = {"","1","2","3","4","5","6","7","8","9","10","11","12","13","14","15","16"};
		String typeStr[] = {"","t","ter","r","re","er"};
		
		String url = Constant.API_URL+"?out=json&order="+sortStr[sort]+"&genre="+genreStr[genre]+"&type="+typeStr[type];
		try {
			url += "&word="+URLEncoder.encode(keyword,"UTF-8");
		} catch (UnsupportedEncodingException e) {}
		if( char_from > 0 )
			url += "&minlen="+char_from;
		if( char_to > 0 )
			url += "&maxlen="+char_to;
		if( time_from > 0 )
			url += "&mintime="+time_from;
		if( time_to > 0 )
			url += "&maxtime="+time_to;
		
		CommonStringRequest request = new CommonStringRequest(
				Method.GET,
				url,
				new Response.Listener<String>(){
					@Override
					public void onResponse(String response) {
						GetData(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DismissProgressDialog();
						ShowAlertDialog("通信エラー","通信がOFFになっていないかチェックしてください。");
					}
				}
				);
		Request(request);
		ShowProgressDialog();
		
		ArrayList<String> words = new ArrayList<String>();
		if( keyword.length() > 0 ) words.add(keyword);
		if( sort > 0 ) words.add(getResources().getStringArray(R.array.sort_list)[sort]);
		if( genre > 0 ) words.add(getResources().getStringArray(R.array.genre_list)[genre]);
		if( type > 0 ) words.add(getResources().getStringArray(R.array.type_list)[type]);
		if( char_from > 0 && char_to > 0 ) words.add(char_from+"～"+char_to+"文字");
		else if( char_from > 0 ) words.add(char_from+"文字以上");
		else if( char_to > 0 ) words.add(char_to+"文字以下");
		if( time_from > 0 && time_to > 0 ) words.add(time_from+"～"+time_to+"分");
		else if( time_from > 0 ) words.add(time_from+"分以上");
		else if( time_to > 0 ) words.add(time_to+"分以下");
		StringBuilder builder = new StringBuilder();
		for( String word : words ){
			builder.append(word).append(", ");
		}
		String search_txt = "検索結果";
		if( builder.length() > 2 )
			search_txt += "：" + builder.substring(0, builder.length() - 2);
		
		((EditText)findViewById(R.id.novel_list_keyword_input)).setText(keyword);
		((TextView)findViewById(R.id.novel_list_search_txt)).setText(search_txt);
	}

	private void GetData( String res ){
		if( res!=null ){
			// 処理が重いため、別タスクで
			(new GetDataTask(res)).execute();
		}
		else{
			DismissProgressDialog();
			ShowAlertDialog("データ取得エラー", "データを取得できませんでした。");
		}
	}
	
	private void UpdateView(){
		if( novelList.size() > 0 ){
			ExpandableListView listview = (ExpandableListView)findViewById(R.id.top_list);
			//NovelInfoAdapter adapter = new NovelInfoAdapter(getApplicationContext(), 0, novelList);
			NovelBaseListAdapter adapter = new NovelBaseListAdapter(NovelListActivity.this,novelList);
			listview.setGroupIndicator(null);
			listview.setAdapter(adapter);
			listview.setOnChildClickListener(new OnChildClickListener(){
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					if( childPosition == 1 ){
						Intent intent = new Intent(getApplicationContext(),NovelInfoActivity.class);
						intent.putExtra("data", novelList.get(groupPosition));
						startActivity(intent);
					}
					return false;
				}
			});
		}
	}
	
	class GetDataTask extends AsyncTask<Void, Void, Void>{
		String res;
		public GetDataTask( String r ){
			res = r;
		}
		@Override
		// メイン処理
		protected Void doInBackground(Void... params) {
			novelList.clear();
			
			try {
				JSONArray jArr = new JSONArray(res);
				int num = jArr.length();
				for( int i=1; i<num; ++i ){
					JSONObject jObj = jArr.getJSONObject(i);
					
					NovelBaseData data = new NovelBaseData();
					data.setTitle(jObj.getString("title"));
					data.setNcode(jObj.getString("ncode").toLowerCase(Locale.JAPANESE));
					data.setUserID(jObj.getInt("userid"));
					data.setWriter(jObj.getString("writer"));
					data.setStory(jObj.getString("story"));
					data.setGenre(NovelGenre.get(jObj.getInt("genre")));
					data.setType(jObj.getInt("novel_type"));
					data.setEndFlag(1-jObj.getInt("end"));
					data.setPageNum(jObj.getInt("general_all_no"));
					data.setLength(jObj.getInt("length"));
					data.setGlobalPoint(jObj.getInt("global_point"));
					data.setUpdateTime(CommonUtility.getDate(jObj.getString("novelupdated_at"), Constant.DATETIME_FORMAT));
					
					data.setFirstUpTime(CommonUtility.getDate(jObj.getString("general_firstup"), Constant.DATETIME_FORMAT));
					data.setLastUpTime(CommonUtility.getDate(jObj.getString("general_lastup"), Constant.DATETIME_FORMAT));
					data.setStopFlag(jObj.getInt("isstop"));
					data.setBookmarkNum(jObj.getInt("fav_novel_cnt"));
					data.setReviewNum(jObj.getInt("review_cnt"));
					data.setRatePoint(jObj.getInt("all_point"));
					data.setRateNum(jObj.getInt("all_hyoka_cnt"));
					data.setImageNum(jObj.getInt("sasie_cnt"));
					data.setKaiwaRate(jObj.getInt("kaiwaritu"));
					
					novelList.add(data);
				}
				
			} catch (JSONException e) {
			}
			
			return null;
		}
		@Override
		// 後処理　UI更新
	    protected void onPostExecute(Void result) {
			UpdateView();
			DismissProgressDialog();
		}
	}
}
