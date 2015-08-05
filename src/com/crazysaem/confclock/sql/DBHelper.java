package com.crazysaem.confclock.sql;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	
	private static final String DATABASE_NAME = "ConfClockDB";
	private static final int DATABASE_VERSION = 10;
	public static final String DATABASE_CREATE_CLOCKS = 
		//wutask=WakeUpTask(boolean): wether you want a brain-teaser when you wake up, or not
		//wutaskcount=WakeUpTaskCount(integer): how many brain teasers you want
		"create table clocks (_id integer primary key autoincrement, name text not null, type integer not null, wutask integer not null, wutaskcount integer not null," +
		" wutime integer not null, wudays integer not null);";
		//The Modules are the Core Functionality of the Alarm Clock.
	public static final String DATABASE_CREATE_MODULES = "create table modules (_id integer primary key autoincrement, clockid integer not null, name text not null, type integer not null, " +
			"tminus integer not null, duration integer not null, url text not null, value integer not null, int01 integer not null, int02 integer not null, text01 text not null, text02 text not null);";

	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	/**
	 * onCreate Function is only called once per DATABASE_VERSION, so if something is changed or you use SQL
	 * for the first time, be sure to increment the DATABASE_VERSION value at the top
	 */
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE_CLOCKS);	
		database.execSQL(DATABASE_CREATE_MODULES);
	}
	
	/**
	 * Gets called when DATABASE_VERSION is incremented. It drops all tables and calls "onCreate()"
	 */
	@Override
	public void onUpgrade(SQLiteDatabase database, int arg1, int arg2) {
		//This will destroy all data !!
		database.execSQL("DROP TABLE IF EXISTS clocks");
		database.execSQL("DROP TABLE IF EXISTS modules");
		database.execSQL("DROP TABLE IF EXISTS test");
		onCreate(database);		
	}	
}
