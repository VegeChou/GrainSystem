package com.aisino.grain.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.aisino.grain.R;
import com.aisino.grain.model.Constants;
import com.aisino.grain.model.SyncBlock;
import com.aisino.grain.model.db.Warehouse;
import com.aisino.grain.model.rfid.RfidAdapter;
import com.aisino.grain.model.rfid.RfidAdapter.Block16Etc;
import com.j256.ormlite.dao.Dao;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RetreatActivity extends CustomMenuActivity {
	
	private double mMoistureDeductAmountFirst = 0;						//初次水分扣量
	private double mImpurityDeductAmountFirst = 0;						//初次杂质扣量
	private double mMoistureDeductAmountAdjust = 0;						//水分扣量加扣
	private double mImpurityDeductAmountAdjust = 0;						//杂质扣量加扣
	private int mMoistureAmountFlagFirst = -1;							//初次水分扣量单位标记
	private int mImpurityAmountFlagFirst = -1;							//初次杂质扣量单位标记
	private int mMoistureAmountFlagAdjust = -1;							//水分扣量单位加扣标记
	private int mImpurityAmountFlagAdjust = -1;							//杂质扣量单位加扣标记
	private int mAdjustDeductFlag = -1;									//二次扣量标记
	
	private HashMap<?, ?> mHashMap = null;									//读取称重信息hash表
	
//	private String mLeagal = null;						//合法性
	private String mVarieties = null;					//品种
	private String mLicensePlate = null;				//车牌
	private String mOwners = null;						//货主
	private String mType = null;						//类型
	private String mLink = null;						//环节
	private String mGrossWeight = null;					//毛重
	private String mTare = null;						//皮重
	private String mNetWeigth = null;					//净重
	private String mDeduction = null;					//扣量
	private String mSigned = null;						//标记位
	
	
	private int mWarehouseID = -1;						//值仓仓库ID
	
	private TextView mVarietiesTextView = null;			//品种编辑框
	private TextView mLicensePlateTextView = null;		//车牌编辑框
	private TextView mOwnersTextView = null;			//货主编辑框
	private TextView mTypeTextView = null;				//类型编辑框
	private TextView mLinkTextView= null;				//环节编辑框
	private TextView mGrossWeightTextView = null;		//毛重编辑框
	private TextView mTareTextView = null;				//皮重编辑框
	private TextView mNetWeigthTextView = null;			//净重编辑框
	private TextView mDeductionTextView = null;			//扣量编辑框
	private TextView mUploadingWarehousTextView = null;	//卸粮仓库编辑框
	
	private Button mReadCardButton = null;					//读卡按钮
	private Button mRetreatButton = null;						//收港退卡按钮
	private TextView mStorehouseWarehouseTextView = null;	//值仓仓库编辑框
	private TextView mPromptTextView = null;				//提示编辑框
	
	private String mUpLoadingWarehouseID = null;					//卸粮仓库ID
	private String mUpLoadingWarehouseName = null;			//卸粮仓库名称
	
	private Dao<Warehouse, Integer> mWarehouseDao =  null;		//仓库信息Dao
	private List<Warehouse> mWarehouselist = null;				//查询结果
	
	private SharedPreferences sharedPreferences = null;			//保存选中的仓库
	
	private int mSelectedID = 0;								//仓库列表选中ID
	private boolean mBusinessFinishFlag = false;				//业务完成标志
	
	private RfidAdapter mRfidAdapter = null;					//rfid读卡适配器
	
	private Handler mHandler = null;						//线程和UI传递消息
	private boolean mReadCardThreadFlag = false;
	private String mCardIDRemember = null;					//已读过卡ID
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_retreat);
		super.onCreate(savedInstanceState);
		InitCtrl();
//		LoginActivity.ActivityList.add(this);
	}
	
	private void InitCtrl(){
		mRfidAdapter = RfidAdapter.getInstance(this);
		
		sharedPreferences = getSharedPreferences("WarehouseInfo", Context.MODE_PRIVATE);
		
		mWarehouseDao = getHelper().getWarehouseDao();
		mWarehouselist = new ArrayList<Warehouse>();
		
		//绑定
		mReadCardButton = (Button)findViewById(R.id.retreat_btn_read_card);
		mRetreatButton = (Button)findViewById(R.id.retreat_btn_retreat);
		mRetreatButton.setEnabled(false);
		mRetreatButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
		mStorehouseWarehouseTextView = (TextView)findViewById(R.id.retreat_tv_storehouse_warehouse);
		mPromptTextView = (TextView)findViewById(R.id.retreat_tv_prompt);
		
		mUploadingWarehousTextView = (TextView)findViewById(R.id.retreat_tv_warehouse);
		mVarietiesTextView = (TextView)findViewById(R.id.retreat_tv_varieties);
		mLicensePlateTextView = (TextView)findViewById(R.id.retreat_tv_license_plate);
		mOwnersTextView = (TextView)findViewById(R.id.retreat_tv_owners);
		mTypeTextView = (TextView)findViewById(R.id.retreat_tv_type);
		mLinkTextView = (TextView)findViewById(R.id.retreat_tv_link);
		mGrossWeightTextView = (TextView)findViewById(R.id.retreat_tv_gross_weight);
		mTareTextView = (TextView)findViewById(R.id.retreat_tv_tare);
		mNetWeigthTextView = (TextView)findViewById(R.id.retreat_tv_net_weight);
		mDeductionTextView = (TextView)findViewById(R.id.retreat_tv_deduction);
		
		mReadCardButton.setOnClickListener(new ReadCardOnClickListener()); 
		mRetreatButton.setOnClickListener(new RetreatOnClickListener());
		
		//读配置信息，设置 选中仓库
		InitWarehouseName();
		
		//开启读卡线程
//		StartReadCard();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SCAN) {
			//读卡获得IC卡的信息，按照业务逻辑进行处理
			if (mRfidAdapter.HasCard() == null) {
				mPopupWindowDialog.showMessage("读卡失败");
				return false;
			}
			
			String data = mRfidAdapter.getTagNo();
			if (data == null) {
				mPopupWindowDialog.showMessage("获取标记位信息失败");
				return false;
			}
//			if (RegistVehicleWorkActivity.IsInitData(data)||(data.equals("0"))) {
			if ((data.equals("0"))) {
				mPopupWindowDialog.showMessage("卡还未在业务中发行");
				return false;
			}
//			mSigned = Integer.parseInt(data);
			mSigned = data;
			
			data = mRfidAdapter.getOperateLink();
			if (data == null) {
				mPopupWindowDialog.showMessage("获取作业环节信息失败");
				return false;
			}
//			if (!RegistVehicleWorkActivity.IsInitData(data)) {
//				mLink = Integer.parseInt(data);
				mLink = data;
//			}
			
			data = mRfidAdapter.getStorage();
			if (data == null) {
				mPopupWindowDialog.showMessage("获取仓库信息失败");
				return false;
			}
//			mUpLoadingWarehouseID = Integer.parseInt(data);
			mUpLoadingWarehouseID = data;
			GetUpLoadingWarehouseName();
			
			if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
				mPopupWindowDialog.showMessage("卡未发行");
			}
			if (mSigned.equals(Constants.MOBILE_RETREATED)) {
				mPopupWindowDialog.showMessage("已在手持机退卡");
			}
			if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
				//值仓仓库与卸粮仓库是否一致
				if (mWarehouselist.size() == 0) {
					mPopupWindowDialog.showMessage("获取值仓仓库信息失败");
					return false;
				}
				if (Integer.valueOf(mUpLoadingWarehouseID) == mWarehouseID) {
					//称重记录包含毛皮记录	游标记录为偶数
					String Cursor1 = mRfidAdapter.getCursor1();
					if (Cursor1 == null) {
						return false;
					}
					if (IsPositiveEven(Integer.valueOf(Cursor1))) {
						mBusinessFinishFlag = true;
						mPromptTextView.setText("业务结束收卡退港，请保管员在退港后收回业务结算卡!");
					}
					//只有毛重或者没有称重记录  游标记录为奇数或者为0
					if (IsPositiveOddOrZero(Integer.valueOf(Cursor1))) {
						mBusinessFinishFlag = false;
						mPromptTextView.setText("非正常退港，业务未完成，请保管员核实确认，如确认退港请在退港后收回结算卡！");
					}
					mRetreatButton.setEnabled(true);
					mRetreatButton.setBackgroundResource(R.drawable.corner_btn_selector);
				}
				else {
					GetUpLoadingWarehouseName();
					mPromptTextView.setText("该车辆应当到"+mUpLoadingWarehouseName+"作业");
				}
			}
			Display();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void ReadCard(){
		//读卡获得IC卡的信息，按照业务逻辑进行处理
		if (mRfidAdapter.HasCard() == null) {
			mPopupWindowDialog.showMessage("读卡失败");
		}
		
		String data = mRfidAdapter.getTagNo();
		if (data == null) {
			mPopupWindowDialog.showMessage("获取标记位信息失败");
		}
		
//		mSigned = Integer.parseInt(data);
		mSigned = data;
		
		data = mRfidAdapter.getOperateLink();
		if (data == null) {
			mPopupWindowDialog.showMessage("获取作业环节信息失败");
		}
//		if (!RegistVehicleWorkActivity.IsInitData(data)) {
//			mLink = Integer.parseInt(data);
			mLink = data;
//		}
		
		data = mRfidAdapter.getStorage();
		if (data == null) {
			mPopupWindowDialog.showMessage("获取仓库信息失败");
		}
//		mUpLoadingWarehouseID = Integer.parseInt(data);
		mUpLoadingWarehouseID = data;
		GetUpLoadingWarehouseName();
		
		if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
			mPopupWindowDialog.showMessage("卡未发行");
		}
		if (mSigned.equals(Constants.MOBILE_RETREATED)) {
			mPopupWindowDialog.showMessage("已在手持机退卡");
		}
		if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
			//值仓仓库与卸粮仓库是否一致
			if (mWarehouselist.size() == 0) {
				mPopupWindowDialog.showMessage("获取值仓仓库信息失败");
			}
			if (Integer.valueOf(mUpLoadingWarehouseID) == mWarehouseID) {
				//称重记录包含毛皮记录	游标记录为偶数
				String Cursor1 = mRfidAdapter.getCursor1();
				if (Cursor1 == null) {
				}
				if (IsPositiveEven(Integer.valueOf(Cursor1))) {
					mBusinessFinishFlag = true;
					mPromptTextView.setText("业务结束收卡退港，请保管员在退港后收回业务结算卡!");
				}
				//只有毛重或者没有称重记录  游标记录为奇数或者为0
				if (IsPositiveOddOrZero(Integer.valueOf(Cursor1))) {
					mBusinessFinishFlag = false;
					mPromptTextView.setText("非正常退港，业务未完成，请保管员核实确认，如确认退港请在退港后收回结算卡！");
				}
				mRetreatButton.setEnabled(true);
				mRetreatButton.setBackgroundResource(R.drawable.corner_btn_selector);
			}
			else {
				GetUpLoadingWarehouseName();
				mPromptTextView.setText("该车辆应当到"+mUpLoadingWarehouseName+"作业");
			}
		}
		Display();
	}
	
	private void InitWarehouseName() {
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		
		//从数据库获取数据粮仓号
		try {
			mWarehouselist = mWarehouseDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		if (mWarehouselist.size() == 0) {
			return;
		}
		mStorehouseWarehouseTextView.setText(mWarehouselist.get(selectedid).getWarehouse_name());
		mWarehouseID = mWarehouselist.get(selectedid).getWarehouse_id();
	}
	
	class ReadCardOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			ReadCard();
		}
	}
	
	class RetreatOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//业务是否完成
			if (mBusinessFinishFlag) {
				//正常退港，退港标记位2，写入卡
				mRfidAdapter.setTagNo("2");
				boolean res1 = SyncBlock.SyncBlock1(mRfidAdapter,RetreatActivity.this);
				int res8 = mRfidAdapter.cleanHYData();
				int res13 = mRfidAdapter.cleanCZData();
				if (res1 && res8 ==0 && res13 ==0) {
					mPopupWindowDialog.showMessage("正常退港成功");
				} else {
					mPopupWindowDialog.showMessage("正常退港失败");
				}
			} else {
				new AlertDialog.Builder(RetreatActivity.this)
				.setTitle("提示")
				.setMessage("该业务未完成，确认要退港吗？")
				.setPositiveButton("是", new PositiveOnClickListener())
				.setNegativeButton("否", null)
				.show();
			}
		}
	}
	
	class PositiveOnClickListener implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			//非正常退港，标记位为2，写卡
			mRfidAdapter.setTagNo("2");
			boolean res1 = SyncBlock.SyncBlock1(mRfidAdapter,RetreatActivity.this);
			int res8 = mRfidAdapter.cleanHYData();
			int res13 = mRfidAdapter.cleanCZData();
			if (res1 && res8 ==0 && res13 ==0) {
				mPopupWindowDialog.showMessage("非正常退港成功");
			} else {
				mPopupWindowDialog.showMessage("非正常退港失败");
			}
		}
	}
	
	private boolean IsPositiveEven(int num) {	//正偶数
		if(num > 0 && num % 2 == 0){
			return true;
		}
		return false;
	}
	
	private boolean IsPositiveOddOrZero(int num) {	//正奇数或0
		if((num > 0 && num % 2 == 1) || (num == 0)){
			return true;
		}
		return false;
	}
	
	private void Display() {
		String string = null;
		string = mRfidAdapter.getVarietyNo();
		if (string == null) {
			mVarieties = "";
		}
		mVarieties = string;
		string = mRfidAdapter.getCarNum();
		if (string == null) {
			mLicensePlate = "";
		}
		mLicensePlate = string;
		string = mRfidAdapter.getOwnerName();
		if (string == null) {
			mOwners = "";
		}
		mOwners = string;
		string = mRfidAdapter.getBusinessType();
		if (string == null) {
			mType = "";
		}
		mType = string;
		
		//读卡只显示毛皮净(后期确认)
		ReadWeight();
		CalcuDeduction();
		
		//显示读卡最终信息
		mVarietiesTextView.setText(mVarieties);
		mLicensePlateTextView.setText(mLicensePlate);
		mOwnersTextView.setText(mOwners);
		mTypeTextView.setText(mType);
		SetLinkTextView();
		mGrossWeightTextView.setText(String.valueOf(mGrossWeight));
		mTareTextView.setText(String.valueOf(mTare));
		mNetWeigthTextView.setText(String.valueOf(mNetWeigth));
		mDeductionTextView.setText(String.valueOf(mDeduction));
		mUploadingWarehousTextView.setText(mUpLoadingWarehouseName);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-8
	 * @description	读卡获取称重信息
	 *
	 */
	private void ReadWeight() {
		mTare = "0";
		mGrossWeight = "0";
		mNetWeigth = "0";
		
		mHashMap = mRfidAdapter.getWeightData();
		
		if (mHashMap == null) {
			return;
		}
		
		Block16Etc etc = null;
		Iterator<?> it = mHashMap.entrySet().iterator();
		while (it.hasNext()) {
			// entry的输出结果如key0=value0等
			@SuppressWarnings("rawtypes")
			Map.Entry entry =(Map.Entry) it.next();
			@SuppressWarnings("unused")
			Object key = entry.getKey();
			Object value = entry.getValue();
			etc = (Block16Etc)value;
			if (IsInitData(etc.mFur)) {
				mTare = "0";
				mGrossWeight = "0";
				mNetWeigth = "0";
				return;
			}
			if (Integer.parseInt(etc.mFur) == 0) {	//皮重
				mTare = etc.mRoughWeight;
			}
			if (Integer.parseInt(etc.mFur) == 1) {	//毛重
				mGrossWeight = etc.mRoughWeight;
			}
			if (Integer.parseInt(etc.mFur) == 2) {	//净重
				mNetWeigth = etc.mRoughWeight;
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-11
	 * @description	计算扣量信息：初次水分扣量+初次杂质扣量+二次水分扣量+二次杂质扣量
	 *
	 */
	private void CalcuDeduction() {
		String string = null;
		string = mRfidAdapter.getF1();
		if (string == null) {
//			mPopupWindowDialog.showMessage("读卡获取初次水分扣量标记位失败");
			string = "0";
		}
		mMoistureAmountFlagFirst = Integer.valueOf(string);
		string = mRfidAdapter.getD1();
		if (string == null) {
//			mPopupWindowDialog.showMessage("读卡获取初次水分扣量失败");
			string = "0";
		}
		if (mMoistureAmountFlagFirst == 0) {	//总量
			mMoistureDeductAmountFirst = Double.parseDouble(string);
		} else {	//百分比
			mMoistureDeductAmountFirst = Double.parseDouble(mNetWeigth)*Double.parseDouble(string)/100;
		}
		string = mRfidAdapter.getF2();
		if (string == null) {
//			mPopupWindowDialog.showMessage("读卡获取初次杂质扣量标记位失败");
			string = "0";
		}
		mImpurityAmountFlagFirst = Integer.valueOf(string);
		string = mRfidAdapter.getD2();
		if (string == null) {
//			mPopupWindowDialog.showMessage("读卡获取初次杂质扣量失败");
			string = "0";
		}
		if (mImpurityAmountFlagFirst == 0) {	//总量
			mImpurityDeductAmountFirst = Double.parseDouble(string);
		} else {	//百分比
			mImpurityDeductAmountFirst = Double.parseDouble(mNetWeigth)*Double.parseDouble(string)/100;
		}
		
		string = mRfidAdapter.getF_1();
		if (string == null) {
//			mPopupWindowDialog.showMessage("读卡获取二次扣量标记位失败");
			string = "0";
		}
		mAdjustDeductFlag = Integer.valueOf(string);
		if (mAdjustDeductFlag == 1) {	//有二次扣量
			string = mRfidAdapter.getF5();
			if (string == null) {
//				mPopupWindowDialog.showMessage("读卡获取二次水分扣量标记位失败");
				string = "0";
			}
			mMoistureAmountFlagAdjust = Integer.valueOf(string);
			string = mRfidAdapter.getD5();
			if (string == null) {
//				mPopupWindowDialog.showMessage("读卡获取二次水分扣量位失败");
				string = "0";
			}
			if (mMoistureAmountFlagAdjust == 0) {	//总量
				mMoistureDeductAmountAdjust = Double.parseDouble(string);
			} else {	//百分比
				mMoistureDeductAmountAdjust = Double.parseDouble(mNetWeigth)*Double.parseDouble(string)/100;
			}
			string = mRfidAdapter.getF6();
			if (string == null) {
//				mPopupWindowDialog.showMessage("读卡获取二次杂质扣量标记位失败");
				string = "0";
			}
			mImpurityAmountFlagAdjust = Integer.valueOf(string);
			string = mRfidAdapter.getD6();
			if (string == null) {
//				mPopupWindowDialog.showMessage("读卡获取二次杂质扣量失败");
				string = "0";
			}
			if (mImpurityAmountFlagAdjust == 0) {	//总量
				mImpurityDeductAmountAdjust = Double.parseDouble(string);
			} else {	//百分比
				mImpurityDeductAmountAdjust = Double.parseDouble(mNetWeigth)*Double.parseDouble(string)/100;
			}
			 mDeduction = String.valueOf(mMoistureDeductAmountFirst + mImpurityDeductAmountFirst+mMoistureDeductAmountAdjust+mImpurityDeductAmountAdjust);
		} else {	//没有二次扣量
			 mDeduction = String.valueOf(mMoistureDeductAmountFirst + mImpurityDeductAmountFirst);
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description 根据读卡的作业环节编号，设置作业环节显示信息
	 *
	 */
	private void SetLinkTextView() {
		if (IsInitData(mLink)) {
			mLink = "0";
		}
		switch (Integer.parseInt(mLink)) {
		case 0:
			mLinkTextView.setText("未值仓");
			break;
		case 1:
			mLinkTextView.setText("值仓确认");
			break;
		case 2:
			mLinkTextView.setText("值仓换仓");
			break;
		case 3:
			mLinkTextView.setText("重新化验");
			break;
		case 4:
			mLinkTextView.setText("称完皮重");
			break;
		case 5:
			mLinkTextView.setText("称完毛重");
			break;
		default:
			break;
		}
	}
	
	private void GetUpLoadingWarehouseName() {
		//找到仓库号对应仓库名称
		for (int i = 0; i < mWarehouselist.size(); i++) {
			if (mWarehouselist.get(i).getWarehouse_id() == Integer.valueOf(mUpLoadingWarehouseID)) {
				mUpLoadingWarehouseName = mWarehouselist.get(i).getWarehouse_name();
			}
		}
	}
	
	public static boolean IsInitData(String string){
		Pattern p = Pattern.compile("^[Ff]+$"); 
		Matcher m = p.matcher(string); 
		return m.find(); 
	}
	
	private void StartReadCard() {
		mReadCardThreadFlag = true;
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				
				while (mReadCardThreadFlag) {
					Message msg=new Message();
					
					if (mRfidAdapter.HasCard() == null) {
						//没有检测到卡
						continue;
					} else {
						//检测到卡
						if (mRfidAdapter.HasCard().equals(mCardIDRemember)) {	//已读过同一张卡
							continue;
						} else {	//未读过同一张卡
							mCardIDRemember = mRfidAdapter.HasCard();	//保存当前已读卡号
							//读取卡内信息
							mSigned = mRfidAdapter.getTagNo();
							mLink = mRfidAdapter.getOperateLink();
							mUpLoadingWarehouseID = mRfidAdapter.getStorage();
							mVarieties = mRfidAdapter.getVarietyNo();
							mLicensePlate = mRfidAdapter.getCarNum();
							mOwners = mRfidAdapter.getOwnerName();
							mType = mRfidAdapter.getBusinessType();
							
							ReadWeight();	//读卡只显示毛皮净(后期确认)
							CalcuDeduction();
							/**/
							//通知界面更新
							msg.what = 0; //消息0:
							mHandler.sendMessage(msg);
						}
						
					}
					
//					if (msg == null) {
//						msg.what = 0; //消息0:
//						mHandler.sendMessage(msg);
//					} else {
//						msg.what = 1; //消息1:回复非空
//						mHandler.sendMessage(msg);
//					}
					
					//间隔1秒判断一次卡是否存在
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
		
		mHandler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
					case 0:
						//更新界面信息
						break;
					default:
						break;
				}
			}
		};
	}
	
	@Override
	protected void onRestart() {
		StartReadCard();
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		mReadCardThreadFlag = false;
		mCardIDRemember = null;
		super.onResume();
	}
	
//	@Override
//	protected void onDestroy() {
//		mReadCardThreadFlag = false;
//		LoginActivity.ActivityList.remove(this);
//		super.onDestroy();
//	}
}
