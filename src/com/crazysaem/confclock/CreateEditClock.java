package com.crazysaem.confclock;

import java.util.ArrayList;
import java.util.List;

import com.crazysaem.confclock.broadcasts.BroadCastReceiverEx;
import com.crazysaem.confclock.broadcasts.BroadCastRecieverExHelper;
import com.crazysaem.confclock.container.ClockContainer;
import com.crazysaem.confclock.container.ModuleContainer;
import com.crazysaem.confclock.dialogs.DialogHelperCallBack;
import com.crazysaem.confclock.dialogs.ListOfItemsCallBack;
import com.crazysaem.confclock.dialogs.DialogHelper;
import com.crazysaem.confclock.dialogs.ListOfItems;
import com.crazysaem.confclock.dialogs.TimeDialog;
import com.crazysaem.confclock.list.ArrayAdapterCallBack;
import com.crazysaem.confclock.list.ArrayAdapterEx;
import com.crazysaem.confclock.list.ArrayAdapterExHelper;
import com.crazysaem.confclock.sql.DBAdapter;
import com.crazysaem.confclock.sql.ModuleTypes;

import android.app.ListActivity;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

public class CreateEditClock extends ListActivity implements OnClickListener, ListOfItemsCallBack, ArrayAdapterCallBack, OnTimeSetListener, DialogHelperCallBack {
	private ArrayAdapter<String> ItemArray;
	private View footerNewModule;
	private ListOfItems moduleList, weekdayList, optionList;
	private final String[] modules = {"Display Light", "Vibration", "Text to Speech"};
	private final String[] weekdays = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
	private final String[] options = {"Copy", "Edit", "Delete", "Try Out"};
	private DBAdapter dbadapter;
	private long clockid;
	private Button bOK, bAlarmTime, bWeekDays, bExtraOptions;
	private EditText eClockName;
	private String clockname;
	private int clocktype, clockwakeuptask, clockwakeuptaskcount, clockwakeuptime, clockwakeupdays;
	private List<ModuleContainer> modulesContainers;
	private TimeDialog timeDialog;
	private boolean firstOnResume=true;
	private ClockContainer clock=null;

	/**
	 * Call back, which gets called when this activity-instance is first created
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Init the custom ArrayAdapter which helps be in control of the Views within each listview-entry
		ArrayAdapterExHelper arrhelper = new ArrayAdapterExHelper(R.layout.listview_image_text, new int[] {R.id.ivListviewImageText_Image01, R.id.tvListviewImageText_Text01}, 1, new boolean [] {true, true},
																	new boolean[] {false, true});
		ItemArray = new ArrayAdapterEx(this, this, arrhelper);	
		//Init the module container list, which save information about every module
		modulesContainers = new ArrayList<ModuleContainer>();
		//Init the header and footer view
		View header = getLayoutInflater().inflate(R.layout.create_edit_clock_header, null);		
		View footer = getLayoutInflater().inflate(R.layout.create_edit_clock_footer, null);		
		//Init every View, and set the Listeners
		bOK = (Button) footer.findViewById(R.id.bCreateEditClock_OK);
		bOK.setOnClickListener(this);
		bAlarmTime = (Button) header.findViewById(R.id.bCreateEditClock_AlarmTime);
		bAlarmTime.setOnClickListener(this);		
		bWeekDays = (Button) header.findViewById(R.id.bCreateEditClock_WeekDays);
		bWeekDays.setOnClickListener(this);		
		bExtraOptions = (Button) header.findViewById(R.id.bCreateEditClock_ExtraOptions);
		bExtraOptions.setOnClickListener(this);		
		eClockName = (EditText) header.findViewById(R.id.eCreateEditClock_ClockName);		
		footerNewModule = getLayoutInflater().inflate(R.layout.listview_image_text, null);			
		ImageView icon = (ImageView) footerNewModule.findViewById(R.id.ivListviewImageText_Image01);        
        icon.setImageResource(R.drawable.add);        
        TextView text = (TextView) footerNewModule.findViewById(R.id.tvListviewImageText_Text01);
        text.setText("Add New Module");		
		footerNewModule.setOnClickListener(this);
		//add the header and footer view to the listview
		this.getListView().addHeaderView(header);
		this.getListView().addFooterView(footerNewModule);
		this.getListView().addFooterView(footer);
		//set the above defined custom arrayadapter as this list adapter
		this.setListAdapter(ItemArray);	
		//Create flag to check whether this activity-instance creates a new clock, or edits an existing clock		
		boolean creatNewClock = true;
		dbadapter = new DBAdapter(this);
        dbadapter.open();
		//Get the Extras which should have been sent from the view which called this one, if this instance should edit an existing clock
		Bundle extras = getIntent().getExtras();
		if (extras!=null) {
			clockid = extras.getInt("ClockId");
			Cursor cursor = dbadapter.fetchClock(clockid);
			if(cursor!=null) {     
	        	if((cursor.getCount()>=1) && (cursor.moveToFirst())) {
	        		//get all the previously saved values
					clockname = cursor.getString(1);
					clocktype = cursor.getInt(2);
					clockwakeuptask = cursor.getInt(3);
					clockwakeuptaskcount = cursor.getInt(4);
					clockwakeuptime = cursor.getInt(5);
					clockwakeupdays = cursor.getInt(6);
					//Set the Clockname to the clockname stored in the database
					eClockName.setText(clockname);
					creatNewClock=false;
	        	}
	        	cursor.close();
			}
		} 
		//If this is a new clock, create a new clock in the database with default values, and set default variables for the views
		if(creatNewClock) {
			clockid = dbadapter.createClock("DeleteThisClock", -1, -1, -1, -1, -1);
	        clockname="DeleteThisClock";
	        clocktype=clockwakeuptask=clockwakeuptaskcount=clockwakeuptime=-1;
	        clockwakeupdays=0;
		}       
        dbadapter.close();
	}

	/**
     * The Onclick-Callback for the 'main'-views form the listview
     */
	@Override
	public void onClick(View v) {
		if(v==footerNewModule) {			
			moduleList = new ListOfItems(this, this, 0, "Pick a Module", modules, null, 0);
			moduleList.show();
		}
		if(v==bOK) {
			saveClockValuesToDataBase();
	        this.finish();
		}
		if(v==bAlarmTime) {
			int temphours=0, tempminutes=0;
			if((clockwakeuptime!=0) && (clockwakeuptime!=-1)) {
				tempminutes = clockwakeuptime & 63;
				temphours = clockwakeuptime >> 6;
			} else {temphours=12;}
			timeDialog = new TimeDialog(this, this, temphours, tempminutes);
			timeDialog.show();
		}
		if(v==bWeekDays) {
			weekdayList = new ListOfItems(this, this, 2, "Choose Weekdays", weekdays, DBAdapter.getCheckedWeekDays(clockwakeupdays), 0);
			weekdayList.show();
		}
		if(v==bExtraOptions) {
			
		}
	}
	
	/**
	 * DialogCallBack for single choice lists
	 */
	@Override
	public void listOfItemsCallSingle(String dialogId, int value, int special) {		
		Intent i = null;
		if(DialogHelper.CompareDialogs(dialogId, moduleList)) {
				switch(value) {
					case 0:
						i = new Intent(CreateEditClock.this, ModuleDisplayLight.class);
						i.putExtra("ModuleId", -1);
						i.putExtra("ClockId", (int)clockid);						
						startActivity(i);
					break;
					case 1:
						i = new Intent(CreateEditClock.this, ModuleVibration.class);
						i.putExtra("ModuleId", -1);
						i.putExtra("ClockId", (int)clockid);						
						startActivity(i);
					break;
					case 2:
						Intent checkIntent = new Intent();
						checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
						startActivityForResult(checkIntent, 1337);
					break;
					default:
						DialogHelper.ShowToast(this, "Wrong Input.");
					break;
				}
		}	
		
		if(DialogHelper.CompareDialogs(dialogId, optionList)) {
			switch(value) {
				case 0:
					dbadapter = new DBAdapter(this);
			        dbadapter.open();
			        dbadapter.copyModule(modulesContainers.get(special).getId());
			        dbadapter.close();
			        this.onResume();
				break;
				
				case 1:
					int type1 = modulesContainers.get(special).getType();
					i = null;
					switch(type1) {
						case ModuleTypes.DISPLAYLIGHT:
							i = new Intent(CreateEditClock.this, ModuleDisplayLight.class);
							i.putExtra("ModuleId", modulesContainers.get(special).getId());
							i.putExtra("ClockId", (int)clockid);						
							startActivity(i);
						break;
						case ModuleTypes.VIBRATION:
							i = new Intent(CreateEditClock.this, ModuleVibration.class);
							i.putExtra("ModuleId", modulesContainers.get(special).getId());
							i.putExtra("ClockId", (int)clockid);						
							startActivity(i);
						break;
						case ModuleTypes.TEXTTOSPECH:
							i = new Intent(CreateEditClock.this, ModuleTTS.class);
							i.putExtra("ModuleId", modulesContainers.get(special).getId());
							i.putExtra("ClockId", (int)clockid);						
							startActivity(i);
						break;
					}
				break;
			
				case 2:
					deletModule(special);
				break;
				
				case 3:
					int type3 = modulesContainers.get(special).getType();
					i = null;
					switch(type3) {
						case ModuleTypes.DISPLAYLIGHT:
							BroadCastReceiverEx.startModuleDisplayLight(this, modulesContainers.get(special), false, -1);
						break;
						case ModuleTypes.VIBRATION:
							BroadCastReceiverEx.startVibrate(this, modulesContainers.get(special), false, -1);
						break;
						case ModuleTypes.TEXTTOSPECH:
							BroadCastReceiverEx.startTTS(this, modulesContainers.get(special), false, -1);
						break;
					}
				break;
			}
		}
	}
	
	/**
	 * Is called when Activity is first started, and when activity gets resumed from a stop
	 */
	@Override	
	protected void onResume() {
		super.onResume();
		//First Remove all items from the visible list of modules
		modulesContainers.clear();
		//Then remove all items from the module-container list
		ItemArray.clear();
		//Open a new Database Connection
		dbadapter = new DBAdapter(this);
        dbadapter.open();
        //Get the every module associated with this clock
        Cursor cursor = dbadapter.fetchModuleByClockid(clockid);
        //Load the clock associated with this instance of the activity
        clock = new ClockContainer(dbadapter.fetchClock(clockid), true);
        if(cursor!=null) {     
        	if((cursor.getCount()>=1) && (cursor.moveToFirst())) {   		
	        	do {      
	        		ItemArray.add(cursor.getString(2));
	        		modulesContainers.add(new ModuleContainer(cursor));
	        	} while(cursor.moveToNext());
        	}
        	cursor.close();
        }        
        dbadapter.close();
        //If the Clock is already enabled, re-initialize the BroadCast to set the new modules:
        if((BroadCastRecieverExHelper.ExistsBroadCastbyClockid(this, (int)clockid)) && (clock.getType()>=0) && !firstOnResume) {
        	saveClockValuesToDataBase();
        	BroadCastRecieverExHelper.CreateBroadCastbyClockid(this, (int)clockid, true);
        }
        firstOnResume=false;
	}
	
	/**
	 * The Onclick-Callback for each view in each clock entry of the list above the footer
	 * This is the singleclick callback
	 */
	@Override
	public void arrAdaptCallOnClick(View v, int position) {		
		if(v.getId()==R.id.ivListviewImageText_Image01) {
			deletModule(position);
		}
		if(v.getId()==R.id.tvListviewImageText_Text01) {	
			int type = modulesContainers.get(position).getType();
			Intent i = null;
			switch(type) {
				case ModuleTypes.DISPLAYLIGHT:
					i = new Intent(CreateEditClock.this, ModuleDisplayLight.class);
					i.putExtra("ModuleId", modulesContainers.get(position).getId());
					i.putExtra("ClockId", (int)clockid);						
					startActivity(i);
				break;
				case ModuleTypes.VIBRATION:
					i = new Intent(CreateEditClock.this, ModuleVibration.class);
					i.putExtra("ModuleId", modulesContainers.get(position).getId());
					i.putExtra("ClockId", (int)clockid);						
					startActivity(i);
				break;
				case ModuleTypes.TEXTTOSPECH:
					i = new Intent(CreateEditClock.this, ModuleTTS.class);
					i.putExtra("ModuleId", modulesContainers.get(position).getId());
					i.putExtra("ClockId", (int)clockid);						
					startActivity(i);
				break;
			}
		}		
	}
	
	/**
	 * Deletes a module from the database, from the modulecontainer list and from the visible listview
	 * @param position
	 */
	private void deletModule(int position) {
		dbadapter = new DBAdapter(this);
        dbadapter.open();
        dbadapter.deleteModule(modulesContainers.get(position).getId());
        modulesContainers.remove(position);
        dbadapter.close();
        ItemArray.remove(ItemArray.getItem(position));
	}

	/**
	 * Callback from the Timepicker-Dialog. If the user has entered a time, this function is executed
	 */
	@Override
	public void onTimeSet(TimePicker tp, int hours, int minutes) {
		clockwakeuptime=hours;
		clockwakeuptime = clockwakeuptime << 6;
		clockwakeuptime += minutes;
		//If the Clock is already enabled, re-initialize the BroadCast to set the new values and modules:
		if(clock!=null) {
	        if((BroadCastRecieverExHelper.ExistsBroadCastbyClockid(this, (int)clockid)) && (clock.getType()>=0) && !firstOnResume) {
	        	saveClockValuesToDataBase();
	        	BroadCastRecieverExHelper.CreateBroadCastbyClockid(this, (int)clockid, true);
	        }
		}
	}

	@Override
	public void arrAdaptCallOnLongClick(View v, int position) {
		if(v.getId()==R.id.tvListviewImageText_Text01) {
			optionList = new ListOfItems(this, this, 0, "Options", options, null, position);
			optionList.show();
		}	
	}
	
	/**
	 * Callback from the weekdaylist. If a weekday has been (un)checked, this function is executed
	 */
	@Override
	public void listOfItemsCallMultiple(String dialogId, int value, boolean clicked, int special) {
		if(DialogHelper.CompareDialogs(dialogId, weekdayList)) {
			if(value>=0) {
				if(clicked) {
					clockwakeupdays = clockwakeupdays | DBAdapter.intPower(2, value);
				} else {
					clockwakeupdays = clockwakeupdays & (255-DBAdapter.intPower(2, value));
				}
			}
			//If the Clock is already enabled, re-initialize the BroadCast to set the new values and modules:
			if(clock!=null) {
				if((BroadCastRecieverExHelper.ExistsBroadCastbyClockid(this, (int)clockid)) && (clock.getType()>=0) && !firstOnResume) {
					saveClockValuesToDataBase();
					BroadCastRecieverExHelper.CreateBroadCastbyClockid(this, (int)clockid, true);
				}
			}
		}
		
		
		
	}

	/**
	 * CallBack from the ArrayAdapter
	 * Gets called when every time a new View in the listview is created and/or refreshed
	 */
	@Override
	public void arrAdaptGetViewCallBack(View view, int position) {/*Not used, because not needed*/}	
	
	private void saveClockValuesToDataBase() {
		dbadapter = new DBAdapter(this);
        dbadapter.open();
        clockname=eClockName.getText().toString();
        dbadapter.updateClockAll(clockid, clockname, clocktype, clockwakeuptask, clockwakeuptaskcount, clockwakeuptime, clockwakeupdays);
        dbadapter.close();
	}
	
	/**
	 * Check whether a Text to Speech Engine is installed.
	 * If not, download it from the Market.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {		
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1337) {
	        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
	            //success, open the tts activity
	            Intent i = new Intent(CreateEditClock.this, ModuleTTS.class);
				i.putExtra("ModuleId", -1);
				i.putExtra("ClockId", (int)clockid);		
				startActivity(i);
	        } else {
	            // missing data, install it
	        	DialogHelper.ShowOKCancelDialog(this, this, "TTS not found", "The Text to Speech Engine\ncould not be found.\nDo you want to download it?");      
	        }
	    }
	}

	@Override
	public void dialogHelperOnOKClick() {
		Intent installIntent = new Intent();
        installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
        startActivity(installIntent);
	}
	
	
	
}
