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
	
	private RestWebServiceAdapter mRestWebServiceAdapter = null;		//webservice
	private UpdateUserInfoRequest mUpdateUserInfoRequest = null;		//webservice同步账户请求
	private UpdateWareHouseRequest mUpdateWareHouseRequest = null;		//webservice同步仓库请求
	private AssayIndexTypeRequest mAssayIndexTypeRequest = null;		//webservice同步质检指标请求
	private UpdateGrainTypeRequest mUpdateGrainTypeRequest = null;		//webservice同步粮食品种请求
	
	private List<UserLoginInfo> mUserLoginInfolList = null;				//UserLoginInfo列表，存放同步返回账户信息
	private Dao<AcountInfo, Integer> mAcountInfoDao =  null;			//账户信息Dao
	private AcountInfo mAcountInfo = null;								//AcountInfo对象
	
	private List<WareHouse> mWareHouseList = null;						//WareHouse列表，存放同步返回仓库信息
	private Dao<Warehouse, Integer> mWarehouseDao =  null;				//仓库信息Dao
	private Warehouse mWarehouse = null;								//Warehouse对象
	
	private List<AssayIndex> mAssayIndexList= null;						//AssayIndex列表，存放同步返回质检指标
	private Dao<QualityIndex, Integer> mQualityInfoDao =  null;			//质检信息Dao
	private QualityIndex mQualityIndex = null;							//QualityIndex对象
	
//	List<BusinessTypeBean> lBusinessTypeBeans= null;					//BusinessTypeBean列表，存放解析的同步返回业务类型
//	private Dao<BusinessType, Integer> mBusinessTypeDao =  null;		//业务类型Dao
//	private BusinessType mBusinessType = null;							//BusinessType对象
	
	private List<GrainType> mGrainTypelList= null;						//GrainType列表，存放同步返回粮食品种信息
	private Dao<GrainTypeDB, Integer> mGrainTypeDao =  null;			//粮食信息Dao
	private GrainTypeDB mGrainType = null;								//GrainTypeBean对象
	
	private Handler mHandler = null;									//监听同步消息
	private String mSyncResult = "";									//存放同步结果
	
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
	
    // BtnCancel的监听器类   
    class BtnCancelDialogListener implements DialogInterface.OnClickListener {   
        @Override  
        public void onClick(DialogInterface dialog, int which) {   
            // 点击取消停止数据同步
            dialog.cancel();   
        }   
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
					Looper.prepare();
					//同步数据处理
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
    
    private void SyncAllInfo() {
		SyncWarehouseInfo();
//		SyncBusinessType();
		SyncGrainType();
		SyncQualityIndex();
		SyncAcountInfo();
    }
    
    private boolean SyncWarehouseInfo() {
		Message StartMessage= new Message();	//同步仓库信息开始
		Message StopMessage = new Message();	//同步仓库信息结束
		StartMessage.what = Constants.START_WAREHOUSE_INFO;
		StopMessage.what = Constants.STOP_WAREHOUSE_INFO;
		mHandler.sendMessage(StartMessage);
		
		mUpdateWareHouseRequest = new UpdateWareHouseRequest();
		mWareHouseList = mRestWebServiceAdapter.Rest(mUpdateWareHouseRequest);
    	
    	if (mWareHouseList == null) {
    		StopMessage.obj = "同步仓库信息失败";
    		mHandler.sendMessage(StopMessage);
			return false;
		}
    	
    	for (int i = 0; i < mWareHouseList.size(); i++) {
//    	for (int i = 0; i < 4; i++) {
    		//解析数据转化为AcountInfo对象
    		mWarehouse.setWarehouse_id(mWareHouseList.get(i).getID());
    		mWarehouse.setWarehouse_name(mWareHouseList.get(i).getName());
    		
//    		//test数据
//    		mWarehouse.setWarehouse_id(i);
//    		mWarehouse.setWarehouse_name("仓库"+i);
    		try {
    			mWarehouseDao.createOrUpdate(mWarehouse);
			} catch (SQLException e) {
				StopMessage.obj = "同步仓库信息失败";
				mHandler.sendMessage(StopMessage);
				return false;
			}
		}
    	StopMessage.obj = "同步仓库信息成功";
    	mHandler.sendMessage(StopMessage);
    	return true;
    }
    /**
     * 业务类型暂不同步，后期需要再添加
     * @return
     */
//    private boolean SyncBusinessType() {
//    	Message StartMessage= new Message();	//同步业务种类开始
//		Message StopMessage = new Message();	//同步业务种类结束
//		StartMessage.what = Constants.START_BUSINESS_TYPE;
//		StopMessage.what = Constants.STOP_BUSINESS_TYPE;
//		mHandler.sendMessage(StartMessage);
//		
//    	mSynchBusinessTypeXml = xmlManager.CreateSynchBusinessTypeXml();
//    	mReturnBusinessTypeXml = mWebService.WebServiceXml(mSynchBusinessTypeXml);			//通过WebSerivce返回业务类型同步信息
//    	if (mReturnBusinessTypeXml == null) {
//    		StopMessage.obj = "同步业务种类失败";
//    		mHandler.sendMessage(StopMessage);
//			return false;
//		}
//    	
//    	lBusinessTypeBeans = xmlManager.AnalyseReturnBusinessType(mReturnBusinessTypeXml);
//    	for(int i = 0;i < lBusinessTypeBeans.size();i++){
//			//解析数据转化为BusinessType对象
//			mBusinessType.setBusiness_type_id(lBusinessTypeBeans.get(i).getBusinessType_id());
//			mBusinessType.setBusiness_type_name(lBusinessTypeBeans.get(i).getBusinessType_name());
//			
//			try {
//				mBusinessTypeDao.createOrUpdate(mBusinessType);
//			} catch (SQLException e) {
//				e.printStackTrace(); 
//				StopMessage.obj = "同步业务种类失败";
//				mHandler.sendMessage(StopMessage);
//				return false;
//			}
//		}
//    	StopMessage.obj = "同步业务种类成功";
//    	mHandler.sendMessage(StopMessage);
//    	return true;
//    }
    
    private boolean SyncGrainType() {
    	Message StartMessage= new Message();	//同步粮食品种开始
		Message StopMessage = new Message();	//同步粮食品种结束
		StartMessage.what = Constants.START_GRAIN_TYPE;
		StopMessage.what = Constants.STOP_GRAIN_TYPE;
		mHandler.sendMessage(StartMessage);
		
		mUpdateGrainTypeRequest = new UpdateGrainTypeRequest();
		mGrainTypelList = mRestWebServiceAdapter.Rest(mUpdateGrainTypeRequest);
    	
		if (mGrainTypelList == null) {
    		StopMessage.obj = "同步粮食品种失败";
    		mHandler.sendMessage(StopMessage);
			return false;
		}
		
    	for(int i = 0;i < mGrainTypelList.size();i++){
			//解析数据转化为GrainType对象
			mGrainType.setGrain_type_id(mGrainTypelList.get(i).getID());
			mGrainType.setGrain_type_name(mGrainTypelList.get(i).getName());
			
			try {
				mGrainTypeDao.createOrUpdate(mGrainType);
			} catch (SQLException e) {
				StopMessage.obj = "同步粮食品种失败";
				mHandler.sendMessage(StopMessage);
				return false;
			}
		}
    	StopMessage.obj = "同步粮食品种成功";
    	mHandler.sendMessage(StopMessage);
    	return true;
    }
    
    private boolean SyncQualityIndex() {
    	Message StartMessage= new Message();	//同步化验指标开始
		Message StopMessage = new Message();	//同步化验指标结束
		StartMessage.what = Constants.START_QUALITY_INDEX;
		StopMessage.what = Constants.STOP_QUALITY_INDEX;
		mHandler.sendMessage(StartMessage);
		
		mAssayIndexTypeRequest = new AssayIndexTypeRequest();
		mAssayIndexList = mRestWebServiceAdapter.Rest(mAssayIndexTypeRequest);
    	if (mAssayIndexList == null) {
    		StopMessage.obj = "同步化验指标失败";
    		mHandler.sendMessage(StopMessage);
			return false;
		}
    	
		for(int i = 0;i < mAssayIndexList.size();i++){
			//解析数据转化为QualityIndex对象
			mQualityIndex.setQuality_index_id(mAssayIndexList.get(i).getID());
			mQualityIndex.setQuality_index_name(mAssayIndexList.get(i).getName());
			
			try {
				mQualityInfoDao.createOrUpdate(mQualityIndex);
			} catch (SQLException e) {
				StopMessage.obj = "同步化验指标失败";
				mHandler.sendMessage(StopMessage);
				return false;
			}
		}
		StopMessage.obj = "同步化验指标成功";
    	mHandler.sendMessage(StopMessage);
		return true;
    }
    
    private boolean SyncAcountInfo() {
    	Message StartMessage= new Message();	//同步用户信息开始
		Message StopMessage = new Message();	//同步用户信息结束
		StartMessage.what = Constants.START_ACOUNT_INFO;
		StopMessage.what = Constants.STOP_ACOUNT_INFO;
		mHandler.sendMessage(StartMessage);
		
		mUpdateUserInfoRequest = new UpdateUserInfoRequest();
		mUserLoginInfolList = mRestWebServiceAdapter.Rest(mUpdateUserInfoRequest);
    	
    	if (mUserLoginInfolList == null) {
    		StopMessage.obj = "同步用户信息失败";
			mHandler.sendMessage(StopMessage);
			return false;
		}
    	
    	for (int i = 0; i < mUserLoginInfolList.size(); i++) {
//    	for (int i = 0; i < 2; i++) {
    		//解析数据转化为AcountInfo对象
    		mAcountInfo.setAcount_name(mUserLoginInfolList.get(i).getLoginName());
    		mAcountInfo.setPassword(mUserLoginInfolList.get(i).getPassword());
    		mAcountInfo.setTag_id(mUserLoginInfolList.get(i).getUserRFID());
    		mAcountInfo.setOperation_code(mUserLoginInfolList.get(i).getUserRight());
    		mAcountInfo.setWahouse_id(mUserLoginInfolList.get(i).getWareHouse());
    		mAcountInfo.setActivate_flag(mUserLoginInfolList.get(i).getUserRFIDState());
    		mAcountInfo.setUser_id(mUserLoginInfolList.get(i).getUserID());
    		
//    		//test数据
//    		mAcountInfo.setAcount_name("admin"+i);
//    		mAcountInfo.setPassword("admin"+i);
//    		mAcountInfo.setTag_id("123456789");
//    		mAcountInfo.setOperation_code("1,2,3");
//    		mAcountInfo.setWahouse_id("4,5,6");
//    		mAcountInfo.setActivate_flag(1);	//全部设置为激活状态？
    		try {
				mAcountInfoDao.createOrUpdate(mAcountInfo);
			} catch (SQLException e) {
				StopMessage.obj = "同步用户信息失败";
				mHandler.sendMessage(StopMessage);
				return false;
			}
		}
    	StopMessage.obj = "同步用户信息成功";
    	mHandler.sendMessage(StopMessage);
    	return true;
	}
}  
