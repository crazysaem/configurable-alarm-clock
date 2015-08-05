package com.crazysaem.confclock.modules;

import android.os.Handler;
import android.os.Vibrator;

public class VibrationThread implements Runnable {
	Vibrator vibrator;
	Handler handler;
	int duration;
	int repeats=0, count=0, delay=0;
	
	public VibrationThread(Vibrator vibrator, Handler handler, int duration, int repeats, int delay) {
		this.vibrator = vibrator;
		this.duration = duration;
		this.repeats = repeats;
		this.delay = delay;
		this.handler = handler;
	}

	@Override
	public void run() {
		if((repeats>0) && (repeats>count)) {
			vibrator.vibrate(duration*100);
			count++;
			handler.postDelayed(this, duration*100 + delay);
		} else {
			vibrator.vibrate(duration*100);
		}
	}
}
