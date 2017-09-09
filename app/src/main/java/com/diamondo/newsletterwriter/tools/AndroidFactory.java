package com.diamondo.newsletterwriter.tools;

import java.util.Calendar;

import com.diamondo.newsletterwriter.ActivityEditor2;
import com.diamondo.newsletterwriter.R;
import com.diamondo.newsletterwriter.services.ScrapingService;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

public class AndroidFactory {
	
	public static Intent emailIntent(String email, String subject, String body){
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_EMAIL, email);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_TEXT, body);
		return intent;
	}
	
	public static Intent activityIntent(String activityname){
		Intent intent = new Intent("android.intent.action." + activityname);
		return intent;
	}
	
	public static Intent scrapingServiceIntent(Context context){
		return new Intent(context, ScrapingService.class);
	}
	
	public static Intent browserIntent(String url){
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		return intent;
	}
	
	public static void scrapingCompletedNotification(Context context) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle("Notification Test ")
			.setContentText("This is to test the functionality of my notification builder");
		NotificationManager notificationManager = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(02, builder.build());
	}
	
	public static void runNotification(Context context, String title, String text) {
		NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
			.setSmallIcon(R.drawable.ic_launcher)
			.setContentTitle(title)
			.setContentText(text);
		Intent intent = new Intent(context, ActivityEditor2.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
		builder.setContentIntent(pIntent);
		NotificationManager notificationManager = 
				(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(001, builder.build());
	}
	
	public static String setRepeatingScrapingSchedule(Context context, int intervalInMinutes) {
		AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		Intent intent = scrapingServiceIntent(context);
		PendingIntent pIntent = PendingIntent.getService(context, 0, intent, 0);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), intervalInMinutes*60*1000, pIntent);
		String dateFormat = context.getResources().getString(R.string.default_date_format);
		String description = "Schedule set. Every " + Integer.toString(intervalInMinutes) +" mins. Starting " + Tools.dateToString(cal.getTime(), dateFormat);
		return description;
	}


}
