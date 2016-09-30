package com.diamondo.newsletterwriter.tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;


import com.diamondo.newsletterwriter.model.Feed;
import com.diamondo.newsletterwriter.model.Item;

public class Tools {

	public static String dateToString(Date date, String dateFormat) {
		if(date != null){
		if (dateFormat.toLowerCase().contains("epoch")) {
			return Long.toString(date.getTime());
		} else {
			return new SimpleDateFormat(dateFormat).format(date);
		}
		}else{
			return null;
		}
	}

	public static Date parseStringToDate(String dateAsString, String dateFormat) {
		Date date = null;
		if (dateAsString != null && dateFormat != null && dateAsString.length() > 0 && dateFormat.length() > 0) {
			dateAsString = dateAsString.replace("th", "");
			dateAsString = dateAsString.replace("st", "");
			dateAsString = dateAsString.replace("nd", "");
			if (dateFormat.contains("epoch")) {
				int multiplier = 1;
				if (dateFormat.contains("epoch/1000"))
					multiplier = 1000;
				date = new Date(multiplier * Long.parseLong(dateAsString));
			} else {
				String dateSubString = dateAsString;
				while (date == null && dateSubString.length() > 4) {
					try {
						SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
						date = formatter.parse(dateSubString);
					} catch (Exception e) {
						e.printStackTrace();
					}
					dateSubString = dateSubString.substring(1, dateSubString.length());
				}
			}
		}
		return date;
	}

	public static int numberOfItemsWithSameUrl(Item item, List<Item> itemList) {
		int i = 0;
		for (Item loopingItem : itemList) {
			if (item.url == loopingItem.url) {
				i++;
			}
		}
		return i;
	}

	public static void removeDuplicatesAndBlanks(List<Item> itemList) {
		for (Iterator<Item> iterator = itemList.iterator(); iterator.hasNext();) {
			Item item = iterator.next();
			if (item.url == null || numberOfItemsWithSameUrl(item, itemList) > 1) {
				iterator.remove();
			}
		}
	}

	public static void removeOldItems(List<Item> itemList, Date dateLimit) {
		for (Iterator<Item> iterator = itemList.iterator(); iterator.hasNext();) {
			Item item = iterator.next();
			if (item.date != null) {
				if (item.date.before(dateLimit)) {
					iterator.remove();
				}
			}
		}
	}

	public static List<String> getNameListFromFeeds(List<Feed> feeds) {
		List<String> names = new ArrayList<String>();
		for (Feed feed : feeds) {
			names.add(feed.name);
		}
		return names;
	}

	public static String truncateString(String string, int limit) {
		if (string.length() > limit) {
			return string.substring(0, limit) + "...";
		} else {
			return string;
		}
	}

	public static String createTableSQLStatement(String tableName, String[] columnNames) {
		String sqlStatement = "CREATE TABLE IF NOT EXISTS " + tableName + "(  ";
		for (String columnName : columnNames) {
			if (columnName == "_id") {
				sqlStatement = sqlStatement + columnName + " integer primary key autoincrement, ";
			} else {
				sqlStatement = sqlStatement + columnName + " TEXT, ";
			}
		}
		return sqlStatement.substring(0, sqlStatement.length()-2) + ");";
		// ITEMS_COLUMN_ID
		// + " integer primary key autoincrement, " + ITEMS_COLUMN_TITLE +
		// " TEXT, " + ITEMS_COLUMN_URL + " TEXT, "
		// + ITEMS_COLUMN_PARAGRAPH + " TEXT, " + ITEMS_COLUMN_DATE + " TEXT, "
		// + ITEMS_COLUMN_FEEDID + " TEXT " + ");";
	}

	public static List<String> itemsToStringList(List<Item> items) {
		List<String> stringList = new ArrayList<String>();
		if (items.isEmpty()) {
			stringList.add("No items");
		} else {
			for (Item item : items) {
				stringList.add(item.getDisplayParagraph());
			}
		}
		return stringList;
	}
	
}
