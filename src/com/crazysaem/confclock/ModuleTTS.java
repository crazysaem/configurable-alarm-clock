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

public class ModuleTTS extends Activity implements OnClickListener, ListOfItemsCallBack {
	private EditText etName, etText;
	private Button bSetTimeb4WakeUp, bOK;	
	private TextView tvTimeb4WakeUp;
	private ModuleContainer moduleContainer;
	private NumberPickerSingle npsTimeB4WakeUp;
	
	private int iClockId, iModuleId, iTimeB4WakeUp;
	private String name, text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.module_tts);     
				
		etName = (EditText) this.findViewById(R.id.etModuleTTS_Name);
		
		tvTimeb4WakeUp = (TextView) this.findViewById(R.id.tvModuleTTS_Timeb4WakeUp);
		
		bSetTimeb4WakeUp = (Button) this.findViewById(R.id.bModuleTTS_SetTimeb4WakeUp);
		bSetTimeb4WakeUp.setOnClickListener(this);
		
		etText = (EditText) this.findViewById(R.id.etModuleTTS_Text);
		
		bOK = (Button) this.findViewById(R.id.bModuleTTS_OK);
		bOK.setOnClickListener(this);
		
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
				name=moduleContainer.getName();
				text=moduleContainer.getText01();
				iTimeB4WakeUp=moduleContainer.getBegin();
				SetAllTextViews(name, text, iTimeB4WakeUp);
			}			
		}
		//TODO: show a list of available languages
	}
	
	private void SetAllTextViews(String name, String text, int iTimeB4WakeUp) {
		etName.setText(name);
		etText.setText(text);
		tvTimeb4WakeUp.setText(""+iTimeB4WakeUp+" Minutes before Wake-Up");
	}

	@Override
	public void onClick(View v) {
		if(v==bOK) {
			DBAdapter dbadapter = new DBAdapter(this);
			dbadapter.open();
			name=etName.getText().toString();
			text=etText.getText().toString();
			if(moduleContainer==null) {				        
		        dbadapter.createModuleTTS(iClockId, name, text, iTimeB4WakeUp); 
		        DialogHelper.ShowToast(this, "Module Saved");
			} else {
				dbadapter.updateModuleTTS(iModuleId, iClockId, name, text, iTimeB4WakeUp);
				DialogHelper.ShowToast(this, "Module Saved");
			}
			dbadapter.close();
			this.finish();
		}
		if(v==bSetTimeb4WakeUp) {
			npsTimeB4WakeUp = new NumberPickerSingle(this, "Minutes", 2);
			npsTimeB4WakeUp.show();
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
	}

	@Override
	public void listOfItemsCallMultiple(String dialogId, int value, boolean clicked, int special) {}
}
