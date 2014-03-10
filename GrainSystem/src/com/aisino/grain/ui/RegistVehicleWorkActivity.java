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
	
	private RfidAdapter mRfidAdapter = null;			//rfid����������
	private Context mContext = null;					
	
	//װж�Ƿ����
	private RadioGroup mLoadingStatusRadioGroup = null;			
	private RadioButton mLoadingFinishRadioButton = null;	//װж���
//	private RadioButton mLoadingUnfinishNeedAssayRadioButton = null;	//װжδ��ɣ������»���
//	private RadioButton mLoadingUnfinishChangeWarehouseRadioButton = null;	//װжδ��ɣ��軻��
	
	private Button mRegistButton = null;				//�Ǽǰ�ť
	private Button mDeductionDiscountButton = null;		//�����ۼ۰�ť
	private Button mReadCardButton = null;
	private Spinner mSpinnerWarehouse = null;
	private Button mChangeWarehouseButton = null;		//�����ֿⰴť
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();
	
	private Dao<Warehouse, Integer> mWarehouseDao =  null;		//�ֿ���ϢDao
	private List<Warehouse> mWarehouselist = null;		//��ѯ���
	private int mSelectedID = -1;						//�ֿ��б�ѡ��ID
	
	private Dao<GrainTypeDB, Integer> mGrainTypeDBDao = null;
	
	private TextView mLeagalTextView = null;			//�Ϸ��Ա༭��
	private TextView mVarietiesTextView = null;			//Ʒ�ֱ༭��
	private TextView mLicensePlateTextView = null;		//���Ʊ༭��
	private TextView mOwnersTextView = null;			//�����༭��
	private TextView mTypeTextView = null;				//���ͱ༭��
	private TextView mWorkNodeTextView= null;			//���ڱ༭��
	private TextView mGrossWeightTextView = null;		//ë�ر༭��
	private TextView mTareTextView = null;				//Ƥ�ر༭��
	private TextView mNetWeigthTextView = null;			//���ر༭��
	private TextView mDeductionTextView = null;			//�����༭��
	private TextView mUploadingWarehousTextView = null;	//ж���ֿ�༭��
	
	
//	private double mMoistureDeductAmountFirst = 0;						//����ˮ�ֿ���
//	private double mImpurityDeductAmountFirst = 0;						//�������ʿ���
//	private double mMoistureDeductAmountAdjust = 0;						//ˮ�ֿ����ӿ�
//	private double mImpurityDeductAmountAdjust = 0;						//���ʿ����ӿ�
//	private int mMoistureAmountFlagFirst = -1;							//����ˮ�ֿ�����λ���
//	private int mImpurityAmountFlagFirst = -1;							//�������ʿ�����λ���
//	private int mMoistureAmountFlagAdjust = -1;							//ˮ�ֿ�����λ�ӿ۱��
//	private int mImpurityAmountFlagAdjust = -1;							//���ʿ�����λ�ӿ۱��
//	private int mAdjustDeductFlag = -1;									//���ο������
	
	private String mGrainTypeName = null;
	private String mBusinessTypeName = null;
	
	//ҵ����Ϣ
	//����
	private String mCardID = null;
//	//�����ۼ���Ϣ
	private DeductedInfo mAssayDeductedInfo = null;		//��������ۼ�
//	private DeductedInfo mAdjustDeductedInfo = null;	//ֵ�ֿ����ۼ�
	//������Ϣ
	private EnrollInfoGeneral mEnrollInfo = null;
	//������Ϣ
	private WeighInfo mWeighInfo = null;
	
	private String mLeagal = null;						//�Ϸ���
	private int mWarehouseID = -1;						//ֵ�ֲֿ�ID
	private boolean mReCheck = false;					//����У��Ϸ��Ա�־
	
	private RestWebServiceAdapter mRestWebServiceAdapter = null;				//REST
	CheckVehicleWorkResponse mCheckVehicleWorkResponse = null;			//����CheckVehicleWorkMessage���
	RegisterVehicleWorkResponse mRegisterVehicleWorkResponse = null;		//����RegisterVehicleWorkMessageBean���
	
	private SharedPreferences sharedPreferences = null;			//����ѡ�еĲֿ�
	private Editor editor = null;								//SharedPreferences�༭��
	
	private SharedPreferences mLoginSharedPreferences = null;	//�����¼��Ϣ
	
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
	 * @description	��ʼ��
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
		editor = sharedPreferences.edit();//��ȡ�༭��
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
	 * @description	��ʼ��ֵ�ֲֿ������б�
	 *
	 */
	private void InitSnipperWarehouse() {
		//�����ݿ��ȡ�������ֺ�,���ݵ�¼��Ϣ����ʾֵ�ֲֿ��б�
		
		//��ȡ��¼�û���ֵ��id��
		String warehouse_id = null;
		warehouse_id = mLoginSharedPreferences.getString("warehouse_id", "null");
		if (warehouse_id.equals("null")) {
//			mPopupWindowDialog.showMessage("��ȡֵ�ֲֿ�IDʧ��");
			return;
		}
		//��ѯ��ֵ��id����Ӧ�Ĳֿ�����
		try {
			GenericRawResults<Object[]> rawResults = mWarehouseDao.queryRaw("select warehouse_id,warehouse_name from Warehouse where warehouse_id in ("+warehouse_id+")",new DataType[] { DataType.INTEGER,DataType.STRING });
			List<Object[]> resultsList = rawResults.getResults();
			if (resultsList.size() == 0) {
//				mPopupWindowDialog.showMessage("ֵ�ֲֿ��б�Ϊ��");
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
			//��Ϣ��λ
			mChangeWarehouseButton.setVisibility(View.INVISIBLE);
			
			if (GetCardInfo()) {	//������Ϣ�ɹ�
				CheckCardLeagal();
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private boolean GetCardInfo() {
		//��ȡ����
		String cardID = null;
		cardID = mRfidAdapter.HasCard();
		if (cardID == null) {
			mPopupWindowDialog.showMessage("δ������,�����¶���");
			return false;
		}
		mCardID = cardID;
		//��ȡ������Ϣ
		EnrollInfoGeneral enrollInfoCard = null;
		enrollInfoCard = mRfidAdapter.getEnrollInfo();
		if (enrollInfoCard == null) {
			mPopupWindowDialog.showMessage("δ����������Ϣ�������¶���");
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
	 * @description	������ť��Ӧʵ��
	 *
	 */
	class ReadCardOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (GetCardInfo()) {	//������Ϣ�ɹ�
				CheckCardLeagal();
			}
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
			mWarehouseID = mWarehouselist.get(mSelectedID).getWarehouse_id();
			
			editor.putInt("SelectedID", mSelectedID);
			editor.commit();
			mChangeWarehouseButton.setVisibility(View.INVISIBLE);
			if (mReCheck == true) {
				if (!RFIDIsExist()) { //��δȡ��
					CheckCardLeagal();
					
				} else { //����ȡ��
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
	 * @description	ж���ֿ��Ƿ����ֵ�ֲֿ⣨ʵ��Ϊֵ�ֲֿ���ж���ֿ��Ƿ���ͬ�����ڸ���ҵ���߼��޸ģ�
	 *
	 * @return ��ͬ:true,����ͬ:false
	 */
	private boolean CheckWarehouseID() {
		if (mEnrollInfo.getWorkWarehouseID() == mWarehouseID) {
			return true;
		}
		return false;
	}
	
	private void ProcessCheckByNetResult() {
		boolean ValidResult = false;										//У����
		String InvalidReason = null;										//У�鲻�Ϸ�ԭ��
		EnrollInfo ResultEnrollInfo = null;									//���۶�����Ϣ
		WeighInfo ResultWeighinfo = null;									//������Ϣ
		VehicleInfo EnrollVehicleInfo = null;								//������Ϣ
		WorkInfo EnrollWorkInfo = null;										//������ҵ��Ϣ
		GoodsInfo EnrollGoodsInfo = null;									//������Ϣ
		int warnflag = -1;													//���ѱ�־
		
		if (mCheckVehicleWorkResponse == null) {
			//����У��ʧ��,����У��
			Toast.makeText(getApplicationContext(), "����У��ʧ��,�Զ��л�Ϊ����У��", Toast.LENGTH_LONG).show();
			CheckByCard();
			return;
		}
		Result Result = mCheckVehicleWorkResponse.getResult();
		if (Result == null) {
			//����У��ʧ��,����У��
			Toast.makeText(getApplicationContext(), "����У��ʧ��,�Զ��л�Ϊ����У��", Toast.LENGTH_LONG).show();
			CheckByCard();
			return;
		}
		
		//������ȡ��У����
		ValidResult = Result.getResponseResult();
		InvalidReason = Result.getFailedReason();
		
		//��ȡУ����������Ϣ
		ResultEnrollInfo = mCheckVehicleWorkResponse.getResultEnrollInfo();
		ResultWeighinfo = mCheckVehicleWorkResponse.getResultWeighinfo();
		
		//������Ϣ
		if (ResultEnrollInfo != null) {	
			//������Ϣ
			EnrollVehicleInfo = ResultEnrollInfo.getEnrollVehicleInfo();
			if (EnrollVehicleInfo != null) {
				mEnrollInfo.setVehiclePlate(EnrollVehicleInfo.getVehiclePlate());
			}
			//��ҵ��Ϣ
			EnrollWorkInfo = ResultEnrollInfo.getEnrollWorkInfo();
			if (EnrollWorkInfo != null) {
				mEnrollInfo.setBusinessType(EnrollWorkInfo.getBusinessType());
				mEnrollInfo.setWorkNode(EnrollWorkInfo.getWorkNode());
				if (EnrollWorkInfo.getWarehouseID() != null) {
					mEnrollInfo.setWorkWarehouseID(Integer.valueOf(EnrollWorkInfo.getWarehouseID()));
				} 
				mEnrollInfo.setWorkNumber(EnrollWorkInfo.getWorkNumber());
				//У��Ϸ����ж���ʾ��־λ
				warnflag = EnrollWorkInfo.getWarnFlag();
				if (warnflag == 1) {
					mPopupWindowDialog.showMessage("��������:"+EnrollWorkInfo.getApprovedWeight()+",�ѳ�����:"+EnrollWorkInfo.getCompletedWeight());
				}
			}
			//������Ϣ
			EnrollGoodsInfo = ResultEnrollInfo.getEnrollGoodsInfo();
			if (EnrollGoodsInfo != null) {
				mEnrollInfo.setGrainType(EnrollGoodsInfo.getGoodsKind());
				mEnrollInfo.setGoodsOwner(EnrollGoodsInfo.getGoodsOwner());
			}
		}
		//������Ϣ
		if (ResultWeighinfo != null) {
			mWeighInfo.setGrossWeight(ResultWeighinfo.getGrossWeight());
			mWeighInfo.setTareWeight(ResultWeighinfo.getTareWeight());
			mWeighInfo.setNetWeight(ResultWeighinfo.getNetWeight());
			mWeighInfo.setDeductWeight(ResultWeighinfo.getDeductWeight());
		}
		DisplayInfo();
		
		if (!ValidResult) {					//У��ʧ��
			if (InvalidReason != null) {	//ʧ��ԭ��Ϊ��
				if (InvalidReason.equals("����ָ���ֿ�")) {
					mChangeWarehouseButton.setVisibility(View.VISIBLE);
					if (mWarehouselist.size()> 0) {
						mChangeWarehouseButton.setText("������"+mWarehouselist.get(mSelectedID).getWarehouse_name());
					}
				}
				SetCheckFalsed(InvalidReason);
			} else {
				SetCheckFalsed("����У��ʧ�ܣ�ԭ��δ֪");
			}
		} else {
			SetCheckTrue();
			mLoadingStatusRadioGroup.clearCheck();
			mLoadingFinishRadioButton.setChecked(true);	//����У��Ϸ�,Ĭ������ֵ��ȷ��
		}
	}
	
	private void DisplayInfo() {
		//��ʾ������Ϣ
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
		//�ҵ��ֿ�Ŷ�Ӧ�ֿ�����
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
	 * @description	ͨ������У��Ϸ���
	 *
	 */
	private void CheckByNet() {
		//REST����CheckVehicleWorkRequest����У��
		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "��������У����....");
		OnCancelListener CheckByNetProcessDialogCancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//��ʾ������
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
	 * @description	�Ǽǲ�������ҵ��ϵͳ
	 *
	 */
	private void RegistByNet() {
		//REST����RegisterVehicleWorkRequest�����ύ
		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "���������Ǽ���....");
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
					mPopupWindowDialog.showMessage("�Ǽǳɹ�");
					//���ߵǼǳɹ�д��
					RegistByCard();
					SetRegistButtonInuseable();
				} else {
					if (mRegisterVehicleWorkResponse != null) {
						mPopupWindowDialog.showMessage("�Ǽ�ʧ��,"+ mRegisterVehicleWorkResponse.getFailedReason());
					} else {
						if (RegistByCard()) {
							mPopupWindowDialog.showMessage("�Ǽǳɹ�");
							SetRegistButtonInuseable();
						} else {
							mPopupWindowDialog.showMessage("�Ǽ�ʧ��");
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
				//�Ǽ���ҵ����
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
	 * @description	����У��Ϸ���
	 *
	 */
	private void CheckByCard() {
		GetDataByCard();
		//��ʾ����������Ϣ
		DisplayInfo();
		if (CheckWorkNode()) {			//��ҵ���ںϷ�
			if (CheckWarehouseID()) {	//��ҵ�ֿ���ֵ�ֲֿ�һ��
				SetCheckTrue();
				//����У��ɹ�,Ĭ������ֵ��ȷ��
				mLoadingStatusRadioGroup.clearCheck();
				mLoadingFinishRadioButton.setChecked(true);
			} else {	//��ҵ�ֿ���ֵ�ֲֿⲻһ��
				SetCheckFalsed("���ֿ��뵱ǰֵ�ֲֿⲻһ�£���ȷ��!");
				mChangeWarehouseButton.setVisibility(View.VISIBLE);
				if (mWarehouselist.size()> 0) {
					mChangeWarehouseButton.setText("������"+mWarehouselist.get(mSelectedID).getWarehouse_name());
				}
			}
		}
	}
	
	private void GetDataByCard() {
		//��ȡ�����ۼ���Ϣ
		DeductedInfo deductedInfo = null;
		deductedInfo = mRfidAdapter.getAssayDeductedInfo();
		if (deductedInfo == null) {
			mPopupWindowDialog.showMessage("δ������������ۼ���Ϣ,�����¶���");
			return;
		}
		mAssayDeductedInfo.setMoistureDeducted(deductedInfo.getMoistureDeducted());
		//��ȡ������Ϣ
		WeighInfo weighInfo = null;
		weighInfo = mRfidAdapter.getWeighInfo();
		if (weighInfo == null) {
			mPopupWindowDialog.showMessage("δ����������Ϣ,,�����¶���");
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
		if (mEnrollInfo.getBusinessType() == Constants.ENTER_WAREHOUSE 			//���
				&& mEnrollInfo.getFirstWeight() == Constants.GROSS_FIRST 		//��ë��Ƥ
				&& mEnrollInfo.getWorkNode() != Constants.WEIGHT_GROSS) {		//δ��ë
			
			if (mEnrollInfo.getWorkNode() != Constants.WAREHOUSE_CONFIRM 						//����ֵ��ȷ��
					&& mEnrollInfo.getWorkNode() != Constants.CHANGE_WAREHOUSE 					//����δж�껻��
					&& mEnrollInfo.getWorkNode() != Constants.ASSAY_CHANGE_WAREHOUSE 			//�����������⻻��
					&& mEnrollInfo.getWorkNode() != Constants.WHOLE_VEHICLE_CHANGE_WAREHOUSE) {	//������������
				SetCheckFalsed("�����ë��Ƥ��ҵ,δ��ë�أ�����ֵ��");
				return false;
			}
		}
		
		if (mEnrollInfo.getBusinessType() == Constants.OUT_OF_WAREHOUSE		//����
				&& mEnrollInfo.getFirstWeight() == Constants.TARE_FIRST && 	//��Ƥ��ë
				mEnrollInfo.getWorkNode() != Constants.WEIGHT_TARE) {		//δ��Ƥ
			
			if (mEnrollInfo.getWorkNode() != Constants.WAREHOUSE_CONFIRM 						//����ֵ��ȷ��
					&& mEnrollInfo.getWorkNode() != Constants.CHANGE_WAREHOUSE 					//����δж�껻��
					&& mEnrollInfo.getWorkNode() != Constants.ASSAY_CHANGE_WAREHOUSE 			//�����������⻻��
					&& mEnrollInfo.getWorkNode() != Constants.WHOLE_VEHICLE_CHANGE_WAREHOUSE) {	//������������
				SetCheckFalsed("������Ƥ��ë��ҵ,δ��Ƥ��,����ֵ��");
				return false;
			}
		}
		
		if (mEnrollInfo.getVehiceType() == Constants.IN_VEHICLE					//�ڲ���
				&& mEnrollInfo.getBusinessType() == Constants.ENTER_WAREHOUSE	//���
				&& mEnrollInfo.getFirstWeight() == Constants.TARE_FIRST			//��Ƥ��ë
				&& mEnrollInfo.getWorkNode() != Constants.WEIGHT_GROSS) {		//δ��ë
			if (mEnrollInfo.getWorkNode() != Constants.WAREHOUSE_CONFIRM 						//����ֵ��ȷ��
					&& mEnrollInfo.getWorkNode() != Constants.CHANGE_WAREHOUSE 					//����δж�껻��
					&& mEnrollInfo.getWorkNode() != Constants.ASSAY_CHANGE_WAREHOUSE 			//�����������⻻��
					&& mEnrollInfo.getWorkNode() != Constants.WHOLE_VEHICLE_CHANGE_WAREHOUSE) {	//������������
				SetCheckFalsed("�ڲ��������Ƥ��ë��ҵ,δ��ë��,����ֵ��");
				return false;
			}
		}
		
		if (mEnrollInfo.getVehiceType() == Constants.IN_VEHICLE					//�ڲ���
				&& mEnrollInfo.getBusinessType() == Constants.OUT_OF_WAREHOUSE	//����
				&& mEnrollInfo.getFirstWeight() == Constants.GROSS_FIRST		//��Ƥ��ë
				&& mEnrollInfo.getWorkNode() != Constants.WEIGHT_TARE) {		//δ��Ƥ
			if (mEnrollInfo.getWorkNode() != Constants.WAREHOUSE_CONFIRM 						//����ֵ��ȷ��
					&& mEnrollInfo.getWorkNode() != Constants.CHANGE_WAREHOUSE 					//����δж�껻��
					&& mEnrollInfo.getWorkNode() != Constants.ASSAY_CHANGE_WAREHOUSE 			//�����������⻻��
					&& mEnrollInfo.getWorkNode() != Constants.WHOLE_VEHICLE_CHANGE_WAREHOUSE) {	//������������
				SetCheckFalsed("�ڲ���������Ƥ��ë��ҵ,δ��Ƥ��,����ֵ��");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description �Ǽǲ���������������ϵͳ
	 *
	 */
	private boolean RegistByCard() {
		//д��������д�����
		//1.д����ҵ��Ϣ����	
		mRfidAdapter.setOperateLink(String.valueOf(mEnrollInfo.getWorkNode()));
		//2.д��ж���ֿ����
		mRfidAdapter.setStorage(String.valueOf(mWarehouseID));
		//3.д��ֵ����ԱID
		mRfidAdapter.setOprID(String.valueOf(mEnrollInfo.getOperatorID()));
		boolean res = SyncBlock.SyncBlock4(mRfidAdapter,RegistVehicleWorkActivity.this);
		if (res) {
			//��ȡд����ҵ������Ϣ
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
	 * @description	У������Ϸ���
	 *
	 */
	private void CheckCardLeagal() {
		if (mEnrollInfo.getSinged() == Constants.CARD_NOT_ISSUED) {
			mPopupWindowDialog.showMessage("��δ����");
		}else if (mEnrollInfo.getSinged() == Constants.MOBILE_RETREATED) {
			mPopupWindowDialog.showMessage("�����ֳֶ��˿�");
		}else if (mEnrollInfo.getSinged() == Constants.CARD_ISSUED) {	//���ѷ���ʹ��
			if (mGrainApplication.SWITCH) {			//��������ģʽ
				if (LoginActivity.NET_AVILIABE) {	//������������
					CheckByNet();	//����У��
				} else {							//���������쳣
					CheckByCard();					//����У��
				}
			} else {								//δ��������ģʽ
				CheckByCard();						//����У��
			}
		}
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
			//��ȡ��ǰ��¼�û�ID
			mEnrollInfo.setOperatorID(mLoginSharedPreferences.getInt("current_user_id", 0));
			if (mEnrollInfo.getOperatorID() == 0) {
				mPopupWindowDialog.showMessage("δ��ȡ��ֵ����ԱID,��ͬ���˻���Ϣ�����µǼ�");
				return;
			}
			
			if (mGrainApplication.SWITCH) {
				RegistByNet();
			} else {
				if (RegistByCard()) {
					mPopupWindowDialog.showMessage("�Ǽǳɹ�");
				} else {
					mPopupWindowDialog.showMessage("�Ǽ�ʧ��");
				}
			}
			
			//�ǼǺ���մ��ڣ����¿�ʼ
			ClearWindow();
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
				//���ø���ֵ�ֲֿ�
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
	 * @description ���ݶ�������ҵ���ڱ�ţ�������ҵ������ʾ��Ϣ
	 *
	 */
	private void SetWorkNodeTextView() {
		switch (mEnrollInfo.getWorkNode()) {
		case 0:
			mWorkNodeTextView.setText("δֵ��");
			break;
		case 1:
			mWorkNodeTextView.setText("ֵ��ȷ��");
			break;
		case 2:
			mWorkNodeTextView.setText("ֵ�ֻ���");
			break;
		case 3:
			mWorkNodeTextView.setText("���»���");
			break;
		case 4:
			mWorkNodeTextView.setText("����Ƥ��");
			break;
		case 5:
			mWorkNodeTextView.setText("����ë��");
			break;
		case 6:
			mWorkNodeTextView.setText("��������");
			break;
		default:
			mWorkNodeTextView.setText("��ҵ���ڴ���");
			break;
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
	
//	/**
//	 * 
//	 * @author zwz
//	 * @date 2013-7-11
//	 * @description	���������Ϣ������ˮ�ֿ���+�������ʿ���+����ˮ�ֿ���+�������ʿ���
//	 *
//	 */
//	private int CalcuDeduction() {
//		String string = null;
//		string = mRfidAdapter.getF1();
//		if (string == null) {
//			mPopupWindowDialog.showMessage("������ȡ����ˮ�ֿ������λʧ��");
//			string = "0";
//		}
//		mMoistureAmountFlagFirst = Integer.valueOf(string);
//		string = mRfidAdapter.getD1();
//		if (string == null) {
//			mPopupWindowDialog.showMessage("������ȡ����ˮ�ֿ���ʧ��");
//			string = "0";
//		}
//		if (mMoistureAmountFlagFirst == 0) {	//����
//			mMoistureDeductAmountFirst = Double.parseDouble(string);
//		} else {	//�ٷֱ�
//			mMoistureDeductAmountFirst = mWeighInfo.getNetWeight()*Double.parseDouble(string)/100;
//		}
//		string = mRfidAdapter.getF2();
//		if (string == null) {
//			mPopupWindowDialog.showMessage("������ȡ�������ʿ������λʧ��");
//			string = "0";
//		}
//		mImpurityAmountFlagFirst = Integer.valueOf(string);
//		string = mRfidAdapter.getD2();
//		if (string == null) {
//			mPopupWindowDialog.showMessage("������ȡ�������ʿ���ʧ��");
//			string = "0";
//		}
//		if (mImpurityAmountFlagFirst == 0) {	//����
//			mImpurityDeductAmountFirst = Double.parseDouble(string);
//		} else {	//�ٷֱ�
//			mImpurityDeductAmountFirst = mWeighInfo.getNetWeight()*Double.parseDouble(string)/100;
//		}
//		
//		string = mRfidAdapter.getF_1();
//		if (string == null) {
//			mPopupWindowDialog.showMessage("������ȡ���ο������λʧ��");
//			string = "0";
//		}
//		mAdjustDeductFlag = Integer.valueOf(string);
//		if (mAdjustDeductFlag == 1) {	//�ж��ο���
//			string = mRfidAdapter.getF5();
//			if (string == null) {
//				mPopupWindowDialog.showMessage("������ȡ����ˮ�ֿ������λʧ��");
//				string = "0";
//			}
//			mMoistureAmountFlagAdjust = Integer.valueOf(string);
//			string = mRfidAdapter.getD5();
//			if (string == null) {
//				mPopupWindowDialog.showMessage("������ȡ����ˮ�ֿ���λʧ��");
//				string = "0";
//			}
//			if (mMoistureAmountFlagAdjust == 0) {	//����
//				mMoistureDeductAmountAdjust = Double.parseDouble(string);
//			} else {	//�ٷֱ�
//				mMoistureDeductAmountAdjust = mWeighInfo.getNetWeight()*Double.parseDouble(string)/100;
//			}
//			string = mRfidAdapter.getF6();
//			if (string == null) {
//				mPopupWindowDialog.showMessage("������ȡ�������ʿ������λʧ��");
//				string = "0";
//			}
//			mImpurityAmountFlagAdjust = Integer.valueOf(string);
//			string = mRfidAdapter.getD6();
//			if (string == null) {
//				mPopupWindowDialog.showMessage("������ȡ�������ʿ���ʧ��");
//				string = "0";
//			}
//			if (mImpurityAmountFlagAdjust == 0) {	//����
//				mImpurityDeductAmountAdjust = Double.parseDouble(string);
//			} else {	//�ٷֱ�
//				mImpurityDeductAmountAdjust = mWeighInfo.getNetWeight()*Double.parseDouble(string)/100;
//			}
//			mWeighInfo.setDeductWeight((int)(mMoistureDeductAmountFirst + mImpurityDeductAmountFirst+mMoistureDeductAmountAdjust+mImpurityDeductAmountAdjust));
//		} else {	//û�ж��ο���
//			mWeighInfo.setDeductWeight((int)(mMoistureDeductAmountFirst + mImpurityDeductAmountFirst));
//		}
//		return 0;
//	}
	
	class LoadingStatusOnCheckedChangeListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			if (checkedId == R.id.regist_vehicle_work_rbtn_loading_finish) {	//װж���
				mEnrollInfo.setWorkNode(Constants.WAREHOUSE_CONFIRM);
			}
			if (checkedId == R.id.regist_vehicle_work_rbtn_loading_unfinish_need_assay) {	//װжδ��ɣ����»���
				mEnrollInfo.setWorkNode(Constants.ASSAY_CHANGE_WAREHOUSE);
			}
			if (checkedId == R.id.regist_vehicle_work_rbtn_loading_unfinish_change_warehouse) {	//װжδ��ɣ�����
				mEnrollInfo.setWorkNode(Constants.CHANGE_WAREHOUSE);
			}
			
		}
	}
	
	private void GetConfigInfo() {
		//��������Ϣ������Ĭ�ϲֿ�ѡ����
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
			typeName = "���";
			break;
		case 2:
			typeName = "����";
			break;
		case 3:
			typeName = "����";
			break;
		default:
			typeName = "";
			break;
		}
		return typeName;
	}
	
	private void SetCheckFalsed(String msg) {
		mLeagal = "���Ϸ�";
		mLeagalTextView.setText(mLeagal);
		mLeagalTextView.setTextColor(Color.rgb(255, 0, 0));
		mPopupWindowDialog.showMessage(msg);
		mReCheck = true;
	}
	
	private void SetCheckTrue() {
		mChangeWarehouseButton.setVisibility(View.INVISIBLE);
		SetRegistButtonUseable();
		mLeagal = "�Ϸ�";
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
			if (GetCardInfo()) {	//������Ϣ�ɹ�
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
