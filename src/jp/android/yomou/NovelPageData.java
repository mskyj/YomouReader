package jp.android.yomou;

import java.util.Date;

public class NovelPageData extends CommonData {

	private String ncode = "";
	private int page = 1;
	private String chapterTitle = "";
	private String subtitle = "";
	private Date time = null;
	private NovelPageStatus status = NovelPageStatus.NG;
	private String content = "";

	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getNcode() {
		return ncode;
	}
	public void setNcode(String ncode) {
		this.ncode = ncode;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public String getChapterTitle() {
		return chapterTitle;
	}
	public void setChapterTitle(String chapterTitle) {
		this.chapterTitle = chapterTitle;
	}
	public NovelPageStatus getStatus() {
		return status;
	}
	public void setStatus(NovelPageStatus status) {
		this.status = status;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
}
