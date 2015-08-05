package com.crazysaem.confclock.broadcasts;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;

import com.crazysaem.confclock.R;
import com.crazysaem.confclock.sql.DBAdapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

public class BroadCastRecieverExHelper {
	
	public static void CreateBroadCastbyTimeAndDate(Context context, PendingIntent pendingIntent, int dayOfWeek, int hour, int minute, int second ) {
		Calendar c = new GregorianCalendar();
        c.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, 0);

        // Schedule the alarm!
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
	}
	
	public static void CancelBroadCastbyClockid(Context context, int clockid) {
		Intent intent = new Intent(context, BroadCastReceiverEx.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), clockid, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pendingIntent);
		pendingIntent.cancel();
		NotificationHelper.DisableNotification(context, clockid);
	}
	
	public static boolean ExistsBroadCastbyClockid(Context context, int clockid) {
		Intent intent = new Intent(context, BroadCastReceiverEx.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), clockid, intent, PendingIntent.FLAG_NO_CREATE);
		
		if((pendingIntent==null) || pendingIntent.equals(null)) {
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean CreateBroadCastbyClockid(Context context, int clockid, boolean isUpdate, boolean isTest) {
		return CreateBroadCastbyClockid(context, clockid, 0, isUpdate, isTest, -1);
	}
	
	public static boolean CreateBroadCastbyClockid(Context context, int clockid, boolean isUpdate) {
		return CreateBroadCastbyClockid(context, clockid, 0, isUpdate, false, -1);
	}
		
	public static boolean CreateBroadCastbyClockid(Context context, int clockid, int listposition, boolean isUpdate, boolean isTest, int testWaitTime) {
		DBAdapter dbadapter = new DBAdapter(context);
        dbadapter.open();
        
        boolean forceNextDay=false;

        Cursor cModules = dbadapter.fetchModuleByClockid(clockid);
        
        List<SortableCustomList> moduleTimes = new ArrayList<SortableCustomList>();  

        if(cModules!=null) {     
        	if((cModules.getCount()>=1) && (cModules.moveToFirst())) {        		
	        	do {       
	        		moduleTimes.add(new SortableCustomList(cModules.getInt(3), cModules.getInt(0)));
	        	} while(cModules.moveToNext());
        	}
        	cModules.close();
        }    
        
        Collections.sort(moduleTimes, Collections.reverseOrder());
        
        Cursor cClock = dbadapter.fetchClock(clockid);
        
        if(cClock.getInt(5)==-1) {return false;}
        
        int minutes = cClock.getInt(5) & 63;
		int hours = cClock.getInt(5) >> 6;
		
		String clockname = cClock.getString(1);
		
		//Java Notation: SUNDAY=1, MONDAY=2,..., SATURDAY=7
		//My Notation: 	 MONDAY=0, ..., SUNDAY=6
		Calendar currentdate = new GregorianCalendar();
		//currentdate.setMinimalDaysInFirstWeek(1);
		int today = currentdate.get(Calendar.DAY_OF_WEEK);
		int todaysweek = currentdate.get(Calendar.WEEK_OF_YEAR);
		int currentyear = currentdate.get(Calendar.YEAR);
		
		int wakeupdays = cClock.getInt(6);
		cClock.close();
		
		if((wakeupdays==-1) || (wakeupdays==0)) {return false;}
		//TODO: If no days are selected, (wakeupdays==0), fire the clock only once		
		
		int tempday = getNextDay(today, wakeupdays);
		
		int timeFirstModule=0;
        if(moduleTimes.size()>0) {
        	if(listposition>=moduleTimes.size()) {
        		listposition=0;
        		forceNextDay=true;
        	}        	
        	timeFirstModule = (Integer)moduleTimes.get(listposition).getSortBy();
    		timeFirstModule=timeFirstModule*60*1000;
        } else {
        	//ERROR: No Modules found
        	return false;
        }
        	
        Calendar c = createCalender(currentyear, todaysweek, tempday, hours, minutes);
        
        //Check whether the Execution time lies in the past
        //Or check, wether all modules are finished, if so, force the next day
		if((((c.getTimeInMillis() - timeFirstModule - currentdate.getTimeInMillis())<=0) || (forceNextDay)) && (listposition==0) && (!isTest)){
			//Check whether the next day lies in the same week..
			//WARNING: The query below has to use Java Notation! If the next day is a sunday, you have to increment the week! Doing it only on mondays will procduce infinite loops!
			if(getNextDay(today+1, wakeupdays)<=today) {
				//..If not Increment the Week
				//Also works if the WeekOfYear value exceeds the maximum
				todaysweek++;
			}
			//.. If yes, use the very next day
			tempday = getNextDay(today+1, wakeupdays);
		}
		
		c = createCalender(currentyear, todaysweek, tempday, hours, minutes);
		
		Intent intent = new Intent(context, BroadCastReceiverEx.class);
		//Send clockid and listposition to the BroadCast, so it can be reactivated.
		intent.putExtra("message", "Hello BroadCast from Module!");
		intent.putExtra("clockid", clockid);
		intent.putExtra("moduleid", (Integer)moduleTimes.get(listposition).getInformation());
		intent.putExtra("isTest", isTest);
		//testWaitTime=(Integer)moduleTimes.get(listposition+1).getSortBy()
		intent.putExtra("listposition", listposition+1);
		
		//TODO: Module Liste übertragen
		//TODO: Nächste Module ansagen
		//Oder die Module ID und die Clock ID übertrage, sodass der BroadCast sich das nächste Modul selbst aussuchen muss
		
		if(!isTest) {
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), clockid, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        	am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis() - timeFirstModule, pendingIntent);
        } else {
        	if(!forceNextDay) {
        	PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        	am.set(AlarmManager.RTC_WAKEUP, currentdate.getTimeInMillis() + testWaitTime, pendingIntent);
        	}
        }
        
        dbadapter.close();
        
        String text = "Alarm \""+clockname+"\" "+"enabled";
        if(isUpdate) {text = null;}
        
        //Set a notification for this enabled Clock:        
        if((listposition==0) && (!isTest)){
        	NotificationHelper.DisableNotification(context, clockid);
        	NotificationHelper.CreateNotification(context, clockid, R.drawable.icon, 
        			text, "Your! Alarm ("+clockname+")", idToWeekday(tempday)+", "+timeToString(hours)+":"+timeToString(minutes)
        			+" o'clock -"+(Integer)moduleTimes.get(listposition).getSortBy()+"m ("+calendarToString(c)+")");
        }
        
        return true;
	}
	
	private static String idToWeekday(int id) {		
		switch(id) {
			case 1:
				return "Sunday";				
			case 2:
				return "Monday";
			case 3:
				return "Tuesday";
			case 4:
				return "Wednesday";
			case 5:
				return "Thursday";
			case 6:
				return "Friday";
			case 7:
				return "Saturday";
			default:
				return null;
		}
	}
	
	private static String timeToString(int time) {
		String res = Integer.toString(time);
		if(res.length()==1) {res="0"+res;}
		return res;
	}
	
	private static String calendarToString(Calendar c) {
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		String res;
		
		res = day+"."+month+"."+year;
		
		return res;
	}
	
	private static Calendar createCalender(int year, int week, int day, int hours, int minutes) {
		Calendar c = new GregorianCalendar();
		//clear the Calender in order to set the week properly
		c.clear();
		//c.setMinimalDaysInFirstWeek(1);
		c.set(Calendar.YEAR, year);
		c.set(Calendar.WEEK_OF_YEAR, week);
        c.set(Calendar.DAY_OF_WEEK, day);
        c.set(Calendar.HOUR_OF_DAY, hours);
        c.set(Calendar.MINUTE, minutes);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);        
        
        return c;
	}
	
	private static int getNextDay(int today, int wakeupdays) {
		int tempday=-1;
		
		//Change Java Notation to my Notation:
		today = dayJavaToMyNotation(today);
		
		for(int i=0;i<=7;i++) {
			tempday = (today + i ) % 7;
			if((wakeupdays & DBAdapter.intPower(2, tempday))>=1) {
				break;
			}
			if(i==7) {tempday=-1;}
		}
		
		//Change to java notation if successful			
		tempday = dayMyNotationToJava(tempday);

		return tempday;
	}
	
	private static int dayJavaToMyNotation(int day) {
		//Change Java Notation to my Notation:
		//Java Notation: SUNDAY=1, MONDAY=2,..., SATURDAY=7
		//My Notation: 	 MONDAY=0, ..., SUNDAY=6
		int tempday = day - 2;
		if(tempday<0) {tempday=6;};
		return tempday;
	}
	
	private static int dayMyNotationToJava(int day) {
		//Change to java notation if successful	
		//Java Notation: SUNDAY=1, MONDAY=2,..., SATURDAY=7
		//My Notation: 	 MONDAY=0, ..., SUNDAY=6
		int tempday = day + 2;
		if(tempday>7) {tempday=1;};
		return tempday;
	}
}
