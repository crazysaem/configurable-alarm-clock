package com.crazysaem.confclock.modules;

import com.crazysaem.confclock.broadcasts.BroadCastRecieverExHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;

public class Vibration extends Service {
	int clockid=-1;
    boolean isTest=false;
    private int delay=2000;
    private int listposition=0;
	
	@Override
	public IBinder onBind(Intent arg0) {return null;}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {		
		int duration=0;	
		int repeats=0;	
		
		Bundle extras = intent.getExtras();
		if (extras!=null) {
			duration = extras.getInt("duration");
			repeats = extras.getInt("repeats");
			clockid = extras.getInt("clockid");		
			isTest = extras.getBoolean("isTest");	
			listposition = extras.getInt("listposition");		
		}
		
		Vibrator vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
		
		//Note: Services and Threads are not the same!
		//		a Service runs like an activity in the same main thread, only difference is, that no GUI-Elements are displayed
		//		so if you still want to do big tasks, use a thread, or an AsyncTask
		Handler handler = new Handler();
        Runnable runnable = new VibrationThread(vibrator, handler, duration, repeats, delay);
        handler.postDelayed(runnable, 1);
        
        int wait=-1;
        
        if(repeats>0) {
        	wait = (duration*100 + delay)*repeats;
        } else {
        	wait = duration*100;
        }
        
        if(isTest) {
			BroadCastRecieverExHelper.CreateBroadCastbyClockid(this, clockid, listposition, false, true, wait);
		}
        
		return super.onStartCommand(intent, flags, startId);
	}
}
