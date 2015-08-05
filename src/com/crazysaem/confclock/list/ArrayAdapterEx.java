package com.crazysaem.confclock.list;

import java.util.ArrayList;
import java.util.List;

import com.crazysaem.confclock.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("unused")
public class ArrayAdapterEx extends ArrayAdapter<String> implements OnLongClickListener {
	private final Activity context;
	private final ArrayAdapterCallBack callBack;
	private ArrayAdapterExHelper arrayHelper;
	
	public ArrayAdapterEx(Activity context, ArrayAdapterCallBack callBack, ArrayAdapterExHelper arrayHelper) {		
		this(context, callBack, new ArrayList<String>(), arrayHelper);
	}
	
	private ArrayAdapterEx(Activity context, ArrayAdapterCallBack callBack, List<String> list, ArrayAdapterExHelper arrayHelper) {
		super(context, arrayHelper.getLayoutid(), list);
		this.context = context;
		this.arrayHelper = arrayHelper;
		this.callBack = callBack;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		
		if(convertView == null) {
			LayoutInflater inflator = context.getLayoutInflater();
			view = inflator.inflate(arrayHelper.getLayoutid(), null);
			
			OnClickListener onClickListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					callBack.arrAdaptCallOnClick(v, (Integer)v.getTag());
				}
				
			};
			
			OnLongClickListener onLongClickListener = new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					callBack.arrAdaptCallOnLongClick(v, (Integer)v.getTag());
					return true;
				}
				
			};
			
			for(int i=0;i<arrayHelper.getIds().length;i++) {
				View v = view.findViewById(arrayHelper.getIds()[i]);
				v.setTag(position);
				if(arrayHelper.getOnClickEnabled()[i]) {
					v.setClickable(true);
					v.setOnClickListener(onClickListener);					
				}
				if(arrayHelper.getOnLongClickEnabled()[i]) {
					v.setLongClickable(true);
					v.setOnLongClickListener(onLongClickListener);
					//TODO: Fix Line below:
					//viewHolder.views[i].setOnLongClickListener(onClickListener);					
				}
			}			
		} else {
			view = convertView;			
		}
		
		for(int i=0;i<arrayHelper.getIds().length;i++) {
			View v = view.findViewById(arrayHelper.getIds()[i]);
			v.setTag(position);
			if(arrayHelper.getOnClickEnabled()[i]) {
				v.setClickable(true);				
			} else {v.setClickable(false);}
			if(arrayHelper.getOnLongClickEnabled()[i]) {
				v.setLongClickable(true);
				//TODO: Fix Line below:
				//viewHolder.views[i].setOnLongClickListener(onClickListener);					
			} else {v.setLongClickable(false);}	
			if(i==arrayHelper.getValueid()) {
				((TextView)v).setText(this.getItem(position));
			}
		}
		
		view.setClickable(false);
        view.setLongClickable(false);
		
		callBack.arrAdaptGetViewCallBack(view, position);
		
		return view;		
	}

	@Override
	public boolean onLongClick(View arg0) {
		//true if the callback consumed the long click, false otherwise.
		CharSequence text = "Long Press";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this.context, text, duration);
		toast.show();	
		return true;
	}
}
