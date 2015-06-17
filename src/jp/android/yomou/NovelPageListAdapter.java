package jp.android.yomou;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

public class NovelPageListAdapter extends BaseExpandableListAdapter {

	private ArrayList<String> mTitleList = null;
	private ArrayList<ArrayList<NovelPageData>> mPageList = null;
	private LayoutInflater mInflater;

	public NovelPageListAdapter( Activity context, ArrayList<String> titleList, ArrayList<ArrayList<NovelPageData>> pageList ){
		mInflater = context.getLayoutInflater();
		mTitleList = titleList;
		mPageList = pageList;
	}

	@Override
	public NovelPageData getChild(int groupPosition, int childPosition) {
		return mPageList.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if( convertView == null )
			convertView = mInflater.inflate(R.layout.listview_page_child, null);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd",Locale.JAPANESE);
		
		((TextView)convertView.findViewById(R.id.list_page_subtitle_txt)).setText(getChild(groupPosition,childPosition).getSubtitle());
		((TextView)convertView.findViewById(R.id.list_page_time_txt)).setText(sdf.format(getChild(groupPosition,childPosition).getTime()));
		
		ImageButton button = (ImageButton)convertView.findViewById(R.id.list_page_btn);
		switch(getChild(groupPosition,childPosition).getStatus()){
		case OK:
			button.setImageResource(R.drawable.icon_page_ok);
			break;
		case REFRESH:
			button.setImageResource(R.drawable.icon_page_refresh);
			break;
		case NO_CONTENT:
		case NG:
			button.setImageResource(R.drawable.icon_page_ng);
			break;
		default:
			break;
		}
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mPageList.get(groupPosition).size();
	}

	@Override
	public String getGroup(int groupPosition) {
		return mTitleList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mTitleList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_page_parent, null);
		}
		((TextView)convertView.findViewById(R.id.list_page_chapter_txt)).setText(getGroup(groupPosition));

		return convertView;
	}

	public int getRowId(int groupPosition) {  
		return groupPosition;  
	}  

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		return true;
	}
}
