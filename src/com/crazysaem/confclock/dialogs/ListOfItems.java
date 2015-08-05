package com.crazysaem.confclock.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnMultiChoiceClickListener;

public class ListOfItems extends AlertDialog.Builder implements android.content.DialogInterface.OnClickListener, OnMultiChoiceClickListener {
	private ListOfItemsCallBack dialogCallBack;
	private int special;
	
	public ListOfItems(Context context, ListOfItemsCallBack dialogCallBack, int type, String title, String[] items, boolean[] checked, int special) {
		super(context);
		this.dialogCallBack = dialogCallBack;
		this.special = special;
		
		if(checked==null) {
			checked = new boolean [items.length];
			for(int i=0;i<items.length;i++) {
				checked[i] = false;
			}
		}
		
		this.setTitle(title);
		switch(type) {
			case 1:
				this.setSingleChoiceItems(items, -1, this);
			break;
			case 2:
				this.setMultiChoiceItems(items, checked, this);
			break;
			default:
				this.setItems(items, this);
			break;
		}
		
		this.create();
	}

	@Override
	public void onClick(DialogInterface dialog, int item) {	
		dialogCallBack.listOfItemsCallSingle(this.toString(), item, special);
	}

	@Override
	public void onClick(DialogInterface arg0, int arg1, boolean arg2) {
		dialogCallBack.listOfItemsCallMultiple(this.toString() ,arg1, arg2, special);	
	}
	
	
}
