package com.crazysaem.confclock.list;

public class ArrayAdapterExHelper {
	private int layoutid;
	private int[] ids;
	private int valueid;
	private boolean[] onClickEnabled;
	private boolean[] onLongClickEnabled;
	
	public ArrayAdapterExHelper(int layoutid, int[] ids, int valueid, boolean[] onClickEnabled, boolean[] onLongClickEnabled) {
		this.layoutid = layoutid;
		this.ids = ids;
		this.onClickEnabled = onClickEnabled;
		this.onLongClickEnabled = onLongClickEnabled;
		//TODO: Find better way to change values of the chosen id-Views:
		this.valueid = valueid;
	}

	public int getValueid() {
		return valueid;
	}

	public void setValueid(int valueid) {
		this.valueid = valueid;
	}

	public int getLayoutid() {
		return layoutid;
	}

	public void setLayoutid(int layoutid) {
		this.layoutid = layoutid;
	}

	public int[] getIds() {
		return ids;
	}

	public void setIds(int[] ids) {
		this.ids = ids;
	}

	public boolean[] getOnClickEnabled() {
		return onClickEnabled;
	}

	public void setOnClickEnabled(boolean[] onClickEnabled) {
		this.onClickEnabled = onClickEnabled;
	}

	public boolean[] getOnLongClickEnabled() {
		return onLongClickEnabled;
	}

	public void setOnLongClickEnabled(boolean[] onLongClickEnabled) {
		this.onLongClickEnabled = onLongClickEnabled;
	}
}
