package com.crazysaem.confclock.modules;

import com.crazysaem.confclock.R;
import com.crazysaem.confclock.broadcasts.BroadCastRecieverExHelper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class DisplayLight extends Activity {
	private LinearLayout layout;	
	//TODO:Use color in this function:
    int duration=-1, startup=-1, color=-1, type=-1;
    int rgbmax[] = new int[3];
    int clockid=-1;
    boolean isTest=false;
    private int listposition=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//Wake the Phone up, if it is sleeping: (crashes the phone, DO NOT USE, or find solution)		
        //PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        //wl.acquire();
		//[...]
		//wl.release();
				
		//Fullscreen:
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//Show Activity over Keyguard/LockScreen:
		int flags = WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
        getWindow().addFlags(flags);
        
        setContentView(R.layout.displaylight);
        
        layout  = (LinearLayout) this.findViewById(R.id.lDisplayLight_root);	        
	    layout.setBackgroundColor(0xff000000 + 0 * 0x10000 + 0 * 0x100 + 0);
        
        Bundle extras = getIntent().getExtras();
		if (extras!=null) {
			duration = extras.getInt("duration");
			startup = extras.getInt("startup");
			color = extras.getInt("color");			
			clockid = extras.getInt("clockid");		
			isTest = extras.getBoolean("isTest");	
			listposition = extras.getInt("listposition");	
		} else {this.finish();return;};        
        
		//Manually Change the Brightness:
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 100 / 100.0f;
        getWindow().setAttributes(lp);
        
        setColor(color);
        
        Handler handler = new Handler();
        Runnable runnable = new DisplayLightThread(this, layout, handler, startup, duration, rgbmax);
        handler.postDelayed(runnable, 1);

        if(isTest) {
			BroadCastRecieverExHelper.CreateBroadCastbyClockid(this, clockid, listposition, false, true, 1000*startup + duration*1000);
		}
	}

	private void setColor(int color) {
		//"White", "Red", "Green", "Blue", "Orange", "Yellow", "Pink"
		switch(color) {		
			case 0:
				rgbmax[0]=rgbmax[1]=rgbmax[2]=255;
			break;
			case 1:
				rgbmax[0]=255;
				rgbmax[1]=rgbmax[2]=0;
			break;
			case 2:
				rgbmax[1]=255;
				rgbmax[0]=rgbmax[2]=0;
			break;
			case 3:
				rgbmax[2]=255;
				rgbmax[0]=rgbmax[1]=0;
			break;
			case 4:				
				rgbmax[0]=255;
				rgbmax[1]=127;
				rgbmax[2]=0;
			break;
			case 5:
				rgbmax[0]=255;
				rgbmax[1]=255;
				rgbmax[2]=0;
			break;
			case 6:
				rgbmax[0]=255;
				rgbmax[1]=0;
				rgbmax[2]=255;
			break;
		}
	}
	
	public void killMeNow() {		
		this.finish();		
		//TODO: Make sure the display is turned off again, if it was off.
	}
	
	//Asynctask below will stay there, as an example, for the time being
	
    //DisplayLightAsyncTask displayLightThread = new DisplayLightAsyncTask(duration, startup, color, this);
    //Thread thread = new Thread(displayLightThread);
    //thread.start();    
    //new ChangeBackgroundColorOverTime().execute(this, null, this);
	/*
	private class ChangeBackgroundColorOverTime extends AsyncTask<DisplayLight, Integer, DisplayLight> {
		@Override
		protected DisplayLight doInBackground(DisplayLight... arg0) {
			
			int r = 0,g = 0,b = 0;
	        
	        for(int i=0;i<=100;i++) {        
	        	//r=(int)(i*2.55f);
	        	//g=(int)(i*2.55f);
	        	//b=(int)(i*2.55f);
	        	r=(int)(rgbmax[0]*(i/100f));
	        	g=(int)(rgbmax[1]*(i/100f));
	        	b=(int)(rgbmax[2]*(i/100f));
	        	publishProgress(r, g, b);
	        	//SystemClock.sleep(10*startup);
	        	try {
	        		Thread.sleep(10*startup);
				} catch (InterruptedException e) {e.printStackTrace();}
	        }
	        
			return arg0[0];
		}
		
		@Override
		protected void onProgressUpdate(Integer... rgb) {
			super.onProgressUpdate(rgb);
			if(rgb==null) {return;}
			layout.setBackgroundColor(0xff000000 + rgb[0] * 0x10000 + rgb[1] * 0x100 + rgb[2]);
		}	
		
		@Override
		protected void onPostExecute(DisplayLight displaylight) {
			super.onPostExecute(displaylight);
			
			DialogHelper.ShowToast(displaylight, "Finished Startup time");
			
			int wait=duration-startup;
	        
	        if(wait<0) {displaylight.killMeNow();}
	        
	        //Display the light for the desired amount of time
	        //SystemClock.sleep(1000*wait);
	        try {
				Thread.sleep(1000*wait);
			} catch (InterruptedException e) {e.printStackTrace();}
	        
	        DialogHelper.ShowToast(displaylight, "Finished Module");
			
			displaylight.killMeNow();
		}
	 }*/
}
