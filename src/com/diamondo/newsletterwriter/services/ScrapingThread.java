package com.diamondo.newsletterwriter.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.diamondo.newsletterwriter.model.Feed;
import com.diamondo.newsletterwriter.model.Item;
import com.diamondo.newsletterwriter.model.Scraper;

import android.content.Context;
import android.os.AsyncTask;

public class ScrapingThread extends AsyncTask<Void, Item, Void> {
//	Context context;
	List<Item> items;
	List<Feed> feeds;
//	Item item;
	String currentFeedName;
	String progressString;
	

	UIInterface uiInterface;
	private Date dateLimit;
	private int itemLimitPerFeed;

	public ScrapingThread(Context context, List<Feed> feeds, Date dateLimit, int itemLimitPerFeed) {
		this.feeds = feeds;
		this.uiInterface = (UIInterface) context;
		this.dateLimit = dateLimit;
		this.itemLimitPerFeed = itemLimitPerFeed;
	}

	public interface UIInterface {

		void onProgressUpdate(String progress);
		void onNewItemFound(Item item);
		
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		items = new ArrayList<Item>();
		for (Feed feed : feeds) {
			updateUI(feed, null, null);
			List<Item> scrapedItems = Scraper.itemsFromFeed(feed);
			int i = 0;
			for (Item item : scrapedItems) {
				sendItemToUI(item);
				if(item.date == null || item.date.after(dateLimit)){
					updateUI(feed, scrapedItems, item);
					Scraper.populate(item);
				}
				if(item.date == null || item.date.after(dateLimit)){
					sendItemToUI(item);
					i++;
				}
				if(i>=itemLimitPerFeed)
					break;
			}
		}
		return null;
	}
	
	private void updateUI(Feed feed, List<Item> scrapedItems, Item potentialItem) {
		progressString = progressString(feed, scrapedItems, potentialItem);
		publishProgress();
	}
	
	private void sendItemToUI(Item potentialItem) {
		progressString = null;
		publishProgress(potentialItem);
	}
	@Override
	protected void onProgressUpdate(Item... items) {
		if(progressString != null)
			uiInterface.onProgressUpdate(progressString);
		for(Item item : items){
			uiInterface.onNewItemFound(new Item(item));
		}
		progressString = null;
	}

	@Override
	protected void onPostExecute(Void result) {

		uiInterface.onProgressUpdate("completed");

		/*
		 * Collections.sort(items, new Comparator<Item>() {
		 * 
		 * @Override public int compare(Item item1, Item item2) { if (item2.date
		 * == null && item1.date == null) { return 0; } else if (item2.date ==
		 * null) { return 1; } else if (item1.date == null) { return -1; } else
		 * { return item2.date.compareTo(item1.date); } } });
		 */
	}



	private String progressString(Feed feed, List<Item> scrapedItems, Item potentialItem) {
		String progressString = "[" + Integer.toString(items.size()) + "]\n" + "Feed "
				+ Integer.toString(feeds.indexOf(feed) + 1) + " of " + Integer.toString(feeds.size()) + ": "
				+ feed.name;
		if (scrapedItems != null && potentialItem != null) {
			progressString = progressString + "\n" + "Item "
					+ Integer.toString(scrapedItems.indexOf(potentialItem) + 1) + " of "
					+ Integer.toString(scrapedItems.size());
		}
		return progressString;
	}
}
