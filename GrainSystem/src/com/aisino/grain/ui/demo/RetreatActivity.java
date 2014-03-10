package com.aisino.grain.ui.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.aisino.grain.R;
import com.aisino.grain.model.db.Warehouse;
import com.aisino.grain.model.rfid.RfidAdapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RetreatActivity extends BaseActivity {
	private String mVarieties = null;					//Ʒ��
	private String mLicensePlate = null;				//����
	private String mOwners = null;						//����
	private String mType = null;						//����
	private String mLink = null;						//����
	private String mGrossWeight = null;					//ë��
	private String mTare = null;						//Ƥ��
	private String mNetWeigth = null;					//����
	private String mDeduction = null;					//����
	
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
	private Button mRetreatButton = null;					//�ո��˿���ť
	private TextView mStorehouseWarehouseTextView = null;	//ֵ�ֲֿ�༭��
	private TextView mPromptTextView = null;				//��ʾ�༭��
	
	private String mUpLoadingWarehouseName = null;			//ж���ֿ�����
	
	private List<Warehouse> mWarehouselist = null;				//��ѯ���
	
	private SharedPreferences sharedPreferences = null;			//����ѡ�еĲֿ�
	
	private int mSelectedID = 0;								//�ֿ��б�ѡ��ID
	
	private RfidAdapter mRfidAdapter = null;					//rfid����������
	
	private Random mRandom = null;
	
	public static String[] GrainKind = {"����","�̵�","����","����","��","����"};
	public static String[] Owner = {"����ʡ��ʳ��","������ʳ��","�д���","������ʳ��"};
	public static String[] GrainAttribute = {"��Ʒ��","�д���","ʡ����","���봢����","����","����չ���"};
	public static String[] LicensePlate = {"��A-00001","��V-02009","��B-12345","��C-88888","��D-66666"};
	public static String[] Owners = {"Aisino","������Ϣ","����","����","����","���Ӳ�Ʒ��"};
	public static String[] Type = {"�չ����","���۳���"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_retreat);
		super.onCreate(savedInstanceState);
		InitCtrl();
	}
	
	private void InitCtrl(){
		mRfidAdapter = RfidAdapter.getInstance(this);
		
		sharedPreferences = getSharedPreferences("WarehouseInfo_Demo", Context.MODE_PRIVATE);
		
		mWarehouselist = new ArrayList<Warehouse>();
		
		//��
		mReadCardButton = (Button)findViewById(R.id.retreat_btn_read_card);
		mRetreatButton = (Button)findViewById(R.id.retreat_btn_retreat);
		mRetreatButton.setEnabled(false);
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
			mUpLoadingWarehouseName = mWarehouselist.get(mSelectedID).getWarehouse_name();
			mRandom = new Random();
			mVarieties = GrainKind[mRandom.nextInt(5)];
			mRandom = new Random();
			mLicensePlate = LicensePlate[mRandom.nextInt(4)];
			mRandom = new Random();
			mOwners = Owner[mRandom.nextInt(3)];
			mRandom = new Random();
			mType = Type[mRandom.nextInt(1)];
			mLink = "����Ƥ��";
			mRandom = new Random();
			mGrossWeight = String.valueOf(mRandom.nextInt(2000)+2000);
			mRandom = new Random();
			mTare = String.valueOf(mRandom.nextInt(2000));
			mNetWeigth = String.valueOf(Integer.parseInt(mGrossWeight)-Integer.parseInt(mTare));
			mDeduction = String.valueOf(mRandom.nextInt(500));
			
			mUploadingWarehousTextView.setText(mUpLoadingWarehouseName);
			mVarietiesTextView.setText(mVarieties);
			mLicensePlateTextView.setText(mLicensePlate);
			mOwnersTextView.setText(mOwners);
			mTypeTextView.setText(mType);
			mLinkTextView.setText(mLink);
			mGrossWeightTextView.setText(String.valueOf(mGrossWeight));
			mTareTextView.setText(String.valueOf(mTare));
			mNetWeigthTextView.setText(String.valueOf(mNetWeigth));
			mDeductionTextView.setText(String.valueOf(mDeduction));
			mPromptTextView.setText("ҵ������տ��˸ۣ��뱣��Ա���˸ۺ��ջ�ҵ����㿨!");
			mRetreatButton.setEnabled(true);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void ReadCard(){
		//�������IC������Ϣ������ҵ���߼����д���
		if (mRfidAdapter.HasCard() == null) {
			mPopupWindowDialog.showMessage("����ʧ��");
			return;
		}
		mUpLoadingWarehouseName = mWarehouselist.get(mSelectedID).getWarehouse_name();
		mRandom = new Random();
		mVarieties = GrainKind[mRandom.nextInt(5)];
		mRandom = new Random();
		mLicensePlate = LicensePlate[mRandom.nextInt(4)];
		mRandom = new Random();
		mOwners = Owner[mRandom.nextInt(3)];
		mRandom = new Random();
		mType = Type[mRandom.nextInt(1)];
		mLink = "����Ƥ��";
		mRandom = new Random();
		mGrossWeight = String.valueOf(mRandom.nextInt(2000)+2000);
		mRandom = new Random();
		mTare = String.valueOf(mRandom.nextInt(2000));
		mNetWeigth = String.valueOf(Integer.parseInt(mGrossWeight)-Integer.parseInt(mTare));
		mDeduction = String.valueOf(mRandom.nextInt(500));
		
		mUploadingWarehousTextView.setText(mUpLoadingWarehouseName);
		mVarietiesTextView.setText(mVarieties);
		mLicensePlateTextView.setText(mLicensePlate);
		mOwnersTextView.setText(mOwners);
		mTypeTextView.setText(mType);
		mLinkTextView.setText(mLink);
		mGrossWeightTextView.setText(String.valueOf(mGrossWeight));
		mTareTextView.setText(String.valueOf(mTare));
		mNetWeigthTextView.setText(String.valueOf(mNetWeigth));
		mDeductionTextView.setText(String.valueOf(mDeduction));
		mPromptTextView.setText("ҵ������տ��˸ۣ��뱣��Ա���˸ۺ��ջ�ҵ����㿨!");
		mRetreatButton.setEnabled(true);
	}
	
	private void InitWarehouseName() {
		for (int i = 0;i < 5;i++) {
			Warehouse warehouse = new Warehouse();
			warehouse.setWarehouse_id(i);
			warehouse.setWarehouse_name(i+"�Ųֿ�");
			mWarehouselist.add(warehouse);
		}
		mSelectedID = sharedPreferences.getInt("SelectedID", 0);
		mStorehouseWarehouseTextView.setText(mWarehouselist.get(mSelectedID).getWarehouse_name());
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
			mPopupWindowDialog.showMessage("�˸۳ɹ�");
		}
	}
}
