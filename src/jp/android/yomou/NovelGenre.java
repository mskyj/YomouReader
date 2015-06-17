package jp.android.yomou;


public enum NovelGenre {
	LITERATURE(1,"���w"),
	LOVE(2,"����"),
	HISTORY(3,"���j"),
	DETECTIVE(4,"����"),
	FANTASY(5,"�t�@���^�W�["),
	SF(6,"SF"),
	HORROR(7,"�z���["),
	COMEDY(8,"�R���f�B�["),
	ADVENTURE(9,"�`��"),
	SCHOOL(10,"�w��"),
	WAR(11,"��L"),
	FAIRYTALE(12,"���b"),
	POEM(13,"��"),
	ESSAY(14,"�G�b�Z�C"),
	REPLAY(16,"���v���C"),
	OTHER(15,"���̑�");
	
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
