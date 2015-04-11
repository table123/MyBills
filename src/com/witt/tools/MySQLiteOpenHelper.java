package com.witt.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	private final static String DB_NAME = "db_myCheck.db";
	private final static int VERSION = 1;
	public SQLiteDatabase dbConn;

	public MySQLiteOpenHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
		dbConn = this.getReadableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("create table if not exists tb_inCheck(_id integer " +
				"primary key autoincrement,type,count,date,detail)");
		db.execSQL("create table if not exists tb_outCheck(_id integer " +
				"primary key autoincrement,mainType,count,date,detailType,detail)");
		db.execSQL("create table if not exists tb_outCheckType(_id integer " +
				"primary key autoincrement,detailType,mainType)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (newVersion > oldVersion) {
			db.execSQL("drop table if exists tb_inCheck");
			db.execSQL("drop table if exists tb_outCheck");
			db.execSQL("drop table if exists tb_outCheckType");
			onCreate(db);
		}
	}

	/**
	 * @作用：执行带占位符的select语句，查询数据，返回Cursor
	 * @param sql
	 * @param selectionArgs
	 * @return Cursor
	 */
	public Cursor selectCursor(String sql, String[] selectionArgs) {
		return dbConn.rawQuery(sql, selectionArgs);
	}

	/**
	 * @作用：执行带占位符的select语句，返回结果集的个数
	 * @param sql
	 * @param selectionArgs
	 * @return int
	 */
	public int selectCount(String sql, String[] selectionArgs) {
		Cursor cursor = dbConn.rawQuery(sql, selectionArgs);
		if (cursor != null && cursor.getCount()!=0) {
			cursor.moveToFirst();
			int count = cursor.getInt(0);
			cursor.close();
			return count;
		} 
		return 0;
	}

	/**
	 * @作用：执行带占位符的select语句，返回多条数据，放进List集合中。
	 * @param sql
	 * @param selectionArgs
	 * @return List<Map<String, String>>
	 */
	public List<Map<String, String>> selectList(String sql,
			String[] selectionArgs) {
		Cursor cursor = dbConn.rawQuery(sql, selectionArgs);
		return cursorToList(cursor);
	}

	/**
	 * @作用：将Cursor对象转成List集合
	 * @param Cursor cursor
	 * @return List<Map<String, Object>>集合
	 */
	public List<Map<String, String>> cursorToList(Cursor cursor) {
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();

		String[] arrColumnName = cursor.getColumnNames();
		while (cursor.moveToNext()) {
			Map<String, String> map = new HashMap<String, String>();
			for (int i = 0; i < arrColumnName.length; i++) {
				String cols_value = cursor.getString(i);
				map.put(arrColumnName[i], cols_value);
			}
			list.add(map);
		}
		if (cursor != null) {
			cursor.close();
		}
		return list;
	}

	/**
	 * @作用：执行带占位符的update、insert、delete语句，更新数据库，返回true或false
	 * @param sql
	 * @param bindArgs
	 * @return boolean
	 */
	public boolean execData(String sql, Object[] bindArgs) {
		try {
			dbConn.execSQL(sql, bindArgs);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void destroy() {
		if (dbConn != null) {
			dbConn.close();
		}
	}
}
