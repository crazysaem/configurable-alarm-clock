package com.crazysaem.confclock.container;

import android.database.Cursor;

public class ClockContainer {	
	private int id;
	private String name;
	private int type;
	private int wakeuptask;
	private int wakeuptaskcount;
	private int hour, minute;
	private int wakeupdays;
	private int wakeuptime;
	
	public ClockContainer(Cursor cursor, boolean deleteCursor) {
		this(cursor);
		if(deleteCursor) {
			cursor.close();
		}
	}
	
	public ClockContainer(Cursor cursor) {
		id = cursor.getInt(0);
		name = cursor.getString(1);
		type = cursor.getInt(2);
		wakeuptask = cursor.getInt(3);
		wakeuptaskcount = cursor.getInt(4);
		wakeuptime = cursor.getInt(5);
		wakeupdays = cursor.getInt(6);
		
		minute = wakeuptime & 63;
		hour = wakeuptime >> 6;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getWakeuptask() {
		return wakeuptask;
	}

	public void setWakeuptask(int wakeuptask) {
		this.wakeuptask = wakeuptask;
	}

	public int getWakeuptaskcount() {
		return wakeuptaskcount;
	}

	public void setWakeuptaskcount(int wakeuptaskcount) {
		this.wakeuptaskcount = wakeuptaskcount;
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getWakeupdays() {
		return wakeupdays;
	}

	public void setWakeupdays(int wakeupdays) {
		this.wakeupdays = wakeupdays;
	}

	public int getWakeuptime() {
		return wakeuptime;
	}

	public void setWakeuptime(int wakeuptime) {
		this.wakeuptime = wakeuptime;
	}	
	
	
}
