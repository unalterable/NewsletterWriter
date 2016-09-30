package com.diamondo.newsletterwriter.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.content.Context;

import com.diamondo.newsletterwriter.services.DBInterface;
import com.diamondo.newsletterwriter.services.ScrapingThread;
import com.diamondo.newsletterwriter.tools.DBHelper;
import com.diamondo.newsletterwriter.tools.JsoupTools;
import com.diamondo.newsletterwriter.tools.ScrapingTools;
import com.diamondo.newsletterwriter.tools.Tools;

public class Scraper {

	/*private List<Item> items = new ArrayList<Item>();
	private List<Feed> feeds;
	private Context context;

	public Scraper (List<Feed> feedsToBeScraped, Context context)
	{
		this.feeds = feedsToBeScraped;
		this.context = context;
	}
	
	public Item nextItem(){
		if()
		return null;
	}*/
	
	public static List<Item> itemsFromFeed(Feed feed) {
		List<Item> linkOnlyItems = new ArrayList<Item>();
		Document document = ScrapingTools.fetchDocumentFromFeedPage(feed);
		
		Elements selectedElements = document.select(feed.feedItemSelector);
		for (Element element : selectedElements) {
			Date date = Tools.parseStringToDate(
					JsoupTools.selectStringFromElement(element, feed.feedDateSelector0, feed.feedDateAttrbute0),
					feed.feedDateFormat0);
			String itemURL = JsoupTools.selectStringFromElement(element, feed.feedURLSelector0,
					feed.feedURLAttrbute0);
			String itemURLText = JsoupTools.selectStringFromElement(element, feed.feedURLSelector0, "");

			itemURL = ScrapingTools.configureURLForLinklessItems(feed, itemURL, itemURLText);

			linkOnlyItems.add(new Item(itemURL, element, date, itemURLText, feed));
		}
		Tools.removeDuplicatesAndBlanks(linkOnlyItems);
		return linkOnlyItems;
	}
	
	public static Item populate(Item item) {
		Element element = item.element;	
		
		if (item.shouldItemPopulateFromOwnPage()) {
			element = JsoupTools.fetchHTMLDocumentFromURL(item.url);
		}
			
		item.title = ScrapingTools.titleFromElement(element, item);
		item.paragraph = ScrapingTools.paragraphFromElement(element, item);
		item.date = ScrapingTools.dateFromElement(element, item);
		
		return item;
		
	}

//	public static void runScraperForAllFeeds(Context context) {
//		DBHelper myDBHelper;
//		DBInterface databaseInterface;
//		myDBHelper = new DBHelper(context);
//		databaseInterface = new DBInterface(myDBHelper);
//		databaseInterface.open();
//			List <Feed> feeds = databaseInterface.getAllFeeds();
//		databaseInterface.close();
//		new ScrapingThread(context, feeds, false).execute();
//	}
	
}
