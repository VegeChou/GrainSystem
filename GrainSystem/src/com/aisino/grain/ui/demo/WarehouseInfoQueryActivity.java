package com.aisino.grain.ui.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.aisino.grain.R;
import com.aisino.grain.beans.InOutInfoOfDay;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class WarehouseInfoQueryActivity extends BaseActivity {
	private static final String TAG = "WarehouseInfoQueryActivity";
	
	//����ⵥѡ��ť
	private RadioGroup mStockInOutRadioGroup = null;			
	private RadioButton mAcquisitionStockInRadioButton = null;	//�չ����
	private RadioButton mSaleStockOutRadioButton = null;		//���۳���
	
	//�������ϢListview
	private ListView mStockInOutInfoListView = null;			//��ʾ�б�	
	private MyBaseAdapter mMyBaseAdapter = null;				//listview������
    private LinearLayout loadProgressBar;						//���ڼ��ؽ�����
    private Button mMoreButton = null;
    private LinearLayout mLinearLayout = null;					//ListView Item ����
    
    //��ȡ����ֿ���Ϣ״̬
    private final int GET_MORE_FAILED = 0;
    private final int GET_MORE_NEW_DATA = 1;
    private final int GET_MORE_NO_DATA = 2;
	
	private List<InOutInfoOfDay> mInOutInfoOfDayList = null;	//�������Ϣ�б�
	
	private String mDate = null;
	private Random mRandom = null;
	private int mTimes = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_warehouse_info_query);
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
		mMoreButton = (Button)findViewById(R.id.warehouse_info_btn_more);
		mMoreButton.setOnClickListener(new MoreOnClickListener());
		mStockInOutRadioGroup = (RadioGroup)findViewById(R.id.warehouse_info_rbtn_group_stock_in_out);
		mAcquisitionStockInRadioButton = (RadioButton)findViewById(R.id.warehouse_info_rbtn_acquisition_stock_in);
		mSaleStockOutRadioButton = (RadioButton)findViewById(R.id.warehouse_info_rbtn_sell_stock_out);
		
		mStockInOutInfoListView = (ListView)findViewById(R.id.warehouse_info_lv);
		
		mStockInOutRadioGroup.setOnCheckedChangeListener(new StockInOutOnCheckedChangeListener());
		
		mInOutInfoOfDayList = new ArrayList<InOutInfoOfDay>();
		
		//�������ϢListView��ӱ�β
		AddListViewPageMore();
		
		mDate = getSystemDate();
		GetWarehouseInfo();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	��ʼ���������Ϣ�б���ʾ��ѯ�������Ϣ
	 *
	 */
	private void InitListView(){
		mMyBaseAdapter = new MyBaseAdapter();
		mStockInOutInfoListView.setAdapter(mMyBaseAdapter);
	}
	
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	����ⵥѡ��ť��Ӧ��������������߳��ⵥѡ��ť�����������ѯ�������Ϣ
	 *
	 */
	class StockInOutOnCheckedChangeListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			mTimes = 0;
			if (checkedId == mAcquisitionStockInRadioButton.getId()) {
				mInOutInfoOfDayList.clear();
				GetWarehouseInfo();
			}
			if (checkedId == mSaleStockOutRadioButton.getId()) {
				mInOutInfoOfDayList.clear();
				GetWarehouseInfo();
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	�Զ���������Ϣlistview��������
	 *
	 */
	class MyBaseAdapter extends BaseAdapter {
		LayoutInflater mInflater = null;
		int count = mInOutInfoOfDayList.size();
		
		public MyBaseAdapter() {
			mInflater = getLayoutInflater();
		}
		
		@Override
		public int getCount() {
			return count;
		}

		@Override
		public Object getItem(int position) {
			return mInOutInfoOfDayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.warehouse_info_item, null);
				mLinearLayout = (LinearLayout)convertView.findViewById(R.id.warehouse_info_ll);
			} else {
				Log.i(TAG, "not null");
			}
			
			StockInOut stockInOut = new StockInOut();
			stockInOut.data = (TextView)convertView.findViewById(R.id.warehouse_info_tv_date);
			stockInOut.netweight = (TextView)convertView.findViewById(R.id.warehouse_info_tv_net_weigth);
			stockInOut.deducted = (TextView)convertView.findViewById(R.id.warehouse_info_tv_deduction);
			
			stockInOut.data.setText(mInOutInfoOfDayList.get(position).getWorkDate());
			stockInOut.netweight.setText(String.valueOf(mInOutInfoOfDayList.get(position).getNetWeight()));
			stockInOut.deducted.setText(String.valueOf(mInOutInfoOfDayList.get(position).getDeducted()));
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
	 * @date 2013-7-12
	 * @description	��ȡ�ֿ���Ϣ��ͨ��WebService��ѯ�ֿ���Ϣ
	 *
	 */
	private void GetWarehouseInfo() {
		// ��ʼ�����Դ���
		int count = 10;
		if (count != 2) {
			count++;
			for (int i = 0; i < 4; i++) {
				InOutInfoOfDay infoOfDay = new InOutInfoOfDay();
				infoOfDay.setWorkDate(mDate);
				mRandom = new Random();
				infoOfDay.setNetWeight(mRandom.nextInt(1000)+1000);
				mRandom = new Random();
				infoOfDay.setDeducted(mRandom.nextInt(500)+500);
				mInOutInfoOfDayList.add(infoOfDay);
			}
		}
		
		InitListView();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description  �����ListView��Item��ͼ��
	 *
	 */
	class StockInOut{
		TextView data;
		TextView netweight;
		TextView deducted;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	��ӳ������Ϣ��β���鿴����
	 *
	 */
    private void AddListViewPageMore(){
        View view=LayoutInflater.from(this).inflate(R.layout.list_page_load, null);
        loadProgressBar=(LinearLayout)view.findViewById(R.id.load_id);
        mStockInOutInfoListView.addFooterView(view);
    }
    
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-12
	 * @description	�߳���Ϣ���������������鿴���ࡱ����Ϣ����UI���и���
	 *
	 */
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case GET_MORE_FAILED:
            	mPopupWindowDialog.showMessage("��ȡ����ʧ��");
                //�ٴ����ء���������
                loadProgressBar.setVisibility(View.GONE);
            	break;
            case GET_MORE_NO_DATA:
            	mPopupWindowDialog.showMessage("�����Ѿ�����");
                //�ٴ����ء���������
                loadProgressBar.setVisibility(View.GONE);
            	break;
            case GET_MORE_NEW_DATA:
//            	InitListView();
            	//�ı�����������Ŀ
            	mMyBaseAdapter.count = mInOutInfoOfDayList.size();
                //֪ͨ�����������ָı����
            	mMyBaseAdapter.notifyDataSetChanged();
                //�ٴ����ء���������
                loadProgressBar.setVisibility(View.GONE);
                break;
            default:
                break;
            }
            super.handleMessage(msg);
        }
    };
    
    /**
     * 
     * @author zwz
     * @date 2013-7-12
     * @description
     *
     * @return	�߳���ϢGET_MORE_FAILED����ȡʧ��		GET_MORE_NO_DATA����Ϣ����	GET_MORE_NEW_DATA��������Ϣ
     */
    private int GetMoreWarehouseInfo() {
		List<InOutInfoOfDay> InOutInfoOfDaysList = null;					//�������Ϣ�б�
		
		//More���Դ���
		int count = 10;
		if (count != 2) {
			count++;
			InOutInfoOfDaysList = new ArrayList<InOutInfoOfDay>();
			for (int i = 0; i < 4; i++) {
				InOutInfoOfDay infoOfDayBean = new InOutInfoOfDay();
				infoOfDayBean.setWorkDate(mDate);
				mRandom = new Random();
				infoOfDayBean.setNetWeight(mRandom.nextInt(1000)+1000);
				mRandom = new Random();
				infoOfDayBean.setDeducted(mRandom.nextInt(500)+500);
				InOutInfoOfDaysList.add(infoOfDayBean);
			}
		}
		mTimes++;
		if (mTimes > 3) {
			return GET_MORE_NO_DATA;
		}
		mInOutInfoOfDayList.addAll(InOutInfoOfDaysList);	//�»�ȡ��Ϣ����ӵ�mInOutInfoOfDayList��
		return GET_MORE_NEW_DATA;
    }
    
    class MoreOnClickListener implements OnClickListener{
    	@Override
    	public void onClick(View v) {
            //��ʾ������
            loadProgressBar.setVisibility(View.VISIBLE);
            
            new Thread(new Runnable() {
                @Override
                public void run() {
                    //�����µ�����
                    int res = GetMoreWarehouseInfo();
                    Message mes=handler.obtainMessage(res);
                    handler.sendMessage(mes);
                }
            }).start();
    	}
    }
    
    private String getSystemDate() {
    	SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    	Date curDate = new Date(System.currentTimeMillis());//��ȡ��ǰʱ��
    	String str  = formatter.format(curDate);
    	return str;
	}
}