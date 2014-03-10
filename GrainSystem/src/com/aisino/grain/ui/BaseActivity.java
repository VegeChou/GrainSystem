package com.aisino.grain.ui;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.aisino.grain.AppManagement;
import com.aisino.grain.broadcastreceiver.GrainBroadcastReceiver;
import com.aisino.grain.model.Constants;
import com.aisino.grain.model.db.DataHelper;
import com.aisino.grain.ui.util.PopupWindowDialog;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

public class BaseActivity extends OrmLiteBaseActivity<DataHelper> {
//	private static final String TAG = "BaseActivity";
	
	public static boolean NET_AVILIABE = false;				//网络是否可用,返回给其他Activity
	public static boolean THREAD_FLAG = true;				//线程运行标志
	public static boolean NET_CON_FLAG = false;				//是否连接网络标志
	
	//提示对话框
	protected PopupWindowDialog mPopupWindowDialog = null;
	protected Dialog mProcessDialog = null;					//处理进度条
	protected GrainBroadcastReceiver mGrainBroadcastReceiver = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppManagement.getInstance().addActivity(this);
		
		InitCtrl();
	}
	
	private void InitCtrl() {
		mGrainBroadcastReceiver = GrainBroadcastReceiver.getInstance();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		filter.addAction(Constants.ACTION_READ_CARD);
		registerReceiver(mGrainBroadcastReceiver, filter);
		
		
		mPopupWindowDialog = new PopupWindowDialog(this);
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(mGrainBroadcastReceiver);
		super.onDestroy();
	}
}


