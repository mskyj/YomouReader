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
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class NovelInfoActivity extends CommonActivity {

	private NovelBaseData mBaseData = null;
	private ArrayList<String> mTitleList = new ArrayList<String>();
	private ArrayList<ArrayList<NovelPageData>> mPageList = new ArrayList<ArrayList<NovelPageData>>();
	private CommonDBAdapter mDBAdapter = null;
	private long mBaseId = 0;
	
	private int mUpdateNum = 0;
	private int mFinishedNum = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novel_info);

		Intent i = getIntent();
		if( i == null || !i.hasExtra("data") ) finish();
		mBaseData = (NovelBaseData)i.getSerializableExtra("data");

		mDBAdapter = new CommonDBAdapter(getApplicationContext());
		mDBAdapter.openDB();
		mBaseId = mDBAdapter.getBaseIdByNcode(mBaseData.getNcode());
		mBaseId = mDBAdapter.addBase(mBaseId,mBaseData,true);
		mDBAdapter.closeDB();

		((Button)findViewById(R.id.novel_info_update_btn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mPageList != null && mPageList.size() > 0 ){
					mUpdateNum = 0;
					for( ArrayList<NovelPageData> list : mPageList )
						for( NovelPageData data : list )
							if( data.getStatus() != NovelPageStatus.OK )
								++mUpdateNum;
					for( ArrayList<NovelPageData> list : mPageList )
						for( NovelPageData data : list )
							if( data.getStatus() != NovelPageStatus.OK )
								RequestPage(data.getPage());
					
					ShowProgressDialog();
				}
			}
		});
	}

	@Override
	protected void onResume(){
		super.onResume();

		if( mBaseData != null ){
			if( !mBackFlag )
				RequestData();
			else{
				UpdateStatus();
				UpdateView();
			}
		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		if( mDBAdapter != null )
			mDBAdapter.close();
	}

	private void RequestData(){
		CommonStringRequest request = new CommonStringRequest(
				Method.POST,
				Constant.PAGE_URL+"/"+mBaseData.getNcode()+"/",
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
	
	private void RequestPage( int page ){
		CommonStringRequest request = new CommonStringRequest(
				Method.POST,
				Constant.PAGE_URL+"/"+mBaseData.getNcode()+"/"+page+"/",
				new Response.Listener<String>(){
					@Override
					public void onResponse(String response) {
						GetPage(response);
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
	}
	
	private void GetPage( String res ){
		if( res!=null ){
			// 処理が重いため、別タスクで
			(new GetPageTask(res)).execute();
		}
		else{
			ShowAlertDialog("データ取得エラー", "データを取得できませんでした。");
		}
	}
	
	private void UpdateStatus(){
		mDBAdapter.openDB();
		for( ArrayList<NovelPageData> list : mPageList )
			for( NovelPageData data : list )
				if( data.getStatus() != NovelPageStatus.OK )
					data.setStatus(mDBAdapter.getPageStatus(mBaseId, data));
		mDBAdapter.closeDB();
	}

	private void UpdateView(){
		if( mTitleList.size() > 0 ){
			ExpandableListView listview = (ExpandableListView)findViewById(R.id.novel_info_list);
			NovelPageListAdapter adapter = new NovelPageListAdapter(NovelInfoActivity.this,mTitleList,mPageList);
			listview.setGroupIndicator(null);
			listview.setAdapter(adapter);
			listview.setOnChildClickListener(new OnChildClickListener(){
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					NovelPageData data = mPageList.get(groupPosition).get(childPosition);

					Intent intent = new Intent(getApplicationContext(),NovelPageActivity.class);
					intent.putExtra("page", data.getPage());
					intent.putExtra("base_id", mBaseId);
					startActivity(intent);
					return false;
				}
			});
			for(int i=0; i < adapter.getGroupCount(); i++)
				listview.expandGroup(i);
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
			mPageList.clear();
			mTitleList.clear();
			mDBAdapter.openDB();

			Document document = Jsoup.parse(res);
			Elements elements = document.getElementsByClass("index_box").get(0).children();
			int elementNum = elements.size();
			if( elements.get(0).tagName().equals("dl") ){
				int chapterIndex = -1;
				int pageNum = 0;
				String chapterTitle = "";
				mPageList.add(new ArrayList<NovelPageData>());
				for( int i=0; i<elementNum; ++i ){
					Element element = elements.get(i);
					if( element.tagName().equals("div") ) continue;

					if( pageNum%100 == 0 ){
						chapterTitle = (pageNum+1)+"話～";
						mPageList.add(new ArrayList<NovelPageData>());
						mTitleList.add(chapterTitle);
						++chapterIndex;
					}
					Elements update = element.getElementsByClass("long_update");
					String time;
					if( update.select("span").isEmpty() )
						time = update.text().replaceAll("[^0-9]","");
					else
						time = update.select("span").attr("title").replaceAll("[^0-9]","");
					Date date = CommonUtility.getDate(time, "yyyyMMdd");

					String url = element.getElementsByTag("a").attr("href");
					String temp[] = url.split("/",0);

					NovelPageData data = new NovelPageData();
					data.setNcode(temp[1]);
					data.setPage(Integer.valueOf(temp[2]));
					data.setChapterTitle(chapterTitle);
					data.setSubtitle(element.getElementsByClass("subtitle").text());
					data.setTime(date);
					data.setStatus(mDBAdapter.getPageStatus(mBaseId,data));
					mPageList.get(chapterIndex).add(data);

					if( data.getStatus() == NovelPageStatus.NG )
						mDBAdapter.addPage(mBaseId, data);

					++pageNum;
				}
			}
			else{
				int chapterIndex = -1;
				String chapterTitle = "";
				for( int i=0; i<elementNum; ++i ){
					Element element = elements.get(i);

					if( element.tagName().equals("div") ){
						chapterTitle = element.text();
						mPageList.add(new ArrayList<NovelPageData>());
						mTitleList.add(chapterTitle);
						++chapterIndex;
					}
					else{
						Elements update = element.getElementsByClass("long_update");
						String time;
						if( update.select("span").isEmpty() )
							time = update.text().replaceAll("[^0-9]","");
						else
							time = update.select("span").attr("title").replaceAll("[^0-9]","");
						Date date = CommonUtility.getDate(time, "yyyyMMdd");

						String url = element.getElementsByTag("a").attr("href");
						String temp[] = url.split("/",0);

						NovelPageData data = new NovelPageData();
						data.setNcode(temp[1]);
						data.setPage(Integer.valueOf(temp[2]));
						data.setChapterTitle(chapterTitle);
						data.setSubtitle(element.getElementsByClass("subtitle").text());
						data.setTime(date);
						data.setStatus(mDBAdapter.getPageStatus(mBaseId,data));
						mPageList.get(chapterIndex).add(data);

						if( data.getStatus() == NovelPageStatus.NG )
							mDBAdapter.addPage(mBaseId, data);
					}
				}
			}
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

	class GetPageTask extends AsyncTask<Void, Void, Void>{
		String res;
		public GetPageTask( String r ){
			res = r;
		}
		@Override
		// メイン処理
		protected Void doInBackground(Void... params) {
			Document document = Jsoup.parse(res);
			String[] temp = document.getElementsByTag("link").get(1).attr("href").split("/");
			String content = document.getElementById("novel_honbun").html();
			//String content = document.getElementById("novel_honbun").text();
			int page = Integer.valueOf(temp[4]);

			for( ArrayList<NovelPageData> list : mPageList )
				for( NovelPageData data : list )
					if( data.getPage() == page )
						data.setContent(content);

			return null;
		}
		@Override
		// 後処理　UI更新
		protected void onPostExecute(Void result) {

			++mFinishedNum;
			if( mFinishedNum >= mUpdateNum ){
				mDBAdapter.openDB();
				mPageList = mDBAdapter.savePageContents(mBaseId, mPageList);
				mDBAdapter.closeDB();
				UpdateView();
				DismissProgressDialog();
			}
		}
	}
}
