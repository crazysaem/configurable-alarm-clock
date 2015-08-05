package com.crazysaem.confclock.container;

import android.database.Cursor;

public class ModuleContainer {
	int id;
	int clockid;
	String name;
	int begin;
    int duration;
    int value;    
    int int01;
    int int02;
    int type;
    
    String text01;
    String text02;
    String url;
    
    public ModuleContainer(Cursor cursor, boolean deleteCursor) {
		this(cursor);
		if(deleteCursor) {
			cursor.close();
		}
	}
	
	public ModuleContainer(Cursor cursor) {
		id = cursor.getInt(0);
		clockid = cursor.getInt(1);
		name = cursor.getString(2);
        begin = cursor.getInt(3);
        duration = cursor.getInt(4);
        value = cursor.getInt(5);
        
        int01 = cursor.getInt(6);
        int02 = cursor.getInt(7);
        
        text01 = cursor.getString(8);
        text02 = cursor.getString(9);
        
        type = cursor.getInt(10);
        url = cursor.getString(11);
	}
	
	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public int getClockid() {
		return clockid;
	}



	public void setClockid(int clockid) {
		this.clockid = clockid;
	}



	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getBegin() {
		return begin;
	}

	public void setBegin(int begin) {
		this.begin = begin;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getInt01() {
		return int01;
	}

	public void setInt01(int int01) {
		this.int01 = int01;
	}

	public int getInt02() {
		return int02;
	}

	public void setInt02(int int02) {
		this.int02 = int02;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getText01() {
		return text01;
	}

	public void setText01(String text01) {
		this.text01 = text01;
	}

	public String getText02() {
		return text02;
	}

	public void setText02(String text02) {
		this.text02 = text02;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
