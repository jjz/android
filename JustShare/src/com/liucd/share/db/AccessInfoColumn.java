package com.liucd.share.db;

import android.provider.BaseColumns;

/** 
 * 类说明：   Sqlite数据库列常量
 * @author  @Cundong
 * @weibo   http://weibo.com/liucundong
 * @blog    http://www.liucundong.com
 * @date    Apr 29, 2011 2:50:48 PM
 * @version 1.0
 */
public class AccessInfoColumn implements BaseColumns 
{
	public AccessInfoColumn(){}
	
	//列名
	public static final String USERID = "USERID";
	public static final String ACCESS_TOKEN = "ACCESS_TOKEN";
	public static final String ACCESS_SECRET = "ACCESS_SECRET";

	//索引值
	public static final int _ID_ACCESS = 0;
	public static final int USERID_COLUMN = 1;
	public static final int ACCESS_TOKEN_COLUMN = 2;
	public static final int ACCESS_SECRET_COLUMN = 3;

	//查询结果集
	public static final String[] PROJECTION = 
	{
		_ID,					//0
		USERID,					//1
		ACCESS_TOKEN,			//2
		ACCESS_SECRET,			//3
	};
}