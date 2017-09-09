package com.diamondo.newsletterwriter;

import java.io.IOException;
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
import com.diamondo.newsletterwriter.ui.ItemListAdapter.ItemEventsInferface;

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

public class ActivityEditor2 extends ActionBarActivity implements
		View.OnClickListener, OnItemSelectedListener, ItemEventsInferface {
	
	
	DBInterface databaseInterface;
	
	LinearLayout Layout1;
	Button sendButton, markAsReadButton;
	Spinner feedMenuSpinner;
	TextView progressTextView;
	ListView itemListView;
	ArrayAdapter<String> feedMenuAdapter;
	ItemListAdapter itemListAdapter;

	List<Item> personalItems = new ArrayList<Item>();
	List<Item> workItems = new ArrayList<Item>();
	
	
	Date dateLimit = Tools.parseStringToDate("280420140000", "ddMMyyyyHHmm");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor2);
		initialiseVariables();
		initialiseViews();
		initialiseMenus();
		initialiseListeners();
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
	
	private void initialiseVariables() {
		DBHelper myDBHelper = new DBHelper(this);
		databaseInterface = new DBInterface(myDBHelper);
	}

	private void initialiseViews() {
		sendButton = (Button) findViewById(R.id.bSend);
		markAsReadButton = (Button) findViewById(R.id.bMarkAsRead);
		feedMenuSpinner = (Spinner) findViewById(R.id.spFeedMenu);
		progressTextView = (TextView) findViewById(R.id.tvProgress);
		itemListView = (ListView) findViewById(R.id.listView1);
	}

	private void initialiseMenus() {
		List<String> feedMenuOptions = new ArrayList<String>();
		feedMenuOptions.add("- All Feeds -");
		
		//Retrieve DB Values
		databaseInterface.open();
			feedMenuOptions.addAll(Tools.getNameListFromFeeds(databaseInterface.getAllFeeds()));
			itemListAdapter = new ItemListAdapter(this, databaseInterface.getUnreadItems(), itemListView);
		databaseInterface.close();
		
		//Item ListView Adapter
		itemListView.setAdapter(itemListAdapter);
		
		//Feed Menu Spinner Adapter
		feedMenuAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, feedMenuOptions);
		feedMenuAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		feedMenuSpinner.setAdapter(feedMenuAdapter);
	}

	private void initialiseListeners() {
		sendButton.setOnClickListener(this);
		markAsReadButton.setOnClickListener(this);
		feedMenuSpinner.setOnItemSelectedListener(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		
		case R.id.bMarkAsRead:
			databaseInterface.open();
				for(int i = 0; i < itemListAdapter.getCount(); i++){
					itemListAdapter.getItem(i).read = "read";
					databaseInterface.changeItem(itemListAdapter.getItem(i));
				}
			databaseInterface.close();
			Toast.makeText(this, "All Items Marked As Read", Toast.LENGTH_LONG).show();
			break;

		case R.id.bSend:
			String totalForEmail = "";
			for(Item sendingItem : workItems){
				totalForEmail += "\n\n" + sendingItem.getSendParagraph();
			}
			workItems.clear();
			startActivity(AndroidFactory.emailIntent("diamond.oliver@gmail.com",
					"Weekly Draft", totalForEmail));
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {

		switch (parent.getId()) {

		case R.id.spFeedMenu:
			String selectedOption = feedMenuSpinner.getItemAtPosition(position).toString();
			break;		
			
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

	}

	public void setProgressTextView(String text) {
		progressTextView.setText(text);
	}

	@Override
	public boolean onItemSwipeLeft(Item item) {
		item.read = "read";
		databaseInterface.open();
			databaseInterface.changeItem(item);
		databaseInterface.close();
		return true;
	}

	@Override
	public boolean onItemMarkedPersonal(Item item) {
		item.read = "read personal";
		databaseInterface.open();
			databaseInterface.changeItem(item);
		databaseInterface.close();
		personalItems.add(item);
		return true;
	}

	@Override
	public boolean onItemMarkedWork(Item item) {
		item.read = "read work";
		databaseInterface.open();
			databaseInterface.changeItem(item);
		databaseInterface.close();
		workItems.add(item);
		return true;
	}
	
	@Override
	public boolean onItemClicked(Item item) {
		Toast.makeText(this, "Opening Item...", Toast.LENGTH_SHORT).show();
		startActivity(AndroidFactory.browserIntent(item.url));
		return false;
	}

}