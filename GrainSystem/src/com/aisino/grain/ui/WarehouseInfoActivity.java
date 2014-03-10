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
	//显示编辑框
	private TextView mCapacityTextView = null;					//仓容显示框
	private TextView mGrainKindTextView = null;					//品种显示框
	private TextView mTotalQuantityTextView = null;				//库存显示框
	private TextView mOwnerTextView = null;						//所属显示框
	private TextView mGrainAttributeTextView = null;			//性质显示框
	
	private int mWarehouseStatusCheckedId = 0;					//RadioGroup选中ID
	
	private Handler mHandler = null;							//延迟调用业务程序Handler
	
	//值仓仓库列表
	private Spinner mSpinnerWarehouse = null;
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();
	private int mSelectedID = 0;								//仓库列表选中ID
	
	private Button mQuertButton = null;							//查询按钮
	private Button mSetButton = null;							//设置按钮
	
	//webservice
	private RestWebServiceAdapter mRestWebServiceAdapter = null;	//webservice
	private WareHouseInfoResponse mWareHouseInfoResponse = null;	//webservice仓库信息回复
	private WareHouseInfo mWareHouseInfo = null;
	
	private CommitWareHouseStateRequest mCommitWareHouseStateRequest = null;	//webservice提交仓库状态请求
	private CommitWareHouseStateResponse mCommitWareHouseStateResponse = null;	//webservice提交仓库状态回复
	
	public static int mWarehouseID = 0;								//仓库ID
	private int mInOutFlag = 0;										//出入库标志，默认为入库(0入1出)
	private int mInOutLength = 4;									//返回逐日汇总记录的条数，默认为4
	private int mPage = 1;											//页号
	
	//数据库
	private Dao<Warehouse, Integer> mWarehouseDao =  null;				//仓库信息Dao
	private List<Warehouse> mWarehouselist = null;						//查询结果
	
	//配置信息
	private SharedPreferences sharedPreferences = null;			//保存选中的仓库
	private Editor editor = null;								//SharedPreferences编辑器
	private SharedPreferences mLoginSharedPreferences = null;	//保存登录信息
	
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
	 * @description	初始化函数
	 *
	 */
	private void InitCtrl(){
		mContext = this;
		
		mRestWebServiceAdapter = RestWebServiceAdapter.getInstance(mContext);
		
		sharedPreferences = getSharedPreferences("WarehouseInfo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//获取编辑器
		
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
		
		//初始化值仓仓库下拉菜单
		InitSnipperWarehouse();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	初始化值仓仓库列表：根据当前登录用户所能值仓权限id，查询数据库对应id的仓库名称，显示到列表中
	 *
	 */
	private void InitSnipperWarehouse(){
		//从数据库获取数据粮仓号,根据登录信息，显示值仓仓库列表
		
		//获取登录用户能值仓id串
		String warehouse_id = null;
		warehouse_id = mLoginSharedPreferences.getString("warehouse_id", "null");
		if (warehouse_id.equals("null")) {
//			mPopupWindowDialog.showMessage("仓库列表为空");
			return;
		}
		//查询能值仓id串对应的仓库名称
		try {
			GenericRawResults<Object[]> rawResults = mWarehouseDao.queryRaw("select warehouse_id,warehouse_name from Warehouse where warehouse_id in ("+warehouse_id+")",new DataType[] { DataType.INTEGER,DataType.STRING });
			List<Object[]> resultsList = rawResults.getResults();
			if (resultsList.size() == 0) {
//				mPopupWindowDialog.showMessage("值仓仓库列表为空");
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
        
        //设置标题   
        mSpinnerWarehouse.setPrompt("请选择值仓仓库"); 
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
				mPopupWindowDialog.showMessage("仓库信息为空");
				return;
			}
			String title = "值仓仓库:"+mWarehouselist.get(mSelectedID).getWarehouse_name();
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
				mPopupWindowDialog.showMessage("请选择值仓消息类型");
				return;
			}
			if (mWarehouseStatusCheckedId == Constants.WAREHOUSE_FULL_MSG) {
				//通过webservice发送仓库已满消息
				CreateFullWarehouseMessage();
				
				mCommitWareHouseStateResponse = (CommitWareHouseStateResponse)mRestWebServiceAdapter.Rest(mCommitWareHouseStateRequest);
				if (mCommitWareHouseStateResponse == null) {
					mPopupWindowDialog.showMessage("仓库满仓消息发送失败");
					return;
				}
				boolean res = mCommitWareHouseStateResponse.getResponseResult();
				if (res) {
					mPopupWindowDialog.showMessage("仓库满仓消息发送成功");
				} else {
					mPopupWindowDialog.showMessage(mCommitWareHouseStateResponse.getFailedReason());
				}
			}
			if (mWarehouseStatusCheckedId == Constants.WAREHOUSE_EMPTY_MSG) {
				CreateEmptyWarehouseMessage();
				mCommitWareHouseStateResponse = (CommitWareHouseStateResponse)mRestWebServiceAdapter.Rest(mCommitWareHouseStateRequest);
				if (mCommitWareHouseStateResponse == null) {
					mPopupWindowDialog.showMessage("仓库空仓消息发送失败");
					return;
				}
				boolean res = mCommitWareHouseStateResponse.getResponseResult();
				if (res) {
					mPopupWindowDialog.showMessage("仓库空仓消息发送成功");
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
				mWarehouseStatusCheckedId = Constants.WAREHOUSE_EMPTY_MSG;	//仓库已空
				break;
			case 1:
				mWarehouseStatusCheckedId = Constants.WAREHOUSE_FULL_MSG;	//仓库已满
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
	 * @description	值仓仓库列表：选中某一仓库，向服务器查询该仓库信息并显示，保存当前选中仓库
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
				mProcessDialog = WaitDialog.createLoadingDialog(mContext, "正在获取中......");
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
	 * @description	获取仓库信息：通过WebService查询仓库信息
	 *
	 */
	private void GetWarehouseInfo(){
		mCapacityTextView.setText("");
		mGrainKindTextView.setText("");
		mTotalQuantityTextView.setText("");
		mOwnerTextView.setText("");
		mGrainAttributeTextView.setText("");
		
		
		//装载WareHouseInfoRequest对象
		GetWareHouseInfoRequest wareHouseInfoRequest = null;		//webservice仓库信息请求
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
		
		mCapacityTextView.setText(String.valueOf(Double.valueOf(mWareHouseInfo.getCapacity())/1000));	//显示为吨
		if (mWareHouseInfo.getGrainTypeName() != null) {
			mGrainKindTextView.setText(mWareHouseInfo.getGrainTypeName());
		}
		if (mWareHouseInfo.getTotalWeight() != null) {
			mTotalQuantityTextView.setText(String.valueOf(Double.valueOf(mWareHouseInfo.getTotalWeight())/1000));	//显示为吨
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
	 * @description	构造仓库满仓消息
	 *
	 */
	private void CreateFullWarehouseMessage() {
		mCommitWareHouseStateRequest = new CommitWareHouseStateRequest();
		mCommitWareHouseStateRequest.setWareHouseID(mWarehouseID);
		mCommitWareHouseStateRequest.setWareHouseState(0);	//仓库满仓
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	构造仓库空仓消息
	 *
	 */
	private void CreateEmptyWarehouseMessage() {
		mCommitWareHouseStateRequest = new CommitWareHouseStateRequest();
		mCommitWareHouseStateRequest.setWareHouseID(mWarehouseID);
		mCommitWareHouseStateRequest.setWareHouseState(1);	//仓库空仓
	}
	
	@Override
	protected void onDestroy() {
//		LoginActivity.ActivityList.remove(this);
		
		super.onDestroy();
	}
}

