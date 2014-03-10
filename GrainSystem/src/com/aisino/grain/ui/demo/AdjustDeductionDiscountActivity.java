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
	
	private Spinner mSpinnerWarehouse = null;								//ֵ�ֲֿ������б�
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();	//�ֿ������б�
	private Button mReadCardButton = null;									//������ť
	private Button mSaveDeductionDiscountButton = null;						//��������ۼ۰�ť
	private Button mRegistVehicleWorkButton = null;							//�Ǽǳ�����ҵ��ť
	private TextView mMoistureDeductAmountTextView = null;					//ˮ�ֿ���
	private TextView mMoistureDeductAmountUnit = null;						//ˮ�ֿ�����λ
	private TextView mImpurityDeductAmountTextView = null;					//���ʿ���
	private TextView mImpurityDeductAmountUnit = null;						//���ʿ�����λ
	private TextView mImpurityDeductPriceTextView = null;					//�����ӷ�
	private TextView mImpurityDeductPriceUnit = null;						//�����ӷѵ�λ
	private TextView mMoistureDeductPriceTextView = null;					//����ɹ��
	private TextView mMoistureDeductPriceUnit = null;						//����ɹ�ѵ�λ
	
	private EditText mMoistureDeductAmountAddEditText = null;				//ˮ�ֿ����ӿ�
	private EditText mImpurityDeductAmountAddEditText = null;				//���ʿ����ӿ�
	private EditText mImpurityDeductPriceAddEditText = null;				//�����ӷѼӿ�
	private EditText mMoistureDeductPriceAddEditText = null;				//����ɹ�Ѽӿ�
	
	private ListView mQualityIndexListView = null;							//����ָ��Listview
	private LinearLayout mLinearLayout = null;								//ListView Item ����

	private String mMoistureDeductAmountFirst = null;						//����ˮ�ֿ���
	private String mImpurityDeductAmountFirst = null;						//�������ʿ���
	private String mImpurityDeductPriceFirst = null;						//���ο����ӷ�
	private String mMoistureDeductPriceFirst = null;						//���ο���ɹ��
	private String mMoistureDeductAmountAdjust = null;						//ˮ�ֿ����ӿ�
	private String mImpurityDeductAmountAdjust = null;						//���ʿ����ӿ�
	private String mImpurityDeductPriceAdjust = null;						//�����ӷѼӿ�
	private String mMoistureDeductPriceAdjust = null;						//����ɹ�Ѽӿ�
	private int mMoistureAmountFlagFirst = -1;								//����ˮ�ֿ�����λ���
	private int mImpurityAmountFlagFirst = -1;								//�������ʿ�����λ���
	private int mImpurityPriceFlagFirst = -1;								//���ο����ӷѵ�λ���
	private int mMoisturePriceFlagFirst = -1;								//���ο���ɹ�ѵ�λ���
	
	private List<QualityIndexItemData> mQualityIndexItemDataList = null;			//QualityIndex����ListView����ʾ

	private List<Warehouse> mWarehouselist = null;							//��ѯ���
	
	private int mSelectedID = -1;											//ֵ�ֲֿ��б�ѡ��ID
	
	private SharedPreferences sharedPreferences = null;						//����ѡ�еĲֿ�
	private Editor editor = null;											//SharedPreferences�༭��
	
	private RfidAdapter mRfidAdapter = null;								//����������
	
//	private PopupWindowDialog mPopupWindowDialog = null;
	private Random mRandom = null;
	public static String[] IndexName = {"������","ˮ��","�������","��������","������","����","����"};
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
	 * @description	��ʼ��
	 *
	 */
	private void InitCtrl() {
		mRfidAdapter = RfidAdapter.getInstance(this);
		
		sharedPreferences = getSharedPreferences("WarehouseInfo_Demo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//��ȡ�༭��
		
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
		
		// �������뷨
		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		// ��ʾ�����������뷨
		imm.hideSoftInputFromWindow(mMoistureDeductAmountAddEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mImpurityDeductAmountAddEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mImpurityDeductPriceAddEditText.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(mMoistureDeductPriceAddEditText.getWindowToken(), 0);
		
		mQualityIndexListView = (ListView)findViewById(R.id.adjust_deduction_discount_lv);
		
		mWarehouselist = new ArrayList<Warehouse>();
		
		InitSnipperWarehouse();
		//��������Ϣ������Ĭ�ϲֿ�ѡ����
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
		
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	��ʼ��ֵ�ֲֿ������˵�
	 *
	 */
	private void InitSnipperWarehouse() {
		for (int i = 0;i < 5;i++) {
			Warehouse warehouse = new Warehouse();
			warehouse.setWarehouse_id(i);
			warehouse.setWarehouse_name(i+"�Ųֿ�");
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
        
        //���ñ���   
        mSpinnerWarehouse.setPrompt("��ѡ��ֵ�ֲֿ�"); 
        mSpinnerWarehouse.setOnItemSelectedListener(new SpinnerWarehouseOnItemSelectedListener());
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	ֵ�ֲֿ������˵�ѡ�м�����
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
				mPopupWindowDialog.showMessage("����ʧ��");
				return false;
			}
			//�����ɹ���ʾ
			//code
				
			//���ڻ�ȡ
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
			//��ʾ����
			DisplayFromCard();
			
			//��ʾ����ָ��
			InitListView();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	������ťʵ��
	 *
	 */
	class ReadCardOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String string = null;
			string = mRfidAdapter.HasCard();
			if (string == null) {
				mPopupWindowDialog.showMessage("����ʧ��");
				return;
			}
			//�����ɹ���ʾ
			//code
				
			//���ڻ�ȡ
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
			//��ʾ����
			DisplayFromCard();
			
			//��ʾ����ָ��
			InitListView();
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	��������ۼ۰�ťʵ��
	 *
	 */
	class SaveDeductionDiscountOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if ((mMoistureDeductAmountFirst == null) || (mImpurityDeductAmountFirst == null) || (mMoistureDeductPriceFirst == null) || (mImpurityDeductPriceFirst == null)) {
				mPopupWindowDialog.showMessage("���ȶ���");
				return;
			}
			
			//��ȡ���������
			GetEditTextData();
			
			//У�����������Ƿ�Ϸ�
			boolean res = CheckDataLeagal();
			
			if (res) {
				//�޸�����д��
				mMoistureDeductAmountFirst = String.valueOf(CalcuMoistureDeduction(mMoistureDeductAmountAdjust));
				mImpurityDeductAmountFirst = String.valueOf(CalcuImpurityDeduction(mImpurityDeductAmountAdjust));
				mImpurityDeductPriceFirst = String.valueOf(CalcuClearSundryFees(mImpurityDeductPriceAdjust));
				mMoistureDeductPriceFirst = String.valueOf(CalcuDeductDryingFees(mMoistureDeductPriceAdjust));
				
				DisplayFromCard();
				
				if (mRfidAdapter.HasCard() == null) {
					mPopupWindowDialog.showMessage("û�м�⵽��������ʧ��");
					return;
				}
				mPopupWindowDialog.showMessage("����ɹ�");
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	���Ӷ�����õ����ݽ�����ʾ,�������ȡ����Ϊ������Ҫ����100
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
	 * @description У���Ƿ���С����С��������Ϊ2λ
	 *
	 * @param ������С�����ַ�����ʽ����
	 * @return У��ɹ�:true;У��ʧ��:false
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
	 * @description	У���Ƿ�Ϊ����
	 *
	 * @param �������������ַ�����ʽ����
	 * @return У��ɹ�:true;У��ʧ��:false
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
	 * @description	��ȡ�������ݣ�ת�����ַ���
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
	 * @description	У���������ݵĺϷ���
	 *
	 * @return У��ɹ�:true��У��ʧ��:false
	 */
	private boolean CheckDataLeagal() {
		//У���������ݺϷ���
		if (mMoistureAmountFlagFirst == 0) {
			if (!IsIntger(mMoistureDeductAmountAdjust)) {
				mMoistureDeductAmountAddEditText.setError("����������");
				return false;
			}
		} else {
			if (!IsDecimal(mMoistureDeductAmountAdjust)) {
				mMoistureDeductAmountAddEditText.setError("�����ʽ����");
				return false;
			}
		}
		
		if (mImpurityAmountFlagFirst == 0) {
			if (!IsIntger(mImpurityDeductAmountAdjust)) {
				mImpurityDeductAmountAddEditText.setError("����������");
				return false;
			}
		} else {
			if (!IsDecimal(mImpurityDeductAmountAdjust)) {
				mImpurityDeductAmountAddEditText.setError("�����ʽ����");
				return false;
			}
		}
		if (mImpurityPriceFlagFirst == 0) {
			if (!IsIntger(mImpurityDeductPriceAdjust)) {
				mImpurityDeductPriceAddEditText.setError("����������");
				return false;
			}
		} else {
			if (!IsDecimal(mImpurityDeductPriceAdjust)) {
				mImpurityDeductPriceAddEditText.setError("�����ʽ����");
				return false;
			}
		}
		if (mMoisturePriceFlagFirst == 0) {
			if (!IsIntger(mMoistureDeductPriceAdjust)) {
				mMoistureDeductPriceAddEditText.setError("����������");
				return false;
			}
		} else {
			if (!IsDecimal(mMoistureDeductPriceAdjust)) {
				mMoistureDeductPriceAddEditText.setError("�����ʽ����");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description �������ˮ�ֿ������ֵ
	 *
	 * @param ˮ�ֶ��ο���ֵ(�ַ�����ʽ)
	 * @return ����ˮ�ֿ������ֵ(�ַ�����ʽ)
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
	 * @description ����������ʿ������ֵ
	 *
	 * @param ���ʶ��ο���ֵ(�ַ�����ʽ)
	 * @return �������ʿ������ֵ(�ַ�����ʽ)
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
	 * @description ������ο����ӷѿ������ֵ
	 *
	 * @param ���ӷѶ��ο���ֵ(�ַ�����ʽ)
	 * @return ���ο����ӷѿ������ֵ(�ַ�����ʽ)
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
	 * @description ������ο�ɹ���ѿ������ֵ
	 *
	 * @param ɹ���Ѷ��ο���ֵ(�ַ�����ʽ)
	 * @return ���ο�ɹ���ѿ������ֵ(�ַ�����ʽ)
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
	 * @description ��ʼ������ָ��listview
	 *
	 */
	private void InitListView(){
		//����ȡ�Ļ���ָ����ֵ�ͻ���ָ�����ƶ�Ӧ����
		BindData();
		MyBaseAdapter adapter = new MyBaseAdapter();
		mQualityIndexListView.setAdapter(adapter);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	����ָ��listview��Adapter
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
			
			QualityIndexItem qualityIndexItem = new QualityIndexItem();	 //������Item
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
	 * @description ����ָ��QualityIndex��Item��ͼ��
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
	 * @description ����ָ��QualityIndex��Item������
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
	 * @description	�����ݣ���ͨ��wevservice��õ����ݺ����ݿ��е����ݰ��Է�����ʾ
	 * 				webservice��������ָ��id��ָ��ֵ�����ݿ��д洢����ָ��id��ָ�����ƣ�������ʾָ�����ƺ�ָ��ֵ
	 *
	 */
	private void BindData() {
		mQualityIndexItemDataList = new ArrayList<QualityIndexItemData>();
		
		//��������
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
	 * @description	�Ǽǳ�����ҵ��ťʵ��
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
	 * @description	����������ָ���࣬�������TreeMapʹ��
	 *
	 */
	class Assay{
		String index;
		String value;
	}
}
