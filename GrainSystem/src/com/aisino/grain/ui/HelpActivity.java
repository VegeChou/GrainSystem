package com.aisino.grain.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.aisino.grain.R;
import com.aisino.grain.beans.ApkInfo;
import com.aisino.grain.model.Constants;
import com.aisino.grain.ui.util.WaitDialog;
import com.google.gson.Gson;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class HelpActivity extends BaseActivity {
	private static final String TAG = "HelpActivity";
	
	private static final int NOT_CON_SERVER = 0;
	private static final int NO_SD_CARD = 1;
	private static final int DOWNLOADING = 2;
	private static final int DOWNLOAD_FINISH = 3;
	private static final int DOWNLOAD_SIZE = 4;
	
	//UI
	private Button mSoftwareUpdateButton = null;
	private TextView mAboutTextView = null;
	private ProgressDialog mProgressDialog = null;
	private EditText mUpdateServerIPEditText = null;
	private EditText mUpdateServerPortEditText = null;
	private EditText mUpdateServerCheckPathEditText = null;
	private EditText mUpdateServerDownloadPathEditText = null;
	//Menu菜单
	private PopupWindow mPopupWindowMenu = null;			//Menu菜单
	private LayoutInflater mLayoutInflater = null;
	private TextView mTextView = null;
	//多线程
	private Handler mThreadHandler = null;
	
	//System
	private Context mContext = null;
	private SharedPreferences mUpdateServerSharedPreferences = null;
	private Editor mUpdateServerEditor = null;
	
	//Data
	private String mAbout = null;
	private ApkInfo mCurrentApkInfo = null;
	private ApkInfo mServerApkInfo = null;
	private String mUpdateServerIP = null;
	private String mUpdateServerPort = null;
	private String mUpdateServerCheckPath = null;
	private String mUpdateServerDownloadPath = null;
	private String mUpdateServerCheckURL = null;
	private String mUpdateServerDownloadURL = null;
	private int mFileSize = 0;
	private int mDownloadSize = 0;
	
	private Handler mHandler = new Handler();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        InitCtrl();
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//弹出自定义Menu
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("menu");// 必须创建一项  
		return true;
	}
    
    private void InitCtrl() {
    	//UI
    	mAboutTextView = (TextView)findViewById(R.id.help_tv_about);
    	mSoftwareUpdateButton = (Button)findViewById(R.id.help_btn_software_update);
    	mSoftwareUpdateButton.setOnClickListener(new SoftwareUpdateOnClickListener());
    	mThreadHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case NOT_CON_SERVER:
					Toast.makeText(mContext, "无法连接服务器,请检查网络!", Toast.LENGTH_LONG).show();
					break;
				case NO_SD_CARD:
					Toast.makeText(mContext, "下载更新包,请插入SD卡!", Toast.LENGTH_LONG).show();
					break;
				case DOWNLOADING:
					mProgressDialog.setProgress(mDownloadSize);
					break;
				case DOWNLOAD_FINISH:
					Toast.makeText(mContext, "下载已完成!", Toast.LENGTH_LONG).show();
					break;
				case DOWNLOAD_SIZE:
					mProgressDialog.setMax(mFileSize);
					break;
				default:
					break;
				}
				super.handleMessage(msg);
			}
		};
    	
    	//System
    	mContext = this;
    	
    	//Data
    	mCurrentApkInfo = new ApkInfo();
    	mServerApkInfo = new ApkInfo();
    	
    	//初始化菜单
    	InitPopupWindowMenu();
    	
    	//显示相关信息
    	DisplayInfo();
	}
    
    @Override
    protected void onResume() {
    	mUpdateServerSharedPreferences = getSharedPreferences("AppUpdate", Context.MODE_PRIVATE);
    	mUpdateServerEditor = mUpdateServerSharedPreferences.edit();
    	mUpdateServerIP = mUpdateServerSharedPreferences.getString("Update_Server_IP", getString(R.string.update_server_ip));
    	mUpdateServerPort = mUpdateServerSharedPreferences.getString("Update_Server_Port", getString(R.string.update_server_port));
    	mUpdateServerCheckPath = mUpdateServerSharedPreferences.getString("Update_Server_Check_Path", getString(R.string.update_server_check_path));
    	mUpdateServerDownloadPath = mUpdateServerSharedPreferences.getString("Update_Server_Download_Path", getString(R.string.update_server_download_path));
    	mUpdateServerCheckURL = "http://" + mUpdateServerIP + ":" + mUpdateServerPort + mUpdateServerCheckPath;
    	mUpdateServerDownloadURL = "http://" + mUpdateServerIP + ":" + mUpdateServerPort + mUpdateServerDownloadPath;
    	
    	super.onResume();
    }
    
    /** 
     * 拦截MENU,实现自定义菜单
     */  
    @Override  
    public boolean onMenuOpened(int featureId, Menu menu) {  
        if(mPopupWindowMenu != null){  
            if(!mPopupWindowMenu.isShowing()){  
                /*最重要的一步：弹出显示   在指定的位置(parent)  最后两个参数 是相对于 x / y 轴的坐标*/  
            	mPopupWindowMenu.showAtLocation(mTextView, Gravity.CENTER, 0, 0);  
            } else {
            	mPopupWindowMenu.dismiss();
			}
        }  
        return false;// 返回为true 则显示系统menu  
    }
    
	public void InitPopupWindowMenu(){
    	mLayoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);      
		View view = mLayoutInflater.inflate(R.layout.menu_settings, null);
		mTextView = (TextView)view.findViewById(R.id.tv);
		mTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				LayoutInflater factory = LayoutInflater.from(mContext);
                final View textEntryView = factory.inflate(R.layout.update_url_dialog, null);
                mUpdateServerIPEditText = (EditText) textEntryView.findViewById(R.id.update_url_dialog_et_update_server_ip);
                mUpdateServerPortEditText = (EditText) textEntryView.findViewById(R.id.update_url_dialog_et_update_server_port);
                mUpdateServerCheckPathEditText = (EditText) textEntryView.findViewById(R.id.update_url_dialog_et_update_server_check_path);
                mUpdateServerDownloadPathEditText = (EditText) textEntryView.findViewById(R.id.update_url_dialog_et_update_server_download_path);
                mUpdateServerIPEditText.setText(mUpdateServerIP);
                mUpdateServerPortEditText.setText(mUpdateServerPort);
                mUpdateServerCheckPathEditText.setText(mUpdateServerCheckPath);
                mUpdateServerDownloadPathEditText.setText(mUpdateServerDownloadPath);
                
                new AlertDialog.Builder(mContext)
            		.setTitle("更新服务器配置")
                    .setView(textEntryView)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
							
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mUpdateServerIP = mUpdateServerIPEditText.getText().toString();
							mUpdateServerEditor.putString("Update_Server_IP", mUpdateServerIP);
							mUpdateServerPort = mUpdateServerPortEditText.getText().toString();
							mUpdateServerEditor.putString("Update_Server_Port", mUpdateServerPort);
							mUpdateServerCheckPath = mUpdateServerCheckPathEditText.getText().toString();
							mUpdateServerEditor.putString("Update_Server_Check_Path", mUpdateServerCheckPath);
							mUpdateServerDownloadPath = mUpdateServerDownloadPathEditText.getText().toString();
							mUpdateServerEditor.putString("Update_Server_Download_Path", mUpdateServerDownloadPath);
							mUpdateServerEditor.commit();
						}
					})
                    .create().show();
                
				mPopupWindowMenu.dismiss();
			}
		});
		
		mTextView.setFocusableInTouchMode(true);
		mTextView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				if ((arg1 == KeyEvent.KEYCODE_MENU)&&(mPopupWindowMenu.isShowing())) { 
					mPopupWindowMenu.dismiss();
		            return true;  
		        }
				return false;
			}
		});
		//用Popupwindow弹出menu
		mPopupWindowMenu = new PopupWindow(view,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindowMenu.setFocusable(true);
		mPopupWindowMenu.setBackgroundDrawable(new ColorDrawable());
    }
    
    private void DisplayInfo() {
    	mCurrentApkInfo = GetCurrentVerInfo();
		mAbout = getString(R.string.title)+"\n\n"+getString(R.string.copyright)+"\n"+"Version:"+mCurrentApkInfo.getVersionName();
		mAboutTextView.setText(mAbout);
	}
    
    class SoftwareUpdateOnClickListener implements OnClickListener{
    	@Override
    	public void onClick(View v) {
    		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "正在校验中......");
    		OnCancelListener LoadingDialogCancelListener = new OnCancelListener() {
    			@Override
    			public void onCancel(DialogInterface dialog) {
    				
    			}
    		};
    		mProcessDialog.setOnCancelListener(LoadingDialogCancelListener);
    		mProcessDialog.show();
    		
    		new Thread(new Runnable() {
				
				@Override
				public void run() {
					Looper.prepare();
					try {
		    			//连接系统更新服务器,查看网络是否连接
						if (!CheckToUpdate()) {
							Message message = new Message();
							message.what = NOT_CON_SERVER;
							mThreadHandler.sendMessage(message);
						}
					} catch (NameNotFoundException e) {
						e.printStackTrace();
					}
					mProcessDialog.dismiss();
					Looper.loop();
				}
			}).start();
    	}
    }
	
	private ApkInfo GetCurrentVerInfo(){
		ApkInfo ApkInfo = new ApkInfo();
		try {
			ApkInfo.setPackageName(mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).packageName);
			ApkInfo.setVersionCode(mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode);
			ApkInfo.setVersionName(mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			ApkInfo.setVersionCode(0);
			ApkInfo.setVersionName("");
		}
		return ApkInfo;
	}
    
	//check new version and update
	private boolean CheckToUpdate() throws NameNotFoundException {
		if(GetServerVersion()){
			if((mServerApkInfo.getVersionCode() > mCurrentApkInfo.getVersionCode()) 
					&& (mServerApkInfo.getPackageName().equals(mCurrentApkInfo.getPackageName()))){	
				//Current Version is old
				ShowUpdateDialog();
			}
			return true;
		} else {
			return false;
		}
	}
	
	class UpdateOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mPopupWindowDialog.dismiss();
			ShowDownloadProgressBar();
		}
	}
	
	//show Update Dialog
	private void ShowUpdateDialog(){
		String msg = "当前版本:" + mCurrentApkInfo.getVersionName()
				+ "\n" + "发现新版本:" + mServerApkInfo.getVersionName()
				+ "\n" + "是否更新?";
		
		mPopupWindowDialog.setOKOnClickListener(new UpdateOnClickListener());
		mPopupWindowDialog.setTitle("软件更新");
		mPopupWindowDialog.setCancelVisible(true);
		mPopupWindowDialog.showMessage(msg);
	}
	
	private void ShowDownloadProgressBar() {
		mProgressDialog = new ProgressDialog(HelpActivity.this);
		mProgressDialog.setTitle("正在下载");
		mProgressDialog.setMax(mFileSize);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.show();
		new Thread(new Runnable() {
			
			@Override
			public void run(){
				DownloadUpdate(mUpdateServerDownloadURL);
			}
		}).start();
	}
	
	//Get Server Version from Server:version.json
	private boolean GetServerVersion() {
		try{
			String verjson = GetApkInfoFromServer(mUpdateServerCheckURL);
			Gson gson = new Gson();
			ApkInfo apkInfo = gson.fromJson(verjson, ApkInfo.class);
			
            try {
            	mServerApkInfo.setVersionCode(apkInfo.getVersionCode());
            	mServerApkInfo.setVersionName(apkInfo.getVersionName());
            	mServerApkInfo.setPackageName(apkInfo.getPackageName());
            } catch (Exception e) {
            	mServerApkInfo.setVersionCode(0);
            	mServerApkInfo.setVersionName("");
            	mServerApkInfo.setPackageName("");
                return false;
            }
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
			return false;
		}
		return true;
	}
	
	private void DownloadUpdate(String url){
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response;
		try {
			response = client.execute(get);
			
			HttpEntity entity = response.getEntity();
			mFileSize = (int)entity.getContentLength();
			Log.isLoggable("DownTag", mFileSize);
			
			Message message_size = new Message();
			message_size.what = DOWNLOAD_SIZE;
		    mThreadHandler.sendMessage(message_size);
			
			InputStream inputStream = entity.getContent();
			if(inputStream == null){
				throw new RuntimeException("isStream is null");
			}
			
			if (!ExistSDCard()) {
				Message message = new Message();
				message.what = NO_SD_CARD;
				mThreadHandler.sendMessage(message);
				mProgressDialog.dismiss();
				return;
			}
			
			File file = new File(Environment.getExternalStorageDirectory(),Constants.APP_NAME);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			
			byte[] buf = new byte[1024];
			int readNum = -1;
			do{
				readNum = inputStream.read(buf);
				if(readNum <= 0)
					break;
				fileOutputStream.write(buf, 0, readNum);
				mDownloadSize += readNum;
				Message message = new Message();
				message.what = DOWNLOADING;
			    mThreadHandler.sendMessage(message);
			    try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while(true);
			inputStream.close();
			fileOutputStream.close();
			
			Message message = new Message();
			message.what = DOWNLOAD_FINISH;
		    mThreadHandler.sendMessage(message);
			
			HaveDownLoad();
		}catch(ClientProtocolException e){
			e.printStackTrace();
			mProgressDialog.dismiss();
		}catch(IOException e){
			e.printStackTrace();
			mProgressDialog.dismiss();
		}
	}
	
	//cancel progressBar and start new App
	protected void HaveDownLoad() {
		mHandler.post(new Runnable(){
			public void run(){
				mProgressDialog.cancel();
				Dialog installDialog = new AlertDialog.Builder(HelpActivity.this)
				.setTitle("下载完成")
				.setMessage("是否安装新的应用")
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						InstallUpdateApk();
						finish();
						}
					})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
						}
					})
				.create();
				installDialog.show();
			}
		});
	}
	
	private void InstallUpdateApk() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(
				new File(Environment.getExternalStorageDirectory(),Constants.APP_NAME)),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
	/**
     * 获取网址内容
     * @param url
     * @return
     * @throws Exception
     */
     private String GetApkInfoFromServer(String url) throws Exception{
         StringBuilder sb = new StringBuilder();
         
         HttpClient client = new DefaultHttpClient();
         HttpParams httpParams = client.getParams();
         //设置网络超时参数
         HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
         HttpConnectionParams.setSoTimeout(httpParams, 8000);
         HttpResponse response = client.execute(new HttpGet(url));
         
         HttpEntity entity = response.getEntity();
         if (entity != null) {
             BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"), 8192);
             String line = null;
             while ((line = reader.readLine())!= null){
                 sb.append(line + "\n");
             }
             reader.close();
         }
         return sb.toString();
     } 
     
     private boolean ExistSDCard() {
   	  	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
   	  		return true;
   	  	} else
   	  		return false;
   	 }
}