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
	
	//�߳��и���UI��Ϣ
	private final int Msg_GetDecutedInfoFailedByNet = 0;
	private final int Msg_GetQulityIndexInfoFailedByCard = 1;
	private final int Msg_SaveAdjustDecutedInfoFailedNeedSyncAcountInfo = 2;
	private final int Msg_SaveAdjustDecutedInfoFailedNoCard = 3;
	
	//UI
	private Spinner mSpinnerWarehouse = null;								//ֵ�ֲֿ������б�
	
	private Button mReadCardButton = null;									//������ť
	private Button mSaveDeductionDiscountButton = null;						//��������ۼ۰�ť
	private Button mRegistVehicleWorkButton = null;							//�Ǽǳ�����ҵ��ť
	
	private TextView mMoistureDeductedTextView = null;						//ˮ�ֿ���
	private TextView mMoistureDeductedUnitTextView = null;					//ˮ�ֿ�����λ
	private TextView mImpurityDeductedTextView = null;						//���ʿ���
	private TextView mImpurityDeductedUnitTextView = null;					//���ʿ�����λ
	private TextView mIncidentalExpensesDeductedTextView = null;			//�����ӷ�
	private TextView mIncidentalExpensesDeductedUnitTextView = null;		//�����ӷѵ�λ
	private TextView mBakedExpensesDeductedTextView = null;					//����ɹ��
	private TextView mBakedExpensesDeductedUnitTextView = null;				//����ɹ�ѵ�λ
	
	private EditText mAdjustMoistureDeductedEditText = null;				//ˮ�ֿ����ӿ�
	private Button mAdjustMoistureDeductedUnitButton = null;				//����ˮ�ֿ�����λ
	private EditText mAdjustImpurityDeductedEditText = null;				//���ʿ����ӿ�
	private Button mAdjustImpurityDeductedUnitButton = null;				//�������ʿ�����λ
	private EditText mAdjustIncidentalExpensesDeductedEditText = null;		//�����ӷѼӿ�
	private Button mAdjustIncidentalExpensesDeductedUnitButton = null;		//���������ӷѵ�λ
	private EditText mAdjustBakedExpensesDeductedEditText = null;			//����ɹ�Ѽӿ�
	private Button mAdjustBakedExpensesDeductedUnitButton = null;			//��������ɹ�ѵ�λ
	
	private ListView mQualityIndexListView = null;							//����ָ��Listview
	private LinearLayout mLinearLayout = null;								//ListView Item ����
	
	private Handler mHandler = null;
	
	//Data
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();		//�ֿ������б�
	private List<QualityIndexItemData> mQualityIndexItemDataList = null;		//QualityIndex����ListView����ʾ
	private List<QualityIndexResultInfo> mQualityIndexResultInfoList = null;	//rest�����Ļ���ָ���б�(ID,Value)
	
	private double mMoistureDeducted = -1;									//����ˮ�ֿ���
	private double mImpurityDeducted = -1;									//�������ʿ���
	private double mIncidentalExpensesDeducted = -1;						//���ο����ӷ�
	private double mBakedExpensesDeducted = -1;								//���ο���ɹ��
	private int mMoistureDeductedMode = 0;									//����ˮ�ֿ�����λ���
	private int mImpurityDeductedMode = 0;									//�������ʿ�����λ���
	private int mIncidentalExpensesDeductedMode = 0;						//���ο����ӷѵ�λ���
	private int mBakedExpensesDeductedMode = 0;								//���ο���ɹ�ѵ�λ���
	
	private double mAdjustMoistureDeducted = 0;								//ˮ�ֿ����ӿ�
	private double mAdjustImpurityDeducted = 0;								//���ʿ����ӿ�
	private double mAdjustIncidentalExpensesDeducted = 0;					//�����ӷѼӿ�
	private double mAdjustBakedExpensesDeducted = 0;						//����ɹ�Ѽӿ�
	private int mAdjustMoistureDeductedMode = -1;							//ˮ�ֿ�����λ�ӿ۱��
	private int mAdjustImpurityDeductedMode = -1;							//���ʿ�����λ�ӿ۱��
	private int mAdjustIncidentalExpensesDeductedMode = -1;					//�����ӷѵ�λ�ӿ۱��
	private int mAdjustBakedExpensesDeductedMode = -1;						//����ɹ�ѵ�λ�ӿ۱��
	
	private String mSigned = null;											//���λ
	private String mCardID = null;											//����
	private int mSelectedID = 0;											//ֵ�ֲֿ��б�ѡ��ID
	
	//REST
	private RestWebServiceAdapter mRestWebServiceAdapter = null;			//rest
	private GetDecutedInfoResponse mGetDecutedInfoResponse = null;
	private AdjustDeductedResponse mAdjustDeductedResponse = null;
	
	
	//Flag
	private boolean mNetSaveSuccess = false;
	private boolean mCardSaveSuccess = false;
	
	//DB
	private Dao<Warehouse, Integer> mWarehouseDao =  null;					//�ֿ���ϢDao
	private List<Warehouse> mWarehouselist = null;							//��ѯ���
	private Dao<QualityIndex, Integer> mQualityIndexDao =  null;			//����ָ����ϢDao
	private List<QualityIndex> mQualityIndexlist = null;					//��ѯ���
	private Dao<AcountInfo, Integer> mAcountInfoDao = null;					//�û���ϢDao
	
	//System
	private SharedPreferences sharedPreferences = null;						//����ѡ�еĲֿ�
	private Editor editor = null;											//SharedPreferences�༭��
	private SharedPreferences mLoginSharedPreferences = null;				//�����¼��Ϣ
	private GrainApplication mGrainApplication = null;
	private Context mContext = null;
	
	//Rfid
	private RfidAdapter mRfidAdapter = null;								//����������
	
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
	 * @description	��ʼ��
	 *
	 */
	private void InitCtrl() {
		//System
		mContext = this;
		mGrainApplication = (GrainApplication)getApplication();
		sharedPreferences = getSharedPreferences("WarehouseInfo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//��ȡ�༭��
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
		
		// �������뷨
		InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		// ��ʾ�����������뷨
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
		//��������Ϣ������Ĭ�ϲֿ�ѡ����
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
		
		//���̸߳���UI
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case Msg_GetDecutedInfoFailedByNet:
					mPopupWindowDialog.showMessage("����δ��ȡ����ʳָ����Ϣ");
					break;
				case Msg_GetQulityIndexInfoFailedByCard:
					mPopupWindowDialog.showMessage("δ��������ָ����Ϣ");
					break;
				case Msg_SaveAdjustDecutedInfoFailedNeedSyncAcountInfo:
					mPopupWindowDialog.showMessage("��ͬ���˻���Ϣ������,����ʧ��");
					break;
				case Msg_SaveAdjustDecutedInfoFailedNoCard:
					mPopupWindowDialog.showMessage("δ������,����ʧ��");
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
			mPopupWindowDialog.showMessage("�ù�����δ����");
		}
	}
	
	class AdjustBakedExpensesDeductedOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mPopupWindowDialog.showMessage("�ù�����δ����");
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description	��ʼ��ֵ�ֲֿ������˵�
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
		mGrainBroadcastReceiver.setIReadCard(mIReadCard);
		super.onResume();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SCAN) {
			//������ȡ����
			if(!ReadByCard()){
				return false;
			}
			
			if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
				mPopupWindowDialog.showMessage("��δ����");
			}
			
			if (mSigned.equals(Constants.MOBILE_RETREATED)) {
				mPopupWindowDialog.showMessage("�����ֳֻ��˿�");
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
	 * @description	������ťʵ��
	 *
	 */
	class ReadCardOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//������ȡ����
			if(!ReadByCard()){
				return;
			}
			
			if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
				mPopupWindowDialog.showMessage("��δ����");
			}
			
			if (mSigned.equals(Constants.MOBILE_RETREATED)) {
				mPopupWindowDialog.showMessage("�����ֳֻ��˿�");
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
	 * @description	��������ۼ۰�ťʵ��
	 *
	 */
	class SaveDeductionDiscountOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if ((mMoistureDeducted == -1) || (mImpurityDeducted == -1) || (mIncidentalExpensesDeducted == -1) || (mBakedExpensesDeducted == -1)) {
				mPopupWindowDialog.showMessage("���ȶ���");
				return;
			}
			
			//У�����������Ƿ�Ϸ�
			boolean res = CheckDataLeagal();
			
			if (res) {
				//��ȡ���������
				GetEditTextData();
				SaveDecutedInfoBusiness();
			}
		}
	}
	
	private void SaveDecutedInfoBusiness() {
		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "���ڱ�����....");
		OnCancelListener SaveProcessDialogCancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//��ʾ������
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
		if (mGrainApplication.SWITCH) {	//��������ģʽ
			if (mAdjustDeductedResponse != null) {	//������������ۼۻ�ȡ�����ؽ��
				if (mNetSaveSuccess) {
					mPopupWindowDialog.showMessage("����ɹ�");
					mSaveDeductionDiscountButton.setEnabled(false);
					mSaveDeductionDiscountButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
				} else {
					mPopupWindowDialog.showMessage(mAdjustDeductedResponse.getFailedReason());
				}
			} else {
				if (mCardSaveSuccess) {
					mPopupWindowDialog.showMessage("����ɹ�");
					mSaveDeductionDiscountButton.setEnabled(false);
					mSaveDeductionDiscountButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
				} else {
					mPopupWindowDialog.showMessage("����ʧ��");
				}
			}
		} else { //δ��������ģʽ
			if (mCardSaveSuccess) {
				mPopupWindowDialog.showMessage("����ɹ�");
				mSaveDeductionDiscountButton.setEnabled(false);
				mSaveDeductionDiscountButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
			} else {
				mPopupWindowDialog.showMessage("����ʧ��");
			}
		}
	}
	
	private void SaveData() {
		//���翪�ش�,����+���ر���;���翪�عر�,���ر���
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
	 * @description ��ȡ��������
	 *
	 */
	private boolean ReadByCard() {
		Log.v(TAG, "ReadByCard()");
		String string = null;
		//����
		string = mRfidAdapter.HasCard();
		if (string == null) {
			mPopupWindowDialog.showMessage("δ������,�����¶���");
			Log.v(TAG, "δ������,�����¶���");
			return false;
		}
		mCardID = string;
		//���λ
		string = mRfidAdapter.getTagNo();
		if (string == null) {
			mPopupWindowDialog.showMessage("δ������־λ,�����¶���");
			Log.v(TAG, "δ������־λ,�����¶���");
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
		//����ˮ�ֿ���
		if (mMoistureDeductedMode == 0) {
			mMoistureDeductedUnitTextView.setText(getString(R.string.kilogram));
		} else {
			mMoistureDeductedUnitTextView.setText(getString(R.string.percent));
		}
		//�������ʿ���
		if (mImpurityDeductedMode == 0) {
			mImpurityDeductedUnitTextView.setText(getString(R.string.kilogram));
		} else {
			mImpurityDeductedUnitTextView.setText(getString(R.string.percent));
		}
		//���ο����ӷ�
		if (mIncidentalExpensesDeductedMode == 0) {
			mIncidentalExpensesDeductedUnitTextView.setText(getString(R.string.yuan));
		} else {
			mIncidentalExpensesDeductedUnitTextView.setText(getString(R.string.percent));
		}
		//���ο���ɹ��
		if (mBakedExpensesDeductedMode == 0) {
			mBakedExpensesDeductedUnitTextView.setText(getString(R.string.yuan));
		} else {
			mBakedExpensesDeductedUnitTextView.setText(getString(R.string.percent));
		}
		//�ֳֻ�ˮ�ֿ����ӿ�
		if (mAdjustMoistureDeductedMode == 0) {
			mAdjustMoistureDeductedUnitButton.setText(getString(R.string.kilogram));
			mAdjustMoistureDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else {
			mAdjustMoistureDeductedUnitButton.setText(getString(R.string.percent));
			mAdjustMoistureDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		//�ֳֻ����ʿ����ӿ�
		if (mAdjustImpurityDeductedMode == 0) {
			mAdjustImpurityDeductedUnitButton.setText(getString(R.string.kilogram));
			mAdjustImpurityDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else {
			mAdjustImpurityDeductedUnitButton.setText(getString(R.string.percent));
			mAdjustImpurityDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		//�ֳֻ������ӷѼӿ�
		if (mAdjustIncidentalExpensesDeductedMode == 0) {
			mAdjustIncidentalExpensesDeductedUnitButton.setText(getString(R.string.yuan));
			mAdjustIncidentalExpensesDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		} else {
			mAdjustIncidentalExpensesDeductedUnitButton.setText(getString(R.string.percent));
			mAdjustIncidentalExpensesDeductedEditText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
		}
		//�ֳֻ�����ɹ�Ѽӿ�
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
		
		//���ñ��水ť����
		mSaveDeductionDiscountButton.setEnabled(true);
		mSaveDeductionDiscountButton.setBackgroundResource(R.drawable.corner_btn_selector);
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
		String test = "^[+]?\\d*([.]\\d{0,2})?$"; 
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
	 * @description	��ȡ�������ݣ�ת�����ַ���
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
	 * @description	У���������ݵĺϷ���
	 *
	 * @return У��ɹ�:true��У��ʧ��:false
	 */
	private boolean CheckDataLeagal() {
		//У���������ݺϷ���
		if (mAdjustMoistureDeductedMode == 0) {
			if (!IsIntger(String.valueOf(mAdjustMoistureDeductedEditText.getText().toString()))) {
				mAdjustMoistureDeductedEditText.setError("�����ʽ����");
				return false;
			}
		} else {
			if (!IsDecimal(String.valueOf(mAdjustMoistureDeductedEditText.getText().toString()))) {
				mAdjustMoistureDeductedEditText.setError("�����ʽ����");
				return false;
			}
		}
		
		if (mAdjustImpurityDeductedMode == 0) {
			if (!IsIntger(String.valueOf(mAdjustImpurityDeductedEditText.getText().toString()))) {
				mAdjustImpurityDeductedEditText.setError("�����ʽ����");
				return false;
			}
		} else {
			if (!IsDecimal(String.valueOf(mAdjustImpurityDeductedEditText.getText().toString()))) {
				mAdjustImpurityDeductedEditText.setError("�����ʽ����");
				return false;
			}
		}
		if (mAdjustIncidentalExpensesDeductedMode == 0) {
			if (!IsIntger(String.valueOf(mAdjustIncidentalExpensesDeductedEditText.getText().toString()))) {
				mAdjustIncidentalExpensesDeductedEditText.setError("����������");
				return false;
			}
		} else {
			if (!IsDecimal(String.valueOf(mAdjustIncidentalExpensesDeductedEditText.getText().toString()))) {
				mAdjustIncidentalExpensesDeductedEditText.setError("�����ʽ����");
				return false;
			}
		}
		if (mAdjustBakedExpensesDeductedMode == 0) {
			if (!IsIntger(String.valueOf(mAdjustBakedExpensesDeductedEditText.getText().toString()))) {
				mAdjustBakedExpensesDeductedEditText.setError("����������");
				return false;
			}
		} else {
			if (!IsDecimal(String.valueOf(mAdjustBakedExpensesDeductedEditText.getText().toString()))) {
				mAdjustBakedExpensesDeductedEditText.setError("�����ʽ����");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-26
	 * @description ��ʼ������ָ��listview
	 *
	 */
	private void InitListView(){
		if (mQualityIndexResultInfoList == null) {
			Toast.makeText(getApplicationContext(), "����ָ����ϢΪ��", Toast.LENGTH_LONG).show();
			return;
		}
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
		//���ο����ۼ���Ϣ
		mMoistureDeducted = AssayDeductedInfo.getMoistureDeducted();
		mImpurityDeducted = AssayDeductedInfo.getImpurityDeducted();
		mIncidentalExpensesDeducted = AssayDeductedInfo.getIncidentalExpensesDeducted();
		mBakedExpensesDeducted = AssayDeductedInfo.getBakedExpensesDeducted();
		
		mMoistureDeductedMode = AssayDeductedInfo.getMoistureDeductedMode();
		mImpurityDeductedMode = AssayDeductedInfo.getImpurityDeductedMode();
		mIncidentalExpensesDeductedMode = AssayDeductedInfo.getIncidentalExpensesDeductedMode();
		mBakedExpensesDeductedMode = AssayDeductedInfo.getBakedExpensesDeductedMode();
		
		if (AdjustDeductedInfo == null) {
			//�ֳֻ�������ϢΪ��
			mAdjustMoistureDeductedMode = mMoistureDeductedMode;
			mAdjustImpurityDeductedMode = mImpurityDeductedMode;
			mAdjustIncidentalExpensesDeductedMode = mIncidentalExpensesDeductedMode;
			mAdjustBakedExpensesDeductedMode = mBakedExpensesDeductedMode;
			return;
		}
		
		//���������ۼ���Ϣ
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
		//����ָ����Ϣ
		mQualityIndexResultInfoList = ResultQualityInfos;
		BindQualityIndexData();
	}
	
	private void BindQualityIndexData() {
		//�����ʾ��Ϣ
    	mQualityIndexItemDataList.clear();
    	//�����ݿ��ȡ����ָ��id,name
		try {
			mQualityIndexlist = mQualityIndexDao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
    	//���mIndexBeansListWeb(webservice)��mQualityIndexlist(���ݿ�)id��ͬ�����ƺ���ֵ�󶨵�mQualityIndexItem
		//��webservice��������Ϊ�������ݿ��д洢����ָ��id��name��web���ص�ǰ���Ű�����ָ��id
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
		
		//���ο����ۼ���Ϣ���͸�������
		if (mAdjustDeductedResponse == null) {
			return false;
		}
		//��ȡ������������Ϣ���н���
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
			mPopupWindowDialog.showMessage("�ù�����δ����");
			return;
//			if(mAdjustIncidentalExpensesDeductedMode == -1){
//				return;			
//			} else if (mAdjustIncidentalExpensesDeductedMode == 0) {
//				mAdjustIncidentalExpensesDeductedMode = 0;
//				mAdjustIncidentalExpensesDeductedEditText.setText("");
//				mAdjustIncidentalExpensesDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//				mAdjustIncidentalExpensesDeductedUnitButton.setText(getString(R.string.yuan));
//				//���ó�ֻ�ܿ��ܽ�����������ܰ��ٷֱȿ۽��
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
			mPopupWindowDialog.showMessage("�ù�����δ����");
			return;
//			if(mAdjustBakedExpensesDeductedMode == -1){
//				return;			
//			} else if (mAdjustBakedExpensesDeductedMode == 0) {
//				mAdjustBakedExpensesDeductedMode = 0;
//				mAdjustBakedExpensesDeductedEditText.setText("");
//				mAdjustBakedExpensesDeductedEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
//				mAdjustBakedExpensesDeductedUnitButton.setText(getString(R.string.yuan));
//				//���ó�ֻ�ܿ��ܽ�����������ܰ��ٷֱȿ۽��
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
		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "���ڻ�ȡ��....");
		OnCancelListener GetProcessDialogCancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				//��ʾ�����ۼ���Ϣ
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
		if (mGrainApplication.SWITCH) {	//��������ģʽ
			if (LoginActivity.NET_AVILIABE) {	//�������
				GetDataByNet();
				if (mGetDecutedInfoResponse == null) {	//��ȡ�����ۼ���Ϣʧ��
					GetDataByCard();
				}
			} else {	//���粻����
				GetDataByCard();
			}
		} else {	//û�п�������ģʽ
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
		//�����ȡ
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
			//������ȡ����
			if(!ReadByCard()){
				return;
			}
			
			if (mSigned.equals(String.valueOf(Constants.CARD_NOT_ISSUED))) {
				Log.v(TAG, "��δ����");
				mPopupWindowDialog.showMessage("��δ����");
			}
			
			if (mSigned.equals(String.valueOf(Constants.MOBILE_RETREATED))) {
				Log.v(TAG, "�����ֳֻ��˿�");
				mPopupWindowDialog.showMessage("�����ֳֻ��˿�");
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
