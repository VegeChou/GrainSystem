package com.aisino.grain.ui;

import java.util.List;

import com.aisino.grain.model.ParseLoginInfo;

import com.aisino.grain.R;
import com.aisino.grain.ui.util.MenuImageView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends CustomMenuActivity {
//	private static final String TAG = "MainActivity";
	
	private MenuImageView mWarehouseInfoButton = null;					//仓库信息
	private MenuImageView mRegisterVehicleWorkButton = null;			//登记车辆作业
	private MenuImageView mAdjustQualityInfoButton = null;				//调整扣量扣价
	private MenuImageView mTaskingButton = null;						//处理扦样任务
	private MenuImageView mTaskedButton = null;							//已处理扦样任务
	private MenuImageView mRegistWeighingWeightButton = null;			//登记称重重量
	private MenuImageView mRetreatButton = null;						//收卡退港
	private MenuImageView mHelpButton = null;							//帮助
	
	private List<String> mOperList = null;						//操作权限列表
	
	private SharedPreferences mLoginSharedPreferences = null;	//保存登录信息]
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_main);
		super.onCreate(savedInstanceState);
		InitCtrl();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			mPopupWindowDialog.exitDialog();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void InitCtrl(){
		mLoginSharedPreferences = getSharedPreferences("LoginInformation", Context.MODE_PRIVATE);
		GetOperation();
		
		mWarehouseInfoButton = (MenuImageView)findViewById(R.id.main_btn_warehouse_info);
		mRegisterVehicleWorkButton = (MenuImageView)findViewById(R.id.main_btn_register_vehicle_work);
		mAdjustQualityInfoButton = (MenuImageView)findViewById(R.id.main_btn_adjust_quality_info);
		mTaskingButton = (MenuImageView)findViewById(R.id.main_btn_tasking);
		mTaskedButton = (MenuImageView)findViewById(R.id.main_btn_tasked);
		mRegistWeighingWeightButton = (MenuImageView)findViewById(R.id.main_btn_regist_weighting_weight);
		mRetreatButton = (MenuImageView)findViewById(R.id.main_btn_retreat);
		mHelpButton = (MenuImageView)findViewById(R.id.main_btn_help);
		
		MyListener listener = new MyListener();
		mWarehouseInfoButton.setOnClickListener(listener);
		mWarehouseInfoButton.setTag(1);
		mRegisterVehicleWorkButton.setOnClickListener(listener);
		mRegisterVehicleWorkButton.setTag(2);
		mAdjustQualityInfoButton.setOnClickListener(listener);
		mAdjustQualityInfoButton.setTag(3);
		mTaskingButton.setOnClickListener(listener);
		mTaskingButton.setTag(4);
		mTaskedButton.setOnClickListener(listener);
		mTaskedButton.setTag(5);
		mRegistWeighingWeightButton.setOnClickListener(listener);
		mRegistWeighingWeightButton.setTag(6);
		mRetreatButton.setOnClickListener(listener);
		mRetreatButton.setTag(7);
		mHelpButton.setOnClickListener(listener);
		mHelpButton.setTag(8);
		
		FunctionUsable();
		
	}
	
	class MyListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			int tag = (Integer)v.getTag();
			switch (tag) {
			case 1:
				intent.setClass(MainActivity.this, WarehouseInfoActivity.class);
				break;
			case 2:
				intent.setClass(MainActivity.this, RegistVehicleWorkActivity.class);
				break;
			case 3:
				intent.setClass(MainActivity.this, AdjustDeductionDiscountActivity.class);
				break;
			case 4:
				intent.setClass(MainActivity.this, TaskingActivity.class);
				break;
			case 5:
				intent.setClass(MainActivity.this, TaskedActivity.class);
				break;
			case 6:
				intent.setClass(MainActivity.this, RegistWeighingWeightActivity.class);
				break;
			case 7:
				intent.setClass(MainActivity.this, RetreatActivity.class);
				break;
			case 8:
				intent.setClass(MainActivity.this, HelpActivity.class);
				break;

			default:
				break;
			}
			startActivity(intent);
		}
	} 
	
	private void GetOperation() {
		String operations = null;
		operations = mLoginSharedPreferences.getString("operations", "0");
		mOperList = ParseLoginInfo.ParseOpeartions(operations);
	}
	
	private void FunctionUsable() {
		if (!mOperList.contains("devicestorage")) {
			mWarehouseInfoButton.setEnabled(false);
			mWarehouseInfoButton.setImageResource(R.drawable.warehouse_info_disable);
		}
		if (!mOperList.contains("deviceregerist")) {
			mRegisterVehicleWorkButton.setEnabled(false);
			mRegisterVehicleWorkButton.setImageResource(R.drawable.register_vehicle_work_disable);
		}
		if (!mOperList.contains("devicededucted")) {
			mAdjustQualityInfoButton.setEnabled(false);
			mAdjustQualityInfoButton.setImageResource(R.drawable.adjust_quality_info_disable);
		}
		if (!mOperList.contains("deviceundealed")) {
			mTaskingButton.setEnabled(false);
			mTaskingButton.setImageResource(R.drawable.tasking_disable);
		}
		if (!mOperList.contains("devicedealed")) {
			mTaskedButton.setEnabled(false);
			mTaskedButton.setImageResource(R.drawable.tasked_disable);
		}
		if (!mOperList.contains("deviceweight")) {
			mRegistWeighingWeightButton.setEnabled(false);
			mRegistWeighingWeightButton.setImageResource(R.drawable.regist_weighting_weight_disable);
		}
		if (!mOperList.contains("devicefinish")) {
			mRetreatButton.setEnabled(false);
			mRetreatButton.setImageResource(R.drawable.retreat_disable);
		}
	}
	
//	/**
//	 * 
//	 * @author zwz
//	 * @date 2013-7-15
//	 * @description	退出菜单确认退出响应函数
//	 *
//	 */
//	class PositiveOnClickListener implements DialogInterface.OnClickListener{
//		@Override
//		public void onClick(DialogInterface dialog, int which) {
//			//程序终止，停止线程检测网路状态
//			THREAD_FLAG = false;
//			LoginActivity.CloseAllActivity();
//		}
//	}
	
//	@Override
//	protected void onDestroy() {
//		LoginActivity.ActivityList.remove(this);
//		super.onDestroy();
//	}
}
