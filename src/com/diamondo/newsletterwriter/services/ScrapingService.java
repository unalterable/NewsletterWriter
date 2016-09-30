package com.diamondo.newsletterwriter.services;

import java.util.List;

import com.diamondo.newsletterwriter.model.Feed;
import com.diamondo.newsletterwriter.model.Item;
import com.diamondo.newsletterwriter.model.Scraper;
import com.diamondo.newsletterwriter.tools.AndroidFactory;
import com.diamondo.newsletterwriter.tools.DBHelper;

import android.app.IntentService;
import android.content.Intent;


public class ScrapingService extends IntentService {

	DBHelper databaseHelper;
	DBInterface databaseInterface;
	int itemTotal = 0;
	
	public ScrapingService() {
		super("Scraping Service");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		connectToDatabase();
		for (Feed feed : allFeedsFromDatabase()) {
			//updateUI(feed);
			for (Item potentialItem : Scraper.itemsFromFeed(feed)) {
				//updateUI(feed);
				if(itemIsNotInDatabase(potentialItem)){
					Scraper.populate(potentialItem);
					writeItemToDatabase(potentialItem);
				}
			}
		}
		updateUI(null);
		
	}
	
	private void connectToDatabase() {
		databaseHelper = new DBHelper(this);
		databaseInterface = new DBInterface(databaseHelper);
	}
	
	private void writeItemToDatabase(Item potentialItem) {
		databaseInterface.open();	
			databaseInterface.writeNewItem(potentialItem);
		databaseInterface.close();
		itemTotal++;
	}

	private boolean itemIsNotInDatabase(Item potentialItem) {
		databaseInterface.open();		
			boolean itemIsInDatabase = databaseInterface.isDuplicateInDatabase(potentialItem);
		databaseInterface.close();
		return !itemIsInDatabase;
	}

	private void updateUI(Feed feed) {
		String title, text;
		if(feed==null) {
			title = "Newspaper Scraper Completed";
			text = "Scraped Items: " + itemTotal;
		} else {
			title = "Newspaper Scraper Running";
			text = "Scraped Items: " + itemTotal + " \n"
				+ "Currently Scraping: " + Integer.toString(feed.id) + ". " + feed.name;
		}
		AndroidFactory.runNotification(this, title, text);
	}

	private List<Feed> allFeedsFromDatabase() {
		List<Feed> feeds;
		databaseInterface.open();
			feeds = databaseInterface.getAllFeeds();
		databaseInterface.close();
		return feeds;
	}

}
