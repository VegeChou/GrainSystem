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
	
	//显示编辑框
	private TextView mCapacityTextView = null;					//仓容显示框
	private TextView mGrainKindTextView = null;					//品种显示框
	private TextView mTotalQuantityTextView = null;				//库存显示框
	private TextView mOwnerTextView = null;						//所属显示框
	private TextView mGrainAttributeTextView = null;			//性质显示框
	
	private String mCapacity = null;							//仓容
	private String mGrainKind = null;							//品种
	private String mTotalQuantity = null;						//库存
	private String mOwner = null;								//所属
	private String mGrainAttribute = null;						//性质
	
	private int mWarehouseStatusCheckedId = 0;					//RadioGroup选中ID
	
	//值仓仓库列表
	private Spinner mSpinnerWarehouse = null;
	private List<String> listSpinnerWarehouse =  new ArrayList<String>();
	private int mSelectedID = 0;								//仓库列表选中ID
	
	private Button mQuertButton = null;							//查询按钮
	private Button mSetButton = null;							//设置按钮
	
	//数据库
	private List<Warehouse> mWarehouselist = null;				//结果列表
	
	//配置信息
	private SharedPreferences sharedPreferences = null;			//保存选中的仓库
	private Editor editor = null;								//SharedPreferences编辑器
	
	private Random mRandom = null;
	public static String[] GrainKind = {"粳稻","籼稻","白麦","玉米","大豆","大米"};
	public static String[] Owner = {"江苏省粮食局","无锡粮食局","中储粮","苏州粮食局"};
	public static String[] GrainAttribute = {"商品粮","市储粮","省储粮","中央储备粮","代储","最低收购价"};
	
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
		sharedPreferences = getSharedPreferences("WarehouseInfo_Demo", Context.MODE_PRIVATE);
		editor = sharedPreferences.edit();//获取编辑器
		
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
		
		//读配置信息，设置默认仓库选中项
		int selectedid = -1;
		selectedid = sharedPreferences.getInt("SelectedID", mSelectedID);
		mSpinnerWarehouse.setSelection(selectedid);
		
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	初始化值仓仓库列表：根据当前登录用户所能值仓权限id，查询数据库对应id的仓库名称，显示到列表中
	 *
	 */
	private void InitSnipperWarehouse(){
		for (int i = 0;i < 5;i++) {
			Warehouse warehouse = new Warehouse();
			warehouse.setWarehouse_id(i);
			warehouse.setWarehouse_name(i+"号仓库");
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
        
        //设置标题   
        mSpinnerWarehouse.setPrompt("请选择值仓仓库"); 
        mSpinnerWarehouse.setOnItemSelectedListener(new SpinnerWarehouseOnItemSelectedListener());
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
			if (mWarehouseStatusCheckedId == WAREHOUSE_FULL_MSG) {
				mPopupWindowDialog.showMessage("仓库已满消息发送成功");
			}
			if (mWarehouseStatusCheckedId == WAREHOUSE_EMPTY_MSG) {
				mPopupWindowDialog.showMessage("仓库已空消息发送成功");
			}
		}
	}
	
	class ResultChoiceOnClickListener implements DialogInterface.OnClickListener{
		@Override
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case 0:
				mWarehouseStatusCheckedId = WAREHOUSE_EMPTY_MSG;	//仓库已空
				break;
			case 1:
				mWarehouseStatusCheckedId = WAREHOUSE_FULL_MSG;	//仓库已满
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
	 * @description	获取仓库信息：通过WebService查询仓库信息
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
		//商品粮  市储粮  省储粮  中央储备粮  代储   最低收购价
		//江苏省粮食局  无锡粮食局  苏州粮食局  中储粮
		
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

