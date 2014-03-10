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
	private Button mSaveButton = null;					//保存按钮
	private Button mSwitchButton = null;				//开关按钮
	private boolean mSwitchFlag = false;				//开关标志
	
	private SharedPreferences sharedPreferences = null;	//保存状态
	private Editor editor = null;						//SharedPreferences编辑器
	
	
	private String mServerURLAddress = null;			//服务器URL地址
	private int mTimeOut = -1;							//通信超时
	public static boolean SWITCH = false;				//保存开关状态
	
	private EditText mServerAddressEditText = null;		//服务器地址编辑框
	private EditText mTimeOutEditText = null;			//通信超时编辑框
	
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
		editor = sharedPreferences.edit();//获取编辑器
		
		mServerAddressEditText = (EditText)findViewById(R.id.system_config_et_server_url_address);
		mServerAddressEditText.addTextChangedListener(new ServerAddressWatcher());
		mTimeOutEditText = (EditText)findViewById(R.id.system_config_et_communication_time_out);
		mTimeOutEditText.addTextChangedListener(new TimeOutWatcher());
		
		LoadConfigInfo();	//加载配置信息
		
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
				mSwitchButton.setText("关");
				mSwitchFlag = false;
				SWITCH = false;
				mGrainApplication.SWITCH = false;
				editor.putBoolean("Net_Switch",SWITCH);
			} else {
				mSwitchButton.setText("开");
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
			mSwitchButton.setText("开");
		} else {
			mSwitchButton.setText("关");
		}
	}
}
