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
	// ͷ��İ�ť
	ImageButton imageButton;
	// ��flag���жϰ�ť��״̬ false��ʾ�鿴����޸�״̬ true��ʾ����޸ı���״̬
	boolean flag = false;
	boolean imageChanged = false;
	boolean isDataChanged = false;

	int currentImagePosition;
	int previousImagePosition;

	String status;
	// ӵ��һ��diaryʵ�������������Intent������
	Diary diary;
	Gallery gallery;
	ImageSwitcher is;

	View numChooseView;
	View imageChooseView;

	AlertDialog imageChooseDialog;

//���е�ͼ��ͼƬ
	private int[] images = new int[] { R.drawable.img1, R.drawable.img2,
			R.drawable.img3, R.drawable.img4, R.drawable.img5, R.drawable.img6,
			R.drawable.img7, R.drawable.img8, R.drawable.img9 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.read);

		// �����ͼ
		Intent intent = getIntent();
		// ����ͼ�еõ���Ҫ��diary����
		diary = (Diary) intent.getSerializableExtra("diary");
		// ��������,���ؼ��ϸ�ֵ
		loadDiaryData();
		// ����EditText���ɱ༭
		setEditTextDisable();

		// Ϊ��ť��Ӽ�����
		btn_save.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (!flag) {
					btn_save.setText("����");
					setEditTextAble();
					flag = true;
				} else {
					// �����ݿ������������
					if (modify() == -1) {
						return;
					}
					flag = false;
					setTitle("�޸ĳɹ�");
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
						"ȷ��", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Toast.makeText(Notes_read.this,"��ɾ��",Toast.LENGTH_SHORT).show();
								delete();
								returnMain();
							}
						}).setNegativeButton("ȡ��",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).setTitle("�Ƿ�Ҫɾ��?").create().show();

			}
		});

		imageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				loadImage();// ����imageChooseView��ֻ����һ��
				initImageChooseDialog();// ����imageChooseDialog��ֻ����һ��
				imageChooseDialog.show();

			}
		});

	}

	public void returnMain() {
		Intent intent = new Intent(Notes_read.this, ex_notes.class);
		startActivity(intent);
		finish();
	}

// ��ò����ļ��еĿؼ������Ҹ��ݴ��ݹ���diary����Կؼ����и�ֵ
	public void loadDiaryData() {
		// ���EditText�ؼ�
		et_diarytitle = (EditText) findViewById(R.id.diarytitle);
		et_diarydate = (EditText) findViewById(R.id.diarydate);
		et_diarycontent = (EditText) findViewById(R.id.diarycontent);

		// ���Button�ؼ�
		btn_save = (Button) findViewById(R.id.bt_save);
		btn_return = (Button) findViewById(R.id.bt_return);
		btn_delete = (Button) findViewById(R.id.bt_delete);
		imageButton = (ImageButton) findViewById(R.id.image_button);

		// Ϊ�ؼ���ֵ
		et_diarytitle.setText(diary.diarytitle);
		et_diarydate.setText(diary.diarydate);
		et_diarycontent.setText(diary.diarycontent);
		imageButton.setImageResource(diary.imageId);
	}

//����EditTextΪ������
	private void setEditTextDisable() {
		et_diarytitle.setEnabled(false);
		et_diarydate.setEnabled(false);
		et_diarycontent.setEnabled(false);
		imageButton.setEnabled(false);
		setColorToWhite();
	}

//����EditTextΪ����״̬
	private void setEditTextAble() {
		et_diarytitle.setEnabled(true);
		et_diarydate.setEnabled(true);
		et_diarycontent.setEnabled(true);
		imageButton.setEnabled(true);
		setColorToBlack();
	}

//������ʾ��������ɫΪ��ɫ
	private void setColorToBlack() {

		et_diarytitle.setTextColor(Color.BLACK);
		et_diarydate.setTextColor(Color.BLACK);
		et_diarycontent.setTextColor(Color.BLACK);
	}

//������ʾ��������ɫΪ��ɫ
	private void setColorToWhite() {
		et_diarytitle.setTextColor(Color.WHITE);
		et_diarydate.setTextColor(Color.WHITE);
		et_diarycontent.setTextColor(Color.WHITE);
	}

//����������ݣ�����DBHelper���󣬸������ݿ�
	private int modify() {

		diary.diarytitle = et_diarytitle.getText().toString();
		diary.diarydate = et_diarydate.getText().toString();
		diary.diarycontent = et_diarycontent.getText().toString();
		if (imageChanged) {
			diary.imageId = images[currentImagePosition % images.length];
		}
		if (diary.diarytitle.trim().equals("")
				|| diary.diarydate.trim().equals("")) {
			Toast.makeText(this, "���������ڲ���Ϊ��", Toast.LENGTH_LONG).show();
			return -1;
		}
		DBHelper helper = new DBHelper(this);
		// �����ݿ�
		helper.openDatabase();
		helper.modify(diary);
		isDataChanged = true;
		return 1;
	}

	private void delete() {
		DBHelper helper = new DBHelper(this);
		// �����ݿ�
		helper.openDatabase();
		helper.delete(diary._id);

	}

//װ��ͼƬ
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
			builder.setTitle("��ѡ��ͼ��").setView(imageChooseView)
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

//�Զ���ͷ��������
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

//gallery������������õ�image
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

//���˳���ʱ�򣬻�����Դ
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