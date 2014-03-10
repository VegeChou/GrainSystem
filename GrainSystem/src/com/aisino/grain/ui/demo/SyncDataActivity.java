package com.aisino.grain.ui.demo;

import com.aisino.grain.R;
import com.aisino.grain.model.Constants;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SyncDataActivity extends BaseActivity {
	private Button mStartSyncButton = null;								//开始同步按钮
	private ProgressDialog mProgressDialog = null;						//同步进度条
	private CheckBox mAllSelectedCheckBox = null;						//全选CheckBox
	private CheckBox mWarehouseInfoCheckBox = null;						//仓库信息CheckBox
	private CheckBox mBusinessTypeCheckBox = null;						//业务种类CheckBox
	private CheckBox mGrainTypeCheckBox = null;							//粮食品种CheckBox
	private CheckBox mQualityIndexCheckBox = null;						//化验指标CheckBox
	private CheckBox mAcountInfoCheckBox = null;						//用户信息CheckBox
	
	private boolean mAllSelectedChecked = false;						//全选checkbox是否选中
	private boolean mWarehouseInfoChecked = false;						//仓库信息checkbox是否选中
	private boolean mBusinessTypeChecked = false;						//业务种类checkbox是否选中
	private boolean mGrainTypeChecked = false;							//粮食品种checkbox是否选中
	private boolean mQualityIndexChecked = false;						//化验指标checkbox是否选中
	private boolean mAcountInfoChecked = false;							//用户信息checkbox是否选中
	
	private Handler mHandler = null;									//监听同步消息
	private String mSyncResult = "";									//存放同步结果
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_sync_data);
		super.onCreate(savedInstanceState);
		InitCtrl();
	}
	
	private void InitCtrl(){
		mStartSyncButton = (Button)findViewById(R.id.sync_data_btn_start_sync);
		mStartSyncButton.setOnClickListener(new StartSyncOnClickListener());
		mAllSelectedCheckBox = (CheckBox) findViewById(R.id.sync_data_cb_all_selected);
		mAllSelectedCheckBox.setOnCheckedChangeListener(new AllSelectedOnCheckedChangeListener());
		mWarehouseInfoCheckBox = (CheckBox) findViewById(R.id.sync_data_cb_warehouse_info);
		mWarehouseInfoCheckBox.setOnCheckedChangeListener(new WarehouseInfoOnCheckedChangeListener());
		mBusinessTypeCheckBox = (CheckBox) findViewById(R.id.sync_data_cb_business_type);
		mBusinessTypeCheckBox.setOnCheckedChangeListener(new BusinessTypeOnCheckedChangeListener()); 
		mGrainTypeCheckBox = (CheckBox) findViewById(R.id.sync_data_cb_grain_type);
		mGrainTypeCheckBox.setOnCheckedChangeListener(new GrainTypeOnCheckedChangeListener());
		mQualityIndexCheckBox = (CheckBox) findViewById(R.id.sync_data_cb_quality_index);
		mQualityIndexCheckBox.setOnCheckedChangeListener(new QualityIndexOnCheckedChangeListener());
		mAcountInfoCheckBox = (CheckBox) findViewById(R.id.sync_data_cb_acount_info);
		mAcountInfoCheckBox.setOnCheckedChangeListener(new AcountInfoOnCheckedChangeListener()); 
	}
	
    class StartSyncOnClickListener implements OnClickListener{
    	@Override
    	public void onClick(View v) {
    		mProgressDialog = new ProgressDialog(SyncDataActivity.this);
    		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);   
    		mProgressDialog.setIcon(R.drawable.ic_launcher);   
    		mProgressDialog.setMessage("正在同步信息,请稍后....."); 
    		mProgressDialog.setIndeterminate(false);   
    		mProgressDialog.setCancelable(false);
    		mProgressDialog.setProgress(100); 
    		mProgressDialog.show();   
    		
    		mHandler = new Handler(){
    			public void handleMessage(android.os.Message msg) {
    				super.handleMessage(msg);
    				switch(msg.what){
	    				case Constants.START_WAREHOUSE_INFO:
	    					mProgressDialog.setMessage("正在同步仓库信息.....");
	    					break;
	    				case Constants.STOP_WAREHOUSE_INFO:
	    					mSyncResult = mSyncResult + msg.obj.toString() + "\n";
	    					mProgressDialog.setProgress(20);
	    					break;
	    				case Constants.START_BUSINESS_TYPE:
	    					mProgressDialog.setMessage("正在同步业务种类....."); 
	    					break;
	    				case Constants.STOP_BUSINESS_TYPE:
	    					mSyncResult = mSyncResult + msg.obj.toString() + "\n";
	    					mProgressDialog.setProgress(40);
	    					break;
    					case Constants.START_GRAIN_TYPE:
    						mProgressDialog.setMessage("正在同步粮食品种....."); 
    						break;
    					case Constants.STOP_GRAIN_TYPE:
    						mSyncResult = mSyncResult + msg.obj.toString() + "\n";
    						mProgressDialog.setProgress(60);
    						break;
    					case Constants.START_QUALITY_INDEX:
    						mProgressDialog.setMessage("正在同步化验指标....."); 
    						break;
    					case Constants.STOP_QUALITY_INDEX:
    						mSyncResult = mSyncResult + msg.obj.toString() + "\n";
    						mProgressDialog.setProgress(80);
    						break;
    					case Constants.START_ACOUNT_INFO:
    						mProgressDialog.setMessage("正在同步用户信息....."); 
    						break;
    					case Constants.STOP_ACOUNT_INFO:
    						mSyncResult = mSyncResult + msg.obj.toString() + "\n";
    						mProgressDialog.setProgress(100);
    						break;
    					case Constants.STOP_SYNC:
    						mProgressDialog.cancel();
    						//显示同步结果
    						mPopupWindowDialog.showMessage("同步结果:\n"+mSyncResult);
    						mSyncResult = "";
    						break;
    					default:
    						break;
    				}
    			}
    		};
    		
    		new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Looper.prepare();
						//同步数据处理
			    		if (mAllSelectedChecked) {
							SyncAllInfo();
						} else {
							if (mWarehouseInfoChecked) {
								SyncWarehouseInfo();
							}
							if (mBusinessTypeChecked) {
								SyncBusinessType();
							}
							if (mGrainTypeChecked) {
								SyncGrainType();
							}
							if (mQualityIndexChecked) {
								SyncQualityIndex();
							}
							if (mAcountInfoChecked) {
								SyncAcountInfo();
							}
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
		    		
		    		//同步结束信息
		    		Message message = new Message();
		    		message.what = Constants.STOP_SYNC; 
		    		mHandler.sendMessage(message);
		    		Looper.loop();
				}
			}).start();
    	}
    }
    
    class AllSelectedOnCheckedChangeListener implements OnCheckedChangeListener{
    	@Override
    	public void onCheckedChanged(CompoundButton buttonView,
    			boolean isChecked) {
    		mAllSelectedChecked = isChecked;
    		if (mAllSelectedChecked) {
    			mWarehouseInfoCheckBox.setChecked(true);
    			mBusinessTypeCheckBox.setChecked(true);
    			mGrainTypeCheckBox.setChecked(true);
    			mQualityIndexCheckBox.setChecked(true);
    			mAcountInfoCheckBox.setChecked(true);
    		} else {
    			mWarehouseInfoCheckBox.setChecked(false);
    			mBusinessTypeCheckBox.setChecked(false);
    			mGrainTypeCheckBox.setChecked(false);
    			mQualityIndexCheckBox.setChecked(false);
    			mAcountInfoCheckBox.setChecked(false);
			}
    	}
    }
    
    class WarehouseInfoOnCheckedChangeListener implements OnCheckedChangeListener{
    	@Override
    	public void onCheckedChanged(CompoundButton buttonView,
    			boolean isChecked) {
    		mWarehouseInfoChecked = isChecked;
    		if (!mWarehouseInfoChecked) {
    			mAllSelectedCheckBox.setChecked(false);
    		}
    	}
    }
    
    class BusinessTypeOnCheckedChangeListener implements OnCheckedChangeListener{
    	@Override
    	public void onCheckedChanged(CompoundButton buttonView,
    			boolean isChecked) {
    		mBusinessTypeChecked = isChecked;
    		if (!mBusinessTypeChecked) {
    			mAllSelectedCheckBox.setChecked(false);
    		}
    	}
    }
    
    class GrainTypeOnCheckedChangeListener implements OnCheckedChangeListener{
    	@Override
    	public void onCheckedChanged(CompoundButton buttonView,
    			boolean isChecked) {
    		mGrainTypeChecked = isChecked;
    		if (!mGrainTypeChecked) {
    			mAllSelectedCheckBox.setChecked(false);
    		}
    	}
    }
    
    class QualityIndexOnCheckedChangeListener implements OnCheckedChangeListener{
    	@Override
    	public void onCheckedChanged(CompoundButton buttonView,
    			boolean isChecked) {
    		mQualityIndexChecked = isChecked;
    		if (!mQualityIndexChecked) {
    			mAllSelectedCheckBox.setChecked(false);
    		}
    	}
    }
    
    class AcountInfoOnCheckedChangeListener implements OnCheckedChangeListener{
    	@Override
    	public void onCheckedChanged(CompoundButton buttonView,
    			boolean isChecked) {
    		mAcountInfoChecked = isChecked;
    		if (!mAcountInfoChecked) {
				mAllSelectedCheckBox.setChecked(false);
			}
    	}
    }
    
    private void SyncAllInfo() throws InterruptedException {
		SyncWarehouseInfo();
		SyncBusinessType();
		SyncGrainType();
		SyncQualityIndex();
		SyncAcountInfo();
    }
    
    private boolean SyncWarehouseInfo() throws InterruptedException {
		Message StartMessage= new Message();	//同步仓库信息开始
		Message StopMessage = new Message();	//同步仓库信息结束
		StartMessage.what = Constants.START_WAREHOUSE_INFO;
		StopMessage.what = Constants.STOP_WAREHOUSE_INFO;
		mHandler.sendMessage(StartMessage);
			
		Thread.sleep(1000);
    	StopMessage.obj = "同步仓库信息成功";
    	mHandler.sendMessage(StopMessage);
    	return true;
    }
    
    private boolean SyncBusinessType() throws InterruptedException {
    	Message StartMessage= new Message();	//同步业务种类开始
		Message StopMessage = new Message();	//同步业务种类结束
		StartMessage.what = Constants.START_BUSINESS_TYPE;
		StopMessage.what = Constants.STOP_BUSINESS_TYPE;
		mHandler.sendMessage(StartMessage);
		
		Thread.sleep(1000);
    	StopMessage.obj = "同步业务种类成功";
    	mHandler.sendMessage(StopMessage);
    	return true;
    }
    
    private boolean SyncGrainType() throws InterruptedException {
    	Message StartMessage= new Message();	//同步粮食品种开始
		Message StopMessage = new Message();	//同步粮食品种结束
		StartMessage.what = Constants.START_GRAIN_TYPE;
		StopMessage.what = Constants.STOP_GRAIN_TYPE;
		mHandler.sendMessage(StartMessage);
		
		Thread.sleep(1000);
    	StopMessage.obj = "同步粮食品种成功";
    	mHandler.sendMessage(StopMessage);
    	return true;
    }
    
    private boolean SyncQualityIndex() throws InterruptedException {
    	Message StartMessage= new Message();	//同步化验指标开始
		Message StopMessage = new Message();	//同步化验指标结束
		StartMessage.what = Constants.START_QUALITY_INDEX;
		StopMessage.what = Constants.STOP_QUALITY_INDEX;
		mHandler.sendMessage(StartMessage);
		
		Thread.sleep(1000);
		StopMessage.obj = "同步化验指标成功";
    	mHandler.sendMessage(StopMessage);
		return true;
    }
    
    private boolean SyncAcountInfo() throws InterruptedException {
    	Message StartMessage= new Message();	//同步用户信息开始
		Message StopMessage = new Message();	//同步用户信息结束
		StartMessage.what = Constants.START_ACOUNT_INFO;
		StopMessage.what = Constants.STOP_ACOUNT_INFO;
		mHandler.sendMessage(StartMessage);
		
		Thread.sleep(1000);
    	StopMessage.obj = "同步用户信息成功";
    	mHandler.sendMessage(StopMessage);
    	return true;
	}
}  

