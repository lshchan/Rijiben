package com.ouling.ex_notes;

import java.util.ArrayList;
import java.util.HashMap;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class ex_notes extends Activity {



	private static final int INSERT_ID = Menu.FIRST;
	private static final int EXIT_ID = Menu.FIRST + 1;
	ListView lv;
	LinearLayout searchLinearout;
	LinearLayout linearLayout;

	// 拥有所有数据的Adapter
	SimpleAdapter adapter;
	ArrayList list;
	


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
		lv = (ListView) findViewById(R.id.list);// 创建ListView对象

		DBHelper helper = new DBHelper(this);// 获得所有用户的list
		helper.openDatabase(); // 打开数据库，就打开这一次，因为Helper中的SQLiteDatabase是静态的。		
		list = helper.getAllDiary();
		
		// 将数据与adapter集合起来
		adapter = new SimpleAdapter(this, list, R.layout.list_item,
				new String[] { "imageid", "diarytitle", "diarydate" },
				new int[] { R.id.diaryimage, R.id.diarytitle, R.id.diarydate });
		lv.setAdapter(adapter);// 将整合好的adapter交给listview，显示给用户看		

		lv.setOnItemClickListener(new OnItemClickListener() {
			 //响应单击事件，单点击某一个选项的时候，跳转到用户详细信息页面
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				HashMap item = (HashMap) arg0.getItemAtPosition(arg2);
//				int _id = Integer.parseInt(String.valueOf(item.get("_id")));

				Intent intent = new Intent(ex_notes.this, Notes_read.class);
				Diary diary = new Diary();
				diary._id = Integer.parseInt(String.valueOf(item.get("_id")));
				diary.diarytitle = String.valueOf(item.get("diarytitle"));
				diary.diarydate = String.valueOf(item.get("diarydate"));
				diary.diarycontent = String.valueOf(item.get("diarycontent"));
				diary.imageId = Integer.parseInt(String.valueOf(item.get("imageid")));

				intent.putExtra("diary", diary);

				if (searchLinearout != null
						&& searchLinearout.getVisibility() == View.VISIBLE) {
					searchLinearout.setVisibility(View.GONE);
				}
				/* 将arg2作为请求码传过去 用于标识修改项的位置 */
				startActivityForResult(intent, arg2);				
				finish();
			}
		});			
		lv.setCacheColorHint(Color.TRANSPARENT); // 设置ListView的背景为透明
	}

	@Override
	protected void onResume() {

		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, INSERT_ID, 1, "添加日记");
		menu.add(0,EXIT_ID,2,"退出");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case INSERT_ID: 
			Intent intent = new Intent(this, Notes_add.class);
			ex_notes.this.startActivity(intent);
			finish();
			break;
		
		case EXIT_ID: 
			finish();
			// 结束进程
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return super.onOptionsItemSelected(item);
	}

}