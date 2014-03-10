package com.aisino.grain.ui.demo;

import com.aisino.grain.R;
import com.aisino.grain.model.db.DataHelper;
import com.aisino.grain.ui.util.PopupWindowDialog;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class BaseActivity extends OrmLiteBaseActivity<DataHelper> {
	private static final String TAG = "BaseActivity";
	
	protected PopupWindowDialog mPopupWindowDialog = null;
	
	private int[] resArray = new int[] {
			R.drawable.system_conifg, R.drawable.data_sync,
			R.drawable.exit
	};	
	private String[] title = new String[]{
			"系统配置", "数据同步", "退出"
	};		
	private PopupWindow mPopupWindow = null;
	private LayoutInflater mLayoutInflater = null;
	private GridView mGridView = null;
	private ImageAdapter mImageAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		InitCtrl();
		super.onCreate(savedInstanceState);
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	资源状态初始化
	 *
	 */
	private void InitCtrl() {
		mPopupWindowDialog = new PopupWindowDialog(this);
		
		mImageAdapter = new ImageAdapter(this);
        InitPopupWindow();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("menu");// 必须创建一项  
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_HOME) {
			int nPid = android.os.Process.myPid();
		    android.os.Process.killProcess(nPid);
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onAttachedToWindow() {
		this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD);
		super.onAttachedToWindow();
	}
	
	
    /** 
     * 拦截MENU 
     */  
    @Override  
    public boolean onMenuOpened(int featureId, Menu menu) {  
        if(mPopupWindow != null){  
            if(!mPopupWindow.isShowing()){  
                /*最重要的一步：弹出显示   在指定的位置(parent)  最后两个参数 是相对于 x / y 轴的坐标*/  
            	mPopupWindow.showAtLocation(mGridView, Gravity.CENTER, 0, 0);  
            }  
        }  
        return false;// 返回为true 则显示系统menu  
    }  
    
    public void InitPopupWindow(){
    	mLayoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);      
		View view = mLayoutInflater.inflate(R.layout.menu, null);
		mGridView = (GridView)view.findViewById(R.id.menuGridChange);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGridView.setOnItemClickListener(new GridViewItemClick());
		mGridView.setFocusableInTouchMode(true);
		mGridView.setAdapter(mImageAdapter);
		//用Popupwindow弹出menu
		mPopupWindow = new PopupWindow(view,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		mGridView.setOnKeyListener(new OnKeyListener() {			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((keyCode == KeyEvent.KEYCODE_MENU)&&(mPopupWindow.isShowing())) { 
					mPopupWindow.dismiss();
		            return true;  
		        }
				return false;
			}
		});
    }
    
    public class ImageAdapter extends BaseAdapter {
		
		private Context context;
		
		public ImageAdapter(Context context) {
			this.context = context;
		}
		
		@Override
		public int getCount() {
			return resArray.length;
		}

		@Override
		public Object getItem(int arg0) {
			return resArray[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			LinearLayout linear = new LinearLayout(context);
			new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			
			linear.setOrientation(LinearLayout.VERTICAL);
			//装进一张图片
			ImageView iv = new ImageView(context);
			iv.setImageBitmap(((BitmapDrawable)context.getResources().getDrawable(resArray[arg0])).getBitmap());
			LinearLayout.LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params2.gravity=Gravity.CENTER;
			linear.addView(iv, params2);
			//显示文字
			TextView tv = new TextView(context);
			tv.setText(title[arg0]);
			tv.setTextColor(Color.rgb(255, 250, 250));  
			LinearLayout.LayoutParams params3 = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params3.gravity=Gravity.CENTER;
			
			linear.addView(tv, params3);
			
			return linear;
		}
	}
	
	class GridViewItemClick implements OnItemClickListener{
		/* (non-Javadoc)
		 * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
		 */
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = new Intent();
			switch (position) {
			case 0:
				Log.v(TAG, "系统配置");
				intent.setClass(BaseActivity.this, SystemConfigActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent); 
				mPopupWindow.dismiss();
				break;
			case 1:
				Log.v(TAG, "同步数据");
				intent.setClass(BaseActivity.this, SyncDataActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				mPopupWindow.dismiss();
				break;
			case 2:
				Log.v(TAG, "退出");
				mPopupWindowDialog.exitDialog();
				mPopupWindow.dismiss();
				break;
			}
		}
	}
}


