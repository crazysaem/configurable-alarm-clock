package com.crazysaem.confclock;

import com.crazysaem.confclock.container.ModuleContainer;
import com.crazysaem.confclock.dialogs.ListOfItemsCallBack;
import com.crazysaem.confclock.dialogs.DialogHelper;
import com.crazysaem.confclock.dialogs.NumberPickerSingle;
import com.crazysaem.confclock.sql.DBAdapter;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ModuleVibration extends Activity implements OnClickListener, ListOfItemsCallBack {
	private Button bSetTimeb4WakeUp, bSetDuration, bRepeats, bOK;
	private EditText etName;
	private TextView tvTimeb4WakeUp, tvDuration, tvRepeats;
	private ModuleContainer moduleContainer = null;
	private NumberPickerSingle npsTimeB4WakeUp, npsDuration, npsRepeats;
	private int iClockId, iModuleId, iTimeB4WakeUp, iDuration, iRepeats;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			
		setContentView(R.layout.module_vibration);	
		
		bSetTimeb4WakeUp = (Button) this.findViewById(R.id.bModuleVibration_SetTimeb4WakeUp);
		bSetTimeb4WakeUp.setOnClickListener(this);
		
		bSetDuration = (Button) this.findViewById(R.id.bModuleVibration_SetDuration);
		bSetDuration.setOnClickListener(this);
		
		bRepeats = (Button) this.findViewById(R.id.bModuleVibration_SetRepeats);
		bRepeats.setOnClickListener(this);
		
		bOK = (Button) this.findViewById(R.id.bModuleVibration_OK);
		bOK.setOnClickListener(this);
		
		tvTimeb4WakeUp = (TextView) this.findViewById(R.id.tvModuleVibration_Timeb4WakeUp);
		
		tvDuration = (TextView) this.findViewById(R.id.tvModuleVibration_Duration);
		
		tvRepeats = (TextView) this.findViewById(R.id.tvModuleVibration_Repeats);
		
		etName = (EditText) this.findViewById(R.id.etModuleVibration_Name);
		
		iClockId=iModuleId=iTimeB4WakeUp=iDuration=iRepeats=-1;
		
		Bundle extras = getIntent().getExtras();
		if (extras!=null) {
			iClockId = extras.getInt("ClockId");
			iModuleId = extras.getInt("ModuleId");
			if(iModuleId!=-1) {
				DBAdapter dbadapter = new DBAdapter(this);
		        dbadapter.open();
		        Cursor cursor = dbadapter.fetchModuleByModuleid(iModuleId);
		        dbadapter.close();
				moduleContainer = new ModuleContainer(cursor);					
				iTimeB4WakeUp=moduleContainer.getBegin();
				iDuration=moduleContainer.getDuration();
				iRepeats=moduleContainer.getValue();
				SetAllTextViews(moduleContainer.getName(), iTimeB4WakeUp, iDuration, iRepeats);
			}			
		}
		
		
	}
	
	private void SetAllTextViews(String name, int iTimeB4WakeUp, int iDuration, int iRepeats) {
		etName.setText(name);
		tvTimeb4WakeUp.setText(""+iTimeB4WakeUp+" Minutes before Wake-Up");
		tvDuration.setText(""+String.format("%.1f",(double)iDuration/10)+" Seconds Duration");
		tvRepeats.setText(""+iRepeats+" Repeats");		
	}
	
	@Override
	public void onClick(View clickedView) {
		if(clickedView==bSetTimeb4WakeUp) {
			npsTimeB4WakeUp = new NumberPickerSingle(this, "Minutes", 2);
			npsTimeB4WakeUp.show();
		}
		
		if(clickedView==bSetDuration) {
			npsDuration = new NumberPickerSingle(this, "1/10 Seconds", 3);
			npsDuration.show();
		}
		
		if(clickedView==bRepeats) {
			npsRepeats = new NumberPickerSingle(this, "Times", 3);
			npsRepeats.show();
		}
		
		if(clickedView==bOK) {	
			DBAdapter dbadapter = null;
			if((iTimeB4WakeUp!=-1) && (iDuration!=-1) && (iRepeats!=-1)) {
				if(moduleContainer==null) {
					dbadapter = new DBAdapter(this);
			        dbadapter.open();
			        dbadapter.createModuleVibration(iClockId, etName.getText().toString(), iTimeB4WakeUp, iDuration, iRepeats);	        
			        dbadapter.close();
			        DialogHelper.ShowToast(this, "Module Saved");
				} else {
					dbadapter = new DBAdapter(this);
			        dbadapter.open();
			        dbadapter.updateModuleVibration(iModuleId, iClockId, etName.getText().toString(), iTimeB4WakeUp, iDuration, iRepeats);       
			        dbadapter.close();
			        DialogHelper.ShowToast(this, "Module Saved");
				}
		        this.finish();
			} else {
				DialogHelper.ShowToast(this, "Some Fields are missing!");
			}		
		}
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
				tvDuration.setText(""+String.format("%.1f",(double)iDuration/10)+" Seconds Duration");
			}
			else {DialogHelper.ShowToast(this, "Wrong Input.");}	
		}
		
		if(DialogHelper.CompareDialogs(dialogId,npsRepeats)) {
			if(value!=-1) {
				iRepeats=value;			
				tvRepeats.setText(""+value+" Repeats");
			}
			else {DialogHelper.ShowToast(this, "Wrong Input.");}	
		}
	}

	@Override
	public void listOfItemsCallMultiple(String dialogId, int value, boolean clicked, int special) {}
	
}
