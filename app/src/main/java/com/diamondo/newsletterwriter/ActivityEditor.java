package com.diamondo.newsletterwriter;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.diamondo.newsletterwriter.model.Item;
import com.diamondo.newsletterwriter.services.DBInterface;
import com.diamondo.newsletterwriter.tools.DBHelper;

import java.util.List;

public class ActivityEditor extends ActionBarActivity implements OnClickListener {

	Button backButton, skipButton, acceptButton, declineButton;
	TextView feedNameView, itemNumberView, urlView, dateView;
	EditText titleEditText, paragraphEditText;
	DBHelper myDBHelper;
	List<Item> items;
	int currentItemNumber;
	DBInterface databaseInterface;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		initialiseVariables();
		initialiseListeners();
	}

	private void initialiseVariables() {
		currentItemNumber = 0;
		
		myDBHelper = new DBHelper(this);
		
		backButton = (Button) findViewById(R.id.bBack);
		skipButton = (Button) findViewById(R.id.bSkip);
		declineButton = (Button) findViewById(R.id.bApplyDecline);
		acceptButton = (Button) findViewById(R.id.bApplyAccept);
		
		feedNameView = (TextView) findViewById(R.id.tvFeedName);
		
		urlView = (TextView) findViewById(R.id.tvURL);
		dateView = (TextView) findViewById(R.id.tvDate);
		itemNumberView = (TextView) findViewById(R.id.tvItemNumber);
		titleEditText = (EditText) findViewById(R.id.etTitle);
		paragraphEditText = (EditText) findViewById(R.id.etParagraph);
		
		databaseInterface = new DBInterface(myDBHelper);

		databaseInterface.open();
		items = databaseInterface.getUnreadItems();
		databaseInterface.close();
		
		setViews(currentItemNumber);
	}
	
	private void setViews(int itemNumber) {
		if(items.isEmpty()){
			feedNameView.setText("No Items");
		} else {
			itemNumberView.setText(Integer.toString(itemNumber+1) + " of [" + Integer.toString(items.size()) + "]");
			feedNameView.setText(items.get(itemNumber).feed.name);
			urlView.setText(items.get(itemNumber).url);
			dateView.setText(items.get(itemNumber).getDisplayDate());
			titleEditText.setText(items.get(itemNumber).title);
			paragraphEditText.setText(items.get(itemNumber).paragraph);
		}
	}
	
	private void initialiseListeners() {
		backButton.setOnClickListener(this);
		skipButton.setOnClickListener(this);
		declineButton.setOnClickListener(this);
		acceptButton.setOnClickListener(this);
	}
	
	private void nextItem() {
		currentItemNumber++;
		if(currentItemNumber >= items.size()){
			currentItemNumber--;
		}
		setViews(currentItemNumber);
	}

	private void writeChanges() {
		if(items.isEmpty()){
			Toast.makeText(this, "No Items", Toast.LENGTH_SHORT).show();
		} else {
			databaseInterface.open();
			items.set(currentItemNumber, databaseInterface.changeItem(
					items.get(currentItemNumber).id(),
					titleEditText.getText().toString(),
					paragraphEditText.getText().toString(),
					"read"));
			databaseInterface.close();
		}		
	}

	private void previousItem() {
		currentItemNumber--;
		if(currentItemNumber < 0){
			currentItemNumber = 0;
		}
		setViews(currentItemNumber);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.editor, menu);
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
		switch (view.getId()){
			case R.id.bBack:
				previousItem();
				break;
			case R.id.bSkip:
				nextItem();
				break;
			case R.id.bApplyDecline:
				writeChanges();
				nextItem();
				break;
			case R.id.bApplyAccept:
				writeChanges();
				nextItem();
				break;
		}
	}

}