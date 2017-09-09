package com.diamondo.newsletterwriter;


import java.util.Calendar;

import com.diamondo.newsletterwriter.R;
import com.diamondo.newsletterwriter.R.id;
import com.diamondo.newsletterwriter.R.layout;
import com.diamondo.newsletterwriter.R.menu;
import com.diamondo.newsletterwriter.model.Scraper;
import com.diamondo.newsletterwriter.services.ScrapingService;
import com.diamondo.newsletterwriter.tools.AndroidFactory;
import com.diamondo.newsletterwriter.tools.Tools;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class ActivityNavigator extends ActionBarActivity implements OnClickListener {

	Button openScraper, openEditor, openEditor2, startAlarm;
	int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigator);
		initialiseVariables();
		initialiseListeners();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.navigator, menu);
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
		startAlarm = (Button) findViewById(R.id.bStartAlarm);
		openScraper = (Button) findViewById(R.id.bOpenScraper);
		openEditor = (Button) findViewById(R.id.bOpenEditor);
		openEditor2 = (Button) findViewById(R.id.bOpenEditor2);
	}

	private void initialiseListeners() {
		startAlarm.setOnClickListener(this);
		openScraper.setOnClickListener(this);
		openEditor.setOnClickListener(this);
		openEditor2.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {

		switch (view.getId()) {
		case R.id.bStartAlarm:
			int intervalInMinutes = 60;
			String description = AndroidFactory.setRepeatingScrapingSchedule(this, intervalInMinutes);
			Toast.makeText(this, description, Toast.LENGTH_LONG).show();
			//startService(AndroidFactory.scrapingServiceIntent(this));
			
			break;

		case R.id.bOpenScraper:
			startActivity(AndroidFactory.activityIntent("SCRAPER"));
			break;
			
		case R.id.bOpenEditor:
			startActivity(AndroidFactory.activityIntent("EDITOR"));
			break;
			
		case R.id.bOpenEditor2:
			startActivity(AndroidFactory.activityIntent("EDITOR2"));
			break;
			
		}
		
	}


}
