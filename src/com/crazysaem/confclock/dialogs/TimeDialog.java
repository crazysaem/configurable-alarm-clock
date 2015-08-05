package com.crazysaem.confclock.dialogs;

import android.app.TimePickerDialog;
import android.content.Context;

public class TimeDialog extends TimePickerDialog {

	public TimeDialog(Context context, OnTimeSetListener callBack,	int hourOfDay, int minute) {
		super(context, callBack, hourOfDay, minute, true);
	}


}
