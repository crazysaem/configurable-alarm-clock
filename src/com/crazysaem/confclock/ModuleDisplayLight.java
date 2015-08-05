package com.crazysaem.confclock;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crazysaem.confclock.dialogs.ListOfItemsCallBack;
import com.crazysaem.confclock.dialogs.DialogHelper;
import com.crazysaem.confclock.dialogs.ListOfItems;
import com.crazysaem.confclock.dialogs.NumberPickerSingle;
import com.crazysaem.confclock.sql.DBAdapter;

public class ModuleDisplayLight extends Activity implements OnClickListener, ListOfItemsCallBack {
	private DBAdapter dbadapter;	
	private Button bSetTimeb4WakeUp, bSetDuration, bSetStartUpTime, bSetLightColor, bOK;
	private EditText etName;
	private TextView tvTimeb4WakeUp, tvDuration, tvStartUpTime, tvLightColor;	
	private NumberPickerSingle npsTimeB4WakeUp, npsDuration, npsStartUpTime;	
	private int iTimeB4WakeUp, iDuration, iStartUpTime, iColor, iClockId, iModuleId;	
	private final String[] colorItems = {"White", "Red", "Green", "Blue", "Orange", "Yellow", "Pink"};	
	private ListOfItems itemdialog;
	private boolean isUpdate;

	/**
	 * Call back, which gets called when this activity-instance is first created
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.module_displaylight);	
		
		bSetTimeb4WakeUp = (Button) this.findViewById(R.id.bModuleDisplayLight_SetTimeb4WakeUp);
		bSetTimeb4WakeUp.setOnClickListener(this);
		
		bSetDuration = (Button) this.findViewById(R.id.bModuleDisplayLight_SetDuration);
		bSetDuration.setOnClickListener(this);
		
		bSetStartUpTime = (Button) this.findViewById(R.id.bModuleDisplayLight_SetStartUpTime);
		bSetStartUpTime.setOnClickListener(this);
		
		bSetLightColor = (Button) this.findViewById(R.id.bModuleDisplayLight_SetLightColor);
		bSetLightColor.setOnClickListener(this);
		
		bOK = (Button) this.findViewById(R.id.bModuleDisplayLight_OK);
		bOK.setOnClickListener(this);
		
		tvTimeb4WakeUp = (TextView) this.findViewById(R.id.tvModuleDisplayLight_Timeb4WakeUp);
		
		tvDuration = (TextView) this.findViewById(R.id.tvModuleDisplayLight_Duration);
		
		tvStartUpTime = (TextView) this.findViewById(R.id.tvModuleDisplayLight_StartUpTime);
		
		tvLightColor = (TextView) this.findViewById(R.id.tvModuleDisplayLight_LightColor);
		
		etName = (EditText) this.findViewById(R.id.etModuleDisplayLight_Name);
		
		isUpdate=false;
		Bundle extras = getIntent().getExtras();
		if (extras!=null) {
			int moduleid = extras.getInt("ModuleId");
			iModuleId = moduleid;
			int clockid = extras.getInt("ClockId");
			iClockId = clockid;
			loadDataFromDatabase(moduleid);
		}
	}
	
	/**
	 * Loads module data into the private variables	
	 * @param moduleId
	 */
	private void loadDataFromDatabase(int moduleId) {
		dbadapter = new DBAdapter(this);
        dbadapter.open();
        Cursor cursor = dbadapter.fetchModuleByModuleid(moduleId);        
        boolean useDefaultValues=true;        
        if(cursor!=null) {     
        	if((cursor.getCount()>=1) && (cursor.moveToFirst())) {
	        	etName.setText(cursor.getString(2));
	        	iTimeB4WakeUp	= cursor.getInt(3);
	        	iDuration		= cursor.getInt(4);
	        	iStartUpTime	= cursor.getInt(5);
	        	iColor			= cursor.getInt(6);	        	
	        	SetAllTextViews();
	        	useDefaultValues=false;
	        	isUpdate=true;
        	}
        	cursor.close();
        }         
        if(useDefaultValues) { iTimeB4WakeUp=iDuration=iStartUpTime=iColor=-1; }
        dbadapter.close();        
	}

	@Override
	public void onClick(View clickedView) {
		
		if(clickedView==bSetTimeb4WakeUp) {
			npsTimeB4WakeUp = new NumberPickerSingle(this, "Minutes", 2);
			npsTimeB4WakeUp.show();
		}
		
		if(clickedView==bSetDuration) {
			npsDuration = new NumberPickerSingle(this, "Seconds", 3);
			npsDuration.show();
		}
		
		if(clickedView==bSetStartUpTime) {
			npsStartUpTime = new NumberPickerSingle(this, "Seconds", 3);
			npsStartUpTime.show();
		}

		if(clickedView==bSetLightColor) {
			
			itemdialog = new ListOfItems(this, this, 0, "Pick one", colorItems, null, 0);
			itemdialog.show();
		}
		
		if(clickedView==bOK) {			
			if((iTimeB4WakeUp!=-1) && (iDuration!=-1) && (iStartUpTime!=-1) && (iColor!=-1) && (iClockId!=-1)) {
				if(!isUpdate) {
					dbadapter = new DBAdapter(this);
			        dbadapter.open();
			        dbadapter.createModuleDisplayLight(etName.getText().toString(), iTimeB4WakeUp, iDuration, iStartUpTime, iColor, iClockId);	        
			        dbadapter.close();
			        DialogHelper.ShowToast(this, "Module Saved");
				} else {
					dbadapter = new DBAdapter(this);
			        dbadapter.open();
			        dbadapter.updateModuleDisplayLight(iModuleId, etName.getText().toString(), iTimeB4WakeUp, iDuration, iStartUpTime, iColor, iClockId);       
			        dbadapter.close();
			        DialogHelper.ShowToast(this, "Module Saved");
				}
		        this.finish();
			} else {
				DialogHelper.ShowToast(this, "Some Fields are missing!");
			}		
		}		
	}
	
	/**
	 * If this instance edits a module, this function sets the textview-values
	 */
	private void SetAllTextViews() {
		tvTimeb4WakeUp.setText(""+iTimeB4WakeUp+" Minutes before Wake-Up");
		tvDuration.setText(""+iDuration+" Seconds Duration");
		tvStartUpTime.setText(""+iStartUpTime+" Seconds Start-Up");
		tvLightColor.setText(""+colorItems[iColor]);
	}

	@Override
	public void listOfItemsCallSingle(String dialogId, int value, int special) {
		
		if(DialogHelper.CompareDialogs(dialogId,npsTimeB4WakeUp)) {
			if(value!=-1) {
				iTimeB4WakeUp=value;			
				tvTimeb4WakeUp.setText(""+value+" Minutes before Wake-Up");
			}
			else {DialogHelper.ShowToast(this, "Wrong Input.");}				
		}
		
		if(DialogHelper.CompareDialogs(dialogId,npsDuration)) {
			if(value!=-1) {
				iDuration=value;			
				tvDuration.setText(""+value+" Seconds Duration");
			}
			else {DialogHelper.ShowToast(this, "Wrong Input.");}	
		}
		
		if(DialogHelper.CompareDialogs(dialogId,npsStartUpTime)) {
			if(value!=-1) {
				iStartUpTime=value;			
				tvStartUpTime.setText(""+value+" Seconds Start-Up");
			}
			else {DialogHelper.ShowToast(this, "Wrong Input.");}	
		}
		
		if(DialogHelper.CompareDialogs(dialogId,itemdialog)) {
			if(value!=-1) {
				iColor=value;			
				tvLightColor.setText(""+colorItems[value]);
			}
			else {DialogHelper.ShowToast(this, "Wrong Input.");}
		}
	}

	@Override
	public void listOfItemsCallMultiple(String dialogId, int value, boolean clicked, int special) {}	
}
