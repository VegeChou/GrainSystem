package com.aisino.grain.ui.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.aisino.grain.R;
import com.aisino.grain.beans.SearchTaskingResponse;
import com.aisino.grain.beans.Task;
import com.aisino.grain.ui.util.PopupWindowDialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TaskingActivity extends BaseActivity {
	private static String TAG = "TaskingActivity";
	
	private Button mNewestTaskButton = null;
	private Button mProcessTaskButton = null;
	private Button mMoreButton = null;
	private Context mContext = null;
	
	private SearchTaskingResponse mSearchTaskingResponse = null;
	private static Task mSelectedTask = null;
	private ListView mTaskingListView = null;							//带处理扦样任务Listview
	private List<TaskingItemData> mTaskingItemDataList = null;			//Tasking数据ListView中显示
	private int mSelectedPosition = -1;									//listview选中id
	private List<Task> mTaskingList = null;
	private MyBaseAdapter mMyBaseAdapter = null;
	private int mCount = 0;										//调用More次数
	
	private Random mRandom = null;
	public static String[] GoodsOwner = {"散户","市级储备","省级储备","中央储备","洋河酒厂","五得利面粉厂","宿迁直属库"};
	public static String[] VehiclePlate = {"京A-02009","沪B-12345","使D-88888","警S-00001","津C-66666","苏U-00110","川Q-11111"};
	public static String[] GrainTypeName = {"粳稻","中晚籼稻","硬质白小麦","软质白小麦","混合麦","玉米","大豆"};
	public static String[] TaskNumber = {"A00001","B00002","C00003","D00004","E00005","F00006","G00007"};
	public static String[] TaskState = {"已完成","未完成","待处理"};
	public static String[] CreateDate = {"2013-10-25","2013-11-02","2011-10-23","2009-04-22","2013-03-11","2010-12-22","2003-03-05"};
	public static String[] EnrollNumber = {"BH00001","BH00002","BH00003","BH00004","BH00005","BH00006","BH00007"};
	public static int[] GrainType = {1,2,3,4,5,6,7};
	public static String[] SampleNumber = {"YP-01","YP-02","YP-03","YP-04","YP-05","YP-06","YP-07"};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_tasking);
		super.onCreate(savedInstanceState);
		InitCtrl();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
//			if (mSelectedPosition > -1) {
//				mSelectedPosition=-1;
//				mTaskingListView.setSelection(mSelectedPosition);
//				mSelectedTask = mTaskingList.get(mSelectedPosition);
//			}
//		}
//		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
//			if (mSelectedPosition < mTaskingItemDataList.size()-1) {
//				mSelectedPosition=+1;
//				mTaskingListView.setSelection(mSelectedPosition);
//				mSelectedTask = mTaskingList.get(mSelectedPosition);
//			}
//		}
//		if (mMyBaseAdapter != null) {
//			mMyBaseAdapter.notifyDataSetInvalidated();
//		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void InitCtrl() {
		mContext = this;
		mPopupWindowDialog = new PopupWindowDialog(mContext);
		
		mTaskingListView = (ListView)findViewById(R.id.tasking_lv);
		mTaskingListView.setOnItemClickListener(new TaskingOnItemClickListener());
		
		mNewestTaskButton = (Button)findViewById(R.id.tasking_btn_newest_task);
		mNewestTaskButton.setOnClickListener(new NewestTaskOnClickLitener());
		mProcessTaskButton = (Button)findViewById(R.id.tasking_btn_process_task);
		mProcessTaskButton.setOnClickListener(new ProcessTaskOnClickLitener());
		mMoreButton = (Button)findViewById(R.id.tasking_btn_more);
		mMoreButton.setOnClickListener(new MoreOnClickLitener());
		
		mMoreButton.setEnabled(false);
		
		mTaskingList = new ArrayList<Task>();
	}
	
	class NewestTaskOnClickLitener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mCount = 0;
			CreateTestData();
			BindData();
			DisplayTasking();
			mMoreButton.setEnabled(true);
		}
	}
	
	class MoreOnClickLitener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (mCount == 3) {	//mSearchTaskingResponse == null
				Toast.makeText(getApplicationContext(), "没有最新任务", Toast.LENGTH_LONG).show();
				mMoreButton.setEnabled(false);
				return;
			}
			CreateMoreTestData();
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
	
	private void CreateTestData() {
		mSearchTaskingResponse = new SearchTaskingResponse();
		mSearchTaskingResponse.setTaskCount(10);
		List<Task> tasks = new ArrayList<Task>();
		for (int i = 0; i < 10; i++) {
			Task task = new Task();
			mRandom = new Random();
			task.setTaskNumber(TaskNumber[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setTaskState(TaskState[mRandom.nextInt(2)]);
			mRandom = new Random();
			task.setCreateDate(CreateDate[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setEnrollNumber(EnrollNumber[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setGoodsOwner(GoodsOwner[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setVehiclePlate(VehiclePlate[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setGrainType(GrainType[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setGrainTypeName(GrainTypeName[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setSampleNumber(SampleNumber[mRandom.nextInt(6)]);
			tasks.add(task);
		}
		mSearchTaskingResponse.setTaskList(tasks);
		mTaskingList = mSearchTaskingResponse.getTaskList();
	}
	
	private void CreateMoreTestData() {
		mCount++;
		mSearchTaskingResponse = new SearchTaskingResponse();
		mSearchTaskingResponse.setTaskCount(10);
		List<Task> tasks = new ArrayList<Task>();
		for (int i = 0; i < 10; i++) {
			Task task = new Task();
			mRandom = new Random();
			task.setTaskNumber(TaskNumber[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setTaskState(TaskState[mRandom.nextInt(2)]);
			mRandom = new Random();
			task.setCreateDate(CreateDate[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setEnrollNumber(EnrollNumber[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setGoodsOwner(GoodsOwner[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setVehiclePlate(VehiclePlate[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setGrainType(GrainType[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setGrainTypeName(GrainTypeName[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setSampleNumber(SampleNumber[mRandom.nextInt(6)]);
			tasks.add(task);
		}
		mSearchTaskingResponse.setTaskList(tasks);
		mTaskingList.addAll(mSearchTaskingResponse.getTaskList());
	}
	
	private void BindMoreData() {	//将需要显示的信息绑定
		for (int i = 0; i < mSearchTaskingResponse.getTaskCount(); i++) {
			TaskingItemData taskingItemData = new TaskingItemData();
			taskingItemData.GoodsOwner = mSearchTaskingResponse.getTaskList().get(i).getGoodsOwner();
			taskingItemData.VehiclePlate = mSearchTaskingResponse.getTaskList().get(i).getVehiclePlate();
			taskingItemData.GrainTypeName = mSearchTaskingResponse.getTaskList().get(i).getGrainTypeName();
			mTaskingItemDataList.add(taskingItemData);
		}
	}
	
	private void BindData() {	//将需要显示的信息绑定
		mTaskingItemDataList = new ArrayList<TaskingItemData>();
		for (int i = 0; i < mSearchTaskingResponse.getTaskCount(); i++) {
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
	
	@Override
	protected void onResume() {
		mSelectedTask = null;
		super.onResume();
	}
}
