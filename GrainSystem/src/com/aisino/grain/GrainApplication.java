package com.aisino.grain;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class GrainApplication extends Application{
//	private static final String TAG = "GrainApplication";
	private String mServerAddress = null;				//��������ַ
	private String mServerPort = null;					//�������˿�
	private int mTimeOut = -1;							//ͨ�ų�ʱ
	public boolean SWITCH = false;				//���濪��״̬
	private SharedPreferences sharedPreferences = null;	//����״̬
	
	

	public String getmServerAddress() {
		return mServerAddress;
	}

	public void setmServerAddress(String mServerAddress) {
		this.mServerAddress = mServerAddress;
	}

	public String getmServerPort() {
		return mServerPort;
	}

	public void setmServerPort(String mServerPort) {
		this.mServerPort = mServerPort;
	}

	public int getmTimeOut() {
		return mTimeOut;
	}

	public void setmTimeOut(int mTimeOut) {
		this.mTimeOut = mTimeOut;
	}

	public boolean isSWITCH() {
		return SWITCH;
	}

	public void setSWITCH(boolean sWITCH) {
		SWITCH = sWITCH;
	}
	@Override
	public void onCreate() {
		sharedPreferences = getSharedPreferences("System_Config", Context.MODE_PRIVATE);
		GetAppGlobalInfo();
		
		super.onCreate();
	}
	/**
	 * ��ȡӦ�ó���ȫ�ֱ���
	 * @param
	 * @return
	 * @date 2013-10-18
	 * @author trs
	 */
	private boolean GetAppGlobalInfo() {
		if (sharedPreferences == null) {
			return false;
		}
		mServerAddress = sharedPreferences.getString("Server_Address", "");
		mServerPort = sharedPreferences.getString("Server_Port", "");
		mTimeOut = sharedPreferences.getInt("Time_Out", 0);
		SWITCH = sharedPreferences.getBoolean("Net_Switch", false);
		
		return true;
	}
}
