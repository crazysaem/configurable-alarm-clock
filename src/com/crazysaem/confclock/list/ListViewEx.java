package com.crazysaem.confclock.list;

import com.crazysaem.confclock.R;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListViewEx implements OnItemClickListener {
	public ListView listview;
	private ArrayAdapter<String> ItemArray;
	private ListCallBack CallBackObject;
	
	public ListViewEx(ListView listview, Activity context, ArrayAdapterCallBack callBack, ListCallBack CallBackObject){
		this.listview = listview;
		this.CallBackObject = CallBackObject;
		//TODO: More Loose coupling, push the ArrayAdapterExHelper to the parent Activity
		ArrayAdapterExHelper arrhelper = new ArrayAdapterExHelper(R.layout.listview_image_text_image, 
				new int[] {R.id.ivListviewImageTextImage_Image01, R.id.tvListviewImageTextImage_Text01, R.id.ivListviewImageTextImage_Image02}, 1, new boolean [] {true, true, true}, new boolean[] {false, true, false});
		ItemArray = new ArrayAdapterEx(context, callBack, arrhelper);	
	}	
	
	public void Init() {
		this.listview.setAdapter(ItemArray); 
	}
	
	public void AddFooterView(View v) {
		this.listview.addFooterView(v);
	}
	
	public void RemoveAllItems() {
		ItemArray.clear();
	}
	
	public void AddItemString(String string) {
		ItemArray.add(string);
	}
	
	public void DeleteItemString(int pos) {
		ItemArray.remove(ItemArray.getItem(pos));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(CallBackObject!=null) {CallBackObject.listCallBack(arg0, arg1, arg2, arg3);}
	}
}
