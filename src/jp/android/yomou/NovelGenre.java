package jp.android.yomou;


public enum NovelGenre {
	LITERATURE(1,"文学"),
	LOVE(2,"恋愛"),
	HISTORY(3,"歴史"),
	DETECTIVE(4,"推理"),
	FANTASY(5,"ファンタジー"),
	SF(6,"SF"),
	HORROR(7,"ホラー"),
	COMEDY(8,"コメディー"),
	ADVENTURE(9,"冒険"),
	SCHOOL(10,"学園"),
	WAR(11,"戦記"),
	FAIRYTALE(12,"童話"),
	POEM(13,"詩"),
	ESSAY(14,"エッセイ"),
	REPLAY(16,"リプレイ"),
	OTHER(15,"その他");
	
	private int id = 0;
	private String name = "";
	NovelGenre( int id, String name ){
		this.id = id;
		this.name = name;
	}
	
	public int getID(){
		return id;
	}
	public String getName(){
		return name;
	}
	public static String getName( int id ){
		return get(id).getName();
	}
	
	public static NovelGenre get( int id ){
		NovelGenre[] genre = NovelGenre.values();
		return genre[id-1];
	}
	
	public static NovelGenre get( String name ){
		NovelGenre[] genreArr = NovelGenre.values();
		for( NovelGenre genre : genreArr )
			if( name.equals(genre.name) )
				return genre;
		return null;
	}
}
