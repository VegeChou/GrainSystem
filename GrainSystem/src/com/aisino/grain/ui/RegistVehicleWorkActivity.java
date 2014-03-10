package com.aisino.grain.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.aisino.grain.GrainApplication;
import com.aisino.grain.R;
import com.aisino.grain.beans.CheckVehicleWorkRequest;
import com.aisino.grain.beans.CheckVehicleWorkResponse;
import com.aisino.grain.beans.DeductedInfo;
import com.aisino.grain.beans.EnrollInfo;
import com.aisino.grain.beans.EnrollInfoGeneral;
import com.aisino.grain.beans.GoodsInfo;
import com.aisino.grain.beans.RegisterVehicleWorkRequest;
import com.aisino.grain.beans.RegisterVehicleWorkResponse;
import com.aisino.grain.beans.Result;
import com.aisino.grain.beans.VehicleInfo;
import com.aisino.grain.beans.WeighInfo;
import com.aisino.grain.beans.WorkInfo;
import com.aisino.grain.broadcastreceiver.IReadCard;
import com.aisino.grain.model.Constants;
import com.aisino.grain.model.SyncBlock;
import com.aisino.grain.model.db.GrainTypeDB;
import com.aisino.grain.model.db.Warehouse;
import com.aisino.grain.model.rest.RestWebServiceAdapter;
import com.aisino.grain.model.rfid.RfidAdapter;
import com.aisino.grain.ui.util.WaitDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;

public class RegistVehicleWorkActivity extends CustomMenuActivity {
	private static String TAG = "RegistVehicleWorkActivity";
	private static final boolean DEBUG = false;
	
	private RfidAdapter mRfidAdapter = null;			//rfid读卡适配器
	private Context mContext = null;					
	
	//装卸是否完成
	private RadioGroup mLoadingStatusRadioGroup = null;			
	private RadioButton mLoadingFinishRadioButton = null;	//装卸完成
//	private RadioButton mLoadingUnfinishNeedAssayRadioButton = null;	//装卸未完成，需重新化验
//	private RadioButton mLoadingUnfinishChangeWarehouseRadioButton = null;	//装卸未完成，需换仓
	
	private Button mRegistButton = null;				//登记按钮
	private Button mDeductionDiscountButton = null;		//扣量扣价按钮
	private Button mReadCardButton = null;
	private Spinner mSpinnerWarehouse = null;
	private Button mChangeWarehouseButton = null;		//调整仓库按钮
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();
	
	private Dao<Warehouse, Integer> mWarehouseDao =  null;		//仓库信息Dao
	private List<Warehouse> mWarehouselist = null;		//查询结果
	private int mSelectedID = -1;						//仓库列表选中ID
	
	private Dao<GrainTypeDB, Integer> mGrainTypeDBDao = null;
	
	private TextView mLeagalTextView = null;			//合法性编辑框
	private TextView mVarietiesTextView = null;			//品种编辑框
	private TextView mLicensePlateTextView = null;		//车牌编辑框
	private TextView mOwnersTextView = null;			//货主编辑框
	private TextView mTypeTextView = null;				//类型编辑框
	private TextView mWorkNodeTextView= null;			//环节编辑框
	private TextView mGrossWeightTextView = null;		//毛重编辑框
	private TextView mTareTextView = null;				//皮重编辑框
	private TextView mNetWeigthTextView = null;			//净重编辑框
	private TextView mDeductionTextView = null;			//扣量编辑框
	private TextView mUploadingWarehousTextView = null;	//卸粮仓库编辑框
	
	
//	private double mMoistureDeductAmountFirst = 0;						//初次水分扣量
//	private double mImpurityDeductAmountFirst = 0;						//初次杂质扣量
//	private double mMoistureDeductAmountAdjust = 0;						//水分扣量加扣
//	private double mImpurityDeductAmountAdjust = 0;						//杂质扣量加扣
//	private int mMoistureAmountFlagFirst = -1;							//初次水分扣量单位标记
//	private int mImpurityAmountFlagFirst = -1;							//初次杂质扣量单位标记
//	private int mMoistureAmountFlagAdjust = -1;							//水分扣量单位加扣标记
//	private int mImpurityAmountFlagAdjust = -1;							//杂质扣量单位加扣标记
//	private int mAdjustDeductFlag = -1;									//二次扣量标记
	
	private String mGrainTypeName = null;
	private String mBusinessTypeName = null;
	
	//业务信息
	//卡号
	private String mCardID = null;
//	//扣量扣价信息
	private DeductedInfo mAssayDeductedInfo = null;		//化验扣量扣价
//	private DeductedInfo mAdjustDeductedInfo = null;	//值仓扣量扣价
	//报港信息
	private EnrollInfoGeneral mEnrollInfo = null;
	//称重信息
	private WeighInfo mWeighInfo = null;
	
	private String mLeagal = null;						//合法性
	private int mWarehouseID = -1;						//值仓仓库ID
	private boolean mReCheck = false;					//重新校验合法性标志
	
	private RestWebServiceAdapter mRestWebServiceAdapter = null;				//REST
	CheckVehicleWorkResponse mCheckVehicleWorkResponse = null;			//解析CheckVehicleWorkMessage结果
	RegisterVehicleWorkResponse mRegisterVehicleWorkResponse = null;		//解析RegisterVehicleWorkMessageBean结果
	
	private SharedPreferences sharedPreferences = null;			//保存选中的仓库
	private Editor editor = null;								//SharedPreferences编辑器
	
	private SharedPreferences mLoginSharedPreferences = null;	//保存登录信息
	
	private GrainApplication mGrainApplication = null;
	
	private boolean mNetRegisterSuccess = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		setContentView(R.layout.activity_regist_vehicle_work);
		super.onCreate(savedInstanceState);
		InitCtrl();
//		LoginActivity.ActivityList.add(this);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	初始化
	 *
	 */
	private void InitCtrl() {
		mAssayDeductedInfo = new DeductedInfo();
//		mAdjustDeductedInfo = new DeductedInfo();
		mEnrollInfo = new EnrollInfoGeneral();
		mWeighInfo = new WeighInfo();
		
		mGrainApplication = (GrainApplication)getApplication();
		
		mRestWebServiceAdapter = RestWebServiceAdapter.getInstance(mContext);
		
		sharedPreferences = getSharedPreferences("WarehouseInfo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//获取编辑器
		mLoginSharedPreferences = getSharedPreferences("LoginInformation", Context.MODE_PRIVATE);
		
		mRfidAdapter = RfidAdapter.getInstance(mContext);
		
		mWarehouselist = new ArrayList<Warehouse>();
		mWarehouseDao = getHelper().getWarehouseDao();
		mGrainTypeDBDao = getHelper().getGrainTypeDao();
		
		mLoadingStatusRadioGroup = (RadioGroup) findViewById(R.id.regist_vehicle_work_rbtn_group_loading);
		mLoadingFinishRadioButton = (RadioButton) findViewById(R.id.regist_vehicle_work_rbtn_loading_finish);
//		mLoadingUnfinishNeedAssayRadioButton = (RadioButton) findViewById(R.id.regist_vehicle_work_rbtn_loading_unfinish_need_assay);
//		mLoadingUnfinishChangeWarehouseRadioButton = (RadioButton) findViewById(R.id.regist_vehicle_work_rbtn_loading_unfinish_change_warehouse);
		mLoadingStatusRadioGroup.setOnCheckedChangeListener(new LoadingStatusOnCheckedChangeListener());
		
		mSpinnerWarehouse = (Spinner)findViewById(R.id.regist_vehicle_work_spinner_warehouse);
		mChangeWarehouseButton = (Button)findViewById(R.id.regist_vehicle_work_btn_change_warehouse);
		mChangeWarehouseButton.setVisibility(View.INVISIBLE);
		mReadCardButton = (Button)findViewById(R.id.regist_vehicle_work_btn_read_card);
		mRegistButton = (Button)findViewById(R.id.regist_vehicle_work_btn_regist);
		SetRegistButtonInuseable();
		mDeductionDiscountButton = (Button)findViewById(R.id.regist_vehicle_work_btn_deduction_discount);
		mUploadingWarehousTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_uploading_warehouse);
		
		
		mLeagalTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_legal);
		mVarietiesTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_varieties);
		mLicensePlateTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_license_plate);
		mOwnersTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_owners);
		mTypeTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_type);
		mWorkNodeTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_link);
		mGrossWeightTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_gross_weight);
		mTareTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_tare);
		mNetWeigthTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_net_weight);
		mDeductionTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_deduction);
		
		mRegistButton.setOnClickListener(new RegistOnClickListener());
		mDeductionDiscountButton.setOnClickListener(new DeductionDiscountOnClickListener());
		mReadCardButton.setOnClickListener(new ReadCardOnClickListener());
		mChangeWarehouseButton.setOnClickListener(new ChangeWarehouseOnClickListener());
		
		InitSnipperWarehouse();
		GetConfigInfo();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	初始化值仓仓库下拉列表
	 *
	 */
	private void InitSnipperWarehouse() {
		//从数据库获取数据粮仓号,根据登录信息，显示值仓仓库列表
		
		//获取登录用户能值仓id串
		String warehouse_id = null;
		warehouse_id = mLoginSharedPreferences.getString("warehouse_id", "null");
		if (warehouse_id.equals("null")) {
//			mPopupWindowDialog.showMessage("读取值仓仓库ID失败");
			return;
		}
		//查询能值仓id串对应的仓库名称
		try {
			GenericRawResults<Object[]> rawResults = mWarehouseDao.queryRaw("select warehouse_id,warehouse_name from Warehouse where warehouse_id in ("+warehouse_id+")",new DataType[] { DataType.INTEGER,DataType.STRING });
			List<Object[]> resultsList = rawResults.getResults();
			if (resultsList.size() == 0) {
//				mPopupWindowDialog.showMessage("值仓仓库列表为空");
			}else {
				for (Object[] objects : resultsList) {
					Warehouse warehouse = new Warehouse();
					warehouse.setWarehouse_id(Integer.valueOf(objects[0].toString()));
					warehouse.setWarehouse_name(objects[1].toString());
					mWarehouselist.add(warehouse);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (mWarehouselist.size()> 0) {
			for (int i = 0; i < mWarehouselist.size(); i++) {
				listSpinnerWarehouse.add(mWarehouselist.get(i).getWarehouse_name());
			}
		}
		
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.my_spinner,listSpinnerWarehouse);  
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerWarehouse.setAdapter(adapter);
        
        //设置标题   
        mSpinnerWarehouse.setPrompt("请选择值仓仓库"); 
        mSpinnerWarehouse.setOnItemSelectedListener(new SpinnerWarehouseOnItemSelectedListener());
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SCAN) {
			//信息置位
			mChangeWarehouseButton.setVisibility(View.INVISIBLE);
			
			if (GetCardInfo()) {	//读卡信息成功
				CheckCardLeagal();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private boolean GetCardInfo() {
		//读取卡号
		String cardID = null;
		cardID = mRfidAdapter.HasCard();
		if (cardID == null) {
			mPopupWindowDialog.showMessage("未读到卡,请重新读卡");
			return false;
		}
		mCardID = cardID;
		//读取报港信息
		EnrollInfoGeneral enrollInfoCard = null;
		enrollInfoCard = mRfidAdapter.getEnrollInfo();
		if (enrollInfoCard == null) {
			mPopupWindowDialog.showMessage("未读到报港信息，请重新读卡");
			return false;
		}
		mEnrollInfo.setSinged(enrollInfoCard.getSinged());
		mEnrollInfo.setGrainType(enrollInfoCard.getGrainType());
		mEnrollInfo.setVehiceType(enrollInfoCard.getVehiceType());
		mEnrollInfo.setBusinessType(enrollInfoCard.getBusinessType());
		mEnrollInfo.setWorkWarehouseID(enrollInfoCard.getWorkWarehouseID());
		mEnrollInfo.setWorkNode(enrollInfoCard.getWorkNode());
		mEnrollInfo.setFirstWeight(enrollInfoCard.getFirstWeight());
		mEnrollInfo.setWorkNumber(enrollInfoCard.getWorkNumber());
		mEnrollInfo.setVehiclePlate(enrollInfoCard.getVehiclePlate());
		mEnrollInfo.setGoodsOwner(enrollInfoCard.getGoodsOwner());
		return true;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	读卡按钮响应实现
	 *
	 */
	class ReadCardOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (GetCardInfo()) {	//读卡信息成功
				CheckCardLeagal();
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	值仓仓库下拉菜单选中监听
	 *
	 */
	class SpinnerWarehouseOnItemSelectedListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			mSelectedID = parent.getSelectedItemPosition();
			mWarehouseID = mWarehouselist.get(mSelectedID).getWarehouse_id();
			
			editor.putInt("SelectedID", mSelectedID);
			editor.commit();
			mChangeWarehouseButton.setVisibility(View.INVISIBLE);
			if (mReCheck == true) {
				if (!RFIDIsExist()) { //卡未取走
					CheckCardLeagal();
					
				} else { //卡已取走
					ClearWindow();
				}
			}
		}
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	卸粮仓库是否包含值仓仓库（实现为值仓仓库与卸粮仓库是否相同，后期根据业务逻辑修改）
	 *
	 * @return 相同:true,不相同:false
	 */
	private boolean CheckWarehouseID() {
		if (mEnrollInfo.getWorkWarehouseID() == mWarehouseID) {
			return true;
		}
		return false;
	}
	
	private void ProcessCheckByNetResult() {
		boolean ValidResult = false;										//校验结果
		String InvalidReason = null;										//校验不合法原因
		EnrollInfo ResultEnrollInfo = null;									//报港对象信息
		WeighInfo ResultWeighinfo = null;									//称重信息
		VehicleInfo EnrollVehicleInfo = null;								//车辆信息
		WorkInfo EnrollWorkInfo = null;										//报港作业信息
		GoodsInfo EnrollGoodsInfo = null;									//货物信息
		int warnflag = -1;													//提醒标志
		
		if (mCheckVehicleWorkResponse == null) {
			//联网校验失败,读卡校验
			Toast.makeText(getApplicationContext(), "联网校验失败,自动切换为读卡校验", Toast.LENGTH_LONG).show();
			CheckByCard();
			return;
		}
		Result Result = mCheckVehicleWorkResponse.getResult();
		if (Result == null) {
			//联网校验失败,读卡校验
			Toast.makeText(getApplicationContext(), "联网校验失败,自动切换为读卡校验", Toast.LENGTH_LONG).show();
			CheckByCard();
			return;
		}
		
		//联网获取到校验结果
		ValidResult = Result.getResponseResult();
		InvalidReason = Result.getFailedReason();
		
		//获取校验结果返回信息
		ResultEnrollInfo = mCheckVehicleWorkResponse.getResultEnrollInfo();
		ResultWeighinfo = mCheckVehicleWorkResponse.getResultWeighinfo();
		
		//报港信息
		if (ResultEnrollInfo != null) {	
			//车辆信息
			EnrollVehicleInfo = ResultEnrollInfo.getEnrollVehicleInfo();
			if (EnrollVehicleInfo != null) {
				mEnrollInfo.setVehiclePlate(EnrollVehicleInfo.getVehiclePlate());
			}
			//作业信息
			EnrollWorkInfo = ResultEnrollInfo.getEnrollWorkInfo();
			if (EnrollWorkInfo != null) {
				mEnrollInfo.setBusinessType(EnrollWorkInfo.getBusinessType());
				mEnrollInfo.setWorkNode(EnrollWorkInfo.getWorkNode());
				if (EnrollWorkInfo.getWarehouseID() != null) {
					mEnrollInfo.setWorkWarehouseID(Integer.valueOf(EnrollWorkInfo.getWarehouseID()));
				} 
				mEnrollInfo.setWorkNumber(EnrollWorkInfo.getWorkNumber());
				//校验合法，判断提示标志位
				warnflag = EnrollWorkInfo.getWarnFlag();
				if (warnflag == 1) {
					mPopupWindowDialog.showMessage("审批数量:"+EnrollWorkInfo.getApprovedWeight()+",已出数量:"+EnrollWorkInfo.getCompletedWeight());
				}
			}
			//货物信息
			EnrollGoodsInfo = ResultEnrollInfo.getEnrollGoodsInfo();
			if (EnrollGoodsInfo != null) {
				mEnrollInfo.setGrainType(EnrollGoodsInfo.getGoodsKind());
				mEnrollInfo.setGoodsOwner(EnrollGoodsInfo.getGoodsOwner());
			}
		}
		//称重信息
		if (ResultWeighinfo != null) {
			mWeighInfo.setGrossWeight(ResultWeighinfo.getGrossWeight());
			mWeighInfo.setTareWeight(ResultWeighinfo.getTareWeight());
			mWeighInfo.setNetWeight(ResultWeighinfo.getNetWeight());
			mWeighInfo.setDeductWeight(ResultWeighinfo.getDeductWeight());
		}
		DisplayInfo();
		
		if (!ValidResult) {					//校验失败
			if (InvalidReason != null) {	//失败原因不为空
				if (InvalidReason.equals("不是指定仓库")) {
					mChangeWarehouseButton.setVisibility(View.VISIBLE);
					if (mWarehouselist.size()> 0) {
						mChangeWarehouseButton.setText("调整到"+mWarehouselist.get(mSelectedID).getWarehouse_name());
					}
				}
				SetCheckFalsed(InvalidReason);
			} else {
				SetCheckFalsed("联网校验失败，原因未知");
			}
		} else {
			SetCheckTrue();
			mLoadingStatusRadioGroup.clearCheck();
			mLoadingFinishRadioButton.setChecked(true);	//联网校验合法,默认设置值仓确认
		}
	}
	
	private void DisplayInfo() {
		//显示最终信息
		mBusinessTypeName = GetBusinessTypeName(mEnrollInfo.getBusinessType());
		mGrainTypeName = GetGrainTypeName(mEnrollInfo.getGrainType());
		
		mVarietiesTextView.setText(mGrainTypeName);
		mLicensePlateTextView.setText(mEnrollInfo.getVehiclePlate());
		mOwnersTextView.setText(mEnrollInfo.getGoodsOwner());
		mTypeTextView.setText(mBusinessTypeName);
		SetWorkNodeTextView();
		mGrossWeightTextView.setText(String.valueOf(mWeighInfo.getGrossWeight()));
		mTareTextView.setText(String.valueOf(mWeighInfo.getTareWeight()));
		mNetWeigthTextView.setText(String.valueOf(mWeighInfo.getNetWeight()));
		mDeductionTextView.setText(String.valueOf(mWeighInfo.getDeductWeight()));
		//找到仓库号对应仓库名称
		for (int i = 0; i < mWarehouselist.size(); i++) {
			if (mEnrollInfo.getWorkWarehouseID() == mWarehouselist.get(i).getWarehouse_id()) {
				mUploadingWarehousTextView.setText(mWarehouselist.get(i).getWarehouse_name());
				return;
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	通过联网校验合法性
	 *
	 */
	private void CheckByNet() {
		//REST发送CheckVehicleWorkRequest联网校验
		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "正在联网校验中....");
		OnCancelListener CheckByNetProcessDialogCancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//提示保存结果
				ProcessCheckByNetResult();
			}
		};
		mProcessDialog.setOnCancelListener(CheckByNetProcessDialogCancelListener);
		mProcessDialog.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				CheckVehicleWorkRequest checkVehicleWorkRequest = new CheckVehicleWorkRequest();
				checkVehicleWorkRequest.setVehicleRFIDTag(mCardID);
				checkVehicleWorkRequest.setWareHouseID(mWarehouseID);
				
				if (DEBUG) {
					mCheckVehicleWorkResponse = TestCheckVehicleWorkResponse();
				} else {
					mCheckVehicleWorkResponse = (CheckVehicleWorkResponse)mRestWebServiceAdapter.Rest(checkVehicleWorkRequest);
				}
				mProcessDialog.cancel();
				Looper.loop();
			}
		}).start();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	登记操作——业务系统
	 *
	 */
	private void RegistByNet() {
		//REST发送RegisterVehicleWorkRequest联网提交
		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "正在联网登记中....");
		OnCancelListener CheckByNetProcessDialogCancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (mRegisterVehicleWorkResponse == null) {
					mNetRegisterSuccess = false;
					return;
				}
				if (mRegisterVehicleWorkResponse.getResponseResult()) {
					mNetRegisterSuccess = true;
				} else {
					mNetRegisterSuccess = false;
				}
				
				
				if (mNetRegisterSuccess) {
					mPopupWindowDialog.showMessage("登记成功");
					//在线登记成功写卡
					RegistByCard();
					SetRegistButtonInuseable();
				} else {
					if (mRegisterVehicleWorkResponse != null) {
						mPopupWindowDialog.showMessage("登记失败,"+ mRegisterVehicleWorkResponse.getFailedReason());
					} else {
						if (RegistByCard()) {
							mPopupWindowDialog.showMessage("登记成功");
							SetRegistButtonInuseable();
						} else {
							mPopupWindowDialog.showMessage("登记失败");
						}
					}
				}
			}
		};
		mProcessDialog.setOnCancelListener(CheckByNetProcessDialogCancelListener);
		mProcessDialog.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				//登记作业请求
				RegisterVehicleWorkRequest registerVehicleWorkRequest = new RegisterVehicleWorkRequest();
				registerVehicleWorkRequest.setVehicleRFIDTag(mCardID);
				registerVehicleWorkRequest.setWareHouseID(mEnrollInfo.getWorkWarehouseID());
				registerVehicleWorkRequest.setWorkNode(mEnrollInfo.getWorkNode());
				registerVehicleWorkRequest.setWorkNumber(mEnrollInfo.getWorkNumber());
				registerVehicleWorkRequest.setBusinessType(mEnrollInfo.getBusinessType());
				registerVehicleWorkRequest.setUserID(mEnrollInfo.getOperatorID());
				
				mRegisterVehicleWorkResponse = (RegisterVehicleWorkResponse)mRestWebServiceAdapter.Rest(registerVehicleWorkRequest);
				mProcessDialog.cancel();
				Looper.loop();
			}
		}).start();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	读卡校验合法性
	 *
	 */
	private void CheckByCard() {
		GetDataByCard();
		//显示读卡最终信息
		DisplayInfo();
		if (CheckWorkNode()) {			//作业环节合法
			if (CheckWarehouseID()) {	//作业仓库与值仓仓库一致
				SetCheckTrue();
				//读卡校验成功,默认设置值仓确认
				mLoadingStatusRadioGroup.clearCheck();
				mLoadingFinishRadioButton.setChecked(true);
			} else {	//作业仓库与值仓仓库不一致
				SetCheckFalsed("粮仓库与当前值仓仓库不一致，请确认!");
				mChangeWarehouseButton.setVisibility(View.VISIBLE);
				if (mWarehouselist.size()> 0) {
					mChangeWarehouseButton.setText("调整到"+mWarehouselist.get(mSelectedID).getWarehouse_name());
				}
			}
		}
	}
	
	private void GetDataByCard() {
		//读取扣量扣价信息
		DeductedInfo deductedInfo = null;
		deductedInfo = mRfidAdapter.getAssayDeductedInfo();
		if (deductedInfo == null) {
			mPopupWindowDialog.showMessage("未读到化验扣量扣价信息,请重新读卡");
			return;
		}
		mAssayDeductedInfo.setMoistureDeducted(deductedInfo.getMoistureDeducted());
		//读取称重信息
		WeighInfo weighInfo = null;
		weighInfo = mRfidAdapter.getWeighInfo();
		if (weighInfo == null) {
			mPopupWindowDialog.showMessage("未读到称重信息,,请重新读卡");
			return;
		}
		mWeighInfo.setGrossWeight(weighInfo.getGrossWeight());
		mWeighInfo.setTareWeight(weighInfo.getTareWeight());
		if (mWeighInfo.getGrossWeight() != 0 && mWeighInfo.getTareWeight() != 0) {
			mWeighInfo.setNetWeight(mWeighInfo.getGrossWeight() - mWeighInfo.getTareWeight());
		}
//		mWeighInfo.setDeductWeight(CalcuDeduction());
	}
	
	private boolean CheckWorkNode() {
		if (mEnrollInfo.getBusinessType() == Constants.ENTER_WAREHOUSE 			//入库
				&& mEnrollInfo.getFirstWeight() == Constants.GROSS_FIRST 		//先毛后皮
				&& mEnrollInfo.getWorkNode() != Constants.WEIGHT_GROSS) {		//未称毛
			
			if (mEnrollInfo.getWorkNode() != Constants.WAREHOUSE_CONFIRM 						//不是值仓确认
					&& mEnrollInfo.getWorkNode() != Constants.CHANGE_WAREHOUSE 					//不是未卸完换仓
					&& mEnrollInfo.getWorkNode() != Constants.ASSAY_CHANGE_WAREHOUSE 			//不是质量问题换仓
					&& mEnrollInfo.getWorkNode() != Constants.WHOLE_VEHICLE_CHANGE_WAREHOUSE) {	//不是整车换仓
				SetCheckFalsed("入库先毛后皮作业,未称毛重，不能值仓");
				return false;
			}
		}
		
		if (mEnrollInfo.getBusinessType() == Constants.OUT_OF_WAREHOUSE		//出库
				&& mEnrollInfo.getFirstWeight() == Constants.TARE_FIRST && 	//先皮后毛
				mEnrollInfo.getWorkNode() != Constants.WEIGHT_TARE) {		//未称皮
			
			if (mEnrollInfo.getWorkNode() != Constants.WAREHOUSE_CONFIRM 						//不是值仓确认
					&& mEnrollInfo.getWorkNode() != Constants.CHANGE_WAREHOUSE 					//不是未卸完换仓
					&& mEnrollInfo.getWorkNode() != Constants.ASSAY_CHANGE_WAREHOUSE 			//不是质量问题换仓
					&& mEnrollInfo.getWorkNode() != Constants.WHOLE_VEHICLE_CHANGE_WAREHOUSE) {	//不是整车换仓
				SetCheckFalsed("出库先皮后毛作业,未称皮重,不能值仓");
				return false;
			}
		}
		
		if (mEnrollInfo.getVehiceType() == Constants.IN_VEHICLE					//内部车
				&& mEnrollInfo.getBusinessType() == Constants.ENTER_WAREHOUSE	//入库
				&& mEnrollInfo.getFirstWeight() == Constants.TARE_FIRST			//先皮后毛
				&& mEnrollInfo.getWorkNode() != Constants.WEIGHT_GROSS) {		//未称毛
			if (mEnrollInfo.getWorkNode() != Constants.WAREHOUSE_CONFIRM 						//不是值仓确认
					&& mEnrollInfo.getWorkNode() != Constants.CHANGE_WAREHOUSE 					//不是未卸完换仓
					&& mEnrollInfo.getWorkNode() != Constants.ASSAY_CHANGE_WAREHOUSE 			//不是质量问题换仓
					&& mEnrollInfo.getWorkNode() != Constants.WHOLE_VEHICLE_CHANGE_WAREHOUSE) {	//不是整车换仓
				SetCheckFalsed("内部车入库先皮后毛作业,未称毛重,不能值仓");
				return false;
			}
		}
		
		if (mEnrollInfo.getVehiceType() == Constants.IN_VEHICLE					//内部车
				&& mEnrollInfo.getBusinessType() == Constants.OUT_OF_WAREHOUSE	//出库
				&& mEnrollInfo.getFirstWeight() == Constants.GROSS_FIRST		//先皮后毛
				&& mEnrollInfo.getWorkNode() != Constants.WEIGHT_TARE) {		//未称皮
			if (mEnrollInfo.getWorkNode() != Constants.WAREHOUSE_CONFIRM 						//不是值仓确认
					&& mEnrollInfo.getWorkNode() != Constants.CHANGE_WAREHOUSE 					//不是未卸完换仓
					&& mEnrollInfo.getWorkNode() != Constants.ASSAY_CHANGE_WAREHOUSE 			//不是质量问题换仓
					&& mEnrollInfo.getWorkNode() != Constants.WHOLE_VEHICLE_CHANGE_WAREHOUSE) {	//不是整车换仓
				SetCheckFalsed("内部车出库先皮后毛作业,未称皮重,不能值仓");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description 登记操作——快速收纳系统
	 *
	 */
	private boolean RegistByCard() {
		//写卡，返回写卡结果
		//1.写如作业信息环节	
		mRfidAdapter.setOperateLink(String.valueOf(mEnrollInfo.getWorkNode()));
		//2.写入卸粮仓库编码
		mRfidAdapter.setStorage(String.valueOf(mWarehouseID));
		//3.写入值仓人员ID
		mRfidAdapter.setOprID(String.valueOf(mEnrollInfo.getOperatorID()));
		boolean res = SyncBlock.SyncBlock4(mRfidAdapter,RegistVehicleWorkActivity.this);
		if (res) {
			//读取写入作业环节信息
			int workNode = Integer.valueOf(mRfidAdapter.getOperateLink());
			if (workNode == Constants.WAREHOUSE_CONFIRM || workNode == Constants.WHOLE_VEHICLE_CHANGE_WAREHOUSE
					|| workNode == Constants.CHANGE_WAREHOUSE || workNode == Constants.ASSAY_CHANGE_WAREHOUSE) {
				return true;
			} else {
				return false;
			}
			
		}
		else {
			return false;
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	校验读卡合法性
	 *
	 */
	private void CheckCardLeagal() {
		if (mEnrollInfo.getSinged() == Constants.CARD_NOT_ISSUED) {
			mPopupWindowDialog.showMessage("卡未发行");
		}else if (mEnrollInfo.getSinged() == Constants.MOBILE_RETREATED) {
			mPopupWindowDialog.showMessage("已在手持端退卡");
		}else if (mEnrollInfo.getSinged() == Constants.CARD_ISSUED) {	//卡已发行使用
			if (mGrainApplication.SWITCH) {			//开启联网模式
				if (LoginActivity.NET_AVILIABE) {	//网络连接正常
					CheckByNet();	//联网校验
				} else {							//网络连接异常
					CheckByCard();					//读卡校验
				}
			} else {								//未开启联网模式
				CheckByCard();						//读卡校验
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description 登记按钮实现
	 *
	 */
	class RegistOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//获取当前登录用户ID
			mEnrollInfo.setOperatorID(mLoginSharedPreferences.getInt("current_user_id", 0));
			if (mEnrollInfo.getOperatorID() == 0) {
				mPopupWindowDialog.showMessage("未获取到值仓人员ID,请同步账户信息后重新登记");
				return;
			}
			
			if (mGrainApplication.SWITCH) {
				RegistByNet();
			} else {
				if (RegistByCard()) {
					mPopupWindowDialog.showMessage("登记成功");
				} else {
					mPopupWindowDialog.showMessage("登记失败");
				}
			}
			
			//登记后清空窗口，重新开始
			ClearWindow();
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description 扣量扣价按钮实现
	 *
	 */
	class DeductionDiscountOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(RegistVehicleWorkActivity.this, AdjustDeductionDiscountActivity.class);
			startActivity(intent);
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description 修改按钮实现，修改仓库到X仓库
	 *
	 */
	class ChangeWarehouseOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//卡是否拿走
			if (!RFIDIsExist()) {
				//清空界面
				ClearWindow();
			} else {
				//设置更换值仓仓库
				mEnrollInfo.setWorkWarehouseID(mWarehouseID);
				mEnrollInfo.setWorkNode(Constants.WHOLE_VEHICLE_CHANGE_WAREHOUSE);
				
				SetCheckTrue();
				for (int i = 0; i < mWarehouselist.size(); i++) {
					if (mEnrollInfo.getWorkWarehouseID() == mWarehouselist.get(i).getWarehouse_id()) {
						mUploadingWarehousTextView.setText(mWarehouselist.get(i).getWarehouse_name());
						return;
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description 根据读卡的作业环节编号，设置作业环节显示信息
	 *
	 */
	private void SetWorkNodeTextView() {
		switch (mEnrollInfo.getWorkNode()) {
		case 0:
			mWorkNodeTextView.setText("未值仓");
			break;
		case 1:
			mWorkNodeTextView.setText("值仓确认");
			break;
		case 2:
			mWorkNodeTextView.setText("值仓换仓");
			break;
		case 3:
			mWorkNodeTextView.setText("重新化验");
			break;
		case 4:
			mWorkNodeTextView.setText("称完皮重");
			break;
		case 5:
			mWorkNodeTextView.setText("称完毛重");
			break;
		case 6:
			mWorkNodeTextView.setText("整车换仓");
			break;
		default:
			mWorkNodeTextView.setText("作业环节错误");
			break;
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description 登记成功后清除窗口信息，以便进行下一次登记操作
	 *
	 */
	private void ClearWindow() {
		mLeagalTextView.setText("");
		mVarietiesTextView.setText("");
		mLicensePlateTextView.setText("");
		mOwnersTextView.setText("");
		mTypeTextView.setText("");
		mWorkNodeTextView.setText("");
		mGrossWeightTextView.setText(String.valueOf(""));
		mTareTextView.setText(String.valueOf(""));
		mNetWeigthTextView.setText(String.valueOf(""));
		mDeductionTextView.setText(String.valueOf(""));
		mUploadingWarehousTextView.setText("");
		SetRegistButtonInuseable();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description 判断RFID卡是否在读卡区
	 *
	 */
	private boolean RFIDIsExist() {
		//调用RFID接口HasCard
		if (mRfidAdapter.HasCard() == null) {
			return false;
		}
		return true;
	}
	
//	/**
//	 * 
//	 * @author zwz
//	 * @date 2013-7-11
//	 * @description	计算扣量信息：初次水分扣量+初次杂质扣量+二次水分扣量+二次杂质扣量
//	 *
//	 */
//	private int CalcuDeduction() {
//		String string = null;
//		string = mRfidAdapter.getF1();
//		if (string == null) {
//			mPopupWindowDialog.showMessage("读卡获取初次水分扣量标记位失败");
//			string = "0";
//		}
//		mMoistureAmountFlagFirst = Integer.valueOf(string);
//		string = mRfidAdapter.getD1();
//		if (string == null) {
//			mPopupWindowDialog.showMessage("读卡获取初次水分扣量失败");
//			string = "0";
//		}
//		if (mMoistureAmountFlagFirst == 0) {	//总量
//			mMoistureDeductAmountFirst = Double.parseDouble(string);
//		} else {	//百分比
//			mMoistureDeductAmountFirst = mWeighInfo.getNetWeight()*Double.parseDouble(string)/100;
//		}
//		string = mRfidAdapter.getF2();
//		if (string == null) {
//			mPopupWindowDialog.showMessage("读卡获取初次杂质扣量标记位失败");
//			string = "0";
//		}
//		mImpurityAmountFlagFirst = Integer.valueOf(string);
//		string = mRfidAdapter.getD2();
//		if (string == null) {
//			mPopupWindowDialog.showMessage("读卡获取初次杂质扣量失败");
//			string = "0";
//		}
//		if (mImpurityAmountFlagFirst == 0) {	//总量
//			mImpurityDeductAmountFirst = Double.parseDouble(string);
//		} else {	//百分比
//			mImpurityDeductAmountFirst = mWeighInfo.getNetWeight()*Double.parseDouble(string)/100;
//		}
//		
//		string = mRfidAdapter.getF_1();
//		if (string == null) {
//			mPopupWindowDialog.showMessage("读卡获取二次扣量标记位失败");
//			string = "0";
//		}
//		mAdjustDeductFlag = Integer.valueOf(string);
//		if (mAdjustDeductFlag == 1) {	//有二次扣量
//			string = mRfidAdapter.getF5();
//			if (string == null) {
//				mPopupWindowDialog.showMessage("读卡获取二次水分扣量标记位失败");
//				string = "0";
//			}
//			mMoistureAmountFlagAdjust = Integer.valueOf(string);
//			string = mRfidAdapter.getD5();
//			if (string == null) {
//				mPopupWindowDialog.showMessage("读卡获取二次水分扣量位失败");
//				string = "0";
//			}
//			if (mMoistureAmountFlagAdjust == 0) {	//总量
//				mMoistureDeductAmountAdjust = Double.parseDouble(string);
//			} else {	//百分比
//				mMoistureDeductAmountAdjust = mWeighInfo.getNetWeight()*Double.parseDouble(string)/100;
//			}
//			string = mRfidAdapter.getF6();
//			if (string == null) {
//				mPopupWindowDialog.showMessage("读卡获取二次杂质扣量标记位失败");
//				string = "0";
//			}
//			mImpurityAmountFlagAdjust = Integer.valueOf(string);
//			string = mRfidAdapter.getD6();
//			if (string == null) {
//				mPopupWindowDialog.showMessage("读卡获取二次杂质扣量失败");
//				string = "0";
//			}
//			if (mImpurityAmountFlagAdjust == 0) {	//总量
//				mImpurityDeductAmountAdjust = Double.parseDouble(string);
//			} else {	//百分比
//				mImpurityDeductAmountAdjust = mWeighInfo.getNetWeight()*Double.parseDouble(string)/100;
//			}
//			mWeighInfo.setDeductWeight((int)(mMoistureDeductAmountFirst + mImpurityDeductAmountFirst+mMoistureDeductAmountAdjust+mImpurityDeductAmountAdjust));
//		} else {	//没有二次扣量
//			mWeighInfo.setDeductWeight((int)(mMoistureDeductAmountFirst + mImpurityDeductAmountFirst));
//		}
//		return 0;
//	}
	
	class LoadingStatusOnCheckedChangeListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == R.id.regist_vehicle_work_rbtn_loading_finish) {	//装卸完成
				mEnrollInfo.setWorkNode(Constants.WAREHOUSE_CONFIRM);
			}
			if (checkedId == R.id.regist_vehicle_work_rbtn_loading_unfinish_need_assay) {	//装卸未完成，重新化验
				mEnrollInfo.setWorkNode(Constants.ASSAY_CHANGE_WAREHOUSE);
			}
			if (checkedId == R.id.regist_vehicle_work_rbtn_loading_unfinish_change_warehouse) {	//装卸未完成，换仓
				mEnrollInfo.setWorkNode(Constants.CHANGE_WAREHOUSE);
			}
			
		}
	}
	
	private void GetConfigInfo() {
		//读配置信息，设置默认仓库选中项
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
		
	}
	
	private String GetGrainTypeName(int index) {
		String string = "";
		QueryBuilder<GrainTypeDB, Integer> qBuilder = mGrainTypeDBDao.queryBuilder();
		try {
			qBuilder.where().eq("grain_type_id", index);
			List<GrainTypeDB> grainTypeDBs = qBuilder.query();
			if (grainTypeDBs.size()>0) {
				string = grainTypeDBs.get(0).getGrain_type_name();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return string;
	}
	
	private CheckVehicleWorkResponse TestCheckVehicleWorkResponse() {
		CheckVehicleWorkResponse checkVehicleWorkResponse = new CheckVehicleWorkResponse();
		Result result = new Result();
		result.setResponseResult(true);
		result.setFailedReason(null);
		
		EnrollInfo enrollInfo = new EnrollInfo();

		GoodsInfo goodsInfo = new GoodsInfo();
		goodsInfo.setGoodsOwner("GoodsOwner");
		goodsInfo.setGoodsKind(16);
		goodsInfo.setPrice(13.2);
		enrollInfo.setEnrollGoodsInfo(goodsInfo);

		VehicleInfo vehicleInfo = new VehicleInfo();
		vehicleInfo.setShipPlate("ShipPlate");
		vehicleInfo.setVehiceType(1);
		vehicleInfo.setVehicleDriver("VehicleDriver");
		vehicleInfo.setVehiclePlate("VehiclePlate");
		vehicleInfo.setVehicleTag("VehicleTag");
		enrollInfo.setEnrollVehicleInfo(vehicleInfo);
		
		WorkInfo workInfo = new WorkInfo();
		workInfo.setApprovedWeight(33);
		workInfo.setBusinessType(3);
		workInfo.setCompletedWeight(44);
		workInfo.setPrice(3.2);
		workInfo.setWarnFlag(1);
		workInfo.setWorkNumber("WorkNumber");
		workInfo.setWorkPlace("WorkPlace");
		workInfo.setWorkNode(3);
		workInfo.setWarehouseID("2");
		enrollInfo.setEnrollWorkInfo(workInfo);
		checkVehicleWorkResponse.setResultEnrollInfo(enrollInfo);
		
		WeighInfo weighinfo = new WeighInfo();
		weighinfo.setDeductWeight(2);
		weighinfo.setGrossWeight(4);
		weighinfo.setNetWeight(6);
		weighinfo.setTareWeight(8);
		checkVehicleWorkResponse.setResultWeighinfo(weighinfo);
		return checkVehicleWorkResponse;
	}
	
	private String GetBusinessTypeName(int type) {
		String typeName = null;
		switch (type) {
		case 1:
			typeName = "入库";
			break;
		case 2:
			typeName = "出库";
			break;
		case 3:
			typeName = "倒仓";
			break;
		default:
			typeName = "";
			break;
		}
		return typeName;
	}
	
	private void SetCheckFalsed(String msg) {
		mLeagal = "不合法";
		mLeagalTextView.setText(mLeagal);
		mLeagalTextView.setTextColor(Color.rgb(255, 0, 0));
		mPopupWindowDialog.showMessage(msg);
		mReCheck = true;
	}
	
	private void SetCheckTrue() {
		mChangeWarehouseButton.setVisibility(View.INVISIBLE);
		SetRegistButtonUseable();
		mLeagal = "合法";
		mLeagalTextView.setText(mLeagal);
		mLeagalTextView.setTextColor(Color.rgb(255, 0, 0));
	}
	
	private void SetRegistButtonUseable() {
		mRegistButton.setEnabled(true);
		mRegistButton.setBackgroundResource(R.drawable.corner_btn_selector);
	}
	
	private void SetRegistButtonInuseable() {
		mRegistButton.setEnabled(false);
		mRegistButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
	}
	
	private IReadCard mIReadCard = new IReadCard() {
		
		@Override
		public void ReadCard() {
			Log.v(TAG, "ReadCard()");
			if (GetCardInfo()) {	//读卡信息成功
				CheckCardLeagal();
			}
		}
	};
	
	protected void onResume() {
		mGrainBroadcastReceiver.setIReadCard(mIReadCard);
		super.onResume();
	};
	
	@Override
	protected void onDestroy() {
		mGrainBroadcastReceiver.setIReadCard(null);
		super.onDestroy();
	}
}
