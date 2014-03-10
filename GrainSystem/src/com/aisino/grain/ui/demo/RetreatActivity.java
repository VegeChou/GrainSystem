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
	private String mVarieties = null;					//品种
	private String mLicensePlate = null;				//车牌
	private String mOwners = null;						//货主
	private String mType = null;						//类型
	private String mLink = null;						//环节
	private String mGrossWeight = null;					//毛重
	private String mTare = null;						//皮重
	private String mNetWeigth = null;					//净重
	private String mDeduction = null;					//扣量
	
	private TextView mVarietiesTextView = null;			//品种编辑框
	private TextView mLicensePlateTextView = null;		//车牌编辑框
	private TextView mOwnersTextView = null;			//货主编辑框
	private TextView mTypeTextView = null;				//类型编辑框
	private TextView mLinkTextView= null;				//环节编辑框
	private TextView mGrossWeightTextView = null;		//毛重编辑框
	private TextView mTareTextView = null;				//皮重编辑框
	private TextView mNetWeigthTextView = null;			//净重编辑框
	private TextView mDeductionTextView = null;			//扣量编辑框
	private TextView mUploadingWarehousTextView = null;	//卸粮仓库编辑框
	
	private Button mReadCardButton = null;					//读卡按钮
	private Button mRetreatButton = null;					//收港退卡按钮
	private TextView mStorehouseWarehouseTextView = null;	//值仓仓库编辑框
	private TextView mPromptTextView = null;				//提示编辑框
	
	private String mUpLoadingWarehouseName = null;			//卸粮仓库名称
	
	private List<Warehouse> mWarehouselist = null;				//查询结果
	
	private SharedPreferences sharedPreferences = null;			//保存选中的仓库
	
	private int mSelectedID = 0;								//仓库列表选中ID
	
	private RfidAdapter mRfidAdapter = null;					//rfid读卡适配器
	
	private Random mRandom = null;
	
	public static String[] GrainKind = {"粳稻","籼稻","白麦","玉米","大豆","大米"};
	public static String[] Owner = {"江苏省粮食局","无锡粮食局","中储粮","苏州粮食局"};
	public static String[] GrainAttribute = {"商品粮","市储粮","省储粮","中央储备粮","代储","最低收购价"};
	public static String[] LicensePlate = {"京A-00001","京V-02009","京B-12345","京C-88888","京D-66666"};
	public static String[] Owners = {"Aisino","航天信息","张三","李四","王五","电子产品部"};
	public static String[] Type = {"收购入库","销售出库"};
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
		
		//绑定
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
		
		//读配置信息，设置 选中仓库
		InitWarehouseName();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_SCAN) {
			//读卡获得IC卡的信息，按照业务逻辑进行处理
			if (mRfidAdapter.HasCard() == null) {
				mPopupWindowDialog.showMessage("读卡失败");
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
			mLink = "称完皮重";
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
			mPromptTextView.setText("业务结束收卡退港，请保管员在退港后收回业务结算卡!");
			mRetreatButton.setEnabled(true);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void ReadCard(){
		//读卡获得IC卡的信息，按照业务逻辑进行处理
		if (mRfidAdapter.HasCard() == null) {
			mPopupWindowDialog.showMessage("读卡失败");
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
		mLink = "称完皮重";
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
		mPromptTextView.setText("业务结束收卡退港，请保管员在退港后收回业务结算卡!");
		mRetreatButton.setEnabled(true);
	}
	
	private void InitWarehouseName() {
		for (int i = 0;i < 5;i++) {
			Warehouse warehouse = new Warehouse();
			warehouse.setWarehouse_id(i);
			warehouse.setWarehouse_name(i+"号仓库");
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
			mPopupWindowDialog.showMessage("退港成功");
		}
	}
}
