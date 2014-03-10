/**
 * 
 */
package com.aisino.grain.service;

import com.aisino.grain.model.Constants;
import com.aisino.grain.model.rfid.RfidAdapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author zwz
 * @date 2013-12-10
 * @description
 *
 */
public class ReadCardService extends Service {
	private static final String TAG = "ReadCardService";
	
	private RfidAdapter mRfidAdapter = null;
	private String mNewCardID = null;
	private String mOldCardID = null;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		setForeground(true);
		mRfidAdapter = RfidAdapter.getInstance(getApplicationContext());
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (true) {
					mNewCardID = mRfidAdapter.HasCard();
					if (mNewCardID == null) {
						Log.v(TAG, "mNewCardID == null");
						mOldCardID = null;
						mNewCardID = null;
					} else {
						if (mOldCardID == null || !mNewCardID.equals(mOldCardID)) {
							Log.v(TAG, "Send ACTION_READ_CARD");
							Intent intent = new Intent();
							intent.setAction(Constants.ACTION_READ_CARD);
							sendBroadcast(intent);
							mOldCardID = mNewCardID;
						}
					}
					try {
						Thread.sleep(500);	//Ã¿500ms¶ÁÒ»´Î¿¨
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		super.onCreate();
	}

}
