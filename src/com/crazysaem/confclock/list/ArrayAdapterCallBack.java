package com.crazysaem.confclock.list;

import android.view.View;

public interface ArrayAdapterCallBack {
	public void arrAdaptCallOnClick(View v, int position);
	public void arrAdaptCallOnLongClick(View v, int position);
	public void arrAdaptGetViewCallBack(View view, int position);
}
