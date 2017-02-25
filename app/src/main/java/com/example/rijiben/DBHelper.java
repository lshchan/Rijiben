package com.ouling.ex_notes;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

public class DBHelper {

	public static final String DBNAME = "db_notes.db";

	public static final String TABLENAME = "tb_notes";

	public static final int VERSION = 1;

	public static SQLiteDatabase dbInstance;

	private MyDBHelper myDBHelper;

	// private StringBuffer tableCreate;

	private Context context;

	public DBHelper(Context context) {
		this.context = context;
	}

	public void openDatabase() {
		if (dbInstance == null) {
			myDBHelper = new MyDBHelper(context, DBNAME, VERSION);
			dbInstance = myDBHelper.getWritableDatabase();
		}
	}

	// �����ݿ������tb_diary�����һ�����ݣ���ʧ�ܷ���-1
	public long insert(Diary diary) {
		ContentValues values = new ContentValues();
		values.put("diarytitle", diary.diarytitle);
		values.put("diarydate", diary.diarydate);
		values.put("diarycontent", diary.diarycontent);
		values.put("imageid", diary.imageId);
		return dbInstance.insert(TABLENAME, null, values);
	}

	// ������ݿ������е���Ϣ����ÿһ���û��ŵ�һ��map��ȥ��Ȼ���ٽ�map�ŵ�list����ȥ����
	public ArrayList getAllDiary() {
		ArrayList list = new ArrayList();
		Cursor cursor = null;
		cursor = dbInstance.query(TABLENAME, new String[] { "_id",
						"diarytitle", "diarydate", "diarycontent", "imageid" }, null,
				null, null, null, null);

		while (cursor.moveToNext()) {
			HashMap item = new HashMap();
			item.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
			item.put("diarytitle", cursor.getString(cursor
					.getColumnIndex("diarytitle")));
			item.put("diarydate", cursor.getString(cursor
					.getColumnIndex("diarydate")));
			item.put("diarycontent", cursor.getString(cursor
					.getColumnIndex("diarycontent")));
			item.put("imageid", cursor.getInt(cursor
							.getColumnIndex("imageid")));
			list.add(item);
		}
		cursor.close();
		return list;
	}

	// �޸���Ϣ
	public void modify(Diary diary) {
		ContentValues values = new ContentValues();
		values.put("diarytitle", diary.diarytitle);
		values.put("diarydate", diary.diarydate);
		values.put("diarycontent", diary.diarycontent);
		values.put("imageid", diary.imageId);

		dbInstance.update(TABLENAME, values, "_id="+String.valueOf(diary._id), null);
	}

	// ɾ��
	public void delete(int _id) {
		dbInstance.delete(TABLENAME, "_id="+_id, null);
		Log.d("DBHelper", "ɾ���С���");
	}

	// ����ܹ���¼��
	public int getTotalCount() {
		Cursor cursor = dbInstance.query(TABLENAME,
				new String[] { "count(*)" }, null, null, null, null, null);
		cursor.moveToNext();
		return cursor.getInt(0);
	}

	// ��ѯ
	public ArrayList getDiarys(String condition) {
		ArrayList list = new ArrayList();
		String strSelection = "";

		String sql = "select * from " + TABLENAME
				+ " where 1=1 and (diarytitle like '%" + condition + "%' "
				+ "or diarydate like '%" + condition
				+ "%' or diarycontent like '%" + condition + "%')"
				+ strSelection;
		Cursor cursor = dbInstance.rawQuery(sql, null);
		while (cursor.moveToNext()) {
			HashMap item = new HashMap();
			item.put("_id", cursor.getInt(cursor.getColumnIndex("_id")));
			item.put("diarytitle", cursor.getString(cursor
					.getColumnIndex("diarytitle")));
			item.put("diarydate", cursor.getString(cursor
					.getColumnIndex("diarydate")));
			item.put("diarycontent", cursor.getString(cursor
					.getColumnIndex("diarycontent")));
			item.put("imageid", cursor.getInt(cursor
							.getColumnIndex("imageid")));
			list.add(item);
		}
		cursor.close();
		return list;
	}

	//���ݿ������
	class MyDBHelper extends SQLiteOpenHelper {

		public MyDBHelper(Context context, String name, int version) {
			super(context, name, null, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String tableCreate = "create table "
					+ TABLENAME
					+ " (_id integer primary key autoincrement,diarytitle text,diarydate text,diarycontent text,imageid int)";
			db.execSQL(tableCreate);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			String sql = "drop table if exists " + TABLENAME;
			db.execSQL(sql);
			myDBHelper.onCreate(db);
		}

	}

}