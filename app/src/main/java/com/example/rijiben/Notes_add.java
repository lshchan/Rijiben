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

	ImageButton imageButton;// ͷ��ť
	View imageChooseView;// ͼ��ѡ�����ͼ
	AlertDialog imageChooseDialog;// ͷ��ѡ��Ի���
	Gallery gallery;// ͷ���Gallery
	ImageSwitcher is;// ͷ���ImageSwitcher
	int currentImagePosition;// ���ڼ�¼��ǰѡ��ͼ����ͼ�������е�λ��
	int previousImagePosition;// ���ڼ�¼��һ��ͼƬ��λ��
	boolean imageChanged;// �ж�ͷ����û�б仯

	Calendar calendar = Calendar.getInstance();


	 //���е�ͼ��ͼƬ

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

		 // ��Ӧ����¼�
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �ж��Ƿ�Ϊ��
				String diarytitle = et_diarytitle.getText().toString();
				String diarydate = et_diarydate.getText().toString();
				if (diarytitle.trim().equals("") || diarydate.trim().equals("")) {
					Toast.makeText(Notes_add.this, "���������ڲ���Ϊ��",
							Toast.LENGTH_LONG).show();
					return;
				}
				// �ӱ��ϻ�ȡ����
				Diary diary = new Diary();
				diary.diarytitle = diarytitle;
				diary.diarydate = diarydate;
				diary.diarycontent = et_diarycontent.getText().toString();

				// �ж�ͷ���Ƿ�ı䣬���ı䣬���õ�ǰ��λ�ã���û�иı䣬����ǰһ�ص�λ��
				if (imageChanged) {
					diary.imageId = images[currentImagePosition % images.length];
				} else {
					diary.imageId = images[previousImagePosition
							% images.length];
				}

				// �������ݿ������
				DBHelper helper = new DBHelper(Notes_add.this);
				// �����ݿ�
				helper.openDatabase();
				// ��diary�洢�����ݿ���
				long result = helper.insert(diary);
				// ͨ��������ж��Ƿ����ɹ�����Ϊ-1�����ʾ��������ʧ��
				if (result == -1) {
					Toast.makeText(Notes_add.this, "���ʧ��", Toast.LENGTH_LONG);
					finish();
				}else{
					setTitle("�ռ���ӳɹ�!");
					Intent intent = new Intent(Notes_add.this, ex_notes.class);
					startActivity(intent);
					// ���ٵ�ǰ��ͼ
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

				loadImage();// Ϊgalleryװ��ͼƬ
				initImageChooseDialog();// ��ʼ��imageChooseDialog
				imageChooseDialog.show();
			}
		});

	}

	public void loadImage() {
		if (imageChooseView == null) {
			LayoutInflater li = LayoutInflater.from(Notes_add.this);
			imageChooseView = li.inflate(R.layout.choice_img, null);

			// ͨ����Ⱦxml�ļ����õ�һ����ͼ��View�������õ����View�����Gallery
			gallery = (Gallery) imageChooseView.findViewById(R.id.gallery);
			// ΪGalleryװ��ͼƬ
			gallery.setAdapter(new ImageAdapter(this));
			gallery.setSelection(images.length / 2);
			
			is = (ImageSwitcher) imageChooseView.findViewById(R.id.imageswitch);
			is.setFactory(this);
			is.setInAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_in));
			// ж��ͼƬ�Ķ���Ч��
			is.setOutAnimation(AnimationUtils.loadAnimation(this,
					android.R.anim.fade_out));
			gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// ��ǰ��ͷ��λ��Ϊѡ�е�λ��
					currentImagePosition = arg2;
					// ΪImageSwitcher����ͼ��
					is.setImageResource(images[arg2 % images.length]);

				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
		}

	}

//�Զ���Gallery��������

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


		// gallery������������õ�image

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
			builder.setTitle("��ѡ������").setView(imageChooseView)
					.setPositiveButton("ȷ��",
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
							}).setNegativeButton("ȡ��",
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



	 //�õ�����

	public  String returnDate() {
		// ��������
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return sDateFormat.format(new java.util.Date());
	}
	
	

	 // ���˳���ʱ�򣬻�����Դ

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
