package com.aisino.grain;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class GrainApplication extends Application{
//	private static final String TAG = "GrainApplication";
	private String mServerAddress = null;				//服务器地址
	private String mServerPort = null;					//服务器端口
	private int mTimeOut = -1;							//通信超时
	public boolean SWITCH = false;				//保存开关状态
	private SharedPreferences sharedPreferences = null;	//保存状态
	
	

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
	 * 获取应用程序全局变量
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
