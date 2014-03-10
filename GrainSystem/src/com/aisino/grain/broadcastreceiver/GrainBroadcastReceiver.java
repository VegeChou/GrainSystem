package com.aisino.grain.broadcastreceiver;


import java.io.File;

import com.aisino.grain.model.Constants;
import com.aisino.grain.ui.BaseActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Parcelable;
import android.util.Log;

public class GrainBroadcastReceiver extends BroadcastReceiver {
	private static final String TAG = "GrainBroadcastReceiver";
	private static int count = 0;
	private IReadCard mIReadCard = null;
	
	private static GrainBroadcastReceiver mInstanceBroadcastReceiver = null;
	
	public IReadCard getIReadCard() {
		return mIReadCard;
	}

	public void setIReadCard(IReadCard mIReadCard) {
		this.mIReadCard = mIReadCard;
	}

	public static GrainBroadcastReceiver getInstance(){
		if (mInstanceBroadcastReceiver == null) {
			mInstanceBroadcastReceiver = new GrainBroadcastReceiver();
		}
		return mInstanceBroadcastReceiver;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "onReceive");
		
		if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
//			Toast.makeText(context, "关闭屏幕广播", Toast.LENGTH_LONG).show();
		}
		if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
			Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);    
            if (null != parcelableExtra) {    
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;    
                State state = networkInfo.getState();  
                boolean isConnected = state==State.CONNECTED;//当然，这边可以更精确的确定状态  
                if(isConnected) {
//                	Toast.makeText(context, "已连接", Toast.LENGTH_LONG).show();
                	BaseActivity.NET_CON_FLAG = true;
                	count++;
                	Log.v(TAG, "start count = " + count);
                	//启动监听线程
                } else {
//                	Toast.makeText(context, "已断开", Toast.LENGTH_LONG).show();
                	BaseActivity.NET_CON_FLAG = false;
				}
            }    
		}
		if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) { //这个监听wifi的打开与关闭，与wifi的连接无关  
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);   
            switch (wifiState) {   
	            case WifiManager.WIFI_STATE_DISABLED:
//	            	Toast.makeText(context, "WIFI_STATE_DISABLED", Toast.LENGTH_LONG).show();
                	BaseActivity.NET_CON_FLAG = false;
	                break;   
	            case WifiManager.WIFI_STATE_DISABLING:
//	            	Toast.makeText(context, "WIFI_STATE_DISABLED", Toast.LENGTH_LONG).show();
	                break;   
            }   
        }
		
		if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED) || intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
			File downLoadApk = new File(Environment.getExternalStorageDirectory(),Constants.APP_NAME);
			if(downLoadApk.exists()){
				downLoadApk.delete();
				Log.i(TAG, "DownLoadApkFile was deleted!");
			}
		}
		
		//已经读到卡广播
		if (intent.getAction().equals(Constants.ACTION_READ_CARD)) {
			Log.i(TAG, "Action = ACTION_READ_CARD!");
			if (mIReadCard != null) {
				Log.i(TAG, "IReadCard.ReadCard()");
				mIReadCard.ReadCard();
			}
		}
		Log.i(TAG, "BroadcastReceiver finish");
	}
}
