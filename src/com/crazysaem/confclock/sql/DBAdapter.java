package com.crazysaem.confclock.sql;


import com.crazysaem.confclock.container.ClockContainer;
import com.crazysaem.confclock.container.ModuleContainer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBAdapter {	
	private static final String DATABASE_TABLE_CLOCKS = "clocks";
	public static final String CLOCKS_ID = "_id";
	public static final String CLOCKS_NAME = "name";
	public static final String CLOCKS_TYPE = "type";
	public static final String CLOCKS_WAKEUPTASK = "wutask";
	public static final String CLOCKS_WAKEUPTASKCOUNT = "wutaskcount";
	public static final String CLOCKS_WAKEUPTIME = "wutime";
	public static final String CLOCKS_WAKEUPDAYS = "wudays";
	
	private static final String DATABASE_TABLE_MODULES = "modules";
	public static final String MODULES_ID = "_id";
	public static final String MODULES_CLOCK_ID = "clockid";
	public static final String MODULES_NAME = "name";
	public static final String MODULES_TYPE = "type";
	public static final String MODULES_BEGIN = "tminus";
	public static final String MODULES_DURATION = "duration";
	public static final String MODULES_URL = "url";
	public static final String MODULES_VALUE = "value";
	public static final String MODULES_INT01 = "int01";
	public static final String MODULES_INT02 = "int02";
	public static final String MODULES_TEXT01 = "text01";
	public static final String MODULES_TEXT02 = "text02";
	
	private Context context;
	private SQLiteDatabase database;
	private DBHelper dbHelper;
	
	public DBAdapter(Context context) {
		this.context = context;
	}
	
	/**
	 * Opens the DataBase and returns itself
	 * @return this
	 * @throws SQLException
	 */
	public DBAdapter open() throws SQLException {
		dbHelper = new DBHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	/**
	 * Closes the Database
	 */
	public void close() {
		database.close();
		dbHelper.close();
	}
	
	public void cleanUp() {
		Cursor cursor = this.fetchClockall();
		if(cursor!=null) {    
        	if((cursor.getCount()>=1) && (cursor.moveToFirst())) {
        		do {        		
            		if((cursor.getString(1).equals("DeleteThisClock")) && (cursor.getInt(2)==-1) && (cursor.getInt(3)==-1) && (cursor.getInt(4)==-1)
            				&& (cursor.getInt(5)==-1) && (cursor.getInt(6)==-1)) {
            			this.deleteClock(cursor.getInt(0));
            		}
	        	} while(cursor.moveToNext());        		
        	}
        	cursor.close();
		}
	}
	
	public long createClock(String name, int type, int wakeuptask, int wakeuptaskcount, int wutime, int wudays) {
		//All Values that the table contains have to be filled (except id of course) !!
		//If they are not in use, fill them with -1 or "", otherwise the entry will fail.
		name=trimSpaces(name);
		ContentValues clockvalues = new ContentValues();
		clockvalues.put(CLOCKS_NAME, name);
		clockvalues.put(CLOCKS_TYPE, type);
		clockvalues.put(CLOCKS_WAKEUPTASK, wakeuptask);
		clockvalues.put(CLOCKS_WAKEUPTASKCOUNT, wakeuptaskcount);
		clockvalues.put(CLOCKS_WAKEUPTIME, wutime);
		clockvalues.put(CLOCKS_WAKEUPDAYS, wudays);

		long clockid = database.insert(DATABASE_TABLE_CLOCKS, null, clockvalues);	
		if(clockid==-1) return -1;
		return clockid;		
	}
	
	public boolean updateClockAll(long clockid, String name, int type, int wakeuptask, int wakeuptaskcount, int wutime, int wudays) {
		name=trimSpaces(name);
		ContentValues clockvalues = new ContentValues();
		clockvalues.put(CLOCKS_NAME, name);
		clockvalues.put(CLOCKS_TYPE, type);
		clockvalues.put(CLOCKS_WAKEUPTASK, wakeuptask);
		clockvalues.put(CLOCKS_WAKEUPTASKCOUNT, wakeuptaskcount);
		clockvalues.put(CLOCKS_WAKEUPTIME, wutime);
		clockvalues.put(CLOCKS_WAKEUPDAYS, wudays);
		
		return database.update(DATABASE_TABLE_CLOCKS, clockvalues, CLOCKS_ID + "=" + clockid, null) > 0;	
	}
	
	public boolean updateClockType(long clockid, int type) {
		ContentValues clockvalues = new ContentValues();
		clockvalues.put(CLOCKS_TYPE, type);
		
		return database.update(DATABASE_TABLE_CLOCKS, clockvalues, CLOCKS_ID + "=" + clockid, null) > 0;	
	}
	
	public boolean deleteClock(long clockid) {		
		int ret = database.delete(DATABASE_TABLE_CLOCKS, CLOCKS_ID + "=" + clockid, null);
		ret += database.delete(DATABASE_TABLE_MODULES, MODULES_CLOCK_ID + "=" + clockid, null);
		return (ret>0);
	}
	
	public boolean deleteModule(long moduleid) {		
		return database.delete(DATABASE_TABLE_MODULES, MODULES_ID + "=" + moduleid, null) > 0;	
	}
	
	public Cursor fetchClock(long clockid) {
		Cursor cursor = database.query(true, DATABASE_TABLE_CLOCKS, new String[] { CLOCKS_ID, CLOCKS_NAME, CLOCKS_TYPE, CLOCKS_WAKEUPTASK, CLOCKS_WAKEUPTASKCOUNT, 
										CLOCKS_WAKEUPTIME, CLOCKS_WAKEUPDAYS },	CLOCKS_ID + "=" + clockid, null, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor;
	}
	
	public Cursor fetchClockall() {
		Cursor ret = null;
		try {
		ret = database.query(DATABASE_TABLE_CLOCKS, new String [] { CLOCKS_ID, CLOCKS_NAME, CLOCKS_TYPE, CLOCKS_WAKEUPTASK, CLOCKS_WAKEUPTASKCOUNT,
								CLOCKS_WAKEUPTIME, CLOCKS_WAKEUPDAYS}, null, null, null, null, null);
		} catch (SQLException e) {
	            Log.e("ConfClockLog ex", e.toString());
	    }
		if (ret != null)
			ret.moveToFirst();
		return ret;
	}
	
	/**
	 * Creates a Display Light Module
	 * @param name The name of the Module
	 * @param begin The time (in seconds) before the actual wake-up time
	 * @param duration The time (in seconds) this module lasts
	 * @param startuptime The time (in seconds) how long it takes until the display-brightness reaches maximum
	 * @param color The color of the display
	 * @param clockid The Id of the Clock, to which the module is bound to
	 * @return long The Module Id if the entry was successful, else -1
	 */	
	public long createModuleDisplayLight(String name, int begin, int duration, int startuptime, int color, long clockid) {
		return createModule(clockid, name, ModuleTypes.DISPLAYLIGHT, begin, duration, "", startuptime, color, -1, "", "");
	}
	
	public long createModuleVibration(int clockid, String name, int begin, int duration, int repeats) {
		return createModule(clockid, name, ModuleTypes.VIBRATION, begin, duration, "", repeats, -1, -1, "", "");
	}
	
	public long createModuleTTS(int clockid, String name, String text, int begin) {
		return createModule(clockid, name, ModuleTypes.TEXTTOSPECH, begin, -1, "", -1, -1, -1, text, "");
	}
	
	private long createModule(long clockid, String name, int moduletype, int begin, int duration, String URL, int value, int int01, int int02, String text01, String text02) {
		//All Values that the table contains have to be filled (except id of course) !!
		//If they are not in use, fill them with -1 or "", otherwise the entry will fail.
		name=trimSpaces(name);
		ContentValues modulevalues = new ContentValues();
		modulevalues.put(MODULES_CLOCK_ID, clockid);
		modulevalues.put(MODULES_NAME, name);
		modulevalues.put(MODULES_TYPE, moduletype);
		modulevalues.put(MODULES_BEGIN, begin);
		modulevalues.put(MODULES_DURATION, duration);
		modulevalues.put(MODULES_URL, URL);
		modulevalues.put(MODULES_VALUE, value);
		modulevalues.put(MODULES_INT01, int01);	
		modulevalues.put(MODULES_INT02, int02);	
		modulevalues.put(MODULES_TEXT01, text01);	
		modulevalues.put(MODULES_TEXT02, text02);	

		long moduleid = database.insert(DATABASE_TABLE_MODULES, null, modulevalues);	
		if(moduleid==-1) return -1;
		
		return moduleid;
	}
	
	public long copyModule(int moduleid) {
		ModuleContainer module = new ModuleContainer(fetchModuleByModuleid(moduleid));
		return createModule(module.getClockid(), module.getName(), module.getType(), module.getBegin(), module.getDuration(), module.getUrl(), module.getValue(), module.getInt01(), module.getInt02(), module.getText01(), module.getText02());
	}
	
	public long copyClock(int clockid) {
		ClockContainer clockcontainer = new ClockContainer(this.fetchClock(clockid), true);
		long newClockid = this.createClock(clockcontainer.getName(), clockcontainer.getType(), clockcontainer.getWakeuptask(), clockcontainer.getWakeuptaskcount(), clockcontainer.getWakeuptime(), clockcontainer.getWakeupdays());
		Cursor modules = this.fetchModuleByClockid(clockid);
		if(modules!=null) {     
        	if((modules.getCount()>=1) && (modules.moveToFirst())) {        		
	        	do {        		
	        		copyModule(modules.getInt(0), (int)newClockid);
	        	} while(modules.moveToNext());
        	}
        	modules.close();
        } 		
		return newClockid;
	}
	
	public long copyModule(int moduleid, int clockid) {
		ModuleContainer module = new ModuleContainer(fetchModuleByModuleid(moduleid));
		return createModule(clockid, module.getName(), module.getType(), module.getBegin(), module.getDuration(), module.getUrl(), module.getValue(), module.getInt01(), module.getInt02(), module.getText01(), module.getText02());
	}
	
	/**
	 * Updates a Display Light Module
	 * @param moduleid The Id of the Module that you want to update
	 * @param name The name of the Module
	 * @param begin The time (in seconds) before the actual wake-up time
	 * @param duration The time (in seconds) this module lasts
	 * @param startuptime The time (in seconds) how long it takes until the display-brightness reaches maximum
	 * @param color The color of the display
	 * @return boolean Whether the Creation of the Module was successful
	 */	
	public boolean updateModuleDisplayLight(int moduleid, String name, int begin, int duration, int startuptime, int color, long clockid) {
		return this.updateModule(moduleid, clockid, name, ModuleTypes.DISPLAYLIGHT, begin, duration, "", startuptime, color, -1, "", "");
	}
	
	public boolean updateModuleVibration(int moduleid, long clockid, String name, int begin, int duration, int repeats) {
		return updateModule(moduleid, clockid, name, ModuleTypes.VIBRATION, begin, duration, "", repeats, -1, -1, "", "");
	}
	
	public boolean updateModuleTTS(int moduleid, long clockid, String name, String text, int begin) {
		return updateModule(moduleid, clockid, name, ModuleTypes.TEXTTOSPECH, begin, -1, "", -1, -1, -1, text, "");
	}
	
	private boolean updateModule(long moduleid, long clockid, String name, int moduletype, int begin, int duration, String URL, int value, int int01, int int02, String text01, String text02) {
		name=trimSpaces(name);
		ContentValues modulevalues = new ContentValues();
		modulevalues.put(MODULES_CLOCK_ID, clockid);
		modulevalues.put(MODULES_NAME, name);
		modulevalues.put(MODULES_TYPE, moduletype);
		modulevalues.put(MODULES_BEGIN, begin);
		modulevalues.put(MODULES_DURATION, duration);
		modulevalues.put(MODULES_URL, URL);
		modulevalues.put(MODULES_VALUE, value);
		modulevalues.put(MODULES_INT01, int01);	
		modulevalues.put(MODULES_INT02, int02);	
		modulevalues.put(MODULES_TEXT01, text01);	
		modulevalues.put(MODULES_TEXT02, text02);
		
		return database.update(DATABASE_TABLE_MODULES, modulevalues, MODULES_ID + "=" + moduleid, null) > 0;
	}
	
	/**
	 * Fetches the desired Display Light Module
	 * @param moduleid The id of the Module you want to get 
	 * @return The Module you chose, if found
	 */
	public Cursor fetchModuleByModuleid(long moduleid) {
		Cursor cursor = database.query(true, DATABASE_TABLE_MODULES, new String[] { MODULES_ID, MODULES_CLOCK_ID, MODULES_NAME, MODULES_BEGIN, MODULES_DURATION, MODULES_VALUE, MODULES_INT01, MODULES_INT02, MODULES_TEXT01, MODULES_TEXT02, MODULES_TYPE, MODULES_URL },
				MODULES_ID + "=" + moduleid, null, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor;
	}
	
	public Cursor fetchModuleByClockid(long clockid) {
		Cursor cursor = database.query(true, DATABASE_TABLE_MODULES, new String[] { MODULES_ID, MODULES_CLOCK_ID, MODULES_NAME, MODULES_BEGIN, MODULES_DURATION, MODULES_VALUE, MODULES_INT01, MODULES_INT02, MODULES_TEXT01, MODULES_TEXT02, MODULES_TYPE, MODULES_URL },
				MODULES_CLOCK_ID + "=" + clockid, null, null, null, null, null);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor;
	}
	
	/**
	 * Fetches all Display Light Modules
	 * @return All Display Light Modules, if found
	 */
	public Cursor fetchModuleAll() {
		Cursor ret = null;
		try {
		ret = database.query(DATABASE_TABLE_MODULES, new String [] { MODULES_ID, MODULES_NAME, MODULES_BEGIN, MODULES_DURATION, MODULES_VALUE, MODULES_INT01, MODULES_INT02 },
				null, null, null, null, null);
		} catch (SQLException e) {
	            Log.e("ConfClockLog ex", e.toString());
	    }
		if (ret != null)
			ret.moveToFirst();
		return ret;
	}
	
	public static boolean[] getCheckedWeekDays(int wakeupdays) {
		boolean[] res = new boolean[7];
		for(int i=0; i<7; i++) {
			res[i] = (wakeupdays & intPower(2, i)) > 0;
		}		
		return res;
	}
	
	public static int intPower(int a, int b) {
		if(b==0) {return 1;}
		int res=1;
		for(int i=0;i<b;i++) {
			res *= a;
		}
		return res;
	}
	
	public static String trimSpaces(String source) {
		String res=source;
        res = res.replaceAll("^\\s+", "");
        res = res.replaceAll("\\s+$", "");
        return res;
    }
	
}
