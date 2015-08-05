package com.crazysaem.confclock;

import com.crazysaem.confclock.list.ArrayAdapterCallBack;
import com.crazysaem.confclock.list.ArrayAdapterEx;
import com.crazysaem.confclock.list.ArrayAdapterExHelper;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

public class Settings extends ListActivity implements ArrayAdapterCallBack  {
	private ArrayAdapter<String> ItemArray;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ArrayAdapterExHelper arrhelper = new ArrayAdapterExHelper(R.layout.listview_text_checkbox, new int[] {R.id.tvListviewTextCheckBox_Text01, R.id.cbListviewTextCheckBox_CheckBox01},
				0, new boolean [] {false, false}, new boolean[] {false, false});
		ItemArray = new ArrayAdapterEx(this, this, arrhelper);
		this.setListAdapter(ItemArray);	
		
		for(int i=0;i<15;i++) {
			ItemArray.add("Test Setting Nr."+i);
		}
		//TODO: Fill with settings .. obviously
	}

	@Override
	public void arrAdaptCallOnClick(View v, int position) {}

	@Override
	public void arrAdaptCallOnLongClick(View v, int position) {}

	@Override
	public void arrAdaptGetViewCallBack(View view, int position) {}

}
