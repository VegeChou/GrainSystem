package com.aisino.grain.ui.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.aisino.grain.R;
import com.aisino.grain.model.db.Warehouse;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class WarehouseInfoActivity extends BaseActivity {
//	private static final String TAG = "WarehouseInfoActivity";
	private int WAREHOUSE_EMPTY_MSG = 1;
	private int WAREHOUSE_FULL_MSG = 2;
	
	private Context mContext = null;
	
	//��ʾ�༭��
	private TextView mCapacityTextView = null;					//������ʾ��
	private TextView mGrainKindTextView = null;					//Ʒ����ʾ��
	private TextView mTotalQuantityTextView = null;				//�����ʾ��
	private TextView mOwnerTextView = null;						//������ʾ��
	private TextView mGrainAttributeTextView = null;			//������ʾ��
	
	private String mCapacity = null;							//����
	private String mGrainKind = null;							//Ʒ��
	private String mTotalQuantity = null;						//���
	private String mOwner = null;								//����
	private String mGrainAttribute = null;						//����
	
	private int mWarehouseStatusCheckedId = 0;					//RadioGroupѡ��ID
	
	//ֵ�ֲֿ��б�
	private Spinner mSpinnerWarehouse = null;
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();
	private int mSelectedID = 0;								//�ֿ��б�ѡ��ID
	
	private Button mQuertButton = null;							//��ѯ��ť
	private Button mSetButton = null;							//���ð�ť
	
	//���ݿ�
	private List<Warehouse> mWarehouselist = null;				//����б�
	
	//������Ϣ
	private SharedPreferences sharedPreferences = null;			//����ѡ�еĲֿ�
	private Editor editor = null;								//SharedPreferences�༭��
	
	private Random mRandom = null;
	public static String[] GrainKind = {"����","�̵�","����","����","��","����"};
	public static String[] Owner = {"����ʡ��ʳ��","������ʳ��","�д���","������ʳ��"};
	public static String[] GrainAttribute = {"��Ʒ��","�д���","ʡ����","���봢����","����","����չ���"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_warehouse_info);
		super.onCreate(savedInstanceState);
		InitCtrl();
	}

	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	��ʼ������
	 *
	 */
	private void InitCtrl(){
		mContext = this;
		sharedPreferences = getSharedPreferences("WarehouseInfo_Demo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//��ȡ�༭��
		
		mWarehouselist = new ArrayList<Warehouse>();
		
		mCapacityTextView = (TextView)findViewById(R.id.warehouse_info_tv_capacity);
		mGrainKindTextView = (TextView)findViewById(R.id.warehouse_info_tv_grain_kind);
		mTotalQuantityTextView = (TextView)findViewById(R.id.warehouse_info_tv_total_quantity);
		mOwnerTextView = (TextView)findViewById(R.id.warehouse_info_tv_owner);
		mGrainAttributeTextView = (TextView)findViewById(R.id.warehouse_info_tv_grain_attribute);
		
		mQuertButton = (Button)findViewById(R.id.warehouse_info_btn_query);
		mSetButton = (Button)findViewById(R.id.warehouse_info_btn_set);
		
		mSpinnerWarehouse = (Spinner)findViewById(R.id.warehouse_info_sp_storehouse_warehouse);
		
		mQuertButton.setOnClickListener(new QueryOnClickListener());
		mSetButton.setOnClickListener(new SetOnClickListener());
		
		//��ʼ��ֵ�ֲֿ������˵�
		InitSnipperWarehouse();
		
		//��������Ϣ������Ĭ�ϲֿ�ѡ����
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
		
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	��ʼ��ֵ�ֲֿ��б����ݵ�ǰ��¼�û�����ֵ��Ȩ��id����ѯ���ݿ��Ӧid�Ĳֿ����ƣ���ʾ���б���
	 *
	 */
	private void InitSnipperWarehouse(){
		for (int i = 0;i < 5;i++) {
			Warehouse warehouse = new Warehouse();
			warehouse.setWarehouse_id(i);
			warehouse.setWarehouse_name(i+"�Ųֿ�");
			mWarehouselist.add(warehouse);
		}
		if (mWarehouselist.size()> 0) {
			for (int j = 0; j < mWarehouselist.size(); j++) {
				listSpinnerWarehouse.add(mWarehouselist.get(j).getWarehouse_name());
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
	 * @date 2013-7-12
	 * @description	ֵ�ֲֿ��б�ѡ��ĳһ�ֿ⣬���������ѯ�òֿ���Ϣ����ʾ�����浱ǰѡ�вֿ�
	 *
	 */
	class SpinnerWarehouseOnItemSelectedListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			int selectedID = parent.getSelectedItemPosition();
			mSelectedID = selectedID;
			editor.putInt("SelectedID", selectedID);
			editor.commit();
			
			GetWarehouseInfo();
			
		}
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	
	 *
	 */
	class SetOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			String title = "ֵ�ֲֿ�:"+mWarehouselist.get(mSelectedID).getWarehouse_name();
			new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.CornerAlertDialog))  
			.setTitle(title)  
			.setSingleChoiceItems(new String[] {getString(R.string.warehouse_empty),getString(R.string.warehouse_full)}, 0,new ResultChoiceOnClickListener())
			.setPositiveButton(getString(R.string.send), new SendOnClickListener())                  
			.setNegativeButton(getString(R.string.Cancel), null)  
			.show();  
		}	
	}
	
	class SendOnClickListener implements DialogInterface.OnClickListener{
		public void onClick(DialogInterface dialog, int which) {
			if (mWarehouseStatusCheckedId == 0) {
				mPopupWindowDialog.showMessage("��ѡ��ֵ����Ϣ����");
				return;
			}
			if (mWarehouseStatusCheckedId == WAREHOUSE_FULL_MSG) {
				mPopupWindowDialog.showMessage("�ֿ�������Ϣ���ͳɹ�");
			}
			if (mWarehouseStatusCheckedId == WAREHOUSE_EMPTY_MSG) {
				mPopupWindowDialog.showMessage("�ֿ��ѿ���Ϣ���ͳɹ�");
			}
		}
	}
	
	class ResultChoiceOnClickListener implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				mWarehouseStatusCheckedId = WAREHOUSE_EMPTY_MSG;	//�ֿ��ѿ�
				break;
			case 1:
				mWarehouseStatusCheckedId = WAREHOUSE_FULL_MSG;	//�ֿ�����
				break;
			default:
				break;
			}
		}
	}
			
	class QueryOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(WarehouseInfoActivity.this, WarehouseInfoQueryActivity.class);
			startActivity(intent);
		}	
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	��ȡ�ֿ���Ϣ��ͨ��WebService��ѯ�ֿ���Ϣ
	 *
	 */
	private void GetWarehouseInfo() {
		mRandom = new Random();
		mCapacity = String.valueOf((mSelectedID+1)*(mRandom.nextInt(9)+1)*100);
		mRandom = new Random();
		mGrainKind = GrainKind[mRandom.nextInt(5)];
		mRandom = new Random();
		mTotalQuantity = String.valueOf(mSelectedID*mRandom.nextInt(10)*10);
		mRandom = new Random();
		mOwner = Owner[mRandom.nextInt(4)];
		mRandom = new Random();
		mGrainAttribute = GrainAttribute[mRandom.nextInt(5)];
		//��Ʒ��  �д���  ʡ����  ���봢����  ����   ����չ���
		//����ʡ��ʳ��  ������ʳ��  ������ʳ��  �д���
		
		mCapacityTextView.setText(mCapacity);
		mGrainKindTextView.setText(mGrainKind);
		mTotalQuantityTextView.setText(mTotalQuantity);
		mOwnerTextView.setText(mOwner);
		mGrainAttributeTextView.setText(mGrainAttribute);
		
	}
	
	@Override
	protected void onResume() {
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
		super.onResume();
	}
}

