package com.aisino.grain.ui.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Spinner;

import com.aisino.grain.R;
import com.aisino.grain.model.db.Warehouse;
import com.aisino.grain.model.rfid.RfidAdapter;

public class RegistVehicleWorkActivity extends BaseActivity {
//	private static String Tag = "RegistVehicleWorkActivity";
	
	private RfidAdapter mRfidAdapter = null;			//rfid读卡适配器
	private Context mContext = null;					
	
	private Button mRegistButton = null;				//登记按钮
	private Button mDeductionDiscountButton = null;		//扣量扣价按钮
	private Button mReadCardButton = null;
	private Spinner mSpinnerWarehouse = null;
	private Button mChangeWarehouseButton = null;		//调整仓库按钮
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();
	
	private List<Warehouse> mWarehouselist = null;		//查询结果
	private int mSelectedID = -1;						//仓库列表选中ID
	
	private TextView mLeagalTextView = null;			//合法性编辑框
	private TextView mVarietiesTextView = null;			//品种编辑框
	private TextView mLicensePlateTextView = null;		//车牌编辑框
	private TextView mOwnersTextView = null;			//货主编辑框
	private TextView mTypeTextView = null;				//类型编辑框t
	private TextView mLinkTextView= null;				//环节编辑框
	private TextView mGrossWeightTextView = null;		//毛重编辑框
	private TextView mTareTextView = null;				//皮重编辑框
	private TextView mNetWeigthTextView = null;			//净重编辑框
	private TextView mDeductionTextView = null;			//扣量编辑框
	private TextView mUploadingWarehousTextView = null;	//卸粮仓库编辑框
	
	//装卸是否完成
	private RadioGroup mLoadingStatusRadioGroup = null;			
	private RadioButton mLoadingFinishRadioButton = null;					//装卸完成
	private RadioButton mLoadingUnfinishNeedAssayRadioButton = null;		//装卸未完成，需重新化验
	private RadioButton mLoadingUnfinishChangeWareouseRadioButton = null;	//装卸未完成，需换仓
	
	private String mLeagal = null;						//合法性
	private String mVarieties = null;					//品种
	private String mLicensePlate = null;				//车牌
	private String mOwners = null;						//货主
	private String mType = null;						//类型
	private String mLink = null;						//环节
	private String mGrossWeight = null;					//毛重
	private String mTare = null;						//皮重
	private String mNetWeigth = null;					//净重
	private String mDeduction = null;					//扣量
	
	private boolean mReCheck = false;					//重新校验合法性标志
	
	private SharedPreferences sharedPreferences = null;			//保存选中的仓库
	private Editor editor = null;								//SharedPreferences编辑器
	
	private Random mRandom = null;
	public static String[] GrainKind = {"粳稻","籼稻","白麦","玉米","大豆","大米"};
	public static String[] Owner = {"江苏省粮食局","无锡粮食局","中储粮","苏州粮食局"};
	public static String[] GrainAttribute = {"商品粮","市储粮","省储粮","中央储备粮","代储","最低收购价"};
	public static String[] LicensePlate = {"京A-00001","京V-02009","京B-12345","京C-88888","京D-66666"};
	public static String[] Owners = {"Aisino","航天信息","张三","李四","王五","电子产品部"};
	public static String[] Type = {"收购入库","销售出库"};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		mContext = this;
		setContentView(R.layout.activity_regist_vehicle_work);
		super.onCreate(savedInstanceState);
		InitCtrl();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	初始化
	 *
	 */
	private void InitCtrl() {
		sharedPreferences = getSharedPreferences("WarehouseInfo_Demo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//获取编辑器
		
		mRfidAdapter = RfidAdapter.getInstance(mContext);
		
		mWarehouselist = new ArrayList<Warehouse>();
		
		mSpinnerWarehouse = (Spinner)findViewById(R.id.regist_vehicle_work_spinner_warehouse);
		mChangeWarehouseButton = (Button)findViewById(R.id.regist_vehicle_work_btn_change_warehouse);
		mChangeWarehouseButton.setVisibility(View.INVISIBLE);
		mReadCardButton = (Button)findViewById(R.id.regist_vehicle_work_btn_read_card);
		mRegistButton = (Button)findViewById(R.id.regist_vehicle_work_btn_regist);
		mRegistButton.setEnabled(false);
		mDeductionDiscountButton = (Button)findViewById(R.id.regist_vehicle_work_btn_deduction_discount);
		mDeductionDiscountButton.setEnabled(false);
		mUploadingWarehousTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_uploading_warehouse);
		
		mLoadingStatusRadioGroup = (RadioGroup) findViewById(R.id.regist_vehicle_work_rbtn_group_loading);
		mLoadingFinishRadioButton = (RadioButton) findViewById(R.id.regist_vehicle_work_rbtn_loading_finish);
		mLoadingUnfinishNeedAssayRadioButton = (RadioButton) findViewById(R.id.regist_vehicle_work_rbtn_loading_unfinish_need_assay);
		mLoadingUnfinishChangeWareouseRadioButton = (RadioButton) findViewById(R.id.regist_vehicle_work_rbtn_loading_unfinish_change_warehouse);
		mLoadingStatusRadioGroup.setOnCheckedChangeListener(new LoadingStatusOnCheckedChangeListener());
		
		mLeagalTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_legal);
		mVarietiesTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_varieties);
		mLicensePlateTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_license_plate);
		mOwnersTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_owners);
		mTypeTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_type);
		mLinkTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_link);
		mGrossWeightTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_gross_weight);
		mTareTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_tare);
		mNetWeigthTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_net_weight);
		mDeductionTextView = (TextView)findViewById(R.id.regist_vehicle_work_tv_deduction);
		
		mRegistButton.setOnClickListener(new RegistOnClickListener());
		mDeductionDiscountButton.setOnClickListener(new DeductionDiscountOnClickListener());
		mReadCardButton.setOnClickListener(new ReadCardOnClickListener());
		mChangeWarehouseButton.setOnClickListener(new ChangeWarehouseOnClickListener());
		
		InitSnipperWarehouse();
		//读配置信息，设置默认仓库选中项
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	初始化值仓仓库下拉列表
	 *
	 */
	private void InitSnipperWarehouse() {
		for (int i = 0;i < 5;i++) {
			Warehouse warehouse = new Warehouse();
			warehouse.setWarehouse_id(i);
			warehouse.setWarehouse_name(i+"号仓库");
			mWarehouselist.add(warehouse);
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
			mRegistButton.setEnabled(false);
			mDeductionDiscountButton.setEnabled(false);
			//读卡，获取界面各项数据
			String string = null;
	 		string = mRfidAdapter.HasCard();
			if (string == null) {
				mPopupWindowDialog.showMessage("读卡失败");
				return false;
			}
			//读卡成功给出提示
			//code
			mLeagal = "否";
			mLeagalTextView.setText(mLeagal);
			mLeagalTextView.setTextColor(Color.rgb(255, 0, 0));
			mDeductionDiscountButton.setEnabled(false);
			mRegistButton.setEnabled(false);
			mPopupWindowDialog.showMessage("粮仓库与当前值仓仓库不一致，请确认!");
			mChangeWarehouseButton.setVisibility(View.VISIBLE);
			mChangeWarehouseButton.setText("调整到"+mWarehouselist.get(mSelectedID).getWarehouse_name());
			mReCheck = true;
			
			//读卡获取相关信息
			mRandom = new Random();
			mVarieties = GrainKind[mRandom.nextInt(5)];
			mRandom = new Random();
			mLicensePlate = LicensePlate[mRandom.nextInt(4)];
			mRandom = new Random();
			mOwners = Owner[mRandom.nextInt(3)];
			mRandom = new Random();
			mType = Type[mRandom.nextInt(1)];
			mLink = "称完毛重";
			mRandom = new Random();
			mGrossWeight = String.valueOf(mRandom.nextInt(2000)+2000);
			mRandom = new Random();
			mTare = String.valueOf(mRandom.nextInt(2000));
			mNetWeigth = String.valueOf(Integer.parseInt(mGrossWeight)-Integer.parseInt(mTare));
			mDeduction = String.valueOf(mRandom.nextInt(500));
			
			//显示读卡最终信息
			mVarietiesTextView.setText(mVarieties);
			mLicensePlateTextView.setText(mLicensePlate);
			mOwnersTextView.setText(mOwners);
			mTypeTextView.setText(mType);
			mLinkTextView.setText(mLink);
			mGrossWeightTextView.setText(String.valueOf(mGrossWeight));
			mTareTextView.setText(String.valueOf(mTare));
			mNetWeigthTextView.setText(String.valueOf(mNetWeigth));
			mDeductionTextView.setText(String.valueOf(mDeduction));
			mUploadingWarehousTextView.setText("5号仓");
		}
		return super.onKeyDown(keyCode, event);
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
			mRegistButton.setEnabled(false);
			mDeductionDiscountButton.setEnabled(false);
			//读卡，获取界面各项数据
			String string = null;
	 		string = mRfidAdapter.HasCard();
			if (string == null) {
				mPopupWindowDialog.showMessage("读卡失败");
				return;
			}
			//读卡成功给出提示
			//code
			mLeagal = "否";
			mLeagalTextView.setText(mLeagal);
			mLeagalTextView.setTextColor(Color.rgb(255, 0, 0));
			mDeductionDiscountButton.setEnabled(false);
			mRegistButton.setEnabled(false);
			mPopupWindowDialog.showMessage("粮仓库与当前值仓仓库不一致，请确认!");
			mChangeWarehouseButton.setVisibility(View.VISIBLE);
			mChangeWarehouseButton.setText("调整到"+mWarehouselist.get(mSelectedID).getWarehouse_name());
			mReCheck = true;
			
			//读卡获取相关信息
			mRandom = new Random();
			mVarieties = GrainKind[mRandom.nextInt(5)];
			mRandom = new Random();
			mLicensePlate = LicensePlate[mRandom.nextInt(4)];
			mRandom = new Random();
			mOwners = Owner[mRandom.nextInt(3)];
			mRandom = new Random();
			mType = Type[mRandom.nextInt(1)];
			mLink = "称完毛重";
			mRandom = new Random();
			mGrossWeight = String.valueOf(mRandom.nextInt(2000)+2000);
			mRandom = new Random();
			mTare = String.valueOf(mRandom.nextInt(2000));
			mNetWeigth = String.valueOf(Integer.parseInt(mGrossWeight)-Integer.parseInt(mTare));
			mDeduction = String.valueOf(mRandom.nextInt(500));
			
			//显示读卡最终信息
			mVarietiesTextView.setText(mVarieties);
			mLicensePlateTextView.setText(mLicensePlate);
			mOwnersTextView.setText(mOwners);
			mTypeTextView.setText(mType);
			mLinkTextView.setText(mLink);
			mGrossWeightTextView.setText(String.valueOf(mGrossWeight));
			mTareTextView.setText(String.valueOf(mTare));
			mNetWeigthTextView.setText(String.valueOf(mNetWeigth));
			mDeductionTextView.setText(String.valueOf(mDeduction));
			mUploadingWarehousTextView.setText("5号仓");
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
			
			editor.putInt("SelectedID", mSelectedID);
			editor.commit();
			mChangeWarehouseButton.setVisibility(View.INVISIBLE);
			if (mReCheck == true) {
				if (!RFIDIsExist()) { //卡已取走
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
	 * @description 登记操作——快速收纳系统
	 *
	 */
	private void RegistFastStorageSystem() {
		//写卡，返回写卡结果
		//1.写如作业信息环节	
		//2.写入卸粮仓库编码
		if (!mLink.equals("2")&&!mLink.equals("3")) {
			mPopupWindowDialog.showMessage("请选择作业是否完成");
			return;
		}
		mPopupWindowDialog.showMessage("登记成功");
		ClearWindow();
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
			RegistFastStorageSystem();
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
				mLeagal = "是";
				mLeagalTextView.setText(mLeagal);
				mLeagalTextView.setTextColor(Color.rgb(255, 0, 0));
				mUploadingWarehousTextView.setText(mWarehouselist.get(mSelectedID).getWarehouse_name());
				mChangeWarehouseButton.setVisibility(View.INVISIBLE);
				mRegistButton.setEnabled(true);
				mDeductionDiscountButton.setEnabled(true);
			}
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
		mLinkTextView.setText("");
		mGrossWeightTextView.setText(String.valueOf(""));
		mTareTextView.setText(String.valueOf(""));
		mNetWeigthTextView.setText(String.valueOf(""));
		mDeductionTextView.setText(String.valueOf(""));
		mUploadingWarehousTextView.setText("");
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
	
	class LoadingStatusOnCheckedChangeListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == mLoadingFinishRadioButton.getId()) {	//装卸完成
				mLink = "2";
			}
			if (checkedId == mLoadingUnfinishNeedAssayRadioButton.getId()) {	//装卸未完成
				mLink = "3";
			}
			if (checkedId == mLoadingUnfinishChangeWareouseRadioButton.getId()) {	//装卸未完成
				mLink = "3";
			}
		}
	}
}
