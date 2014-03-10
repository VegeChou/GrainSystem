package com.aisino.grain.ui.util;

import com.aisino.grain.AppManagement;
import com.aisino.grain.R;
import com.aisino.grain.ui.BaseActivity;
import com.aisino.grain.ui.LoginActivity;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @author zwz
 * @date 2013-8-30
 * @description
 *
 */
public class PopupWindowDialog extends PopupWindow{
	private LayoutInflater mLayoutInflater = null;
	private View mPopupWindowView = null;
	private PopupWindow mPopupWindow = null;
	
	private TextView mMessageTextView = null;
	private TextView mTitleTextView = null;
	private Button mOKButton = null;
	private Button mCancelButton = null;
	
	private boolean mDialgExitFlag = false;
	
	public PopupWindowDialog (Context context){
		super(context);
		mLayoutInflater = LayoutInflater.from(context);
//		mLayoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
		mPopupWindowView = mLayoutInflater.inflate(R.layout.popup_window_dialog, null,false);
		LinearLayout linearLayout =(LinearLayout)mPopupWindowView.findViewById(R.id.popup_window_root);
		linearLayout.setFocusableInTouchMode(true);
//		linearLayout.setOnKeyListener(new PopupWindowOnKeyListener());
		mPopupWindow= new PopupWindow(mPopupWindowView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,true);
		mMessageTextView = (TextView)mPopupWindowView.findViewById(R.id.popup_window_message);
		mTitleTextView = (TextView)mPopupWindowView.findViewById(R.id.popup_window_title);
		mOKButton = (Button)mPopupWindowView.findViewById(R.id.popup_window_ok);
		mOKButton.setOnClickListener(new OKOnClickListener());
		mCancelButton = (Button)mPopupWindowView.findViewById(R.id.popup_window_cancel);
		mCancelButton.setOnClickListener(new CancelOnClickListener());
	}
	
	public void showMessage(String msg) {
		mMessageTextView.setText(msg);
		mPopupWindow.showAtLocation(mPopupWindowView, Gravity.CENTER | Gravity.CENTER, 0, 0);
	}
	
	public void setCancelVisible(Boolean bool){
		if (bool) {
			mCancelButton.setVisibility(View.VISIBLE);
		} else {
			mCancelButton.setVisibility(View.GONE);
		}
	}
	
	public void setTitle(String text){
		mTitleTextView.setText(text);
	}
	
	public void setOKOnClickListener(OnClickListener clickListener) {
		mOKButton.setOnClickListener(clickListener);
	}
	
	public void setCancelOnClickListener(OnClickListener clickListener) {
		mCancelButton.setOnClickListener(clickListener);
	}
	
	public void dismiss(){
		mPopupWindow.dismiss();
	}
	
	public void exitDialog() {
		mDialgExitFlag = true;
		mTitleTextView.setText("退出系统");
		mMessageTextView.setText("确认退出?");
		setCancelVisible(true);
		mPopupWindow.showAtLocation(mPopupWindowView, Gravity.CENTER | Gravity.CENTER, 0, 0);
	}
	
	class OKOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			if (mDialgExitFlag) {
				mDialgExitFlag = false;
				BaseActivity.THREAD_FLAG = false;
				LoginActivity.DestroyConView();
				AppManagement.getInstance().exit();
			} else {
				mPopupWindow.dismiss();
			}
		}
	}
	
	class CancelOnClickListener implements OnClickListener{
		@Override
		public void onClick(View v) {
			mPopupWindow.dismiss();
		}
	}
}
