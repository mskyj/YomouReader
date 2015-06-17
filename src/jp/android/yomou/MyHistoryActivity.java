package jp.android.yomou;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

public class MyHistoryActivity extends CommonActivity {

	private CommonDBAdapter mDBAdapter = null;
	private ArrayList<NovelBaseData> novelList = new ArrayList<NovelBaseData>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_history);
		
		mDBAdapter = new CommonDBAdapter(getApplicationContext());
	}
	
	@Override
	protected void onResume(){
		super.onResume();

		UpdateView();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		if( mDBAdapter != null )
			mDBAdapter.close();
	}
	
	private void UpdateView(){
		mDBAdapter.openDB();
		novelList = mDBAdapter.getHistory(0, 10);
		mDBAdapter.closeDB();
		
		if( novelList.size() > 0 ){
			ExpandableListView listview = (ExpandableListView)findViewById(R.id.history_list);
			//NovelInfoAdapter adapter = new NovelInfoAdapter(getApplicationContext(), 0, novelList);
			NovelBaseListAdapter adapter = new NovelBaseListAdapter(MyHistoryActivity.this,novelList);
			listview.setGroupIndicator(null);
			listview.setAdapter(adapter);
			listview.setOnChildClickListener(new OnChildClickListener(){
				@Override
				public boolean onChildClick(ExpandableListView parent, View v,
						int groupPosition, int childPosition, long id) {
					if( childPosition == 1 ){
						if( novelList != null ){
							Intent intent = new Intent(getApplicationContext(),NovelInfoActivity.class);
							intent.putExtra("data", novelList.get(groupPosition));
							startActivity(intent);
						}
					}
					return false;
				}
			});
		}
	}
}
