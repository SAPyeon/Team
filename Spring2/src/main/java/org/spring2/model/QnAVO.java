package org.spring2.model;

public class QnAVO {
	private String title;
	private String content;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	@Override
	public String toString() {
		return "QnAVO [title=" + title + ", content=" + content + "]";
	}
	
	
}
