package com.liucd.share.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.liucd.share.bean.AccessInfo;

/** 
 * 类说明：   数据库操作
 * @author  @Cundong
 * @weibo   http://weibo.com/liucundong
 * @blog    http://www.liucundong.com
 * @date    Apr 29, 2011 2:50:48 PM
 * @version 1.0
 */
public class AccessInfoHelper 
{
	private DBHelper dbHelper;
	private SQLiteDatabase newsDB;
	private Context context;
	
	public AccessInfoHelper( Context context )
	{
		this.context = context;
	}
	
	/**
	 * 初始化数据库连接
	 */
	public AccessInfoHelper open()
	{
		dbHelper = new DBHelper( this.context );
		
		newsDB = dbHelper.getWritableDatabase();
		return this;
	}
	
	/**
	 * 关闭连接
	 */
	public void close()
	{
		if(dbHelper!=null)
		{
			dbHelper.close();
		}
	}
	
	/**
	 * 创建一条记录
	 * @param accessInfo
	 * @return
	 */
	public long create( AccessInfo accessInfo )
	{
		ContentValues values = new ContentValues();
		
		values.put(AccessInfoColumn.USERID, accessInfo.getUserID() );
		values.put(AccessInfoColumn.ACCESS_TOKEN, accessInfo.getAccessToken() );
		values.put(AccessInfoColumn.ACCESS_SECRET, accessInfo.getAccessSecret() );
		
		return newsDB.insert(DBHelper.ACCESSLIB_TABLE, null, values);
	}
	
	/**
	 * 更新
	 * @param image
	 * @return
	 */
	public boolean update( AccessInfo accessInfo )
	{
		ContentValues values = new ContentValues();
		
		values.put(AccessInfoColumn.USERID, accessInfo.getUserID() );
		values.put(AccessInfoColumn.ACCESS_TOKEN, accessInfo.getAccessToken() );
		values.put(AccessInfoColumn.ACCESS_SECRET, accessInfo.getAccessSecret() );
		
		String whereClause = AccessInfoColumn.USERID + "=" + accessInfo.getUserID();
		
		return newsDB.update(DBHelper.ACCESSLIB_TABLE, values, whereClause, null) > 0;
	}
	
	/**
	 * 获取全部AccessInfo信息
	 * @return
	 */
	public ArrayList<AccessInfo> getAccessInfos()
	{
		ArrayList<AccessInfo> list = new ArrayList<AccessInfo>();
		
		AccessInfo accessInfo = null;
		Cursor cursor = newsDB.query(DBHelper.ACCESSLIB_TABLE, AccessInfoColumn.PROJECTION, 
				null, null, null, null, null);
		
		if( cursor.getCount() > 0 )
		{
			for( cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext() )
			{
				accessInfo = new AccessInfo();
				
				accessInfo.setUserID( cursor.getString( AccessInfoColumn.USERID_COLUMN ) );
				accessInfo.setAccessToken( cursor.getString( AccessInfoColumn.ACCESS_TOKEN_COLUMN ) );
				accessInfo.setAccessSecret( cursor.getString( AccessInfoColumn.ACCESS_SECRET_COLUMN ) );
				list.add(accessInfo);
			}
		}
		
		cursor.close();
		cursor = null;
		
		return list;
	}
	
	/**
	 * 获取一条图片记录
	 * @param imageID
	 * @return
	 */
	public AccessInfo getAccessInfo( String userID )
	{
		AccessInfo accessInfo = null;
		String selection = AccessInfoColumn.USERID + "=" + userID;
		
		Cursor cursor = newsDB.query(DBHelper.ACCESSLIB_TABLE, AccessInfoColumn.PROJECTION, 
				selection, null, null, null, null);
		
		if( cursor != null && cursor.getCount()>0 )
		{
			cursor.moveToFirst();
			accessInfo = new AccessInfo();
			accessInfo.setUserID( cursor.getString( AccessInfoColumn.USERID_COLUMN ) );
			accessInfo.setAccessToken( cursor.getString( AccessInfoColumn.ACCESS_TOKEN_COLUMN ) );
			accessInfo.setAccessSecret( cursor.getString( AccessInfoColumn.ACCESS_SECRET_COLUMN ) );
		}
		return accessInfo;
	}
	
	/**
	 * 删除
	 */
	public boolean delete()
	{
		int ret = newsDB.delete(DBHelper.ACCESSLIB_TABLE, null, null);
		return ret>0?true:false;
	}
}