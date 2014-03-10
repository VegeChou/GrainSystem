package com.aisino.grain.ui.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
import com.aisino.grain.beans.SearchTaskedResponse;
import com.aisino.grain.beans.Task;

public class TaskedActivity extends BaseActivity {
	private static String TAG = "TaskedActivity";
	
	private Button mQueryButton = null;
	private Button mMoreButton = null;
	private Button mPrintButton = null;
	private Button mCloseButton = null;
	
	private ListView mTaskedListView = null;							//带处理扦样任务Listview
	private EditText mPlatEditText = null;
	
	private SearchTaskedResponse mSearchTaskedResponse = null;
	private MyBaseAdapter mMyBaseAdapter = null;
	private List<Task> mTaskedList = null;
	private List<TaskedItemData> mTaskedItemDataList = null;			//Tasked数据ListView中显示
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
		setContentView(R.layout.activity_tasked);
		super.onCreate(savedInstanceState);
		InitCtrl();
	}
	
	private void InitCtrl() {
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
			CreateTestData();
			BindData();
			DisplayTasked();
			mMoreButton.setEnabled(true);
		}
	}
	
	class PrintOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			//正在打印已处理扦样任务信息
			Toast.makeText(getApplicationContext(), "正在打印已处理扦样任务信息", Toast.LENGTH_LONG).show();
		}
	}
	
	class MoreOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (mCount == 3) {	//mSearchTaskedResponse == null
				Toast.makeText(getApplicationContext(), "没有更多已处理扦样任务", Toast.LENGTH_LONG).show();
				mMoreButton.setEnabled(false);
				return;
			}
			CreateMoreTestData();
			BindMoreData();
			DisplayMoreTasked();
		}
	}
	
	private void CreateTestData() {
		mCount = 0;
		String plate = mPlatEditText.getText().toString();
		int size = 0;
		if (plate.equals("")) {
			size = 8;
		}
		else {
			size = 4;
		}
		
		mSearchTaskedResponse = new SearchTaskedResponse();
		mSearchTaskedResponse.setTaskCount(size);
		List<Task> tasks = new ArrayList<Task>();
		for (int i = 0; i < size; i++) {
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
			if (plate.equals("")) {
				mRandom = new Random();
				task.setVehiclePlate(VehiclePlate[mRandom.nextInt(6)]);
			} else {
				task.setVehiclePlate(plate);
			}
			mRandom = new Random();
			task.setGrainType(GrainType[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setGrainTypeName(GrainTypeName[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setSampleNumber(SampleNumber[mRandom.nextInt(6)]);
			tasks.add(task);
		}
		mSearchTaskedResponse.setTaskList(tasks);
		mTaskedList = mSearchTaskedResponse.getTaskList();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-10-25
	 * @description	已处理扦样任务listview的Adapter
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
	 * @description 带处理扦样任务mTaskedListView中Item数据类
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
	 * @description 带处理扦样任务Tasked中Item视图类
	 *
	 */
	class TaskedItem{
		TextView TaskDate;
		TextView Plate;
		TextView TaskKind;
		TextView SampleNumber;
	}
	
	private void BindData() {	//将需要显示的信息绑定
		mTaskedItemDataList = new ArrayList<TaskedItemData>();
		for (int i = 0; i < mSearchTaskedResponse.getTaskCount(); i++) {
			TaskedItemData TaskedItemData = new TaskedItemData();
			TaskedItemData.TaskDate = mSearchTaskedResponse.getTaskList().get(i).getCreateDate();
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
	
	private void CreateMoreTestData() {
		mCount++;
		String plate = mPlatEditText.getText().toString();
		int size = 0;
		if (plate.equals("")) {
			size = 8;
		}
		else {
			size = 4;
		}
		
		mSearchTaskedResponse = new SearchTaskedResponse();
		mSearchTaskedResponse.setTaskCount(size);
		List<Task> tasks = new ArrayList<Task>();
		for (int i = 0; i < size; i++) {
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
			if (plate.equals("")) {
				mRandom = new Random();
				task.setVehiclePlate(VehiclePlate[mRandom.nextInt(6)]);
			} else {
				task.setVehiclePlate(plate);
			}
			mRandom = new Random();
			task.setGrainType(GrainType[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setGrainTypeName(GrainTypeName[mRandom.nextInt(6)]);
			mRandom = new Random();
			task.setSampleNumber(SampleNumber[mRandom.nextInt(6)]);
			tasks.add(task);
		}
		mSearchTaskedResponse.setTaskList(tasks);
		mTaskedList.addAll(mSearchTaskedResponse.getTaskList());
	}
	
	private void BindMoreData() {	//将需要显示的信息绑定
		for (int i = 0; i < mSearchTaskedResponse.getTaskCount(); i++) {
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
	
}
