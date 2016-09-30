package com.diamondo.newsletterwriter.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.diamondo.newsletterwriter.model.Feed;
import com.diamondo.newsletterwriter.model.Item;
import com.diamondo.newsletterwriter.tools.DBHelper;
import com.diamondo.newsletterwriter.tools.Tools;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBInterface {

	private static final String TABLE_FEEDS = "feeds";

	private static final String FEEDS_COLUMN_ID = "_id";
	private static final String FEEDS_COLUMN_NAME = "name";
	private static final String FEEDS_COLUMN_URL = "URL";
	private static final String FEEDS_COLUMN_CATEGORY = "category";
	private static final String FEEDS_COLUMN_TAG = "tag";
	private static final String FEEDS_COLUMN_TYPE = "type";
	private static final String FEEDS_COLUMN_PAGE2URL = "page2URL";
	private static final String FEEDS_COLUMN_FEEDITEMSELECTOR = "feedItemSelector";
	private static final String FEEDS_COLUMN_FEEDURLSELECTOR0 = "feedURLSelector0";
	private static final String FEEDS_COLUMN_FEEDURLATTRBUTE0 = "feedURLAttrbute0";
	private static final String FEEDS_COLUMN_FEEDDATESELECTOR0 = "feedDateSelector0";
	private static final String FEEDS_COLUMN_FEEDDATEATTRBUTE0 = "feedDateAttrbute0";
	private static final String FEEDS_COLUMN_FEEDDATEFORMAT0 = "feedDateFormat0";
	private static final String FEEDS_COLUMN_ITEMPARAGRAPHSELECTOR0 = "itemParagraphSelector0";
	private static final String FEEDS_COLUMN_ITEMPARAGRAPHATTRIBUTE0 = "itemParagraphAttribute0";
	private static final String FEEDS_COLUMN_ITEMPARAGRAPHSELECTOR1 = "itemParagraphSelector1";
	private static final String FEEDS_COLUMN_ITEMPARAGRAPHATTRIBUTE1 = "itemParagraphAttribute1";
	private static final String FEEDS_COLUMN_ITEMDATESELECTOR0 = "itemDateSelector0";
	private static final String FEEDS_COLUMN_ITEMDATEATTRIBUTE0 = "itemDateAttribute0";
	private static final String FEEDS_COLUMN_ITEMDATEFORMAT0 = "itemDateFormat0";
	private static final String[] FEEDS_ALLCOLUMNS = {FEEDS_COLUMN_ID, FEEDS_COLUMN_NAME, FEEDS_COLUMN_URL, FEEDS_COLUMN_CATEGORY,
			FEEDS_COLUMN_TAG, FEEDS_COLUMN_TYPE, FEEDS_COLUMN_PAGE2URL, FEEDS_COLUMN_FEEDITEMSELECTOR,
			FEEDS_COLUMN_FEEDURLSELECTOR0, FEEDS_COLUMN_FEEDURLATTRBUTE0,
			FEEDS_COLUMN_FEEDDATESELECTOR0, FEEDS_COLUMN_FEEDDATEATTRBUTE0,
			FEEDS_COLUMN_FEEDDATEFORMAT0, FEEDS_COLUMN_ITEMPARAGRAPHSELECTOR0,
			FEEDS_COLUMN_ITEMPARAGRAPHATTRIBUTE0, FEEDS_COLUMN_ITEMPARAGRAPHSELECTOR1,
			FEEDS_COLUMN_ITEMPARAGRAPHATTRIBUTE1, FEEDS_COLUMN_ITEMDATESELECTOR0,
			FEEDS_COLUMN_ITEMDATEATTRIBUTE0, FEEDS_COLUMN_ITEMDATEFORMAT0 };
//	private static final String ITEMS_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS " + TABLE_ITEMS + "( " + ITEMS_COLUMN_ID
//			+ " integer primary key autoincrement, " + ITEMS_COLUMN_TITLE + " TEXT, " + ITEMS_COLUMN_URL + " TEXT, "
//			+ ITEMS_COLUMN_PARAGRAPH + " TEXT, " + ITEMS_COLUMN_DATE + " TEXT, " + ITEMS_COLUMN_FEEDID + " TEXT " + ");";

	private static final String TABLE_ITEMS = "items";

	private static final String ITEMS_COLUMN_ID = "_id";
	private static final String ITEMS_COLUMN_TITLE = "title";
	private static final String ITEMS_COLUMN_URL = "URL";
	private static final String ITEMS_COLUMN_PARAGRAPH = "paragraph";
	private static final String ITEMS_COLUMN_DATE = "date";
	private static final String ITEMS_COLUMN_FEEDID = "feedID";
	private static final String ITEMS_COLUMN_READ = "read";
	private static final String[] ITEMS_ALLCOLUMNS = { ITEMS_COLUMN_ID, ITEMS_COLUMN_TITLE, ITEMS_COLUMN_URL, ITEMS_COLUMN_PARAGRAPH, ITEMS_COLUMN_DATE, ITEMS_COLUMN_FEEDID, ITEMS_COLUMN_READ};
	
	
	private static final String DATE_FORMAT = "epoch";


	private SQLiteDatabase database;
	private DBHelper dbHelper;
	

	public DBInterface(DBHelper passedDBHelper) {
		this.dbHelper = passedDBHelper;
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		database.execSQL(Tools.createTableSQLStatement(TABLE_FEEDS, FEEDS_ALLCOLUMNS));
		database.execSQL(Tools.createTableSQLStatement(TABLE_ITEMS, ITEMS_ALLCOLUMNS));
	}

	public void close() {
		dbHelper.close();
	}

	public Item writeNewItem(Item itemForWriting) {
		ContentValues values = new ContentValues();
		values.put(ITEMS_COLUMN_TITLE, itemForWriting.title);
		values.put(ITEMS_COLUMN_URL, itemForWriting.url);
		values.put(ITEMS_COLUMN_PARAGRAPH, itemForWriting.paragraph);
		values.put(ITEMS_COLUMN_DATE, Tools.dateToString(itemForWriting.date, DATE_FORMAT));
		values.put(ITEMS_COLUMN_FEEDID, itemForWriting.feed.id);
		values.put(ITEMS_COLUMN_READ, itemForWriting.read);
		
		long insertId = database.insert(TABLE_ITEMS, null, values);
		Cursor cursor = database.query(TABLE_ITEMS, ITEMS_ALLCOLUMNS, ITEMS_COLUMN_ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Item writtenItem = cursorToItemList(cursor).get(0);
		cursor.close();
		return writtenItem;
	}
	
	public Item changeItem(int id, String title, String paragraph, String read) {
		
		ContentValues values = new ContentValues();
		values.put(ITEMS_COLUMN_TITLE, title);
		values.put(ITEMS_COLUMN_PARAGRAPH, paragraph);
		values.put(ITEMS_COLUMN_READ, read);
		String[] whereArgs = { Integer.toString(id) };
		
		database.update(TABLE_ITEMS, values, ITEMS_COLUMN_ID + " = ?", whereArgs);
		
		Cursor cursor = database.query(TABLE_ITEMS, ITEMS_ALLCOLUMNS, ITEMS_COLUMN_ID + " = ?", whereArgs, null, null, null);
		cursor.moveToFirst();
		Item writtenItem = cursorToItemList(cursor).get(0);
		cursor.close();
		
		return writtenItem;
	}
	
	public Item changeItem(Item item) {
		
		ContentValues values = new ContentValues();
		values.put(ITEMS_COLUMN_TITLE, item.title);
		values.put(ITEMS_COLUMN_PARAGRAPH, item.paragraph);
		values.put(ITEMS_COLUMN_READ, item.read);
		String[] whereArgs = { Integer.toString(item.id()) };
		
		database.update(TABLE_ITEMS, values, ITEMS_COLUMN_ID + " = ?", whereArgs);
		
		Cursor cursor = database.query(TABLE_ITEMS, ITEMS_ALLCOLUMNS, ITEMS_COLUMN_ID + " = ?", whereArgs, null, null, null);
		cursor.moveToFirst();
		Item writtenItem = cursorToItemList(cursor).get(0);
		cursor.close();
		
		return writtenItem;
	}
	
	public List<Item> getAllItems() {
		Cursor cursor = database.query(TABLE_ITEMS, ITEMS_ALLCOLUMNS, null, null, null, null, null);
		List<Item> loadedItems = cursorToItemList(cursor);
		cursor.close();
		
		return loadedItems;
	}
	
	public List<Item> getUnreadItems() {
		List<Item> loadedItems = new ArrayList<Item>();
		String whereClause = ITEMS_COLUMN_READ + " NOT LIKE ?";
		String[] whereArgs = { "%read%" };

		Cursor cursor = database.query(TABLE_ITEMS, ITEMS_ALLCOLUMNS, whereClause, whereArgs, null, null, null);
		loadedItems = cursorToItemList(cursor);
		cursor.close();
		
		return loadedItems;
	}
	
	private List<Item> cursorToItemList(Cursor cursor) {
		List<Item> items = new ArrayList<Item>();
		cursor.moveToFirst();
		List<Feed> feedList = getAllFeeds();
		while (!cursor.isAfterLast()) {
			Item item = cursorToItem(cursor, feedList);
			items.add(item);
			cursor.moveToNext();
		}
		
		return items;
	}

	private Item cursorToItem(Cursor cursor, List<Feed> feedList) {
		int id = cursor.getInt(0);
		String title = cursor.getString(1);
		String url = cursor.getString(2);
		String paragraph = cursor.getString(3);
		String dateAsString = cursor.getString(4);
		int feedID = cursor.getInt(5);
		String read = cursor.getString(6);

		Date date = Tools.parseStringToDate(dateAsString, DATE_FORMAT);

		for (Feed feed : feedList) {
			if (feed.id == feedID) {
				return new Item(id, url, date, title, paragraph, feed, read);
			}
		}
		
		return new Item(id, url, date, title, paragraph, null, read);
	}

	public String findTableNames() {
		Cursor cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
		String tables = "";
		if (cursor.moveToFirst()) {
			while (cursor.isAfterLast() == false) {
				tables = tables + cursor.getString(0) + "; ";
				cursor.moveToNext();
			}
		} else {
			tables = "NO Table Found";
		}
		return tables;
	}

	public List<Feed> getFeedsOfCategory(String category) {
		String whereClause = FEEDS_COLUMN_CATEGORY + " = ?";
		String[] whereArgs = {category};
		Cursor cursor = database.query(TABLE_FEEDS, FEEDS_ALLCOLUMNS, whereClause, whereArgs, null, null, null);
		List<Feed> loadedFeeds = cursorToFeedList(cursor);
		cursor.close();
		return loadedFeeds;
	}
	
	public List<Feed> getFeedsOfName(String name) {
		String whereClause = FEEDS_COLUMN_NAME + " = ?";
		String[] whereArgs = {name};
		Cursor cursor = database.query(TABLE_FEEDS, FEEDS_ALLCOLUMNS, whereClause, whereArgs, null, null, null);
		List<Feed> loadedFeeds = cursorToFeedList(cursor);
		cursor.close();
		return loadedFeeds;
	}
	
	public Feed getFeedOfID(int id) {
		String whereClause = FEEDS_COLUMN_ID + " = ?";
		String[] whereArgs = {Integer.toString(id)};
		Cursor cursor = database.query(TABLE_FEEDS, FEEDS_ALLCOLUMNS, whereClause, whereArgs, null, null, null);
		List<Feed> loadedFeeds = cursorToFeedList(cursor);
		cursor.close();
		return loadedFeeds.get(0);
	}

	public List<Feed> getAllFeeds() {
		Cursor cursor = database.query(TABLE_FEEDS, FEEDS_ALLCOLUMNS, null, null, null, null, null);
		List<Feed> loadedFeeds = cursorToFeedList(cursor);
		cursor.close();
		return loadedFeeds;
	}

	private List<Feed>  cursorToFeedList(Cursor cursor) {
		List<Feed> feeds = new ArrayList<Feed>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Feed feed = cursorToFeed(cursor);
			feeds.add(feed);
			cursor.moveToNext();
		}
		return feeds;
	}

	private Feed cursorToFeed(Cursor cursor) {
			int id = cursor.getInt(0);
			String name = cursor.getString(1);
			String url = cursor.getString(2);
			String category = cursor.getString(3);
			String tag = cursor.getString(4);
			String type = cursor.getString(5);
			String page2URL = cursor.getString(6);
			String feedItemSelector = cursor.getString(7);
			String feedURLSelector0 = cursor.getString(8);
			String feedURLAttrbute0 = cursor.getString(9);
			String feedDateSelector0 = cursor.getString(10);
			String feedDateAttrbute0 = cursor.getString(11);
			String feedDateFormat0 = cursor.getString(12);
			String itemParagraphSelector0 = cursor.getString(13);
			String itemParagraphAttribute0 = cursor.getString(14);
			String itemParagraphSelector1 = cursor.getString(15);
			String itemParagraphAttribute1 = cursor.getString(16);
			String itemDateSelector0 = cursor.getString(17);
			String itemDateAttribute0 = cursor.getString(18);
			String itemDateFormat0 = cursor.getString(19);
			
			Feed feed = new Feed(id, name, url, category, tag,
				type, page2URL, feedItemSelector,
				feedURLSelector0, feedURLAttrbute0,
				feedDateSelector0, feedDateAttrbute0,
				feedDateFormat0, itemParagraphSelector0,
				itemParagraphAttribute0, itemParagraphSelector1,
				itemParagraphAttribute1, itemDateSelector0,
				itemDateAttribute0, itemDateFormat0);
			
		return feed;
	}

	public boolean isDuplicateInDatabase(Item potentialItem) {
		int count = -1;
		Cursor cursor = null;
		if (potentialItem.url != null && potentialItem.url.length() > 0) {
			try {
				cursor = database.rawQuery("SELECT COUNT (*) FROM " + TABLE_ITEMS + " WHERE " + ITEMS_COLUMN_URL
						+ " = ?", new String[] { potentialItem.url });
				if (cursor.moveToFirst()) {
					count = cursor.getInt(0);
				}
				return count > 0;
			} finally {
				if(cursor != null){
					cursor.close();
				}
			}
		} else {
			return false;
		}
	}
}
