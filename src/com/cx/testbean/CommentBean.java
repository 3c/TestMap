package com.cx.testbean;

public class CommentBean {
	public String cid;
	public String appid;
	public String appModuleId;
	public String authorid;
	public String author;
	public String id;
	public String idtype;
	public String putime;
	public String message;
	public String imglink;
	public String thumblink;
	@Override
	public String toString() {
		return "CommentBean [cid=" + cid + ", appid=" + appid
				+ ", appModuleId=" + appModuleId + ", authorid=" + authorid
				+ ", author=" + author + ", id=" + id + ", idtype=" + idtype
				+ ", putime=" + putime + ", message=" + message + ", imglink="
				+ imglink + ", thumblink=" + thumblink + "]";
	}
}
