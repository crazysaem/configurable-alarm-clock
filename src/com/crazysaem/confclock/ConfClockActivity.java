package com.crazysaem.confclock;

import java.util.ArrayList;
import java.util.List;

import com.crazysaem.confclock.broadcasts.BroadCastRecieverExHelper;
import com.crazysaem.confclock.container.ClockContainer;
import com.crazysaem.confclock.dialogs.ListOfItemsCallBack;
import com.crazysaem.confclock.dialogs.DialogHelper;
import com.crazysaem.confclock.dialogs.ListOfItems;
import com.crazysaem.confclock.list.ArrayAdapterCallBack;
import com.crazysaem.confclock.list.ListViewEx;
import com.crazysaem.confclock.modules.TTSCheck;
import com.crazysaem.confclock.sql.DBAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This is the main Activity.
 * It lists all the clocks and enables the user to create new clocks
 * Edit/Delete/Copy/TryOut Existing Clocks
 * Set some settings
 * Start the White Noise
 * @author Samuel Schneider
 *
 */
public class ConfClockActivity extends Activity implements OnClickListener, ArrayAdapterCallBack, ListOfItemsCallBack {
	private View footer;
	private ListViewEx clocklist;
	private List<ClockContainer> clocks;
	private Button WhiteNoise, settings;
	private ListOfItems optionList;
	private final String[] options = {"Toggle", "Copy", "Edit", "Delete", "Try Out"};
	
    /**
     * Main Entry Point into the Application
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the Layout
        setContentView(R.layout.main);        
        //Init the ClockContainer-List (this will store all information about all the clocks)
        clocks = new ArrayList<ClockContainer>();    
        //Init the footer, which enables the user to create a new clock
        footer = getLayoutInflater().inflate(R.layout.listview_image_text, null);        
        ImageView icon = (ImageView) footer.findViewById(R.id.ivListviewImageText_Image01);        
        icon.setImageResource(R.drawable.add);        
        TextView text = (TextView) footer.findViewById(R.id.tvListviewImageText_Text01);
        text.setText("Add New Clock");        
        footer.setOnClickListener(this);
        //Init the list for the visible clock-entries
        clocklist = new ListViewEx((ListView) findViewById(R.id.lvAlarmClocks), this, this, null);
        clocklist.AddFooterView(footer);        
        clocklist.Init();
        //Init the Buttons and link the Listeners to this Class
        WhiteNoise = (Button) findViewById(R.id.bMain_WhiteNoise);
        WhiteNoise.setOnClickListener(this);        
        settings = (Button) findViewById(R.id.bMain_Settings);
        settings.setOnClickListener(this);
    }
    
    /**
     * The Onclick-Callback for the 2 Buttons, and the footer (Create new clock entry, at the bottom of the list)
     */
	@Override
	public void onClick(View v) {
		if(v==footer) {
			//Open up the Activty to create a new clock
			Intent i = new Intent(ConfClockActivity.this, CreateEditClock.class);
	        startActivity(i);	
		}
		if(v==settings) {
			//Open up the Settings Activity
			Intent i = new Intent(ConfClockActivity.this, Settings.class);
	        startActivity(i);	        
		}
		if(v==WhiteNoise) {
			//TODO: Make the test code below to production code
			//TODO: Also Include a Phone-Restart Reciever (if necessary, test before implementing!) to activate the modules after a restart again	
			TTSCheck check = new TTSCheck(this);
			/*
			List<String> items = TTSCheck.localesToString(TTSCheck.getTTSAvailableLanguages(this));
			String[] items_String = new String[items.size()];
			for(int i=0;i<items.size();i++) {
				items_String[i] = items.get(0);
			}
			ListOfItems test = new ListOfItems(this, this, 0, "TTS Languages", items_String, null, 0);
			test.show();*/
		}
	}
	
	/**
	 * The Onclick-Callback for each view in each clock entry of the list above the footer
	 * This is the singleclick callback
	 */
	@Override
	public void arrAdaptCallOnClick(View v, int position) {
		//If the Delete Button is pressed
		if(v.getId()==R.id.ivListviewImageTextImage_Image01) {
			deleteClock(position);
		}
		//If the Text is clicked
		if(v.getId()==R.id.tvListviewImageTextImage_Text01) {
			editClock(position);
		}
		//If the Activate/Deactivate Button is pressed
		if(v.getId()==R.id.ivListviewImageTextImage_Image02) {
			toggleClockState(v, position);
		}
	}
	
	/**
	 * Deletes a Clock within the Database and the visible listview based on the listview-position
	 * @param position - The Position of the Clock in the listview
	 */	
	private void deleteClock(int position) {
		//Cancel Alarm BroadCast from Clock:
		BroadCastRecieverExHelper.CancelBroadCastbyClockid(this, clocks.get(position).getId());
		//Delete Clock from list and database:
		DBAdapter dbadapter = new DBAdapter(this);
        dbadapter.open();
        dbadapter.deleteClock(clocks.get(position).getId());	 
        clocks.remove(position);        
		clocklist.DeleteItemString(position);
		dbadapter.close();
	}
	
	/**
	 * Starts the Edit-Clock-Activity
	 * @param position - The Position of the Clock in the listview
	 */
	private void editClock(int position) {
		Intent i = new Intent(ConfClockActivity.this, CreateEditClock.class);
		i.putExtra("ClockId", clocks.get(position).getId());
        startActivity(i);
	}
	
	private void toggleClockState(View v,int position) {
        //Check wether a BroadCast already exists / the alarm for the clock is already set
        if(BroadCastRecieverExHelper.ExistsBroadCastbyClockid(this, clocks.get(position).getId())) {
        	//If it is set ..
        	//.. show the button as inactive
			if(v!=null) {((ImageView)v).setImageResource(R.drawable.inactive);}
			//delete the alarm
			BroadCastRecieverExHelper.CancelBroadCastbyClockid(this, clocks.get(position).getId());
		} else {
			//If it is not set ..
			//.. try to set a new alarm
			if(BroadCastRecieverExHelper.CreateBroadCastbyClockid(this, clocks.get(position).getId(), false)) {
				//If setting the new alarm was successful, show the button as active
				if(v!=null) {((ImageView)v).setImageResource(R.drawable.active);}
			} else /*If it fails ..*/ {
				//.. show suggestions to the user
				DialogHelper.ShowSimpleDialog(this, "Alarm Clock could not be activated", "Try to:\n" +
						"- set the alarm time\n"+
						"- set the weekdays\n"+
						"- add a module");
			}
		}	
	}

	/**
	 * onResume Callback
	 * Gets called, when the Activity is first created
	 * and every time it is awakened from its sleep
	 */
	@Override
	protected void onResume() {
		//TODO: Restart AlarmClock-BroadCast if phone was rebooted
		super.onResume();
		resetClockList();    
	}
	
	/**
	 * Refreshes all clocks in the visible listview and the clock-container list
	 */
	private void resetClockList() {
		//First Remove all items from the visible list of Clocks
		clocklist.RemoveAllItems();
		//Then remove all items from the clock-container list
		clocks.clear();		
		//Open a new Database Connection
		DBAdapter dbadapter = new DBAdapter(this);
        dbadapter.open();
        dbadapter.cleanUp();        
        //Get all clocks from the DataBase into the cursor
		Cursor cursor = dbadapter.fetchClockall();
        if(cursor!=null) {     
        	if((cursor.getCount()>=1) && (cursor.moveToFirst())) {        		
	        	do {       
	        		//if clocks were found, add all of them into the visible and clock-container list
	        		clocks.add(new ClockContainer(cursor));
	        		clocklist.AddItemString(clocks.get(0).getName());	        		
	        	} while(cursor.moveToNext());
        	}
        	cursor.close();
        }                
        dbadapter.close();      
	}

	/**
	 * The Onclick-Callback for each view in each clock entry of the list above the footer
	 * This is the longclock callback
	 */
	@Override
	public void arrAdaptCallOnLongClick(View v, int position) {		
		//If the text was pressed
		if(v.getId()==R.id.tvListviewImageTextImage_Text01) {
			//Show the user a list of available options, to interact with the clock
			optionList = new ListOfItems(this, this, 0, "Options", options, null, position);
			optionList.show();
		}
	}

	/**
	 * CallBack from the ArrayAdapter
	 * Gets called when every time a new View in the listview is created and/or refreshed
	 */
	@Override
	public void arrAdaptGetViewCallBack(View view, int position) {		
		ImageView image02 = (ImageView) view.findViewById(R.id.ivListviewImageTextImage_Image02);		
		//Check wether a alarm is active and then set the enable/disable button state
		if((image02!=null)) {
			if(BroadCastRecieverExHelper.ExistsBroadCastbyClockid(this, clocks.get(position).getId())) {
				image02.setImageResource(R.drawable.active);	
				//TODO: Check whether Notification exists. If not, create one.
			} else {
				image02.setImageResource(R.drawable.inactive);	
			}		
		}
	}

	/**
	 * DialogCallBack for single choice lists
	 */
	@Override
	public void listOfItemsCallSingle(String dialogId, int value, int special) {
		//If the optionlist was finished:
		if(DialogHelper.CompareDialogs(dialogId, optionList)) {
			switch(value) {
				//Toggle the clock (enable or disable, depending on the previous state)
				case 0:
					toggleClockState(null, special);
					resetClockList();
				break;
				// Copy Clock:
				case 1:
					DBAdapter dbadapter = new DBAdapter(this);
			        dbadapter.open();
			        dbadapter.copyClock(clocks.get(special).getId());
			        dbadapter.close();
			        resetClockList();
				break;
				//Edit Clock
				case 2:
					editClock(special);
				break;
				//Delete Clock
				case 3:
					deleteClock(special);
				break;
				//Try the clock out
				case 4:
					BroadCastRecieverExHelper.CreateBroadCastbyClockid(this, clocks.get(special).getId(), false, true);
				break;
			}
		}
	}

	/**
	 * DialogCallBack for multiple choice lists
	 * Not used, but implemented because of the Interface
	 */
	@Override
	public void listOfItemsCallMultiple(String dialogId, int value, boolean clicked, int special) {}
}