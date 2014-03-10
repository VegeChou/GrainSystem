package com.aisino.grain.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.aisino.grain.GrainApplication;
import com.aisino.grain.R;
import com.aisino.grain.model.rest.RestWebServiceAdapter;

public class SystemConfigActivity extends BaseActivity {
	private Button mSaveButton = null;					//���水ť
	private Button mSwitchButton = null;				//���ذ�ť
	private boolean mSwitchFlag = false;				//���ر�־
	
	private SharedPreferences sharedPreferences = null;	//����״̬
	private Editor editor = null;						//SharedPreferences�༭��
	
	
	private String mServerURLAddress = null;			//������URL��ַ
	private int mTimeOut = -1;							//ͨ�ų�ʱ
	public static boolean SWITCH = false;				//���濪��״̬
	
	private EditText mServerAddressEditText = null;		//��������ַ�༭��
	private EditText mTimeOutEditText = null;			//ͨ�ų�ʱ�༭��
	
	private GrainApplication mGrainApplication = null;
	
	private RestWebServiceAdapter mRestWebServiceAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_system_config);
		super.onCreate(savedInstanceState);
		
		InitCtrl();
	}
	
	private void InitCtrl() {
		mRestWebServiceAdapter = RestWebServiceAdapter.getInstance(this);
		mGrainApplication = (GrainApplication)getApplication();
		
		mSaveButton = (Button)findViewById(R.id.system_config_btn_save);
		mSaveButton.setOnClickListener(new SaveButtonOnClickListener());
		mSwitchButton = (Button)findViewById(R.id.system_config_btn_switch);
		mSwitchButton.setOnClickListener(new SwitchButtonOnClickListener());
		
		sharedPreferences = getSharedPreferences("System_Config", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//��ȡ�༭��
		
		mServerAddressEditText = (EditText)findViewById(R.id.system_config_et_server_url_address);
		mServerAddressEditText.addTextChangedListener(new ServerAddressWatcher());
		mTimeOutEditText = (EditText)findViewById(R.id.system_config_et_communication_time_out);
		mTimeOutEditText.addTextChangedListener(new TimeOutWatcher());
		
		LoadConfigInfo();	//����������Ϣ
		
	}
	
	class SaveButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			
			editor.putString("Server_URL_Address", mServerURLAddress);
			editor.putInt("Time_Out", mTimeOut);
			
			editor.commit();
			
			mRestWebServiceAdapter.UpdateURL();
			mPopupWindowDialog.showMessage(getString(R.string.Save_Success));
		}
	}
	
	class SwitchButtonOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (mSwitchFlag) {
				mSwitchButton.setText("��");
				mSwitchFlag = false;
				SWITCH = false;
				mGrainApplication.SWITCH = false;
				editor.putBoolean("Net_Switch",SWITCH);
			} else {
				mSwitchButton.setText("��");
				mSwitchFlag = true;
				SWITCH = true;
				mGrainApplication.SWITCH = true;
				editor.putBoolean("Net_Switch",SWITCH);
			}
			editor.commit();
		}
	}
	
	class ServerAddressWatcher implements TextWatcher{
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			mServerURLAddress = mServerAddressEditText.getText().toString();
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
	}
	
	class TimeOutWatcher implements TextWatcher{
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			mTimeOut = Integer.valueOf(mTimeOutEditText.getText().toString());
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
	}
	
	@Override
	protected void onResume() {
		LoadConfigInfo();
		super.onResume();
	}
	
	private void LoadConfigInfo() {
		mServerURLAddress = sharedPreferences.getString("Server_URL_Address", "http://localhost/MobileAndroidService.svc");
		mTimeOut = sharedPreferences.getInt("Time_Out", 0);
		SWITCH = sharedPreferences.getBoolean("Net_Switch", false);
		
		mServerAddressEditText.setText(mServerURLAddress);
		mTimeOutEditText.setText(String.valueOf(mTimeOut));
		if (SWITCH) {
			mSwitchButton.setText("��");
		} else {
			mSwitchButton.setText("��");
		}
	}
}
