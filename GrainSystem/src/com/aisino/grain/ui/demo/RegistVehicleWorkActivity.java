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
	
	private RfidAdapter mRfidAdapter = null;			//rfid����������
	private Context mContext = null;					
	
	private Button mRegistButton = null;				//�Ǽǰ�ť
	private Button mDeductionDiscountButton = null;		//�����ۼ۰�ť
	private Button mReadCardButton = null;
	private Spinner mSpinnerWarehouse = null;
	private Button mChangeWarehouseButton = null;		//�����ֿⰴť
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();
	
	private List<Warehouse> mWarehouselist = null;		//��ѯ���
	private int mSelectedID = -1;						//�ֿ��б�ѡ��ID
	
	private TextView mLeagalTextView = null;			//�Ϸ��Ա༭��
	private TextView mVarietiesTextView = null;			//Ʒ�ֱ༭��
	private TextView mLicensePlateTextView = null;		//���Ʊ༭��
	private TextView mOwnersTextView = null;			//�����༭��
	private TextView mTypeTextView = null;				//���ͱ༭��t
	private TextView mLinkTextView= null;				//���ڱ༭��
	private TextView mGrossWeightTextView = null;		//ë�ر༭��
	private TextView mTareTextView = null;				//Ƥ�ر༭��
	private TextView mNetWeigthTextView = null;			//���ر༭��
	private TextView mDeductionTextView = null;			//�����༭��
	private TextView mUploadingWarehousTextView = null;	//ж���ֿ�༭��
	
	//װж�Ƿ����
	private RadioGroup mLoadingStatusRadioGroup = null;			
	private RadioButton mLoadingFinishRadioButton = null;					//װж���
	private RadioButton mLoadingUnfinishNeedAssayRadioButton = null;		//װжδ��ɣ������»���
	private RadioButton mLoadingUnfinishChangeWareouseRadioButton = null;	//װжδ��ɣ��軻��
	
	private String mLeagal = null;						//�Ϸ���
	private String mVarieties = null;					//Ʒ��
	private String mLicensePlate = null;				//����
	private String mOwners = null;						//����
	private String mType = null;						//����
	private String mLink = null;						//����
	private String mGrossWeight = null;					//ë��
	private String mTare = null;						//Ƥ��
	private String mNetWeigth = null;					//����
	private String mDeduction = null;					//����
	
	private boolean mReCheck = false;					//����У��Ϸ��Ա�־
	
	private SharedPreferences sharedPreferences = null;			//����ѡ�еĲֿ�
	private Editor editor = null;								//SharedPreferences�༭��
	
	private Random mRandom = null;
	public static String[] GrainKind = {"����","�̵�","����","����","��","����"};
	public static String[] Owner = {"����ʡ��ʳ��","������ʳ��","�д���","������ʳ��"};
	public static String[] GrainAttribute = {"��Ʒ��","�д���","ʡ����","���봢����","����","����չ���"};
	public static String[] LicensePlate = {"��A-00001","��V-02009","��B-12345","��C-88888","��D-66666"};
	public static String[] Owners = {"Aisino","������Ϣ","����","����","����","���Ӳ�Ʒ��"};
	public static String[] Type = {"�չ����","���۳���"};
	
	
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
	 * @description	��ʼ��
	 *
	 */
	private void InitCtrl() {
		sharedPreferences = getSharedPreferences("WarehouseInfo_Demo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//��ȡ�༭��
		
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
		//��������Ϣ������Ĭ�ϲֿ�ѡ����
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	��ʼ��ֵ�ֲֿ������б�
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
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SCAN) {
			mRegistButton.setEnabled(false);
			mDeductionDiscountButton.setEnabled(false);
			//��������ȡ�����������
			String string = null;
	 		string = mRfidAdapter.HasCard();
			if (string == null) {
				mPopupWindowDialog.showMessage("����ʧ��");
				return false;
			}
			//�����ɹ�������ʾ
			//code
			mLeagal = "��";
			mLeagalTextView.setText(mLeagal);
			mLeagalTextView.setTextColor(Color.rgb(255, 0, 0));
			mDeductionDiscountButton.setEnabled(false);
			mRegistButton.setEnabled(false);
			mPopupWindowDialog.showMessage("���ֿ��뵱ǰֵ�ֲֿⲻһ�£���ȷ��!");
			mChangeWarehouseButton.setVisibility(View.VISIBLE);
			mChangeWarehouseButton.setText("������"+mWarehouselist.get(mSelectedID).getWarehouse_name());
			mReCheck = true;
			
			//������ȡ�����Ϣ
			mRandom = new Random();
			mVarieties = GrainKind[mRandom.nextInt(5)];
			mRandom = new Random();
			mLicensePlate = LicensePlate[mRandom.nextInt(4)];
			mRandom = new Random();
			mOwners = Owner[mRandom.nextInt(3)];
			mRandom = new Random();
			mType = Type[mRandom.nextInt(1)];
			mLink = "����ë��";
			mRandom = new Random();
			mGrossWeight = String.valueOf(mRandom.nextInt(2000)+2000);
			mRandom = new Random();
			mTare = String.valueOf(mRandom.nextInt(2000));
			mNetWeigth = String.valueOf(Integer.parseInt(mGrossWeight)-Integer.parseInt(mTare));
			mDeduction = String.valueOf(mRandom.nextInt(500));
			
			//��ʾ����������Ϣ
			mVarietiesTextView.setText(mVarieties);
			mLicensePlateTextView.setText(mLicensePlate);
			mOwnersTextView.setText(mOwners);
			mTypeTextView.setText(mType);
			mLinkTextView.setText(mLink);
			mGrossWeightTextView.setText(String.valueOf(mGrossWeight));
			mTareTextView.setText(String.valueOf(mTare));
			mNetWeigthTextView.setText(String.valueOf(mNetWeigth));
			mDeductionTextView.setText(String.valueOf(mDeduction));
			mUploadingWarehousTextView.setText("5�Ų�");
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	������ť��Ӧʵ��
	 *
	 */
	class ReadCardOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mRegistButton.setEnabled(false);
			mDeductionDiscountButton.setEnabled(false);
			//��������ȡ�����������
			String string = null;
	 		string = mRfidAdapter.HasCard();
			if (string == null) {
				mPopupWindowDialog.showMessage("����ʧ��");
				return;
			}
			//�����ɹ�������ʾ
			//code
			mLeagal = "��";
			mLeagalTextView.setText(mLeagal);
			mLeagalTextView.setTextColor(Color.rgb(255, 0, 0));
			mDeductionDiscountButton.setEnabled(false);
			mRegistButton.setEnabled(false);
			mPopupWindowDialog.showMessage("���ֿ��뵱ǰֵ�ֲֿⲻһ�£���ȷ��!");
			mChangeWarehouseButton.setVisibility(View.VISIBLE);
			mChangeWarehouseButton.setText("������"+mWarehouselist.get(mSelectedID).getWarehouse_name());
			mReCheck = true;
			
			//������ȡ�����Ϣ
			mRandom = new Random();
			mVarieties = GrainKind[mRandom.nextInt(5)];
			mRandom = new Random();
			mLicensePlate = LicensePlate[mRandom.nextInt(4)];
			mRandom = new Random();
			mOwners = Owner[mRandom.nextInt(3)];
			mRandom = new Random();
			mType = Type[mRandom.nextInt(1)];
			mLink = "����ë��";
			mRandom = new Random();
			mGrossWeight = String.valueOf(mRandom.nextInt(2000)+2000);
			mRandom = new Random();
			mTare = String.valueOf(mRandom.nextInt(2000));
			mNetWeigth = String.valueOf(Integer.parseInt(mGrossWeight)-Integer.parseInt(mTare));
			mDeduction = String.valueOf(mRandom.nextInt(500));
			
			//��ʾ����������Ϣ
			mVarietiesTextView.setText(mVarieties);
			mLicensePlateTextView.setText(mLicensePlate);
			mOwnersTextView.setText(mOwners);
			mTypeTextView.setText(mType);
			mLinkTextView.setText(mLink);
			mGrossWeightTextView.setText(String.valueOf(mGrossWeight));
			mTareTextView.setText(String.valueOf(mTare));
			mNetWeigthTextView.setText(String.valueOf(mNetWeigth));
			mDeductionTextView.setText(String.valueOf(mDeduction));
			mUploadingWarehousTextView.setText("5�Ų�");
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description	ֵ�ֲֿ������˵�ѡ�м���
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
				if (!RFIDIsExist()) { //����ȡ��
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
	 * @description �Ǽǲ���������������ϵͳ
	 *
	 */
	private void RegistFastStorageSystem() {
		//д��������д�����
		//1.д����ҵ��Ϣ����	
		//2.д��ж���ֿ����
		if (!mLink.equals("2")&&!mLink.equals("3")) {
			mPopupWindowDialog.showMessage("��ѡ����ҵ�Ƿ����");
			return;
		}
		mPopupWindowDialog.showMessage("�Ǽǳɹ�");
		ClearWindow();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description �Ǽǰ�ťʵ��
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
	 * @description �����ۼ۰�ťʵ��
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
	 * @description �޸İ�ťʵ�֣��޸Ĳֿ⵽X�ֿ�
	 *
	 */
	class ChangeWarehouseOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//���Ƿ�����
			if (!RFIDIsExist()) {
				//��ս���
				ClearWindow();
			} else {
				mLeagal = "��";
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
	 * @description �Ǽǳɹ������������Ϣ���Ա������һ�εǼǲ���
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
	 * @description �ж�RFID���Ƿ��ڶ�����
	 *
	 */
	private boolean RFIDIsExist() {
		//����RFID�ӿ�HasCard
		if (mRfidAdapter.HasCard() == null) {
			return false;
		}
		return true;
	}
	
	class LoadingStatusOnCheckedChangeListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == mLoadingFinishRadioButton.getId()) {	//װж���
				mLink = "2";
			}
			if (checkedId == mLoadingUnfinishNeedAssayRadioButton.getId()) {	//װжδ���
				mLink = "3";
			}
			if (checkedId == mLoadingUnfinishChangeWareouseRadioButton.getId()) {	//װжδ���
				mLink = "3";
			}
		}
	}
}
