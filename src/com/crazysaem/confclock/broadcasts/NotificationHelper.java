package com.crazysaem.confclock.broadcasts;

import com.crazysaem.confclock.ConfClockActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationHelper {
	public static void CreateNotification(Context context, int clockId, int iconId, String tickertext, String title, String message) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		CharSequence tickerText = tickertext;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(iconId, tickerText, when);
		
		Intent notificationIntent = new Intent(context, ConfClockActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		PendingIntent contentIntent = PendingIntent.getActivity(context, clockId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		notification.setLatestEventInfo(context, title, message, contentIntent);
		
		notification.flags |= Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
		
		mNotificationManager.notify(clockId, notification);
	}
	
	public static void DisableNotification(Context context, int clockId) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(clockId);
	}
	
	public static void DisableAllNotification(Context context) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancelAll();
	}
	
	public static boolean ExistsNotification(Context context, int clockid) {
		Intent notificationIntent = new Intent(context, ConfClockActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, clockid, notificationIntent, PendingIntent.FLAG_NO_CREATE);
		
		if((contentIntent==null) || contentIntent.equals(null)) {
			return false;
		} else {
			return true;
		}
	}
}
