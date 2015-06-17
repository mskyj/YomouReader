package jp.android.yomou;

import java.io.Serializable;
import java.util.Date;

public class NovelBaseData extends CommonData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String title = "";
	private String ncode = "";
	private String writer = "";
	private int userID = 0;
	private String story = "";
	private NovelGenre genre = NovelGenre.OTHER;
	private Date firstUpTime = null;
	private Date lastUpTime = null;
	private int type = 1;
	private int endFlag = 0;
	private int pageNum = 0;
	private int length = 0;
	private int stopFlag = 0;
	private int globalPoint = 0;
	private int bookmarkNum = 0;
	private int reviewNum = 0;
	private int ratePoint = 0;
	private int rateNum = 0;
	private int imageNum = 0;
	private int kaiwaRate = 0;
	private Date updateTime = null;
	
	private int bookmarkPage = 0;
	private int favoriteFlag = 0;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNcode() {
		return ncode;
	}
	public void setNcode(String ncode) {
		this.ncode = ncode;
	}
	public String getWriter() {
		return writer;
	}
	public void setWriter(String writer) {
		this.writer = writer;
	}
	public int getUserID() {
		return userID;
	}
	public void setUserID(int userID) {
		this.userID = userID;
	}
	public String getStory() {
		return story;
	}
	public void setStory(String story) {
		this.story = story;
	}
	public NovelGenre getGenre() {
		return genre;
	}
	public void setGenre(NovelGenre genre) {
		this.genre = genre;
	}
	public Date getFirstUpTime() {
		return firstUpTime;
	}
	public void setFirstUpTime(Date firstUpTime) {
		this.firstUpTime = firstUpTime;
	}
	public Date getLastUpTime() {
		return lastUpTime;
	}
	public void setLastUpTime(Date lastUpTime) {
		this.lastUpTime = lastUpTime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getEndFlag() {
		return endFlag;
	}
	public void setEndFlag(int endFlag) {
		this.endFlag = endFlag;
	}
	public int getPageNum() {
		return pageNum;
	}
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getStopFlag() {
		return stopFlag;
	}
	public void setStopFlag(int stopFlag) {
		this.stopFlag = stopFlag;
	}
	public int getGlobalPoint() {
		return globalPoint;
	}
	public void setGlobalPoint(int globalPoint) {
		this.globalPoint = globalPoint;
	}
	public int getBookmarkNum() {
		return bookmarkNum;
	}
	public void setBookmarkNum(int bookmarkNum) {
		this.bookmarkNum = bookmarkNum;
	}
	public int getReviewNum() {
		return reviewNum;
	}
	public void setReviewNum(int reviewNum) {
		this.reviewNum = reviewNum;
	}
	public int getRatePoint() {
		return ratePoint;
	}
	public void setRatePoint(int ratePoint) {
		this.ratePoint = ratePoint;
	}
	public int getRateNum() {
		return rateNum;
	}
	public void setRateNum(int rateNum) {
		this.rateNum = rateNum;
	}
	public int getImageNum() {
		return imageNum;
	}
	public void setImageNum(int imageNum) {
		this.imageNum = imageNum;
	}
	public int getKaiwaRate() {
		return kaiwaRate;
	}
	public void setKaiwaRate(int kaiwaRate) {
		this.kaiwaRate = kaiwaRate;
	}
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public int getBookmarkPage() {
		return bookmarkPage;
	}
	public void setBookmarkPage(int bookmarkPage) {
		this.bookmarkPage = bookmarkPage;
	}
	public int getFavoriteFlag() {
		return favoriteFlag;
	}
	public void setFavoriteFlag(int favoriteFlag) {
		this.favoriteFlag = favoriteFlag;
	}
	

}
