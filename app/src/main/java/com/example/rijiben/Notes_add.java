package com.ouling.ex_notes;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
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



public class Notes_add extends Activity implements ViewFactory {

	EditText et_diarytitle;
	EditText et_diarydate;
	EditText et_diarycontent;
	Button btn_save;
	Button btn_return;

	ImageButton imageButton;// 头像按钮
	View imageChooseView;// 图像选择的视图
	AlertDialog imageChooseDialog;// 头像选择对话框
	Gallery gallery;// 头像的Gallery
	ImageSwitcher is;// 头像的ImageSwitcher
	int currentImagePosition;// 用于记录当前选中图像在图像数组中的位置
	int previousImagePosition;// 用于记录上一次图片的位置
	boolean imageChanged;// 判断头像有没有变化

	Calendar calendar = Calendar.getInstance();


	 //所有的图像图片

	private int[] images = new int[] { R.drawable.img1, 
			R.drawable.img2, R.drawable.img3, R.drawable.img4,R.drawable.img5, R.drawable.img6,
			R.drawable.img7, R.drawable.img8, R.drawable.img9 };

	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add);

		et_diarytitle = (EditText) findViewById(R.id.diarytitle);
		et_diarydate = (EditText) findViewById(R.id.diarydate);
		et_diarycontent = (EditText) findViewById(R.id.diarycontent);
		btn_save = (Button) findViewById(R.id.bt_save);
		btn_return = (Button) findViewById(R.id.bt_return);
		imageButton = (ImageButton) findViewById(R.id.image_button);

		et_diarydate.setText(returnDate());		

		 // 响应点击事件
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断是否为空
				String diarytitle = et_diarytitle.getText().toString();
				String diarydate = et_diarydate.getText().toString();
				if (diarytitle.trim().equals("") || diarydate.trim().equals("")) {
					Toast.makeText(Notes_add.this, "标题与日期不能为空",
							Toast.LENGTH_LONG).show();
					return;
				}
				// 从表单上获取数据
				Diary diary = new Diary();
				diary.diarytitle = diarytitle;
				diary.diarydate = diarydate;
				diary.diarycontent = et_diarycontent.getText().toString();

				// 判断头像是否改变，若改变，则用当前的位置，若没有改变，则用前一回的位置
				if (imageChanged) {
					diary.imageId = images[currentImagePosition % images.length];
				} else {
					diary.imageId = images[previousImagePosition
							% images.length];
				}

				// 创建数据库帮助类
				DBHelper helper = new DBHelper(Notes_add.this);
				// 打开数据库
				helper.openDatabase();
				// 把diary存储到数据库里
				long result = helper.insert(diary);
				// 通过结果来判断是否插入成功，若为-1，则表示插入数据失败
				if (result == -1) {
					Toast.makeText(Notes_add.this, "添加失败", Toast.LENGTH_LONG);
					finish();
				}else{
					setTitle("日记添加成功!");
					Intent intent = new Intent(Notes_add.this, ex_notes.class);
					startActivity(intent);
					// 销毁当前视图
					finish();
				}	

			}
		});

		btn_return.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Notes_add.this, ex_notes.class);
				startActivity(intent);
				finish();
			}
		});

		imageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				loadImage();// 为gallery装载图片
				initImageChooseDialog();// 初始化imageChooseDialog
				imageChooseDialog.show();
			}
		});

	}

	public void loadImage() {
		if (imageChooseView == null) {
			LayoutInflater li = LayoutInflater.from(Notes_add.this);
			imageChooseView = li.inflate(R.layout.choice_img, null);

			// 通过渲染xml文件，得到一个视图（View），再拿到这个View里面的Gallery
			gallery = (Gallery) imageChooseView.findViewById(R.id.gallery);
			// 为Gallery装载图片
			gallery.setAdapter(new ImageAdapter(this));
			gallery.setSelection(images.length / 2);
			
			is = (ImageSwitcher) imageChooseView.findViewById(R.id.imageswitch);
			is.setFactory(this);
			is.setInAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in));
			// 卸载图片的动画效果
			is.setOutAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out));
			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// 当前的头像位置为选中的位置
					currentImagePosition = arg2;
					// 为ImageSwitcher设置图像
					is.setImageResource(images[arg2 % images.length]);

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}

	}

//自定义Gallery的适配器

	class ImageAdapter extends BaseAdapter {

		private Context context;

		public ImageAdapter(Context context) {
			this.context = context;
		}

		@Override
		public int getCount() {
			return Integer.MAX_VALUE;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}


		// gallery从这个方法中拿到image

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

	public void initImageChooseDialog() {
		if (imageChooseDialog == null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("请选择天气").setView(imageChooseView)
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



	 //得到日期

	public  String returnDate() {
		// 返回日期
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return sDateFormat.format(new java.util.Date());
	}
	
	

	 // 当退出的时候，回收资源

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

		super.onDestroy();
	}
}
