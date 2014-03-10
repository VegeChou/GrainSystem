package com.aisino.grain.ui.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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

import com.aisino.grain.R;
import com.aisino.grain.model.db.Warehouse;
import com.aisino.grain.model.rfid.RfidAdapter;

public class AdjustDeductionDiscountActivity extends BaseActivity {
	protected static final String TAG = "AdjustDeductionDiscountActivity";
	
	private Spinner mSpinnerWarehouse = null;								//值仓仓库下拉列表
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();	//仓库名称列表
	private Button mReadCardButton = null;									//读卡按钮
	private Button mSaveDeductionDiscountButton = null;						//保存扣量扣价按钮
	private Button mRegistVehicleWorkButton = null;							//登记车辆作业按钮
	private TextView mMoistureDeductAmountTextView = null;					//水分扣量
	private TextView mMoistureDeductAmountUnit = null;						//水分扣量单位
	private TextView mImpurityDeductAmountTextView = null;					//杂质扣量
	private TextView mImpurityDeductAmountUnit = null;						//杂质扣量单位
	private TextView mImpurityDeductPriceTextView = null;					//扣清杂费
	private TextView mImpurityDeductPriceUnit = null;						//扣清杂费单位
	private TextView mMoistureDeductPriceTextView = null;					//扣整晒费
	private TextView mMoistureDeductPriceUnit = null;						//扣整晒费单位
	
	private EditText mMoistureDeductAmountAddEditText = null;				//水分扣量加扣
	private EditText mImpurityDeductAmountAddEditText = null;				//杂质扣量加扣
	private EditText mImpurityDeductPriceAddEditText = null;				//扣清杂费加扣
	private EditText mMoistureDeductPriceAddEditText = null;				//扣整晒费加扣
	
	private ListView mQualityIndexListView = null;							//质量指标Listview
	private LinearLayout mLinearLayout = null;								//ListView Item 背景

	private String mMoistureDeductAmountFirst = null;						//初次水分扣量
	private String mImpurityDeductAmountFirst = null;						//初次杂质扣量
	private String mImpurityDeductPriceFirst = null;						//初次扣清杂费
	private String mMoistureDeductPriceFirst = null;						//初次扣整晒费
	private String mMoistureDeductAmountAdjust = null;						//水分扣量加扣
	private String mImpurityDeductAmountAdjust = null;						//杂质扣量加扣
	private String mImpurityDeductPriceAdjust = null;						//扣清杂费加扣
	private String mMoistureDeductPriceAdjust = null;						//扣整晒费加扣
	private int mMoistureAmountFlagFirst = -1;								//初次水分扣量单位标记
	private int mImpurityAmountFlagFirst = -1;								//初次杂质扣量单位标记
	private int mImpurityPriceFlagFirst = -1;								//初次扣清杂费单位标记
	private int mMoisturePriceFlagFirst = -1;								//初次扣整晒费单位标记
	
	private List<QualityIndexItemData> mQualityIndexItemDataList = null;			//QualityIndex数据ListView中显示

	private List<Warehouse> mWarehouselist = null;							//查询结果
	
	private int mSelectedID = -1;											//值仓仓库列表选中ID
	
	private SharedPreferences sharedPreferences = null;						//保存选中的仓库
	private Editor editor = null;											//SharedPreferences编辑器
	
	private RfidAdapter mRfidAdapter = null;								//读卡适配器
	
//	private PopupWindowDialog mPopupWindowDialog = null;
	private Random mRandom = null;
	public static String[] IndexName = {"出米率","水分","谷外糙米","整精米率","出糙率","黄粒","杂质"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
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
		mRfidAdapter = RfidAdapter.getInstance(this);
		
		sharedPreferences = getSharedPreferences("WarehouseInfo_Demo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//获取编辑器
		
		mSpinnerWarehouse = (Spinner)findViewById(R.id.adjust_deduction_discount_spinner_warehouse);
		mReadCardButton = (Button)findViewById(R.id.adjust_deduction_discount_btn_read_card);
		mReadCardButton.setOnClickListener(new ReadCardOnClickListener());
		mSaveDeductionDiscountButton = (Button)findViewById(R.id.adjust_deduction_discount_btn_save_deduction_discount);
		mSaveDeductionDiscountButton.setOnClickListener(new SaveDeductionDiscountOnClickListener());
		mRegistVehicleWorkButton = (Button)findViewById(R.id.adjust_deduction_discount_btn_regist_vehicle_work);
		mRegistVehicleWorkButton.setOnClickListener(new RegistVehicleWorkOnClickListener());
		
		mMoistureDeductAmountTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_moisture_deduct_amount);
		mImpurityDeductAmountTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_impurity_deduct_amount);
		mImpurityDeductPriceTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_clear_sundry_fees);
		mMoistureDeductPriceTextView = (TextView)findViewById(R.id.adjust_deduction_discount_tv_deduct_drying_fees);
		
		mMoistureDeductAmountUnit = (TextView)findViewById(R.id.adjust_deduction_discount_tv_kilogram_percent_one);
		mImpurityDeductAmountUnit = (TextView)findViewById(R.id.adjust_deduction_discount_tv_kilogram_percent_two);
		mImpurityDeductPriceUnit = (TextView)findViewById(R.id.adjust_deduction_discount_tv_yuan_percent_one);
		mMoistureDeductPriceUnit = (TextView)findViewById(R.id.adjust_deduction_discount_tv_yuan_percent_two);
		
		mMoistureDeductAmountAddEditText = (EditText)findViewById(R.id.adjust_deduction_discount_et_moisture_deduct_amount_add);
		mImpurityDeductAmountAddEditText = (EditText)findViewById(R.id.adjust_deduction_discount_et_impurity_deduct_amount_add);
		mImpurityDeductPriceAddEditText = (EditText)findViewById(R.id.adjust_deduction_discount_et_clear_sundry_fees_add);
		mMoistureDeductPriceAddEditText = (EditText)findViewById(R.id.adjust_deduction_discount_et_deduct_drying_fees_add);
		
		// 隐藏输入法
		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		imm.hideSoftInputFromWindow(mMoistureDeductAmountAddEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mImpurityDeductAmountAddEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mImpurityDeductPriceAddEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mMoistureDeductPriceAddEditText.getWindowToken(), 0);
		
		mQualityIndexListView = (ListView)findViewById(R.id.adjust_deduction_discount_lv);
		
		mWarehouselist = new ArrayList<Warehouse>();
		
		InitSnipperWarehouse();
		//读配置信息，设置默认仓库选中项
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
		
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	初始化值仓仓库下拉菜单
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
		super.onResume();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SCAN) {
			String string = null;
			string = mRfidAdapter.HasCard();
			if (string == null) {
				mPopupWindowDialog.showMessage("读卡失败");
				return false;
			}
			//读卡成功提示
			//code
				
			//卡内获取
			mRandom = new Random();
			mMoistureDeductAmountFirst = String.valueOf(mRandom.nextInt(4999)+1);
			mRandom = new Random();
			mImpurityDeductAmountFirst = String.valueOf(mRandom.nextInt(4999)+1);
			mRandom = new Random();
			mImpurityDeductPriceFirst = String.valueOf(mRandom.nextInt(4999)+1);
			mRandom = new Random();
			mMoistureDeductPriceFirst = String.valueOf(mRandom.nextInt(4999)+1);
			
			mRandom = new Random();
			mMoistureAmountFlagFirst = mRandom.nextInt(2);
			mImpurityAmountFlagFirst = mMoistureAmountFlagFirst;
			mRandom = new Random();
			mImpurityPriceFlagFirst = mRandom.nextInt(2);
			mMoisturePriceFlagFirst = mImpurityPriceFlagFirst;
			//显示数据
			DisplayFromCard();
			
			//显示化验指标
			InitListView();
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
			String string = null;
			string = mRfidAdapter.HasCard();
			if (string == null) {
				mPopupWindowDialog.showMessage("读卡失败");
				return;
			}
			//读卡成功提示
			//code
				
			//卡内获取
			mRandom = new Random();
			mMoistureDeductAmountFirst = String.valueOf(mRandom.nextInt(4999)+1);
			mRandom = new Random();
			mImpurityDeductAmountFirst = String.valueOf(mRandom.nextInt(4999)+1);
			mRandom = new Random();
			mImpurityDeductPriceFirst = String.valueOf(mRandom.nextInt(4999)+1);
			mRandom = new Random();
			mMoistureDeductPriceFirst = String.valueOf(mRandom.nextInt(4999)+1);
			
			mRandom = new Random();
			mMoistureAmountFlagFirst = mRandom.nextInt(2);
			mImpurityAmountFlagFirst = mMoistureAmountFlagFirst;
			mRandom = new Random();
			mImpurityPriceFlagFirst = mRandom.nextInt(2);
			mMoisturePriceFlagFirst = mImpurityPriceFlagFirst;
			//显示数据
			DisplayFromCard();
			
			//显示化验指标
			InitListView();
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
			if ((mMoistureDeductAmountFirst == null) || (mImpurityDeductAmountFirst == null) || (mMoistureDeductPriceFirst == null) || (mImpurityDeductPriceFirst == null)) {
				mPopupWindowDialog.showMessage("请先读卡");
				return;
			}
			
			//获取输入框数据
			GetEditTextData();
			
			//校验输入数据是否合法
			boolean res = CheckDataLeagal();
			
			if (res) {
				//修改数据写卡
				mMoistureDeductAmountFirst = String.valueOf(CalcuMoistureDeduction(mMoistureDeductAmountAdjust));
				mImpurityDeductAmountFirst = String.valueOf(CalcuImpurityDeduction(mImpurityDeductAmountAdjust));
				mImpurityDeductPriceFirst = String.valueOf(CalcuClearSundryFees(mImpurityDeductPriceAdjust));
				mMoistureDeductPriceFirst = String.valueOf(CalcuDeductDryingFees(mMoistureDeductPriceAdjust));
				
				DisplayFromCard();
				
				if (mRfidAdapter.HasCard() == null) {
					mPopupWindowDialog.showMessage("没有检测到卡，保存失败");
					return;
				}
				mPopupWindowDialog.showMessage("保存成功");
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	将从读卡获得的数据进行显示,与网络获取区别为数据需要除以100
	 *
	 */
	private void DisplayFromCard() {
		if (mMoistureAmountFlagFirst == 0) {
			mMoistureDeductAmountTextView.setText(mMoistureDeductAmountFirst);
			mMoistureDeductAmountUnit.setText(getString(R.string.kilogram));
			mMoistureDeductAmountAddEditText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
		} else {
			String string = String.valueOf(Float.parseFloat(mMoistureDeductAmountFirst)/100);
			mMoistureDeductAmountTextView.setText(string);
			mMoistureDeductAmountUnit.setText(getString(R.string.percent));
			mMoistureDeductAmountAddEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		
		if (mImpurityAmountFlagFirst == 0) {
			mImpurityDeductAmountTextView.setText(mImpurityDeductAmountFirst);
			mImpurityDeductAmountUnit.setText(getString(R.string.kilogram));
			mImpurityDeductAmountAddEditText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
		} else {
			mImpurityDeductAmountTextView.setText(String.valueOf(Float.parseFloat(mImpurityDeductAmountFirst)/100));
			mImpurityDeductAmountUnit.setText(getString(R.string.percent));
			mImpurityDeductAmountAddEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		
		if (mImpurityPriceFlagFirst == 0) {
			mImpurityDeductPriceTextView.setText(mImpurityDeductPriceFirst);
			mImpurityDeductPriceUnit.setText(getString(R.string.yuan));
			mImpurityDeductPriceAddEditText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
		} else {
			mImpurityDeductPriceTextView.setText(String.valueOf(Float.parseFloat(mImpurityDeductPriceFirst)/100));
			mImpurityDeductPriceUnit.setText(getString(R.string.percent));
			mImpurityDeductPriceAddEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		
		if (mMoisturePriceFlagFirst == 0) {
			mMoistureDeductPriceTextView.setText(mMoistureDeductPriceFirst);
			mMoistureDeductPriceUnit.setText(getString(R.string.yuan));
			mMoistureDeductPriceAddEditText.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED);
		} else {
			mMoistureDeductPriceTextView.setText(String.valueOf(Float.parseFloat(mMoistureDeductPriceFirst)/100));
			mMoistureDeductPriceUnit.setText(getString(R.string.percent));
			mMoistureDeductPriceAddEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		mMoistureDeductAmountAddEditText.setText("0");
		mImpurityDeductAmountAddEditText.setText("0");
		mImpurityDeductPriceAddEditText.setText("0");
		mMoistureDeductPriceAddEditText.setText("0");
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
		String test = "^[-|+]?\\d*([.]\\d{0,2})?$"; 
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
		String test = "^-?[0-9]\\d*$"; 
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
		if (mMoistureDeductAmountAddEditText.getText().toString().equals("")) {
			mMoistureDeductAmountAdjust = "0";
		} else {
			mMoistureDeductAmountAdjust = mMoistureDeductAmountAddEditText.getText().toString();
		}
		if (mImpurityDeductAmountAddEditText.getText().toString().equals("")) {
			mImpurityDeductAmountAdjust = "0";
		} else {
			mImpurityDeductAmountAdjust = mImpurityDeductAmountAddEditText.getText().toString();
		}
		if (mImpurityDeductPriceAddEditText.getText().toString().equals("")) {
			mImpurityDeductPriceAdjust = "0";
		} else {
			mImpurityDeductPriceAdjust = mImpurityDeductPriceAddEditText.getText().toString();
		}
		if (mMoistureDeductPriceAddEditText.getText().toString().equals("")) {
			mMoistureDeductPriceAdjust = "0";
		} else {
			mMoistureDeductPriceAdjust = mMoistureDeductPriceAddEditText.getText().toString();
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
		if (mMoistureAmountFlagFirst == 0) {
			if (!IsIntger(mMoistureDeductAmountAdjust)) {
				mMoistureDeductAmountAddEditText.setError("请输入整数");
				return false;
			}
		} else {
			if (!IsDecimal(mMoistureDeductAmountAdjust)) {
				mMoistureDeductAmountAddEditText.setError("输入格式有误");
				return false;
			}
		}
		
		if (mImpurityAmountFlagFirst == 0) {
			if (!IsIntger(mImpurityDeductAmountAdjust)) {
				mImpurityDeductAmountAddEditText.setError("请输入整数");
				return false;
			}
		} else {
			if (!IsDecimal(mImpurityDeductAmountAdjust)) {
				mImpurityDeductAmountAddEditText.setError("输入格式有误");
				return false;
			}
		}
		if (mImpurityPriceFlagFirst == 0) {
			if (!IsIntger(mImpurityDeductPriceAdjust)) {
				mImpurityDeductPriceAddEditText.setError("请输入整数");
				return false;
			}
		} else {
			if (!IsDecimal(mImpurityDeductPriceAdjust)) {
				mImpurityDeductPriceAddEditText.setError("输入格式有误");
				return false;
			}
		}
		if (mMoisturePriceFlagFirst == 0) {
			if (!IsIntger(mMoistureDeductPriceAdjust)) {
				mMoistureDeductPriceAddEditText.setError("请输入整数");
				return false;
			}
		} else {
			if (!IsDecimal(mMoistureDeductPriceAdjust)) {
				mMoistureDeductPriceAddEditText.setError("输入格式有误");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description 计算二次水分扣量后的值
	 *
	 * @param 水分二次扣量值(字符串形式)
	 * @return 二次水分扣量后的值(字符串形式)
	 */
	private String CalcuMoistureDeduction(String string) {
		String total = null;
		if (mMoistureDeductAmountAdjust.equals("")) {
			total = String.valueOf(Integer.valueOf(mMoistureDeductAmountFirst));
			return total;
		}
		if (mMoistureAmountFlagFirst == 0) {
			total = String.valueOf(Integer.valueOf(mMoistureDeductAmountFirst)+Integer.valueOf(mMoistureDeductAmountAdjust));
		} else {
			total = String.valueOf(Integer.valueOf(mMoistureDeductAmountFirst)+(int)(float)(Float.valueOf(mMoistureDeductAmountAdjust)*100));
		}
		return total;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description 计算二次杂质扣量后的值
	 *
	 * @param 杂质二次扣量值(字符串形式)
	 * @return 二次杂质扣量后的值(字符串形式)
	 */
	private String CalcuImpurityDeduction(String string) {
		String total = null;
		if (mImpurityDeductAmountAdjust.equals("")) {
			total = String.valueOf(Integer.valueOf(mImpurityDeductAmountFirst));
			return total;
		}
		if (mImpurityAmountFlagFirst == 0) {
			total = String.valueOf(Integer.valueOf(mImpurityDeductAmountFirst)+Integer.valueOf(mImpurityDeductAmountAdjust));
		} else {
			total = String.valueOf(Integer.valueOf(mImpurityDeductAmountFirst)+(int)(float)(Float.valueOf(mImpurityDeductAmountAdjust)*100));
		}
		return total;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description 计算二次扣清杂费扣量后的值
	 *
	 * @param 清杂费二次扣量值(字符串形式)
	 * @return 二次扣清杂费扣量后的值(字符串形式)
	 */
	private String CalcuClearSundryFees(String string) {
		String total = null;
		if (mImpurityDeductPriceAdjust.equals("")) {
			total = String.valueOf(Integer.valueOf(mImpurityDeductPriceFirst));
			return total;
		}
		if (mImpurityPriceFlagFirst == 0) {
			total = String.valueOf(Integer.valueOf(mImpurityDeductPriceFirst)+Integer.valueOf(mImpurityDeductPriceAdjust));
		} else {
			total = String.valueOf(Integer.valueOf(mImpurityDeductPriceFirst)+(int)(float)(Float.valueOf(mImpurityDeductPriceAdjust)*100));
		}
		return total;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description 计算二次扣晒整费扣量后的值
	 *
	 * @param 晒整费二次扣量值(字符串形式)
	 * @return 二次扣晒整费扣量后的值(字符串形式)
	 */
	private String CalcuDeductDryingFees(String string) {
		String total = null;
		if (mMoistureDeductPriceAdjust.equals("")) {
			total = String.valueOf(Integer.valueOf(mMoistureDeductPriceFirst));
			return total;
		}
		if (mMoisturePriceFlagFirst == 0) {
			total = String.valueOf(Integer.valueOf(mMoistureDeductPriceFirst)+Integer.valueOf(mMoistureDeductPriceAdjust));
		} else {
			total = String.valueOf(Integer.valueOf(mMoistureDeductPriceFirst)+(int)(float)(Float.valueOf(mMoistureDeductPriceAdjust)*100));
		}
		return total;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description 初始化质量指标listview
	 *
	 */
	private void InitListView(){
		//将获取的化验指标数值和化验指标名称对应起来
		BindData();
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
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	绑定数据，将通过wevservice获得的数据和数据库中的数据绑定以方便显示
	 * 				webservice返回质量指标id和指标值，数据库中存储质量指标id和指标名称，数据显示指标名称和指标值
	 *
	 */
	private void BindData() {
		mQualityIndexItemDataList = new ArrayList<QualityIndexItemData>();
		
		//测试数据
		for (int i = 0; i < IndexName.length; i++) {
			QualityIndexItemData qualityIndexItemData = new QualityIndexItemData();
			qualityIndexItemData.IndexName = IndexName[i];
			mRandom = new Random();
			qualityIndexItemData.IndexValue = mRandom.nextInt(49)+1+"%";
			mQualityIndexItemDataList.add(qualityIndexItemData);
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
}
