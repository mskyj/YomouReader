package jp.android.yomou;

import java.util.ArrayList;
import java.util.Date;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Spinner;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class NovelRankingActivity  extends CommonActivity{

	private ArrayList<NovelBaseData> mNovelList = new ArrayList<NovelBaseData>();
	private int mListType = 0;
	private int mType = 0;
	private int mGenre = 0;
	private int mNum = 0;
	private int mPage = 0;
	
	private CommonDBAdapter mDBAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novel_ranking);

		mListType = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_RANKING_LIST);
		mType = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_RANKING_TYPE);
		mGenre = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_RANKING_GENRE);
		mNum = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_RANKING_NUM);
		((Spinner)findViewById(R.id.ranking_list_type_select)).setSelection(mListType);
		((Spinner)findViewById(R.id.ranking_type_select)).setSelection(mType);
		((Spinner)findViewById(R.id.ranking_genre_select)).setSelection(mGenre);
		((Spinner)findViewById(R.id.ranking_num_select)).setSelection(mNum);
		
		if( mListType != 1 )
			((Spinner)findViewById(R.id.ranking_genre_select)).setEnabled(false);
		
		ShowLayout();

		((LinearLayout)findViewById(R.id.ranking_header_layout)).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				LinearLayout layout = (LinearLayout)NovelRankingActivity.this.findViewById(R.id.ranking_select_layout);
				if( layout.getVisibility() == View.GONE ){
					ShowLayout();
				}
				else{
					HideLayout();
				}
			}
		});
		((Button)findViewById(R.id.ranking_exe_button)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				int oldListType = mListType;
				int oldType = mType;
				int oldGenre = mGenre;
				int oldNum = mNum;
				
				mListType = ((Spinner)findViewById(R.id.ranking_list_type_select)).getSelectedItemPosition();
				mType = ((Spinner)findViewById(R.id.ranking_type_select)).getSelectedItemPosition();
				mGenre = ((Spinner)findViewById(R.id.ranking_genre_select)).getSelectedItemPosition();
				mNum = ((Spinner)findViewById(R.id.ranking_num_select)).getSelectedItemPosition();

				CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_RANKING_LIST, mListType);
				CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_RANKING_TYPE, mType);
				CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_RANKING_GENRE, mGenre);
				CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_RANKING_NUM, mNum);

				mPage = 0;
				
				if( oldListType == mListType && oldType == mType && oldGenre == mGenre && oldNum != mNum )
					UpdateView();
				else{
					mDBAdapter.openDB();
					mNovelList = mDBAdapter.getRanking(mListType, mType, mGenre);
					mDBAdapter.closeDB();
					
					if( mNovelList == null ){
						mNovelList = new ArrayList<NovelBaseData>();
						RequestData();
					}
					else
						UpdateView();
				}
				HideLayout();
			}
		});
		((Spinner)findViewById(R.id.ranking_list_type_select)).setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent,View view, int position,long id) {
				if( position != mListType ){
					if( mListType == 1 )
						((Spinner)findViewById(R.id.ranking_genre_select)).setEnabled(false);
					else
						((Spinner)findViewById(R.id.ranking_genre_select)).setEnabled(true);
				}
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				
			}
		});
		
		mDBAdapter = new CommonDBAdapter(getApplicationContext());
	}

	private void RequestData(){
		String listTypeStr[] = {"list","genrelist","secondlist"};
		String typeStr[] = {"daily_","weekly_","monthly_","quarter_","yearly_","total_"};
		String type = typeStr[mType];
		if( mListType == 1 )
			type += (mGenre+1);
		else
			type += "total";
		
		CommonStringRequest request = new CommonStringRequest(
				Method.GET,
				Constant.SERVER_URL+"/rank/"+listTypeStr[mListType]+"/type/"+type+"/",
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
		if( mNovelList.size() > 0 ){
			int numList[] = {10,20,50,100};
			int num = numList[mNum];
			
			ExpandableListView listview = (ExpandableListView)findViewById(R.id.ranking_list);
			NovelRankingAdapter adapter = new NovelRankingAdapter(NovelRankingActivity.this,mNovelList,mPage*num,num);
			listview.setGroupIndicator(null);
			listview.setAdapter(adapter);
			listview.setOnChildClickListener(new OnChildClickListener(){
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					if( childPosition == 1 ){
						Intent intent = new Intent(getApplicationContext(),NovelInfoActivity.class);
						intent.putExtra("data", mNovelList.get(groupPosition));
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
			mNovelList.clear();

			Document document = Jsoup.parse(res);
			Elements hElements = document.getElementsByClass("rank_h");
			Elements tElements = document.getElementsByClass("rank_table");
			for(int i=0; i<hElements.size(); ++i){
				Element hElement = hElements.get(i);
				Element tElement = tElements.get(i);
				Elements a = hElement.select("a");

				int offset = tElement.text().lastIndexOf("最終更新日：");
				String time = CommonUtility.pullString(tElement.text(), "日：", "分", offset).replaceAll("[^0-9]","");
				Date date = CommonUtility.getDate(time, "yyyyMMddHHmm");

				int type = 1;
				int end_flag = 0;
				int page_num = 1;
				String temp = tElement.getElementsByClass("attention").text();
				String status = tElement.getElementsByClass("left").text().replaceAll(temp,"").replaceAll("\r\n|[\n\r\u2028\u2029\u0085]","");
				if( status.indexOf("短") == 0 ){
					type = 2;
					end_flag = 1;
				}
				else{
					if( status.indexOf("完") == 0 )
						end_flag = 1;
					page_num = CommonUtility.getInt(CommonUtility.pullString(status, "(", ")"));
				}
				
				NovelBaseData data = new NovelBaseData();
				data.setTitle(a.get(0).text());
				data.setStory(tElement.getElementsByClass("ex").text());
				data.setType(type);
				data.setEndFlag(end_flag);
				data.setPageNum(page_num);
				data.setNcode(CommonUtility.pullString(a.get(0).attr("href"), "com/", "/"));
				data.setWriter(a.get(1).text());
				data.setUserID(Integer.valueOf(CommonUtility.pullString(a.get(1).attr("href"), "com/", "/")));
				NovelGenre genre = NovelGenre.get(a.get(2).text());
				data.setGenre(genre);
				data.setGlobalPoint(CommonUtility.getInt(temp));
				data.setLength(CommonUtility.getInt(tElement.getElementsByClass("marginleft").text()));

				data.setUpdateTime(date);

				mNovelList.add(data);
			}
			
			mDBAdapter.openDB();
			mDBAdapter.updateRanking(mListType,mType,mGenre,mNovelList);
			mDBAdapter.closeDB();

			return null;
		}
		@Override
		// 後処理　UI更新
		protected void onPostExecute(Void result) {
			UpdateView();
			DismissProgressDialog();
		}
	}

	private void ShowLayout(){
		LinearLayout layout = (LinearLayout)NovelRankingActivity.this.findViewById(R.id.ranking_select_layout);
		TranslateAnimation layoutAnim = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_SELF, 0,
				TranslateAnimation.RELATIVE_TO_SELF, 0,
				TranslateAnimation.RELATIVE_TO_SELF, -1.0f,
				TranslateAnimation.RELATIVE_TO_SELF, 0);
		layout.setVisibility(View.VISIBLE);
		layoutAnim.setZAdjustment(Animation.ZORDER_BOTTOM);
		layoutAnim.setDuration(500);
		layout.startAnimation(layoutAnim);

		ImageView plusImg = (ImageView)findViewById(R.id.ranking_plus_img);
		RotateAnimation plusAnim = new RotateAnimation(0, 45, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		plusAnim.setDuration(500);
		plusAnim.setFillAfter(true);
		plusImg.startAnimation(plusAnim);
	}

	private void HideLayout(){
		LinearLayout layout = (LinearLayout)NovelRankingActivity.this.findViewById(R.id.ranking_select_layout);
		TranslateAnimation layoutAnim = new TranslateAnimation(
				TranslateAnimation.RELATIVE_TO_SELF, 0,
				TranslateAnimation.RELATIVE_TO_SELF, 0,
				TranslateAnimation.RELATIVE_TO_SELF, 0,
				TranslateAnimation.RELATIVE_TO_SELF, -1.0f);
		layout.setVisibility(View.GONE);
		layoutAnim.setZAdjustment(Animation.ZORDER_BOTTOM);
		layoutAnim.setDuration(500);
		layout.startAnimation(layoutAnim);

		ImageView plusImg = (ImageView)findViewById(R.id.ranking_plus_img);
		RotateAnimation plusAnim = new RotateAnimation(45, 0, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
		plusAnim.setDuration(500);
		plusAnim.setFillAfter(true);
		plusImg.startAnimation(plusAnim);
	}
}
