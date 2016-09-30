package com.diamondo.newsletterwriter.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import com.diamondo.newsletterwriter.tools.JsoupTools;
import com.diamondo.newsletterwriter.tools.ScrapingTools;
import com.diamondo.newsletterwriter.tools.Tools;

public class Item {
	private int databaseID;
	public String url;
	public String title;
	public String paragraph;
	public Date date;
	public String read = "";
	public Element element = null;

	public Feed feed;

	public Item(String url, Element element, Date date, String title, Feed feed) {
		this.url = url;
		this.title = title;
		this.paragraph = "";
		this.date = date;
		this.element = element;
		this.feed = feed;
	}
	
	public Item(int databaseID, String url, Date date, String title, String paragraph, Feed feed, String read) {
		this.databaseID = databaseID;
		this.url = url;
		this.title = title;
		this.paragraph = paragraph;
		this.date = date;
		this.read = read;
		this.feed = feed;
	}
	
	public Item(Item item){
		this.databaseID = item.databaseID;
		this.url = item.url;
		this.title = item.title;
		this.paragraph = item.paragraph;
		this.date = item.date;
		this.read = item.read;
		this.element = item.element;

		this.feed = item.feed;
		
	}
	
	public int id(){
		return databaseID;
	}
	
	public String getUrl() {
		if (this.url == null) {
			return "NULL VALUE";
		} else {
			return this.url;
		}
	}

	public String getTitle() {
		if (this.title == null) {
			return "NULL VALUE";
		} else {
			return this.title;
		}
	}

	public String getParagraph() {
		if (this.paragraph == null) {
			return "NULL VALUE";
		} else {
			return this.paragraph;
		}
	}

	public void populate() {
		Element element = this.element;	
		
		if (shouldItemPopulateFromOwnPage()) {
			element = JsoupTools.fetchHTMLDocumentFromURL(this.url);
		}
			
		this.title = ScrapingTools.titleFromElement(element, this);
		this.paragraph = ScrapingTools.paragraphFromElement(element, this);
		this.date = ScrapingTools.dateFromElement(element, this);
	}

	public String getDisplayDate() {
		if (date == null) {
			if (ScrapingTools.dateFromElement(this.element, this) == null) {
				return "NULL: from NULL VALUE";
			} else {
				return "NULL: from a Value";
			}
		} else {
			String dateFormat = "HH:mm, d MMM yy";
			String displayDate = Tools.dateToString(date, dateFormat);
			return displayDate;
		}
	}

	public String getDisplayParagraph() {
		return "Feed: " + feed.name + "\n"
				+ Tools.truncateString(getTitle(), 20) + " | "
				+ Tools.truncateString(getDisplayDate(), 20) + "\n"
				+ Tools.truncateString(getParagraph(), 150) + "\n"
				+ Tools.truncateString(getUrl(), 35) + "\n";
	}

	public String getSendParagraph() {
		return getTitle()+ "\n"
				+ getParagraph() + "\n"
				+ getUrl() + "\n";
	}

	public Boolean isDateSet() {
		return !(this.date == null);
	}

	public Boolean isParagraphSet() {
		return !(this.paragraph == null);
	}

	public boolean isPopulatedFromFeedPage() {
		return this.feed.type.contains("noItemLinks") || this.feed.type.contains("allContentInFeed");
	}
	
	public boolean shouldItemPopulateFromOwnPage() {
		return !isPopulatedFromFeedPage();
	}

}
