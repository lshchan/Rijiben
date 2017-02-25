package com.ouling.ex_notes;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.ViewSwitcher.ViewFactory;


public class Notes_read extends Activity implements ViewFactory {

	EditText et_diarytitle;
	EditText et_diarydate;
	EditText et_diarycontent;

	Button btn_save;
	Button btn_return;
	Button btn_delete;
	// 头像的按钮
	ImageButton imageButton;
	// 用flag来判断按钮的状态 false表示查看点击修改状态 true表示点击修改保存状态
	boolean flag = false;
	boolean imageChanged = false;
	boolean isDataChanged = false;

	int currentImagePosition;
	int previousImagePosition;

	String status;
	// 拥有一个diary实例，这个对象由Intent传过来
	Diary diary;
	Gallery gallery;
	ImageSwitcher is;

	View numChooseView;
	View imageChooseView;

	AlertDialog imageChooseDialog;

//所有的图像图片
	private int[] images = new int[] { R.drawable.img1, R.drawable.img2,
			R.drawable.img3, R.drawable.img4, R.drawable.img5, R.drawable.img6,
			R.drawable.img7, R.drawable.img8, R.drawable.img9 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read);

		// 获得意图
		Intent intent = getIntent();
		// 从意图中得到需要的diary对象
		diary = (Diary) intent.getSerializableExtra("diary");
		// 加载数据,往控件上赋值
		loadDiaryData();
		// 设置EditText不可编辑
		setEditTextDisable();

		// 为按钮添加监听类
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!flag) {
					btn_save.setText("保存");
					setEditTextAble();
					flag = true;
				} else {
					// 往数据库里面更新数据
					if (modify() == -1) {
						return;
					}
					flag = false;
					setTitle("修改成功");
					returnMain();
				}

			}
		});

		btn_return.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				returnMain();
			}
		});

		btn_delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(Notes_read.this).setPositiveButton(
						"确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(Notes_read.this,"已删除",Toast.LENGTH_SHORT).show();
								delete();
								returnMain();
							}
						}).setNegativeButton("取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).setTitle("是否要删除?").create().show();

			}
		});

		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadImage();// 加载imageChooseView，只加载一次
				initImageChooseDialog();// 加载imageChooseDialog，只加载一次
				imageChooseDialog.show();

			}
		});

	}

	public void returnMain() {
		Intent intent = new Intent(Notes_read.this, ex_notes.class);
		startActivity(intent);
		finish();
	}

// 获得布局文件中的控件，并且根据传递过来diary对象对控件进行赋值
	public void loadDiaryData() {
		// 获得EditText控件
		et_diarytitle = (EditText) findViewById(R.id.diarytitle);
		et_diarydate = (EditText) findViewById(R.id.diarydate);
		et_diarycontent = (EditText) findViewById(R.id.diarycontent);

		// 获得Button控件
		btn_save = (Button) findViewById(R.id.bt_save);
		btn_return = (Button) findViewById(R.id.bt_return);
		btn_delete = (Button) findViewById(R.id.bt_delete);
		imageButton = (ImageButton) findViewById(R.id.image_button);

		// 为控件赋值
		et_diarytitle.setText(diary.diarytitle);
		et_diarydate.setText(diary.diarydate);
		et_diarycontent.setText(diary.diarycontent);
		imageButton.setImageResource(diary.imageId);
	}

//设置EditText为不可用
	private void setEditTextDisable() {
		et_diarytitle.setEnabled(false);
		et_diarydate.setEnabled(false);
		et_diarycontent.setEnabled(false);
		imageButton.setEnabled(false);
		setColorToWhite();
	}

//设置EditText为可用状态
	private void setEditTextAble() {
		et_diarytitle.setEnabled(true);
		et_diarydate.setEnabled(true);
		et_diarycontent.setEnabled(true);
		imageButton.setEnabled(true);
		setColorToBlack();
	}

//设置显示的字体颜色为黑色
	private void setColorToBlack() {

		et_diarytitle.setTextColor(Color.BLACK);
		et_diarydate.setTextColor(Color.BLACK);
		et_diarycontent.setTextColor(Color.BLACK);
	}

//设置显示的字体颜色为白色
	private void setColorToWhite() {
		et_diarytitle.setTextColor(Color.WHITE);
		et_diarydate.setTextColor(Color.WHITE);
		et_diarycontent.setTextColor(Color.WHITE);
	}

//获得最新数据，创建DBHelper对象，更新数据库
	private int modify() {

		diary.diarytitle = et_diarytitle.getText().toString();
		diary.diarydate = et_diarydate.getText().toString();
		diary.diarycontent = et_diarycontent.getText().toString();
		if (imageChanged) {
			diary.imageId = images[currentImagePosition % images.length];
		}
		if (diary.diarytitle.trim().equals("")
				|| diary.diarydate.trim().equals("")) {
			Toast.makeText(this, "标题与日期不能为空", Toast.LENGTH_LONG).show();
			return -1;
		}
		DBHelper helper = new DBHelper(this);
		// 打开数据库
		helper.openDatabase();
		helper.modify(diary);
		isDataChanged = true;
		return 1;
	}

	private void delete() {
		DBHelper helper = new DBHelper(this);
		// 打开数据库
		helper.openDatabase();
		helper.delete(diary._id);

	}

//装载图片
	public void loadImage() {
		if (imageChooseView == null) {
			LayoutInflater li = LayoutInflater.from(Notes_read.this);
			imageChooseView = li.inflate(R.layout.choice_img, null);
			gallery = (Gallery) imageChooseView.findViewById(R.id.gallery);
			gallery.setAdapter(new ImageAdapter(this));
			gallery.setSelection(images.length / 2);
			is = (ImageSwitcher) imageChooseView.findViewById(R.id.imageswitch);
			is.setFactory(this);
			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					currentImagePosition = arg2 % images.length;
					is.setImageResource(images[arg2 % images.length]);

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}

	}

	public void initImageChooseDialog() {
		if (imageChooseDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请选择图像").setView(imageChooseView)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									imageChanged = true;
									previousImagePosition = currentImagePosition;
									imageButton
											.setImageResource(images[currentImagePosition
													% images.length]);
								}
							}).setNegativeButton("取消",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									currentImagePosition = previousImagePosition;

								}
							});
			imageChooseDialog = builder.create();
		}
	}

//自定义头像适配器
	class ImageAdapter extends BaseAdapter {

		private Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

//gallery从这个方法中拿到image
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView iv = new ImageView(context);
			iv.setImageResource(images[position % images.length]);
			iv.setAdjustViewBounds(true);
			iv.setLayoutParams(new Gallery.LayoutParams(80, 80));
			iv.setPadding(15, 10, 15, 10);
			return iv;
		}

	}

	@Override
	public View makeView() {
		ImageView view = new ImageView(this);
		view.setBackgroundColor(0xff000000);
		view.setScaleType(ScaleType.FIT_CENTER);
		view.setLayoutParams(new ImageSwitcher.LayoutParams(90, 90));
		return view;
	}

//当退出的时候，回收资源
	@Override
	protected void onDestroy() {
		if (is != null) {
			is = null;
		}
		if (gallery != null) {
			gallery = null;
		}
		if (imageChooseDialog != null) {
			imageChooseDialog = null;
		}
		if (imageChooseView != null) {
			imageChooseView = null;
		}
		if (imageButton != null) {
			imageButton = null;
		}

		if (numChooseView != null) {
			numChooseView = null;
		}

		super.onDestroy();
	}
}