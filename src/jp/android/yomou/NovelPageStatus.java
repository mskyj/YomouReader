package jp.android.yomou;

public enum NovelPageStatus {
	NG(0),NO_CONTENT(1),REFRESH(2),OK(3);
	
	private int id = 0;
	NovelPageStatus( int id ){
		this.id = id;
	}
	public int getID(){
		return id;
	}
	
	public static NovelPageStatus get( int id ){
		NovelPageStatus[] genre = NovelPageStatus.values();
		return genre[id];
	}
}
