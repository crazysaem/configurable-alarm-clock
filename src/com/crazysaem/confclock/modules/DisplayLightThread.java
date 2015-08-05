package com.crazysaem.confclock.modules;

import android.os.Handler;
import android.widget.LinearLayout;

public class DisplayLightThread implements Runnable {
	private LinearLayout layout;
	private DisplayLight module;
	private int rgbmax[];
	private Handler handler;
	private int i=0, delay, addWait;
	
	public DisplayLightThread(DisplayLight module, LinearLayout layout, Handler handler, int startup, int duration, int rgbmax[]) {
		this.layout = layout;
		this.module = module;
		this.rgbmax = rgbmax;
		this.delay = startup*100;
		this.handler = handler;
		this.addWait = duration;
	}

	@Override
	public void run() {		
		i++;
    	if(i<=10) {
    		handler.postDelayed(this, delay);
    		int r=(int)(rgbmax[0]*(i/10f));
    		int g=(int)(rgbmax[1]*(i/10f));
    		int b=(int)(rgbmax[2]*(i/10f));  
    		layout.setBackgroundColor(0xff000000 + r * 0x10000 + g * 0x100 + b);    	    		
    	} 
    	if(i==11)
    	{
    		handler.postDelayed(this, addWait*1000);
    	}
    	if(i>=12) {
    		module.killMeNow();
    	}    	
	}

}
