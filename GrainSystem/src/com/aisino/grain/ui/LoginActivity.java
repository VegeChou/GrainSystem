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
	
	//���߳�
	public Handler mHandler = null;						//�̺߳�UI������Ϣ
	public Thread mThread = null;
	
	//UI
	private Button mLoginButton = null;						//��¼��ť
	private Button mReadCardLoginButton = null;				//������¼��ť
	private Button mClearButton = null;						//����û���Ϣ��ť
	private EditText mUserEditText = null;					//�û��������
	private EditText mPasswordEditText = null;				//���������
	private CheckBox mCheckBox = null;						//�����û���������
	//��ʾ����ͼ��
	private static WindowManager mWindowManager = null;
	private WindowManager.LayoutParams wmParams = null;
	public static ImageView mImageView = null;
	//Menu�˵�
	private PopupWindow mPopupWindowMenu = null;			//Menu�˵�
	private LayoutInflater mLayoutInflater = null;
	private TextView mTextView = null;
	
	//Rest
	private RestWebServiceAdapter mRestWebServiceAdapter = null;										//webservice
	private GetUserInfoRequest mGetUserInfoRequest = null;			//Rest��¼����
	private GetUserInfoResponse mGetUserInfoResponse = null;		//Rest��¼�ظ�
	private boolean mValidResult = false;
	private String mInvalidReason = null;							//��¼���
	private ResultUserLoginInfo mResultUserLoginInfo = null;
	private boolean mServiceAvailableTestResponse = false;
	
	//Data
	//�˻���Ϣ
	private String mAcountName = null;					//��¼�˻���
	private String mAcountPassword = null;				//��¼����
	private String mCardID = null;						//��Ա��ID
	private String mOperations= null;					//����Ȩ�޴���
	private String mWarehouses = null;					//��ֵ�ֿ�ID
	private int mCurrentUserID = -1;					//ֵ��User ID
	
	private static int count = 0;
	private static int times = 0;
	
	//���ش洢
	private SharedPreferences mUserInfoSharedPreferences = null;	//�����û���������
	private Editor mUserInfoEditor = null;							//�û�������SharedPreferences�༭��
	private SharedPreferences mLoginSharedPreferences = null;		//�����¼��Ϣ
	private Editor mLoginEditor = null;								//��¼��ϢSharedPreferences�༭��
	private boolean mUserInfoChecked = false;						//�����û��������Ƿ�ѡ��
	
	//DB
	private Dao<AcountInfo, Integer> mAcountInfoDao =  null;	//�˻���ϢDao
	private List<AcountInfo> mAcountInfolist = null;			//��ѯ���
	private AcountInfo mAcountInfo = null;						//AcountInfo����
	
	//RFID
	private RfidAdapter mRfidAdapter = null;				//rfid����������
	
	//Flag
	private boolean mReadCardFlag = false;					//������¼���
	private boolean mLocalCheck = false;					//���ص�¼���
	private boolean mLoginFlag = false;						//��½�ɹ���ʶ
	
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
	 * @description	��ʼ������
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
		//���ش洢
		mUserInfoSharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		mUserInfoEditor = mUserInfoSharedPreferences.edit();//��ȡ�༭��
		mLoginSharedPreferences = getSharedPreferences("LoginInformation", Context.MODE_PRIVATE);
		mLoginEditor = mLoginSharedPreferences.edit();//��ȡ�༭��
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
		
		//���ص�½��Ϣ
		LoadUserInfo();
		
		//��ʼ���˵�
		InitPopupWindowMenu();
		
		//�����������
		StartNetListener();
		
		//������������
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(),ReadCardService.class);
		startService(intent);
	}
	
	private void StartNetListener() {
		//��ʾ��������״̬ͼ��
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
							msg.what = 0; //��Ϣ0:�ظ�Ϊ��
							mHandler.sendMessage(msg);
						} else {
							msg.what = 1; //��Ϣ1:�ظ��ǿ�
							mHandler.sendMessage(msg);
						}
					}
					//5s��ѯһ��
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
		//�Ƿ񱣴��û���������
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
		//��Popupwindow����menu
		mPopupWindowMenu = new PopupWindow(view,LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mPopupWindowMenu.setFocusable(true);
		mPopupWindowMenu.setBackgroundDrawable(new ColorDrawable());
    }
	
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	��¼��ť��Ӧ����
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
			
			//��ȡ����������Ϣ
			mAcountName = mUserEditText.getText().toString().trim();
			mAcountPassword = mPasswordEditText.getText().toString();
			
			//�Ƿ����ѧϰģʽ
			if ((mAcountName.equals("0")) && (mAcountPassword.equals("0"))) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				startActivity(intent);
				return;
			}
			
			//У��������Ϣ
			if ((mAcountName.equals("")) || (mAcountPassword.equals(""))) {
				mPopupWindowDialog.showMessage(getString(R.string.User_Password_Empty));
				return;
			}
			
			//�����û��������¼�������
			mGetUserInfoRequest = CreateGetUserInfoByNameRequest();
			
			//��¼
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
	 * @description	������¼��ť��Ӧ����
	 *
	 */
	class ReadCardLoginOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (mRfidAdapter == null)
				mRfidAdapter = RfidAdapter.getInstance(mContext);
			if (HasCard()) {
				//�����ɹ�,����У��
				mReadCardFlag = true;
				//���ɶ�����¼�������
				mGetUserInfoRequest = CreateGetUserInfoByRFIDRequest();		//���ɵ�¼����
				Login();
			} else {
				mPopupWindowDialog.showMessage("����ʧ��");
			}
		}
	}
	
	/**
	 * 
	 * @author zwz
	 * @date 2013-7-15
	 * @description	���ͼ�갴ť��Ӧ����
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
	 * @description	�����û���������ѡ�м�������
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
				//���ݿ�Bean
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
	 * @description	��ǰ��¼�û����ڱ������ݿ���
	 *
	 * @return	true����ǰ���ݿ��а������û�������false
	 */
	private boolean UserIsExistInDatabase() {
		try {
			mAcountInfolist = mAcountInfoDao.queryForEq("acount_name", mAcountName);	//��ѯ��ǰ�û��Ƿ����
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
	 * @description	�û���¼����У�麯��
	 *
	 * @return	����У������Constants.LOCAL_LOGIN_FAILED������У��ʧ�ܣ�Constants.LOCAL_LOGIN_SUCCESS������У��ɹ���
	 * 						Constants.LOCAL_LOGIN_SUCCESS_CARD_NOT_ACTIVITY������У��ɹ����ǿ�δ����
	 */
	private int LocalVerify() {
		//��ӦAcountInfo���м���״̬����activate_flag
		int activate_flag = -1;
		
		try {
			if (mReadCardFlag) {	//������½��У�鿨��
				QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
				qb.where().eq("tag_id", mCardID);
				mAcountInfolist = qb.query();	//��ѯ��ǰ�û��Ƿ��½�ɹ�
				if (mAcountInfolist.size() == 0) {
					return Constants.LOCAL_LOGIN_FAILED;
				}else {
					//��ȡ����״̬
					activate_flag = mAcountInfolist.get(0).getActivate_flag();
					if (activate_flag == 1) {	//���Ѽ���
						return Constants.LOCAL_LOGIN_SUCCESS;
					} else {
						return Constants.LOCAL_LOGIN_SUCCESS_CARD_NOT_ACTIVITY;
					}
				}
			} else {	//У���û���������
				QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
				qb.where().eq("acount_name", mAcountName).and().eq("password",mAcountPassword);
				mAcountInfolist = qb.query();	//��ѯ��ǰ�û��Ƿ��½�ɹ�
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
	 * @description	��������
	 *
	 * @return	���ض����Ƿ�ɹ�״̬
	 */
	private boolean HasCard() {
		mCardID = mRfidAdapter.HasCard();
		if (mCardID == null) {
			mCardID = "";	//����ʧ�ܣ����Ÿ�ֵΪ��
			return false;
		}
		return true;
	}
	
	private void Login() {
		mProcessDialog = WaitDialog.createLoadingDialog(mContext, "���ڵ�¼��......");
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
	 * @description	��¼ҵ����
	 *
	 */
	private void LoginBusiness() {
		int res = LoginWebService(mGetUserInfoRequest);	//ͨ��WebSerivce��ȡ��¼���ؽ��
		if (res == Constants.ONLINE_LOGIN_SUCCESS) {	//���ߵ�¼�ɹ�
			OnlineLoginSuccess();
		} else if(res == Constants.ONLINE_LOGIN_NET_FAILED){ //���������¼ʧ��
			//������֤
			int result = LocalVerify();
			if (result == Constants.LOCAL_LOGIN_SUCCESS) {
				LocalLoginSuccess();
			} else if (result == Constants.LOCAL_LOGIN_FAILED) {
				//����У��ʧ�ܣ���ʾ��������ʧ�ܣ��������¼
				mPopupWindowDialog.showMessage(getString(R.string.Login_Failed));
				mLoginFlag = false;
			} else if (result == Constants.LOCAL_LOGIN_SUCCESS_CARD_NOT_ACTIVITY) {
				mPopupWindowDialog.showMessage("��δ������ȼ��Ƭ");
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
	 * @description	��ȡ��ǰ��¼�û�����Ȩ��
	 *
	 * @return	�ɲ���Ȩ���б�
	 */
	private String GetOperations() {
		try {
			if (mLocalCheck == true) {	//���ص�¼
				if (mReadCardFlag == true) {	//������¼
					QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
					qb.where().eq("tag_id", mCardID);
					mAcountInfolist = qb.query();	//��ѯ��ǰ�û��Ƿ��½�ɹ�
					if (mAcountInfolist.size() == 0) {
						return null;
					}else {
						return mAcountInfolist.get(0).getOperation_code();
					} 
				} else {	//�û��������¼
					QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
					qb.where().eq("acount_name", mAcountName).and().eq("password", mAcountPassword);
					mAcountInfolist = qb.query();	//��ѯ��ǰ�û��Ƿ��½�ɹ�
					if (mAcountInfolist.size() == 0) {
						return null;
					}else {
						return mAcountInfolist.get(0).getOperation_code();
					}
				} 
			} else {	//������¼
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
	 * @description	��ȡ�ܹ�ֵ�ֲֿ�id�ַ���
	 *
	 * @return	��ֵ�ֲֿ�id�ַ���
	 */
	private String GetWahouseID() {
		try {
			if (mLocalCheck == true) {	//���ص�¼
				if (mReadCardFlag == true) {	//������¼
					QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
					qb.where().eq("tag_id", mCardID);
					mAcountInfolist = qb.query();	//��ѯ��ǰ�û��Ƿ��½�ɹ�
					if (mAcountInfolist.size() == 0) {
						return null;
					}else {
						return mAcountInfolist.get(0).getWahouse_id();
					} 
				} else {	//�û��������¼
					QueryBuilder<AcountInfo, Integer> qb = mAcountInfoDao.queryBuilder();
					qb.where().eq("acount_name", mAcountName).and().eq("password", mAcountPassword);
					mAcountInfolist = qb.query();	//��ѯ��ǰ�û��Ƿ��½�ɹ�
					if (mAcountInfolist.size() == 0) {
						return null;
					}else {
						return mAcountInfolist.get(0).getWahouse_id();
					}
				} 
			} else {	//������¼
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
    	//��ȡWindowManager
    	mWindowManager = (WindowManager)getApplicationContext().getSystemService("window");
        //����LayoutParams(ȫ�ֱ�������ز���
    	wmParams = new WindowManager.LayoutParams();

         /**
         *���¶���WindowManager.LayoutParams���������
         * ������;�ɲο�SDK�ĵ�
         */
        wmParams.type=LayoutParams.TYPE_PHONE;   //����window type
        wmParams.format=PixelFormat.RGBA_8888;   //����ͼƬ��ʽ��Ч��Ϊ����͸��

        //����Window flag
        wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL
                              | LayoutParams.FLAG_NOT_FOCUSABLE;
        /*
         * �����flags���Ե�Ч����ͬ����������
         * ���������ɴ������������κ��¼�,ͬʱ��Ӱ�������¼���Ӧ��
         wmParams.flags=LayoutParams.FLAG_NOT_TOUCH_MODAL 
                               | LayoutParams.FLAG_NOT_FOCUSABLE
                               | LayoutParams.FLAG_NOT_TOUCHABLE;
        */
        
        
        wmParams.gravity=Gravity.LEFT|Gravity.TOP;   //�����������������Ͻ�
        //����Ļ���Ͻ�Ϊԭ�㣬����x��y��ʼֵ
        wmParams.x=280;
        wmParams.y=0;
        
        //�����������ڳ�������
        wmParams.width=40;
        wmParams.height=30;
    
        //��ʾmyFloatViewͼ��
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
		//�����Զ���Menu
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("menu");// ���봴��һ��  
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
     * ����MENU,ʵ���Զ���˵�
     */  
    @Override  
    public boolean onMenuOpened(int featureId, Menu menu) {  
        if(mPopupWindowMenu != null){  
            if(!mPopupWindowMenu.isShowing()){  
                /*����Ҫ��һ����������ʾ   ��ָ����λ��(parent)  ����������� ������� x / y �������*/  
            	mPopupWindowMenu.showAtLocation(mTextView, Gravity.CENTER, 0, 0);  
            } else {
            	mPopupWindowMenu.dismiss();
			}
        }  
        return false;// ����Ϊtrue ����ʾϵͳmenu  
    }  
    
    private void OnlineLoginSuccess() {
    	//���浱ǰ��¼�û������û�ID
		mLoginEditor.putString("current_user", mAcountName);
		mLoginEditor.putInt("current_user_id", mCurrentUserID);
		mLoginEditor.commit();
		
		//��������û�������ѡ�У������½�˻���Ϣ
		if (mUserInfoChecked) {
			mUserInfoEditor.putString("user", mAcountName);
			mUserInfoEditor.putString("password", mAcountPassword);
			mUserInfoEditor.commit();
		}
		
		boolean result = UserIsExistInDatabase();	//�û������ݿ����Ƿ����
		if (result) {
			try {	//���±������ݿ�
				mAcountInfoDao.createOrUpdate(mAcountInfo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			try {	//���뱾�����ݿ�
				mAcountInfoDao.create(mAcountInfo);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		//����Ȩ�޴���
		String Operations = null;
		Operations = GetOperations();
		if (Operations == null) {
			mPopupWindowDialog.showMessage("��ȡȨ�޴���ʧ��");
		}
		mLoginEditor.putString("operations", Operations);
		mLoginEditor.commit();
		
		//����ֵ�ֲֿ�ID��
		String warehouse_id = null;
		warehouse_id = GetWahouseID();
		if (warehouse_id == null) {
			mPopupWindowDialog.showMessage("ֵ�ֲֿ�IDΪ��");
		}
		mLoginEditor.putString("warehouse_id", warehouse_id);
		mLoginEditor.commit();
		
		mReadCardFlag = false;
		mLocalCheck = false;
		mLoginFlag = true;
	}
    
    private void LocalLoginSuccess() {
    	//����У��ɹ�������ϵͳ������
		mLocalCheck = true;
		
		//���浱ǰ��¼�û������û�ID
		mLoginEditor.putString("current_user", mAcountName);
		//��ѯ�û�ID
		mCurrentUserID = GetCurrentUserID(mAcountName);
		if (mCurrentUserID == -1) {
			mPopupWindowDialog.showMessage("���ص�¼��ѯ�û�IDʧ��");
			return;
		}
		mLoginEditor.putInt("current_user_id", mCurrentUserID);
		mLoginEditor.commit();
		//��������û�������ѡ�У������½�˻���Ϣ
		if (mUserInfoChecked) {
			mUserInfoEditor.putString("user", mAcountName);
			mUserInfoEditor.putString("password", mAcountPassword);
			mUserInfoEditor.commit();
		}
		
		//����Ȩ�޴���
		String Operations = null;
		Operations = GetOperations();
		if (Operations == null) {
			mPopupWindowDialog.showMessage("��ȡȨ�޴���ʧ��");
		}
		mLoginEditor.putString("operations", Operations);
		mLoginEditor.commit();
		
		//����ֵ�ֲֿ�ID��
		String warehouse_id = null;
		warehouse_id = GetWahouseID();
		if (warehouse_id == null) {
			mPopupWindowDialog.showMessage("��ȡֵ�ֲֿ�ID��ʧ��");
		}
		mLoginEditor.putString("warehouse_id", warehouse_id);
		mLoginEditor.commit();
		mReadCardFlag = false;
		mLocalCheck = false;
		mLoginFlag = true;
		mProcessDialog.cancel();
	}
}
