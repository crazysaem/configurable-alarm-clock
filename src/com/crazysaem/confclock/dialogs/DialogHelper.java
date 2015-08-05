package com.crazysaem.confclock.dialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class DialogHelper {
	public static boolean CompareDialogs(String dialogId, Object ObjectDialog) {
		if(ObjectDialog==null) return false;
		if(dialogId.equals(ObjectDialog.toString()))
			return true;
		else
			return false;
	}
	
	public static void ShowToast(Context context, String text) {
		int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
	}
	
	public static void ShowSimpleDialog(Context context, String title, String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(text)
			.setCancelable(true)
			.setTitle(title)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	public static void ShowOKCancelDialog(Context context, final DialogHelperCallBack callback, String title, String text) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(text)
			.setCancelable(true)
			.setTitle(title)
			.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				callback.dialogHelperOnOKClick();
				}
				})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}
