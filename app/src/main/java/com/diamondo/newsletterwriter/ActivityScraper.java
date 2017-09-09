package com.diamondo.newsletterwriter;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import com.diamondo.newsletterwriter.R;
import com.diamondo.newsletterwriter.R.id;
import com.diamondo.newsletterwriter.R.layout;
import com.diamondo.newsletterwriter.R.menu;
import com.diamondo.newsletterwriter.model.Feed;
import com.diamondo.newsletterwriter.model.Item;
import com.diamondo.newsletterwriter.services.DBInterface;
import com.diamondo.newsletterwriter.services.ScrapingThread;
import com.diamondo.newsletterwriter.tools.DBHelper;
import com.diamondo.newsletterwriter.tools.AndroidFactory;
import com.diamondo.newsletterwriter.tools.Tools;
import com.diamondo.newsletterwriter.ui.ItemListAdapter;
import com.diamondo.newsletterwriter.ui.SimpleDialog;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ActivityScraper extends ActionBarActivity implements View.OnClickListener, OnItemClickListener,
		OnItemSelectedListener, ItemListAdapter.ItemEventsInferface, ScrapingThread.UIInterface, SimpleDialog.DialogListener{

	private DBHelper myDBHelper;
	private DBInterface databaseInterface;

	private Button runButton, sendButton;
	private Button refreshDBButton;
	private Spinner feedMenuSpinner;
	private Spinner itemLimitMenuSpinner;
	private Spinner dateLimitMenuSpinner;
	private TextView progressTextView;
	private ListView itemListView;
	private List<Feed> feeds;
	private ArrayAdapter<String> feedMenuAdapter;
	private ArrayAdapter<String> itemLimitMenuAdapter;
	private ArrayAdapter<String> dateLimitMenuAdapter;
	private ItemListAdapter itemListAdapter;
	
	private static String DATE_FORMAT = "EEE dd-MMM-yyyy HH.mm.ss";

	Date dateLimit = new Date(0);
	int itemLimitPerFeed = Integer.MAX_VALUE;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scraper);
		initialiseVariables();
		initialiseViews();
		initialiseMenus();
		initialiseListeners();
	}

	private void initialiseVariables() {
		myDBHelper = new DBHelper(this);
		databaseInterface = new DBInterface(myDBHelper);
		feeds = new ArrayList<Feed>();
	}

	private void initialiseViews() {
		runButton = (Button) findViewById(R.id.bFindItems);
		sendButton = (Button) findViewById(R.id.bSend);
		refreshDBButton = (Button) findViewById(R.id.bRefreshDB);
		feedMenuSpinner = (Spinner) findViewById(R.id.spFeedMenu);
		itemLimitMenuSpinner = (Spinner) findViewById(R.id.spItemLimitMenu);
		dateLimitMenuSpinner = (Spinner) findViewById(R.id.spDateLimitMenu);
		progressTextView = (TextView) findViewById(R.id.tvProgress);
		itemListView = (ListView) findViewById(R.id.listView1);
	}

	private void initialiseMenus() {
		
		// Feed Menu Spinner Adapter
		List<String> options = new ArrayList<String>();
		options.add("- All Feeds -");
		databaseInterface.open();
			options.addAll(Tools.getNameListFromFeeds(databaseInterface.getAllFeeds()));
		databaseInterface.close();
		feedMenuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
		feedMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		feedMenuSpinner.setAdapter(feedMenuAdapter);

		// Item Limit Spinner Adapter
		options = new ArrayList<String>();
		options.add("No Item Limit");
		options.add("1");
		options.add("2");
		options.add("5");
		options.add("10");
		itemLimitMenuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
		itemLimitMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		itemLimitMenuSpinner.setAdapter(itemLimitMenuAdapter);
		
		// Date Limit Spinner Adapter
		options = new ArrayList<String>();
		options.add("No Age Limit");
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		options.add(Tools.dateToString(new Date(cal.getTimeInMillis() - (1000 * 60 * 60 * 24) * 1), DATE_FORMAT));
		options.add(Tools.dateToString(new Date(cal.getTimeInMillis() - (1000 * 60 * 60 * 24) * 2), DATE_FORMAT));
		options.add(Tools.dateToString(new Date(cal.getTimeInMillis() - (1000 * 60 * 60 * 24) * 3), DATE_FORMAT));
		options.add(Tools.dateToString(new Date(cal.getTimeInMillis() - (1000 * 60 * 60 * 24) * 7), DATE_FORMAT));
		dateLimitMenuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, options);
		dateLimitMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		dateLimitMenuSpinner.setAdapter(dateLimitMenuAdapter);
		

		// Item ListView Adapter
		itemListAdapter = new ItemListAdapter(this, new ArrayList<Item>(), itemListView);
		itemListView.setAdapter(itemListAdapter);
	}

	private void initialiseListeners() {
		runButton.setOnClickListener(this);
		sendButton.setOnClickListener(this);
		refreshDBButton.setOnClickListener(this);
		itemListView.setOnItemClickListener(this);
		feedMenuSpinner.setOnItemSelectedListener(this);
		dateLimitMenuSpinner.setOnItemSelectedListener(this);
		itemLimitMenuSpinner.setOnItemSelectedListener(this);
	}

	private void loadAllFeeds() {
		feeds.clear();
		databaseInterface.open();
		feeds.addAll(databaseInterface.getAllFeeds());
		databaseInterface.close();
		Toast.makeText(this, feeds.size() + " feeds loaded", Toast.LENGTH_SHORT).show();
	}

	private void loadFeedsOfName(String name) {
		feeds.clear();
		databaseInterface.open();
		feeds.addAll(databaseInterface.getFeedsOfName(name));
		databaseInterface.close();
		Toast.makeText(this, feeds.size() + " feed(s) with name '" + name + "' loaded", Toast.LENGTH_SHORT).show();
	}

	private void refreshDatabase() {
		this.deleteDatabase(DBHelper.DB_NAME);
		try {
			myDBHelper.copyDataBase();
			Toast.makeText(this, "DB Refreshed", Toast.LENGTH_SHORT).show();
		} catch (IOException ioe) {
		}
		initialiseMenus();
		loadAllFeeds();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.scraper, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {

//		case R.id.bTest:
//			new ScrapingThread(this, feeds, true).execute();
//			break;

		case R.id.bFindItems:
			new ScrapingThread(this, feeds, dateLimit, itemLimitPerFeed).execute();
			break;

		case R.id.bSend:
			// setProgressTextView("0");
			// String totalForEmail = "";
			// for(Item item : items){
			// // if(item.selected == true){
			// // item.selected = false;
			// // totalForEmail += "\n\n" + item.getSendParagraph();
			// // }
			// }
			// itemListAdapter.notifyDataSetChanged();
			//
			// startActivity(AndroidFactory.emailIntent("diamond.oliver@gmail.com",
			// "Weekly Draft", totalForEmail));
			//
			break;

//		case R.id.bLoadAllFeeds:
//			loadAllFeeds();
//			break;

		case R.id.bRefreshDB:
			SimpleDialog dialog = new SimpleDialog(R.id.bRefreshDB,
					"Refresh Database",
					"Do you actually want to refresh the Database?",
					this);
			dialog.show(getFragmentManager(), "hello");
//			refreshDatabase();
			break;

		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

		String selectedOption;
		switch (parent.getId()) {
			
			case R.id.spFeedMenu: 
	
				selectedOption = feedMenuSpinner.getItemAtPosition(position).toString();
	
				if (selectedOption == "- All Feeds -") {
					loadAllFeeds();
				} else {
					loadFeedsOfName(selectedOption);
				}
				break;
				
			case R.id.spItemLimitMenu:
				selectedOption = itemLimitMenuSpinner.getItemAtPosition(position).toString();
				
				if (selectedOption == "No Item Limit") {
					itemLimitPerFeed = Integer.MAX_VALUE;
				} else {
					itemLimitPerFeed = Integer.parseInt(selectedOption);
				}
				break;
				
			case R.id.spDateLimitMenu:
				selectedOption = dateLimitMenuSpinner.getItemAtPosition(position).toString();
				
				if (selectedOption == "No Age Limit") {
					dateLimit = new Date(0);
				} else {
					dateLimit = Tools.parseStringToDate(selectedOption, DATE_FORMAT);
				}
				Toast.makeText(this, "Date Limit set:\n" + Tools.dateToString(dateLimit, DATE_FORMAT), Toast.LENGTH_LONG).show();
				break;
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	@Override
	public void onProgressUpdate(String progress) {
		progressTextView.setText(progress);

	}
	
	@Override
	public void onNewItemFound(Item item) {
		itemListAdapter.add(item);
		itemListAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemSwipeLeft(Item item) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean onItemMarkedPersonal(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onItemMarkedWork(Item item) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onItemClicked(Item item) {
		Toast.makeText(this, "Opening Item...", Toast.LENGTH_SHORT).show();
		startActivity(AndroidFactory.browserIntent(item.url));
		return false;
	}

	@Override
	public void onDialogPositiveClick(int commandReference) {

		switch (commandReference) {
			case R.id.bRefreshDB:
				refreshDatabase();
				break;
		}

		
	}

}
