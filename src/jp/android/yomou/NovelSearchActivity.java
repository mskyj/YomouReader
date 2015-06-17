package jp.android.yomou;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

public class NovelSearchActivity extends CommonActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_novel_search);
		
		String keyword = CommonUtility.GetString(getApplicationContext(), Constant.PRE_SEARCH_KEYWORD);
		int sort = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_SORT);
		int genre = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_GENRE);
		int type = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_TYPE);
		int time_from = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_TIME_FROM);
		int time_to = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_TIME_TO);
		int char_from = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_CHAR_FROM);
		int char_to = CommonUtility.GetInt(getApplicationContext(), Constant.PRE_SEARCH_CHAR_TO);
		
		((EditText)findViewById(R.id.search_keyword_input)).setText(keyword);
		((Spinner)findViewById(R.id.search_sort_select)).setSelection(sort);
		((Spinner)findViewById(R.id.search_genre_select)).setSelection(genre);
		((Spinner)findViewById(R.id.search_type_select)).setSelection(type);
		if( time_from > 0 )
			((EditText)findViewById(R.id.search_time_from_input)).setText(String.valueOf(time_from));
		if( time_to > 0 )
			((EditText)findViewById(R.id.search_time_to_input)).setText(String.valueOf(time_to));
		if( char_from > 0 )
			((EditText)findViewById(R.id.search_char_num_from_input)).setText(String.valueOf(char_from));
		if( char_to > 0 )
			((EditText)findViewById(R.id.search_char_num_to_input)).setText(String.valueOf(char_to));
		
		
		((ImageButton)findViewById(R.id.search_close_btn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finishWithAnim();
			}
		});
		((Button)findViewById(R.id.search_exe_btn)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int sort = ((Spinner)findViewById(R.id.search_sort_select)).getSelectedItemPosition();
				int genre = ((Spinner)findViewById(R.id.search_genre_select)).getSelectedItemPosition();
				int type = ((Spinner)findViewById(R.id.search_type_select)).getSelectedItemPosition();
				String time_from = ((EditText)findViewById(R.id.search_time_from_input)).getText().toString();
				String time_to = ((EditText)findViewById(R.id.search_time_to_input)).getText().toString();
				String char_from = ((EditText)findViewById(R.id.search_char_num_from_input)).getText().toString();
				String char_to = ((EditText)findViewById(R.id.search_char_num_to_input)).getText().toString();
				String keyword = ((EditText)findViewById(R.id.search_keyword_input)).getText().toString();
				
				CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_SORT, sort);
				CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_GENRE, genre);
				CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_TYPE, type);
				if( time_from.length() > 0 )
					CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_TIME_FROM, CommonUtility.getInt(time_from));
				else
					CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_TIME_FROM, 0);
				
				if( time_to.length() > 0 )
					CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_TIME_TO, CommonUtility.getInt(time_to));
				else
					CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_TIME_TO, 0);

				if( char_from.length() > 0 )
					CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_CHAR_FROM, CommonUtility.getInt(char_from));
				else
					CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_CHAR_FROM, 0);
				
				if( char_to.length() > 0 )
					CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_CHAR_TO, CommonUtility.getInt(char_to));
				else
					CommonUtility.SaveInt(getApplicationContext(), Constant.PRE_SEARCH_CHAR_TO, 0);

				CommonUtility.SaveString(getApplicationContext(), Constant.PRE_SEARCH_KEYWORD, keyword);
				
				finish();
			}
		});
	}
}
