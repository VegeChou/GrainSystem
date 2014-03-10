package com.aisino.grain.ui;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.aisino.grain.beans.RegisterSampleRequest;
import com.aisino.grain.beans.RegisterSampleResponse;
import com.aisino.grain.beans.Task;
import com.aisino.grain.model.Constants;
import com.aisino.grain.model.db.GrainTypeDB;
import com.aisino.grain.model.rest.RestWebServiceAdapter;
import com.aisino.grain.model.rfid.RfidAdapter;
import com.aisino.grain.ui.util.PopupWindowDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class RegisterSampleActivity extends CustomMenuActivity {
	private static final boolean DEBUG = false;
	private Context mContext = null;
	
	private EditText mCreateDateEditText = null;
	private TextView mEnrollNumberTextView = null;
	private TextView mGoodsOwnerTextView = null;
	private TextView mVehiclePlateTextView = null;
	private TextView mSampleTypeTextView = null;
	private EditText mSampleCountEditText = null;
	
	private Button mRegisterSampleButton = null;
	private Button mCancelButton = null;
	private Button mAddCountButton = null;
	private Button mSubCountButton = null;
	
	private String mCreateDate = null;		//创建日期
	private String mEnrollNumber = null;	//报港编号
	private String mGoodsOwner = null;		//货主姓名
	private String mVehiclePlate = null;	//车船牌照
	private String mSampleType = null;		//样品品种
	private int mSampleCount = 1;			//样品数量
	private int mGrainTypeNumber = 0;		//粮食品种编号
	
	private Dao<GrainTypeDB, Integer> mGrainTypeDBDao =  null;	//粮食品种信息Dao
	
	private Task mTask = null;
	
	private RestWebServiceAdapter mRestWebServiceAdapter = null;
	private RegisterSampleResponse mRegisterSampleResponse = null;
	
	private SharedPreferences mLoginSharedPreferences = null;	//保存登录信息
	private int mCurrentUserID = -1;							//当前登录用户ID
	
	private RfidAdapter mRfidAdapter = null;
	private int mReadCardSigned = -1;
	
	private Calendar mCalendar = null;
	private int mYear = 0;
	private int mMonth = 0;
	private int mDay = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_register_sample);
		super.onCreate(savedInstanceState);
//		LoginActivity.ActivityList.add(this);
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
		mRestWebServiceAdapter = RestWebServiceAdapter.getInstance(mContext);
		mPopupWindowDialog = new PopupWindowDialog(mContext);
		mLoginSharedPreferences = getSharedPreferences("LoginInformation", Context.MODE_PRIVATE);
		mRfidAdapter = RfidAdapter.getInstance(mContext);
		
		mGrainTypeDBDao = getHelper().getGrainTypeDao();
		
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
		mRegisterSampleButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
		
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
			//处理扦样任务
			if (mReadCardSigned == Constants.CARD) {
				//读卡流程
				//打印样品信息
				Toast.makeText(getApplicationContext(), "正在打印样品信息", Toast.LENGTH_LONG).show();
				
				mReadCardSigned = Constants.INIT;
				return;
			}
			
			//网络流程
			RegisterSampleRequest registerSampleRequest = new RegisterSampleRequest();
			//获取当前登录用户ID
			mCurrentUserID = mLoginSharedPreferences.getInt("current_user_id", -1);
			if (mCurrentUserID == -1) {
				mPopupWindowDialog.showMessage("登记失败,请同步账户信息");
				return;
			}
			registerSampleRequest.setUserID(mCurrentUserID);
			registerSampleRequest.setTaskNumber(mTask.getTaskNumber());
			registerSampleRequest.setSampleCount(mSampleCount);
			
			if (DEBUG) {
				mRegisterSampleResponse = TestRegisterSampleResponse();
			} else {
				mRegisterSampleResponse = mRestWebServiceAdapter.Rest(registerSampleRequest);
			}
			
			if (mRegisterSampleResponse == null) {
				mPopupWindowDialog.showMessage("登记样品服务器返回为空");
			} else {
				if (mRegisterSampleResponse.getResponseResult()) {
					mPopupWindowDialog.showMessage("登记样品成功");
					//打印样品信息
					Toast.makeText(getApplicationContext(), "正在打印样品信息", Toast.LENGTH_LONG).show();
				} else {
					mPopupWindowDialog.showMessage("登记样品失败");
				}
				
			}
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
		mRegisterSampleButton.setBackgroundResource(R.drawable.corner_btn_selector);
	}
	
	private RegisterSampleResponse TestRegisterSampleResponse() {
		RegisterSampleResponse registerSampleResponse = null;
//		return registerSampleResponse;
		
		registerSampleResponse = new RegisterSampleResponse();
		
//		registerSampleResponse.setResponseResult(false);
//		registerSampleResponse.setSampleNumber(null);
		
		registerSampleResponse.setResponseResult(true);
		registerSampleResponse.setSampleNumber("12345");
		
		return registerSampleResponse;
	}
	private void ReadCardRegisterSample() {
		String readCard = null;
		//读取卡号
		readCard = mRfidAdapter.HasCard();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("读卡失败");
			return;
		}
		//读取货主姓名
		readCard = mRfidAdapter.getOwnerName();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("读卡获取货主姓名失败");
			return;
		}
		mGoodsOwner = readCard;
		//读取车船号
		readCard = mRfidAdapter.getCarNum();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("读卡获取车船号失败");
			return;
		}
		mVehiclePlate = readCard;
		//读取报港编号
		readCard = mRfidAdapter.getOperateNum();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("读卡获取报港编号失败");
			return;
		}
		mEnrollNumber = readCard.trim();
		//获取系统日期
		mCreateDate = mYear + "-" + (mMonth+1) + "-" + mDay;
		//获取样品品种
		readCard = mRfidAdapter.getVarietyNo();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("读卡获取粮食品种 编号失败");
			return;
		}
		mGrainTypeNumber = Integer.valueOf(readCard);
		mSampleType = GetGrainTypeName(mGrainTypeNumber);
		
		//设置显示信息
		mCreateDateEditText.setText(mCreateDate);
		mEnrollNumberTextView.setText(mEnrollNumber);
		mGoodsOwnerTextView.setText(mGoodsOwner);
		mVehiclePlateTextView.setText(mVehiclePlate);
		mSampleTypeTextView.setText(mSampleType);
		mRegisterSampleButton.setEnabled(true);
		mRegisterSampleButton.setBackgroundResource(R.drawable.corner_btn_selector);
		mReadCardSigned = Constants.CARD;
	}
	
	private String GetGrainTypeName(int grainNumber) {
		String name = null;
		QueryBuilder<GrainTypeDB,Integer> queryBuilder = mGrainTypeDBDao.queryBuilder();
		try {
			queryBuilder.where().eq("grain_type_id", String.valueOf(grainNumber));
			List<GrainTypeDB> grainTypes = queryBuilder.query();
			if (grainTypes.size() > 0) {
				name = grainTypes.get(0).getGrain_type_name();
			} else {
				mPopupWindowDialog.showMessage("没有找到对应的粮食名称");
				name = "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}
}
