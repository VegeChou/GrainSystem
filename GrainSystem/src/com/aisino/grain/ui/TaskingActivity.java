package com.aisino.grain.ui;

import java.util.ArrayList;
import java.util.List;

import com.aisino.grain.R;
import com.aisino.grain.beans.SearchTaskingRequest;
import com.aisino.grain.beans.SearchTaskingResponse;
import com.aisino.grain.beans.Task;
import com.aisino.grain.model.rest.RestWebServiceAdapter;
import com.aisino.grain.ui.util.PopupWindowDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class TaskingActivity extends CustomMenuActivity {
	private static final String TAG = "TaskingActivity";
	private static final boolean DEBUG = false;
	
	private Button mNewestTaskButton = null;
	private Button mProcessTaskButton = null;
	private Button mMoreButton = null;
	private Context mContext = null;
	
	private SearchTaskingResponse mSearchTaskingResponse = null;
	private List<Task> mTaskingList = null;
	private int mTaskCount = 0;
	
	private static Task mSelectedTask = null;
	private ListView mTaskingListView = null;							//带处理扦样任务Listview
	private List<TaskingItemData> mTaskingItemDataList = null;			//Tasking数据ListView中显示
	private int mSelectedPosition = -1;									//listview选中id
	private MyBaseAdapter mMyBaseAdapter = null;
	
	private RestWebServiceAdapter mRestWebServiceAdapter = null;
	
	private int PageNumber = 0;		//页号
	private int PageSize = 10;		//每页
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_tasking);
		super.onCreate(savedInstanceState);
//		LoginActivity.ActivityList.add(this);
		InitCtrl();
	}
	
	private void InitCtrl() {
		mContext = this;
		mPopupWindowDialog = new PopupWindowDialog(mContext);
		mRestWebServiceAdapter = RestWebServiceAdapter.getInstance(mContext);
		
		mTaskingListView = (ListView)findViewById(R.id.tasking_lv);
		mTaskingListView.setOnItemClickListener(new TaskingOnItemClickListener());
		
		mNewestTaskButton = (Button)findViewById(R.id.tasking_btn_newest_task);
		mNewestTaskButton.setOnClickListener(new NewestTaskOnClickLitener());
		mProcessTaskButton = (Button)findViewById(R.id.tasking_btn_process_task);
		mProcessTaskButton.setOnClickListener(new ProcessTaskOnClickLitener());
		mMoreButton = (Button)findViewById(R.id.tasking_btn_more);
		mMoreButton.setOnClickListener(new MoreOnClickLitener());
		
		mMoreButton.setEnabled(false);
		mMoreButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
		
		mTaskingList = new ArrayList<Task>();
	}
	
	class NewestTaskOnClickLitener implements OnClickListener{
		@Override
		public void onClick(View v) {
			PageNumber = 0;
			SearchTaskingRequest searchTaskingRequest = new SearchTaskingRequest();
			searchTaskingRequest.setPageNumber(PageNumber);
			searchTaskingRequest.setPageSize(PageSize);
			if (DEBUG) {
				mSearchTaskingResponse = TestSearchTaskingResponse();
			} else {
				mSearchTaskingResponse = mRestWebServiceAdapter.Rest(searchTaskingRequest);
			}
			mTaskCount = mSearchTaskingResponse.getTaskCount();
			if (mSearchTaskingResponse == null) {
				mPopupWindowDialog.showMessage("获取最新任务失败");
				return;
			}
			if (mTaskCount == 0) {
				mPopupWindowDialog.showMessage("没有最新任务");
				return;
			}
			mTaskingList.clear();
			mTaskingList.addAll(mSearchTaskingResponse.getTaskList());
			BindData();
			DisplayTasking();
			mMoreButton.setEnabled(true);
			mMoreButton.setBackgroundResource(R.drawable.corner_btn_selector);
		}
	}
	
	class MoreOnClickLitener implements OnClickListener{
		@Override
		public void onClick(View v) {
			PageNumber = PageNumber + 1;
			SearchTaskingRequest searchTaskingRequest = new SearchTaskingRequest();
			searchTaskingRequest.setPageNumber(PageNumber);
			searchTaskingRequest.setPageSize(PageSize);
			if (DEBUG) {
				mSearchTaskingResponse = TestMoreSearchTaskingResponse();
			} else {
				mSearchTaskingResponse = mRestWebServiceAdapter.Rest(searchTaskingRequest);
			}
			if (mSearchTaskingResponse == null) {
				mPopupWindowDialog.showMessage("获取更多最新任务失败");
				return;
			}
			if (mTaskingList.size() == mTaskCount) {
				mPopupWindowDialog.showMessage("没有更多最新任务");
				mMoreButton.setEnabled(false);
				mMoreButton.setBackgroundResource(R.drawable.corner_btn_default_disable);
				return;
			}
			mTaskingList.addAll(mSearchTaskingResponse.getTaskList());
			BindMoreData();
			DisplayMoreTasking();
		}
	}
	
	class ProcessTaskOnClickLitener implements OnClickListener{
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(mContext, RegisterSampleActivity.class);
			startActivity(intent);
		}
	}
	
	class TaskingOnItemClickListener implements OnItemClickListener{
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			mSelectedPosition = position;
			mSelectedTask = mTaskingList.get(position);
			mMyBaseAdapter.notifyDataSetInvalidated();
		}
	}
	
	private void DisplayTasking() {
		mMyBaseAdapter = new MyBaseAdapter();
		mTaskingListView.setAdapter(mMyBaseAdapter);
	}
	
	private void DisplayMoreTasking() {
		mMyBaseAdapter.count = mTaskingItemDataList.size();
    	mMyBaseAdapter.notifyDataSetChanged();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-10-25
	 * @description	带处理扦样任务listview的Adapter
	 *
	 */
	class MyBaseAdapter extends BaseAdapter {
		LayoutInflater mInflater = null;
		int count = mTaskingItemDataList.size();
		
		public MyBaseAdapter() {
			
			mInflater = getLayoutInflater();
		}
		
		@Override
		public int getCount() {
			return count;
		}

		@Override
		public Object getItem(int position) {
			return mTaskingItemDataList.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.tasking_item, null);
			} else {
				Log.i(TAG, "not null");
			}
			
			TaskingItem taskingItem = new TaskingItem();
			taskingItem.GoodsOwner = (TextView)convertView.findViewById(R.id.tasking_tv_goods_owner);
			taskingItem.VehiclePlate = (TextView)convertView.findViewById(R.id.tasking_tv_vehicle_plate);
			taskingItem.GrainTypeName = (TextView)convertView.findViewById(R.id.tasking_tv_grain_type_name);
			
			taskingItem.GoodsOwner.setText(mTaskingItemDataList.get(position).GoodsOwner);
			taskingItem.VehiclePlate.setText(mTaskingItemDataList.get(position).VehiclePlate);
			taskingItem.GrainTypeName.setText(mTaskingItemDataList.get(position).GrainTypeName);
			if (position%2 == 0) {
				convertView.setBackgroundResource(R.drawable.listview_item_selector1);
			} else {
				convertView.setBackgroundResource(R.drawable.listview_item_selector2);
			}
			if (position == mSelectedPosition) {
				convertView .setBackgroundResource(R.drawable.listview_item_selected);
			}
			return convertView;
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-10-25
	 * @description 带处理扦样任务mTaskingListView中Item数据类
	 *
	 */
	class TaskingItemData{
		String GoodsOwner;
		String VehiclePlate;
		String GrainTypeName;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-10-25
	 * @description 带处理扦样任务Tasking中Item视图类
	 *
	 */
	class TaskingItem{
		TextView GoodsOwner;
		TextView VehiclePlate;
		TextView GrainTypeName;
	}
	
	private void BindMoreData() {	//将需要显示的信息绑定
		
		for (int i = 0; i < mSearchTaskingResponse.getTaskList().size(); i++) {
			TaskingItemData taskingItemData = new TaskingItemData();
			taskingItemData.GoodsOwner = mSearchTaskingResponse.getTaskList().get(i).getGoodsOwner();
			taskingItemData.VehiclePlate = mSearchTaskingResponse.getTaskList().get(i).getVehiclePlate();
			taskingItemData.GrainTypeName = mSearchTaskingResponse.getTaskList().get(i).getGrainTypeName();
			mTaskingItemDataList.add(taskingItemData);
		}
	}
	
	private void BindData() {	//将需要显示的信息绑定
		mTaskingItemDataList = new ArrayList<TaskingItemData>();
		for (int i = 0; i < mSearchTaskingResponse.getTaskList().size(); i++) {
			TaskingItemData taskingItemData = new TaskingItemData();
			taskingItemData.GoodsOwner = mSearchTaskingResponse.getTaskList().get(i).getGoodsOwner();
			taskingItemData.VehiclePlate = mSearchTaskingResponse.getTaskList().get(i).getVehiclePlate();
			taskingItemData.GrainTypeName = mSearchTaskingResponse.getTaskList().get(i).getGrainTypeName();
			mTaskingItemDataList.add(taskingItemData);
		}
	}
	
	public static Task GetTask() {
		return mSelectedTask;
	}
	
	private SearchTaskingResponse TestSearchTaskingResponse() {
		SearchTaskingResponse searchTaskingResponse = null;
//		return searchTaskingResponse;
		
		searchTaskingResponse = new SearchTaskingResponse();
		searchTaskingResponse.setTaskCount(10);
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
		searchTaskingResponse.setTaskList(tasks);
		
//		searchTaskingResponse.setTaskCount(0);
//		searchTaskingResponse.setTaskList(null);
		return searchTaskingResponse;
	}
	
	private SearchTaskingResponse TestMoreSearchTaskingResponse() {
		SearchTaskingResponse searchTaskingResponse = null;
//		return searchTaskingResponse;
		
		searchTaskingResponse = new SearchTaskingResponse();
		
		searchTaskingResponse.setTaskCount(10);
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
		searchTaskingResponse.setTaskList(tasks);
		
//		searchTaskingResponse.setTaskCount(0);
//		searchTaskingResponse.setTaskList(null);
		return searchTaskingResponse;
	}
	
	@Override
	protected void onResume() {
		mSelectedTask = null;
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
//		LoginActivity.ActivityList.remove(this);
		super.onDestroy();
	}
}
