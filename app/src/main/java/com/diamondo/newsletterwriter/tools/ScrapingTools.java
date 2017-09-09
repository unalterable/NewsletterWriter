package com.diamondo.newsletterwriter.tools;

import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.diamondo.newsletterwriter.model.Feed;
import com.diamondo.newsletterwriter.model.Item;

public class ScrapingTools {

	public static String configureURLForLinklessItems(Feed feed, String itemURL, String itemURLText) {
		if (feed.type.contains("noItemLinks")) {
			if(itemURL != null && itemURL.length() > 0)
			{
				itemURL = feed.url + "#" + itemURL;
			} else {
				itemURL = feed.url + "#" + itemURLText;
			}
		}
		return itemURL;
	}

	public static Document fetchDocumentFromFeedPage(Feed feed) {
		Document document = null;
		int i = 0;
		do {
			i++;
			if (feed.type.contains("xml")) {
				document = JsoupTools.fetchXMLDocumentFromURL(feed.url);
			} else {
				document = JsoupTools.fetchHTMLDocumentFromURL(feed.url);
			}
			if(document == null && i == 5)
			{
				document = new Document("");
			}
		} while (document == null && i < 5);
		return document;
	}
	
	public static String titleFromElement(Element element, Item item) {
		String title = null;
		if(item.shouldItemPopulateFromOwnPage()){
			title = JsoupTools.titleTagFromElement(element);
		} else {
			title = JsoupTools.selectStringFromElement(element, item.feed.itemParagraphSelector1, item.feed.itemParagraphAttribute1);
		}
		return title;
	}
	
	public static String paragraphFromElement(Element element, Item item){
		String paragraph = null;
		paragraph = JsoupTools.selectStringFromElement(element, item.feed.itemParagraphSelector0, item.feed.itemParagraphAttribute0);
		if(paragraph == null){
			paragraph = JsoupTools.selectStringFromElement(element, item.feed.itemParagraphSelector1, item.feed.itemParagraphAttribute1);
		}
		return paragraph;
	}

	public static Date dateFromElement(Element element, Item item) {
		Date newDate = item.date;
		String dateAsString = JsoupTools.selectStringFromElement(element, item.feed.itemDateSelector0, item.feed.itemDateAttribute0);
		Date dateFromDocument = Tools.parseStringToDate(dateAsString, item.feed.itemDateFormat0);
		if (dateFromDocument != null && (newDate == null || dateFromDocument.after(newDate))) {
			newDate = dateFromDocument;
		}
		return newDate;
	}

}
