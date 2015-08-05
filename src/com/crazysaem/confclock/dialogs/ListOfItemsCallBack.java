package com.crazysaem.confclock.dialogs;


public interface ListOfItemsCallBack {
	void listOfItemsCallSingle(String dialogId, int value, int special);
	void listOfItemsCallMultiple(String dialogId, int value, boolean clicked, int special);
}
