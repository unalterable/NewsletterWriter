package com.diamondo.newsletterwriter.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.database.Cursor;
import android.widget.Toast;

public class Feed {

	public int id;
	
	public String name;
	public String url;
	public String category;
	public String tag;
	public String type;
	public String page2URL;

	public String feedItemSelector;
	public String feedURLSelector0;
	public String feedURLAttrbute0;
	public String feedDateSelector0;
	public String feedDateAttrbute0;
	public String feedDateFormat0;

	public String itemParagraphSelector0;
	public String itemParagraphAttribute0;
	public String itemParagraphSelector1;
	public String itemParagraphAttribute1;

	public String itemDateSelector0;
	public String itemDateAttribute0;
	public String itemDateFormat0;

	public Feed(int id, String name, String url, String category, String tag, String type, String page2URL,
			String feedItemSelector, String feedURLSelector0, String feedURLAttrbute0, String feedDateSelector0,
			String feedDateAttrbute0, String feedDateFormat0, String itemParagraphSelector0,
			String itemParagraphAttribute0, String itemParagraphSelector1, String itemParagraphAttribute1,
			String itemDateSelector0, String itemDateAttribute0, String itemDateFormat0) {
		this.id = id;	
		this.name = name;
		this.url = url;
		this.category = category;
		this.tag = tag;
		this.type = type;
		this.page2URL = page2URL;
		this.feedItemSelector = feedItemSelector;
		this.feedURLSelector0 = feedURLSelector0;
		this.feedURLAttrbute0 = feedURLAttrbute0;
		this.feedDateSelector0 = feedDateSelector0;
		this.feedDateAttrbute0 = feedDateAttrbute0;
		this.feedDateFormat0 = feedDateFormat0;
		this.itemParagraphSelector0 = itemParagraphSelector0;
		this.itemParagraphAttribute0 = itemParagraphAttribute0;
		this.itemParagraphSelector1 = itemParagraphSelector1;
		this.itemParagraphAttribute1 = itemParagraphAttribute1;
		this.itemDateSelector0 = itemDateSelector0;
		this.itemDateAttribute0 = itemDateAttribute0;
		this.itemDateFormat0 = itemDateFormat0;
	}
}
