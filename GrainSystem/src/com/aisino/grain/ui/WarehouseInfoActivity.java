package com.aisino.grain.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.aisino.grain.R;
import com.aisino.grain.beans.CommitWareHouseStateRequest;
import com.aisino.grain.beans.CommitWareHouseStateResponse;
import com.aisino.grain.beans.GetWareHouseInfoRequest;
import com.aisino.grain.beans.WareHouseInfo;
import com.aisino.grain.beans.WareHouseInfoResponse;
import com.aisino.grain.model.Constants;
import com.aisino.grain.model.db.Warehouse;
import com.aisino.grain.model.rest.RestWebServiceAdapter;
import com.aisino.grain.ui.util.WaitDialog;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.field.DataType;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class WarehouseInfoActivity extends CustomMenuActivity {
//	private static final String TAG = "WarehouseInfoActivity";
	private Context mContext = null;
	
	//UI
	//��ʾ�༭��
	private TextView mCapacityTextView = null;					//������ʾ��
	private TextView mGrainKindTextView = null;					//Ʒ����ʾ��
	private TextView mTotalQuantityTextView = null;				//�����ʾ��
	private TextView mOwnerTextView = null;						//������ʾ��
	private TextView mGrainAttributeTextView = null;			//������ʾ��
	
	private int mWarehouseStatusCheckedId = 0;					//RadioGroupѡ��ID
	
	private Handler mHandler = null;							//�ӳٵ���ҵ�����Handler
	
	//ֵ�ֲֿ��б�
	private Spinner mSpinnerWarehouse = null;
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();
	private int mSelectedID = 0;								//�ֿ��б�ѡ��ID
	
	private Button mQuertButton = null;							//��ѯ��ť
	private Button mSetButton = null;							//���ð�ť
	
	//webservice
	private RestWebServiceAdapter mRestWebServiceAdapter = null;	//webservice
	private WareHouseInfoResponse mWareHouseInfoResponse = null;	//webservice�ֿ���Ϣ�ظ�
	private WareHouseInfo mWareHouseInfo = null;
	
	private CommitWareHouseStateRequest mCommitWareHouseStateRequest = null;	//webservice�ύ�ֿ�״̬����
	private CommitWareHouseStateResponse mCommitWareHouseStateResponse = null;	//webservice�ύ�ֿ�״̬�ظ�
	
	public static int mWarehouseID = 0;								//�ֿ�ID
	private int mInOutFlag = 0;										//������־��Ĭ��Ϊ���(0��1��)
	private int mInOutLength = 4;									//�������ջ��ܼ�¼��������Ĭ��Ϊ4
	private int mPage = 1;											//ҳ��
	
	//���ݿ�
	private Dao<Warehouse, Integer> mWarehouseDao =  null;				//�ֿ���ϢDao
	private List<Warehouse> mWarehouselist = null;						//��ѯ���
	
	//������Ϣ
	private SharedPreferences sharedPreferences = null;			//����ѡ�еĲֿ�
	private Editor editor = null;								//SharedPreferences�༭��
	private SharedPreferences mLoginSharedPreferences = null;	//�����¼��Ϣ
	
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
		
		mRestWebServiceAdapter = RestWebServiceAdapter.getInstance(mContext);
		
		sharedPreferences = getSharedPreferences("WarehouseInfo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//��ȡ�༭��
		
		mLoginSharedPreferences = getSharedPreferences("LoginInformation", Context.MODE_PRIVATE);
		
		mWarehouseDao = getHelper().getWarehouseDao();
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
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	��ʼ��ֵ�ֲֿ��б����ݵ�ǰ��¼�û�����ֵ��Ȩ��id����ѯ���ݿ��Ӧid�Ĳֿ����ƣ���ʾ���б���
	 *
	 */
	private void InitSnipperWarehouse(){
		//�����ݿ��ȡ�������ֺ�,���ݵ�¼��Ϣ����ʾֵ�ֲֿ��б�
		
		//��ȡ��¼�û���ֵ��id��
		String warehouse_id = null;
		warehouse_id = mLoginSharedPreferences.getString("warehouse_id", "null");
		if (warehouse_id.equals("null")) {
//			mPopupWindowDialog.showMessage("�ֿ��б�Ϊ��");
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
			Toast.makeText(getApplicationContext(), getString(R.string.warehouse_exception), Toast.LENGTH_LONG).show();
			return;
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
	 * @date 2013-7-12
	 * @description	
	 *
	 */
	class SetOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (mWarehouselist.size() == 0) {
				mPopupWindowDialog.showMessage("�ֿ���ϢΪ��");
				return;
			}
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
			if (mWarehouseStatusCheckedId == Constants.WAREHOUSE_FULL_MSG) {
				//ͨ��webservice���Ͳֿ�������Ϣ
				CreateFullWarehouseMessage();
				
				mCommitWareHouseStateResponse = (CommitWareHouseStateResponse)mRestWebServiceAdapter.Rest(mCommitWareHouseStateRequest);
				if (mCommitWareHouseStateResponse == null) {
					mPopupWindowDialog.showMessage("�ֿ�������Ϣ����ʧ��");
					return;
				}
				boolean res = mCommitWareHouseStateResponse.getResponseResult();
				if (res) {
					mPopupWindowDialog.showMessage("�ֿ�������Ϣ���ͳɹ�");
				} else {
					mPopupWindowDialog.showMessage(mCommitWareHouseStateResponse.getFailedReason());
				}
			}
			if (mWarehouseStatusCheckedId == Constants.WAREHOUSE_EMPTY_MSG) {
				CreateEmptyWarehouseMessage();
				mCommitWareHouseStateResponse = (CommitWareHouseStateResponse)mRestWebServiceAdapter.Rest(mCommitWareHouseStateRequest);
				if (mCommitWareHouseStateResponse == null) {
					mPopupWindowDialog.showMessage("�ֿ�ղ���Ϣ����ʧ��");
					return;
				}
				boolean res = mCommitWareHouseStateResponse.getResponseResult();
				if (res) {
					mPopupWindowDialog.showMessage("�ֿ�ղ���Ϣ���ͳɹ�");
				} else {
					mPopupWindowDialog.showMessage(mCommitWareHouseStateResponse.getFailedReason());
				}
			}
		}
	}
	
	class ResultChoiceOnClickListener implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				mWarehouseStatusCheckedId = Constants.WAREHOUSE_EMPTY_MSG;	//�ֿ��ѿ�
				break;
			case 1:
				mWarehouseStatusCheckedId = Constants.WAREHOUSE_FULL_MSG;	//�ֿ�����
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
	 * @description	ֵ�ֲֿ��б�ѡ��ĳһ�ֿ⣬���������ѯ�òֿ���Ϣ����ʾ�����浱ǰѡ�вֿ�
	 *
	 */
	class SpinnerWarehouseOnItemSelectedListener implements OnItemSelectedListener{
		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			int selectedID = parent.getSelectedItemPosition();
			mWarehouseID = mWarehouselist.get(selectedID).getWarehouse_id();
			mSelectedID = selectedID;
			editor.putInt("SelectedID", selectedID);
			editor.commit();
			
			if (mProcessDialog == null) {
				mProcessDialog = WaitDialog.createLoadingDialog(mContext, "���ڻ�ȡ��......");
			}
			mProcessDialog.show();
			
			Runnable getRunnable = new Runnable() {
				@Override
				public void run() {
					GetWarehouseInfo();
				}
			};
			mHandler.post(getRunnable);
		}
		public void onNothingSelected(AdapterView<?> parent) {
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @throws SQLException 
	 * @date 2013-7-12
	 * @description	��ȡ�ֿ���Ϣ��ͨ��WebService��ѯ�ֿ���Ϣ
	 *
	 */
	private void GetWarehouseInfo(){
		mCapacityTextView.setText("");
		mGrainKindTextView.setText("");
		mTotalQuantityTextView.setText("");
		mOwnerTextView.setText("");
		mGrainAttributeTextView.setText("");
		
		
		//װ��WareHouseInfoRequest����
		GetWareHouseInfoRequest wareHouseInfoRequest = null;		//webservice�ֿ���Ϣ����
		wareHouseInfoRequest = new GetWareHouseInfoRequest();
		wareHouseInfoRequest.setWareHouseID(mWarehouseID);
		wareHouseInfoRequest.setSearchFlag(mInOutFlag);
		wareHouseInfoRequest.setSearchLength(mInOutLength);
		wareHouseInfoRequest.setPage(mPage);
		
		mWareHouseInfoResponse = (WareHouseInfoResponse)mRestWebServiceAdapter.Rest(wareHouseInfoRequest);	
		
		if (mWareHouseInfoResponse == null) {
			mProcessDialog.cancel();
			return;
		}
		mWareHouseInfo = mWareHouseInfoResponse.getResultWareHouseInfo();
		
		mCapacityTextView.setText(String.valueOf(Double.valueOf(mWareHouseInfo.getCapacity())/1000));	//��ʾΪ��
		if (mWareHouseInfo.getGrainTypeName() != null) {
			mGrainKindTextView.setText(mWareHouseInfo.getGrainTypeName());
		}
		if (mWareHouseInfo.getTotalWeight() != null) {
			mTotalQuantityTextView.setText(String.valueOf(Double.valueOf(mWareHouseInfo.getTotalWeight())/1000));	//��ʾΪ��
		}
		if (mWareHouseInfo.getGrainOwner() != null) {
			mOwnerTextView.setText(mWareHouseInfo.getGrainOwner());
		}
		if (mWareHouseInfo.getGrainAttribute() != null) {
			mGrainAttributeTextView.setText(mWareHouseInfo.getGrainAttribute());
		}
		mProcessDialog.cancel();
	}
	
	@Override
	protected void onResume() {
		Runnable startRunnable = new Runnable() {
			@Override
			public void run() {
				int selectedid = -1;
				selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
				mSpinnerWarehouse.setSelection(selectedid);
			}
		};
		
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		};
		mHandler.post(startRunnable);
		super.onResume();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	����ֿ�������Ϣ
	 *
	 */
	private void CreateFullWarehouseMessage() {
		mCommitWareHouseStateRequest = new CommitWareHouseStateRequest();
		mCommitWareHouseStateRequest.setWareHouseID(mWarehouseID);
		mCommitWareHouseStateRequest.setWareHouseState(0);	//�ֿ�����
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	����ֿ�ղ���Ϣ
	 *
	 */
	private void CreateEmptyWarehouseMessage() {
		mCommitWareHouseStateRequest = new CommitWareHouseStateRequest();
		mCommitWareHouseStateRequest.setWareHouseID(mWarehouseID);
		mCommitWareHouseStateRequest.setWareHouseState(1);	//�ֿ�ղ�
	}
	
	@Override
	protected void onDestroy() {
//		LoginActivity.ActivityList.remove(this);
		
		super.onDestroy();
	}
}

