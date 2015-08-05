package com.crazysaem.confclock.dialogs;

import com.crazysaem.confclock.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

public class NumberPickerSingle extends Dialog implements OnClickListener {
	private EditText etValue;
	private Button bOK;
	private Context parentcontext;
	
	private InputMethodManager showSoftInput;

	public NumberPickerSingle(Context context, String title, int maxinputlength) {
		super(context);
		setContentView(R.layout.dialog_number_picker_single);
		setTitle(title);
		
		parentcontext = context;
		
		etValue = (EditText) this.findViewById(R.id.etDialogNumberPickerSingle_Value);
		

		InputFilter[] FilterArray = new InputFilter[1];
		FilterArray[0] = new InputFilter.LengthFilter(maxinputlength);
		etValue.setFilters(FilterArray);		
		
		bOK = (Button) this.findViewById(R.id.bDialogNumberPickerSingle_OK);
		bOK.setOnClickListener(this);
		
		showSoftInput = ((InputMethodManager)((Activity)parentcontext).getSystemService(Context.INPUT_METHOD_SERVICE));  
		etValue.requestFocus();
		
		showSoftInput.getInputMethodList();
        showSoftInput.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);	
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public void onClick(View clickedView) {

		if(clickedView==bOK) {
			showSoftInput.toggleSoftInput(0, 0);	
			String sValue = etValue.getText().toString();
			if((sValue!=null) && (sValue.length()!=0)) {
				((ListOfItemsCallBack)parentcontext).listOfItemsCallSingle(this.toString(), Integer.parseInt(sValue), 0);
			}
			else {
				((ListOfItemsCallBack)parentcontext).listOfItemsCallSingle(this.toString(), -1, 0);
			}
			
			this.dismiss();
		}
		
	}
	
}
