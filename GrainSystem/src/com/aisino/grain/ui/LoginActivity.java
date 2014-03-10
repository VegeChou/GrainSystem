package com.aisino.grain.ui;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

import com.aisino.grain.AppManagement;
import com.aisino.grain.R;
import com.aisino.grain.beans.GetUserInfoByNameRequest;
import com.aisino.grain.beans.GetUserInfoByRFIDRequest;
import com.aisino.grain.beans.GetUserInfoRequest;
import com.aisino.grain.beans.GetUserInfoResponse;
import com.aisino.grain.beans.ResultUserLoginInfo;
import com.aisino.grain.beans.ServiceAvailableTestRequest;
import com.aisino.grain.model.Constants;
import com.aisino.grain.model.rest.RestWebServiceAdapter;
import com.aisino.grain.model.rfid.RfidAdapter;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.aisino.grain.model.db.AcountInfo;
import com.aisino.grain.service.ReadCardService;
import com.aisino.grain.ui.demo.MainActivity;
import com.aisino.grain.ui.util.WaitDialog;

public class LoginActivity extends BaseActivity {
	private static final String TAG = "LoginActivity";
	
	private Context mContext = null;
	
	//多线程
	public Handler mHandler = null;						//线程和UI传递消息
	public Thread mThread = null;
	
	//UI
	private Button mLoginButton = null;						//登录按钮
	private Button mReadCardLoginButton = null;				//读卡登录按钮
	private Button mClearButton = null;						//清除用户信息按钮
	private EditText mUserEditText = null;					//用户名输入框
	private EditText mPasswordEditText = null;				//密码输入框
	private CheckBox mCheckBox = null;						//保存用户名和密码
	//显示连接图标
	private static WindowManager mWindowManager = null;
	private WindowManager.LayoutParams wmParams = null;
	public static ImageView mImageView = null;
	//Menu菜单
	private PopupWindow mPopupWindowMenu = null;			//Menu菜单
	private LayoutInflater mLayoutInflater = null;
	private TextView mTextView = null;
	
	//Rest
	private RestWebServiceAdapter mRestWebServiceAdapter = null;										//webservice
	private GetUserInfoRequest mGetUserInfoRequest = null;			//Rest登录请求
	private GetUserInfoResponse mGetUserInfoResponse = null;		//Rest登录回复
	private boolean mValidResult = false;
	private String mInvalidReason = null;							//登录结果
	private ResultUserLoginInfo mResultUserLoginInfo = null;
	private boolean mServiceAvailableTestResponse = false;
	
	//Data
	//账户信息
	private String mAcountName = null;					//登录账户名
	private String mAcountPassword = null;				//登录密码
	private String mCardID = null;						//人员卡ID
	private String mOperations= null;					//操作权限代码
	private String mWarehouses = null;					//能值仓库ID
	private int mCurrentUserID = -1;					//值仓User ID
	
	private static int count = 0;
	private static int times = 0;
	
	//本地存储
	private SharedPreferences mUserInfoSharedPreferences = null;	//保存用户名和密码
	private Editor mUserInfoEditor = null;							//用户名密码SharedPreferences编辑器
	private SharedPreferences mLoginSharedPreferences = null;		//保存登录信息
	private Editor mLoginEditor = null;								//登录信息SharedPreferences编辑器
	private boolean mUserInfoChecked = false;						//保存用户名密码是否选中
	
	//DB
	private Dao<AcountInfo, Integer> mAcountInfoDao =  null;	//账户信息Dao
	private List<AcountInfo> mAcountInfolist = null;			//查询结果
	private AcountInfo mAcountInfo = null;						//AcountInfo对象
	
	//RFID
	private RfidAdapter mRfidAdapter = null;				//rfid读卡适配器
	
	//Flag
	private boolean mReadCardFlag = false;					//读卡登录标记
	private boolean mLocalCheck = false;					//本地登录标记
	private boolean mLoginFlag = false;						//登陆成功标识
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);
		super.onCreate(savedInstanceState);
		
		InitCtrl();
	}
    
    /**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	初始化函数
	 *
	 */
	private void InitCtrl() {
		mContext = this;
		//Rest
		mRestWebServiceAdapter = RestWebServiceAdapter.getInstance(mContext);
		//DB
		mAcountInfoDao = getHelper().getAcountInfoDao();
		mAcountInfolist = new ArrayList<AcountInfo>();
		mAcountInfo = new AcountInfo();
		//本地存储
		mUserInfoSharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		mUserInfoEditor = mUserInfoSharedPreferences.edit();//获取编辑器
		mLoginSharedPreferences = getSharedPreferences("LoginInformation", Context.MODE_PRIVATE);
		mLoginEditor = mLoginSharedPreferences.edit();//获取编辑器
		//UI
		mLoginButton = (Button)findViewById(R.id.login_btn_login);
		mReadCardLoginButton = (Button)findViewById(R.id.login_btn_read_card_login);
		mClearButton = (Button)findViewById(R.id.login_btn_clear);
		mUserEditText = (EditText)findViewById(R.id.login_et_login_user);
		mPasswordEditText = (EditText)findViewById(R.id.login_et_login_password);
		mCheckBox = (CheckBox)findViewById(R.id.login_cb_save);
		
		mUserEditText.addTextChangedListener(new UserWatcher());
		mLoginButton.setOnClickListener(new LoginOnClickListener());
		mReadCardLoginButton.setOnClickListener(new ReadCardLoginOnClickListener());
		mClearButton.setOnClickListener(new ClearOnClickListener());
		mCheckBox.setOnCheckedChangeListener(new UserInfoCheckedChangeListener());
		
		//加载登陆信息
		LoadUserInfo();
		
		//初始化菜单
		InitPopupWindowMenu();
		
		//启动网络监听
		StartNetListener();
		
		//启动读卡服务
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(),ReadCardService.class);
		startService(intent);
	}
	
	private void StartNetListener() {
		//显示网络连接状态图标
		DisplayConView();
		
		mHandler = new Handler(){
			public void handleMessage(android.os.Message msg) {
				super.handleMessage(msg);
				switch(msg.what){
					case 0:
						NET_AVILIABE = false;
						LoginActivity.mImageView.setImageResource(R.drawable.unconnection);
						break;
					case 1:
						NET_AVILIABE = true;
						LoginActivity.mImageView.setImageResource(R.drawable.connection);
						break;
					default:
						break;
				}
			}
		};
		
		new Thread(new Runnable() {
			@Override
			public void run() {
				count++;
				Log.v(TAG, "Thread start count = " + count);
				while (THREAD_FLAG) {
					times++;
					Log.v(TAG, "Enter thread times = " + times + "---" + NET_CON_FLAG);
					if (NET_CON_FLAG) {
						Message msg = new Message();
						ServiceAvailableTestRequest serviceAvailableTestRequest = new ServiceAvailableTestRequest();
						mServiceAvailableTestResponse = mRestWebServiceAdapter.Rest(serviceAvailableTestRequest);
						if (mServiceAvailableTestResponse == false) {
							msg.what = 0; //消息0:回复为空
							mHandler.sendMessage(msg);
						} else {
							msg.what = 1; //消息1:回复非空
							mHandler.sendMessage(msg);
						}
					}
					//5s查询一次
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}
	
	private void LoadUserInfo() {
		//是否保存用户名和密码
		mUserInfoChecked = mUserInfoSharedPreferences.getBoolean("checkbox", false);
		if (mUserInfoChecked) {
			mAcountName = mUserInfoSharedPreferences.getString("user", "");
			mAcountPassword = mUserInfoSharedPreferences.getString("password", "");
			mUserEditText.setText(mAcountName);
			mPasswordEditText.setText(mAcountPassword);
			mCheckBox.setChecked(true);
		}else {
			mCheckBox.setChecked(false);
		}
	}
	
	public void InitPopupWindowMenu(){
    	mLayoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);      
		View view = mLayoutInflater.inflate(R.layout.menu_system_config, null);
		mTextView = (TextView)view.findViewById(R.id.tv);
		mTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, SystemConfigActivity.class);
				startActivity(intent); 
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
	
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	登录按钮响应函数
	 *
	 */
	class LoginOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
//			//fast start test
//			Intent intent1 = new Intent();
//			intent1.setClass(LoginActivity.this, com.aisino.grain.ui.MainActivity.class);
//			startActivity(intent1);
//			finish();
			
			//获取界面输入信息
			mAcountName = mUserEditText.getText().toString().trim();
			mAcountPassword = mPasswordEditText.getText().toString();
			
			//是否进入学习模式
			if ((mAcountName.equals("0")) && (mAcountPassword.equals("0"))) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				return;
			}
			
			//校验输入信息
			if ((mAcountName.equals("")) || (mAcountPassword.equals(""))) {
				mPopupWindowDialog.showMessage(getString(R.string.User_Password_Empty));
				return;
			}
			
			//生成用户名密码登录请求对象
			mGetUserInfoRequest = CreateGetUserInfoByNameRequest();
			
			//登录
			Login();
		}
	}
	
	private GetUserInfoRequest CreateGetUserInfoByNameRequest() {
		GetUserInfoByNameRequest getUserInfoByNameRequest = new GetUserInfoByNameRequest();
		if (mAcountName == null) {
			mAcountName = "";
		}
		if (mAcountPassword == null) {
			mAcountPassword = "";
		}
		getUserInfoByNameRequest.setLoginName(mAcountName);
		getUserInfoByNameRequest.setPassword(mAcountPassword);
		
		return getUserInfoByNameRequest;
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	读卡登录按钮响应函数
	 *
	 */
	class ReadCardLoginOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (mRfidAdapter == null)
				mRfidAdapter = RfidAdapter.getInstance(mContext);
			if (HasCard()) {
				//读卡成功,联网校验
				mReadCardFlag = true;
				//生成读卡登录请求对象
				mGetUserInfoRequest = CreateGetUserInfoByRFIDRequest();		//生成登录请求
				Login();
			} else {
				mPopupWindowDialog.showMessage("读卡失败");
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	清除图标按钮响应函数
	 *
	 */
	class ClearOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mUserEditText.setText("");
			mPasswordEditText.setText("");
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	保存用户名和密码选中监听函数
	 *
	 */
	class UserInfoCheckedChangeListener implements OnCheckedChangeListener{
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				mUserInfoChecked = true;
				mUserInfoEditor.putBoolean("checkbox", true);
				mUserInfoEditor.commit();
			} else {
				mUserInfoChecked = false;
				mUserInfoEditor.putBoolean("checkbox", false);
				mUserInfoEditor.commit();
			}
		}
	}
	
	class UserWatcher implements TextWatcher{
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			Log.i("UserWatcher", "afterTextChanged");
			mAcountName = mUserEditText.getText().toString();
			mPasswordEditText.setText("");
			mCheckBox.setChecked(false);
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
	}
	
	@Override
	protected void onResume() {
		mUserInfoChecked = mUserInfoSharedPreferences.getBoolean("checkbox", false);
		if (mUserInfoChecked) {
			mAcountName = mUserInfoSharedPreferences.getString("user", "");
			mAcountPassword = mUserInfoSharedPreferences.getString("password", "");
			mUserEditText.setText(mAcountName);
			mPasswordEditText.setText(mAcountPassword);
			mCheckBox.setChecked(true);
		}else {
			mCheckBox.setChecked(false);
		}
		super.onResume();
	}
	
	private GetUserInfoRequest CreateGetUserInfoByRFIDRequest() {
		GetUserInfoByRFIDRequest getUserInfoByRFIDRequest = new GetUserInfoByRFIDRequest();
		getUserInfoByRFIDRequest.setUserRFID(mCardID);
		
		return getUserInfoByRFIDRequest;
	}
	
	private int LoginWebService(GetUserInfoRequest getUserInfo) {
		if (getUserInfo instanceof GetUserInfoByNameRequest) {
			mGetUserInfoResponse = (GetUserInfoResponse)mRestWebServiceAdapter.Rest((GetUserInfoByNameRequest)getUserInfo);
		}
		if (getUserInfo instanceof GetUserInfoByRFIDRequest) {
			mGetUserInfoResponse = (GetUserInfoResponse)mRestWebServiceAdapter.Rest((GetUserInfoByRFIDRequest)getUserInfo);
		}
		
		if (mGetUserInfoResponse == null) {
			return Constants.ONLINE_LOGIN_NET_FAILED;
		}
		
		mInvalidReason = mGetUserInfoResponse.getInvalidReason();
		mResultUserLoginInfo = mGetUserInfoResponse.getResultUserLoginInfo();
		mValidResult = mGetUserInfoResponse.getValidResult();
		
		if (mValidResult) {
			if (mResultUserLoginInfo != null) {
				mOperations = mResultUserLoginInfo.getUserRight();
				mWarehouses = mResultUserLoginInfo.getWareHouse();
				mCurrentUserID = mResultUserLoginInfo.getUserID();
				//数据库Bean
				mAcountInfo.setAcount_name(mResultUserLoginInfo.getLoginName());
				mAcountInfo.setPassword(mResultUserLoginInfo.getPassword());
				mAcountInfo.setTag_id(mResultUserLoginInfo.getUserRFID());
				mAcountInfo.setActivate_flag(mResultUserLoginInfo.getUserRFIDState());
				mAcountInfo.setOperation_code(mOperations);
				mAcountInfo.setWahouse_id(mWarehouses);
				mAcountInfo.setUser_id(mCurrentUserID);
			}
			return Constants.ONLINE_LOGIN_SUCCESS;
		} else {
			return Constants.ONLINE_LOGIN_CHECK_FAILED;
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	当前登录用户名在本地数据库中
	 *
	 * @return	true：当前数据库中包含该用户，否则false
	 */
	private boolean UserIsExistInDatabase() {
		try {
			mAcountInfolist = mAcountInfoDao.queryForEq("acount_name", mAcountName);	//查询当前用户是否存在
			if (mAcountInfolist.size() == 0) {
				return false;
			}else {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	用户登录本地校验函数
	 *
	 * @return	返回校验结果：Constants.LOCAL_LOGIN_FAILED：本地校验失败；Constants.LOCAL_LOGIN_SUCCESS：本地校验成功；
	 * 						Constants.LOCAL_LOGIN_SUCCESS_CARD_NOT_ACTIVITY：本地校验成功但是卡未激活
	 */
	private int LocalVerify() {
		//对应AcountInfo表中激活状态变量activate_flag
		int activate_flag = -1;
		
		try {
			if (mReadCardFlag) {	//读卡登陆，校验卡号
				QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
				qb.where().eq("tag_id", mCardID);
				mAcountInfolist = qb.query();	//查询当前用户是否登陆成功
				if (mAcountInfolist.size() == 0) {
					return Constants.LOCAL_LOGIN_FAILED;
				}else {
					//获取激活状态
					activate_flag = mAcountInfolist.get(0).getActivate_flag();
					if (activate_flag == 1) {	//卡已激活
						return Constants.LOCAL_LOGIN_SUCCESS;
					} else {
						return Constants.LOCAL_LOGIN_SUCCESS_CARD_NOT_ACTIVITY;
					}
				}
			} else {	//校验用户名和密码
				QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
				qb.where().eq("acount_name", mAcountName).and().eq("password",mAcountPassword);
				mAcountInfolist = qb.query();	//查询当前用户是否登陆成功
				if (mAcountInfolist.size() == 0) {
					return Constants.LOCAL_LOGIN_FAILED;
				}else {
					return Constants.LOCAL_LOGIN_SUCCESS;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return Constants.LOCAL_LOGIN_FAILED;
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	读卡函数
	 *
	 * @return	返回读卡是否成功状态
	 */
	private boolean HasCard() {
		mCardID = mRfidAdapter.HasCard();
		if (mCardID == null) {
			mCardID = "";	//读卡失败，卡号赋值为空
			return false;
		}
		return true;
	}
	
	private void Login() {
		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "正在登录中......");
		OnCancelListener LoadingDialogCancelListener = new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				if (mLoginFlag) {
					Intent intent = new Intent();
					intent.setClass(LoginActivity.this, com.aisino.grain.ui.MainActivity.class);
					startActivity(intent);
					finish();
				}
			}
		};
		mProcessDialog.setOnCancelListener(LoadingDialogCancelListener);
		mProcessDialog.show();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Looper.prepare();
				LoginBusiness();
				Looper.loop();
			}
		}).start();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	登录业务函数
	 *
	 */
	private void LoginBusiness() {
		int res = LoginWebService(mGetUserInfoRequest);	//通过WebSerivce获取登录返回结果
		if (res == Constants.ONLINE_LOGIN_SUCCESS) {	//在线登录成功
			OnlineLoginSuccess();
		} else if(res == Constants.ONLINE_LOGIN_NET_FAILED){ //在线网络登录失败
			//本地验证
			int result = LocalVerify();
			if (result == Constants.LOCAL_LOGIN_SUCCESS) {
				LocalLoginSuccess();
			} else if (result == Constants.LOCAL_LOGIN_FAILED) {
				//本地校验失败，提示网络连接失败，联网后登录
				mPopupWindowDialog.showMessage(getString(R.string.Login_Failed));
				mLoginFlag = false;
			} else if (result == Constants.LOCAL_LOGIN_SUCCESS_CARD_NOT_ACTIVITY) {
				mPopupWindowDialog.showMessage("卡未激活，请先激活卡片");
				mLoginFlag = false;
			}
		} else if (res == Constants.ONLINE_LOGIN_CHECK_FAILED) {
			mPopupWindowDialog.showMessage(mInvalidReason);
			mLoginFlag = false;
		} 
		mProcessDialog.cancel();
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	获取当前登录用户操作权限
	 *
	 * @return	可操作权限列表
	 */
	private String GetOperations() {
		try {
			if (mLocalCheck == true) {	//本地登录
				if (mReadCardFlag == true) {	//读卡登录
					QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
					qb.where().eq("tag_id", mCardID);
					mAcountInfolist = qb.query();	//查询当前用户是否登陆成功
					if (mAcountInfolist.size() == 0) {
						return null;
					}else {
						return mAcountInfolist.get(0).getOperation_code();
					} 
				} else {	//用户名密码登录
					QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
					qb.where().eq("acount_name", mAcountName).and().eq("password", mAcountPassword);
					mAcountInfolist = qb.query();	//查询当前用户是否登陆成功
					if (mAcountInfolist.size() == 0) {
						return null;
					}else {
						return mAcountInfolist.get(0).getOperation_code();
					}
				} 
			} else {	//联网登录
				return mOperations;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	获取能够值仓仓库id字符串
	 *
	 * @return	可值仓仓库id字符串
	 */
	private String GetWahouseID() {
		try {
			if (mLocalCheck == true) {	//本地登录
				if (mReadCardFlag == true) {	//读卡登录
					QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
					qb.where().eq("tag_id", mCardID);
					mAcountInfolist = qb.query();	//查询当前用户是否登陆成功
					if (mAcountInfolist.size() == 0) {
						return null;
					}else {
						return mAcountInfolist.get(0).getWahouse_id();
					} 
				} else {	//用户名密码登录
					QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
					qb.where().eq("acount_name", mAcountName).and().eq("password", mAcountPassword);
					mAcountInfolist = qb.query();	//查询当前用户是否登陆成功
					if (mAcountInfolist.size() == 0) {
						return null;
					}else {
						return mAcountInfolist.get(0).getWahouse_id();
					}
				} 
			} else {	//联网登录
				return mWarehouses;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void DestroyConView(){
		if (mWindowManager != null && mImageView != null) {
			mWindowManager.removeView(mImageView);
			mWindowManager = null;
			mImageView = null;
		}		
	}
	
    private void DisplayConView(){
    	mImageView = new ImageView(this);
    	mImageView.setImageResource(R.drawable.unconnection);
    	//获取WindowManager
    	mWindowManager = (WindowManager)getApplicationContext().getSystemService("window");
        //设置LayoutParams(全局变量）相关参数
    	wmParams = new WindowManager.LayoutParams();

         /**
         *以下都是WindowManager.LayoutParams的相关属性
         * 具体用途可参考SDK文档
         */
        wmParams.type=LayoutParams.TYPE_PHONE;   //设置window type
        wmParams.format=PixelFormat.RGBA_8888;   //设置图片格式，效果为背景透明

        //设置Window flag
        wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL
                              | LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * 下面的flags属性的效果形同“锁定”。
         * 悬浮窗不可触摸，不接受任何事件,同时不影响后面的事件响应。
         wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL 
                               | LayoutParams.FLAG_NOT_FOCUSABLE
                               | LayoutParams.FLAG_NOT_TOUCHABLE;
        */
        
        
        wmParams.gravity=Gravity.LEFT|Gravity.TOP;   //调整悬浮窗口至左上角
        //以屏幕左上角为原点，设置x、y初始值
        wmParams.x=280;
        wmParams.y=0;
        
        //设置悬浮窗口长宽数据
        wmParams.width=40;
        wmParams.height=30;
    
        //显示myFloatView图像
        mWindowManager.addView(mImageView, wmParams);
    }
    
	private int GetCurrentUserID(String acountName) {
		QueryBuilder<AcountInfo,Integer> queryBuilder = mAcountInfoDao.queryBuilder();
		int userID = -1;
		try {
			queryBuilder.where().eq("acount_name", acountName);
			List<AcountInfo> acountInfos = queryBuilder.query();
			if (acountInfos.size()>0) {
				userID = acountInfos.get(0).getUser_id();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userID;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//弹出自定义Menu
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("menu");// 必须创建一项  
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			BaseActivity.THREAD_FLAG = false;
			LoginActivity.DestroyConView();
			AppManagement.getInstance().exit();
		}
		return super.onKeyDown(keyCode, event);
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
    
    private void OnlineLoginSuccess() {
    	//保存当前登录用户名和用户ID
		mLoginEditor.putString("current_user", mAcountName);
		mLoginEditor.putInt("current_user_id", mCurrentUserID);
		mLoginEditor.commit();
		
		//如果保存用户名密码选中，保存登陆账户信息
		if (mUserInfoChecked) {
			mUserInfoEditor.putString("user", mAcountName);
			mUserInfoEditor.putString("password", mAcountPassword);
			mUserInfoEditor.commit();
		}
		
		boolean result = UserIsExistInDatabase();	//用户在数据库中是否存在
		if (result) {
			try {	//更新本地数据库
				mAcountInfoDao.createOrUpdate(mAcountInfo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {	//插入本地数据库
				mAcountInfoDao.create(mAcountInfo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//保存权限代码
		String Operations = null;
		Operations = GetOperations();
		if (Operations == null) {
			mPopupWindowDialog.showMessage("获取权限代码失败");
		}
		mLoginEditor.putString("operations", Operations);
		mLoginEditor.commit();
		
		//保存值仓仓库ID串
		String warehouse_id = null;
		warehouse_id = GetWahouseID();
		if (warehouse_id == null) {
			mPopupWindowDialog.showMessage("值仓仓库ID为空");
		}
		mLoginEditor.putString("warehouse_id", warehouse_id);
		mLoginEditor.commit();
		
		mReadCardFlag = false;
		mLocalCheck = false;
		mLoginFlag = true;
	}
    
    private void LocalLoginSuccess() {
    	//本地校验成功，进入系统主界面
		mLocalCheck = true;
		
		//保存当前登录用户名和用户ID
		mLoginEditor.putString("current_user", mAcountName);
		//查询用户ID
		mCurrentUserID = GetCurrentUserID(mAcountName);
		if (mCurrentUserID == -1) {
			mPopupWindowDialog.showMessage("本地登录查询用户ID失败");
			return;
		}
		mLoginEditor.putInt("current_user_id", mCurrentUserID);
		mLoginEditor.commit();
		//如果保存用户名密码选中，保存登陆账户信息
		if (mUserInfoChecked) {
			mUserInfoEditor.putString("user", mAcountName);
			mUserInfoEditor.putString("password", mAcountPassword);
			mUserInfoEditor.commit();
		}
		
		//保存权限代码
		String Operations = null;
		Operations = GetOperations();
		if (Operations == null) {
			mPopupWindowDialog.showMessage("获取权限代码失败");
		}
		mLoginEditor.putString("operations", Operations);
		mLoginEditor.commit();
		
		//保存值仓仓库ID串
		String warehouse_id = null;
		warehouse_id = GetWahouseID();
		if (warehouse_id == null) {
			mPopupWindowDialog.showMessage("获取值仓仓库ID串失败");
		}
		mLoginEditor.putString("warehouse_id", warehouse_id);
		mLoginEditor.commit();
		mReadCardFlag = false;
		mLocalCheck = false;
		mLoginFlag = true;
		mProcessDialog.cancel();
	}
}
