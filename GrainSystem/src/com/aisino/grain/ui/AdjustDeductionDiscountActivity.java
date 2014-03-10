package com.aisino.grain.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import com.aisino.grain.GrainApplication;
import com.aisino.grain.R;
import com.aisino.grain.beans.AdjustDeductedRequest;
import com.aisino.grain.beans.AdjustDeductedResponse;
import com.aisino.grain.beans.DeductedInfo;
import com.aisino.grain.beans.GetDecutedInfoRequest;
import com.aisino.grain.beans.GetDecutedInfoResponse;
import com.aisino.grain.beans.QualityIndexResultInfo;
import com.aisino.grain.broadcastreceiver.IReadCard;
import com.aisino.grain.model.Constants;
import com.aisino.grain.model.SyncBlock;
import com.aisino.grain.model.db.AcountInfo;
import com.aisino.grain.model.db.QualityIndex;
import com.aisino.grain.model.db.Warehouse;
import com.aisino.grain.model.rest.RestWebServiceAdapter;
import com.aisino.grain.model.rfid.RfidAdapter;
import com.aisino.grain.ui.util.WaitDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.stmt.QueryBuilder;

public class AdjustDeductionDiscountActivity extends CustomMenuActivity{
	private static final String TAG = "AdjustDeductionDiscountActivity";
	private static final boolean DEBUG = false;
	
	//线程中更新UI信息
	private final int Msg_GetDecutedInfoFailedByNet = 0;
	private final int Msg_GetQulityIndexInfoFailedByCard = 1;
	private final int Msg_SaveAdjustDecutedInfoFailedNeedSyncAcountInfo = 2;
	private final int Msg_SaveAdjustDecutedInfoFailedNoCard = 3;
	
	//UI
	private Spinner mSpinnerWarehouse = null;								//值仓仓库下拉列表
	
	private Button mReadCardButton = null;									//读卡按钮
	private Button mSaveDeductionDiscountButton = null;						//保存扣量扣价按钮
	private Button mRegistVehicleWorkButton = null;							//登记车辆作业按钮
	
	private TextView mMoistureDeductedTextView = null;						//水分扣量
	private TextView mMoistureDeductedUnitTextView = null;					//水分扣量单位
	private TextView mImpurityDeductedTextView = null;						//杂质扣量
	private TextView mImpurityDeductedUnitTextView = null;					//杂质扣量单位
	private TextView mIncidentalExpensesDeductedTextView = null;			//扣清杂费
	private TextView mIncidentalExpensesDeductedUnitTextView = null;		//扣清杂费单位
	private TextView mBakedExpensesDeductedTextView = null;					//扣整晒费
	private TextView mBakedExpensesDeductedUnitTextView = null;				//扣整晒费单位
	
	private EditText mAdjustMoistureDeductedEditText = null;				//水分扣量加扣
	private Button mAdjustMoistureDeductedUnitButton = null;				//调整水分扣量单位
	private EditText mAdjustImpurityDeductedEditText = null;				//杂质扣量加扣
	private Button mAdjustImpurityDeductedUnitButton = null;				//调整杂质扣量单位
	private EditText mAdjustIncidentalExpensesDeductedEditText = null;		//扣清杂费加扣
	private Button mAdjustIncidentalExpensesDeductedUnitButton = null;		//调整扣清杂费单位
	private EditText mAdjustBakedExpensesDeductedEditText = null;			//扣整晒费加扣
	private Button mAdjustBakedExpensesDeductedUnitButton = null;			//调整扣整晒费单位
	
	private ListView mQualityIndexListView = null;							//质量指标Listview
	private LinearLayout mLinearLayout = null;								//ListView Item 背景
	
	private Handler mHandler = null;
	
	//Data
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();		//仓库名称列表
	private List<QualityIndexItemData> mQualityIndexItemDataList = null;		//QualityIndex数据ListView中显示
	private List<QualityIndexResultInfo> mQualityIndexResultInfoList = null;	//rest解析的化验指标列表(ID,Value)
	
	private double mMoistureDeducted = -1;									//初次水分扣量
	private double mImpurityDeducted = -1;									//初次杂质扣量
	private double mIncidentalExpensesDeducted = -1;						//初次扣清杂费
	private double mBakedExpensesDeducted = -1;								//初次扣整晒费
	private int mMoistureDeductedMode = 0;									//初次水分扣量单位标记
	private int mImpurityDeductedMode = 0;									//初次杂质扣量单位标记
	private int mIncidentalExpensesDeductedMode = 0;						//初次扣清杂费单位标记
	private int mBakedExpensesDeductedMode = 0;								//初次扣整晒费单位标记
	
	private double mAdjustMoistureDeducted = 0;								//水分扣量加扣
	private double mAdjustImpurityDeducted = 0;								//杂质扣量加扣
	private double mAdjustIncidentalExpensesDeducted = 0;					//扣清杂费加扣
	private double mAdjustBakedExpensesDeducted = 0;						//扣整晒费加扣
	private int mAdjustMoistureDeductedMode = -1;							//水分扣量单位加扣标记
	private int mAdjustImpurityDeductedMode = -1;							//杂质扣量单位加扣标记
	private int mAdjustIncidentalExpensesDeductedMode = -1;					//扣清杂费单位加扣标记
	private int mAdjustBakedExpensesDeductedMode = -1;						//扣整晒费单位加扣标记
	
	private String mSigned = null;											//标记位
	private String mCardID = null;											//卡号
	private int mSelectedID = 0;											//值仓仓库列表选中ID
	
	//REST
	private RestWebServiceAdapter mRestWebServiceAdapter = null;			//rest
	private GetDecutedInfoResponse mGetDecutedInfoResponse = null;
	private AdjustDeductedResponse mAdjustDeductedResponse = null;
	
	
	//Flag
	private boolean mNetSaveSuccess = false;
	private boolean mCardSaveSuccess = false;
	
	//DB
	private Dao<Warehouse, Integer> mWarehouseDao =  null;					//仓库信息Dao
	private List<Warehouse> mWarehouselist = null;							//查询结果
	private Dao<QualityIndex, Integer> mQualityIndexDao =  null;			//质量指标信息Dao
	private List<QualityIndex> mQualityIndexlist = null;					//查询结果
	private Dao<AcountInfo, Integer> mAcountInfoDao = null;					//用户信息Dao
	
	//System
	private SharedPreferences sharedPreferences = null;						//保存选中的仓库
	private Editor editor = null;											//SharedPreferences编辑器
	private SharedPreferences mLoginSharedPreferences = null;				//保存登录信息
	private GrainApplication mGrainApplication = null;
	private Context mContext = null;
	
	//Rfid
	private RfidAdapter mRfidAdapter = null;								//读卡适配器
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		LoginActivity.ActivityList.add(this);
		setContentView(R.layout.activity_adjust_deduction_discount);
		super.onCreate(savedInstanceState);
		InitCtrl();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	初始化
	 *
	 */
	private void InitCtrl() {
		//System
		mContext = this;
		mGrainApplication = (GrainApplication)getApplication();
		sharedPreferences = getSharedPreferences("WarehouseInfo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//获取编辑器
		mLoginSharedPreferences = getSharedPreferences("LoginInformation", Context.MODE_PRIVATE);
		//Rfid
		mRfidAdapter = RfidAdapter.getInstance(this);
		//REST
		mRestWebServiceAdapter = RestWebServiceAdapter.getInstance(mContext);
		//Data
		mQualityIndexResultInfoList = new ArrayList<QualityIndexResultInfo>();
		mQualityIndexItemDataList = new ArrayList<QualityIndexItemData>();
		//UI
		mSpinnerWarehouse = (Spinner)findViewById(R.id.adjust_deduction_discount_spinner_warehouse);
		mReadCardButton = (Button)findViewById(R.id.adjust_deduction_discount_btn_read_card);
		mReadCardButton.setOnClickListener(new ReadCardOnClickListener());
		mSaveDeductionDiscountButton = (Button)findViewById(R.id.adjust_deduction_discount_btn_save_deduction_discount);
		mSaveDeductionDiscountButton.setOnClickListener(new SaveDeductionDiscountOnClickListener());
		mSaveDeductionDiscountButton.setEnabled(false);
		mSaveDeductionDiscountButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
		mRegistVehicleWorkButton = (Button)findViewById(R.id.adjust_deduction_discount_btn_regist_vehicle_work);
		mRegistVehicleWorkButton.setOnClickListener(new RegistVehicleWorkOnClickListener());
		
		mMoistureDeductedTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_moisture_deduct_amount);
		mImpurityDeductedTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_impurity_deduct_amount);
		mIncidentalExpensesDeductedTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_clear_sundry_fees);
		mBakedExpensesDeductedTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_deduct_drying_fees);
		
		mMoistureDeductedUnitTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_kilogram_percent_one);
		mImpurityDeductedUnitTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_kilogram_percent_two);
		mIncidentalExpensesDeductedUnitTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_yuan_percent_one);
		mBakedExpensesDeductedUnitTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_yuan_percent_two);
		
		mAdjustMoistureDeductedEditText = (EditText)findViewById(R.id.adjust_deduction_discount_et_moisture_deduct_amount_add);
		mAdjustImpurityDeductedEditText = (EditText)findViewById(R.id.adjust_deduction_discount_et_impurity_deduct_amount_add);
		mAdjustIncidentalExpensesDeductedEditText = (EditText)findViewById(R.id.adjust_deduction_discount_et_clear_sundry_fees_add);
		mAdjustIncidentalExpensesDeductedEditText.setOnClickListener(new AdjustIncidentalExpensesDeductedOnClickListener());
		mAdjustBakedExpensesDeductedEditText = (EditText)findViewById(R.id.adjust_deduction_discount_et_deduct_drying_fees_add);
		mAdjustBakedExpensesDeductedEditText.setOnClickListener(new AdjustBakedExpensesDeductedOnClickListener());
		
		mAdjustMoistureDeductedUnitButton = (Button)findViewById(R.id.adjust_deduction_discount_btn_adjust_moisture_deducted_unit);
		mAdjustMoistureDeductedUnitButton.setOnClickListener(new AdjustMoistureDeductedUnitOnClickListener());
		mAdjustImpurityDeductedUnitButton = (Button)findViewById(R.id.adjust_deduction_discount_btn_adjust_impurity_deducted_unit);
		mAdjustImpurityDeductedUnitButton.setOnClickListener(new AdjustImpurityDeductedUnitOnClickListener());
		mAdjustIncidentalExpensesDeductedUnitButton = (Button)findViewById(R.id.adjust_deduction_discount_btn_adjust_incidental_expenses_deducted_unit);
		mAdjustIncidentalExpensesDeductedUnitButton.setOnClickListener(new AdjustIncidentalExpensesDeductedUnitOnClickListener());
		mAdjustBakedExpensesDeductedUnitButton = (Button)findViewById(R.id.adjust_deduction_discount_btn_adjust_baked_expenses_deducted_unit);
		mAdjustBakedExpensesDeductedUnitButton.setOnClickListener(new AdjustBakedExpensesDeductedUnitOnClickListener());
		
		mQualityIndexListView = (ListView)findViewById(R.id.adjust_deduction_discount_lv);
		
		// 隐藏输入法
		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		imm.hideSoftInputFromWindow(mAdjustMoistureDeductedEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mAdjustImpurityDeductedEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mAdjustIncidentalExpensesDeductedEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mAdjustBakedExpensesDeductedEditText.getWindowToken(), 0);
		
		mWarehouselist = new ArrayList<Warehouse>();
		mWarehouseDao = getHelper().getWarehouseDao();
		mQualityIndexlist = new ArrayList<QualityIndex>();
		mQualityIndexDao = getHelper().getQualityIndexDao();
		mAcountInfoDao = getHelper().getAcountInfoDao();
		
		InitSnipperWarehouse();
		//读配置信息，设置默认仓库选中项
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
		
		//多线程更新UI
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Msg_GetDecutedInfoFailedByNet:
					mPopupWindowDialog.showMessage("联网未获取到粮食指标信息");
					break;
				case Msg_GetQulityIndexInfoFailedByCard:
					mPopupWindowDialog.showMessage("未读到化验指标信息");
					break;
				case Msg_SaveAdjustDecutedInfoFailedNeedSyncAcountInfo:
					mPopupWindowDialog.showMessage("请同步账户信息后重试,保存失败");
					break;
				case Msg_SaveAdjustDecutedInfoFailedNoCard:
					mPopupWindowDialog.showMessage("未读到卡,保存失败");
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
		
	}
	
	class AdjustIncidentalExpensesDeductedOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mPopupWindowDialog.showMessage("该功能暂未开放");
		}
	}
	
	class AdjustBakedExpensesDeductedOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mPopupWindowDialog.showMessage("该功能暂未开放");
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	初始化值仓仓库下拉菜单
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
			if (resultsList.size() > 0) {
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
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	值仓仓库下拉菜单选中监听器
	 *
	 */
	class SpinnerWarehouseOnItemSelectedListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			int selectedID = parent.getSelectedItemPosition();
			editor.putInt("SelectedID", selectedID);
			editor.commit();
		}
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}
	
	@Override
	protected void onResume() {
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
		mGrainBroadcastReceiver.setIReadCard(mIReadCard);
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SCAN) {
			//读卡获取数据
			if(!ReadByCard()){
				return false;
			}
			
			if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
				mPopupWindowDialog.showMessage("卡未发行");
			}
			
			if (mSigned.equals(Constants.MOBILE_RETREATED)) {
				mPopupWindowDialog.showMessage("已在手持机退卡");
			}
			
			if (mSigned.equals("1")) {
				GetDecutedInfoBusiness();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	读卡按钮实现
	 *
	 */
	class ReadCardOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//读卡获取数据
			if(!ReadByCard()){
				return;
			}
			
			if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
				mPopupWindowDialog.showMessage("卡未发行");
			}
			
			if (mSigned.equals(Constants.MOBILE_RETREATED)) {
				mPopupWindowDialog.showMessage("已在手持机退卡");
			}
			
			if (mSigned.equals(Constants.CARD_ISSUED)) {
				GetDecutedInfoBusiness();
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	保存扣量扣价按钮实现
	 *
	 */
	class SaveDeductionDiscountOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if ((mMoistureDeducted == -1) || (mImpurityDeducted == -1) || (mIncidentalExpensesDeducted == -1) || (mBakedExpensesDeducted == -1)) {
				mPopupWindowDialog.showMessage("请先读卡");
				return;
			}
			
			//校验输入数据是否合法
			boolean res = CheckDataLeagal();
			
			if (res) {
				//获取输入框数据
				GetEditTextData();
				SaveDecutedInfoBusiness();
			}
		}
	}
	
	private void SaveDecutedInfoBusiness() {
		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "正在保存中....");
		OnCancelListener SaveProcessDialogCancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//提示保存结果
				SaveResult();
			}
		};
		mProcessDialog.setOnCancelListener(SaveProcessDialogCancelListener);
		mProcessDialog.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				SaveData();
				Looper.loop();
			}
		}).start();
	}
	
	private void SaveResult() {
		if (mGrainApplication.SWITCH) {	//启用在线模式
			if (mAdjustDeductedResponse != null) {	//联网保存扣量扣价获取到返回结果
				if (mNetSaveSuccess) {
					mPopupWindowDialog.showMessage("保存成功");
					mSaveDeductionDiscountButton.setEnabled(false);
					mSaveDeductionDiscountButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
				} else {
					mPopupWindowDialog.showMessage(mAdjustDeductedResponse.getFailedReason());
				}
			} else {
				if (mCardSaveSuccess) {
					mPopupWindowDialog.showMessage("保存成功");
					mSaveDeductionDiscountButton.setEnabled(false);
					mSaveDeductionDiscountButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
				} else {
					mPopupWindowDialog.showMessage("保存失败");
				}
			}
		} else { //未启用在线模式
			if (mCardSaveSuccess) {
				mPopupWindowDialog.showMessage("保存成功");
				mSaveDeductionDiscountButton.setEnabled(false);
				mSaveDeductionDiscountButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
			} else {
				mPopupWindowDialog.showMessage("保存失败");
			}
		}
	}
	
	private void SaveData() {
		//网络开关打开,联网+本地保存;网络开关关闭,本地保存
		if (mGrainApplication.SWITCH) {
			mNetSaveSuccess = SaveByNet();
		}
		mCardSaveSuccess = SaveByCard();
		mProcessDialog.cancel();
	}
	
	/**
	 *
	 * @author zwz
	 * @date 2013-6-26
	 * @description 读取卡内数据
	 *
	 */
	private boolean ReadByCard() {
		Log.v(TAG, "ReadByCard()");
		String string = null;
		//卡号
		string = mRfidAdapter.HasCard();
		if (string == null) {
			mPopupWindowDialog.showMessage("未读到卡,请重新读卡");
			Log.v(TAG, "未读到卡,请重新读卡");
			return false;
		}
		mCardID = string;
		//标记位
		string = mRfidAdapter.getTagNo();
		if (string == null) {
			mPopupWindowDialog.showMessage("未读到标志位,请重新读卡");
			Log.v(TAG, "未读到标志位,请重新读卡");
			return false;
		}
		mSigned = string;
		return true;
	}
	
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description
	 *
	 */
	private void DisplayDecutedInfo(GetDecutedInfoResponse getDecutedInfoResponse) {
		//初次水分扣量
		if (mMoistureDeductedMode == 0) {
			mMoistureDeductedUnitTextView.setText(getString(R.string.kilogram));
		} else {
			mMoistureDeductedUnitTextView.setText(getString(R.string.percent));
		}
		//初次杂质扣量
		if (mImpurityDeductedMode == 0) {
			mImpurityDeductedUnitTextView.setText(getString(R.string.kilogram));
		} else {
			mImpurityDeductedUnitTextView.setText(getString(R.string.percent));
		}
		//初次扣清杂费
		if (mIncidentalExpensesDeductedMode == 0) {
			mIncidentalExpensesDeductedUnitTextView.setText(getString(R.string.yuan));
		} else {
			mIncidentalExpensesDeductedUnitTextView.setText(getString(R.string.percent));
		}
		//初次扣整晒费
		if (mBakedExpensesDeductedMode == 0) {
			mBakedExpensesDeductedUnitTextView.setText(getString(R.string.yuan));
		} else {
			mBakedExpensesDeductedUnitTextView.setText(getString(R.string.percent));
		}
		//手持机水分扣量加扣
		if (mAdjustMoistureDeductedMode == 0) {
			mAdjustMoistureDeductedUnitButton.setText(getString(R.string.kilogram));
			mAdjustMoistureDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else {
			mAdjustMoistureDeductedUnitButton.setText(getString(R.string.percent));
			mAdjustMoistureDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		//手持机杂质扣量加扣
		if (mAdjustImpurityDeductedMode == 0) {
			mAdjustImpurityDeductedUnitButton.setText(getString(R.string.kilogram));
			mAdjustImpurityDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else {
			mAdjustImpurityDeductedUnitButton.setText(getString(R.string.percent));
			mAdjustImpurityDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		//手持机扣清杂费加扣
		if (mAdjustIncidentalExpensesDeductedMode == 0) {
			mAdjustIncidentalExpensesDeductedUnitButton.setText(getString(R.string.yuan));
			mAdjustIncidentalExpensesDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else {
			mAdjustIncidentalExpensesDeductedUnitButton.setText(getString(R.string.percent));
			mAdjustIncidentalExpensesDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		//手持机扣整晒费加扣
		if (mAdjustBakedExpensesDeductedMode == 0) {
			mAdjustBakedExpensesDeductedUnitButton.setText(getString(R.string.yuan));
			mAdjustBakedExpensesDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else {
			mAdjustBakedExpensesDeductedUnitButton.setText(getString(R.string.percent));
			mAdjustBakedExpensesDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		mMoistureDeductedTextView.setText(String.valueOf(mMoistureDeducted));
		mImpurityDeductedTextView.setText(String.valueOf(mImpurityDeducted));
		mIncidentalExpensesDeductedTextView.setText(String.valueOf(mIncidentalExpensesDeducted));
		mBakedExpensesDeductedTextView.setText(String.valueOf(mBakedExpensesDeducted));
		
		mAdjustMoistureDeductedEditText.setText(String.valueOf(mAdjustMoistureDeducted));
		mAdjustImpurityDeductedEditText.setText(String.valueOf(mAdjustImpurityDeducted));
		mAdjustIncidentalExpensesDeductedEditText.setText(String.valueOf(mAdjustIncidentalExpensesDeducted));
		mAdjustBakedExpensesDeductedEditText.setText(String.valueOf(mAdjustBakedExpensesDeducted));
		
		//设置保存按钮可用
		mSaveDeductionDiscountButton.setEnabled(true);
		mSaveDeductionDiscountButton.setBackgroundResource(R.drawable.corner_btn_selector);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description 校验是否是小数，小数点后最多为2位
	 *
	 * @param 将输入小数以字符串形式传入
	 * @return 校验成功:true;校验失败:false
	 */
	private boolean IsDecimal(String string){
		String test = "^[+]?\\d*([.]\\d{0,2})?$"; 
		Pattern pattern = Pattern.compile(test); 
		Matcher matcher = pattern.matcher(string); 
		return matcher.matches(); 
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	校验是否为整数
	 *
	 * @param 将输入整数以字符串形式传入
	 * @return 校验成功:true;校验失败:false
	 */
	private boolean IsIntger(String string){
		if (string.equals("0.0")) {
			return true;
		}
		String test = "^[1-9]\\d*(\\.[0])?$"; 
		
        Pattern pattern = Pattern.compile(test); 
        Matcher matcher = pattern.matcher(string); 
        return matcher.matches(); 
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	获取输入数据，转化成字符串
	 *
	 */
	private void GetEditTextData() {
		if (mAdjustMoistureDeductedEditText.getText().toString().equals("")) {
			mAdjustMoistureDeducted = 0;
		} else {
			mAdjustMoistureDeducted = Double.valueOf(mAdjustMoistureDeductedEditText.getText().toString());
		}
		if (mAdjustImpurityDeductedEditText.getText().toString().equals("")) {
			mAdjustImpurityDeducted = 0;
		} else {
			mAdjustImpurityDeducted = Double.valueOf(mAdjustImpurityDeductedEditText.getText().toString());
		}
		if (mAdjustIncidentalExpensesDeductedEditText.getText().toString().equals("")) {
			mAdjustIncidentalExpensesDeducted = 0;
		} else {
			mAdjustIncidentalExpensesDeducted = Double.valueOf(mAdjustIncidentalExpensesDeductedEditText.getText().toString());
		}
		if (mAdjustBakedExpensesDeductedEditText.getText().toString().equals("")) {
			mAdjustBakedExpensesDeducted = 0;
		} else {
			mAdjustBakedExpensesDeducted = Double.valueOf(mAdjustBakedExpensesDeductedEditText.getText().toString());
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	校验输入数据的合法性
	 *
	 * @return 校验成功:true，校验失败:false
	 */
	private boolean CheckDataLeagal() {
		//校验输入数据合法性
		if (mAdjustMoistureDeductedMode == 0) {
			if (!IsIntger(String.valueOf(mAdjustMoistureDeductedEditText.getText().toString()))) {
				mAdjustMoistureDeductedEditText.setError("输入格式有误");
				return false;
			}
		} else {
			if (!IsDecimal(String.valueOf(mAdjustMoistureDeductedEditText.getText().toString()))) {
				mAdjustMoistureDeductedEditText.setError("输入格式有误");
				return false;
			}
		}
		
		if (mAdjustImpurityDeductedMode == 0) {
			if (!IsIntger(String.valueOf(mAdjustImpurityDeductedEditText.getText().toString()))) {
				mAdjustImpurityDeductedEditText.setError("输入格式有误");
				return false;
			}
		} else {
			if (!IsDecimal(String.valueOf(mAdjustImpurityDeductedEditText.getText().toString()))) {
				mAdjustImpurityDeductedEditText.setError("输入格式有误");
				return false;
			}
		}
		if (mAdjustIncidentalExpensesDeductedMode == 0) {
			if (!IsIntger(String.valueOf(mAdjustIncidentalExpensesDeductedEditText.getText().toString()))) {
				mAdjustIncidentalExpensesDeductedEditText.setError("请输入整数");
				return false;
			}
		} else {
			if (!IsDecimal(String.valueOf(mAdjustIncidentalExpensesDeductedEditText.getText().toString()))) {
				mAdjustIncidentalExpensesDeductedEditText.setError("输入格式有误");
				return false;
			}
		}
		if (mAdjustBakedExpensesDeductedMode == 0) {
			if (!IsIntger(String.valueOf(mAdjustBakedExpensesDeductedEditText.getText().toString()))) {
				mAdjustBakedExpensesDeductedEditText.setError("请输入整数");
				return false;
			}
		} else {
			if (!IsDecimal(String.valueOf(mAdjustBakedExpensesDeductedEditText.getText().toString()))) {
				mAdjustBakedExpensesDeductedEditText.setError("输入格式有误");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description 初始化质量指标listview
	 *
	 */
	private void InitListView(){
		if (mQualityIndexResultInfoList == null) {
			Toast.makeText(getApplicationContext(), "化验指标信息为空", Toast.LENGTH_LONG).show();
			return;
		}
		MyBaseAdapter adapter = new MyBaseAdapter();
		mQualityIndexListView.setAdapter(adapter);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	质量指标listview的Adapter
	 *
	 */
	class MyBaseAdapter extends BaseAdapter {
		LayoutInflater mInflater = null;
		public MyBaseAdapter() {
			
			mInflater = getLayoutInflater();
		}
		
		@Override
		public int getCount() {
			return mQualityIndexItemDataList.size();
		}

		@Override
		public Object getItem(int position) {
			return mQualityIndexItemDataList.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.laboratory_index_item, null);
				mLinearLayout = (LinearLayout)convertView.findViewById(R.id.laboratory_index_ll);
			} else {
				Log.i(TAG, "not null");
			}
			
			QualityIndexItem qualityIndexItem = new QualityIndexItem();	 //化验结果Item
			qualityIndexItem.IndexName = (TextView)convertView.findViewById(R.id.laboratory_index_tv_index);
			qualityIndexItem.IndexValue = (TextView)convertView.findViewById(R.id.laboratory_index_tv_laboratory_value);
			
			qualityIndexItem.IndexName.setText(mQualityIndexItemDataList.get(position).IndexName);
			qualityIndexItem.IndexValue.setText(mQualityIndexItemDataList.get(position).IndexValue);
			if (position%2 == 0) {
				mLinearLayout.setBackgroundColor(0xFFEAEDF1);
			} else {
				mLinearLayout.setBackgroundColor(0xFFD7E1EA);
			}
			
			return convertView;
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description 化验指标QualityIndex中Item视图类
	 *
	 */
	class QualityIndexItem{
		TextView IndexName;
		TextView IndexValue;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description 化验指标QualityIndex中Item数据类
	 *
	 */
	class QualityIndexItemData{
		String IndexName;
		String IndexValue;
	}
	
	private void AnalyseDecutedInfo(GetDecutedInfoResponse getDecutedInfoResponse) {
		DeductedInfo AssayDeductedInfo = null;
		DeductedInfo AdjustDeductedInfo = null;
		List<QualityIndexResultInfo> ResultQualityInfos = null;
		AssayDeductedInfo = getDecutedInfoResponse.getAssayDeductedInfo();
		AdjustDeductedInfo = getDecutedInfoResponse.getAdjustDeductedInfo();
		ResultQualityInfos = getDecutedInfoResponse.getResultQualityInfos();
		
		if (AssayDeductedInfo == null) {
			return;
		}
		//初次扣量扣价信息
		mMoistureDeducted = AssayDeductedInfo.getMoistureDeducted();
		mImpurityDeducted = AssayDeductedInfo.getImpurityDeducted();
		mIncidentalExpensesDeducted = AssayDeductedInfo.getIncidentalExpensesDeducted();
		mBakedExpensesDeducted = AssayDeductedInfo.getBakedExpensesDeducted();
		
		mMoistureDeductedMode = AssayDeductedInfo.getMoistureDeductedMode();
		mImpurityDeductedMode = AssayDeductedInfo.getImpurityDeductedMode();
		mIncidentalExpensesDeductedMode = AssayDeductedInfo.getIncidentalExpensesDeductedMode();
		mBakedExpensesDeductedMode = AssayDeductedInfo.getBakedExpensesDeductedMode();
		
		if (AdjustDeductedInfo == null) {
			//手持机扣量信息为空
			mAdjustMoistureDeductedMode = mMoistureDeductedMode;
			mAdjustImpurityDeductedMode = mImpurityDeductedMode;
			mAdjustIncidentalExpensesDeductedMode = mIncidentalExpensesDeductedMode;
			mAdjustBakedExpensesDeductedMode = mBakedExpensesDeductedMode;
			return;
		}
		
		//调整扣量扣价信息
		mAdjustMoistureDeducted = AdjustDeductedInfo.getMoistureDeducted();
		mAdjustImpurityDeducted = AdjustDeductedInfo.getImpurityDeducted();
		mAdjustIncidentalExpensesDeducted = AdjustDeductedInfo.getIncidentalExpensesDeducted();
		mAdjustBakedExpensesDeducted = AdjustDeductedInfo.getBakedExpensesDeducted();
		
		mAdjustMoistureDeductedMode = AdjustDeductedInfo.getMoistureDeductedMode();
		mAdjustImpurityDeductedMode = AdjustDeductedInfo.getImpurityDeductedMode();
		mAdjustIncidentalExpensesDeductedMode = AdjustDeductedInfo.getIncidentalExpensesDeductedMode();
		mAdjustBakedExpensesDeductedMode = AdjustDeductedInfo.getBakedExpensesDeductedMode();
		
		if (ResultQualityInfos == null) {
			return;
		}
		//质量指标信息
		mQualityIndexResultInfoList = ResultQualityInfos;
		BindQualityIndexData();
	}
	
	private void BindQualityIndexData() {
		//清空显示信息
    	mQualityIndexItemDataList.clear();
    	//从数据库获取化验指标id,name
		try {
			mQualityIndexlist = mQualityIndexDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
    	//如果mIndexBeansListWeb(webservice)和mQualityIndexlist(数据库)id相同，名称和数值绑定到mQualityIndexItem
		//以webservice返回数据为主，数据库中存储所有指标id和name，web返回当前卡号包含的指标id
		if ((mQualityIndexlist.size()> 0)&&(mQualityIndexResultInfoList.size()>0)) {
			for (int i = 0; i < mQualityIndexResultInfoList.size(); i++) {
				QualityIndexItemData qualityIndexItemData = new QualityIndexItemData();
				int indexid = 0;
				for (indexid = 0; indexid < mQualityIndexlist.size(); indexid++) {
					if (mQualityIndexResultInfoList.get(i).getQualityIndexID() == mQualityIndexlist.get(indexid).getQuality_index_id()) {
						break;
					}
				}
				if (indexid == mQualityIndexlist.size()) {
					return;
				}
				qualityIndexItemData.IndexName = mQualityIndexlist.get(indexid).getQuality_index_name();
				qualityIndexItemData.IndexValue = String.valueOf(mQualityIndexResultInfoList.get(i).getQualityIndexResult());
				if (!mQualityIndexItemDataList.contains(qualityIndexItemData)) {
					mQualityIndexItemDataList.add(qualityIndexItemData);
				}
			}
		}  
	}
	
		
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	登记车辆作业按钮实现
	 *
	 */
	class RegistVehicleWorkOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(AdjustDeductionDiscountActivity.this, RegistVehicleWorkActivity.class);
			startActivity(intent);
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-8
	 * @description	读卡，化验指标类，处理读卡TreeMap使用
	 *
	 */
	class Assay{
		String index;
		String value;
	}
	
	private boolean SaveByNet() {
		AdjustDeductedRequest adjustDeductedRequest = new AdjustDeductedRequest();
		adjustDeductedRequest.setVehicleRFIDTag(mCardID);
		
		DeductedInfo AdjustDeductedInfo = new DeductedInfo();
		AdjustDeductedInfo.setIsDeducted(true);
		
		String AcountName = mLoginSharedPreferences.getString("current_user", "");
		int currentUserID = GetCurrentUserID(AcountName);
		if (currentUserID == -1) {
			Message message = new Message();
			message.what = Msg_SaveAdjustDecutedInfoFailedNeedSyncAcountInfo;
			mHandler.sendMessage(message);
			return false;
		}
		
		AdjustDeductedInfo.setAssayOperatorID(currentUserID);
		AdjustDeductedInfo.setMoistureDeducted(Double.valueOf(mAdjustMoistureDeducted));
		AdjustDeductedInfo.setMoistureDeductedMode(mAdjustMoistureDeductedMode);
		AdjustDeductedInfo.setImpurityDeducted(Double.valueOf(mAdjustImpurityDeducted));
		AdjustDeductedInfo.setImpurityDeductedMode(mAdjustImpurityDeductedMode);
		AdjustDeductedInfo.setIncidentalExpensesDeducted(Double.valueOf(mAdjustIncidentalExpensesDeducted));
		AdjustDeductedInfo.setIncidentalExpensesDeductedMode(mAdjustIncidentalExpensesDeductedMode);
		AdjustDeductedInfo.setBakedExpensesDeducted(Double.valueOf(mAdjustBakedExpensesDeducted));
		AdjustDeductedInfo.setBakedExpensesDeductedMode(mAdjustBakedExpensesDeductedMode);
		adjustDeductedRequest.setAdjustDeductedInfo(AdjustDeductedInfo);
		
		mAdjustDeductedResponse = (AdjustDeductedResponse)mRestWebServiceAdapter.Rest(adjustDeductedRequest);
		
		//二次扣量扣价信息发送给服务器
		if (mAdjustDeductedResponse == null) {
			return false;
		}
		//获取服务器返回信息进行解析
		if (mAdjustDeductedResponse.getResponseResult()) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean SaveByCard() {
		if (mRfidAdapter.HasCard() == null) {
			Message message = new Message();
			message.what = Msg_SaveAdjustDecutedInfoFailedNoCard;
			mHandler.sendMessage(message);
			return false;
		}
		mRfidAdapter.setF_2("1");
		
		if (mAdjustMoistureDeductedMode == 0) {
			mRfidAdapter.setD5(String.valueOf(mAdjustMoistureDeducted));
		} else if (mAdjustMoistureDeductedMode == 1) {
			mRfidAdapter.setD5(String.valueOf(mAdjustMoistureDeducted*100));
		}
		mRfidAdapter.setF5(String.valueOf(mAdjustMoistureDeductedMode));
		
		if (mAdjustImpurityDeductedMode == 0) {
			mRfidAdapter.setD6(String.valueOf(mAdjustImpurityDeducted));
		} else if (mAdjustImpurityDeductedMode == 1) {
			mRfidAdapter.setD6(String.valueOf(mAdjustImpurityDeducted*100));
		}
		mRfidAdapter.setF6(String.valueOf(mAdjustImpurityDeductedMode));
		
		if (mAdjustIncidentalExpensesDeductedMode == 0) {
			mRfidAdapter.setD7(String.valueOf(mAdjustIncidentalExpensesDeducted));
		} else if (mAdjustIncidentalExpensesDeductedMode == 1) {
			mRfidAdapter.setD7(String.valueOf(mAdjustIncidentalExpensesDeducted*100));
		}
		mRfidAdapter.setF7(String.valueOf(mAdjustIncidentalExpensesDeductedMode));
		
		if (mAdjustBakedExpensesDeductedMode ==0) {
			mRfidAdapter.setD8(String.valueOf(mAdjustBakedExpensesDeducted));
		} else if (mAdjustBakedExpensesDeductedMode == 1) {
			mRfidAdapter.setD8(String.valueOf(mAdjustBakedExpensesDeducted*100));
		}
		mRfidAdapter.setF8(String.valueOf(mAdjustBakedExpensesDeductedMode));
		
		boolean res = SyncBlock.SyncBlock2(mRfidAdapter,AdjustDeductionDiscountActivity.this);
		if (res) {
			return true;
		} else {
			return false;
		}
	}
	
	
	private GetDecutedInfoResponse TestGetDecutedInfoResponse() {
		GetDecutedInfoResponse getDecutedInfoResponse = new GetDecutedInfoResponse();
		
		DeductedInfo assayDeductedInfo = new DeductedInfo();
		assayDeductedInfo.setMoistureDeductedMode(0);
		assayDeductedInfo.setImpurityDeducted(0);
		assayDeductedInfo.setIncidentalExpensesDeductedMode(1);
		assayDeductedInfo.setBakedExpensesDeductedMode(1);
		assayDeductedInfo.setMoistureDeducted(1.2);
		assayDeductedInfo.setImpurityDeducted(2.3);
		assayDeductedInfo.setIncidentalExpensesDeducted(3.4);
		assayDeductedInfo.setBakedExpensesDeducted(4.5);
		getDecutedInfoResponse.setAssayDeductedInfo(assayDeductedInfo);
		
		
		DeductedInfo adjustDeductedInfo = new DeductedInfo();
		adjustDeductedInfo.setMoistureDeductedMode(0);
		adjustDeductedInfo.setImpurityDeducted(0);
		adjustDeductedInfo.setIncidentalExpensesDeductedMode(0);
		adjustDeductedInfo.setBakedExpensesDeductedMode(0);
		adjustDeductedInfo.setMoistureDeducted(0);
		adjustDeductedInfo.setImpurityDeducted(0);
		adjustDeductedInfo.setIncidentalExpensesDeducted(0);
		adjustDeductedInfo.setBakedExpensesDeducted(0);
		getDecutedInfoResponse.setAdjustDeductedInfo(adjustDeductedInfo);
		
		
		List<QualityIndexResultInfo> qualityIndexResultInfos = new ArrayList<QualityIndexResultInfo>();
		for (int i = 1; i < 5; i++) {
			QualityIndexResultInfo qualityIndexResultInfo = new QualityIndexResultInfo();
			qualityIndexResultInfo.setQualityIndexID(i);
			qualityIndexResultInfo.setQualityIndexResult(i+i*0.1);
			qualityIndexResultInfos.add(qualityIndexResultInfo);
		}
		getDecutedInfoResponse.setResultQualityInfos(qualityIndexResultInfos);
		
		return null;
	}
	
	private int GetCurrentUserID(String acountName) {
		QueryBuilder<AcountInfo,Integer> queryBuilder = mAcountInfoDao.queryBuilder();
		int userID = -1;
		try {
			queryBuilder.where().eq("acount_name", acountName);
			List<AcountInfo> acountInfos = queryBuilder.query();
			if (acountInfos.size()>0) {
				userID = acountInfos.get(0).getUser_id();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userID;
	}
	
	class AdjustMoistureDeductedUnitOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(mAdjustMoistureDeductedMode == -1){
				return;			
			} else if (mAdjustMoistureDeductedMode == 0) {
				mAdjustMoistureDeductedMode = 1;
				mAdjustMoistureDeductedEditText.setText("");
				mAdjustMoistureDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
				mAdjustMoistureDeductedUnitButton.setText(getString(R.string.percent));
			} else if (mAdjustMoistureDeductedMode == 1) {
				mAdjustMoistureDeductedMode = 0;
				mAdjustMoistureDeductedEditText.setText("");
				mAdjustMoistureDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
				mAdjustMoistureDeductedUnitButton.setText(getString(R.string.kilogram));
			}
		}
	}
	
	class AdjustImpurityDeductedUnitOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if(mAdjustImpurityDeductedMode == -1){
				return;			
			} else if (mAdjustImpurityDeductedMode == 0) {
				mAdjustImpurityDeductedMode = 1;
				mAdjustImpurityDeductedEditText.setText("");
				mAdjustImpurityDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
				mAdjustImpurityDeductedUnitButton.setText(getString(R.string.percent));
			} else if (mAdjustImpurityDeductedMode == 1) {
				mAdjustImpurityDeductedMode = 0;
				mAdjustImpurityDeductedEditText.setText("");
				mAdjustImpurityDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
				mAdjustImpurityDeductedUnitButton.setText(getString(R.string.kilogram));
			}
		}
	}
	
	class AdjustIncidentalExpensesDeductedUnitOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mPopupWindowDialog.showMessage("该功能暂未开放");
			return;
//			if(mAdjustIncidentalExpensesDeductedMode == -1){
//				return;			
//			} else if (mAdjustIncidentalExpensesDeductedMode == 0) {
//				mAdjustIncidentalExpensesDeductedMode = 0;
//				mAdjustIncidentalExpensesDeductedEditText.setText("");
//				mAdjustIncidentalExpensesDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//				mAdjustIncidentalExpensesDeductedUnitButton.setText(getString(R.string.yuan));
//				//设置成只能扣总金额数量，不能按百分比扣金额
////				mAdjustIncidentalExpensesDeductedMode = 1;
////				mAdjustIncidentalExpensesDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
////				mAdjustIncidentalExpensesDeductedUnitButton.setText(getString(R.string.percent));
//			} else if (mAdjustIncidentalExpensesDeductedMode == 1) {
//				mAdjustIncidentalExpensesDeductedMode = 0;
//				mAdjustIncidentalExpensesDeductedEditText.setText("");
//				mAdjustIncidentalExpensesDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//				mAdjustIncidentalExpensesDeductedUnitButton.setText(getString(R.string.yuan));
//			}
		}
	}
	
	class AdjustBakedExpensesDeductedUnitOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mPopupWindowDialog.showMessage("该功能暂未开放");
			return;
//			if(mAdjustBakedExpensesDeductedMode == -1){
//				return;			
//			} else if (mAdjustBakedExpensesDeductedMode == 0) {
//				mAdjustBakedExpensesDeductedMode = 0;
//				mAdjustBakedExpensesDeductedEditText.setText("");
//				mAdjustBakedExpensesDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//				mAdjustBakedExpensesDeductedUnitButton.setText(getString(R.string.yuan));
//				//设置成只能扣总金额数量，不能按百分比扣金额
////				mAdjustBakedExpensesDeductedMode = 1;
////				mAdjustBakedExpensesDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
////				mAdjustBakedExpensesDeductedUnitButton.setText(getString(R.string.percent));
//			} else if (mAdjustBakedExpensesDeductedMode == 1) {
//				mAdjustBakedExpensesDeductedMode = 0;
//				mAdjustBakedExpensesDeductedEditText.setText("");
//				mAdjustBakedExpensesDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//				mAdjustBakedExpensesDeductedUnitButton.setText(getString(R.string.yuan));
//			}
		}
	}
	
	private void GetDecutedInfoBusiness() {
		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "正在获取中....");
		OnCancelListener GetProcessDialogCancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//显示扣量扣价信息
				DisplayInfo();
			}
		};
		mProcessDialog.setOnCancelListener(GetProcessDialogCancelListener);
		mProcessDialog.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				GetData();
				Looper.loop();
			}
		}).start();
	}
	
	private void ProcessGetData() {
		if (mGrainApplication.SWITCH) {	//开启联网模式
			if (LoginActivity.NET_AVILIABE) {	//网络可用
				GetDataByNet();
				if (mGetDecutedInfoResponse == null) {	//获取扣量扣价信息失败
					GetDataByCard();
				}
			} else {	//网络不可用
				GetDataByCard();
			}
		} else {	//没有开启联网模式
			GetDataByCard();
		}
	}
	
	private void GetData() {
		ProcessGetData();
		if (mGetDecutedInfoResponse == null) {
			mProcessDialog.cancel();
			return;
		}
		AnalyseDecutedInfo(mGetDecutedInfoResponse);
		mProcessDialog.cancel();
	}
	
	private void GetDataByCard() {
		DeductedInfo AssayDeductedInfo = mRfidAdapter.getAssayDeductedInfo();
		DeductedInfo AdjustDeductedInfo = mRfidAdapter.getAdjustDeductedInfo();
		List<QualityIndexResultInfo> ResultQualityInfos = mRfidAdapter.getResultQualityInfos();
		
		mGetDecutedInfoResponse = new GetDecutedInfoResponse();
		mGetDecutedInfoResponse.setAssayDeductedInfo(AssayDeductedInfo);
		mGetDecutedInfoResponse.setAdjustDeductedInfo(AdjustDeductedInfo);
		mGetDecutedInfoResponse.setResultQualityInfos(ResultQualityInfos);
	}
	
	private void GetDataByNet() {
		//网络获取
		GetDecutedInfoRequest getDecutedInfoRequest = new GetDecutedInfoRequest();
		getDecutedInfoRequest.setVehicleRFIDTag(mCardID);
		
		if (DEBUG) {
			mGetDecutedInfoResponse = TestGetDecutedInfoResponse();
		} else {
			mGetDecutedInfoResponse = (GetDecutedInfoResponse)mRestWebServiceAdapter.Rest(getDecutedInfoRequest);
		}
		
		if (mGetDecutedInfoResponse == null) {
    		Message message = new Message();
    		message.what = Msg_GetDecutedInfoFailedByNet;
    		mHandler.sendMessage(message);
		}
	}
	
	private void DisplayInfo() {
		DisplayDecutedInfo(mGetDecutedInfoResponse);
		InitListView();
	}
	
	private IReadCard mIReadCard = new IReadCard() {
		
		@Override
		public void ReadCard() {
			Log.v(TAG, "ReadCard()");
			//读卡获取数据
			if(!ReadByCard()){
				return;
			}
			
			if (mSigned.equals(String.valueOf(Constants.CARD_NOT_ISSUED))) {
				Log.v(TAG, "卡未发行");
				mPopupWindowDialog.showMessage("卡未发行");
			}
			
			if (mSigned.equals(String.valueOf(Constants.MOBILE_RETREATED))) {
				Log.v(TAG, "已在手持机退卡");
				mPopupWindowDialog.showMessage("已在手持机退卡");
			}
			
			if (mSigned.equals(String.valueOf(Constants.CARD_ISSUED))) {
				GetDecutedInfoBusiness();
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		mGrainBroadcastReceiver.setIReadCard(null);
		super.onDestroy();
	}
}
