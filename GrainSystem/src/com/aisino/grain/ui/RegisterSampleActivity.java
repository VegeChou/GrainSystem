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
	
	private String mCreateDate = null;		//��������
	private String mEnrollNumber = null;	//���۱��
	private String mGoodsOwner = null;		//��������
	private String mVehiclePlate = null;	//��������
	private String mSampleType = null;		//��ƷƷ��
	private int mSampleCount = 1;			//��Ʒ����
	private int mGrainTypeNumber = 0;		//��ʳƷ�ֱ��
	
	private Dao<GrainTypeDB, Integer> mGrainTypeDBDao =  null;	//��ʳƷ����ϢDao
	
	private Task mTask = null;
	
	private RestWebServiceAdapter mRestWebServiceAdapter = null;
	private RegisterSampleResponse mRegisterSampleResponse = null;
	
	private SharedPreferences mLoginSharedPreferences = null;	//�����¼��Ϣ
	private int mCurrentUserID = -1;							//��ǰ��¼�û�ID
	
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
				mSampleCountEditText.setError("������1~99����");
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
			//����Ǥ������
			if (mReadCardSigned == Constants.CARD) {
				//��������
				//��ӡ��Ʒ��Ϣ
				Toast.makeText(getApplicationContext(), "���ڴ�ӡ��Ʒ��Ϣ", Toast.LENGTH_LONG).show();
				
				mReadCardSigned = Constants.INIT;
				return;
			}
			
			//��������
			RegisterSampleRequest registerSampleRequest = new RegisterSampleRequest();
			//��ȡ��ǰ��¼�û�ID
			mCurrentUserID = mLoginSharedPreferences.getInt("current_user_id", -1);
			if (mCurrentUserID == -1) {
				mPopupWindowDialog.showMessage("�Ǽ�ʧ��,��ͬ���˻���Ϣ");
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
				mPopupWindowDialog.showMessage("�Ǽ���Ʒ����������Ϊ��");
			} else {
				if (mRegisterSampleResponse.getResponseResult()) {
					mPopupWindowDialog.showMessage("�Ǽ���Ʒ�ɹ�");
					//��ӡ��Ʒ��Ϣ
					Toast.makeText(getApplicationContext(), "���ڴ�ӡ��Ʒ��Ϣ", Toast.LENGTH_LONG).show();
				} else {
					mPopupWindowDialog.showMessage("�Ǽ���Ʒʧ��");
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
		//��ȡ����
		readCard = mRfidAdapter.HasCard();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("����ʧ��");
			return;
		}
		//��ȡ��������
		readCard = mRfidAdapter.getOwnerName();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("������ȡ��������ʧ��");
			return;
		}
		mGoodsOwner = readCard;
		//��ȡ������
		readCard = mRfidAdapter.getCarNum();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("������ȡ������ʧ��");
			return;
		}
		mVehiclePlate = readCard;
		//��ȡ���۱��
		readCard = mRfidAdapter.getOperateNum();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("������ȡ���۱��ʧ��");
			return;
		}
		mEnrollNumber = readCard.trim();
		//��ȡϵͳ����
		mCreateDate = mYear + "-" + (mMonth+1) + "-" + mDay;
		//��ȡ��ƷƷ��
		readCard = mRfidAdapter.getVarietyNo();
		if (readCard == null) {
			mPopupWindowDialog.showMessage("������ȡ��ʳƷ�� ���ʧ��");
			return;
		}
		mGrainTypeNumber = Integer.valueOf(readCard);
		mSampleType = GetGrainTypeName(mGrainTypeNumber);
		
		//������ʾ��Ϣ
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
				mPopupWindowDialog.showMessage("û���ҵ���Ӧ����ʳ����");
				name = "";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return name;
	}
}
