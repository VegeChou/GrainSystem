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
	
	private double mMoistureDeductAmountFirst = 0;						//����ˮ�ֿ���
	private double mImpurityDeductAmountFirst = 0;						//�������ʿ���
	private double mMoistureDeductAmountAdjust = 0;						//ˮ�ֿ����ӿ�
	private double mImpurityDeductAmountAdjust = 0;						//���ʿ����ӿ�
	private int mMoistureAmountFlagFirst = -1;							//����ˮ�ֿ�����λ���
	private int mImpurityAmountFlagFirst = -1;							//�������ʿ�����λ���
	private int mMoistureAmountFlagAdjust = -1;							//ˮ�ֿ�����λ�ӿ۱��
	private int mImpurityAmountFlagAdjust = -1;							//���ʿ�����λ�ӿ۱��
	private int mAdjustDeductFlag = -1;									//���ο������
	
	private HashMap<?, ?> mHashMap = null;									//��ȡ������Ϣhash��
	
//	private String mLeagal = null;						//�Ϸ���
	private String mVarieties = null;					//Ʒ��
	private String mLicensePlate = null;				//����
	private String mOwners = null;						//����
	private String mType = null;						//����
	private String mLink = null;						//����
	private String mGrossWeight = null;					//ë��
	private String mTare = null;						//Ƥ��
	private String mNetWeigth = null;					//����
	private String mDeduction = null;					//����
	private String mSigned = null;						//���λ
	
	
	private int mWarehouseID = -1;						//ֵ�ֲֿ�ID
	
	private TextView mVarietiesTextView = null;			//Ʒ�ֱ༭��
	private TextView mLicensePlateTextView = null;		//���Ʊ༭��
	private TextView mOwnersTextView = null;			//�����༭��
	private TextView mTypeTextView = null;				//���ͱ༭��
	private TextView mLinkTextView= null;				//���ڱ༭��
	private TextView mGrossWeightTextView = null;		//ë�ر༭��
	private TextView mTareTextView = null;				//Ƥ�ر༭��
	private TextView mNetWeigthTextView = null;			//���ر༭��
	private TextView mDeductionTextView = null;			//�����༭��
	private TextView mUploadingWarehousTextView = null;	//ж���ֿ�༭��
	
	private Button mReadCardButton = null;					//������ť
	private Button mRetreatButton = null;						//�ո��˿���ť
	private TextView mStorehouseWarehouseTextView = null;	//ֵ�ֲֿ�༭��
	private TextView mPromptTextView = null;				//��ʾ�༭��
	
	private String mUpLoadingWarehouseID = null;					//ж���ֿ�ID
	private String mUpLoadingWarehouseName = null;			//ж���ֿ�����
	
	private Dao<Warehouse, Integer> mWarehouseDao =  null;		//�ֿ���ϢDao
	private List<Warehouse> mWarehouselist = null;				//��ѯ���
	
	private SharedPreferences sharedPreferences = null;			//����ѡ�еĲֿ�
	
	private int mSelectedID = 0;								//�ֿ��б�ѡ��ID
	private boolean mBusinessFinishFlag = false;				//ҵ����ɱ�־
	
	private RfidAdapter mRfidAdapter = null;					//rfid����������
	
	private Handler mHandler = null;						//�̺߳�UI������Ϣ
	private boolean mReadCardThreadFlag = false;
	private String mCardIDRemember = null;					//�Ѷ�����ID
	
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
		
		//��
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
		
		//��������Ϣ������ ѡ�вֿ�
		InitWarehouseName();
		
		//���������߳�
//		StartReadCard();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SCAN) {
			//�������IC������Ϣ������ҵ���߼����д���
			if (mRfidAdapter.HasCard() == null) {
				mPopupWindowDialog.showMessage("����ʧ��");
				return false;
			}
			
			String data = mRfidAdapter.getTagNo();
			if (data == null) {
				mPopupWindowDialog.showMessage("��ȡ���λ��Ϣʧ��");
				return false;
			}
//			if (RegistVehicleWorkActivity.IsInitData(data)||(data.equals("0"))) {
			if ((data.equals("0"))) {
				mPopupWindowDialog.showMessage("����δ��ҵ���з���");
				return false;
			}
//			mSigned = Integer.parseInt(data);
			mSigned = data;
			
			data = mRfidAdapter.getOperateLink();
			if (data == null) {
				mPopupWindowDialog.showMessage("��ȡ��ҵ������Ϣʧ��");
				return false;
			}
//			if (!RegistVehicleWorkActivity.IsInitData(data)) {
//				mLink = Integer.parseInt(data);
				mLink = data;
//			}
			
			data = mRfidAdapter.getStorage();
			if (data == null) {
				mPopupWindowDialog.showMessage("��ȡ�ֿ���Ϣʧ��");
				return false;
			}
//			mUpLoadingWarehouseID = Integer.parseInt(data);
			mUpLoadingWarehouseID = data;
			GetUpLoadingWarehouseName();
			
			if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
				mPopupWindowDialog.showMessage("��δ����");
			}
			if (mSigned.equals(Constants.MOBILE_RETREATED)) {
				mPopupWindowDialog.showMessage("�����ֳֻ��˿�");
			}
			if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
				//ֵ�ֲֿ���ж���ֿ��Ƿ�һ��
				if (mWarehouselist.size() == 0) {
					mPopupWindowDialog.showMessage("��ȡֵ�ֲֿ���Ϣʧ��");
					return false;
				}
				if (Integer.valueOf(mUpLoadingWarehouseID) == mWarehouseID) {
					//���ؼ�¼����ëƤ��¼	�α��¼Ϊż��
					String Cursor1 = mRfidAdapter.getCursor1();
					if (Cursor1 == null) {
						return false;
					}
					if (IsPositiveEven(Integer.valueOf(Cursor1))) {
						mBusinessFinishFlag = true;
						mPromptTextView.setText("ҵ������տ��˸ۣ��뱣��Ա���˸ۺ��ջ�ҵ����㿨!");
					}
					//ֻ��ë�ػ���û�г��ؼ�¼  �α��¼Ϊ��������Ϊ0
					if (IsPositiveOddOrZero(Integer.valueOf(Cursor1))) {
						mBusinessFinishFlag = false;
						mPromptTextView.setText("�������˸ۣ�ҵ��δ��ɣ��뱣��Ա��ʵȷ�ϣ���ȷ���˸������˸ۺ��ջؽ��㿨��");
					}
					mRetreatButton.setEnabled(true);
					mRetreatButton.setBackgroundResource(R.drawable.corner_btn_selector);
				}
				else {
					GetUpLoadingWarehouseName();
					mPromptTextView.setText("�ó���Ӧ����"+mUpLoadingWarehouseName+"��ҵ");
				}
			}
			Display();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void ReadCard(){
		//�������IC������Ϣ������ҵ���߼����д���
		if (mRfidAdapter.HasCard() == null) {
			mPopupWindowDialog.showMessage("����ʧ��");
		}
		
		String data = mRfidAdapter.getTagNo();
		if (data == null) {
			mPopupWindowDialog.showMessage("��ȡ���λ��Ϣʧ��");
		}
		
//		mSigned = Integer.parseInt(data);
		mSigned = data;
		
		data = mRfidAdapter.getOperateLink();
		if (data == null) {
			mPopupWindowDialog.showMessage("��ȡ��ҵ������Ϣʧ��");
		}
//		if (!RegistVehicleWorkActivity.IsInitData(data)) {
//			mLink = Integer.parseInt(data);
			mLink = data;
//		}
		
		data = mRfidAdapter.getStorage();
		if (data == null) {
			mPopupWindowDialog.showMessage("��ȡ�ֿ���Ϣʧ��");
		}
//		mUpLoadingWarehouseID = Integer.parseInt(data);
		mUpLoadingWarehouseID = data;
		GetUpLoadingWarehouseName();
		
		if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
			mPopupWindowDialog.showMessage("��δ����");
		}
		if (mSigned.equals(Constants.MOBILE_RETREATED)) {
			mPopupWindowDialog.showMessage("�����ֳֻ��˿�");
		}
		if (mSigned.equals(Constants.CARD_NOT_ISSUED)) {
			//ֵ�ֲֿ���ж���ֿ��Ƿ�һ��
			if (mWarehouselist.size() == 0) {
				mPopupWindowDialog.showMessage("��ȡֵ�ֲֿ���Ϣʧ��");
			}
			if (Integer.valueOf(mUpLoadingWarehouseID) == mWarehouseID) {
				//���ؼ�¼����ëƤ��¼	�α��¼Ϊż��
				String Cursor1 = mRfidAdapter.getCursor1();
				if (Cursor1 == null) {
				}
				if (IsPositiveEven(Integer.valueOf(Cursor1))) {
					mBusinessFinishFlag = true;
					mPromptTextView.setText("ҵ������տ��˸ۣ��뱣��Ա���˸ۺ��ջ�ҵ����㿨!");
				}
				//ֻ��ë�ػ���û�г��ؼ�¼  �α��¼Ϊ��������Ϊ0
				if (IsPositiveOddOrZero(Integer.valueOf(Cursor1))) {
					mBusinessFinishFlag = false;
					mPromptTextView.setText("�������˸ۣ�ҵ��δ��ɣ��뱣��Ա��ʵȷ�ϣ���ȷ���˸������˸ۺ��ջؽ��㿨��");
				}
				mRetreatButton.setEnabled(true);
				mRetreatButton.setBackgroundResource(R.drawable.corner_btn_selector);
			}
			else {
				GetUpLoadingWarehouseName();
				mPromptTextView.setText("�ó���Ӧ����"+mUpLoadingWarehouseName+"��ҵ");
			}
		}
		Display();
	}
	
	private void InitWarehouseName() {
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		
		//�����ݿ��ȡ�������ֺ�
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
			//ҵ���Ƿ����
			if (mBusinessFinishFlag) {
				//�����˸ۣ��˸۱��λ2��д�뿨
				mRfidAdapter.setTagNo("2");
				boolean res1 = SyncBlock.SyncBlock1(mRfidAdapter,RetreatActivity.this);
				int res8 = mRfidAdapter.cleanHYData();
				int res13 = mRfidAdapter.cleanCZData();
				if (res1 && res8 ==0 && res13 ==0) {
					mPopupWindowDialog.showMessage("�����˸۳ɹ�");
				} else {
					mPopupWindowDialog.showMessage("�����˸�ʧ��");
				}
			} else {
				new AlertDialog.Builder(RetreatActivity.this)
				.setTitle("��ʾ")
				.setMessage("��ҵ��δ��ɣ�ȷ��Ҫ�˸���")
				.setPositiveButton("��", new PositiveOnClickListener())
				.setNegativeButton("��", null)
				.show();
			}
		}
	}
	
	class PositiveOnClickListener implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			//�������˸ۣ����λΪ2��д��
			mRfidAdapter.setTagNo("2");
			boolean res1 = SyncBlock.SyncBlock1(mRfidAdapter,RetreatActivity.this);
			int res8 = mRfidAdapter.cleanHYData();
			int res13 = mRfidAdapter.cleanCZData();
			if (res1 && res8 ==0 && res13 ==0) {
				mPopupWindowDialog.showMessage("�������˸۳ɹ�");
			} else {
				mPopupWindowDialog.showMessage("�������˸�ʧ��");
			}
		}
	}
	
	private boolean IsPositiveEven(int num) {	//��ż��
		if(num > 0 && num % 2 == 0){
			return true;
		}
		return false;
	}
	
	private boolean IsPositiveOddOrZero(int num) {	//��������0
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
		
		//����ֻ��ʾëƤ��(����ȷ��)
		ReadWeight();
		CalcuDeduction();
		
		//��ʾ����������Ϣ
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
	 * @description	������ȡ������Ϣ
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
			// entry����������key0=value0��
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
			if (Integer.parseInt(etc.mFur) == 0) {	//Ƥ��
				mTare = etc.mRoughWeight;
			}
			if (Integer.parseInt(etc.mFur) == 1) {	//ë��
				mGrossWeight = etc.mRoughWeight;
			}
			if (Integer.parseInt(etc.mFur) == 2) {	//����
				mNetWeigth = etc.mRoughWeight;
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-11
	 * @description	���������Ϣ������ˮ�ֿ���+�������ʿ���+����ˮ�ֿ���+�������ʿ���
	 *
	 */
	private void CalcuDeduction() {
		String string = null;
		string = mRfidAdapter.getF1();
		if (string == null) {
//			mPopupWindowDialog.showMessage("������ȡ����ˮ�ֿ������λʧ��");
			string = "0";
		}
		mMoistureAmountFlagFirst = Integer.valueOf(string);
		string = mRfidAdapter.getD1();
		if (string == null) {
//			mPopupWindowDialog.showMessage("������ȡ����ˮ�ֿ���ʧ��");
			string = "0";
		}
		if (mMoistureAmountFlagFirst == 0) {	//����
			mMoistureDeductAmountFirst = Double.parseDouble(string);
		} else {	//�ٷֱ�
			mMoistureDeductAmountFirst = Double.parseDouble(mNetWeigth)*Double.parseDouble(string)/100;
		}
		string = mRfidAdapter.getF2();
		if (string == null) {
//			mPopupWindowDialog.showMessage("������ȡ�������ʿ������λʧ��");
			string = "0";
		}
		mImpurityAmountFlagFirst = Integer.valueOf(string);
		string = mRfidAdapter.getD2();
		if (string == null) {
//			mPopupWindowDialog.showMessage("������ȡ�������ʿ���ʧ��");
			string = "0";
		}
		if (mImpurityAmountFlagFirst == 0) {	//����
			mImpurityDeductAmountFirst = Double.parseDouble(string);
		} else {	//�ٷֱ�
			mImpurityDeductAmountFirst = Double.parseDouble(mNetWeigth)*Double.parseDouble(string)/100;
		}
		
		string = mRfidAdapter.getF_1();
		if (string == null) {
//			mPopupWindowDialog.showMessage("������ȡ���ο������λʧ��");
			string = "0";
		}
		mAdjustDeductFlag = Integer.valueOf(string);
		if (mAdjustDeductFlag == 1) {	//�ж��ο���
			string = mRfidAdapter.getF5();
			if (string == null) {
//				mPopupWindowDialog.showMessage("������ȡ����ˮ�ֿ������λʧ��");
				string = "0";
			}
			mMoistureAmountFlagAdjust = Integer.valueOf(string);
			string = mRfidAdapter.getD5();
			if (string == null) {
//				mPopupWindowDialog.showMessage("������ȡ����ˮ�ֿ���λʧ��");
				string = "0";
			}
			if (mMoistureAmountFlagAdjust == 0) {	//����
				mMoistureDeductAmountAdjust = Double.parseDouble(string);
			} else {	//�ٷֱ�
				mMoistureDeductAmountAdjust = Double.parseDouble(mNetWeigth)*Double.parseDouble(string)/100;
			}
			string = mRfidAdapter.getF6();
			if (string == null) {
//				mPopupWindowDialog.showMessage("������ȡ�������ʿ������λʧ��");
				string = "0";
			}
			mImpurityAmountFlagAdjust = Integer.valueOf(string);
			string = mRfidAdapter.getD6();
			if (string == null) {
//				mPopupWindowDialog.showMessage("������ȡ�������ʿ���ʧ��");
				string = "0";
			}
			if (mImpurityAmountFlagAdjust == 0) {	//����
				mImpurityDeductAmountAdjust = Double.parseDouble(string);
			} else {	//�ٷֱ�
				mImpurityDeductAmountAdjust = Double.parseDouble(mNetWeigth)*Double.parseDouble(string)/100;
			}
			 mDeduction = String.valueOf(mMoistureDeductAmountFirst + mImpurityDeductAmountFirst+mMoistureDeductAmountAdjust+mImpurityDeductAmountAdjust);
		} else {	//û�ж��ο���
			 mDeduction = String.valueOf(mMoistureDeductAmountFirst + mImpurityDeductAmountFirst);
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-6-25
	 * @description ���ݶ�������ҵ���ڱ�ţ�������ҵ������ʾ��Ϣ
	 *
	 */
	private void SetLinkTextView() {
		if (IsInitData(mLink)) {
			mLink = "0";
		}
		switch (Integer.parseInt(mLink)) {
		case 0:
			mLinkTextView.setText("δֵ��");
			break;
		case 1:
			mLinkTextView.setText("ֵ��ȷ��");
			break;
		case 2:
			mLinkTextView.setText("ֵ�ֻ���");
			break;
		case 3:
			mLinkTextView.setText("���»���");
			break;
		case 4:
			mLinkTextView.setText("����Ƥ��");
			break;
		case 5:
			mLinkTextView.setText("����ë��");
			break;
		default:
			break;
		}
	}
	
	private void GetUpLoadingWarehouseName() {
		//�ҵ��ֿ�Ŷ�Ӧ�ֿ�����
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
						//û�м�⵽��
						continue;
					} else {
						//��⵽��
						if (mRfidAdapter.HasCard().equals(mCardIDRemember)) {	//�Ѷ���ͬһ�ſ�
							continue;
						} else {	//δ����ͬһ�ſ�
							mCardIDRemember = mRfidAdapter.HasCard();	//���浱ǰ�Ѷ�����
							//��ȡ������Ϣ
							mSigned = mRfidAdapter.getTagNo();
							mLink = mRfidAdapter.getOperateLink();
							mUpLoadingWarehouseID = mRfidAdapter.getStorage();
							mVarieties = mRfidAdapter.getVarietyNo();
							mLicensePlate = mRfidAdapter.getCarNum();
							mOwners = mRfidAdapter.getOwnerName();
							mType = mRfidAdapter.getBusinessType();
							
							ReadWeight();	//����ֻ��ʾëƤ��(����ȷ��)
							CalcuDeduction();
							/**/
							//֪ͨ�������
							msg.what = 0; //��Ϣ0:
							mHandler.sendMessage(msg);
						}
						
					}
					
//					if (msg == null) {
//						msg.what = 0; //��Ϣ0:
//						mHandler.sendMessage(msg);
//					} else {
//						msg.what = 1; //��Ϣ1:�ظ��ǿ�
//						mHandler.sendMessage(msg);
//					}
					
					//���1���ж�һ�ο��Ƿ����
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
						//���½�����Ϣ
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
