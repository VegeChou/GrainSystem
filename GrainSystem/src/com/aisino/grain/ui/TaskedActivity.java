package com.aisino.grain.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aisino.grain.R;
import com.aisino.grain.beans.SearchTaskedRequest;
import com.aisino.grain.beans.SearchTaskedResponse;
import com.aisino.grain.beans.Task;
import com.aisino.grain.model.rest.RestWebServiceAdapter;
import com.aisino.grain.ui.util.PopupWindowDialog;

public class TaskedActivity extends CustomMenuActivity {
	private static String TAG = "TaskedActivity";
	private static final boolean DEBUG = false;
	
	private Button mQueryButton = null;
	private Button mMoreButton = null;
	private Button mPrintButton = null;
	private Button mCloseButton = null;
	
	private ListView mTaskedListView = null;							//������Ǥ������Listview
	private EditText mPlatEditText = null;
	
	private SearchTaskedResponse mSearchTaskedResponse = null;
	private List<Task> mTaskingList = null;
	private int mTaskCount = 0;
	private MyBaseAdapter mMyBaseAdapter = null;
	private List<TaskedItemData> mTaskedItemDataList = null;			//Tasked����ListView����ʾ
	
	private Context mContext = null;
	private RestWebServiceAdapter mRestWebServiceAdapter = null;
	private SharedPreferences mLoginSharedPreferences = null;	//�����¼��Ϣ
	private int mCurrentUserID = -1;
	private String mPlate = null;
	private int mPageNumber = 0;		//ҳ��
	private int mPageSize = 8;		//ÿҳ
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_tasked);
		super.onCreate(savedInstanceState);
//		LoginActivity.ActivityList.add(this);
		InitCtrl();
	}
	
	@Override
	protected void onDestroy() {
//		LoginActivity.ActivityList.remove(this);
		super.onDestroy();
	}
	
	private void InitCtrl() {
		mContext = this;
		mRestWebServiceAdapter = RestWebServiceAdapter.getInstance(mContext);
		mLoginSharedPreferences = getSharedPreferences("LoginInformation", Context.MODE_PRIVATE);
		mPopupWindowDialog = new PopupWindowDialog(mContext);
		
		mTaskedListView = (ListView)findViewById(R.id.tasked_lv);
		
		mCloseButton = (Button)findViewById(R.id.tasked_btn_close);
		mCloseButton.setOnClickListener(new CloseOnClickListener());
		mQueryButton = (Button)findViewById(R.id.tasked_btn_query);
		mQueryButton.setOnClickListener(new QueryOnClickListener());
		mPrintButton = (Button)findViewById(R.id.tasked_btn_print);
		mPrintButton.setOnClickListener(new PrintOnClickListener());
		mMoreButton = (Button)findViewById(R.id.tasked_btn_more);
		mMoreButton.setOnClickListener(new MoreOnClickListener());
		mMoreButton.setEnabled(false);
		mMoreButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
		
		mPlatEditText = (EditText)findViewById(R.id.tasked_et_plate);
	}
	
	class CloseOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			finish();
		}
	}
	
	class QueryOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mPageNumber = 0;
			SearchTaskedRequest searchTaskedRequest = new SearchTaskedRequest();
			//��ȡ��ǰ��¼�û�ID
			mCurrentUserID = mLoginSharedPreferences.getInt("current_user_id", -1);
			if (mCurrentUserID == -1) {
				mPopupWindowDialog.showMessage("�Ǽ�ʧ��,��ͬ���˻���Ϣ");
				return;
			}
			searchTaskedRequest.setUserID(mCurrentUserID);
			searchTaskedRequest.setPageNumber(mPageNumber);
			searchTaskedRequest.setPageSize(mPageSize);
			mPlate = mPlatEditText.getText().toString();
			searchTaskedRequest.setVehiclePlate(mPlate);
			if (DEBUG) {
				mSearchTaskedResponse = TestSearchTaskedResponse();
			} else {
				mSearchTaskedResponse = mRestWebServiceAdapter.Rest(searchTaskedRequest);
			}
			if (mSearchTaskedResponse == null) {
				mPopupWindowDialog.showMessage("��ȡ�Ѵ���Ǥ���������������Ϊ��");
			}
			else {
				if (mSearchTaskedResponse.getTaskCount() == 0) {
					mPopupWindowDialog.showMessage("û�и����Ѵ���Ǥ������");
				} else {
					mTaskingList.clear();
					mTaskingList.addAll(mSearchTaskedResponse.getTaskList());
					BindData();
					DisplayTasked();
					mMoreButton.setEnabled(true);
					mMoreButton.setBackgroundResource(R.drawable.corner_btn_selector);
				}
			}
		}
	}
	
	class PrintOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//���ڴ�ӡ�Ѵ���Ǥ��������Ϣ
			Toast.makeText(getApplicationContext(), "���ڴ�ӡ�Ѵ���Ǥ��������Ϣ", Toast.LENGTH_LONG).show();
		}
	}
	
	class MoreOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mPageNumber = mPageNumber + 1;
			SearchTaskedRequest searchTaskedRequest = new SearchTaskedRequest();
			searchTaskedRequest.setUserID(mCurrentUserID);
			searchTaskedRequest.setPageNumber(mPageNumber);
			searchTaskedRequest.setPageSize(mPageSize);
			searchTaskedRequest.setVehiclePlate(mPlate);
			if (DEBUG) {
				mSearchTaskedResponse = TestMoreSearchTaskedResponse();
			} else {
				mSearchTaskedResponse = mRestWebServiceAdapter.Rest(searchTaskedRequest);
			}
			if (mSearchTaskedResponse == null) {	//mSearchTaskedResponse == null
				mPopupWindowDialog.showMessage("��ȡ�����Ѵ���Ǥ���������������Ϊ��");
				mMoreButton.setEnabled(false);
				mMoreButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
				return;
			} else {
				if (mTaskingList.size() == mTaskCount) {
					mPopupWindowDialog.showMessage("û�и����Ѵ���Ǥ������");
					mMoreButton.setEnabled(false);
					mMoreButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
				} else {
					mTaskingList.addAll(mSearchTaskedResponse.getTaskList());
					BindMoreData();
					DisplayMoreTasked();
				}
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-10-25
	 * @description	�Ѵ���Ǥ������listview��Adapter
	 *
	 */
	class MyBaseAdapter extends BaseAdapter {
		LayoutInflater mInflater = null;
		int count = mTaskedItemDataList.size();
		
		public MyBaseAdapter() {
			
			mInflater = getLayoutInflater();
		}
		
		@Override
		public int getCount() {
			return count;
		}

		@Override
		public Object getItem(int position) {
			return mTaskedItemDataList.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.tasked_item, null);
			} else {
				Log.i(TAG, "not null");
			}
			
			TaskedItem taskedItem = new TaskedItem();
			taskedItem.TaskDate = (TextView)convertView.findViewById(R.id.tasked_tv_task_date);
			taskedItem.Plate = (TextView)convertView.findViewById(R.id.tasked_tv_vehicle_plate);
			taskedItem.TaskKind = (TextView)convertView.findViewById(R.id.tasked_tv_task_kind);
			taskedItem.SampleNumber = (TextView)convertView.findViewById(R.id.tasked_tv_sample_number);
			
			taskedItem.TaskDate.setText(mTaskedItemDataList.get(position).TaskDate);
			taskedItem.Plate.setText(mTaskedItemDataList.get(position).Plate);
			taskedItem.TaskKind.setText(mTaskedItemDataList.get(position).TaskKind);
			taskedItem.SampleNumber.setText(mTaskedItemDataList.get(position).SampleNumber);
			if (position%2 == 0) {
				convertView.setBackgroundResource(R.drawable.listview_item_selector1);
			} else {
				convertView.setBackgroundResource(R.drawable.listview_item_selector2);
			}
			return convertView;
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-10-25
	 * @description ������Ǥ������mTaskedListView��Item������
	 *
	 */
	class TaskedItemData{
		String TaskDate;
		String Plate;
		String TaskKind;
		String SampleNumber;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-10-25
	 * @description ������Ǥ������Tasked��Item��ͼ��
	 *
	 */
	class TaskedItem{
		TextView TaskDate;
		TextView Plate;
		TextView TaskKind;
		TextView SampleNumber;
	}
	
	private void BindData() {	//����Ҫ��ʾ����Ϣ��
		mTaskedItemDataList = new ArrayList<TaskedItemData>();
		for (int i = 0; i < mSearchTaskedResponse.getTaskList().size(); i++) {
			TaskedItemData TaskedItemData = new TaskedItemData();
			TaskedItemData.TaskDate =mSearchTaskedResponse.getTaskList().get(i).getCreateDate();
			TaskedItemData.Plate = mSearchTaskedResponse.getTaskList().get(i).getVehiclePlate();
			TaskedItemData.TaskKind = mSearchTaskedResponse.getTaskList().get(i).getGrainTypeName();
			TaskedItemData.SampleNumber = mSearchTaskedResponse.getTaskList().get(i).getSampleNumber();
			mTaskedItemDataList.add(TaskedItemData);
		}
	}
	
	private void DisplayTasked() {
		mMyBaseAdapter = new MyBaseAdapter();
		mTaskedListView.setAdapter(mMyBaseAdapter);
	}
	
	private void BindMoreData() {	//����Ҫ��ʾ����Ϣ��
		for (int i = 0; i < mSearchTaskedResponse.getTaskList().size(); i++) {
			TaskedItemData TaskedItemData = new TaskedItemData();
			TaskedItemData.TaskDate = mSearchTaskedResponse.getTaskList().get(i).getCreateDate();
			TaskedItemData.Plate = mSearchTaskedResponse.getTaskList().get(i).getVehiclePlate();
			TaskedItemData.TaskKind = mSearchTaskedResponse.getTaskList().get(i).getGrainTypeName();
			TaskedItemData.SampleNumber = mSearchTaskedResponse.getTaskList().get(i).getSampleNumber();
			mTaskedItemDataList.add(TaskedItemData);
		}
	}
	
	private void DisplayMoreTasked() {
		mMyBaseAdapter.count = mTaskedItemDataList.size();
    	mMyBaseAdapter.notifyDataSetChanged();
	}
	
	
	private SearchTaskedResponse TestSearchTaskedResponse() {
		SearchTaskedResponse searchTaskedResponse = null;
//		return searchTaskedResponse;
		
		searchTaskedResponse = new SearchTaskedResponse();
		
		searchTaskedResponse.setTaskCount(10);
		List<Task> tasks = new ArrayList<Task>();
		for (int i = 0; i < 10; i++) {
			Task task = new Task();
			task.setTaskNumber("TaskNumber"+i);
			task.setTaskState("TaskState"+i);
			task.setCreateDate("CreateDate"+i);
			task.setEnrollNumber("EnrollNumber"+i);
			task.setGoodsOwner("GoodsOwner"+i);
			task.setVehiclePlate("VehiclePlate"+i);
			task.setGrainType(i);
			task.setGrainTypeName("GrainTypeName"+i);
			task.setSampleNumber("SampleNumber"+i);
			tasks.add(task);
		}
		searchTaskedResponse.setTaskList(tasks);
		
//		searchTaskedResponse.setTaskCount(0);
//		searchTaskedResponse.setTaskList(null);
		return searchTaskedResponse;
	}
	
	private SearchTaskedResponse TestMoreSearchTaskedResponse() {
		SearchTaskedResponse searchTaskedResponse = null;
//		return searchTaskedResponse;
		
		searchTaskedResponse = new SearchTaskedResponse();
		
//		searchTaskedResponse.setTaskCount(10);
//		List<Task> tasks = new ArrayList<Task>();
//		for (int i = 0; i < 10; i++) {
//			Task task = new Task();
//			task.setTaskNumber("TaskNumber"+i);
//			task.setTaskState("TaskState"+i);
//			task.setCreateDate("CreateDate"+i);
//			task.setEnrollNumber("EnrollNumber"+i);
//			task.setGoodsOwner("GoodsOwner"+i);
//			task.setVehiclePlate("VehiclePlate"+i);
//			task.setGrainType(i);
//			task.setGrainTypeName("GrainTypeName"+i);
//			task.setSampleNumber("SampleNumber"+i);
//			tasks.add(task);
//		}
//		searchTaskedResponse.setTaskList(tasks);
		
		searchTaskedResponse.setTaskCount(0);
		searchTaskedResponse.setTaskList(null);
		return searchTaskedResponse;
	}
}
