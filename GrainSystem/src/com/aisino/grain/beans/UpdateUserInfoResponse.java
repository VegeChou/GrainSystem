package com.aisino.grain.beans;

import java.util.List;

public class UpdateUserInfoResponse extends ResponseBean {
	private List<UserLoginInfo> UserLoginInfoList = null;

	public List<UserLoginInfo> getmUserLoginInfolList() {
		return UserLoginInfoList;
	}

	public void setmUserLoginInfolList(List<UserLoginInfo> mUserLoginInfolList) {
		this.UserLoginInfoList = mUserLoginInfolList;
	}
	
}
