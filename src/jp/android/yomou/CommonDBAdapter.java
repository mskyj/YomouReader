package jp.android.yomou;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CommonDBAdapter {
	static final String DATABASE_NAME = "novel.db";
	static final int DATABASE_VERSION = 11;
	static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	static final long UPDATE_TIME = 3600000;

	protected final Context mContext;
	protected CommonSQLiteOpenHelper mHelper;
	protected SQLiteDatabase mWDB;
	protected SQLiteDatabase mRDB;

	public CommonDBAdapter(Context context){
		mContext = context;
		mHelper = new CommonSQLiteOpenHelper(mContext);
	}

	public void openDB(){
		openWriteDB();
		openReadDB();
	}

	public void openWriteDB(){
		mWDB = mHelper.getWritableDatabase();
	}

	public void openReadDB(){
		mRDB = mHelper.getReadableDatabase();
	}

	public void closeDB(){
		closeWriteDB();
		closeReadDB();
	}

	public void closeWriteDB(){
		mWDB.close();
		mWDB = null;
	}

	public void closeReadDB(){
		mRDB.close();
		mRDB = null;
	}

	public void close(){
		mHelper.close();
	}

	public long addBase( long base_id, NovelBaseData data, boolean view_flag ){
		String update_time = CommonUtility.getString(data.getUpdateTime(), DATE_FORMAT);

		ContentValues values = new ContentValues();
		values.put("title", data.getTitle());
		values.put("genre", data.getGenre().getID());
		values.put("ncode", data.getNcode());
		values.put("writer", data.getWriter());
		values.put("user_id", data.getUserID());
		values.put("length", data.getLength());
		values.put("sum_point", data.getGlobalPoint());
		values.put("story", data.getStory());
		values.put("type", data.getType());
		values.put("end_flag", data.getEndFlag());
		values.put("page_num", data.getPageNum());
		values.put("update_time", update_time);

		if( view_flag ){
			String now_time = CommonUtility.getString(new Date(), DATE_FORMAT);
			values.put("view_time", now_time);
		}

		values.put("favorite_flag", 0);
		values.put("bookmark_page", 0);

		if( base_id == -1 )
			base_id = mWDB.insert("tbl_base_info", null, values);
		else
			mWDB.update("tbl_base_info", values, "_id="+base_id, null);
		return base_id;
	}

	public NovelBaseData getBaseData( long base_id ){
		String sql = "SELECT * FROM tbl_base_info WHERE `_id` = " + base_id;
		Cursor cursor = mRDB.rawQuery(sql , null);
		if( !cursor.moveToFirst() ) return null;

		NovelBaseData data = new NovelBaseData();
		data.setTitle(cursor.getString(cursor.getColumnIndex("title")));
		data.setGenre(NovelGenre.get(cursor.getInt(cursor.getColumnIndex("genre"))));
		data.setNcode(cursor.getString(cursor.getColumnIndex("ncode")));
		data.setWriter(cursor.getString(cursor.getColumnIndex("writer")));
		data.setUserID(cursor.getInt(cursor.getColumnIndex("user_id")));
		data.setLength(cursor.getInt(cursor.getColumnIndex("length")));
		data.setGlobalPoint(cursor.getInt(cursor.getColumnIndex("sum_point")));
		data.setStory(cursor.getString(cursor.getColumnIndex("story")));
		data.setType(cursor.getInt(cursor.getColumnIndex("type")));
		data.setEndFlag(cursor.getInt(cursor.getColumnIndex("end_flag")));
		data.setPageNum(cursor.getInt(cursor.getColumnIndex("page_num")));
		data.setUpdateTime(CommonUtility.getDate(cursor.getString(cursor.getColumnIndex("update_time")), DATE_FORMAT));

		data.setBookmarkPage(cursor.getInt(cursor.getColumnIndex("bookmark_page")));
		data.setFavoriteFlag(cursor.getInt(cursor.getColumnIndex("favorite_flag")));

		cursor.close();

		return data;
	}

	public void setFavorite( long base_id, int flag ){
		ContentValues values = new ContentValues();
		values.put("favorite_flag", flag);
		mWDB.update("tbl_base_info", values, "_id="+base_id, null);
	}

	public void setBookmark( long base_id, int page ){
		ContentValues values = new ContentValues();
		values.put("bookmark_page", page);
		mWDB.update("tbl_base_info", values, "_id="+base_id, null);
	}

	// 重複注意
	public void addPage( long base_id, NovelPageData data ){
		if( base_id == -1 ) return;
		String update_time = CommonUtility.getString(data.getTime(), DATE_FORMAT);

		ContentValues values = new ContentValues();
		values.put("base_id", base_id);
		values.put("page", data.getPage());
		values.put("chapter_title", data.getChapterTitle());
		values.put("sub_title", data.getSubtitle());
		values.put("update_time", update_time);
		values.put("status", NovelPageStatus.NG.getID());

		mWDB.insert("tbl_page_info", null, values);
	}

	/*
	private String getContent( long base_id, int page ){
		InputStream in;
	    String lineBuffer = "";

	    try {
	        in = mContext.openFileInput(base_id+"_"+page+".txt");
	        BufferedReader reader= new BufferedReader(new InputStreamReader(in,"UTF-8"));
	        while( (lineBuffer += reader.readLine()) != null ){
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return lineBuffer;
	}

	private boolean saveContent( long base_id, int page, String content ){
		OutputStream out;
	    try {
	        out = mContext.openFileOutput(base_id+"_"+page+".txt",0);
	        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out,"UTF-8"));

	        writer.write(content);
	        writer.close();

	        return true;
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	 */

	public NovelPageData getPageData( long base_id, int page ){
		String sql = "SELECT * FROM tbl_page_info WHERE `base_id` = " + base_id + " AND `page` = " + page;
		Cursor cursor = mRDB.rawQuery(sql , null);
		if( !cursor.moveToFirst() ) return null;

		String content = cursor.getString(cursor.getColumnIndex("content"));
		if( content == null || content.length() == 0 )
			content = "";

		NovelPageData data = new NovelPageData();
		data.setPage(page);
		data.setPage(cursor.getInt(cursor.getColumnIndex("page")));
		data.setChapterTitle(cursor.getString(cursor.getColumnIndex("chapter_title")));
		data.setSubtitle(cursor.getString(cursor.getColumnIndex("sub_title")));
		data.setContent(content);
		data.setStatus(NovelPageStatus.get(cursor.getInt(cursor.getColumnIndex("status"))));

		cursor.close();

		return data;
	}

	public void savePageContent( long base_id, int page, String content ){

		ContentValues values = new ContentValues();
		values.put("content", content);
		values.put("status", NovelPageStatus.OK.getID());
		mWDB.update("tbl_page_info", values, "base_id="+base_id+" AND page="+page, null);
	}
	
	public ArrayList<ArrayList<NovelPageData>> savePageContents( long base_id, ArrayList<ArrayList<NovelPageData>> pageList ){
		mWDB.beginTransaction();
		try {
			ContentValues values = new ContentValues();
			for( ArrayList<NovelPageData> list : pageList )
				for( NovelPageData data : list )
					if( data.getStatus() != NovelPageStatus.OK && data.getContent() != null && data.getContent().length() > 0 ){
						values.put("content", data.getContent());
						values.put("status", NovelPageStatus.OK.getID());
						mWDB.update("tbl_page_info", values, "base_id="+base_id+" AND page="+data.getPage(), null);
						
						values.clear();
						data.setStatus(NovelPageStatus.OK);
						data.setContent("");
					}
			mWDB.setTransactionSuccessful();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			mWDB.endTransaction();
		}
		return pageList;
	}

	public long getBaseIdByNcode( String ncode ){
		String sql = "SELECT `_id` FROM tbl_base_info WHERE `ncode` = '" + ncode + "'";
		Cursor cursor = mRDB.rawQuery(sql , null);
		if( !cursor.moveToFirst() ) return -1;
		long id = cursor.getInt(0);
		cursor.close();
		return id;
	}

	public NovelPageStatus getPageStatus( long base_id, NovelPageData data ){
		String sql = "SELECT * FROM tbl_page_info WHERE `base_id` = " + base_id + " AND `page` = " + data.getPage();
		Cursor cursor = mRDB.rawQuery(sql , null);
		if( !cursor.moveToFirst() ) return NovelPageStatus.NG;
		String content = cursor.getString(cursor.getColumnIndex("content"));
		if( content == null || content.length() == 0 )
			return NovelPageStatus.NO_CONTENT;

		long id = cursor.getInt(cursor.getColumnIndex("_id"));
		String time1 = cursor.getString(cursor.getColumnIndex("update_time"));
		String time2 = CommonUtility.getString(data.getTime(), DATE_FORMAT);
		cursor.close();

		if( time1.equals(time2) )
			return NovelPageStatus.OK;
		else{
			ContentValues values = new ContentValues();
			values.put("status", NovelPageStatus.REFRESH.getID());
			mWDB.update("tbl_page_info", values, "_id="+id, null);
			return NovelPageStatus.REFRESH;
		}
	}

	public ArrayList<NovelPageData> getPageByNcode( String ncode ){
		long base_id = getBaseIdByNcode(ncode);
		if( base_id == -1 ) return null;

		String sql = "SELECT * FROM tbl_page_info WHERE `base_id` = " + base_id;
		Cursor cursor = mRDB.rawQuery(sql , null);
		if( !cursor.moveToFirst() ) return null;

		ArrayList<NovelPageData> pageList = new ArrayList<NovelPageData>();
		for (int i = 1; i <= cursor.getCount(); i++) {
			NovelPageData data = new NovelPageData();
			data.setNcode(ncode);
			data.setPage(cursor.getInt(cursor.getColumnIndex("page")));
			data.setChapterTitle(cursor.getString(cursor.getColumnIndex("chapter_title")));
			data.setSubtitle(cursor.getString(cursor.getColumnIndex("sub_title")));
			pageList.add(data);
			cursor.moveToNext();
		}
		cursor.close();

		return pageList;
	}

	public void updateRanking( int list_type, int type, int genre, ArrayList<NovelBaseData> list ){

		ContentValues values = new ContentValues();
		values.put("list_type", list_type);
		values.put("type", type);
		values.put("genre", genre);
		values.put("update_time", CommonUtility.getString(new Date(), DATE_FORMAT));

		boolean new_flag = false;
		boolean update_flag = false;
		RankingData ranking_data = getRankingInfo(list_type,type,genre);
		long ranking_id;
		if( ranking_data == null ){
			new_flag = true;
			ranking_id = mWDB.insert("tbl_ranking_info", null, values);
		}else{
			ranking_id = ranking_data.id;
			long diff = (new Date()).getTime() - ranking_data.lastUpdateTime.getTime();
			if( diff > UPDATE_TIME )
				update_flag = true;

			mWDB.update("tbl_ranking_info", values, "_id="+ranking_id, null);
		}

		if( update_flag || new_flag ){
			mWDB.beginTransaction();
			try {
				int rank = 1;
				values = new ContentValues();

				for( NovelBaseData data : list ){
					long base_id = getBaseIdByNcode(data.getNcode());
					base_id = addBase(base_id, data, false);

					values.put("ranking_id", ranking_id);
					values.put("base_id", base_id);
					values.put("rank", rank);

					if( new_flag )
						mWDB.insert("tbl_ranking_page_info", null, values);
					else
						mWDB.update("tbl_ranking_page_info", values, "ranking_id="+ranking_id+" AND rank="+rank, null);

					values.clear();
				}
				mWDB.setTransactionSuccessful();
			}catch (SQLException e) {
				e.printStackTrace();
			}finally {
				mWDB.endTransaction();
			}

		}
	}

	public RankingData getRankingInfo( int list_type, int type, int genre ){
		String sql;
		if( list_type == 1 )
			sql = "SELECT * FROM tbl_ranking_info WHERE `list_type` = " + list_type + " AND `type` = " + type + " AND `genre` = " + genre;
		else
			sql = "SELECT * FROM tbl_ranking_info WHERE `list_type` = " + list_type + " AND `type` = " + type;
		Cursor cursor = mRDB.rawQuery(sql , null);
		if( !cursor.moveToFirst() ) return null;
		RankingData data = new RankingData();
		data.id = cursor.getLong(cursor.getColumnIndex("_id"));
		data.lastUpdateTime = CommonUtility.getDate(cursor.getString(cursor.getColumnIndex("update_time")), DATE_FORMAT);
		cursor.close();
		return data;
	}

	public ArrayList<NovelBaseData> getRanking( int list_type, int type, int genre ){
		RankingData ranking_data = getRankingInfo(list_type,type,genre);
		if( ranking_data == null ) return null;
		if( (new Date()).getTime() - ranking_data.lastUpdateTime.getTime() > UPDATE_TIME )
			return null;

		String sql = "SELECT * FROM tbl_ranking_page_info WHERE `ranking_id` = " + ranking_data.id;
		Cursor cursor = mRDB.rawQuery(sql , null);
		if( !cursor.moveToFirst() ) return null;

		ArrayList<NovelBaseData> list = new ArrayList<NovelBaseData>();
		for (int i = 1; i <= cursor.getCount(); i++) {
			long base_id = cursor.getLong(cursor.getColumnIndex("base_id"));
			NovelBaseData data = getBaseData(base_id);
			list.add(data);
			cursor.moveToNext();
		}
		cursor.close();
		return list;
	}
	
	public ArrayList<NovelBaseData> getHistory( int page, int num ){
		if( page < 0 || num < 0 ) return null;
		
		String sql = "SELECT * FROM tbl_base_info order by view_time desc Limit "+(page*num)+","+num;
		Cursor cursor = mRDB.rawQuery(sql , null);
		if( !cursor.moveToFirst() ) return null;
		
		ArrayList<NovelBaseData> dataList = new ArrayList<NovelBaseData>();
		for (int i = 1; i <= cursor.getCount(); i++) {
			NovelBaseData data = new NovelBaseData();
			data.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			data.setGenre(NovelGenre.get(cursor.getInt(cursor.getColumnIndex("genre"))));
			data.setNcode(cursor.getString(cursor.getColumnIndex("ncode")));
			data.setWriter(cursor.getString(cursor.getColumnIndex("writer")));
			data.setUserID(cursor.getInt(cursor.getColumnIndex("user_id")));
			data.setLength(cursor.getInt(cursor.getColumnIndex("length")));
			data.setGlobalPoint(cursor.getInt(cursor.getColumnIndex("sum_point")));
			data.setStory(cursor.getString(cursor.getColumnIndex("story")));
			data.setType(cursor.getInt(cursor.getColumnIndex("type")));
			data.setEndFlag(cursor.getInt(cursor.getColumnIndex("end_flag")));
			data.setPageNum(cursor.getInt(cursor.getColumnIndex("page_num")));
			data.setUpdateTime(CommonUtility.getDate(cursor.getString(cursor.getColumnIndex("update_time")), DATE_FORMAT));

			data.setBookmarkPage(cursor.getInt(cursor.getColumnIndex("bookmark_page")));
			data.setFavoriteFlag(cursor.getInt(cursor.getColumnIndex("favorite_flag")));
			dataList.add(data);
			cursor.moveToNext();
		}
		cursor.close();

		return dataList;
	}

	private static class CommonSQLiteOpenHelper extends SQLiteOpenHelper{
		public CommonSQLiteOpenHelper(Context c){
			super(c, DATABASE_NAME, null, DATABASE_VERSION);
		}
		// データベースの新規作成時
		public void onCreate(SQLiteDatabase db) {
			// create
			//db.execSQL("CREATE TABLE tbl_base_info( _id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, genre INTEGER, ncode TEXT, author TEXT, author_no INTEGER, char_num INTEGER, description TEXT, status TEXT, review_num INTEGER, unique_num INTEGER, rate_num INTEGER, rate_point INTEGER, bookmark_num INTEGER, sum_point INTEGER, bookmark_page INTEGER, favorite_flag INTEGER, update_time TEXT, view_time TEXT);");
			db.execSQL("CREATE TABLE tbl_base_info( _id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, genre INTEGER, ncode TEXT, writer TEXT, user_id INTEGER, length INTEGER, story TEXT, sum_point INTEGER, type INTEGER, end_flag INTEGER, page_num INTEGER, bookmark_page INTEGER, favorite_flag INTEGER, update_time TEXT, view_time TEXT);");
			db.execSQL("CREATE TABLE tbl_page_info( _id INTEGER PRIMARY KEY AUTOINCREMENT, base_id INTEGER, page INTEGER, chapter_title TEXT, sub_title TEXT, content TEXT, status INTEGER, update_time TEXT, view_time TEXT);");

			db.execSQL("CREATE TABLE tbl_ranking_info( _id INTEGER PRIMARY KEY AUTOINCREMENT, list_type INTEGER, type INTEGER, genre INTEGER, update_time TEXT);");
			db.execSQL("CREATE TABLE tbl_ranking_page_info( _id INTEGER PRIMARY KEY AUTOINCREMENT, ranking_id INTEGER, base_id INTEGER, rank INTEGER);");
		}
		// データベースのversionアップ時
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// drop & create → updateの方が良いかも
			db.execSQL("DROP TABLE IF EXISTS tbl_base_info");
			db.execSQL("DROP TABLE IF EXISTS tbl_page_info");
			db.execSQL("DROP TABLE IF EXISTS tbl_ranking_info");
			db.execSQL("DROP TABLE IF EXISTS tbl_ranking_page_info");
			onCreate(db);
		}
	}

	private class RankingData{
		long id;
		Date lastUpdateTime;
	}
}
