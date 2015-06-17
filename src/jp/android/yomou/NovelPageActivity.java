package jp.android.yomou;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;

public class NovelPageActivity extends CommonActivity {

	private long mBaseId = -1;
	private CommonDBAdapter mDBAdapter = null;
	private NovelBaseData mBaseData = null;
	private NovelPageData mPageData = null;
	private int mPage;
	private boolean mFirstFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novel_page);

		Intent i = getIntent();
		if( i == null || !i.hasExtra("page") || !i.hasExtra("base_id") ) finish();
		mPage = i.getIntExtra("page", 1);
		mBaseId = i.getLongExtra("base_id", -1);

		mDBAdapter = new CommonDBAdapter(getApplicationContext());
		if( i.hasExtra("data") )
			mBaseData = (NovelBaseData)i.getSerializableExtra("data");
		else{
			mDBAdapter.openDB();
			mBaseData = mDBAdapter.getBaseData(mBaseId);
			mDBAdapter.closeDB();
		}
		if( mBaseData == null ) finish();

		((Button)findViewById(R.id.page_menu_back)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mPage == 1 )
					ShowAlertDialog("エラー","前ページがありません。");
				else{
					--mPage;
					Update();
				}
			}
		});
		((Button)findViewById(R.id.page_menu_next)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				++mPage;
				Update();
			}
		});
		
		mFirstFlag = true;
	}

	@Override
	protected void onResume(){
		super.onResume();
		
		if( mFirstFlag ){
			Update();
			mFirstFlag = false;
		}
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		if( mDBAdapter != null )
			mDBAdapter.close();
	}

	private void Update(){
		mDBAdapter.openDB();
		mPageData = mDBAdapter.getPageData(mBaseId, mPage);
		mDBAdapter.closeDB();
		if( mPageData == null || mPageData.getStatus() != NovelPageStatus.OK ) 
			RequestData();
		else
			UpdateView();
	}

	private void RequestData(){
		CommonStringRequest request = new CommonStringRequest(
				Method.POST,
				Constant.PAGE_URL+"/"+mBaseData.getNcode()+"/"+mPage+"/",
				new Response.Listener<String>(){
					@Override
					public void onResponse(String response) {
						GetData(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						DismissProgressDialog();
						int errorCode = error.networkResponse.statusCode;
						if( errorCode == 404 ){
							ShowAlertDialog("エラー","ページが存在しません。");
							--mPage;
						}
						else
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
		((ScrollView)findViewById(R.id.page_scroll)).fullScroll(ScrollView.FOCUS_UP);
		((TextView)findViewById(R.id.page_title_txt)).setText(mBaseData.getTitle());
		if( mPageData.getChapterTitle().length() == 0 )
			((TextView)findViewById(R.id.page_chapter_txt)).setText(mPageData.getChapterTitle());
		else
			((TextView)findViewById(R.id.page_chapter_txt)).setVisibility(View.GONE);
		((TextView)findViewById(R.id.page_subtitle_txt)).setText(mPageData.getSubtitle());
		CharSequence cs = Html.fromHtml(mPageData.getContent());
		((TextView)findViewById(R.id.page_content_txt)).setText(cs);
	}

	class GetDataTask extends AsyncTask<Void, Void, Void>{
		String res;
		public GetDataTask( String r ){
			res = r;
		}
		@Override
		// メイン処理
		protected Void doInBackground(Void... params) {
			Document document = Jsoup.parse(res);
			String content = document.getElementById("novel_honbun").html();
			//String content = document.getElementById("novel_honbun").text();

			if( document.hasClass("chapter_title") )
				mPageData.setChapterTitle(document.getElementsByClass("chapter_title").text());
			mPageData.setSubtitle(document.getElementsByClass("novel_subtitle").text());
			mPageData.setContent(content);

			mDBAdapter.openDB();
			mDBAdapter.savePageContent(mBaseId, mPageData.getPage(), content);
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
}
