package com.liucd.share.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/** 
 * 类说明：   数据库帮助类
 * @author  @Cundong
 * @weibo   http://weibo.com/liucundong
 * @blog    http://www.liucundong.com
 * @date    Apr 29, 2011 2:50:48 PM
 * @version 1.0
 */
public class DBHelper extends SQLiteOpenHelper 
{	
	public static final String DATABASE_NAME = "share.db";
	public static final int DATABASE_VERSION = 1;
	public static final String ACCESSLIB_TABLE = "accessinfo";
	
	private static final String CREATE_ACCESSINFO_LIB = "CREATE TABLE " + ACCESSLIB_TABLE +" ("
		+ AccessInfoColumn._ID + " integer primary key autoincrement,"
		+ AccessInfoColumn.USERID + " text,"
		+ AccessInfoColumn.ACCESS_TOKEN + " text,"
		+ AccessInfoColumn.ACCESS_SECRET + " text)";
	
	public DBHelper(Context context) 
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_ACCESSINFO_LIB);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//do something
	}
}