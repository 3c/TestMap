package com.cx.testhandler;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.cx.testbean.CommentBean;

public class PullParseService {
	public static ArrayList<CommentBean> getCommentBeans(String url)
			throws Exception {
		ArrayList<CommentBean> CommentBeans = null;
		CommentBean CommentBean = null;
		//从指定的URl得到一个InputStream
		URL uri=new URL(url);
		URLConnection ucon = uri.openConnection();
		InputStream is = ucon.getInputStream();

		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");

		int event = parser.getEventType();// 产生第一个事件
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_DOCUMENT:// 判断当前事件是否是文档开始事件
				CommentBeans = new ArrayList<CommentBean>();// 初始化CommentBeans集合
				break;
			case XmlPullParser.START_TAG:// 判断当前事件是否是标签元素开始事件
				if ("comment".equals(parser.getName())) {// 判断开始标签元素是否是CommentBean
					CommentBean = new CommentBean();
				}
				if (CommentBean != null) {
					if ("cid".equals(parser.getName())) {// 判断开始标签元素是否是name
						CommentBean.cid = (parser.nextText());
					} else if ("appid".equals(parser.getName())) {// 判断开始标签元素是否是price
						CommentBean.appid = parser.nextText();
					} else if ("appModuleId".equals(parser.getName())) {// 判断开始标签元素是否是price
						CommentBean.appModuleId = parser.nextText();
					} else if ("authorid".equals(parser.getName())) {// 判断开始标签元素是否是price
						CommentBean.authorid = parser.nextText();
					} else if ("author".equals(parser.getName())) {// 判断开始标签元素是否是price
						CommentBean.author = parser.nextText();
					} else if ("id".equals(parser.getName())) {// 判断开始标签元素是否是price
						CommentBean.id = parser.nextText();
					} else if ("idtype".equals(parser.getName())) {// 判断开始标签元素是否是price
						CommentBean.idtype = parser.nextText();
					} else if ("putime".equals(parser.getName())) {// 判断开始标签元素是否是price
						CommentBean.putime = new SimpleDateFormat("yyyy.MM.dd HH:mm")
						.format(new Date(new Long(parser.nextText()) * 1000L)).toString();
					} else if ("imglink".equals(parser.getName())) {// 判断开始标签元素是否是price
						CommentBean.imglink = parser.nextText();
					} else if ("thumblink".equals(parser.getName())) {// 判断开始标签元素是否是price
						CommentBean.thumblink = parser.nextText();
					} else if ("message".equals(parser.getName())) {// 判断开始标签元素是否是price
						CommentBean.message = parser.nextText();
					}
				}
				break;
			case XmlPullParser.END_TAG:// 判断当前事件是否是标签元素结束事件
				if ("comment".equals(parser.getName())) {// 判断结束标签元素是否是CommentBean
					CommentBeans.add(CommentBean);// 将CommentBean添加到CommentBeans集合
					CommentBean = null;
				}
				break;
			}
			event = parser.next();// 进入下一个元素并触发相应事件
		}// end while
		return CommentBeans;
	}
}