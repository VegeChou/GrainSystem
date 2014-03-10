package com.aisino.grain.ui.demo;

import com.aisino.grain.R;
import com.aisino.grain.ui.HelpActivity;
import com.aisino.grain.ui.util.MenuImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends BaseActivity {
	private MenuImageView mWarehouseInfoButton = null;					//仓库信息
	private MenuImageView mRegisterVehicleWorkButton = null;			//登记车辆作业
	private MenuImageView mAdjustQualityInfoButton = null;				//调整扣量扣价
	private MenuImageView mTaskingButton = null;						//处理扦样任务
	private MenuImageView mTaskedButton = null;							//已处理扦样任务
	private MenuImageView mRegistWeighingWeightButton = null;			//登记称重重量
	private MenuImageView mRetreatButton = null;						//收卡退港
	private MenuImageView mHelpButton = null;							//帮助
	
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
				intent.setClass(MainActivity.this, com.aisino.grain.ui.RegistVehicleWorkActivity.class);
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
}
