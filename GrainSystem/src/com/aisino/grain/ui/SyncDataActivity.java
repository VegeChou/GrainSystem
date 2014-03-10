package com.aisino.grain.ui;

import java.sql.SQLException;
import java.util.List;

import com.aisino.grain.R;
import com.aisino.grain.beans.AssayIndex;
import com.aisino.grain.beans.AssayIndexTypeRequest;
import com.aisino.grain.beans.GrainType;
import com.aisino.grain.beans.UpdateGrainTypeRequest;
import com.aisino.grain.beans.UpdateUserInfoRequest;
import com.aisino.grain.beans.UpdateWareHouseRequest;
import com.aisino.grain.beans.UserLoginInfo;
import com.aisino.grain.beans.WareHouse;
import com.aisino.grain.model.Constants;
import com.aisino.grain.model.db.AcountInfo;
import com.aisino.grain.model.db.GrainTypeDB;
import com.aisino.grain.model.db.QualityIndex;
import com.aisino.grain.model.db.Warehouse;
import com.aisino.grain.model.rest.RestWebServiceAdapter;
import com.j256.ormlite.dao.Dao;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

public class SyncDataActivity extends CustomMenuActivity {
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
	
	private RestWebServiceAdapter mRestWebServiceAdapter = null;		//webservice
	private UpdateUserInfoRequest mUpdateUserInfoRequest = null;		//webserviceͬ���˻�����
	private UpdateWareHouseRequest mUpdateWareHouseRequest = null;		//webserviceͬ���ֿ�����
	private AssayIndexTypeRequest mAssayIndexTypeRequest = null;		//webserviceͬ���ʼ�ָ������
	private UpdateGrainTypeRequest mUpdateGrainTypeRequest = null;		//webserviceͬ����ʳƷ������
	
	private List<UserLoginInfo> mUserLoginInfolList = null;				//UserLoginInfo�б����ͬ�������˻���Ϣ
	private Dao<AcountInfo, Integer> mAcountInfoDao =  null;			//�˻���ϢDao
	private AcountInfo mAcountInfo = null;								//AcountInfo����
	
	private List<WareHouse> mWareHouseList = null;						//WareHouse�б����ͬ�����زֿ���Ϣ
	private Dao<Warehouse, Integer> mWarehouseDao =  null;				//�ֿ���ϢDao
	private Warehouse mWarehouse = null;								//Warehouse����
	
	private List<AssayIndex> mAssayIndexList= null;						//AssayIndex�б����ͬ�������ʼ�ָ��
	private Dao<QualityIndex, Integer> mQualityInfoDao =  null;			//�ʼ���ϢDao
	private QualityIndex mQualityIndex = null;							//QualityIndex����
	
//	List<BusinessTypeBean> lBusinessTypeBeans= null;					//BusinessTypeBean�б���Ž�����ͬ������ҵ������
//	private Dao<BusinessType, Integer> mBusinessTypeDao =  null;		//ҵ������Dao
//	private BusinessType mBusinessType = null;							//BusinessType����
	
	private List<GrainType> mGrainTypelList= null;						//GrainType�б����ͬ��������ʳƷ����Ϣ
	private Dao<GrainTypeDB, Integer> mGrainTypeDao =  null;			//��ʳ��ϢDao
	private GrainTypeDB mGrainType = null;								//GrainTypeBean����
	
	private Handler mHandler = null;									//����ͬ����Ϣ
	private String mSyncResult = "";									//���ͬ�����
	
	private Context mContext = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_sync_data);
		super.onCreate(savedInstanceState);
		InitCtrl();
//		LoginActivity.ActivityList.add(this);
	}
	
//	@Override
//	protected void onDestroy() {
//		LoginActivity.ActivityList.remove(this);
//		super.onDestroy();
//	}
	
	private void InitCtrl(){
		mContext = this;
		mRestWebServiceAdapter = RestWebServiceAdapter.getInstance(mContext);
		
		mAcountInfoDao = getHelper().getAcountInfoDao();
		mAcountInfo = new AcountInfo();
		mWarehouseDao = getHelper().getWarehouseDao();
		mWarehouse = new Warehouse();
		mQualityInfoDao = getHelper().getQualityIndexDao();
		mQualityIndex = new QualityIndex();
//		mBusinessTypeDao = getHelper().getBusinessTypeDao();
//		mBusinessType = new BusinessType();
		mGrainTypeDao = getHelper().getGrainTypeDao();
		mGrainType = new GrainTypeDB();
		
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
	
    // BtnCancel�ļ�������   
    class BtnCancelDialogListener implements DialogInterface.OnClickListener {   
        @Override  
        public void onClick(DialogInterface dialog, int which) {   
            // ���ȡ��ֹͣ����ͬ��
            dialog.cancel();   
        }   
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
					Looper.prepare();
					//ͬ�����ݴ���
		    		if (mAllSelectedChecked) {
						SyncAllInfo();
					} else {
						if (mWarehouseInfoChecked) {
							SyncWarehouseInfo();
						}
//						if (mBusinessTypeChecked) {
//							SyncBusinessType();
//						}
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
    
    private void SyncAllInfo() {
		SyncWarehouseInfo();
//		SyncBusinessType();
		SyncGrainType();
		SyncQualityIndex();
		SyncAcountInfo();
    }
    
    private boolean SyncWarehouseInfo() {
		Message StartMessage= new Message();	//ͬ���ֿ���Ϣ��ʼ
		Message StopMessage = new Message();	//ͬ���ֿ���Ϣ����
		StartMessage.what = Constants.START_WAREHOUSE_INFO;
		StopMessage.what = Constants.STOP_WAREHOUSE_INFO;
		mHandler.sendMessage(StartMessage);
		
		mUpdateWareHouseRequest = new UpdateWareHouseRequest();
		mWareHouseList = mRestWebServiceAdapter.Rest(mUpdateWareHouseRequest);
    	
    	if (mWareHouseList == null) {
    		StopMessage.obj = "ͬ���ֿ���Ϣʧ��";
    		mHandler.sendMessage(StopMessage);
			return false;
		}
    	
    	for (int i = 0; i < mWareHouseList.size(); i++) {
//    	for (int i = 0; i < 4; i++) {
    		//��������ת��ΪAcountInfo����
    		mWarehouse.setWarehouse_id(mWareHouseList.get(i).getID());
    		mWarehouse.setWarehouse_name(mWareHouseList.get(i).getName());
    		
//    		//test����
//    		mWarehouse.setWarehouse_id(i);
//    		mWarehouse.setWarehouse_name("�ֿ�"+i);
    		try {
    			mWarehouseDao.createOrUpdate(mWarehouse);
			} catch (SQLException e) {
				StopMessage.obj = "ͬ���ֿ���Ϣʧ��";
				mHandler.sendMessage(StopMessage);
				return false;
			}
		}
    	StopMessage.obj = "ͬ���ֿ���Ϣ�ɹ�";
    	mHandler.sendMessage(StopMessage);
    	return true;
    }
    /**
     * ҵ�������ݲ�ͬ����������Ҫ�����
     * @return
     */
//    private boolean SyncBusinessType() {
//    	Message StartMessage= new Message();	//ͬ��ҵ�����࿪ʼ
//		Message StopMessage = new Message();	//ͬ��ҵ���������
//		StartMessage.what = Constants.START_BUSINESS_TYPE;
//		StopMessage.what = Constants.STOP_BUSINESS_TYPE;
//		mHandler.sendMessage(StartMessage);
//		
//    	mSynchBusinessTypeXml = xmlManager.CreateSynchBusinessTypeXml();
//    	mReturnBusinessTypeXml = mWebService.WebServiceXml(mSynchBusinessTypeXml);			//ͨ��WebSerivce����ҵ������ͬ����Ϣ
//    	if (mReturnBusinessTypeXml == null) {
//    		StopMessage.obj = "ͬ��ҵ������ʧ��";
//    		mHandler.sendMessage(StopMessage);
//			return false;
//		}
//    	
//    	lBusinessTypeBeans = xmlManager.AnalyseReturnBusinessType(mReturnBusinessTypeXml);
//    	for(int i = 0;i < lBusinessTypeBeans.size();i++){
//			//��������ת��ΪBusinessType����
//			mBusinessType.setBusiness_type_id(lBusinessTypeBeans.get(i).getBusinessType_id());
//			mBusinessType.setBusiness_type_name(lBusinessTypeBeans.get(i).getBusinessType_name());
//			
//			try {
//				mBusinessTypeDao.createOrUpdate(mBusinessType);
//			} catch (SQLException e) {
//				e.printStackTrace(); 
//				StopMessage.obj = "ͬ��ҵ������ʧ��";
//				mHandler.sendMessage(StopMessage);
//				return false;
//			}
//		}
//    	StopMessage.obj = "ͬ��ҵ������ɹ�";
//    	mHandler.sendMessage(StopMessage);
//    	return true;
//    }
    
    private boolean SyncGrainType() {
    	Message StartMessage= new Message();	//ͬ����ʳƷ�ֿ�ʼ
		Message StopMessage = new Message();	//ͬ����ʳƷ�ֽ���
		StartMessage.what = Constants.START_GRAIN_TYPE;
		StopMessage.what = Constants.STOP_GRAIN_TYPE;
		mHandler.sendMessage(StartMessage);
		
		mUpdateGrainTypeRequest = new UpdateGrainTypeRequest();
		mGrainTypelList = mRestWebServiceAdapter.Rest(mUpdateGrainTypeRequest);
    	
		if (mGrainTypelList == null) {
    		StopMessage.obj = "ͬ����ʳƷ��ʧ��";
    		mHandler.sendMessage(StopMessage);
			return false;
		}
		
    	for(int i = 0;i < mGrainTypelList.size();i++){
			//��������ת��ΪGrainType����
			mGrainType.setGrain_type_id(mGrainTypelList.get(i).getID());
			mGrainType.setGrain_type_name(mGrainTypelList.get(i).getName());
			
			try {
				mGrainTypeDao.createOrUpdate(mGrainType);
			} catch (SQLException e) {
				StopMessage.obj = "ͬ����ʳƷ��ʧ��";
				mHandler.sendMessage(StopMessage);
				return false;
			}
		}
    	StopMessage.obj = "ͬ����ʳƷ�ֳɹ�";
    	mHandler.sendMessage(StopMessage);
    	return true;
    }
    
    private boolean SyncQualityIndex() {
    	Message StartMessage= new Message();	//ͬ������ָ�꿪ʼ
		Message StopMessage = new Message();	//ͬ������ָ�����
		StartMessage.what = Constants.START_QUALITY_INDEX;
		StopMessage.what = Constants.STOP_QUALITY_INDEX;
		mHandler.sendMessage(StartMessage);
		
		mAssayIndexTypeRequest = new AssayIndexTypeRequest();
		mAssayIndexList = mRestWebServiceAdapter.Rest(mAssayIndexTypeRequest);
    	if (mAssayIndexList == null) {
    		StopMessage.obj = "ͬ������ָ��ʧ��";
    		mHandler.sendMessage(StopMessage);
			return false;
		}
    	
		for(int i = 0;i < mAssayIndexList.size();i++){
			//��������ת��ΪQualityIndex����
			mQualityIndex.setQuality_index_id(mAssayIndexList.get(i).getID());
			mQualityIndex.setQuality_index_name(mAssayIndexList.get(i).getName());
			
			try {
				mQualityInfoDao.createOrUpdate(mQualityIndex);
			} catch (SQLException e) {
				StopMessage.obj = "ͬ������ָ��ʧ��";
				mHandler.sendMessage(StopMessage);
				return false;
			}
		}
		StopMessage.obj = "ͬ������ָ��ɹ�";
    	mHandler.sendMessage(StopMessage);
		return true;
    }
    
    private boolean SyncAcountInfo() {
    	Message StartMessage= new Message();	//ͬ���û���Ϣ��ʼ
		Message StopMessage = new Message();	//ͬ���û���Ϣ����
		StartMessage.what = Constants.START_ACOUNT_INFO;
		StopMessage.what = Constants.STOP_ACOUNT_INFO;
		mHandler.sendMessage(StartMessage);
		
		mUpdateUserInfoRequest = new UpdateUserInfoRequest();
		mUserLoginInfolList = mRestWebServiceAdapter.Rest(mUpdateUserInfoRequest);
    	
    	if (mUserLoginInfolList == null) {
    		StopMessage.obj = "ͬ���û���Ϣʧ��";
			mHandler.sendMessage(StopMessage);
			return false;
		}
    	
    	for (int i = 0; i < mUserLoginInfolList.size(); i++) {
//    	for (int i = 0; i < 2; i++) {
    		//��������ת��ΪAcountInfo����
    		mAcountInfo.setAcount_name(mUserLoginInfolList.get(i).getLoginName());
    		mAcountInfo.setPassword(mUserLoginInfolList.get(i).getPassword());
    		mAcountInfo.setTag_id(mUserLoginInfolList.get(i).getUserRFID());
    		mAcountInfo.setOperation_code(mUserLoginInfolList.get(i).getUserRight());
    		mAcountInfo.setWahouse_id(mUserLoginInfolList.get(i).getWareHouse());
    		mAcountInfo.setActivate_flag(mUserLoginInfolList.get(i).getUserRFIDState());
    		mAcountInfo.setUser_id(mUserLoginInfolList.get(i).getUserID());
    		
//    		//test����
//    		mAcountInfo.setAcount_name("admin"+i);
//    		mAcountInfo.setPassword("admin"+i);
//    		mAcountInfo.setTag_id("123456789");
//    		mAcountInfo.setOperation_code("1,2,3");
//    		mAcountInfo.setWahouse_id("4,5,6");
//    		mAcountInfo.setActivate_flag(1);	//ȫ������Ϊ����״̬��
    		try {
				mAcountInfoDao.createOrUpdate(mAcountInfo);
			} catch (SQLException e) {
				StopMessage.obj = "ͬ���û���Ϣʧ��";
				mHandler.sendMessage(StopMessage);
				return false;
			}
		}
    	StopMessage.obj = "ͬ���û���Ϣ�ɹ�";
    	mHandler.sendMessage(StopMessage);
    	return true;
	}
}  
