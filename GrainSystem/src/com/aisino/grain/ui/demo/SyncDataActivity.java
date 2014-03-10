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
	private Button mStartSyncButton = null;								//��ʼͬ����ť
	private ProgressDialog mProgressDialog = null;						//ͬ��������
	private CheckBox mAllSelectedCheckBox = null;						//ȫѡCheckBox
	private CheckBox mWarehouseInfoCheckBox = null;						//�ֿ���ϢCheckBox
	private CheckBox mBusinessTypeCheckBox = null;						//ҵ������CheckBox
	private CheckBox mGrainTypeCheckBox = null;							//��ʳƷ��CheckBox
	private CheckBox mQualityIndexCheckBox = null;						//����ָ��CheckBox
	private CheckBox mAcountInfoCheckBox = null;						//�û���ϢCheckBox
	
	private boolean mAllSelectedChecked = false;						//ȫѡcheckbox�Ƿ�ѡ��
	private boolean mWarehouseInfoChecked = false;						//�ֿ���Ϣcheckbox�Ƿ�ѡ��
	private boolean mBusinessTypeChecked = false;						//ҵ������checkbox�Ƿ�ѡ��
	private boolean mGrainTypeChecked = false;							//��ʳƷ��checkbox�Ƿ�ѡ��
	private boolean mQualityIndexChecked = false;						//����ָ��checkbox�Ƿ�ѡ��
	private boolean mAcountInfoChecked = false;							//�û���Ϣcheckbox�Ƿ�ѡ��
	
	private Handler mHandler = null;									//����ͬ����Ϣ
	private String mSyncResult = "";									//���ͬ�����
	
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
    		mProgressDialog.setMessage("����ͬ����Ϣ,���Ժ�....."); 
    		mProgressDialog.setIndeterminate(false);   
    		mProgressDialog.setCancelable(false);
    		mProgressDialog.setProgress(100); 
    		mProgressDialog.show();   
    		
    		mHandler = new Handler(){
    			public void handleMessage(android.os.Message msg) {
    				super.handleMessage(msg);
    				switch(msg.what){
	    				case Constants.START_WAREHOUSE_INFO:
	    					mProgressDialog.setMessage("����ͬ���ֿ���Ϣ.....");
	    					break;
	    				case Constants.STOP_WAREHOUSE_INFO:
	    					mSyncResult = mSyncResult + msg.obj.toString() + "\n";
	    					mProgressDialog.setProgress(20);
	    					break;
	    				case Constants.START_BUSINESS_TYPE:
	    					mProgressDialog.setMessage("����ͬ��ҵ������....."); 
	    					break;
	    				case Constants.STOP_BUSINESS_TYPE:
	    					mSyncResult = mSyncResult + msg.obj.toString() + "\n";
	    					mProgressDialog.setProgress(40);
	    					break;
    					case Constants.START_GRAIN_TYPE:
    						mProgressDialog.setMessage("����ͬ����ʳƷ��....."); 
    						break;
    					case Constants.STOP_GRAIN_TYPE:
    						mSyncResult = mSyncResult + msg.obj.toString() + "\n";
    						mProgressDialog.setProgress(60);
    						break;
    					case Constants.START_QUALITY_INDEX:
    						mProgressDialog.setMessage("����ͬ������ָ��....."); 
    						break;
    					case Constants.STOP_QUALITY_INDEX:
    						mSyncResult = mSyncResult + msg.obj.toString() + "\n";
    						mProgressDialog.setProgress(80);
    						break;
    					case Constants.START_ACOUNT_INFO:
    						mProgressDialog.setMessage("����ͬ���û���Ϣ....."); 
    						break;
    					case Constants.STOP_ACOUNT_INFO:
    						mSyncResult = mSyncResult + msg.obj.toString() + "\n";
    						mProgressDialog.setProgress(100);
    						break;
    					case Constants.STOP_SYNC:
    						mProgressDialog.cancel();
    						//��ʾͬ�����
    						mPopupWindowDialog.showMessage("ͬ�����:\n"+mSyncResult);
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
						//ͬ�����ݴ���
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
		    		
		    		//ͬ��������Ϣ
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
		Message StartMessage= new Message();	//ͬ���ֿ���Ϣ��ʼ
		Message StopMessage = new Message();	//ͬ���ֿ���Ϣ����
		StartMessage.what = Constants.START_WAREHOUSE_INFO;
		StopMessage.what = Constants.STOP_WAREHOUSE_INFO;
		mHandler.sendMessage(StartMessage);
			
		Thread.sleep(1000);
    	StopMessage.obj = "ͬ���ֿ���Ϣ�ɹ�";
    	mHandler.sendMessage(StopMessage);
    	return true;
    }
    
    private boolean SyncBusinessType() throws InterruptedException {
    	Message StartMessage= new Message();	//ͬ��ҵ�����࿪ʼ
		Message StopMessage = new Message();	//ͬ��ҵ���������
		StartMessage.what = Constants.START_BUSINESS_TYPE;
		StopMessage.what = Constants.STOP_BUSINESS_TYPE;
		mHandler.sendMessage(StartMessage);
		
		Thread.sleep(1000);
    	StopMessage.obj = "ͬ��ҵ������ɹ�";
    	mHandler.sendMessage(StopMessage);
    	return true;
    }
    
    private boolean SyncGrainType() throws InterruptedException {
    	Message StartMessage= new Message();	//ͬ����ʳƷ�ֿ�ʼ
		Message StopMessage = new Message();	//ͬ����ʳƷ�ֽ���
		StartMessage.what = Constants.START_GRAIN_TYPE;
		StopMessage.what = Constants.STOP_GRAIN_TYPE;
		mHandler.sendMessage(StartMessage);
		
		Thread.sleep(1000);
    	StopMessage.obj = "ͬ����ʳƷ�ֳɹ�";
    	mHandler.sendMessage(StopMessage);
    	return true;
    }
    
    private boolean SyncQualityIndex() throws InterruptedException {
    	Message StartMessage= new Message();	//ͬ������ָ�꿪ʼ
		Message StopMessage = new Message();	//ͬ������ָ�����
		StartMessage.what = Constants.START_QUALITY_INDEX;
		StopMessage.what = Constants.STOP_QUALITY_INDEX;
		mHandler.sendMessage(StartMessage);
		
		Thread.sleep(1000);
		StopMessage.obj = "ͬ������ָ��ɹ�";
    	mHandler.sendMessage(StopMessage);
		return true;
    }
    
    private boolean SyncAcountInfo() throws InterruptedException {
    	Message StartMessage= new Message();	//ͬ���û���Ϣ��ʼ
		Message StopMessage = new Message();	//ͬ���û���Ϣ����
		StartMessage.what = Constants.START_ACOUNT_INFO;
		StopMessage.what = Constants.STOP_ACOUNT_INFO;
		mHandler.sendMessage(StartMessage);
		
		Thread.sleep(1000);
    	StopMessage.obj = "ͬ���û���Ϣ�ɹ�";
    	mHandler.sendMessage(StopMessage);
    	return true;
	}
}  

