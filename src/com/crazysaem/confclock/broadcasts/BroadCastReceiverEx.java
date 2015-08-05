package com.crazysaem.confclock.broadcasts;

import com.crazysaem.confclock.container.ModuleContainer;
import com.crazysaem.confclock.dialogs.DialogHelper;
import com.crazysaem.confclock.modules.DisplayLight;
import com.crazysaem.confclock.modules.TTS;
import com.crazysaem.confclock.modules.Vibration;
import com.crazysaem.confclock.sql.DBAdapter;
import com.crazysaem.confclock.sql.ModuleTypes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class BroadCastReceiverEx extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int clockid=-1;
		int moduleid=-1;
		int listposition=-1;
		boolean isTest=false;
		
		//Pull extras from intent:
		Bundle extras = intent.getExtras();
		if(extras!=null) {			
			clockid = extras.getInt("clockid");
			moduleid = extras.getInt("moduleid");
			listposition = extras.getInt("listposition");	
			isTest =  extras.getBoolean("isTest");	
		} else {
			DialogHelper.ShowToast(context, "An Erorr occured:\nNo Clockid found..");
		}
		
		//Get Module information from Database with moduleid:
		DBAdapter dbadapter = new DBAdapter(context);
        dbadapter.open();        
        ModuleContainer module = new ModuleContainer(dbadapter.fetchModuleByModuleid(moduleid), true);        
        dbadapter.close();
        
        switch(module.getType()) {
        	case ModuleTypes.DISPLAYLIGHT:
        		DialogHelper.ShowToast(context, "DisplayLight Module started");
        		startModuleDisplayLight(context, module, isTest, listposition);
        	break;
        	
        	case ModuleTypes.RADIO:
            break;
        	
        	case ModuleTypes.SOUND:
            break;
        	
        	case ModuleTypes.TEXTTOSPECH:
        		DialogHelper.ShowToast(context, "TTS Module started");
        		startTTS(context, module, isTest, listposition);
            break;
        	
        	case ModuleTypes.VIBRATION:
        		DialogHelper.ShowToast(context, "Vibration Module started");
        		startVibrate(context, module, isTest, listposition);
            break;
            
        	case ModuleTypes.AUDIOFILE:
            break;
        }
		
		//Activate next Module or the next occurrence
		if(!isTest) {
			BroadCastRecieverExHelper.CreateBroadCastbyClockid(context, clockid, listposition, false, false, -1);
		}
	}
	
	public static void startModuleDisplayLight(Context context, ModuleContainer module, boolean isTest, int listposition) {
		Intent i = new Intent(context, DisplayLight.class);
		i.putExtra("duration", module.getDuration());
		i.putExtra("startup", module.getValue());
		i.putExtra("color", module.getInt01());		
		i.putExtra("clockid", module.getClockid());		
		i.putExtra("isTest", isTest);
		i.putExtra("listposition", listposition);		
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);	
	}
	
	public static void startVibrate(Context context, ModuleContainer module, boolean isTest, int listposition) {
		Intent i = new Intent(context, Vibration.class);
		i.putExtra("duration", module.getDuration());
		i.putExtra("repeats", module.getValue());
		i.putExtra("clockid", module.getClockid());	
		i.putExtra("isTest", isTest);
		i.putExtra("listposition", listposition);	
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(i);
	}
	
	public static void startTTS(Context context, ModuleContainer module, boolean isTest, int listposition) {
		Intent i = new Intent(context, TTS.class);
		i.putExtra("text", module.getText01());
		i.putExtra("clockid", module.getClockid());	
		i.putExtra("isTest", isTest);
		i.putExtra("listposition", listposition);	
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startService(i);
	}
}
