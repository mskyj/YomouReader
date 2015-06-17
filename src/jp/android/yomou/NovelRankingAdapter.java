package jp.android.yomou;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class NovelRankingAdapter extends BaseExpandableListAdapter {

	private ArrayList<NovelBaseData> mNovelList = null;
	private LayoutInflater mInflater;
	private int offset = 0;
	private int showNum = -1;

	public NovelRankingAdapter( Activity context, ArrayList<NovelBaseData> novelList ){
		mInflater = context.getLayoutInflater();
		mNovelList = novelList;
		showNum = novelList.size();
	}
	
	public NovelRankingAdapter( Activity context, ArrayList<NovelBaseData> novelList, int offset, int showNum ){
		mInflater = context.getLayoutInflater();
		mNovelList = novelList;
		this.offset = offset;
		this.showNum = showNum;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mNovelList.get(groupPosition+offset);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		if( childPosition == 1 ){
			convertView = mInflater.inflate(R.layout.listview_base_child2, null);
		}
		else{
			NovelBaseData data = (NovelBaseData)getChild(groupPosition,childPosition);
			
			convertView = mInflater.inflate(R.layout.listview_base_child, null);
			((TextView)convertView.findViewById(R.id.list_info_story_txt)).setText(data.getStory());
		}
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 2;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mNovelList.get(groupPosition+offset);
	}

	@Override
	public int getGroupCount() {
		return showNum;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ParentViewHolder holder = null;

		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.listview_base_parent, null);

			holder = new ParentViewHolder();
			holder.title_txt = (TextView)convertView.findViewById(R.id.list_info_title_txt);
			holder.writer_txt = (TextView)convertView.findViewById(R.id.list_info_writer_txt);
			holder.genre_txt = (TextView)convertView.findViewById(R.id.list_info_genre_txt);
			holder.status_txt = (TextView)convertView.findViewById(R.id.list_info_status_txt);
			holder.length_txt = (TextView)convertView.findViewById(R.id.list_info_length_txt);
			holder.time_txt = (TextView)convertView.findViewById(R.id.list_info_time_txt);
			holder.point_txt = (TextView)convertView.findViewById(R.id.list_info_point_txt);
			convertView.setTag(holder);
		}
		else
			holder = (ParentViewHolder)convertView.getTag();

		NovelBaseData data = (NovelBaseData)getGroup(groupPosition);
		String status = "";
		if( data.getType() == 2 )
			status = "短編";
		else{
			if( data.getEndFlag() == 0 )
				status = "連載中";
			else
				status = "完結済み";
			status += "(全"+data.getPageNum()+"ページ)";
		}
		
		holder.title_txt.setText(data.getTitle());
		holder.writer_txt.setText(data.getWriter());
		holder.genre_txt.setText(data.getGenre().getName());
		holder.status_txt.setText(status);
		holder.length_txt.setText(data.getLength()+"文字");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm",Locale.JAPANESE);
		holder.time_txt.setText(sdf.format(data.getUpdateTime()));
		holder.point_txt.setText(data.getGlobalPoint()+"pt");

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

	private class ParentViewHolder {
		TextView title_txt,writer_txt,genre_txt,status_txt,length_txt,time_txt,point_txt;
	}
}
