package com.aisino.grain.ui.demo;

import java.util.Calendar;
import java.util.Random;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.aisino.grain.R;
import com.aisino.grain.beans.Task;
import com.aisino.grain.model.rfid.RfidAdapter;
import com.aisino.grain.ui.util.PopupWindowDialog;

public class RegisterSampleActivity extends BaseActivity {
	private Context mContext = null;
	
	private EditText mCreateDateEditText = null;
	private TextView mEnrollNumberTextView = null;
	private TextView mGoodsOwnerTextView = null;
	private TextView mVehiclePlateTextView = null;
	private TextView mSampleTypeTextView = null;
	private EditText mSampleCountEditText = null;
	
	private int mSampleCount = 1;			//样品数量
	
	private static String[] GoodsOwner = {"散户","市级储备","省级储备","中央储备","洋河酒厂","五得利面粉厂","宿迁直属库"};
	private static String[] VehiclePlate = {"京A-02009","沪B-12345","使D-88888","警S-00001","津C-66666","苏U-00110","川Q-11111"};
	private static String[] GrainTypeName = {"粳稻","中晚籼稻","硬质白小麦","软质白小麦","混合麦","玉米","大豆"};
	private static String[] EnrollNumber = {"BH00001","BH00002","BH00003","BH00004","BH00005","BH00006","BH00007"};
	
	private Random mRandom = null;
	
	private Button mRegisterSampleButton = null;
	private Button mCancelButton = null;
	private Button mAddCountButton = null;
	private Button mSubCountButton = null;
	
	private Task mTask = null;
	
	private RfidAdapter mRfidAdapter = null;
	
	private Calendar mCalendar = null;
	private int mYear = 0;
	private int mMonth = 0;
	private int mDay = 0;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_register_sample);
		super.onCreate(savedInstanceState);
		InitCtrl();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SCAN) {
			ReadCardRegisterSample();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void InitCtrl() {
		mContext = this;
		mRfidAdapter = RfidAdapter.getInstance(mContext);
		mPopupWindowDialog = new PopupWindowDialog(mContext);
		mCalendar = Calendar.getInstance();
		mYear = mCalendar.get(Calendar.YEAR);
		mMonth = mCalendar.get(Calendar.MONTH) ; 
		mDay = mCalendar.get(Calendar.DAY_OF_MONTH);
		mCreateDateEditText = (EditText)findViewById(R.id.register_sample_et_create_date);
		mCreateDateEditText.setOnClickListener(new CreateDateOnClickListner());
		mEnrollNumberTextView = (TextView)findViewById(R.id.register_sample_tv_enroll_number);
		mGoodsOwnerTextView = (TextView)findViewById(R.id.register_sample_tv_goods_owner);
		mVehiclePlateTextView = (TextView)findViewById(R.id.register_sample_tv_vehicle_plate);
		mSampleTypeTextView = (TextView)findViewById(R.id.register_sample_tv_sample_type);
		mSampleCountEditText = (EditText)findViewById(R.id.register_sample_et_sample_count);
		mSampleCountEditText.addTextChangedListener(new SampleCountWatcher());
		
		mRegisterSampleButton = (Button)findViewById(R.id.register_sample_btn_register);
		mRegisterSampleButton.setOnClickListener(new RegisterSampleOnClickListner());
		
		mCancelButton = (Button)findViewById(R.id.register_sample_btn_cancel);
		mCancelButton.setOnClickListener(new CanceleOnClickListner());
		mAddCountButton = (Button)findViewById(R.id.register_sample_btn_add);
		mAddCountButton.setOnClickListener(new AddCountOnClickListner());
		mSubCountButton = (Button)findViewById(R.id.register_sample_btn_sub);
		mSubCountButton.setOnClickListener(new SubCountOnClickListner());
		
		mRegisterSampleButton.setEnabled(false);
		
		mTask = TaskingActivity.GetTask();
		if (mTask == null) {
			return;
		}
		DisplayTaskInfo();
	}
	
	class SampleCountWatcher implements TextWatcher{
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			String count = mSampleCountEditText.getText().toString();
			if (count.equals("")) {
				mSampleCount = 1;
				return;
			}
			mSampleCount = Integer.valueOf(count);
			if (mSampleCount > 99 || mSampleCount < 1) {
				mSampleCountEditText.setError("请输入1~99整数");
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
	}
	
	class RegisterSampleOnClickListner implements OnClickListener{
		@Override
		public void onClick(View v) {
			//打印样品信息
			Toast.makeText(getApplicationContext(), "正在打印样品信息", Toast.LENGTH_LONG).show();
			mPopupWindowDialog.showMessage("登记成功");
		}
	}
	
	class CanceleOnClickListner implements OnClickListener{
		@Override
		public void onClick(View v) {
			finish();
		}
	}
	
	class CreateDateOnClickListner implements OnClickListener{
		@Override
		public void onClick(View v) {
			DatePickerDialog datePickerDialog = new DatePickerDialog(mContext,new CreateDateOnDateSetListner(),mYear,mMonth,mDay);
			datePickerDialog.show();
		}
	}
	
	class CreateDateOnDateSetListner implements OnDateSetListener{
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mCreateDateEditText.setText(year + "-" + (monthOfYear+1) + "-" +dayOfMonth);
		}
	}
	
	class AddCountOnClickListner implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (mSampleCount != 99) {
				mSampleCount++;
			}
			mSampleCountEditText.setText(String.valueOf(mSampleCount));
		}
	}
	
	class SubCountOnClickListner implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (mSampleCount != 1) {
				mSampleCount--;
			}
			mSampleCountEditText.setText(String.valueOf(mSampleCount));
		}
	}
	
	private void DisplayTaskInfo() {
		mCreateDateEditText.setText(mTask.getCreateDate());
		mEnrollNumberTextView.setText(mTask.getEnrollNumber());
		mGoodsOwnerTextView.setText(mTask.getGoodsOwner());
		mVehiclePlateTextView.setText(mTask.getVehiclePlate());
		mSampleTypeTextView.setText(mTask.getGrainTypeName());
		mSampleCountEditText.setText(String.valueOf(mSampleCount));
		
		mRegisterSampleButton.setEnabled(true);
	}
	
	private void ReadCardRegisterSample() {
		String readCard = null;
		readCard = mRfidAdapter.HasCard();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("读卡失败");
			return;
		}
		mRandom = new Random();
		mCreateDateEditText.setText(mYear + "-" + (mMonth+1) + "-" + mDay);
		mEnrollNumberTextView.setText(EnrollNumber[mRandom.nextInt(6)]);
		mGoodsOwnerTextView.setText(GoodsOwner[mRandom.nextInt(6)]);
		mVehiclePlateTextView.setText(VehiclePlate[mRandom.nextInt(6)]);
		mSampleTypeTextView.setText(GrainTypeName[mRandom.nextInt(6)]);
		mRegisterSampleButton.setEnabled(true);
	}
	
	@Override
	protected void onResume() {
		mTask = null;
		super.onResume();
	}
}
