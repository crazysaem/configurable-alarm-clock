package com.crazysaem.confclock.modules;

import java.util.Locale;

import com.crazysaem.confclock.broadcasts.BroadCastRecieverExHelper;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

public class TTS extends Service implements OnInitListener, OnUtteranceCompletedListener {
	private TextToSpeech tts;	
	private String text;
	int clockid=-1;
    boolean isTest=false;
    private int listposition=0;
	
	@Override
	public IBinder onBind(Intent arg0) {return null;}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {				
		Bundle extras = intent.getExtras();
		if (extras!=null) {
			text = extras.getString("text");
			clockid = extras.getInt("clockid");		
			isTest = extras.getBoolean("isTest");	
			listposition = extras.getInt("listposition");	
		}
		
		tts = new TextToSpeech(this, this);
		
		return 0;
	}

	@Override
	public void onInit(int arg0) {
		if(tts==null) {return;}
		//%t = time
		//%c = celcius
		//%f = fahrenheit
		//%w = weatherdescription
		tts.setLanguage(Locale.ENGLISH);
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
		tts.setOnUtteranceCompletedListener(this);
	}

	@Override
	public void onUtteranceCompleted(String arg0) {
		if(isTest) {
			BroadCastRecieverExHelper.CreateBroadCastbyClockid(this, clockid, listposition, false, true, -1);
		}		
	}

}
